package com.liuyan.personalblog.Controller;

import com.liuyan.personalblog.POJO.LoginInfro;
import com.liuyan.personalblog.POJO.Result;
import com.liuyan.personalblog.POJO.User;
import com.liuyan.personalblog.Service.UserService;
import com.liuyan.personalblog.Utils.AliyunOSSOperator;
import com.liuyan.personalblog.Utils.CurrentHolder;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.SQLIntegrityConstraintViolationException;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    private final AliyunOSSOperator aliyunOSSOperator;

    public UserController(UserService userService, AliyunOSSOperator aliyunOSSOperator) {
        this.userService = userService;
        this.aliyunOSSOperator = aliyunOSSOperator;
    }
    @Transactional
    @PostMapping("/register")
    public Result registerUser(@RequestBody User user) {
        userService.registerUser(user);


        return Result.success();
    }
    @PostMapping("/login")
    public Result loginUser( String username,String password) {
        LoginInfro loginInfro = userService.loginUser(username, password);
        return loginInfro == null ? Result.error("用户名或密码错误") : Result.success(loginInfro);
    }
    @PostMapping("/logout")
    public Result logOut() {
        CurrentHolder.remove();
        return Result.success();
    }
    @PostMapping("/upload")
    public Result uploadAvatar(MultipartFile file) throws Exception {
        byte[] bytes = file.getBytes();
        String originalFilename = file.getOriginalFilename();
        String url = null;
        if (originalFilename != null) {
            url = aliyunOSSOperator.upload(bytes, originalFilename);
        }
        return Result.success(url);

    }
    @GetMapping("/getUserInfo")
    public Result getUserInfo(Integer id) {
        User userInfo = userService.getUserInfo(id);
        return Result.success(userInfo);
    }
    @Transactional
    @PostMapping("/updateUserInfo")
    public Result updateUserInfo(@RequestBody User user) {
        try {
            userService.updateUserInfo(user);
            return Result.success();
        } catch (Exception e) {
            if (e instanceof DuplicateKeyException ||
                    (e.getCause() != null && e.getCause() instanceof SQLIntegrityConstraintViolationException)) {
                return Result.error("用户名已存在");
            }
            return Result.error("更新用户信息失败: " + e.getMessage());
        }
    }


}
