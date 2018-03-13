package com.edu.edushortscreen;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.java_websocket.WebSocket;
import org.java_websocket.WebSocketAdapter;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class TestActivity extends Activity {
	private WebSocketClient mWebSocketClient;
	// 注意更改成为你的服务器地址，格式："ws://ip:port/项目名字/websocket入口地址
	private String address = "ws://funsoft.com.cn:8080/hello";
	private TextView tv;
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what == 0x111) {
				String news = "";
				news = msg.getData().getString("news");
				Toast.makeText(TestActivity.this, "收到消息:" + news, Toast.LENGTH_SHORT).show();
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_text);
		tv = (TextView) findViewById(R.id.tv);
		tv.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					initSocketClient();
				} catch (URISyntaxException e) {
					e.printStackTrace();
				}
			}
		});

	}

	private void initSocketClient() throws URISyntaxException {
		if (mWebSocketClient == null) {
			mWebSocketClient = new WebSocketClient(new URI(address)) {
				@Override
				public void onOpen(ServerHandshake serverHandshake) {
					// 连接成功
					Log.i("LOG", "opened connection");
				}

				@Override
				public void onMessage(String s) {
					// 服务端消息来了
					Log.i("LOG", "received:" + s);
					Message msg = Message.obtain();
					msg.what = 0x111;
					Bundle bundle = new Bundle();
					bundle.putString("news", s);
					msg.setData(bundle);
					handler.sendMessage(msg);
				}

				@Override
				public void onClose(int i, String s, boolean remote) {
					// 连接断开，remote判定是客户端断开还是服务端断开
					Log.i("LOG", "Connection closed by " + (remote ? "remote peer" : "us") + ", info=" + s);
					//
					closeConnect();
				}

				@Override
				public void onError(Exception e) {
					Log.i("LOG", "error:" + e);
				}
			};
		}
	}

	// 断开连接
	private void closeConnect() {
		try {
			mWebSocketClient.close();
			mWebSocketClient.connect();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			mWebSocketClient = null;
		}
	}

	// 向服务器发送消息的方法
	private void sendMsg(String msg) {
		mWebSocketClient.send(msg);
	}

}
