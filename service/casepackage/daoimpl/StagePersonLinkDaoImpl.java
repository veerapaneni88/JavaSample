package us.tx.state.dfps.service.casepackage.daoimpl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;
import us.tx.state.dfps.common.domain.StagePersonLink;
import us.tx.state.dfps.common.dto.ServiceReqHeaderDto;
import us.tx.state.dfps.service.casepackage.dao.StagePersonLinkDao;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.CaseFileMgtReq;
import us.tx.state.dfps.service.common.request.CvsFaHomeReq;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataLayerException;
import us.tx.state.dfps.service.exception.StgPersonLinlDtlsNotFoundException;
import us.tx.state.dfps.service.investigation.dto.AllegationDetailDto;
import us.tx.state.dfps.service.investigation.dto.AllegtnPrsnDto;
import us.tx.state.dfps.service.subcare.dto.StgPersonLinkDto;
import us.tx.state.dfps.service.workload.dto.StageIdDto;
import us.tx.state.dfps.service.workload.dto.StagePersDto;
import us.tx.state.dfps.service.workload.dto.StagePersonLinkDto;
import us.tx.state.dfps.service.workload.dto.StagePrincipalDto;
import us.tx.state.dfps.service.workload.dto.StageResourceDto;

/**
 * 
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name:CCFC21S Class
 * Description: This Method implements OfficeDao. This is used to retrieve count
 * of case worker based on PR Role from database. Mar 24, 2017 - 7:30:30 PM
 */
@Repository
public class StagePersonLinkDaoImpl implements StagePersonLinkDao {

	@Value("${SearchStagePersonLinkDaoImpl.searchStageIdList}")
	private String searchStageIdList;

	@Value("${StagePersonLinkDao.getStgPersLinkPersonId}")
	String getStgPersLinkPersonIdSql;

	@Value("${StagePersonLinkDaoImpl.getStagePersonLinkId}")
	String getStagePersonLinkId;

	@Value("${StagePersonLinkDaoImpl.GetKin}")
	private String getKinshipSql;

	@Value("${StagePersonLinkDaoImpl.deletePRSEStagePersonLinkByIdStage}")
	private String deletePRSEStagePersonLinkByIdStageSql;

	@Value("${StagePersonLinkDaoImpl.deleteTodoForClosingStage}")
	private String deleteTodoForClosingStageSql;

	@Value("${StagePersonLinkDaoImpl.getStagePrincipalByIdStageType}")
	private String getStagePrincipalByIdStageTypeSql;

	@Value("${StagePersonLinkDaoImpl.getIsKin}")
	private String getIsKinSql;

	@Value("${StagePersonLinkDaoImpl.getIsAlreadyPrimaryKin}")
	private String getIsAlreadyKinSql;

	@Value("${StagePersonLinkDaoImpl.getStagePersonLinkByIdStage}")
	private String getStagePersonLinkByIdStageSql;

	@Value("${StagePersonLinkDaoImpl.getStagePrsnLinkByStageId}")
	private String getStagePrsnLinkByStageId;

	@Value("${StagePersonLinkDaoImpl.getStageResourceForChild}")
	private String getStageResourceForChildSql;

	@Value("${StagePersonLinkDaoImpl.getPrimaryChildIdByIdStage}")
	private String getPrimaryChildIdByIdStageSql;

	@Value("${StagePersonLinkDaoImpl.getIdADOStageByIdSUBStage}")
	private String getIdADOStageByIdSUBStageSql;

	@Value("${StagePersonLinkDaoImpl.getIdSUBStageByIdADOStage}")
	private String getIdSUBStageByIdADOStageSql;

	@Value("${StagePersonLinkDaoImpl.getStageListByIdPerson}")
	private String getStageListByIdPersonSql;

	@Value("${StagePersonLinkDaoImpl.getPersonLegalStatus}")
	private String getPersonLegalStatusSql;

	@Value("${StagePersonLinkDaoImpl.getStgPersonLinkDtlsBasedOnCase}")
	private String getStgPersonLinkDtlsBasedOnCaseSql;

	@Autowired
	private SessionFactory sessionFactory;

	public StagePersonLinkDaoImpl() {

	}

	private static final Logger log = Logger.getLogger(StagePersonLinkDaoImpl.class);

	/**
	 * 
	 * Method Description: This Method will will retrieve the COUNT from the
	 * stage table where the ID PERSON entered along with the ID CASE match an
	 * ID PERSON (primary worker) in one of the stages in the case DAM:CMSC36D
	 * Service: CCFC21S
	 * 
	 * @param ulIdCase
	 * @param ulIdPerson
	 * @return @
	 */
	public Long getPrimaryCaseWorker(CaseFileMgtReq caseFileMgtReq) {
		Long count = 0l;
		Criteria cr = sessionFactory.getCurrentSession().createCriteria(StagePersonLink.class)
				.setProjection(Projections.rowCount());
		cr.add(Restrictions.eq("idPerson", caseFileMgtReq.getIdPerson()));
		cr.add(Restrictions.eq("idCase", caseFileMgtReq.getIdCase()));
		cr.add(Restrictions.eq("cdStagePersRole", ServiceConstants.CD_STAGE_PERS_ROLE_SERVICE_PROVIDER));
		count = (Long) cr.uniqueResult();
		log.info("TransactionId :" + caseFileMgtReq.getTransactionId());
		return count;
	}

