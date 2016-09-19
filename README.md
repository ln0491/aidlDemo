## aidl使用



###AIDL支付数据类型


1. 基本数据类型(int ,long,char,boolean,double等)
2. String 和CharSequence;
3. List:只支持ArrayList,里面的每个元素都必须能够被AIDL支持
4. Map：只支付HashMap，里面的每个元素都必须能够被AIDL支持
5. Parcelable：所有实现了Parcelable接口的对象
6. AIDL:所有的AIDL接口本身也可以在AIDL文件中使用


#### 其中自定义的Parcelable对象和AIDL对象必须要显示的import导入进来,不管它们是否和当前的AIDL文件是否位于同一个包内

>IBookManager.aidl和Book位于同一个包

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

>Book

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
	....
	}


仍然需要显式的import Book



#### 另外如果AIDL中用到自定义的Parcelable对象

必须新建一个和它同名的AIDL文件，并在其中声明它为Parcelable类型
在IBookManager.aidl 中用到Book这个类所以要创建Book.aidl然后添加

		package com.liu.aidldemo.aidl;
		parcelable Book;

AIDL中每个实现了Parceable接口的类都需要按照上面的方式建立相应的AIDL文件并声明那个类为
	
	parcelabel

AIDL中除了基本数据类型，其他的参数必须标上方向：

	in、out、或者inout

in:表示输入参数
out:表示输出参数
inout:表示输入输出参数

#### AIDL中只支持方法，不支持声明静态常量

为了方便AIDL开发，建议把所有的和AIDL相关的类和文件全部放放同一个包中，这样做的好处是， 当客户端是另外一个应用时，可以直接把整个包都复制到客户端工程中



### RemoteCallbackList是系统提供的用于删除跨进程listener的接口

RemoteCallbackList是一个泛型 支持管理任意的AIDL接口

>声明

	public class RemoteCallbackList<E extends IInterface> 

它的内部有一个MAP结构专门来保存所有的AIDL回调

这个Map的key是IBinder类型，value是CallBack类型
	
	ArrayMap<IBinder, Callback> mCallbacks  = new ArrayMap<IBinder, Callback>();




### Binder意外死亡

> 给Binder设置DeathRecipient监听，当Binder死亡时，收到bindDied方法的加高，在重新绑定


	/**
		 * 为Binder设置死亡监听
		 */
		private IBinder.DeathRecipient mDeathRecipient = new DeathRecipient() {
			
			@Override
			public void binderDied() {
				
				if(mIBookManager==null){
					return;
				}
				
				mIBookManager.asBinder().unlinkToDeath(mDeathRecipient, 0);
				
				mIBookManager =null;
				/**
				 * 重新连接远程服务
				 */
				toBindService();
			}
		};

>在ServiceConnection的onServiceDisconnected中重新连接远程服务

	private class MyServiceConn implements ServiceConnection {
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			
		}
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			//重新绑定服务
			toBindService();
		}
		
	}


>绑定服务

		private void toBindService() {
			
			Intent intent = new Intent(this, BookManagerService.class);
			
			if (mMyServiceConn == null) {
				mMyServiceConn = new MyServiceConn();
			}
			bindService(intent, mMyServiceConn, Context.BIND_AUTO_CREATE);
		}


### 权限验证

>声明权限

	<permission 
	        android:name="com.liu.aidldemo.permission.ACCESS_BOOK_SERVICE"
	        android:protectionLevel="normal"/>

>在清单文件中使用权限

 	<uses-permission android:name="com.liu.aidldemo.permission.ACCESS_BOOK_SERVICE"/>

#### 第一种

在service 的onBind方法中验证


	@Override
	public IBinder onBind(Intent intent) {
		Log.d(TAG, "service onBind");
		/**
		 * 权限验证
		 */
		int check = checkCallingOrSelfPermission("com.liu.aidldemo.permission.ACCESS_BOOK_SERVICE");
		
		if(check==PackageManager.PERMISSION_DENIED){
			return null;
		}
		return mBinder;
	}


#### 在onTransact方法中验证

		private Binder mBinder = new IBookManager.Stub() {
		
		....其它方法
		
		
		/**
		 * 权限验证
		 */
		public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws RemoteException {
			
			
			int check = checkCallingOrSelfPermission("com.liu.aidldemo.permission.ACCESS_BOOK_SERVICE");
			
			
			if(check==PackageManager.PERMISSION_DENIED){
				return false;
			}
			
			String packageName = null;
			String [] packages = getPackageManager().getPackagesForUid(getCallingUid());
			
			
			if(packages!=null && packages.length>0){
				packageName = packages[0];
			}
			
			
			if(!packageName.startsWith("com.liu")){
				return false;
			}			
			
			return super.onTransact(code, data, reply, flags);
			
		};
	};


