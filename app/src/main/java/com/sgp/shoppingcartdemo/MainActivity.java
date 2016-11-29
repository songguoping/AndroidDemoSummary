package com.sgp.shoppingcartdemo;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.res.AssetManager;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.sgp.shoppingcartdemo.adapter.ExpandableListAdapter;
import com.sgp.shoppingcartdemo.bean.GoodBean;
import com.sgp.shoppingcartdemo.view.SmoothCheckBox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity implements UpdateView, View.OnClickListener {

    private ExpandableListView mExpandableListView;
    private SmoothCheckBox mCbSelectAll;
    private TextView mTvAllMoney;
    private Button mBtnBuy;
    StringBuffer          stringBuffer;
    GoodBean              goodBean;
    ExpandableListAdapter adapter;
    // 购物车图片显示
    private ImageView mShoppingCartIv;
    // 路径测量
    private PathMeasure mPathMeasure;
    // 贝塞尔曲线中间过程点坐标
    private float[] mCurrentPosition = new float[2];
    // 购物车商品数目
    private RelativeLayout mRlCurve;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
        initEvent();
    }

    private void initEvent() {
        mCbSelectAll.setOnClickListener(this);
    }

    private void initData() {
        AssetManager assetManager = getAssets();
        try {
            InputStream is = assetManager.open("data.json");
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            stringBuffer = new StringBuffer();
            String str = null;
            while ((str = br.readLine()) != null) {
                stringBuffer.append(str);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Gson gson = new Gson();
        goodBean = gson.fromJson(stringBuffer.toString(), GoodBean.class);
        adapter = new ExpandableListAdapter(this,goodBean);
        adapter.setChangedListener(this);
        mExpandableListView.setAdapter(adapter);
        for (int i = 0; i < goodBean.getContent().size(); i++) {
            mExpandableListView.expandGroup(i);
        }

    }

    private void initView() {
        mExpandableListView = (ExpandableListView) findViewById(R.id.expandableListView);
        mCbSelectAll = (SmoothCheckBox) findViewById(R.id.cb_SelectAll);
        mTvAllMoney = (TextView) findViewById(R.id.tv_AllMoney);
        mBtnBuy = (Button) findViewById(R.id.btn_Settlement);
        mRlCurve = (RelativeLayout) findViewById(R.id.rl_curve);
        mShoppingCartIv = (ImageView) findViewById(R.id.iv_bezier_curve_shopping_cart);

        mExpandableListView.setGroupIndicator(null);

        //用于列表滑动时，EditText清除焦点，收起软键盘
        mExpandableListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                if (SCROLL_STATE_TOUCH_SCROLL == scrollState) {
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

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

            }
        });


    }

    @Override
    public void update(boolean isAllSelected, int count, int price) {
        mBtnBuy.setText("结算("+count+")");
        mTvAllMoney.setText("￥"+price);
        mCbSelectAll.setChecked(isAllSelected);
    }

    @Override
    public void callBackImg(ImageView goodsImg) {
        // 添加商品到购物车时的动画
        addGoodsToCart(goodsImg);
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
        mShoppingCartIv.getLocationInWindow(endLoc);
        // 开始掉落的商品的起始点：商品起始点-父布局起始点+该商品图片的一半
        float startX = startLoc[0] - parentLocation[0] + goodsImg.getWidth() / 2;
        float startY = startLoc[1] - parentLocation[1] + goodsImg.getHeight() / 2;

        // 商品掉落后的终点坐标：购物车起始点-父布局起始点+购物车图片的1/5
        float toX = endLoc[0] - parentLocation[0] + mShoppingCartIv.getWidth() / 5;
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

    @Override
    public void onClick(View view) {
        selectAll();
    }

    private void selectAll(){
        int allCount=goodBean.getAllcount();
        int allMoney=goodBean.getAllmoney();
        if(!mCbSelectAll.isChecked()){
            goodBean.setAllSelect(true);
            for(int i=0;i<goodBean.getContent().size();i++){
                goodBean.getContent().get(i).setIsselected(true);
                for(int n=0;n<goodBean.getContent().get(i).getGooddetail().size();n++){
                    if(!goodBean.getContent().get(i).getGooddetail().get(n).isselected()){
                        allCount++;
                        allMoney+=Integer.valueOf(goodBean.getContent().get(i).getGooddetail().get(n).getCount())
                                *Integer.valueOf(goodBean.getContent().get(i).getGooddetail().get(n).getPrice());
                        goodBean.getContent().get(i).getGooddetail().get(n).setIsselected(true);
                    }
                }
            }
        }else{
            goodBean.setAllSelect(false);
            for(int i=0;i<goodBean.getContent().size();i++){
                goodBean.getContent().get(i).setIsselected(false);
                for(int n=0;n<goodBean.getContent().get(i).getGooddetail().size();n++){
                    goodBean.getContent().get(i).getGooddetail().get(n).setIsselected(false);
                }
                allCount=0;
                allMoney=0;
            }
        }
        goodBean.setAllmoney(allMoney);
        goodBean.setAllcount(allCount);
        update(goodBean.isAllSelect(),allCount,allMoney);
        adapter.notifyDataSetChanged();
    }

}
