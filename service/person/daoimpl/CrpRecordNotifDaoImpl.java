package us.tx.state.dfps.service.person.daoimpl;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import us.tx.state.dfps.common.domain.CrpRecordNotif;
import us.tx.state.dfps.service.casepackage.dto.ContactNarrativeDto;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.CrpRecordNotifReq;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.forms.dto.CrpRecordNotifAndDetailsDto;
import us.tx.state.dfps.service.person.dao.CrpRecordNotifDao;
import us.tx.state.dfps.service.person.dto.*;
import us.tx.state.dfps.service.recordscheck.dto.ResourceContractInfoDto;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * service-business- IMPACT 2.0 Class Description:
 * CrpRecordNotifDaoImpl implemented all operation defined in
 * CrpRecordNotifDao Interface to fetch the records from table which map
 * to public central registry screen. Jan 25, 2024- 9:58:00 PM © 2024 Texas Department
 * of Family and Protective Services
 *
 * ********Change History**********
 * 01/25/2024 thompswa Initial.
 * 08/02/2024 thompswa artf268135 matched requests added.
 */

@Repository
public class CrpRecordNotifDaoImpl implements CrpRecordNotifDao {

	private static final Logger logger = Logger.getLogger(CrpRecordNotifDaoImpl.class);

	@Value("${CrpRecordNotifDaoImpl.getResourceContractInfoDtl}")
	String getResourceContractInfoDtlSql;
	@Value("${CrpRecordNotifDaoImpl.getCrpReqDtl}")
	String getCrpReqDtlSql;
	@Value("${CrpRecordNotifDaoImpl.getCrpPersonNames}")
	String getCrpPersonNamesSql;
	@Value("${CrpRecordNotifDaoImpl.getCrpBatchResult}")
	String getCrpBatchResultSql;
	@Value("${CrpRecordNotifDaoImpl.getCrpRecordDetailSql}")
	String getCrpRecordDetailSql;

	@Value("${CrpRecordNotifDaoImpl.getCrpRecordDetailAndSql}")
	String getCrpRecordDetailAndSql;

	@Value("${CrpRecordNotifDaoImpl.nextRequestStatusId}")
	private transient String nextRequestStatusId;

	@Value("${CrpRecordNotifDaoImpl.insertCrpRequestStatus}")
	String insertCrpRequestStatus;

	@Autowired
	private SessionFactory sessionFactory;

