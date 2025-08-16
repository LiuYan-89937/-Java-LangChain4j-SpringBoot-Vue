package com.liuyan.personalblog.Service;

import com.liuyan.personalblog.POJO.Comment;

import java.util.List;

public interface CommentService {
    List<Comment> getCommentsByBlogId(Integer id);

    void addHeartNum(Integer id);

    String getImageByUsername(String username);

    Comment addComment(String content, String username, Integer blogId);

    void deleteComment(Integer id);
}
