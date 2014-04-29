package cn.niven.web4app;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.ref.SoftReference;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import javax.servlet.http.Cookie;

public class Session {

	private static HashMap<String, SoftReference<Session>> sessions = new HashMap<>();
	private static LinkedList<Session> holder = new LinkedList<Session>();
	private static final File sessionDir = new File("session");
	private static Timer cleanerTimer = new Timer();

	static {
		if (sessionDir.exists()) {
			Util.deleteFile(sessionDir);
		}
		sessionDir.mkdirs();
		// clear empty map items
		cleanerTimer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				LinkedList<String> rmList = new LinkedList<String>();
				for (Map.Entry<String, SoftReference<Session>> item : sessions
						.entrySet()) {
					if (item.getValue().get() == null) {
						rmList.add(item.getKey());
					}
				}
				for (String key : rmList) {
					sessions.remove(key);
				}
			}
		}, 3600000, 3600000);
		// delete old session files
		cleanerTimer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				long t = new Date().getTime() - 72000;
				for (File item : sessionDir.listFiles()) {
					if (item.isFile()) {
						ObjectInputStream in = null;
						try {
							in = new ObjectInputStream(
									new FileInputStream(item));
							long ft = in.readLong();
							in.close();
							in = null;
							if (t > ft) {
								item.delete();
							}
						} catch (IOException e) {
							e.printStackTrace();
						} finally {
							if (in != null) {
								try {
									in.close();
								} catch (IOException e) {
								}
							}
						}
					}
				}
			}
		}, 36000000, 36000000);
	}

	static Session getSession() {
		RequestContext ctx = RequestContext.getContext();
		String sessionID = ctx.request.getHeader("sessionid");
		if (sessionID == null && ctx.request.getCookies() != null) {
			for (Cookie cookie : ctx.request.getCookies()) {
				if (cookie.getName().equals("sessionid")) {
					sessionID = cookie.getValue();
					break;
				}
			}
		}
		Session ret = null;
		if (sessionID != null) {
			if (sessions.containsKey(sessionID)) {
				ret = sessions.get(sessionID).get();
			}
			if (ret == null) {
				ret = new Session(new File(sessionDir.getAbsolutePath() + "/"
						+ sessionID));
			}
		}
		if (ret == null || ret.sessionID == null) {
			ret = new Session(sessionID);
		}
		holder.remove(ret);
		holder.addLast(ret);
		if (holder.size() > 1000) {
			holder.getFirst().save();
			holder.removeFirst();
		}
		sessions.put(ret.sessionID, new SoftReference<Session>(ret));

		Cookie cookie = new Cookie("sessionid", ret.sessionID);
		cookie.setPath("/");
		cookie.setMaxAge(3600);
		ctx.response.addCookie(cookie);
		ctx.response.addHeader("sessionid", ret.sessionID);

		return ret;
	}

	public final String sessionID;
	private HashMap<String, Serializable> data = new HashMap<>();

	private Session(String sessionID) {
		if(sessionID == null)
			sessionID = UUID.randomUUID().toString();
		this.sessionID = sessionID;
	}

	@SuppressWarnings("unchecked")
	private Session(File file) {
		String sess = null;
		ObjectInputStream in = null;
		try {
			in = new ObjectInputStream(new FileInputStream(file));
			in.readLong();
			sess = (String) in.readObject();
			data = (HashMap<String, Serializable>) in.readObject();
		} catch (IOException | ClassNotFoundException e) {
		} finally {
			if (in != null)
				try {
					in.close();
				} catch (IOException e) {
				}
		}
		sessionID = sess;
	}

	private void save() {
		if (sessionID != null) {
			File f = new File(sessionDir.getAbsolutePath() + "/" + sessionID);
			ObjectOutputStream out = null;
			try {
				out = new ObjectOutputStream(new FileOutputStream(f));
				out.writeLong(new Date().getTime());
				out.writeObject(sessionID);
				out.writeObject(data);
			} catch (IOException e) {
			} finally {
				if (out != null)
					try {
						out.close();
					} catch (IOException e) {
					}
			}
		}
	}

	public void put(String key, Serializable value) {
		if (value == null)
			data.remove(key);
		else
			data.put(key, value);
	}

	public Serializable get(String key) {
		return data.get(key);
	}

	public static void set(String key, Serializable value) {
		RequestContext.getContext().session.put(key, value);
	}

	public static Serializable valueOf(String key) {
		return RequestContext.getContext().session.get(key);
	}
	
	public static String sessionID() {
		return RequestContext.getContext().session.sessionID;
	}
}
