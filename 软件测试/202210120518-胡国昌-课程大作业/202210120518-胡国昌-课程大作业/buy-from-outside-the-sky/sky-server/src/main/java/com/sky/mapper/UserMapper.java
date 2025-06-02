package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

@Mapper
public interface UserMapper {
    @Select("select * from user where openid=#{openid}")
    User findByOpenid(String openid);

    @Insert("insert into user(openid,create_time) value (#{openid},#{createTime})")
    @Options(useGeneratedKeys = true,keyProperty = "id")
    void insert(User user);

    Integer userStatistics(Map map);
}
