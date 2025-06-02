package com.sky.controller.user;

import com.sky.entity.Dish;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("userDishController")
@RequestMapping("/user/dish")
@Api(tags = "小程序菜品相关接口")
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private RedisTemplate redisTemplate;

    @GetMapping("/list")
    @ApiOperation("根据分类id查询菜品")
    public Result<List> findDishById(Long categoryId) {
//        先查缓存
        String key = "dish_" + categoryId;
        ValueOperations valueOperations = redisTemplate.opsForValue();
        List<DishVO> dishes = (List<DishVO>) valueOperations.get(key);
        if (dishes != null && dishes.size() > 0) {
//            缓存中有数据，返回
            return Result.success(dishes);
        }
//        没数据查询并添加进缓存
        dishes = dishService.findWithflavor(categoryId);
        valueOperations.set(key,dishes);
        return Result.success(dishes);
    }
}
