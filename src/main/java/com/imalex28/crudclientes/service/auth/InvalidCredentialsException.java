
package com.imalex28.crudclientes.service.auth;

public class InvalidCredentialsException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public InvalidCredentialsException() {
        super("Credenciales inválidas");
    }
}
