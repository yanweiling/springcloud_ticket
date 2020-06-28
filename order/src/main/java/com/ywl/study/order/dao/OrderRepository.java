package com.ywl.study.order.dao;

import com.ywl.study.order.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by mavlarn on 2018/1/20.
 */
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findOneByCustomerId(Long customerId);

    /*根据uuid查看该uuid是否被处理过*/
    Order findOneByUuid(String uuid);
}
