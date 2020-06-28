package com.ywl.study.ticket.web;

import com.ywl.study.dto.OrderDTO;
import com.ywl.study.service.IOrderService;
import com.ywl.study.ticket.dao.TicketRepository;
import com.ywl.study.ticket.domain.Ticket;
import com.ywl.study.ticket.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * Created by mavlarn on 2018/1/20.
 */
@RestController
@RequestMapping("/api/ticket")
public class TicketResource  {


    @PostConstruct
    public void init() {
        if(ticketRepository.count()>0){
            return;
        }
       Ticket ticket=new Ticket();
       ticket.setName("Num.1");
       ticket.setTicketNum(100l);
       ticketRepository.save(ticket);
    }

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private TicketService ticketService;

    @PostMapping("/lock")
    public Ticket ticketLock(@RequestBody  OrderDTO dto){
       return ticketService.ticketLock(dto);
    }

    @PostMapping("/lock2")
    public int ticketLock2(@RequestBody  OrderDTO dto){
        return ticketService.ticketLock2(dto);
    }

}
