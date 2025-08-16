package com.liuyan.personalblog.Service.Impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.liuyan.personalblog.Mapper.BlogMapper;
import com.liuyan.personalblog.Mapper.CommentMapper;
import com.liuyan.personalblog.POJO.Blog;
import com.liuyan.personalblog.POJO.PageResult;
import com.liuyan.personalblog.POJO.QueryParams.BlogQueryParam;
import com.liuyan.personalblog.POJO.User;
import com.liuyan.personalblog.Service.BlogService;
import com.liuyan.personalblog.Utils.CurrentHolder;
import org.springframework.stereotype.Service;

import javax.xml.stream.events.Comment;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class BlogServiceImpl implements BlogService {
    private final BlogMapper blogMapper;
    private final CommentMapper commentMapper;

    public BlogServiceImpl(BlogMapper blogMapper, CommentMapper commentMapper) {
        this.blogMapper = blogMapper;
        this.commentMapper = commentMapper;
    }


    @Override
    public void addBlog(Blog blog) {
        blog.setUserId(CurrentHolder.getCurrentId());
        blog.setCreateTime(LocalDateTime.now().toString());
        blogMapper.addBlog(blog);
    }

    @Override
    public List<Blog> getAllBlogs() {

        List<Blog> blogs = blogMapper.getAllBlogs();
        return blogs;
    }

    @Override
    public Blog getBlogInfro(Integer id) {
        Blog blog = blogMapper.getBlogInfro(id);
        return blog;
    }

    @Override
        public PageResult<Blog> searchBlogs(BlogQueryParam queryParam) {
        PageHelper.startPage(queryParam.getPage(),queryParam.getPageSize());
        List<Blog> blogs = blogMapper.searchBlogs(queryParam);
        Page< Blog> pages=(Page< Blog>)blogs;

        return new PageResult<>(pages.getTotal(),pages.getResult());
    }

    @Override
    public void deleteBlog(Integer id) {
        commentMapper.deleteCommentsByBlogId(id);
        blogMapper.deleteBlog(id);
    }

    @Override
    public List<Blog> getRandomBlogs(Integer limit) {
        return blogMapper.getRandomBlogs(limit);
    }

    @Override
    public User getUserInfroByBlogId(Integer id) {
        User user = blogMapper.getUserInfroByBlogId(id);
        return user;
    }
}
