package com.liu.aidldemo.aidl;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 
 * @ClassName: Book
 * @Description: TODO(实现了Parcelable接口的实体类)
 * @author 刘楠
 * @date 2016年9月18日 下午9:59:07
 *
 */
public class Book implements Parcelable{
	
	private int bookId;
	private String bookName;
	public Book() {
		super();
	}
	public Book(int bookId, String bookName) {
		super();
		this.bookId = bookId;
		this.bookName = bookName;
	}
	
	@Override
	public int describeContents() {
		return 0;
	}
	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeInt(bookId);
		out.writeString(bookName);
	}
	
	public static final Parcelable.Creator<Book> CREATOR = new Creator<Book>() {
		
		@Override
		public Book[] newArray(int size) {
			return new Book[size];
		}
		
		@Override
		public Book createFromParcel(Parcel in) {
			return new Book(in);
		}
	}; 
	
	public Book(Parcel in) {
		bookId= in.readInt();
		bookName = in.readString();
	}
	@Override
	public String toString() {
		return "Book [bookId=" + bookId + ", bookName=" + bookName + "]";
	}
	
	
}
