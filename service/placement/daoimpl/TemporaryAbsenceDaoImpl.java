package us.tx.state.dfps.service.placement.daoimpl;


import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import us.tx.state.dfps.common.domain.Event;
import us.tx.state.dfps.common.domain.PlacementTa;
import us.tx.state.dfps.common.domain.StagePersonLink;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.EventDao;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.csa.dao.CSADao;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.fcl.dao.SexualVictimizationHistoryDao;
import us.tx.state.dfps.service.placement.dao.TemporaryAbsenceDao;
import us.tx.state.dfps.service.placement.dto.TemporaryAbsenceDto;
import us.tx.state.dfps.service.subcare.dto.PlacementDto;

@Repository
public class TemporaryAbsenceDaoImpl implements TemporaryAbsenceDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    MessageSource messageSource;
    
    @Autowired
	private EventDao eventDao;

    @Autowired
    private SexualVictimizationHistoryDao svhDao;

    @Autowired
    private CSADao cSADao;

    @Value("${TemporaryAbsenceDaoImpl.getOpenPlacementForStage}")
    private String getOpenPlacementForStageSql;

    @Value("${TemporaryAbsenceDaoImpl.getTemporaryAbsenceList}")
    private String TemporaryAbsenceList;

    @Value("${TemporaryAbsenceDaoImpl.isTAStartOrEndDtBeforePlcmtStart}")
    private String isTAStartOrEndDtBeforePlcmtStart;

    @Value("${TemporaryAbsenceDaoImpl.isTAStartOrEndDtBetweenRange}")
    private String isTAStartOrEndDtBetweenRange;

    @Value("${TemporaryAbsenceDaoImpl.isTABetweenRangeAddnlCondition}")
    private String isTARangeAdditionalCond;

    @Value("${TemporaryAbsenceDaoImpl.isNonMissingChildTABetweenRangeAddnlCondition}")
    private String isNonMissingChildTABetweenRangeAddnlCondition;

    @Value("${TemporaryAbsenceDaoImpl.getActiveTemporaryAbsencesForActivePlacements}")
    private String getActiveTemporaryAbsencesForActivePlacementsSql;

    @Value("${TemporaryAbsenceDaoImpl.getActiveTemporaryAbsencesForActivePlacement}")
    private String getActiveTemporaryAbsencesForActivePlacementSql;

    @Value("${TemporaryAbsenceDaoImpl.checkActivePlacementsCount}")
    private String checkActivePlacementsCount;

    @Value("${TemporaryAbsenceDaoImpl.isTAEndDtAfterPlcmtEnd}")
    private String isTAEndDtAfterPlcmtEnd;

    @Value("${TemporaryAbsenceDaoImpl.isPlacementEnded}")
    private String isPlacementEnded;
    
    @Value("${TemporaryAbsenceDaoImpl.isOpenTAPlacementPresent}")
    private String isOpenTAPlacementPresent;

    @Value("${TemporaryAbsenceDaoImpl.getActiveTAForActivePlacement}")
    private String getActiveTAForActivePlacement;

    @Value("${TemporaryAbsenceDaoImpl.getActiveTAForPlacement}")
    private String getActiveTAForPlacement;
    
    @Value("${TemporaryAbsenceDaoImpl.getLatestMissingTA}")
    private String getLatestMissingTA;

    /**
     * @param idStage
     * @return
     */
    @Override
    public PlacementDto getOpenPlacementForStage(Long idStage) {
        PlacementDto plcmtDto = null;
        Query query = sessionFactory.getCurrentSession().createSQLQuery(getOpenPlacementForStageSql)
                .addScalar("idPlcmtEvent", StandardBasicTypes.LONG)
                .addScalar("dtPlcmtStart", StandardBasicTypes.DATE)
                .addScalar("dtPlcmtEnd", StandardBasicTypes.DATE)
                .addScalar("txtEventDescr", StandardBasicTypes.STRING)
                .addScalar("cdPlcmtType", StandardBasicTypes.STRING)
                .addScalar("cdPlcmtLivArr", StandardBasicTypes.STRING)
                .setParameter("idStage", idStage)
                .setResultTransformer(Transformers.aliasToBean(PlacementDto.class));

        plcmtDto = (PlacementDto) query.uniqueResult();
        return plcmtDto;
    }

    /**
     * @param taDto
     * @param cReqFun
     * @return
     */
    @Override
    public TemporaryAbsenceDto saveOrUpdateTaInfo(TemporaryAbsenceDto taDto, String cReqFun){
        PlacementTa placementTa = new PlacementTa();
        if (!ObjectUtils.isEmpty(taDto.getIdPlacementTa())) {
            placementTa = (PlacementTa) sessionFactory.getCurrentSession().load(PlacementTa.class, taDto.getIdPlacementTa());
        }
        if(!ObjectUtils.isEmpty(taDto.getIdLinkedPlcmtEvent())) {
            Event event = new Event();
            event.setIdEvent(taDto.getIdLinkedPlcmtEvent());
            placementTa.setEventByIdPlcmtEvent(event);
        }
        placementTa.setDtStart(taDto.getDtTemporaryAbsenceStart());
        placementTa.setDtEnd(taDto.getDtTemporaryAbsenceEnd());
        placementTa.setCdType(taDto.getTemporaryAbsenceType());
        placementTa.setIdResource(taDto.getIdTaRsrcAgency());
        placementTa.setIdRespitePerson(taDto.getIdPersonRespiteVisit());
        placementTa.setNmResource(taDto.getNmTemporaryAbsence());
        placementTa.setTxtComments(taDto.getTxtComments());
        placementTa.setAddrStLn1(taDto.getTaAddrLn1());
        placementTa.setAddrStLn2(taDto.getTaAddrLn2());
        placementTa.setAddrCity(taDto.getTaAddrCity());
        if(!ObjectUtils.isEmpty(taDto.getTaAddrZip())){
            placementTa.setAddrAddrZip(taDto.getTaAddrZip().concat((taDto.getTaAddrZipExtension() != null && !StringUtils.isEmpty(taDto.getTaAddrZipExtension())) ? taDto.getTaAddrZipExtension() : ""));
        }else{
        	placementTa.setAddrAddrZip(null);
        }
        placementTa.setCdAddrState(taDto.getTaAddrSt());
        placementTa.setCdAddrCounty(taDto.getTaAddrCnty());
        placementTa.setNbrPhn(taDto.getNbrPhone());
        placementTa.setNbrPhoneExt(taDto.getNbrPhoneExt());
        placementTa.setNmPointOfContact(taDto.getPointOfContact());
        placementTa.setDt2279B(taDto.getDt2279BReviewed());
        placementTa.setDtAttachA(taDto.getDtAttachAReviewed());
        placementTa.setInd2279BNa(taDto.getIndDt2279bNA());
        placementTa.setIndAttachANa(taDto.getIndDtAttachANA());
        placementTa.setCdGcdRtrn(taDto.getCdGcdRtrn());
        placementTa.setCdAddrRtrn(taDto.getCdAddrRtrn());
        placementTa.setTxtMailbltyScore(taDto.getTxtMailbltyScore());
        placementTa.setNbrGcdLong(taDto.getNbrGcdLong());
        placementTa.setNbrGcdLat(taDto.getNbrGcdLat());
        placementTa.setNmCnty(taDto.getNmCnty());
        placementTa.setNmCntry(taDto.getNmCntry());
    	placementTa.setIndValdtd(taDto.getIndValdtd());
    	if(taDto.getIsAddrValidated()!=null && taDto.getIsAddrValidated()){
    		placementTa.setDtValdtd(new Date());
    	}else if (taDto.getIndValdtd()==null || taDto.getIndValdtd().trim().length()==0){
    		placementTa.setDtValdtd(null);
    	}
    	if(!ObjectUtils.isEmpty(taDto.getIdEvent())) {
            placementTa.setIdEvent(taDto.getIdEvent());
        }
        if(!ObjectUtils.isEmpty(taDto.getIdChldMsngDtl())) {
            placementTa.setidChldMsngDtl(taDto.getIdChldMsngDtl());
        }

        switch (cReqFun) {
            case ServiceConstants.REQ_FUNC_CD_ADD:
                placementTa.setIdCreatedPerson(taDto.getIdCreatedPerson());
                placementTa.setDtCreated(new Date());
                placementTa.setIdLastUpdatePerson(taDto.getIdCreatedPerson());
                placementTa.setDtLastUpdate(new Date());
                break;
            case ServiceConstants.REQ_FUNC_CD_UPDATE:
                placementTa.setIdLastUpdatePerson(taDto.getIdLastUpdatePerson());
                placementTa.setDtLastUpdate(new Date());
                break;
            default:
                break;
        }

        sessionFactory.getCurrentSession().saveOrUpdate(placementTa);
        taDto.setIdPlacementTa(placementTa.getIdPlacementTa());
        taDto.setDtLastUpdate(placementTa.getDtLastUpdate());
        taDto.setDtCreated(placementTa.getDtCreated());
        return taDto;
    }

    /**
     * @param idCase
     * @param idStage
     * @return
     */
    @Override
    public List<TemporaryAbsenceDto> getTemporaryAbsenceList(Long idCase, Long idStage){
        SQLQuery query = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(TemporaryAbsenceList)
                .addScalar("idPlacementTa", StandardBasicTypes.LONG)
                .addScalar("idLinkedPlcmtEvent", StandardBasicTypes.LONG)
                .addScalar("idEvent", StandardBasicTypes.LONG)
                .addScalar("linkedPlacementDesc", StandardBasicTypes.STRING)
                .addScalar("dtTemporaryAbsenceStart", StandardBasicTypes.DATE)
                .addScalar("dtTemporaryAbsenceEnd", StandardBasicTypes.DATE)
                .addScalar("temporaryAbsenceType", StandardBasicTypes.STRING)
                .setParameter("idStage", idStage)
                .setResultTransformer(Transformers.aliasToBean(TemporaryAbsenceDto.class)));

        List<TemporaryAbsenceDto> temporaryAbsenceList = (List<TemporaryAbsenceDto>) query.list();

        return temporaryAbsenceList;
    }

    /**
     * @param idPlacementTa
     * @return
     */
    @Override
    public PlacementTa getTemporaryAbsenceById(Long idPlacementTa){
        Criteria cr = sessionFactory.getCurrentSession().createCriteria(PlacementTa.class)
                .add(Restrictions.eq("idPlacementTa", idPlacementTa));
        return (PlacementTa) cr.uniqueResult();
    }

    @Override
    public void updateTAEndDate(Long idPlacementTa, Date endDate, Long idUser) {
        PlacementTa placementTa = (PlacementTa) sessionFactory.getCurrentSession().load(PlacementTa.class, idPlacementTa);
        placementTa.setDtEnd(endDate);
        placementTa.setIdLastUpdatePerson(idUser);
        placementTa.setDtLastUpdate(new Date());
        sessionFactory.getCurrentSession().saveOrUpdate(placementTa);
    }

    @Override
    public void updateTAStartDate(Long idPlacementTa, Date startDate, Long idUser) {
        PlacementTa placementTa = (PlacementTa) sessionFactory.getCurrentSession().load(PlacementTa.class, idPlacementTa);
        placementTa.setDtStart(startDate);
        placementTa.setIdLastUpdatePerson(idUser);
        placementTa.setDtLastUpdate(new Date());
        sessionFactory.getCurrentSession().saveOrUpdate(placementTa);
    }

    /**
     * this method will validate if the TA start date or end dt added or updated from UI falls before
     * placement start or end date
     * @param idPlcmtEvent
     * @param taStartOrEndDt
     * @return true if TA start date or end date are before plcmt start date
     */
    @Override
    public boolean isTAStartOrEndDtBeforePlcmtStart(Long idPlcmtEvent, Date taStartOrEndDt){
    	Query query = sessionFactory.getCurrentSession().createSQLQuery(isTAStartOrEndDtBeforePlcmtStart)
				.addScalar("idPlcmtEvent", StandardBasicTypes.LONG);
        query.setParameter("taStartOrEndDt", taStartOrEndDt);
		query.setParameter("idPlcmtEvent", idPlcmtEvent);
		List<Long> list = query.list();
		return (!ObjectUtils.isEmpty(list)) ? true : false;
    }

    /**
     * this method will validate if the TA start date or end dt added or updated from UI falls between
     * existing TA
     * @param idPlacementTa
     * @param dtStart
     * @return true if TA start date or end date are between existing TA date range
     */
    @Override
    public boolean isTAStartOrEndDtBetweenRange(Long idStage, Date dtStart,Date dtEnd, Long idPlacementTa, String temporaryAbsenceType) throws ParseException {
    	idPlacementTa = Objects.isNull(idPlacementTa)? 0L:idPlacementTa;
    	dtStart = dtStart==null?new SimpleDateFormat("MM/dd/yyyy").parse("12/31/4712"):dtStart;
    	dtEnd = dtEnd==null?new SimpleDateFormat("MM/dd/yyyy").parse("12/31/4712"):dtEnd;
        String sqlquery = isTAStartOrEndDtBetweenRange;
        if(!dtEnd.equals(new SimpleDateFormat("MM/dd/yyyy").parse("12/31/4712"))){
            sqlquery = sqlquery + isTARangeAdditionalCond;
        }
        if(!"Missing Child".equalsIgnoreCase(temporaryAbsenceType) || !dtEnd.equals(new SimpleDateFormat("MM/dd/yyyy").parse("12/31/4712"))) {
            sqlquery = sqlquery + isNonMissingChildTABetweenRangeAddnlCondition;
        }
        sqlquery = sqlquery + ")";
    	Query query = sessionFactory.getCurrentSession().createSQLQuery(sqlquery)
				.addScalar("idPlcmtEvent", StandardBasicTypes.LONG);

		query.setParameter("dtStart", dtStart);
		query.setParameter("dtEnd", dtEnd);
		query.setParameter("idPlacementTa", idPlacementTa);
		query.setParameter("idStage", idStage);
		List<Long> list = query.list();
		return (!ObjectUtils.isEmpty(list)) ? true : false;
    }

    /**
     * @param stageId
     * @return
     */
    @Override
    public Long getActiveTemporaryAbsencesForActivePlacements(Long stageId) {
        Query countActiveTAs = ((Query) sessionFactory.getCurrentSession()
                .createSQLQuery(getActiveTemporaryAbsencesForActivePlacementsSql).setParameter("stageId", stageId));
        List<Long> list = countActiveTAs.list();
        return (!ObjectUtils.isEmpty(list)) ?((BigDecimal) countActiveTAs.uniqueResult()).longValue():0L;
    }

    /**
     * @param placementEventId
     * @return
     */
    @Override
    public Long getActiveTemporaryAbsencesForActivePlacement(Long placementEventId) {
        Query countActiveTAs = ((Query) sessionFactory.getCurrentSession()
                .createSQLQuery(getActiveTemporaryAbsencesForActivePlacementSql).setParameter("placementEventId", placementEventId));
        List<Long> list = countActiveTAs.list();
        return (!ObjectUtils.isEmpty(list)) ? ((BigDecimal) countActiveTAs.uniqueResult()).longValue():0L;
    }

    @Override
    public Long getActiveTAForActivePlacement(Long placementEventId) {
        Query idPlacementTA = ((Query) sessionFactory.getCurrentSession()
                .createSQLQuery(getActiveTAForActivePlacement).setParameter("placementEventId", placementEventId));
        List<Long> list = idPlacementTA.list();
        return (!ObjectUtils.isEmpty(list)) ? ((BigDecimal) idPlacementTA.uniqueResult()).longValue():0L;
    }

    /**
     * @param idCase
     * @param idStage
     * @return
     */
    @Override
    public Long getPrimaryChildId(Long idCase, Long idStage){
        Long childId = ServiceConstants.ZERO_VAL;
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(StagePersonLink.class);
        criteria.add(Restrictions.eq("idStage", idStage));
        criteria.add(Restrictions.eq("idCase", idCase));
        criteria.add(Restrictions.eq("cdStagePersRole", ServiceConstants.CROLES_PC));
        List<StagePersonLink> stagePersonLinkList = criteria.list();
        if (!TypeConvUtil.isNullOrEmpty(stagePersonLinkList)) {
            childId = stagePersonLinkList.get(ServiceConstants.Zero).getIdPerson();
        } else {
            throw new DataNotFoundException(messageSource.getMessage("Common.noRecordFound", null, Locale.US));
        }
        return childId;
    }

    @Override
    public Long checkActivePlacementsCountForStageId(Long stageId) {
        Query countQuery = ((Query) sessionFactory.getCurrentSession()
                .createSQLQuery(checkActivePlacementsCount).setParameter("stageId", stageId));
        List<Long> list = countQuery.list();
        return (!ObjectUtils.isEmpty(list)) ? ((BigDecimal) countQuery.uniqueResult()).longValue() : 0L;
    }

    @Override
    public void deleteTaInfo(Long placementTaId, Long loginUserId) {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(PlacementTa.class);
        criteria.add(Restrictions.eq("idPlacementTa", placementTaId));
        PlacementTa placementTa = (PlacementTa) criteria.uniqueResult();
        if (TypeConvUtil.isNullOrEmpty(placementTa)) {
            throw new DataNotFoundException(messageSource
                    .getMessage("TemporaryAbsenceDaoImpl.placementTa.not.found", null, Locale.US));
        }
        //check to see if TA is part of SIH or CSA
        svhDao.updateSVHForTA(placementTaId,loginUserId);
        cSADao.updateCSAForTA(placementTaId,loginUserId);
        Long idEvent = placementTa.getIdEvent();
        sessionFactory.getCurrentSession().delete(placementTa);
		eventDao.deleteEventById(idEvent);
    }

    /**
     * @param idPlcmtEvent
     * @param taEndDt
     * @return
     */
    @Override
    public boolean isTAEndDtAfterPlcmtEnd(Long idPlcmtEvent, Date taEndDt){
        Query query = sessionFactory.getCurrentSession().createSQLQuery(isTAEndDtAfterPlcmtEnd)
                .addScalar("idPlcmtEvent", StandardBasicTypes.LONG);
        query.setParameter("taEndDt", taEndDt);
        query.setParameter("idPlcmtEvent", idPlcmtEvent);
        List<Long> list = query.list();
        return (!ObjectUtils.isEmpty(list)) ? true : false;
    }

    /**
     * @param idPlcmtEvent
     * @return
     */
    @Override
    public boolean isPlacementEnded(Long idPlcmtEvent){
        Query query = sessionFactory.getCurrentSession().createSQLQuery(isPlacementEnded)
                .addScalar("idPlcmtEvent", StandardBasicTypes.LONG);
               query.setParameter("idPlcmtEvent", idPlcmtEvent);
        List<Long> list = query.list();
        return (!ObjectUtils.isEmpty(list)) ? true : false;
    }
    
    /**
     * @param idEvent
     * @return
     */
    @Override
    public Long getPlacementTaByEventId(Long idEvent){
        Criteria cr = sessionFactory.getCurrentSession().createCriteria(PlacementTa.class)
                .add(Restrictions.eq("idEvent", idEvent));
        PlacementTa placementTa =  (PlacementTa) cr.uniqueResult();
        return placementTa!=null?placementTa.getIdPlacementTa():null;
    }
    
    public boolean isOpenTAPlacementPresent(Long idStage){
    	Query countActiveTAs = ((Query) sessionFactory.getCurrentSession()
                .createSQLQuery(isOpenTAPlacementPresent).setParameter("idStage", idStage));
        List<Long> list = countActiveTAs.list();
        return list!=null && list.size()>0;
    }

    @Override
    public TemporaryAbsenceDto getActiveTAForPlacement(Long idPlacementEvent) {
        Query query = (Query)sessionFactory.getCurrentSession()
                .createSQLQuery(getActiveTAForPlacement)
                .addScalar("idPlacementTa", StandardBasicTypes.LONG)
                .addScalar("dtTemporaryAbsenceStart", StandardBasicTypes.DATE)
                .addScalar("dtTemporaryAbsenceEnd", StandardBasicTypes.DATE)
                .addScalar("temporaryAbsenceType", StandardBasicTypes.STRING)
                .setParameter("idPlacementEvent", idPlacementEvent)
                .setResultTransformer(Transformers.aliasToBean(TemporaryAbsenceDto.class));
        TemporaryAbsenceDto temporaryAbsenceDto = (TemporaryAbsenceDto) query.uniqueResult();
        return temporaryAbsenceDto;
    }

    @Override
    public PlacementTa getPlacementTAByMissingChild(Long idChildMissingDtl) {
        Criteria cr = sessionFactory.getCurrentSession().createCriteria(PlacementTa.class)
                .add(Restrictions.eq("idChldMsngDtl", idChildMissingDtl))
                .addOrder(Order.desc("idChldMsngDtl"))
                .setMaxResults(1);
        PlacementTa placementTa = (PlacementTa) cr.uniqueResult();
        return placementTa != null ? placementTa : null;
    }
    
    @Override
    public TemporaryAbsenceDto getLatestMissingTA(Long idPlcmtEvent) {
    	Query query = (Query)sessionFactory.getCurrentSession()
                .createSQLQuery(getLatestMissingTA)
                .addScalar("idPlacementTa", StandardBasicTypes.LONG)
                .addScalar("dtTemporaryAbsenceStart", StandardBasicTypes.DATE)
                .addScalar("dtTemporaryAbsenceEnd", StandardBasicTypes.DATE)
                .addScalar("idChldMsngEvent", StandardBasicTypes.LONG)
                .setParameter("idPlcmtEvent", idPlcmtEvent)
                .setResultTransformer(Transformers.aliasToBean(TemporaryAbsenceDto.class));
        TemporaryAbsenceDto temporaryAbsenceDto = (TemporaryAbsenceDto) query.uniqueResult();
        return temporaryAbsenceDto;
    }
}

