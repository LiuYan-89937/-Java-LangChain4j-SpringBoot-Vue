package com.liuyan.personalblog.Controller;

import com.liuyan.personalblog.POJO.Blog;
import com.liuyan.personalblog.POJO.QueryParams.BlogQueryParam;
import com.liuyan.personalblog.POJO.Result;
import com.liuyan.personalblog.Service.BlogService;
import com.liuyan.personalblog.Utils.CurrentHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/blog")
public class BlogController {
    private final BlogService blogService;

    public BlogController(BlogService blogService) {
        this.blogService = blogService;
    }
    @PostMapping("/addBlog")
    public Result addBlog(@RequestBody Blog blog){
        blogService.addBlog(blog);
        return Result.success();
    }
    @GetMapping("/getAllBlogs")
    public Result getAllBlogs(){
        List<Blog> blogs = blogService.getAllBlogs();
        return Result.success(blogs);
    }
    @GetMapping("/getBlogInfro")
    public Result getBlogInfro(Integer id){
        Blog blog = blogService.getBlogInfro(id);
        return Result.success(blog);
    }
    @PostMapping("/searchBlogs")
    public Result searchBlogs(@RequestBody BlogQueryParam queryParam){
        return Result.success( blogService.searchBlogs(queryParam));
    }
    @PostMapping("/deleteBlog")
    public Result deleteBlog(Integer id){
        blogService.deleteBlog(id);
        return Result.success();
    }
    @GetMapping("/getRandomBlogs")
    public Result getRandomBlogs(){
        Integer limit=12;
        List<Blog> blogs = blogService.getRandomBlogs(limit);
        return Result.success(blogs);
    }
    @GetMapping("/getUserInfroByBlogId")
    public Result getUserInfroByBlogId(Integer id){
        return Result.success(blogService.getUserInfroByBlogId(id));
    }

}
