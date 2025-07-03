package us.tx.state.dfps.service.business.context;

import java.util.Arrays;
import java.util.List;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.cfg.Configuration;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.event.spi.PreDeleteEvent;
import org.hibernate.event.spi.PreDeleteEventListener;
import org.hibernate.event.spi.PreInsertEvent;
import org.hibernate.event.spi.PreInsertEventListener;
import org.hibernate.event.spi.PreUpdateEvent;
import org.hibernate.event.spi.PreUpdateEventListener;
import org.hibernate.integrator.spi.Integrator;
import org.hibernate.metamodel.source.MetadataImplementor;
import org.hibernate.persister.entity.AbstractEntityPersister;
import org.hibernate.service.spi.SessionFactoryServiceRegistry;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.ObjectUtils;
import us.tx.state.dfps.common.domain.BaseModel;
import us.tx.state.dfps.service.common.ServiceConstants;

public class CustomAuditEntityListener implements PreInsertEventListener, PreUpdateEventListener,
    PreDeleteEventListener, Integrator {

    private static final Logger log = Logger.getLogger(CustomAuditEntityListener.class);

    private static final List<String> ignoreIds= Arrays.asList(ServiceConstants.INTERNAL_USER_ID,ServiceConstants.EXTERNAL_USER_ID);

    private static final String CREATED_PERSON_ID = "createdPersonId";

    private static final String LAST_UPDATED_PERSON_ID = "lastUpdatedPersonId";

    /**
     * Looks for Authentication set by CustomJWTInterceptor in Security Context and returns the login user id
     *
     * @return
     */
    private Long getLoggedInIdPerson() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && !ObjectUtils.isEmpty(authentication.getPrincipal())
                && !ignoreIds.contains(authentication.getPrincipal())) {
            try {
                return Long.parseLong(authentication.getPrincipal().toString());
            } catch (NumberFormatException ex){
                log.error(" id person is not of type Long " + ex.getMessage());
            }
        }
        return null;
    }

    /**
     * Sets the created person and updated person columns based on the logged-in user id, if the entity has the
     * audit columns defined (extends BaseModel), date audit fields will be set by DB triggers
     *
     * @param event
     * @return
     */
    @Override
    public boolean onPreInsert(PreInsertEvent event) {
        log.info("Executing onPreInsert");
        if (event.getEntity() instanceof BaseModel) {
            log.info("Entity is an instance of BaseModel, setting created and updated person id");
            Long idPerson = getLoggedInIdPerson();
            if (idPerson != null) {
                String[] propertyNames = event.getPersister().getEntityMetamodel().getPropertyNames();
                Object[] state = event.getState();
                for (int i = 0; i < propertyNames.length; i++) {
                    if (CREATED_PERSON_ID.equals(propertyNames[i]) || LAST_UPDATED_PERSON_ID.equals(propertyNames[i])) {
                        state[i] = idPerson;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Sets the updated person column based on the logged-in user id, if the entity has the audit columns
     * defined (extends BaseModel), date audit fields will be set by DB triggers
     *
     * @param event
     * @return
     */
    @Override
    public boolean onPreUpdate(PreUpdateEvent event) {
        log.info("Executing onPreUpdate");
        if (event.getEntity() instanceof BaseModel) {
            log.info("Entity is an instance of BaseModel, setting updated person id");
            Long idPerson = getLoggedInIdPerson();
            if (idPerson != null) {
                String[] propertyNames = event.getPersister().getEntityMetamodel().getPropertyNames();
                Object[] state = event.getState();
                for (int i = 0; i < propertyNames.length; i++) {
                    if (LAST_UPDATED_PERSON_ID.equals(propertyNames[i])) {
                        state[i] = idPerson;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean onPreDelete(PreDeleteEvent event) {
        log.info("Executing PreDeleteEvent");
        if (event.getEntity() instanceof BaseModel) {
            try {
                Long idPerson = getLoggedInIdPerson();
                if (idPerson != null) {
                    log.debug(
                        "Setting idlastUpdatedPerson to " + idPerson + " before deletion of entity "
                            + event.getEntity().getClass().getSimpleName());
                    Session session = event.getSession();
                    AbstractEntityPersister persister = (AbstractEntityPersister) event.getPersister();
                    Object idValue = persister.getIdentifier(event.getEntity(), event.getSession());
                    String idColumnName = persister.getIdentifierColumnNames()[0];
                    String tableName = persister.getTableName();

                    String sql = "UPDATE " + tableName + " SET ID_LAST_UPDATE_PERSON = :personId " +
                        "WHERE " + idColumnName + " = :id";
                    int updated = session.createSQLQuery(sql)
                        .setParameter("personId", idPerson)
                        .setParameter("id", idValue)
                        .executeUpdate();
                    if (updated == 0) {
                        log.warn(
                            "No rows found to update for entity " + event.getEntity().getClass()
                                .getSimpleName() + " Id: " + idValue);
                    } else {
                        log.debug("Successfully updated idlastUpdatedPerson before deletion");
                    }
                }
            } catch (Exception e) {
                log.warn("Error updating idlastUpdatedPerson before deletion", e);
            }
        }
        return false;
    }


    /**
     * sets the entity listeners (this class) in the EventListenerRegistry for the required hibernate events
     * to set audit columns
     *
     * @param configuration
     * @param sessionFactoryImplementor
     * @param sessionFactoryServiceRegistry
     */
    @Override
    public void integrate(Configuration configuration, SessionFactoryImplementor sessionFactoryImplementor, SessionFactoryServiceRegistry sessionFactoryServiceRegistry) {
        EventListenerRegistry eventListenerRegistry =
                sessionFactoryServiceRegistry.getService(EventListenerRegistry.class);

        CustomAuditEntityListener hibernateInterceptor = new CustomAuditEntityListener();
        eventListenerRegistry.appendListeners(
                EventType.PRE_INSERT,
                hibernateInterceptor);

        eventListenerRegistry.appendListeners(
                EventType.PRE_UPDATE,
                hibernateInterceptor);

        eventListenerRegistry.appendListeners(
            EventType.PRE_DELETE,
            hibernateInterceptor);
    }

    @Override
    public void integrate(MetadataImplementor metadataImplementor, SessionFactoryImplementor sessionFactoryImplementor, SessionFactoryServiceRegistry sessionFactoryServiceRegistry) {
        //Nothing to implement for this method
    }

    @Override
    public void disintegrate(SessionFactoryImplementor sessionFactoryImplementor, SessionFactoryServiceRegistry sessionFactoryServiceRegistry) {
        //Nothing to disintegrate.
    }
}
