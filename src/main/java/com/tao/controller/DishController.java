package com.tao.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tao.common.R;
import com.tao.dto.DishDto;
import com.tao.entity.Category;
import com.tao.entity.Dish;
import com.tao.entity.DishFlavor;
import com.tao.service.CategoryService;
import com.tao.service.DishFlavorService;
import com.tao.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public R<String> save(@RequestBody DishDto dto){
        dishService.saveWithFlavor(dto);
        return R.success("保存成功");
    }

    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){

        //创建分页信息
        Page<Dish> pageInfo = new Page<>(page,pageSize);
        Page<DishDto> dishDtoPage = new Page<>();

        //构造查询条件
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        if(name != null) {
            queryWrapper.like(Dish::getName, name);
        }
        //执行查询
        dishService.page(pageInfo,queryWrapper);
        //拷贝信息
        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");

        List<Dish> list = pageInfo.getRecords();

        List<DishDto> dishDtoList = list.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item,dishDto);

            Category category = categoryService.getById(item.getCategoryId());
            dishDto.setCategoryName(category.getName());

            return dishDto;
        }).collect(Collectors.toList());

        dishDtoPage.setRecords(dishDtoList);
        return R.success(dishDtoPage);
    }

    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id){
        DishDto dishDto = dishService.getWithFlavorById(id);
        return R.success(dishDto);
    }

    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        dishService.updateWithFlavor(dishDto);
        return R.success("修改成功");
    }

    @PostMapping("/status/{status}")
    public R<String> changeStatus(@PathVariable Integer status,Long[] ids){
        log.info("接收到数据{}，{}",status,ids);
        Dish dish = new Dish();

        for (int i = 0; i < ids.length; i++) {
            dish.setId(ids[i]);
            dish.setStatus(status);
            dishService.updateById(dish);
        }
        return R.success("修改成功");
    }

    @DeleteMapping
    public R<String> delete(Long[] ids){
        dishService.deleteWithFlavorById(ids);
        return R.success("删除成功");
    }

    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish){
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dish::getStatus,1);
        queryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> list = dishService.list(queryWrapper);

        List<DishDto> dishDtoList = list.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item,dishDto);

            Category category = categoryService.getById(item.getCategoryId());
            dishDto.setCategoryName(category.getName());

            LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(DishFlavor::getDishId,item.getId());

            List<DishFlavor> dishFlavorList = dishFlavorService.list(lambdaQueryWrapper);

            dishDto.setFlavors(dishFlavorList);
            return dishDto;
        }).collect(Collectors.toList());

        return R.success(dishDtoList);

    }
}
