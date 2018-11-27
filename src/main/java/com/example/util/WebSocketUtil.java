package com.example.util;

import com.example.entity.Message;
import com.example.entity.HttpResponse;
import com.example.entity.WebSocketResponse;
import com.example.service.MessageService;
import com.example.service.UserService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import javax.websocket.Session;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Description: 工具类，用于处理WebSocket中传递的信息
 * Date: 2018-11-03
 */
public class WebSocketUtil {

    @Autowired
    private static MessageService messageService;

    // 实现服务端与单一客户端通信，用Map来存放，其中Key可以为用户标识
    private static ConcurrentHashMap<Integer, Session> sessions = new ConcurrentHashMap<>();

    public static ConcurrentHashMap<Integer, Session> getSessions() {
        return sessions;
    }

    /**
     * 解析数据，分类处理
     *
     * @param sender  发送人id
     * @param message 接受的消息
     * @param session 当前用户的session
     */
    public static void handleMessage(int sender, String message, Session session) {
        LoggerUtil.log("接受到消息：" + message);
        JSONObject json = new JSONObject(message);

        if ((!json.has("type")) || (!json.has("data"))) {
            LoggerUtil.log("消息格式错误");
            return;
        }
        JSONObject data = json.getJSONObject("data");
        String type = json.getString("type");
        LoggerUtil.log("消息解析成功");
        switch (type) {
            case SystemConstant.MESSAGE_CHAT:
                sendMessage(sender, data, session);
                break;
            case SystemConstant.REQUEST_ADD_FRIEND:
                break;
            case SystemConstant.REQUEST_ADD_GROUP:
                break;
            default:
                LoggerUtil.log("消息类型错误");
        }
    }

    /**
     * 发送消息
     *
     * @param sender  发送人ID
     * @param data    信息
     * @param session 用户session
     */
    private static void sendMessage(int sender, JSONObject data, Session session) {

        if (!(data.has("chatSessionId") && data.has("msg"))) return;
        // 获取会话ID
        int chatSessionId = data.getInt("chatSessionId");
        String content = data.getString("msg");
        // 获取会话参与者ID
        // 数据库的IO比较耗时，可以考虑缓存机制
        List<Integer> chatSessionMembers = messageService.listChatSessionMembers(chatSessionId);
        chatSessionMembers.remove(sender);

        Message message = new Message();
        message.setSender(sender);
        message.setChatSession(chatSessionId);
        message.setContent(content);

        try {
            for (Integer item : chatSessionMembers) {
                if (sessions.containsKey(item)) {
                    // 构建待发送的消息
                    JSONObject toSend = new JSONObject();
                    toSend.put("sender", sender);
                    toSend.put("msg", content);
                    toSend.put("time", message.getDate());
                    WebSocketResponse webSocketResponse = new WebSocketResponse(SystemConstant.MESSAGE_CHAT, toSend.toString());
                    // 发送
                    Session receiverSession = sessions.get(item);
                    receiverSession.getBasicRemote().sendText(new JSONObject(webSocketResponse).toString());
                    LoggerUtil.log("消息已发送至在线用户"+item);
                } else {
                    LoggerUtil.log("用户" + item + "不在线！");
                }
            }
            // 保存消息
             messageService.saveMessage(message);
        } catch (IOException e) {
            sendResponseMessage(SystemConstant.FAIL, "服务器错误，消息发送失败", "", session);
            e.printStackTrace();
            LoggerUtil.log(e.getMessage());
        }
    }

    /**
     * WebSocket返回数据
     *
     * @param code    状态码
     * @param msg     描述信息
     * @param data    携带数据
     * @param session 用户session
     */
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

