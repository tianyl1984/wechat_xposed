package com.tianyl.android.wechat.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

public class NetUtil {

	public static String post(String url, String msg) {
		StringBuffer result = new StringBuffer();
		try {
			HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
			conn.setRequestMethod("POST");
			conn.setUseCaches(false);
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.connect();

			DataOutputStream out = new DataOutputStream(conn.getOutputStream());
			out.write(msg.getBytes(Charset.forName("utf-8")));
			out.flush();
			out.close();

			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String temp = null;
			while ((temp = reader.readLine()) != null) {
				result.append(temp);
			}
			reader.close();
			conn.disconnect();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result.toString();
	}

}
