package com.tianyl.android.wechat.hook;

import java.io.File;
import java.util.Map;
import java.util.UUID;

import org.json.JSONObject;

import com.tianyl.android.wechat.util.FileUtil;
import com.tianyl.android.wechat.util.WechatUtil;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class MessageInsertHook extends XC_MethodHook{

	private LoadPackageParam loadPackageParam;
	
	public MessageInsertHook(LoadPackageParam lpp){
		this.loadPackageParam = lpp;
	}
	
	@Override
	protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
		Object sqlObj = param.args[0];
		Object[] objs = (Object[])param.args[1];
		if(sqlObj == null){
			XposedBridge.log("sql为空");
			return;
		}
		if(!sqlObj.toString().contains("message")){
			return;
		}
		if(objs == null){
			XposedBridge.log("参数为空");
			return;
		}
		for(Object obj:objs){
			String val = WechatUtil.getStr(obj);
			if(val.startsWith("~SEMI_XML~")){
				@SuppressWarnings("unchecked")
				Map<String, String> contentMap = (Map<String, String>)XposedHelpers.callStaticMethod(XposedHelpers.findClass("com.tencent.mm.sdk.platformtools.au",loadPackageParam.classLoader), "Ks", val);
				if(contentMap != null){
					JSONObject json = new JSONObject(contentMap);
					String jsonStr = json.toString();
					FileUtil.saveStringToFile(jsonStr, new File(FileUtil.getBathPath() + "json/" + UUID.randomUUID().toString() + ".json"));
				}else{
					XposedBridge.log("content map is null");
				}
			}
		}
	}
}
