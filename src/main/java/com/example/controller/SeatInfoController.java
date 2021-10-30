package com.example.controller;

import com.example.common.Result;
import com.example.entity.SeatInfo;
import com.example.service.SeatInfoService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/seatInfo")
public class SeatInfoController {

    @Resource
    private SeatInfoService seatInfoService;

    @PostMapping()
    public Result add(@RequestBody SeatInfo seatInfo) {
        seatInfoService.save(seatInfo);
        return Result.success();
    }

    @GetMapping("/detail")
    public Result<SeatInfo> findByUserId(@RequestParam Long goodsId) {
        return Result.success(seatInfoService.findDetail(goodsId));
    }
}
