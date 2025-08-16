package com.liuyan.personalblog.Service;

import com.liuyan.personalblog.POJO.LoginInfro;
import com.liuyan.personalblog.POJO.User;

public interface UserService {
    void registerUser(User user);

    LoginInfro loginUser(String username, String password);

     User getUserInfo(Integer id);

    void updateUserInfo(User user);
}
