package com.tianyl.android.wechat;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;

public class SqlExecuteHooker extends XC_MethodHook{

	@Override
	protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
		XposedBridge.log("----------------start-----------------method:" + param.method.getName() + ",time:" + WechatUtil.getTime());
		Object sqlObj = param.args[0];
		Object[] objs = (Object[])param.args[1];
		if(sqlObj == null || objs == null){
			XposedBridge.log("insert参数为空");
			return;
		}
		String sql = sqlObj.toString();
		XposedBridge.log("execute sql:" + sql);
		int index = 0;
		for(Object obj:objs){
			XposedBridge.log("args[" + index + "]:" + WechatUtil.getType(obj));
			XposedBridge.log("args[" + index + "]:" + WechatUtil.getStr(obj));
			index++;
		}
		XposedBridge.log("---------------stack------------------");
		XposedBridge.log(new Exception());
		XposedBridge.log("-----------------end------------------");
	}
	
}
