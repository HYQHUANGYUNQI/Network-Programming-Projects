import org.omg.PortableServer.SERVANT_RETENTION_POLICY_ID;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class HttpServer {
    public static final String WEB_ROOT = "D:\\hyq\\计网\\h1" + File.separator + "webroot";
    private static final String SHUTDOWN_COMMAND = "/SHUTDOWN";
    private Boolean shutdown = false;

    public static void main(String[] args) throws IOException {
        HttpServer server = new HttpServer();
        server.await();
    }

    public void await() throws IOException {
        ServerSocket serverSocket = new ServerSocket(8080, 1, InetAddress.getByName("127.0.0.1"));
        InputStream input = null;
        OutputStream output = null;
        while (!shutdown) {
            Socket socket = serverSocket.accept();
            input = socket.getInputStream();
            Request request = new Request(input);
            request.parseRequest();
            output = socket.getOutputStream();
            Response response = new Response(output);
            response.setRequest(request);
            if (response.getRequest().getStart().equals("GET")) {
                response.sendGet();
            } else if (response.getRequest().getStart().equals("HEAD")) {
                response.sendHead();
            } else {
                response.getErrorMessage();
            }
            socket.close();
        }
    }
}
