package com.example.websocket;

import com.example.entity.FriendApply;
import com.example.entity.HttpResponse;
import com.example.entity.Message;
import com.example.entity.WebSocketResponse;
import com.example.service.MessageService;
import com.example.service.impl.MessageServiceImpl;
import com.example.util.DateUtil;
import com.example.util.LoggerUtil;
import com.example.util.SystemConstant;
import org.apache.commons.text.StringEscapeUtils;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Description: WebSocket连接，所有时间敏感消息都需要通过这个信道
 * Date: 2018-11-03
 */

// 虽然@Component默认是单例模式的，但springboot还是会为每个WebSocket连接初始化一个bean
// 将URL带上user，便于为区分每一个用户的连接
@ServerEndpoint(value = "/webSocket/{user}", configurator = SpringContextHelper.class)
@Component
public class WebSocket {


    private static MessageService messageService;

    static {
        messageService = (MessageServiceImpl) SpringContextHelper.getBean("messageService");
    }


    // 实现服务端与单一客户端通信，用Map来存放，其中Key可以为用户标识
    private static ConcurrentHashMap<Integer, Session> sessions = new ConcurrentHashMap<>();

    // 当前连接的用户
    private int user;

    /**
     * 连接连接时执行
     *
     * @param user    当前用户ID
     * @param session 当前用户session
     */
    @OnOpen
    public void onOpen(@PathParam("user") int user, Session session) {
        // 绑定用户与WebSocket
        this.user = user;
        // 绑定用户与Session
        sessions.put(user, session);
        LoggerUtil.log("用户" + user + "上线");
        LoggerUtil.log("当前在线人数："+sessions.size());
    }

    /**
     * 连接关闭时执行
     */
    @OnClose
    public void onClose() {
        // 将保存的用户session移除
        sessions.remove(this.user);
        LoggerUtil.log("用户" + this.user + "下线");
    }

    /**
     * 收到消息时执行
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        handleMessage(this.user, message, session);
    }

    /**
     * 连接错误时执行
     */
    @OnError
    public void onError(Throwable error) {
        error.printStackTrace();
        LoggerUtil.log("用户" + this.user + "连接错误，错误信息打印:\n" + error.getMessage());
    }


    /**
     * 解析数据，分类处理
     *
     * @param sender  发送人id
     * @param message 接受的消息
     * @param session 当前用户的session
     */
    private void handleMessage(int sender, String message, Session session) {
        LoggerUtil.log("接受到消息：" + message);
        JSONObject json;
        try {
            json = new JSONObject(message);
        } catch (Exception e) {
            LoggerUtil.log("该消息不是json格式");
            return;
        }
        if ((!json.has("type")) || (!json.has("data"))) {
            LoggerUtil.log("消息字段错误");
            return;
        }
        try {
            JSONObject data = json.getJSONObject("data");
            String type = json.getString("type");
            LoggerUtil.log("消息解析成功");
            // 分发请求
            switch (type) {
                case SystemConstant.MESSAGE_CHAT:
                    sendMessage(sender, data, session);
                    break;
                case SystemConstant.Apply_ADD_FRIEND:
                    sendFriendApply(sender, data, session);
                    break;
                case SystemConstant.Apply_ADD_GROUP:
                    break;
                default:
                    LoggerUtil.log("消息类型错误");
            }
        } catch (Exception e) {
            LoggerUtil.log("消息解析失败" + e.getMessage());

        }
    }

    /**
     * 发送聊天消息
     *
     * @param sender  发送人ID
     * @param data    信息
     * @param session 用户session
     */
    private void sendMessage(int sender, JSONObject data, Session session) {

        if (!data.has("chatSessionId") || !data.has("msg")) {
            LoggerUtil.log("消息字段错误");
            return;
        }
        int chatSessionId = data.getInt("chatSessionId");
        String msg = data.getString("msg");

        // 获取会话参与者ID，数据库的IO比较耗时，可以考虑Redis缓存
        List<Integer> chatSessionMembers = messageService.listMembers(chatSessionId);
        if (chatSessionMembers.get(0) == sender) {
            chatSessionMembers.remove(0);
        } else {
            chatSessionMembers.remove(1);
        }

        Message message = new Message();
        message.setType(SystemConstant.MESSAGE_CHAT);
        message.setSender(sender);
        message.setChatSessionId(chatSessionId);
        message.setContent(msg);

        try {
            for (Integer item : chatSessionMembers) {
                if (sessions.containsKey(item)) {
                    // 构建待发送的消息
                    Map<String, Object> toSend = new HashMap<>();
                    toSend.put("chatSessionId",chatSessionId);
                    toSend.put("sender", sender);
                    toSend.put("msg", msg);
                    toSend.put("time", DateUtil.string(message.getDate()));
                    toSend.put("type",SystemConstant.MESSAGE_CHAT);
                    // 发送
                    Session receiverSession = sessions.get(item);
                    JSONObject jsonObject = new JSONObject(toSend);

                    receiverSession.getBasicRemote().sendText(StringEscapeUtils.unescapeJava(jsonObject.toString()));
                    LoggerUtil.log("消息已发送至在线用户" + item);
                } else {
                    LoggerUtil.log("用户" + item + "不在线！");
                }
            }
            // 保存消息
            messageService.saveMessage(message);
        } catch (IOException e) {
            e.printStackTrace();
            LoggerUtil.log(e.getMessage());
        }
    }

    /**
     * 发送好友申请
     *
     * @param sender  发送者
     * @param data    数据
     * @param session 发送者连接
     */
    private void sendFriendApply(int sender, JSONObject data, Session session) {
        if (!data.has("receiver") || !data.has("msg")) {
            LoggerUtil.log("消息字段错误");
            return;
        }
        int receiver = data.getInt("receiver");
        String msg = data.getString("msg");
        // 构建消息实体
        FriendApply friendApply = new FriendApply();
        friendApply.setSenderId(user);
        friendApply.setHandleId(receiver);
        friendApply.setContent(msg);
        friendApply.setTimestamp(new Timestamp((new Date()).getTime()));
        // 保存申请到数据库
        messageService.saveRequest(friendApply);
        // 转发请求
        if (sessions.containsKey(receiver)) {
            Session receiverSession = sessions.get(receiver);
            try {
                Map<String, Object> param = new HashMap<>();
                param.put("applyId", friendApply.getApplyId());
                param.put("sender", sender);
                param.put("msg", msg);
                param.put("time", DateUtil.string(friendApply.getTimestamp()));
                param.put("type",SystemConstant.Apply_ADD_FRIEND);

                JSONObject jsonObject = new JSONObject(param);
                receiverSession.getBasicRemote().sendText(StringEscapeUtils.unescapeJava(jsonObject.toString()));
            } catch (Exception e) {
                LoggerUtil.log(e.getMessage());
            }
        } else {
            LoggerUtil.log("用户" + receiver + "不在线！");
        }

    }

    // 方法过时
    /*
     * WebSocket返回系统数据，一般处理错误信息
     *
     * @param code    状态码
     * @param msg     描述信息
     * @param data    携带数据
     * @param session 用户session
     */
    @Deprecated
    private static void sendResponseMessage(int code, String msg, String data, Session session) {
        try {
            String result = new JSONObject(new WebSocketResponse(SystemConstant.MESSAGE_FROM_SYSTEM,
                    new JSONObject(new HttpResponse(code, msg, data))
            )).toString();
            session.getBasicRemote().sendText(result);
        } catch (Exception ex) {
            ex.printStackTrace();
            LoggerUtil.log(ex.getMessage());
        }

    }
}
