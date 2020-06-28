package com.ywl.study.user.config;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ywl.study.dto.OrderDTO;
import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.jms.support.converter.MessageConverter;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import java.io.IOException;


public class UserConvert implements MessageConverter {

    @Override
    public Message toMessage(Object o, Session session) throws JMSException, MessageConversionException {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String ret = mapper.writeValueAsString(o);
            return session.createTextMessage(ret);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return session.createTextMessage("error");
    }

    @Override
    public OrderDTO fromMessage(Message message) throws JMSException, MessageConversionException {
        try {
            TextMessage msg = (TextMessage) message;
            ObjectMapper mapper = new ObjectMapper();
            String text = msg.getText();
            return mapper.readValue(text, OrderDTO.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
