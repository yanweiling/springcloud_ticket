package com.ywl.study.user.config;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.connection.TransactionAwareConnectionFactoryProxy;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;
import org.springframework.transaction.PlatformTransactionManager;

import javax.jms.ConnectionFactory;

@Configuration
public class JmsConfig {
    @Bean
    public ConnectionFactory connectionFactory() {
        ConnectionFactory cf = new ActiveMQConnectionFactory("tcp://101.201.125.82:61616");
        TransactionAwareConnectionFactoryProxy proxy = new TransactionAwareConnectionFactoryProxy();
        proxy.setTargetConnectionFactory(cf);
        proxy.setSynchedLocalTransactionAllowed(true);//允许同步到LocalTransaction上面去
        return proxy;
    }

    /*如果我们不设置JmsTmeplate，有时候会有问题，有时候就没有问题*/
    /*如果不设置的话，有时候JmsTempalte就不会在事务中执行，就直接提交了，遇到异常也不会回滚*/
    @Bean
    public JmsTemplate jmsTemplate(ConnectionFactory connectionFactory) {
        JmsTemplate jmsTemplate = new JmsTemplate(connectionFactory);
        jmsTemplate.setSessionTransacted(true);//让jmsTemplate发送消息是在事务中进行
        jmsTemplate.setMessageConverter(new UserConvert());
        return jmsTemplate;

    }

    @Bean
    public JmsListenerContainerFactory<?> msgFactory(ConnectionFactory cf,
                                                     DefaultJmsListenerContainerFactoryConfigurer configurer,
                                                     PlatformTransactionManager transactionManager) {
        //factory 的设置会影响jmsListener监听读消息的配置
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setTransactionManager(transactionManager);
        factory.setReceiveTimeout(10000l);
        factory.setConcurrency("10");//读取消息的并发数
        factory.setPubSubDomain(Boolean.FALSE);//支持queue
        factory.setMessageConverter(new UserConvert());
        //containerFactory 同一个containerFactory监听不同的队列时，其中一个队列的监听session关闭时，会把connection也彻底关闭掉；
        //这样导致监听另一个队列的session也被迫关闭掉
//        factory.setCacheLevelName("CACHE_CONNECTION");//当session被关闭的时候，可以不是直接close掉connection，而是将connection被缓存起来
        configurer.configure(factory, cf);
        return factory;
    }
//
//    /**
//     * 收发数据的时候，自动将字符串转换成json数据
//     * @return
//     */
//    @Bean
//    public MessageConverter jacksonJmsMessageConverter(){
//        MappingJackson2MessageConverter converter=new MappingJackson2MessageConverter();
////        converter.setTargetType(MessageType.TEXT);
////        converter.setTypeIdPropertyName("_type");
//        return converter;
//    }
}
