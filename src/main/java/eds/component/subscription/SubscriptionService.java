/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eds.component.subscription;

import eds.component.GenericEnterpriseObjectService;
import eds.component.data.DBConnectionException;
import eds.component.data.EnterpriseObjectNotFoundException;
import eds.component.data.EntityExistsException;
import eds.component.data.EntityNotFoundException;
import eds.component.data.IncompleteDataException;
import eds.component.mail.MailService;
import eds.component.user.UserService;
import eds.entity.client.Client;
import eds.entity.client.Client_;
import eds.entity.mail.Email;
import eds.entity.resource.SystemResourceAssignment;
import eds.entity.subscription.ListAssignment;
import eds.entity.subscription.SubscriberAccount;
import eds.entity.subscription.SubscriberAccount_;
import eds.entity.subscription.Subscription;
import eds.entity.subscription.SubscriptionList;
import eds.entity.subscription.SubscriptionList_;
import eds.entity.subscription.connection.SMTPConnectionSES;
import eds.entity.subscription.email.EmailTemplate;
import eds.entity.subscription.email.EmailTemplate.EMAIL_TYPE;
import eds.entity.subscription.email.EmailTemplate_;
import eds.entity.subscription.email.TemplateListAssignment;
import eds.entity.subscription.email.TemplateClientAssignment;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.apache.commons.validator.routines.EmailValidator;
import org.hibernate.exception.GenericJDBCException;
import org.joda.time.DateTime;

/**
 *
 * @author LeeKiatHaw
 */
@Stateless
public class SubscriptionService {
    
    @PersistenceContext(name="HIBERNATE")
    private EntityManager em;
    
