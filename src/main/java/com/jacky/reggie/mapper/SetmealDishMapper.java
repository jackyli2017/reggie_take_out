package com.jacky.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jacky.reggie.entity.SetmealDish;
import com.jacky.reggie.service.SetmealDishService;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper
public interface SetmealDishMapper extends BaseMapper<SetmealDish> {
}
