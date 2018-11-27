package com.example.dao;

import com.example.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

@Mapper
@Component("userMapper")
public interface UserMapper {

    int insertUser(User user);

    User selectUserById(int userId);

    User selectUserByAccount(String account);

    int updateImage(int imageId,int userId);
}
