package com.jacky.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jacky.reggie.common.CustomException;
import com.jacky.reggie.entity.Category;
import com.jacky.reggie.entity.Dish;
import com.jacky.reggie.entity.Setmeal;
import com.jacky.reggie.mapper.CategoryMapper;
import com.jacky.reggie.service.CategoryService;
import com.jacky.reggie.service.DishService;
import com.jacky.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;

    /**
     * 根据id删除分类，删除之前需要判断是否有关联其他数据
     * @param ids
     */
    @Override
    public void remove(Long ids) {
        //如果已经关联菜品或套餐，抛出业务异常
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.eq(Dish::getCategoryId, ids);
        int countDish = dishService.count(dishLambdaQueryWrapper);

        if(countDish > 0) {
            throw new CustomException("当前分类关联了菜品，不能删除");
        }

        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId, ids);
        int countSetmeal = setmealService.count(setmealLambdaQueryWrapper);
        if(countSetmeal > 0) {
            throw new CustomException("当前菜品关联了套餐，不能删除");
        }

        super.removeById(ids);
    }
}
