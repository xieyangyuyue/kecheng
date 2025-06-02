package com.sky.controller.user;

import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.ibatis.annotations.CacheNamespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("SetmealController")
@RequestMapping("/user/setmeal")
@Api(tags = "小程序套餐接口")
public class SetmealController {
    @Autowired
    private SetmealService setmealService;
    @GetMapping("/list")
    @ApiOperation("根据分类id查询套餐")
    @Cacheable(cacheNames = "setmeal",key ="#categoryId" )
    public Result<List> findByCategoryId(Integer categoryId ){
        List<Setmeal> setmeals= setmealService.findByCategoryId(categoryId);
        return Result.success(setmeals);
    }
    @GetMapping("/dish/{id}")
    @ApiOperation("根据套餐id查询菜品")
    public Result<List> findByIdWidthDish(@PathVariable Long id){
        List<DishItemVO> dishItemVO = setmealService.findById(id);
        return Result.success(dishItemVO);
    }
}
