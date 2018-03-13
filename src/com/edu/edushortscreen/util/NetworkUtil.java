package com.edu.edushortscreen.util;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;

/**
 * 网络工具类
 * 
 * @author lucher
 * 
 */
public class NetworkUtil {

	private static String ip;
	private static AlertDialog dialog;

	/**
	 * 检查网络状况
	 * 
	 * @param context
	 * @param showConnectDialog
	 * 			  如果网络不可用,是否显示网络设置对话框
	 * @param cancelable
	 *            网络设置对话框是否可取消
	 * @param touchOutside
	 * 			  点击外面是否可关闭对话框
	 * @return wifi是否连接
	 */
	public static boolean checkNetwork(final Context context, boolean showConnectDialog, boolean cancelable, boolean touchOutside) {
		if (!isNetworkAvailable(context)) {
			if (Looper.getMainLooper() != Looper.myLooper() || !showConnectDialog) {//如果当前非主线程或者不需要显示对话框
				return false;
			}
			showAlertDialog(context, "网络设置提示", "当前网络连接不可用,是否进行设置?", "设置", new Runnable() {
				@Override
				public void run() {
					context.startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
				}
			}, null, null, cancelable, touchOutside);
			return false;
		}

		return true;
	}

	/**
	 * 检查网络状况,对话框不能取消
	 * 
	 * @param context
	 * @param cancelable
	 *            是否可取消
	 * @return wifi是否连接
	 */
	public static boolean checkNetwork(final Context context, boolean cancelable) {
		if (!isNetworkAvailable(context)) {
			if (Looper.getMainLooper() != Looper.myLooper())
				return false;
			showAlertDialog(context, "网络设置提示", "当前网络连接不可用,是否进行设置?", "设置", new Runnable() {
				@Override
				public void run() {
					context.startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
				}
			}, null, null, cancelable, false);
			return false;
		}

		return true;
	}

	/**
	 * 检查wifi是否打开
	 * 
	 * @return
	 */
	public static boolean isWifiConnected(Context context) {
		ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mWiFiNetworkInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (mWiFiNetworkInfo != null) {
			return mWiFiNetworkInfo.isAvailable();
		}
		return false;
	}

	/**
	 * 显示开启wifi的对话框
	 * 
	 * @param context
	 * @param title
	 * @param message
	 * @param btn1
	 * @param btn1Run
	 * @param btn2
	 * @param btn2Run
	 * @param cancelable
	 * @param touchOutside
	 */
	public static void showAlertDialog(Context context, String title, String message, String btn1, final Runnable btn1Run, String btn2, final Runnable btn2Run, boolean cancelable, boolean touchOutside) {
		try {
			Builder builder = new AlertDialog.Builder(context).setIcon(android.R.drawable.ic_dialog_alert).setTitle(title).setMessage(message)
					.setPositiveButton(btn1, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if (btn1Run != null) {
								new Handler().post(btn1Run);
							}
						}
					});
			if (btn2 != null) {
				builder.setNegativeButton(btn2, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (btn2Run != null) {
							new Handler().post(btn2Run);
						}
					}
				});
			}
			dialog = builder.create();
			dialog.setCancelable(cancelable);
			dialog.setCanceledOnTouchOutside(touchOutside);
			dialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 检查是否有网络
	 * */
	public static boolean isNetworkAvailable(Context context) {
		NetworkInfo info = getNetworkInfo(context);
		if (info != null) {
			return info.isAvailable();
		}
		return false;
	}

	/**
	 * 检查是否是WIFI
	 * */
	public static boolean isWifi(Context context) {
		NetworkInfo info = getNetworkInfo(context);
		if (info != null) {
			if (info.getType() == ConnectivityManager.TYPE_WIFI)
				return true;
		}
		return false;
	}

	/**
	 * 检查是否是移动网络
	 * */
	public static boolean isMobile(Context context) {
		NetworkInfo info = getNetworkInfo(context);
		if (info != null) {
			if (info.getType() == ConnectivityManager.TYPE_MOBILE)
				return true;
		}
		return false;
	}

	/**
	 * 获取ip地址
	 * 
	 * @param context
	 * @return
	 */
	public static String getIpAdress(Context context) {
		if (ip != null)
			return ip;
		WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		// 检查Wifi状态
		if (!wm.isWifiEnabled())
			wm.setWifiEnabled(true);
		WifiInfo wi = wm.getConnectionInfo();
		// 获取32位整型IP地址
		int ipAdd = wi.getIpAddress();
		// 把整型地址转换成“*.*.*.*”地址
		ip = intToIp(ipAdd);
		return ip;
	}

	/**
	 * 转换为ip格式
	 * 
	 * @param i
	 * @return
	 */
	private static String intToIp(int i) {
		return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF) + "." + (i >> 24 & 0xFF);
	}

	/**
	 * 获取网络信息
	 * 
	 * @param context
	 * @return
	 */
	private static NetworkInfo getNetworkInfo(Context context) {

		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		return cm.getActiveNetworkInfo();
	}
}
