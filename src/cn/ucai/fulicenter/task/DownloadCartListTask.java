package cn.ucai.fulicenter.task;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import cn.ucai.fulicenter.D;
import cn.ucai.fulicenter.FuLiCenterApplication;
import cn.ucai.fulicenter.bean.CartBean;
import cn.ucai.fulicenter.bean.GoodDetailsBean;
import cn.ucai.fulicenter.data.OkHttpUtils2;
import cn.ucai.fulicenter.utils.I;
import cn.ucai.fulicenter.utils.Utils;

/**
 * Created by Administrator on 2016/7/20.
 */
public class DownloadCartListTask {
    private final  static  String TAG = DownloadCartListTask.class.getSimpleName();
    String userName;
    Context context;

    public DownloadCartListTask(Context context, String userName) {
        this.context = context;
        this.userName = userName;
    }

    public void execute() {
        final OkHttpUtils2<CartBean[]> utils = new OkHttpUtils2<CartBean[]>();
        utils.setRequestUrl(I.REQUEST_FIND_CARTS)
                .addParam(I.Cart.USER_NAME,userName)
                .addParam(I.PAGE_ID,String.valueOf(I.PAGE_ID_DEFAULT))
                .addParam(I.PAGE_SIZE,String.valueOf(I.PAGE_SIZE_DEFAULT))
                .targetClass(CartBean[].class)
                .execute(new OkHttpUtils2.OnCompleteListener<CartBean[]>() {
                    @Override
                    public void onSuccess(CartBean[] result) {
                        for (int i= 0;i<result.length;i++) {
                            Log.i("main", "在DownloadCartListTask下载购物车得到的返回结果：" + result[i].toString());
                        }
                        if (result != null ) {
                            ArrayList<CartBean> cartBeen = Utils.array2List(result);
                            List<CartBean> cartBeanList = FuLiCenterApplication.getInstance().getCartBeanList();
                            for (final CartBean cart : cartBeen) {
                                if (!(cartBeanList.contains(cart))) {
                                    OkHttpUtils2<GoodDetailsBean> utils = new OkHttpUtils2<GoodDetailsBean>();
                                    utils.setRequestUrl(I.REQUEST_FIND_GOOD_DETAILS)
                                            .addParam(D.GoodDetails.KEY_GOODS_ID,String.valueOf(cart.getGoodsId()))
                                            .targetClass(GoodDetailsBean.class)
                                            .execute(new OkHttpUtils2.OnCompleteListener<GoodDetailsBean>() {
                                                @Override
                                                public void onSuccess(GoodDetailsBean result) {
                                                    cart.setGoods(result);
                                                    context.sendStickyBroadcast(new Intent("update_cart_list"));
                                                }

                                                @Override
                                                public void onError(String error) {

                                                }
                                            });
                                    cartBeanList.add(cart);
                                    FuLiCenterApplication.getInstance().setCartBeanList(cartBeanList);

                                } else {
                                    cartBeanList.get(cartBeanList.indexOf(cart)).setChecked(cart.isChecked());
                                    cartBeanList.get(cartBeanList.indexOf(cart)).setCount(cart.getCount());

                                }
                            }
                            context.sendStickyBroadcast(new Intent("update_cart_list"));
                        }
                    }
                    @Override
                    public void onError(String error) {

                    }
                });
    }

    private void getGoodDetailsByGoodId(OkHttpUtils2.OnCompleteListener<GoodDetailsBean> listener,String goodId) {
        OkHttpUtils2<GoodDetailsBean> utils = new OkHttpUtils2<GoodDetailsBean>();
        utils.setRequestUrl(I.REQUEST_FIND_GOOD_DETAILS)
                .addParam(D.GoodDetails.KEY_GOODS_ID,String.valueOf(goodId))
                .targetClass(GoodDetailsBean.class)
                .execute(listener);
    }




}
