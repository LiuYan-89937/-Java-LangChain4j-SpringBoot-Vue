package com.liuyan.personalblog.POJO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FollowParam {
    private Integer userId;
    private Integer followId;
}
