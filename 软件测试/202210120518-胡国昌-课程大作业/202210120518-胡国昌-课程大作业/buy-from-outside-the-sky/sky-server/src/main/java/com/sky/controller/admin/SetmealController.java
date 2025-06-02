package com.sky.controller.admin;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController("adminSetmealController")
@Api(tags = "套餐数据持久层")
@RequestMapping("/admin/setmeal")
public class SetmealController {
    @Autowired
    private SetmealService setmealService;

    /**
     * 添加套餐
     *
     * @param setmealDTO
     * @return
     */
    @PostMapping
    @CacheEvict(cacheNames = "setmeal",key = "#setmealDTO.categoryId")
    @ApiOperation("添加套餐")
    public Result addSetmeal(@RequestBody SetmealDTO setmealDTO) {
        log.info("添加套餐：{}",setmealDTO);
        setmealService.add(setmealDTO);
        return Result.success();
    }

    /**
     * 动态分页查询套餐
     *
     * @param setmealPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("套餐分页查询")
    public Result<PageResult> pageQuery(SetmealPageQueryDTO setmealPageQueryDTO){
        log.info("套餐分页查询：{}",setmealPageQueryDTO);
        PageResult pageResult= setmealService.pageQuery(setmealPageQueryDTO);

        return Result.success(pageResult);

    }

    /**
     * 套餐停售与起售
     * @param status
     * @param id
     * @return
     */
    @PostMapping("status/{status}")
    @ApiOperation("套餐停售与起售")
    @CacheEvict(cacheNames = "setmeal",allEntries = true)
    public Result changeStatus(@PathVariable Integer status,Long id){
        log.info("根据id{}修改套餐停售与起售状态{}",id,status);
        setmealService.updateStatus(status,id);
        return Result.success();
    }

    /**
     * 根据id集合删除套餐
     * @param ids
     * @return
     */
    @DeleteMapping
    @ApiOperation("根据id集合删除套餐")
    @CacheEvict(cacheNames = "setmeal",allEntries = true)
    public Result deleteSetmeal(@RequestParam List<Long> ids){
        log.info("根据id集合删除套餐:{}",ids);
        setmealService.deleteByList(ids);
        return Result.success();
    }

    /**
     * 根据id查询套餐
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation("根据id查询套餐")
    public Result<SetmealVO> findSetmeal(@PathVariable Long id){
        log.info("根据id{}查询套餐信息",id);
        SetmealVO setmealVO= setmealService.findSetmealWidthDish(id);
        return Result.success(setmealVO);
    }
    @PutMapping
    @ApiOperation("根据id修改套餐信息")
    @CacheEvict(cacheNames = "setmeal",key = "#setmealDTO.categoryId")
    public Result<String> updateSetmeal(@RequestBody SetmealDTO setmealDTO){
        log.info("根据id修改套餐:{}",setmealDTO);
        setmealService.updateSetmeal(setmealDTO);
        return Result.success();
    }

}
