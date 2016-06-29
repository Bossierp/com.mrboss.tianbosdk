/*
       Licensed to the Apache Software Foundation (ASF) under one
       or more contributor license agreements.  See the NOTICE file
       distributed with this work for additional information
       regarding copyright ownership.  The ASF licenses this file
       to you under the Apache License, Version 2.0 (the
       "License"); you may not use this file except in compliance
       with the License.  You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

       Unless required by applicable law or agreed to in writing,
       software distributed under the License is distributed on an
       "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
       KIND, either express or implied.  See the License for the
       specific language governing permissions and limitations
       under the License.
*/

package com.mrboss.tianbosdk;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import org.json.JSONArray;
import org.json.JSONException;

import org.apache.cordova.*;
import org.apache.cordova.engine.*;

import java.io.IOException;
import java.io.InputStream;

import android.os.AsyncTask;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Context;
import android.app.AlertDialog;

import com.telpo.tps550.api.printer.ThermalPrinter;

import java.io.*;

public class tianbosdk extends CordovaPlugin {

    private static final String LOG_TAG = "tianbosdkPlugin";

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        try {
            if ("TianBoSdkPrint".equals(action)) {
                String printtext = args.getString(0);
                String encode = args.getString(1);
                TianBoSdkPrint(printtext, encode, callbackContext);
                return true;
            }
        } catch (Exception e) {
            callbackContext.error(e.getMessage());
            return false;
        }
        callbackContext.error("No This Method");
        return false;
    }

    public void TianBoSdkPrint(String printstr, String encode, CallbackContext callbackContext) {
        TianBoSdkPrinterTask tbspt = new TianBoSdkPrinterTask();
        tbspt.printstr = printstr;
        tbspt.encode = encode;
        tbspt.encode = encode;
        tbspt.callbackContext = callbackContext;
        tbspt.activity = this.cordova.getActivity();
        tbspt.execute();
    }

    private class TianBoSdkPrinterTask extends AsyncTask {
        public CallbackContext callbackContext;
        public String encode = "GBK";
        public String printstr = "";
        public Activity activity;
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected Object doInBackground(Object[] params) {
            String result = "OK";

            try {
                ThermalPrinter.start(activity);
                ThermalPrinter.reset();
                ThermalPrinter.addString(printstr);
                ThermalPrinter.printString();
                ThermalPrinter.clearString();
                ThermalPrinter.walkPaper(100);
            }
            catch (Exception e) {
                e.printStackTrace();
                result = e.toString();
            } 
            finally {
                ThermalPrinter.stop(activity);
            }
            return result;
        }

        @Override
        protected void onPostExecute(Object o) {
            //Alert(o.toString() + "");
            if (o.toString() == "OK") {
                callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, o.toString()));
                callbackContext.success();
            } else {
                callbackContext.error(o.toString());
            }
        }
    }

    private void Alert(String msg) {
        Dialog alertDialog = new AlertDialog.Builder(this.cordova.getActivity()).
        setTitle("对话框的标题").
        setMessage(msg).
        setCancelable(false).
        setNegativeButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
            }
        }).
        create();
        alertDialog.show();
    }
}