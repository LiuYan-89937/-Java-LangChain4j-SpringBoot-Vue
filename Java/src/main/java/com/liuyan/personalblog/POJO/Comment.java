package com.liuyan.personalblog.POJO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Comment {
    private Integer id;
    private String content;
    private String username;
    private Integer blogId;
    private Integer heartNum;
    private LocalDateTime createTime;
}
