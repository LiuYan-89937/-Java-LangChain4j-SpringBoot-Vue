package com.liuyan.personalblog.Controller;

import com.liuyan.personalblog.POJO.FollowParam;
import com.liuyan.personalblog.POJO.Result;
import com.liuyan.personalblog.Service.FollowerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/follow")
public class FollowerController {
    @Autowired
    private FollowerService followerService;
    @PostMapping("/followUserById")
    public Result followUserById(@RequestBody FollowParam param){
        followerService.followUserById(param.getUserId(), param.getFollowId());
        return Result.success();
    }
    @PostMapping("/unfollowUserById")
    public Result unfollowUserById(@RequestBody FollowParam param){
        followerService.unfollowUserById(param.getUserId(), param.getFollowId());
        return Result.success();
    }
    @GetMapping("/getFollowingList")
    public Result getFollowingList(Integer userId){
        List<Integer> list = followerService.getFollowingList(userId);
        return Result.success(list);
    }

}
