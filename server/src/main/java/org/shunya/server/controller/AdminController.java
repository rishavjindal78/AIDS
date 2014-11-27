package org.shunya.server.controller;

import org.shunya.server.model.Team;
import org.shunya.server.services.DBService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;

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
}
