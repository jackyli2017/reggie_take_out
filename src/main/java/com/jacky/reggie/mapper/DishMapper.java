package com.jacky.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jacky.reggie.entity.Dish;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DishMapper extends BaseMapper<Dish> {
}
