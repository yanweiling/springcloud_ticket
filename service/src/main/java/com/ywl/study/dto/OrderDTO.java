package com.ywl.study.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * Created by mavlarn on 2018/2/14.
 */
@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO implements Serializable {

    private Long id;
    private String uuid;

    private Long customerId;

    private String title;

    private Long ticketNum;

    private int amount;

    private String status;

}
