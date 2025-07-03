package us.tx.state.dfps.service.conservatorship.daoimpl;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.CnsrvtrshpRemoval;
import us.tx.state.dfps.common.dto.EmailDetailsDto;
import us.tx.state.dfps.rmvlchecklist.dto.RmvlChckListRspnDto;
import us.tx.state.dfps.rmvlchecklist.dto.RmvlChcklstSctnTaskValueDto;
import us.tx.state.dfps.rmvlchecklist.dto.RmvlChcklstValueDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.CommonEventIdReq;
import us.tx.state.dfps.service.common.response.CommonBooleanRes;
import us.tx.state.dfps.service.common.response.CommonHelperRes;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.conservatorship.dao.CnsrvtrshpRemovalDao;
import us.tx.state.dfps.service.conservatorship.dto.CnsrvtrshpRemovalDto;
import us.tx.state.dfps.service.cpsinvreport.daoimpl.CpsInvReportDaoImpl;
import us.tx.state.dfps.service.exception.ServiceLayerException;
import us.tx.state.dfps.service.lookup.dao.LookupDao;


/**
 * 
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name:CSUB14S Class
 * Description: This Method extends BaseDao and implements CnsrvtrshpRemovalDao.
 * This is used to retrieve CnsrvtrshpRemoval details from database. May 01,
 * 2017 - 9:50:30 AM
 *  * **********  Change History *********************************
 * 05/04/2020 thompswa artf147748 : CPI Project - adjustment for removal checklist data model change
 */

@Repository
public class CnsrvtrshpRemovalDaoImpl implements CnsrvtrshpRemovalDao {
	
	private static final Logger logger = Logger.getLogger(CnsrvtrshpRemovalDaoImpl.class);
	
	@Value("${CnsrvtrshpRemovalDaoImpl.getCnsrvtrshpRemovalDtl}")
	private String CnsrvtrshpRemovalDtl;

	@Value("${CnsrvtrshpRemovalDaoImpl.babyMosesRemovalCount}")
	private String babyMosesRemovalCount;

	@Value("${CnsrvtrshpRemovalDaoImpl.fetchEmailAddress}")
	private String emailAddress;

	@Value("${CnsrvtrshpRemovalDaoImpl.nextGroupIdSequence}")
	private String nextGroupIdSequence;

	@Value("${CnsrvtrshpRemovalDaoImpl.getRmvlDtForEarliestEvent}")
	private String getRmvlDtForEarliestEventSql;

	@Value("${CnsrvtrshpRemovalDaoImpl.getRmvlGroupsByStage}")
	private String getRmvlGroupByStageSql;

	@Value("${CnsrvtrshpRemovalDaoImpl.getRmvlSctnTaskByStage}")
	private String getRmvlSctnTaskByStageSql;

	@Value("${CnsrvtrshpRemovalDaoImpl.getRmvlRspnsByStage}")
	private String getRmvlRspnsByStageSql;

	@Autowired
	MessageSource messageSource;

	@Autowired
	public SessionFactory sessionFactory;

	@Autowired
	LookupDao lookupDao;

	public CnsrvtrshpRemovalDaoImpl() {

	}

