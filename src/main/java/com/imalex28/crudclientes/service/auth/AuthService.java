package com.imalex28.crudclientes.service.auth;

/*
 * Interface for exposing Service to other layers.
 */
public interface AuthService {
    String loginAndIssueToken(String username, String password) throws InvalidCredentialsException;
}
