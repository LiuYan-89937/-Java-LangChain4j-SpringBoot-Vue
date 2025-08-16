package com.liuyan.personalblog.POJO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginInfro {
    private String username;
    private Integer id;
    private String avatar;
    private String token;
}
