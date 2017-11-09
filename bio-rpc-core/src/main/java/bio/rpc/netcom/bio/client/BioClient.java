package bio.rpc.netcom.bio.client;

import bio.rpc.netcom.Rules.RpcRequest;
import bio.rpc.netcom.Rules.RpcResponse;
import bio.rpc.netcom.client.IClient;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by luyu on 2017/11/9.
 */
public class BioClient extends IClient{

    @Override
    public RpcResponse send(RpcRequest request) throws Exception {

//        byte[] requestBytes = serializer.serialize(request);
        Socket sc = new Socket("127.0.0.1",7080);
        OutputStream os = sc.getOutputStream();
        ObjectOutputStream oo = new ObjectOutputStream(os);
        oo.writeObject(request);
        oo.flush();
        InputStream is = sc.getInputStream();
        ObjectInputStream oi = new ObjectInputStream(is);
        RpcResponse ret = (RpcResponse) oi.readObject();
        return  ret;



//        os.write(requestBytes);
//        os.flush();
//        InputStream is = sc.getInputStream();
//        byte[] responseBytes = new byte[]{};
//        int tem = is.read(responseBytes);
//        return (RpcResponse) serializer.deserialize(responseBytes, RpcResponse.class);

    }
}
