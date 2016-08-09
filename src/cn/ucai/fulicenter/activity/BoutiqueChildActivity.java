package cn.ucai.fulicenter.activity;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.ucai.fulicenter.D;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.adapter.GoodsAdapter;
import cn.ucai.fulicenter.bean.NewGoodBean;
import cn.ucai.fulicenter.data.OkHttpUtils2;
import cn.ucai.fulicenter.utils.I;
import cn.ucai.fulicenter.utils.Utils;
import cn.ucai.fulicenter.view.DisplayUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class BoutiqueChildActivity extends BaseActivity {
    BoutiqueChildActivity mContext;
    SwipeRefreshLayout mSwipeRefreshLayout;
    RecyclerView mRecyclerView;
    List<NewGoodBean> mGoodList;
    GridLayoutManager mGridLayoutManager;
    GoodsAdapter mGoodsAdapter;

    int catId;
    int pageId = 1;
    int action = I.ACTION_DOWNLOAD;
    TextView mtvRefresh;
    public BoutiqueChildActivity() {
    }

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        mContext = this;
        setContentView(R.layout.activity_boutique_child);
        mGoodList = new ArrayList<NewGoodBean>();
        initView();
        initData();
        setListener();
    }



    private void setListener() {
        setPullDownRefreshListener();
        setPullUpRefreshListener();
    }

    private void setPullUpRefreshListener() {
        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            int lastItemPosition;
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                int a = RecyclerView.SCROLL_STATE_DRAGGING;//1
                int b = RecyclerView.SCROLL_STATE_IDLE;//0
                int c = RecyclerView.SCROLL_STATE_SETTLING;//2
                if (newState == RecyclerView.SCROLL_STATE_IDLE
                        && lastItemPosition == mGoodsAdapter.getItemCount() - 1) {
                    if (mGoodsAdapter.isMore()) {
                        action = I.ACTION_PULL_UP;
                        pageId ++;
                        initData();
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int f = mGridLayoutManager.findFirstVisibleItemPosition();
                int l = mGridLayoutManager.findLastVisibleItemPosition();
                lastItemPosition = mGridLayoutManager.findLastVisibleItemPosition();
                mSwipeRefreshLayout.setEnabled(mGridLayoutManager.findFirstVisibleItemPosition()==0);
                if (f == -1 || l == -1) {
                    lastItemPosition = mGoodsAdapter.getItemCount()-1;
                }
            }
        });
    }

    private void setPullDownRefreshListener() {
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                action = I.ACTION_PULL_DOWN;
                pageId = 1;
                mtvRefresh.setVisibility(View.VISIBLE);
                mSwipeRefreshLayout.setEnabled(true);
                mSwipeRefreshLayout.setRefreshing(true);
                initData();

            }
        });
    }

    private void initData() {
        catId = getIntent().getIntExtra(D.Boutique.KEY_GOODS_ID, 0);
        Log.i("main", "在BoutiqueChildActivity里面得到的catId：" + catId);
        if (catId > 0) {
            findNewGoodsList(new OkHttpUtils2.OnCompleteListener<NewGoodBean[]>() {
                @Override
                public void onSuccess(NewGoodBean[] result) {
                    mtvRefresh.setVisibility(View.GONE);
                    mSwipeRefreshLayout.setRefreshing(false);
                    mGoodsAdapter.setMore(true);
                    mGoodsAdapter.setFooterString(getResources().getString(R.string.load_more));
                    for (int i =0; i<result.length;i++) {
                        Log.i("main", "在BoutiqueChildActivity下载商品时返回的结果：" + result[0].toString());
                    }
                    if (result != null) {
                        Log.i("main", "在BoutiqueChildActivity里面result的长度：" + result.length);
                        ArrayList<NewGoodBean> newGoodBeanArrayList = Utils.array2List(result);
                        if (action == I.ACTION_DOWNLOAD || action == I.ACTION_PULL_DOWN) {
                            mGoodsAdapter.initItem(newGoodBeanArrayList);
                        } else {
                            mGoodsAdapter.addMoreItem(newGoodBeanArrayList);
                        }
                        if (newGoodBeanArrayList.size() < I.PAGE_SIZE_DEFAULT) {
                            mGoodsAdapter.setMore(false);
                            mGoodsAdapter.setFooterString(getResources().getString(R.string.no_more_load));
                        }
                    } else {
                        mGoodsAdapter.setMore(false);
                        mGoodsAdapter.setFooterString(getResources().getString(R.string.no_more_load));
                    }
                }

                @Override
                public void onError(String error) {
                    Log.i("main", "在BoutiqueChildActivity下载商息时返回的错误信息：" + error);
                    Toast.makeText(BoutiqueChildActivity.this, "在BoutiqueChildActivity里面获取商品信息失败", Toast.LENGTH_SHORT).show();
                    mtvRefresh.setVisibility(View.GONE);
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            });
        } else {
            finish();
            Toast.makeText(BoutiqueChildActivity.this, "在BoutiqueChildActivity里面获取商品信息失败", Toast.LENGTH_SHORT).show();
        }


    }
    private  void findNewGoodsList(OkHttpUtils2.OnCompleteListener<NewGoodBean[]> listener ) {
        OkHttpUtils2<NewGoodBean[]> utils = new OkHttpUtils2<NewGoodBean[]>();
        utils.setRequestUrl(I.REQUEST_FIND_NEW_BOUTIQUE_GOODS)
                .addParam(I.NewAndBoutiqueGood.CAT_ID,String.valueOf(catId))
                .addParam(I.PAGE_ID,String.valueOf(pageId))
                .addParam(I.PAGE_SIZE,String.valueOf(I.PAGE_SIZE_DEFAULT))
                .targetClass(NewGoodBean[].class)
                .execute(listener);
    }

    private void initView() {
        String name = getIntent().getStringExtra(D.Boutique.KEY_NAME);
        DisplayUtils.initBackWithTitle(mContext,name);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.srl_boutique_child);
        mSwipeRefreshLayout.setColorSchemeColors(
                R.color.google_blue,
                R.color.google_green,
                R.color.google_red,
                R.color.google_yellow
        );
        mRecyclerView = (RecyclerView) findViewById(R.id.recycle_boutique_child);
        mGridLayoutManager = new GridLayoutManager(mContext, I.COLUM_NUM);
        mGridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        mGoodsAdapter = new GoodsAdapter(mContext, mGoodList);
        mRecyclerView.setAdapter(mGoodsAdapter);

        mtvRefresh = (TextView) findViewById(R.id.tv_refresh_hint);

    }

}
