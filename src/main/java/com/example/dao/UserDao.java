package com.example.dao;

import com.example.entity.User;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component("userDao")
public interface UserDao {

    /**
     * 新增用户
     */
    @Insert("insert into user(account,password,name) values(#{account},#{password},#{name})")
    @Options(useGeneratedKeys = true,keyProperty = "userId",keyColumn = "userId")
    int insertUser(User user);

    /**
     * 根据ID获取用户信息
     */
    @Select("select * from user where userId = #{userId}")
    User selectUserById(@Param("userId") int userId);

    /**
     * 根据账号获取用户信息
     * @param account
     * @return
     */
    @Select("select * from user where account = #{account}")
    User selectUserByAccount(@Param("account") String account);

    /**
     * 插入好友关系
     */
    @Insert("insert into user_has_user(userId,friendId) values(#{userId},#{friendId})")
    int insertFriend(@Param("userId") int userId,@Param("friendId") int friendId);

    /**
     * 更新用户信息
     */
    @Update("update user set ${sql} where userId = #{userId}")
    int updateUser(@Param("sql") String sql, @Param("userId") int userId);

    /**
     * 获取好友列表
     */
    @Select("select userId,account,name,area from user where userId in" +
            "(select friendId from user_has_user where userId = #{userId})")
    List<User> selectFriendList(@Param("userId")int userId);
}
