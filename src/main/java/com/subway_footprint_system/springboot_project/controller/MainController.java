package com.subway_footprint_system.springboot_project.controller;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.*;

@EnableAutoConfiguration
@RestController
public class MainController {

    @CrossOrigin
    @RequestMapping("/hello")
    public String hello(String username){

        return "Hello,"+username+"!";
    }


    @PostMapping("/test")
    @CrossOrigin
    @ResponseBody
    public String test(@RequestBody Object object){

        return object.toString();
    }
}
