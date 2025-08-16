package com.liuyan.personalblog.Mapper;

import com.liuyan.personalblog.POJO.Blog;
import com.liuyan.personalblog.POJO.QueryParams.BlogQueryParam;
import com.liuyan.personalblog.POJO.User;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface BlogMapper {
    void addBlog(Blog blog);

    List<Blog> getAllBlogs();
    @Select("select * from blog where id=#{id}")
    Blog getBlogInfro(Integer id);

    List<Blog> searchBlogs(BlogQueryParam queryParam);


    @Delete("delete from blog where id=#{id}")
    void deleteBlog(Integer id);

    List<Blog> getRandomBlogs(Integer limit);

    User getUserInfroByBlogId(Integer id);
}
