package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;

    @Override
    public void addShoppingCar(ShoppingCartDTO shoppingCartDTO) {
//        先查表中有数据没
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        //todo 当前是模拟小程序前端有缺陷没有将token写进请求头
        shoppingCart.setUserId(1l);
        List<ShoppingCart> shoppingCartList = shoppingCartMapper.queryByDishIdOrSetmealId(shoppingCart);
        if (shoppingCartList.size() > 0 && shoppingCartList != null) {
//           表中有数据修改数量
            shoppingCart = shoppingCartList.get(0);
            shoppingCart.setNumber(shoppingCart.getNumber() + 1);
            shoppingCartMapper.updateByid(shoppingCart);
            return;
        }
//           表中没数据
        if (shoppingCartDTO.getDishId() != null) {
//            当前添加的是菜品,补充shoppingCart字段
            Dish dish = dishMapper.findById(shoppingCartDTO.getDishId());
            shoppingCart.setName(dish.getName());
            shoppingCart.setImage(dish.getImage());
            shoppingCart.setAmount(dish.getPrice());
        } else {
//            当前添加的是套餐补充shoppingCart字段
            Setmeal setmeal = setmealMapper.findById(shoppingCartDTO.getSetmealId());
            shoppingCart.setName(setmeal.getName());
            shoppingCart.setImage(setmeal.getImage());
            shoppingCart.setAmount(setmeal.getPrice());

        }
        shoppingCart.setNumber(1);
        shoppingCart.setCreateTime(LocalDateTime.now());
        shoppingCartMapper.save(shoppingCart);
    }

    @Override
    public List<ShoppingCart> list() {
//        查询当前用户购物车
        ShoppingCart shoppingCart = ShoppingCart.builder().userId(1l).build();
        List<ShoppingCart> shoppingCartList = shoppingCartMapper.queryByDishIdOrSetmealId(shoppingCart);
        return shoppingCartList;
    }

    @Override
    public void clean() {
        shoppingCartMapper.clean(1l);
    }

    //        删除购物车中商品的数量
    @Override
    public void subtract(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart = ShoppingCart.builder()
                .userId(1l)
                .dishId(shoppingCartDTO.getDishId())
                .setmealId(shoppingCartDTO.getSetmealId())
                .dishFlavor(shoppingCartDTO.getDishFlavor())
                .build();
        List<ShoppingCart> shoppingCartList = shoppingCartMapper.queryByDishIdOrSetmealId(shoppingCart);
        shoppingCart = shoppingCartList.get(0);
//            删除购物车中某个菜品的数量
        if (shoppingCart.getNumber() > 1) {
//                数量减1
            shoppingCart.setNumber(shoppingCart.getNumber() - 1);
            shoppingCartMapper.updateByid(shoppingCart);
        } else {
//                等于1直接从表中删除
            shoppingCartMapper.deleteById(shoppingCart);
        }
    }

}
