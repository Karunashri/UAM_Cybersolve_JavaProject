package models;

import java.sql.Timestamp;

public class Request {
	
	private long requestId;
	private String requirement;
	private String requestor;
	private String requestStatus;
	
	
	public Request() {
		super();
	}

	public Request(long requestId, String requestor, String requirement, String requestStatus) {
		super();
		this.requestId = requestId;
		this.requirement = requirement;
		this.requestor = requestor;
		this.requestStatus = requestStatus;
		
	}

	public long getRequestId() {
		return requestId;
	}

	public void setRequestId(long requestId) {
		this.requestId = requestId;
	}

	public String getRequirement() {
		return requirement;
	}

	public void setRequirement(String requirement) {
		this.requirement = requirement;
	}

	public String getRequestor() {
		return requestor;
	}

	public void setRequestor(String requestor) {
		this.requestor = requestor;
	}

	public String getRequestStatus() {
		return requestStatus;
	}

	public void setRequestStatus(String requestStatus) {
		this.requestStatus = requestStatus;
	}

	

	@Override
	public String toString() {
		return "Request [requestId=" + requestId + ", requirement=" + requirement + ", requestor=" + requestor
				+ ", requestStatus=" + requestStatus + ", requestDate="+ "]";
	}
	
	

}
