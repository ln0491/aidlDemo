package com.liu.aidldemo.aidl;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

public class BookManagerService extends Service {
	private static final String TAG = "vivi";
	/**
	 * 支持读写分离
	 */
	private CopyOnWriteArrayList<Book> mBookList = new CopyOnWriteArrayList<Book>();
	
	/**
	 * 监听器
	 */
	private CopyOnWriteArrayList<IOnNewBookArrivedListener> mListenerList = new CopyOnWriteArrayList<IOnNewBookArrivedListener>();
	/**
	 * 服务是否注销 默认没有 当服务Destory是设置为true
	 */
	private AtomicBoolean mIsServicedDestoryed = new AtomicBoolean(false);
	
	private Binder mBinder = new IBookManager.Stub() {
		
		@Override
		public List<Book> getBookList() throws RemoteException {
			Log.d(TAG, "service getBookList");
			return mBookList;
		}
		
		@Override
		public void addBook(Book book) throws RemoteException {
			Log.d(TAG, "service addBook");
			mBookList.add(book);
			onNewBookArrived(book);
		}
		
		@Override
		public void registerListener(IOnNewBookArrivedListener iOnNewBookArrivedListener) throws RemoteException {
			
			if (!mListenerList.contains(iOnNewBookArrivedListener)) {
				mListenerList.add(iOnNewBookArrivedListener);
				Log.d(TAG, "注册这个监听器! registerListener");
			} else {
				Log.d(TAG, "已经注册过了! registerListener");
			}
			Log.d(TAG, "registerListener " + mListenerList.size());
		}
		
		@Override
		public void unRegisterListener(IOnNewBookArrivedListener iOnNewBookArrivedListener) throws RemoteException {
			
			if (mListenerList.contains(iOnNewBookArrivedListener)) {
				
				mListenerList.remove(iOnNewBookArrivedListener);
				Log.d(TAG, "移除这个监听器!");
			} else {
				Log.d(TAG, "没有找到这具监听器!");
			}
			
			Log.d(TAG, "unRegisterListener " + mListenerList.size());
		}
	};
	
	@Override
	public IBinder onBind(Intent intent) {
		Log.d(TAG, "service onBind");
		return mBinder;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "service onCreate");
		mBookList.add(new Book(1, "Android"));
		mBookList.add(new Book(2, "IOS"));
		mBookList.add(new Book(3, "HTML5"));
		
		new Thread(new ServiceWorker()).start();
	}
	
	
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		mIsServicedDestoryed.set(true);
	}
	
	
	private class ServiceWorker implements Runnable{

		@Override
		public void run() {
			
			
			//后台 进程
			while(!mIsServicedDestoryed.get()){
				try {
					Thread.sleep(5000);
					
					
					
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				int bookId = mBookList.size()+1;
				
				Book newBook = new Book(bookId," new Book "+bookId);
				
				try {
					onNewBookArrived(newBook);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				
			}
			
		}
		
	}


	public void onNewBookArrived(Book newBook) throws RemoteException{
		
		mBookList.add(newBook);
		Log.d(TAG, "onNewBookArrived" +mListenerList.size());
		
		
		
		for(int i = 0; i< mListenerList.size(); i++){
			IOnNewBookArrivedListener listener = mListenerList.get(i);
			
			Log.d(TAG, " onNewBookArrived  通知:"+listener);
			listener.onNewBookArrived(newBook);
		}
		
	}
	
	
}