	/**
	 * Method Description: This Method will retrieve the resource contract Info
	 * details form record check, resource address tables by passing idRecCheck
	 * as input.
	 *
	 * @param idCrpCheck
	 * @return ResourceContractInfoDto
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ResourceContractInfoDto getResourceContractInfo(Long idCrpCheck) {
		List<ResourceContractInfoDto> resourceContractInfoDtoList = new ArrayList<ResourceContractInfoDto>();
		resourceContractInfoDtoList = (List<ResourceContractInfoDto>) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getResourceContractInfoDtlSql).setParameter("idCrpCheck", idCrpCheck))
				.addScalar("cdCntrctType", StandardBasicTypes.STRING)
				.addScalar("txtEmailAddress", StandardBasicTypes.STRING)
				.addScalar("nmResource", StandardBasicTypes.STRING)
				.addScalar("rsrcAddrStLn1", StandardBasicTypes.STRING)
				.addScalar("rsrcAddrStLn2", StandardBasicTypes.STRING)
				.addScalar("cdRsrcAddrState", StandardBasicTypes.STRING)
				.addScalar("rsrcAddrCity", StandardBasicTypes.STRING)
				.addScalar("rsrcAddrZip", StandardBasicTypes.STRING)
				.addScalar("idrecCheckRequestor", StandardBasicTypes.LONG)
				.addScalar("idBackGroundCheck", StandardBasicTypes.LONG)
				.addScalar("backGroundCheckReqid", StandardBasicTypes.STRING)
				.addScalar("indBgcRsltRecpnt", StandardBasicTypes.STRING)
				.setResultTransformer(Transformers.aliasToBean(ResourceContractInfoDto.class)).list();
		if (!ObjectUtils.isEmpty(resourceContractInfoDtoList)) {
			return resourceContractInfoDtoList.get(0);
		} else {
			return null;
		}

	}


	/**
	 * Method Description: This Method will retrieve the Central Registry Check Info
	 * details from CENTRAL_REGISTRY_CHECK@impactp, CRP_PERSON_ADDRESS@impactp tables
	 * by passing idCrpCheck as input.
	 *
	 * @param idRequest
	 * @return CentralRegistryCheckRequest
	 */
	@SuppressWarnings("unchecked")
	@Override
	public CentralRegistryCheckDto getCrpReqDtl(Long idRequest) {
		List<CentralRegistryCheckDto> crpRequestList = new ArrayList<>();
		crpRequestList = (List<CentralRegistryCheckDto>) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getCrpReqDtlSql).setParameter("idRequest", idRequest))
				.addScalar("idCentralRegistryCheck", StandardBasicTypes.LONG)
				.addScalar("cdPurpose", StandardBasicTypes.STRING)
				.addScalar("cdPlacementChild", StandardBasicTypes.STRING)
				.addScalar("txtOthPlacementChild", StandardBasicTypes.STRING)
				.addScalar("cdChildCare", StandardBasicTypes.STRING)
				.addScalar("nmEmployerAgency", StandardBasicTypes.STRING)
				.addScalar("nmFirst", StandardBasicTypes.STRING)
				.addScalar("nmMiddle", StandardBasicTypes.STRING)
				.addScalar("nmLast", StandardBasicTypes.STRING)
				.addScalar("indMiddleNm", StandardBasicTypes.STRING)
				.addScalar("indNoOtherNames", StandardBasicTypes.STRING)
				.addScalar("indSsn", StandardBasicTypes.STRING)
				.addScalar("nbrSsn", StandardBasicTypes.STRING)
				.addScalar("cdAltrntIdType", StandardBasicTypes.STRING)
				.addScalar("txtAltrntId", StandardBasicTypes.STRING)
				.addScalar("cdAltrntIdState", StandardBasicTypes.STRING)
				.addScalar("dtPersonBirth", StandardBasicTypes.DATE)
				.addScalar("cdPersonSex", StandardBasicTypes.STRING)
				.addScalar("cdEthnicity", StandardBasicTypes.STRING)
				.addScalar("nbrPhone", StandardBasicTypes.STRING)
				.addScalar("txtEmailAddress", StandardBasicTypes.STRING)
				.addScalar("indLivedOthCities", StandardBasicTypes.STRING)
				.addScalar("indValidInformation", StandardBasicTypes.STRING)
				.addScalar("indRequestDocuments", StandardBasicTypes.STRING)
				.addScalar("indInaccurateOmission", StandardBasicTypes.STRING)
				.addScalar("dtSubmitted", StandardBasicTypes.DATE)
				.addScalar("idRequest", StandardBasicTypes.LONG)
				.addScalar("cdStatus", StandardBasicTypes.STRING)
				.addScalar("indSingleUser", StandardBasicTypes.STRING)
				.addScalar("tsCreated", StandardBasicTypes.TIMESTAMP)
				.addScalar("idCreatedBy", StandardBasicTypes.LONG)
				.addScalar("tsLastUpdate", StandardBasicTypes.TIMESTAMP)
				.addScalar("idLastUpdateBy", StandardBasicTypes.LONG)
				.addScalar("idContract", StandardBasicTypes.LONG)
				.addScalar("addrStLn1", StandardBasicTypes.STRING)
				.addScalar("addrStLn2", StandardBasicTypes.STRING)
				.addScalar("addrCity", StandardBasicTypes.STRING)
				.addScalar("cdAddrState", StandardBasicTypes.STRING)
				.addScalar("addrZip", StandardBasicTypes.STRING)
				.addScalar("cdCounty", StandardBasicTypes.STRING)
				.addScalar("subjectEmail", StandardBasicTypes.STRING)
				.setResultTransformer(Transformers.aliasToBean(CentralRegistryCheckDto.class)).list();
		if (!ObjectUtils.isEmpty(crpRequestList)) {
			return crpRequestList.get(0);
		} else {
			return null;
		}

	}


	/**
	 * Method Description: This Method will retrieve the Central Registry Check Person
	 * Names from CRP_RECORD_NAME@impactp, by passing idRequest as input.
	 *
	 * @param idRequest
	 * @return crpPersonNameList
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<CrpPersonNameDto>  getCrpPersonNames(Long idRequest) {
		List<CrpPersonNameDto> crpPersonNameList = new ArrayList<>();
		crpPersonNameList = (List<CrpPersonNameDto>) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(getCrpPersonNamesSql).setParameter("idRequest", idRequest))
				.addScalar("idCrpPersonName", StandardBasicTypes.LONG)
				.addScalar("idCentralRegistryCheck", StandardBasicTypes.LONG)
				.addScalar("nameFirst", StandardBasicTypes.STRING)
				.addScalar("nameMiddle", StandardBasicTypes.STRING)
				.addScalar("nameLast", StandardBasicTypes.STRING)
				.addScalar("indMiddleNm", StandardBasicTypes.STRING)
				.addScalar("idRequest", StandardBasicTypes.LONG)
				.setResultTransformer(Transformers.aliasToBean(CrpPersonNameDto.class)).list();
		return crpPersonNameList;

	}

	/**
	 * Method Description: This Method will retrieve the Central Registry Check Info
	 * details from CENTRAL_REGISTRY_CHECK@impactp, CRP_PERSON_ADDRESS@impactp tables
	 * by passing idCrpCheck as input.
	 *
	 * @param idRequestList   // artf268135
	 * @return crpBatchResultList
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<PublicCentralRegistryDto> getCrpBatchResult(List<Long> idRequestList) {
		List<PublicCentralRegistryDto> crpBatchResultList = new ArrayList<>();

		Query query = (Query) sessionFactory.getCurrentSession()
					.createSQLQuery(getCrpBatchResultSql)
					.addScalar("idPublicCentralRegistry", StandardBasicTypes.LONG)
					.addScalar("dtSubmittedCr", StandardBasicTypes.DATE)
					.addScalar("dtLastUpdatePmr", StandardBasicTypes.DATE)
					.addScalar("idRequest", StandardBasicTypes.LONG)
					.addScalar("cdPersMatchReqSex", StandardBasicTypes.STRING)
					.addScalar("idPersMatchReqPerson", StandardBasicTypes.LONG)
					.addScalar("cdPersMatchReqRsltCode", StandardBasicTypes.STRING)
					.addScalar("nmPersMatchReqFirst", StandardBasicTypes.STRING)
					.addScalar("nmPersMatchReqMiddle", StandardBasicTypes.STRING)
					.addScalar("nmPersMatchReqLast", StandardBasicTypes.STRING)
					.addScalar("nmPersMatchReqSuffix", StandardBasicTypes.STRING)
					.addScalar("dtPersMatchReqBirth", StandardBasicTypes.DATE)
					.addScalar("nbrPersMatchReqTdhs", StandardBasicTypes.STRING)
					.addScalar("nbrPersMatchReqSsn", StandardBasicTypes.STRING)
					.addScalar("nbrEmployeeSsn", StandardBasicTypes.STRING)
					.addScalar("indCrCleared", StandardBasicTypes.STRING)
					.addScalar("idCase", StandardBasicTypes.LONG)
					.addScalar("idStage", StandardBasicTypes.LONG)
					.addScalar("cdStage", StandardBasicTypes.STRING)
					.addScalar("cdStageProgram", StandardBasicTypes.STRING)
					.addScalar("cdAllegType", StandardBasicTypes.STRING)
					.addScalar("dtStageClosed", StandardBasicTypes.DATE)
					.addScalar("cdPersonStatus", StandardBasicTypes.STRING)
					.addScalar("cdStagePersRole", StandardBasicTypes.STRING)
					.addScalar("txtRoleDecode", StandardBasicTypes.STRING)
					.addScalar("indDobApprx", StandardBasicTypes.STRING)
					.addScalar("indMatchClear", StandardBasicTypes.STRING)
					.addScalar("dtLastUpdate", StandardBasicTypes.DATE)
					.addScalar("indSavePost", StandardBasicTypes.STRING)
				    .setParameterList("idRequestList", idRequestList)
				    .setResultTransformer(Transformers.aliasToBean(PublicCentralRegistryDto.class));
		crpBatchResultList = (List<PublicCentralRegistryDto>) query.list();
		return crpBatchResultList;
	}


	/**
	 * Method Name : getCrpRecordNotifAndDetails
	 * Method Description: This method will fetch the Record check notification
	 * details by passing idCrpRecordDetail as input
	 *
	 * @param crpRecordNotifReq
	 * @return CrpRecordDetailDto @
	 */
	@SuppressWarnings("unchecked")
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
			Exception.class })
	public CrpRecordNotifAndDetailsDto getCrpRecordNotifAndDetails(CrpRecordNotifReq crpRecordNotifReq) {
		List<CrpRecordNotifAndDetailsDto> crpRecordNotifAndDetailsDtoList = new ArrayList<>();

		String append = " ";
		String dynamic = getCrpRecordDetailSql;
		if (!TypeConvUtil.isNullOrEmpty(crpRecordNotifReq.getIdCrpRecordNotif())) {
			dynamic = dynamic.concat(append + getCrpRecordDetailAndSql + crpRecordNotifReq.getIdCrpRecordNotif());
		}
		logger.info("dynamic = " + dynamic);
		crpRecordNotifAndDetailsDtoList = (List<CrpRecordNotifAndDetailsDto>) ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(dynamic)
				.setParameter(0, crpRecordNotifReq.getIdCrpCheck()))
				.addScalar("idCrpRecordDetail", StandardBasicTypes.LONG)
				.addScalar("idMailTracking", StandardBasicTypes.STRING)
				.addScalar("cdRequestStatus", StandardBasicTypes.STRING)
				.addScalar("dtCompleted", StandardBasicTypes.DATE)
				.addScalar("idRequest", StandardBasicTypes.LONG)
				.addScalar("cdSoahStatus", StandardBasicTypes.STRING)
				.addScalar("dtResponseReceived", StandardBasicTypes.DATE)
				.addScalar("dtSoahEmailed", StandardBasicTypes.DATE)
				.addScalar("dtSoahDecision", StandardBasicTypes.DATE)
				.addScalar("idCrpRecordNotif", StandardBasicTypes.LONG)
				.addScalar("cdNotifType", StandardBasicTypes.STRING)
				.addScalar("cdNotifctnStat", StandardBasicTypes.STRING)
				.addScalar("dtNotifctnSent", StandardBasicTypes.DATE)
				.addScalar("txtRecpntEmail", StandardBasicTypes.STRING)
				.addScalar("idSndrPerson", StandardBasicTypes.LONG)
				.addScalar("txtSndrEmail", StandardBasicTypes.STRING)
				.addScalar("indSecureNotification", StandardBasicTypes.STRING)
				.addScalar("subjectEmail", StandardBasicTypes.STRING)
				.setResultTransformer(Transformers.aliasToBean(CrpRecordNotifAndDetailsDto.class)).list();
		if (!ObjectUtils.isEmpty(crpRecordNotifAndDetailsDtoList)) {
			if (TypeConvUtil.isNullOrEmpty(crpRecordNotifReq.getIdCrpRecordNotif())) {
				crpRecordNotifAndDetailsDtoList.get(0).setIdCrpRecordNotif(null);
				crpRecordNotifAndDetailsDtoList.get(0).setCdNotifType(crpRecordNotifReq.getDocType());
				crpRecordNotifAndDetailsDtoList.get(0).setCdNotifctnStat(CodesConstant.CNOTSTAT_NEW);
			}
			return crpRecordNotifAndDetailsDtoList.get(0);
		} else {
			return new CrpRecordNotifAndDetailsDto();
		}
	}

	/**
	 * Method Description: This method will fetch the Record check notification
	 * details by passing idRecordsCheckNotif as input
	 *
	 * @param idCrpRecordNotif
	 * @return RecordsCheckNotifDto @
	 */
	@Override
	public CrpRecordNotifDto getCrpRecordNotification(Long idCrpRecordNotif) {

		CrpRecordNotifDto crpRecordNotifDto = new CrpRecordNotifDto();
		crpRecordNotifDto = (CrpRecordNotifDto) sessionFactory.getCurrentSession()
				.createCriteria(CrpRecordNotif.class)
				.setProjection(Projections.projectionList()
						.add(Projections.property("idCrpRecordNotif"), "idCrpRecordNotif")
						.add(Projections.property("idCrpRecordDetail"), "idCrpRecordDetail")
						.add(Projections.property("dtCreated"), "dtCreated")
						.add(Projections.property("idCreatedPerson"), "idCreatedPerson")
						.add(Projections.property("dtLastUpdate"), "dtLastUpdate")
						.add(Projections.property("idLastUpdatePerson"), "idLastUpdatePerson")
						.add(Projections.property("cdNotifType"), "cdNotifType")
						.add(Projections.property("cdNotifctnStat"), "cdNotifctnStat")
						.add(Projections.property("dtNotifctnSent"), "dtNotifctnSent")
						.add(Projections.property("txtRecpntEmail"), "txtRecpntEmail")
						.add(Projections.property("txtSndrEmail"), "txtSndrEmail")
						.add(Projections.property("idSndrPerson"), "idSndrPerson"))
				.add(Restrictions.eq("idCrpRecordNotif", idCrpRecordNotif))
				.setResultTransformer(Transformers.aliasToBean(CrpRecordNotifDto.class)).uniqueResult();
		return crpRecordNotifDto;
	}

	/**
	 * Method Description: This method will update the Record check notification
	 * table by passing required input
	 *
	 * @param crpRecordNotifDto
	 * @return rtnMsg @
	 */
	@Override
	public String updateCrpRecordNotif(CrpRecordNotifDto crpRecordNotifDto) {
		String rtnMsg = ServiceConstants.EMPTY_STRING;
		CrpRecordNotif crpRecordNotifEntity = (CrpRecordNotif) sessionFactory.getCurrentSession()
				.get(CrpRecordNotif.class, crpRecordNotifDto.getIdCrpRecordNotif());
		if (!TypeConvUtil.isNullOrEmpty(crpRecordNotifDto.getIdLastUpdatePerson()))
			crpRecordNotifEntity.setIdLastUpdatePerson(crpRecordNotifDto.getIdLastUpdatePerson());
		if (!TypeConvUtil.isNullOrEmpty(crpRecordNotifDto.getCdNotifctnStat())) {
			if (!CodesConstant.CNOTSTAT_RESENT.equals(crpRecordNotifDto.getCdNotifctnStat())) {
				if (CodesConstant.CNOTSTAT_NEW.equals(crpRecordNotifDto.getCdNotifctnStat())) {
					crpRecordNotifEntity.setCdNotifctnStat(CodesConstant.CNOTSTAT_DRFT);
				} else {
					crpRecordNotifEntity.setCdNotifctnStat(CodesConstant.CNOTSTAT_SENT);
				}
			}
			if (!CodesConstant.CNOTSTAT_NEW.equals(crpRecordNotifDto.getCdNotifctnStat())) {
				if (!CodesConstant.CNOTSTAT_RESENT.equals(crpRecordNotifDto.getCdNotifctnStat())) {
					crpRecordNotifEntity.setDtNotifctnSent(new Date());
				}
//				if (!TypeConvUtil.isNullOrEmpty(crpRecordNotifDto.getIdRecpntPerson()))
//					crpRecordNotifEntity.setIdRecpntPerson(crpRecordNotifDto.getIdRecpntPerson());
				if (!TypeConvUtil.isNullOrEmpty(crpRecordNotifDto.getTxtRecpntEmail()))
					crpRecordNotifEntity.setTxtRecpntEmail(crpRecordNotifDto.getTxtRecpntEmail().trim());
				if (!TypeConvUtil.isNullOrEmpty(crpRecordNotifDto.getIdSndrPerson())) {
					crpRecordNotifEntity.setIdSndrPerson(crpRecordNotifDto.getIdSndrPerson());
				}
			}

		}
		if (!TypeConvUtil.isNullOrEmpty(crpRecordNotifDto.getTxtSndrEmail()))
			crpRecordNotifEntity.setTxtSndrEmail(crpRecordNotifDto.getTxtSndrEmail());
		if (!TypeConvUtil.isNullOrEmpty(crpRecordNotifDto.getIndSecureNotification()))
			crpRecordNotifEntity.setIndSecureNotification(crpRecordNotifDto.getIndSecureNotification());
		sessionFactory.getCurrentSession().saveOrUpdate(crpRecordNotifEntity);
		rtnMsg = ServiceConstants.SUCCESS;
		return rtnMsg;
	}

	/**
	 * Method Description: This method will create the new crp record
	 * notification detail.
	 *
	 * @param crpRecordNotifDto
	 * @return Long
	 */
	public Long insertCrpRecordNotif(CrpRecordNotifDto crpRecordNotifDto) {

		Long idCrpRecordNotif = ServiceConstants.ZERO;
		CrpRecordNotif crpRecordNotifEntity = new CrpRecordNotif();
		if (!TypeConvUtil.isNullOrEmpty(crpRecordNotifDto.getIdCrpRecordDetail()))
			crpRecordNotifEntity.setIdCrpRecordDetail(crpRecordNotifDto.getIdCrpRecordDetail());
		crpRecordNotifEntity.setDtCreated(new Date());
		crpRecordNotifEntity.setDtLastUpdate(new Date());
		if (!TypeConvUtil.isNullOrEmpty(crpRecordNotifDto.getIdCreatedPerson()))
			crpRecordNotifEntity.setIdCreatedPerson(crpRecordNotifDto.getIdCreatedPerson());
		if (!TypeConvUtil.isNullOrEmpty(crpRecordNotifDto.getIdLastUpdatePerson()))
			crpRecordNotifEntity.setIdLastUpdatePerson(crpRecordNotifDto.getIdLastUpdatePerson());
		if (!TypeConvUtil.isNullOrEmpty(crpRecordNotifDto.getCdNotifType()))
			crpRecordNotifEntity.setCdNotifType(crpRecordNotifDto.getCdNotifType());
		if (!TypeConvUtil.isNullOrEmpty(crpRecordNotifDto.getCdNotifctnStat()))
			crpRecordNotifEntity.setCdNotifctnStat(crpRecordNotifDto.getCdNotifctnStat());
		if (!TypeConvUtil.isNullOrEmpty(crpRecordNotifDto.getDtNotifctnSent()))
		    crpRecordNotifEntity.setDtNotifctnSent(crpRecordNotifDto.getDtNotifctnSent());

		idCrpRecordNotif = (Long) sessionFactory.getCurrentSession().save(crpRecordNotifEntity);
		logger.info("insert crpRecordNotifDto : idCrpRecordNotif = " + idCrpRecordNotif);
		return idCrpRecordNotif;
	}



	/**
	 * Method Description: This method will insert the new crp record
	 * notification pdf.
	 *
	 * @param crpRequestStatusDto
	 * @return Long
	 */

	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public String insertCrpRequestStatus(CrpRequestStatusDto crpRequestStatusDto) {
		String rtnMsg;
		Long idCrpRequestStatus = ServiceConstants.ZERO;

		byte[] pdfBytes = crpRequestStatusDto.getPdfNarrative();

		CentralRegistryCheckDto centralRegistryCheckDto = getCrpReqDtl(crpRequestStatusDto.getIdRequest());

		Date dtSubmittedCr = centralRegistryCheckDto.getDtSubmitted();

		SQLQuery getNextReqStatusId = sessionFactory.getCurrentSession()
				.createSQLQuery(nextRequestStatusId)
				.addScalar("idRequestStatus", StandardBasicTypes.LONG);
		Long idRequestStatus = (Long) getNextReqStatusId.uniqueResult();

		Query queryCreateContent = sessionFactory.getCurrentSession()
				.createSQLQuery(insertCrpRequestStatus)
				.setParameter("ID_CRP_REQUEST_STATUS", idRequestStatus)
				.setParameter("ID_REQUEST", crpRequestStatusDto.getIdRequest())
				.setParameter("CD_REQ_STATUS", crpRequestStatusDto.getCdReqStat())
				.setParameter("DT_SUBMITTED_CR", dtSubmittedCr)
				.setParameter("DT_NOTIFICATION_SENT", new Date())
				.setParameter("CD_NOTIF_TYPE", crpRequestStatusDto.getCdNotifType())
				.setParameter("TS_CREATED", new Date())
				.setParameter("ID_CREATED_BY", crpRequestStatusDto.getIdCreatedBy())
				.setParameter("TS_LAST_UPDATE", new Date())
				.setParameter("ID_LAST_UPDATE_BY", crpRequestStatusDto.getIdLastUpdateBy())
				.setParameter("PDF_NARRATIVE", pdfBytes);

		queryCreateContent.executeUpdate();
		rtnMsg = ServiceConstants.SUCCESS;

		logger.info("insert crpRequestStatusDto : idCrpRequestStatus = " + idCrpRequestStatus);
		return rtnMsg;
	}
}
