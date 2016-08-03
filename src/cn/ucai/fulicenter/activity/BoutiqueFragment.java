package cn.ucai.fulicenter.activity;


import android.content.Context;
import android.os.Bundle;
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
import cn.ucai.fulicenter.adapter.BoutiqueAdapter;
import cn.ucai.fulicenter.adapter.GoodsAdapter;
import cn.ucai.fulicenter.bean.BoutiqueBean;
import cn.ucai.fulicenter.bean.NewGoodBean;
import cn.ucai.fulicenter.data.OkHttpUtils2;
import cn.ucai.fulicenter.utils.I;
import cn.ucai.fulicenter.utils.Utils;

/**
 * A simple {@link Fragment} subclass.
 */
public class BoutiqueFragment extends Fragment {
    Context mContext;
    SwipeRefreshLayout mSwipeRefreshLayout;
    RecyclerView mRecyclerView;
    List<BoutiqueBean> mGoodList;
    LinearLayoutManager mLinearLayoutManager;
    BoutiqueAdapter mBoutiqueAdapter;

    int pageId = 1;
    int action = I.ACTION_DOWNLOAD;
    TextView mtvRefresh;
    public BoutiqueFragment() {
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContext = (FuLiCenterMainActivity)getContext();
        View view = inflater.inflate(R.layout.fragment_boutique, container, false);
        mGoodList = new ArrayList<BoutiqueBean>();
        initView(view);
        initData();
        setListener();
        return view;
    }

    private void setListener() {
        setPullDownRefreshListener();
//        setPullUpRefreshListener();
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
                        && lastItemPosition == mBoutiqueAdapter.getItemCount() - 1) {
                    if (mBoutiqueAdapter.isMore()) {
                        action = I.ACTION_PULL_UP;
                        pageId ++;
                        initData();
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int f = mLinearLayoutManager.findFirstVisibleItemPosition();
                int l = mLinearLayoutManager.findLastVisibleItemPosition();
                lastItemPosition = mLinearLayoutManager.findLastVisibleItemPosition();
                mSwipeRefreshLayout.setEnabled(mLinearLayoutManager.findFirstVisibleItemPosition()==0);
                if (f == -1 || l == -1) {
                    lastItemPosition = mBoutiqueAdapter.getItemCount()-1;
                }
            }
        });
    }

    private void setPullDownRefreshListener() {
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                action = I.ACTION_PULL_DOWN;
//                pageId = 1;
                mtvRefresh.setVisibility(View.VISIBLE);
                mSwipeRefreshLayout.setEnabled(true);
                mSwipeRefreshLayout.setRefreshing(true);
                initData();

            }
        });
    }

    private void initData() {
        findBoutiqueList(new OkHttpUtils2.OnCompleteListener<BoutiqueBean[]>() {
            @Override
            public void onSuccess(BoutiqueBean[] result) {
                mtvRefresh.setVisibility(View.GONE);
                mSwipeRefreshLayout.setRefreshing(false);
                mBoutiqueAdapter.setMore(true);
//                mBoutiqueAdapter.setFooterString(getResources().getString(R.string.load_more));
                Log.i("main", "在BoutiqueFragment下载精品信息时返回的结果：" + result[0]);
                if (result != null) {
                    Log.i("main", "result的长度：" + result.length);
                    ArrayList<BoutiqueBean> boutiqueBeenArrayList = Utils.array2List(result);
                    if (action == I.ACTION_DOWNLOAD || action == I.ACTION_PULL_DOWN) {
                        mBoutiqueAdapter.initItem(boutiqueBeenArrayList);
                    } else {
                        mBoutiqueAdapter.addMoreItem(boutiqueBeenArrayList);
                    }
                    if (boutiqueBeenArrayList.size() == mBoutiqueAdapter.getItemCount()-1) {
                        mBoutiqueAdapter.setMore(false);
                        mBoutiqueAdapter.setFooterString(getResources().getString(R.string.no_more_load));
                    }
                } else {
                    mBoutiqueAdapter.setMore(false);
                    mBoutiqueAdapter.setFooterString(getResources().getString(R.string.no_more_load));
                }
            }

            @Override
            public void onError(String error) {
                Log.i("main", "在BoutiqueFragment下载精品信息时返回的错误信息：" + error);
                mtvRefresh.setVisibility(View.GONE);
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

    }

    private  void findBoutiqueList(OkHttpUtils2.OnCompleteListener<BoutiqueBean[]> listener ) {
        OkHttpUtils2<BoutiqueBean[]> utils = new OkHttpUtils2<BoutiqueBean[]>();
        utils.setRequestUrl(I.REQUEST_FIND_BOUTIQUES)
                .targetClass(BoutiqueBean[].class)
                .execute(listener);
    }
    private void initView(View view) {
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.srl_boutique);
        mSwipeRefreshLayout.setColorSchemeColors(
                R.color.google_blue,
                R.color.google_green,
                R.color.google_red,
                R.color.google_yellow
        );
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycle_boutique);
        mLinearLayoutManager = new LinearLayoutManager(mContext);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mBoutiqueAdapter = new BoutiqueAdapter(mContext, mGoodList);
        mRecyclerView.setAdapter(mBoutiqueAdapter);

        mtvRefresh = (TextView) view.findViewById(R.id.tv_refresh_hint);

    }

}
