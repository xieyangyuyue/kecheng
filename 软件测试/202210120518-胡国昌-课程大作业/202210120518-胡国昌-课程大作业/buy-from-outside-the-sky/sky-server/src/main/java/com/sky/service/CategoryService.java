package com.sky.service;


import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;

import java.util.List;

public interface CategoryService {
    public void update(CategoryDTO categoryDTO);


    public PageResult pagequery(CategoryPageQueryDTO categoryPageQueryDTO);

    void updateById(Integer status, Long id);

    /**
     * 新增分类
     * @param categoryDTO
     */
    void insert(CategoryDTO categoryDTO);

    List<Category> findByType(Integer type);

    void deleteById(Integer id);
}

