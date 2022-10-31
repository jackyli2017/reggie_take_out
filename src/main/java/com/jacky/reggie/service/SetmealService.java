package com.jacky.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jacky.reggie.dto.SetmealDto;
import com.jacky.reggie.entity.Dish;
import com.jacky.reggie.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {
    /**
     * 新增套餐，同时需要保存套餐和菜品的关联关系
     * @param setmealDto
     */
    public void saveWithDish(SetmealDto setmealDto);

    /**
     * 删除套餐和套餐菜品里关联的dish
     * @param ids
     */
    public void removeWithDish(List<Long> ids);
}
