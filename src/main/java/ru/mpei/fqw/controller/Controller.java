package ru.mpei.fqw.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mpei.fqw.service.ComtradeService;

@RestController
public class Controller {

    @Autowired
    private ComtradeService service;

    @GetMapping
    public String getJsonData(){
        return service.comtradeToJSON();
    }
}
