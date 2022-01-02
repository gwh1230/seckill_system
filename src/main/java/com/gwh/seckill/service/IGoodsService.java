package com.gwh.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gwh.seckill.pojo.Goods;
import com.gwh.seckill.vo.GoodsVo;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author jobob
 * @since 2021-12-11
 */
public interface IGoodsService extends IService<Goods> {

    List<GoodsVo> findGoodsVo();

    GoodsVo findGoodsVoByGoodsId(Long goodsId);
}
