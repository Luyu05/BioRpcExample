package bio.rpc.netcom.nio.server;

import bio.rpc.netcom.NetComServerFactory;
import bio.rpc.netcom.protocol.RpcRequest;
import bio.rpc.netcom.protocol.RpcResponse;
import bio.rpc.netcom.server.IServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * Created by luyu on 2017/11/21.
 */
public class NioServer extends IServer {

    @Override
    public void start(final int port) throws Exception {
        NioServer server = new NioServer();
        server.init(port);
    }

    private void init(int port) throws IOException, ClassNotFoundException {
        //打开一个ServerSocketChannel
        ServerSocketChannel ssc = ServerSocketChannel.open();
        //获取ServerSocketChannel绑定的socket
        ServerSocket ss = ssc.socket();
        //设置Socket绑定的端口
        ss.bind(new InetSocketAddress(port));
        //是这ServerSocketChannel为非阻塞模式
        ssc.configureBlocking(false);
        //打开选择器
        Selector selector = Selector.open();
        //将ServerSocketChannel注册到选择器上并监听accept事件
        ssc.register(selector, SelectionKey.OP_ACCEPT);

        processRequest(selector);
    }

    private void processRequest(Selector selector) throws IOException, ClassNotFoundException {
        while(true){
            //阻塞到至少有一个通道在注册的事件上就绪了
            int n = selector.select();
            //获取通道上已经就绪的事件集合
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while(iterator.hasNext()){
                SelectionKey sk = iterator.next();
                //通道上是否有可接受的连接
                if(sk.isAcceptable()){
                    ServerSocketChannel ssc1 = (ServerSocketChannel)sk.channel();
                    SocketChannel sc = ssc1.accept();
                    sc.configureBlocking(false);
                    sc.register(selector,SelectionKey.OP_READ);
                }else if(sk.isReadable()){
                    readDataFromSocket(sk);
                }
                iterator.remove();
            }
        }
    }

    private void readDataFromSocket(SelectionKey sk) throws IOException, ClassNotFoundException {

        SocketChannel sc = (SocketChannel)sk.channel();
        ByteBuffer bb = ByteBuffer.allocate(1024);
        sc.read(bb);
        ByteArrayInputStream bais = new ByteArrayInputStream(bb.array());
        ObjectInputStream oi = new ObjectInputStream(bais);
        RpcRequest request =(RpcRequest) oi.readObject();

        RpcResponse response = NetComServerFactory.invokeService(request, null);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(response);
        oos.flush();
        byte[] arr = baos.toByteArray();
        ByteBuffer bb1 = ByteBuffer.wrap(arr);
        oos.close();
        sc.write(bb1);
        sc.close();

    }

    @Override
    public void destroy() throws Exception {

    }
}
