package bio.rpc.netcom.client;

import bio.rpc.netcom.protocol.RpcRequest;
import bio.rpc.netcom.protocol.RpcResponse;

/**
 * Created by luyu on 2017/11/9.
 */
public abstract class IClient {

    // ---------------------- config ----------------------
    protected String serverAddress;
    protected long timeoutMillis;

    public void init(String serverAddress,long timeoutMillis) {
        this.serverAddress = serverAddress;
        this.timeoutMillis = timeoutMillis;
    }

    // ---------------------- operate ----------------------

    public abstract RpcResponse send(RpcRequest request) throws Exception;

}
