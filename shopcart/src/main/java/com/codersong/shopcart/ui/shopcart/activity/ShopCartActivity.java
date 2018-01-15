package com.codersong.shopcart.ui.shopcart.activity;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.res.AssetManager;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.codersong.shopcart.R;
import com.codersong.shopcart.bean.GoodsBean;
import com.codersong.shopcart.bean.MallBean;
import com.codersong.shopcart.ui.shopcart.adapter.ShopCartAdapter;
import com.codersong.shopcart.utils.SpacesItemDecoration;
import com.codersong.shopcart.utils.TextSizeUtils;
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

import static android.widget.AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL;

/**
 * Created by user on 11/1/18.
 * 购物车 Demo，解决一下问题
 * 1. 两级菜单显示，使用 BaseRecyclerViewAdapterHelper 开源库
 * 2. recycleview 中嵌套 Checkbox 的选择问题，一般在 bean 类添加一个 boolean 类型的字段辅助判断
 * 3. recycleview 中嵌套 EditText 的复用与光标问题
 *      3.1 recycleview 上下滑动的时候 EditText 内容会混乱，设置Tag ,通过 Tag 保存 EditTextWatcher；监听 recycleview 滑动
 *      3.2 recycleview 添加 android:descendantFocusability="beforeDescendants"；activity 添加 android:windowSoftInputMode="stateHidden|adjustPan"
 * 4. 贝塞尔曲线实现商品动画
 */

