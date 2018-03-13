package com.edu.edushortscreen.util;

import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Reader;

import android.content.Context;
import android.net.wifi.WifiManager;

public class MacAddressUtil {

	/** 
	* 获取手机的MAC地址,直接读取文件
	*  
	* @return 
	*/
	public static String getMac() {
		String str = "";
		String macSerial = "";
		try {
			Process pp = Runtime.getRuntime().exec("cat /sys/class/net/wlan0/address");
			InputStreamReader ir = new InputStreamReader(pp.getInputStream());
			LineNumberReader input = new LineNumberReader(ir);

			for (; null != str;) {
				str = input.readLine();
				if (str != null) {
					macSerial = str.trim();// 去空格  
					break;
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		if (macSerial == null || "".equals(macSerial)) {
			try {
				return loadFileAsString("/sys/class/net/eth0/address").toUpperCase().substring(0, 17);
			} catch (Exception e) {
				e.printStackTrace();

			}

		}
		return macSerial;
	}

	public static String loadFileAsString(String fileName) throws Exception {
		FileReader reader = new FileReader(fileName);
		String text = loadReaderAsString(reader);
		reader.close();
		return text;
	}

	public static String loadReaderAsString(Reader reader) throws Exception {
		StringBuilder builder = new StringBuilder();
		char[] buffer = new char[4096];
		int readLength = reader.read(buffer);
		while (readLength >= 0) {
			builder.append(buffer, 0, readLength);
			readLength = reader.read(buffer);
		}
		return builder.toString();
	}

	/**
	 * 通过wifimanager获取，只有打开wifi时才能获取到
	 * 注: [置顶] Android6.0系统获getMacAddress（）取Wifi和蓝牙Mac地址返回02:00:00:00:00:00
	 * 参考文献:http://blog.csdn.net/jia635/article/details/51899919
	 * http://blog.csdn.net/wwdlss/article/details/52918682
	 * 
	 * @param context
	 * @return
	 */
	@Deprecated
	public static String getMac(Context context) {
		WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		String macAddress = wifiManager.getConnectionInfo().getMacAddress();
		return macAddress;
	}
}
