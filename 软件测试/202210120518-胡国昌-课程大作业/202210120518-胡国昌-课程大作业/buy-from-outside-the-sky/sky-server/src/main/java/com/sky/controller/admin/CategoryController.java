package com.sky.controller.admin;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Delete;
import org.apache.xmlbeans.impl.xb.xsdschema.Public;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController("adminCategoryController")
@Api(tags = "分类数据操作")
@RequestMapping("/admin/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * 根据id修改分类数据
     * @param categoryDTO
     * @return
     */
    @ApiOperation("根据id修改分类数据")
    @PutMapping
    public Result changeCategory(@RequestBody CategoryDTO categoryDTO) {
        log.info("修改分类信息:{}",categoryDTO);
        categoryService.update(categoryDTO);
        return Result.success();
    }

    /**
     *
     * @param categoryPageQueryDTO
     * @return Result
     */
    @ApiOperation("分类分页查询")
    @GetMapping("/page")
    public Result<PageResult> pageQuery(CategoryPageQueryDTO categoryPageQueryDTO){
        log.info("分页查询：{}",categoryPageQueryDTO);
        PageResult pageResult= categoryService.pagequery(categoryPageQueryDTO);
        return Result.success(pageResult);
    }
    @ApiOperation("根据id修改分类状态")
    @PostMapping("/status/{status}")
    public Result<String> changeState(@PathVariable Integer status,Long id){
        log.info("根据id修改分类状态：{}，{}",status,id);
        categoryService.updateById(status,id);
        return Result.success();
    }

    /**
     *
     * @param categoryDTO
     * @return
     */
    @PostMapping
    @ApiOperation("新增分类")
    public Result<String> insert(@RequestBody CategoryDTO categoryDTO){
        categoryService.insert(categoryDTO);
        return Result.success();
    }

    /**
     *
     * @param type
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("根据类型查询")
    public Result<List> findByType(Integer type){
        log.info("根据类型查询数据：{}",type);
        List<Category> categories= categoryService.findByType(type);
        return Result.success(categories);
    }
    @DeleteMapping
    @ApiOperation("根据id删除分类")
    public Result<String> deleteById(Integer id){
        log.info("根据id删除分类：{}",id);
        categoryService.deleteById(id);
        return Result.success();
    }
}
