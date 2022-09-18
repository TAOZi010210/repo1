package com.tao.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tao.common.BaseContext;
import com.tao.common.R;
import com.tao.dto.DishDto;
import com.tao.dto.OrdersDto;
import com.tao.entity.*;
import com.tao.service.AddressBookService;
import com.tao.service.OrderDetailService;
import com.tao.service.OrderService;
import com.tao.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderDetailService orderDetailService;

    @Autowired
    private UserService userService;

    @Autowired
    private AddressBookService addressBookService;

    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
        log.info(orders.toString());
        orderService.submit(orders);

        return R.success("成功");
    }

    @GetMapping("/userPage")
    public R<Page> list(int page,int pageSize) {
        //创建分页信息
        Page<Orders> pageInfo = new Page<>(page,pageSize);
        Page<OrdersDto> ordersDtoPage = new Page<>();

        LambdaQueryWrapper<Orders> orderQueryWrapper = new LambdaQueryWrapper<>();
        orderQueryWrapper.eq(Orders::getUserId, BaseContext.getCurrentId());
        orderQueryWrapper.orderByDesc(Orders::getOrderTime);

        List<Orders> ordersList = orderService.list(orderQueryWrapper);

        orderService.page(pageInfo,orderQueryWrapper);

        User user = userService.getById(BaseContext.getCurrentId());


        //拷贝信息
        BeanUtils.copyProperties(pageInfo,ordersDtoPage,"records");

        List<OrdersDto> ordersDtoList = ordersList.stream().map((item) -> {
            OrdersDto ordersDto = new OrdersDto();
            BeanUtils.copyProperties(item,ordersDto);

            LambdaQueryWrapper<OrderDetail> orderDetailLambdaQueryWrapper = new LambdaQueryWrapper<>();
            orderDetailLambdaQueryWrapper.eq(OrderDetail::getOrderId,item.getId());
            List<OrderDetail> orderDetails = orderDetailService.list(orderDetailLambdaQueryWrapper);

            AddressBook addressbook = addressBookService.getById(item.getAddressBookId());

            ordersDto.setOrderDetails(orderDetails);

            ordersDto.setAddressBookId(addressbook.getId());
            ordersDto.setConsignee(addressbook.getConsignee());


            return ordersDto;
        }).collect(Collectors.toList());

        ordersDtoPage.setRecords(ordersDtoList);
        return R.success(ordersDtoPage);
    }
}