    @EJB private GenericEnterpriseObjectService genericEntepriseObjectService;
    @EJB private MailService mailService;
    @EJB private UserService userService;
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public SubscriptionList addList(long clientid, String listname, boolean remote) {
        try{
            if(listname == null || listname.isEmpty())
                throw new ListException("List name cannot be empty.");
            
            //1. Create the list object and persist it first
            SubscriptionList newList = new SubscriptionList();
            newList.setLIST_NAME(listname); //Right now we don't keep history
            newList.setLOCATION( remote ? SubscriptionList.LOCATION.REMOTE : SubscriptionList.LOCATION.LOCAL);
            
            em.persist(newList);
            
            //2. Create the assignment to the client object
            Client client = this.genericEntepriseObjectService.getEnterpriseObjectById(clientid, Client.class);
            if (client == null)
                throw new EnterpriseObjectNotFoundException(clientid);
            //Test at this point whethe the newList object still gets persisted
            
            ListAssignment listAssignment = new ListAssignment(newList,client);
            
            em.persist(listAssignment);
            
            return newList;
            
        } catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                throw new DBConnectionException(pex.getCause().getMessage());
            }
            throw new EJBException(pex);
        } 
    }
    
    /**
     * Checks of a particular client has no lists created. Used in the setup page.
     * 
     * @param clientid
     * @return 
     */
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public boolean hasNoList(long clientid){
        try {
            long count = this.genericEntepriseObjectService.countRelationshipsForTarget(clientid, ListAssignment.class);
            return (count <= 0);
        } catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                throw new DBConnectionException(pex.getCause().getMessage());
            }
            throw new EJBException(pex);
        } catch (Exception ex) {
            throw new EJBException(ex);
        }
    }
    
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<SubscriptionList> getAllListForClient(long clientid){
        try {
            List<SubscriptionList> allList = 
                    this.genericEntepriseObjectService.getAllSourceObjectsFromTarget(clientid, ListAssignment.class, SubscriptionList.class);
            
            return allList;
        } catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                throw new DBConnectionException(pex.getCause().getMessage());
            }
            throw new EJBException(pex);
        } catch (Exception ex) {
            throw new EJBException(ex);
        }
    }
    /**
     * Subscribes a new subscriber to an existing list
     * 
     * @param newSub
     * @param listId 
     * @param confirmation 
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void subscribe(SubscriberAccount newSub, long listId, boolean confirmation){
        try{
            //Validate the email address rules
            EmailValidator validator = EmailValidator.getInstance();
            if(!validator.isValid(newSub.getEMAIL()))
                throw new SubscriptionException("Email address is not valid!");
            
            //Find the list object
            SubscriptionList list = this.genericEntepriseObjectService.getEnterpriseObjectById(listId, SubscriptionList.class);
            if(list == null)
                throw new SubscriptionException("List "+listId+" not found!");
            
            if(this.checkSubscribed(newSub.getEMAIL(), listId))
                throw new SubscriptionException("Email is already subscribed to list.");
            
            //Persist the new subscriber
            em.persist(newSub);
            
            //Create new subscription
            Subscription subsc = new Subscription();
            subsc.setSOURCE(newSub);
            subsc.setTARGET(list);
            subsc.setSTATUS(Subscription.STATUS.NEW);
            
            em.persist(subsc);
            
            // Send out the confimration email
            if(!confirmation) return;
            
            // Get confirmation email template
            // Assume that there is only 1
            List<EmailTemplate> templates = this.getTemplateForList(listId, EMAIL_TYPE.CONFIRMATION);
            if(templates == null || templates.isEmpty())
                throw new SubscriptionException("Cannot find any confimation email templates for list "+list.getLIST_NAME());
            EmailTemplate template = templates.get(0);
            
            Email email = template.generateEmail();
            email.setAUTHOR(list);
            email.addRecipient(newSub);
            
            // Get the SMTP connection settings from the List
            // Assume that there is only 1
            List<SMTPConnectionSES> smtps = this.genericEntepriseObjectService
                    .getAllSourceObjectsFromTarget(listId, SystemResourceAssignment.class, SMTPConnectionSES.class);
            
            // If the connection was not found, queue the message to be sent later
            if(smtps == null || smtps.isEmpty()){
                email.schedule(new DateTime());
                em.persist(email);
                throw new SubscriptionException("Cannot find any SMTP connection set for list "+list.getLIST_NAME());
            }
            
            SMTPConnectionSES smtp = smtps.get(0);
            
            mailService.sendEmail(email, smtp, true);
            
        } catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                throw new DBConnectionException(pex.getCause().getMessage());
            }
            throw new EJBException(pex);
        } catch (Exception ex) {
            throw new EJBException(ex);
        }
    }
    
    /**
     * Check if an email is already subscribed to a list
     * 
     * @param email
     * @param listId
     * @return 
     */
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public boolean checkSubscribed(String email, long listId){
        try{
            //Retrieving Subscriptions which has a SubscriberAccount email and a SubscriptionList ID
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<Subscription> criteria = builder.createQuery(Subscription.class);
            Root<Subscription> source = criteria.from(Subscription.class);
            
            Join<Subscription,SubscriberAccount> subAcc = source.join("SOURCE");
            Join<Subscription,SubscriptionList> subList = source.join("TARGET");
            List<Predicate> conditions = new ArrayList();
            
            conditions.add(builder.equal(subAcc.get(SubscriberAccount_.EMAIL), email));
            conditions.add(builder.equal(subList.get(SubscriptionList_.OBJECTID), listId));
            
            criteria.where(conditions.toArray(new Predicate[] {}));
            
            List<Subscription> results = em.createQuery(criteria).getResultList();
            
            return !(results == null || results.isEmpty());
            
        } catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                throw new DBConnectionException(pex.getCause().getMessage());
            }
            throw new EJBException(pex);
        } catch (Exception ex) {
            throw new EJBException(ex);
        }
    }
    
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<EmailTemplate> getTemplateForList(long listid, EMAIL_TYPE type){
        try {
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery query = builder.createQuery(EmailTemplate.class);
            Root<TemplateListAssignment> sourceEntity = query.from(TemplateListAssignment.class);
            
            Join<TemplateListAssignment,EmailTemplate> template = sourceEntity.join("SOURCE");
            Join<TemplateListAssignment,SubscriptionList> list = sourceEntity.join("TARGET");
            
            query.select(template);
            
            List<Predicate> conditions = new ArrayList();
            
            if(type != null) conditions.add(builder.equal(template.get(EmailTemplate_.TYPE), type));
            conditions.add(builder.equal(list.get(SubscriptionList_.OBJECTID), listid));
            
            query.where(conditions.toArray(new Predicate[] {}));
            
            List<EmailTemplate> results = em.createQuery(query)
                    .getResultList();
            
            return results;
            
        } catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                throw new DBConnectionException(pex.getCause().getMessage());
            }
            throw new EJBException(pex);
        } catch (Exception ex) {
            throw new EJBException(ex);
        }
    }
    
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<EmailTemplate> getAvailableTemplatesForClient(long clientid, EMAIL_TYPE type){
        try {
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery query = builder.createQuery(EmailTemplate.class);
            Root<TemplateClientAssignment> sourceEntity = query.from(TemplateClientAssignment.class);
            
            Join<TemplateClientAssignment,EmailTemplate> template = sourceEntity.join("SOURCE");
            Join<TemplateClientAssignment,Client> client = sourceEntity.join("TARGET");
            
            query.select(template);
            
            List<Predicate> conditions = new ArrayList();
            
            if(type != null) conditions.add(builder.equal(template.get(EmailTemplate_.TYPE), type));
            conditions.add(builder.equal(client.get(Client_.OBJECTID), clientid));
            
            query.where(conditions.toArray(new Predicate[] {}));
            
            List<EmailTemplate> results = em.createQuery(query)
                    .getResultList();
            
            return results;
        } catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                throw new DBConnectionException(pex.getCause().getMessage());
            }
            throw new EJBException(pex);
        } catch (Exception ex) {
            throw new EJBException(ex);
        }
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public EmailTemplate addTemplate(String subject, String body, EMAIL_TYPE type) 
            throws EntityExistsException, IncompleteDataException{
        try {
            if(subject == null || subject.isEmpty())
                throw new IncompleteDataException("Subject cannot be null.");
            // Check if the template subject already exist for the type
            if(this.checkTemplateSubjectExist(subject, type))
                throw new EntityExistsException("Please choose a different email subject");
            
            EmailTemplate newTemplate = new EmailTemplate();
            newTemplate.setBODY(body);
            newTemplate.setSUBJECT(subject);
            newTemplate.setTYPE(type);
            
            em.persist(newTemplate);
            
            return newTemplate;
            
        } catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                throw new DBConnectionException(pex.getCause().getMessage());
            }
            throw new EJBException(pex);
        } /*catch (Exception ex) { 
            throw new EJBException(ex); // Stupid idea
        }*/
    }
    
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public boolean checkTemplateSubjectExist(String subject, EMAIL_TYPE type){
        try {
            CriteriaBuilder builder = em.getCriteriaBuilder();
            CriteriaQuery<Long> query = builder.createQuery(Long.class);
            Root<EmailTemplate> sourceEntity = query.from(EmailTemplate.class);
            
            query.select(builder.count(query.from(EmailTemplate.class)));
            
            query.where(builder.and(
                    builder.equal(sourceEntity.get(EmailTemplate_.SUBJECT), subject),
                    builder.equal(sourceEntity.get(EmailTemplate_.TYPE), type)
                ));
            
            long result = em.createQuery(query)
                    .getSingleResult();
            
            return (result > 0);
        } catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                throw new DBConnectionException(pex.getCause().getMessage());
            }
            throw new EJBException(pex);
        } catch (Exception ex) {
            throw new EJBException(ex);
        }
    }
    
    public void assignEmailTemplateToClient(long emailTemplateId, long clientId) throws EntityNotFoundException{
        try {
            EmailTemplate template = this.genericEntepriseObjectService.getEnterpriseObjectById(emailTemplateId, EmailTemplate.class);
            if(template == null)
                throw new EntityNotFoundException("Template id "+emailTemplateId+" not found!");
            
            Client client = this.genericEntepriseObjectService.getEnterpriseObjectById(clientId, Client.class);
            if(client == null)
                throw new EntityNotFoundException("Client id "+client+" not found!");
            
            TemplateClientAssignment assignment = new TemplateClientAssignment();
            assignment.setSOURCE(template);
            assignment.setTARGET(client);
            
            em.persist(assignment);
            
        } catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                throw new DBConnectionException(pex.getCause().getMessage());
            }
            throw pex;
        } /*catch (Exception ex) {
            throw new EJBException(ex);
        }*/
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public EmailTemplate addTemplate(String subject, String body, EMAIL_TYPE type, long clientId) 
            throws EntityExistsException, IncompleteDataException{
        try {
            //Create the new template first
            EmailTemplate newTemplate = this.addTemplate(subject, body, type);
            
            //Assign it to the client
            this.assignEmailTemplateToClient(newTemplate.getOBJECTID(), clientId);
            
            return newTemplate;
            
        } catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                throw new DBConnectionException(pex.getCause().getMessage());
            }
            throw new EJBException(pex);
        } /*catch (Exception ex) {
            throw new EJBException(ex);
        }*/catch (EntityNotFoundException ex){
            throw new RuntimeException(ex); // Something is very wrong here!
        }
        
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public EmailTemplate saveTemplate(EmailTemplate template){
        try {
            return em.merge(template);
            
        } catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                throw new DBConnectionException(pex.getCause().getMessage());
            }
            throw new EJBException(pex);
        } catch (Exception ex) {
            throw new EJBException(ex);
        }
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void deleteTemplate(EmailTemplate template){
        try {
            em.remove(template);
            
        } catch (PersistenceException pex) {
            if (pex.getCause() instanceof GenericJDBCException) {
                throw new DBConnectionException(pex.getCause().getMessage());
            }
            throw new EJBException(pex);
        } catch (Exception ex) {
            throw new EJBException(ex);
        }
    }
}
