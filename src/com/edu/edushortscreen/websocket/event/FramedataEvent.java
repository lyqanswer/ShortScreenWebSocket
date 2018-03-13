package com.edu.edushortscreen.websocket.event;

import org.java_websocket.framing.Framedata;

/**
 * 网络请求片段响应事件封装
 * @author lucher
 *
 */
public class FramedataEvent extends BaseEvent {

	private Framedata frameData;

	public FramedataEvent(EventType type) {
		super(type);
	}

	public Framedata getFrameData() {
		return frameData;
	}

	public FramedataEvent setFrameData(Framedata frameData) {
		this.frameData = frameData;
		return this;
	}

}
