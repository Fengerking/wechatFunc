package net.rockv.rockvlesson01.pay;

import android.util.Log;
import java.util.Map;
import java.util.HashMap;
import java.io.StringReader;

import android.util.Xml;
import org.xmlpull.v1.XmlPullParser;


public class GetPrepayIdResult {
	private static final String TAG = "GetPrepayIdResult";
	public int			errCode = 1;
	public String 		strCode = null;
	public String 		strMsg = null;
	Map<String,String>	m_result = null;
	public String 		prepayId = null;

	public void parseFrom(String content) {
		if (content == null || content.length() <= 0) {
			Log.e(TAG, "parseFrom fail, content is null");
			return;
		}
		decodeXml (content);
		strCode = GetValue ("return_code");
		strMsg = GetValue ("return_msg");
		if (strCode.equals("SUCCESS")) {
			errCode = 0;
			prepayId = GetValue("prepay_id");
		}
	}

	public String GetValue (String strKey) {
		if (m_result == null)
			return null;
		return m_result.get (strKey);
	}

	public boolean decodeXml(String content) {

		try {
			m_result = new HashMap<String, String>();
			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(new StringReader(content));
			int event = parser.getEventType();
			while (event != XmlPullParser.END_DOCUMENT) {

				String nodeName=parser.getName();
				switch (event) {
					case XmlPullParser.START_DOCUMENT:

						break;
					case XmlPullParser.START_TAG:

						if("xml".equals(nodeName)==false){
							//实例化student对象
							m_result.put(nodeName, parser.nextText());
						}
						break;
					case XmlPullParser.END_TAG:
						break;
				}
				event = parser.next();
			}

			return true;
		} catch (Exception e) {
			Log.e("orion",e.toString());
		}
		return false;

	}
}
