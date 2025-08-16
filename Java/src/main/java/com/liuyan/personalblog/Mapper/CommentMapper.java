package com.liuyan.personalblog.Mapper;

import com.liuyan.personalblog.POJO.Comment;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CommentMapper {
    List<Comment> getCommentsByBlogId(Integer id);
    @Update("update comment set heart_num = heart_num + 1 where id = #{id}")
    void addHeartNum(Integer id);
    @Select("select image from user where username = #{username}")
    String getImageByUsername(String username);
    void addComment(String content, String username, Integer blogId);
    @Delete("delete from comment where id = #{id}")
    void deleteComment(Integer id);
    @Delete("delete from comment where blog_id = #{blogId}")
    void deleteCommentsByBlogId(Integer blogId);
}
