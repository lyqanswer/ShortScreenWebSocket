package com.edu.edushortscreen.service;

import org.greenrobot.eventbus.EventBus;

import com.edu.edushortscreen.util.EventMessage;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.hardware.display.DisplayManager;
import android.os.IBinder;
import android.os.Looper;
import android.os.PowerManager;
import android.view.Display;

/**
 * 爱丁核心服务，主要包括：用户行为数据上传，应用系统更新数据上传，白名单更新等功能
 * 
 * @author lucher
 * 
 */
public class EduCoreService extends Service {

	private static final String TAG = "EduCoreServiceDemo";

	// 任务执行周期，单位ms
	private static final int UPDATE_TIME = 2 * 60 * 1000;
	// 任务的线程
	private CoreTaskThread mTaskThread;
	private Context mContext;
	// 是否运行
	private boolean running;
	// 屏幕是否点亮
	private boolean screenOn;

	@Override
	public void onCreate() {
		super.onCreate();

		/* 注册屏幕唤醒时的广播 */
		IntentFilter mscreenOnOnFilter = new IntentFilter("android.intent.action.SCREEN_ON");
		EduCoreService.this.registerReceiver(mscreenOnOReceiver, mscreenOnOnFilter);

		/* 注册机器锁屏时的广播 */
		IntentFilter mscreenOnOffFilter = new IntentFilter("android.intent.action.SCREEN_OFF");
		EduCoreService.this.registerReceiver(mscreenOnOReceiver, mscreenOnOffFilter);

		System.out.println(TAG + "--service oncreate..");
		mContext = this.getApplicationContext();

		startTask();
	}

	/**
	 * 锁屏的管理类叫KeyguardManager，
	 * 通过调用其内部类KeyguardLockmKeyguardLock的对象的disableKeyguard方法可以取消系统锁屏，
	 * newKeyguardLock的参数用于标识是谁隐藏了系统锁屏
	 */
	private BroadcastReceiver mscreenOnOReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();

			if (action.equals("android.intent.action.SCREEN_ON")) {
				System.out.println(TAG + "—— screen_ON ——");
				startTask();
				screenOn = true;
			} else if (action.equals("android.intent.action.SCREEN_OFF")) {
				System.out.println(TAG + "—— screen_OFF ——");
				stopTask();
				screenOn = false;
			}
		}

	};

	/**
	 * 开启任务线程
	 */
	private void startTask() {
		// 创建并开启线程UpdateThread
		mTaskThread = new CoreTaskThread();
		if (isScreenOn()) {
			running = true;
			mTaskThread.start();
			System.out.println(TAG + "—— 启动 ——" + running);
		} else {
			running = false;
			System.out.println(TAG + "—— 关闭 ——" + running);
		}

	}

	@Override
	public void onDestroy() {
		System.out.println(TAG + "--service destroy.....");
		super.onDestroy();
		if (screenOn) {
			Intent localIntent = new Intent();
			localIntent.setClass(this, EduCoreService.class); // 销毁时重新启动Service
			this.startService(localIntent);
		}

	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		System.out.println(TAG + "--onStartCommand =  " + startId);
		super.onStartCommand(intent, flags, startId);
		if (mTaskThread == null || !mTaskThread.isAlive() && screenOn) {
			startTask();
		}
		// 打开进程杀手
		mContext.sendBroadcast(new Intent("com.edu.action.START_KILLER"));
		checkEduKiller();

		return START_STICKY;
	}

	/**
	 * 执行任务的线程
	 * 
	 * @author lucher
	 * 
	 */
	private class CoreTaskThread extends Thread {

		@Override
		public void run() {
			try {
				while (running) {
					System.out.println(TAG + "---CoreTaskThread..." + running);
					if (Looper.myLooper() == null) {
						Looper.prepare();
					}
					EventBus.getDefault().post(new EventMessage());
					System.out.println(TAG + "---EventBus..." + running);
					Thread.sleep(UPDATE_TIME);

				}
			} catch (InterruptedException e) {
				e.printStackTrace();
				if (screenOn) {
					startTask();
					System.out.println(TAG + "---InterruptedException..." + running);
				}
			}
		}
	}

	// 停止轮询
	private void stopTask() {
		if (mTaskThread != null && mTaskThread.isAlive()) {
			mTaskThread.interrupt();
			running = false;
			System.out.println(TAG + "--- stopTask..." + running);
		}
	}

	/**
	 * 检测是否存在进程杀手，不存在则隐藏状态栏
	 */
	private void checkEduKiller() {
		PackageInfo packageInfo;
		try {
			packageInfo = mContext.getPackageManager().getPackageInfo("com.android.coreprocess4edu", 0);
		} catch (NameNotFoundException e) {
			packageInfo = null;
			e.printStackTrace();
		}
		if (packageInfo == null) {
			// 隐藏状态栏，需要系统版本为V2.2.1以上
			mContext.sendBroadcast(new Intent("com.edu.hide.systemui"));
		}
	}

	// 判断当前屏幕状态
	private boolean isScreenOn() {
		if (android.os.Build.VERSION.SDK_INT >= 20) {
			DisplayManager dm = (DisplayManager) getSystemService(Context.DISPLAY_SERVICE);
			Display[] displays = dm.getDisplays();
			for (Display display : displays) {
				if (display.getState() == Display.STATE_ON || display.getState() == Display.STATE_UNKNOWN) {
					return true;
				}
			}
			return false;
		}

		PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
		if (powerManager.isScreenOn()) {
			return true;
		}
		return false;
	}
}
