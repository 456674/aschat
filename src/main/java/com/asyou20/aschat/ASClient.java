package com.asyou20.aschat;

import com.asyou20.aschat.dao.UserDao;
import com.asyou20.aschat.entity.User;
import org.apache.ibatis.session.SqlSession;

import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.asyou20.aschat.utils.daosource.getDao;

/** This is a documentation comment
 * @author gaojack
 * @version 1.0
 */
public class ASClient extends JFrame {
    private static final String SERVER_HOST = "124.221.134.131"; // 服务器的IP地址
    static final int SERVER_PORT = 12345; // 服务器的端口号
    private JTextArea chatArea;//文本显示区域
    private JTextField messageField;
    private JButton sendButton;
    private String username;
    private String targetUsername;
    DatagramSocket socket;
    InetAddress serverAddress;
    private List<String> onlineUsers;
    private JMenuBar jMenuBar;
    private  JMenu jMenu1;
    private  JMenu jMenu2;
    private JMenu jMenu3;

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
        jMenuBar = new JMenuBar();
        jMenu1 = new JMenu("连接对象配置");
        jMenu2 = new JMenu("用户信息");
        jMenu3 = new JMenu("登出");
        jMenu3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        jMenu2.addMenuListener(new MenuListener() {
            @Override
            public void menuSelected(MenuEvent e) {
                JOptionPane.showMessageDialog(null,username,"用户名",JOptionPane.INFORMATION_MESSAGE);
            }

            @Override
            public void menuDeselected(MenuEvent e) {

            }

            @Override
            public void menuCanceled(MenuEvent e) {

            }
        });
        jMenu1.addMenuListener(new MenuListener() {
            Icon icon = new Icon() {
                @Override
                public void paintIcon(Component c, Graphics g, int x, int y) {
                    g.setColor(Color.green);
                    g.fillOval(1,6,6,6); // 绘制一个绿色的圆点，表示在线状态
                }
                @Override
                public int getIconWidth() {
                    return 0;
                }
                @Override
                public int getIconHeight() {
                    return 0;
                }
            };
            @Override
            public void menuSelected(MenuEvent e) {
                //给服务器发送消息以获取在线列表
                String onlineMessage = "online|";
                byte[] onlinemessage = onlineMessage.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(onlinemessage, onlinemessage.length, serverAddress, SERVER_PORT); // 创建一个数据报包，指定目标地址和端口
                try {
                    socket.send(sendPacket); // 发送数据报包
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                try {
                    Thread.sleep(100); // 等待100毫秒，避免发送过快
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
                List<String> contacts = onlineUsers; // 获取在线用户列表
                for (String contact : contacts) {
                    JMenuItem item = new JMenuItem(contact,icon); // 为每个在线用户创建一个菜单项，带有图标
                    jMenu1.removeAll(); // 清空菜单
                    jMenu1.add(item); // 添加菜单项
                    item.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            targetUsername = contact; // 设置目标用户名为选中的联系人
                        }
                    });
                }
            }
            @Override
            public void menuDeselected(MenuEvent e) {
            }
            @Override
            public void menuCanceled(MenuEvent e) {
            }
        });

        jMenuBar.add(jMenu1);
        jMenuBar.add(jMenu2);
         jMenuBar.add(jMenu3);

        add(jMenuBar,BorderLayout.NORTH);

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
            socket = new DatagramSocket(); // 创建一个数据报套接字
            serverAddress = InetAddress.getByName(SERVER_HOST); // 获取服务器的IP地址对象
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void sendMessage() {
        try {

            String message = messageField.getText(); // 获取输入框中的文本
            StringBuffer sb = new StringBuffer();
            //拼接字符串，格式为：发送者|消息|接收者
            sb.append(username).append("|").append(message).append("|").append(targetUsername);
            byte[] sendData = sb.toString().getBytes("UTF-8");//解决中文乱码
            System.out.println(sb.toString());
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, SERVER_PORT); // 创建一个数据报包，指定目标地址和端口
            socket.send(sendPacket); // 发送数据报包
            appendToChatAreaMyself(message); // 在聊天区域显示自己发送的消息
            messageField.setText(""); // 清空输入框
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void start(String susername) throws IOException {

        username = susername; // 设置用户名
        String senddata = "login|"+susername;
        byte[] mybyte = senddata.getBytes("UTF-8");
        DatagramPacket sendPacket = new DatagramPacket(mybyte, mybyte.length, serverAddress, SERVER_PORT);
        socket.send(sendPacket);
        setVisible(true); // 显示界面
        setEnabled(true); // 启用界面
        receiveMessages(); // 开始接收消息
    }
    private void receiveMessages() {
        new Thread(new Runnable() { // 创建一个新的线程
            @Override
            public void run() {
                while (true) {
                    try {
                        byte[] receiveData = new byte[1024]; // 创建一个字节数组，用于存储接收到的数据
                        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length); // 创建一个数据报包，用于接收数据
                        socket.receive(receivePacket); // 接收数据报包
                        String message = new String(receivePacket.getData(), 0, receivePacket.getLength(),"UTF-8"); // 将数据报包中的数据转换为字符串，解决中文乱码
                        //从服务器获取在线用户
                        System.out.println(message);
                        if(message.startsWith("online")){ // 如果消息以"online|"开头，表示是服务器发送的在线用户列表
                            String[] parts = message.split("\\|"); // 按照"|"分割消息
                            onlineUsers = new ArrayList<>();
                            for(int s=1;s<parts.length;s++){

                                onlineUsers.add(parts[s]); // 将每个在线用户添加到列表中
                            }
                            onlineUsers.removeIf(onlineUser->onlineUser.equals(username)); // 从列表中移除自己的用户名
                        }
                        else{ // 否则，表示是其他用户发送的消息
                            appendToChatArea(message); // 在聊天区域显示接收到的消息
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start(); // 启动线程
    }
    private void appendToChatArea(String message) {
        SwingUtilities.invokeLater(new Runnable() { // 在事件分派线程中执行更新界面的任务
            @Override
            public void run() {

                String[] parts = message.split("\\|"); // 按照"|"分割消息

                chatArea.append(new Date(System.currentTimeMillis())+parts[0]+" ->"+parts[2]+"\n" +parts[0]+":  "+parts[1]+ "\n");
            }
        });
    }
    private void appendToChatAreaMyself(String message) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                chatArea.append(new Date(System.currentTimeMillis())+" ("+username+" -> "+targetUsername+")"+"\n" +username+":  "+message+ "\n");
            }
        });
    }
}
    class LoginRigister extends JFrame{
        public static ASClient getClient() {
            return client;
        }
        private static ASClient client;
        public LoginRigister(ASClient sclient) throws IOException {
            client = sclient;
            loginGUI();
        }
        boolean flag = false;
        private JButton loginButton;
        private JButton registerButton;
        private JButton faceloginButton;
        SqlSession sqlSession =  getDao();
        private UserDao userDao = sqlSession.getMapper(UserDao.class);
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
                        try {
                            login();
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                });
                registerButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        try {
                            register();
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
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
                        new Opencv();

                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }});
        }
        private void login() throws IOException {
            JTextField s_username = new JTextField();
            JTextField s_password = new JPasswordField();

            Object[] message = {
                    "用户名:",s_username,
                    "密码:",s_password
            };
            int option = JOptionPane.showConfirmDialog(null,message,"登录",JOptionPane.OK_CANCEL_OPTION);
            if(option==JOptionPane.OK_OPTION){
            if(s_username.getText()!=null&&s_password!=null){
                 if(userDao.getUserByUsername(s_username.getText())!=null){
                    User user = userDao.getUserByUsername(s_username.getText());
                    if(s_password.getText().equals(user.getPassword())){
                        client.start(s_username.getText());
                    }
                    else{
                        JOptionPane.showMessageDialog(null,"该用户名未注册！","登录未成功",JOptionPane.ERROR_MESSAGE);
                    }
                 }
                else{
                    JOptionPane.showMessageDialog(null,"该用户名未注册！","登录未成功",JOptionPane.ERROR_MESSAGE);
                 }
            }
            else{
                JOptionPane.showMessageDialog(null,"用户名或密码为空！","登录未成功",JOptionPane.ERROR_MESSAGE);
            }
            }
            else{
                System.out.println("登录取消");
            }
        }
        private void register() throws IOException {
            String input = JOptionPane.showInputDialog("Enter your username:");
            if (input != null && !input.trim().isEmpty()) {
                String newUsername = input.trim();
                RegisterInterface register = new RegisterImpl();
                register.register(newUsername);
                if(register.register(newUsername)==true){
                    client.start(newUsername);
                }
            }
        }
    }


