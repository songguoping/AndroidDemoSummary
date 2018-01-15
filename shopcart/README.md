# ShopCart 案例

## 截图

![购物车](screenshots/shopcart.png)

## 功能与特性

 * 两级菜单显示，使用 [BaseRecyclerViewAdapterHelper](https://github.com/CymChad/BaseRecyclerViewAdapterHelper) 开源库
 * recycleview 中嵌套 Checkbox 的选择问题，一般在 bean 类添加一个 boolean 类型的字段辅助判断
 * recycleview 中嵌套 EditText 的复用与光标问题
    * recycleview 上下滑动的时候 EditText 内容会混乱，设置Tag ,通过 Tag 保存 EditTextWatcher；监听 recycleview 滑动
    * recycleview 添加 android:descendantFocusability="beforeDescendants"；activity 添加 android:windowSoftInputMode="stateHidden|adjustPan"
 * 贝塞尔曲线实现商品动画

