/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package seca2.program.test;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import org.hibernate.Session;
import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import seca2.component.data.DBConnectionException;
import seca2.component.data.EntityExplorer;
import seca2.component.data.HibernateEMServices;
import seca2.program.messenger.FacesMessenger;

/**
 *
 * @author KH
 */
@RequestScoped
public class FormTestDB implements Serializable {
    
    @EJB private HibernateEMServices hibernateDBServices;
    
    @Inject private EntityExplorer explorer;
    
    private final String TestGenerateDBFormName = "setupDBForm";
    
    public void generateDBEM(){
        try{
            EntityManager em = hibernateDBServices.getEM();
            FacesMessenger.setFacesMessage(TestGenerateDBFormName, FacesMessage.SEVERITY_INFO, "Success!",null);
        } catch(Exception ex){
            FacesMessenger.setFacesMessage(TestGenerateDBFormName, FacesMessage.SEVERITY_ERROR, "Oops!", ex.getMessage());
        }
    }
    
    public void generateDBSession(){
        try {
            Session session = hibernateDBServices.getSession();
            Configuration cfg = hibernateDBServices.createFullConfig();
            
            //add all entity packages
            seca2.component.data.Package root = new seca2.component.data.Package();
            root.push("seca2");
            root.push("entity");

            ClassLoader loader = explorer.getClassLoader();
            List<Class> entities = explorer.collectEntities(root, loader);
            for(Class c : entities){
                cfg.addAnnotatedClass(c);
            }
            new SchemaExport(cfg)
                    //.setProperty("hibernate.hbm2ddl.auto", "create")) //it is currently update
                    .execute(true, true, false, true);
            FacesMessenger.setFacesMessage(TestGenerateDBFormName, FacesMessage.SEVERITY_FATAL, "Success!",null);
        } catch (DBConnectionException dbcex) {
            FacesMessenger.setFacesMessage(TestGenerateDBFormName, FacesMessage.SEVERITY_ERROR, "Oops!", dbcex.getMessage());
        } catch(Exception ex){
            FacesMessenger.setFacesMessage(TestGenerateDBFormName, FacesMessage.SEVERITY_ERROR, "Oops!", ex.getMessage());
        }
        
    }
}
