package com.example.controller;

import com.example.entity.ChatSession;
import com.example.entity.HttpResponse;
import com.example.service.MessageService;
import com.example.util.LoggerUtil;
import com.example.util.SystemConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;

/**
 * 测试
 */
@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    MessageService messageService;

    /*
     * 测试能否向服务器发起请求
     */
    @GetMapping("/connect")
    public String test() {
        return "Hello，服务器正常工作！";
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

    @GetMapping("/newChatSession")
    public HttpResponse testNewChatSession() {
        ChatSession session = new ChatSession("测试会话");
        messageService.makePersonSession(session, 9, 1);
        return new HttpResponse(SystemConstant.SUCCEED, null, session);
    }

}
