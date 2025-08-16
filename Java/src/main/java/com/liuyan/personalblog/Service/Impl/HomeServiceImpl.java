package com.liuyan.personalblog.Service.Impl;

import com.liuyan.personalblog.Mapper.HomeMapper;
import com.liuyan.personalblog.POJO.Blog;
import com.liuyan.personalblog.Service.HomeService;
import com.liuyan.personalblog.Utils.CurrentHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
@Service
public class HomeServiceImpl implements HomeService {
    private final HomeMapper homeMapper;

    public HomeServiceImpl(HomeMapper homeMapper) {
        this.homeMapper = homeMapper;
    }


}
