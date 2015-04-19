/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eds.entity.navigation;

import eds.entity.data.EnterpriseRelationship;
import eds.entity.user.UserType;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 *
 * @author KH
 */
@Entity
@Table(name="MENU_ITEM_ACCESS")
public class MenuItemAccess extends EnterpriseRelationship<MenuItem,UserType> {

    public MenuItemAccess() {
    }

    public MenuItemAccess(MenuItem s, UserType t) {
        super(s, t);
    }

    //Any additional attributes to be maintained for this relationship?
    //protected String REL_TYPE = "MENU_ITEM_ACCESS"; no need to redefine it here
    
    /*@PrePersist
    public void prePersist(){
        this.REL_TYPE = "MENU_ITEM_ACCESS";
    }
    
    @PreUpdate
    public void preUpdate(){
        this.REL_TYPE = "MENU_ITEM_ACCESS";
    }*/
    
    @Override
    public void randInit() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object generateKey() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
