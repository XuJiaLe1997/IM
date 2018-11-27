package com.example.websocket;

import com.example.util.LoggerUtil;
import com.example.util.WebSocketUtil;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

/**
 * Description: WebSocket连接，所有时间敏感消息都需要通过这个信道
 * Date: 2018-11-03
 */

// 虽然@Component默认是单例模式的，但springboot还是会为每个WebSocket连接初始化一个bean
// 将URL带上user，便于为区分每一个用户的连接
@ServerEndpoint(value = "/webSocket/{user}")
@Component
public class WebSocket {

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
        WebSocketUtil.getSessions().put(user, session);
        LoggerUtil.log("用户" + user + "上线");
    }

    /**
     * 连接关闭时执行
     */
    @OnClose
    public void onClose() {
        // 将保存的用户session移除
        WebSocketUtil.getSessions().remove(this.user);
        LoggerUtil.log("用户" + this.user + "下线");
    }

    /**
     * 收到消息时执行
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        // 将消息的处理交给WebSocket
        WebSocketUtil.handleMessage(this.user, message, session);
    }

    /**
     * 连接错误时执行
     */
    @OnError
    public void onError(Throwable error) {
        error.printStackTrace();
        LoggerUtil.log("用户" + this.user+"连接错误，错误信息打印:\n"+error.getMessage());
    }
}
