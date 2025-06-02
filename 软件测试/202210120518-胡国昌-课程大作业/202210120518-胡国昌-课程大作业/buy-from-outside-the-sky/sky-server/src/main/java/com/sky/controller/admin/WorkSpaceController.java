package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.WorkSpaceService;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.SetmealOverViewVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@RequestMapping("/admin/workspace")
@Api(tags = "工作台相关接口")
@RestController
@Slf4j
public class WorkSpaceController {
    @Autowired
    private WorkSpaceService workSpaceService;

    @GetMapping("/businessData")
    @ApiOperation("获取今天的业务数据")
    public Result<BusinessDataVO> getBusinessData() {
        log.info("获取今天的业务数据");
        LocalDateTime beginTime = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        BusinessDataVO businessDataVO = workSpaceService.getBusinessData( beginTime,endTime );
        return Result.success(businessDataVO);
    }

    @GetMapping("/overviewSetmeals")
    @ApiOperation("获取套餐概览数据")
    public Result<SetmealOverViewVO> getOverviewSetmeals() {
        log.info("获取套餐概览数据");
        SetmealOverViewVO setmealOverViewVO = workSpaceService.getOverviewSetmeals();
        return Result.success(setmealOverViewVO);
    }
    @GetMapping("/overviewDishes")
    @ApiOperation("获取菜品概览数据")
    public Result<DishOverViewVO> getOverviewDishes() {
        log.info("获取菜品概览数据");
        DishOverViewVO dishOverViewVO = workSpaceService.getOverviewDishes();
        return Result.success(dishOverViewVO);
    }
}
