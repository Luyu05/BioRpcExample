package bio.rpc.example.client.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import bio.rpc.example.api.*;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by luyu on 2017/11/9.
 */
@Controller
public class IndexController {
    @Resource
    private IDemoService demoService;

    @RequestMapping("")
    @ResponseBody
    public List<String> http() throws Exception {
        String userName = "jack";

        List<String> list = new ArrayList<String>();
        list.add(demoService.sayHi(userName).toString());

        return list;
    }
}
