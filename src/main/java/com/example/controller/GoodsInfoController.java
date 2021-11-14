package com.example.controller;

import com.example.common.Result;
import com.example.entity.GoodsInfo;
import com.example.entity.Account;
import com.example.entity.SeatInfo;
import com.example.service.GoodsInfoService;
import com.example.service.SeatInfoService;
import com.example.util.RedisUtil;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping(value = "/goodsInfo")
public class GoodsInfoController implements InitializingBean {
    @Resource
    private GoodsInfoService goodsInfoService;

    @Autowired
    RedisUtil redisUtil;

    @Resource
    private SeatInfoService seatInfoService;

    @PostMapping
    public Result<GoodsInfo> add(@RequestBody GoodsInfo goodsInfo, HttpServletRequest request) {
        Account user = (Account) request.getSession().getAttribute("user");
        goodsInfo.setUserId(user.getId());
        goodsInfo.setLevel(user.getLevel());
        goodsInfoService.add(goodsInfo);
        return Result.success(goodsInfo);
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Long id) {
        goodsInfoService.delete(id);
        return Result.success();
    }

    @PutMapping
    public Result update(@RequestBody GoodsInfo goodsInfo) {
        goodsInfoService.update(goodsInfo);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<GoodsInfo> detail(@PathVariable Long id) {
        GoodsInfo goodsInfo = goodsInfoService.findById(id);
        return Result.success(goodsInfo);
    }

    @GetMapping
    public Result<List<GoodsInfo>> all() {
        return Result.success(goodsInfoService.findAll());
    }

    @GetMapping("/page/{name}")
    public Result<PageInfo<GoodsInfo>> page(@RequestParam(defaultValue = "1") Integer pageNum,
                                            @RequestParam(defaultValue = "10") Integer pageSize,
                                            @PathVariable String name,
                                            HttpServletRequest request) {
        return Result.success(goodsInfoService.findPage(pageNum, pageSize, name, request));
    }

    @GetMapping("/findByType/{typeId}")
    public Result<List<GoodsInfo>> findByType(@PathVariable Integer typeId) {
        return Result.success(goodsInfoService.findByType(typeId));
    }

    @GetMapping("/recommend")
    public Result<PageInfo<GoodsInfo>> recommendGoods(@RequestParam(defaultValue = "1") Integer pageNum,
                                                      @RequestParam(defaultValue = "6") Integer pageSize) {
        return Result.success(goodsInfoService.findRecommendGoods(pageNum, pageSize));
    }

    @GetMapping("/sales")
    public Result<PageInfo<GoodsInfo>> sales(@RequestParam(defaultValue = "1") Integer pageNum,
                                             @RequestParam(defaultValue = "6") Integer pageSize) {
        return Result.success(goodsInfoService.findHotSalesGoods(pageNum, pageSize));
    }

    /**
     * 查询用户买到过的所有商品
     * @return
     */
    @GetMapping("/comment/{userId}/{level}")
    public Result<List<GoodsInfo>> orderGoods(@PathVariable("userId") Long userId,
                                              @PathVariable("level") Integer level) {
        return Result.success(goodsInfoService.getOrderGoods(userId, level));
    }

    @GetMapping("/searchGoods")
    public Result<List<GoodsInfo>> searchGoods(@RequestParam String text) {
        return Result.success(goodsInfoService.searchGoods(text));
    }


    /**
     * 系统初始化的时候做的事情。
     * 在容器启动时候，检测到了实现了接口InitializingBean之后，
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        List<SeatInfo> seatslist = seatInfoService.findAllDetail();
//        System.out.println(seatslist);
        if(seatslist == null) {
            return;
        }
        for(SeatInfo seats:seatslist) {
            //如果不是null的时候，将库存加载到redis里面去
            redisUtil.set(seats.getGoodsId().toString(), seats);
        }
    }
}
