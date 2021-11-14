package com.example.controller;

import com.example.common.Result;
import com.example.entity.SeatInfo;
import com.example.service.SeatInfoService;
import com.example.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/seatInfo")
public class SeatInfoController {

    @Resource
    private SeatInfoService seatInfoService;

    @Autowired
    RedisUtil redisUtil;

    @PostMapping()
    public Result add(@RequestBody SeatInfo seatInfo) {
        seatInfoService.save(seatInfo);
        return Result.success();
    }

    @PostMapping("/seckill")
    public Result add_seckill(@RequestBody SeatInfo seatInfo) {
        System.out.println(seatInfo);
        return Result.error();
    }

    @GetMapping("/detail")
    public Result<SeatInfo> findByUserId(@RequestParam Long goodsId) {
        SeatInfo seatInfo = (SeatInfo)redisUtil.get(goodsId.toString());
        return Result.success(seatInfo);
//        return Result.success(seatInfoService.findDetail(goodsId));
    }

}
