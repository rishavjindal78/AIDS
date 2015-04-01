package org.shunya.server.controller;

import org.shunya.server.Role;
import org.shunya.server.model.Authority;
import org.shunya.server.model.User;
import org.shunya.server.services.DBService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static java.util.Arrays.asList;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private DBService dbService;

    @Autowired
    private HttpServletRequest request;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @RequestMapping(value = "register", method = RequestMethod.GET)
    public String registerUser() {
        return "registerUser";
    }

    @RequestMapping(value = "register", method = RequestMethod.POST)
    public String registerUser(@ModelAttribute("user") User user) throws Exception {
        user.setAuthorities(asList(dbService.findAuthorityByName(Role.ROLE_USER)));
        user.setEnabled(true);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        dbService.save(user);
        return "redirect:/server";
    }

    @RequestMapping(value = "update", method = RequestMethod.POST)
    public String updateUser(@ModelAttribute("user") User user) throws Exception {
        dbService.update(user);
        return "redirect:/server";
    }

    @RequestMapping(value = "login", method = RequestMethod.GET)
    public String login(@ModelAttribute("model") ModelMap model) {
        return "login";
    }

    @RequestMapping(value = "profile/{username}", method = RequestMethod.GET)
    public String profile(@ModelAttribute("model") ModelMap model, @PathVariable("username") String username) {
        User user = dbService.findUserByUsername(username);
        model.addAttribute("user", user);
        return "profile";
    }
}
