package com.liu.aidldemo.aidl;
import com.liu.aidldemo.aidl.Book;
 interface IOnNewBookArrivedListener {
	
	void onNewBookArrived(in Book newBook);
	
}
