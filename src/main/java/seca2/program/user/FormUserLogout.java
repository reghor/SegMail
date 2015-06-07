/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.program.user;

import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import eds.component.data.DBConnectionException;
import seca2.bootstrap.module.User.UserContainer;
import javax.servlet.http.HttpSession;
import seca2.jsf.custom.messenger.FacesMessenger;

/**
 *
 * @author vincent.a.lee
 */
@Named("FormUserLogout")
@RequestScoped
public class FormUserLogout {

    @Inject private UserContainer userContainer;
    
    private final String messageBoxId = "form-user-login";

    public void logout() {
        try {
            ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
            HttpSession session = (HttpSession) ec.getSession(true);
            session.invalidate();
            ec.redirect(ec.getRequestContextPath());
            
        } catch (DBConnectionException dbex) {
            FacesMessenger.setFacesMessage(messageBoxId, FacesMessage.SEVERITY_ERROR, "Could not connect to database!", "Please contact admin.");
        } catch (Exception ex) {
            FacesMessenger.setFacesMessage(messageBoxId, FacesMessage.SEVERITY_ERROR, ex.getMessage(), null);
        }
    }
}
