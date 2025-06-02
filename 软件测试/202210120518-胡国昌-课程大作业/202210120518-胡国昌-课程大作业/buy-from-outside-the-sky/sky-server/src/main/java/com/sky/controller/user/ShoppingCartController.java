package com.sky.controller.user;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.result.Result;
import com.sky.service.ShoppingCartService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Api(tags = "购物车操作")
@RequestMapping("/user/shoppingCart")

public class ShoppingCartController {
    @Autowired
    private  ShoppingCartService shoppingCartService;
    /**
     * 添加购物车方法
     * @param shoppingCartDTO
     * @return
     */
    @PostMapping("/add")
    @ApiOperation("添加购物车方法")
    public Result saveShoppingCar(@RequestBody ShoppingCartDTO shoppingCartDTO){
        shoppingCartService.addShoppingCar(shoppingCartDTO);
        return Result.success();
    }
    @GetMapping("/list")
    @ApiOperation("查看购物车方法")
    public Result<List<ShoppingCart>> list(){
        List<ShoppingCart> shoppingCartList= shoppingCartService.list();
        return Result.success(shoppingCartList);
    }
    @DeleteMapping("/clean")
    @ApiOperation("清空购物车")
    public Result clean(){
        shoppingCartService.clean();
        return Result.success();
    }
    @PostMapping("/sub")
    @ApiOperation("删除某个购物车商品")
    public Result subtract(@RequestBody ShoppingCartDTO shoppingCartDTO){
        shoppingCartService.subtract(shoppingCartDTO);
        return Result.success();
    }
}
