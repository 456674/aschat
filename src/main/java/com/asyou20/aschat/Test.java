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

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }


        }
    }
