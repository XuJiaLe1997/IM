package com.example.service;

import com.example.entity.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Description: 所有涉及用户信息的服务由此接口提供
 * Date: 2018-11-04
 */

public interface UserService {

    /**
     *  注册
     * @param user 用户信息
     */
    int register(User user);

    /**
     * 登录
     * @param account 账号
     * @param password 密码
     */
    int login(String account, String password);

    /**
     * 根据id查询
     */
    User getUserById(int userId);

    /**
     * 根据account查询用户信息
     * @param account 账号
     */
    User getUserByAccount(String account);

    /**
     * 上传用户头像
     * @param file 头像文件
     * @param userId 用户ID
     */
    int uploadImage(MultipartFile file, int userId, String imagePath);

    /**
     * 修改用户信息
     * @param user 用户信息
     */
    int modifyUser(User user);

    /**
     * 获取用户的好友列表
     * @param userId 用户ID
     */
    List<User> getFriendList(int userId);

}
