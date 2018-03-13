package com.edu.edushortscreen.net;

import com.lucher.net.req.config.ReqConfig;

/**
 * 网络请求配置类
 *
 * @author lucher
 */
public class ReqConfiger {

	/**
	 * 获取默认的网络请求配置
	 *
	 * @return
	 */
	public static ReqConfig getDefaultConfig() {
		ReqConfig config = new ReqConfig();
		config.connectTimeout(10 * 1000).respTimeout(10 * 1000).retryTimeout(10 * 1000).retryTimeout(3)// 设置超时类参数
				.showSettings(true).settingsCancelable(true).settingsTouchOutside(true)// 设置网络设置对话框类参数
				.showProgress(true).progressTitle("自定义").progressCancelable(true).progressTouchOutside(true);// 设置加载对话框类参数

		return config;
	}
}
