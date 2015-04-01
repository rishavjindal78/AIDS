package org.shunya.server.services;

import org.shunya.server.Role;
import org.shunya.server.model.Authority;
import org.shunya.server.model.Task;
import org.shunya.server.model.User;
import org.shunya.server.UserBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

import java.util.List;

import static java.util.Arrays.asList;

@Service
public class DbBootstrapService {

    @Autowired
    private DBService dbService;

    @Autowired
    private MyJobScheduler myJobScheduler;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    @PostConstruct
    public void init() {
        loadAuthorities();
        loadDefaultUsers();
        scheduleTasks();
    }

    public void scheduleTasks(){
        List<Task> tasks = dbService.listTasks();
        tasks.stream()
                .filter(task -> task.getSchedule()!= null && !task.getSchedule().isEmpty())
                .forEach(task -> myJobScheduler.schedule(task.getSchedule(), task.getId()));
    }

    public void loadAuthorities() {
        String[] roles = {Role.ROLE_AGENT, Role.ROLE_ADMIN, Role.ROLE_GUEST, Role.ROLE_USER};
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
        Authority role_admin = dbService.findAuthorityByName(Role.ROLE_ADMIN);
        User adminUser = UserBuilder.anUser().withName("Admin").withUsername("admin").withPassword(passwordEncoder.encode("admin")).withAuthorities(asList(role_admin)).withEnabled(true).build();
        try {
            dbService.save(adminUser);
        } catch (Exception e) {
            User admin = dbService.findUserByUsername("admin");
            if(admin.getPassword().equalsIgnoreCase("admin")){
                admin.setPassword(passwordEncoder.encode("admin"));
                dbService.save(admin);
            }
            e.printStackTrace();
        }

        Authority role_user = dbService.findAuthorityByName(Role.ROLE_USER);
        User user = UserBuilder.anUser().withName("User").withUsername("user").withPassword(passwordEncoder.encode("user")).withAuthorities(asList(role_user)).withEnabled(true).build();
        try {
            dbService.save(user);
        } catch (Exception e) {
            User userByUsername = dbService.findUserByUsername("user");
            if(userByUsername.getPassword().equalsIgnoreCase("user")){
                userByUsername.setPassword(passwordEncoder.encode("user"));
                dbService.save(userByUsername);
            }
            e.printStackTrace();
        }

        Authority role_agent = dbService.findAuthorityByName(Role.ROLE_AGENT);
        User agentUser = UserBuilder.anUser().withName("Agent").withUsername("agent").withPassword(passwordEncoder.encode("agent")).withAuthorities(asList(role_agent)).withEnabled(true).build();
        try {
            dbService.save(agentUser);
        } catch (Exception e) {
            User userByUsername = dbService.findUserByUsername("agent");
            if(userByUsername.getPassword().equalsIgnoreCase("agent")){
                userByUsername.setPassword(passwordEncoder.encode("agent"));
                dbService.save(userByUsername);
            }
            e.printStackTrace();
        }
    }
}
