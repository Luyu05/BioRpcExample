package bio.rpc.netcom.aio.server;

import bio.rpc.netcom.NetComServerFactory;
import bio.rpc.netcom.protocol.RpcRequest;
import bio.rpc.netcom.protocol.RpcResponse;
import bio.rpc.netcom.server.IServer;

import javax.annotation.Resource;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CountDownLatch;

/**
 * Created by luyu on 2017/12/6.
 */
public class AioServer extends IServer {

    private AsyncServerHandler serverHandler;

    @Override
    public void start(final int port) throws Exception {
        serverHandler = new AsyncServerHandler(port);
        new Thread(serverHandler,"Server").start();
    }

    @Override
    public void destroy() throws Exception {

    }

}

class AsyncServerHandler implements Runnable {

    public CountDownLatch latch;
    public AsynchronousServerSocketChannel channel;

    public AsyncServerHandler(int port) {
        try {
            channel = AsynchronousServerSocketChannel.open();
            channel.bind(new InetSocketAddress(port));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Resource
    public void run() {
        latch = new CountDownLatch(1);
        channel.accept(this,new AcceptHandler());
        try {
            latch.await();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

}
/**
 * 尖括号中第一个参数代表第49行，accept的返回值
 * 第二个参数代表accept方法的附加参数attachment的类型
 * */
class AcceptHandler implements CompletionHandler<AsynchronousSocketChannel,AsyncServerHandler>{

    @Override
    public void completed(AsynchronousSocketChannel result, AsyncServerHandler attachment) {
        attachment.channel.accept(attachment,this);
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        result.read(buffer,buffer,new ReadHandler(result));
    }

    @Override
    public void failed(Throwable exc, AsyncServerHandler attachment) {
        attachment.latch.countDown();
    }
}

class ReadHandler implements CompletionHandler<Integer,ByteBuffer> {

    private AsynchronousSocketChannel channel;

    public ReadHandler(AsynchronousSocketChannel channel){
        this.channel = channel;
    }

    @Override
    public void completed(Integer result, ByteBuffer attachment) {
        attachment.flip();
        byte[] message = new byte[attachment.remaining()];
        attachment.get(message);
        ByteArrayInputStream byteArray = new ByteArrayInputStream(message);
        ObjectInputStream oi;
        RpcRequest request = null;
        try {
            oi = new ObjectInputStream(byteArray);
            request =(RpcRequest) oi.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        doWrite(request);
    }

    private void doWrite(RpcRequest request) {

        RpcResponse response = NetComServerFactory.invokeService(request, null);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(baos);
            oos.writeObject(response);
            oos.flush();
            final byte[] arr = baos.toByteArray();
            ByteBuffer writeBuffer = ByteBuffer.wrap(arr);
            oos.close();
            channel.write(writeBuffer, writeBuffer, new CompletionHandler<Integer, ByteBuffer>() {
                @Override
                public void completed(Integer result, ByteBuffer attachment) {
                    if(attachment.hasRemaining()){
                        channel.write(attachment,attachment,this);
                    }else {
                        ByteBuffer readBuffer = ByteBuffer.allocate(1024);
                        channel.read(readBuffer,readBuffer,new ReadHandler(channel));
                    }
                }

                @Override
                public void failed(Throwable exc, ByteBuffer attachment) {
                    try {
                        channel.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void failed(Throwable exc, ByteBuffer attachment) {
        try {
            this.channel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
