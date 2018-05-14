package com.cabily.cabilydriver.Interface;

/**
 * Created by user88 on 7/12/2017.
 */

public interface CallBack {
    void onComplete(String LocationName);

    void onError(String errorMsg);
}