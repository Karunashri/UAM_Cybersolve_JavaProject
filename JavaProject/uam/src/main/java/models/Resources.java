package models;

public class Resources {
	
	private long resourceId;
	private String resourceName;
	
	public Resources() {
		super();
	}

	public Resources(long resourceId, String resourceName) {
		super();
		this.resourceId = resourceId;
		this.resourceName = resourceName;
	}

	public long getResourceId() {
		return resourceId;
	}

	public void setResourceId(long resourceId) {
		this.resourceId = resourceId;
	}

	public String getResourceName() {
		return resourceName;
	}

	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}

	@Override
	public String toString() {
		return "Resources [resourceId=" + resourceId + ", resourceName=" + resourceName + "]";
	}
	
	
	
	

}
