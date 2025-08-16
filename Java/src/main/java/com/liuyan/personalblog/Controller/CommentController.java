package com.liuyan.personalblog.Controller;

import com.liuyan.personalblog.POJO.Comment;
import com.liuyan.personalblog.POJO.Result;
import com.liuyan.personalblog.Service.CommentService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comment")
public class CommentController {
    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }
    @GetMapping("/getCommentsByBlogId")
    public Result getCommentsByBlogId(Integer id) {
        return Result.success(commentService.getCommentsByBlogId(id));
    }
    @Transactional
    @PostMapping("/addHeartNum")
    public Result addHeartNum(Integer id) {
        commentService.addHeartNum(id);
        return Result.success();
    }
    @GetMapping("getImageByUsername")
    public Result getImageByUsername(String username) {
        return Result.success(commentService.getImageByUsername(username));
    }
    @Transactional
    @PostMapping("/addComment")
    public Result addComment(Integer blogId,String content,String username) {
        Comment comment = commentService.addComment(content, username, blogId);
        return Result.success(comment);
    }
    @Transactional
    @PostMapping("/deleteComment")
    public Result deleteComment(Integer id) {
        commentService.deleteComment(id);
        return Result.success();
    }

}
