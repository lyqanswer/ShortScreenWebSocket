package com.edu.edushortscreen.websocket;

/**
 * 请求配置类
 * @author lucher
 *
 */
public class ConnConfig {
	
	//默认超时时间
	private long timeout = 15 * 1000;

	//是否显示网络设置对话框
	private boolean showSettings;
	//网络设置对话框是否可取消
	private boolean settingsCancelable;
	//网络设置对话框是否可点击外面取消
	private boolean settingsTouchOutside;

	//连接超时
	private long connectTimeout;

	public ConnConfig() {
		connectTimeout(timeout);
		showSettings(true);
		settingsCancelable(false);
		settingsTouchOutside(false);
	}

	public boolean isShowSettings() {
		return showSettings;
	}

	/**是否显示网络设置对话框
	 * @param showSettings
	 * @return
	 */
	public ConnConfig showSettings(boolean showSettings) {
		this.showSettings = showSettings;
		return this;
	}

	public boolean isSettingsCancelable() {
		return settingsCancelable;
	}

	/**网络设置对话框是否可取消
	 * @param settingsCancelable
	 * @return
	 */
	public ConnConfig settingsCancelable(boolean settingsCancelable) {
		this.settingsCancelable = settingsCancelable;
		return this;
	}

	public long getConnectTimeout() {
		return connectTimeout;
	}

	/**连接超时
	 * @param connectTimeout
	 * @return
	 */
	public ConnConfig connectTimeout(long connectTimeout) {
		this.connectTimeout = connectTimeout;
		return this;
	}

	public boolean isSettingsTouchOutside() {
		return settingsTouchOutside;
	}

	/**网络设置对话框是否可点击外面取消
	 * @param settingsTouchOutside
	 * @return
	 */
	public ConnConfig settingsTouchOutside(boolean settingsTouchOutside) {
		this.settingsTouchOutside = settingsTouchOutside;
		return this;
	}

}
