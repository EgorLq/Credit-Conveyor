package com.neoflex.java.util;

import lombok.extern.log4j.Log4j2;
/**
 * Универсальный логгер
 */
@Log4j2
public class CustomLogger {
    private CustomLogger() {}

    /**
     * Функция вывода информации о вызванном методе и классе, из которого метод вызван
     */
    public static void logInfoClassAndMethod() {
        log.info("В классе " + new Exception().getStackTrace()[1].getClassName() + " вызван метод: " + new Exception().getStackTrace()[1].getMethodName());
    }

    /**
     * Функция вывода информации о поступившем запросе (объекте)
     */
    public static void logInfoRequest(Object request) {
        log.info("Запрос: " + request);
    }
}
