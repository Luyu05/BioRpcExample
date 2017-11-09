package bio.rpc.netcom.bio.server;

import bio.rpc.netcom.NetComServerFactory;
import bio.rpc.netcom.Rules.RpcRequest;
import bio.rpc.netcom.Rules.RpcResponse;
import bio.rpc.netcom.server.IServer;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by luyu on 2017/11/9.
 */
public class BioServer extends IServer{



    @Override
    public void start(final int port) throws Exception {

        BioServer server = new BioServer();
        server.init(port);

    }

    public void init(int PORT) {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            while (true) {
                // 一旦有堵塞, 则表示服务器与客户端获得了连接
                Socket client = serverSocket.accept();
                // 处理这次连接
                new HandlerThread(client);
            }
        } catch (Exception e) {
            System.out.println("服务器异常: " + e.getMessage());
        }
    }

    private class HandlerThread implements Runnable {
        private Socket socket;
        public HandlerThread(Socket client) {
            socket = client;
            new Thread(this).start();
        }

        public void run() {
            try {
                InputStream input = socket.getInputStream();
                ObjectInputStream oi = new ObjectInputStream(input);
                RpcRequest request = (RpcRequest) oi.readObject();
                RpcResponse response = NetComServerFactory.invokeService(request, null);
                OutputStream os = socket.getOutputStream();
                ObjectOutputStream oo = new ObjectOutputStream(os);
                oo.writeObject(response);
                oo.flush();
                oo.close();
            } catch (Exception e) {
                System.out.println("服务器 run 异常: " + e.getMessage());
            } finally {
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (Exception e) {
                        socket = null;
                        System.out.println("服务端 finally 异常:" + e.getMessage());
                    }
                }
            }
        }
    }

    @Override
    public void destroy() throws Exception {

    }

}
