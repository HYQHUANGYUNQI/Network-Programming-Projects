import java.io.IOException;

public class Test {
    public static void main(String[] args) {
        try {
            new HttpProxy(8080);
            System.out.println("启动成功");
        } catch (IOException ioe) {
            System.err.println("发生错误");
            ioe.printStackTrace();
        }
        try {
            System.in.read();
        } catch (Throwable t) {
        }
    }
}
