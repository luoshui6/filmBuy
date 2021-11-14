package com.example.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler"})
@Table(name = "goods_info")
public class GoodsInfo implements Serializable {
    /**
     * 主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 商品名称
     */
    private String name;

    /**
     * 商品描述
     */
    private String description;

    /**
     * 商品图片id
     */
    @Column(name = "fileIds")
    private String fileIds;

    /**
     * 商品价格
     */
    private Double price;

    /**
     * 商品销量
     */
    private Integer sales;

    /**
     * 商品点赞数
     */
    private Integer hot;

    private String actor;

    @Column(name = "beginTime")
    private String beginTime;

    private String time;

    /**
     * 所属类别
     */
    @Column(name = "typeId")
    private Long typeId;
    /**
     * 所属用户
     */
    @Column(name = "userId")
    private Long userId;

    @Column(name = "level")
    private Integer level;

    /**
     * 折扣
     */
    private Double discount;

    @Column(name = "recommend")
    private String recommend = "否";

    @Transient
    private String typeName;

    @Transient
    private String userName;

    @Transient
    private List<Long> fileList;

    /**
     * 商品评价状态
     */
    @Transient
    private String commentStatus;

    @Transient
    private Integer num;

    /**
     * 商品秒杀价格
     */
    private Double price_seckill;

    @Column(name = "startTime")
    private String startTime;

    @Column(name = "endTime")
    private String endTime;

    public Double getPrice_seckill() {
        return price_seckill;
    }

    public void setPrice_seckill(Double price_seckill) {
        this.price_seckill = price_seckill;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    /**
     * 获取主键
     *
     * @return id - 主键
     */
    public Long getId() {
        return id;
    }

    /**
     * 设置主键
     *
     * @param id 主键
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取商品名称
     *
     * @return name - 商品名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置商品名称
     *
     * @param name 商品名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取商品描述
     *
     * @return description - 商品描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * 设置商品描述
     *
     * @param description 商品描述
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * 获取商品价格
     *
     * @return price - 商品价格
     */
    public Double getPrice() {
        return price;
    }

    /**
     * 设置商品价格
     *
     * @param price 商品价格
     */
    public void setPrice(Double price) {
        this.price = price;
    }

    /**
     * 获取商品销量
     *
     * @return sales - 商品销量
     */
    public Integer getSales() {
        return sales;
    }

    /**
     * 设置商品销量
     *
     * @param sales 商品销量
     */
    public void setSales(Integer sales) {
        this.sales = sales;
    }

    /**
     * 获取商品点赞数
     *
     * @return hot - 商品点赞数
     */
    public Integer getHot() {
        return hot;
    }

    /**
     * 设置商品点赞数
     *
     * @param hot 商品点赞数
     */
    public void setHot(Integer hot) {
        this.hot = hot;
    }

    /**
     * 获取所属类别
     *
     * @return type_id - 所属类别
     */
    public Long getTypeId() {
        return typeId;
    }

    /**
     * 设置所属类别
     *
     * @param typeId 所属类别
     */
    public void setTypeId(Long typeId) {
        this.typeId = typeId;
    }

    /**
     * 获取所属用户
     *
     * @return user_id - 所属用户
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * 设置所属用户
     *
     * @param userId 所属用户
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getRecommend() {
        return recommend;
    }

    public void setRecommend(String recommend) {
        this.recommend = recommend;
    }

    public String getFileId() {
        return fileIds;
    }

    public void setFileId(String fileIds) {
        this.fileIds = fileIds;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFileIds() {
        return fileIds;
    }

    public void setFileIds(String fileIds) {
        this.fileIds = fileIds;
    }

    public List<Long> getFileList() {
        return fileList;
    }

    public void setFileList(List<Long> fileList) {
        this.fileList = fileList;
    }

    public Double getDiscount() {
        return discount;
    }

    public void setDiscount(Double discount) {
        this.discount = discount;
    }

    public String getCommentStatus() {
        return commentStatus;
    }

    public void setCommentStatus(String commentStatus) {
        this.commentStatus = commentStatus;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public String getActor() {
        return actor;
    }

    public void setActor(String actor) {
        this.actor = actor;
    }

    public String getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }
}