package com.imalex28.crudclientes.dto.auth;

import jakarta.annotation.Nonnull;

public class LoginResponseDTO {
	@Nonnull
    public String token;

	public LoginResponseDTO(String token) {
		this.token = token;
	}

	public LoginResponseDTO() {
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

}
