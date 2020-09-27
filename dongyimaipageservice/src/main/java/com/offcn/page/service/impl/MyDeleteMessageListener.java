package com.offcn.page.service.impl;

import com.offcn.page.service.ItemPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

@Component
public class MyDeleteMessageListener implements MessageListener {

    @Autowired
    private ItemPageService itemPageService;

    @Override
    public void onMessage(Message message) {

        ObjectMessage objectMessage= (ObjectMessage) message;

        try {

            Long[] ids = (Long[]) objectMessage.getObject();

            System.out.println("ItemDeletdListtenner监听接收到信息..."+ids);

            boolean b = itemPageService.deleteItemHtml(ids);

            System.out.println("网页删除结果："+b);

        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
