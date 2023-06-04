package com.asyou20.aschat;

import javax.swing.*;
import java.io.IOException;

public class Test {
    public static void main(String[] args) {

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                ASClient client = new ASClient();
                try {
                    LoginRigister login = new LoginRigister(client);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }});
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
