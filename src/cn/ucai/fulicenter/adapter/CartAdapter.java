package cn.ucai.fulicenter.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.ucai.fulicenter.D;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.activity.GoodDetailsActivity;
import cn.ucai.fulicenter.bean.CartBean;
import cn.ucai.fulicenter.task.UpdateCartTask;
import cn.ucai.fulicenter.utils.I;
import cn.ucai.fulicenter.utils.ImageUtils;
import cn.ucai.fulicenter.view.FooterViewHolder;

/**
 * Created by Administrator on 2016/8/9.
 */
public class CartAdapter extends RecyclerView.Adapter {

    Context mContext;
    List<CartBean> mCartList;

    CartViewHolder mCartViewHolder;

    boolean isMore;


    public boolean isMore() {
        return isMore;
    }

    public void setMore(boolean more) {
        isMore = more;
    }

    public CartAdapter(Context mContext, List<CartBean> mCartList) {
        this.mContext = mContext;
        this.mCartList = new ArrayList<CartBean>();
        this.mCartList.addAll(mCartList);

    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        LayoutInflater inflater = LayoutInflater.from(mContext);
//        holder = new BoutiqueViewHolder(inflater.inflate(R.layout.item_new_goods, null, false));
        return new CartViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_cart, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof CartViewHolder) {
            mCartViewHolder = (CartViewHolder) holder;
            final CartBean cartBean = mCartList.get(position);
            ImageUtils.setGoodsImage(mContext, mCartViewHolder.ivCartThumb, cartBean.getGoods().getGoodsImg());
            mCartViewHolder.tvCartGoodName.setText(cartBean.getGoods().getGoodsName());
            mCartViewHolder.tvCartGoodCount.setText("("+cartBean.getCount()+")");
            mCartViewHolder.tvCartGoodPrice.setText(cartBean.getGoods().getCurrencyPrice());

            mCartViewHolder.ivCartThumb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mContext.startActivity(new Intent(mContext, GoodDetailsActivity.class)
                    .putExtra(D.GoodDetails.KEY_GOODS_NAME,cartBean.getGoodsId()));
                }
            });
            mCartViewHolder.cbCartSelect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    cartBean.setChecked(isChecked);
                    new UpdateCartTask(mContext, cartBean).execute();
                }
            });
            mCartViewHolder.ivCartAdd.setOnClickListener(new ChangeCountListener(cartBean,1));
            mCartViewHolder.ivCartDelete.setOnClickListener(new ChangeCountListener(cartBean,-1));
        }
    }

    @Override
    public int getItemCount() {
        return mCartList !=null? mCartList.size():0;
    }

    public void initItem(List<CartBean> list) {
        if (mCartList != null) {
            mCartList.clear();
        }
        mCartList.addAll(list);
        notifyDataSetChanged();
    }

    public void addMoreItem(List<CartBean> list) {
        mCartList.addAll(list);
        notifyDataSetChanged();
    }


    class CartViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout layout;
        CheckBox cbCartSelect;
        ImageView ivCartThumb,ivCartAdd,ivCartDelete;
        TextView tvCartGoodName,tvCartGoodCount,tvCartGoodPrice;

        public CartViewHolder(View itemView) {
            super(itemView);
            layout = (RelativeLayout) itemView.findViewById(R.id.layout_cart_item);
            ivCartThumb = (ImageView) itemView.findViewById(R.id.iv_cart_thumb);
            ivCartAdd = (ImageView) itemView.findViewById(R.id.iv_cart_add);
            ivCartDelete = (ImageView) itemView.findViewById(R.id.iv_cart_delete);
            tvCartGoodName = (TextView) itemView.findViewById(R.id.tv_cart_good_name);
            tvCartGoodCount = (TextView) itemView.findViewById(R.id.tv_cart_count);
            tvCartGoodPrice = (TextView) itemView.findViewById(R.id.tv_cart_price);
            cbCartSelect = (CheckBox) itemView.findViewById(R.id.cb_cart_selected);


        }
    }

    class ChangeCountListener implements View.OnClickListener {
        CartBean cartBean;
        int count;

        public ChangeCountListener(CartBean cartBean, int count) {
            this.cartBean = cartBean;
            this.count = count;
        }

        @Override
        public void onClick(View view) {
            this.cartBean.setCount(cartBean.getCount()+count);
            new UpdateCartTask(mContext,cartBean).execute();
        }

    }

}
