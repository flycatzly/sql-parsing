package com.github.flycatzly.sqlparsing.node;

import com.github.flycatzly.sqlparsing.context.Context;
import com.github.flycatzly.sqlparsing.utils.OgnlUtil;

import java.util.List;
import java.util.Set;

public class ChooseNode implements SqlNode {
	//<otherwise>标签对应的SqlNode对象
	private SqlNode defaultSqlNode;
	//所有<when>标签对应的SqlNode，每个<when>标签通过IfSqlNode描述
	private List<SqlNode> ifSqlFragments;

	public ChooseNode(List<SqlNode> ifSqlFragments, SqlNode sqlNode) {
		this.ifSqlFragments = ifSqlFragments;
		this.defaultSqlNode = sqlNode;
	}

	@Override
	public void applyParameter(Set<String> set) {
		ifSqlFragments.forEach(item->item.applyParameter(set));
		if (defaultSqlNode != null) {
			defaultSqlNode.applyParameter(set);
		}
	}

	@Override
	public void apply(Context context) {
		//依次处理if标签，但凡有一个if标签的ognl表达式为true，则直接返回
		for (SqlNode item : ifSqlFragments) {
			if(item instanceof IfSqlNode){
				Boolean flag = OgnlUtil.getBooleanValue(((IfSqlNode) item).getTest(),context.getData());
				if(flag){
					defaultSqlNode =null;
					item.apply(context);
					break;
				}
			}
		}

		if (defaultSqlNode != null) {
			defaultSqlNode.apply(context);
		}
	}

}
