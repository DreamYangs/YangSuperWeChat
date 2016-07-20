package cn.ucai.superwechat.task;

import android.content.Context;
import android.content.Intent;

import java.util.List;

import cn.ucai.superwechat.SuperWeChatApplication;
import cn.ucai.superwechat.bean.Result;
import cn.ucai.superwechat.bean.UserAvatar;
import cn.ucai.superwechat.data.OkHttpUtils2;
import cn.ucai.superwechat.utils.I;
import cn.ucai.superwechat.utils.Utils;

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
                    public void onSuccess(String result) {
                        Result result1 = Utils.getListResultFromJson(result, UserAvatar.class);
                        List<UserAvatar> list = (List<UserAvatar>) result1.getRetData();
                        if (list != null && list.size()>0) {
                            SuperWeChatApplication.getInstance().setUserList(list);
                            context.sendStickyBroadcast(new Intent("update_contact_list"));
                        }
                    }

                    @Override
                    public void onError(String error) {

                    }
                });
    }
}
