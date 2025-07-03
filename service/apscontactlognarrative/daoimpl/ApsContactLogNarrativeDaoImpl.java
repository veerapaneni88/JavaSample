package us.tx.state.dfps.service.apscontactlognarrative.daoimpl;

import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;
import us.tx.state.dfps.service.apscontactlognarrative.dao.ApsContactLogNarrativeDao;
import us.tx.state.dfps.service.apscontactlognarrative.dto.*;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.webservices.gold.dto.GoldNarrativeDto;

import java.util.List;

/**
 * class for Aps Contact Log Narrative Dao implementation
 */
@Repository
public class ApsContactLogNarrativeDaoImpl implements ApsContactLogNarrativeDao {


    @Value("${ApsContactLogNarrativeDaoImpl.getsafetyAssmtEventsByStage}")
    private String getsafetyAssmtEventsByStage;

    @Value("${ApsContactLogNarrativeDaoImpl.getContactsByStage}")
    private String getContactsByStage;

    @Value("${ApsContactLogNarrativeDaoImpl.getFirstContactBySaEvent}")
    private String getFirstContactBySaEvent;

    @Value("${ApsContactLogNarrativeDaoImpl.getDtContactoccured}")
    private String getDtContactoccured;

    @Value("${ApsContactLogNarrativeDaoImpl.getApsSafetyAssessmentResults}")
    private String getApsSafetyAssessmentResults;

    @Value("${ApsContactLogNarrativeDaoImpl.getApsSaSafetyContactData}")
    private String getApsSaSafetyContactData;

    @Value("${ApsContactLogNarrativeDaoImpl.getApsAssessmentType}")
    private String getApsAssessmentType;

    @Value("${ApsContactLogNarrativeDaoImpl.getApsSaEventId}")
    private String getApsSaEventId;

    @Value("${ApsContactLogNarrativeDaoImpl.getApsSaSafetyContactDataByEventId}")
    private  String getApsSaSafetyContactDataByEventId;

    @Value("${ApsContactLogNarrativeDaoImpl.getPersonsContacted}")
    private String getPersonsContacted;


    @Value("${ApsContactLogNarrativeDaoImpl.getGuardianShipReferralNarrativeByEventId}")
    private String getGuardianShipReferralNarrativeByEventIdSql;
    private static final Logger log = Logger.getLogger(ApsContactLogNarrativeDaoImpl.class);

    @Autowired
    MessageSource messageSource;

    @Autowired
    private SessionFactory sessionFactory;

    private static final String ID_EVENT = "idEvent";

    private static final String TRANSACTIONID = "TransactionId :";

    private static final String DTCONTACTOCCURRED = "dateContactOccurred" ;

    private static final String CONTACTEVENTID = "contactEventId";

    private static final String DTLASTUPDATE = "dateLastUpdate";


    public ApsContactLogNarrativeDaoImpl() {
        // empty constructor
    }

    /**
     * method to get Safety Assessment Events by Stage
     * @param idStage
     * @return
     */

