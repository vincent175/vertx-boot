package pers.vincent.vertxboot.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
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
