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

import java.util.ArrayList;
import java.util.List;

import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.adapter.GoodsAdapter;
import cn.ucai.fulicenter.bean.NewGoodBean;
import cn.ucai.fulicenter.data.OkHttpUtils2;
import cn.ucai.fulicenter.utils.I;
import cn.ucai.fulicenter.utils.Utils;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewGoodsFragment extends Fragment {
    Context mContext;
    SwipeRefreshLayout mSwipeRefreshLayout;
    RecyclerView mRecyclerView;
    List<NewGoodBean> mGoodList;
    GridLayoutManager mGridLayoutManager;
    GoodsAdapter mGoodsAdapter;

    int pageId = 1;
    int action = I.ACTION_DOWNLOAD;
    TextView mtvRefresh;
    public NewGoodsFragment() {
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mContext = (FuLiCenterMainActivity)getContext();
        View layout = inflater.inflate(R.layout.fragment_new_goods, container, false);
        mGoodList = new ArrayList<NewGoodBean>();
        initView(layout);
        initData();
        setListener();
        return layout;
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
        findNewGoodsList(new OkHttpUtils2.OnCompleteListener<NewGoodBean[]>() {
            @Override
            public void onSuccess(NewGoodBean[] result) {
                mtvRefresh.setVisibility(View.GONE);
                mSwipeRefreshLayout.setRefreshing(false);
                mGoodsAdapter.setMore(true);
                mGoodsAdapter.setFooterString(getResources().getString(R.string.load_more));
                Log.i("main", "在下载新品时返回的结果：" + result);
                if (result != null) {
                    Log.i("main", "result的长度：" + result.length);
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
                Log.i("main", "在在下载新品消息时返回的错误信息：" + error);
                mtvRefresh.setVisibility(View.GONE);
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

    }
    private  void findNewGoodsList(OkHttpUtils2.OnCompleteListener<NewGoodBean[]> listener ) {
        OkHttpUtils2<NewGoodBean[]> utils = new OkHttpUtils2<NewGoodBean[]>();
        utils.setRequestUrl(I.REQUEST_FIND_NEW_BOUTIQUE_GOODS)
                .addParam(I.NewAndBoutiqueGood.CAT_ID,String.valueOf(I.CAT_ID))
                .addParam(I.PAGE_ID,String.valueOf(pageId))
                .addParam(I.PAGE_SIZE,String.valueOf(I.PAGE_SIZE_DEFAULT))
                .targetClass(NewGoodBean[].class)
                .execute(listener);
    }

    private void initView(View layout) {
        mSwipeRefreshLayout = (SwipeRefreshLayout) layout.findViewById(R.id.srl_new_goods);
        mSwipeRefreshLayout.setColorSchemeColors(
                R.color.google_blue,
                R.color.google_green,
                R.color.google_red,
                R.color.google_yellow
        );
        mRecyclerView = (RecyclerView) layout.findViewById(R.id.recycle_new_goods);
        mGridLayoutManager = new GridLayoutManager(mContext, I.COLUM_NUM);
        mGridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        mGoodsAdapter = new GoodsAdapter(mContext, mGoodList);
        mRecyclerView.setAdapter(mGoodsAdapter);

        mtvRefresh = (TextView) layout.findViewById(R.id.tv_refresh_hint);

    }

}
