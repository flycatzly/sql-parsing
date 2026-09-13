package com.github.flycatzly.sqlparsing.node;

import com.github.flycatzly.sqlparsing.token.TokenHandler;
import com.github.flycatzly.sqlparsing.token.TokenParser;
import com.github.flycatzly.sqlparsing.utils.OgnlUtil;
import com.github.flycatzly.sqlparsing.context.Context;
import org.apache.commons.lang3.StringUtils;

import java.util.*;


public class ForeachSqlNode implements SqlNode {

    //数据集合，支持Collection、数组
    private String collection;
    //拼接起始SQL
    private String open;
    //拼接结束SQL
    private String close;
    //分隔符
    private String separator;
    //item 变量名
    private String item;

    private String index;
    private SqlNode contents;
    private String indexDataName;

    public ForeachSqlNode(String collection, String open, String close, String separator, String item, String index, SqlNode contents) {
        this.collection = collection;
        this.open = open;
        this.close = close;
        this.separator = separator;
        this.item = item;
        this.index = index;
        this.contents = contents;
        this.indexDataName = String.format("__index_%s", collection);
    }

    @Override
    public void apply(Context context) {
        //标签类SqlNode先拼接空格，和前面的内容隔开
        context.appendSql(" ");
        Iterable<?> iterable = OgnlUtil.getIterable(collection, context.getData());
        int currentIndex = 0;
        ArrayList<Integer> indexs = new ArrayList<>();
        context.getData().put(indexDataName, indexs);
        context.appendSql(open);
        for (Object o : iterable) {
            ((ArrayList<Integer>) context.getData().get(indexDataName)).add(currentIndex);
            //不是第一次，需要拼接分隔符
            if (currentIndex != 0) {
                context.appendSql(separator);
            }
            Context proxy = new Context(context.getData());
            String childSqlText = getChildText(proxy, currentIndex);
            context.appendSql(childSqlText);
            currentIndex++;
        }
        context.appendSql(close);

    }

    @Override
    public void applyParameter(Set<String> set) {
        set.add(collection);
        Set<String> temp = new HashSet<>();
        contents.applyParameter(set);
        for (String key: temp){
            if (key.matches(item + "[.,:\\s\\[]")) {
                continue;
            }
            if (key.matches(index + "[.,:\\s\\[]")) {
                continue;
            }
            set.add(key);
        }
    }

    public String getChildText(Context proxy, int currentIndex) {
        //ognl可以直接获取  aaa[0]  形式的值
        String newItem = String.format("%s[%d]", collection, currentIndex);
        String newIndex = String.format("%s[%d]", indexDataName, currentIndex);
        this.contents.apply(proxy);
        String sql = proxy.getSql();
        if(StringUtils.isBlank(sql)){
            List<String> mapList = (List<String>) proxy.getData().get(collection);
            if(!isEmpty(mapList)){
                proxy.addParameter(mapList.get(currentIndex));
                return mapList.get(currentIndex);
            }
        }
        TokenParser tokenParser = new TokenParser("#{", "}", new TokenHandler() {
            @Override
            public String handleToken(String content) {
                //item替换成自己的变量名: item[0]  item[1] item[2] ......
                String replace = ForeachSqlNode.replace(content, item, newItem);
                if (replace.equals(content)) {
                    //index替换成自己的变量名: __index_xxx[0]  __index_xxx[1] __index_xxx[2] ......
                    replace = ForeachSqlNode.replace(content, index, newIndex);
                }
                StringBuilder builder = new StringBuilder();
                return builder.append("#{").append(replace).append("}").toString();
            }
        });
        return tokenParser.parse(sql);
    }

    public static String replace(String content, String item, String newItem) {
        return content.replaceFirst("^\\s*" + item + "(?![^.,:\\s])", newItem);
    }

    /**
     * Return {@code true} if the supplied Collection is {@code null} or empty.
     * Otherwise, return {@code false}.
     * @param collection the Collection to check
     * @return whether the given Collection is empty
     */
    public static boolean isEmpty(Collection<?> collection) {
        return (collection == null || collection.isEmpty());
    }

    /**
     * Return {@code true} if the supplied Map is {@code null} or empty.
     * Otherwise, return {@code false}.
     * @param map the Map to check
     * @return whether the given Map is empty
     */
    public static boolean isEmpty(Map<?, ?> map) {
        return (map == null || map.isEmpty());
    }

}
