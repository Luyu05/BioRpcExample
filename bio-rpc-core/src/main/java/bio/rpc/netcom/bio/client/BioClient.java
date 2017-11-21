package bio.rpc.netcom.bio.client;

import bio.rpc.netcom.protocol.RpcRequest;
import bio.rpc.netcom.protocol.RpcResponse;
import bio.rpc.netcom.client.IClient;

import java.io.*;
import java.net.Socket;

/**
 * Created by luyu on 2017/11/9.
 */
public class BioClient extends IClient{

    @Override
    public RpcResponse send(RpcRequest request) throws Exception {

        Socket sc = new Socket("127.0.0.1",7080);
        OutputStream os = sc.getOutputStream();
        ObjectOutputStream oo = new ObjectOutputStream(os);
        oo.writeObject(request);
        oo.flush();
        InputStream is = sc.getInputStream();
        ObjectInputStream oi = new ObjectInputStream(is);
        RpcResponse ret = (RpcResponse) oi.readObject();
        return  ret;

    }

}
