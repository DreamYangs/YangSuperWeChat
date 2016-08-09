package cn.ucai.fulicenter.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.ucai.fulicenter.D;
import cn.ucai.fulicenter.FuLiCenterApplication;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.activity.GoodDetailsActivity;
import cn.ucai.fulicenter.bean.CollectBean;
import cn.ucai.fulicenter.bean.MessageBean;
import cn.ucai.fulicenter.data.OkHttpUtils2;
import cn.ucai.fulicenter.task.DownloadCollectCountTask;
import cn.ucai.fulicenter.utils.I;
import cn.ucai.fulicenter.utils.ImageUtils;
import cn.ucai.fulicenter.view.FooterViewHolder;


public class CollectAdapter extends RecyclerView.Adapter<ViewHolder> {
    Context mContext;
    List<CollectBean> mCollectList;

    CollectViewHolder mCollectViewHolder;
    FooterViewHolder mFooterViewHolder;

    boolean isMore;
    String footerString;

    int sortBy;
    public void setSortBy(int sortBy) {
        this.sortBy = sortBy;
        notifyDataSetChanged();
    }

    public void setFooterString(String footerString) {
        this.footerString = footerString;
    }

    public boolean isMore() {
        return isMore;
    }

    public void setMore(boolean more) {
        isMore = more;
    }

    public CollectAdapter(Context mContext, List<CollectBean> mCollectList) {
        this.mContext = mContext;
        this.mCollectList = new ArrayList<CollectBean>();
        this.mCollectList.addAll(mCollectList);

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
                View viewItem = LayoutInflater.from(mContext).inflate(R.layout.item_collect, parent, false);
                holder = new CollectViewHolder(viewItem);
                break;
        }
//        LayoutInflater inflater = LayoutInflater.from(mContext);
//        holder = new BoutiqueViewHolder(inflater.inflate(R.layout.item_new_goods, null, false));

        return  holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (holder instanceof CollectViewHolder) {
            mCollectViewHolder = (CollectViewHolder) holder;
            final CollectBean collectBean = mCollectList.get(position);
            ImageUtils.getNewGoodsThumb(mContext, mCollectViewHolder.ivGoodThumb, collectBean.getGoodsThumb());
            mCollectViewHolder.tvGoodName.setText(collectBean.getGoodsName());
            mCollectViewHolder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mContext.startActivity(new Intent(mContext, GoodDetailsActivity.class)
                    .putExtra(D.GoodDetails.KEY_GOODS_NAME,collectBean.getGoodsId()));
                }
            });
            mCollectViewHolder.ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    OkHttpUtils2<MessageBean> utils = new OkHttpUtils2<MessageBean>();
                    utils.setRequestUrl(I.REQUEST_DELETE_COLLECT)
                            .addParam(I.Collect.USER_NAME, FuLiCenterApplication.getInstance().getUserName())
                            .addParam(I.Collect.GOODS_ID,String.valueOf(collectBean.getGoodsId()))
                            .targetClass(MessageBean.class)
                            .execute(new OkHttpUtils2.OnCompleteListener<MessageBean>() {
                                @Override
                                public void onSuccess(MessageBean result) {
                                    if (result != null && result.isSuccess()) {
                                        mCollectList.remove(collectBean);
                                        new DownloadCollectCountTask(mContext, FuLiCenterApplication.getInstance().getUserName()).execute();
                                        notifyDataSetChanged();
                                    }
                                    Toast.makeText(mContext, result.getMsg(), Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onError(String error) {

                                }
                            });
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
        return mCollectList!=null?mCollectList.size()+1:1;
    }

    public void initItem(ArrayList<CollectBean> list) {
        if (mCollectList != null) {
            mCollectList.clear();
        }
        mCollectList.addAll(list);
        notifyDataSetChanged();
    }

    public void addMoreItem(ArrayList<CollectBean> list) {
        mCollectList.addAll(list);
        notifyDataSetChanged();
    }

    class CollectViewHolder extends ViewHolder {
        LinearLayout layout;
        ImageView ivGoodThumb;
        TextView tvGoodName;
        ImageView ivDelete;
        public CollectViewHolder(View itemView) {
            super(itemView);
            layout = (LinearLayout) itemView.findViewById(R.id.layout_good);
            ivGoodThumb = (ImageView) itemView.findViewById(R.id.niv_good_thumb);
            tvGoodName = (TextView) itemView.findViewById(R.id.tv_good_name);
            ivDelete = (ImageView) itemView.findViewById(R.id.iv_collect_delete);
        }
    }


}
