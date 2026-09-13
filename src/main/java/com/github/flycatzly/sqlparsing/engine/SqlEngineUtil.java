package com.github.flycatzly.sqlparsing.engine;

/**
 * @author Alvin
 */
public class SqlEngineUtil {

    public static DynamicSqlEngine engine = new DynamicSqlEngine();

    public static DynamicSqlEngine getEngine() {
        return engine;
    }

    public static void put(String text) {
        engine.put(text);
    }
    public static void destroy() {
        engine.destroy();
    }
    public static void destroy(String text) {
        engine.destroy(text);
    }
}
