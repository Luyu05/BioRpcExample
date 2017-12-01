package bio.rpc.netcom.aio.client;

import bio.rpc.netcom.client.IClient;
import bio.rpc.netcom.protocol.RpcRequest;
import bio.rpc.netcom.protocol.RpcResponse;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;

/**
 * Created by luyu on 2017/11/30.
 */
public class AioClient extends IClient {

    RpcResponse response;


    @Override
    public RpcResponse send(RpcRequest request) throws Exception {

        AsynchronousSocketChannel client = AsynchronousSocketChannel.open();
        client.connect(new InetSocketAddress("127.0.0.1",7080));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(request);
        oos.flush();
        byte[] arr = baos.toByteArray();
        ByteBuffer bb1 = ByteBuffer.wrap(arr);
        oos.close();
        Future<Integer> resultWrite = client.write(bb1);
        resultWrite.get();

        ByteBuffer bbln = ByteBuffer.allocate(1024);
        Future<Integer>  resultRead = client.read(bbln);
        resultRead.get();
        ByteArrayInputStream bln = new ByteArrayInputStream(bbln.array());
        ObjectInputStream in = new ObjectInputStream(bln);
        RpcResponse response = (RpcResponse) in.readObject();

        return  response;
    }


}
