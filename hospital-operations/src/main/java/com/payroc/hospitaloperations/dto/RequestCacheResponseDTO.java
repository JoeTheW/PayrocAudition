package com.payroc.hospitaloperations.dto;

public class RequestCacheResponseDTO
{
	private Integer responseCode;
    private Object responseBody;
    
    public RequestCacheResponseDTO()
    {}
    
	public RequestCacheResponseDTO(Integer responseCode, Object responseBody) {
		super();
		this.responseCode = responseCode;
		this.responseBody = responseBody;
	}
	
	public Integer getResponseCode() {
		return responseCode;
	}
	public void setResponseCode(final Integer responseCode) {
		this.responseCode = responseCode;
	}
	public Object getResponseBody() {
		return responseBody;
	}
	public void setResponseBody(final Object responseBody) {
		this.responseBody = responseBody;
	}
    
    
}