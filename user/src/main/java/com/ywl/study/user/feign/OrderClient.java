package com.ywl.study.user.feign;

import com.ywl.study.dto.OrderDTO;
import com.ywl.study.service.IOrderService;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 需要将IOrderService中的所有的接口都实现ReqeustMapping映射，否则会报错的
 */
@FeignClient(value = "order",path = "/api/order")
public interface OrderClient extends IOrderService {

    /**
     * 对于feign的规范，其中@PathVariable中name必须要写，否则报错
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    OrderDTO getMyOrder(@PathVariable(name = "id") Long id);

    @PostMapping("")
    OrderDTO create(@RequestBody OrderDTO dto);

}
