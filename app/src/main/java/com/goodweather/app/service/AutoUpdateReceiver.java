package com.goodweather.app.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AutoUpdateReceiver extends BroadcastReceiver{
	
	@Override
	public void onReceive(Context context, Intent intent){
		Intent i = new Intent(context, AutoUpdateReceiver.class);
		context.startService(i);
	}
	
}