	/**
	 * 
	 * Method Description: This Method is used to perform AUD operation based on
	 * stagePersonLinkDto Service Name: CCMN25S DAM Name : CCMN80D, CCMND3D
	 * 
	 * @param serviceReqHeaderDto
	 * @param stagePersonLinkDto
	 * @return LastUpdateDateDto @
	 */
	@Override
	public String getStagePersonLinkAUD(StagePersonLinkDto stagePersonLinkDto,
			ServiceReqHeaderDto serviceReqHeaderDto) {
		String retMsg = "";
		StagePersonLink stagePerLinkEntity = new StagePersonLink();
		Date date = new Date();
		if (serviceReqHeaderDto.getReqFuncCd().equalsIgnoreCase(ServiceConstants.REQ_IND_AUD_ADD)
				|| serviceReqHeaderDto.getReqFuncCd().equalsIgnoreCase(ServiceConstants.REQ_FUNC_CD_ADD)) {
			stagePerLinkEntity.setDtLastUpdate(date);
			if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toString(stagePersonLinkDto.getCdStagePersType())))
				stagePerLinkEntity.setCdStagePersType(stagePersonLinkDto.getCdStagePersType());
			if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toString(stagePersonLinkDto.getCdStagePersSearchInd())))
				stagePerLinkEntity.setCdStagePersSearchInd(stagePersonLinkDto.getCdStagePersSearchInd());
			if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toString(stagePersonLinkDto.getStagePersNotes())))
				stagePerLinkEntity.setTxtStagePersNotes(stagePersonLinkDto.getStagePersNotes());
			if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toString(stagePersonLinkDto.getIndStagePersReporter())))
				stagePerLinkEntity.setIndStagePersReporter(stagePersonLinkDto.getIndStagePersReporter());
			if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toString(stagePersonLinkDto.getIndStagePersInLaw())))
				stagePerLinkEntity.setIndStagePersInLaw(stagePersonLinkDto.getIndStagePersInLaw());
			if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toLong(stagePersonLinkDto.getIndStagePersEmpNew())))
				stagePerLinkEntity.setIndStagePersEmpNew(stagePersonLinkDto.getIndStagePersEmpNew());
			if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toLong(stagePersonLinkDto.getIdStage())))
				stagePerLinkEntity.setIdStage(stagePersonLinkDto.getIdStage());
			if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toLong(stagePersonLinkDto.getIdPerson())))
				stagePerLinkEntity.setIdPerson(stagePersonLinkDto.getIdPerson());
			if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toString(stagePersonLinkDto.getIdCase())))
				stagePerLinkEntity.setIdCase(stagePersonLinkDto.getIdCase());
			/*
			 * if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toLong(
			 * stagePersonLinkDto.getIdStagePersonLink())))
			 * stagePerLinkEntity.setIdStagePersonLink(stagePersonLinkDto.
			 * getIdStagePersonLink());
			 */
			if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toString(stagePersonLinkDto.getCdStagePersRole())))
				stagePerLinkEntity.setCdStagePersRole(stagePersonLinkDto.getCdStagePersRole());
			if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toString(stagePersonLinkDto.getDtStagePersLink())))
				stagePerLinkEntity.setDtStagePersLink(stagePersonLinkDto.getDtStagePersLink());
			if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toString(stagePersonLinkDto.getCdStagePersRelInt())))
				stagePerLinkEntity.setCdStagePersRelInt(stagePersonLinkDto.getCdStagePersRelInt());
			if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toString(stagePersonLinkDto.getIndNmStage()))) {
				// Defect#14155 setting both true/false if not null.
					stagePerLinkEntity.setIndNmStage(stagePersonLinkDto.getIndNmStage());
			}
			sessionFactory.getCurrentSession().save(stagePerLinkEntity);
			retMsg = ServiceConstants.SUCCESS;
		} else if (serviceReqHeaderDto.getReqFuncCd().equalsIgnoreCase(ServiceConstants.REQ_IND_AUD_UPDATE)
				|| serviceReqHeaderDto.getReqFuncCd().equalsIgnoreCase(ServiceConstants.REQ_FUNC_CD_UPDATE)) {
			stagePerLinkEntity.setDtLastUpdate(date);
			if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toString(stagePersonLinkDto.getCdStagePersType())))
				stagePerLinkEntity.setCdStagePersType(stagePersonLinkDto.getCdStagePersType());
			if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toString(stagePersonLinkDto.getCdStagePersSearchInd())))
				stagePerLinkEntity.setCdStagePersSearchInd(stagePersonLinkDto.getCdStagePersSearchInd());
			if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toString(stagePersonLinkDto.getStagePersNotes())))
				stagePerLinkEntity.setTxtStagePersNotes(stagePersonLinkDto.getStagePersNotes());
			if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toString(stagePersonLinkDto.getIndStagePersReporter())))
				stagePerLinkEntity.setIndStagePersReporter(stagePersonLinkDto.getIndStagePersReporter());
			if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toString(stagePersonLinkDto.getIndStagePersInLaw())))
				stagePerLinkEntity.setIndStagePersInLaw(stagePersonLinkDto.getIndStagePersInLaw());
			if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toLong(stagePersonLinkDto.getIndStagePersEmpNew())))
				stagePerLinkEntity.setIndStagePersEmpNew(stagePersonLinkDto.getIndStagePersEmpNew());
			if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toLong(stagePersonLinkDto.getIdStage())))
				stagePerLinkEntity.setIdStage(stagePersonLinkDto.getIdStage());
			if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toLong(stagePersonLinkDto.getIdPerson())))
				stagePerLinkEntity.setIdPerson(stagePersonLinkDto.getIdPerson());
			if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toLong(stagePersonLinkDto.getIdStagePersonLink())))
				stagePerLinkEntity.setIdStagePersonLink(stagePersonLinkDto.getIdStagePersonLink());
			if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toString(stagePersonLinkDto.getCdStagePersRole())))
				stagePerLinkEntity.setCdStagePersRole(stagePersonLinkDto.getCdStagePersRole());
			if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toString(stagePersonLinkDto.getDtStagePersLink())))
				stagePerLinkEntity.setDtStagePersLink(stagePersonLinkDto.getDtStagePersLink());
			if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toString(stagePersonLinkDto.getCdStagePersRelInt())))
				stagePerLinkEntity.setCdStagePersRelInt(stagePersonLinkDto.getCdStagePersRelInt());
			if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toString(stagePersonLinkDto.getIdCase())))
				stagePerLinkEntity.setIdCase(stagePersonLinkDto.getIdCase());
			// sessionFactory.getCurrentSession().saveOrUpdate(sessionFactory.getCurrentSession().merge(stagePerLinkEntity));
			sessionFactory.getCurrentSession().merge(stagePerLinkEntity);
			retMsg = ServiceConstants.SUCCESS;
		} else if (serviceReqHeaderDto.getReqFuncCd().equalsIgnoreCase(ServiceConstants.REQ_IND_AUD_DELETE)
				|| serviceReqHeaderDto.getReqFuncCd().equalsIgnoreCase(ServiceConstants.REQ_FUNC_CD_DELETE)) {
			Criteria cr = sessionFactory.getCurrentSession().createCriteria(StagePersonLink.class)
					.add(Restrictions.eq("idStagePersonLink", stagePersonLinkDto.getIdStagePersonLink()));
			stagePerLinkEntity = (StagePersonLink) cr.uniqueResult();
			if (stagePerLinkEntity == null) {
				throw new StgPersonLinlDtlsNotFoundException(stagePersonLinkDto.getIdStagePersonLink());
			}
			sessionFactory.getCurrentSession().delete(stagePerLinkEntity);
			/*
			 * Query queryDelTodo = session.createQuery(
			 * "from StagePersonLink where idStagePersonLink = :idStgperLink" );
			 * queryDelTodo.setParameter("approversID",
			 * stagePersonLinkDto.getIdStagePersonLink()); stagePerLinkEntity =
			 * (StagePersonLink) queryDelTodo.uniqueResult();
			 * session.delete(stagePerLinkEntity); tx.commit();
			 */
			retMsg = ServiceConstants.SUCCESS;
		} else {
			throw new DataLayerException(ServiceConstants.NOAUDDOP);
		}
		return retMsg;
	}

	@SuppressWarnings("unchecked")
	public List<StageIdDto> getStageIdList(Long idPerson) {
		List<StageIdDto> stageOutput = new ArrayList<StageIdDto>();
		Query queryStageId = sessionFactory.getCurrentSession().createSQLQuery(searchStageIdList)
				.addScalar("idStage", StandardBasicTypes.LONG)
				.setResultTransformer(Transformers.aliasToBean(StageIdDto.class));
		queryStageId.setParameter("idPerson", idPerson);
		stageOutput = queryStageId.list();
		return stageOutput;
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
	public Long getPersonIdByRole(Long idStage, String cdStagePersRole) {
		Long idPerson = null;
		Query query = sessionFactory.getCurrentSession().createQuery(getStgPersLinkPersonIdSql);
		query.setParameter("idStage", idStage);
		query.setParameter("cdStagePersRole", cdStagePersRole);
		idPerson = (Long) query.uniqueResult();
		return idPerson;
	}

	/**
	 * 
	 * Method Description: This Method Changes New Assignement Indicator from
	 * True to false. Dam Name: CCMN52D Service Name: CCMN14S
	 * 
	 * @param idPerson
	 * @param ulIdStage
	 * @return List<StageDto> @
	 */
	public String StgPrsnLinkUpdt(List<Long> ulIdStage, Long ulIdPerson) {
		StagePersonLink stagePersonLinkEntity = new StagePersonLink();
		String returnMsg = "";
		for (int i = 0; i < ulIdStage.size(); i++) {
			Criteria cr = sessionFactory.getCurrentSession().createCriteria(StagePersonLink.class)
					.add(Restrictions.eq("idStage", ulIdStage.get(i))).add(Restrictions.eq("idPerson", ulIdPerson));
			stagePersonLinkEntity = (StagePersonLink) cr.uniqueResult();
			if (!TypeConvUtil.isNullOrEmpty(TypeConvUtil.toLong(ulIdStage.get(i))))
				stagePersonLinkEntity.setIndStagePersEmpNew(ServiceConstants.IND_STAGE_PERS_EMP_NEW_NO);
			sessionFactory.getCurrentSession()
					.saveOrUpdate(sessionFactory.getCurrentSession().merge(stagePersonLinkEntity));
			returnMsg = ServiceConstants.SUCCESS;
		}
		return returnMsg;
	}

	/**
	 * 
	 * Method Description: Method to retrieve Kinship details to populate the
	 * CVS Home window. EJB - CVS FA HOME
	 * 
	 * @param cvsFaHomeReq
	 * @return cvsFaHomeRes @
	 */
	@Override
	public StgPersonLinkDto getStagePersonLinkDetails(Long uidPerson, Long uidStage) {
		StgPersonLinkDto stgPersonLinkDtls = null;

		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(StagePersonLink.class)
				.add(Restrictions.eq("idStage", uidStage))
				.add(Restrictions.eq("idPerson", uidPerson));
		StagePersonLink stagePersonLink = (StagePersonLink) criteria.uniqueResult();
		if (null != stagePersonLink) {
			stgPersonLinkDtls = new StgPersonLinkDto();
			BeanUtils.copyProperties(stagePersonLink, stgPersonLinkDtls);
		}

		return stgPersonLinkDtls;
	}

	/**
	 * Make sure to delete all therecords in the STAGE PERSON LINK table where
	 * ID STAGE equals the Input.ID STAGE and the CD_STAGE_PERS_ROLE = 'PR' or
	 * 'SE'
	 * 
	 * @param idStage
	 * @
	 */
	@Override
	public void deletePRSEStagePersonLinkByIdStage(Long idStage) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(deletePRSEStagePersonLinkByIdStageSql)
				.setParameter("idStage", idStage).setParameter("secondaryRole", ServiceConstants.STAGE_PERS_ROLE_SE);
		query.executeUpdate();
	}

	/**
	 * 
	 * Method Description: Method to update Kinship details and populate the CVS
	 * Home window. EJB - CVS FA HOME
	 * 
	 * @param cvsFaHomeReq
	 * @return cvsFaHomeRes @
	 */
	@Override
	public StgPersonLinkDto updateStagePersonLinkDetails(StagePersonLink stagePersonLink) {
		StgPersonLinkDto stagePersonLinkDto = new StgPersonLinkDto();
		if (TypeConvUtil.isNullOrEmpty(stagePersonLink.getIdStagePersonLink()))
			stagePersonLink.setIdStagePersonLink(
					getStagePersonLinkId(stagePersonLink.getIdStage(), stagePersonLink.getIdPerson()));
		if (!ObjectUtils.isEmpty(stagePersonLink.getIdStagePersonLink())) {
			sessionFactory.getCurrentSession().saveOrUpdate(sessionFactory.getCurrentSession().merge(stagePersonLink));
			stagePersonLinkDto = getStagePersonLinkDetails(stagePersonLink.getIdPerson(), stagePersonLink.getIdStage());
		} else {
			sessionFactory.getCurrentSession().save(stagePersonLink);
			BeanUtils.copyProperties(stagePersonLink, stagePersonLinkDto);
		}
		return stagePersonLinkDto;
	}

	/**
	 * 
	 * Method Description: Method to retrieve StagePersonLinkID details t
	 * 
	 * @param cvsFaHomeReq
	 * @return stagePersonLinkId @
	 */
	@Override
	public Long getStagePersonLinkId(Long uidStage, Long uidPerson) {
		Long stagePersonLinkId = 0l;
		stagePersonLinkId = (Long) ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getStagePersonLinkId)
				.setParameter("idPerson", uidPerson).setParameter("idStage", uidStage))
						.addScalar("idStagePersonLink", StandardBasicTypes.LONG).uniqueResult();
		return stagePersonLinkId;
	}

	/**
	 * This DAM is used by CloseOpenStage (Stage Progression) to perform DELETE
	 * functionality on the TODO table. Given ID STAGE, delete all
	 * system-generated (where person created is null), non-completed (where
	 * date completed is null) todos which are not tied to a monthly contact
	 * event (contact type = 'CMST' and 'C3MT'). The timestamp is not used.
	 * There is no ADD or UPDATE functionality. Service Name: CCMN03U, Dam Name-
	 * CCMNH1D
	 * 
	 * @param idStage
	 * @
	 */
	@Override
	public void deleteTodoForClosingStage(Long idStage) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(deleteTodoForClosingStageSql)
				.setParameter("idStage", idStage).setParameter("cmst", ServiceConstants.CONTACT_TYPE_CMST)
				.setParameter("c3mt", ServiceConstants.CONTACT_TYPE_C3MT)
				.setParameter("cs45", ServiceConstants.CONTACT_TYPE_CS45)
				.setParameter("cs60", ServiceConstants.CONTACT_TYPE_CS60);
		query.executeUpdate();
	}

	/**
	 * This dam rtrieves all principals linked to stage along with their county,
	 * region, name, stage role, & stage relation
	 * 
	 * 
	 * Service Name: CCMN03U, Dam Name- CLSC18D
	 * 
	 * @param idStage
	 * @param stageType
	 * @return @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<StagePrincipalDto> getStagePrincipalByIdStageType(Long idStage, String stageType) {
		List<StagePrincipalDto> stagePrincipalDtoList = new ArrayList<StagePrincipalDto>();
		Query queryStage = sessionFactory.getCurrentSession().createSQLQuery(getStagePrincipalByIdStageTypeSql)
				.addScalar("idStagePersonLink", StandardBasicTypes.LONG)
				.addScalar("dtLastUpdate", StandardBasicTypes.TIMESTAMP).addScalar("idStage", StandardBasicTypes.LONG)
				.addScalar("idPerson", StandardBasicTypes.LONG).addScalar("cdStagePersRole", StandardBasicTypes.STRING)
				.addScalar("indStagePersInLaw", StandardBasicTypes.STRING)
				.addScalar("cdStagePersType", StandardBasicTypes.STRING)
				.addScalar("cdStagePersSearchInd", StandardBasicTypes.STRING)
				.addScalar("stagePersNotes", StandardBasicTypes.STRING)
				.addScalar("dtStagePersLink", StandardBasicTypes.TIMESTAMP)
				.addScalar("cdStagePersRelInt", StandardBasicTypes.STRING)
				.addScalar("indStagePersReporter", StandardBasicTypes.STRING)
				.addScalar("indStagePersEmpNew", StandardBasicTypes.STRING)
				.addScalar("cdPersonSex", StandardBasicTypes.STRING)
				.addScalar("nmPersonFull", StandardBasicTypes.STRING)
				.addScalar("indPersCancelHist", StandardBasicTypes.STRING)
				.addScalar("personAge", StandardBasicTypes.INTEGER)
				.addScalar("dtPersonDeath", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtPersonBirth", StandardBasicTypes.TIMESTAMP)
				.addScalar("cdPersonReligion", StandardBasicTypes.STRING)
				.addScalar("cdPersonChar", StandardBasicTypes.STRING)
				.addScalar("indPersonDobApprox", StandardBasicTypes.STRING)
				.addScalar("cdPersonLivArr", StandardBasicTypes.STRING)
				.addScalar("cdPersGuardCnsrv", StandardBasicTypes.STRING)
				.addScalar("cdPersonStatus", StandardBasicTypes.STRING)
				.addScalar("cdPersonDeath", StandardBasicTypes.STRING)
				.addScalar("cdPersonMaritalStatus", StandardBasicTypes.STRING)
				.addScalar("personOccupation", StandardBasicTypes.STRING)
				.addScalar("cdPersonLanguage", StandardBasicTypes.STRING)
				.addScalar("cdPersonEthnicGroup", StandardBasicTypes.STRING)
				.addScalar("nmPersonFirst", StandardBasicTypes.STRING)
				.addScalar("nmPersonLast", StandardBasicTypes.STRING)
				.addScalar("cdPersonSuffix", StandardBasicTypes.STRING)
				.addScalar("cdDeathRsnCps", StandardBasicTypes.STRING)
				.setResultTransformer(Transformers.aliasToBean(StagePrincipalDto.class));
		queryStage.setParameter("idStage", idStage);
		queryStage.setParameter("cdType", stageType);
		stagePrincipalDtoList = queryStage.list();
		return stagePrincipalDtoList;
	}

	/**
	 * 
	 * Method Description: Method to check if already a Kin for CVS Home window.
	 * EJB - CVS FA HOME
	 * 
	 * @param cvsFaHomeReq
	 * @return isKin @
	 */
	public String getIsKin(CvsFaHomeReq cvsFaHomeReq) {
		String isKin = ServiceConstants.FALSE_VAL;
		Long idStagePersonLink = null;
		idStagePersonLink = (Long) ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getIsKinSql)
				.setParameter("idPerson", cvsFaHomeReq.getPersonDtlDto().getIdPerson())
				.setParameter("idStage", cvsFaHomeReq.getStagePersonLinkDto().getIdStage()))
						.addScalar("idStagePersonLink", StandardBasicTypes.LONG).uniqueResult();
		if (!TypeConvUtil.isNullOrEmpty(idStagePersonLink)) {
			isKin = ServiceConstants.TRUE_VAL;
		}
		return isKin;
	}

	/**
	 * 
	 * Method Description: Method to check if another primary caregiver is
	 * present for CVS Home window. EJB - CVS FA HOME
	 * 
	 * @param cvsFaHomeReq
	 * @return isKin @
	 */
	public String getIsExistPrimaryKin(CvsFaHomeReq cvsFaHomeReq) {
		String isExistPrimaryKin = ServiceConstants.FALSE_VAL;
		Long count = ServiceConstants.ZERO_VAL;
		count = (Long) ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getIsAlreadyKinSql)
				.setParameter("idPerson", cvsFaHomeReq.getPersonDtlDto().getIdPerson())
				.setParameter("idStage", cvsFaHomeReq.getStagePersonLinkDto().getIdStage()))
						.addScalar("idCount", StandardBasicTypes.LONG).uniqueResult();
		if (count > ServiceConstants.ZERO_VAL) {
			isExistPrimaryKin = ServiceConstants.TRUE_VAL;
		}
		return isExistPrimaryKin;
	}

	/**
	 * This DAM receives ID STAGE from the service and returns one or more rows
	 * from the STAGE_PERSON_LINK table.
	 * 
	 * 
	 * Service Name - CCMN03U, DAM Name - CCMNB9D
	 * 
	 * @param idStage
	 * @return @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<StagePersonLinkDto> getStagePersonLinkByIdStage(Long idStage) {
		List<StagePersonLinkDto> stagePersonLinkDtoList = new ArrayList<StagePersonLinkDto>();
		Query queryStage = sessionFactory.getCurrentSession().createSQLQuery(getStagePersonLinkByIdStageSql)
				.addScalar("idStagePersonLink", StandardBasicTypes.LONG)
				.addScalar("dtLastUpdate", StandardBasicTypes.TIMESTAMP).addScalar("idStage", StandardBasicTypes.LONG)
				.addScalar("idPerson", StandardBasicTypes.LONG).addScalar("cdStagePersRole", StandardBasicTypes.STRING)
				.addScalar("indStagePersInLaw", StandardBasicTypes.STRING)
				.addScalar("cdStagePersType", StandardBasicTypes.STRING)
				.addScalar("cdStagePersSearchInd", StandardBasicTypes.STRING)
				.addScalar("stagePersNotes", StandardBasicTypes.STRING)
				.addScalar("dtStagePersLink", StandardBasicTypes.TIMESTAMP)
				.addScalar("cdStagePersRelInt", StandardBasicTypes.STRING)
				.addScalar("indStagePersReporter", StandardBasicTypes.STRING)
				.addScalar("indStagePersEmpNew", StandardBasicTypes.STRING)
				.addScalar("strIndNmStage", StandardBasicTypes.STRING)
				.setResultTransformer(Transformers.aliasToBean(StagePersonLinkDto.class));
		queryStage.setParameter("idStage", idStage);
		stagePersonLinkDtoList = queryStage.list();
		return stagePersonLinkDtoList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<AllegtnPrsnDto> getStagePrsnLinkByStageId(Long idStage, String cdStagePersType) {
		List<AllegtnPrsnDto> persnRoleList = new ArrayList<>();
		persnRoleList = (List<AllegtnPrsnDto>) sessionFactory.getCurrentSession()
				.createSQLQuery(getStagePrsnLinkByStageId).addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("tsLastUpdate", StandardBasicTypes.TIMESTAMP).setParameter("hi_uidstage", idStage)
				.setParameter("hi_cdStagePersType", cdStagePersType)
				.setResultTransformer(Transformers.aliasToBean(AllegtnPrsnDto.class)).list();
		return persnRoleList;
	}

	/**
	 * This DAM will retreive full rows from the Stage_Person_Link table and the
	 * Caps_Resource table for a given child. It is intended to be used to
	 * determine the FAD Family members and case name for the new Post-Adoption
	 * stage being created at stage progression.
	 * 
	 * Service Name - CCMN03U, DAM Name - CLSS63D
	 * 
	 * @param idPerson
	 * @param plannedType
	 * @return @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<StageResourceDto> getStageResourceForChild(Long idPerson, String plannedType) {
		List<StageResourceDto> stageResourceDtoList = new ArrayList<StageResourceDto>();
		Query queryStage = sessionFactory.getCurrentSession().createSQLQuery(getStageResourceForChildSql)
				.addScalar("idResouce", StandardBasicTypes.LONG).addScalar("dtLastUpdate", StandardBasicTypes.TIMESTAMP)
				.addScalar("idRsrcFaHomeStage", StandardBasicTypes.LONG)
				.addScalar("nmResource", StandardBasicTypes.STRING)
				.addScalar("nmRsrcContact", StandardBasicTypes.STRING)
				.addScalar("idStagePersonLink", StandardBasicTypes.LONG).addScalar("idStage", StandardBasicTypes.LONG)
				.addScalar("idPerson", StandardBasicTypes.LONG).addScalar("idCase", StandardBasicTypes.LONG)
				.addScalar("cdStagePersRole", StandardBasicTypes.STRING)
				.addScalar("indStagePersInLaw", StandardBasicTypes.STRING)
				.addScalar("cdStagePersType", StandardBasicTypes.STRING)
				.addScalar("cdStagePersSearchInd", StandardBasicTypes.STRING)
				.addScalar("stagePersNotes", StandardBasicTypes.STRING)
				.addScalar("dtStagePersLink", StandardBasicTypes.TIMESTAMP)
				.addScalar("cdStagePersRelInt", StandardBasicTypes.STRING)
				.addScalar("indStagePersReporter", StandardBasicTypes.STRING)
				.addScalar("indStagePersEmpNew", StandardBasicTypes.STRING)
				.setResultTransformer(Transformers.aliasToBean(StageResourceDto.class));
		queryStage.setParameter("idPerson", idPerson);
		queryStage.setParameter("plannedType", plannedType);
		stageResourceDtoList = queryStage.list();
		return stageResourceDtoList;
	}

	/**
	 * Retrieves Primary Child id and Case id from Stage_person_link given stage
	 * id as input.
	 * 
	 * Service Name - CCMN02U, DAM Name - CLSSA5D
	 * 
	 * @param idStage
	 * @return @
	 */
	@Override
	public StagePersonLinkDto getPrimaryChildIdByIdStage(Long idStage) {
		StagePersonLinkDto stagePersonLinkDto = new StagePersonLinkDto();
		Query queryStage = sessionFactory.getCurrentSession().createSQLQuery(getPrimaryChildIdByIdStageSql)
				.addScalar("idPerson", StandardBasicTypes.LONG).addScalar("idCase", StandardBasicTypes.LONG)
				.setResultTransformer(Transformers.aliasToBean(StagePersonLinkDto.class));
		queryStage.setParameter("idStage", idStage);
		stagePersonLinkDto = (StagePersonLinkDto) queryStage.uniqueResult();
		return stagePersonLinkDto;
	}

	/**
	 * Retrieves a corresponding ADO stage id from stage_link given a SUB stage
	 * id.
	 * 
	 * Service Name - CCMN02U, DAM Name - CLSSA6D
	 * 
	 * @param idStage
	 * @return @
	 */
	@Override
	public Long getIdADOStageByIdSUBStage(Long idStage) {
		Long idADOStage = ServiceConstants.ZERO_VAL;
		Query queryStage = sessionFactory.getCurrentSession().createSQLQuery(getIdADOStageByIdSUBStageSql);
		queryStage.setParameter("idStage", idStage);
		if (!ObjectUtils.isEmpty(queryStage.uniqueResult())){
			idADOStage = ((BigDecimal) queryStage.uniqueResult()).longValue(); //Defect 10785 - Hibernate by default returns big decimal. convert it to long
		}
		return idADOStage;
	}

	/**
	 * Retrieves SUB stage id from stage_link given correspoding ADO stage id.
	 * 
	 * Service Name - CCMN02U, DAM Name - CLSSA7D
	 * 
	 * @param idStage
	 * @return @
	 */
	@Override
	public Long getIdSUBStageByIdADOStage(Long idStage) {
		Long idSUBStage = ServiceConstants.ZERO_VAL;
		Query queryStage = sessionFactory.getCurrentSession().createSQLQuery(getIdSUBStageByIdADOStageSql);
		queryStage.setParameter("idStage", idStage);
		idSUBStage = (Long) queryStage.uniqueResult();
		return idSUBStage;
	}

	@Override
	public void updateStagePersonLink(AllegationDetailDto allegationDetail) {
		// update perpetrator record
		if (!ObjectUtils.isEmpty(allegationDetail.getIdAllegedPerpetrator())) {
			StagePersonLink stagePersonLink = getStagePersonLink(allegationDetail.getIdStage(),
					allegationDetail.getIdAllegedPerpetrator());
			// Defect#5418 IF the Alleged Perpetrator is not on Stage allow
			// allegation to be deleted.
			if (!ObjectUtils.isEmpty(stagePersonLink)) {
				stagePersonLink.setCdStagePersRole(allegationDetail.getCdStagePersRole());
				stagePersonLink.setDtLastUpdate(new Date());
				sessionFactory.getCurrentSession().update(stagePersonLink);
			}
		}
		// update victim record
		if (!ObjectUtils.isEmpty(allegationDetail.getIdVictim())) {
			StagePersonLink stagePersonLinkVictim = getStagePersonLink(allegationDetail.getIdStage(),
					allegationDetail.getIdVictim());
			stagePersonLinkVictim.setCdStagePersRole(allegationDetail.getCdStagePersRole2());
			stagePersonLinkVictim.setDtLastUpdate(new Date());
			sessionFactory.getCurrentSession().update(stagePersonLinkVictim);
		}
	}

	@Override
	public StagePersonLink getStagePersonLink(Long idStage, Long idPerson) {
		StagePersonLink stagePersonLink = (StagePersonLink) sessionFactory.getCurrentSession()
				.createCriteria(StagePersonLink.class).add(Restrictions.eq("idStage", idStage))
				.add(Restrictions.eq("idPerson", idPerson)).uniqueResult();
		return stagePersonLink;
	}

	// CMSC23D
	@Override
	public List<StagePersDto> getStageListByIdPerson(Long idPerson) {
		List<StagePersDto> stagePersDtoList = new ArrayList<>();
		Query queryStage = sessionFactory.getCurrentSession().createSQLQuery(getStageListByIdPersonSql)
				.addScalar("idStage", StandardBasicTypes.LONG).addScalar("dtLastUpdate", StandardBasicTypes.TIMESTAMP)
				.addScalar("cdStageType", StandardBasicTypes.STRING).addScalar("idUnit", StandardBasicTypes.LONG)
				.addScalar("idStage", StandardBasicTypes.LONG).addScalar("idCase", StandardBasicTypes.LONG)
				.addScalar("idSituation", StandardBasicTypes.LONG)
				.addScalar("dtStageClose", StandardBasicTypes.TIMESTAMP)
				.addScalar("cdStageClassification", StandardBasicTypes.STRING)
				.addScalar("cdStageCurrPriority", StandardBasicTypes.STRING)
				.addScalar("cdStageInitialPriority", StandardBasicTypes.STRING)
				.addScalar("cdStageReasonClosed", StandardBasicTypes.STRING)
				.addScalar("cdStageRsnPriorityChgd", StandardBasicTypes.STRING)
				.addScalar("indStageClose", StandardBasicTypes.STRING)
				.addScalar("cdStageCnty", StandardBasicTypes.STRING).addScalar("nmStage", StandardBasicTypes.STRING)
				.addScalar("cdStageRegion", StandardBasicTypes.STRING)
				.addScalar("dtStageStart", StandardBasicTypes.TIMESTAMP)
				.addScalar("cdStageProgram", StandardBasicTypes.STRING).addScalar("cdStage", StandardBasicTypes.STRING)
				.addScalar("stagePriorityCmnts", StandardBasicTypes.STRING)
				.addScalar("stageClosureCmnts", StandardBasicTypes.STRING)
				.addScalar("idStagePersonLink", StandardBasicTypes.LONG)
				.addScalar("dtLastUpdatePers", StandardBasicTypes.TIMESTAMP)
				.addScalar("idPerson", StandardBasicTypes.LONG).addScalar("cdStagePersRole", StandardBasicTypes.STRING)
				.addScalar("indStagePersInLaw", StandardBasicTypes.STRING)
				.addScalar("cdStagePersType", StandardBasicTypes.STRING)
				.addScalar("cdStagePersSearchInd", StandardBasicTypes.STRING)
				.addScalar("stagePersNotes", StandardBasicTypes.STRING)
				.addScalar("dtStagePersLink", StandardBasicTypes.TIMESTAMP)
				.addScalar("cdStagePersRelInt", StandardBasicTypes.STRING)
				.addScalar("indStagePersReporter", StandardBasicTypes.STRING)
				.addScalar("indStagePersEmpNew", StandardBasicTypes.STRING)
				.setResultTransformer(Transformers.aliasToBean(StagePersDto.class));
		queryStage.setParameter("idPerson", idPerson);
		stagePersDtoList = queryStage.list();
		return stagePersDtoList;
	}

	/**
	 * 
	 * Method Name: getPersonLegalStatus(CCMNH9D) Method Description:Retrieves
	 * all the person with Legal Statuses in a given Case.
	 * 
	 * @param idCase
	 * @param cdEventType
	 * @return
	 */
	public List<StagePersDto> getPersonLegalStatus(Long idCase, String cdEventType) {

		List<StagePersDto> stagePersDtoList = new ArrayList<>();
		Query queryStage = sessionFactory.getCurrentSession().createSQLQuery(getPersonLegalStatusSql)
				.addScalar("cdStageType", StandardBasicTypes.STRING).addScalar("idPerson", StandardBasicTypes.LONG)
				.setParameter("idCase", idCase).setParameter("cdEventType", cdEventType)
				.setResultTransformer(Transformers.aliasToBean(StagePersDto.class));
		stagePersDtoList = queryStage.list();
		return stagePersDtoList;

	}

	/**
	 * Method Name: isChildPrimary Method Description: This method is used to
	 * check if the child is primary.
	 * 
	 * @param idPerson
	 * @param idStage
	 * @param idCase
	 * @return
	 */
	@Override
	@SuppressWarnings("unchecked")
	public boolean isChildPrimary(Long idPerson, Long idStage, Long idCase) {
		List<StagePersonLink> stagePersonLink = sessionFactory.getCurrentSession().createCriteria(StagePersonLink.class)
				.add(Restrictions.eq("idStage", idStage)).add(Restrictions.eq("idPerson", idPerson))
				.add(Restrictions.eq("idCase", idCase)).list();
		boolean isChildprimary = stagePersonLink.stream()
				.anyMatch(o -> CodesConstant.CROLES_PC.equalsIgnoreCase(o.getCdStagePersRole()));
		return isChildprimary;
	}

	/**
	 * Method name:getChildPrimaryInfo Method Description: This method returns
	 * the Priamry Child Information for the given Person ID
	 * 
	 * @param idPerson
	 * @param idCase
	 * @return
	 */
	@Override
	@SuppressWarnings("unchecked")
	public StagePersonLinkDto getChildPrimaryInfo(Long idPerson, Long idCase) {
		StagePersonLinkDto stagePersonLinkDto = new StagePersonLinkDto();
		StagePersonLink stagePersonLink = (StagePersonLink) sessionFactory.getCurrentSession()
				.createCriteria(StagePersonLink.class).add(Restrictions.eq("idPerson", idPerson))
				.add(Restrictions.eq("idCase", idCase)).add(Restrictions.eq("cdStagePersRole", CodesConstant.CROLES_PC))
				.uniqueResult();
		if(!ObjectUtils.isEmpty(stagePersonLink)){
			BeanUtils.copyProperties(stagePersonLink, stagePersonLinkDto);
		}
		return stagePersonLinkDto;
	}

	/**
	 * Method Name: getRiskAssessment Method Description:CINV80D - This DAM
	 * retrieves the Person Id for the given idStage and cdStagePersType
	 * 
	 * @param idStage
	 * @param cdStagePersType
	 * @return StagePersonLink
	 */
	@SuppressWarnings("unchecked")
	public List<StagePersonLink> getStagePersonLinkDtl(Long idStage, String cdStagePersType) {
		Criteria criteria = (Criteria) sessionFactory.getCurrentSession().createCriteria(StagePersonLink.class);
		criteria.add(Restrictions.eq("idStage", idStage));
		criteria.add(Restrictions.eq("cdStagePersType", cdStagePersType));
		List<StagePersonLink> stagePersonLinkList = criteria.list();
		return stagePersonLinkList;
	}

	public List<StagePersonLink> getStagePersonLinkNonHpRole(Long idStage) {
		Criteria criteria = (Criteria) sessionFactory.getCurrentSession().createCriteria(StagePersonLink.class);
		criteria.add(Restrictions.eq("idStage", idStage));
		criteria.add(Restrictions.ne("cdStagePersRole", "HP"));
		List<StagePersonLink> stagePersonLinkList = criteria.list();
		return stagePersonLinkList;
	}

	/**
	 * artf251080 : Kinship Case will NOT Approve
	 * get primary caregiver for the given stage id
	 * @param stageId
	 * @return
	 */
	public Long getPrimaryCareGiverbyStage(Long stageId){

		Criteria criteria = (Criteria) sessionFactory.getCurrentSession().createCriteria(StagePersonLink.class);
		criteria.add(Restrictions.eq("idStage", stageId));
		criteria.add(Restrictions.eq("indKinPrCaregiver", ServiceConstants.Y));

		try {
			StagePersonLink stagePersonLink = (StagePersonLink) criteria.uniqueResult();
			return stagePersonLink.getIdPerson();
		} catch (Exception ex){
			throw new DataLayerException("Unique Primary Caregiver was not found for the Stage id " + stageId);
		}
	}

	@Override
	public List<StagePersonLinkDto> getStgPersonLinkDtlsBasedOnCase(Long idCase, String cdStagePersType) {
		List<StagePersonLinkDto> stagePersonLinkDtoList = new ArrayList<>();
		Query queryStage = sessionFactory.getCurrentSession().createSQLQuery(getStgPersonLinkDtlsBasedOnCaseSql)
				.addScalar("idStagePersonLink", StandardBasicTypes.LONG)
				.addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("indStagePersReporter", StandardBasicTypes.STRING)
				.setParameter("idCase", idCase)
				.setParameter("cdStagePersType", cdStagePersType)
				.setResultTransformer(Transformers.aliasToBean(StagePersonLinkDto.class));
		stagePersonLinkDtoList = queryStage.list();
		return stagePersonLinkDtoList;
	}
}
