package com.inman.model.rest;

public class PrepareResponse {

	public final static String rootUrl = "/prepare";
	
	public PrepareResponse( String entityType, int entityCount ) {
		this.entityType = entityType;
		this.entityCount = entityCount;
	}

	private String entityType;
	private int entityCount;
	
	public String getEntityType() {
		return entityType;
	}
	public void setEntityType(String entityType) {
		this.entityType = entityType;
	}
	public int getEntityCount() {
		return entityCount;
	}
	public void setEntityCount(int entityCount) {
		this.entityCount = entityCount;
	}

}
