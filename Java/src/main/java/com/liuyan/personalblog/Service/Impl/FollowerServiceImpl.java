package com.liuyan.personalblog.Service.Impl;

import com.liuyan.personalblog.Service.FollowerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class FollowerServiceImpl implements FollowerService {

    @Autowired
    private RedisTemplate<String, Integer> redisTemplate;

    @Override
    public void followUserById(Integer userId, Integer followId) {
        // 将followId添加到userId的关注列表中
        redisTemplate.opsForList().leftPush(userId+"following:" , followId);
    }

    @Override
    public List<Integer> getFollowingList(Integer userId) {
        // 获取用户关注列表
        String key = userId+ "following:";
        Long size = redisTemplate.opsForList().size(key);
        if (size != null && size > 0) {
            return redisTemplate.opsForList().range(key, 0, size - 1);
        }
        return List.of(); // 返回空列表
    }

    @Override
    public void unfollowUserById(Integer userId, Integer followId) {
        redisTemplate.opsForList().remove(userId + "following:", 0, followId);

    }
}
