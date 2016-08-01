package cn.ucai.fulicenter.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroupManager;

import cn.ucai.fulicenter.DemoHXSDKHelper;
import cn.ucai.fulicenter.FuLiCenterApplication;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.bean.Result;
import cn.ucai.fulicenter.bean.UserAvatar;
import cn.ucai.fulicenter.data.OkHttpUtils2;
import cn.ucai.fulicenter.db.UserDao;
import cn.ucai.fulicenter.task.DownloadContactListTask;
import cn.ucai.fulicenter.utils.I;
import cn.ucai.fulicenter.utils.Utils;

/**
 * 开屏页
 *
 */
public class SplashActivity extends BaseActivity {
	private static final String TAG = SplashActivity.class.getSimpleName();
	private RelativeLayout rootLayout;
	private TextView versionText;
	
	private static final int sleepTime = 2000;
	String splashUserName;
	@Override
	protected void onCreate(Bundle arg0) {
		setContentView(R.layout.activity_splash);
		super.onCreate(arg0);

		rootLayout = (RelativeLayout) findViewById(R.id.splash_root);
		versionText = (TextView) findViewById(R.id.tv_version);

		versionText.setText(getVersion());
		AlphaAnimation animation = new AlphaAnimation(0.3f, 1.0f);
		animation.setDuration(1500);
		rootLayout.startAnimation(animation);
	}

	@Override
	protected void onStart() {
		super.onStart();

		new Thread(new Runnable() {
			public void run() {
				if (DemoHXSDKHelper.getInstance().isLogined()) {
					// ** 免登陆情况 加载所有本地群和会话
					//不是必须的，不加sdk也会自动异步去加载(不会重复加载)；
					//加上的话保证进了主页面会话和群组都已经load完毕
					long start = System.currentTimeMillis();
					EMGroupManager.getInstance().loadAllGroups();
					EMChatManager.getInstance().loadAllConversations();
					splashUserName = FuLiCenterApplication.getInstance().getUserName();
					Log.i("main", "currentuser:" + splashUserName);
					Log.i("main", "currentuser:" + FuLiCenterApplication.getInstance().getUser());
					String userName = FuLiCenterApplication.getInstance().getUserName();
					UserDao dao = new UserDao(SplashActivity.this);
					UserAvatar user = dao.getUserAvatar(userName);
					Log.e(TAG, "user=" + user);
					Log.i("main", "闪屏中的UserAvatar数据：" + user);
					if (user == null) {
						reInitData(splashUserName);

					} else {
						//设置全局变量的昵称，在闪屏中获得的用户信息
//						FuLiCenterApplication.getInstance().setUserNick(user.getMUserNick());//转在登录界面去获取昵称了
						FuLiCenterApplication.getInstance().setUser(user);
						FuLiCenterApplication.currentUserNick = user.getMUserNick();
						new DownloadContactListTask(SplashActivity.this, userName).execute();

					}

					long costTime = System.currentTimeMillis() - start;
					//等待sleeptime时长
					if (sleepTime - costTime > 0) {
						try {
							Thread.sleep(sleepTime - costTime);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					//进入主页面
					startActivity(new Intent(SplashActivity.this, FuLiCenterMainActivity.class));
					finish();
				}else {
					try {
						Thread.sleep(sleepTime);
					} catch (InterruptedException e) {
					}
					startActivity(new Intent(SplashActivity.this, FuLiCenterMainActivity.class));
					finish();
				}
			}
		}).start();

	}

	private void reInitData(final String splashUserName ) {
		//不能再线程中在创建线程。
		/*LoginActivity loginActivity = new LoginActivity();
		loginActivity.loginAppServer();
		String userName1 = FuLiCenterApplication.getInstance().getUserName();
		UserDao dao1 = new UserDao(SplashActivity.this);
		UserAvatar userAvatar = dao1.getUserAvatar(userName1);
		FuLiCenterApplication.getInstance().setUser(userAvatar);
		FuLiCenterApplication.currentUserNick = userAvatar.getMUserNick();
		new DownloadContactListTask(SplashActivity.this, userName1).execute();*/
		final OkHttpUtils2<String> utils = new OkHttpUtils2<String>();
		utils.setRequestUrl(I.REQUEST_FIND_USER)
				.addParam(I.User.USER_NAME,splashUserName)
				.targetClass(String.class)
				.execute(new OkHttpUtils2.OnCompleteListener<String>() {
					@Override
					public void onSuccess(String s) {
						Log.i("main", "在闪屏中根据用户名查找用户结果：" + s);
						Result result = Utils.getResultFromJson(s, UserAvatar.class);
						Log.i("main","在闪屏再次下载中得到的UserAvatar数据："+result);
						if (result != null && result.isRetMsg()) {
							UserAvatar userAvatar = (UserAvatar) result.getRetData();
							FuLiCenterApplication.getInstance().setUser(userAvatar);
							FuLiCenterApplication.currentUserNick = userAvatar.getMUserNick();
							new DownloadContactListTask(SplashActivity.this,splashUserName).execute();
						}
					}

					@Override
					public void onError(String error) {
						Log.i("main", "查询错误信息：" + error);
					}
				});
	}

	/**
	 * 获取当前应用程序的版本号
	 */
	private String getVersion() {
		String st = getResources().getString(R.string.Version_number_is_wrong);
		PackageManager pm = getPackageManager();
		try {
			PackageInfo packinfo = pm.getPackageInfo(getPackageName(), 0);
			String version = packinfo.versionName;
			return version;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return st;
		}
	}

	public static String getUserName() {
		return FuLiCenterApplication.getInstance().getUserName();
	}

}
