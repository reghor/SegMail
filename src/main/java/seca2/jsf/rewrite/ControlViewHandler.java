/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package seca2.jsf.rewrite;

import javax.faces.application.ViewHandler;
import javax.faces.application.ViewHandlerWrapper;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import seca2.bootstrap.UserRequestContainer;

/**
 *
 * @author LeeKiatHaw
 */
public class ControlViewHandler extends ViewHandlerWrapper {

    private ViewHandler wrapped;
    
    @Inject UserRequestContainer reqContainer;

    public ControlViewHandler(ViewHandler wrapped) {
        this.wrapped = wrapped;
    }
    
    @Override
    public ViewHandler getWrapped() {
        return wrapped;
    }

    @Override
    public String getActionURL(FacesContext context, String viewId) {
        String superValue = super.getActionURL(context, viewId); //Change the url based on the mapping here!
        /*if(superValue.endsWith("index.xhtml")){
            superValue = superValue.substring(0, superValue.lastIndexOf("index.xhtml"));
        }*/
        String contextPath = context.getExternalContext().getRequestContextPath();
        String servletPath = context.getExternalContext().getRequestServletPath();
        String pathInfo = context.getExternalContext().getRequestPathInfo();
        
        if(pathInfo.endsWith("index.xhtml")){
            pathInfo = pathInfo.replace("index.xhtml", "");
        }
        
        pathInfo = (reqContainer.getProgramName() == null) ? 
                pathInfo : "/".concat(reqContainer.getProgramName());
            
        return contextPath.concat("").concat(pathInfo); 
    }

    @Override
    public String deriveViewId(FacesContext context, String input) {
        String superValue = super.deriveViewId(context, input);
        return superValue; //To change body of generated methods, choose Tools | Templates.
    }
    
    
}
