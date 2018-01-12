package com.codersong.demo.ui.shopcart.activity;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.codersong.demo.R;
import com.codersong.demo.bean.GoodsBean;
import com.codersong.demo.bean.MallBean;
import com.codersong.demo.ui.shopcart.adapter.ShopCartAdapter;
import com.codersong.demo.utils.SpacesItemDecoration;
import com.codersong.demo.utils.TextSizeUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 11/1/18.
 */

public class ShopCartActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private ShopCartAdapter mShopCartAdapter;
    private StringBuffer mStringBuffer;
    private TextView mToolbar;
    private TextView mTvTotalPrice;
    private int mTotalPrice=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopcart);
        initView();
        initData();
        initEvent();

    }
    private void initView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_shopcart);
        mToolbar = (TextView) findViewById(R.id.tv_toolbar_title);
        mTvTotalPrice = (TextView) findViewById(R.id.tv_shopcart_total);
        mToolbar.setText("购物车");
        mTvTotalPrice.setText(String.valueOf(mTotalPrice));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new SpacesItemDecoration(TextSizeUtils.dip2px(this,1)));
    }

    private void initData() {
        AssetManager assetManager = getAssets();
        try {
            InputStream is = assetManager.open("shopcart.json");
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            mStringBuffer = new StringBuffer();
            String str = null;
            while ((str = br.readLine()) != null) {
                mStringBuffer.append(str);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Gson gson = new GsonBuilder().create();
        JsonArray array = new JsonParser().parse(mStringBuffer.toString()).getAsJsonArray();
        List<MallBean> malls = new ArrayList<>();
        for (final JsonElement elem : array) {
            malls.add(gson.fromJson(elem, MallBean.class));
        }
        final ArrayList<MultiItemEntity> res = new ArrayList<>();
        for (int i = 0; i < malls.size(); i++) {
            MallBean mallInfo = malls.get(i);
            for (int j = 0; j < malls.get(i).goods.size(); j++) {
                GoodsBean goodsInfo = malls.get(i).goods.get(j);
                //添加两级数据
                mallInfo.addSubItem(goodsInfo);
            }
            //添加一级数据
            res.add(mallInfo);
        }
        mShopCartAdapter = new ShopCartAdapter(res);
        mRecyclerView.setAdapter(mShopCartAdapter);
        mShopCartAdapter.bindToRecyclerView(mRecyclerView);
    }

    private void initEvent() {
        mRecyclerView.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                MultiItemEntity data = mShopCartAdapter.getData().get(position);
                if (data instanceof MallBean) {
                    if (((MallBean) data).isExpanded()) {
                        mShopCartAdapter.collapse(position, false);
                    } else {
                        mShopCartAdapter.expand(position, false);
                    }
                }
            }

            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                if(view.getId()==R.id.ctv_mall){
                    clickMallItem((MallBean) mShopCartAdapter.getData().get(position));
                    return;
                }
                EditText etGoodsCount = (EditText) adapter.getViewByPosition(mRecyclerView,position, R.id.et_goods_count);
                String sCount = etGoodsCount.getText().toString().trim();
                int count;
                if(!TextUtils.isEmpty(sCount)){
                    count = Integer.parseInt(sCount);
                }else {
                    count=1;
                }
                switch (view.getId()){
                    case R.id.tv_goods_add:
                        etGoodsCount.setText(String.valueOf(count+1));
                        break;
                    case R.id.tv_goods_sub:
                        if(count>1){
                            etGoodsCount.setText(String.valueOf(count-1));
                        }
                        break;
                    case R.id.iv_goods_del:
                        GoodsBean goodsBean = (GoodsBean) mShopCartAdapter.getData().get(position);
                        int parentPosition = mShopCartAdapter.getParentPosition(goodsBean);
                        ((MallBean)mShopCartAdapter.getData().get(parentPosition)).removeSubItem(goodsBean);
                        mShopCartAdapter.getData().remove(position);
                        mShopCartAdapter.notifyDataSetChanged();
                        mTotalPrice-=count*Integer.parseInt(goodsBean.price);
                        break;
                    case R.id.ctv_goods:
                        clickGoodsItem(count,position);
                        break;
                }
                mTvTotalPrice.setText(String.valueOf(mTotalPrice));
            }
        });

        mShopCartAdapter.setOnTextChangeListener(new ShopCartAdapter.OnTextChangeListener() {
            @Override
            public void textChanged(int beforeCount,int position) {
                MultiItemEntity entity = mShopCartAdapter.getData().get(position);
                if(entity instanceof GoodsBean){
                    GoodsBean goodsBean = (GoodsBean) entity;
                    if(goodsBean.isselected){
                        //把上次改变的减掉再加上本次改变的
                        mTotalPrice-=beforeCount*Integer.parseInt(goodsBean.price);
                        mTotalPrice+=Integer.parseInt(goodsBean.count)*Integer.parseInt(goodsBean.price);
                        mTvTotalPrice.setText(String.valueOf(mTotalPrice));
                    }
                }
            }
        });
    }

    private void clickMallItem(MallBean mallBean) {
        if(mallBean.isselected){
            mallBean.isselected=false;
            //取消该店铺商品全选
            for (GoodsBean goods : mallBean.goods){
                goods.isselected=false;
            }
        }else {
            mallBean.isselected=true;
            //该店铺商品全选
            for (GoodsBean goods : mallBean.goods){
                goods.isselected=true;
            }
        }
        mShopCartAdapter.notifyDataSetChanged();
    }

    private void clickGoodsItem(int count, int position) {
        MultiItemEntity entity = mShopCartAdapter.getData().get(position);
        if(entity instanceof GoodsBean){
            GoodsBean goodsBean = (GoodsBean) entity;
            if(goodsBean.isselected){
                goodsBean.isselected=false;
                mTotalPrice-=count*Integer.parseInt(goodsBean.price);
            }else {
                goodsBean.isselected=true;
                mTotalPrice+=count*Integer.parseInt(goodsBean.price);
            }
            mShopCartAdapter.notifyDataSetChanged();
        }
    }

}
