package cn.ucai.fulicenter.task;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.ucai.fulicenter.SuperWeChatApplication;
import cn.ucai.fulicenter.bean.MemberUserAvatar;
import cn.ucai.fulicenter.bean.Result;
import cn.ucai.fulicenter.data.OkHttpUtils2;
import cn.ucai.fulicenter.utils.I;
import cn.ucai.fulicenter.utils.Utils;

/**
 * Created by Administrator on 2016/7/20.
 */
public class DownloadGroupMemberListTask {
    private final  static  String TAG = DownloadGroupMemberListTask.class.getSimpleName();
    String hxId;
    Context context;

    public DownloadGroupMemberListTask(Context context, String hxId) {
        this.context = context;
        this.hxId = hxId;
    }

    public void execute() {
        final OkHttpUtils2<String> utils = new OkHttpUtils2<String>();
        utils.setRequestUrl(I.REQUEST_DOWNLOAD_GROUP_MEMBERS_BY_HXID)
                .addParam(I.Member.GROUP_HX_ID,hxId)
                .targetClass(String.class)
                .execute(new OkHttpUtils2.OnCompleteListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        Log.i("main", "在DownloadGroupMemberListTask下载群组成员信息得到的返回结果：" + s);
                        Result result1 = Utils.getListResultFromJson(s, MemberUserAvatar.class);
                        List<MemberUserAvatar> list = (List<MemberUserAvatar>) result1.getRetData();
                        if (list != null && list.size()>0) {
                            Map<String, HashMap<String, MemberUserAvatar>> membersMap
                                    = SuperWeChatApplication.getInstance().getMembersMap();
                            if (!membersMap.containsKey(hxId)) {
                                membersMap.put(hxId, new HashMap<String, MemberUserAvatar>());
                            }
                            HashMap<String, MemberUserAvatar> hxIdMembers = membersMap.get(hxId);
                            for (MemberUserAvatar u : list) {
                                hxIdMembers.put(u.getMUserName(), u);
                            }
                            context.sendStickyBroadcast(new Intent("update_member_list"));

                        }
                    }

                    @Override
                    public void onError(String error) {
                        Log.i("main", "在DownloadGroupMemberListTask下载的错误信息：" + error);
                    }
                });
    }
}
