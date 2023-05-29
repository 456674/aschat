package com.asyou20.aschat.service.Impl;

import com.arcsoft.face.FaceEngine;
import com.arcsoft.face.FaceFeature;
import com.arcsoft.face.FaceInfo;
import com.arcsoft.face.FaceSimilar;
import com.arcsoft.face.toolkit.ImageInfo;
import com.asyou20.aschat.dao.UserDao;
import com.asyou20.aschat.entity.User;
import com.asyou20.aschat.service.FaceService;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.asyou20.aschat.utils.daosource.getDao;
@Service("FaceService")
public class FaceServiceImpl implements FaceService {
    SqlSession sqlSession =  getDao();
    private UserDao userDao = sqlSession.getMapper(UserDao.class);
private float confidence;
    public FaceServiceImpl() throws IOException {
    }

    @Override
    public String facesearch(ImageInfo imageInfo,FaceEngine faceEngine) {

        List<User> faceUser = userDao.selectAll();
        if(imageInfo!=null) {
            List<FaceInfo> faceInfoList = new ArrayList<FaceInfo>();
            int code1 = faceEngine.detectFaces(imageInfo.getImageData(), imageInfo.getWidth(), imageInfo.getHeight(), imageInfo.getImageFormat(), faceInfoList);
            System.out.println(code1);
            if (faceInfoList.size() != 0) {
                FaceFeature targetFacefearture = new FaceFeature();
                faceEngine.extractFaceFeature(imageInfo.getImageData(), imageInfo.getWidth(), imageInfo.getHeight(), imageInfo.getImageFormat(), faceInfoList.get(0), targetFacefearture);
                for (User faceuser : faceUser) {
                    byte[] facefeature = faceuser.getBase64();
                    //byte[] bytes = ImgbyteBase64.Base64toImgbyte(facebase64);
                    FaceFeature sourceFacefeature = new FaceFeature();
                    //System.out.println(new String(targetFacefearture.getFeatureData()));
                    sourceFacefeature.setFeatureData(facefeature);
                    FaceSimilar faceSimilar = new FaceSimilar();
                    int code2 = faceEngine.compareFaceFeature(targetFacefearture, sourceFacefeature, faceSimilar);
                    if (faceSimilar.getScore() > 0.9) {
                        confidence = faceSimilar.getScore();
                        return faceuser.getUsername();
                    } else {

                        return "0";
                    }
                }
            } else {
                return String.valueOf(0);
            }
        }
        else{
            return String.valueOf(0);
        }

    return null;
}

    @Override
    public void faceload(byte[] bytes) {
          User user = new User();
          user.setBase64(bytes);
          userDao.insertUser(user);
          sqlSession.commit();
    }

    @Override
    public float getConfidence() {
        return confidence;
    }
}
