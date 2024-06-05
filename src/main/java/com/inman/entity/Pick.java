package com.inman.entity;

import jakarta.persistence.Entity;

@Entity
public class Pick extends EntityMaster {

	public Pick(long id, String external ) {
		this.id = id;
		this.external = external;
	}

	protected String external;

	public Pick() {
	}

	@Override
	public EntityMaster copy(EntityMaster oldValue) {
		assert oldValue != null;
		Pick pick = new Pick();
		pick.external = external;
		return pick;

	}

	public String getExternal() {
		return external;
	}

	public void setExternal(String external ) {
		this.external = external;
	}

	public static String formatExternalFromSummaryDescription( String summary, String description ) {
		assert summary != null;
		assert description != null;

		return summary + ", " + description;
	}
}
