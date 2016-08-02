package cn.ucai.fulicenter.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.bean.NewGoodBean;

/**
 * Created by Administrator on 2016/8/1.
 */
public class GoodsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context mContext;
    List<NewGoodBean> mGoodList;

    GoodViewHolder mGoodViewHolder;

    public GoodsAdapter(Context mContext, List<NewGoodBean> mGoodList) {
        this.mContext = mContext;
        this.mGoodList = new ArrayList<NewGoodBean>();
        this.mGoodList.addAll(mGoodList);

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder holder = null;
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_new_goods, null, false);
        holder = new GoodViewHolder(view);
        return  holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof GoodViewHolder) {
            mGoodViewHolder = (GoodViewHolder) holder;
            NewGoodBean goodBean = mGoodList.get(position);
            mGoodViewHolder.tvGoodName.setText(goodBean.getGoodsName());
            mGoodViewHolder.tvGoodPrice.setText(goodBean.getCurrencyPrice());

        }

    }

    @Override
    public int getItemCount() {
        return mGoodList.size();
    }

    class GoodViewHolder extends RecyclerView.ViewHolder {
        LinearLayout layout;
        ImageView ivGoodThumb;
        TextView tvGoodName;
        TextView tvGoodPrice;

        public GoodViewHolder(View itemView) {
            super(itemView);
            layout = (LinearLayout) itemView.findViewById(R.id.layout_group);
            ivGoodThumb = (ImageView) itemView.findViewById(R.id.niv_good_thumb);
            tvGoodName = (TextView) itemView.findViewById(R.id.tv_good_name);
            tvGoodPrice = (TextView) itemView.findViewById(R.id.tv_good_price);
        }
    }
}
