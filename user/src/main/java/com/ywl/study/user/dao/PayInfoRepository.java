package com.ywl.study.user.dao;

import com.ywl.study.user.domain.PayInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PayInfoRepository extends JpaRepository<PayInfo,Long> {
    PayInfo findOneByOrderId(Long orderId);
}
