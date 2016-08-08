package cn.ucai.fulicenter.task;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.List;
import java.util.Map;

import cn.ucai.fulicenter.FuLiCenterApplication;
import cn.ucai.fulicenter.bean.MessageBean;
import cn.ucai.fulicenter.bean.Result;
import cn.ucai.fulicenter.bean.UserAvatar;
import cn.ucai.fulicenter.data.OkHttpUtils2;
import cn.ucai.fulicenter.utils.I;
import cn.ucai.fulicenter.utils.Utils;

/**
 * Created by Administrator on 2016/7/20.
 */
public class DownloadCollectCountTask {
    private final  static  String TAG = DownloadCollectCountTask.class.getSimpleName();
    String userName;
    Context context;

    public DownloadCollectCountTask(Context context, String userName) {
        this.context = context;
        this.userName = userName;
    }

    public void execute() {
        final OkHttpUtils2<MessageBean> utils = new OkHttpUtils2<MessageBean>();
        Log.i("main", "在DownloadCollectCountTask里面的userName:" + userName);
        utils.setRequestUrl(I.REQUEST_FIND_COLLECT_COUNT)
                .addParam(I.Collect.USER_NAME,userName)
                .targetClass(MessageBean.class)
                .execute(new OkHttpUtils2.OnCompleteListener<MessageBean>() {
                    @Override
                    public void onSuccess(MessageBean msg) {
                        Log.i("main", "在DownloadCollectCountTask下载收藏的数量的返回结果：" + msg.toString());
                        if (msg != null) {
                            if (msg.isSuccess()) {
                                FuLiCenterApplication.getInstance().setCollectCount(Integer.valueOf(msg.getMsg()));
                            } else {
                                FuLiCenterApplication.getInstance().setCollectCount(0);
                            }
                                context.sendStickyBroadcast(new Intent("update_collect"));

                         }
                    }

                    @Override
                    public void onError(String error) {

                    }
                });
    }
}
