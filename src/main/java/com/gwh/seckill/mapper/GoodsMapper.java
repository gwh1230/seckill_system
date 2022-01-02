package com.gwh.seckill.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gwh.seckill.pojo.Goods;
import com.gwh.seckill.vo.GoodsVo;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author jobob
 * @since 2021-12-11
 */
public interface GoodsMapper extends BaseMapper<Goods> {

    List<GoodsVo> findGoodsVo();

    GoodsVo findGoodsVoByGoodsId(Long goodsId);
}
