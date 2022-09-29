package com.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.reggie.common.R;
import com.reggie.dto.DishDto;
import com.reggie.entity.Category;
import com.reggie.entity.Dish;
import com.reggie.entity.Setmeal;
import com.reggie.service.CategoryService;
import com.reggie.service.DishFlavorService;
import com.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜品管理
 */
@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/page")
    public R<Page> page(int page, int pageSize,String name){
        Page<Dish> pageInfo = new Page<Dish>(page, pageSize);
        Page<DishDto> dishDtoPage=new Page<>();

        LambdaQueryWrapper<Dish> queryWrapper=new LambdaQueryWrapper();
        //模糊查询
        queryWrapper.like(name!=null, Dish::getName,name);
        //添加排序条件
        queryWrapper.orderByDesc(Dish::getUpdateTime);

        dishService.page(pageInfo,queryWrapper);
        //对象copy
        BeanUtils.copyProperties(pageInfo, dishDtoPage,"records");
        List<Dish> records = pageInfo.getRecords();

        List<DishDto> list=records.stream().map((item)->{
            DishDto dishDto=new DishDto();
            BeanUtils.copyProperties(item,dishDto);
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if (category!=null){
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }

            return dishDto;
        }).collect(Collectors.toList());
        dishDtoPage.setRecords(list);
        return R.success(dishDtoPage);
    }

    /**
     *
     * id 查询菜品信息和对应的口味信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id){
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }

    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        log.info(dishDto.toString());
        dishService.updateWithFlavor(dishDto);

        return R.success("更新菜品成功");
    }

    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
        log.info(dishDto.toString());
        dishService.saveWithFlavor(dishDto);

        return R.success("新增菜品成功");
    }

    @GetMapping("/list")
    public R<List<Dish>> list(Dish dish){
        LambdaQueryWrapper<Dish> queryWrapper=new LambdaQueryWrapper<>();
        //查询条件
        queryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
        queryWrapper.eq(Dish::getStatus,1);
        //排序条件
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        List<Dish> list = dishService.list(queryWrapper);

        return R.success(list);


    }

    //删除菜品
    @DeleteMapping
    public R<String> delete(@RequestParam List<String> id){

       dishService.removeByIds(id);

        return R.success("删除成功");
    }
    //菜品的批量 起售，停售
    @PostMapping("/status/{status}")
    public R<String> upAndStop(@PathVariable int status,@RequestParam List<String> ids){
        LambdaUpdateWrapper<Dish> updateWrapper=new LambdaUpdateWrapper<>();
        updateWrapper.set(Dish::getStatus, status).in(Dish::getId, ids);
        dishService.update(updateWrapper);
        return R.success("成功");

    }


}
