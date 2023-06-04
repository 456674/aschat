package com.asyou20.aschat;

import com.arcsoft.face.*;
import com.arcsoft.face.enums.DetectMode;
import com.arcsoft.face.enums.DetectOrient;
import com.arcsoft.face.enums.ImageFormat;
import com.arcsoft.face.toolkit.ImageFactory;
import com.arcsoft.face.toolkit.ImageInfo;
import com.asyou20.aschat.service.FaceService;
import com.asyou20.aschat.service.Impl.FaceServiceImpl;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.*;
import org.bytedeco.opencv.opencv_core.IplImage;
import org.bytedeco.opencv.opencv_core.Mat;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class Opencv extends JFrame {

    private  JButton registerButton;
    private JButton loginButton; // 添加登录按钮
    private JLabel statusLabel; // 添加状态标签
    private JButton testButton;
    private static OpenCVFrameGrabber grabber;
    private static CanvasFrame canvas;
    private static Frame frame;
    private static OpenCVFrameConverter.ToIplImage converter;

    private static String sdkLibPath = System.getProperty("user.dir")+"\\libs\\WIN64";//拼接字符串获取绝对路径
    static FaceInfo faceInfo = null;
    public List<FaceInfo> imageInfoList = new ArrayList<>();
    private static byte[] imageData;
    private JLabel imageLabel;
    private boolean flag;

    public boolean isFlag() {
        return flag;
    }

    public FaceEngine getFaceEngine() {
        return faceEngine;
    }

    private FaceEngine faceEngine;

    private FaceService faceService = new FaceServiceImpl();

    public String username;
    private ImageInfo imageInfo;
    private boolean foundflag = false;

    public static byte[] getImageData() {
        return imageData;

    }

    public  List<FaceInfo> getImageInfoList() {

        return imageInfoList;
    }

    public void testCamera() throws Exception {

        //OpenCVFrameRecorder recorder = new OpenCVFrameRecorder();
    }

    public void updateImage(BufferedImage image) {
        imageLabel.setIcon(new ImageIcon(image));
    }

    public void startCamera() throws Exception{
        new Thread(()->{while (true) {
            // 将原来在 while (true) 循环中的代码放在这里
            try {
                frame = grabber.grab();
            } catch (FrameGrabber.Exception e) {
                throw new RuntimeException(e);
            }
            IplImage iplImage = converter.convert(frame);
            imageData = new byte[iplImage.imageSize()];
            iplImage.imageData().get(imageData);
            int errorCode = faceEngine.detectFaces(imageData, iplImage.width(), iplImage.height(), ImageFormat.CP_PAF_BGR24, imageInfoList);
            //username = faceService.facesearch("1");
            // 将 IplImage 转换为 BufferedImage
            Mat mat = new Mat(iplImage);
            BufferedImage bufferedImage = new BufferedImage(mat.cols(), mat.rows(), BufferedImage.TYPE_3BYTE_BGR);
            byte[] data = ((DataBufferByte) bufferedImage.getRaster().getDataBuffer()).getData();
            mat.data().get(data);
            imageInfo = ImageFactory.bufferedImage2ImageInfo(bufferedImage);
            username = faceService.facesearch(imageInfo,faceEngine);
            if(!foundflag) {
                if (imageInfoList.size() > 0) {
                    if (!username.equals("0")) {
                        String text = "人脸检测状态： <font color='green'>" + "检测到 " + imageInfoList.size() + " 张人脸</font><br>识别用户： " + username +"<br>置信度:  "+faceService.getConfidence();
                        statusLabel.setText("<html>" + text + "</html>");
                        loginButton.setEnabled(true); // 启用登录按钮
                        registerButton.setEnabled(false);
                        foundflag = true;

                    } else {
                        String text = "人脸检测状态： <font color='green'>" + "检测到 " + imageInfoList.size() + " 张人脸</font><br>识别用户： 暂未识别到用户";
                        statusLabel.setText("<html>" + text + "</html>");
                        loginButton.setEnabled(false); //
                    }
                } else {
                    String text = "人脸检测状态： <font color='red'>" + "未检测到人脸</font>" + "<br>识别用户： ";
                    statusLabel.setText("<html>" + text + "</html>");
                    loginButton.setEnabled(false); // 禁用登录按钮
                }
            }

            // 更新显示的图像
            updateImage(bufferedImage);

            //canvas.showImage(frame);
        }}).start();

        //注册按钮的监听线程
        new Thread(()->{while(true){
            if(imageInfoList!=null){
            if(imageInfoList.size()==0){
                registerButton.setEnabled(false);

            }else{
                registerButton.setEnabled(true);
            }}
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }}).start();
    }
    Opencv() throws Exception {

        faceEngine = new FaceEngine(sdkLibPath);
        grabber = new OpenCVFrameGrabber(0);
        grabber.start();
        //canvas = new CanvasFrame("摄像头窗口");
        //canvas.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        //canvas.setAlwaysOnTop(true);
        this.setLayout(new GridLayout());
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension scrnsize = toolkit.getScreenSize();
        this.setBounds(scrnsize.width / 3 + 100, scrnsize.height / 3, 800, 400);
        loginButton = new JButton("登录");
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    LoginRigister.getClient().start(username);
                } catch (UnsupportedEncodingException ex) {
                    throw new RuntimeException(ex);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                setEnabled(false);
                dispose();
            }
        });

        registerButton = new JButton("人脸注册");
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FaceFeature facefearture = new FaceFeature();
                faceEngine.extractFaceFeature(imageInfo.getImageData(), imageInfo.getWidth(), imageInfo.getHeight(), imageInfo.getImageFormat(), imageInfoList.get(0),facefearture);
                faceService.faceload(facefearture.getFeatureData());
            }
        });
        statusLabel = new JLabel("状态");
        imageLabel = new JLabel();

        // ...
        this.add(imageLabel);
        this.add(loginButton);
        this.add(statusLabel);
        this.add(registerButton);


        this.setLocationRelativeTo(null);
        this.setVisible(true);
        converter = new OpenCVFrameConverter.ToIplImage();
        EngineConfiguration engineConfiguration = new EngineConfiguration();
        engineConfiguration.setDetectMode(DetectMode.ASF_DETECT_MODE_VIDEO);
        engineConfiguration.setDetectFaceOrientPriority(DetectOrient.ASF_OP_0_ONLY);
        engineConfiguration.setDetectFaceMaxNum(10);
        engineConfiguration.setDetectFaceScaleVal(16); //视频推荐16 图片推荐32
        //功能配置
        FunctionConfiguration functionConfiguration = new FunctionConfiguration();
        functionConfiguration.setSupportAge(true);
        functionConfiguration.setSupportFace3dAngle(true);
        functionConfiguration.setSupportFaceDetect(true);
        functionConfiguration.setSupportFaceRecognition(true);
        functionConfiguration.setSupportGender(true);
        functionConfiguration.setSupportLiveness(true);
        functionConfiguration.setSupportIRLiveness(true);
        engineConfiguration.setFunctionConfiguration(functionConfiguration);
        //初始化引擎
        int errorCode = faceEngine.init(engineConfiguration);
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    startCamera();
                } catch (Exception e) {

                }
            }
        });
        /*while(true) {
            frame = grabber.grab();
            IplImage iplImage = converter.convert(frame);
            imageData = new byte[iplImage.imageSize()];
            iplImage.imageData().get(imageData);
            errorCode = faceEngine.detectFaces(imageData, iplImage.width(), iplImage.height(), ImageFormat.CP_PAF_BGR24, imageInfoList);

            // 将 IplImage 转换为 BufferedImage
            Mat mat = new Mat(iplImage);
            BufferedImage bufferedImage = new BufferedImage(mat.cols(), mat.rows(), BufferedImage.TYPE_3BYTE_BGR);
            byte[] data = ((DataBufferByte) bufferedImage.getRaster().getDataBuffer()).getData();
            mat.data().get(data);

            // 更新显示的图像
            updateImage(bufferedImage);
            Thread.sleep(100);
            //canvas.showImage(frame);

        }

        }*/


    }


}
