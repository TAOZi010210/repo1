package com.tao.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tao.dto.DishDto;
import com.tao.entity.Dish;
import com.tao.entity.DishFlavor;
import com.tao.mapper.DishMapper;
import com.tao.service.DishFlavorService;
import com.tao.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;

    //操作多张表，开启事务
    @Transactional
    public void saveWithFlavor(DishDto dishDto){
        //保存菜品
        this.save(dishDto);

        //获取菜品ID，并保存口味表
        Long dishID = dishDto.getId();

        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishID);
            return item;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);
    }

    @Override
    public DishDto getWithFlavorById(Long id) {

        //查询菜品信息
        Dish dish = this.getById(id);
        DishDto dishDto = new DishDto();
        //拷贝信息到dto
        BeanUtils.copyProperties(dish,dishDto);

        //查询口味信息
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,id);
        List<DishFlavor> list = dishFlavorService.list(queryWrapper);

        dishDto.setFlavors(list);

        return dishDto;
    }

    @Override
    public void updateWithFlavor(DishDto dishDto) {
        this.updateById(dishDto);

        //获取菜品ID，并保存口味表
        Long dishID = dishDto.getId();

        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishID);
            return item;
        }).collect(Collectors.toList());

        dishFlavorService.updateBatchById(flavors);

    }

    @Transactional
    public void deleteWithFlavorById(Long[] ids){
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();

        for (int i = 0; i < ids.length; i++) {
            this.removeById(ids[i]);

            queryWrapper.eq(DishFlavor::getDishId,ids[i]);
            dishFlavorService.remove(queryWrapper);
        }
    }

}
