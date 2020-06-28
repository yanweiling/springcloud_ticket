package com.ywl.study.ticket.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by mavlarn on 2018/1/20.
 */
@Entity
@Table(name = "ticket")
public class Ticket {

    @Id
    @GeneratedValue
    private Long id;
    @Column
    private String name;
    @Column
    private Long owner;
    @Column(name = "lock_user")
    private Long lockUser;
    @Column(name = "ticket_num")
    private Long ticketNum;

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the owner
     */
    public Long getOwner() {
        return owner;
    }

    /**
     * @param owner to set
     */
    public void setOwner(Long owner) {
        this.owner = owner;
    }

    /**
     * @return the lockUser
     */
    public Long getLockUser() {
        return lockUser;
    }

    /**
     * @param lockUser to set
     */
    public void setLockUser(Long lockUser) {
        this.lockUser = lockUser;
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

    @Override
    public String toString() {
        return "Ticket{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", owner=" + owner +
                ", lockUser=" + lockUser +
                ", ticketNum=" + ticketNum +
                '}';
    }
}
