import com.sun.net.httpserver.Authenticator;

import java.io.*;

public class Response {
    private static final int BUFFER_SIZE = 1024;
    private String errorMessage = "HTTP/1.1 404 File Not Found\r\n" +
            "Content-Type:text/html\r\n" +
            "Content-Length:23\r\n" +
            "\r\n" +
            "<h1>File Not Found<h1>";
    Request request;
    OutputStream output = null;
    public Response(OutputStream output){
        this.output = output;
    }
    public void setRequest(Request request){
        this.request = request;
    }
    public Request getRequest() {
        return request;
    }
    public String getErrorMessage() {
        return this.errorMessage;
    }
    public void sendGet() throws IOException {
        byte[] bytes = new byte[BUFFER_SIZE];
        FileInputStream f = null;
        try{
            File file = new File(HttpServer.WEB_ROOT, request.getUri());
            if(file.exists()){
                f = new FileInputStream(file);
                String successMessage = "HTTP/1.1 200 \r\n" +
                        "Content-Type: text/html\r\n" +
                        "Content-Length: " + file.length() + "\r\n" + "\r\n";
                output.write(successMessage.getBytes());
                int ch = f.read(bytes, 0, BUFFER_SIZE);
                while (ch != -1) {
                    output.write(bytes, 0, BUFFER_SIZE);
                    ch = f.read(bytes, 0, BUFFER_SIZE);
                }
            } else {
                output.write(errorMessage.getBytes());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (f != null) {
                f.close();
            }
        }
    }

    public void sendHead() throws IOException {
        byte[] bytes = new byte[BUFFER_SIZE];
        FileInputStream f = null;
        try {
            File file = new File(HttpServer.WEB_ROOT, request.getUri());
            if(file.exists()) {
                String successMessage = "HTTP/1.1 200 OK\r\n" +
                            "Content-Type: text/html\r\n" +
                            "Content-Length: " + file.length() + "\r\n" + "\r\n";
                output.write(successMessage.getBytes());
            } else {
                output.write(errorMessage.getBytes());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (f != null) {
                f.close();
            }
        }

    }
}
