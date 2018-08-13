package net.rockv.rockvlesson01;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import net.rockv.rockvlesson01.pay.*;


public class WXPayActivity extends AppCompatActivity {
    private GetPrepayIdTask   m_taskGetPrepayID = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wxpay);

        m_taskGetPrepayID = new GetPrepayIdTask(this);

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                m_taskGetPrepayID.execute("1", "2", "3");
            }
        });
    }
}
