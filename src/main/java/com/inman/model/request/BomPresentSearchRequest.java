package com.inman.model.request;

public class BomPresentSearchRequest {
	public static final String findByParent = "bom/findByParent";
	public static final String findById = "bom/findById";
	public static final String all = "bomPresent/all";

	long idToSearchFor;

	public BomPresentSearchRequest() { };

	//  Used with findByParent, findByChild and findById...
	public BomPresentSearchRequest(Long xIdToSearchFor ) {
		idToSearchFor = xIdToSearchFor;
	}

	public long getIdToSearchFor() {
		return idToSearchFor;
	}
	public void setIdToSearchFor( long xId ) {
		idToSearchFor = xId;
	}
}
