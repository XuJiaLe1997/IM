package com.example.entity;

/**
 * Description: 会话实体类
 * Date: 2018-11-05
 */
public class ChatSession {

    private int chatSessionId;
    private String chatName;

    public ChatSession(String chatName){
        this.chatName = chatName;
    }

    public int getChatSessionId() {
        return chatSessionId;
    }

    public void setChatSessionId(int chatSessionId) {
        this.chatSessionId = chatSessionId;
    }

    public String getChatName() {
        return chatName;
    }

    public void setChatName(String chatName) {
        this.chatName = chatName;
    }
}
