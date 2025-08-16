package com.liuyan.personalblog.Mapper;

import com.liuyan.personalblog.POJO.Blog;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface HomeMapper {
    List<Blog> getBlogs(Integer id);

    void addBlog(Blog blog);
}
