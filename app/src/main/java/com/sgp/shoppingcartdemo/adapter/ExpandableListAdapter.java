package com.sgp.shoppingcartdemo.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sgp.shoppingcartdemo.R;
import com.sgp.shoppingcartdemo.UpdateView;
import com.sgp.shoppingcartdemo.bean.GoodBean;
import com.sgp.shoppingcartdemo.view.SmoothCheckBox;


public class ExpandableListAdapter extends BaseExpandableListAdapter {


    private Context    context;
    private GoodBean   goodBean;
    private UpdateView updateViewListener;
    protected static final int KEY_DATA = 0xFFF11133;

    public ExpandableListAdapter(Context context, GoodBean goodBean) {
        this.context = context;
        this.goodBean = goodBean;
    }

    @Override
    public int getGroupCount() {
        return goodBean.getContent().size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return goodBean.getContent().get(groupPosition).getGooddetail().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return goodBean.getContent().get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return goodBean.getContent().get(groupPosition).getGooddetail().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        GroupViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_shopingcargroup, parent, false);
            holder = new GroupViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (GroupViewHolder) convertView.getTag();
        }
        holder.cbGroupItem.setTag(groupPosition);
        holder.cbGroupItem.setOnClickListener(listener);
        holder.tvPosition.setText(goodBean.getContent().get(groupPosition).getAdress());
        if (goodBean.getContent().get(groupPosition).isselected()) {
            if (!holder.cbGroupItem.isChecked()) {
                holder.cbGroupItem.setChecked(true);
            }
        } else {
            holder.cbGroupItem.setChecked(false);
        }
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ChildViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_shopingcarchild, parent, false);
            holder = new ChildViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ChildViewHolder) convertView.getTag();
        }
        String tag = groupPosition + "," + childPosition;
        holder.cbItem.setTag(tag);
        holder.tvReduce.setTag(tag);
        holder.tvAdd.setTag(tag);
        holder.ivDelete.setTag(tag);
        holder.ivIcon.setTag(tag);
        holder.cbItem.setOnClickListener(listener);
        holder.tvReduce.setOnClickListener(listener);
        final ChildViewHolder finalHolder = holder;
        holder.tvAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //添加商品数量
                updateViewListener.callBackImg(finalHolder.ivIcon);
                String tag = view.getTag().toString();
                String[] split;
                int groupId = 0;
                int childId = 0;
                int allCount = goodBean.getAllcount();
                int allMoney = goodBean.getAllmoney();
                if (tag.contains(",")) {
                    split = tag.split(",");
                    groupId = Integer.parseInt(split[0]);
                    childId = Integer.parseInt(split[1]);
                }
                String var2 = goodBean.getContent().get(groupId).getGooddetail().get(childId).getCount();
                goodBean.getContent().get(groupId).getGooddetail().get(childId).setCount(addCount(var2));
                allMoney = goodBean.getAllmoney();
                if (goodBean.getContent().get(groupId).getGooddetail().get(childId).isselected()) {
                    allMoney += Integer.valueOf(goodBean.getContent().get(groupId).getGooddetail().get(childId).getPrice());
                    updateViewListener.update(goodBean.isAllSelect(), allCount, allMoney);
                }
                goodBean.setAllmoney(allMoney);
                notifyDataSetChanged();
            }
        });

        holder.ivDelete.setOnClickListener(listener);
        if (goodBean.getContent().get(groupPosition).getGooddetail().get(childPosition).isselected()) {
            holder.cbItem.setChecked(true);
        } else {
            holder.cbItem.setChecked(false);
        }
        holder.tvPrice.setText("￥" + goodBean.getContent().get(groupPosition).getGooddetail().get(childPosition).getPrice());
        EditTextWatcher textWatcher = (EditTextWatcher) holder.etCount.getTag(KEY_DATA);
        if (textWatcher != null) {
            holder.etCount.removeTextChangedListener(textWatcher);
        }
        holder.etCount.setText(String.valueOf(goodBean.getContent().get(groupPosition).getGooddetail().get(childPosition).getCount()));
        EditTextWatcher watcher = new EditTextWatcher(goodBean.getContent().get(groupPosition).getGooddetail().get(childPosition));
        holder.etCount.setTag(KEY_DATA,watcher);
        holder.etCount.addTextChangedListener(watcher);

        holder.etCount.setText(goodBean.getContent().get(groupPosition).getGooddetail().get(childPosition).getCount());

        return convertView;

    }

    /**
     * EditText内容改变的监听
     */
    class EditTextWatcher implements TextWatcher {

        private GoodBean.ContentBean.GooddetailBean Gooddetail;

        public EditTextWatcher(GoodBean.ContentBean.GooddetailBean item) {
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
                Gooddetail.setCount(textNum);
            }
        }
    }
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }


    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            SmoothCheckBox checkBox;
            String tag = v.getTag().toString();
            String[] split;
            int groupId = 0;
            int childId = 0;
            int childSize = 0;
            int groupPosition = 0;
            int allCount = goodBean.getAllcount();
            int allMoney = goodBean.getAllmoney();
            if (tag.contains(",")) {
                split = tag.split(",");
                groupId = Integer.parseInt(split[0]);
                childId = Integer.parseInt(split[1]);
            } else {
                groupPosition = Integer.parseInt(tag);
                childSize = goodBean.getContent().get(groupPosition).getGooddetail().size();
            }
            switch (v.getId()) {
                case R.id.cb_GroupItem:
                    checkBox = (SmoothCheckBox) v;
                    goodBean.getContent().get(groupPosition).setIsselected(!checkBox.isChecked());
                    if (!checkBox.isChecked()) {
                        for (int i = 0; i < childSize; i++) {
                            if (!goodBean.getContent().get(groupPosition).getGooddetail().get(i).isselected()) {
                                allCount++;
                                goodBean.getContent().get(groupPosition).getGooddetail().get(i).setIsselected(!checkBox.isChecked());
                                allMoney += Integer.valueOf(goodBean.getContent().get(groupPosition).getGooddetail().get(i).getCount())
                                        * Integer.valueOf(goodBean.getContent().get(groupPosition).getGooddetail().get(i).getPrice());
                            }
                        }
                    } else {
                        allCount -= childSize;
                        for (int i = 0; i < childSize; i++) {
                            goodBean.getContent().get(groupPosition).getGooddetail().get(i).setIsselected(!checkBox.isChecked());
                            allMoney -= Integer.valueOf(goodBean.getContent().get(groupPosition).getGooddetail().get(i).getCount())
                                    * Integer.valueOf(goodBean.getContent().get(groupPosition).getGooddetail().get(i).getPrice());
                        }
                    }
                    int cm = 0;
                    //判断是否所有的父item都被选中，决定全选按钮状态
                    for (int i = 0; i < goodBean.getContent().size(); i++) {
                        if (goodBean.getContent().get(i).isselected()) {
                            cm++;
                        }
                    }
                    if (cm == goodBean.getContent().size()) {
                        goodBean.setAllSelect(true);
                    } else {
                        goodBean.setAllSelect(false);
                    }
                    goodBean.setAllcount(allCount);
                    goodBean.setAllmoney(allMoney);
                    notifyDataSetChanged();
                    updateViewListener.update(goodBean.isAllSelect(), allCount, allMoney);
                    break;
                //单个子项item被点击
                case R.id.cb_Item:
                    checkBox = (SmoothCheckBox) v;
                    int n = 0;
                    int m = 0;
                    goodBean.getContent().get(groupId).getGooddetail().get(childId).setIsselected(!checkBox.isChecked());
                    //遍历父item所有数据，统计被选中的item数量
                    for (int i = 0; i < goodBean.getContent().get(groupId).getGooddetail().size(); i++) {
                        if (goodBean.getContent().get(groupId).getGooddetail().get(i).isselected()) {
                            n++;
                        }
                    }
                    //判断是否所有的子item都被选中，决定父item状态
                    if (n == goodBean.getContent().get(groupId).getGooddetail().size()) {
                        goodBean.getContent().get(groupId).setIsselected(true);
                    } else {
                        goodBean.getContent().get(groupId).setIsselected(false);
                    }
                    //判断是否所有的父item都被选中，决定全选按钮状态
                    for (int i = 0; i < goodBean.getContent().size(); i++) {
                        if (goodBean.getContent().get(i).isselected()) {
                            m++;
                        }
                    }
                    if (m == goodBean.getContent().size()) {
                        goodBean.setAllSelect(true);
                    } else {
                        goodBean.setAllSelect(false);
                    }
                    //判断子item状态，更新结算总商品数和合计Money
                    if (!checkBox.isChecked()) {
                        allCount++;
                        allMoney += Integer.valueOf(goodBean.getContent().get(groupId).getGooddetail().get(childId).getCount())
                                * Integer.valueOf(goodBean.getContent().get(groupId).getGooddetail().get(childId).getPrice());
                    } else {
                        allCount--;
                        allMoney -= Integer.valueOf(goodBean.getContent().get(groupId).getGooddetail().get(childId).getCount())
                                * Integer.valueOf(goodBean.getContent().get(groupId).getGooddetail().get(childId).getPrice());
                    }
                    goodBean.setAllcount(allCount);
                    goodBean.setAllmoney(allMoney);
                    notifyDataSetChanged();
                    updateViewListener.update(goodBean.isAllSelect(), allCount, allMoney);
                    break;
                case R.id.tv_Reduce:
                    //减少商品数量
                    String var1 = goodBean.getContent().get(groupId).getGooddetail().get(childId).getCount();
                    if (Integer.valueOf(var1) > 1) {
                        goodBean.getContent().get(groupId).getGooddetail().get(childId).setCount(reduceCount(var1));
                        if (goodBean.getContent().get(groupId).getGooddetail().get(childId).isselected()) {
                            allMoney -= Integer.valueOf(goodBean.getContent().get(groupId).getGooddetail().get(childId).getPrice());
                            updateViewListener.update(goodBean.isAllSelect(), allCount, allMoney);
                        }
                        goodBean.setAllmoney(allMoney);
                        notifyDataSetChanged();
                    }
                    break;
               /* case R.id.tv_Add:
                    //添加商品数量
                    String var2 = goodBean.getContent().get(groupId).getGooddetail().get(childId).getCount();
                    goodBean.getContent().get(groupId).getGooddetail().get(childId).setCount(addCount(var2));
                    allMoney = goodBean.getAllmoney();
                    if (goodBean.getContent().get(groupId).getGooddetail().get(childId).isselected()) {
                        allMoney += Integer.valueOf(goodBean.getContent().get(groupId).getGooddetail().get(childId).getPrice());
                        updateViewListener.update(goodBean.isAllSelect(), allCount, allMoney);
                    }
                    goodBean.setAllmoney(allMoney);
                    notifyDataSetChanged();
                    break;*/
                case R.id.iv_Delete:
                    Toast.makeText(context, "删除", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    public void setChangedListener(UpdateView listener) {
        if (updateViewListener == null) {
            this.updateViewListener = listener;
        }
    }

    private String addCount(String var2) {
        Integer inte = Integer.valueOf(var2);
        inte++;
        return inte + "";
    }

    private String reduceCount(String var) {
        Integer integer = Integer.valueOf(var);
        if (integer > 1) {
            integer--;
        }
        return integer + "";
    }


    static class GroupViewHolder {
        SmoothCheckBox cbGroupItem;
        TextView tvPosition;

        GroupViewHolder(View view) {
            cbGroupItem = (SmoothCheckBox) view.findViewById(R.id.cb_GroupItem);
            tvPosition = (TextView) view.findViewById(R.id.tv_Position);
        }
    }

    static class ChildViewHolder {
        SmoothCheckBox cbItem;
        TextView tvPrice;
        EditText etCount;
        TextView tvReduce;
        TextView tvAdd;
        ImageView ivDelete;
        ImageView ivIcon;

        ChildViewHolder(View view) {
            cbItem = (SmoothCheckBox) view.findViewById(R.id.cb_Item);
            tvPrice = (TextView) view.findViewById(R.id.tv_Price);
            etCount = (EditText) view.findViewById(R.id.et_Count);
            tvReduce = (TextView) view.findViewById(R.id.tv_Reduce);
            tvAdd = (TextView) view.findViewById(R.id.tv_Add);
            ivDelete = (ImageView) view.findViewById(R.id.iv_Delete);
            ivIcon = (ImageView) view.findViewById(R.id.iv_icon);
        }
    }
}
