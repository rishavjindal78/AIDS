package org.shunya.server.controller;

import org.shunya.server.model.Team;
import org.shunya.server.model.User;
import org.shunya.server.services.DBService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private DBService dbService;

    @RequestMapping(value = "team/index", method = RequestMethod.GET)
    public String teamList(@ModelAttribute("model") ModelMap model) {
        List<Team> teams = dbService.listTeams();
        model.addAttribute("teams", teams);
        return "teams";
    }

    @RequestMapping(value = "team/create", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public String createTeam(@ModelAttribute("team") Team team) {
        dbService.save(team);
        return "redirect:index";
    }

    @RequestMapping(value = "team/update", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public String updateTeam(@ModelAttribute("team") Team team) {
        dbService.update(team);
        return "redirect:index";
    }

    @RequestMapping(value = "team/update/{teamId}", method = RequestMethod.GET)
    public String editAgent(@ModelAttribute("model") ModelMap model, @PathVariable("teamId") long teamId) throws Exception {
        Team team = dbService.findTeamById(teamId);
        model.addAttribute("team", team);
        return "editTeam";
    }

    @RequestMapping(value = "team/{teamId}", method = RequestMethod.GET)
    public String team(@ModelAttribute("model") ModelMap model, @PathVariable("teamId") long teamId) {
        Team team = dbService.findTeamById(teamId);
        model.addAttribute("team", team);
        return "team";
    }

    @RequestMapping(value = "team/{teamId}/addUser", method = RequestMethod.GET)
    public String addAgent(@ModelAttribute("model") ModelMap model, @PathVariable("teamId") String teamId) throws Exception {
        model.addAttribute("users", dbService.listUser());
        model.addAttribute("teamId", teamId);
        return "addUser";
    }

    @RequestMapping(value = "team/{teamId}/addUser", method = RequestMethod.POST)
    public String addAgentPOST(@ModelAttribute("user") User user, @PathVariable("teamId") long teamId) throws Exception {
        Team team = dbService.findTeamById(teamId);
        User existingUser = dbService.getUser(user.getId());
        team.getUserList().add(existingUser);
        dbService.save(team);
        existingUser.getTeamList().add(team);
        dbService.save(existingUser);
        return "redirect:../index";
    }
}
