package pers.vincent.vertxboot.user.service.impl;

import pers.vincent.vertxboot.user.entity.UserInfo;
import pers.vincent.vertxboot.user.mapper.UserInfoMapper;
import pers.vincent.vertxboot.user.service.IUserInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Vincent
 * @since 2021-09-18
 */
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements IUserInfoService {

}
