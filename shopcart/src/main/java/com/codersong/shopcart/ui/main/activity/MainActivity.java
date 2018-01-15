package com.codersong.shopcart.ui.main.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.codersong.shopcart.R;
import com.codersong.shopcart.ui.main.adapter.MainAdapter;
import com.codersong.shopcart.ui.shopcart.activity.ShopCartActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity  {


    private RecyclerView mRvMain;
    private MainAdapter mMainAdapter;
    private List<String> mModuleClass = new ArrayList<>();
    private TextView mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
    }

    private void initView() {
        mRvMain = (RecyclerView) findViewById(R.id.rv_main);
        mToolbar = (TextView) findViewById(R.id.tv_toolbar_title);
        mToolbar.setText(getString(R.string.app_name));
        mRvMain.setLayoutManager(new GridLayoutManager(this,2));
    }

    private void initData() {
        //添加各个模块activity
        mModuleClass.add(ShopCartActivity.class.getName());
        mMainAdapter = new MainAdapter(mModuleClass);
        mRvMain.setAdapter(mMainAdapter);
        mRvMain.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                String activityName = mModuleClass.get(position);
                startActivity(new Intent().setClassName(MainActivity.this,activityName));
            }
        });
    }

}
