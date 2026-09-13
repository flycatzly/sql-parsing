package com.github.flycatzly.sqlparsing.handler;

import com.github.flycatzly.sqlparsing.node.ForeachSqlNode;
import com.github.flycatzly.sqlparsing.node.MixedSqlNode;
import com.github.flycatzly.sqlparsing.node.SqlNode;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Element;

import java.util.List;


/**
 * @author Ml
 */
public class ForeachHandler implements NodeHandler {

    public ForeachHandler() {
        // Prevent Synthetic Access
    }

    @Override
    public void handle(Element element, List<SqlNode> targetContents) {
        List<SqlNode> contents = XmlParser.parseElement(element);
        String collection = element.attributeValue("collection");
        String item = element.attributeValue("item");
        String index = element.attributeValue("index");
        String open = element.attributeValue("open");
        String close = element.attributeValue("close");
        String separator = element.attributeValue("separator");

        if (StringUtils.isBlank(collection)) {
            throw new RuntimeException("<foreach> attribute missing : collection");
        }
        if (StringUtils.isBlank(item)) {
            item = "item";
        }
        if (StringUtils.isBlank(index)) {
            index = "index";
        }

        ForeachSqlNode foreachSqlNode = new ForeachSqlNode(collection, open, close, separator, item, index, new MixedSqlNode(contents));
        targetContents.add(foreachSqlNode);

    }
}
