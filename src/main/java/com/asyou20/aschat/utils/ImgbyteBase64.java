package com.asyou20.aschat.utils;

import org.apache.commons.codec.binary.Base64;

public class ImgbyteBase64 {
    public static String ImgbytetoBase64(byte[] mybyte){
        return Base64.encodeBase64String(mybyte);
    }
    public static byte[] Base64toImgbyte(String base64str){
        return Base64.decodeBase64(base64str);
    }



}
