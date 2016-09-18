package com.liu.aidldemo.aidl;

import java.util.List;
import com.liu.aidldemo.aidl.Book;
import com.liu.aidldemo.aidl.IOnNewBookArrivedListener;

 interface IBookManager {
	
	
	List<Book> getBookList();
	
	void addBook(in Book book);
	
	
	void registerListener(IOnNewBookArrivedListener iOnNewBookArrivedListener);
	
	void unRegisterListener(IOnNewBookArrivedListener iOnNewBookArrivedListener);
	
}
