package cn.ucai.fulicenter.activity;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.ucai.fulicenter.FuLiCenterApplication;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.adapter.BoutiqueAdapter;
import cn.ucai.fulicenter.adapter.CartAdapter;
import cn.ucai.fulicenter.bean.BoutiqueBean;
import cn.ucai.fulicenter.bean.CartBean;
import cn.ucai.fulicenter.utils.I;
import cn.ucai.fulicenter.utils.Utils;

/**
 * A simple {@link Fragment} subclass.
 */
public class CartFragment extends Fragment {
    Context mContext;
    SwipeRefreshLayout mSwipeRefreshLayout;
    RecyclerView mRecyclerView;
    List<CartBean> mCartList;
    LinearLayoutManager mLinearLayoutManager;
    CartAdapter mCartAdapter;

    int pageId = 1;
    int action = I.ACTION_DOWNLOAD;
    TextView mtvRefresh;

    TextView mtvSumPrice,mtvSavePrice,mtvBuy;
    public CartFragment() {
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContext = (FuLiCenterMainActivity)getContext();
        View view = inflater.inflate(R.layout.fragment_cart, container, false);
        mCartList = new ArrayList<CartBean>();
        initView(view);
        initData();
        setListener();
        return view;
    }

    private void initData() {
        List<CartBean> cartBeanList = FuLiCenterApplication.getInstance().getCartBeanList();
        mCartList.clear();
        mCartList.addAll(cartBeanList);
        mtvRefresh.setVisibility(View.GONE);
        mSwipeRefreshLayout.setRefreshing(false);
        mCartAdapter.setMore(true);
        if (mCartList != null) {
            if (action == I.ACTION_DOWNLOAD || action == I.ACTION_PULL_DOWN) {
                mCartAdapter.initItem(mCartList);
            } else {
                mCartAdapter.addMoreItem(mCartList);
            }
            if (mCartList.size() < I.PAGE_SIZE_DEFAULT) {
                mCartAdapter.setMore(false);
            }
        } else {
            mCartAdapter.setMore(false);
        }

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
                        && lastItemPosition == mCartAdapter.getItemCount() - 1) {
                    if (mCartAdapter.isMore()) {
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
                    lastItemPosition = mCartAdapter.getItemCount()-1;
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

    private void initView(View view) {
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.srl_cart);
        mSwipeRefreshLayout.setColorSchemeColors(
                R.color.google_blue,
                R.color.google_green,
                R.color.google_red,
                R.color.google_yellow
        );
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycle_cart);
        mLinearLayoutManager = new LinearLayoutManager(mContext);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mCartAdapter = new CartAdapter(mContext, mCartList);
        mRecyclerView.setAdapter(mCartAdapter);

        mtvRefresh = (TextView) view.findViewById(R.id.tv_refresh_hint);

        mtvSumPrice = (TextView) view.findViewById(R.id.tv_cart_sum_price);
        mtvSavePrice = (TextView) view.findViewById(R.id.tv_cart_save_price);
        mtvBuy = (TextView) view.findViewById(R.id.tv_cart_buy);
    }

}
