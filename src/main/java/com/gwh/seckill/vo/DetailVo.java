package com.gwh.seckill.vo;

import com.gwh.seckill.pojo.User;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetailVo {

    private User user;

    private GoodsVo goodsVo;

    private Integer secKillStatus;

    private Integer remainSeconds;
}
