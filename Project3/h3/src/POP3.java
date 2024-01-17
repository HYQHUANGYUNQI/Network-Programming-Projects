import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class POP3{
    private static String POP3Server = "pop.qq.com";
    private static String USER = "937768986@qq.com";
    private static String PASSWORD = "zemdjavjuzsybaid";
    private static int PORT = 110;

    public void Get(POP3 mailpop){
        try {
            Socket sc = new Socket(POP3.POP3Server, POP3.PORT);
            DataInputStream input = new DataInputStream(sc.getInputStream());
            DataOutputStream out = new DataOutputStream(sc.getOutputStream());
            System.out.println(input.readLine());
            out.writeBytes("user " + POP3.USER + "\r\n");
            System.out.println(input.readLine());
            out.writeBytes("pass " + POP3.PASSWORD + "\r\n");
            System.out.println(input.readLine());
            out.writeBytes("stat" + "\r\n");
            String temp[] = input.readLine().split(" ");
            int count = Integer.parseInt(temp[1]);
            for (int i = 1; i <= count; i++) {
                out.writeBytes("retr " + i + "\r\n");
                while (true) {
                    String reply = input.readLine();
                    System.out.println(reply);
                    if (reply.toLowerCase().equals(".")) {
                        break;
                    }
                }
            }
            System.out.println();
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
