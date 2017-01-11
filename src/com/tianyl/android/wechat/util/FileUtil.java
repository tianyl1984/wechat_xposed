package com.tianyl.android.wechat.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import android.os.Environment;

public class FileUtil {

	public static final String getBathPath() {
		String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/wechat/";
		File file = new File(path);
		if (!file.exists()) {
			file.mkdirs();
		}
		return path;
	}

	public static final void appendStringToFile(String str, File file) {
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
		FileWriter fw = null;
		try {
			fw = new FileWriter(file, true);
			fw.append(str + "\r\n");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fw != null) {
				try {
					fw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static final void saveStringToFile(String str, File file) {
		if (!file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}
		FileWriter fw = null;
		try {
			fw = new FileWriter(file);
			fw.write(str);
			fw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fw != null) {
				try {
					fw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
