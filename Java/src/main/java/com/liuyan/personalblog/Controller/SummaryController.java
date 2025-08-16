package com.liuyan.personalblog.Controller;

import com.liuyan.personalblog.Service.ConsultantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/summary")
public class SummaryController {
    @Autowired
    private ConsultantService consultantService;

    @PostMapping(value = "/chat", produces = "text/html;charset=utf-8")
    public String chat(@RequestBody String content) {
        String result = consultantService.chat(content);
        return result;
    }
}
