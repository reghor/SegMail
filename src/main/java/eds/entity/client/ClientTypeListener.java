 /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eds.entity.client;

import eds.entity.program.Program;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

/**
 *
 * @author LeeKiatHaw
 */
public class ClientTypeListener {
    
    @PrePersist
    @PreUpdate
    public void PrePersistUpdate(Program program){
        this.replicateObjectName(program);
    }
    
    public void replicateObjectName(Program program){
        program.setOBJECT_NAME(program.getPROGRAM_NAME());
    }
}