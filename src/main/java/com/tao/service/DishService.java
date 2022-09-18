package com.tao.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tao.dto.DishDto;
import com.tao.entity.Dish;

public interface DishService extends IService<Dish> {

    void saveWithFlavor(DishDto dishDto);

    DishDto getWithFlavorById(Long id);

    void updateWithFlavor(DishDto dishDto);

    void deleteWithFlavorById(Long[] ids);
}
