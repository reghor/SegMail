/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.bootstrap;

import eds.entity.layout.Layout;
import eds.entity.navigation.MenuItem;
import eds.entity.program.Program;
import java.io.Serializable;
import javax.enterprise.context.SessionScoped;
import eds.entity.user.User;
import eds.entity.user.UserType;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Named;
import seca2.bootstrap.module.Navigation.MenuItemContainer;

/**
 *
 * @author LeeKiatHaw
 */
@Named("UserSessionContainer")
@SessionScoped
public class UserSessionContainer implements Serializable {
    
    private User user;
    private UserType userType;
    private String lastProgram;
    private boolean loggedIn; //default is always false
    private String sessionId;
    
    private String contextPath;
    private String servletPath;
    
    private Program currentProgram;
    private Layout currentLayout;
    
    private List<MenuItemContainer> menu;
    
    
    
    @PostConstruct
    public void init(){
        
    }
    
    public String getLastURL(){
        return this.contextPath + this.servletPath + "/"+ this.getLastProgram() + "/"; //Giving nullnull
    }
    
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public String getLastProgram() {
        return lastProgram;
    }

    public void setLastProgram(String lastProgram) {
        this.lastProgram = lastProgram;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getUsername() {
        
        return (user != null) ? user.getOBJECT_NAME() : "";
    }
    
    public String getUsertypeName() {
        return (userType != null) ? userType.getUSERTYPENAME() : "";
    }

    public Program getCurrentProgram() {
        return currentProgram;
    }

    public void setCurrentProgram(Program currentProgram) {
        this.currentProgram = currentProgram;
    }

    public Layout getCurrentLayout() {
        return currentLayout;
    }

    public void setCurrentLayout(Layout currentLayout) {
        this.currentLayout = currentLayout;
    }

    public List<MenuItemContainer> getMenu() {
        return menu;
    }

    public void setMenu(List<MenuItemContainer> menu) {
        this.menu = menu;
    }

    
}
