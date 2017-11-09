package bio.rpc.netcom.annotation;

import java.lang.annotation.*;

/**
 * Created by luyu on 2017/11/9.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface RpcProviderService {
    Class<?> value();
}
