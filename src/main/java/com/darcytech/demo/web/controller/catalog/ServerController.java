package com.darcytech.demo.web.controller.catalog;

import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.darcytech.demo.mysql.catalog.dao.ServerDao;
import com.darcytech.demo.mysql.catalog.entity.Server;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/catalog/servers")
@RequiredArgsConstructor
public class ServerController {

    private final ServerDao serverDao;

    @RequestMapping("")
    public List<Server> findAll() {
        return serverDao.findAll();
    }


}
