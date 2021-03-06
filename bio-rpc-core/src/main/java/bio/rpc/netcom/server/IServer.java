package bio.rpc.netcom.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by luyu on 2017/11/9.
 */
public abstract class IServer {
    private static final Logger logger = LoggerFactory.getLogger(IServer.class);

    public abstract void start(final int port) throws Exception;

    public abstract void destroy() throws Exception;
}


