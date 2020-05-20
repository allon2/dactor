package cn.ymotel.example.imgcode;

import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SpringRequestMapping {
    @RequestMapping(value = "*.service")
    public Object execute() throws Exception {
//        throw new Exception("错误");
        SpringResponse springResponse=new SpringResponse();
        springResponse.setResultByteString("ttttt");
        String[] values=new String[2];
        values[0]="value0";
        values[1]="values2";
        springResponse.setValues(values);
        return springResponse;
    }
}
