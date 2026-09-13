package com.github.flycatzly.sqlparsing.handler;

import com.github.flycatzly.sqlparsing.node.SqlNode;
import com.github.flycatzly.sqlparsing.node.VarDeclSqlNode;
import org.dom4j.Element;

import java.util.List;

/**
 * @author Alvin
 */
public class BindHandler implements NodeHandler {
    public BindHandler() {
        // Prevent Synthetic Access
    }
    @Override
    public void handle(Element element, List<SqlNode> contents) {
        String name = element.attributeValue("name");
        String value = element.attributeValue("value");
        if (name == null || value == null) {
            throw new RuntimeException("<if> tag missing test attribute");
        }
        //todo
        final VarDeclSqlNode node = new VarDeclSqlNode(name, value);
        contents.add(node);
    }
}
