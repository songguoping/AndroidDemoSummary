package com.codersong.shopcart.ui.main.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.codersong.shopcart.R;

import java.util.List;

/**
 * Created by sgp on 11/1/18.
 */

public class MainAdapter extends BaseQuickAdapter<String,BaseViewHolder> {

    public MainAdapter(@Nullable List<String> data) {
        super(R.layout.item_main,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {

    }
}
