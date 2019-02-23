package com.example.controller;

import com.example.entity.ChatSession;
import com.example.entity.HttpResponse;
import com.example.entity.Message;
import com.example.entity.User;
import com.example.service.MessageService;
import com.example.service.UserService;
import com.example.util.DateUtil;
import com.example.util.LoggerUtil;
import com.example.util.SystemConstant;
import com.example.websocket.WebSocket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 测试
 */
@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    MessageService messageService;
    @Autowired
    UserService userService;

    /*
     * 测试能否向服务器发起请求
     */
    @GetMapping("/connect")
    public String test() {
        return "Hello，server working.";
    }

    /**
     * 测试图片上传
     */
    @PostMapping("/upload")
    public HttpResponse testUpload(@RequestParam("image") MultipartFile file, HttpServletRequest request) {
        try {
            if (file == null)
                return new HttpResponse(SystemConstant.FAIL, "服务器没有接受到图片文件", null);
            String imageName = "test.jpg";
            String imagePath = request.getServletContext().getRealPath("/static/image/");
            LoggerUtil.log("图片存储路径为：" + imagePath);
            File filePath = new File(imagePath, imageName);
            // 创建目录
            if (!filePath.getParentFile().exists()) {
                filePath.getParentFile().mkdirs();
            }
            // 覆盖上传
            if (filePath.exists()) {
                filePath.delete();
            }
            // 转存文件到指定路径
            file.transferTo(new File(imagePath + File.separator + imageName));
            return new HttpResponse(SystemConstant.SUCCEED, "图片上传成功", null);
        } catch (IOException e) {
            e.printStackTrace();
            LoggerUtil.log(e.getMessage());
        }

        return new HttpResponse(SystemConstant.FAIL, "图片上传失败", null);
    }

    /*
    @GetMapping("/newChatSession")
    public HttpResponse testNewChatSession() {
        ChatSession session = new ChatSession("测试会话");
        messageService.makePrivateSession(session, 9, 1);
        return new HttpResponse(SystemConstant.SUCCEED, null, session);
    }

    @GetMapping("/search")
    public HttpResponse searchUser(@RequestParam("account")String account){
        User user = userService.getUserByAccount(account);
        if(user == null){
            return new HttpResponse(SystemConstant.FAIL,"用户不存在",null);
        }
        user.setPassword(null);
        return new HttpResponse(SystemConstant.SUCCEED,null,user);
    }

    @GetMapping("/historyMessage")
    public HttpResponse getHistoryMessage(@RequestParam("chatSessionId") int chatSessionId) {
        List<Message> messages = messageService.listHistoryMessage(chatSessionId);

        Map<String, Object> result = new HashMap<>();
        result.put("list", messages);
        return new HttpResponse(SystemConstant.SUCCEED, null, result);
    }

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
    */

}
