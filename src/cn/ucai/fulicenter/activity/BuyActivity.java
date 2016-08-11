package cn.ucai.fulicenter.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.pingplusplus.android.PingppLog;
import com.pingplusplus.libone.PaymentHandler;
import com.pingplusplus.libone.PingppOne;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cn.ucai.fulicenter.FuLiCenterApplication;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.bean.CartBean;
import cn.ucai.fulicenter.view.DisplayUtils;

public class BuyActivity extends BaseActivity implements PaymentHandler {
    EditText mRecipient,mOrderPhone,mOrderStreet;
    Spinner mSpArea;
    Button mToBuy;

    private static String URL = "http://218.244.151.190/demo/charge";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy);
        initView();
        setListener();
        //设置需要使用的支付方式
        PingppOne.enableChannels(new String[]{"wx", "alipay", "upacp", "bfb", "jdpay_wap"});

        // 提交数据的格式，默认格式为json
        // PingppOne.CONTENT_TYPE = "application/x-www-form-urlencoded";
        PingppOne.CONTENT_TYPE = "application/json";

        PingppLog.DEBUG = true;
    }

    private void setListener() {
        mToBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String receiveName = mRecipient.getText().toString();
                if (TextUtils.isEmpty(receiveName)) {
                    mRecipient.setError("收货人姓名不等为空！！");
                    mRecipient.requestFocus();
                    return;
                }
                String mobile = mOrderPhone.getText().toString();
                if (TextUtils.isEmpty(mobile)) {
                    mOrderPhone.setError("手机号码不能为空！！");
                    mOrderPhone.requestFocus();
                    return;
                }
                if (!mobile.matches("[\\d]{11}")) {
                    mOrderPhone.setError("手机号码格式错误！！");
                    mOrderPhone.requestFocus();
                    return;
                }
                String area = mSpArea.getSelectedItem().toString();
                if (TextUtils.isEmpty(area)) {
                    Toast.makeText(BuyActivity.this, "收货的区不能为空！！", Toast.LENGTH_SHORT).show();
                    return;
                }
                String address = mOrderStreet.getText().toString();
                if (TextUtils.isEmpty(address)) {
                    mOrderStreet.setError("街道地址不能为空！！");
                    mOrderStreet.requestFocus();
                    return;
                }
                // 产生个订单号
                String orderNo = new SimpleDateFormat("yyyyMMddhhmmss")
                        .format(new Date());

                // 计算总金额（以分为单位）
                int amount = 0;
                JSONArray billList = new JSONArray();
                List<CartBean> cartBeanList = FuLiCenterApplication.getInstance().getCartBeanList();
                for (CartBean cart : cartBeanList) {
                    if (cart.isChecked()) {
                        amount = convertPrice(cart.getGoods().getCurrencyPrice())*cart.getCount();
                        billList.put(cart.getGoods().getGoodsName() + " x " + cart.getCount());
                    }
                }

                // 构建账单json对象
                JSONObject bill = new JSONObject();

                // 自定义的额外信息 选填
                JSONObject extras = new JSONObject();
                try {
                    extras.put("extra1", "extra1");
                    extras.put("extra2", "extra2");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    bill.put("order_no", orderNo);
                    bill.put("amount", amount);
                    bill.put("extras", extras);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //壹收款: 创建支付通道的对话框
                PingppOne.showPaymentChannels(getSupportFragmentManager(), bill.toString(), URL, BuyActivity.this);


            }
        });
    }
    private int convertPrice(String price) {
        price = price.substring(1);
        return Integer.valueOf(price);
    }
    private void initView() {
        DisplayUtils.initBackWithTitle(BuyActivity.this,"填写收货地址：");
        mRecipient = (EditText) findViewById(R.id.ed_order_recipient);
        mOrderPhone = (EditText) findViewById(R.id.ed_order_phone);
        mOrderStreet = (EditText) findViewById(R.id.ed_order_street);
        mSpArea = (Spinner) findViewById(R.id.sp_order_area);
        mToBuy = (Button) findViewById(R.id.btn_order_buy);
    }

    @Override
    public void handlePaymentResult(Intent data) {
        if (data != null) {

            // result：支付结果信息
            // code：支付结果码
            //-2:用户自定义错误
            //-1：失败
            // 0：取消
            // 1：成功
            // 2:应用内快捷支付支付结果

            if (data.getExtras().getInt("code") != 2) {
                PingppLog.d(data.getExtras().getString("result") + "  " + data.getExtras().getInt("code"));
            } else {
                String result = data.getStringExtra("result");
                try {
                    JSONObject resultJson = new JSONObject(result);
                    if (resultJson.has("error")) {
                        result = resultJson.optJSONObject("error").toString();
                    } else if (resultJson.has("success")) {
                        result = resultJson.optJSONObject("success").toString();
                    }
                    PingppLog.d("result::" + result);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
