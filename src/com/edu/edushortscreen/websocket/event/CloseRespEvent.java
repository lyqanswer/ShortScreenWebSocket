package com.edu.edushortscreen.websocket.event;

/**
 * 网络请求关闭响应事件封装
 * @author lucher
 *
 */
public class CloseRespEvent extends BaseEvent {
	//错误代码
	private int code;
	//错误原因
	private String reason;
	//是否远程
	private boolean remote;

	public CloseRespEvent(EventType type) {
		super(type);
	}

	public int getCode() {
		return code;
	}

	public CloseRespEvent setCode(int code) {
		this.code = code;
		return this;
	}

	public String getReason() {
		return reason;
	}

	public CloseRespEvent setReason(String reason) {
		this.reason = reason;
		return this;
	}

	public boolean isRemote() {
		return remote;
	}

	public CloseRespEvent setRemote(boolean remote) {
		this.remote = remote;
		return this;
	}

}
