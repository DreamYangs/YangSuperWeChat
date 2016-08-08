package cn.ucai.fulicenter.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import cn.ucai.fulicenter.DemoHXSDKHelper;
import cn.ucai.fulicenter.R;

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
//                .add(R.id.fragment_container,mPersonalCenterFragment)
                .hide(mBoutiqueFragment).hide(mCategoryFragment)
//                .hide(mPersonalCenterFragment)
                .show(mNewGoodsFragment)
                .commit();
    }

    private void initFragment() {
        mNewGoodsFragment = new NewGoodsFragment();
        mBoutiqueFragment = new BoutiqueFragment();
        mCategoryFragment = new CategoryFragment();
        mPersonalCenterFragment = new PersonalCenterFragment();
        mFragment = new Fragment[5];
        mFragment[0] = mNewGoodsFragment;
        mFragment[1] = mBoutiqueFragment;
        mFragment[2] = mCategoryFragment;
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
                break;
            case R.id.layout_boutique:
                index = 1;
                break;
            case R.id.layout_category:
                index = 2;
                break;
            case R.id.layout_cart:
                index = 3;
                break;
            case R.id.layout_personal_center:
                if (DemoHXSDKHelper.getInstance().isLogined()) {
                    index = 4;
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
            } else {
                setRadioButtonStatus(currentIndex);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (DemoHXSDKHelper.getInstance().isLogined()) {

            setFragment();
        } else {
            index = currentIndex;
            if (index == 4) {
                index = 0;
            }
            setFragment();
        }
    }
}
