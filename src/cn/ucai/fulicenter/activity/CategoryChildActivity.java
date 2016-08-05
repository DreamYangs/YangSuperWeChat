package cn.ucai.fulicenter.activity;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.ucai.fulicenter.D;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.adapter.GoodsAdapter;
import cn.ucai.fulicenter.bean.CategoryChildBean;
import cn.ucai.fulicenter.bean.NewGoodBean;
import cn.ucai.fulicenter.data.OkHttpUtils2;
import cn.ucai.fulicenter.utils.I;
import cn.ucai.fulicenter.utils.Utils;
import cn.ucai.fulicenter.view.CatChildFilterButton;
import cn.ucai.fulicenter.view.DisplayUtils;

public class CategoryChildActivity extends Activity {
    CategoryChildActivity mContext;
    SwipeRefreshLayout mSwipeRefreshLayout;
    RecyclerView mRecyclerView;
    List<NewGoodBean> mGoodList;
    GridLayoutManager mGridLayoutManager;
    GoodsAdapter mGoodsAdapter;

    Button mBtnSortPrice,mBtnSortAddTime;
    boolean mSortPriceAsc,mSortAddTimeAsc;
    int mSortBy;

    CatChildFilterButton mCatChildFilterButton;
    String mGroupName;
    ArrayList<CategoryChildBean> mChildList;

    int catId;
    int pageId = 1;
    int action = I.ACTION_DOWNLOAD;
    TextView mtvRefresh;
    public CategoryChildActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_category_child);
        mGoodList = new ArrayList<NewGoodBean>();
        mSortBy = I.SORT_BY_ADDTIME_ASC;
        initView();
        initData();
        setListener();
    }



    private void setListener() {
        setPullDownRefreshListener();
        setPullUpRefreshListener();
        SortStatusChangedListener listener = new SortStatusChangedListener();
        mBtnSortPrice.setOnClickListener(listener);
        mBtnSortAddTime.setOnClickListener(listener);
        mCatChildFilterButton.setOnCatFilterClickListener(mGroupName,mChildList);

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
        catId = getIntent().getIntExtra(I.CategoryChild.CAT_ID, 0);
        mGroupName = getIntent().getStringExtra(I.CategoryGroup.NAME);
        mCatChildFilterButton.setText(mGroupName);
        mChildList = (ArrayList<CategoryChildBean>) getIntent().getSerializableExtra("childList");
        Log.i("main", "在CategoryChildActivity里面得到的catId：" + catId);
        if (catId > 0) {
            findNewGoodsList(new OkHttpUtils2.OnCompleteListener<NewGoodBean[]>() {
                @Override
                public void onSuccess(NewGoodBean[] result) {
                    mtvRefresh.setVisibility(View.GONE);
                    mSwipeRefreshLayout.setRefreshing(false);
                    mGoodsAdapter.setMore(true);
                    mGoodsAdapter.setFooterString(getResources().getString(R.string.load_more));
                    for (int i =0; i<result.length;i++) {
                        Log.i("main", "在CategoryChildActivity下载商品时返回的结果：" + result[i].toString());
                    }
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
                    Log.i("main", "在CategoryChildActivity下载商息时返回的错误信息：" + error);
                    Toast.makeText(CategoryChildActivity.this, "在CategoryChildActivity获取商品信息失败"
                            , Toast.LENGTH_SHORT).show();
                    mtvRefresh.setVisibility(View.GONE);
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            });
        } else {
            finish();
            Toast.makeText(CategoryChildActivity.this, "在CategoryChildActivity获取商品信息失败"
                    , Toast.LENGTH_SHORT).show();
        }


    }
    private  void findNewGoodsList(OkHttpUtils2.OnCompleteListener<NewGoodBean[]> listener ) {
        OkHttpUtils2<NewGoodBean[]> utils = new OkHttpUtils2<NewGoodBean[]>();
        utils.setRequestUrl(I.REQUEST_FIND_GOODS_DETAILS)
                .addParam(I.NewAndBoutiqueGood.CAT_ID,String.valueOf(catId))
                .addParam(I.PAGE_ID,String.valueOf(pageId))
                .addParam(I.PAGE_SIZE,String.valueOf(I.PAGE_SIZE_DEFAULT))
                .targetClass(NewGoodBean[].class)
                .execute(listener);
    }

    private void initView() {
        String name = getIntent().getStringExtra(D.CategoryChild.NAME);
        DisplayUtils.initBackWithTitle(mContext,name);
//        DisplayUtils.initBack(mContext);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.srl_category_child);
        mSwipeRefreshLayout.setColorSchemeColors(
                R.color.google_blue,
                R.color.google_green,
                R.color.google_red,
                R.color.google_yellow
        );
        mRecyclerView = (RecyclerView) findViewById(R.id.recycle_category_child);
        mGridLayoutManager = new GridLayoutManager(mContext, I.COLUM_NUM);
        mGridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        mGoodsAdapter = new GoodsAdapter(mContext, mGoodList);
        mRecyclerView.setAdapter(mGoodsAdapter);

        mtvRefresh = (TextView) findViewById(R.id.tv_refresh_hint);

        mBtnSortPrice = (Button) findViewById(R.id.btn_sort_price);
        mBtnSortAddTime = (Button) findViewById(R.id.btn_sort_add_time);

        mCatChildFilterButton = (CatChildFilterButton) findViewById(R.id.btnCatChildFilter);

    }

    class SortStatusChangedListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            Drawable right;
            switch (view.getId()) {
                case R.id.btn_sort_price:
                    if (mSortPriceAsc) {
                        mSortBy = I.SORT_BY_PRICE_ASC;
                        right = getResources().getDrawable(R.drawable.arrow_order_up);
                    } else {
                        mSortBy = I.SORT_BY_PRICE_DESC;
                        right = getResources().getDrawable(R.drawable.arrow_order_down);
                    }
                    mSortPriceAsc = !mSortPriceAsc;
                    right.setBounds(0,0,right.getIntrinsicWidth(),right.getIntrinsicHeight());
                    mBtnSortPrice.setCompoundDrawablesRelativeWithIntrinsicBounds(null,null,right,null);
                    break;
                case R.id.btn_sort_add_time:
                    if (mSortAddTimeAsc) {
                        mSortBy = I.SORT_BY_ADDTIME_ASC;
                        right = getResources().getDrawable(R.drawable.arrow_order_up);
                    } else {
                        mSortBy = I.SORT_BY_ADDTIME_DESC;
                        right = getResources().getDrawable(R.drawable.arrow_order_down);
                    }
                    mSortAddTimeAsc = !mSortAddTimeAsc;
                    right.setBounds(0,0,right.getIntrinsicWidth(),right.getIntrinsicHeight());
                    mBtnSortAddTime.setCompoundDrawablesRelativeWithIntrinsicBounds(null,null,right,null);
                    break;
            }
            mGoodsAdapter.setSortBy(mSortBy);
        }
    }

}
