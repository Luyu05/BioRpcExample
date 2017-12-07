package bio.rpc.netcom;

import bio.rpc.netcom.aio.client.AioClient;
import bio.rpc.netcom.netty.client.NettyClient;
import bio.rpc.netcom.protocol.RpcRequest;
import bio.rpc.netcom.protocol.RpcResponse;
import bio.rpc.netcom.bio.client.BioClient;
import bio.rpc.netcom.nio.client.NioClient;
import bio.rpc.netcom.client.IClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Date;
import java.util.UUID;

/**
 * Created by luyu on 2017/11/9.
 */
public class NetComClientProxy implements FactoryBean<Object>, InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(NetComClientProxy.class);
    // [tips01: save 30ms/100invoke. why why why??? with this logger, it can save lots of time.]

    // ---------------------- config ----------------------
    private String serverAddress;
    private Class<?> iface;
    private long timeoutMillis = 5000;
    private String netcom = "bio";

    public NetComClientProxy(){	}
    public NetComClientProxy(String serverAddress,Class<?> iface, long timeoutMillis) {
        this.setServerAddress(serverAddress);
        this.setIface(iface);
        this.setTimeoutMillis(timeoutMillis);
        try {
            this.afterPropertiesSet();
        } catch (Exception e) {
            logger.error("", e);
        }
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public void setIface(Class<?> iface) {
        this.iface = iface;
    }
    public void setTimeoutMillis(long timeoutMillis) {
        this.timeoutMillis = timeoutMillis;
    }

    public void setNetcom(String netcom){
        this.netcom = netcom;
    }

    // ---------------------- init client, operate ----------------------
    IClient client = null;

    @Override
    public void afterPropertiesSet() throws Exception {
        if(netcom.equalsIgnoreCase("bio")){
            client = new BioClient();
        }else if(netcom.equalsIgnoreCase("nio")){
            client = new NioClient();
        }else if(netcom.equalsIgnoreCase("aio")){
            client = new AioClient();
        }else if(netcom.equalsIgnoreCase("netty")){
            client = new NettyClient();
        }
        client.init(serverAddress, timeoutMillis);
    }

    @Override
    public Object getObject() throws Exception {
        return Proxy.newProxyInstance(Thread.currentThread()
                        .getContextClassLoader(), new Class[] { iface },
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

                        // request
                        RpcRequest request = new RpcRequest();
                        request.setRequestId(UUID.randomUUID().toString()+new Date());
                        request.setCreateMillisTime(System.currentTimeMillis());
                        request.setClassName(method.getDeclaringClass().getName());
                        request.setMethodName(method.getName());
                        request.setParameterTypes(method.getParameterTypes());
                        request.setParameters(args);

                        // send
                        RpcResponse response = client.send(request);

                        // valid response
                        if (response == null) {
                            logger.error(">>>>>>>>>>> bio-rpc response not found.");
                            throw new Exception(">>>>>>>>>>> bio-rpc response not found.");
                        }
                        if (response.isError()) {
                            throw response.getError();
                        } else {
                            return response.getResult();
                        }

                    }
                });
    }
    @Override
    public Class<?> getObjectType() {
        return iface;
    }
    @Override
    public boolean isSingleton() {
        return false;
    }

}