    @Override
    public List<Long> getsafetyAssmtEventsByStage(Long idStage) {

        List<Long> apsSaList = null;
        apsSaList = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getsafetyAssmtEventsByStage)
                .setParameter("idStage", idStage))
                .addScalar(ID_EVENT, StandardBasicTypes.LONG).list();
        log.info(TRANSACTIONID + idStage);

        return apsSaList;
    }

    /**
     * method to get Aps Contact Events
     * @param idStage
     * @return
     */
    @Override
    public List<Long> getContactEvents(Long idStage) {
        log.debug("Entering method  getContactEvents in ApsContactLogNarrativeDaoImpl");
        List<Long> contactEventIds = null;
        contactEventIds = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getContactsByStage)
                .setParameter("idEventStage", idStage))
                .addScalar(ID_EVENT, StandardBasicTypes.LONG).list();
        log.info(TRANSACTIONID + idStage);
        return contactEventIds;
    }

    /**
     * method to get First Contact Event by APS SA
     * @param apsSaEventId
     * @return
     */
    @Override
    public Long getFirstContactEventBySA(Long apsSaEventId) {
        log.debug("Entering method  getFirstContactBySaEvent in ApsContactLogNarrativeDaoImpl");
        Long resourceId = null;
        resourceId = (Long) ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getFirstContactBySaEvent)
                .setParameter("apsSaEventId", apsSaEventId))
                .addScalar("iDContactEvent", StandardBasicTypes.LONG).uniqueResult();
        log.info(TRANSACTIONID + apsSaEventId);
        return resourceId;
    }

    /**
     * Method Name: getDtContactoccured Method Description: Method to getDtContactoccured
     *
     * @param idEvent
     * @return
     */
    @Override
    public APSSafetyAssessmentContactDto getDtContactoccured(Long idEvent) {
        log.debug("Entering method getDtContactoccured in ApsContactLogNarrativeDaoImpl");

        APSSafetyAssessmentContactDto apsSafetyAssessmentContactDto = null;
        SQLQuery sQLQuery = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getDtContactoccured)
                .addScalar(DTCONTACTOCCURRED,StandardBasicTypes.DATE)
                .addScalar(CONTACTEVENTID,StandardBasicTypes.LONG)
                .setParameter(ID_EVENT, idEvent)
                .setResultTransformer(Transformers.aliasToBean(APSSafetyAssessmentContactDto.class)));
        apsSafetyAssessmentContactDto =(APSSafetyAssessmentContactDto) sQLQuery.uniqueResult();
        apsSafetyAssessmentContactDto.setTimeContactOccurred(DateUtils.fullISODateTimeFormat(apsSafetyAssessmentContactDto.getDateContactOccurred()));
        return apsSafetyAssessmentContactDto;

    }

    /**
     * method to get Aps Safety Assessment Resutls by Event id
     * @param idEvent
     * @return
     */
    @Override
    public APSSafetyAssessmentDto getApsSafetyAssessmentResults(Long idEvent) {
        log.debug("Entering method getApsSafetyAssessmentResults in ApsContactLogNarrativeDaoImpl");

        APSSafetyAssessmentDto apsSafetyAssessmentDto = null;
        SQLQuery sQLQuery = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getApsSafetyAssessmentResults)
                .addScalar("idApsSa",StandardBasicTypes.LONG)
                .addScalar("caseId",StandardBasicTypes.LONG)
                .addScalar("stageId",StandardBasicTypes.LONG)
                .addScalar("stageCode",StandardBasicTypes.STRING)
                .addScalar("eventId",StandardBasicTypes.LONG)
                .addScalar("formVersionId",StandardBasicTypes.LONG)
                .addScalar("formVersionNumber",StandardBasicTypes.LONG)
                .addScalar("caseName",StandardBasicTypes.STRING)
                .addScalar("eventStatus",StandardBasicTypes.STRING)
                .addScalar(DTLASTUPDATE,StandardBasicTypes.DATE)
                .addScalar("eventDateLastUpdate",StandardBasicTypes.DATE)
                .addScalar("dateEventOccurred",StandardBasicTypes.DATE)
                .addScalar("dateAssessmentCompleted",StandardBasicTypes.DATE)
                .addScalar("indCaretakerNotApplicable",StandardBasicTypes.STRING)
                .addScalar("indImmediateIntervention",StandardBasicTypes.STRING)
                .addScalar("createdOn",StandardBasicTypes.DATE)
                .addScalar("createdBy",StandardBasicTypes.STRING)
                .addScalar("updatedBy",StandardBasicTypes.STRING)
                .addScalar("indReferralRequired",StandardBasicTypes.STRING)
                .addScalar("indInterventionsInPlace",StandardBasicTypes.STRING)
                .addScalar("indCaseInitiationComplete",StandardBasicTypes.STRING)
                .addScalar("savedSafetyDecisionCode",StandardBasicTypes.STRING)
                .addScalar("initialStagePriority",StandardBasicTypes.STRING)
                .addScalar("currentStagePriority",StandardBasicTypes.STRING)
                .addScalar("stageDateLastUpdate",StandardBasicTypes.DATE)
                .addScalar("stagePriorityCmnts",StandardBasicTypes.STRING)
                .addScalar("stageTypeCode",StandardBasicTypes.STRING)
                .setParameter(ID_EVENT, idEvent)
                .setResultTransformer(Transformers.aliasToBean(APSSafetyAssessmentDto.class)));
        apsSafetyAssessmentDto = (APSSafetyAssessmentDto) sQLQuery.uniqueResult();
        apsSafetyAssessmentDto.setAssessmentType(getApsAssessmentType(idEvent));
        return apsSafetyAssessmentDto;

    }

    /**
     * method to get Aps Safety Contact Data
     * @param idEvent
     * @return
     */
    @Override
    public List<APSSafetyAssessmentContactDto> getApsSaSafetyContactData(Long idEvent) {
        log.debug("Entering method getApsSaSafetyContactData in ApsContactLogNarrativeDaoImpl");

        List<APSSafetyAssessmentContactDto> assessmentContactDtoList = null;
        SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getApsSaSafetyContactData)
                .addScalar(CONTACTEVENTID,StandardBasicTypes.LONG)
                .addScalar("contactWorkerId",StandardBasicTypes.LONG)
                .addScalar("contactWorkerFullName",StandardBasicTypes.STRING)
                .addScalar("indContactAttempted",StandardBasicTypes.STRING)
                .addScalar("codeContactType",StandardBasicTypes.STRING)
                .addScalar(DTLASTUPDATE,StandardBasicTypes.DATE)
                .addScalar("idApsSaEvent",StandardBasicTypes.LONG)
                .addScalar(DTCONTACTOCCURRED,StandardBasicTypes.DATE)
                .setParameter(ID_EVENT, idEvent).setResultTransformer(Transformers.aliasToBean(APSSafetyAssessmentContactDto.class)));
        assessmentContactDtoList = sQLQuery1.list();

        if(null != assessmentContactDtoList){
            for(APSSafetyAssessmentContactDto apsSafetyAssessmentContactDto : assessmentContactDtoList) {
                String strContactType = null;
                if (!ObjectUtils.isEmpty(apsSafetyAssessmentContactDto.getCodeContactType())) {
                    if (apsSafetyAssessmentContactDto.getCodeContactType().equals(CodesConstant.CCNTCTYP_C24H)) {
                        strContactType = "CI";
                    } else if (apsSafetyAssessmentContactDto.getCodeContactType().equals(CodesConstant.CCNTCTYP_CFTF)
                            || apsSafetyAssessmentContactDto.getCodeContactType().equals(CodesConstant.CCNTCTYP_CSAF)
                            || apsSafetyAssessmentContactDto.getCodeContactType().equals(CodesConstant.CCNTCTYP_CSVC)) {
                        strContactType = "F2F";
                    }
                }
                apsSafetyAssessmentContactDto.setContactType(strContactType);
                apsSafetyAssessmentContactDto.setTimeContactOccurred(DateUtils.getTime(apsSafetyAssessmentContactDto.getDateContactOccurred()));
            }
        }
        return assessmentContactDtoList;
    }

    /**
     * method to get Aps Assessment type
     * @param idEvent
     * @return
     */
    @Override
    public String getApsAssessmentType(Long idEvent) {
        log.debug("Entering method getApsAssessmentType in ApsContactLogNarrativeDaoImpl");
        String apsAssessmentType = null;
        apsAssessmentType = (String) ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getApsAssessmentType)
                .setParameter(ID_EVENT, idEvent))
                .addScalar("apsAssesmentType", StandardBasicTypes.STRING).uniqueResult();
        return apsAssessmentType;
    }


    /**
     * method to get ApsSa Event Id
     * @param idEvent
     * @return
     */
    @Override
    public Long getApsSaEventId(Long idEvent) {
        log.debug("Entering method  getFirstContactBySaEvent in ApsContactLogNarrativeDaoImpl");
        Long idApsSaEvent = null;
        idApsSaEvent = (Long) ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getApsSaEventId)
                .setParameter(ID_EVENT, idEvent))
                .addScalar("idApsSaEvent", StandardBasicTypes.LONG).uniqueResult();
        log.info(TRANSACTIONID + idEvent);
        return idApsSaEvent;
    }

    /**
     * method to get Aps Sa Safety Contact Data by Event Id
     * @param idEvent
     * @return
     */
    @Override
    public APSSafetyAssessmentContactDto getApsSaSafetyContactDataByEventId(Long idEvent) {
        log.debug("Entering method getApsSaSafetyContactDataByEventId in ApsContactLogNarrativeDaoImpl");

        APSSafetyAssessmentContactDto assessmentContactDto = null;
        SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getApsSaSafetyContactDataByEventId)
                .addScalar(CONTACTEVENTID,StandardBasicTypes.LONG)
                .addScalar("contactWorkerId",StandardBasicTypes.LONG)
                .addScalar("indContactAttempted",StandardBasicTypes.STRING)
                .addScalar("codeContactType",StandardBasicTypes.STRING)
                .addScalar(DTCONTACTOCCURRED,StandardBasicTypes.DATE)
                .addScalar(DTLASTUPDATE,StandardBasicTypes.DATE)
                .addScalar("location",StandardBasicTypes.STRING)
                .addScalar("contactWorkerFullName",StandardBasicTypes.STRING)
                .addScalar("contactMethodType",StandardBasicTypes.STRING)
                .addScalar("contactPurpose",StandardBasicTypes.STRING)
                .setParameter(ID_EVENT, idEvent).setResultTransformer(Transformers.aliasToBean(APSSafetyAssessmentContactDto.class)));
        assessmentContactDto = (APSSafetyAssessmentContactDto) sQLQuery1.uniqueResult();

        if(null != assessmentContactDto){
                String strContactType = null;
                if (!ObjectUtils.isEmpty(assessmentContactDto.getCodeContactType())) {
                    if (assessmentContactDto.getCodeContactType().equals(CodesConstant.CCNTCTYP_C24H)) {
                        strContactType = "CI";
                    } else if (assessmentContactDto.getCodeContactType().equals(CodesConstant.CCNTCTYP_CFTF)
                            ||assessmentContactDto.getCodeContactType().equals(CodesConstant.CCNTCTYP_CSAF)
                            || assessmentContactDto.getCodeContactType().equals(CodesConstant.CCNTCTYP_CSVC)) {
                        strContactType = "F2F";
                    }
                }
            assessmentContactDto.setContactType(strContactType);
            assessmentContactDto.setTimeContactOccurred(DateUtils.getTime(assessmentContactDto.getDateContactOccurred()));
            assessmentContactDto.setContactFullName(getPersonsContacted(idEvent));

        }
        return assessmentContactDto;
    }

    /**
     * method to get contact full number for event 
     * @param idEvent
     * @return
     */
    @Override
    public List<String> getPersonsContacted(Long idEvent){

        log.debug("Entering method  getContactEvents in ApsContactLogNarrativeDaoImpl");
        List<String> contactEventIds = null;
        contactEventIds = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getPersonsContacted)
                .setParameter(ID_EVENT, idEvent))
                .addScalar("contactFullName", StandardBasicTypes.STRING).list();
        log.info(TRANSACTIONID + idEvent);
        return contactEventIds;
    }

    /**
     * Method helps to run the query and get the results
     *
     * @param idEvent - selected id event value
     * @return - Return as Gold Narrative Dto data
     */
    @Override
    public GoldNarrativeDto getGuardianShipReferralNarrativeByEventId(Long idEvent) {
        SQLQuery sqlQuery = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getGuardianShipReferralNarrativeByEventIdSql)
                .addScalar("narrative", StandardBasicTypes.BLOB)
                .addScalar("formDescription", StandardBasicTypes.STRING)
                .addScalar("documentName", StandardBasicTypes.STRING)
                .addScalar("Title", StandardBasicTypes.STRING)
                .setParameter(ID_EVENT, idEvent)
                .setResultTransformer(Transformers.aliasToBean(GoldNarrativeDto.class)));
        GoldNarrativeDto dto = (GoldNarrativeDto) sqlQuery.uniqueResult();


        return dto;
    }

}
