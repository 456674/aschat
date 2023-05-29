package com.asyou20.aschat.service;

import com.arcsoft.face.FaceEngine;
import com.arcsoft.face.toolkit.ImageInfo;

public interface FaceService {
    public String facesearch(ImageInfo imageInfo, FaceEngine faceEngine);
    public void faceload(byte[] bytes);
    public float getConfidence();
}
