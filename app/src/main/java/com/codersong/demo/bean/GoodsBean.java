package com.codersong.demo.bean;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.codersong.demo.ui.shopcart.adapter.ShopCartAdapter;

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
    public String count;
    public String name;
    public String price;
    public boolean isselected;

    @Override
    public int getItemType() {
        return ShopCartAdapter.TYPE_LEVEL_1;
    }
}
