/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eds.entity.client;

import eds.entity.EnterpriseData_;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

/**
 *
 * @author KH
 */
@StaticMetamodel(ContactInfo.class)
public class ContactInfo_ extends EnterpriseData_{
    public static volatile SingularAttribute<ClientType,String> FIRSTNAME;
    public static volatile SingularAttribute<ClientType,String> LASTNAME;
    public static volatile SingularAttribute<ClientType,String> EMAIL;
    public static volatile SingularAttribute<ClientType,String> MOBILE;
    public static volatile SingularAttribute<ClientType,String> OTHER;
}
