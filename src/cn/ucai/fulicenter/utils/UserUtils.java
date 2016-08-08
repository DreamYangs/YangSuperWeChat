package cn.ucai.fulicenter.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import cn.ucai.fulicenter.FuLiCenterApplication;
import cn.ucai.fulicenter.applib.controller.HXSDKHelper;
import cn.ucai.fulicenter.DemoHXSDKHelper;

import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.bean.MemberUserAvatar;
import cn.ucai.fulicenter.bean.UserAvatar;
import cn.ucai.fulicenter.domain.User;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class UserUtils {
    /**
     * 根据username获取相应user，由于demo没有真实的用户数据，这里给的模拟的数据；
     * @param username
     * @return
     */
    public static User getUserInfo(String username){
        User user = ((DemoHXSDKHelper) HXSDKHelper.getInstance()).getContactList().get(username);
        if(user == null){
            user = new User(username);
        }
            
        if(user != null){
            //demo没有这些数据，临时填充
        	if(TextUtils.isEmpty(user.getNick()))
        		user.setNick(username);
        }
        return user;
    }

	public static MemberUserAvatar getAppMemberInfo(String hxId,String userName) {
		MemberUserAvatar memberUser = null;
		HashMap<String, MemberUserAvatar> memberMap
				= FuLiCenterApplication.getInstance().getMembersMap().get(hxId);
		if (memberMap == null || memberMap.size() < 0) {
			return null;
		} else {
			memberUser = memberMap.get(userName);
		}

		return memberUser;
	}

	/**
     * 设置用户头像
     * @param username
     */
    public static void setUserAvatar(Context context, String username, ImageView imageView){
    	User user = getUserInfo(username);
        if(user != null && user.getAvatar() != null){
            Picasso.with(context).load(user.getAvatar()).placeholder(R.drawable.default_avatar).into(imageView);
        }else{
            Picasso.with(context).load(R.drawable.default_avatar).into(imageView);
        }
    }
	/**
	 * 设置用户头像
	 * @param username
	 */
	public static void setAppUserAvatar(Context context, String username, ImageView imageView) {
		String path = getUserAvatarPath(username);
		if (path != null && username != null) {
//			Log.i("main", "sql语句:" + path);
			Picasso.with(context).load(path).placeholder(R.drawable.default_avatar).into(imageView);
		} else {
			Picasso.with(context).load(R.drawable.default_avatar).into(imageView);
		}
	}


	public static String getUserAvatarPath(String userName) {
		StringBuilder path = new StringBuilder(I.SERVER_URL);
		path.append(I.QUESTION).append(I.KEY_REQUEST)
				.append(I.EQUAL).append(I.REQUEST_DOWNLOAD_AVATAR)
				.append(I.AND)
		.append(I.AVATAR_TYPE).append(I.EQUAL).append(userName);

		return path.toString();

	}



		/**
         * 设置当前用户头像
         */
	public static void setCurrentUserAvatar(Context context, ImageView imageView) {
		User user = ((DemoHXSDKHelper)HXSDKHelper.getInstance()).getUserProfileManager().getCurrentUserInfo();
		if (user != null && user.getAvatar() != null) {
			Picasso.with(context).load(user.getAvatar()).placeholder(R.drawable.default_avatar).into(imageView);
		} else {
			Picasso.with(context).load(R.drawable.default_avatar).into(imageView);
		}
	}
	/**
	 * 设置当前用户头像
	 */
	public static void setAppCurrentUserAvatar(Context context, ImageView imageView) {
		String userName = FuLiCenterApplication.getInstance().getUserName();
		Log.i("main", "在setAPPCurrentUserAvatar中通过FuLiCenterApplication.getInstance().getUserName();得到的userName：" + userName);
		setAppUserAvatar(context,userName,imageView);
	}
    /**
     * 设置用户昵称
     */
    public static void setUserNick(String username,TextView textView){
    	User user = getUserInfo(username);
    	if(user != null){
    		textView.setText(user.getNick());
    	}else{
    		textView.setText(username);
    	}
    }

    /**
     * 设置登录账户昵称
     */

    public static void setAppUserNick(String userName,TextView textView,int requestCode) {
//        String userNick = FuLiCenterApplication.getInstance().getUserNick();
//        Log.i("main", "在SuperWeChatApplication中获得的UserNick：" + userNick);

        String userNick = FuLiCenterApplication.currentUserNick;  // 也可以是这句话，不知道有这个方法。但是这个方法的范围大些
        Log.i("main", "在SuperWeChatApplication中.currentNick方法中获取的昵称：" + userNick);
		Log.i("main", "设置登录账户的昵称：" + userNick);
		if (userNick == null||userNick=="") {
            textView.setText(userName);
        } else {
            textView.setText(userNick);
        }
    }
    /**
	 * 设置用户好友昵称
	 */
	public static void setAppUserNick(String username,TextView textView){
		UserAvatar user = getAppUserInfo(username);
        setAppUserNick(user,textView);
	}
    /**
     * 设置用户昵称
     */
    public static void setAppUserNick(UserAvatar user,TextView textView){
        Log.i("main", "设置界面显示的账户昵称：" + user.getMUserNick());
        if(user != null){
            if (user.getMUserNick() != null) {
                textView.setText(user.getMUserNick());
            } else {
                textView.setText(user.getMUserName());

            }
        }else{
            textView.setText(user.getMUserName());
        }
//        if (userAvatar != null) {
//            if (userAvatar.getMUserNick() != null) {
//                textView.setText(userAvatar.getMUserNick());
//            } else {
//                textView.setText(userAvatar.getMUserName());
//            }
//        }
    }
	/**
	 * 根据username获取相应userAvatar
	 * @param username
	 * @return
	 */
	public static UserAvatar getAppUserInfo(String username){
		UserAvatar user = FuLiCenterApplication.getInstance().getUserMap().get(username);
		if(user == null){
			user = new UserAvatar(username);
		}

		return user;
	}
    /**
     * 设置当前用户昵称
     */
    public static void setCurrentUserNick(TextView textView){
    	User user = ((DemoHXSDKHelper)HXSDKHelper.getInstance()).getUserProfileManager().getCurrentUserInfo();
    	if(textView != null){
    		textView.setText(user.getNick());
    	}
    }
    
    /**
     * 保存或更新某个用户
     * @param newUser
     */
	public static void saveUserInfo(User newUser) {
		if (newUser == null || newUser.getUsername() == null) {
			return;
		}
		((DemoHXSDKHelper) HXSDKHelper.getInstance()).saveContact(newUser);
	}

}
