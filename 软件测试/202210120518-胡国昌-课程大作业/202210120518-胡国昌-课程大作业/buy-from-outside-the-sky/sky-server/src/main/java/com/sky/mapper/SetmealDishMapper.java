package com.sky.mapper;

import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealDishMapper {

    List<Long> findByDishId(List<Long> dishIds);

    void insertByList(List<SetmealDish> setmealDishes);

    void deleteByDishIds(List<Long> DishIds);

    @Select("select id, setmeal_id, dish_id, name, price, copies from setmeal_dish where setmeal_id=#{id}")
    List<SetmealDish> fiandBySetmealId(Long setmealId);

    @Delete("delete from setmeal_dish where setmeal_id=#{setmaelId}")
    void deleteBySetmaelId(Long setmaelId);

}
