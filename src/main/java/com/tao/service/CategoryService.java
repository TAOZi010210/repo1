package com.tao.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tao.entity.Category;

public interface CategoryService extends IService<Category> {

    public void remove(Long id);

}
