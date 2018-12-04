package com.example.dao;

import com.example.entity.ChatSession;
import com.example.entity.Message;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import javax.validation.OverridesAttribute;
import java.util.List;

@Mapper
@Component("messageMapper")
public interface MessageMapper {

    @Select("select * from message where chatSessionId = #{charSessionId}")
    List<Message> selectHistoryMessageByChatSessionId(@Param("chatSessionId") int chatSessionId);

    @Select("select * from message where chatSessionId = #{chatSessionId} and status=0")
    List<Message> selectOfflineMessageByChatSessionId(int chatSessionId);

    @Insert("insert into message(type,content,date,status,sender,chatSessionId) " +
            "values(#{type},#{msg},#{date},#{status},#{sender},#{chatSessionId})")
    int insertMessage(Message message);

    @Insert("insert into chatsession(chatName) values(#{chatName})")
    @Options(useGeneratedKeys = true, keyProperty = "chatSessionId", keyColumn = "chatSessionId")
    int insertChatSession(ChatSession chatSession);

    /**
     * 根据成员查询会话
     * @param myId 成员ID
     * @param targetId 成员ID
     */
    @Select("select chatSessionId from chatsession_has_user where userId = #{myId} and chatSessionId in" +
            "(select chatSessionId from chatsession_has_user where userId = #{targetId})")
    Integer selectChatSessionByUserId(@Param("myId") int myId, @Param("targetId") int targetId);

    /**
     * 添加会话成员
     *
     * @param chatSessionId 会话ID
     * @param userId        成员ID
     */
    @Insert("insert into chatsession_has_user(chatSessionId,userId) values(#{chatSessionId},#{userId})")
    int insertChatSessionMember(@Param("chatSessionId") int chatSessionId, @Param("userId") int userId);

    @Select("select userId from chatsession_has_user where chatSessionId = #{chatSessionId}")
    List<Integer> selectChatSessionMember(@Param("chatSessionId") int chatSessionId);
}
