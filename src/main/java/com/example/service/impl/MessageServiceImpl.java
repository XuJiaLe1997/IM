package com.example.service.impl;

import com.example.dao.MessageMapper;
import com.example.entity.Message;
import com.example.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class MessageServiceImpl implements MessageService {

    private MessageMapper messageMapper;

    @Autowired
    public void setMessageMapper(MessageMapper messageMapper){
        this.messageMapper = messageMapper;
    }

    @Override
    public int saveMessage(Message message) {
        return messageMapper.insertMessage(message);
    }

    @Override
    public List<Message> listOfflineMessage(int chatSessionId) {
        return messageMapper.selectOfflineMessageByChatSessionId(chatSessionId);
    }

    @Override
    public List<Message> listHistoryMessage(int chatSessionId) {
        return messageMapper.selectHistoryMessageByChatSessionId(chatSessionId);
    }

    @Override
    public int makePersonSession(int myId, int targetId) {
        return 0;
    }

    @Override
    public int makeGroupSession(int myId) {
        return 0;
    }

    @Override
    public List<Integer> listChatSessionMembers(int chatSessionId) {
        return null;
    }
}
