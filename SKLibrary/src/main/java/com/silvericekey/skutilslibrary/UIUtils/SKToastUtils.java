package com.silvericekey.skutilslibrary.UIUtils;

import android.widget.Toast;

import com.silvericekey.skutilslibrary.SKUtilsLibrary;

/**
 * Created by silverknife on 2017/12/13.
 */

public class SKToastUtils {
    public static void showToast(String msg){
        Toast.makeText(SKUtilsLibrary.getContext(),msg,Toast.LENGTH_SHORT).show();
    }
}
