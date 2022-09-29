package com.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.reggie.common.CustomException;
import com.reggie.dto.SetmealDto;
import com.reggie.entity.Dish;
import com.reggie.entity.Setmeal;
import com.reggie.entity.SetmealDish;
import com.reggie.mapper.DishMapper;
import com.reggie.mapper.SetmealMapper;
import com.reggie.service.SetmealDishService;
import com.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;
    /**
     * 新增套餐，同时保存套餐与菜品的关系
     * @param setmealDto
     */

    @Override
    public void saveWithDish(SetmealDto setmealDto) {
        this.save(setmealDto);
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.forEach(item->item.setSetmealId(setmealDto.getId()));


        setmealDishService.saveBatch(setmealDishes);
    }

    @Transactional
    @Override
    public void removeWithDish(List<String> id) {
        //查询套餐状态
        LambdaQueryWrapper<Setmeal> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId, id);
        queryWrapper.eq(Setmeal::getStatus, 1);
        int count = this.count(queryWrapper);
        if (count>0){
           throw new CustomException("套餐正在售卖中，不能删除");
        }

        this.removeByIds(id);
        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(SetmealDish::getSetmealId, id);
        setmealDishService.remove(lambdaQueryWrapper);


    }
}
