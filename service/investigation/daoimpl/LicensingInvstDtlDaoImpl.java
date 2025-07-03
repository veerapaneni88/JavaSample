package us.tx.state.dfps.service.investigation.daoimpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.*;
import us.tx.state.dfps.service.admin.dto.EmpJobHisDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.NameChangeReq;
import us.tx.state.dfps.service.investigation.dao.LicensingInvstDtlDao;
import us.tx.state.dfps.service.investigation.dto.ClassFacilityDto;
import us.tx.state.dfps.service.investigation.dto.InvstRestraintDto;
import us.tx.state.dfps.service.investigation.dto.LicensingInvstDtlDto;
import us.tx.state.dfps.service.investigation.dto.AllegedSxVctmztnDto;
import us.tx.state.dfps.service.resource.dto.ResourceValueBeanDto;
import us.tx.state.dfps.service.workload.dto.TodoDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<This class
 * is used to get informaion from database> Mar 27, 2018- 3:05:39 PM © 2017
 * Texas Department of Family and Protective Services.
 * * *************** Change History ****************************************
 * 11/1/2019  kanakas artf129782: Licensing Investigation Conclusion
 * 06/09/2020 kanakas artf152402 : Prior investigation overwritten by later 
 * 
 */
@Repository
public class LicensingInvstDtlDaoImpl implements LicensingInvstDtlDao {

	/** The get stage alleg question Y. */
	@Value("${LicensingInvstDtlDaoImpl.getCountForCpsInvCnclsn}")
	private String getStageAllegQuestionY;

	/** The get stage alleg question. */
	@Value("${LicensingInvstDtlDaoImpl.getStageAllegQuestion}")
	private String getStageAllegQuestion;

	/** The get class facility view. */
	@Value("${LicensingInvstDtlDaoImpl.getClassFacilityView}")
	private String getClassFacilityView;

	/** The get class facility view where agency. */
	@Value("${LicensingInvstDtlDaoImpl.getClassFacilityViewWhereAgency}")
	private String getClassFacilityViewWhereAgency;

	/** The get class facility view where branch. */
	@Value("${LicensingInvstDtlDaoImpl.getClassFacilityViewWhereBranch}")
	private String getClassFacilityViewWhereBranch;

	/** The get class facility view branch null. */
	@Value("${LicensingInvstDtlDaoImpl.getClassFacilityViewBranchNull}")
	private String getClassFacilityViewBranchNull;

	/** The get class facility view branch null. */
	@Value("${LicensingInvstDtlDaoImpl.getApprovalInfo}")
	private String getApprovalInfo;

	@Value("${LicensingInvstDtlDaoImpl.deleteInvCnlsnRstrntByStage}")
	private String deleteInvCnlsnRstrntByStageHql;

	@Value("${LicensingInvstDtlDaoImpl.getOverallDispositionExists}")
	private String getOverallDispositionExistsSql;

	@Value("${LicensingInvstDtlDaoImpl.updateCaseNameAndCountySql}")
	private String updateCaseNameAndCountySql;

	@Value("${LicensingInvstDtlDaoImpl.updateStageNameAndCountySql}")
	private String updateStageNameAndCountySql;

	@Value("${LicensingInvstDtlDaoImpl.updateSafetyPlanFacilityIdSql}")
	private String updateSafetyPlanFacilityIdSql;

	@Value("${LicensingInvstDtlDaoImpl.getCVSNotificationAlert}")
	private String getCVSNotificationAlert;

	/** The session factory. */
	@Autowired
	private SessionFactory sessionFactory;

	/**
	 * Instantiates a new licensing invst dtl dao impl.
	 */
	public LicensingInvstDtlDaoImpl() {

	}

	/**
	 * This method is used to get list of LicensingInvstDtl entities based on
	 * stageId
	 */

	@SuppressWarnings("unchecked")
	@Override
	public List<LicensingInvstDtl> getLicensingInvstDtlDaobyParentId(Long uIdStage) {

		List<LicensingInvstDtl> licensingInvstDtlList = new ArrayList<>();
		Criteria crApsInvstDetail = sessionFactory.getCurrentSession().createCriteria(LicensingInvstDtl.class)
				.add(Restrictions.eq("stage.idStage", uIdStage));
		licensingInvstDtlList = (List<LicensingInvstDtl>) crApsInvstDetail.list();
		return licensingInvstDtlList;
	}

	/**
	 * This DAM will add, update, or delete a full record from the
	 * LICENSING_INVST_DTL table.
	 * 
	 * Service Name: CCMN03U, DAM Name: CINV53D
	 *
	 * @param licensingInvstDtl
	 *            the licensing invst dtl @
	 */
	@Override
	public void saveLicensingInvstDtl(LicensingInvstDtl licensingInvstDtl) {

		sessionFactory.getCurrentSession().saveOrUpdate(licensingInvstDtl);

	}

