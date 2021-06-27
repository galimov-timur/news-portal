package kz.epam.newsportal.model;

import java.io.Serializable;

public class JwtToken implements Serializable {
    private String token;

    public JwtToken() {
    }

    public JwtToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
