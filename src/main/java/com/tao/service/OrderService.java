package com.tao.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tao.entity.Orders;

public interface OrderService extends IService<Orders> {
    void submit(Orders orders);
}
