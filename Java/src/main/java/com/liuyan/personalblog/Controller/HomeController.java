package com.liuyan.personalblog.Controller;

import com.liuyan.personalblog.POJO.Blog;
import com.liuyan.personalblog.POJO.Result;
import com.liuyan.personalblog.Service.HomeService;
import com.liuyan.personalblog.Utils.CurrentHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/home")
public class HomeController {
    private final HomeService homeService;

    public HomeController(HomeService homeService) {
        this.homeService = homeService;
    }
}
