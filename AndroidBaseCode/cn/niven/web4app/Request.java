package cn.niven.web4app;

import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import android.os.Handler;
import android.os.Message;

public final class Request<ResultInterface> implements Runnable {

	public String url;
	public HashMap<String, Object> params = new HashMap<String, Object>();
	public HashMap<String, String> headers = new HashMap<String, String>();
	public byte[] rawData;
	public Class<?> resultInterfaceType;
	public Class<?> resultType;
	WeakReference<Object> resultHandler;
	Object handlerHolder;
	boolean listenResult = false;

	public Request<ResultInterface> onResult(ResultInterface onResult) {
		return onResult(onResult, resultInterfaceType);
	}

	public Request<ResultInterface> onResult(Object onResult,
			Class<?> resultInterfaceType) {
		if (onResult == null) {
			throw new NullPointerException();
		}
		resultHandler = new WeakReference<Object>(onResult);
		this.resultInterfaceType = resultInterfaceType;
		listenResult = true;
		return this;
	}

	public Request<ResultInterface> holdListener() {
		if (null != resultHandler)
			handlerHolder = resultHandler.get();
		return this;
	}

	private static Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.obj instanceof Runnable) {
				((Runnable) msg.obj).run();
			}
		}
	};

	private int errorCode = 0;
	private String errorMsg = null;
	private Object result = null;

	@Override
	public void run() {
		if (resultHandler != null && resultHandler.get() != null) {
			Method m = resultInterfaceType.getDeclaredMethods()[0];
			try {
				switch (m.getParameterTypes().length) {
				case 0:
					m.invoke(resultHandler.get());
					break;
				case 1:
					m.invoke(resultHandler.get(), result);
					break;
				case 2:
					m.invoke(resultHandler.get(), errorCode, errorMsg);
					break;
				case 3:
					m.invoke(resultHandler.get(), errorCode, errorMsg, result);
					break;
				}
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}

	void success(Object result) {
		this.result = result;
		Message msg = new Message();
		msg.obj = this;
		mHandler.sendMessage(msg);
	}

	void fail(int code, String message) {
		errorCode = code;
		errorMsg = message;
		Message msg = new Message();
		msg.obj = this;
		mHandler.sendMessage(msg);
	}

	public void fire() {
		Composition.requestQueue.enque(this);
	}
}
