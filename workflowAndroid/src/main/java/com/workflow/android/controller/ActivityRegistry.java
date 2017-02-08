package com.workflow.android.controller;

import android.app.Activity;
import android.content.Intent;
import android.util.Pair;

import com.workflow.core.controller.ArgumentHandler;
import com.workflow.core.controller.SimpleHandler;

import java.util.HashMap;

public abstract class ActivityRegistry {

    private static HashMap<Integer, Pair<ArgumentHandler<Intent>, SimpleHandler>> handlers = new HashMap<Integer, Pair<ArgumentHandler<Intent>, SimpleHandler>>();

    public static void register(int requestCode, ArgumentHandler<Intent> successHandler, SimpleHandler failureHandler){
        handlers.put(requestCode, new Pair<ArgumentHandler<Intent>, SimpleHandler>(successHandler, failureHandler));
    }

    public static void onActivityResult(int requestCode, int resultCode, Intent data){
        if(handlers.containsKey(requestCode)){
            if(resultCode == Activity.RESULT_OK){
                handlers.get(requestCode).first.handle(data);
            }else if(resultCode == Activity.RESULT_CANCELED){
                handlers.get(requestCode).second.handle();
            }
        }
    }

}