	/**
	 * This DAM will add, update, or delete a full record from the
	 * LICENSING_INVST_DTL table.
	 * 
	 * Service Name: CCMN03U, DAM Name: CINV53D
	 *
	 * @param licensingInvstDtl
	 *            the licensing invst dtl @
	 */
	@Override
	public void licensingInvstDtlDelete(LicensingInvstDtl licensingInvstDtl) {
		sessionFactory.getCurrentSession().delete(licensingInvstDtl);
	}

	/**
	 * This service is used to check whether questions in allegation were
	 * answered as Y.
	 *
	 * @param idStage
	 *            the id stage
	 * @return true, if successful
	 */
	@Override
	public boolean fetchAllegQuestionYAnswers(Long idStage) {
		long count = 0;
		Query query = sessionFactory.getCurrentSession().createQuery(getStageAllegQuestionY).setParameter("idStage",
				idStage);
		count = (long) query.uniqueResult();
		if (count > 0) {
			return true;
		}
		return false;
	}

	/**
	 * This service is used to check whether questions in allegation were
	 * answered.
	 *
	 * @param idStage
	 *            the id stage
	 * @return true, if successful
	 */
	@Override
	public boolean fetchAllegQuestionAnswers(Long idStage) {
		long count = 0;
		Query query = sessionFactory.getCurrentSession().createQuery(getStageAllegQuestion).setParameter("idStage",
				idStage);
		count = (long) query.uniqueResult();
		if (count > 0) {
			return true;
		}
		return false;
	}

	/**
	 * This method is used to update or add or delete InvstConclusionRestraint
	 * entity
	 */
	@Override
	public void invstConclusionRestraintUpdate(InvstRestraintDto invstRestraintDto) {
		InvstConclusionRestraint invstConclusionRestraint = new InvstConclusionRestraint();
		invstConclusionRestraint.setCdRstraint(invstRestraintDto.getCdRstraint());
		invstConclusionRestraint.setIdStage(invstRestraintDto.getIdStage());
		switch (invstRestraintDto.getReqFuncCd()) {
		case ServiceConstants.REQ_FUNC_CD_ADD:
			invstConclusionRestraint.setDtLastUpdate(new Date());
			sessionFactory.getCurrentSession().save(invstConclusionRestraint);
			break;
		case ServiceConstants.REQ_FUNC_CD_UPDATE:
			sessionFactory.getCurrentSession().saveOrUpdate(invstConclusionRestraint);
			break;
		case ServiceConstants.REQ_FUNC_CD_DELETE:
			sessionFactory.getCurrentSession().createQuery(deleteInvCnlsnRstrntByStageHql)
					.setParameter("idStage", invstRestraintDto.getIdStage()).executeUpdate();
			break;
		}
	}

