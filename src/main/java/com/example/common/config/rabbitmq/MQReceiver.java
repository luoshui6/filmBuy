package com.example.common.config.rabbitmq;


import com.example.common.Result;
import com.example.common.ResultCode;
import com.example.entity.GoodsInfo;
import com.example.entity.OrderInfo;
import com.example.entity.SeatInfo;
import com.example.exception.CustomException;
import com.example.service.GoodsInfoService;
import com.example.service.OrderInfoService;
import com.example.service.SeatInfoService;
import com.example.util.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

//接收者
@Service
public class MQReceiver {

	@Autowired
	RedisUtil redisUtil;

	@Autowired
	SeatInfoService seatInfoService;

	@Resource
	private GoodsInfoService goodsInfoService;

	@Resource
	private OrderInfoService orderInfoService;

	private static Logger log= LoggerFactory.getLogger(MQReceiver.class);

	public static String trimFirstAndLastChar(String str, String element){
		boolean beginIndexFlag = true;
		boolean endIndexFlag = true;
		do{
			int beginIndex = str.indexOf(element) == 0 ? 1 : 0;
			int endIndex = str.lastIndexOf(element) + 1 == str.length() ? str.lastIndexOf(element) : str.length();
			str = str.substring(beginIndex, endIndex);
			beginIndexFlag = (str.indexOf(element) == 0);
			endIndexFlag = (str.lastIndexOf(element) + 1 == str.length());
		} while (beginIndexFlag || endIndexFlag);
		return str;
	}


	@RabbitListener(queues=MQConfig.MIAOSHA_QUEUE)//指明监听的是哪一个queue
	public void receiveMiaosha(String message) {
		log.info("receiveMiaosha message:"+message);
		//通过string类型的message还原成bean
		//拿到了秒杀信息之后。开始业务逻辑秒杀，
		MiaoshaMessage mm =redisUtil.stringToBean(message, MiaoshaMessage.class);

		Long goodsId = mm.getGoodsId();
		if (redisUtil.hasKey(mm.getGoodsId().toString())) {
			SeatInfo info = (SeatInfo)redisUtil.get(mm.getGoodsId().toString());
			String oldPosition = info.getPosition();
			String addPosition = mm.getPosition();
			oldPosition = trimFirstAndLastChar(oldPosition, "[");
			oldPosition = trimFirstAndLastChar(oldPosition, "]");
			addPosition = trimFirstAndLastChar(addPosition, "[");
			addPosition = trimFirstAndLastChar(addPosition, "]");

			String newPosition = "["+oldPosition+",".concat(addPosition)+"]";
			info.setPosition(newPosition);

			redisUtil.set(mm.getGoodsId().toString(),info);

			//更新数据库
			seatInfoService.save(info);
		}else {
			//更新数据库
			SeatInfo info = new SeatInfo();
			info.setGoodsId(mm.getGoodsId());
			info.setPosition(mm.getPosition());
			seatInfoService.save(info);
			//更新缓存
			SeatInfo seatInfo = seatInfoService.findDetail(mm.getGoodsId());
			redisUtil.set(mm.getGoodsId().toString(), seatInfo);
		}



//		String position = mm.getPosition();

		//判断座位是否被占
//		SeatInfo detail = seatInfoService.findDetail(goodsId);
//		String allPosition = detail.getPosition();
//
//		String allPosition_ = trimFirstAndLastChar(allPosition, "[");
//		allPosition_ = trimFirstAndLastChar(allPosition_, "]");
//
//		String position_ = trimFirstAndLastChar(position, "[");
//		position_ = trimFirstAndLastChar(position_, "]");
//		System.out.println(position+allPosition);

//		if (allPosition_.indexOf(position_) == -1) {
			Long userId = mm.getUserId();
			Integer total = mm.getTotal();
			GoodsInfo goodsInfo = goodsInfoService.findById(goodsId);
			if (userId == null) {
				throw new CustomException(ResultCode.PARAM_ERROR);
			}
			if (goodsInfo != null) {
				List<GoodsInfo> goodsList = Collections.singletonList(goodsInfo);
				mm.setTotalPrice(total * goodsInfo.getPrice() * goodsInfo.getDiscount());
				mm.setGoodsList(goodsList);
				mm.setStatus("待付款");
				orderInfoService.add(mm);
			}else {
				throw new CustomException(ResultCode.PARAM_ERROR);
			}
		}


	}
	
	
	
	
	
//	@RabbitListener(queues=MQConfig.QUEUE)//指明监听的是哪一个queue
//	public void receive(String message) {
//		log.info("receive message:"+message);
//	}
//	
//	@RabbitListener(queues=MQConfig.TOPIC_QUEUE1)//指明监听的是哪一个queue
//	public void receiveTopic1(String message) {
//		log.info("receiveTopic1 message:"+message);
//	}
//	
//	@RabbitListener(queues=MQConfig.TOPIC_QUEUE2)//指明监听的是哪一个queue
//	public void receiveTopic2(String message) {
//		log.info("receiveTopic2 message:"+message);
//	}
//	
//	@RabbitListener(queues=MQConfig.HEADER_QUEUE)//指明监听的是哪一个queue
//	public void receiveHeaderQueue(byte[] message) {
//		log.info("receive Header Queue message:"+new String(message));
//	}

