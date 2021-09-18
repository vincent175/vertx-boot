package pers.vincent.vertxboot.user.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import pers.vincent.vertxboot.http.annotation.RequestMapping;
import pers.vincent.vertxboot.user.entity.User;
import pers.vincent.vertxboot.user.mapper.UserMapper;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author Vincent
 * @since 2021-09-17
 */
@Slf4j
@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserMapper userMapper;

    @RequestMapping("/findById")
    public User findUserById(Integer userId) {
        User user = userMapper.selectById(userId);
        return user;
    }
}
