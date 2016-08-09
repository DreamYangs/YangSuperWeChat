package cn.ucai.fulicenter.activity;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.ucai.fulicenter.D;
import cn.ucai.fulicenter.FuLiCenterApplication;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.adapter.CollectAdapter;
import cn.ucai.fulicenter.adapter.GoodsAdapter;
import cn.ucai.fulicenter.bean.CollectBean;
import cn.ucai.fulicenter.bean.NewGoodBean;
import cn.ucai.fulicenter.data.OkHttpUtils2;
import cn.ucai.fulicenter.utils.I;
import cn.ucai.fulicenter.utils.Utils;
import cn.ucai.fulicenter.view.DisplayUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class CollectActivity extends BaseActivity {
    CollectActivity mContext;
    SwipeRefreshLayout mSwipeRefreshLayout;
    RecyclerView mRecyclerView;
    List<CollectBean> mCollectList;
    GridLayoutManager mGridLayoutManager;
    CollectAdapter mCollectAdapter;

    int catId;
    int pageId = 1;
    int action = I.ACTION_DOWNLOAD;
    TextView mtvRefresh;
    public CollectActivity() {
    }

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        mContext = this;
        setContentView(R.layout.activity_collect);
        mCollectList = new ArrayList<CollectBean>();
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
                        && lastItemPosition == mCollectAdapter.getItemCount() - 1) {
                    if (mCollectAdapter.isMore()) {
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
                    lastItemPosition = mCollectAdapter.getItemCount()-1;
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
        String userName = FuLiCenterApplication.getInstance().getUserName();

        if (!userName.isEmpty()) {
            findCollectList(new OkHttpUtils2.OnCompleteListener<CollectBean[]>() {
                @Override
                public void onSuccess(CollectBean[] result) {
                    mtvRefresh.setVisibility(View.GONE);
                    mSwipeRefreshLayout.setRefreshing(false);
                    mCollectAdapter.setMore(true);
                    mCollectAdapter.setFooterString(getResources().getString(R.string.load_more));
                    for (int i=0; i<result.length;i++) {
                        Log.i("main", "在CollectActivity下载商品时返回的结果：" + result[i].toString());
                    }
                    if (result != null) {
                        Log.i("main", "在CollectActivity中result的长度：" + result.length);
                        ArrayList<CollectBean> collectList = Utils.array2List(result);
                        if (action == I.ACTION_DOWNLOAD || action == I.ACTION_PULL_DOWN) {
                            mCollectAdapter.initItem(collectList);
                        } else {
                            mCollectAdapter.addMoreItem(collectList);
                        }
                        if (collectList.size() < I.PAGE_SIZE_DEFAULT) {
                            mCollectAdapter.setMore(false);
                            mCollectAdapter.setFooterString(getResources().getString(R.string.no_more_load));
                        }
                    } else {
                        mCollectAdapter.setMore(false);
                        mCollectAdapter.setFooterString(getResources().getString(R.string.no_more_load));
                    }
                }

                @Override
                public void onError(String error) {
                    Log.i("main", "在CollectActivity下载商息时返回的错误信息：" + error);
                    Toast.makeText(CollectActivity.this, "在CollectActivity获取商品信息失败", Toast.LENGTH_SHORT).show();
                    mtvRefresh.setVisibility(View.GONE);
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            });
        } else {
            finish();
            Toast.makeText(CollectActivity.this, "在CollectActivity获取商品信息失败", Toast.LENGTH_SHORT).show();
        }


    }
    private  void findCollectList(OkHttpUtils2.OnCompleteListener<CollectBean[]> listener ) {
        OkHttpUtils2<CollectBean[]> utils = new OkHttpUtils2<CollectBean[]>();
        utils.setRequestUrl(I.REQUEST_FIND_COLLECTS)
                .addParam(I.Collect.USER_NAME, FuLiCenterApplication.getInstance().getUserName())
                .addParam(I.PAGE_ID,String.valueOf(pageId))
                .addParam(I.PAGE_SIZE,String.valueOf(I.PAGE_SIZE_DEFAULT))
                .targetClass(CollectBean[].class)
                .execute(listener);
    }

    private void initView() {
        DisplayUtils.initBackWithTitle(mContext,"收藏的宝贝");
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.srl_collect);
        mSwipeRefreshLayout.setColorSchemeColors(
                R.color.google_blue,
                R.color.google_green,
                R.color.google_red,
                R.color.google_yellow
        );
        mRecyclerView = (RecyclerView) findViewById(R.id.recycle_collect);
        mGridLayoutManager = new GridLayoutManager(mContext, I.COLUM_NUM);
        mGridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        mCollectAdapter = new CollectAdapter(mContext, mCollectList);
        mRecyclerView.setAdapter(mCollectAdapter);

        mtvRefresh = (TextView) findViewById(R.id.tv_refresh_hint);

    }

}
