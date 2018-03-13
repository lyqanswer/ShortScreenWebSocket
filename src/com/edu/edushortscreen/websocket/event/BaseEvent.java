package com.edu.edushortscreen.websocket.event;

/**
 * EventBus事件基类
 * @author lucher
 *
 */
public abstract class BaseEvent {

	//事件类型
	protected EventType type;

	/**
	 * 构造
	 * @param type
	 */
	public BaseEvent(EventType type) {
		setType(type);
	}

	public EventType getType() {
		return type;
	}

	public void setType(EventType type) {
		this.type = type;
	}

}
