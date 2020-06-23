package com.bookmyride.demo.dao.impl;

/**
 * 
 * @author seeya.wamane
 *
 */
public class QueryParams {
	
	StringBuilder query=null;
	Object[] params = null;
	int[] paramType = null;
	
	public QueryParams(StringBuilder query, Object[] params, int[] paramType) {
		super();
		this.query = query;
		this.params = params;
		this.paramType = paramType;
	}

	public StringBuilder getQuery() {
		return query;
	}

	public void setQuery(StringBuilder query) {
		this.query = query;
	}

	public Object[] getParams() {
		return params;
	}

	public void setParams(Object[] params) {
		this.params = params;
	}

	public int[] getParamType() {
		return paramType;
	}

	public void setParamType(int[] paramType) {
		this.paramType = paramType;
	}
	
}
