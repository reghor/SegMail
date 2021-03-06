/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.program.user;

import java.util.HashMap;
import java.util.Map;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import eds.component.data.DBConnectionException;
import eds.component.user.UserAccountLockedException;
import seca2.bootstrap.UserSessionContainer;
import eds.component.user.UserLoginException;
import eds.component.user.UserService;
import eds.entity.user.User;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import seca2.jsf.custom.messenger.FacesMessenger;

/**
 *
 * @author vincent.a.lee
 */
@Named("FormUserLogin")
@RequestScoped
public class FormUserLogin {

    @EJB
    private UserService userService;
    @Inject private UserSessionContainer userContainer;

    private String username;
    private String password;

    private final String messageBoxId = "form-user-login";
    
    /**
     * This can work only because the variables in this bean was assessed in the xhtml page.
     * Always remember this!
     */
    @PostConstruct
    public void init(){
        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();
        if(fc.isPostback() /* && must have some other ways to know this is a timeout*/){
            FacesMessenger.setFacesMessage(messageBoxId, FacesMessage.SEVERITY_ERROR, "Your session has expired. Please login again.", null);
        }
    }

    public void login() {
        try {
            Map<String,Object> userValues = new HashMap<String,Object>();
            User authenticatedUser = userService.login(this.username, this.password);
            FacesMessenger.setFacesMessage(messageBoxId, FacesMessage.SEVERITY_FATAL, "Login successful!", null);
            
            ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
            //Initialize userValues into userContainer
            this.userContainer.setSessionId(ec.getSessionId(true));
            this.userContainer.setLoggedIn(true);
            this.userContainer.setUser(authenticatedUser);
            this.userContainer.setUserType(authenticatedUser.getUSERTYPE());
            
            
            //do a redirect to refresh the view
            //Something is faulty here after a redirect
            String previousURI = this.userContainer.getLastProgram();
            
            if (previousURI != null && !previousURI.isEmpty()) {
                /*ec.redirect(ec.getRequestContextPath()
                        +ec.getRequestServletPath()
                        +"/"
                        +previousURI
                        +"/");*/ //calling "test" -> "/SegMail/program/test/test"
                String lastProgram = "/".concat((userContainer.getLastProgram() == null) ? "" : userContainer.getLastProgram());
                String contextPath = (ec.getRequestContextPath() == null) ? "" : ec.getRequestContextPath();
                ec.redirect(contextPath+lastProgram);
                //we need an adaptor pattern for redirection!
                //this should be in the navigation module
                
                String URI = ((HttpServletRequest)ec.getRequest()).getRequestURI();//debug
                System.out.println(URI);
            } else {
                ec.redirect(ec.getRequestContextPath());//go to home
            }
        } catch (UserLoginException esliex) {
            FacesMessenger.setFacesMessage(messageBoxId, FacesMessage.SEVERITY_ERROR, esliex.getMessage(), null);
        } catch (DBConnectionException dbex) {
            FacesMessenger.setFacesMessage(messageBoxId, FacesMessage.SEVERITY_ERROR, "Could not connect to database!", "Please contact admin.");
        } catch (UserAccountLockedException ualex) {
            FacesMessenger.setFacesMessage(messageBoxId, FacesMessage.SEVERITY_ERROR, "Your account has been locked. Please contact admin.", null);
        } catch (Exception ex) {
            FacesMessenger.setFacesMessage(messageBoxId, FacesMessage.SEVERITY_ERROR, ex.getMessage(), null);
        }
    }

    /*
     public void login2() throws IOException {

     //Check if username and password are present
     if (username == null || username.isEmpty()) {
     FacesMessenger.setFacesMessage(messageBoxId, FacesMessage.SEVERITY_ERROR,
     "Please enter username", null);
     return;
     }
     if (password == null || password.isEmpty()) {
     FacesMessenger.setFacesMessage(messageBoxId, FacesMessage.SEVERITY_ERROR,
     "Please enter password", null);
     return;
     }
     UserSessionContainer uc = null;
     try {
     //use UserService to login
     uc = userService.login(username, password);
            
     } catch (UserAccountLockedException usalex) {
     FacesMessenger.setFacesMessage(messageBoxId, FacesMessage.SEVERITY_ERROR,
     "Oops...Your account has been locked. Please contact administrator to unlock it.", null);
     return;
     } catch (DBConnectionException dbex) {
     FacesMessenger.setFacesMessage(messageBoxId, FacesMessage.SEVERITY_ERROR, dbex.getMessage(), null);
     return;
     } catch (Exception ex) {
     FacesMessenger.setFacesMessage(messageBoxId, FacesMessage.SEVERITY_ERROR, ex.getMessage(), null);
     return;
     }

     if (uc == null) {
     FacesMessenger.setFacesMessage(messageBoxId, FacesMessage.SEVERITY_ERROR,
     "Wrong credentials. Are you sure you entered the correct credentials?",
     "Alternatively, would you like to created a new account? ");
     return;
     }

     ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
     HttpServletRequest req = (HttpServletRequest) ec.getRequest();
     HttpServletResponse resp = (HttpServletResponse) ec.getResponse();

     //HttpSession session = req.getSession(true);
     //session.setAttribute("user", 1);
     //userModule.setsSessionId(sessionId);
     //sSessionId = session.getId();
     DateTime sessionStarttime = new DateTime();
     //System.out.println("Session " + userModule.getsSessionId() + " started at " + sessionStarttime);
     password = "";
     username = "";
        
     //Regenerate session ID
     HttpSession session = req.getSession(true);
     //Set UserSessionContainer with session ID
     String sessionId = session.getId();
     uc.setSessionId(sessionId);
        
     //do a redirect to refresh the view
     String previousURI = uc.getLastURL();
     if (previousURI != null && !previousURI.isEmpty()) {
     ec.redirect(previousURI);
     } else {
     ec.redirect(ec.getRequestContextPath());//go to home
     }

     //Remember to construct the UserSession before redirecting!
     }
     */
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
