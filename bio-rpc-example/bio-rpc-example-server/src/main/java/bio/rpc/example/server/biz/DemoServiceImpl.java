package bio.rpc.example.server.biz;

import bio.rpc.example.api.*;
import bio.rpc.example.api.dto.UserDto;
import bio.rpc.netcom.annotation.RpcProviderService;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;

/**
 * Created by luyu on 2017/11/9.
 */
@RpcProviderService(IDemoService.class)
@Service
public class DemoServiceImpl implements IDemoService {
    @Override
    public UserDto sayHi(String name) {

        String word = MessageFormat.format("Hi {0}, from {1} as {2}",
                name, DemoServiceImpl.class.getName(), System.currentTimeMillis());

        return new UserDto(name, word);
    }
}
