package org.shunya.server;

import org.shunya.server.model.User;
import org.shunya.server.services.DBService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AuthenticationInterceptor implements HandlerInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationInterceptor.class);

    @Autowired
    private DBService dbService;

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        if (httpServletRequest.getSession().getAttribute("SPRING_SECURITY_CONTEXT") != null) {
            String username = ((SecurityContext) httpServletRequest.getSession().getAttribute("SPRING_SECURITY_CONTEXT")).getAuthentication().getName();
            if (httpServletRequest.getSession().getAttribute("SELECTED_TEAM") == null) {
                User byUsername = dbService.findUserByUsername(username);
                httpServletRequest.getSession().setAttribute("LOGGED_USER", byUsername);
                if (byUsername.getTeamList().size() > 0)
                    httpServletRequest.getSession().setAttribute("SELECTED_TEAM", byUsername.getTeamList().get(0));
                logger.debug("Name of the user = " + byUsername.getName());
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
