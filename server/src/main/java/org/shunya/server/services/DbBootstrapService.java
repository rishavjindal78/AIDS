package org.shunya.server.services;

import org.shunya.server.model.Authority;
import org.shunya.server.model.User;
import org.shunya.server.model.UserBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

import static java.util.Arrays.asList;

@Service
public class DbBootstrapService {

    @Autowired
    private DBService dbService;

    @PostConstruct
    public void init() {
        loadAuthorities();
        loadDefaultUsers();
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

    public void loadDefaultUsers() {
        Authority role_admin = dbService.findByName("ROLE_ADMIN");
        User adminUser = UserBuilder.anUser().withName("Admin").withUsername("admin").withPassword("admin").withAuthorities(asList(role_admin)).withEnabled(true).build();
        try {
            dbService.save(adminUser);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Authority role_user = dbService.findByName("ROLE_USER");
        User user = UserBuilder.anUser().withName("Admin").withUsername("user").withPassword("user").withAuthorities(asList(role_user)).withEnabled(true).build();
        try {
            dbService.save(user);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Authority role_agent = dbService.findByName("ROLE_AGENT");
        User agentUser = UserBuilder.anUser().withName("Admin").withUsername("agent").withPassword("agent").withAuthorities(asList(role_agent)).withEnabled(true).build();
        try {
            dbService.save(agentUser);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}