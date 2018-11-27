package com.example.dao;

import com.example.entity.ChatSession;
import com.example.entity.Message;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component("messageMapper")
public interface MessageMapper {


    List<Message> selectHistoryMessageByChatSessionId(int chatSessionId);

    List<Message> selectOfflineMessageByChatSessionId(int chatSessionId);

    int insertMessage(Message message);

    int insertChatSession(ChatSession chatSession);

}
