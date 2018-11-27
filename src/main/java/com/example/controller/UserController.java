package com.example.controller;

import com.example.entity.HttpResponse;
import com.example.entity.User;
import com.example.service.UserService;
import com.example.util.LoggerUtil;
import com.example.util.SystemConstant;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.POST;
import java.io.File;
import java.io.IOException;

/**
 * Description: 用户控制类
 * Date: 2018-11-06
 */

@RestController
@RequestMapping("/user")
public class UserController {

    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    /**
     * 登录
     */
    @PostMapping("/login")
    public HttpResponse login(@RequestParam("account") String account,
                              @RequestParam("password") String password,
                              HttpSession session) {
        int userId = userService.login(account, password);
        HttpResponse response;
        if (userId == SystemConstant.ACCOUNT_NOT_EXIST) {
            response = new HttpResponse(SystemConstant.FAIL, "账号不存在", null);
        } else if (userId == SystemConstant.PASSWORD_NOT_MATCH) {
            response = new HttpResponse(SystemConstant.FAIL, "账号密码不匹配", null);
        } else {
            response = new HttpResponse(SystemConstant.SUCCEED, "登录成功", null);
        }
        // 将userId保存到session
        session.setAttribute("userId", userId);
        return response;
    }

    /**
     * 注册
     */
    @PostMapping("/register")
    public HttpResponse register(@RequestParam("account") String account,
                                 @RequestParam("password") String password) {
        User user = new User();
        user.setAccount(account);
        user.setPassword(password);
        int result = userService.register(user);
        if (result == SystemConstant.SUCCEED) {
            return new HttpResponse(SystemConstant.SUCCEED, null, null);
        } else if (result == SystemConstant.ACCOUNT_EXIST) {
            return new HttpResponse(SystemConstant.FAIL, "账号已存在", null);
        }

        return new HttpResponse(SystemConstant.FAIL, null, null);
    }

    /**
     * 修改个人信息
     */
    @PostMapping("/info")
    public HttpResponse modifyUserInfo(@RequestParam(value = "name", required = false) String name,
                                       @RequestParam(value = "sex", required = false) int sex,
                                       @RequestParam(value = "age", required = false) int age,
                                       HttpServletRequest request) {
        /*
          待补充
         */
        return new HttpResponse(SystemConstant.FAIL, "此功能尚未开放", null);
    }

    /**
     * 获取个人信息
     */
    @GetMapping("/info")
    public HttpResponse getUserInfo(HttpServletRequest request) {
        /*
          待补充
         */
        return new HttpResponse(SystemConstant.FAIL, "此功能尚未开放", null);
    }

    /**
     * 获取好友列表
     */
    @GetMapping("/friendList")
    public HttpResponse getFriendList() {
        return new HttpResponse(SystemConstant.FAIL, "此功能尚未开放", null);
    }

    /**
     * 查看好友信息
     */
    @GetMapping("/friendInfo")
    public HttpResponse getFriendInfo(@RequestParam("targetId") String targetId) {
        return new HttpResponse(SystemConstant.FAIL, "此功能尚未开放", null);
    }

    /**
     * 上传头像
     *
     * @param file 头像文件
     */
    @PostMapping("/image")
    public HttpResponse uploadImage(@RequestParam("image") MultipartFile file,
                                    HttpSession session, HttpServletRequest request) {
        int userId = (int) session.getAttribute("userId");
        String imagePath = request.getServletContext().getRealPath("/static/image/");
        int result = userService.uploadImage(file, userId, imagePath);
        if (result == 1) {
            return new HttpResponse(SystemConstant.SUCCEED, null, null);
        } else {
            return new HttpResponse(SystemConstant.FAIL, null, null);
        }
    }

}
