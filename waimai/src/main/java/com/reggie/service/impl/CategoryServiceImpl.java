package com.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reggie.common.CustomException;
import com.reggie.common.R;
import com.reggie.entity.Category;
import com.reggie.entity.Dish;
import com.reggie.entity.Setmeal;
import com.reggie.mapper.CategoryMapper;
import com.reggie.service.CategoryService;
import com.reggie.service.DishService;
import com.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {


    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;
    //id 删除分类
    @Override
    public void remove(Long id) {
       //查询是否关联套餐 ，若关联，则抛出异常
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper=new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,id);
        int count1 = dishService.count(dishLambdaQueryWrapper);
        if (count1>0){
            //关联菜品,抛出异常
            throw new CustomException("当前分类下关联了菜品，不能删除!");


        }

        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper=new LambdaQueryWrapper<>();

        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId, id);
        int count2= setmealService.count(setmealLambdaQueryWrapper);
        if (count2>0){
            //关联套餐,抛出异常
            throw new CustomException("当前分类下关联了套餐,不能删除!");

        }
        super.removeById(id);

    }

}
