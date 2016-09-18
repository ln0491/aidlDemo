package com.liu.aidldemo;

import java.util.List;

import com.liu.aidldemo.aidl.Book;
import com.liu.aidldemo.aidl.BookManagerService;
import com.liu.aidldemo.aidl.IBookManager;
import com.liu.aidldemo.aidl.IOnNewBookArrivedListener;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

public class BookManagerActivity extends Activity {
	
	private static final String TAG = "vivi";
	
	private MyServiceConn mMyServiceConn;
	
	private IBookManager mIBookManager;
	
	private static final int MESSAGE_NEW_BOOK_ARRIVED =1;
	
	
	
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			
			switch (msg.what) {
				case MESSAGE_NEW_BOOK_ARRIVED:
					Log.d(TAG, "接收到新书了。"+msg.obj);
					break;
				
				default:
					super.handleMessage(msg);
			}
		};
	};
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_book_manager);
		
		Log.d("vivi", "onCreate");
		
		toBindService();
	}
	
	private void toBindService() {
		
		Intent intent = new Intent(this, BookManagerService.class);
		
		if (mMyServiceConn == null) {
			mMyServiceConn = new MyServiceConn();
		}
		bindService(intent, mMyServiceConn, Context.BIND_AUTO_CREATE);
	}
	
	private class MyServiceConn implements ServiceConnection {
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mIBookManager = IBookManager.Stub.asInterface(service);
			
			// 获取书的列表
			try {
				List<Book> bookList = mIBookManager.getBookList();
				
				Log.d("vivi", "bookList query " + bookList.size() + "    " + bookList.toString());
				
				
				mIBookManager.registerListener(mOnNewBookArrivedListener);
				mIBookManager.addBook(new Book(4, "Android 开发者艺术探索"));
				
				List<Book> newBookList = mIBookManager.getBookList();
				
				Log.d("vivi", "newBookList query " + newBookList.size() + "    " + newBookList.toString());
				
			
				
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			
		}
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			if (mIBookManager != null) {
				mIBookManager = null;
			}
		}
		
	}
	
	
	private IOnNewBookArrivedListener mOnNewBookArrivedListener = new IOnNewBookArrivedListener.Stub() {
		
		@Override
		public void onNewBookArrived(Book newBook) throws RemoteException {

			mHandler.obtainMessage(MESSAGE_NEW_BOOK_ARRIVED, newBook).sendToTarget();
		}
	};
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mMyServiceConn != null) {
			unbindService(mMyServiceConn);
		}
		
		if(mIBookManager!=null&&mIBookManager.asBinder().isBinderAlive()){
			
			
			try {
				
				Log.d(TAG, "解除注册 监听新书.."+mOnNewBookArrivedListener);
				mIBookManager.unRegisterListener(mOnNewBookArrivedListener);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}
}
