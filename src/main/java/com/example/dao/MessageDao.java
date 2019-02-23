package com.example.dao;

import com.example.entity.ChatSession;
import com.example.entity.FriendApply;
import com.example.entity.Message;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import javax.validation.OverridesAttribute;
import java.util.List;

@Mapper
@Component("messageDao")
public interface MessageDao {

    /**
     * 获取历史消息
     */
    @Select("select * from message where chatSessionId = #{chatSessionId}")
    List<Message> selectHistoryMessageByChatSessionId(@Param("chatSessionId") int chatSessionId);

    /**
     * 获取离线消息
     */
    @Select("select * from message where chatSessionId = #{chatSessionId} and status=0")
    List<Message> selectOfflineMessageByChatSessionId(int chatSessionId);

    /**
     * 保存消息
     * @param message 消息体
     */
    @Insert("insert into message(type,content,date,status,sender,chatSessionId) " +
            "values(#{type},#{content},#{date},#{status},#{sender},#{chatSessionId})")
    int insertMessage(Message message);


    /**
     * 保存好友请求
     */
    @Insert("insert into friends_apply(handleId,senderId,state,timestamp,content) " +
            "values(#{handleId},#{senderId},#{state},#{timestamp},#{content})")
    @Options(useGeneratedKeys = true, keyColumn = "applyId",keyProperty = "applyId")
    int insertRequest(FriendApply friendApply);


    /**
     * 获取用户的所有未处理的好友申请
     * @param handleId 用户ID
     */
    @Select("select * from friends_apply where handleId = #{handleId} and state=NoHandle")
    List<FriendApply> selectFriendApplyByUserId(@Param("handleId") int handleId);

    /**
     * 标记申请为已处理
     * @param applyId 待处理申请
     */
    @Update("update friends_apply set state='handled' where applyId=#{applyId}")
    int updateApplyHandle(@Param("applyId") int applyId);

    /**
     * 获取好友申请的具体内容
     * @param applyId 申请ID
     */
    @Select("select * from friends_apply where applyId=#{applyId}")
    FriendApply selectApplyById(@Param("applyId") int applyId);


    /**
     * 插入会话
     */
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

    /**
     * 根据成员获取会话
     */
    @Select("select userId from chatsession_has_user where chatSessionId = #{chatSessionId}")
    List<Integer> selectChatSessionMember(@Param("chatSessionId") int chatSessionId);

    @Select("select chatSessionId,userId from chatsession_has_user where chatSessionId in" +
            "(select chatSessionId from chatsession_has_user where userId=#{userId})")
    List<ChatSession> selectAllChatSession(@Param("userId") int userId);
}
