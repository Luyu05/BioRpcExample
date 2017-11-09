package bio.rpc.netcom.bio.client;

import bio.rpc.netcom.Rules.RpcRequest;
import bio.rpc.netcom.Rules.RpcResponse;
import bio.rpc.netcom.client.IClient;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by luyu on 2017/11/9.
 */
public class BioClient extends IClient{

    @Override
    public RpcResponse send(RpcRequest request) throws Exception {

        byte[] requestBytes = serializer.serialize(request);
        Socket sc = new Socket("127.0.0.1",7080);
        OutputStream os = sc.getOutputStream();
        os.write(requestBytes);
        InputStream is = sc.getInputStream();
        byte[] responseBytes = new byte[]{};
        int tem = is.read(responseBytes);
        return (RpcResponse) serializer.deserialize(responseBytes, RpcResponse.class);

    }
}
