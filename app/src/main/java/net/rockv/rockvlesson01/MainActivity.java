package net.rockv.rockvlesson01;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

public class MainActivity extends AppCompatActivity {
    private IWXAPI api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //通过WXAPIFactory工厂获取IWXApI的示例
        api = WXAPIFactory.createWXAPI(this, Config.APP_ID_WX);//,true);
        //将应用的appid注册到微信
        api.registerApp(Config.APP_ID_WX);

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendAuth.Req req = new SendAuth.Req();
                req.scope = "snsapi_userinfo";
//                req.scope = "snsapi_login";//提示 scope参数错误，或者没有scope权限
                req.state = "wechat_sdk_test";
                boolean bSend = api.sendReq(req);
                bSend = bSend;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
      //  Glide.with(WXLoginActivity.this).load(MyApplication.getShared().getString("headUrl","")).into(ivHead);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == 0){
            String headUrl = data.getStringExtra("headUrl");
      //      ViseLog.d("url:"+headUrl);
      //      Glide.with(WXLoginActivity.this).load(headUrl).into(ivHead);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
