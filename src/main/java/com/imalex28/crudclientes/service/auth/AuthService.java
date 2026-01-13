package com.imalex28.crudclientes.service.auth;

public interface AuthService {
    String loginAndIssueToken(String username, String password) throws InvalidCredentialsException;
}
