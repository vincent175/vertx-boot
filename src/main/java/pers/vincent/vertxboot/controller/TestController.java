package pers.vincent.vertxboot.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import pers.vincent.vertxboot.http.annotation.RequestMapping;

/**
 * @author: Vincent
 * @date: 2021/9/15
 */
@Slf4j
@Controller
@RequestMapping("/test")
public class TestController {

    @RequestMapping("/method1")
    public String testMethod1(String param1, Integer param2, Long param3, Boolean param4) {
        log.info("receive params:{},{},{},{}", param1, param2, param3, param4);
        log.info("testMethod1");
        return "success";
    }
}
