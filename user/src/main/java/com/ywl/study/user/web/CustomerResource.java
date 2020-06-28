package com.ywl.study.user.web;


import com.netflix.discovery.converters.Auto;
import com.ywl.study.dto.OrderDTO;
import com.ywl.study.user.dao.CustomerRepository;
import com.ywl.study.user.domain.Customer;
import com.ywl.study.user.feign.OrderClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mavlarn on 2018/1/20.
 */
@RestController
@RequestMapping("/api/customer")
public class CustomerResource {

    @PostConstruct
    public void init() {
        if(customerRepository.count()>0)return;
        Customer customer = new Customer();
        customer.setUsername("imooc");
        customer.setPassword("111111");
        customer.setRole("User");
        customer.setDeposit(1000);
        customerRepository.save(customer);
    }

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    OrderClient orderClient;

    @PostMapping("")
    public Customer create(@RequestBody Customer customer) {
        return customerRepository.save(customer);
    }

    @GetMapping("")
    public List<Customer> getAll(){
        return customerRepository.findAll();
    }

    @GetMapping("/{id}")
    public Map getMyInfo(@PathVariable Long id){
        Customer customer = customerRepository.findOneByUsername("imooc");
        OrderDTO order = orderClient.getMyOrder(1l);
        Map result = new HashMap();
        result.put("customer", customer);
        result.put("order", order);
        return result;
    }

}
