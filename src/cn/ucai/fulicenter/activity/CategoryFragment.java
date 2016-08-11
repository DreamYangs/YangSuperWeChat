package cn.ucai.fulicenter.activity;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.squareup.leakcanary.RefWatcher;

import java.util.ArrayList;
import java.util.List;

import cn.ucai.fulicenter.FuLiCenterApplication;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.adapter.CategoryAdapter;
import cn.ucai.fulicenter.bean.CategoryChildBean;
import cn.ucai.fulicenter.bean.CategoryGroupBean;
import cn.ucai.fulicenter.data.OkHttpUtils2;
import cn.ucai.fulicenter.utils.I;
import cn.ucai.fulicenter.utils.Utils;

/**
 * A simple {@link Fragment} subclass.
 */
public class CategoryFragment extends Fragment {
    FuLiCenterMainActivity mContext;

    ExpandableListView mExpandableListView;
    List<CategoryGroupBean> mGroupList;
    List<ArrayList<CategoryChildBean>> mChildList;
    CategoryAdapter mCategoryAdapter;

    int groupCount;
    public CategoryFragment() {
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category, container, false);
        mContext = (FuLiCenterMainActivity) getContext();
        mGroupList = new ArrayList<CategoryGroupBean>();
        mChildList = new ArrayList<ArrayList<CategoryChildBean>>();
        mCategoryAdapter = new CategoryAdapter(mContext, mGroupList, mChildList);
        initView(view);
        initData();
        return view;
    }

    private void initData() {
        findCategoryGroupList(new OkHttpUtils2.OnCompleteListener<CategoryGroupBean[]>() {
            @Override
            public void onSuccess(CategoryGroupBean[] result) {
                for (int i=0;i<result.length;i++) {
                    Log.i("main", "在CategoryFragment里面下载大类返回的结果：" + result[i].toString());
                }
                if (result != null) {
                    Log.i("main", "CategoryFragment里面下载大类的result长度：" + result.length);
                    ArrayList<CategoryGroupBean> categoryGroupBeen = Utils.array2List(result);
                    if (categoryGroupBeen != null) {
                        mGroupList = categoryGroupBeen;
                        int i = 0 ;
                        for (CategoryGroupBean g : categoryGroupBeen) {
                            mChildList.add(new ArrayList<CategoryChildBean>());
                            findCategoryChildList(g.getId(),i);
                            i++;
                        }
                    }
                }
            }

            @Override
            public void onError(String error) {
                Log.i("main", "在CategoryFragment里面下载小类返回的错误信息：" + error);
            }
        });

    }
    private void findCategoryChildList(int parentId, final int index) {
        OkHttpUtils2<CategoryChildBean[]> utils = new OkHttpUtils2<CategoryChildBean[]>();
        utils.setRequestUrl(I.REQUEST_FIND_CATEGORY_CHILDREN)
                .addParam(I.CategoryChild.PARENT_ID,String.valueOf(parentId))
                .addParam(I.PAGE_ID,String.valueOf(I.PAGE_ID_DEFAULT))
                .addParam(I.PAGE_SIZE,String.valueOf(I.PAGE_SIZE_DEFAULT))
                .targetClass(CategoryChildBean[].class)
                .execute(new OkHttpUtils2.OnCompleteListener<CategoryChildBean[]>() {
                    @Override
                    public void onSuccess(CategoryChildBean[] result) {
                        groupCount++;
                        for (int i=0;i<result.length;i++) {
                            Log.i("main", "在CategoryFragment里面下载小类返回的结果：" + result[i].toString());
                        }
                        if (result != null) {
                            Log.i("main", "CategoryFragment里面下载小类的result长度：" + result.length);
                            ArrayList<CategoryChildBean> categoryChildBeen = Utils.array2List(result);
                            mChildList.set(index,categoryChildBeen);
                        }
                        if (groupCount == mGroupList.size()) {
                            mCategoryAdapter.addAll(mGroupList,mChildList);
                        }

                    }

                    @Override
                    public void onError(String error) {
                        Log.i("main", "在CategoryFragment里面下载小类返回的错误信息：" + error);
                    }
                });
    }

    private void findCategoryGroupList(OkHttpUtils2.OnCompleteListener<CategoryGroupBean[]> listener) {
        OkHttpUtils2<CategoryGroupBean[]> utils = new OkHttpUtils2<CategoryGroupBean[]>();
        utils.setRequestUrl(I.REQUEST_FIND_CATEGORY_GROUP)
                .targetClass(CategoryGroupBean[].class)
                .execute(listener);
    }

    private void initView(View view) {
        mExpandableListView = (ExpandableListView) view.findViewById(R.id.elvCategory);
        mExpandableListView.setGroupIndicator(null);
        mExpandableListView.setAdapter(mCategoryAdapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        OkHttpUtils2.release();
        RefWatcher refWatcher = FuLiCenterApplication.getRefWatcher(getActivity());
        refWatcher.watch(this);
    }
}
