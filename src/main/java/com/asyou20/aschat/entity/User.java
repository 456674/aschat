package com.asyou20.aschat.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class User {

    private Integer id;
    private String username;
    private String password;
    private String facebase64;

}
