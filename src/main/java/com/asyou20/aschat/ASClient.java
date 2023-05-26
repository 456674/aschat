package com.asyou20.aschat;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
public class ASClient extends JFrame {
    private static final String SERVER_HOST = "127.0.0.1"; // 服务器的IP地址
    static final int SERVER_PORT = 12345; // 服务器的端口号
    private JTextArea chatArea;
    private JTextField messageField;
    private JButton sendButton;

    DatagramSocket socket;
    InetAddress serverAddress;
    String username;
    public ASClient() {
        initializeGUI();
        initializeSocket();
    }
    private void initializeGUI() {
        setTitle("UDP Chat Client");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 500);
        setLocationRelativeTo(null);

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(chatArea);

        messageField = new JTextField();
        sendButton = new JButton("Send");

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(messageField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH);
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });
        messageField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });
        setEnabled(false); // 禁用界面，直到登录成功
    }
    private void initializeSocket() {
        try {
            socket = new DatagramSocket();
            serverAddress = InetAddress.getByName(SERVER_HOST);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendMessage() {
        try {
            String message = messageField.getText();
            byte[] sendData = message.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, SERVER_PORT);
            socket.send(sendPacket);
            messageField.setText("");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void start() {
        setVisible(true);
        setEnabled(true);
        receiveMessages();
    }
    private void receiveMessages() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        byte[] receiveData = new byte[1024];
                        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                        socket.receive(receivePacket);
                        String message = new String(receivePacket.getData(), 0, receivePacket.getLength());
                            appendToChatArea(message);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
    private void appendToChatArea(String message) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                chatArea.append(message + "\n");
            }
        });
    }}
    class LoginRigister extends JFrame{
        private ASClient client;
        public LoginRigister(ASClient sclient){
            client = sclient;
            loginGUI();
        }

        boolean flag = false;
        private JButton loginButton;
        private JButton registerButton;
        private JButton faceloginButton;
        private void loginGUI() {
            try {
                Toolkit toolkit = Toolkit.getDefaultToolkit();
                Dimension scrnsize = toolkit.getScreenSize();
                this.setBounds(scrnsize.width/3+100,scrnsize.height/3,800,400);
                this.setVisible(true);
                //this.setDefaultCloseOperation(EXIT_ON_CLOSE);
                //Container container = this.getContentPane();
                //container.setLayout(null);
                this.setLayout(new GridLayout());
                loginButton = new JButton("登录");
                registerButton = new JButton("注册");
                faceloginButton = new JButton("人脸登录");
                loginButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        login();
                    }
                });
                registerButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        register();
                    }
                });
                faceloginButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        faceGUI();
                    }
                });
                this.add(loginButton);
                this.add(registerButton);
                this.add(faceloginButton);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        private void faceGUI(){
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    try {
                        Opencv opencv = new Opencv();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }

                }});

        }
        private void login() {
            String input = JOptionPane.showInputDialog("Enter your username:");
            if (input != null && !input.trim().isEmpty()) {
                String username = input.trim();
                LoginInterface loginx = new LoginImpl();
                if(loginx.login(username)==true){
                    client.start();
                }
                //sendLoginRequest();
            } else {
                //System.exit(0 );
            }
        }
        private void register() {
            String input = JOptionPane.showInputDialog("Enter your username:");
            if (input != null && !input.trim().isEmpty()) {
                String newUsername = input.trim();
                RegisterInterface register = new RegisterImpl();
                register.register(newUsername);
                if(register.register(newUsername)==true){
                    client.start();
                }
            }
        }
        /*private void sendRegistrationRequest(String username, String password) {
            try {
                String message = "REGISTER|" + username + "|" + password;
                byte[] sendData = message.getBytes();

                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, client.serverAddress, ASClient.SERVER_PORT);
                client.socket.send(sendPacket);
                JOptionPane.showMessageDialog(this, "Registration successful. You can now log in.");
                login();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }*/
    }


