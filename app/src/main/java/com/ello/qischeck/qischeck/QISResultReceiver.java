package com.ello.qischeck.qischeck;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

public class QISResultReceiver<T> extends ResultReceiver {

    static final int RESULT_CODE_OK = 1100;
    private static final int RESULT_CODE_ERROR = 666;
    private static final String PARAM_EXCEPTION = "exception";
    static final String PARAM_RESULT = "result";

    private ResultReceiverCallBack mReceiver;

    QISResultReceiver(Handler handler) {
        super(handler);
    }

    void setReceiver(ResultReceiverCallBack<T> receiver) {
        mReceiver = receiver;
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        if (mReceiver != null) {
            if(resultCode == RESULT_CODE_OK){
                mReceiver.onSuccess(resultData.getSerializable(PARAM_RESULT));
            }
            if(resultCode == RESULT_CODE_ERROR){
                mReceiver.onError((Exception) resultData.getSerializable(PARAM_EXCEPTION));
            }
        }
    }

    public interface ResultReceiverCallBack<T>{
        void onSuccess(T data);
        void onError(Exception exception);
    }
 }