package com.ywl.study.ticket.dao;

import com.ywl.study.ticket.domain.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by mavlarn on 2018/1/20.
 */
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    List<Ticket> findByOwner(Long owner);

    Ticket findOneByTicketNumAndLockUser(Long ticketNum,Long lockUser);
    Ticket findOneByTicketNum(Long ticketNum);

    @Override
    @Modifying(clearAutomatically = true)
    Ticket save(Ticket ticket);

    @Modifying
    @Query("UPDATE Ticket SET owner=?1,lockUser=NULL WHERE lockUser=?1 AND ticketNum=?2")
    int moveTicket(Long customerId,Long ticketNum);

    @Modifying
    @Query("UPDATE Ticket SET lockUser=?1 WHERE lockUser IS NULL AND ticketNum=?2")
    int lockTicket(Long customerId,Long ticketNum);



}
