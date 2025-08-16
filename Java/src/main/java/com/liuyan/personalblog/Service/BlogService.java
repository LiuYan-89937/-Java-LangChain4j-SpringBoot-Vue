package com.liuyan.personalblog.Service;

import com.liuyan.personalblog.POJO.Blog;
import com.liuyan.personalblog.POJO.PageResult;
import com.liuyan.personalblog.POJO.QueryParams.BlogQueryParam;
import com.liuyan.personalblog.POJO.User;

import java.util.List;

public interface BlogService {

    void addBlog(Blog blog);

    List<Blog> getAllBlogs();

    Blog getBlogInfro(Integer id);

    PageResult<Blog> searchBlogs(BlogQueryParam queryParam);

    void deleteBlog(Integer id);

    List<Blog> getRandomBlogs(Integer limit);

    User getUserInfroByBlogId(Integer id);
}
