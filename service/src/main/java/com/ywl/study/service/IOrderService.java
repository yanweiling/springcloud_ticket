package com.ywl.study.service;

import com.ywl.study.dto.OrderDTO;

/**
 * Created by mavlarn on 2018/2/14.
 */
public interface IOrderService {

    OrderDTO create(OrderDTO dto);
    OrderDTO getMyOrder(Long id);
}
