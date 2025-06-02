package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface DishMapper {
    @Select("select COUNT(*) from dish")
    public int count();

    @AutoFill(OperationType.INSERT)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    @Insert("insert into dish(name, category_id, price, image, description, create_time, update_time, create_user, update_user) " +
            "values (#{name},#{categoryId},#{price},#{image},#{description},#{createTime},#{updateTime},#{createUser},#{updateUser})")
    void insert(Dish dish);

    Page<DishVO> select(DishPageQueryDTO dishPageQueryDTO);

    @Select("select id, name, category_id, price, image, description, status, create_time, update_time, create_user, update_user from dish where id=#{id}")
    Dish findById(Long id);

    void deleteByList(List<Long> ids);

    @AutoFill(OperationType.UPDATE)
    void update(Dish dish);

    @Select("select id, name, category_id, price, image, description, status, create_time, update_time, create_user, update_user from dish where category_id=#{categoryId}")
    List<Dish> findByCateGoryId(Long categoryId);


    List<Dish> findByIds(List<Long> dishIds);

    @Select("select * from dish where category_id=#{categoryId} and status=#{enable}")
    List<Dish> findByStatusAndCategoryId(Long categoryId, Integer enable);
    @Select("select COUNT(*) from dish where status=#{status}")
    Integer getCountByStatus(int status);
}
