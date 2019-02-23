package com.example.service.impl;

import com.example.dao.MessageDao;
import com.example.dao.UserDao;
import com.example.entity.ChatSession;
import com.example.entity.FriendApply;
import com.example.entity.Message;
import com.example.service.MessageService;
import com.example.util.SystemConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service("messageService")
@Transactional
public class MessageServiceImpl implements MessageService {

    private MessageDao messageDao;
    private UserDao userDao;

    @Autowired
    public void setMessageDao(MessageDao messageDao) {
        this.messageDao = messageDao;
    }

    @Autowired
    public void setUserDao(UserDao userDao){
        this.userDao = userDao;
    }

    @Override
    public int saveMessage(Message message) {
        return messageDao.insertMessage(message);
    }

    @Override
    public int saveRequest(FriendApply friendApply) {
        return messageDao.insertRequest(friendApply);
    }

    @Override
    public List<FriendApply> selectApplyById(int handleId) {
        return messageDao.selectFriendApplyByUserId(handleId);
    }

    @Override
    public int handleApply(int applyId, String handle, int userId) {
        int result;
        FriendApply apply = messageDao.selectApplyById(applyId);
        // 同意，新增好友关系
        if("agree".equals(handle)){
            int friendId = apply.getSenderId();
            result = userDao.insertFriend(userId,friendId);
            if(result != 1) return SystemConstant.FAIL;
            result = userDao.insertFriend(friendId,userId);
            if(result != 1) return SystemConstant.FAIL;
        }
        // 更新好友申请的处理状态，标记为已处理
        result = messageDao.updateApplyHandle(applyId);
        if(result == 1){
            return SystemConstant.SUCCEED;
        }else {
            return SystemConstant.FAIL;
        }
    }

    @Override
    public List<Message> listOfflineMessage(int chatSessionId) {
        return messageDao.selectOfflineMessageByChatSessionId(chatSessionId);
    }

    @Override
    public List<Message> listHistoryMessage(int chatSessionId) {
        return messageDao.selectHistoryMessageByChatSessionId(chatSessionId);
    }

    @Override
    public int makePrivateSession(ChatSession chatSession, int myId, int targetId) {
        // 检查是否已经存在会话
        Integer existChatSessionId = messageDao.selectChatSessionByUserId(myId, targetId);
        if (existChatSessionId != null) {
            chatSession.setChatSessionId(existChatSessionId);
            return SystemConstant.SUCCEED;
        }
        // 会话不存在，创建新会话
        int result = messageDao.insertChatSession(chatSession);
        if (result == 0) {
            return SystemConstant.FAIL;
        }
        // 插入会话成员
        result = messageDao.insertChatSessionMember(chatSession.getChatSessionId(), myId);
        if (result == 0) {
            return SystemConstant.FAIL;
        }
        result = messageDao.insertChatSessionMember(chatSession.getChatSessionId(), targetId);
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
    public List<Integer> listMembers(int chatSessionId) {
        return messageDao.selectChatSessionMember(chatSessionId);
    }

    @Override
    public List<ChatSession> listAllChatSession(int userId) {
        // 获取用户参与的所有会话
        List<ChatSession> chatSessions = messageDao.selectAllChatSession(userId);
        List<ChatSession> temp = new ArrayList<>();
        // 只保留会话ID和其他成员的ID
        for(ChatSession chatSession: chatSessions){
            if(chatSession.getUserId() != userId)
                temp.add(chatSession);
        }
        return temp;
    }
}
