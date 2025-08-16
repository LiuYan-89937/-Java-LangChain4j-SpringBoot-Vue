package com.liuyan.personalblog.POJO.QueryParams;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BlogQueryParam {
    private Integer page=1;
    private Integer pageSize=12;
    private String title;
    private String content;
    private String username;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startTime;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endTime;
}
