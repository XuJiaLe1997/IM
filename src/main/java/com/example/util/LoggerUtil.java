package com.example.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Description: 自定义日志打印工具
 * Date: 2018-11-04
 */
public class LoggerUtil {

    private static final String TAG = "<-- Debug Logger -->";

    private static Logger logger = LoggerFactory.getLogger(TAG);

    private static final int level = SystemConstant.LOGGER_ON;
    // private static final int level = SystemConstant.LOGGER_OFF;

    /**
     * 打印信息请用此方法，便于生产部署后控制打印信息
     * @param msg 打印内容
     */
    public static void log(String msg) {
        if (level == SystemConstant.LOGGER_OFF) {
            // 日志系统关闭
            return;
        }
        logger.info(msg);
    }
}
