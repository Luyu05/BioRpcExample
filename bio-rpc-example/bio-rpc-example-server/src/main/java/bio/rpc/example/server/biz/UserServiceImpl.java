package bio.rpc.example.server.biz;

import bio.rpc.example.api.UserService;
import bio.rpc.netcom.annotation.RpcProviderService;
import org.springframework.stereotype.Service;

/**
 * Created by luyu on 2017/11/10.
 */
@RpcProviderService(UserService.class)
@Service
public class UserServiceImpl implements UserService {
    @Override
    public String introduce(String userName){
        String str = "My name is "+userName;
        return str;
    }
}
