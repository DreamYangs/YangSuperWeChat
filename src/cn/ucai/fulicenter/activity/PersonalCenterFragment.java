package cn.ucai.fulicenter.activity;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.utils.I;

/**
 * A simple {@link Fragment} subclass.
 */
public class PersonalCenterFragment extends Fragment {
    FuLiCenterMainActivity mContext;
    TextView mSettings;
    LinearLayout mCollectThings,mCollectStores,mMyFootprint;
    ImageView mSessionImageView;
    ImageView mUserAvatar;
    TextView mUserName;
    ImageView mUserQrCode;
    TextView mMyThings;
    ImageView mCheckMyThingsIn;

    public PersonalCenterFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContext = (FuLiCenterMainActivity) getContext();
        View view = inflater.inflate(R.layout.fragment_personal_center, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        mSettings = (TextView) view.findViewById(R.id.tv_personal_center_top_settings);
        mCollectThings = (LinearLayout) view.findViewById(R.id.layout_personal_center_collect_things);
        mCollectStores = (LinearLayout) view.findViewById(R.id.layout_personal_center_collect_stores);
        mMyFootprint = (LinearLayout) view.findViewById(R.id.layout_personal_center_my_footprint);
        mSessionImageView = (ImageView) view.findViewById(R.id.iv_personal_center_top_session_image);
        mUserAvatar = (ImageView) view.findViewById(R.id.iv_personal_center_user_avatar);
        mUserName = (TextView) view.findViewById(R.id.tv_personal_center_user_name);
        mUserQrCode = (ImageView) view.findViewById(R.id.iv_personal_center_user_qr_code);
        mMyThings = (TextView) view.findViewById(R.id.tv_personal_center_check_myThings);
        mCheckMyThingsIn = (ImageView) view.findViewById(R.id.iv_check_myThings_in);

    }

}
