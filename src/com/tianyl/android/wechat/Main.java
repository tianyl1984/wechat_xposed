package com.tianyl.android.wechat;

import java.io.File;
import java.lang.reflect.Method;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import dalvik.system.PathClassLoader;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class Main implements IXposedHookLoadPackage{

	private static final String WechatPackageName = "com.tencent.mm";
	
	@Override
	public void handleLoadPackage(LoadPackageParam lpparam) throws Throwable {
		String pkgName = lpparam.packageName;
		if(!pkgName.equals(WechatPackageName)){
			return;
		}
		handleFromJar(lpparam);
	}

	private void handleFromJar(LoadPackageParam lpparam)throws Throwable {
		String curPkg = Main.class.getPackage().getName();
		String filePath = String.format("/data/app/%s-%s.apk", curPkg, 1);
		if (!new File(filePath).exists()){
            filePath = String.format("/data/app/%s-%s.apk", curPkg, 2);
            if (!new File(filePath).exists()){
                XposedBridge.log("Error:在/data/app找不到APK文件:" + curPkg);
                return;
            }
        }
		final PathClassLoader pathClassLoader = new PathClassLoader(filePath, ClassLoader.getSystemClassLoader());
        final Class<?> aClass = Class.forName(curPkg + "." + Main.class.getSimpleName(), true, pathClassLoader);
        final Method aClassMethod = aClass.getMethod("handle", XC_LoadPackage.LoadPackageParam.class);
        aClassMethod.invoke(aClass.newInstance(), lpparam);		
	}

	public void handle(XC_LoadPackage.LoadPackageParam lp){
		XposedBridge.log("打开微信...:" + WechatUtil.getTime());
		XposedHelpers.findAndHookMethod("com.tencent.mm.booter.NotifyReceiver", lp.classLoader, "onReceive", Context.class,Intent.class,new XC_MethodHook() {
			
			@Override
			protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
				Context context = (Context)param.args[0];
				Intent intent = (Intent)param.args[1];
				Bundle bundle = intent.getExtras();
				if(bundle != null){
					XposedBridge.log("接收消息，时间:" + WechatUtil.getTime());
					XposedBridge.log("消息[notify_uin]:" + bundle.get("notify_uin"));
					XposedBridge.log("消息[notify_respType]:" + bundle.getInt("notify_respType"));//268369921
//					XposedBridge.log("消息[notify_respBuf]:" + new String(bundle.getByteArray("notify_respBuf")));
				}
			}
			
		});
		
//		XposedHelpers.findAndHookMethod("com.tencent.mm.plugin.zero.b", lp.classLoader, "a","com.tencent.mm.plugin.zero.a.e", new XC_MethodHook() {
//			@Override
//			protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//				XposedBridge.log("HookMethod:" + TimeUtil.getTime());
//				XposedBridge.log("param:" + param.args[0].getClass().getName());
//			}
//		});

//		DebugHooker hooker = new DebugHooker();
//		XposedHelpers.findAndHookMethod("com.tencent.mm.plugin.bbom.j", lp.classLoader, "a","com.tencent.mm.booter.NotifyReceiver.a", Integer.TYPE,Integer.TYPE,String.class, hooker);
//		XposedHelpers.findAndHookMethod("com.tencent.mm.plugin.bbom.j", lp.classLoader, "b", Integer.TYPE,new byte[]{}.getClass(),new byte[]{}.getClass(), hooker);
//		XposedHelpers.findAndHookMethod("com.tencent.mm.plugin.bbom.j", lp.classLoader, "c", Integer.TYPE,new byte[]{}.getClass(),new byte[]{}.getClass(), hooker);
//		XposedHelpers.findAndHookMethod("com.tencent.mmdb.database.SQLiteSession", lp.classLoader, "executeForLastInsertedRowId",String.class,new Object[]{}.getClass(),Integer.TYPE,"com.tencent.mmdb.support.CancellationSignal", new XC_MethodHook(){
//			@Override
//			protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//				XposedBridge.log("----------------start-----------------");
//				Object sqlObj = param.args[0];
//				Object[] objs = (Object[])param.args[1];
//				if(sqlObj == null || objs == null){
//					XposedBridge.log("insert参数为空");
//					return;
//				}
//				String sql = sqlObj.toString();
//				XposedBridge.log("execute sql:" + sql);
//				int index = 0;
//				for(Object obj:objs){
//					XposedBridge.log("args[" + index + "]:" + WechatUtil.getType(obj));
//					XposedBridge.log("args[" + index + "]:" + WechatUtil.getStr(obj));
//					index++;
//				}
//				XposedBridge.log("-----------------end------------------");
//			}
//		});
		
		String className = "com.tencent.mmdb.database.SQLiteSession";
		SqlExecuteHooker hooker = new SqlExecuteHooker();
		XposedHelpers.findAndHookMethod("com.tencent.mmdb.database.SQLiteSession", lp.classLoader, "execute",
				String.class, Object[].class, Integer.TYPE, "com.tencent.mmdb.support.CancellationSignal", hooker);
		XposedHelpers.findAndHookMethod(className, lp.classLoader, "executeForChangedRowCount",
				String.class, Object[].class, Integer.TYPE, "com.tencent.mmdb.support.CancellationSignal", hooker);
		XposedHelpers.findAndHookMethod(className, lp.classLoader, "executeForCursorWindow",
				String.class, Object[].class, Integer.TYPE, Integer.TYPE, Integer.TYPE, "com.tencent.mm.m.a.b", "com.tencent.mm.m.a.c", hooker);
		XposedHelpers.findAndHookMethod(className, lp.classLoader, "executeForCursorWindow",
				String.class, Object[].class, "com.tencent.mmdb.CursorWindow", Integer.TYPE, Integer.TYPE, Boolean.TYPE, Integer.TYPE, "com.tencent.mmdb.support.CancellationSignal",
				hooker);
		XposedHelpers.findAndHookMethod(className, lp.classLoader, "executeForLastInsertedRowId",
				String.class, Object[].class, Integer.TYPE, "com.tencent.mmdb.support.CancellationSignal", hooker);
		XposedHelpers.findAndHookMethod(className, lp.classLoader, "executeForLong",
				String.class, Object[].class, Integer.TYPE, "com.tencent.mmdb.support.CancellationSignal", hooker);
		XposedHelpers.findAndHookMethod(className, lp.classLoader, "executeForString",
				String.class, Object[].class, Integer.TYPE, "com.tencent.mmdb.support.CancellationSignal", hooker);
		
		
//		XposedHelpers.findAndHookMethod("com.tencent.mm.sdk.modelmsg.WXTextObject", lp.classLoader, "checkArgs", new XC_MethodHook() {
//			@Override
//			protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//				XposedBridge.log("text:" + TimeUtil.getTime());
//				Object obj = param.thisObject;
//				Object text = XposedHelpers.getObjectField(obj, "text");
//				XposedBridge.log("value:" + text);
//			}
//		});
	}
}
