package com.edu.edushortscreen.websocket.event;

/**
 * 网络请求消息响应事件封装
 * @author lucher
 *
 */
public class MessageRespEvent extends BaseEvent {

	//消息内容
	private String message;

	public MessageRespEvent(EventType type) {
		super(type);
	}

	public String getMessage() {
		return message;
	}

	public MessageRespEvent setMessage(String message) {
		this.message = message;
		return this;
	}

}
