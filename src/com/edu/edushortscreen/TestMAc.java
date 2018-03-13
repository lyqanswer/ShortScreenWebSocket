package com.edu.edushortscreen;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.edu.edushortscreen.util.MacAddressUtil;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class TestMAc extends Activity {
	TextView tv1;
	TextView tv2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test);

		initView();
		initData();

	}

	private void initData() {
		tv1.setText(MacAddressUtil.getMac() + "");

		// tv2.setText(MacAddressUtil.getMac(TestMAc.this) + "");

		try {
			tv2.setText(strToUnicode(MacAddressUtil.getMac()) + "");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void initView() {
		tv1 = (TextView) findViewById(R.id.macf);
		tv2 = (TextView) findViewById(R.id.macs);
	}

	public String StringToMd5(String psw) {
		{
			try {
				MessageDigest md5 = MessageDigest.getInstance("MD5");
				md5.update(psw.getBytes("UTF-8"));
				byte[] encryption = md5.digest();

				StringBuffer strBuf = new StringBuffer();
				for (int i = 0; i < encryption.length; i++) {
					if (Integer.toHexString(0xff & encryption[i]).length() == 1) {
						strBuf.append("0").append(Integer.toHexString(0xff & encryption[i]));
					} else {
						strBuf.append(Integer.toHexString(0xff & encryption[i]));
					}
				}

				return strBuf.toString();
			} catch (NoSuchAlgorithmException e) {
				return "";
			} catch (UnsupportedEncodingException e) {
				return "";
			}
		}
	}

	public static String str2HexStr(String str) {

		char[] chars = "0123456789ABCDEF".toCharArray();
		StringBuilder sb = new StringBuilder("");
		byte[] bs = str.getBytes();
		int bit;

		for (int i = 0; i < bs.length; i++) {
			bit = (bs[i] & 0x0f0) >> 4;
			sb.append(chars[bit]);
			bit = bs[i] & 0x0f;
			sb.append(chars[bit]);
			sb.append(' ');
		}
		return sb.toString().trim();
	}

	public static String strToUnicode(String strText) throws Exception {
		char c;
		StringBuilder str = new StringBuilder();
		int intAsc;
		String strHex;
		for (int i = 0; i < strText.length(); i++) {
			c = strText.charAt(i);
			intAsc = (int) c;
			strHex = Integer.toHexString(intAsc);
			if (intAsc > 128)
				str.append("\\u" + strHex);
			else // 低位在前面补00
				str.append("\\u00" + strHex);
		}
		return str.toString();
	}
}
