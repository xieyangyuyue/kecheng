package com.sky.service.impl;

import com.sky.mapper.DishMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.WorkSpaceService;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.SetmealOverViewVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class WorkSpaceServiceImpl implements WorkSpaceService {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private DishMapper dishMapper;
    /**
     * 获取业务数据
     * @return BusinessDataVO包含5个数据，今日营业额、今日新增用户、有效订单数、订单完成率、平均客单价
     */
    @Override
    public BusinessDataVO getBusinessData(LocalDateTime beginTime, LocalDateTime endTime) {
        // 指定时间营业额
        Double aDouble = orderMapper.turnoverStatistics(beginTime, endTime);
//        订单数不考虑状态
        Integer orderCount = orderMapper.orderStatistics(beginTime, endTime);
        Map map=new HashMap();
        map.put("begin",beginTime);
        map.put("end",endTime);
        Double orderCompletionRate = 0.0;
        double unitPrice=aDouble==null?0.0:aDouble;
        if(orderCount!=0){
            orderCompletionRate= (double) (orderCount/orderCount);
            unitPrice=aDouble/orderCount;
        }


//        新增用户
        Integer userCount = userMapper.userStatistics(map);
        return BusinessDataVO
                .builder()
                .newUsers(userCount)
                .validOrderCount(orderCount)
                .orderCompletionRate(orderCompletionRate)
                .turnover(aDouble==null?0.0:aDouble)
                .unitPrice(unitPrice)
                .build();
    }

    /**
     *
     * @return SetmealOverViewVO包含两个数据，套餐停售数量、套餐起售数量
     */
    @Override
    public SetmealOverViewVO getOverviewSetmeals() {
       Integer discontinued= setmealMapper.getCountByStatus(0);
       Integer sold=setmealMapper.getCountByStatus(1);
        return SetmealOverViewVO
                .builder()
                .discontinued(discontinued)
                .sold(sold)
                .build();
    }
    /**
     *
     * @return DishOverViewVO包含两个数据，菜品停售数量、菜品起售数量
     */
    @Override
    public DishOverViewVO getOverviewDishes() {
        Integer discontinued= dishMapper.getCountByStatus(0);
        Integer sold=dishMapper.getCountByStatus(1);
        return DishOverViewVO
                .builder()
                .discontinued(discontinued)
                .sold(sold)
                .build();
    }

}
