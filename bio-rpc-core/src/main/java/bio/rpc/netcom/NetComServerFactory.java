package bio.rpc.netcom;

import bio.rpc.netcom.Rules.*;
import bio.rpc.netcom.annotation.RpcProviderService;
import bio.rpc.netcom.bio.server.BioServer;
import bio.rpc.netcom.server.IServer;
import bio.rpc.serialize.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.cglib.reflect.FastClass;
import org.springframework.cglib.reflect.FastMethod;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by luyu on 2017/11/9.
 */
public class NetComServerFactory implements ApplicationContextAware ,InitializingBean ,DisposableBean{
    private static final Logger logger = LoggerFactory.getLogger(NetComServerFactory.class);

    // ---------------------- server config ----------------------
    private int port = 7080;
    private int http_port = 7070;
    private Serializer serializer = Serializer.SerializeEnum.HESSIAN.serializer;

    public void setPort(int port) {
        this.port = port;
    }
    public void setHttp_port(int http_port) {
        this.http_port = http_port;
    }
    public void setSerializer(String serializer) {
        this.serializer = Serializer.SerializeEnum.match(serializer, Serializer.SerializeEnum.HESSIAN).serializer;
    }
    public Serializer getSerializer() {
        return serializer;
    }

    // ---------------------- server init ----------------------
    /**
     * init local rpc service map
     */
    private static Map<String, Object> serviceMap = new HashMap<String, Object>();
    public static RpcResponse invokeService(RpcRequest request, Object serviceBean) {
        if (serviceBean==null) {
            serviceBean = serviceMap.get(request.getClassName());
        }

        RpcResponse response = new RpcResponse();
        response.setRequestId(request.getRequestId());

        try {
            Class<?> serviceClass = serviceBean.getClass();
            String methodName = request.getMethodName();
            Class<?>[] parameterTypes = request.getParameterTypes();
            Object[] parameters = request.getParameters();

            /*Method method = serviceClass.getMethod(methodName, parameterTypes);
            method.setAccessible(true);
            return method.invoke(serviceBean, parameters);*/

            FastClass serviceFastClass = FastClass.create(serviceClass);
            FastMethod serviceFastMethod = serviceFastClass.getMethod(methodName, parameterTypes);

            Object result = serviceFastMethod.invoke(serviceBean, parameters);

            response.setResult(result);
        } catch (Throwable t) {
            t.printStackTrace();
            response.setError(t);
        }

        return response;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

        Map<String, Object> serviceBeanMap = applicationContext.getBeansWithAnnotation(RpcProviderService.class);
        if (serviceBeanMap!=null && serviceBeanMap.size()>0) {
            for (Object serviceBean : serviceBeanMap.values()) {
                String interfaceName = serviceBean.getClass().getAnnotation(RpcProviderService.class).value().getName();
                serviceMap.put(interfaceName, serviceBean);
            }
        }

    }

    private IServer server;
//    private IServer httpserver;
    @Override
    public void afterPropertiesSet() throws Exception {
        // init rpc provider
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                server = new BioServer();
                try {
                    server.start(port, serializer);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
//        t.setDaemon(true);
        t.start();
        Thread.sleep(3000);

        // init rpc-http provider
//        httpserver = NetComEnum.JETTY.serverClass.newInstance();
//        httpserver.start(http_port, serializer);

    }

    @Override
    public void destroy() throws Exception {

        server.destroy();
//        httpserver.destroy();
    }
}
