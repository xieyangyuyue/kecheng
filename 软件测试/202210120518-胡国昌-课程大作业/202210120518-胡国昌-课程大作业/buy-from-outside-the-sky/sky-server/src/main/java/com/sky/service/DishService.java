package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;

public interface DishService {


    void insertWidthFlavor(DishDTO dishDTO);

    PageResult pageQueryWidthCategory(DishPageQueryDTO dishPageQueryDTO);

    void deleteByList(List<Long> ids);

    DishVO findById(Long id);

    void update(DishDTO dishDTO);

    List<Dish> findByCateGoryId(Long categoryId);

    void changeStatus(Integer status, Long id);

    List<DishVO> findWithflavor(Long categoryId);
}
