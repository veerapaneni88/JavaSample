package us.tx.state.dfps.service.workload.daoimpl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.PriorityTracking;
import us.tx.state.dfps.common.domain.SsccApiAddressValidation;
import us.tx.state.dfps.common.dto.StagePersonValueDto;
import us.tx.state.dfps.common.web.bean.AddressDetailBean;
import us.tx.state.dfps.service.admin.dto.WorkLoadDto;
import us.tx.state.dfps.service.casepackage.dto.ARPendingStagesDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.PriorityClosureSaveReq;
import us.tx.state.dfps.service.common.response.CommonHelperRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.workload.dao.WorkLoadDao;
import us.tx.state.dfps.service.workload.dto.AssignedDto;
import us.tx.state.dfps.service.workload.dto.PriorityHistoryDto;

@Repository
public class WorkLoadDaoImpl implements WorkLoadDao {

	@Autowired
	private MessageSource messageSource;

	@Value("${WorkloadDao.getWrkLoadPersonId}")
	String getWrkLoadPersonIdSql;

	@Value("${WorkloadDao.getWrkLoadPersonIdsByRole}")
	String getWrkLoadPersonIdsByRoleSql;

	@Value("${WorkloadDao.getInvCnclPendingStages}")
	private String getInvCnclPendingStages;

	@Value("${WorkloadDao.getCheckedOutStages}")
	private String getCheckedOutStages;

	@Value("${WorkloadDao.getStagesWithARasPriorStage}")
	private String getStagesWithARasPriorStage;

	@Value("${WorkloadDao.getARPendingStages}")
	private String getARPendingStages;

	@Value("${WorkloadDao.getARExtensionRequest}")
	private String getARExtensionRequest;

	@Value("${PriorityClosureEjb.getIdEventPersonInfo}")
	private String getIdEventPersonInfoSql;

	@Value("${WorkloadDao.cdMobileStatusSql}")
	private String cdMobileStatusSql;

	@Value("${WorkloadDao.roleWorkloadStageSql}")
	private String roleWorkloadStageSql;

	@Value("${WorkloadDao.getActiveStagesForPersonSql}")
	private String getActiveStagesForPersonSql;

	@Value("${WorkloadDao.disableTletsCheckSql}")
	private String disableTletsCheckSql;

	@Value("${WorkloadDao.contactPurposeStatusSql}")
	private String contactPurposeStatusSql;

	@Value("${WorkloadDao.contactApprovedSql}")
	private String contactApprovedSql;

	@Value("${WorkloadDao.checkoutPersonStatusSql}")
	private String checkoutPersonStatusSql;

	@Value("${WorkloadDao.getCheckedOutStagesForPerson}")
	private String getCheckedOutStagesForPerson;

	@Value("${WorkloadDao.getOpenStageListForPerson}")
	private String getOpenStageListForPerson;

	@Value("${WorkloadDao.getPriorityTracking}")
	private String getPriorityTracking;

	@Value("${WorkloadDao.getApproverList}")
	private String getApproverList;

	@Value("${WorkloadDao.getAssignedWorkersSQL}")
	private String getAssignedWorkers;
	
	@Value("${WorkloadDao.getStagePersRoleSql}")
	private String getStagePersRole;

	@Value("${WorkloadDao.hasAppEventExistsBeforeFBSSRef}")
	private String hasAppEventExistsBeforeFBSSRef;

	@Value("${WorkloadDao.getCaseAssignedToPerson}")
	private String getCaseAssignedToPerson;

	@Value("${WorkloadDao.getOpenRCLINVStagesForPerson}")
	private String getOpenRCLINVStagesForPerson;

	@Value("${WorkloadDao.workloadHasLoginUserForStageAndCase}")
	private String getWorkloadHasLoginUserForStageAndCase;

	@Value("${WorkloadDao.findEMRProgramAdminSql}")
	private String findEMRProgramAdminSql;

	@Value("${WorkloadDao.getApprovalPersonSecurityEMRSql}")
	private String getApprovalPersonSecurityEMRSql;

	@Value("${WorkloadDao.getValidatedAddressApi}")
	private String getValidatedAddressApi;

	@Autowired
	private SessionFactory sessionFactory;

	public static final Logger log = Logger.getLogger(WorkLoadDaoImpl.class);

	public WorkLoadDaoImpl() {

	}

