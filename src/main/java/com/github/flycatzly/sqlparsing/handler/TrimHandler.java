package com.github.flycatzly.sqlparsing.handler;

import com.github.flycatzly.sqlparsing.node.SqlNode;
import com.github.flycatzly.sqlparsing.node.TrimSqlNode;
import com.github.flycatzly.sqlparsing.node.MixedSqlNode;
import org.dom4j.Element;

import java.util.Arrays;
import java.util.List;


public class TrimHandler implements NodeHandler {

    @Override
    public void handle(Element element, List<SqlNode> targetContents) {
        String prefix = element.attributeValue("prefix");
        String suffix = element.attributeValue("suffix");
        String prefixesToOverride = element.attributeValue("prefixesToOverride");
        List<String> prefixesOverride = prefixesToOverride == null ? null : Arrays.asList(prefixesToOverride.split("\\|"));
        String suffixesToOverride = element.attributeValue("suffixesToOverride");
        List<String> suffixesOverride = suffixesToOverride == null ? null : Arrays.asList(suffixesToOverride.split("\\|"));

        List<SqlNode> contents = XmlParser.parseElement(element);
        TrimSqlNode trimSqlNode = new TrimSqlNode(new MixedSqlNode(contents), prefix, suffix, prefixesOverride, suffixesOverride);
        targetContents.add(trimSqlNode);
    }
}
