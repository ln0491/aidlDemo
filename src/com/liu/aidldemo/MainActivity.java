package com.liu.aidldemo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity implements OnClickListener {
	
	private Button mBtnSend;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		initView();
		initData();
		initListener();
	}

	private void initView() {
		
		mBtnSend = (Button) findViewById(R.id.btnSend);
	}

	private void initData() {
		
	}

	private void initListener() {
		
		mBtnSend.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
			case R.id.btnSend:
				
				sendMessage();
				break;
			
			default:
				break;
		}
	}

	private void sendMessage() {
		
	}
}
