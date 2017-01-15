package com.tianyl.android.wechat.sync;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.tianyl.android.wechat.util.FileUtil;
import com.tianyl.android.wechat.util.LogUtil;
import com.tianyl.android.wechat.util.NetUtil;
import com.tianyl.android.wechat.util.StringUtil;

public class UploadService {

	public static void main(String[] args) {
		update();
	}

	public static void update() {
		List<File> jsonFiles = findJsonFiles();
		for (File file : jsonFiles) {
			String jsonStr = FileUtil.read(file);
			List<AppMsg> msgs = null;
			try {
				JSONObject json = JSONObject.parseObject(jsonStr);
				msgs = buildMsg(json);
			} catch (JSONException e) {
				e.printStackTrace();
				// 记录日志，删除文件
				LogUtil.log("json格式错误：" + e.getMessage());
				deleteFile(file);
				continue;
			}
			boolean sendFlag = sendToServer(msgs);
			if (sendFlag) {
				deleteFile(file);
			}
		}
	}

	private static boolean sendToServer(List<AppMsg> msgs) {
		String url = "https://tianice.51vip.biz/api/wx/article/save";
		String result = NetUtil.post(url, JSONArray.toJSONString(msgs));
		try {
			JSONObject json = JSONObject.parseObject(result);
			return json.getBooleanValue("result");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return false;
	}

	private static void deleteFile(File file) {
        if(file.exists()){
            file.delete();
        }
	}

	private static List<AppMsg> buildMsg(JSONObject json) throws JSONException {
		List<AppMsg> result = new ArrayList<>();
		AppMsg msg = new AppMsg();
		msg.setAppName(json.getString(".msg.appinfo.appname"));
		msg.setTitle(json.getString(".msg.appmsg.title"));
		msg.setDigest(json.getString(".msg.appmsg.des"));
		msg.setPublisherUsername(json.getString(".msg.appmsg.mmreader.publisher.username"));
		msg.setPublishTime(getTime(json.getLongValue(".msg.appmsg.mmreader.category.item.pub_time")));
		msg.setType(json.getString(".msg.appmsg.type"));
		msg.setUrl(json.getString(".msg.appmsg.url"));

		if (msg.getType().equals("6")) {// 收到文件
			return result;
		}
		if (msg.getPublisherUsername() == null || msg.getPublisherUsername().equals("")) {// 聊天中的，insert时想办法不记录
			return result;
		}
		if (msg.getPublisherUsername().equals("exmail_tool")) {// 邮件，insert时想办法不记录
			return result;
		}


		result.add(msg);
		for (int i=1;i<3;i++){
			String url = json.getString(".msg.appmsg.mmreader.category.item" + i + ".url");
			if(StringUtil.isNotBlank(url)){
				AppMsg msgTemp = new AppMsg();
				msgTemp.setAppName(json.getString(".msg.appinfo.appname"));
				msgTemp.setTitle(json.getString(".msg.appmsg.mmreader.category.item" + i + ".title"));
				msgTemp.setDigest(json.getString(".msg.appmsg.mmreader.category.item" + i + ".digest"));
				msgTemp.setPublisherUsername(json.getString(".msg.appmsg.mmreader.publisher.username"));
				msgTemp.setPublishTime(getTime(json.getLongValue(".msg.appmsg.mmreader.category.item" + i + ".pub_time")));
				msgTemp.setType(json.getString(".msg.appmsg.type"));
				msgTemp.setUrl(url);
				result.add(msgTemp);
			}
		}
		return result;
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
