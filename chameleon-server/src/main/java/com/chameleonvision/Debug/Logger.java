package com.chameleonvision.Debug;

import com.chameleonvision.Main;

public class Logger {
    private Logger() {}

    private static boolean isTestMode() {
        return Main.testMode;
    }

    public static void Log(String infoMessage) {
        if (isTestMode()) {
            System.out.println(infoMessage);
        }
    }

    public static void Log(String smallInfo, String largeInfo) {
        System.out.println(isTestMode() ? String.format("%s - %s" , smallInfo, largeInfo) : smallInfo);
    }
    public static void Log(String info, Color color){
        Log( color.getValue() + info + Colors.kReset.getValue());
    }
    public static void LogErr(String TAG,String err){
        System.err.println(Colors.kRed.getValue()+"Error In ["+TAG+"]: \n "+err+Colors.kReset.getValue());
    }
}
