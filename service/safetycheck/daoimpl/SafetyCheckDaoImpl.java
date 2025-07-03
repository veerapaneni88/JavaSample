package us.tx.state.dfps.service.safetycheck.daoimpl;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;
import us.tx.state.dfps.common.domain.HMSafetyCheck;
import us.tx.state.dfps.common.domain.HMSafetyCheckAttachment;
import us.tx.state.dfps.common.domain.HMSafetyCheckLink;
import us.tx.state.dfps.common.domain.HMSafetyCheckNarr;
import us.tx.state.dfps.service.hmm.dto.SafetyCheckChildInfoDto;
import us.tx.state.dfps.service.hmm.dto.SafetyCheckListDto;
import us.tx.state.dfps.service.safetycheck.dao.SafetyCheckDao;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Class Description:SafetyCheckDaoImpl is the DAOImpl for Safety Check Detail Page
 */
@Repository
public class SafetyCheckDaoImpl implements SafetyCheckDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Value("${SafetyCheckDaoImpl.getSafetyCheckListForPlcmnt}")
    private String getSafetyCheckListForPlcmntSql;

    @Value("${SafetyCheckDaoImpl.getChildInfoForRsrc}")
    private String getChildInfoForRsrcSql;

    @Value("${SafetyCheckDaoImpl.getSafetyCheckDetailForPlcmnt}")
    private String getSafetyCheckDetailForPlcmntSql;

    @Value("${SafetyCheckDaoImpl.getSafetyCheckDetailForPlcmntByEventId}")
    private String getSafetyCheckDetailForPlcmntByEventIdSql;

    /**
     * PPM 60692-artf178537-Start-Changes for Safety check List
     * Method Description: This method retrieves all the safety checks done for a resource
     *
     * @param idResource
     * @return SafetyCheckRes
     */
    @Override
    public List<SafetyCheckListDto> getSafetyCheckDetails(Long idResource) {
         //fetch the safety checks done for the resource. Also perform the outer join with person table to fetch the full name of id created and id updated.
        // Outer join makes sure the safety checks are fetched even when we have dummy person id in case of any batch or script updates
                List<SafetyCheckListDto> list= (List<SafetyCheckListDto>) sessionFactory.getCurrentSession().createCriteria(HMSafetyCheck.class)
                .createAlias("createdPerson","createdPerson", JoinType.LEFT_OUTER_JOIN)
                .createAlias("lastUpdatedPerson","lastUpdatedPerson", JoinType.LEFT_OUTER_JOIN).
                add(Restrictions.eq("capsResource.idResource",idResource)).addOrder(Order.desc("dtCreated")).setProjection(
                Projections.projectionList()
                        .add(Property.forName("createdPerson.nmPersonFull"),"createdBy").add(Property.forName("cdStatus"),"cdEventStatus")
                        .add(Property.forName("dtCreated"),"dtEventCreated").add(Property.forName("idHMSafetyCheck"),"idHMSafetyCheck")
                        .add(Property.forName("lastUpdatedPerson.nmPersonFull"),"lastUpdateBy")
                        ).setResultTransformer(Transformers.aliasToBean(SafetyCheckListDto.class)).list();

         return list;
    }

    /**
     * PPM 60692-artf179776- Safety Check Details for Placement
     * Method to get safety check record list for placement
     * @param idStage
     * @return List
     */
    public List<SafetyCheckListDto> getSafetyCheckListForPlcmnt(Long idStage) {

        List<SafetyCheckListDto> list= sessionFactory.getCurrentSession().createSQLQuery(getSafetyCheckListForPlcmntSql)
                .addScalar("dtEventCreated", StandardBasicTypes.TIMESTAMP)
                .addScalar("cdEventStatus")
                .addScalar("idHMSafetyCheck",StandardBasicTypes.LONG)
                .addScalar("cdEventType")
                .addScalar("cdStage")
                .addScalar("idStage", StandardBasicTypes.LONG)
                .addScalar("idResource", StandardBasicTypes.LONG)
                .addScalar("nmResource")
                .addScalar("createdBy")
                .addScalar("idEvent", StandardBasicTypes.LONG)
                .setParameter("idStage", idStage)
                .setResultTransformer(Transformers.aliasToBean(SafetyCheckListDto.class))
                .list();
        return list;
    }

    /**
     * PPM 60692-artf179566- getChildInfoForRsrc
     * Method Description: This method is used to fetch kids placed in an operation
     *
     * @param idResource
     * @return SafetyCheckDto
     */
    @Override
    public List<SafetyCheckChildInfoDto> getChildInfoForRsrc(Long idResource) {
        List<SafetyCheckChildInfoDto> safetyCheckChildInfoDtoList= sessionFactory.getCurrentSession().createSQLQuery(getChildInfoForRsrcSql)
                .addScalar("idCase",StandardBasicTypes.LONG)
                .addScalar("cdStage")
                .addScalar("nmStage")
                .addScalar("idStage",StandardBasicTypes.LONG)
                .addScalar("placementStartDate", StandardBasicTypes.TIMESTAMP)
                .addScalar("idPlacementEvent",StandardBasicTypes.LONG)
                .addScalar("idPerson",StandardBasicTypes.LONG)
                .setParameter("idresource", idResource)
                .setResultTransformer(Transformers.aliasToBean(SafetyCheckChildInfoDto.class))
                .list();
        return safetyCheckChildInfoDtoList;
    }
    /**
     * Artifact artf179776 : Safety Check Details for Placement
     * This method fetches Safety check details for the record selected from the list.
     * @param idHmSafetyCheck
     * @param idEvent
     * @return SafetyCheckChildInfoDto
     */
    @Override
    public SafetyCheckChildInfoDto getSafetyCheckDetailForPlcmnt(Long idHmSafetyCheck,Long idEvent) {

        SafetyCheckChildInfoDto safetyCheckChildInfoDto= (SafetyCheckChildInfoDto) sessionFactory.getCurrentSession().createSQLQuery(getSafetyCheckDetailForPlcmntSql)
                .addScalar("placementStartDate", StandardBasicTypes.TIMESTAMP)
                .addScalar("callIdNumber",StandardBasicTypes.LONG)
                .addScalar("safetyCheckDate", StandardBasicTypes.TIMESTAMP)
                .addScalar("childSafe")
                .addScalar("abuseNeglectReportInitiated")
                .addScalar("idPlacementEvent", StandardBasicTypes.LONG)
                .addScalar("idCreatedPerson", StandardBasicTypes.LONG)
                .addScalar("idLastUpdatePerson", StandardBasicTypes.LONG)
                .addScalar("dtCreated",StandardBasicTypes.TIMESTAMP)
                .addScalar("dtLastUpdate", StandardBasicTypes.TIMESTAMP)
                .setParameter("idHmSafetyCheck", idHmSafetyCheck)
                .setParameter("idEvent", idEvent)
                .setResultTransformer(Transformers.aliasToBean(SafetyCheckChildInfoDto.class))
                .uniqueResult();
        return safetyCheckChildInfoDto;
    }

    /**
     * Artifact artf179776 : Safety Check Details for Placement
     * This method fetches Safety check details for the record selected from the event list using event id.
     * @param idEvent
     * @return SafetyCheckChildInfoDto
     */
    @Override
    public SafetyCheckChildInfoDto getSafetyCheckDetailForPlcmnt(Long idEvent) {
        SafetyCheckChildInfoDto safetyCheckChildInfoDto= (SafetyCheckChildInfoDto) sessionFactory.getCurrentSession().createSQLQuery(getSafetyCheckDetailForPlcmntByEventIdSql)
                .addScalar("placementStartDate", StandardBasicTypes.TIMESTAMP)
                .addScalar("callIdNumber",StandardBasicTypes.LONG)
                .addScalar("safetyCheckDate", StandardBasicTypes.TIMESTAMP)
                .addScalar("childSafe")
                .addScalar("abuseNeglectReportInitiated")
                .addScalar("idResource", StandardBasicTypes.LONG)
                .setParameter("idEvent", idEvent)
                .setResultTransformer(Transformers.aliasToBean(SafetyCheckChildInfoDto.class))
                .uniqueResult();
        return safetyCheckChildInfoDto;
    }

    /**
     * PPM 60692-artf179566- loadHMSafetyCheck
     * Method Description: This method is used to fetch saved HMSafetyCheck entity
     *
     * @param idHMSafetyCheck@return SafetyCheckDto
     */
    @Override
    public HMSafetyCheck loadHMSafetyCheck(Long idHMSafetyCheck) {
        return (HMSafetyCheck) sessionFactory.getCurrentSession().get(HMSafetyCheck.class,idHMSafetyCheck);
    }
    /**
     * PPM 60692-artf179566- saveOrUpdate
     * Method Description: This method is used to save or update safety check
     *
     * @param hmSafetyCheck
     */
    @Override
    public void saveOrUpdate(HMSafetyCheck hmSafetyCheck) {
        sessionFactory.getCurrentSession().saveOrUpdate(hmSafetyCheck);
    }

    /**
     * PPM 60692-artf179566- deleteSafetyCheck
     * Method Description: This method is used to delete safety check Details
     *
     * @param hmSafetyCheck
     * @return void
     */
    @Override
    public void deleteSafetyCheck(HMSafetyCheck hmSafetyCheck) {
        if (!ObjectUtils.isEmpty(hmSafetyCheck.getHmSafetyCheckLink())) {
            List<Long> idEventList=hmSafetyCheck.getHmSafetyCheckLink().stream().map(childlinkentity-> childlinkentity.getEvent().getIdEvent()).collect(Collectors.toList());
            idEventList.forEach(id -> {
                HMSafetyCheckNarr hmSafetyCheckNarr = (HMSafetyCheckNarr) sessionFactory.getCurrentSession().createCriteria(HMSafetyCheckNarr.class).add(Restrictions.eq("event.idEvent",id))
                        .uniqueResult();
                if( hmSafetyCheckNarr != null){
                    sessionFactory.getCurrentSession().delete(hmSafetyCheckNarr);
                }

            });
        }
        sessionFactory.getCurrentSession().delete(hmSafetyCheck);
    }

    /**
     * PPM 60692-artf179566- deleteHmSafetyCheckChildInfo
     * Method Description: This method is used to delete the child records in the safety check table that has been saved once but unselected from front end afterwards
     *
     * @param idHMSafetyCheckChildInfoList
     * @return void
     */
    @Override
    public void deleteHmSafetyCheckChildInfo(List<Long> idHMSafetyCheckChildInfoList) {
        if (!ObjectUtils.isEmpty(idHMSafetyCheckChildInfoList)) {
            idHMSafetyCheckChildInfoList.stream().forEach(id -> {
                HMSafetyCheckLink hmSafetyCheckLink = (HMSafetyCheckLink) sessionFactory.getCurrentSession().get(HMSafetyCheckLink.class,id);
                Long idEvent=hmSafetyCheckLink.getEvent().getIdEvent();

                HMSafetyCheckNarr hmSafetyCheckNarr= (HMSafetyCheckNarr) sessionFactory.getCurrentSession().createCriteria(HMSafetyCheckNarr.class).add(Restrictions.eq("event.idEvent",idEvent))
                        .uniqueResult();
                    if( hmSafetyCheckNarr != null){
                        sessionFactory.getCurrentSession().delete(hmSafetyCheckNarr);
                    }
                sessionFactory.getCurrentSession().delete(hmSafetyCheckLink);
            });
        }
    }

    /**
     * PPM 60692-artf179566- hmSafetyCheckNarrExists
     * Method Description: This method is used to check is the narrative already exists
     *
     * @param idEvent
     * @return boolean
     */
    @Override
    public boolean hmSafetyCheckNarrExists(Long idEvent) {
        HMSafetyCheckNarr hmSafetyCheckNarr= (HMSafetyCheckNarr) sessionFactory.getCurrentSession().createCriteria(HMSafetyCheckNarr.class).add(Restrictions.eq("event.idEvent",idEvent))
                .uniqueResult();
        return hmSafetyCheckNarr != null ;
    }

    /**
     * PPM 60692-artf179566- deleteAttachment
     * Method Description: This method is used to delete Attachment
     *
     * @param idHMSafetyCheckAttachment
     * @return void
     */
    @Override
    public void deleteAttachment(Long idHMSafetyCheckAttachment) {
        HMSafetyCheckAttachment hmSafetyCheckAttachment=(HMSafetyCheckAttachment) sessionFactory.getCurrentSession().get(HMSafetyCheckAttachment.class,idHMSafetyCheckAttachment);
        sessionFactory.getCurrentSession().delete(hmSafetyCheckAttachment);
    }

}
