package cn.ucai.fulicenter.bean;

import java.io.Serializable;


/**
 * 购物车
 * 
 * @author yao
 *
 */
public class CartBean implements Serializable {
	int id;
	String userName;
	int goodsId;
	/** 购物车中的商品信息 */
	private GoodDetailsBean goods;
	/** 该商品被选中的件数 */
	private int count;
	private boolean checked;
	
	public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

    public GoodDetailsBean getGoods() {
        return goods;
    }

    public void setGoods(GoodDetailsBean goods) {
        this.goods = goods;
    }

    public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public int getGoodsId() {
		return goodsId;
	}

	public void setGoodsId(int goodsId) {
		this.goodsId = goodsId;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public CartBean() {
		// TODO Auto-generated constructor stub
	}

    public CartBean(int id, String userName, int goodsId,
            int count, boolean checked) {
        super();
        this.id = id;
        this.userName = userName;
        this.goodsId = goodsId;
        this.count = count;
        this.checked = checked;
    }

	@Override
	public String toString() {
		return "CartBean{" +
				"id=" + id +
				", userName='" + userName + '\'' +
				", goodsId=" + goodsId +
				", goods=" + goods +
				", count=" + count +
				", checked=" + checked +
				'}';
	}
}
