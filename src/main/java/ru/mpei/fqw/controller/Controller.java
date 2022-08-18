package ru.mpei.fqw.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mpei.fqw.service.ComtradeService;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class Controller {

    @Autowired
    private ComtradeService service;

    @GetMapping("/scopes")
    public String getJsonData(){
        return service.comtradeToJSON();
    }
}
