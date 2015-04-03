package org.shunya.server.controller;

import org.shunya.server.model.Team;
import org.shunya.server.model.User;
import org.shunya.server.services.DBService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    private DBService dbService;

    @RequestMapping(value = "team/index", method = GET)
    public String teamList(@ModelAttribute("model") ModelMap model) {
        List<Team> teams = dbService.listTeams();
        model.addAttribute("teams", teams);
        return "teams";
    }

    @RequestMapping(value = "team/create", method = POST)
    @ResponseStatus(HttpStatus.CREATED)
    public String createTeam(@ModelAttribute("team") Team team) {
        dbService.save(team);
        return "redirect:index";
    }

    @RequestMapping(value = "team/update", method = POST)
    @ResponseStatus(HttpStatus.CREATED)
    public String updateTeam(@ModelAttribute("team") Team team) {
        dbService.update(team);
        return "redirect:index";
    }

    @RequestMapping(value = "team/update/{teamId}", method = GET)
    public String editAgent(@ModelAttribute("model") ModelMap model, @PathVariable("teamId") long teamId) throws Exception {
        Team team = dbService.findTeamById(teamId);
        model.addAttribute("team", team);
        return "editTeam";
    }

    @RequestMapping(value = "team/{teamId}", method = GET)
    public String team(@ModelAttribute("model") ModelMap model, @PathVariable("teamId") long teamId) {
        Team team = dbService.findTeamById(teamId);
        model.addAttribute("team", team);
        return "team";
    }

    @RequestMapping(value = "team/{teamId}/addUser", method = GET)
    public String addAgent(@ModelAttribute("model") ModelMap model, @PathVariable("teamId") String teamId) throws Exception {
        model.addAttribute("users", dbService.listUser());
        model.addAttribute("teamId", teamId);
        return "addUser";
    }

    @RequestMapping(value = "team/{teamId}/addUser", method = POST)
    public String addAgentPOST(@ModelAttribute("user") User user, @PathVariable("teamId") long teamId) throws Exception {
        dbService.addTeamToUser(user, teamId);
        return "redirect:../index";
    }

    @RequestMapping(value = "settings", method = GET)
    public String getSettings(@ModelAttribute("model") ModelMap model) {
        return "settings";
    }
}
