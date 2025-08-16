package com.liuyan.personalblog.Controller;

import com.liuyan.personalblog.Mapper.UpdateMapper;
import com.liuyan.personalblog.POJO.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/board")
public class UpdateController {
    @Autowired
    private UpdateMapper updateMapper;
    @PostMapping("/update")
    public Result InsertUpdate(@RequestBody String content) {
        updateMapper.InsertUpdate(content);
        return Result.success();
    }

}
