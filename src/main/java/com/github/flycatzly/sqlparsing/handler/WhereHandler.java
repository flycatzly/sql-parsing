package com.github.flycatzly.sqlparsing.handler;

import com.github.flycatzly.sqlparsing.node.MixedSqlNode;
import com.github.flycatzly.sqlparsing.node.SqlNode;
import com.github.flycatzly.sqlparsing.node.WhereSqlNode;
import org.dom4j.Element;

import java.util.List;


public class WhereHandler implements NodeHandler {

    //todo 多个下自己在条件里写连接符 “and”
    @Override
    public void handle(Element element, List<SqlNode> targetContents) {
        List<SqlNode> contents = XmlParser.parseElement(element);
        WhereSqlNode node = new WhereSqlNode(new MixedSqlNode(contents));
        targetContents.add(node);
    }
}
