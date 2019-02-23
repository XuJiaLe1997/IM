package com.example.controller;

import com.example.entity.ChatSession;
import com.example.entity.FriendApply;
import com.example.entity.HttpResponse;
import com.example.entity.Message;
import com.example.service.MessageService;
import com.example.util.DateUtil;
import com.example.util.SystemConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: 消息控制类
 * Date: 2018-11-06
 */

@RestController
@RequestMapping("/message")
public class MessageController {

    private MessageService messageService;

    @Autowired
    public void setMessageService(MessageService messageService) {
        this.messageService = messageService;
    }

    /**
     * 发起私聊
     *
     * @param targetId 对方ID
     */
    @PostMapping("/privateChat")
    public HttpResponse makePrivateChat(@RequestParam("targetId") int targetId, HttpSession session) {
        int userId = (int) session.getAttribute("userId");
        ChatSession chatSession = new ChatSession(userId + "和" + targetId + "的私聊");
        int result = messageService.makePrivateSession(chatSession, userId, targetId);
        if (result == SystemConstant.FAIL) {
            return new HttpResponse(SystemConstant.FAIL, null, null);
        }
        Map<String, Object> param = new HashMap<>();
        param.put("chatSessionId", chatSession.getChatSessionId());
        return new HttpResponse(SystemConstant.SUCCEED, null, param);
    }

    /**
     * 获取历史消息
     *
     * @param chatSessionId 会话ID
     */
    @GetMapping("/historyMessage")
    public HttpResponse getHistoryMessage(@RequestParam("chatSessionId") int chatSessionId) {
        List<Message> messages = messageService.listHistoryMessage(chatSessionId);
        List<Map<String, Object>> params = new ArrayList<>();
        for(Message message: messages){
            Map<String, Object> param = new HashMap<>();
            param.put("content",message.getContent());
            param.put("time", DateUtil.string(message.getDate()));
            param.put("sender",message.getSender());
            param.put("chatSessionId",message.getChatSessionId());
            param.put("messageId",message.getMessageId());
            params.add(param);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("list", params);
        return new HttpResponse(SystemConstant.SUCCEED, null, result);
    }

    /**
     * 获取离线消息
     *
     * @param chatSessionId 会话ID
     */
    @GetMapping("/offlineMessage")
    public HttpResponse getOfflineMessage(@RequestParam("chatSessionId") int chatSessionId) {
        List<Message> messages = messageService.listOfflineMessage(chatSessionId);
        return new HttpResponse(SystemConstant.SUCCEED, null, messages);
    }

    /**
     * 获取用户所有未处理的好友申请
     */
    @GetMapping("friendApply")
    public HttpResponse getFriendApply(HttpSession session) {
        int userId = (int) session.getAttribute("userId");
        List<FriendApply> applys = messageService.selectApplyById(userId);
        List<Map<String, Object>> params = new ArrayList<>();
        for (FriendApply apply : applys) {
            Map<String, Object> param = new HashMap<>();
            param.put("applyId", apply.getApplyId());
            param.put("sender", apply.getSenderId());
            param.put("time", DateUtil.string(apply.getTimestamp()));
            param.put("msg", apply.getContent());
            params.add(param);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("list", params);
        return new HttpResponse(SystemConstant.SUCCEED, null, result);
    }

    /**
     * 处理好友申请
     *
     * @param applyId 申请ID
     * @param isAgree 是否同意
     */
    @PostMapping("handleApply")
    public HttpResponse handleApply(@RequestParam("applyId") int applyId,
                                    @RequestParam("isAgree") String isAgree,
                                    HttpSession session) {
        int userId = (int) session.getAttribute("userId");
        int result = messageService.handleApply(applyId, isAgree, userId);
        if (result == SystemConstant.SUCCEED) {
            return new HttpResponse(SystemConstant.SUCCEED, null, null);
        } else {
            return new HttpResponse(SystemConstant.FAIL, null, null);
        }
    }

    /**
     * 获取用户所所有的会话
     */
    @GetMapping("/chatSessionList")
    public HttpResponse getChatSessionList(HttpSession session) {
        int userId = (int) session.getAttribute("userId");
        List<ChatSession> result = messageService.listAllChatSession(userId);
        Map<String, Object> param = new HashMap<>();
        param.put("list", result);
        return new HttpResponse(SystemConstant.SUCCEED, null, param);
    }

}
