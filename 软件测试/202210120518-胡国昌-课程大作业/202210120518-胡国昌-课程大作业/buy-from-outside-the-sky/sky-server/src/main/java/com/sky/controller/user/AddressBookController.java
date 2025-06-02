package com.sky.controller.user;

import com.sky.entity.AddressBook;
import com.sky.result.Result;
import com.sky.service.AddressBookService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Api(tags = "地址薄相关接口")
@RequestMapping("/user/addressBook")
public class AddressBookController {
    @Autowired
    private AddressBookService addressBookService;
    @PostMapping
    @ApiOperation("添加地址")
    public Result save(@RequestBody AddressBook addressBook){
        addressBookService.save(addressBook);
        return Result.success();
    }
    @GetMapping("/list")
    @ApiOperation("查询所有地址信息")
    public Result<List<AddressBook>> list(){
        List<AddressBook> addressBooks= addressBookService.list();
        return Result.success(addressBooks);
    }
    @PutMapping("/default")
    @ApiOperation("修改默认地址")
    public Result setDefault( @RequestBody AddressBook addressBook){
        addressBookService.updateById(addressBook.getId());
        return Result.success();
    }
    @GetMapping("/{id}")
    @ApiOperation("根据id查询地址")
    public Result getAddress(@PathVariable Long id){
        AddressBook addressBook=addressBookService.getAddressById(id);
        return Result.success(addressBook);
    }
    @PutMapping
    @ApiOperation("修改指定地址")
    public Result updateAddress(@RequestBody AddressBook addressBook){
        addressBookService.updateAddress(addressBook);
        return Result.success();
    }
    @GetMapping("/default")
    @ApiOperation("查询默认地址")
    public Result getDefault(){
        AddressBook addressBook= addressBookService.getDefault();
        return Result.success(addressBook);
    }
    @DeleteMapping
    @ApiOperation("根据id删除地址")
    public Result deleteAddress(Long id){
        addressBookService.delete(id);
        return Result.success();
    }
}
