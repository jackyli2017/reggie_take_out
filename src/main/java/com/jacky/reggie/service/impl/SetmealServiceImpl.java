package com.jacky.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jacky.reggie.common.CustomException;
import com.jacky.reggie.dto.SetmealDto;
import com.jacky.reggie.entity.Setmeal;
import com.jacky.reggie.entity.SetmealDish;
import com.jacky.reggie.mapper.SetmealMapper;
import com.jacky.reggie.service.SetmealDishService;
import com.jacky.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;

    @Override
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
        log.info("setmealDto1: {}", setmealDto);
        this.save(setmealDto);
        log.info("setmealDto2: {}", setmealDto);
        Long setmealId = setmealDto.getId();


        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.stream().map((item) -> {
            item.setSetmealId(setmealId);
            return item;
        }).collect(Collectors.toList());

        setmealDishService.saveBatch(setmealDishes);
    }

    /**
     *
     * @param ids 套餐的id
     */
    @Override
    @Transactional
    public void removeWithDish(List<Long> ids) {
        // 查询套餐状态，确定是否可以删除
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId, ids);
        queryWrapper.eq(Setmeal::getStatus, 1);

        int count = this.count(queryWrapper);
        if(count > 0) {
            throw new CustomException("套餐售卖中，不能删除");
        }

        this.removeByIds(ids);

        // delete from setmeal_dish where setmeal_id in (...)
        LambdaQueryWrapper<SetmealDish> setmealDishQueryWrapper = new LambdaQueryWrapper<>();
        setmealDishQueryWrapper.in(SetmealDish::getSetmealId, ids);

        setmealDishService.remove(setmealDishQueryWrapper);
    }
}