	/**
	 * This method is used to get Information from CLASS_FACILITY_VIEW@class
	 * table
	 */
	@Override
	public List<ClassFacilityDto> getClassFacilityView(Integer nbrRsrcFacilAcclaim, Integer nbrAgency,
			Integer nbrBranch, String indAgencyHome) {
		StringBuilder query = new StringBuilder();
		query.append(getClassFacilityView);

		if (!ObjectUtils.isEmpty(nbrAgency)) {
			query.append(getClassFacilityViewWhereAgency);
		}
		if (!ObjectUtils.isEmpty(nbrBranch)) {
			query.append(getClassFacilityViewWhereBranch);
		} else if (!"A".equals(indAgencyHome)) {
			query.append(getClassFacilityViewBranchNull);
		}
		SQLQuery sqlQuery = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(query.toString())
				.setParameter("nbrFacility", nbrRsrcFacilAcclaim))
						.addScalar("idClassFacility", StandardBasicTypes.INTEGER)
						.addScalar("nmFacility", StandardBasicTypes.STRING)
						.addScalar("nbrFacility", StandardBasicTypes.INTEGER)
						.addScalar("nbrAgency", StandardBasicTypes.INTEGER)
						.addScalar("nbrBranch", StandardBasicTypes.SHORT)
						.addScalar("descFacilityType", StandardBasicTypes.STRING)
						.addScalar("cdClassFacilType", StandardBasicTypes.STRING);
		if (!ObjectUtils.isEmpty(nbrAgency)) {
			sqlQuery.setParameter("nbrAgency", nbrAgency);
		}
		if (!ObjectUtils.isEmpty(nbrBranch)) {
			sqlQuery.setParameter("nbrBranch", nbrBranch);
		}
		List<ClassFacilityDto> classFacilityDtoList = (List<ClassFacilityDto>) sqlQuery
				.setResultTransformer(Transformers.aliasToBean(ClassFacilityDto.class)).list();
		return classFacilityDtoList;
	}

	/**
	 * Gets the resouce information from CapsResource based on facility number.
	 */
	@Override
	public ResourceValueBeanDto getResourceByFacilityNbr(Long nbrRsrcFacilAcclaim) {
		ResourceValueBeanDto resourceValueBeanDto = new ResourceValueBeanDto();
		Criteria resourceDetails = sessionFactory.getCurrentSession().createCriteria(CapsResource.class)
				.add(Restrictions.eq("nbrRsrcFacilAcclaim", nbrRsrcFacilAcclaim));
		CapsResource capsResource = (CapsResource) resourceDetails.uniqueResult();
		if (!ObjectUtils.isEmpty(capsResource)) {
			BeanUtils.copyProperties(capsResource, resourceValueBeanDto);
		}
		return resourceValueBeanDto;
	}

	/**
	 * This Method is used to get the information related to approval date and
	 * approval reason.
	 * 
	 * @param idStage
	 * @param idFacil
	 */
	@SuppressWarnings("unchecked")
	@Override
	public LicensingInvstDtlDto getApprovalInfo(Long idStage, Integer idFacil) {
		List<LicensingInvstDtlDto> licensingInvstDtlList = ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getApprovalInfo).setParameter("idStage", idStage).setParameter("idFacil", idFacil))
						.addScalar("dtLicngInvstExtAprvl", StandardBasicTypes.DATE)
						.addScalar("txtExtnAprvlRsn", StandardBasicTypes.STRING)
						.setResultTransformer(Transformers.aliasToBean(LicensingInvstDtlDto.class)).list();
		if (!licensingInvstDtlList.isEmpty()) {
			return licensingInvstDtlList.get(0);
		} else {
			return new LicensingInvstDtlDto();
		}
	}

	/**
	 * 
	 *Method Name:	getLicensingInvDtlFromEvent
	 *Method Description: this method updates the licensing investigation completed date
	 *@param idEvent
	 */
	@Override
	public void saveLicensingInvDtlBasedOnDtComplt(Long idEvent, Date dtComplted){
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(LicensingInvstDtl.class)
				.add(Restrictions.eq("idEvent", idEvent));
		LicensingInvstDtl licensingInvstDtl = (LicensingInvstDtl) criteria.uniqueResult();
		licensingInvstDtl.setDtLicngInvstComplt(dtComplted);
		saveLicensingInvstDtl(licensingInvstDtl);
	}
	/**
	 * artf129782: Licensing Investigation Conclusion
	 * updated the code for artf152403 : Columns not populating on ALLEGED_SX_VCT
	 * Method Name: saveAllegedBehaviorDetails
	 */
	@Override
	public void saveAllegedBehaviorDetails(AllegedSxVctmztn allegedSxVctmztn, AllegedSxVctmztnDto savedAllegedSxVctmztnDto){
		if(savedAllegedSxVctmztnDto != null && savedAllegedSxVctmztnDto.getIdStage() == null){
			sessionFactory.getCurrentSession().save(allegedSxVctmztn);
		}
		else{
			 //updated the code so that the update will not remove the created person and updated person and even dt_created
		        AllegedSxVctmztn allegedSxVctmztnEntity = (AllegedSxVctmztn) sessionFactory.getCurrentSession().load(AllegedSxVctmztn.class,
		        allegedSxVctmztn.getIdAllegedSxVctmztn());
		        allegedSxVctmztnEntity.setIdLastUpdatePerson(allegedSxVctmztn.getIdLastUpdatePerson());
	            allegedSxVctmztnEntity.setIndAllegedHumanTrafficking(allegedSxVctmztn.getIndAllegedHumanTrafficking());
	            allegedSxVctmztnEntity.setIndAllegedSxAggression(allegedSxVctmztn.getIndAllegedSxAggression());
	            allegedSxVctmztnEntity.setIndAllegedSxBehaviorProblem(allegedSxVctmztn.getIndAllegedSxBehaviorProblem());
	            allegedSxVctmztnEntity.setIndAllegedVctmCsa(allegedSxVctmztn.getIndAllegedVctmCsa());
			sessionFactory.getCurrentSession().saveOrUpdate(allegedSxVctmztnEntity);
		}
		
	}
	
	/**
	 * Method Name: getOverallDispositionExists
	 * Method Description: This method is used to query the database and see if a stage has an overall disposition
	 * present.
	 * artf128755 - CCI reporter letter
	 *
	 * @param idStage stage to be searched
	 * @return true or false depending on presence of overall disposition.
	 */
	@Override
	public boolean getOverallDispositionExists(Long idStage) {
		int indOverallDispPresent = 0;
		Query query = ((SQLQuery)sessionFactory.getCurrentSession().createSQLQuery(getOverallDispositionExistsSql)
				.setParameter("idStage", 	idStage))
				.addScalar("IND_OVERALL_DISP_PRESENT", StandardBasicTypes.INTEGER);
		// There is apparently a Hibernate bug, since uniqueResult causes NPR if no rows are returned. We
		// work around the bug by using list().
		List<Integer> personIdList = query.list();
		if (!ObjectUtils.isEmpty(personIdList)) {
			indOverallDispPresent = personIdList.get(0);
		}
		if (indOverallDispPresent > 0) {
			return true;
		}
		return false;
	}

	/**
	 * Method Name:	updateCaseName
	 * Method Description:this method updates the case name in CAPS_CASE table
	 * @param nameChangeReq
	 * @param county
	 */
	@Override
	public void updateCaseNameAndCounty(NameChangeReq nameChangeReq, String county){
		SQLQuery updateNameQuery = (SQLQuery)sessionFactory.getCurrentSession().createSQLQuery(updateCaseNameAndCountySql);
		updateNameQuery.setParameter("idCase", nameChangeReq.getCaseId());
		updateNameQuery.setParameter("nmCase", nameChangeReq.getUpdatedResourceName());
		updateNameQuery.setParameter("county", county);
		updateNameQuery.executeUpdate();
	}

	/**
	 * Method Name:	updateStageName
	 * Method Description:this method updates the stage name in STAGE table
	 * @param nameChangeReq
	 * @param county
	 */
	@Override
	public void updateStageNameAndCounty(NameChangeReq nameChangeReq, String county){
		SQLQuery updateNameQuery = (SQLQuery)sessionFactory.getCurrentSession().createSQLQuery(updateStageNameAndCountySql);
		updateNameQuery.setParameter("idCase", nameChangeReq.getCaseId());
		updateNameQuery.setParameter("nmStage", nameChangeReq.getUpdatedResourceName());
		updateNameQuery.setParameter("county", county);
		updateNameQuery.executeUpdate();
	}

	/**
	 * Method Name:	updateSafetyPlanFacilityId
	 * Method Description:this method updates the ID_FCLTY_SEL column to null
	 * if the CD_SAFETY_PLAN_STATUS is "In process"(INP) in SAFETY_PLAN table
	 * @param status
	 * @param idStage
	 */
	@Override
	public void updateSafetyPlanFacilityId(Long idStage, String status){
		SQLQuery updateQuery = (SQLQuery)sessionFactory.getCurrentSession().createSQLQuery(updateSafetyPlanFacilityIdSql);
		updateQuery.setParameter("safetyPlanStatus", status);
		updateQuery.setParameter("idStage", idStage);
		updateQuery.executeUpdate();
	}

	/**
	 * Method Name:	saveCvsNotificationLog
	 * Method Description: Save data to CvsNotifLog
	 * @param cvsNotifLog
	 */
	@Override
	public Long saveCvsNotificationLog(CvsNotifLog cvsNotifLog) {
		return (Long)sessionFactory.getCurrentSession().save(cvsNotifLog);
	}

	/**
	 * Method Name:	deleteCvsNotificationLog
	 * Method Description: Delete CvsNotifLog
	 * @param cvsNotifLog
	 */
	@Override
	public void deleteCvsNotificationLog(CvsNotifLog cvsNotifLog) {
		sessionFactory.getCurrentSession().delete(cvsNotifLog);
	}


	/**
	 * This method is used to get list of CvsNotifLog
	 * @param idCase
	 * @param idVictimPerson
	 * @param cdEmailType
	 */
	@Override
	public List<CvsNotifLog> getCVSNotificationLog(Long idCase, Long idVictimPerson, String cdEmailType) {
		List<CvsNotifLog> cvsNotifLogList = new ArrayList<CvsNotifLog>();
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CvsNotifLog.class)
				.add(Restrictions.eq("idCase", idCase))
				.add(Restrictions.eq("idVictimPerson", idVictimPerson))
				.add(Restrictions.eq("cdEmailType", cdEmailType));
		cvsNotifLogList = (List<CvsNotifLog>) criteria.list();
		return cvsNotifLogList;
	}

	/**
	 * Method Name: getCVSNotificationAlert
	 * Method Description: This method is used to get CVS Notification alert for a person
	 *
	 * @param idCase
	 * @param idStage
	 * @param idPerson
	 */
	@Override
	public List<TodoDto> getCVSNotificationAlert(Long idCase, Long idStage, Long idPerson) {
		List<TodoDto> todoDtoList  =  (ArrayList<TodoDto>)((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getCVSNotificationAlert)
				.setParameter("idCase", idCase)
				.setParameter("idStage", idStage)
				.setParameter("idPerson", idPerson))
				.addScalar("idTodo", StandardBasicTypes.LONG)
				.addScalar("dtTodoCreated", StandardBasicTypes.TIMESTAMP)
				.setResultTransformer(Transformers.aliasToBean(TodoDto.class)).list();
		return todoDtoList;
	}
}
