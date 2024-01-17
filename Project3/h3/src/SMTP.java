import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class SMTP {
    public String getMailServer() {
        return mailServer;
    }

    public void setMailServer(String mailServer) {
        this.mailServer = mailServer;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    String mailServer, sender, password, recipient, content;
    String lineFeet = "\r\n";
    Socket client;
    BufferedReader in;
    DataOutputStream out;
    private int port = 25;

    private boolean init() {
        boolean boo = true;
        if (mailServer == null || "".equals(mailServer)) {
            return false;
        }
        try {
            client = new Socket(mailServer, port);
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            out = new DataOutputStream(client.getOutputStream());
            String isConnect = getResponse();
            if (isConnect.startsWith("220")) {
                System.out.println("连接成功" );
            } else {
                System.out.println("连接失败" + isConnect);
                boo = false;
            }
        } catch (UnknownHostException e) {
            System.out.println("连接失败" );
            e.printStackTrace();
            boo = false;
        } catch (IOException e) {
            System.out.println("连接失败");
            e.printStackTrace();
            boo = false;
        }
        return boo;
    }

    private String sendMsg(String msg) {
        String result = null;
        try {
            out.writeBytes(msg);
            out.flush();
            result = getResponse();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private String getResponse() {
        String result = null;
        try {
            result = in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private void close() {
        try {
            out.close();
            in.close();
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public boolean sendMail() {
        if(client == null) {
            if (init()) {
                System.out.println("连接正常");
            } else {
                return false;
            }
        }
        if (sender == null || sender.isEmpty() || recipient == null || recipient.isEmpty()) {
            return  false;
        }
        //建立握手信息
        String result = sendMsg("HELO " + mailServer + lineFeet);
        if (isStartWith(result, "250")) {
            System.out.println("握手结果："+true);
        } else {
            System.out.println("握手失败" + result);
            return false;
        }

        //验证发信人信息
        String auth = sendMsg("AUTH LOGIN" + lineFeet);
        if (isStartWith(auth, "334")) {
        } else {
            System.out.println(auth);
            return false;
        }

        //验证用户信息
        String user = sendMsg(new String(Base64.encode(sender.getBytes()))
                + lineFeet);
        if (isStartWith(user, "334")) {
        } else {
            System.out.println(user);
            return false;
        }

        //验证密码信息
        String p = sendMsg(new String(Base64.encode(password.getBytes()))
                + lineFeet);
        if (isStartWith(p, "235")) {
        } else {
            System.out.println(p);
            return false;
        }

        //发送指令
        String f = sendMsg("Mail From:<" + sender + ">" + lineFeet);
        if (isStartWith(f, "250")) {
        } else {

            return false;
        }
        String toStr = sendMsg("RCPT TO:<" + recipient + ">" + lineFeet);
        if (isStartWith(toStr, "250")) {
        } else {
            return false;
        }

        String data = sendMsg("DATA" + lineFeet);
        if (isStartWith(data, "354")) {
        } else {
            return false;
        }

        StringBuilder message = new StringBuilder();
        //添加内容
        message.append("From:<" + sender + ">" + lineFeet);
        message.append("To:<" + recipient + ">" + lineFeet);
        message.append("Subject:计算机网络" + lineFeet);
        message.append("Date:2019/5/27 17:30" + lineFeet);
        message.append("Content-Type:text/plain;charset=\"utf-8\"" + lineFeet);
        message.append(lineFeet);
        message.append(content);
        message.append(lineFeet + "." + lineFeet);


        String conStr = sendMsg(message.toString());
        if (isStartWith(conStr, "250")) {
        } else {
            return false;
        }

        String quit = sendMsg("QUIT" + lineFeet);
        if (isStartWith(quit, "221")) {
        } else {
            return false;
        }
        close();
        return true;
    }

    private boolean isStartWith(String result, String s) {
        return result.startsWith(s);
    }

    public void Send(SMTP mail) {
        String mailServer, Sender, Password, Recipient, Content;
        Scanner sc = new Scanner(System.in);
        System.out.println("请输入smtp服务器地址：");
        mailServer = sc.nextLine();
        System.out.println("请输入发件人邮箱：");
        Sender = sc.nextLine();
        System.out.println("请输入发件人邮箱授权码");
        Password = sc.nextLine();
        System.out.println("请输入收件人邮箱");
        Recipient = sc.nextLine();
        System.out.println("请输入邮件内容：");
        Content = sc.nextLine();
        mail.setMailServer(mailServer);
        mail.setSender(Sender);
        mail.setPassword(Password);
        mail.setRecipient(Recipient);
        mail.setContent(Content);
        boolean boo = mail.sendMail();
        if (boo)
            System.out.println("发送成功！");
        else {
            System.out.println("发送失败");
        }
    }




}
