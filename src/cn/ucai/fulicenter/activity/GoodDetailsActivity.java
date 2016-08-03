package cn.ucai.fulicenter.activity;

import android.os.Bundle;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import cn.ucai.fulicenter.D;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.bean.GoodDetailsBean;
import cn.ucai.fulicenter.data.OkHttpUtils2;
import cn.ucai.fulicenter.utils.I;
import cn.ucai.fulicenter.view.FlowIndicator;
import cn.ucai.fulicenter.view.SlideAutoLoopView;

/**
 * Created by Administrator on 2016/8/3.
 */
public class GoodDetailsActivity extends BaseActivity {
    ImageView mivShare,mivCollect,mivCart;
    TextView mtvCartCount,mtvGoodEnglishName,mtvGoodName,mtvGoodPriceCurrent,mtvGoodPriceShop;

    SlideAutoLoopView mSlideAutoLoopView;
    FlowIndicator mFlowIndicator;
    WebView mWebView;

    int mGoodId;
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_good_details);
        initView();
        initData();
    }

    private void initData() {
        mGoodId = getIntent().getIntExtra(D.GoodDetails.KEY_GOODS_NAME, 0);
        Log.i("main", "在GoodDetailsActivity里面得到传来的goodId：" + mGoodId);
        if (mGoodId > 0) {
            getGoodDetailsByGoodId(new OkHttpUtils2.OnCompleteListener<GoodDetailsBean>() {
                @Override
                public void onSuccess(GoodDetailsBean result) {
//                    Log.i("main", "在GoodDetailsActivity里面获取商品详情的信息："+result.toString());
                    if (result != null) {
                        showGoodDetails(result);
                    }
                }

                @Override
                public void onError(String error) {
                    Log.i("main", "在GoodDetailsActivity里面获取商品详情失败信息：" + error);
                    finish();
                    Toast.makeText(GoodDetailsActivity.this, "获取商品详情失败", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            finish();
            Toast.makeText(GoodDetailsActivity.this, "获取商品详情失败", Toast.LENGTH_SHORT).show();
        }
    }

    private void showGoodDetails(GoodDetailsBean detail) {
        mtvGoodEnglishName.setText(detail.getGoodsEnglishName());
        mtvGoodName.setText(detail.getGoodsName());
        mtvGoodPriceCurrent.setText(detail.getCurrencyPrice());
        mtvGoodPriceShop.setText(detail.getShopPrice());
    }

    private void getGoodDetailsByGoodId(OkHttpUtils2.OnCompleteListener<GoodDetailsBean> listener) {
        OkHttpUtils2<GoodDetailsBean> utils = new OkHttpUtils2<GoodDetailsBean>();
        utils.setRequestUrl(I.REQUEST_FIND_GOOD_DETAILS)
                .addParam(D.GoodDetails.KEY_GOODS_ID,String.valueOf(mGoodId))
                .targetClass(GoodDetailsBean.class)
                .execute(listener);
    }

    private void initView() {
        mivShare = (ImageView) findViewById(R.id.iv_good_share);
        mivCollect = (ImageView) findViewById(R.id.iv_good_collect);
        mivCart = (ImageView) findViewById(R.id.iv_good_cart);
        mtvCartCount = (TextView) findViewById(R.id.tv_good_cart_count);
        mtvGoodEnglishName = (TextView) findViewById(R.id.tv_good_name_english);
        mtvGoodName = (TextView) findViewById(R.id.tv_good_name);
        mtvGoodPriceCurrent = (TextView) findViewById(R.id.tv_good_price_current);
        mtvGoodPriceShop = (TextView) findViewById(R.id.tv_good_price_shop);
        mSlideAutoLoopView = (SlideAutoLoopView) findViewById(R.id.salv);
        mFlowIndicator = (FlowIndicator) findViewById(R.id.indicator);
        mWebView = (WebView) findViewById(R.id.wv_good_brief);
        WebSettings settings = mWebView.getSettings();
        settings.setBuiltInZoomControls(true);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);

    }
}
