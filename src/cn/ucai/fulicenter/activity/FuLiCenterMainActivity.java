package cn.ucai.fulicenter.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import cn.ucai.fulicenter.DemoHXSDKHelper;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.utils.Utils;

public class FuLiCenterMainActivity extends BaseActivity {
    RadioButton rbNewGood,rbBoutique,rbCategory,rbCart,rbPersonalCenter;
    TextView tvCartHint;
    RadioButton[] mrbTabs;
    Fragment[] mFragment;
    int index = 0;
    int currentIndex = 0;
    NewGoodsFragment mNewGoodsFragment;
    BoutiqueFragment mBoutiqueFragment;
    CategoryFragment mCategoryFragment;
    PersonalCenterFragment mPersonalCenterFragment;
    CartFragment mCartFragment;
    public static final  int ACTION_LOGIN = 100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fu_li_center_main);
        initView();
        initFragment();

        // 添加显示第一个fragment
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, mNewGoodsFragment)
                .add(R.id.fragment_container, mBoutiqueFragment)
                .add(R.id.fragment_container, mCategoryFragment)
                .add(R.id.fragment_container,mPersonalCenterFragment)
                .add(R.id.fragment_container,mCartFragment)
                .hide(mBoutiqueFragment).hide(mCategoryFragment)
                .hide(mPersonalCenterFragment)
                .hide(mCartFragment)
                .show(mNewGoodsFragment)
                .commit();
    }


    private void initFragment() {
        mNewGoodsFragment = new NewGoodsFragment();
        mBoutiqueFragment = new BoutiqueFragment();
        mCategoryFragment = new CategoryFragment();
        mCartFragment = new CartFragment();
        mPersonalCenterFragment = new PersonalCenterFragment();
        mFragment = new Fragment[5];
        mFragment[0] = mNewGoodsFragment;
        mFragment[1] = mBoutiqueFragment;
        mFragment[2] = mCategoryFragment;
        mFragment[3] = mCartFragment;
        mFragment[4] = mPersonalCenterFragment;
    }

    private void initView() {
        rbNewGood = (RadioButton) findViewById(R.id.layout_new_good);
        rbBoutique = (RadioButton) findViewById(R.id.layout_boutique);
        rbCategory = (RadioButton) findViewById(R.id.layout_category);
        rbCart = (RadioButton) findViewById(R.id.layout_cart);
        rbPersonalCenter = (RadioButton) findViewById(R.id.layout_personal_center);
        tvCartHint = (TextView) findViewById(R.id.tvCartHint);

        mrbTabs = new RadioButton[5];
        mrbTabs[0] = rbNewGood;
        mrbTabs[1] = rbBoutique;
        mrbTabs[2] = rbCategory;
        mrbTabs[3] = rbCart;
        mrbTabs[4] = rbPersonalCenter;


    }

    public void onCheckedChange(View view) {
        switch (view.getId()) {
            case R.id.layout_new_good:
                index = 0;
                setFragment();
                break;
            case R.id.layout_boutique:
                index = 1;
                setFragment();
                break;
            case R.id.layout_category:
                index = 2;
                setFragment();
                break;
            case R.id.layout_cart:
                index = 3;
                setFragment();
                break;
            case R.id.layout_personal_center:
                if (DemoHXSDKHelper.getInstance().isLogined()) {
                    index = 4;
                    setFragment();
                } else {
                    gotoLogin();
                }
                break;
        }
        setFragment();

    }

    private void setFragment() {
        if (index != currentIndex) {
            FragmentTransaction trx =getSupportFragmentManager().beginTransaction();
            trx.hide(mFragment[currentIndex]);
            if (!mFragment[index].isAdded()) {
                trx.add(R.id.fragment_container, mFragment[index]);
            }
            trx.show(mFragment[index]).commit();
            setRadioButtonStatus(index);
            currentIndex = index;
        }
    }

    private void gotoLogin() {
        startActivityForResult(new Intent(FuLiCenterMainActivity.this,LoginActivity.class),ACTION_LOGIN);
    }

    private void setRadioButtonStatus(int index) {
        for (int i =0 ; i<mrbTabs.length ; i++) {
            if (index == i) {
                mrbTabs[i].setChecked(true);
            } else {
                mrbTabs[i].setChecked(false);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ACTION_LOGIN) {
            if (DemoHXSDKHelper.getInstance().isLogined()) {
                index = 4;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpdateCartNumListener();
        if (!DemoHXSDKHelper.getInstance().isLogined() && index == 4) {
            index = 0;
        }
            setFragment();
            setRadioButtonStatus(currentIndex);
    }

    class UpdateCartNumReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
           int count = Utils.sumCartCount();
            if (count == 0 || !DemoHXSDKHelper.getInstance().isLogined()) {
                tvCartHint.setText(String.valueOf(count));
                tvCartHint.setVisibility(View.GONE);
            } else {
                tvCartHint.setText(String.valueOf(count));
                tvCartHint.setVisibility(View.VISIBLE);
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
