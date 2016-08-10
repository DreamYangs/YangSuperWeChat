package cn.ucai.fulicenter.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.ucai.fulicenter.D;
import cn.ucai.fulicenter.DemoHXSDKHelper;
import cn.ucai.fulicenter.FuLiCenterApplication;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.bean.AlbumBean;
import cn.ucai.fulicenter.bean.CartBean;
import cn.ucai.fulicenter.bean.GoodDetailsBean;
import cn.ucai.fulicenter.bean.MessageBean;
import cn.ucai.fulicenter.data.OkHttpUtils2;
import cn.ucai.fulicenter.task.DownloadCollectCountTask;
import cn.ucai.fulicenter.task.UpdateCartTask;
import cn.ucai.fulicenter.utils.I;
import cn.ucai.fulicenter.utils.Utils;
import cn.ucai.fulicenter.view.DisplayUtils;
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
    LinearLayout mBackLinearLayout;
    int mGoodId;
    GoodDetailsBean mGoodDetails;

    boolean isCollect;
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_good_details);
        initView();
        initData();
        setListener();
    }

    private void setListener() {
        MyOnClickListener listener = new MyOnClickListener();
        mivCollect.setOnClickListener(listener);
        mivShare.setOnClickListener(listener);
        mivCart.setOnClickListener(listener);
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
                        mGoodDetails = result;
                        showGoodDetails();
                    }
                }

                @Override
                public void onError(String error) {
                    Log.i("main", "在GoodDetailsActivity里面获取商品详情失败信息：" + error);
                    finish();
                    Toast.makeText(GoodDetailsActivity.this, "在GoodDetailsActivity里获取商品详情失败", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            finish();
            Toast.makeText(GoodDetailsActivity.this, "在GoodDetailsActivity里获取商品详情失败", Toast.LENGTH_SHORT).show();
        }
    }

    private void showGoodDetails() {
        mtvGoodEnglishName.setText(mGoodDetails.getGoodsEnglishName());
        mtvGoodName.setText(mGoodDetails.getGoodsName());
        mtvGoodPriceCurrent.setText(mGoodDetails.getCurrencyPrice());
        mtvGoodPriceShop.setText(mGoodDetails.getShopPrice());
        mSlideAutoLoopView.startPlayLoop(mFlowIndicator,
                getAlbumImageUrl(), getAlbumImageSize());
        mWebView.loadDataWithBaseURL(null,mGoodDetails.getGoodsBrief(),D.TEXT_HTML,D.UTF_8,null);
    }

    private String[] getAlbumImageUrl() {
        String[] albumImageUrl = new String[]{};
        if (mGoodDetails.getProperties() != null && mGoodDetails.getProperties().length > 0) {
           AlbumBean[] albums  = mGoodDetails.getProperties()[0].getAlbums();
            albumImageUrl =  new String[albums.length];
            for (int i =0; i< albumImageUrl.length; i++) {
                albumImageUrl[i] = albums[i].getImgUrl();
            }
        }
        return albumImageUrl;

    }

    private int getAlbumImageSize() {
        if (mGoodDetails.getProperties() != null && mGoodDetails.getProperties().length > 0) {
            return mGoodDetails.getProperties()[0].getAlbums().length;
        }
        return 0;

    }

    private void getGoodDetailsByGoodId(OkHttpUtils2.OnCompleteListener<GoodDetailsBean> listener) {
        OkHttpUtils2<GoodDetailsBean> utils = new OkHttpUtils2<GoodDetailsBean>();
        utils.setRequestUrl(I.REQUEST_FIND_GOOD_DETAILS)
                .addParam(D.GoodDetails.KEY_GOODS_ID,String.valueOf(mGoodId))
                .targetClass(GoodDetailsBean.class)
                .execute(listener);
    }

    private void initView() {
        DisplayUtils.initBack(this);
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
        mBackLinearLayout = (LinearLayout) findViewById(R.id.backClickArea);

    }

    @Override
    protected void onResume() {
        super.onResume();
        initCollectStatus();
        setUpdateCartNumListener();
    }

    private void initCollectStatus() {
        if (DemoHXSDKHelper.getInstance().isLogined()) {
            String userName = FuLiCenterApplication.getInstance().getUserName();
            OkHttpUtils2<MessageBean> utils = new OkHttpUtils2<MessageBean>();
            utils.setRequestUrl(I.REQUEST_IS_COLLECT)
                    .addParam(I.Collect.USER_NAME,userName)
                    .addParam(I.Collect.GOODS_ID,String.valueOf(mGoodId))
                    .targetClass(MessageBean.class)
                    .execute(new OkHttpUtils2.OnCompleteListener<MessageBean>() {
                        @Override
                        public void onSuccess(MessageBean result) {
                            if (result != null && result.isSuccess()) {
                                isCollect = true;
                                updateCollectStatus();
                            } else {
                                isCollect = false;
                                updateCollectStatus();
                            }
                        }

                        @Override
                        public void onError(String error) {

                        }
                    });
        }
    }

    class MyOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.iv_good_collect:
                    goodCollect();
                    break;
                case R.id.iv_good_share:
                    showShare();
                    break;
                case R.id.iv_good_cart:
                    addCart();
                    break;
            }
        }
    }

    private void addCart() {
        boolean isExits = true;
        List<CartBean> cartBeanList = FuLiCenterApplication.getInstance().getCartBeanList();
        for (CartBean cart : cartBeanList) {
            if (cart.getGoods().getGoodsId() == mGoodId) {
                Log.i("main", "isExits:1" + isExits);
                isExits = false;
                cart.setCount(cart.getCount()+1);
                new UpdateCartTask(GoodDetailsActivity.this,cart).execute();
            }
        }
        if (isExits) {
            Log.i("main", "isExits:2" + isExits);
            CartBean cartBean = new CartBean();
            cartBean.setChecked(true);
            cartBean.setCount(1);
            cartBean.setGoods(mGoodDetails);
            cartBean.setGoodsId(mGoodId);
            cartBean.setUserName(FuLiCenterApplication.getInstance().getUserName());
            new UpdateCartTask(GoodDetailsActivity.this,cartBean ).execute();
        }

    }

    //取消或者添加收藏。
    private void goodCollect() {
        if (DemoHXSDKHelper.getInstance().isLogined()) {
            if (isCollect) {
                //取消收藏
                OkHttpUtils2<MessageBean> utils = new OkHttpUtils2<MessageBean>();
                utils.setRequestUrl(I.REQUEST_DELETE_COLLECT)
                        .addParam(I.Collect.USER_NAME, FuLiCenterApplication.getInstance().getUserName())
                        .addParam(I.Collect.GOODS_ID,String.valueOf(mGoodId))
                        .targetClass(MessageBean.class)
                        .execute(new OkHttpUtils2.OnCompleteListener<MessageBean>() {
                            @Override
                            public void onSuccess(MessageBean result) {
                                if (result != null && result.isSuccess()) {
                                    isCollect = false;
                                    new DownloadCollectCountTask(GoodDetailsActivity.this,
                                            FuLiCenterApplication.getInstance().getUserName()).execute();
                                }
                                updateCollectStatus();
                                Toast.makeText(GoodDetailsActivity.this, result.getMsg(), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onError(String error) {

                            }
                        });
            } else {
                //添加收藏
                OkHttpUtils2<MessageBean> utils = new OkHttpUtils2<MessageBean>();
                utils.setRequestUrl(I.REQUEST_ADD_COLLECT)
                        .addParam(I.Collect.USER_NAME, FuLiCenterApplication.getInstance().getUserName())
                        .addParam(I.Collect.GOODS_ID,String.valueOf(mGoodId))
                        .addParam(I.Collect.GOODS_NAME,mGoodDetails.getGoodsName())
                        .addParam(I.Collect.GOODS_ENGLISH_NAME,mGoodDetails.getGoodsEnglishName())
                        .addParam(I.Collect.GOODS_THUMB,mGoodDetails.getGoodsThumb())
                        .addParam(I.Collect.GOODS_IMG,mGoodDetails.getGoodsImg())
                        .addParam(I.Collect.ADD_TIME,String.valueOf(mGoodDetails.getAddTime()))
                        .targetClass(MessageBean.class)
                        .execute(new OkHttpUtils2.OnCompleteListener<MessageBean>() {
                            @Override
                            public void onSuccess(MessageBean result) {
                                if (result != null && result.isSuccess()) {
                                    isCollect = true;
                                    new DownloadCollectCountTask(GoodDetailsActivity.this,
                                            FuLiCenterApplication.getInstance().getUserName()).execute();
                                } else {
                                    isCollect = false;
                                }
                                updateCollectStatus();
                                Toast.makeText(GoodDetailsActivity.this, result.getMsg(), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onError(String error) {

                            }
                        });

            }
        } else {
            startActivity(new Intent(this,LoginActivity.class));
        }
    }

    private void updateCollectStatus() {
        if (isCollect) {
            mivCollect.setImageResource(R.drawable.bg_collect_out);
        } else {
            mivCollect.setImageResource(R.drawable.bg_collect_in);
        }
    }
    private void showShare() {
        ShareSDK.initSDK(this);
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

// 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
        //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle(getString(R.string.share));
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl(mGoodDetails.getShareUrl());
        // text是分享文本，所有平台都需要这个字段
        oks.setText("我是分享文本");
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        //oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl(mGoodDetails.getShareUrl());
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("我是测试评论文本");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl(mGoodDetails.getShareUrl());

        // 启动分享GUI
        oks.show(this);
    }

    class UpdateCartNumReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int count = Utils.sumCartCount();
            if (count == 0 || !DemoHXSDKHelper.getInstance().isLogined()) {
                mtvCartCount.setText(String.valueOf(count));
                mtvCartCount.setVisibility(View.GONE);
            } else {
                mtvCartCount.setText(String.valueOf(count));
                mtvCartCount.setVisibility(View.VISIBLE);
            }
        }
    }

    UpdateCartNumReceiver mUpdateCartNumReceiver;
    private void setUpdateCartNumListener() {
        mUpdateCartNumReceiver = new UpdateCartNumReceiver();
        IntentFilter filter = new IntentFilter("update_cart_list");
        registerReceiver(mUpdateCartNumReceiver,filter);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mUpdateCartNumReceiver != null) {
            unregisterReceiver(mUpdateCartNumReceiver);
        }
    }

}
