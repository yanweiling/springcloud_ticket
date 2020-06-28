package com.ywl.study.order.service;

import com.ywl.study.dto.OrderDTO;
import com.ywl.study.order.dao.OrderRepository;
import com.ywl.study.order.domain.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.ZonedDateTime;

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
}
