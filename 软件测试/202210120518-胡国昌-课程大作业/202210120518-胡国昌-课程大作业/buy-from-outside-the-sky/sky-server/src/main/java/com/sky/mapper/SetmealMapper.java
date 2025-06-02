package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.enumeration.OperationType;
import com.sky.vo.SetmealVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealMapper {
    @Select("select count(*) from setmeal")
    public int count();

    @AutoFill(OperationType.INSERT)
    @Insert("insert into setmeal(category_id, name, price, description, image, create_time, update_time, create_user, update_user,status) " +
            "VALUES (#{categoryId},#{name},#{price},#{description},#{image},#{createTime},#{updateTime},#{createUser},#{updateUser},#{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Setmeal setmeal);

    Page<SetmealVO> findPage(SetmealPageQueryDTO setmealPageQueryDTO);

    @AutoFill(OperationType.UPDATE)
    void updateById(Setmeal setmeal);

    List<Setmeal> findByList(List<Long> ids);

    void deleteByList(List<Long> ids);

    @Select("select * from  setmeal where id=#{id}")
    Setmeal findById(Long id);

    @Select("select * from setmeal where category_id=#{categoryId}")
    List<Setmeal> findByCategoryId(Integer categoryId);

    @Select("select count(*) from setmeal where status=#{status}")
    Integer getCountByStatus(int status);
}
