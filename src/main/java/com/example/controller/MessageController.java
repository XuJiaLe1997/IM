package com.example.controller;

import com.example.entity.ChatSession;
import com.example.entity.HttpResponse;
import com.example.entity.Message;
import com.example.service.MessageService;
import com.example.util.SystemConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
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
    public HttpResponse makeChat(@RequestParam("targetId") int targetId, HttpSession session) {
        int userId = (int) session.getAttribute("userId");
        ChatSession chatSession = new ChatSession(userId + "和" + targetId + "的私聊");
        int result = messageService.makePersonSession(chatSession, userId, targetId);
        if (result == SystemConstant.FAIL) {
            return new HttpResponse(SystemConstant.FAIL, null, null);
        }
        Map<String,Object> param = new HashMap<>();
        param.put("chatSessionId",chatSession.getChatSessionId());
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

        return new HttpResponse(SystemConstant.SUCCEED, null,
                new HashMap<String,Object>().put("list",messages));
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
}
