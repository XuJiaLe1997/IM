package com.example.controller;

import com.example.entity.HttpResponse;
import com.example.entity.User;
import com.example.service.UserService;
import com.example.util.SystemConstant;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        // 验证
        int userId = userService.login(account, password);
        HttpResponse response;
        if (userId == SystemConstant.ACCOUNT_NOT_EXIST) {
            response = new HttpResponse(SystemConstant.FAIL, "账号不存在", null);
        } else if (userId == SystemConstant.PASSWORD_NOT_MATCH) {
            response = new HttpResponse(SystemConstant.FAIL, "账号密码不匹配", null);
        } else {
            response = new HttpResponse(SystemConstant.SUCCEED, "登录成功", null);
        }
        // 登录成功后将userId保存到session
        session.setAttribute("userId", userId);
        return response;
    }

    /**
     * 注册
     */
    @PostMapping("/register")
    public HttpResponse register(@RequestParam("account") String account,
                                 @RequestParam("password") String password,
                                 @RequestParam("name") String name) {
        User user = new User();
        user.setAccount(account);
        user.setPassword(password);
        user.setName(name);
        // 注册
        int result = userService.register(user);
        if (result == SystemConstant.SUCCEED) {
            // 注册成功，返回用户ID
            return new HttpResponse(SystemConstant.SUCCEED, null,
                    new JSONObject().put("userId", user.getUserId()).toString());
        } else if (result == SystemConstant.ACCOUNT_EXIST) {
            return new HttpResponse(SystemConstant.FAIL, "账号已存在", null);
        }

        return new HttpResponse(SystemConstant.FAIL, null, null);
    }

    /**
     * 获取个人信息
     */
    @GetMapping("/info")
    public HttpResponse getUserInfo(HttpServletRequest request) {

        HttpSession session = request.getSession();
        int userId = (int) session.getAttribute("userId");
        User user = userService.getUserById(userId);
        Map<String, Object> param = new HashMap<>();
        param.put("userId", user.getUserId());
        param.put("name", user.getName());
        param.put("area", user.getArea());
        return new HttpResponse(SystemConstant.SUCCEED, null, param);
    }

    /**
     * 修改个人信息
     */
    @PostMapping("/info")
    public HttpResponse modifyUserInfo(@RequestParam(value = "name", required = false) String name,
                                       @RequestParam(value = "sex", required = false, defaultValue = SystemConstant.NO_PARAMETER) int sex,
                                       @RequestParam(value = "age", required = false, defaultValue = SystemConstant.NO_PARAMETER) int age,
                                       @RequestParam(value = "area", required = false) String area,
                                       HttpServletRequest request) {
        HttpSession session = request.getSession();
        int userId = (int) session.getAttribute("userId");
        User user = new User();
        user.setUserId(userId);
        user.setName(name);
        user.setSex(sex);
        user.setAge(age);
        user.setArea(area);

        int result = userService.modifyUserInfo(user);
        if (result == SystemConstant.SUCCEED)
            return new HttpResponse(SystemConstant.SUCCEED, null, null);
        return new HttpResponse(SystemConstant.FAIL, null, null);
    }

    /**
     * 获取好友列表
     */
    @GetMapping("/friendList")
    public HttpResponse getFriendList(HttpServletRequest request) {
        HttpSession session = request.getSession();
        int userId = (int) session.getAttribute("userId");

        List<User> friends = userService.getFriendList(userId);

        Map<String, Object> param = new HashMap<>();
        param.put("list", friends);
        return new HttpResponse(SystemConstant.SUCCEED, null, param);
    }

    /**
     * 查看其他用户信息
     */
    @GetMapping("/otherUserInfo")
    public HttpResponse getOtherUserInfo(@RequestParam("userId") int userId) {
        User user = userService.getUserById(userId);
        if (user == null) {
            return new HttpResponse(SystemConstant.FAIL, "用户不存在", null);
        }
        Map<String, Object> param = new HashMap<>();
        param.put("account", user.getAccount());
        param.put("name", user.getName());
        param.put("area", user.getArea());
        return new HttpResponse(SystemConstant.SUCCEED, null, param);
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


    /**
     * 根据账号查找指定用户
     * @param account 账号
     */
    @GetMapping("/search")
    public HttpResponse searchUser(@RequestParam("account")String account){
        User user = userService.getUserByAccount(account);
        if(user == null){
            return new HttpResponse(SystemConstant.FAIL,"用户不存在",null);
        }
        user.setPassword(null);
        return new HttpResponse(SystemConstant.SUCCEED,null,user);
    }

}
