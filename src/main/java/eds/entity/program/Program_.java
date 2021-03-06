/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eds.entity.program;

import eds.entity.data.EnterpriseObject_;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import eds.entity.user.UserAccount;

/**
 *
 * @author LeeKiatHaw
 */
@StaticMetamodel(Program.class)
public class Program_ extends EnterpriseObject_ {

    public static volatile SingularAttribute<Program, String> PROGRAM_NAME;
    public static volatile SingularAttribute<Program, String> VIEW_DIRECTORY;
    public static volatile SingularAttribute<Program, String> VIEW_ROOT;
    public static volatile SingularAttribute<Program, String> BEAN_DIRECTORY;
    public static volatile SingularAttribute<Program, String> DISPLAY_TITLE;
    public static volatile SingularAttribute<Program, String> DISPLAY_DESCRIPTION;
    public static volatile SingularAttribute<Program, Boolean> PUBLIC;
}
