package com.edu.edushortscreen.websocket.event;

/**
 * EventBus事件类别定义
 * @author lucher
 *
 */
public enum EventType {
	/**
	 * 连接中
	 */
	CONNECTING,
	/**
	 * 断开中
	 */
	DISCONNECTING,
	/**
	 * 建立连接事件
	 */
	OPEN, 
	/**
	 * 断开连接事件
	 */
	CLOSE,
	/**
	 * 消息事件
	 */
	MESSAGE,
	/**
	 * 片段
	 */
	FRAGMENT,
	/**
	 * 错误
	 */
	ERROR,
	/**
	 * 超时事件
	 */
	TIMEOUT
}
