package com.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.reggie.dto.DishDto;
import com.reggie.entity.Dish;
import org.springframework.stereotype.Service;


public interface DishService extends IService<Dish> {
    //新增菜品，插入菜品口味，两张表
    public void saveWithFlavor(DishDto dishDto);

    //id 查询菜品与口味
    public DishDto getByIdWithFlavor(Long id);

    public void updateWithFlavor(DishDto dishDto);

}
