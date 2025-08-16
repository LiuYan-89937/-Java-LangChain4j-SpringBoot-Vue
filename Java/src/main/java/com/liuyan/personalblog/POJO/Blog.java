package com.liuyan.personalblog.POJO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Blog {
    private Integer id;
    private Integer userId;
    private String title;
    private String content;
    private String username;
    private String createTime;
}
