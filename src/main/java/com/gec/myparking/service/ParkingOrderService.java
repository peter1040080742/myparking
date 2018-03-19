package com.gec.myparking.service;

import com.gec.myparking.dao.ParkingOrderMapper;
import com.gec.myparking.domain.ParkingOrder;
import com.gec.myparking.domain.ParkingPort;
import com.gec.myparking.domain.User;
import com.gec.myparking.dto.ParkingOrderDTO;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ParkingOrderService {
    @Autowired
    private ParkingOrderMapper parkingOrderMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private ParkingPortService portService;


    public PageInfo<ParkingOrder> getOrders(Integer page, Integer limit) {
        List<ParkingOrder> parkingOrders= null;
        if (page!= null &&  limit!=null)
        {
            PageHelper.startPage(page,limit);
            parkingOrders =parkingOrderMapper.selectAllOrders();
        }else
        {
            PageHelper.startPage(1,10);
            parkingOrders =parkingOrderMapper.selectAllOrders();
        }
        //包装内容
        PageInfo<ParkingOrder> pageInfo = new PageInfo<>(parkingOrders);
        return  pageInfo;
    }

	public List<ParkingOrderDTO> getOrdersByUserId(Integer userId) {
        List<ParkingOrder> orders = parkingOrderMapper.selectAllOrdersByUserId(userId);
        List<ParkingOrderDTO> orderDTOS =new ArrayList<>();
        for (ParkingOrder order : orders) {
            User user = userService.getUserById(order.getUserId());
            ParkingPort port = portService.getPortById(order.getCarPortId());
            ParkingOrderDTO dto = new ParkingOrderDTO(order.getId(),order.getBeginTime(),order.getEndTime(),user,port,order.getPrice(),order.getDuration(),order.getStatus());
            orderDTOS.add(dto);
        }

        return orderDTOS;
    }
}
