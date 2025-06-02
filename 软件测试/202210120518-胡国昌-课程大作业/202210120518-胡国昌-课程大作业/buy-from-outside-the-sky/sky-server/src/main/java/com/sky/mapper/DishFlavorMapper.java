package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.entity.DishFlavor;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishFlavorMapper {
    void insertByList(List<DishFlavor> flavors);

    void deleteByDishIds(List<Long> dishIds);

    @Select("select * from dish_flavor where dish_id=#{DishId}")
    List<DishFlavor> findByDishId(Long DishId);
}