public class ShopCartActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private ShopCartAdapter mShopCartAdapter;
    private StringBuffer mStringBuffer;
    private TextView mToolbar;
    private TextView mTvTotalPrice;
    private int mTotalPrice=0;
    // 购物车图片显示
    private ImageView mTvShopCart;
    // 路径测量
    private PathMeasure mPathMeasure;
    // 贝塞尔曲线中间过程点坐标
    private float[] mCurrentPosition = new float[2];
    private RelativeLayout mRlCurve;

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
        mTvShopCart = (ImageView) findViewById(R.id.iv_shopcart);
        mRlCurve = (RelativeLayout) findViewById(R.id.rl_curve);
        mToolbar.setText("购物车");
        mTvTotalPrice.setText(String.valueOf(mTotalPrice));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new SpacesItemDecoration(TextSizeUtils.dip2px(this,1)));
        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (SCROLL_STATE_TOUCH_SCROLL == newState) {
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity
                            .INPUT_METHOD_SERVICE);
                    View focusView = getCurrentFocus();
                    if (focusView != null) {
                        inputMethodManager.hideSoftInputFromWindow(focusView.getWindowToken(), InputMethodManager
                                .HIDE_NOT_ALWAYS);
                        focusView.clearFocus();
                    }
                }
            }
        });
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
                //点击第一级菜单
                if(view.getId()==R.id.ctv_mall){
                    clickMallItem((MallBean) mShopCartAdapter.getData().get(position));
                    return;
                }
                //d点击第二级菜单
                EditText etGoodsCount = (EditText) adapter.getViewByPosition(mRecyclerView,position, R.id.et_goods_count);
                ImageView ivGoodsIcon = (ImageView) adapter.getViewByPosition(mRecyclerView, position, R.id.iv_goods_icon);
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
                        // 添加商品到购物车时的动画
                        addGoodsToCart(ivGoodsIcon);
                        break;
                    case R.id.tv_goods_sub:
                        if(count>1){
                            etGoodsCount.setText(String.valueOf(count-1));
                        }
                        break;
                    case R.id.iv_goods_del:
                        GoodsBean goodsBean = (GoodsBean) mShopCartAdapter.getData().get(position);
                        if(goodsBean.isselected){
                            mTotalPrice-=count*goodsBean.price;
                        }
                        int parentPosition = mShopCartAdapter.getParentPosition(goodsBean);
                        MallBean mallBean = (MallBean) mShopCartAdapter.getData().get(parentPosition);
                        mallBean.removeSubItem(goodsBean);
                        mallBean.goods.remove(goodsBean);
                        mShopCartAdapter.getData().remove(goodsBean);
                        mShopCartAdapter.setData(parentPosition,mallBean);
                        mShopCartAdapter.notifyDataSetChanged();
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
                        mTotalPrice-=beforeCount*goodsBean.price;
                        mTotalPrice+=goodsBean.count*goodsBean.price;
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
            System.out.println("clickMallItem size "+mallBean.goods.size());
            for (GoodsBean goods : mallBean.goods){
                if(goods.isselected){
                    //如果该店铺之前有商品选择了 总价则减去
                    mTotalPrice-=goods.count*goods.price;
                }
                goods.isselected=false;
            }
        }else {
            mallBean.isselected=true;
            //该店铺商品全选
            for (GoodsBean goods : mallBean.goods){
                if(!goods.isselected){
                    //如果该店铺之前商品没有选择 总价则加上
                    mTotalPrice+=goods.count*goods.price;
                }
                goods.isselected=true;
            }
        }
        mShopCartAdapter.notifyDataSetChanged();
        mTvTotalPrice.setText(String.valueOf(mTotalPrice));
    }

    private void clickGoodsItem(int count, int position) {
        MultiItemEntity entity = mShopCartAdapter.getData().get(position);
        if(entity instanceof GoodsBean){
            GoodsBean goodsBean = (GoodsBean) entity;
            if(goodsBean.isselected){
                goodsBean.isselected=false;
                mTotalPrice-=count*goodsBean.price;
            }else {
                goodsBean.isselected=true;
                mTotalPrice+=count*goodsBean.price;
            }
            //得到第一级菜单坐标
            int parentPosition = mShopCartAdapter.getParentPosition(goodsBean);
            MallBean mallBean = (MallBean) mShopCartAdapter.getData().get(parentPosition);
            List<GoodsBean> goodList = ((MallBean) mShopCartAdapter.getData().get(parentPosition)).goods;
            boolean isShowCtv=true;
            for (GoodsBean goods : goodList){
                if(!goods.isselected){
                    //第二级菜单只要有一个没有选择，第一级就不选择
                    isShowCtv=false;
                }
            }
            mallBean.isselected = isShowCtv;
            mShopCartAdapter.setData(parentPosition,mallBean);
            mShopCartAdapter.notifyDataSetChanged();
        }
    }

    private void addGoodsToCart(ImageView goodsImg) {
        // 创造出执行动画的主题goodsImg（这个图片就是执行动画的图片,从开始位置出发,经过一个抛物线（贝塞尔曲线）,移动到购物车里）
        final ImageView goods = new ImageView(this);
        goods.setImageDrawable(goodsImg.getDrawable());
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(100, 100);
        mRlCurve.addView(goods, params);

        // 得到父布局的起始点坐标（用于辅助计算动画开始/结束时的点的坐标）
        int[] parentLocation = new int[2];
        mRlCurve.getLocationInWindow(parentLocation);

        // 得到商品图片的坐标（用于计算动画开始的坐标）
        int startLoc[] = new int[2];
        goodsImg.getLocationInWindow(startLoc);
        // 得到购物车图片的坐标(用于计算动画结束后的坐标)
        int endLoc[] = new int[2];
        mTvShopCart.getLocationInWindow(endLoc);
        // 开始掉落的商品的起始点：商品起始点-父布局起始点+该商品图片的一半
        float startX = startLoc[0] - parentLocation[0] + goodsImg.getWidth() / 2;
        float startY = startLoc[1] - parentLocation[1] + goodsImg.getHeight() / 2;

        // 商品掉落后的终点坐标：购物车起始点-父布局起始点+购物车图片的1/5
        float toX = endLoc[0] - parentLocation[0] + mTvShopCart.getWidth() / 5;
        float toY = endLoc[1] - parentLocation[1];

        // 开始绘制贝塞尔曲线
        Path path = new Path();
        // 移动到起始点（贝塞尔曲线的起点）
        path.moveTo(startX, startY);
        // 使用二阶贝塞尔曲线：注意第一个起始坐标越大，贝塞尔曲线的横向距离就会越大，一般按照下面的式子取即可
        path.quadTo((startX + toX) / 2, startY, toX, toY);
        // mPathMeasure用来计算贝塞尔曲线的曲线长度和贝塞尔曲线中间插值的坐标，如果是true，path会形成一个闭环
        mPathMeasure = new PathMeasure(path, false);

        // 属性动画实现（从0到贝塞尔曲线的长度之间进行插值计算，获取中间过程的距离值）
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, mPathMeasure.getLength());
        valueAnimator.setDuration(500);

        // 匀速线性插值器
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                // 当插值计算进行时，获取中间的每个值，
                // 这里这个值是中间过程中的曲线长度（下面根据这个值来得出中间点的坐标值）
                float value = (Float) animation.getAnimatedValue();
                // 获取当前点坐标封装到mCurrentPosition
                // boolean getPosTan(float distance, float[] pos, float[] tan) ：
                // 传入一个距离distance(0<=distance<=getLength())，然后会计算当前距离的坐标点和切线，pos会自动填充上坐标，这个方法很重要。
                // mCurrentPosition此时就是中间距离点的坐标值
                mPathMeasure.getPosTan(value, mCurrentPosition, null);
                // 移动的商品图片（动画图片）的坐标设置为该中间点的坐标
                goods.setTranslationX(mCurrentPosition[0]);
                goods.setTranslationY(mCurrentPosition[1]);
            }
        });

        // 开始执行动画
        valueAnimator.start();

        // 动画结束后的处理
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                // 把执行动画的商品图片从父布局中移除
                mRlCurve.removeView(goods);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
    }


}
