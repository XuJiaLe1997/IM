package com.example.service;

import com.example.entity.User;
import org.springframework.web.multipart.MultipartFile;

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
     * 根据账号模糊查询用户信息
     * @param account 账号
     */
    User getUserByAccount(String account);

    /**
     * 上传用户头像
     * @param file 头像文件
     * @param userId 用户ID
     */
    int uploadImage(MultipartFile file, int userId, String imagePath);

}
