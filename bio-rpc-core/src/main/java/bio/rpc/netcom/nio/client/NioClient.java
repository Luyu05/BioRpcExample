package bio.rpc.netcom.nio.client;

import bio.rpc.netcom.protocol.RpcRequest;
import bio.rpc.netcom.protocol.RpcResponse;
import bio.rpc.netcom.client.IClient;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * Created by luyu on 2017/11/21.
 */
public class NioClient extends IClient {

    @Override
    public RpcResponse send(RpcRequest request) throws Exception {

        SocketChannel channel = SocketChannel.open(new InetSocketAddress("127.0.0.1",7080));
        //start write obj
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bout);
        out.writeObject(request);
        out.flush();
        byte[] arr = bout.toByteArray();
        ByteBuffer bb =ByteBuffer.wrap(arr);
        out.close();
        channel.write(bb);

        //start read obj
        ByteBuffer bbln = ByteBuffer.allocate(1024);
        channel.read(bbln);
        channel.close();
        ByteArrayInputStream bln = new ByteArrayInputStream(bbln.array());
        ObjectInputStream in = new ObjectInputStream(bln);
        RpcResponse response = (RpcResponse) in.readObject();
        return response;

    }

}
