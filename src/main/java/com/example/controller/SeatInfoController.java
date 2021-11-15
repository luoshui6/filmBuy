package com.example.controller;

import com.example.common.Result;
import com.example.common.ResultCode;
import com.example.common.config.rabbitmq.MQSender;
import com.example.common.config.rabbitmq.MiaoshaMessage;
import com.example.entity.Account;
import com.example.entity.OrderInfo;
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

    @Autowired
    MQSender mQSender;


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
                 return Result.error("该座位已被抢占,下单失败！");
             }else {
                 redisUtil.set(seat, user.getName());
                 MiaoshaMessage miaoshaMessage = new MiaoshaMessage();
                 miaoshaMessage.setUserId(id);
                 miaoshaMessage.setLevel(user.getLevel());
                 miaoshaMessage.setTotal(1);
                 miaoshaMessage.setGoodsId(seatInfo.getGoodsId());
                 miaoshaMessage.setPosition(seatInfo.getPosition());

                 System.out.println(seatInfo.getPosition());

                 mQSender.sendMiaoshaMessage(miaoshaMessage);
                 redisUtil.releaseLock(LOCK_ID);
                 return Result.success("下单成功！");
             }
        }else {
            return Result.error("该座位已被抢占,下单失败!");
        }
    }

    @GetMapping("/detail")
    public Result<SeatInfo> findByUserId(@RequestParam Long goodsId) {
        return Result.success(seatInfoService.findDetail(goodsId));
    }

    @GetMapping("/detail_seckill")
    public Result<SeatInfo> findByUserId2(@RequestParam Long goodsId) {
        if (redisUtil.hasKey(goodsId.toString())){
            SeatInfo seatInfo = (SeatInfo)redisUtil.get(goodsId.toString());
            return Result.success(seatInfo);
        }else {
            return Result.success(seatInfoService.findDetail(goodsId));
        }

    }
}
