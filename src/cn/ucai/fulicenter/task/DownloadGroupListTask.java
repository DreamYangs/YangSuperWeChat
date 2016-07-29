package cn.ucai.fulicenter.task;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.List;

import cn.ucai.fulicenter.FuLiCenterApplication;
import cn.ucai.fulicenter.bean.GroupAvatar;
import cn.ucai.fulicenter.bean.Result;
import cn.ucai.fulicenter.data.OkHttpUtils2;
import cn.ucai.fulicenter.utils.I;
import cn.ucai.fulicenter.utils.Utils;

/**
 * Created by Administrator on 2016/7/20.
 */
public class DownloadGroupListTask {
    private final  static  String TAG = DownloadGroupListTask.class.getSimpleName();
    String userName;
    Context context;

    public DownloadGroupListTask(Context context, String userName) {
        this.context = context;
        this.userName = userName;
    }

    public void execute() {
        final OkHttpUtils2<String> utils = new OkHttpUtils2<String>();
        utils.setRequestUrl(I.REQUEST_FIND_GROUP_BY_USER_NAME)
                .addParam(I.User.USER_NAME,userName)
                .targetClass(String.class)
                .execute(new OkHttpUtils2.OnCompleteListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        Log.i("main", "在DownloadGroupListTask下载群组信息得到的返回结果：" + s);
                        Result result1 = Utils.getListResultFromJson(s, GroupAvatar.class);
                        List<GroupAvatar> list = (List<GroupAvatar>) result1.getRetData();
                        if (list != null && list.size()>0) {

                            FuLiCenterApplication.getInstance().setGroupList(list);
                            for (GroupAvatar g : list) {
                                FuLiCenterApplication.getInstance().getGroupMap().put(g.getMGroupHxid(), g);
                            }
                            context.sendStickyBroadcast(new Intent("update_group_list"));

                        }
                    }

                    @Override
                    public void onError(String error) {
                        Log.i("main", "在DownloadGroupListTask下载群组信息的错误信息：" + error);
                    }
                });
    }
}
