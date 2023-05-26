package com.asyou20.aschat;

public class LoginImpl  implements LoginInterface{
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
