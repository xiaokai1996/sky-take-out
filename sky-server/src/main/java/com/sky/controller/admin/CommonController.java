package com.sky.controller.admin;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/admin/common")
public class CommonController {

    @PostMapping("/upload")
    public void upload() {
        return;
    }
}