	/**
	 * 
	 * Method Description: This method will retrieve the ID PERSON for a given
	 * role, for a given stage. It's used to find the primary worker for a given
	 * stage. Dam Name: CINV51D
	 * 
	 * @param idStage
	 * @param cdStgPersRole
	 * @return Long @
	 */
	@Override
	public Long getPersonIdByRole(Long idStage, String cdStgPersRole) {
		Long idPerson = null;
		String sql = cdStgPersRole.equals(ServiceConstants.PRIMARY_ROLE)
				|| cdStgPersRole.equals(ServiceConstants.SECONDARY_ROLE) ? getWrkLoadPersonIdSql
						: getWrkLoadPersonIdsByRoleSql;
		List<BigDecimal> idPersonList = new ArrayList<BigDecimal>();
		Query query = sessionFactory.getCurrentSession().createSQLQuery(sql);
		query.setParameter("idStage", idStage);
		query.setParameter("cdStagePersRole", cdStgPersRole);
		idPersonList = (List<BigDecimal>) query.list();
		if (ObjectUtils.isEmpty(idPersonList)) {
			idPerson = ServiceConstants.ZERO;
		}else if(!ObjectUtils.isEmpty(idPersonList) && 0 < idPersonList.size()){
			idPerson = idPersonList.get(0).longValue();
		}
		return idPerson;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Long> getPersonIdsByRole(Long idStage, String cdStagePersRole) {

		List<Long> idPersonList = null;
		SQLQuery sqlQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getWrkLoadPersonIdsByRoleSql)
				.setParameter("idStage", idStage).setParameter("cdStagePersRole", cdStagePersRole);
		idPersonList = (List<Long>) sqlQuery.list();

		if (ObjectUtils.isEmpty(idPersonList) || idPersonList.size() < 1) {
			throw new DataNotFoundException(messageSource.getMessage("idPerson.not.found.stageId", null, Locale.US));
		}
		return idPersonList;
	}

	/**
	 * 
	 * Method Description: Returns CPS stageIds that has pending investigation
	 * conclusions. Return the CPS Investigation Stages that matches the
	 * following criteria. Current Date - Intake Start Date - Total Number of
	 * Days for All the Extension Requests >= 30
	 * 
	 * System needs to display the stage in red color on the workload page of
	 * Primary or Secondary Worker, if it has been 30 days since the Intake has
	 * been started and Investigation has not been completed. And if the
	 * Extension Request has been created and Approved, System waits until the
	 * number of Days in the Extension Request.
	 * 
	 * Tuxedo Service Name: NA Tuxedo DAM Name: NA
	 * 
	 * @param idWorker
	 *            - Worker ID
	 * @ @return List<Long>
	 */

	@SuppressWarnings("unchecked")
	@Override
	public List<Long> getInvCnclPendingStages(Long idWorker) {

		List<Long> invCnclPendingStages = new ArrayList<Long>();
		Query query = (Query) sessionFactory.getCurrentSession().createSQLQuery(getInvCnclPendingStages)
				.addScalar("STG", StandardBasicTypes.LONG).setParameter("idWkldPerson", idWorker);
		invCnclPendingStages = (List<Long>) query.list();

		return invCnclPendingStages;
	}

