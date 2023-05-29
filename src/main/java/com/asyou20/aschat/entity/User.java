package com.asyou20.aschat.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    private Integer id ;
    public String username = UUID.randomUUID().toString().replaceAll("-","").substring(0,8);
    public String password = UUID.randomUUID().toString().replaceAll("-","").substring(0,8);
    public byte[] base64;


}
