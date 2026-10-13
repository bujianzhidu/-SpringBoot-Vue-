package com.example.tourism.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.tourism.entity.TourRoute;

public interface TourRouteService extends IService<TourRoute> {
    Page<TourRoute> search(String keyword, Integer days, String destination, Integer pageNum, Integer pageSize);
    void createRoute(TourRoute tourRoute);
    void updateRoute(TourRoute tourRoute);
    void updateStatus(Long id, String status);
} 