package com.tao.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tao.dto.DishDto;
import com.tao.dto.SetmealDto;
import com.tao.entity.Dish;
import com.tao.entity.DishFlavor;
import com.tao.entity.Setmeal;
import com.tao.entity.SetmealDish;
import com.tao.mapper.SetmealMapper;
import com.tao.service.SetmealDishService;
import com.tao.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;

    @Override
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
        //保存套餐
        this.save(setmealDto);

        //获取菜品ID，并保存套餐菜品表
        Long setmealID = setmealDto.getId();

        List<SetmealDish> dishes = setmealDto.getSetmealDishes();
        dishes = dishes.stream().map((item) -> {
            item.setSetmealId(setmealID);
            return item;
        }).collect(Collectors.toList());

        setmealDishService.saveBatch(dishes);
    }

    @Override
    public SetmealDto getWithDishById(Long id) {

        //查询套餐信息
        Setmeal setmeal = this.getById(id);
        SetmealDto setmealDto = new SetmealDto();
        //拷贝信息到dto
        BeanUtils.copyProperties(setmeal,setmealDto);

        //查询套餐菜品信息
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,id);
        List<SetmealDish> list = setmealDishService.list(queryWrapper);

        setmealDto.setSetmealDishes(list);

        return setmealDto;
    }

    @Override
    public void updateWithDish(SetmealDto setmealDto) {
        this.updateById(setmealDto);

        //获取菜品ID，并保存口味表
        Long setmealID = setmealDto.getId();

        List<SetmealDish> dishes = setmealDto.getSetmealDishes();
        dishes = dishes.stream().map((item) -> {
            item.setSetmealId(setmealID);
            return item;
        }).collect(Collectors.toList());

        setmealDishService.updateBatchById(dishes);
    }

    @Override
    @Transactional
    public void deleteWithDishById(Long[] ids) {
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();

        for (int i = 0; i < ids.length; i++) {
            this.removeById(ids[i]);

            queryWrapper.eq(SetmealDish::getDishId,ids[i]);
            setmealDishService.remove(queryWrapper);
        }
    }

}
