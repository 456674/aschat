package com.asyou20.aschat;

import javax.swing.*;

public class Test {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                ASClient client = new ASClient();
                LoginRigister login = new LoginRigister(client);



        }});
       /* while (true) {
            if (Opencv.getImageInfoList() != null) {
                if(Opencv.getImageInfoList().size()!=0){
                System.out.println(Opencv.getImageInfoList());
                String s = ImgbyteBase64.ImgbytetoBase64(Opencv.getImageData());
                System.out.println(s);}
            }  */
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }


        }
    }