	/**
	 * 
	 * Method Description: Returns an array of stage ID's representing the
	 * subset of a passed array of stage ID's that are currently checked out to
	 * MPS
	 * 
	 * Tuxedo Service Name: NA Tuxedo DAM Name: NA
	 * 
	 * @param stageIds
	 *            - Stage IDs that have to be filtered
	 * @ @return List<Long>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Long> getCheckedOutStages(List<Long> stageIds) {
		List<Long> checkedOutStageIDs = new ArrayList<Long>();

		Query query = (Query) sessionFactory.getCurrentSession().createSQLQuery(getCheckedOutStages)
				.addScalar("ID_WKLD_STAGE", StandardBasicTypes.LONG).setParameterList("stageIDs", stageIds);
		checkedOutStageIDs = (List<Long>) query.list();

		return checkedOutStageIDs;
	}

	/**
	 * 
	 * Method Description: Optimize workload page query to improve page loading
	 * times. Returns the list of Stage Ids that has A-R as a prior stage
	 * 
	 * Tuxedo Service Name: NA Tuxedo DAM Name: NA
	 * 
	 * @param idWorker
	 *            - Worker ID
	 * @ @return Set<Long>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Long> getStagesWithARasPriorStage(Long idWorker) {

		List<Long> stagesWithARAsPriorStage = new ArrayList<Long>();
		Query query = (Query) sessionFactory.getCurrentSession().createSQLQuery(getStagesWithARasPriorStage)
				.addScalar("STG", StandardBasicTypes.LONG).setParameter("idWkldPerson", idWorker);
		stagesWithARAsPriorStage = (List<Long>) query.list();

		return stagesWithARAsPriorStage;
	}

	/**
	 * 
	 * Method Description: Returns list of AR stages that requires worker
	 * attention
	 * 
	 * Tuxedo Service Name: NA Tuxedo DAM Name: NA
	 * 
	 * @param idWorker
	 *            - Worker ID
	 * @ @return List<ARPendingStagesDto>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<ARPendingStagesDto> getARPendingStages(Long idWorker) {

		List<ARPendingStagesDto> arPendingStages = new ArrayList<ARPendingStagesDto>();
		Query query = (Query) sessionFactory.getCurrentSession().createSQLQuery(getARPendingStages)
				.addScalar("idWkldStage", StandardBasicTypes.LONG)
				.addScalar("dtStageCreated", StandardBasicTypes.TIMESTAMP)
				.addScalar("nmWkldStage", StandardBasicTypes.STRING).setParameter("idWkldPerson", idWorker)
				.setResultTransformer(Transformers.aliasToBean(ARPendingStagesDto.class));
		arPendingStages = (List<ARPendingStagesDto>) query.list();

		return arPendingStages;

	}

	/**
	 * 
	 * Method Description: Returns Extension request Event object for the given
	 * AR stage.
	 * 
	 * Tuxedo Service Name: NA Tuxedo DAM Name: NA
	 * 
	 * @param idStage
	 *            - Stage ID
	 * @ @return ARPendingStagesDto
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ARPendingStagesDto getARExtensionRequest(Long idStage) {

		List<ARPendingStagesDto> arExtensionReqStages = new ArrayList<ARPendingStagesDto>();
		ARPendingStagesDto arExtensionReqStatus = new ARPendingStagesDto();
		Query query = (Query) sessionFactory.getCurrentSession().createSQLQuery(getARExtensionRequest)
				.addScalar("cdEventStatus", StandardBasicTypes.STRING).addScalar("idEvent", StandardBasicTypes.LONG)
				.setParameter("idStage", idStage)
				.setResultTransformer(Transformers.aliasToBean(ARPendingStagesDto.class));
		arExtensionReqStages = (List<ARPendingStagesDto>) query.list();
		if (CollectionUtils.isNotEmpty(arExtensionReqStages)) {
			return arExtensionReqStages.get(0);
		}

		return arExtensionReqStatus;
	}

	/**
	 * 
	 * Method Description: This method is used to get data from DAO class and
	 * Map in set with key and value pair. Service Name:priorityClosureEjb DAM
	 * Name : EJB Service - priorityClosureDao
	 * 
	 * @ @return map
	 */

	@SuppressWarnings("unchecked")
	public Map<String, Object> getIdEventPersonInfo(Long idStage) {
		List<Map<String, Object>> priorityClosureEjb = null;
		Map<String, Object> map = null;

		Query query = (Query) sessionFactory.getCurrentSession().createSQLQuery(getIdEventPersonInfoSql)
				.addScalar("idEventPerson", StandardBasicTypes.LONG)
				.addScalar("cdEmployeeClass", StandardBasicTypes.STRING)
				.addScalar("nmPersonFull", StandardBasicTypes.STRING).addScalar("jobClass", StandardBasicTypes.STRING)
				.setParameter("idStage", idStage).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
		priorityClosureEjb = (List<Map<String, Object>>) query.list();
		if (!CollectionUtils.isEmpty(priorityClosureEjb))
			map = priorityClosureEjb.iterator().next();

		if (TypeConvUtil.isNullOrEmpty(map)) {
			map = null;
		}

		return map;

	}

	/**
	 * Method Description:Return the mobile status given the idWkldCase
	 * 
	 * @param idCase
	 *            in CommonHelperReq
	 * @return String @
	 */
	@Override
	public String getCdMobileStatus(Long idCase) {
		String cdMobileStatus = null;
		Query query = (Query) sessionFactory.getCurrentSession().createSQLQuery(cdMobileStatusSql)
				.addScalar("CD_MOBILE_STATUS", StandardBasicTypes.STRING).setParameter("idWkldCase", idCase);
		cdMobileStatus = (String) query.uniqueResult();
		return cdMobileStatus;
	}

	/**
	 * Method-Description:Returns the role of a person in a stage, provided the
	 * case is in that person's workload. If the person does not have a role, or
	 * the stage is no in their workload, will return empty string (""). Should
	 * always return PRIMARY ("PR") or SECONDARY ("SE").
	 *
	 * @param IdPerson
	 * @param IdStage
	 * @return the role of the person in that stage in their workload. @
	 */
	@Override
	public String getRoleInWorkloadStage(Long idStage, Long idPerson) {
		String personRole = null;
		Query query = (Query) sessionFactory.getCurrentSession().createSQLQuery(roleWorkloadStageSql)
				.addScalar("cdWkldStagePersRole", StandardBasicTypes.STRING).setParameter("idwkldStage", idStage)
				.setParameter("idwkldPerson", idPerson);
		personRole = (String) query.uniqueResult();
		return personRole;
	}

