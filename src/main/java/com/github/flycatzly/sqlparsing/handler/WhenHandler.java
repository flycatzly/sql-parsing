package com.github.flycatzly.sqlparsing.handler;

import com.github.flycatzly.sqlparsing.node.SqlNode;
import com.github.flycatzly.sqlparsing.node.IfSqlNode;
import com.github.flycatzly.sqlparsing.node.MixedSqlNode;
import org.dom4j.Element;

import java.util.List;


public class WhenHandler implements NodeHandler {
    public WhenHandler() {
        // Prevent Synthetic Access
    }
    @Override
    public void handle(Element element, List<SqlNode> targetContents) {
        String test = element.attributeValue("test");
        if (test == null) {
            throw new RuntimeException("<if> tag missing test attribute");
        }
        List<SqlNode> contents = XmlParser.parseElement(element);
        IfSqlNode ifSqlNode = new IfSqlNode(test, new MixedSqlNode(contents));
        targetContents.add(ifSqlNode);
    }
}
