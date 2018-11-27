package com.example.service;

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
     * 获取离线消息
     */
    List<Message> listOfflineMessage(int chatSessionId);

    List<Message> listHistoryMessage(int chatSessionId);

    /**
     * 发起私人对话
     *
     * @param myId     本方ID
     * @param targetId 对方ID
     * @return 新建的会话ID
     */
    int makePersonSession(int myId, int targetId);

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
    List<Integer> listChatSessionMembers(int chatSessionId);
}
