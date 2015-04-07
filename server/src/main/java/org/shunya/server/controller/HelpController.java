package org.shunya.server.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by munichan on 4/6/2015.
 */
@Controller
@RequestMapping("/topicHelp")
public class HelpController {

    @RequestMapping(value = "/{topic}", method = RequestMethod.GET)
    public String registerUser(@ModelAttribute("model") ModelMap model, @PathVariable("topic") String topic) {
        model.addAttribute("helpFileName", topic + ".markdown");
        return "help";
    }
}
