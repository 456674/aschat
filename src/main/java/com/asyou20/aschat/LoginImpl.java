package com.asyou20.aschat;

import com.asyou20.aschat.service.FaceService;



public class LoginImpl  implements LoginInterface{

    private FaceService faceService;
    boolean flag;
    @Override
    public boolean login(String username) {
        return true;
    }
    @Override
    public boolean facelogin() {
        return false;
    }
    @Override
    public boolean slogin() {
        return false;
    }
}
