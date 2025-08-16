package com.liuyan.personalblog.Service.Impl;

import com.liuyan.personalblog.Mapper.UserMapper;
import com.liuyan.personalblog.POJO.LoginInfro;
import com.liuyan.personalblog.POJO.User;
import com.liuyan.personalblog.Service.UserService;
import com.liuyan.personalblog.Utils.JwtUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;

    public UserServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public void registerUser(User user) {
        user.setCreateTime(LocalDateTime.now());
        userMapper.registerUser(user);

    }

    @Override
    public LoginInfro loginUser(String username, String password) {
        User user = userMapper.loginUser(username, password);
        if (user == null)
            return null;
        Map<String, Object> map= Map.of("username", username, "password", password, "id", user.getId());
        LoginInfro loginInfro = new LoginInfro(username,  user.getId(), user.getImage(),JwtUtils.generateToken(map));
        return loginInfro;

    }

    @Override
    public User getUserInfo(Integer id) {
        return userMapper.getUserInfo(id);
    }

    @Override
    public void updateUserInfo(User user) {
        userMapper.updateUserInfo(user);

    }
}
