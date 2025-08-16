package com.liuyan.personalblog.Service.Impl;

import com.liuyan.personalblog.Mapper.CommentMapper;
import com.liuyan.personalblog.POJO.Comment;
import com.liuyan.personalblog.Service.CommentService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {
    private final CommentMapper commentMapper;

    public CommentServiceImpl(CommentMapper commentMapper) {
        this.commentMapper = commentMapper;
    }

    @Override
    public List<Comment> getCommentsByBlogId(Integer id) {
        List<Comment> comments = commentMapper.getCommentsByBlogId(id);
        return comments;
    }

    @Override
    public void addHeartNum(Integer id) {
        commentMapper.addHeartNum(id);

    }

    @Override
    public String getImageByUsername(String username) {
        return commentMapper.getImageByUsername(username);
    }

    @Override
    public Comment addComment(String content, String username, Integer blogId) {
        commentMapper.addComment(content, username, blogId);
        return new Comment(null, content, username, blogId, 0, LocalDateTime.now());
    }

    @Override
    public void deleteComment(Integer id) {
        commentMapper.deleteComment(id);

    }
}
