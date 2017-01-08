package com.tianyl.android.wechat;

import java.lang.reflect.Member;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;

public class DebugHooker extends XC_MethodHook{

	@Override
	protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
		XposedBridge.log("DebugHooker start:" + WechatUtil.getTime());
		Object obj = param.thisObject;
		XposedBridge.log("class:" + obj.getClass().getName());
		Member member = param.method;
		XposedBridge.log("method:" + member.getName());
		XposedBridge.log("args:" + param.args);
		XposedBridge.log("DebugHooker end:" + WechatUtil.getTime());
	}
	
}
