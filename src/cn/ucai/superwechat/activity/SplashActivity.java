package cn.ucai.superwechat.activity;

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
import cn.ucai.superwechat.DemoHXSDKHelper;
import cn.ucai.superwechat.R;
import cn.ucai.superwechat.SuperWeChatApplication;
import cn.ucai.superwechat.bean.UserAvatar;
import cn.ucai.superwechat.db.UserDao;
import cn.ucai.superwechat.task.DownloadContactListTask;

/**
 * 开屏页
 *
 */
public class SplashActivity extends BaseActivity {
	private static final String TAG = SplashActivity.class.getSimpleName();
	private RelativeLayout rootLayout;
	private TextView versionText;
	
	private static final int sleepTime = 2000;
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
					Log.i("main", "currentuser:" + SuperWeChatApplication.getInstance().getUserName());
					Log.i("main", "currentuser:" + SuperWeChatApplication.getInstance().getUser());
					String userName = SuperWeChatApplication.getInstance().getUserName();
					UserDao dao = new UserDao(SplashActivity.this);
					UserAvatar user = dao.getUserAvatar(userName);
					Log.e(TAG, "user=" + user);
					Log.i("main", "闪屏中的UserAvatar数据：" + user);
					if (user == null) {
						reLoginAppServerAndInitData();

					} else {
						//设置全局变量的昵称，在闪屏中获得的用户信息
//						SuperWeChatApplication.getInstance().setUserNick(user.getMUserNick());//转在登录界面去获取昵称了
						SuperWeChatApplication.getInstance().setUser(user);
						SuperWeChatApplication.currentUserNick = user.getMUserNick();
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
					startActivity(new Intent(SplashActivity.this, MainActivity.class));
					finish();
				}else {
					try {
						Thread.sleep(sleepTime);
					} catch (InterruptedException e) {
					}
					startActivity(new Intent(SplashActivity.this, LoginActivity.class));
					finish();
				}
			}
		}).start();

	}

	private void reLoginAppServerAndInitData() {
		LoginActivity loginActivity = new LoginActivity();
		loginActivity.loginAppServer();
		String userName1 = SuperWeChatApplication.getInstance().getUserName();
		UserDao dao1 = new UserDao(SplashActivity.this);
		UserAvatar userAvatar = dao1.getUserAvatar(userName1);
		SuperWeChatApplication.getInstance().setUser(userAvatar);
		SuperWeChatApplication.currentUserNick = userAvatar.getMUserNick();
		new DownloadContactListTask(SplashActivity.this, userName1).execute();
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
		return SuperWeChatApplication.getInstance().getUserName();
	}

}
