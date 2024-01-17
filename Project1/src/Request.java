import java.io.IOException;
import java.io.InputStream;

public class Request{
    private InputStream input;
    private String uri = null;
    private String start = null;
    public Request(InputStream input) {
        this.input = input;
    }
    public String getUri() {
        return this.uri;
    }

    public String getStart() {
        return this.start;
    }
    public void parseRequest() throws IOException {
        StringBuffer request = new StringBuffer(2048);
        int i;
        byte[] buffer = new byte[2048];
        //从输入中读入存入buffer，并将buffer长度赋给i。
        i = input.read(buffer);
        for (int j = 0; j < i; j++) {
            //将buffer中的内容存入request
            request.append((char)buffer[j]);
        }
        //处理GET方法
        if (request.toString().startsWith("GET")) {
            start = "GET";
            uri = parseUri(request.toString());
        }
        //处理HEAD方法
        else if(request.toString().startsWith("HEAD")) {
            start = "HEAD";
            uri = parseUri(request.toString());
        }
        System.out.print(request.toString());//从控制台输出request
    }
    public String parseUri(String requestString) {
        int index1, index2;
        index1 = requestString.indexOf(" ");
        if(index1 != -1) {
            index2 = requestString.indexOf(" ", index1 + 1);
            if(index2 > index1) {
                return requestString.substring(index1 + 1, index2);//获取字串（返回第一个）
            }
        }
        return null;
    }

}
