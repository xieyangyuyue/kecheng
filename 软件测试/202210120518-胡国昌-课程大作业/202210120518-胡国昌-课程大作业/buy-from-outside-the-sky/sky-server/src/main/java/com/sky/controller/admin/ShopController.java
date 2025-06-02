package com.sky.controller.admin;

import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController("adminShopController")
@Api(tags = "pc端管理店铺状态")
@RequestMapping("/admin/shop")
public class ShopController {
    private static final String KEY="SHOP_STATUS";
    @Autowired
    private RedisTemplate redisTemplate;
    @GetMapping("/status")
    @ApiOperation("pc端获取当前店铺营业状态")
    public Result getShop(){
        log.info("pc端获取当前店铺营业状态");
        ValueOperations valueOperations = redisTemplate.opsForValue();
        Integer status = (Integer) valueOperations.get(KEY);
        return Result.success(status);
    }
    @PutMapping("/{status}")
    @ApiOperation("修改店铺状态")
    public Result updateShopStatus(@PathVariable Integer status){
        log.info("设置当前店铺状态为：{}",status==1?"营业":"打烊");
        ValueOperations valueOperations = redisTemplate.opsForValue();
        valueOperations.set(KEY,status);
        return Result.success();
    }
}
