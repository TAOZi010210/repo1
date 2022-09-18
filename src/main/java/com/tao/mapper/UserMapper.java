package com.tao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tao.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
