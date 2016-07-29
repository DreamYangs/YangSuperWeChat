package cn.ucai.fulicenter.task;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.List;
import java.util.Map;

import cn.ucai.fulicenter.FuLiCenterApplication;
import cn.ucai.fulicenter.bean.Result;
import cn.ucai.fulicenter.bean.UserAvatar;
import cn.ucai.fulicenter.data.OkHttpUtils2;
import cn.ucai.fulicenter.utils.I;
import cn.ucai.fulicenter.utils.Utils;

/**
 * Created by Administrator on 2016/7/20.
 */
public class DownloadContactListTask {
    private final  static  String TAG = DownloadContactListTask.class.getSimpleName();
    String userName;
    Context context;

    public DownloadContactListTask(Context context,String userName) {
        this.context = context;
        this.userName = userName;
    }

    public void execute() {
        final OkHttpUtils2<String> utils = new OkHttpUtils2<String>();
        utils.setRequestUrl(I.REQUEST_DOWNLOAD_CONTACT_ALL_LIST)
                .addParam(I.Contact.USER_NAME,userName)
                .targetClass(String.class)
                .execute(new OkHttpUtils2.OnCompleteListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        Log.i("main", "在DownloadContactListTask下载好友信息得到的返回结果：" + s);
                        Result result1 = Utils.getListResultFromJson(s, UserAvatar.class);
                        List<UserAvatar> list = (List<UserAvatar>) result1.getRetData();
                        if (list != null && list.size()>0) {
                            FuLiCenterApplication.getInstance().setUserList(list);
                            context.sendStickyBroadcast(new Intent("update_contact_list"));
                            Map<String, UserAvatar> userMap = FuLiCenterApplication.getInstance().getUserMap();

                            for (UserAvatar u:list) {
                                Log.i("main","下载的好友信息："+ u);
                                userMap.put(u.getMUserName(), u);
                            }
                        }
                    }

                    @Override
                    public void onError(String error) {

                    }
                });
    }
}
