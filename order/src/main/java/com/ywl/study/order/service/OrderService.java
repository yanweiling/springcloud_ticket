package com.ywl.study.order.service;

import com.ywl.study.dto.OrderDTO;
import com.ywl.study.order.dao.OrderRepository;
import com.ywl.study.order.domain.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.ZonedDateTime;
import java.util.List;

@Service
public class OrderService {
    private static final Logger LOG= LoggerFactory.getLogger(OrderService.class);
    @Autowired
    JmsTemplate jmsTemplate;

    @Autowired
    OrderRepository orderRepository;

    @JmsListener(destination = "order:locked",containerFactory = "msgFactory")
    @Transactional
    public void handleOrderNew(OrderDTO dto){
        LOG.info("Get new Order to create:{}",dto);
        //判断是否被处理过
        if(orderRepository.findOneByUuid(dto.getUuid())!=null){
          LOG.info("Msg already processed:{}",dto);
        }else{
            Order order=createOrder(dto);
            orderRepository.save(order);
            dto.setId(order.getId());

        }
        dto.setStatus("NEW");
        jmsTemplate.convertAndSend("order:pay",dto);

    }
    @JmsListener(destination = "order:finish",containerFactory = "msgFactory")
    @Transactional
    public void handleOrderFinish(OrderDTO dto){
        LOG.info("Get order for finish:{}",dto);
        Order order=orderRepository.findOne(dto.getId());
        order.setStatus("FINISH");
        orderRepository.save(order);
    }

    /**
     * 订单失败情况处理
     * 1.索票失败
     * 2.扣费失败，解锁票，然后触发
     * 3.定时任务中检查到订单超时
     * @param dto
     */
    @JmsListener(destination = "order:fail",containerFactory = "msgFactory")
    @Transactional
    public void handleOrderFail(OrderDTO dto){
        LOG.info("Get order for fail:{}",dto);
        Order order=null;
        if(dto.getId()==null){//索票阶段失败，还未生成订单ID

            //创建失败的订单
            order=createOrder(dto);
            order.setStatus("FAIL");
            order.setReason("TICKET_LOCK_FAIL");
        }else{ //检查余额扣费时错误
            order=orderRepository.findOne(dto.getId());
            if(dto.getStatus().equals("NOT_ENOUGH_DEPOSIT")){
                order.setReason("NOT_ENOUGH_DEPOSIT");
            }else if(dto.getStatus().equals("TIMEOUT")){
                order.setReason("TIMEOUT");
            }

        }
        order.setStatus("FAIL");
        orderRepository.save(order);


    }


    private Order createOrder(OrderDTO dto){
        Order order=new Order();
        order.setUuid(dto.getUuid());
        order.setAmount(dto.getAmount());
        order.setTitle(dto.getTitle());
        order.setCustomerId(dto.getCustomerId());
        order.setTicketNum(dto.getTicketNum());
        order.setStatus("NEW");
        order.setCreatedDate(ZonedDateTime.now());
        return order;
    }

    @Scheduled(fixedDelay = 1000L)
    public void checkTimeOut(){
        //设置超过一分钟就算超时
        ZonedDateTime checkTime=ZonedDateTime.now().minusMinutes(1L);
        List<Order> orderss=orderRepository.findAllByStatusAndCreatedDateBefore("NEW",checkTime);
        orderss.forEach(order -> {
            LOG.error("Order timeout:{}",order);
            OrderDTO dto=new OrderDTO();
            dto.setId(order.getId());
            dto.setUuid(order.getUuid());
            dto.setTicketNum(order.getTicketNum());
            dto.setAmount(order.getAmount());
            dto.setTitle(order.getTitle());
            dto.setCustomerId(order.getCustomerId());
            dto.setStatus("TIMEOUT");
            jmsTemplate.convertAndSend("order:fail",dto);
        });

    }
}
