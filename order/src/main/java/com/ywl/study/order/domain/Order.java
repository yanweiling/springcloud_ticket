package com.ywl.study.order.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.ZonedDateTime;

/**
 * Created by mavlarn on 2018/1/20.
 */
@Entity
@Table(name = "customer_order")
public class Order {

    @Id
    @GeneratedValue
    private Long id;

    /*用来幂等性判断*/
    @Column
    private String uuid;

    /*标志订单是由谁发起的*/
    @Column(name = "customer_Id")
    private Long customerId;

    @Column
    private String title;

    /*票号*/
    @Column(name = "ticket_num")
    private Long ticketNum;

    /*金额*/
    @Column
    private int amount;
    /*订单状态*/
    @Column
    private String status;

    /*出错原因*/
    @Column
    private String reason;

    private ZonedDateTime createdDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    /**
     * @return the uuid
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * @param uuid to set
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     * @return the customerId
     */
    public Long getCustomerId() {
        return customerId;
    }

    /**
     * @param customerId to set
     */
    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    /**
     * @return the ticketNum
     */
    public Long getTicketNum() {
        return ticketNum;
    }

    /**
     * @param ticketNum to set
     */
    public void setTicketNum(Long ticketNum) {
        this.ticketNum = ticketNum;
    }

    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return the reason
     */
    public String getReason() {
        return reason;
    }

    /**
     * @param reason to set
     */
    public void setReason(String reason) {
        this.reason = reason;
    }

    /**
     * @return the createdDate
     */
    public ZonedDateTime getCreatedDate() {
        return createdDate;
    }

    /**
     * @param createdDate to set
     */
    public void setCreatedDate(ZonedDateTime createdDate) {
        this.createdDate = createdDate;
    }
}
