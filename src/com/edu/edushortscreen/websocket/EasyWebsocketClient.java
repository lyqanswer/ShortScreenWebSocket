package com.edu.edushortscreen.websocket;

import java.net.URI;

import org.greenrobot.eventbus.EventBus;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.framing.Framedata;
import org.java_websocket.handshake.ServerHandshake;

import com.edu.edushortscreen.util.NetworkUtil;
import com.edu.edushortscreen.websocket.event.CloseRespEvent;
import com.edu.edushortscreen.websocket.event.EventType;
import com.edu.edushortscreen.websocket.event.FramedataEvent;
import com.edu.edushortscreen.websocket.event.MessageRespEvent;
import com.edu.edushortscreen.websocket.event.SimpleRespEvent;

import android.content.Context;
import android.util.Log;

/**
 * websocket客户端
 * 
 * @author lucher
 *
 */
public class EasyWebsocketClient extends WebSocketClient {

	private static final String TAG = EasyWebsocketClient.class.getSimpleName();

	// 当前连接状态
	private static Status mStatus = Status.INIT;

	// 自身单例
	private static EasyWebsocketClient mSingleton;
	// 连接配置类
	private ConnConfig mConfig;

	// 超时线程
	private TimeOutThread timeOutThread;
	private Context mContext;

	/**
	 * 单例模式获取实例
	 * 
	 * @param serverUri
	 *            服务端地址,如:ws://192.168.1.144:8080/WebsocketServer/websocket/
	 * @param context
	 * @return
	 */
	public static EasyWebsocketClient getSingleTon(Context context, URI serverUri) {
		if (mSingleton == null) {
			mSingleton = new EasyWebsocketClient(context, serverUri, new Draft_17());
		}
		return mSingleton;
	}

	private EasyWebsocketClient(Context context, URI serverUri, Draft draft) {
		super(serverUri, draft);
		mContext = context;
		mConfig = new ConnConfig();
	}

	/**
	 * 获取连接配置
	 * 
	 * @return
	 */
	public ConnConfig getConfig() {
		return mConfig;
	}

	/**
	 * 设置网络连接配置
	 * 
	 * @param config
	 * @return
	 */
	public void setConfig(ConnConfig config) {
		mConfig = config;
	}

	/**
	 * 获取当前状态
	 * 
	 * @return
	 */
	public Status getStatus() {
		return mStatus;
	}

	@Override
	public void connect() {
		if (!NetworkUtil.checkNetwork(mContext, mConfig.isShowSettings(), mConfig.isSettingsCancelable(), mConfig.isSettingsTouchOutside())) {// 检测网络连接
			return;
		}
		onConnecting();
		super.connect();
	}

	@Override
	public boolean connectBlocking() throws InterruptedException {
		if (!NetworkUtil.checkNetwork(mContext, mConfig.isShowSettings(), mConfig.isSettingsCancelable(), mConfig.isSettingsTouchOutside())) {// 检测网络连接
			return false;
		}
		onConnecting();
		return super.connectBlocking();
	}

	@Override
	public void close() {
		onDisconnecting();
		super.close();
	}

	@Override
	public void close(int code) {
		onDisconnecting();
		super.close(code);
	}

	@Override
	public void close(int code, String message) {
		onDisconnecting();
		super.close(code, message);
	}

	@Override
	public void closeBlocking() throws InterruptedException {
		onDisconnecting();
		super.closeBlocking();
	}

	@Override
	public void closeConnection(int code, String message) {
		onDisconnecting();
		super.closeConnection(code, message);
	}

	/**
	 * 连接中
	 */
	public void onConnecting() {
		mStatus = Status.CONNECTING;
		timeOutThread = new TimeOutThread();// 开启超时任务线程
		timeOutThread.start();
		EventBus.getDefault().post(new SimpleRespEvent(EventType.CONNECTING));
	}

	/**
	 * 断开连接中
	 */
	public void onDisconnecting() {
		mStatus = Status.DISCONNECTING;
		EventBus.getDefault().post(new SimpleRespEvent(EventType.DISCONNECTING));
	}

	@Override
	public void onOpen(ServerHandshake handshakedata) {
		Log.d(TAG, "建立连接:" + handshakedata);
		timeOutThread.cancel();
		mStatus = Status.CONNECTED;
		EventBus.getDefault().post(new SimpleRespEvent(EventType.OPEN));
	}

	@Override
	public void onMessage(String message) {
		Log.d(TAG, "接收到消息:" + message);
		EventBus.getDefault().post(new MessageRespEvent(EventType.MESSAGE).setMessage(message));
	}

	@Override
	public void onFragment(Framedata fragment) {
		Log.d(TAG, "接收到片段:" + new String(fragment.getPayloadData().array()));
		EventBus.getDefault().post(new FramedataEvent(EventType.FRAGMENT).setFrameData(fragment));
	}

	@Override
	public void onClose(int code, String reason, boolean remote) {
		Log.d(TAG, "连接关闭:" + code + "," + reason + "," + remote);
		if (mStatus != Status.TIMEOUT) {
			mSingleton = null;
			mStatus = Status.DISCONNECTED;
			timeOutThread.cancel();
			EventBus.getDefault().post(new CloseRespEvent(EventType.CLOSE).setCode(code).setReason(reason).setRemote(remote));
		}
	}

	@Override
	public void onError(Exception ex) {
		if (mStatus != Status.TIMEOUT) {
			Log.d(TAG, "连接错误:" + ex.getMessage());
			ex.printStackTrace();
			mStatus = Status.ERROR;
			timeOutThread.cancel();
			EventBus.getDefault().post(new SimpleRespEvent(EventType.ERROR));
		}
	}

	/**
	 * 连接超时检测线程
	 * 
	 * @author lucher
	 *
	 */
	public class TimeOutThread extends Thread {

		// 是否取消
		private boolean cancel;

		@Override
		public synchronized void run() {
			try {
				wait(mConfig.getConnectTimeout());
				if (!cancel) {
					close();
					mSingleton = null;
					mStatus = Status.TIMEOUT;
					EventBus.getDefault().post(new SimpleRespEvent(EventType.TIMEOUT));
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		/**
		 * 取消
		 */
		public void cancel() {
			cancel = true;
		}
	}
}
