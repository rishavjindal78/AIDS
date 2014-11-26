package org.shunya.server.services;

import org.shunya.server.model.Authority;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class DbBootstrapService {

    @Autowired
    private DBService dbService;

    @PostConstruct
    public void init() {
        loadAuthorities();
    }

    public void loadAuthorities() {
        String[] roles = {"ROLE_USER", "ROLE_ADMIN", "ROLE_AGENT", "ROLE_GUEST"};
        for (String role : roles) {
            try {
                Authority authority = new Authority();
                authority.setRole(role);
                dbService.save(authority);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
