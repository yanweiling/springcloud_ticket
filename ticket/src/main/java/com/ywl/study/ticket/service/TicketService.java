package com.ywl.study.ticket.service;

import com.netflix.discovery.converters.Auto;
import com.ywl.study.dto.OrderDTO;
import com.ywl.study.ticket.config.UserConvert;
import com.ywl.study.ticket.dao.TicketRepository;
import com.ywl.study.ticket.domain.Ticket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TicketService {
    private static final Logger LOG= LoggerFactory.getLogger(TicketService.class);

    @Autowired
    TicketRepository ticketRepository;
    @Autowired
    JmsTemplate jmsTemplate;

    @Transactional
    @JmsListener(destination = "order:new",containerFactory = "msgFactory")
    public void handleTicketLock(OrderDTO dto){
        LOG.info("Get new Order for ticket lock:{}",dto);
        int lockCount=ticketRepository.lockTicket(dto.getCustomerId(),dto.getTicketNum());
        if(lockCount==1){
            LOG.info("锁票成功");
            dto.setStatus("TICKET_LOCKED");
            jmsTemplate.convertAndSend("order:locked",dto);

        }else{
            LOG.info("锁票失败");
            dto.setStatus("TICKET_LOCK_FAIL");
            jmsTemplate.convertAndSend("order:fail",dto);

        }
    }

    @Transactional
    @JmsListener(destination = "order:ticket_move",containerFactory = "msgFactory")
    public void handleTicketMove(OrderDTO dto){
        LOG.info("Get new Order for ticket move:{}",dto);
//        Ticket ticket=ticketRepository.findOneByTicketNumAndLockUser(dto.getTicketNum(),dto.getCustomerId());
//        ticket.setOwner(dto.getCustomerId());
//        ticket.setLockUser(null);
//        ticketRepository.save(ticket);
        int count=ticketRepository.moveTicket(dto.getCustomerId(),dto.getTicketNum());
        if(count==0){
            //表示已经处理过了
            LOG.warn("Ticket already moved:{}",dto);
//            return;
        }
        dto.setStatus("TICKET_MOVED");
        jmsTemplate.convertAndSend("order:finish",dto);
    }


    /**
     * 票解锁+票转移后撤销
     * @param dto
     */
    @Transactional
    @JmsListener(destination = "order:ticket_error",containerFactory = "msgFactory")
    public void handleTickeError(OrderDTO dto){
        LOG.info("Get new Order for unlock {}",dto);
        int count=ticketRepository.unlockTicket(dto.getCustomerId(),dto.getTicketNum());
        if(count==0){
            LOG.info("Ticket already unlock:{}",dto);
        }
        count=ticketRepository.unmoveTicket(dto.getCustomerId(),dto.getTicketNum());
        if(count==0){
            LOG.info("Ticket already unmoved,or not moved!");
        }
        jmsTemplate.convertAndSend("order:fail",dto);

    }
    /*在10s的时间内发送两个请求，一个请求参数
    {
    "customerId": 1,
    "ticketNum": 100
    }
    以后，在请求，参数为
    {
        "customerId": 2,
        "ticketNum": 100
    }

    数据库中customer_id会直接变更为2；
    原因是ticketRepository.save(ticket) 方法会将参数保存到persistentContext 中，暂时缓存起来，在缓存阶段，收到了两次请求，
    最后一个请求会覆盖之前的请求，缓存中的参数最后保存为最后一次提交的参数
     {
        "customerId": 2,
        "ticketNum": 100
    }
    ，然后提交到数据库中；

    如果将请求参数直接写到数据库中，而不临时暂存在persistentContext中呢？
    重写save方法，在TicketRepositry中，重写
     @Override
    @Modifying(clearAutomatically = true)
    Ticket save(Ticket ticket);

    这样连着请求两次后，我们数据库中customerid由1 会变为2

*/
    @Transactional
    public Ticket ticketLock(OrderDTO dto){
        Ticket ticket=ticketRepository.findOneByTicketNum(dto.getTicketNum());
        ticket.setLockUser(dto.getCustomerId());
        ticket=ticketRepository.save(ticket);
        try {
            Thread.sleep(10*1000);
        } catch (InterruptedException e) {
            LOG.error(e.getMessage());
        }
        return ticket;

    }

    @Transactional
    public int ticketLock2(OrderDTO dto){
        int lockCount=ticketRepository.lockTicket(dto.getCustomerId(),dto.getTicketNum());
        LOG.info("updated ticket lock count:{}",lockCount);

        try {
            Thread.sleep(10*1000);
        } catch (InterruptedException e) {
            LOG.error(e.getMessage());
        }
        return lockCount;

    }



}
