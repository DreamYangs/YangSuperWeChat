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
import cn.ucai.fulicenter.bean.MessageBean;
import cn.ucai.fulicenter.data.OkHttpUtils2;
import cn.ucai.fulicenter.utils.I;
import cn.ucai.fulicenter.utils.Utils;

/**
 * Created by Administrator on 2016/7/20.
 */
public class UpdateCartTask {
    private final  static  String TAG = UpdateCartTask.class.getSimpleName();
    CartBean cartBean;
    Context context;

    public UpdateCartTask(Context context, CartBean cart) {
        this.context = context;
        this.cartBean = cart;
    }

    public void execute() {
        final List<CartBean> cartBeanList = FuLiCenterApplication.getInstance().getCartBeanList();
        if (cartBeanList.contains(cartBean)) {
            if (cartBean.getCount() > 0) {
                //更新购物车数据
                updateCart(new OkHttpUtils2.OnCompleteListener<MessageBean>() {
                    @Override
                    public void onSuccess(MessageBean result) {
                        cartBeanList.set(cartBeanList.indexOf(cartBean), cartBean);
                        context.sendStickyBroadcast(new Intent("update_cart_list"));
                    }

                    @Override
                    public void onError(String error) {

                    }
                });
            } else {
                //刪除购物车数据
            }

        } else {
            //新增购物车数据
        }
    }


    private void updateCart(OkHttpUtils2.OnCompleteListener<MessageBean> listener) {
        OkHttpUtils2<MessageBean> utils = new OkHttpUtils2<MessageBean>();
        utils.setRequestUrl(I.REQUEST_UPDATE_CART)
                .addParam(I.Cart.ID,String.valueOf(cartBean.getId()))
                .addParam(I.Cart.COUNT,String.valueOf(cartBean.getCount()))
                .addParam(I.Cart.IS_CHECKED,String.valueOf(cartBean.isChecked()))
                .targetClass(MessageBean.class)
                .execute(listener);

    }


}
