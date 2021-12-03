package com.example.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SeatUserVo implements Serializable {
    private Long seatId;
    private String position;
    private Long goodsId;
    private Long userId;
    private String name;
    private Integer level;

}
