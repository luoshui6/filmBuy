package com.example.controller;

import com.example.common.Result;
import com.example.common.ResultCode;
import com.example.entity.Account;
import com.example.entity.SeatInfo;
import com.example.service.SeatInfoService;
import com.example.util.RedisUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
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
        //获取当前的用户
        Subject currentUser = SecurityUtils.getSubject();
        Account user = (Account) currentUser.getPrincipal();
        Long id = user.getId();
        //redis分布式锁占座位 10s后自动释放
        String LOCK_ID = seatInfo.getGoodsId().toString() + seatInfo.getPosition()+ "_lock";
        String seat = seatInfo.getGoodsId().toString() + seatInfo.getPosition();
        System.out.println(LOCK_ID);
        boolean lock = redisUtil.getLock(LOCK_ID, 10 * 1000);
        if (lock) {
             if (redisUtil.hasKey(seat)) {
                 return Result.error("该座位已被抢占,下单失败");
             }else {
                 redisUtil.set(seat, 1);


                 redisUtil.releaseLock(LOCK_ID);
                 return Result.success("抢到座位了");
             }
        }else {
            return Result.error("该座位已被抢占,下单失败");
        }
    }

    @GetMapping("/detail")
    public Result<SeatInfo> findByUserId(@RequestParam Long goodsId) {
        SeatInfo seatInfo = (SeatInfo)redisUtil.get(goodsId.toString());
        return Result.success(seatInfo);
//        return Result.success(seatInfoService.findDetail(goodsId));
    }

}
