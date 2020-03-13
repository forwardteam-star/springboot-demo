package com.darcytech.demo.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.darcytech.demo.WebProperties;
import com.darcytech.demo.web.security.LoginOp;
import com.darcytech.demo.web.security.Operator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {

    @Autowired
    private WebProperties webProperties;

    @GetMapping("find-by-userId")
    public String helloWorld(@LoginOp Operator operator) {
        return operator.getOperatorName();
    }

    @GetMapping("show-name")
    public String showName() {
        return webProperties.getName();
    }
}
