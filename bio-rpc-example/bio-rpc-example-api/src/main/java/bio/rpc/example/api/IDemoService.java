package bio.rpc.example.api;

import bio.rpc.example.api.dto.UserDto;

/**
 * Created by luyu on 2017/11/9.
 */
public interface IDemoService {
    public UserDto sayHi(String name);
}
