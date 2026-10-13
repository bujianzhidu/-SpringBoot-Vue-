package com.example.tourism.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.tourism.entity.TourRoute;
import com.example.tourism.mapper.TourRouteMapper;
import com.example.tourism.service.TourRouteService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Service
public class TourRouteServiceImpl extends ServiceImpl<TourRouteMapper, TourRoute> implements TourRouteService {

    @Override
    public Page<TourRoute> search(String keyword, Integer days, String destination, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<TourRoute> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(keyword)) {
            wrapper.like(TourRoute::getName, keyword)
                    .or()
                    .like(TourRoute::getDescription, keyword);
        }
        
        if (days != null) {
            wrapper.eq(TourRoute::getDays, days);
        }
        
        if (StringUtils.hasText(destination)) {
            wrapper.like(TourRoute::getDestinations, destination);
        }
        
        wrapper.eq(TourRoute::getStatus, "ACTIVE")
                .orderByDesc(TourRoute::getCreateTime);
        
        return page(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    @Transactional
    public void createRoute(TourRoute tourRoute) {
        tourRoute.setStatus("ACTIVE");
        tourRoute.setCreateTime(LocalDateTime.now());
        tourRoute.setUpdateTime(LocalDateTime.now());
        save(tourRoute);
    }

    @Override
    @Transactional
    public void updateRoute(TourRoute tourRoute) {
        tourRoute.setUpdateTime(LocalDateTime.now());
        updateById(tourRoute);
    }

    @Override
    @Transactional
    public void updateStatus(Long id, String status) {
        TourRoute tourRoute = getById(id);
        if (tourRoute != null) {
            tourRoute.setStatus(status);
            tourRoute.setUpdateTime(LocalDateTime.now());
            updateById(tourRoute);
        }
    }
} 