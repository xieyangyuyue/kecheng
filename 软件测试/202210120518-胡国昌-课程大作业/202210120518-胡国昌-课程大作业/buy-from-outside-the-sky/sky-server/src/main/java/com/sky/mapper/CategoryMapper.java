package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.CategoryDTO;
import com.sky.entity.Category;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CategoryMapper {
    /**
     * 根据id修改员工数据
     *
     * @param category
     */
    @AutoFill(OperationType.UPDATE)
    void update(Category category);

    Page<Category> query(Category category);

    @AutoFill(OperationType.INSERT)
    @Insert("insert into category(type, name, sort, status, create_time, update_time, create_user, update_user) " +
            "values (#{type},#{name},#{sort},#{status},#{createTime},#{updateTime},#{createUser},#{updateUser})")
    void insert(Category category);


    List<Category> findByType(Integer type);

    @Delete("delete from category where id=#{id}")
    void deleteById(Integer id);

    @Select("select id, type, name, sort, status, create_time, update_time, create_user, update_user from category where id=#{id}")
    Category findById(Long id);
}
