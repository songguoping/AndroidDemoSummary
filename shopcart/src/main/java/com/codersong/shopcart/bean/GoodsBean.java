package com.codersong.shopcart.bean;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.codersong.shopcart.ui.shopcart.adapter.ShopCartAdapter;

import java.io.Serializable;

/**
 * Created by user on 11/1/18.
 */

public class GoodsBean implements Serializable,MultiItemEntity{
    /**
     * id : 00
     * count : 1
     * name : 小米Note3
     * price : 1899
     * isselected : false
     */

    public String id;
    public int count;
    public String name;
    public int price;
    public boolean isselected;

    @Override
    public int getItemType() {
        return ShopCartAdapter.TYPE_LEVEL_1;
    }
}
