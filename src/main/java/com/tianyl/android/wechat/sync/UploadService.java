package com.tianyl.android.wechat.sync;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.tianyl.android.wechat.util.FileUtil;
import com.tianyl.android.wechat.util.LogUtil;
import com.tianyl.android.wechat.util.NetUtil;

public class UploadService {

	public static void main(String[] args) {
		update();
	}

	public static void update() {
		List<File> jsonFiles = findJsonFiles();
		for (File file : jsonFiles) {
			String jsonStr = FileUtil.read(file);
			AppMsg msg = null;
			try {
				JSONObject json = JSONObject.parseObject(jsonStr);
				msg = buildMsg(json);
			} catch (JSONException e) {
				e.printStackTrace();
				// 记录日志，删除文件
				LogUtil.log("json格式错误：" + e.getMessage());
				deleteFile(file);
				continue;
			}
			if (msg.getType().equals("6")) {// 收到文件
				deleteFile(file);
				continue;
			}
			if (msg.getPublisherUsername() == null || msg.getPublisherUsername().equals("")) {// 聊天中的，insert时想办法不记录
				deleteFile(file);
				continue;
			}
			if (msg.getPublisherUsername().equals("exmail_tool")) {// 邮件，insert时想办法不记录
				deleteFile(file);
				continue;
			}
			boolean sendFlag = sendToServer(msg);
			if (sendFlag) {
				deleteFile(file);
			}
		}
	}

	private static boolean sendToServer(AppMsg msg) {
		String url = "";
		String result = NetUtil.post(url, JSONObject.toJSONString(msg));
		try {
			JSONObject json = JSONObject.parseObject(result);
			return json.getBooleanValue("result");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return false;
	}

	private static void deleteFile(File file) {

	}

	private static AppMsg buildMsg(JSONObject json) throws JSONException {
		AppMsg msg = new AppMsg();
		msg.setAppName(json.getString(".msg.appinfo.appname"));
		msg.setTitle(json.getString(".msg.appmsg.title"));
		msg.setDigest(json.getString(".msg.appmsg.des"));
		msg.setPublisherUsername(json.getString(".msg.appmsg.mmreader.publisher.username"));
		msg.setPublishTime(getTime(json.getLongValue(".msg.appmsg.mmreader.category.item.pub_time")));
		msg.setType(json.getString(".msg.appmsg.type"));
		msg.setUrl(json.getString(".msg.appmsg.url"));
		return msg;
	}

	private static String getTime(long time) {
		if (time == 0) {
			return "";
		}
		Date date = new Date(time * 1000);
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.CHINA).format(date);
	}

	private static List<File> findJsonFiles() {
		String parentDir = FileUtil.getBathPath() + "json/";
		List<File> files = new ArrayList<>();
		for (File file : new File(parentDir).listFiles()) {
			if (file.getName().endsWith("json")) {
				files.add(file);
			}
		}
		return files;
	}

}
