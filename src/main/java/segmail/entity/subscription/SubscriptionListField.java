/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package segmail.entity.subscription;

import eds.entity.data.EnterpriseData;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 *
 * @author LeeKiatHaw
 */
@Entity
@Table(name="SUBSCRIPTION_LIST_FIELD")
public class SubscriptionListField extends EnterpriseData<SubscriptionList>{
    
    private boolean MANDATORY;
    private String FIELD_NAME;
    private String TYPE;
    private String DESCRIPTION;

    //A must for all JPA entities
    public SubscriptionListField() {
    }

    public SubscriptionListField(int order, boolean MANDATORY, String FIELD_NAME, FIELD_TYPE TYPE, String DESCRIPTION) {
        this.FIELD_NAME = FIELD_NAME;
        this.MANDATORY = MANDATORY;
        this.TYPE = TYPE.name();
        this.DESCRIPTION = DESCRIPTION;
        this.SNO = order;
    }
    
    public String getFIELD_NAME() {
        return FIELD_NAME;
    }

    public void setFIELD_NAME(String FIELD_NAME) {
        this.FIELD_NAME = FIELD_NAME;
    }

    public String getTYPE() {
        return TYPE;
    }
    
    public void setTYPE(String type) {
        this.TYPE = type;
    }

    public void setTYPE(FIELD_TYPE TYPE) {
        this.TYPE = TYPE.name();
    }


    public String getDESCRIPTION() {
        return DESCRIPTION;
    }

    public void setDESCRIPTION(String DESCRIPTION) {
        this.DESCRIPTION = DESCRIPTION;
    }

    public boolean isMANDATORY() {
        return MANDATORY;
    }

    public void setMANDATORY(boolean MANDATORY) {
        this.MANDATORY = MANDATORY;
    }
    
    @Transient
    public String getHTMLFieldName(){
        String htmlName = this.FIELD_NAME.trim().replaceAll(" ", "-");
        return htmlName;
    }

    @Override
    public void randInit() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object generateKey() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
