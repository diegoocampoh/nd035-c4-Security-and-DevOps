package com.example.demo.security;

public interface SecurityConstants {
    String SECRET = "H@McQfTjWmZq4t7w!z%C*F-JaNdRgUkX";
    long EXPIRATION_TIME = 864_000_000; // 10 days
    String TOKEN_PREFIX = "Bearer ";
    String HEADER_STRING = "Authorization";
    String SIGN_UP_URL = "/api/user/create";
}