	/**
	 * Method Description: This Method will be used to retrieve a full row from
	 * the REMOVAL. Service Name:CSUB14S DAM:CSES20D
	 * 
	 * @param idEvent
	 * @return CnsrvtrshpRemovalDto @
	 */
	@SuppressWarnings("unchecked")
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public List<CnsrvtrshpRemovalDto> getCnsrvtrshpRemovalDtl(List<Long> idEvents) {

		List<CnsrvtrshpRemovalDto> removalDtlList = new ArrayList<>();

		Query query = (Query) sessionFactory.getCurrentSession().createSQLQuery(CnsrvtrshpRemovalDtl)
				.addScalar("dtLastUpdate", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtRemoval", StandardBasicTypes.TIMESTAMP)
				.addScalar("indRemovalNaCare", StandardBasicTypes.CHARACTER)
				.addScalar("indRemovalNaChild", StandardBasicTypes.CHARACTER)
				.addScalar("removalAgeMo", StandardBasicTypes.LONG).addScalar("removalAgeYr", StandardBasicTypes.LONG)
				.addScalar("idRemovalEvent", StandardBasicTypes.LONG).addScalar("idVictim", StandardBasicTypes.LONG)
				.addScalar("nmPersonFull", StandardBasicTypes.STRING).setParameterList("idRemEvents", idEvents)
				.setResultTransformer(Transformers.aliasToBean(CnsrvtrshpRemovalDto.class));

		removalDtlList = (List<CnsrvtrshpRemovalDto>) query.list();

		return removalDtlList;
	}

	@Override
	public CommonHelperRes updateIdRmvlGroup(CommonEventIdReq eventIdList) {
		CommonHelperRes updateIdRmvlGroup = new CommonHelperRes();
		if (!TypeConvUtil.isNullOrEmpty(eventIdList)) {
			BigDecimal idGroupRmvl = (BigDecimal) sessionFactory.getCurrentSession().createSQLQuery(nextGroupIdSequence)
					.uniqueResult();
			for (Long eventId : eventIdList.getIdEvents()) {
				CnsrvtrshpRemoval cnsrvtrshpRemoval = (CnsrvtrshpRemoval) sessionFactory.getCurrentSession()
						.load(CnsrvtrshpRemoval.class, Long.valueOf(eventId));
				if (!TypeConvUtil.isNullOrEmpty(cnsrvtrshpRemoval)) {

					cnsrvtrshpRemoval.setDtLastUpdate(new Date());
					cnsrvtrshpRemoval.setIdRmvlGroup(idGroupRmvl.longValue());
					sessionFactory.getCurrentSession().saveOrUpdate(cnsrvtrshpRemoval);
				}

			}
			updateIdRmvlGroup.setMessage(ServiceConstants.SUCCESS);
		}
		return updateIdRmvlGroup;
	}

	/**
	 * 
	 * Method Name: babyMosesRemovalReasonExists Method Description:
	 * 
	 * @param idCase
	 * @return @
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public CommonBooleanRes babyMosesRemovalReasonExists(Long idStage) {
		CommonBooleanRes resp = new CommonBooleanRes();
		// Updating the QUERY to join with event table to fetch only for the
		// selected
		// stage
		Long count = (Long) ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(babyMosesRemovalCount)
				.setParameter("idStage", idStage).setParameter("cdRemovalReason", ServiceConstants.CREMFRHR_TAA))
						.addScalar("count", StandardBasicTypes.LONG).uniqueResult();
		if (count > 0) {
			resp.setExists(Boolean.TRUE);
		} else {
			resp.setExists(Boolean.FALSE);
		}

		return resp;
	}

	/**
	 * 
	 * Method Name: fetchEmployeeEmail Method Description:This Method is used
	 * for fetching the primary and secondary case-workers employee email
	 * addresses based on the event id
	 * 
	 * @param idEventList
	 * @return List<EmailDetailsDto> @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<EmailDetailsDto> fetchEmployeeEmail(List<Long> idEventList) {
		// Creating the New List of EmailDetailsDto to be sent as response
		List<EmailDetailsDto> emailDetailsDtoList = new ArrayList<EmailDetailsDto>();
		// creating the SQL query to fetch the list of emailDTO's
		emailDetailsDtoList = (List<EmailDetailsDto>) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(emailAddress).setParameterList("idEvent", idEventList))
						.addScalar("emailAddress", StandardBasicTypes.STRING)
						.addScalar("stageName", StandardBasicTypes.STRING).addScalar("idStage", StandardBasicTypes.LONG)
						.addScalar("idEvent", StandardBasicTypes.LONG)
						.setResultTransformer(Transformers.aliasToBean(EmailDetailsDto.class)).list();

		return emailDetailsDtoList;
	}

	/**
	 * Method Name: getRmvlDtForEarliestEvent Method Description:This method is
	 * used to fetch the removal date from the earliest conservatorship removal.
	 * 
	 * @param idPriorStage
	 * @return Date
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Date getRmvlDtForEarliestEvent(Long idStage) {
		SQLQuery query = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getRmvlDtForEarliestEventSql)
				.addScalar("dtRemoval", StandardBasicTypes.TIMESTAMP).setParameter("idStage", idStage)
				.setResultTransformer(Transformers.aliasToBean(CnsrvtrshpRemovalDto.class));
		List<CnsrvtrshpRemovalDto> list = query.list();
		Date dtRemoval = list.get(0).getDtRemoval();

		return dtRemoval;
	}

	/**
	 * Method Name: getRmvlGroupsByStage Method Description:This method is
	 * used to fetch the removal checklist list.
	 * artf147748 add stageString param and idRmvlChcklstLink, dtCreated scalars
	 * 
	 * @param stageString
	 * @return rmvlChcklstValueDto
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<RmvlChcklstValueDto> getRmvlGroupsByStage(String stageString) {

		List<Long> idStages = new ArrayList<>();
		if (!ObjectUtils.isEmpty(stageString)) {
			for (String idStage : stageString.split(",")) {
				// Warranty Defect Fix - Null Pointer Check - 10825
				if(!idStage.equalsIgnoreCase("null"))
				{
				idStages.add(Long.valueOf(idStage));
				}
			}
		} 
		SQLQuery query = (SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getRmvlGroupByStageSql)
				.addScalar("idRmvlChcklstLink", StandardBasicTypes.LONG)  
				.addScalar("idRmvlGroup", StandardBasicTypes.LONG)
				.addScalar("idRmvlChcklstLookUp", StandardBasicTypes.LONG)
				.addScalar("idStage", StandardBasicTypes.LONG)
				.addScalar("txtChildList", StandardBasicTypes.STRING)
				.addScalar("nmChklst", StandardBasicTypes.STRING)
				.addScalar("cdRmvlChcklstStatus", StandardBasicTypes.STRING)
				.addScalar("txtPurps", StandardBasicTypes.STRING)
				.addScalar("txtInstrctns", StandardBasicTypes.STRING)
				.addScalar("txtNote", StandardBasicTypes.STRING)
				.addScalar("cdStageReasonClosed", StandardBasicTypes.STRING)
				.addScalar("dtCreated", StandardBasicTypes.DATE)  
				.addScalar("dtRemoval", StandardBasicTypes.DATE)
				.setParameterList("stageString", idStages)
				.setResultTransformer(Transformers.aliasToBean(RmvlChcklstValueDto.class));
		List<RmvlChcklstValueDto> rmvlChcklstValueDto = query.list();
		return rmvlChcklstValueDto;

	}

	/**
	 * Method Name: getRmvlSctnTaskByStage Method Description:This method is
	 * used to fetch the removal checklist list.
	 * artf147748 add stageString param and idRmvlChcklstLink scalar
	 * 
	 * @param stageString
	 * @return rmvlChcklstSctnTasklist
	 */
	@SuppressWarnings("unchecked")
	public List<RmvlChcklstSctnTaskValueDto> getRmvlSctnTaskByStage(String stageString) {

        // artf147748 account for removal checklist data model change after june 2020 cpi project
		Boolean isAfterCreldateJun2020 = Boolean.FALSE;
		String jun2020Dt = null;
		Date creldateJun2020 = null;
		try {
			jun2020Dt = lookupDao.simpleDecode(ServiceConstants.CRELDATE, ServiceConstants.CRELDATE_JUN_2020);
			creldateJun2020 = DateUtils.toJavaDateFromInput(jun2020Dt);
		} catch (Exception e) {
			new ServiceLayerException(e.getMessage());
		}
		
		List<Long> idStages = new ArrayList<>();
		if (!ObjectUtils.isEmpty(stageString)) {
			for (String idStage : stageString.split(",")) {
				// Warranty Defect Fix - Null Pointer Check - 10825
				if(!idStage.equalsIgnoreCase("null"))
				{
				idStages.add(Long.valueOf(idStage));
				}
			}
		}		
		SQLQuery getRmvlSctnTaskByStagequery = (SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getRmvlSctnTaskByStageSql)
				.addScalar("idRmvlGroup", StandardBasicTypes.LONG)
				.addScalar("idRmvlChcklstLink", StandardBasicTypes.LONG) 
				.addScalar("idStage", StandardBasicTypes.LONG)
				.addScalar("idRmvlChcklstSctnLookUp", StandardBasicTypes.LONG)
				.addScalar("idRmvlChcklstLookup", StandardBasicTypes.LONG)
				.addScalar("nbrSectnOrder", StandardBasicTypes.LONG)
				.addScalar("dtCreated", StandardBasicTypes.DATE)
				.addScalar("idRmvlChcklstTaskLookup", StandardBasicTypes.LONG)
				.addScalar("indHeader", StandardBasicTypes.STRING)
				.addScalar("idRmvlChcklstTaskGroup", StandardBasicTypes.LONG)
				.addScalar("nbrTaskOrder", StandardBasicTypes.LONG)
				.addScalar("txtDesc", StandardBasicTypes.STRING)
				.addScalar("cdTrigger", StandardBasicTypes.STRING)
				.addScalar("nbrTriggerValue", StandardBasicTypes.LONG)
				.addScalar("cdTriggerInterval", StandardBasicTypes.STRING)
				.addScalar("indTaskDltd", StandardBasicTypes.STRING)
				.addScalar("dtEnd", StandardBasicTypes.DATE)
				.setParameterList("stageString", idStages)
				.setResultTransformer(Transformers.aliasToBean(RmvlChcklstSctnTaskValueDto.class));
		List<RmvlChcklstSctnTaskValueDto> rmvlChcklstSctnTasklist = getRmvlSctnTaskByStagequery.list();

		SQLQuery getRmvlRspnsByStagequery = (SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getRmvlRspnsByStageSql)
				.addScalar("idRmvlGroup", StandardBasicTypes.LONG)
				.addScalar("idRmvlChcklstLink", StandardBasicTypes.LONG)
				.addScalar("idVictim", StandardBasicTypes.LONG)
				.addScalar("idRmvlChcklstRspns", StandardBasicTypes.LONG)
				.addScalar("idRmvlChcklstTaskLookup", StandardBasicTypes.LONG)
				.addScalar("txtRspns", StandardBasicTypes.STRING)
				.setParameterList("stageString", idStages)
				.setResultTransformer(Transformers.aliasToBean(RmvlChckListRspnDto.class));
		List<RmvlChckListRspnDto> rmvlChckListRspnlist = getRmvlRspnsByStagequery.list();

		for (RmvlChcklstSctnTaskValueDto rmvlChcklstSctnTaskValueDto : rmvlChcklstSctnTasklist) {
			isAfterCreldateJun2020 = DateUtils.isAfter(rmvlChcklstSctnTaskValueDto.getDtCreated(), creldateJun2020);
			rmvlChcklstSctnTaskValueDto.setIsAfterCreldateJun2020(isAfterCreldateJun2020);

			for (RmvlChckListRspnDto rmvlChckListRspnDto : rmvlChckListRspnlist) {
				// artf147748 first include check IdRmvlChcklstLink ...
				if ( ( rmvlChcklstSctnTaskValueDto.getIsAfterCreldateJun2020() && null != rmvlChcklstSctnTaskValueDto.getIdRmvlChcklstLink()
						&& rmvlChcklstSctnTaskValueDto.getIdRmvlChcklstLink().equals(rmvlChckListRspnDto.getIdRmvlChcklstLink())
						&& rmvlChcklstSctnTaskValueDto.getIdRmvlGroup().equals(rmvlChckListRspnDto.getIdRmvlGroup())
						&& rmvlChcklstSctnTaskValueDto.getIdRmvlChcklstTaskLookup().equals(rmvlChckListRspnDto.getIdRmvlChcklstTaskLookup()))
					|| // end artf147748 ... or as before ...
					 ( ! rmvlChcklstSctnTaskValueDto.getIsAfterCreldateJun2020()
						&& rmvlChcklstSctnTaskValueDto.getIdRmvlGroup().equals(rmvlChckListRspnDto.getIdRmvlGroup())
						&& rmvlChcklstSctnTaskValueDto.getIdRmvlChcklstTaskLookup().equals(rmvlChckListRspnDto.getIdRmvlChcklstTaskLookup()))) {
					rmvlChcklstSctnTaskValueDto.setTxtRspn(rmvlChckListRspnDto.getTxtRspns());
				}
			}
		}

		return rmvlChcklstSctnTasklist;

	}

}
