import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class HttpProxy {
    static long threadCount = 0;
    int Port;
    private ServerSocket ServerSocket;
    private Thread Thread;

    public HttpProxy(int port) throws IOException {
        Port = 8080;
        ServerSocket = new ServerSocket(Port);
        Thread = new Thread(new Runnable() {
            public void run() {
                try {
                    while (true)
                        new HTTPSession(ServerSocket.accept());
                } catch (IOException ioe) {
                    System.out.println("发生错误");
                }
            }
        });
        Thread.setDaemon(true);
        Thread.start();
    }

    /**
     * Stops the server.
     */


    public class HTTPSession implements Runnable {
        private Socket socket;

        public HTTPSession(Socket s) {
            socket = s;
            Thread t = new Thread(this);
            t.setDaemon(true);
            t.start();
        }

        @Override
        public void run() {
            try {
                ++threadCount;
                InputStream input = socket.getInputStream();
                if (input == null) {
                    return;
                }
                byte[] buffer = new byte[8192];
                int splitbyte = 0;
                int length = 0;
                {
                    int read = input.read(buffer, 0, 8192);
                    while (read > 0) {
                        length += read;
                        splitbyte = findHeaderEnd(buffer, length);
                        if (splitbyte > 0) {
                            break;
                        }
                        read = input.read(buffer, length, 8192 - length);
                    }
                    ByteArrayInputStream in = new ByteArrayInputStream(buffer, 0, length);
                    BufferedReader buf = new BufferedReader(new InputStreamReader(in));
                    Host host = new Host();
                    {   String string;
                        boolean flag = false;
                        while ((string = buf.readLine()) != null) {
                            if (string.toLowerCase().startsWith("host:")) {
                                host.host = string;
                                flag = true;
                            }
                            System.out.println(string);
                        }
                        if (!flag) {
                            socket.getOutputStream().write(
                                    "error!".getBytes());
                            socket.close();
                            return;
                        }
                    }

                    host.cal();
                    System.out.println("address:[" + host.address + "]port:"
                            + host.port + "\n-------------------\n");

                    try {
                        pipe(buffer, length,socket, socket.getInputStream(),
                                socket.getOutputStream(), host);
                    } catch (Exception e) {
                        System.out.println("发生错误");
                        e.printStackTrace();
                    }
                }

            } catch (Exception e) {
                System.out.println("发生错误");
            }
            System.out.println("threadcount:" + --threadCount);
        }

        /**
         * finad http header
         **/
        private int findHeaderEnd(final byte[] buff, int rlen) {
            int splitbyte = 0;
            while (splitbyte + 3 < rlen) {
                if (buff[splitbyte] == '\r' && buff[splitbyte + 1] == '\n'
                        && buff[splitbyte + 2] == '\r'
                        && buff[splitbyte + 3] == '\n')
                    return splitbyte + 4;
                splitbyte++;
            }
            return 0;
        }

        void pipe(byte[] request, int requestLength, Socket client,
                  InputStream clientIS, OutputStream clientOS, Host host)
                throws Exception {
            byte bytes[] = new byte[1024 * 32];
            Socket socket = new Socket(host.address, host.port);
            socket.setSoTimeout(5000);
            OutputStream os = socket.getOutputStream();
            InputStream is = socket.getInputStream();
            try {
                do {
                    os.write(request, 0, requestLength);
                    int resultLength;
                    try {
                        while ((resultLength = is.read(bytes)) != -1 && !socket.isClosed() && !socket.isClosed()) {
                            clientOS.write(bytes, 0, resultLength);
                        }
                    } catch (Exception e) {
                        System.out.println("发生错误");
                    }
                    System.out.println("发生错误");
                } while (!socket.isClosed() && (requestLength = clientIS.read(request)) != -1);
            } catch (Exception e) {
                System.out.println("发生错误" );
            }

            System.out.println("end,socket:" + socket.hashCode());
            os.close();
            is.close();
            clientIS.close();
            clientOS.close();
            socket.close();
            socket.close();

        }

        // target Host info
        final class Host {
            public String address;
            public int port;
            public String host;

            public boolean cal() {
                if (host == null) {
                    return false;
                }
                int start = host.indexOf(": ");
                if (start == -1) {
                    return false;
                }
                int next = host.indexOf(':', start + 2);
                if (next == -1) {
                    port = 80;
                    address = host.substring(start + 2);
                } else {
                    address = host.substring(start + 2, next);
                    port = Integer.valueOf(host.substring(next + 1));
                }
                return true;
            }
        }
    }


}