package com.inman.model.request;

public class BomSearchRequest {
	public static final String findByParent = "bom/findByParent";
	public static final String findById = "bom/findById";
	public static final String all = "bom/all";
	public static final String findUsingItemParameters = "bom/findItemParameters";

	Long idToSearchFor;

	public BomSearchRequest() { };

	//  Used with findByParent, findByChild and findById...
	public BomSearchRequest(Long xIdToSearchFor ) {
		idToSearchFor = xIdToSearchFor;
	}

	public Long getIdToSearchFor() {
		return idToSearchFor;
	}
	public void setIdToSearchFor( Long xId ) {
		idToSearchFor = xId;
	}


}
