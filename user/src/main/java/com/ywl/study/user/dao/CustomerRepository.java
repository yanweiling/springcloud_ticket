package com.ywl.study.user.dao;

import com.ywl.study.user.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
 * Created by mavlarn on 2018/1/20.
 */
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Customer findOneByUsername(String username);

    @Modifying
    @Query("UPDATE Customer SET deposit=deposit-?2 WHERE id=?1")
    int charge(Long customerId,int amount);
}
