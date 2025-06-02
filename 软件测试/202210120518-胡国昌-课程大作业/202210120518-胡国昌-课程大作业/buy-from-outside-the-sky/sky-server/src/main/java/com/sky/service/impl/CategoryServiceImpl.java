package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.context.BaseContext;
import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.CategoryMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.CategoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;

    /**
     * 根据id修改员工数据
     *
     * @param categoryDTO
     */
    @Override
    public void update(CategoryDTO categoryDTO) {
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO, category);
        category.setUpdateUser(BaseContext.getCurrentId());
        category.setUpdateTime(LocalDateTime.now());
        categoryMapper.update(category);
    }

    @Override
    public PageResult pagequery(CategoryPageQueryDTO categoryPageQueryDTO) {
        PageHelper.startPage(categoryPageQueryDTO.getPage(), categoryPageQueryDTO.getPageSize());
        Category category = Category.builder()
                .name(categoryPageQueryDTO.getName())
                .type(categoryPageQueryDTO.getType())
                .build();
        Page<Category> page = categoryMapper.query(category);

        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 根据id修改分类状态
     *
     * @param status 状态
     * @param id     分类id
     */
    @Override
    public void updateById(Integer status, Long id) {
        Category category = Category.builder()
                .status(status)
                .id(id)
                .updateTime(LocalDateTime.now())
                .updateUser(BaseContext.getCurrentId())
                .build();
        categoryMapper.update(category);
    }

    /**
     * @param categoryDTO
     */
    @Override
    public void insert(CategoryDTO categoryDTO) {
        Category category = Category.builder()
                .status(0)
                .updateUser(BaseContext.getCurrentId())
                .updateTime(LocalDateTime.now())
                .createTime(LocalDateTime.now())
                .createUser(BaseContext.getCurrentId())
                .build();
        BeanUtils.copyProperties(categoryDTO, category);
        categoryMapper.insert(category);
    }

    @Override
    public List<Category> findByType(Integer type) {
        List<Category> categories = categoryMapper.findByType(type);
        return categories;
    }

    /**
     * 根据id删除数据
     *
     * @param id
     */
    @Override
    public void deleteById(Integer id) {
//        检查菜品分类下是否有菜品
        if (dishMapper.count() > 0) {
            throw new DeletionNotAllowedException("当前分类还有菜品");
        }
//        检查套餐分类下是否有套餐
        if (setmealMapper.count() > 0) {
            throw new DeletionNotAllowedException("当前分类还有菜品");
        }
        categoryMapper.deleteById(id);
    }
}
