package com.github.flycatzly.sqlparsing.utils;

import ognl.Ognl;
import ognl.OgnlContext;
import ognl.OgnlException;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.*;

/**
 * @author Alvin
 */
public class OgnlUtil {

    /**
     * 根据Ongl表达式，获取指定对象的参数值
     * @param expression
     * @param paramObject
     * @return
     */
    public static Object getValue(String expression, Object paramObject) {
        try {
            OgnlContext context = new OgnlContext();
            context.setRoot(paramObject);

            //mybatis中的动态标签使用的是ognl表达式
            //mybatis中的${}使用的是ognl表达式
            //构建Ognl表达式
            Object ognlExpression = Ognl.parseExpression(expression);
            //解析表达式
            Object value = Ognl.getValue(ognlExpression, context, context.getRoot());
            return value;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 通过Ognl表达式，去计算boolean类型的结果
     * @param expression
     * @param parameterObject
     * @return
     */
    public static boolean evaluateBoolean(String expression, Object parameterObject) {
        Object value = OgnlUtil.getValue(expression, parameterObject);
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        if (value instanceof Number) {
            return new BigDecimal(String.valueOf(value)).compareTo(BigDecimal.ZERO) != 0;
        }
        return value != null;
    }

    public static Object getValue(String expression, Map<String, Object> root) {
        try {
            Map context = Ognl.createDefaultContext(root);
            Object value = Ognl.getValue(Ognl.parseExpression(expression), context, root);
            return value;
        } catch (OgnlException e) {
            throw new RuntimeException("400", e);
        }
    }

    public static Boolean getBooleanValue(String expression, Map<String, Object> root) {
        Object value = getValue(expression, root);
        if (value instanceof Boolean) {
            return (Boolean) value;
        } else if (value instanceof Number) {
            return !new BigDecimal(String.valueOf(value)).equals(BigDecimal.ZERO);
        } else {
            throw new RuntimeException("expression value is not boolean or number type: " + expression);
        }
    }

    public static Iterable<?> getIterable(String expression, Map<String, Object> root) {
        Object value = getValue(expression, root);
        if (value == null) {
            throw new RuntimeException("The expression '" + expression + "' evaluated to a null value.");
        }
        if (value instanceof Iterable) {
            return (Iterable<?>) value;
        }
        if (value.getClass().isArray()) {
            // the array may be primitive, so Arrays.asList() may throw
            // a ClassCastException (issue 209). Do the work manually
            // Curse primitives! :) (JGB)
            int size = Array.getLength(value);
            List<Object> answer = new ArrayList<Object>();
            for (int i = 0; i < size; i++) {
                Object o = Array.get(value, i);
                answer.add(o);
            }
            return answer;
        }
        if (value instanceof Map) {
            return ((Map) value).entrySet();
        }else if(value instanceof String){
            return Arrays.asList(value.toString().split(","));
        }
        throw new RuntimeException("Error evaluating expression '" + expression + "'.  Return value (" + value + ") was not iterable.");
    }

    public static void main(String[] args) {
        Map<String, Object> root = new HashMap<>();
        List<Integer> list = new ArrayList<>();
        list.add(12);
        list.add(22);
        list.add(32);
        list.add(42);
        root.put("ids", list);

        Object o = getValue("ids[3]", root);
        System.out.println(o);

    }
}
