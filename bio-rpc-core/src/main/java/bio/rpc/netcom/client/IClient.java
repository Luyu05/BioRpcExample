package bio.rpc.netcom.client;

import bio.rpc.netcom.Rules.RpcRequest;
import bio.rpc.netcom.Rules.RpcResponse;
import bio.rpc.serialize.Serializer;

/**
 * Created by luyu on 2017/11/9.
 */
public abstract class IClient {

    // ---------------------- config ----------------------
    protected String serverAddress;
    protected Serializer serializer;
    protected long timeoutMillis;

    public void init(String serverAddress, Serializer serializer, long timeoutMillis) {
        this.serverAddress = serverAddress;
        this.serializer = serializer;
        this.timeoutMillis = timeoutMillis;
    }

    // ---------------------- operate ----------------------

    public abstract RpcResponse send(RpcRequest request) throws Exception;

}
