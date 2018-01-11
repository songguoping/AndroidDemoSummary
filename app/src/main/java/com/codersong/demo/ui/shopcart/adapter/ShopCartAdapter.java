package com.codersong.demo.ui.shopcart.adapter;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.CheckedTextView;
import android.widget.EditText;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.codersong.demo.R;
import com.codersong.demo.bean.GoodsBean;
import com.codersong.demo.bean.MallBean;

import java.util.List;

/**
 * Created by user on 11/1/18.
 */

public class ShopCartAdapter extends BaseMultiItemQuickAdapter<MultiItemEntity,BaseViewHolder> {

    public static final int TYPE_LEVEL_0 = 0;
    public static final int TYPE_LEVEL_1 = 1;
    protected static final int KEY_DATA = 0xFFF11133;

    public ShopCartAdapter(List<MultiItemEntity> data) {
        super(data);
        addItemType(TYPE_LEVEL_0, R.layout.item_mall_info);
        addItemType(TYPE_LEVEL_1,R.layout.item_goods_info);
    }

    @Override
    protected void convert(BaseViewHolder helper, MultiItemEntity item) {
        switch (helper.getItemViewType()) {
            case TYPE_LEVEL_0:
                MallBean mall = (MallBean) item;
                helper.setText(R.id.tv_mall_name,mall.adress);
                CheckedTextView ctvMall = helper.getView(R.id.ctv_mall);
                if(mall.isselected){
                    ctvMall.setChecked(true);
                }else {
                    ctvMall.setChecked(false);
                }
                helper.addOnClickListener(R.id.ctv_mall);
                break;
            case TYPE_LEVEL_1:
                GoodsBean goods = (GoodsBean) item;
                helper.setText(R.id.tv_goods_price,goods.price);
                CheckedTextView ctvGoods = helper.getView(R.id.ctv_goods);
                EditText etGoodsCount = helper.getView(R.id.et_goods_count);
                if(goods.isselected){
                    ctvGoods.setChecked(true);
                }else {
                    ctvGoods.setChecked(false);
                }
                EditTextWatcher textWatcher = (EditTextWatcher) etGoodsCount.getTag(KEY_DATA);
                if (textWatcher != null) {
                    etGoodsCount.removeTextChangedListener(textWatcher);
                }
                etGoodsCount.setText(goods.count);
                EditTextWatcher watcher = new EditTextWatcher(goods);
                etGoodsCount.setTag(KEY_DATA,watcher);
                etGoodsCount.addTextChangedListener(watcher);
                helper.addOnClickListener(R.id.tv_goods_add).addOnClickListener(R.id.tv_goods_sub).addOnClickListener(R.id.iv_goods_del);
                break;
        }
    }

    /**
     * EditText内容改变的监听
     */
    class EditTextWatcher implements TextWatcher {

        private GoodsBean Gooddetail;

        public EditTextWatcher(GoodsBean item) {
            this.Gooddetail = item;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if(!TextUtils.isEmpty(s.toString().trim())){
                String textNum = s.toString().trim();
                Gooddetail.count=textNum;
            }
        }
    }
}
