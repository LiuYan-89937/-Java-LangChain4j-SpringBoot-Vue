package com.liuyan.personalblog.Service;

import java.util.List;

public interface FollowerService {
    void followUserById(Integer userId, Integer followId);
    List<Integer> getFollowingList(Integer userId);

    void unfollowUserById(Integer userId, Integer followId);
}
