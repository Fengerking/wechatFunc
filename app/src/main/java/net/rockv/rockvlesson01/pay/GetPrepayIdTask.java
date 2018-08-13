package net.rockv.rockvlesson01.pay;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import android.os.Handler;
import java.io.IOException;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Random;


import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;


import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import net.rockv.rockvlesson01.util.MD5;
import net.rockv.rockvlesson01.Config;
import net.rockv.rockvlesson01.util.WXSystemInfo;


public class GetPrepayIdTask extends AsyncTask<String, Void, GetPrepayIdResult> {
	private static final String TAG = "GetPrepayIdTask";
	private Context 			context;
	private ProgressDialog 		dialog;
	private String[] 			goods_info;
	private GetPrepayIdResult 	m_result = null;

	private IWXAPI 				api;

	public GetPrepayIdTask(Context context) {
		this.context = context;
	}

	@Override
	protected void onPreExecute() {
		dialog = ProgressDialog.show(context, "提示", "正在获取预支付订单...");
	}

	@Override
	protected GetPrepayIdResult doInBackground(String... params) {
		m_result = new GetPrepayIdResult();
		goods_info = new String[] { params[0], params[1], params[2] };
		String url = "https://api.mch.weixin.qq.com/pay/unifiedorder";
		String entity = genProductArgs();
		Log.d(TAG, "doInBackground, url = " + url + ", entity = " + entity);
		OkHttpUtils.postString()
				.url(url)
				.content(entity)
		//		.mediaType(MediaType.parse("application/xml; charset=utf-8"))
				.build ()
				.execute(new StringCallback() {
					@Override
					public void onError(okhttp3.Call call, Exception e, int id) {
						Toast.makeText(context, "post string failed.", Toast.LENGTH_LONG).show();
					}

					@Override
					public void onResponse(String response, int id) {
						if (m_result != null)
							m_result.parseFrom(response);
					}
				});



		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}



		return m_result;
	}

	@Override
	protected void onPostExecute(GetPrepayIdResult result) {
		if (dialog != null) {
			dialog.dismiss();
		}
		if (result.errCode == 0) {
			Toast.makeText(context, "获取prepayid成功", Toast.LENGTH_LONG).show();
			payWithWechat();
		} else {
			Toast.makeText(context, "获取prepayid失败，原因" + result.strMsg, Toast.LENGTH_LONG).show();
		}
	}

	private String genNonceStr() {
		Random random = new Random();
		return MD5.getMessageDigest(String.valueOf(random.nextInt(10000)).getBytes());
	}

	private long genTimeStamp() {
		return System.currentTimeMillis() / 1000;
	}

	private String genOutTradNo() {
		Random random = new Random();
		return MD5.getMessageDigest(String.valueOf(random.nextInt(10000)).getBytes());
	}


	private String genProductArgs() {
		String ip = WXSystemInfo.getWifiIp(context);
		if (ip == "" && ip == "") {
			ip = WXSystemInfo.getLocalIpAddress();
		}
		try {
			String nonceStr = genNonceStr();
			List<NameValuePair> packageParams = new LinkedList<NameValuePair>();
			packageParams.add(new BasicNameValuePair("appid", 			Config.APP_ID_WX));
			packageParams.add(new BasicNameValuePair("body", 			"APP pay test"));
			packageParams.add(new BasicNameValuePair("mch_id", 		Config.PARTNER_ID));
			packageParams.add(new BasicNameValuePair("nonce_str", 		nonceStr));
			packageParams.add(new BasicNameValuePair("notify_url",		"www.rockv.net"));
			packageParams.add(new BasicNameValuePair("out_trade_no",	genOutTradNo()));
			packageParams.add(new BasicNameValuePair("spbill_create_ip",ip));
			packageParams.add(new BasicNameValuePair("total_fee", 		"1"));
			packageParams.add(new BasicNameValuePair("trade_type", 	"APP"));
			String sign = genAppSign(packageParams);
			packageParams.add(new BasicNameValuePair("sign", sign));
			String xmlstring =toXml(packageParams);
			return xmlstring;
		} catch (Exception e) {
			Log.e("TAG", "fail, ex = " + e.getMessage());
			return null;
		}
	}

	private String genAppSign(List<NameValuePair> params) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < params.size(); i++) {
			sb.append(params.get(i).getName());
			sb.append('=');
			sb.append(params.get(i).getValue());
			sb.append('&');
		}
		sb.append("key=");
		sb.append(Config.PARTNER_KEY);
		//sb.append("sign str\n"+sb.toString()+"\n\n");
		String appSign = MD5.getMessageDigest(sb.toString().getBytes());
		Log.e("orion",appSign);
		return appSign;
	}

	private String toXml(List<NameValuePair> params) {
		StringBuilder sb = new StringBuilder();
		sb.append("<xml>");
		for (int i = 0; i < params.size(); i++) {
			sb.append("<"+params.get(i).getName()+">");
			sb.append(params.get(i).getValue());
			sb.append("</"+params.get(i).getName()+">");
		}
		sb.append("</xml>");

		Log.e("orion",sb.toString());
		return sb.toString();
	}

	private boolean payWithWechat () {
		PayReq req = new PayReq();
		req.appId			= Config.APP_ID_WX;
		req.partnerId		= Config.PARTNER_ID;
		req.prepayId		= m_result.prepayId;
		req.nonceStr		= genNonceStr();
		req.timeStamp		= String.valueOf(genTimeStamp());
		req.packageValue	= "prepay_id="+m_result.GetValue("prepay_id");
		//req.extData			= "app data"; // optional
		List<NameValuePair> signParams = new LinkedList<NameValuePair>();
		signParams.add(new BasicNameValuePair("appid", req.appId));
		signParams.add(new BasicNameValuePair("noncestr", req.nonceStr));
		signParams.add(new BasicNameValuePair("package", req.packageValue));
		signParams.add(new BasicNameValuePair("partnerid", req.partnerId));
		signParams.add(new BasicNameValuePair("prepayid", req.prepayId));
		signParams.add(new BasicNameValuePair("timestamp", req.timeStamp));
		req.sign = genAppSign(signParams);

		api = WXAPIFactory.createWXAPI(context, Config.APP_ID_WX);
		boolean bRC = api.sendReq(req);

		return bRC;
	}

}
