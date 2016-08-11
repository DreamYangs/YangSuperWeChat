package cn.ucai.fulicenter.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Spinner;

import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.view.DisplayUtils;

public class BuyActivity extends Activity {
    EditText mRecipient,mOrderPhone,mOrderStreet;
    Spinner mSpArea;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy);
        initView();
    }

    private void initView() {
        DisplayUtils.initBackWithTitle(BuyActivity.this,"填写收货地址：");
        mRecipient = (EditText) findViewById(R.id.ed_order_recipient);
        mOrderPhone = (EditText) findViewById(R.id.ed_order_phone);
        mOrderStreet = (EditText) findViewById(R.id.ed_order_street);
        mSpArea = (Spinner) findViewById(R.id.sp_order_area);
    }
}
