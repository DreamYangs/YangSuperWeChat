package cn.ucai.fulicenter.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.ucai.fulicenter.D;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.activity.BoutiqueChildActivity;
import cn.ucai.fulicenter.activity.GoodDetailsActivity;
import cn.ucai.fulicenter.bean.BoutiqueBean;
import cn.ucai.fulicenter.utils.I;
import cn.ucai.fulicenter.utils.ImageUtils;
import cn.ucai.fulicenter.view.FooterViewHolder;

/**
 * Created by Administrator on 2016/8/1.
 */
public class BoutiqueAdapter extends RecyclerView.Adapter<ViewHolder> {
    Context mContext;
    List<BoutiqueBean> mBoutiqueList;

    BoutiqueViewHolder mBoutiqueViewHolder;
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

    public BoutiqueAdapter(Context mContext, List<BoutiqueBean> mBoutiqueList) {
        this.mContext = mContext;
        this.mBoutiqueList = new ArrayList<BoutiqueBean>();
        this.mBoutiqueList.addAll(mBoutiqueList);

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder holder = null;
        switch (viewType) {
            case I.TYPE_FOOTER:
                View viewFooter = LayoutInflater.from(mContext).inflate(R.layout.item_footer, parent, false);
                holder = new FooterViewHolder(viewFooter);
                break;
            case I.TYPE_ITEM:
                View viewItem = LayoutInflater.from(mContext).inflate(R.layout.item_boutique, parent, false);
                holder = new BoutiqueViewHolder(viewItem);
                break;
        }
//        LayoutInflater inflater = LayoutInflater.from(mContext);
//        holder = new BoutiqueViewHolder(inflater.inflate(R.layout.item_new_goods, null, false));

        return  holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (holder instanceof BoutiqueViewHolder) {
            mBoutiqueViewHolder = (BoutiqueViewHolder) holder;
            final BoutiqueBean boutiqueBean = mBoutiqueList.get(position);
            Log.i("main", "精品的图片地址：" + boutiqueBean.getImageUrl());
            ImageUtils.getBoutiqueImage(mContext, mBoutiqueViewHolder.ivBoutiqueImage, boutiqueBean.getImageUrl());
            mBoutiqueViewHolder.tvBoutiqueTitle.setText(boutiqueBean.getTitle());
            mBoutiqueViewHolder.tvBoutiqueName.setText(boutiqueBean.getName());
            mBoutiqueViewHolder.tvBoutiqueDesc.setText(boutiqueBean.getDescription());
            mBoutiqueViewHolder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mContext.startActivity(new Intent(mContext, BoutiqueChildActivity.class)
                    .putExtra(D.Boutique.KEY_GOODS_ID,boutiqueBean.getId())
                    .putExtra(D.Boutique.KEY_NAME,boutiqueBean.getName()));
                }
            });
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
        return mBoutiqueList !=null? mBoutiqueList.size()+1:1;
    }

    public void initItem(ArrayList<BoutiqueBean> list) {
        if (mBoutiqueList != null) {
            mBoutiqueList.clear();
        }
        mBoutiqueList.addAll(list);
        notifyDataSetChanged();
    }

    public void addMoreItem(ArrayList<BoutiqueBean> list) {
        mBoutiqueList.addAll(list);
        notifyDataSetChanged();
    }

    class BoutiqueViewHolder extends  ViewHolder {
        RelativeLayout layout;
        ImageView ivBoutiqueImage;
        TextView tvBoutiqueTitle;
        TextView tvBoutiqueName;
        TextView tvBoutiqueDesc;

        public BoutiqueViewHolder(View itemView) {
            super(itemView);
            layout = (RelativeLayout) itemView.findViewById(R.id.layout_boutique_item);
            ivBoutiqueImage = (ImageView) itemView.findViewById(R.id.iv_boutique_image);
            tvBoutiqueTitle = (TextView) itemView.findViewById(R.id.tv_boutique_title);
            tvBoutiqueName = (TextView) itemView.findViewById(R.id.tv_boutique_name);
            tvBoutiqueDesc = (TextView) itemView.findViewById(R.id.tv_boutique_desc);
        }
    }
}
