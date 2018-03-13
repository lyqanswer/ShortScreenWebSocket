package com.edu.edushortscreen.net;

import android.content.Context;

import com.loopj.android.http.RequestParams;
import com.lucher.net.req.BaseReqParamsEntity;

/**
 * NetSendCodeEntity网络访问实体类
 * 
 * @author lyq
 * 
 */
public class NetSendCodeEntity extends BaseReqParamsEntity {

	public NetSendCodeEntity(Context context, int reqMethod, String url) {
		super(context, reqMethod, url);
	}

	@Override
	public RequestParams getReqParams() {
		return null;
	}

}
