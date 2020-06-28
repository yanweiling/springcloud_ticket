package com.ywl.study.user.service;

import com.netflix.discovery.converters.Auto;
import com.ywl.study.dto.OrderDTO;
import com.ywl.study.user.dao.CustomerRepository;
import com.ywl.study.user.dao.PayInfoRepository;
import com.ywl.study.user.domain.Customer;
import com.ywl.study.user.domain.PayInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class CustomerService {
    private static final Logger LOG= LoggerFactory.getLogger(CustomerService.class);
    @Autowired
    JmsTemplate jmsTemplate;

    @Autowired
    CustomerRepository customerRepository;
    @Autowired
    PayInfoRepository payInfoRepository;

    @JmsListener(destination = "order:pay",containerFactory = "msgFactory")
    @Transactional
    public void handleOrderPay(OrderDTO dto) {
        LOG.info("Get new order for pay:{}", dto);
        //检查是否已经支付过了，幂等性检查
        PayInfo payinfo = payInfoRepository.findOneByOrderId(dto.getId());
        if (payinfo != null) {
            LOG.warn("Order already paid,duplicated message:{}", dto);
            return;
        } else {
            //检查余额
            Customer customer = customerRepository.findOne(dto.getCustomerId());
            if (customer.getDeposit() < dto.getAmount()) {//余额不足，解锁

                return;
            }
            payinfo = new PayInfo();
            payinfo.setOrderId(dto.getId());
            payinfo.setAmount(dto.getAmount());
            payinfo.setStatus("PAID");
            payInfoRepository.save(payinfo);

            customerRepository.charge(dto.getCustomerId(), dto.getAmount());
        }
        dto.setStatus("PAID");
        jmsTemplate.convertAndSend("order:ticket_move", dto);
    }
}
