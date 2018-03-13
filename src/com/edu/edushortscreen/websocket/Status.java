package com.edu.edushortscreen.websocket;

/**
 * websocket连接状态
 * @author lucher
 *
 */
public enum Status {
	/**
	 * 初始状态
	 */
	INIT,
	/**
	 * 连接中
	 */
	CONNECTING,
	/**
	 * 已连接
	 */
	CONNECTED,
	/**
	 * 断开中
	 */
	DISCONNECTING,
	/**
	 * 已断开
	 */
	DISCONNECTED,
	/**
	 * 连接错误
	 */
	ERROR,
	/**
	 * 连接超时
	 */
	TIMEOUT
}
