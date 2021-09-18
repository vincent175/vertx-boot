package pers.vincent.vertxboot.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Component;
import pers.vincent.vertxboot.user.entity.User;

/**
 * <p>
 * 用户表 Mapper 接口
 * </p>
 *
 * @author Vincent
 * @since 2021-09-17
 */
@Component
public interface UserMapper extends BaseMapper<User> {

}
