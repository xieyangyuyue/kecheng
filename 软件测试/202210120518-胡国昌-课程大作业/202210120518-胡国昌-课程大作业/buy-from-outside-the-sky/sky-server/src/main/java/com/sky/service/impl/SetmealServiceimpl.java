package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Category;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.BaseException;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.exception.SetmealEnableFailedException;
import com.sky.mapper.CategoryMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Insert;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class SetmealServiceimpl implements SetmealService {
    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private CategoryMapper categoryMapper;

    /**
     * 添加套餐
     *
     * @param setmealDTO
     */
    @Override
    @Transactional
    public void add(SetmealDTO setmealDTO) {
//        新增套餐需要操作两张表，套餐表和套餐菜品关系表
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
//        新增套餐表
        setmealMapper.insert(setmeal);
//        批量添加套餐菜品关系表
//        补充setmealDishes中的套餐id
        for (SetmealDish setmealDish : setmealDishes) {
            setmealDish.setSetmealId(setmeal.getId());
        }
        setmealDishMapper.insertByList(setmealDishes);
    }

    /**
     * 分页查询
     *
     * @param setmealPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
//        查询套餐数据
        PageHelper.startPage(setmealPageQueryDTO.getPage(), setmealPageQueryDTO.getPageSize());
        Page<SetmealVO> page = setmealMapper.findPage(setmealPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());

    }

    @Override
    public void updateStatus(Integer status, Long id) {
        Setmeal setmeal = Setmeal.builder()
                .status(status)
                .id(id)
                .build();
//        修改状态前先判断status停售可以直接停，但起售需要判断菜品是否起售
        if(status==1){
//            起售
            List<SetmealDish> setmealDishes= setmealDishMapper.fiandBySetmealId(id);
            List<Long> dishId=new ArrayList<>();
            for (SetmealDish setmealDish : setmealDishes) {
                dishId.add(setmealDish.getDishId());
            }
            List<Dish> dishes= dishMapper.findByIds(dishId);
            for (Dish dish : dishes) {
                if(dish.getStatus()== StatusConstant.DISABLE){
                    throw new SetmealEnableFailedException(MessageConstant.SETMEAL_ENABLE_FAILED);
                }
            }
        }
        setmealMapper.updateById(setmeal);
    }

    @Override
    @Transactional
    public void deleteByList(List<Long> ids) {
//        删除套餐表需要操作两张表，套餐表和套餐菜品关系表，虽然套餐表和菜品表是多对多的关系但关系表的作用主要是用来描述套餐的所以套餐删除关系表也要删除
//        现实业务还需要考虑当前是否起售
        List<Setmeal> setmeals= null;
        try {
            setmeals = setmealMapper.findByList(ids);
        } catch (Exception e) {
//            todo 由于全局异常处理类的sql异常没有很好的处理先用不能删除异常代替一下
            throw new BaseException(MessageConstant.NO_DISHES_SELECTED);
        }
        for (Setmeal setmeal : setmeals) {
            if(setmeal.getStatus()==1){
                throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
            }
        }
//        删除套餐表数据
        setmealMapper.deleteByList(ids);
//        删除套餐菜品关系表
        setmealDishMapper.deleteByDishIds(ids);
    }

    /**
     * 查找套餐信息
     * @param id
     * @return
     */
    @Override
    public SetmealVO findSetmealWidthDish(Long id) {
//        需要查四张表，套餐表，套餐菜品关系表，菜品表,分类表
//        套餐表获取套餐基本信息
        Setmeal setmeal= setmealMapper.findById(id);
//       套餐菜品关系表获取菜品id
        List<SetmealDish> setmealDishes = setmealDishMapper.fiandBySetmealId(id);
        List dishId=new ArrayList();
        for (SetmealDish setmealDish : setmealDishes) {
            dishId.add(setmealDish.getDishId());
        }
//        菜品表获取菜品
        List dish = dishMapper.findByIds(dishId);
//        分类表获取名字
        Category category = categoryMapper.findById(setmeal.getCategoryId());
        SetmealVO setmealVO=new SetmealVO();
        BeanUtils.copyProperties(setmeal,setmealVO);
        setmealVO.setSetmealDishes(dish);
        setmealVO.setCategoryName(category.getName());
        return setmealVO;
    }

    /**
     * 修改套餐信息
     * @param setmealDTO
     */
    @Override
    @Transactional
    public void updateSetmeal(SetmealDTO setmealDTO) {
//        需要操作两张套餐表和套餐菜品关系表
//        获取套餐信息修改套餐信息
        Setmeal setmeal=new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);
        setmealMapper.updateById(setmeal);
//        获取关联菜品信息，先删除相关菜品信息在添加
        setmealDishMapper.deleteBySetmaelId(setmeal.getId());
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        setmealDishMapper.insertByList(setmealDishes);
    }

    @Override
    public List<Setmeal> findByCategoryId(Integer categoryId) {
        List<Setmeal> setmeals= setmealMapper.findByCategoryId(categoryId);
        return setmeals;
    }

    @Override
    public List<DishItemVO> findById(Long id) {
        List<DishItemVO> data=new ArrayList<>();
        List<SetmealDish> setmealDishes = setmealDishMapper.fiandBySetmealId(id);
        List<Long> dishids=new ArrayList<>();
        for (SetmealDish setmealDish : setmealDishes) {
            DishItemVO dishItemVO = new DishItemVO();
            BeanUtils.copyProperties(setmealDish,dishItemVO);
            data.add(dishItemVO);
            dishids.add(setmealDish.getDishId());
        }
        List<Dish> byIds = null;
        try {
            byIds = dishMapper.findByIds(dishids);
        } catch (Exception e) {
            throw new BaseException("当前套餐没有菜品");
        }
        for (int i = 0; i < byIds.size(); i++) {
            data.get(i).setImage(byIds.get(i).getImage());
            data.get(i).setDescription(byIds.get(i).getDescription());
        }
        return data;
    }
}
