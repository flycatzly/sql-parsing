package com.github.flycatzly.sqlparsing.handler;

import com.github.flycatzly.sqlparsing.node.SqlNode;
import org.dom4j.Element;

import java.util.List;

/**
 * @author Ml
 */
public interface NodeHandler {

    void handle(Element element, List<SqlNode> contents);
}
