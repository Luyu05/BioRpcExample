package bio.rpc.netcom.Rules;

import java.io.Serializable;

/**
 * Created by luyu on 2017/11/9.
 */
public class RpcResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    private String requestId;
    private Throwable error;
    private Object result;

    public boolean isError() {
        return error != null;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public Throwable getError() {
        return error;
    }

    public void setError(Throwable error) {
        this.error = error;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "RpcResponse [requestId=" + requestId + ", error=" + error
                + ", result=" + result + "]";
    }

}

