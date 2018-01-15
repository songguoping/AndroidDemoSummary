package com.codersong.shopcart.bean;

import com.chad.library.adapter.base.entity.AbstractExpandableItem;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.codersong.shopcart.ui.shopcart.adapter.ShopCartAdapter;

import java.io.Serializable;
import java.util.List;

/**
 * Created by user on 11/1/18.
 */

public class MallBean extends AbstractExpandableItem<GoodsBean> implements Serializable,MultiItemEntity {
    /**
     * id : 0
     * adress : 小米官方旗舰店
     * isselected : false
     * goods : [{"id":"00","count":"1","name":"小米Note3","price":"1899","isselected":"false"},{"id":"01","count":"1","name":"小米6","price":"2299","isselected":"false"},{"id":"02","count":"1","name":"小米MIX2","price":"3299","isselected":"false"}]
     */

    public String id;
    public String adress;
    public boolean isselected;
    public List<GoodsBean> goods;

    @Override
    public int getLevel() {
        return 0;
    }

    @Override
    public int getItemType() {
        return ShopCartAdapter.TYPE_LEVEL_0;
    }
}