	/**
	 * Method-Description:This method will fetch all active stages for a person
	 * 
	 * @param PersonID
	 * @return List of all active Stage Id(s) @
	 */
	@Override
	public List<Long> getActiveStagesForPerson(Long idPerson) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getActiveStagesForPersonSql)
				.addScalar("idStage", StandardBasicTypes.LONG).setParameter("idPerson", idPerson);
		return query.list();
	}

	/**
	 * Method-Description:Check Configuration table ONLINE_PARAMETERS, if Tlets
	 * Check needs to be Enabled or Disabled
	 * 
	 * @return Boolean -- true or false @
	 */
	@Override
	public Boolean disableTletsCheck() {
		Boolean disableTletsCheck = Boolean.FALSE;
		Query query = sessionFactory.getCurrentSession().createSQLQuery(disableTletsCheckSql)
				.addScalar("value", StandardBasicTypes.STRING).setParameter("idtxtName", ServiceConstants.TLETS_CHECK);
		if (null != query.uniqueResult()
				&& query.uniqueResult().toString().equalsIgnoreCase(ServiceConstants.STRING_IND_Y)) {
			disableTletsCheck = Boolean.TRUE;
		}

		return disableTletsCheck;
	}

	/**
	 * Method Description: This method is to find if the contact with the
	 * purpose of initial already exist.
	 * 
	 * @paramidStage
	 * @returnBoolean -- true or False
	 */
	@Override
	public Boolean getContactPurposeStatus(Long idStage) {
		Boolean isContactPurposePresent = Boolean.FALSE;
		Query query = sessionFactory.getCurrentSession().createSQLQuery(contactPurposeStatusSql)
				.addScalar("recCount", StandardBasicTypes.INTEGER).setParameter("idStage", idStage)
				.setParameter("cdContactPurpose", ServiceConstants.CD_CONTACT_PURPOSE);
		if (null != query.uniqueResult() && Integer.parseInt(query.uniqueResult().toString()) > 0) {
			isContactPurposePresent = Boolean.TRUE;
		}
		return isContactPurposePresent;
	}

	@Override
	public Boolean isAprvContactInCase(Long idCase, String idContactType) {
		Boolean isContactApproved = Boolean.FALSE;
		Query query = sessionFactory.getCurrentSession().createSQLQuery(contactApprovedSql)
				.addScalar("cdEventStatus", StandardBasicTypes.STRING).setParameter("idCase", idCase)
				.setParameter("idContactType", idContactType);
		if(null == query.uniqueResult()){
			isContactApproved = Boolean.TRUE;
		}else if (query.uniqueResult().toString().isEmpty() == false
				&& query.uniqueResult().toString().equalsIgnoreCase(ServiceConstants.EVENTSTATUS_APPROVE) == true) {
			isContactApproved = Boolean.TRUE;
		}
		return isContactApproved;
	}

	@Override
	public Boolean getCheckedOutPersonStatus(Long idPerson, Long idPerson2) {
		Boolean ischeckoutPersonStatus = Boolean.FALSE;
		Query query = sessionFactory.getCurrentSession().createSQLQuery(checkoutPersonStatusSql)
				.addScalar("recCount", StandardBasicTypes.INTEGER).setParameter("idPerson", idPerson)
				.setParameter("idPerson2", idPerson2)
				.setParameter("cdMobileStatus", ServiceConstants.CHECKOUTSTATUS_OT);
		if (!TypeConvUtil.isNullOrEmpty(query.uniqueResult())
				&& Integer.parseInt(query.uniqueResult().toString()) > 0) {
			ischeckoutPersonStatus = Boolean.TRUE;
		}
		return ischeckoutPersonStatus;
	}

	@SuppressWarnings("unchecked")
	@Override
	public ArrayList<StagePersonValueDto> getCheckedOutStagesForPerson(Long idPerson) {
		ArrayList<StagePersonValueDto> result = null;
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getCheckedOutStagesForPerson)
				.addScalar("indSensitiveCase", StandardBasicTypes.STRING)
				.addScalar("idPrimaryWorker", StandardBasicTypes.LONG)
				.addScalar("nmPrimaryWorker", StandardBasicTypes.STRING).addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("cdStage", StandardBasicTypes.STRING).addScalar("idStage", StandardBasicTypes.LONG)
				.addScalar("idCase", StandardBasicTypes.LONG).addScalar("cdStageRegion", StandardBasicTypes.STRING)
				.addScalar("cdStageProgram", StandardBasicTypes.STRING)
				.addScalar("cdStagePersRole", StandardBasicTypes.STRING)
				.addScalar("cdStagePersType", StandardBasicTypes.STRING)
				.addScalar("cdStagePersRelLong", StandardBasicTypes.STRING)
				.addScalar("dtStageCheckout", StandardBasicTypes.TIMESTAMP).setParameter("idPerson", idPerson)
				.setResultTransformer(Transformers.aliasToBean(StagePersonValueDto.class));
		result = (ArrayList<StagePersonValueDto>) query.list();
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public ArrayList<StagePersonValueDto> getStagesForPerson(Long idPerson) {
		ArrayList<StagePersonValueDto> result = null;
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getOpenStageListForPerson)
				.addScalar("indSensitiveCase", StandardBasicTypes.STRING).addScalar("nmCase", StandardBasicTypes.STRING)
				.addScalar("nmStage", StandardBasicTypes.STRING).addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("cdStage", StandardBasicTypes.STRING).addScalar("idStage", StandardBasicTypes.LONG)
				.addScalar("idCase", StandardBasicTypes.LONG).addScalar("cdStageProgram", StandardBasicTypes.STRING)
				.addScalar("cdStagePersRole", StandardBasicTypes.STRING)
				.addScalar("cdStagePersType", StandardBasicTypes.STRING)
				.addScalar("cdStagePersRelLong", StandardBasicTypes.STRING)
				.addScalar("dtStageStart", StandardBasicTypes.TIMESTAMP).setParameter("idPerson", idPerson)
				.setResultTransformer(Transformers.aliasToBean(StagePersonValueDto.class));
		result = (ArrayList<StagePersonValueDto>) query.list();
		return result;
	}

	/**
	 * 
	 * Method Description: This method will retrieve the ID PERSON for a given
	 * role, for a given stage. It's used to find the primary worker for a given
	 * stage. Dam Name: CINV51D
	 * 
	 * @param idStage
	 * @param cdStgPersRole
	 * @return Long @
	 */
	@Override
	public Long getStagePersonIdByRole(Long idStage, String cdStgPersRole) {

		Long idPerson = ServiceConstants.ZERO_VAL;
		;
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getWrkLoadPersonIdsByRoleSql);
		query.setParameter("idStage", idStage);
		query.setParameter("cdStagePersRole", cdStgPersRole);
		if (!ObjectUtils.isEmpty(query.uniqueResult()))
			idPerson = ((BigDecimal) query.uniqueResult()).longValue();
		return idPerson;
	}

	/**
	 * Method Name:getPriorityTracking Method Description: This method will
	 * retrieve the Priority Tracking Details for the particular stageId
	 * 
	 * @param idStage
	 * @return List @
	 */
	@Override
	public List<PriorityHistoryDto> getPriorityTracking(Long idStage) {
		List<PriorityHistoryDto> priorityHistoryDtoList = null;
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getPriorityTracking)
				.addScalar("idPriorityTracking", StandardBasicTypes.LONG).addScalar("idStage", StandardBasicTypes.LONG)
				.addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("cdStageCurrPriority", StandardBasicTypes.STRING)
				.addScalar("cdStageInitialPriority", StandardBasicTypes.STRING)
				.addScalar("cdReasonChanged", StandardBasicTypes.STRING).addScalar("dtStart", StandardBasicTypes.DATE)
				.addScalar("dtEnd", StandardBasicTypes.DATE).addScalar("dtCreated", StandardBasicTypes.DATE)
				.addScalar("idCreatedPerson", StandardBasicTypes.LONG)
				.addScalar("dtLastUpadte", StandardBasicTypes.TIMESTAMP)
				.addScalar("idLastUpdatePerson", StandardBasicTypes.LONG).addScalar("dtPurge", StandardBasicTypes.DATE)
				.addScalar("nmPersonFull", StandardBasicTypes.STRING).setParameter("idStage", idStage)
				.setResultTransformer(Transformers.aliasToBean(PriorityHistoryDto.class));
		priorityHistoryDtoList = query.list();
		return priorityHistoryDtoList;

	}

	/**
	 * Method Name:savePriorityTracking Method Description: This method will
	 * save the Priority Tracking Details
	 * 
	 * @param idStage
	 *
	 */
	public void savePriorityTracking(PriorityClosureSaveReq priorityClosureSaveReq) {
		PriorityTracking priorityTracking = new PriorityTracking();
		priorityTracking.setIdPerson(priorityClosureSaveReq.getPriorityClosureDto().getIdPerson());
		priorityTracking.setIdCreatedPerson(priorityClosureSaveReq.getPriorityClosureDto().getIdPerson());
		priorityTracking.setIdLastUpdatePerson(priorityClosureSaveReq.getPriorityClosureDto().getIdPerson());
		priorityTracking.setDtLastUpadte(new Date());
		priorityTracking.setDtStart(priorityClosureSaveReq.getPriorityClosureDto().getDtStageStart());
		priorityTracking.setDtEnd(priorityClosureSaveReq.getPriorityClosureDto().getDtStageClose());
		priorityTracking.setDtCreated(priorityClosureSaveReq.getPriorityClosureDto().getDtEventOccurred());
		priorityTracking.setIdStage(priorityClosureSaveReq.getPriorityClosureDto().getIdStage());
		priorityTracking.setCdReasonChanged(priorityClosureSaveReq.getPriorityClosureDto().getCdStageRsnPriorityChgd());
		priorityTracking
				.setCdStageCurrPriority(priorityClosureSaveReq.getPriorityClosureDto().getCdStageCurrPriority());
		priorityTracking
				.setCdStageInitialPriority(priorityClosureSaveReq.getPriorityClosureDto().getCdStageInitialPriority());
		sessionFactory.getCurrentSession().save(priorityTracking);

	}

	@SuppressWarnings("unchecked")
	@Override
	public TreeMap<Long, String> getLatestChildPlanEvent(Long idCase, String cdStage) {
		TreeMap<Long, String> idEventMap = new TreeMap<Long, String>();
		List<Long> idEventFPList = null;
		List<Long> idEventFPEList = null;
		List<Long> idEventFSUList = null;
		List<Long> idEventFREList = null;
		if (ServiceConstants.CSTAGES_FPR.equals(cdStage)) {
			Query queryFP = sessionFactory.getCurrentSession().createSQLQuery(getApproverList)
					.addScalar("idEvent", StandardBasicTypes.LONG).setParameter("idCase", idCase)
					.setParameter("cdTask", ServiceConstants.FAMILY_PRES_TASK);
			idEventFPList = (List<Long>) queryFP.list();
			if (!ObjectUtils.isEmpty(idEventFPList)) {
				idEventMap.put(idEventFPList.get(0), cdStage);
			}
			Query queryFPE = sessionFactory.getCurrentSession().createSQLQuery(getApproverList)
					.addScalar("idEvent", StandardBasicTypes.LONG).setParameter("idCase", idCase)
					.setParameter("cdTask", ServiceConstants.CD_TASK_FPR_FAM_PLAN_EVAL);
			idEventFPEList = (List<Long>) queryFPE.list();
			if (!ObjectUtils.isEmpty(idEventFPEList)) {
				idEventMap.put(idEventFPEList.get(0), cdStage);
			}
		} else if (ServiceConstants.CSTAGES_FSU.equals(cdStage)) {
			Query queryFSU = sessionFactory.getCurrentSession().createSQLQuery(getApproverList)
					.addScalar("idEvent", StandardBasicTypes.LONG).setParameter("idCase", idCase)
					.setParameter("cdTask", ServiceConstants.FAMILY_PLAN_TASK_FSU);
			idEventFSUList = (List<Long>) queryFSU.list();
			if (!ObjectUtils.isEmpty(idEventFSUList)) {
				idEventMap.put(idEventFSUList.get(0), cdStage);
			}
		} else if (ServiceConstants.CSTAGES_FRE.equals(cdStage)) {
			Query queryFRE = sessionFactory.getCurrentSession().createSQLQuery(getApproverList)
					.addScalar("idEvent", StandardBasicTypes.LONG).setParameter("idCase", idCase)
					.setParameter("cdTask", ServiceConstants.FAMILY_PLAN_TASK_FRE);
			idEventFREList = (List<Long>) queryFRE.list();
			if (!ObjectUtils.isEmpty(idEventFREList)) {
				idEventMap.put(idEventFREList.get(0), cdStage);
			}
		}
		return idEventMap;
	}

	/**
	 * Method Name: getAssignedWorkersForStage Method Description: Fetch the
	 * Primary and Secondary Workers assigned to the passed Stage ID
	 * 
	 * @param idStage
	 * @return List<Long>
	 */
	public List<Long> getAssignedWorkersForStage(Long idStage) {
		List<Long> idAssignedWorkers;
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getAssignedWorkers).addScalar("idWkldPerson",
				StandardBasicTypes.LONG);
		query.setParameter("idStage", idStage);
		idAssignedWorkers = query.list();
		if (ObjectUtils.isEmpty(idAssignedWorkers)) {
			idAssignedWorkers = new ArrayList<>();
		}
		return idAssignedWorkers;
	}
	
	
	public String getStagePersRole(Long idStage, Long idPerson) {
		List<String> roles;
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getStagePersRole)
				.addScalar("cdWkldStagePersRole",
				StandardBasicTypes.STRING);
		query.setParameter("idStage", idStage);
		query.setParameter("idPerson", idPerson);//WorkLoadDto
		roles = query.list();
		
		return roles.get(0);
	}

	/**
	 * Artifact ID: artf151569
	 * Method Name: hasAppEventExistsBeforeFBSSRef
	 * Method Description: This method is used to check whether the current stage approval has been created before the
	 * FPR Release, and there is no approval FBSS Referral event
	 *
	 * @param idApproval
	 * @return
	 */
	@Override
	public Boolean hasAppEventExistsBeforeFBSSRef(Long idApproval) {

		Boolean stageExists = Boolean.FALSE;

		Query query = sessionFactory.getCurrentSession().createSQLQuery(hasAppEventExistsBeforeFBSSRef)
				.addScalar("stageExists", StandardBasicTypes.INTEGER)
				.setParameter("idApproval", idApproval);

		if (null != query.uniqueResult() && Integer.parseInt(query.uniqueResult().toString()) > 0) {
			stageExists = Boolean.TRUE;
		}

		return stageExists;
	}

	/**
	 * Artifact ID: artf140443
	 *Method Name:	isCaseAssignedToPerson
	 *Method Description:checks if a case is assigned to the case worker
	 *@param idPerson
	 *@param idCase
	 *@return
	 */
	@Override
	public boolean isCaseAssignedToPerson(Long idPerson, Long idCase) {
		Query query  =  sessionFactory.getCurrentSession().createSQLQuery(getCaseAssignedToPerson)
				.setParameter("idPerson", idPerson).setParameter("idCase", idCase);
		List<Long> stageIds = (List<Long>) query.list();
		return stageIds != null && !stageIds.isEmpty();
	}

	/**
	 * Retreives the open RCL INV stages where the idPerson is a victim
	 * @param idPerson
	 * @return
	 */
	public List<StagePersonValueDto> getOpenRCLINVStagesForPerson(Long idPerson){
		List<StagePersonValueDto> openStages = null;
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getOpenRCLINVStagesForPerson)
				.addScalar("indSensitiveCase", StandardBasicTypes.STRING).addScalar("nmCase", StandardBasicTypes.STRING)
				.addScalar("nmStage", StandardBasicTypes.STRING).addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("cdStage", StandardBasicTypes.STRING).addScalar("idStage", StandardBasicTypes.LONG)
				.addScalar("idCase", StandardBasicTypes.LONG).addScalar("cdStageProgram", StandardBasicTypes.STRING)
				.addScalar("cdStagePersRole", StandardBasicTypes.STRING)
				.addScalar("cdStagePersType", StandardBasicTypes.STRING)
				.addScalar("cdStagePersRelLong", StandardBasicTypes.STRING)
				.addScalar("dtStageStart", StandardBasicTypes.TIMESTAMP).setParameter("idPerson", idPerson)
				.setResultTransformer(Transformers.aliasToBean(StagePersonValueDto.class));
		openStages = (ArrayList<StagePersonValueDto>) query.list();
		return openStages;
	}

	@Override
	public boolean getWorkloadHasLoginUserForStageAndCase(Long stageId, Long caseId, Long loginUserId) {
		Query countQuery = sessionFactory.getCurrentSession().createSQLQuery(getWorkloadHasLoginUserForStageAndCase)
				.addScalar("count", StandardBasicTypes.INTEGER)
				.setParameter("stageId", stageId)
				.setParameter("caseId", caseId)
				.setParameter("loginUserId", loginUserId);

		Integer workloadCount = (Integer) countQuery.uniqueResult();
		if (!TypeConvUtil.isNullOrEmpty(workloadCount)) {
			return workloadCount.intValue() > 0;
		}
		return false;
	}

	/**
	 * Dao Impleation method form the query with parameters data and get the results from DB
	 *
	 * @param idUser - logged in user
	 * @param securityRole - security role
	 * @return - returns Assigned dto data
	 */
	@Override
	public AssignedDto findEMRProgramAdminByUseridAndSecurityRole(int idUser, String securityRole) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(findEMRProgramAdminSql)
				.addScalar("nmPersonFull", StandardBasicTypes.STRING)
				.addScalar("idTodoPersAssigned", StandardBasicTypes.LONG)
				.setParameter("idUser", idUser)
				.setParameter("securityRole", securityRole)
				.setResultTransformer(Transformers.aliasToBean(AssignedDto.class));
		List<AssignedDto> assignedDtoList = query.list();
		return !ObjectUtils.isEmpty(assignedDtoList) ? assignedDtoList.get(0) : new AssignedDto();
	}

	@Override
	public boolean getExecStaffSecurityForEMR(Long idPerson, String securityRole) {
		Boolean secExists  = Boolean.FALSE;
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getApprovalPersonSecurityEMRSql)
				.addScalar("sec_exists", StandardBasicTypes.INTEGER)
				.setParameter("idPerson", idPerson)
				.setParameter("securityRole", securityRole);
		if (null != query.uniqueResult() && Integer.parseInt(query.uniqueResult().toString()) > 0) {
			secExists  = Boolean.TRUE;
		}
		return secExists ;
	}

	@Override
	public boolean insertValidatedAddressApi(AddressDetailBean addressDetailBean) {
		SsccApiAddressValidation ssccApiAddressValidation = new SsccApiAddressValidation();
		ssccApiAddressValidation.setIdStage(addressDetailBean.getIdStage());
		ssccApiAddressValidation.setAddrStreetLn1(addressDetailBean.getStreet1());
		ssccApiAddressValidation.setAddrStreetLn2(addressDetailBean.getStreet2());
		ssccApiAddressValidation.setAddrCity(addressDetailBean.getCity());
		ssccApiAddressValidation.setAddrZip(addressDetailBean.getZip());
		ssccApiAddressValidation.setCdAddrState(addressDetailBean.getCdState());
		ssccApiAddressValidation.setCdAddrCounty(addressDetailBean.getCdCounty());
		ssccApiAddressValidation.setNmCnty(addressDetailBean.getCounty());
		ssccApiAddressValidation.setNmCountry(addressDetailBean.getCountry());
		ssccApiAddressValidation.setNbrGcdLat(addressDetailBean.getGcdLat());
		ssccApiAddressValidation.setNbrGcdLong(addressDetailBean.getGcdLong());
		ssccApiAddressValidation.setCdAddrRtrn(addressDetailBean.getCdAddrRtrn());
		ssccApiAddressValidation.setCdGcdRtrn(addressDetailBean.getCdGcdRtrn());
		ssccApiAddressValidation.setIndValdtd(addressDetailBean.getIndValdtd());
		ssccApiAddressValidation.setDtValdtd(addressDetailBean.getDtValdtd());
		ssccApiAddressValidation.setTxtMailbityScore(addressDetailBean.getMailabilityScore());
		ssccApiAddressValidation.setTxtAddressGuid(addressDetailBean.getGuid());
		ssccApiAddressValidation.setTsTTL(addressDetailBean.getTsTTL());
		ssccApiAddressValidation.setIdCreatedPerson(addressDetailBean.getIdPerson());
		ssccApiAddressValidation.setIdLastUpdatePerson(addressDetailBean.getIdPerson());

		sessionFactory.getCurrentSession().save(ssccApiAddressValidation);
		return true;
	}

	@Override
	public AddressDetailBean getValidatedAddressApi(String guid){
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getValidatedAddressApi)
				.addScalar("idAddress", StandardBasicTypes.LONG)
				.addScalar("idStage", StandardBasicTypes.LONG)
				.addScalar("street1", StandardBasicTypes.STRING)
				.addScalar("street2", StandardBasicTypes.STRING)
				.addScalar("city", StandardBasicTypes.STRING)
				.addScalar("zip", StandardBasicTypes.STRING)
				.addScalar("cdState", StandardBasicTypes.STRING)
				.addScalar("cdCounty", StandardBasicTypes.STRING)
				.addScalar("county", StandardBasicTypes.STRING)
				.addScalar("country", StandardBasicTypes.STRING)
				.addScalar("gcdLat", StandardBasicTypes.BIG_DECIMAL)
				.addScalar("gcdLong", StandardBasicTypes.BIG_DECIMAL)
				.addScalar("cdAddrRtrn", StandardBasicTypes.STRING)
				.addScalar("cdGcdRtrn", StandardBasicTypes.STRING)
				.addScalar("indValdtd", StandardBasicTypes.STRING)
				.addScalar("dtValdtd", StandardBasicTypes.DATE)
				.addScalar("mailabilityScore", StandardBasicTypes.STRING)
				.addScalar("tsTTL", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtCreated", StandardBasicTypes.TIMESTAMP)
				.addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("dtLastUpdate", StandardBasicTypes.TIMESTAMP)
				.addScalar("idLastUpdatePerson", StandardBasicTypes.LONG)
//				.addScalar("dtPurged", StandardBasicTypes.DATE)
				.setParameter("guid", guid)
				.setResultTransformer(Transformers.aliasToBean(AddressDetailBean.class));
		AddressDetailBean addressDetailBean;
		addressDetailBean = (AddressDetailBean) query.uniqueResult();
		if(!ObjectUtils.isEmpty(addressDetailBean)) {
			addressDetailBean.setGuid(guid);
			if (!ObjectUtils.isEmpty(addressDetailBean.getZip()) && addressDetailBean.getZip().contains("-")) {
				String[] zipExt = addressDetailBean.getZip().split("-");
				if (zipExt.length == 2) {
					addressDetailBean.setZip(zipExt[0]);
					addressDetailBean.setExtension(zipExt[1]);
				}
			}
		} else {
			addressDetailBean = new AddressDetailBean();
			addressDetailBean.setErrorMessage("Unable to find the validated address for GUID: "+guid);
		}
		return addressDetailBean;
	}
}