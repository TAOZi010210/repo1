package com.tao.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tao.common.CustomException;
import com.tao.entity.Category;
import com.tao.entity.Dish;
import com.tao.entity.Setmeal;
import com.tao.mapper.CategoryMapper;
import com.tao.mapper.DishMapper;
import com.tao.service.CategoryService;
import com.tao.service.DishService;
import com.tao.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;

    @Override
    public void remove(Long id) {
        LambdaQueryWrapper<Dish> dishQueryWrapper = new LambdaQueryWrapper<>();
        dishQueryWrapper.eq(Dish::getCategoryId,id);
        int count = dishService.count(dishQueryWrapper);

        log.info("count 为{}",count);

        if(count > 0){
            //抛异常
            throw new CustomException("当前分类关联了菜品，无法删除");
        }

        LambdaQueryWrapper<Setmeal> setmealQueryWrapper = new LambdaQueryWrapper<>();
        setmealQueryWrapper.eq(Setmeal::getCategoryId,id);
        int count2 = setmealService.count(setmealQueryWrapper);

        if(count2 > 0){
            //抛异常
            throw new CustomException("当前分类关联了套餐，无法删除");
        }

        log.info("count2 为{}",count2);

        //正常删除
        super.removeById(id);
    }
}
