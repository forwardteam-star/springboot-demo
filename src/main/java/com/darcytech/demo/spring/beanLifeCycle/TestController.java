package com.darcytech.demo.spring.beanLifeCycle;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Maps;

@RestController
@RequestMapping("/life-cycle")
public class TestController {

    @Autowired
    private User user;

    @GetMapping("/user")
    public Map<String, Object> getUserLifeCycle() {
        Map<String, Object> resultMap = Maps.newHashMap();
        resultMap.put("beanName", user.getBeanName());
        resultMap.put("factory", user.getFactory());
        return resultMap;
    }

}
