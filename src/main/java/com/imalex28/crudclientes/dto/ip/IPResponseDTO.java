package com.imalex28.crudclientes.dto.ip;

public class IPResponseDTO {
	private String ip;
	private String as_name;
	private String as_domain;
	
	private String country;

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getAs_name() {
		return as_name;
	}

	public void setAs_name(String as_name) {
		this.as_name = as_name;
	}

	public String getAs_domain() {
		return as_domain;
	}

	public void setAs_domain(String as_domain) {
		this.as_domain = as_domain;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public IPResponseDTO(String ip, String as_name, String as_domain, String country) {
		this.ip = ip;
		this.as_name = as_name;
		this.as_domain = as_domain;
		this.country = country;
	}

	public IPResponseDTO() {
	}
	
	
}
