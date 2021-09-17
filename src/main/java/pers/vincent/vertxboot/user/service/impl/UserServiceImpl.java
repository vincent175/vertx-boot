package pers.vincent.vertxboot.user.service.impl;

import pers.vincent.vertxboot.user.entity.User;
import pers.vincent.vertxboot.user.mapper.UserMapper;
import pers.vincent.vertxboot.user.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author Vincent
 * @since 2021-09-17
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

}
