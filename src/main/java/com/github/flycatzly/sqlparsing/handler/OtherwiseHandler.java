package com.github.flycatzly.sqlparsing.handler;

import com.github.flycatzly.sqlparsing.node.MixedSqlNode;
import com.github.flycatzly.sqlparsing.node.SqlNode;
import org.dom4j.Element;

import java.util.List;

/**
 * @author Alvin
 */
public class OtherwiseHandler implements NodeHandler {
    public OtherwiseHandler() {
        // Prevent Synthetic Access
    }
    @Override
    public void handle(Element element, List<SqlNode> targetContents) {
        List<SqlNode> contents = XmlParser.parseElement(element);
        targetContents.add(new MixedSqlNode(contents));
    }
}
