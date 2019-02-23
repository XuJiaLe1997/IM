package com.example.service.impl;

import com.example.entity.User;
import com.example.dao.UserDao;
import com.example.service.UserService;
import com.example.util.LoggerUtil;
import com.example.util.SystemConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Description: 用户服务
 * Date: 2018-11-05
 */

@Service("userService")
@Transactional
public class UserServiceImpl implements UserService {

    private UserDao userDao;

    @Autowired
    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public int register(User user) {
        if (userDao.selectUserByAccount(user.getAccount()) != null)
            return SystemConstant.ACCOUNT_EXIST;

        return userDao.insertUser(user);
    }

    @Override
    public int login(String account, String password) {
        // 获取数据库信息
        User user = userDao.selectUserByAccount(account);
        if (user == null) {
            LoggerUtil.log("账号" + account + "不存在");
            return SystemConstant.ACCOUNT_NOT_EXIST;
        }
        if (!password.equals(user.getPassword())) {
            LoggerUtil.log("密码不匹配");
            return SystemConstant.PASSWORD_NOT_MATCH;
        }
        return user.getUserId();
    }

    @Override
    public User getUserById(int userId) {
        return userDao.selectUserById(userId);
    }

    @Override
    public User getUserByAccount(String account) {
        return userDao.selectUserByAccount(account);
    }

    @Override
    public int uploadImage(MultipartFile file, int userId, String imagePath) {
        try {
            if (file == null)
                return SystemConstant.FAIL;
            String imageName = userId + ".jpg";
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
            return SystemConstant.SUCCEED;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return SystemConstant.FAIL;
    }

    @Override
    public int modifyUserInfo(User user) {
        // 拼装sql语句
        StringBuilder sb = new StringBuilder();
        if (user.getName() != null)
            sb.append("name='").append(user.getName()).append("',");
        if (user.getAge() != Integer.valueOf(SystemConstant.NO_PARAMETER)) {
            sb.append("age=").append(user.getAge()).append(",");
        }
        if (user.getSex() != Integer.valueOf(SystemConstant.NO_PARAMETER)) {
            sb.append("sex=").append(user.getAge()).append(",");
        }
        if (user.getArea() != null) {
            sb.append("area='").append(user.getArea()).append("',");
        }
        sb.deleteCharAt(sb.length()-1);
        int result =  userDao.updateUser(sb.toString(),user.getUserId());
        if(result == 1){
            return SystemConstant.SUCCEED;
        }else{
            return SystemConstant.FAIL;
        }
    }

    @Override
    public List<User> getFriendList(int userId) {
        return userDao.selectFriendList(userId);
    }
}
