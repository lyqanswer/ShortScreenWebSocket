package com.edu.edushortscreen;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.Header;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import com.edu.edushortscreen.net.UploadUtil;
import com.edu.edushortscreen.net.UploadUtil.OnUploadProcessListener;
import com.edu.edushortscreen.service.EduCoreService;
import com.edu.edushortscreen.util.EventMessage;
import com.edu.edushortscreen.util.ShellUtils;
import com.edu.edushortscreen.util.ToastUtil;
import com.edu.edushortscreen.websocket.EasyWebsocketClient;
import com.edu.edushortscreen.websocket.Status;
import com.edu.edushortscreen.websocket.event.BaseEvent;
import com.edu.edushortscreen.websocket.event.CloseRespEvent;
import com.edu.edushortscreen.websocket.event.FramedataEvent;
import com.edu.edushortscreen.websocket.event.MessageRespEvent;
import com.edu.library.usercenter.UserCenterHelper;
import com.loopj.android.http.RequestParams;
import com.lucher.net.req.RequestMethod;
import com.lucher.net.req.impl.SimpleParamsReqEntity;
import com.lucher.net.req.impl.UploadNetReqManager;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener, OnUploadProcessListener {
	private String savePath;
	// 连接按钮
	private Button btnConnect, btnSend;
	// websocket客户端
	private EasyWebsocketClient mClient;
	// private TextView tvContent;
	private EditText etUrl, etInfo;
	private static String requestURL = "http://192.168.1.172/adzhbp/index.php/test/index";// 上传图片接口
	private ImageView imgView;
	private Button btnShortPic;
	private Button btnUploadPic;
	private Button btnJump;
	private TextView websocketInfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		EventBus.getDefault().register(this);

		initView();
		initData();

		String SERVER_URL = UserCenterHelper.getServerUrl(this, "");
		Log.e("SERVER_URL", SERVER_URL);

	}

	private void initView() {
		btnConnect = (Button) findViewById(R.id.btnConnect);
		btnSend = (Button) findViewById(R.id.btnSend);
		btnConnect.setOnClickListener(this);
		btnSend.setOnClickListener(this);
		// tvContent = (TextView) findViewById(R.id.websocket_info);
		etUrl = (EditText) findViewById(R.id.et_url);
		etInfo = (EditText) findViewById(R.id.et_info);
		imgView = (ImageView) findViewById(R.id.img);
		websocketInfo = (TextView) findViewById(R.id.websocket_info);

		btnShortPic = (Button) findViewById(R.id.short_pic);
		btnUploadPic = (Button) findViewById(R.id.send_pic);
		btnJump = (Button) findViewById(R.id.btn_jump);
		btnShortPic.setOnClickListener(this);
		btnUploadPic.setOnClickListener(this);
		btnJump.setOnClickListener(this);

	}

	private void initData() {
		String sdCardPath = Environment.getExternalStorageDirectory().getPath();
		// 图片文件路径
		savePath = sdCardPath + File.separator + "shortScreen.png";
		startService(new Intent(MainActivity.this, EduCoreService.class));
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
	}

	/**
	 * 处理截屏
	 * 
	 * @param event
	 */
	@Subscribe(threadMode = ThreadMode.MAIN)
	public void handleEvent(EventMessage event) {
		// if (hasSdcard()) {
		// String sImg;
		// byte[] srtbyte = null;
		// ShellUtils.execCommand("/system/bin/screencap -p " + savePath, true);
		// Toast.makeText(MainActivity.this, "截图成功", Toast.LENGTH_SHORT).show();
		// try {
		// // sImg = String.valueOf(readStream(savePath));
		// srtbyte = readStream(savePath);
		// Log.e("图片 77777", "111" + srtbyte);
		// } catch (Exception e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		//
		// imgView.setImageBitmap(getBitmapFromByte(srtbyte));
		// }
	}

	/**
	 * 判断sdcard是否存在
	 */
	private boolean hasSdcard() {
		// 判断Sdcard是否可用
		if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
			return true;
		} else
			return false;
	}

	/**
	 * 处理连接事件
	 */
	private void handleConnect() {
		if (mClient == null) {// 未连接
			connect();
		} else if (mClient.getStatus() == Status.CONNECTED) {// 已连接,则断开
			disconnect();
		} else if (mClient.getStatus() == Status.CONNECTING) {// 连接中,不做处理
		} else {// 其他状态,连接服务端
			connect();
		}
	}

	/**
	 * 连接
	 */
	private void connect() {
		// String url = Constant.WEBSOCKET_URL;
		String url = etUrl.getText().toString();
		// 为空判断
		if (TextUtils.isEmpty(url)) {
			ToastUtil.showToast(this, "地址不能为空");
			return;
		}
		// 连接任务开始
		try {
			mClient = EasyWebsocketClient.getSingleTon(this, new URI(url));
			mClient.setConfig(mClient.getConfig().settingsCancelable(true).connectTimeout(10 * 1000));// 超时时间10秒
			mClient.connect();
		} catch (URISyntaxException e) {
			e.printStackTrace();
			ToastUtil.showToast(this, "服务器地址错误");
		}
	}

	/**
	 * 断开
	 */
	private void disconnect() {
		if (mClient != null) {
			mClient.close();
		}
	}

	/**
	 * 发送消息
	 */
	private void sendMessage() {
		if (mClient == null || mClient.getStatus() != Status.CONNECTED) {
			ToastUtil.showToast(this, "当前是未连接状态,不能发送消息");
			return;
		}
		if (TextUtils.isEmpty(etInfo.getText().toString())) {
			ToastUtil.showToast(this, "无发送消息");
			return;
		}
		mClient.send(etInfo.getText().toString());
		Log.e("MainActivity.this", "" + "sendMessage");
		// mClient.send("msg");
	}

	/**
	 * 新增内容
	 */
	private void appendContent(String content) {
		websocketInfo.setText("当前内容：" + content);
		ToastUtil.showToast(MainActivity.this, "" + content);
		Log.e("MainActivity.this", "当前" + content);

	}

	/**
	 * 处理websocket响应事件
	 * 
	 * @param event
	 */
	@Subscribe(threadMode = ThreadMode.MAIN)
	public void handleEvent(BaseEvent event) {
		switch (event.getType()) {
		case CONNECTING:
			ToastUtil.showToast(this, "建立连接");
			refreshStatus();

			break;
		case OPEN:
			ToastUtil.showToast(this, "已建立连接");
			refreshStatus();
			sendMessage();
			Log.d("OPEN", "已建立连接:" + "OPEN");
			break;
		case DISCONNECTING:
			ToastUtil.showToast(this, "断开中");
			refreshStatus();

			break;
		case CLOSE:
			String reason = ((CloseRespEvent) event).getReason();
			ToastUtil.showToast(this, "连接断开:" + reason);
			refreshStatus();

			break;
		case ERROR:
			ToastUtil.showToast(this, "连接错误");
			refreshStatus();

			break;
		case TIMEOUT:
			ToastUtil.showToast(this, "连接超时");
			refreshStatus();

			break;
		case MESSAGE:
			String msg = ((MessageRespEvent) event).getMessage();
			appendContent(msg);
			Log.d("OPEN", "已建立连接:" + "MESSAGE");
			break;
		case FRAGMENT:
			String data = new String(((FramedataEvent) event).getFrameData().getPayloadData().array());
			appendContent(data);
			Log.d("OPEN", "已建立连接:" + "FRAGMENT");
			break;
		default:
			break;
		}

	}

	/**
	 * 刷新当前连接状态
	 */
	private void refreshStatus() {
		btnConnect.setEnabled(true);
		btnConnect.setText("连接");
		switch (mClient.getStatus()) {
		case INIT:
			ToastUtil.showToast(this, "初始状态");

			break;
		case CONNECTING:
			ToastUtil.showToast(this, "连接中...");
			btnConnect.setText("连接中...");
			btnConnect.setEnabled(false);

			break;
		case CONNECTED:
			ToastUtil.showToast(this, "已连接");
			btnConnect.setText("断开");

			break;
		case DISCONNECTING:
			ToastUtil.showToast(this, "断开中...");
			btnConnect.setText("断开中...");
			// btnConnect.setEnabled(false);

			break;
		case DISCONNECTED:
			ToastUtil.showToast(this, "已断开");
			if (mClient != null) {
				mClient.close();
			}
			break;

		case TIMEOUT:
			ToastUtil.showToast(this, "连接超时");
			if (mClient != null) {
				mClient.close();
			}
			break;
		case ERROR:
			ToastUtil.showToast(this, "连接错误");
			if (mClient != null) {
				mClient.close();
			}
			break;

		default:
			break;
		}
	}

	/**
	 * 点击
	 * 
	 * @param view
	 */
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.btnConnect:
			handleConnect();

			break;
		case R.id.btnSend:
			sendMessage();

			break;

		case R.id.short_pic:
			ShellUtils.execCommand("/system/bin/screencap -p " + savePath, true);
			Toast.makeText(MainActivity.this, "截图成功", Toast.LENGTH_SHORT).show();
			break;

		case R.id.send_pic:
			// byte[] srtbyte = null;
			// try {
			// srtbyte = readStream(savePath);
			// requestParams3(srtbyte);
			// Log.e("图片 77777", "111" + srtbyte);
			// } catch (Exception e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			// toUploadFile();
			break;

		case R.id.btn_jump:
			Intent intent = new Intent(MainActivity.this, UploadPicActivity.class);
			startActivity(intent);
			break;

		default:
			break;
		}
	}

	// 上传图片---2
	private void toUploadFile() {
		String fileKey = "pic";
		UploadUtil uploadUtil = UploadUtil.getInstance();
		uploadUtil.setOnUploadProcessListener(this); // 设置监听器监听上传状态

		Map<String, String> params = new HashMap<String, String>();
		params.put("mac", "e0b04a2162685039c4ecb52201e87d35");
		uploadUtil.uploadFile(savePath, fileKey, requestURL, params);
	}

	@Override
	public void onUploadDone(int responseCode, String message) {
		// ToastUtil.showToast(MainActivity.this, "" + message);
		Log.e("MainActivity.this", "" + message);
	}

	@Override
	public void onUploadProcess(int uploadSize) {
		// ToastUtil.showToast(MainActivity.this, "" + uploadSize);
		Log.e("MainActivity.this", "" + uploadSize);
	}

	@Override
	public void initUpload(int fileSize) {
		// ToastUtil.showToast(MainActivity.this, "" + fileSize);
		Log.e("MainActivity.this", "" + fileSize);

	}

	public static byte[] readStream(String imagepath) throws Exception {
		FileInputStream fs = new FileInputStream(imagepath);
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		while (-1 != (len = fs.read(buffer))) {
			outStream.write(buffer, 0, len);
		}
		outStream.close();
		fs.close();
		return outStream.toByteArray();
	}

	public Bitmap getBitmapFromByte(byte[] temp) {
		if (temp != null) {
			Bitmap bitmap = BitmapFactory.decodeByteArray(temp, 0, temp.length);
			return bitmap;
		} else {
			return null;
		}
	}

	/**
	 * Json参数-发送json请求----1
	 */
	private void requestParams3(byte[] buffer) throws FileNotFoundException {
		SimpleParamsReqEntity entity = new SimpleParamsReqEntity(MainActivity.this, RequestMethod.POST, "http://192.168.1.172/adzhbp/index.php/test/index");
		RequestParams params = new RequestParams();
		final String contentType = RequestParams.APPLICATION_OCTET_STREAM;
		// 添加字节数组用于上传
		params.put("file", new ByteArrayInputStream(buffer), contentType, ".shortScreen.png");
		params.put("mac", "e0b04a2162685039c4ecb52201e87d35");
		params.setHttpEntityIsRepeatable(true);
		params.setUseJsonStreamer(false);
		entity.setRequestParams(params);

		new UploadNetReqManager() {

			@Override
			public void onConnectionFailure(String errorInfo, Header[] headers) {
				ToastUtil.showToast(mContext, "文件上传失败" + errorInfo);
			}

			@Override
			public void onConnectionError(String errorInfo) {
				ToastUtil.showToast(mContext, "文件上传出错" + errorInfo);
			}

			@Override
			public void onConnectionSuccess(byte[] responseBody, Header[] headers) {
				ToastUtil.showToast(mContext, "文件上传成功------" + new String(responseBody));
			}

			@Override
			public void onConnectionProgress(long bytesWritten, long totalSize) {
				ToastUtil.showToast(mContext, "文件上传进度更新：" + bytesWritten + "/" + totalSize);
			}
		}.sendRequest(entity, "文件上传中,请稍等...");
	}

}
