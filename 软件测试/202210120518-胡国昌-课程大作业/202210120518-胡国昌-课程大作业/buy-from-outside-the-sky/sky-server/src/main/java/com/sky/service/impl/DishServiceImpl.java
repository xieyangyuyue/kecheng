package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Category;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.*;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class DishServiceImpl implements DishService {
    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private DishFlavorMapper dishFlavorMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;
    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    @Transactional
    public void insertWidthFlavor(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        List<DishFlavor> flavors = dishDTO.getFlavors();
        dishMapper.insert(dish);
        if (flavors.size() <= 0 || flavors == null) {
            return;
        }
        for (DishFlavor dishFlavor : flavors) {
            dishFlavor.setDishId(dish.getId());
        }
        dishFlavorMapper.insertByList(flavors);
    }

    @Override
    public PageResult pageQueryWidthCategory(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        Page<DishVO> page = dishMapper.select(dishPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Transactional
    @Override
    public void deleteByList(@RequestParam List<Long> ids) {
//        关联两张表，套餐表setmeal，菜品口味表
        for (Long id : ids) {
            Dish dish = dishMapper.findById(id);
            if (dish.getStatus() == StatusConstant.ENABLE) {
                //当前菜品处于起售中，不能删除
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }
//        查询是否有关联套餐的菜品
        List<Long> setmealIds = setmealDishMapper.findByDishId(ids);
        if (setmealIds.size() > 0 && setmealIds != null) {
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }
//        开始删除
        dishMapper.deleteByList(ids);
        dishFlavorMapper.deleteByDishIds(ids);
    }

    @Override
    public DishVO findById(Long id) {
//        查询两张表的数据一个是口味表一个是菜品表
//        查询菜品信息
        Dish dish = dishMapper.findById(id);
//        查询口味信息
        List<DishFlavor> dishFlavors = dishFlavorMapper.findByDishId(id);
//        补充菜品分类名
        Category category = categoryMapper.findById(dish.getCategoryId());

        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish, dishVO);

        dishVO.setFlavors(dishFlavors);
        dishVO.setCategoryName(category.getName());
        return dishVO;
    }

    @Override
    public void update(DishDTO dishDTO) {
//        操作两张表，dish菜品表和，dishflavor菜品口味表
        Dish dish=new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        dishMapper.update(dish);
        List<Long> ids=new ArrayList<Long>();
        ids.add(dish.getId());
        dishFlavorMapper.deleteByDishIds(ids);
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if(flavors.size()>0&& flavors!=null){
            for (DishFlavor flavor : flavors) {
                flavor.setDishId(dish.getId());
            }
            dishFlavorMapper.insertByList(flavors);
        }

    }

    /**
     * 根据分类id查询菜品
     *
     * @param categoryId
     * @return
     */
    @Override
    public List<Dish> findByCateGoryId(Long categoryId) {
        List <Dish> dishes= dishMapper.findByCateGoryId(categoryId);
        return dishes;
    }

    @Override
    public void changeStatus(Integer status, Long id) {
        Dish dish=Dish.builder()
                .status(status)
                .id(id)
                .build();
        dishMapper.update(dish);
    }

    @Override
    public List<DishVO> findWithflavor(Long categoryId) {
//        设置当前查询那些菜品
        List<DishVO> data=new ArrayList<>();
        List<Dish> dishes= dishMapper.findByStatusAndCategoryId(categoryId,StatusConstant.ENABLE);
        for (Dish dish : dishes) {
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(dish,dishVO);
            List<DishFlavor> flavors= dishFlavorMapper.findByDishId(dish.getId());
            dishVO.setFlavors(flavors);
            data.add(dishVO);
        }
        return data;
    }

}
