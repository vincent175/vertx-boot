package pers.vincent.vertxboot.user.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import pers.vincent.vertxboot.http.annotation.RequestMapping;
import pers.vincent.vertxboot.user.entity.UserInfo;
import pers.vincent.vertxboot.user.mapper.UserInfoMapper;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Vincent
 * @since 2021-09-18
 */
@Controller
@RequestMapping("/user")
public class UserInfoController {

    @Autowired
    UserInfoMapper userInfoMapper;

    @RequestMapping("/findById")
    public UserInfo findUserById(Long userId){
        UserInfo userInfo = userInfoMapper.selectById(userId);
        return userInfo;
    }

    @RequestMapping("/add")
    public String addUser(String name){
        UserInfo userInfo = new UserInfo();
        userInfo.setName(name);
        int insert = userInfoMapper.insert(userInfo);
        if (insert == 1) {
            return "success";
        }
        return "fail";
    }
}
