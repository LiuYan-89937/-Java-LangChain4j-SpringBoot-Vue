package com.liuyan.personalblog.Mapper;

import com.liuyan.personalblog.POJO.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {
    void registerUser(User user);

    User loginUser(String username, String password);
    @Select("select * from user where id=#{id}")
    User getUserInfo(Integer id);

    void updateUserInfo(User user);
}
