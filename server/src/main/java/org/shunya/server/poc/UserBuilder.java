package org.shunya.server.poc;

import org.shunya.server.model.Authority;
import org.shunya.server.model.Team;
import org.shunya.server.model.User;

import java.util.List;

public class UserBuilder {
    private String username;
    private String password;
    private boolean enabled;
    private String name;
    private String email;
    private String phone;
    private String telegramId;
    private List<Team> teamList;
    private List<Authority> authorities;

    private UserBuilder() {
    }

    public static UserBuilder anUser() {
        return new UserBuilder();
    }

    public UserBuilder withUsername(String username) {
        this.username = username;
        return this;
    }

    public UserBuilder withPassword(String password) {
        this.password = password;
        return this;
    }

    public UserBuilder withEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public UserBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public UserBuilder withEmail(String email) {
        this.email = email;
        return this;
    }

    public UserBuilder withPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public UserBuilder withTelegramId(String telegramId) {
        this.telegramId = telegramId;
        return this;
    }

    public UserBuilder withTeamList(List<Team> teamList) {
        this.teamList = teamList;
        return this;
    }

    public UserBuilder withAuthorities(List<Authority> authorities) {
        this.authorities = authorities;
        return this;
    }

    public UserBuilder but() {
        return anUser().withUsername(username).withPassword(password).withEnabled(enabled).withName(name).withEmail(email).withPhone(phone).withTelegramId(telegramId).withTeamList(teamList).withAuthorities(authorities);
    }

    public User build() {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setEnabled(enabled);
        user.setName(name);
        user.setEmail(email);
        user.setPhone(phone);
        user.setTelegramId(telegramId);
        user.setTeamList(teamList);
        user.setAuthorities(authorities);
        return user;
    }
}
