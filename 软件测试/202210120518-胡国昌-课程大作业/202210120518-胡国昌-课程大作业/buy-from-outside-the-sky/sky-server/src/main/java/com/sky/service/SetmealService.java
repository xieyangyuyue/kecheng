package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.result.PageResult;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;

import java.util.List;

public interface SetmealService {
    void add(SetmealDTO setmealDTO);

    PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    void updateStatus(Integer status, Long id);

    void deleteByList(List<Long> ids);

    SetmealVO findSetmealWidthDish(Long id);

    void updateSetmeal(SetmealDTO setmealDTO);

    List<Setmeal> findByCategoryId(Integer categoryId);

    List<DishItemVO> findById(Long id);
}

