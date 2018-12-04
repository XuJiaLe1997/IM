package com.example.service.impl;

import com.example.dao.MessageMapper;
import com.example.entity.ChatSession;
import com.example.entity.Message;
import com.example.service.MessageService;
import com.example.util.SystemConstant;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sun.jvm.hotspot.ui.action.MemoryAction;

import java.util.List;
import java.util.PrimitiveIterator;

@Service
@Transactional
public class MessageServiceImpl implements MessageService {

    private MessageMapper messageMapper;

    @Autowired
    public void setMessageMapper(MessageMapper messageMapper) {
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
    public int makePersonSession(ChatSession chatSession, int myId, int targetId) {
        // 检查是否已经存在会话
        Integer existChatSessionId = messageMapper.selectChatSessionByUserId(myId, targetId);
        if (existChatSessionId != null) {
            chatSession.setChatSessionId(existChatSessionId);
            return SystemConstant.SUCCEED;
        }
        // 会话不存在，创建新会话
        int result = messageMapper.insertChatSession(chatSession);
        if (result == 0) {
            return SystemConstant.FAIL;
        }
        result = messageMapper.insertChatSessionMember(chatSession.getChatSessionId(), myId);
        if (result == 0) {
            return SystemConstant.FAIL;
        }
        result = messageMapper.insertChatSessionMember(chatSession.getChatSessionId(), targetId);
        if (result == 0) {
            return SystemConstant.FAIL;
        }
        return SystemConstant.SUCCEED;
    }

    @Override
    public int makeGroupSession(int myId) {
        return 0;
    }

    @Override
    public List<Integer> listChatSessionMembers(int chatSessionId) {
        return messageMapper.selectChatSessionMember(chatSessionId);
    }
}
