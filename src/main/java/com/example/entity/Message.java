package com.example.entity;

import com.example.util.SystemConstant;

import java.util.Date;
import java.sql.Timestamp;

/**
 * Description:  普通消息类
 */
public class Message {

    private String type; //消息类型

    private int messageId;

    private int sender;

    private int chatSessionId;

    private String content;

    private Timestamp date;

    private int status;  // 标记是否已读

    public Message() {
        this.type = null;
        this.sender = 0;
        this.chatSessionId = 0;
        this.content = null;
        this.date =new Timestamp((new Date()).getTime());  //时间初始化为当前
        this.status = SystemConstant.MESSAGE_NOT_READ;  //默认为未读消息
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public int getSender() {
        return sender;
    }

    public void setSender(int sender) {
        this.sender = sender;
    }

    public int getChatSessionId() {
        return chatSessionId;
    }

    public void setChatSessionId(int chatSessionId) {
        this.chatSessionId = chatSessionId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

}
