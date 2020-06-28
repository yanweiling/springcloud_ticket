package com.ywl.study.order.web;

import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedGenerator;
import com.netflix.discovery.converters.Auto;
import com.ywl.study.dto.OrderDTO;
import com.ywl.study.order.config.UserConvert;
import com.ywl.study.order.dao.OrderRepository;
import com.ywl.study.order.domain.Order;
import com.ywl.study.service.IOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * Created by mavlarn on 2018/1/20.
 */
@RestController
@RequestMapping("/api/order")
public class OrderResource {


    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    JmsTemplate jmsTemplate;

    private TimeBasedGenerator uuidGenerator= Generators.timeBasedGenerator();

    @PostMapping("")
    public void create(@RequestBody OrderDTO order){
        order.setUuid(uuidGenerator.generate().toString());
        jmsTemplate.setMessageConverter(new UserConvert());
        jmsTemplate.convertAndSend("order:new",order);
    }

    @GetMapping("")
    public List<Order> getAll() {
        return orderRepository.findAll();
    }


    @GetMapping("/{id}")
    public OrderDTO getMyOrder(@PathVariable Long id){
        Order order=orderRepository.findOne(id);
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setAmount(order.getAmount());
        dto.setTitle(order.getTitle());
        return dto;
    }

}
