package com.jacky.reggie.dto;

import com.jacky.reggie.entity.Setmeal;
import com.jacky.reggie.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
