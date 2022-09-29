package com.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.reggie.dto.SetmealDto;
import com.reggie.entity.Setmeal;

import java.util.List;
import java.util.Set;


public interface SetmealService extends IService<Setmeal> {
    public void saveWithDish(SetmealDto setmealDto);

    public void removeWithDish(List<String> id);
}
