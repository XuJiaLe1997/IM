package com.example.service;

import com.example.entity.ChatSession;
import com.example.entity.FriendApply;
import com.example.entity.Message;

import java.util.List;

/**
 * Description: 所有涉及消息、会话的服务都由此接口提供
 * Date: 2018-11-05
 */

public interface MessageService {

    /**
     * 保存消息
     *
     * @param message 消息主体
     */
    int saveMessage(Message message);


    /**
     * 保存好友申请
     * @param friendApply 申请体
     */
    int saveRequest(FriendApply friendApply);

    /**
     * 获取用户所有未处理申请
     * @param handleId 用户ID
     */
    List<FriendApply> selectApplyById(int handleId);

    /**
     * 处理申请
     */
    int handleApply(int applyId, String handle, int userId);

    /**
     * 获取离线消息
     */
    List<Message> listOfflineMessage(int chatSessionId);

    /**
     * 获取历史消息
     * @param chatSessionId 会话ID
     */
    List<Message> listHistoryMessage(int chatSessionId);

    /**
     * 发起私聊
     * @param chatSession 会话
     * @param myId 用户id
     * @param targetId 对方id
     */
    int makePrivateSession(ChatSession chatSession, int myId, int targetId);

    /**
     * 发起群聊
     *
     * @param userId 用户ID
     * @return 新建的会话ID
     */
    int makeGroupSession(int userId);

    /**
     * 获取会话中的所有成员
     *
     * @param chatSessionId 会话ID
     * @return 所有成员的ID列表
     */
    List<Integer> listMembers(int chatSessionId);

    /**
     * 获取用户所有的chatSession
     * @param userId 用户ID
     */
    List<ChatSession> listAllChatSession(int userId);
}
