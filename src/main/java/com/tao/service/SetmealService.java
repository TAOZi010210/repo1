package com.tao.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tao.dto.SetmealDto;
import com.tao.entity.Setmeal;


public interface SetmealService extends IService<Setmeal> {

    void saveWithDish(SetmealDto setmealDto);

    SetmealDto getWithDishById(Long id);

    void updateWithDish(SetmealDto setmealDto);

    void deleteWithDishById(Long[] ids);
}
