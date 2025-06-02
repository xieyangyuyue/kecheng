package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {
    /**
     * 微信登录逻辑
     *
     * @param userLoginDTO
     * @return
     */
    private static final String LOGIN_url = "https://api.weixin.qq.com/sns/jscode2session";
    @Autowired
    private WeChatProperties weChatProperties;
    @Autowired
    private UserMapper userMapper;

    @Override
    public User wxLogin(String js_code) {
//        设置请求数据
        Map<String, String> data = new HashMap<>();
        data.put("appid", weChatProperties.getAppid());
        data.put("secret", weChatProperties.getSecret());
        data.put("js_code", js_code);
        data.put("grant_type", "authorization_code");

//        创建请求并发送，获取openid
        String response = HttpClientUtil.doGet(LOGIN_url, data);
        JSONObject parse = JSON.parseObject(response);
        String openid = parse.getString("openid");
//        判断openid是否存在表示当前用户请求是否正确
        if (openid == null || openid == "") {
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }
//openid正确判断当前用户是否存在于表中
        User user= userMapper.findByOpenid(openid);
//        为空在表中新增数据
        if (user==null){
//补充openid,其他字段暂时获取不到,创建时间需要手动填充
           user=User.builder()
                   .createTime(LocalDateTime.now())
                   .openid(openid)
                   .build();
            userMapper.insert(user);
        }
        return user;
    }
}
