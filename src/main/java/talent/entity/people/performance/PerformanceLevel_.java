/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package talent.entity.people.performance;

import eds.entity.config.EnterpriseConfiguration_;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

/**
 *
 * @author LeeKiatHaw
 */
@StaticMetamodel(PerformanceLevel.class)
public class PerformanceLevel_ extends EnterpriseConfiguration_ {
    public static volatile SingularAttribute<PerformanceLevel,String> LEVEL_NAME;
    public static volatile SingularAttribute<PerformanceLevel,Integer> RATING;
}
