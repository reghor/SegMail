/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package seca2.program.test;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.inject.Inject;
import eds.component.data.DBConnectionException;
import eds.component.user.UserAccountLockedException;
import seca2.bootstrap.module.User.UserContainer;
import eds.component.user.UserLoginException;
import eds.component.user.UserRegistrationException;
import eds.component.user.UserService;
import eds.component.user.UserTypeException;
import eds.entity.user.UserType;
import seca2.jsf.custom.messenger.FacesMessenger;

/**
 *
 * @author vincent.a.lee
 */
@RequestScoped
public class FormTestUser implements Serializable {
    
    //Create UserType
    private String userTypeName;
    private String description;
    
    private final String createUsertypeFormName = "createUsertypeForm";
    
    //Create User
    private List<UserType> allUserTypes = new ArrayList<UserType>();
    private long chosenUserType;
    private String username;
    private String password;
    
    private final String createUserFormName = "createUserForm";
    
    //Login User
    private String loginUsername;
    private String loginPassword;
    @Inject
    private UserContainer userContainer; //this is not resolved precisely [20150131]
    
    private final String loginUserFormName = "loginUserForm";
    
    @EJB private UserService userService;
    
    @PostConstruct
    public void init(){
        this.initializeAllUserTypes();
    }
    
    public void createUserType(){
        try{
            userService.createUserType(userTypeName, description);
            FacesMessenger.setFacesMessage(createUsertypeFormName, FacesMessage.SEVERITY_FATAL, "Usertype "+userTypeName+" created!", null);
        } 
        catch (UserTypeException utex) {
            FacesMessenger.setFacesMessage(createUsertypeFormName, FacesMessage.SEVERITY_ERROR, utex.getClass().getSimpleName(), utex.getMessage());
        } 
        catch (DBConnectionException ex) {
            FacesMessenger.setFacesMessage(createUsertypeFormName, FacesMessage.SEVERITY_ERROR, "Could not connect to database!", "Please contact admin.");
        } 
        catch(Exception ex){
            FacesMessenger.setFacesMessage(createUsertypeFormName, FacesMessage.SEVERITY_ERROR, ex.getClass().getSimpleName(), ex.getMessage());
        }
    }
    
    public void createUser(){
        try{
            userService.registerUserByUserTypeId(chosenUserType, username, password);
            FacesMessenger.setFacesMessage(createUserFormName, FacesMessage.SEVERITY_FATAL, "User "+username+" has been created!", null);
        } catch(DBConnectionException dbex){
            FacesMessenger.setFacesMessage(createUserFormName, FacesMessage.SEVERITY_ERROR, "Could not connect to database!", "Please contact admin.");
        } catch (UserRegistrationException ex) {
            FacesMessenger.setFacesMessage(createUserFormName, FacesMessage.SEVERITY_ERROR, ex.getLocalizedMessage(), "Please contact admin.");
        } catch(Exception ex){
            FacesMessenger.setFacesMessage(createUserFormName, FacesMessage.SEVERITY_ERROR, 
                    ex.getCause().getClass().getSimpleName(), //why? i forgot why...
                    ex.getCause().getMessage());
        }
    }
    
    public void loginUser(){
        try{
            Map<String,Object> userValues = new HashMap<String,Object>();
            userService.login(this.loginUsername, this.loginPassword, userValues);
            FacesMessenger.setFacesMessage(loginUserFormName, FacesMessage.SEVERITY_FATAL, "Login successful!", null);
        } catch(UserLoginException esliex){
            FacesMessenger.setFacesMessage(loginUserFormName, FacesMessage.SEVERITY_ERROR, esliex.getLocalizedMessage(), null);
        } catch(DBConnectionException dbex){
            FacesMessenger.setFacesMessage(loginUserFormName, FacesMessage.SEVERITY_ERROR, "Could not connect to database!", "Please contact admin.");
        } catch(UserAccountLockedException ualex){
            FacesMessenger.setFacesMessage(loginUserFormName, FacesMessage.SEVERITY_ERROR, ualex.getLocalizedMessage(), "Please contact admin.");
        } catch(Exception ex){
            FacesMessenger.setFacesMessage(loginUserFormName, FacesMessage.SEVERITY_ERROR, ex.getLocalizedMessage(), null);
        }
    }
    
    public void initializeAllUserTypes(){
         try{
            allUserTypes = userService.getAllUserTypes();
            //who knows whether there is empty list or not?
        }
        catch(DBConnectionException dbex){
            FacesMessenger.setFacesMessage(createUserFormName, FacesMessage.SEVERITY_ERROR, "Could not connect to database!", "Please contact admin.");
        }
        catch(Exception ex){
            FacesMessenger.setFacesMessage(createUserFormName, FacesMessage.SEVERITY_ERROR, 
                    ex.getCause().getClass().getSimpleName(), 
                    ex.getCause().getMessage());
        }
    }

    public String getUserTypeName() {
        return userTypeName;
    }

    public void setUserTypeName(String userTypeName) {
        this.userTypeName = userTypeName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<UserType> getAllUserTypes() {
        return allUserTypes;
    }

    public void setAllUserTypes(List<UserType> allUserTypes) {
        this.allUserTypes = allUserTypes;
    }

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

    public long getChosenUserType() {
        return chosenUserType;
    }

    public void setChosenUserType(long chosenUserType) {
        this.chosenUserType = chosenUserType;
    }

    public String getLoginUsername() {
        return loginUsername;
    }

    public void setLoginUsername(String loginUsername) {
        this.loginUsername = loginUsername;
    }

    public String getLoginPassword() {
        return loginPassword;
    }

    public void setLoginPassword(String loginPassword) {
        this.loginPassword = loginPassword;
    }
    
    
}
