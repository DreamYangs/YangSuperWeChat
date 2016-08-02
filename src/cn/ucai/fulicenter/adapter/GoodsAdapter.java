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
import cn.ucai.fulicenter.utils.I;
import cn.ucai.fulicenter.utils.ImageUtils;
import cn.ucai.fulicenter.view.FooterViewHolder;

/**
 * Created by Administrator on 2016/8/1.
 */
public class GoodsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context mContext;
    List<NewGoodBean> mGoodList;

    GoodViewHolder mGoodViewHolder;
    FooterViewHolder mFooterViewHolder;

    boolean isMore;
    String footerString;

    public void setFooterString(String footerString) {
        this.footerString = footerString;
    }

    public boolean isMore() {
        return isMore;
    }

    public void setMore(boolean more) {
        isMore = more;
    }

    public GoodsAdapter(Context mContext, List<NewGoodBean> mGoodList) {
        this.mContext = mContext;
        this.mGoodList = new ArrayList<NewGoodBean>();
        this.mGoodList.addAll(mGoodList);

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder holder = null;
        switch (viewType) {
            case I.TYPE_FOOTER:
                View viewFooter = LayoutInflater.from(mContext).inflate(R.layout.item_footer, parent, false);
                holder = new FooterViewHolder(viewFooter);
                break;
            case I.TYPE_ITEM:
                View viewItem = LayoutInflater.from(mContext).inflate(R.layout.item_new_goods, parent, false);
                holder = new GoodViewHolder(viewItem);
                break;
        }
//        LayoutInflater inflater = LayoutInflater.from(mContext);
//        holder = new GoodViewHolder(inflater.inflate(R.layout.item_new_goods, null, false));

        return  holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof GoodViewHolder) {
            mGoodViewHolder = (GoodViewHolder) holder;
            NewGoodBean goodBean = mGoodList.get(position);
            ImageUtils.getNewGoodsThumb(mContext, mGoodViewHolder.ivGoodThumb, goodBean.getGoodsThumb());
            mGoodViewHolder.tvGoodName.setText(goodBean.getGoodsName());
            mGoodViewHolder.tvGoodPrice.setText(goodBean.getCurrencyPrice());
        }
        if (holder instanceof FooterViewHolder) {
            mFooterViewHolder = (FooterViewHolder) holder;
            mFooterViewHolder.tvFooter.setText(footerString);
        }

    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1) {
            return I.TYPE_FOOTER;
        } else {
            return I.TYPE_ITEM;
        }
    }

    @Override
    public int getItemCount() {
        return mGoodList!=null?mGoodList.size()+1:1;
    }

    public void addData(ArrayList<NewGoodBean> list) {
        if (mGoodList != null) {
            mGoodList.clear();
        }
        mGoodList.addAll(list);
        notifyDataSetChanged();
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
