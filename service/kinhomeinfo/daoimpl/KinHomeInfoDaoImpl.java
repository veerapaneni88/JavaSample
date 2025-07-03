package us.tx.state.dfps.service.kinhomeinfo.daoimpl;

import static org.apache.commons.lang3.math.NumberUtils.BYTE_ONE;
import static us.tx.state.dfps.service.common.CodesConstant.CATOFSVC_24;
import static us.tx.state.dfps.service.common.CodesConstant.CCONFUNC_CPS;
import static us.tx.state.dfps.service.common.CodesConstant.CCONPAY_URT;
import static us.tx.state.dfps.service.common.CodesConstant.CCONPAY_VUR;
import static us.tx.state.dfps.service.common.CodesConstant.CCONPROC_NCN;
import static us.tx.state.dfps.service.common.CodesConstant.CCONPROG_CPS;
import static us.tx.state.dfps.service.common.CodesConstant.CCONSTAT_ACT;
import static us.tx.state.dfps.service.common.CodesConstant.CCONUNIT_DA2;
import static us.tx.state.dfps.service.common.CodesConstant.CCONUNIT_ONE; 
import static us.tx.state.dfps.service.common.CodesConstant.CINVUTYP_DA2;
import static us.tx.state.dfps.service.common.CodesConstant.CKNPYELG_NELG;
import static us.tx.state.dfps.service.common.CodesConstant.CNPERIOD_DAY;
import static us.tx.state.dfps.service.common.CodesConstant.CRSCPROG_01;
import static us.tx.state.dfps.service.common.CodesConstant.CSVATYPE_INI;
import static us.tx.state.dfps.service.common.CodesConstant.KIN_HOME_APPROVAL_STATUS;
import static us.tx.state.dfps.service.common.CodesConstant.STRING_Y;
import static us.tx.state.dfps.service.common.ServiceConstants.CEVTSTAT_PEND;
import static us.tx.state.dfps.service.common.ServiceConstants.CHAR_IND_N;
import static us.tx.state.dfps.service.common.ServiceConstants.CHAR_IND_Y;
import static us.tx.state.dfps.service.common.ServiceConstants.CSVCCODE_68O;
import static us.tx.state.dfps.service.common.ServiceConstants.CSVCCODE_68P;
import static us.tx.state.dfps.service.common.ServiceConstants.CSVCCODE_68Q;
import static us.tx.state.dfps.service.common.ServiceConstants.CSVCCODE_68R;
import static us.tx.state.dfps.service.common.ServiceConstants.KINSHIP_AUTOMATED_SYSTEM_ID;
import static us.tx.state.dfps.service.common.ServiceConstants.STRING_IND_N;
import static us.tx.state.dfps.service.common.ServiceConstants.STRING_IND_Y;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apache.log4j.Logger;
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
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import us.tx.state.dfps.common.domain.Approvers;
import us.tx.state.dfps.common.domain.CapsResource;
import us.tx.state.dfps.common.domain.Contract;
import us.tx.state.dfps.common.domain.ContractCounty;
import us.tx.state.dfps.common.domain.ContractPeriod;
import us.tx.state.dfps.common.domain.ContractPeriodId;
import us.tx.state.dfps.common.domain.ContractService;
import us.tx.state.dfps.common.domain.ContractVersion;
import us.tx.state.dfps.common.domain.Event;
import us.tx.state.dfps.common.domain.KinPaymentEligibilityHistory;
import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.common.domain.ResourceAddress;
import us.tx.state.dfps.common.domain.ResourceService;
import us.tx.state.dfps.common.domain.ServiceAuthorization;
import us.tx.state.dfps.common.domain.StagePersonLink;
import us.tx.state.dfps.common.domain.SvcAuthDetail;
import us.tx.state.dfps.phoneticsearch.IIRHelper.DateHelper;
import us.tx.state.dfps.service.admin.dto.EmployeeDto;
import us.tx.state.dfps.service.casepackage.dao.ServiceAuthorizationDao;
import us.tx.state.dfps.service.casepackage.dto.RecordsRetentionDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.kin.dto.KinCaregiverEligibilityDto;
import us.tx.state.dfps.service.kin.dto.KinChildDto;
import us.tx.state.dfps.service.kin.dto.KinHomeInfoDto;
import us.tx.state.dfps.service.kin.dto.KinMonthlyPaymentExtensionDto;
import us.tx.state.dfps.service.kin.dto.KinPaymentEligibilityDto;
import us.tx.state.dfps.service.kin.dto.KinHomeAssessmentDto;
import us.tx.state.dfps.service.kinhomeinfo.dao.KinHomeInfoDao;
import us.tx.state.dfps.service.subcare.dto.ResourceAddressDto;
import us.tx.state.dfps.service.subcare.dto.ResourcePhoneDto;
import us.tx.state.dfps.service.subcare.dto.StgPersonLinkDto;
import us.tx.state.dfps.service.workload.dto.StageDto;
import us.tx.state.dfps.service.workload.dto.TodoDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:Implementation for KinHomeInfoDao May 14, 2018- 09:09:55 AM ©
 * 2018 Texas Department of Family and Protective Services
 */
@Repository
public class KinHomeInfoDaoImpl implements KinHomeInfoDao {

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	private MessageSource messageSource;

	@Value("${KinHomeInfoDaoImpl.getHomeAssessmentApprv}")
	private String getHomeAssessmentApprv;

	@Value("${KinHomeInfoDaoImpl.getFadResourceId}")
	private String getFadResourceId;

	@Value("${KinHomeInfoDaoImpl.getFADstageID}")
	private String getFADstageID;

	@Value("${KinHomeInfoDaoImpl.getPrimKinCareGiver}")
	private String getPrimKinCareGiver;

	@Value("${KinHomeInfoDaoImpl.getHasOpenPlacements}")
	private String getHasOpenPlacements;

	@Value("${KinHomeInfoDaoImpl.getKinHasPrimAddress}")
	private String getKinHasPrimAddress;

	@Value("${KinHomeInfoDaoImpl.getKinHasPrimPhone}")
	private String getKinHasPrimPhone;

	@Value("${KinHomeInfoDaoImpl.getFosterCareRate}")
	private String getFosterCareRateSql;

	@Value("${KinHomeInfoDaoImpl.getResourceServiceExists}")
	private String getResourceServiceExistsSql;

	@Value("${KinHomeInfoDaoImpl.getResourceServiceExistsForCountyRegion}")
	private String getResourceServiceExistsForCountyRegionSql;

	@Value("${KinHomeInfoDaoImpl.getSelectFaIndividualTraining}")
	private String getSelectFaIndividualTrainingSql;

	@Value("${KinHomeInfoDaoImpl.getContractId}")
	private String getContractIdSql;

	@Value("${KinHomeInfoDaoImpl.getContractCountyId}")
	private String getContractCountyIdSql;

	@Value("${KinHomeInfoDaoImpl.getResourceAddressId}")
	private String getResourceAddressIdSql;

	@Value("${KinHomeInfoDaoImpl.updateResourceForEligibility}")
	private String updateResourceForEligibilitySql;

	@Value("${KinHomeInfoDaoImpl.getKinshipStoredValues}")
	private String getKinshipStoredValuesSql;

	@Value("${KinHomeInfoDaoImpl.getPaymentEligibilityHistory}")
	private String getPaymentEligibilityHistorySql;

	@Value("${KinHomeInfoDaoImpl.getPaymentEligibilityHistoryForResource}")
	private String getPaymentEligibilityHistoryForResourceSql;

	@Value("${KinHomeInfoDaoImpl.getKinCareEligibilityHistoryForResource}")
	private String getKinCareEligibilityHistoryForResourceSql;

	@Value("${KinHomeInfoDaoImpl.getLegalStatusInfo}")
	private String getLegalStatusInfoSql;

	@Value("${KinHomeInfoDaoImpl.getChildrenPId}")
	private String getChildrenPIdSql;

	@Value("${KinHomeInfoDaoImpl.getMaxServiceAuthTermDate}")
	private String getMaxServiceAuthTermDateSql;

	@Value("${KinHomeInfoDaoImpl.getContractLineItem}")
	private String getContractLineItemSql;

	@Value("${KinHomeInfoDaoImpl.getSAEventLinkCaseId}")
	private String getSAEventLinkCaseIdSql;

	@Value("${KinHomeInfoDaoImpl.getChildLivingArrangement}")
	private String getChildLivingArrangementSql;

	@Value("${KinHomeInfoDaoImpl.getPaymentStartDate}")
	private String getPaymentStartDateSql;

	@Value("${KinHomeInfoDaoImpl.isStageOpen}")
	private String isStageOpenSql;

	@Value("${KinHomeInfoDaoImpl.getMinPlacementDate}")
	private String getMinPlacementDateSql;

	@Value("${KinHomeInfoDaoImpl.getMonthlyExtnBean}")
	private String getMonthlyExtnBeanSql;

	@Value("${KinHomeInfoDaoImpl.getSubStagePersonId}")
	private String getSubStagePersonIdSql;

	@Value("${KinHomeInfoDaoImpl.updateMonthlyPayment}")
	private String updateMonthlyPaymentSql;

	@Value("${KinHomeInfoDaoImpl.getCaseIdForHomeInfo}")
	private String getCaseIdForHomeInfoSql;

	@Value("${KinHomeInfoDaoImpl.getCdRegion}")
	private String getCdRegionSql;

	@Value("${KinHomeInfoDaoImpl.getLegalRegionFromContract}")
	private String getLegalRegionFromContractSql;

	@Value("${KinHomeInfoDaoImpl.getPlacementInfoForResource}")
	private String getPlacementInfoForResourceSql;

	@Value("${KinHomeInfoDaoImpl.getValidContractCount}")
	private String getValidContractCountSql;

	@Value("${KinHomeInfoDaoImpl.getActiveContract}")
	private String getActiveContractSql;

	@Value("${KinHomeInfoDaoImpl.getContractServiceLineItem}")
	private String getContractServiceLineItemSql;

	@Value("${KinHomeInfoDaoImpl.getSplitContractPeriodNumber}")
	private String getSplitContractPeriodNumberSql;

	@Value("${KinHomeInfoDaoImpl.getActiveContractPeriodNumber}")
	private String getActiveContractPeriodNumberSql;

	@Value("${KinHomeInfoDaoImpl.getValidContractPeriod}")
	private String getValidContractPeriodSql;

	@Value("${KinHomeInfoDaoImpl.getSplitContractVersionNumber}")
	private String getSplitContractVersionNumberSql;

	@Value("${KinHomeInfoDaoImpl.getContractVersionNumber}")
	private String getContractVersionNumberSql;

	@Value("${KinHomeInfoDaoImpl.updateKinHomeStatus}")
	private String updateKinHomeStatusSql;

	@Value("${KinHomeInfoDaoImpl.updateKinHomeInfrmtnStatus}")
	private String updateKinHomeInfrmtnStatusSql;

	@Value("${KinHomeInfoDaoImpl.closeStage}")
	private String closeStageSql;

	@Value("${KinHomeInfoDaoImpl.getPersonInOpenStage}")
	private String getPersonInOpenStageSql;

	@Value("${KinHomeInfoDaoImpl.getSelectOtherOpenStage}")
	private String getSelectOtherOpenStageSql;

	@Value("${KinHomeInfoDaoImpl.updatePersonStatus}")
	private String updatePersonStatusSql;

	@Value("${KinHomeInfoDaoImpl.getCaseClosedDate}")
	private String getCaseClosedDateSql;

	@Value("${KinHomeInfoDaoImpl.getSelectEmployee}")
	private String getSelectEmployeeSql;

	@Value("${KinHomeInfoDaoImpl.getUpdateStagePerson}")
	private String getUpdateStagePersonSql;

	@Value("${KinHomeInfoDaoImpl.deleteStagePersonRecords}")
	private String deleteStagePersonRecordsSql;

	@Value("${KinHomeInfoDaoImpl.updateCase}")
	private String updateCaseSql;

	@Value("${KinHomeInfoDaoImpl.updateSituation}")
	private String updateSituationSql;

	@Value("${KinHomeInfoDaoImpl.getRecordsRetention}")
	private String getRecordsRetentionSql;

	@Value("${KinHomeInfoDaoImpl.updateRecordsRetention}")
	private String updateRecordsRetentionSql;

	@Value("${KinHomeInfoDaoImpl.updateCapsResourceForEligibility}")
	private String updateCapsResourceForEligibilitySql;

	@Value("${KinHomeInfoDaoImpl.getHomeApprovalDetails}")
	private String getHomeApprovalDetailsSql;

	@Value("${KinHomeInfoDaoImpl.getDeclineApprovalDate}")
	private String getDeclineApprovalDateSql;

	@Value("${KinHomeInfoDaoImpl.getHomeAssessmentIntermediateDate}")
	private String getHomeAssessmentIntermediateDateSql;

	@Value("${KinHomeInfoDaoImpl.getUpdateCapsResource}")
	private String getUpdateCapsResourceSql;

	@Value("${KinHomeInfoDaoImpl.getLatestStatusDate}")
	private String getLatestStatusDateSql;

	@Value("${KinHomeInfoDaoImpl.getStatusDate}")
	private String getStatusDateSql;

	private static final Logger LOG = Logger.getLogger("ServiceBusiness-KinHomeInfoDaoImplLog");

	@Autowired
	ServiceAuthorizationDao serviceAuthorizationDao;

	/**
	 * Method Name: getResServiceInfo Method Description:Get Resource Service
	 * Info for given Home Stage ID
	 * 
	 * @param kinHomeInfoDto
	 * @return KinHomeInfoDto
	 * @throws DataNotFoundException
	 */
	@SuppressWarnings("unchecked")
	@Override
	public KinHomeInfoDto getResServiceInfo(KinHomeInfoDto kinHomeInfoDto) throws DataNotFoundException {

		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CapsResource.class);
		criteria.add(Restrictions.eq("stage.idStage", kinHomeInfoDto.getIdHomeStage()));
		List<CapsResource> capsResourceList = criteria.list();
		if (TypeConvUtil.isNullOrEmpty(capsResourceList)) {
			throw new DataNotFoundException(messageSource.getMessage("ResServiceInfo.NotFound", null, Locale.US));
		}
		CapsResource capsResource = capsResourceList.get(ServiceConstants.Zero);
		KinHomeInfoDto homeInfoDto = new KinHomeInfoDto();
		homeInfoDto.setResourceAddressState(capsResource.getCdRsrcState());
		homeInfoDto.setResourceAddressCounty(capsResource.getCdRsrcCnty());
		homeInfoDto.setRegion(capsResource.getCdRsrcRegion());
		homeInfoDto.setResourceSignedAgreement(characterToString(capsResource.getIndSignedAgreement()));
		homeInfoDto.setResourceIncomeQualification(characterToString(capsResource.getIndIncomeQual()));
		homeInfoDto.setResourceClosureReason(capsResource.getCdRsrcClosureRsn());

		return homeInfoDto;
	}

	private String characterToString(String value) {
		if (TypeConvUtil.isNullOrEmpty(value)) {
			return null;
		} else {
			return value.toString();
		}
	}

	public KinHomeInfoDto getKinHomeInfo(Long stageId) throws DataNotFoundException {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(CapsResource.class);
		criteria.add(Restrictions.eq("stage.idStage", stageId));
		CapsResource capsResource = (CapsResource) criteria.uniqueResult();
		if (ObjectUtils.isEmpty(capsResource) ||
				ObjectUtils.isEmpty(capsResource.getIdResource())) {
			throw new DataNotFoundException(
					messageSource.getMessage("record.not.found.capsresource", null, Locale.US));
		}

		KinHomeInfoDto homeInfoDto = new KinHomeInfoDto();

		homeInfoDto.setIdHomeResource(capsResource.getIdResource());
		homeInfoDto.setResourceName(capsResource.getNmResource());
		homeInfoDto.setIdHomeStage(capsResource.getStage().getIdStage());
		homeInfoDto.setIdHomeCase(capsResource.getIdCase());
		homeInfoDto.setResourceNumOfPersons(capsResource.getNbrPersons());
		homeInfoDto.setResourceCategory(capsResource.getCdRsrcCategory());
		homeInfoDto.setHomeStatus(capsResource.getCdRsrcFaHomeStatus());
		homeInfoDto.setResourceEthnicity(capsResource.getCdRsrcEthnicity());
		homeInfoDto.setResourceLanguage(capsResource.getCdRsrcLanguage());
		homeInfoDto.setResourceReligion(capsResource.getCdRsrcReligion());
		homeInfoDto.setResourceAnnualIncome(Double.valueOf(String.valueOf(capsResource.getNbrRsrcAnnualIncome())));
		homeInfoDto.setResourceMaritalStatus(capsResource.getCdRsrcMaritalStatus());
		homeInfoDto.setResourceSourceInquiry(capsResource.getCdRsrcSourceInquiry());
		homeInfoDto.setResourceRelativeCaregiver(capsResource.getIndRelativeCrgvr());
		homeInfoDto.setResourceFictiveCaregiver(capsResource.getIndFictiveCrgvr());
		homeInfoDto.setResourceIncomeQualification(capsResource.getIndIncomeQual());
		homeInfoDto.setResourceManualGiven(capsResource.getIndManualGiven());
		homeInfoDto.setResourceSignedAgreement(capsResource.getIndSignedAgreement());
		homeInfoDto.setResourceAllKinEmployed(capsResource.getIndAllKinEmployed());
		homeInfoDto.setResourceAddressStreetLine1(capsResource.getAddrRsrcStLn1());
		homeInfoDto.setResourceAddressCity(capsResource.getAddrRsrcCity());
		homeInfoDto.setResourceAddressState(capsResource.getCdRsrcState());
		homeInfoDto.setResourceAddressZip(capsResource.getAddrRsrcZip());
		homeInfoDto.setResourcePhoneNumber(capsResource.getNbrRsrcPhn());
		homeInfoDto.setResourcePhoneExtension(capsResource.getNbrRsrcPhoneExt());
		homeInfoDto.setResourceRegion(capsResource.getCdRsrcRegion());
		homeInfoDto.setResourceAddressCounty(capsResource.getCdRsrcCnty());
		homeInfoDto.setResourceClosureReason(capsResource.getCdRsrcClosureRsn());
		homeInfoDto.setResourceRecommendReopening(capsResource.getCdRsrcRecmndReopen());
		homeInfoDto.setHasBegunTraining(capsResource.getHasBegunTraining());
		homeInfoDto.setAssessmentApprovedDate(capsResource.getAssessmentApprovedDate());
		homeInfoDto.setIsPlacmentCourtOrdered(capsResource.getIsPlacementCourtOrdered());
		homeInfoDto.setAgreementSignedDate(capsResource.getAgreementSignedDate());
		homeInfoDto.setIsPaymentCourtOrdered(capsResource.getIsPaymentCourtOrdered());
		homeInfoDto.setIsHouseholdMeetFPL(capsResource.getIsHouseholdMeetFPL());
		homeInfoDto.setIsCaregiverMaternal(capsResource.getIsCaregiverMaternal());
		homeInfoDto.setIsCaregiverPaternal(capsResource.getIsCaregiverPaternal());
		homeInfoDto.setPaymentStartDate(capsResource.getPaymentStartDate());
		homeInfoDto.setKinCaregiverEligStatusCode(capsResource.getKinCaregiverEligStatusCode());
		homeInfoDto.setKinCaregiverPaymentEligStatusCode(capsResource.getKinCaregiverPaymentEligStatusCode());
		homeInfoDto.setAssessmentStatusCode(capsResource.getAssessmentStatusCode());
		homeInfoDto.setFplAmount(capsResource.getFplAmount());
		homeInfoDto.setCourtOrderedPlacementDate(capsResource.getCourtOrderedPlacementDate());
		homeInfoDto.setCourtOrderedPaymentDate(capsResource.getCourtOrderedPaymentDate());

		List<ResourceAddressDto> resourceAddresses = new ArrayList<ResourceAddressDto>();
		capsResource.getResourceAddresses().stream()
				.forEach(resourceAddress -> {
					ResourceAddressDto resourceAddressDto = new ResourceAddressDto();
					BeanUtils.copyProperties(resourceAddress, resourceAddressDto);
					resourceAddressDto.setIdResource(resourceAddress.getCapsResource().getIdResource());
					resourceAddresses.add(resourceAddressDto);
				});
		homeInfoDto.getResourceAddresses().addAll(resourceAddresses);

		List<ResourcePhoneDto> resourcePhones = new ArrayList<ResourcePhoneDto>();
		capsResource.getResourcePhones().stream()
				.forEach(resourcePhone -> {
					ResourcePhoneDto resourcePhoneDto = new ResourcePhoneDto();
					BeanUtils.copyProperties(resourcePhone, resourcePhoneDto);
					resourcePhoneDto.setIdResource(resourcePhone.getCapsResource().getIdResource());
					resourcePhones.add(resourcePhoneDto);
				});
		homeInfoDto.getResourcePhones().addAll(resourcePhones);

		homeInfoDto.getKinCaregiverEligibilityList().addAll(getKinCareEligibilityHistoryForResource(capsResource.getIdResource()));

		homeInfoDto.getKinPaymentEligibilityList().addAll(getPaymentEligibilityHistoryForResource(capsResource.getIdResource()));

		if (!CollectionUtils.isEmpty(capsResource.getContracts())) {
			Contract contract = capsResource.getContracts().stream()
					.filter(e -> !ObjectUtils.isEmpty(e.getIdContract()))
					.findFirst().orElse(null);
			if (!ObjectUtils.isEmpty(contract)) {
				boolean contractRegMatch = true;

				homeInfoDto.setIndContractExist(true);
				homeInfoDto = getContractValidationFlag(homeInfoDto, contract.getIdContract());

				String legalCounty = getLegalRegion(capsResource.getIdResource());
				List<String>  legalCountyFromContractList = getLegalRegionFromContract(capsResource.getIdResource(), contract.getIdContract());
				String legalCountyFromContract = null;
				if(!CollectionUtils.isEmpty(legalCountyFromContractList)){
					legalCountyFromContract = legalCountyFromContractList.get(0);
				}

				if (!ObjectUtils.isEmpty(legalCounty)) {
					contractRegMatch = legalCounty.equalsIgnoreCase(legalCountyFromContract);
				}
				if (!ObjectUtils.isEmpty(legalCounty) && !contractRegMatch) {
					homeInfoDto.setIndLegalCountyMatch(false);
				} else {
					homeInfoDto.setIndLegalCountyMatch(true);
				}
			} else {
				homeInfoDto.setIndContractExist(false);
				homeInfoDto.setIndLegalCountyMatch(true);
			}
		} else {
			homeInfoDto.setIndContractExist(false);
			homeInfoDto.setIndLegalCountyMatch(true);
		}

		return homeInfoDto;
	}

	public String getLegalRegion(Long resourceId){
		String cdRegion = null;

		List<KinChildDto> placementInfoList =  getPlacementInfoList(resourceId);

		if (!CollectionUtils.isEmpty(placementInfoList)) {
			Long childId = placementInfoList.get(0).getChildId();

			if (!ObjectUtils.isEmpty(childId)){
				List <String> regions = getCdRegion(childId);
				if(!CollectionUtils.isEmpty(regions)){
					cdRegion = regions.get(0);
				}
			}
		}

		return cdRegion;
	}

	public List <KinChildDto> getPlacementInfoList(Long resourceId) {
		List<KinChildDto> kinChildDtos = new ArrayList<KinChildDto>();
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getPlacementInfoForResourceSql)
				.addScalar("childId", StandardBasicTypes.LONG)
				.addScalar("caseId", StandardBasicTypes.LONG)
				.addScalar("livingArrangement", StandardBasicTypes.STRING)
				.addScalar("placementEventId", StandardBasicTypes.LONG)
				.addScalar("placementTypeCode", StandardBasicTypes.STRING)
				.addScalar("placementStartDate", StandardBasicTypes.DATE)
				.addScalar("placementEndDate", StandardBasicTypes.DATE)
				.addScalar("birthDate", StandardBasicTypes.DATE)
				.addScalar("dateOfDeath", StandardBasicTypes.DATE)
				.addScalar("childFullName", StandardBasicTypes.STRING)
				.setParameter("resourceId", resourceId)
				.setResultTransformer(Transformers.aliasToBean(KinChildDto.class));
		kinChildDtos.addAll(query.list());

		kinChildDtos.stream().forEach(kinChildDto -> kinChildDto.setAge(DateHelper.getAge(kinChildDto.getBirthDate())));

		return kinChildDtos;
	}

	public List<String> getCdRegion(Long childId) {
		if (!ObjectUtils.isEmpty(childId)) {
			Query querySql = ((Query) sessionFactory.getCurrentSession()
					.createSQLQuery(getCdRegionSql)
					.setParameter("childId", childId));
			return (List<String>) querySql.list();
		}
		return null;
	}

	public List<String> getLegalRegionFromContract(Long resourceId, Long contractId) {
		if (!ObjectUtils.isEmpty(contractId) && !ObjectUtils.isEmpty(resourceId)) {
			Query querySql = ((Query) sessionFactory.getCurrentSession()
					.createSQLQuery(getLegalRegionFromContractSql)
					.setParameter("contractId", contractId)
					.setParameter("resourceId", resourceId));
			return (List<String>) querySql.list();
		}
		return null;
	}

	public double getFosterCareRate(String serviceCode, Date paymentStartDate) {
		Object rate = null;
		if (!ObjectUtils.isEmpty(serviceCode)) {
			Query queryFosterCareRateSql = ((Query) sessionFactory.getCurrentSession()
                    .createSQLQuery(getFosterCareRateSql).setParameter("serviceCode", serviceCode)
                    .setParameter("paymentStartDate", paymentStartDate)
                    .setParameter("paymentStartDate", paymentStartDate));
			rate = queryFosterCareRateSql.uniqueResult();
		}
		return ObjectUtils.isEmpty(rate) ? 0.0 : ((BigDecimal) rate).doubleValue();
	}

	public boolean getIsResourceExists(Long resourceId, String resourceServiceCode) {
		Query query = ((Query) sessionFactory.getCurrentSession()
				.createSQLQuery(getResourceServiceExistsSql)
				.setParameter("resourceId", resourceId)
				.setParameter("resourceServiceCode", resourceServiceCode));
		return CHAR_IND_Y == (char) query.uniqueResult();
	}

	public boolean getResourceService(Long resourceId, String countyCode, String regionCode,
														   String serviceCode, String state) {
		Query query = ((Query) sessionFactory.getCurrentSession()
				.createSQLQuery(getResourceServiceExistsForCountyRegionSql)
				.setParameter("resourceId", resourceId)
				.setParameter("countyCode", countyCode)
				.setParameter("regionCode", regionCode)
				.setParameter("serviceCode", serviceCode)
				.setParameter("state", state));

		return CHAR_IND_Y == (char) query.uniqueResult();
	}

	public String getKinTrainCompleted(Long stageId) {
		if (!ObjectUtils.isEmpty(stageId)) {
			Query queryResourceServiceExistsSql = ((Query) sessionFactory.getCurrentSession()
					.createSQLQuery(getSelectFaIndividualTrainingSql)
					.addScalar("indAllKinCompleted", StandardBasicTypes.STRING)
					.setParameter("stageId", stageId));
			List<String> kinTrainingCompleted = (List<String>) queryResourceServiceExistsSql.list();
			if (!CollectionUtils.isEmpty(kinTrainingCompleted)) {
				return kinTrainingCompleted.get(0);
			}
		}
		return STRING_IND_N;
	}

	public Long getContractId(Long resourceId) {
		if (!ObjectUtils.isEmpty(resourceId)) {
			SQLQuery sqlQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getContractIdSql)
					.addScalar("contractId", StandardBasicTypes.LONG)
					.setParameter("resourceId", resourceId);
			List<Long> contractIdList = (List<Long>) sqlQuery.list();
			if (!CollectionUtils.isEmpty(contractIdList)) {
				return contractIdList.get(0);
			}
		}
		return null;
	}

	public boolean getContractCountyId(Long contractId, int numContractPeriod, int contractVersionId,
									   String serviceCode, String countryCode, int lineIem) {
		Query query = ((Query) sessionFactory.getCurrentSession()
				.createSQLQuery(getContractCountyIdSql)
				.setParameter("contractId", contractId)
				.setParameter("numContractPeriod", numContractPeriod)
				.setParameter("contractVersionId", contractVersionId)
				.setParameter("serviceCode", serviceCode)
				.setParameter("countryCode", countryCode)
				.setParameter("lineIem", lineIem));
		return CHAR_IND_Y == (char) query.uniqueResult();
	}

	public int getLineItemNumber(Long contractId, int numContractPeriod, int contractVersionId,
								 String serviceCode, String paymentType, String unitType) {
		List<Integer> lineItemNumberList = null;
		SQLQuery sqlQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getContractServiceLineItemSql)
				.addScalar("contractServiceLineItem", StandardBasicTypes.INTEGER)
				.setParameter("contractId", contractId)
				.setParameter("numContractPeriod", numContractPeriod)
				.setParameter("contractVersionId", contractVersionId)
				.setParameter("serviceCode", serviceCode)
				.setParameter("paymentType", paymentType)
				.setParameter("unitType", unitType);
		if (!CollectionUtils.isEmpty(sqlQuery.list())) {
			lineItemNumberList = (List<Integer>) sqlQuery.list();
		}
		return !CollectionUtils.isEmpty(lineItemNumberList) ? lineItemNumberList.get(0) : 0;
	}

	public Long getResourceAddressId(Long resourceId) {
		if (!ObjectUtils.isEmpty(resourceId)) {
			SQLQuery sqlQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getResourceAddressIdSql)
					.addScalar("addressId", StandardBasicTypes.LONG)
					.setParameter("resourceId", resourceId);

			return (Long) sqlQuery.uniqueResult();
		}
		return null;
	}

	public int updateResourceForEligibility(KinHomeInfoDto homeInfoDto) {
		Query queryUpdate = sessionFactory.getCurrentSession().createSQLQuery(updateResourceForEligibilitySql);
		queryUpdate.setString("paymentEligibilityStatusCode", homeInfoDto.getKinCaregiverPaymentEligStatusCode());
		queryUpdate.setDate("paymentEligibilityStartDate", homeInfoDto.getPaymentStartDate());
		queryUpdate.setLong("resourceId", homeInfoDto.getIdHomeResource());
		queryUpdate.setString("personFullName", homeInfoDto.getPersonFullName());

		return queryUpdate.executeUpdate();
	}

	public String getKinshipConstants(String name) {
		if (!ObjectUtils.isEmpty(name)) {
			Query querySql = ((Query) sessionFactory.getCurrentSession()
					.createSQLQuery(getKinshipStoredValuesSql)
					.setParameter("name", name));
			return (String) querySql.uniqueResult();
		}
		return null;
	}

	public KinPaymentEligibilityDto getPaymentEligibilityHistory(Long resourceId, Long placementEventId) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getPaymentEligibilityHistorySql)
				.addScalar("idPaymentHistory", StandardBasicTypes.LONG)
				.addScalar("indCourtOrdedPayment", StandardBasicTypes.STRING)
				.addScalar("dtEligPaymntStart", StandardBasicTypes.DATE)
				.addScalar("indIncomeQual", StandardBasicTypes.STRING)
				.addScalar("cdPaymentEligStatus", StandardBasicTypes.STRING)
				.addScalar("idResource", StandardBasicTypes.LONG)
				.addScalar("createdDate", StandardBasicTypes.DATE)
				.addScalar("idCreatedPerson", StandardBasicTypes.LONG)
				.addScalar("dtLastUpdate", StandardBasicTypes.DATE)
				.addScalar("lastUpdatedPerson", StandardBasicTypes.LONG)
				.addScalar("childAge", StandardBasicTypes.INTEGER)
				.addScalar("indSignedAgrmnt", StandardBasicTypes.STRING)
				.addScalar("indBegunTraining", StandardBasicTypes.STRING)
				.addScalar("idPlcmentEvent", StandardBasicTypes.LONG)
				.addScalar("dtSignedAgrmnt", StandardBasicTypes.DATE)
				.addScalar("dtCourtOrdrdPymnt", StandardBasicTypes.DATE)
				.addScalar("childFullName", StandardBasicTypes.STRING)
				.addScalar("cdLegalStatus", StandardBasicTypes.STRING)
				.addScalar("updatedBy", StandardBasicTypes.STRING)
				.setParameter("resourceId", resourceId)
				.setParameter("placementEventId", placementEventId)
				.setResultTransformer(Transformers.aliasToBean(KinPaymentEligibilityDto.class));

		return (KinPaymentEligibilityDto) query.uniqueResult();
	}

	public List<KinPaymentEligibilityDto> getPaymentEligibilityHistoryForResource(Long resourceId) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getPaymentEligibilityHistoryForResourceSql)
				.addScalar("idPaymentHistory", StandardBasicTypes.LONG)
				.addScalar("indCourtOrdedPayment", StandardBasicTypes.STRING)
				.addScalar("dtEligPaymntStart", StandardBasicTypes.DATE)
				.addScalar("indIncomeQual", StandardBasicTypes.STRING)
				.addScalar("cdPaymentEligStatus", StandardBasicTypes.STRING)
				.addScalar("idResource", StandardBasicTypes.LONG)
				.addScalar("createdDate", StandardBasicTypes.DATE)
				.addScalar("idCreatedPerson", StandardBasicTypes.LONG)
				.addScalar("dtLastUpdate", StandardBasicTypes.DATE)
				.addScalar("lastUpdatedPerson", StandardBasicTypes.LONG)
				.addScalar("childAge", StandardBasicTypes.INTEGER)
				.addScalar("indSignedAgrmnt", StandardBasicTypes.STRING)
				.addScalar("indBegunTraining", StandardBasicTypes.STRING)
				.addScalar("idPlcmentEvent", StandardBasicTypes.LONG)
				.addScalar("dtSignedAgrmnt", StandardBasicTypes.DATE)
				.addScalar("dtCourtOrdrdPymnt", StandardBasicTypes.DATE)
				.addScalar("childFullName", StandardBasicTypes.STRING)
				.addScalar("cdLegalStatus", StandardBasicTypes.STRING)
				.addScalar("updatedBy", StandardBasicTypes.STRING)
				.setParameter("resourceId", resourceId)
				.setResultTransformer(Transformers.aliasToBean(KinPaymentEligibilityDto.class));

		return (List<KinPaymentEligibilityDto>) query.list();
	}

	public List<KinCaregiverEligibilityDto> getKinCareEligibilityHistoryForResource(Long resourceId) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getKinCareEligibilityHistoryForResourceSql)
				.addScalar("idKinEligHist", StandardBasicTypes.LONG)
				.addScalar("isPlacmentCourtOrdered", StandardBasicTypes.STRING)
				.addScalar("cdAssessmentStatus", StandardBasicTypes.STRING)
				.addScalar("cdKinEligStatus", StandardBasicTypes.STRING)
				.addScalar("createdDate", StandardBasicTypes.DATE)
				.addScalar("createdBy", StandardBasicTypes.STRING)
				.addScalar("idResource", StandardBasicTypes.LONG)
				.addScalar("hasApprovedPlacement", StandardBasicTypes.STRING)
				.addScalar("cdAssessmentStatus", StandardBasicTypes.STRING)
				.addScalar("homeAprvDate", StandardBasicTypes.DATE)
				.addScalar("dtPlacementCourtOrdrd", StandardBasicTypes.DATE)
				.setParameter("resourceId", resourceId)
				.setResultTransformer(Transformers.aliasToBean(KinCaregiverEligibilityDto.class));

		return (List<KinCaregiverEligibilityDto>) query.list();
	}

	public List<KinChildDto>  getLegalStatusInfo(Long personId) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getLegalStatusInfoSql)
				.addScalar("legalStatusStatDate", StandardBasicTypes.DATE)
				.addScalar("cdLegalStatus", StandardBasicTypes.STRING)
				.setParameter("personId", personId)
				.setResultTransformer(Transformers.aliasToBean(KinChildDto.class));

		return (List<KinChildDto>)query.list();
	}

	public List<Long> getChildrensPid(Long resourceId) {
		return (List<Long>) sessionFactory.getCurrentSession().createSQLQuery(getChildrenPIdSql)
				.addScalar("placementChildId", StandardBasicTypes.LONG)
				.setParameter("resourceId", resourceId).list();
	}

	public Date getMaxServiceAuthTermDate(Long childId, Date legalStartDate) {
		Query querySql = ((Query) sessionFactory.getCurrentSession()
				.createSQLQuery(getMaxServiceAuthTermDateSql)
				.addScalar("DT_SVC_AUTH_DTL_TERM", StandardBasicTypes.DATE)
				.setParameter("legalStartDate", legalStartDate)
				.setParameter("childId", childId));
		return (Date) querySql.uniqueResult();
	}

	//artf249579: ALM ID : 22068 : Line Item should be based on Payment start date/Ext Req start date.
	public int getContractLineItem(Long contractId, String serviceCode, Date paymentStartDate) {
		int lineItem = 0;
		Query query = ((Query) sessionFactory.getCurrentSession().createSQLQuery(getContractLineItemSql)
				.addScalar("LINE_ITEM", StandardBasicTypes.INTEGER)
				.setParameter("contractId", contractId)
				.setParameter("serviceCode", serviceCode)
				.setParameter("paymentStartDate", paymentStartDate));
		if (null != query.uniqueResult()) {
			lineItem = (Integer) query.uniqueResult();
		}
		return lineItem;
	}

	public Long getSAEventLinkCaseId(Long serviceAuthId) {
		Long caseId = 0L;
		Query query = ((Query) sessionFactory.getCurrentSession().createSQLQuery(getSAEventLinkCaseIdSql)
				.addScalar("ID_CASE", StandardBasicTypes.LONG)
				.setParameter("serviceAuthId", serviceAuthId));
		if(!ObjectUtils.isEmpty(query.list())){
			caseId = (Long) query.list().get(0);
		}
		return caseId;
	}

	public String getChildLivingArrangement(Long childId) {
		Query querySql = ((Query) sessionFactory.getCurrentSession()
				.createSQLQuery(getChildLivingArrangementSql)
				.setParameter("childId", childId));
		return (String) querySql.uniqueResult();
	}

	public Date getPaymentStartDate(Long placementEventId, Long resourceId) {
		Query querySql = ((Query) sessionFactory.getCurrentSession()
				.createSQLQuery(getPaymentStartDateSql)
				.setParameter("resourceId", resourceId)
				.setParameter("placementEventId", placementEventId));
		return (Date) querySql.uniqueResult();
	}

	public boolean isStageOpen(Long serviceAuthId) {
		Query query = ((Query) sessionFactory.getCurrentSession().createSQLQuery(isStageOpenSql)
				.setParameter("serviceAuthId", serviceAuthId));
		return 'Y' == (char)query.uniqueResult();
	}

	public Date getMinPlacementDate(Long resourceId) {
		Query querySql = ((Query) sessionFactory.getCurrentSession()
				.createSQLQuery(getMinPlacementDateSql)
				.setParameter("resourceId", resourceId));
		return (Date) querySql.uniqueResult();

	}

	public Long addContractService(KinHomeInfoDto homeInfoDto, Long contractId, KinHomeInfoDto savedBean,
								  String paymentType, String unitType, String serviceCode,
								   int lineItem, Double rate) {
		Date today = Calendar.getInstance().getTime();
		ContractService contractService = new ContractService();

		contractService.setDtLastUpdate(today);
		contractService.setCreatedPersonId(Long.valueOf(String.valueOf(KINSHIP_AUTOMATED_SYSTEM_ID)));
		contractService.setDtLastUpdate(today);
		contractService.setLastUpdatedPersonId(Long.valueOf(String.valueOf(KINSHIP_AUTOMATED_SYSTEM_ID)));
		contractService.setIdContract(contractId);

		Person person = (Person) sessionFactory.getCurrentSession().get(Person.class,
				homeInfoDto.getUserId());
		if (TypeConvUtil.isNullOrEmpty(person)) {
			throw new DataNotFoundException(
					messageSource.getMessage("record.not.found.person", null, Locale.US));
		}
		contractService.setPerson(person);

		contractService.setCdCnsvcService(serviceCode);
		contractService.setCdCnsvcPaymentType(paymentType);
		contractService.setCdCnsvcUnitType(unitType);
		contractService.setNbrCnsvcUnitRate(BigDecimal.valueOf(rate));
		contractService.setNbrCnsvcPeriod(BYTE_ONE);
		contractService.setNbrCnsvcVersion(BYTE_ONE);
		contractService.setNbrCnsvcLineItem(new Byte(String.valueOf(lineItem)));
		contractService.setIndCnsvcNewRow(CHAR_IND_N);

		sessionFactory.getCurrentSession().save(contractService);
		return contractService.getIdCnsvc();
	}

	public Long addResourceService(KinHomeInfoDto homeInfoDto, String serviceCode,
								   KinHomeInfoDto khVb, String indTrain) {
		Date today = Calendar.getInstance().getTime();

		ResourceService resourceService = new ResourceService();
		CapsResource capsResource = (CapsResource) sessionFactory.getCurrentSession().get(CapsResource.class,
				homeInfoDto.getIdHomeResource());
		if (TypeConvUtil.isNullOrEmpty(capsResource)) {
			throw new DataNotFoundException(
					messageSource.getMessage("record.not.found.capsresource", null, Locale.US));
		}
		resourceService.setCapsResource(capsResource);

		resourceService.setCdRsrcSvcCategRsrc(CATOFSVC_24);
		resourceService.setIndRsrcSvcShowRow(STRING_IND_Y);
		resourceService.setIndRsrcSvcIncomeBsed(STRING_IND_N);
		resourceService.setCdRsrcSvcCnty(khVb.getResourceAddressCounty());
		resourceService.setCdRsrcSvcProgram(CRSCPROG_01);
		resourceService.setCdRsrcSvcRegion(khVb.getResourceRegion());
		resourceService.setCdRsrcSvcService(serviceCode);
		resourceService.setCdRsrcSvcState(khVb.getResourceAddressState());
		resourceService.setIndRsrcSvcCntyPartial(STRING_IND_N);

		resourceService.setIndKnshpTraining(ObjectUtils.isEmpty(indTrain) ? null : (
				STRING_IND_Y.equals(indTrain) ? CHAR_IND_Y : CHAR_IND_N));

		resourceService.setIndKnshpHomeAssmnt(CHAR_IND_Y);

		resourceService.setIndKnshpIncome(ObjectUtils.isEmpty(homeInfoDto.getResourceIncomeQualification()) ? null : (
				STRING_IND_Y.equals(homeInfoDto.getResourceIncomeQualification()) ? CHAR_IND_Y : CHAR_IND_N));

		resourceService.setIndKnshpAgreement(ObjectUtils.isEmpty(homeInfoDto.getResourceSignedAgreement()) ? null : (
				STRING_IND_Y.equals(homeInfoDto.getResourceSignedAgreement()) ? CHAR_IND_Y : CHAR_IND_N));

		resourceService.setDtLastUpdate(today);
		resourceService.setCreatedPersonId(Long.valueOf(String.valueOf(KINSHIP_AUTOMATED_SYSTEM_ID)));
		resourceService.setCreatedDate(today);
		resourceService.setLastUpdatedPersonId(Long.valueOf(String.valueOf(KINSHIP_AUTOMATED_SYSTEM_ID)));

		sessionFactory.getCurrentSession().save(resourceService);
		return resourceService.getIdResourceService();
	}

	public Long addContract(KinHomeInfoDto homeInfoDto, KinHomeInfoDto savedBean, Long resourceAddressId) {
		Date today = Calendar.getInstance().getTime();

		Contract contract = new Contract();

		Person person = (Person) sessionFactory.getCurrentSession().get(Person.class,
				homeInfoDto.getUserId());
		if (TypeConvUtil.isNullOrEmpty(person)) {
			throw new DataNotFoundException(
					messageSource.getMessage("record.not.found.person", null, Locale.US));
		}
		contract.setPersonByIdCntrctWkr(person);
		contract.setPersonByIdCntrctManager(person);

		CapsResource capsResource = (CapsResource) sessionFactory.getCurrentSession().get(CapsResource.class,
				homeInfoDto.getIdHomeResource());
		if (TypeConvUtil.isNullOrEmpty(capsResource)) {
			throw new DataNotFoundException(
					messageSource.getMessage("record.not.found.capsresource", null, Locale.US));
		}
		contract.setCapsResource(capsResource);

		ResourceAddress resourceAddress = (ResourceAddress) sessionFactory.getCurrentSession().get(ResourceAddress.class,
				resourceAddressId);
		if (TypeConvUtil.isNullOrEmpty(resourceAddress)) {
			throw new DataNotFoundException(
					messageSource.getMessage("record.not.found.address", null, Locale.US));
		}
		contract.setResourceAddress(resourceAddress);

		contract.setCdCntrctFuncType(CCONFUNC_CPS);
		contract.setCdCntrctProgramType(CCONPROG_CPS);
		contract.setCdCntrctProcureType(CCONPROC_NCN);
		contract.setCdCntrctRegion(savedBean.getResourceRegion());
		contract.setDtLastUpdate(today);
		contract.setCreatedPersonId(Long.valueOf(String.valueOf(KINSHIP_AUTOMATED_SYSTEM_ID)));
		contract.setCreatedDate(today);
		contract.setLastUpdatedPersonId(Long.valueOf(String.valueOf(KINSHIP_AUTOMATED_SYSTEM_ID)));
		contract.setIndCntrctBudgLimit(STRING_IND_N);

		sessionFactory.getCurrentSession().save(contract);
		return contract.getIdContract();
	}

	public Long addContractCounty(KinHomeInfoDto homeInfoDto, Long contractId,
								  KinHomeInfoDto savedBean, String serviceCode, int lineItem) {
		Date today = Calendar.getInstance().getTime();
		Date termDate = null;
		if(homeInfoDto.getPaymentStartDate() != null){
			termDate = DateHelper.addToDate(homeInfoDto.getPaymentStartDate(),5,0,0);
		}
		ContractCounty contractCounty = new ContractCounty();
		contractCounty.setDtLastUpdate(today);
		contractCounty.setCreatedPersonId(Long.valueOf(String.valueOf(KINSHIP_AUTOMATED_SYSTEM_ID)));
		contractCounty.setCreatedDate(today);
		contractCounty.setLastUpdatedPersonId(Long.valueOf(String.valueOf(KINSHIP_AUTOMATED_SYSTEM_ID)));
		contractCounty.setIdContract(contractId);
		contractCounty.setNbrCncntyPeriod(BYTE_ONE);
		contractCounty.setNbrCncntyVersion(BYTE_ONE);
		contractCounty.setNbrCncntyLineItem(Byte.valueOf(String.valueOf(lineItem)));
		contractCounty.setCdCncntyCounty(savedBean.getResourceAddressCounty());

		Person person = (Person) sessionFactory.getCurrentSession().get(Person.class,
				homeInfoDto.getUserId());
		if (TypeConvUtil.isNullOrEmpty(person)) {
			throw new DataNotFoundException(
					messageSource.getMessage("record.not.found.person", null, Locale.US));
		}
		contractCounty.setPerson(person);

		CapsResource capsResource = (CapsResource) sessionFactory.getCurrentSession().get(CapsResource.class,
				savedBean.getIdHomeResource());
		if (TypeConvUtil.isNullOrEmpty(capsResource)) {
			throw new DataNotFoundException(
					messageSource.getMessage("record.not.found.capsresource", null, Locale.US));
		}
		contractCounty.setCapsResource(capsResource);

		contractCounty.setCdCncntyService(serviceCode);
		contractCounty.setDtCncntyEffective(homeInfoDto.getPaymentStartDate());
		contractCounty.setDtCncntyEnd(termDate);

		sessionFactory.getCurrentSession().save(contractCounty);
		return contractCounty.getIdCncnty();
	}

	public void addContractPeriod(KinHomeInfoDto homeInfoDto, Long contractId) {
		Date today = Calendar.getInstance().getTime();
		Date termDate = null;
		if(homeInfoDto.getPaymentStartDate() != null){
			termDate = DateHelper.addToDate(homeInfoDto.getPaymentStartDate(),5,0,0);
		}
		ContractPeriod contractPeriod = new ContractPeriod();
		ContractPeriodId contractPeriodId = new ContractPeriodId(contractId, BYTE_ONE);
		contractPeriod.setId(contractPeriodId);

		Person person = (Person) sessionFactory.getCurrentSession().get(Person.class,
				homeInfoDto.getUserId());
		if (TypeConvUtil.isNullOrEmpty(person)) {
			throw new DataNotFoundException(
					messageSource.getMessage("record.not.found.person", null, Locale.US));
		}
		contractPeriod.setPerson(person);

		contractPeriod.setCdCnperStatus(CCONSTAT_ACT);
		contractPeriod.setDtCnperStart(homeInfoDto.getPaymentStartDate());
		contractPeriod.setDtCnperTerm(termDate);
		contractPeriod.setDtCnperClosure(termDate);
		contractPeriod.setIndCnperSigned(CHAR_IND_Y);
		contractPeriod.setCreatedPersonId(Long.valueOf(String.valueOf(KINSHIP_AUTOMATED_SYSTEM_ID)));
		contractPeriod.setCreatedDate(today);
		contractPeriod.setLastUpdatedPersonId(Long.valueOf(String.valueOf(KINSHIP_AUTOMATED_SYSTEM_ID)));
		contractPeriod.setDtLastUpdate(today);

		sessionFactory.getCurrentSession().save(contractPeriod);
	}

	public Long addContractVersion(KinHomeInfoDto homeInfoDto, Long contractId) {
		Date today = Calendar.getInstance().getTime();
		Date termDate = null;
		if( homeInfoDto.getPaymentStartDate() != null){
			termDate =	DateHelper.addToDate(homeInfoDto.getPaymentStartDate(),5,0,0);
		}
		ContractVersion contractVersion = new ContractVersion();

		ContractPeriod contractPeriod = (ContractPeriod) sessionFactory.getCurrentSession()
				.createCriteria(ContractPeriod.class, "contractPeriod")
				.add(Restrictions.eq("id.idContract", contractId))
				.add(Restrictions.eq("id.nbrCnperPeriod", BYTE_ONE)).uniqueResult();

		if (TypeConvUtil.isNullOrEmpty(contractPeriod)) {
			throw new DataNotFoundException(
					messageSource.getMessage("contractPeriod.not.found.attributes", null, Locale.US));
		}
		contractVersion.setContractPeriod(contractPeriod);

		Person person = (Person) sessionFactory.getCurrentSession().get(Person.class,
				homeInfoDto.getUserId());
		if (TypeConvUtil.isNullOrEmpty(person)) {
			throw new DataNotFoundException(
					messageSource.getMessage("record.not.found.person", null, Locale.US));
		}
		contractVersion.setPerson(person);

		contractVersion.setIndCnverVerLock(CHAR_IND_Y);
		contractVersion.setNbrCnverVersion(BYTE_ONE);
		contractVersion.setDtCnverCreate(today);
		contractVersion.setDtCnverEffective(homeInfoDto.getPaymentStartDate());
		contractVersion.setDtCnverEnd(termDate);
		contractVersion.setDtLastUpdate(today);
		contractVersion.setCreatedPersonId(Long.valueOf(String.valueOf(KINSHIP_AUTOMATED_SYSTEM_ID)));
		contractVersion.setCreatedDate(today);
		contractVersion.setLastUpdatedPersonId(Long.valueOf(String.valueOf(KINSHIP_AUTOMATED_SYSTEM_ID)));
		contractVersion.setTxtCnverComment("A new period has been added. KIN auto-created");

		sessionFactory.getCurrentSession().save(contractVersion);

		return contractVersion.getIdCnver();
	}

	public Long saveKinPaymentHistory (KinHomeInfoDto homeInfoDto, KinChildDto childBean, KinPaymentEligibilityDto savedPayBean) {
		Date today = Calendar.getInstance().getTime();
		KinPaymentEligibilityHistory kinPaymentEligibilityHistory = new KinPaymentEligibilityHistory();

		if (!ObjectUtils.isEmpty(savedPayBean) && !ObjectUtils.isEmpty(savedPayBean.getIdPaymentHistory())) {
			Criteria criteria = sessionFactory.getCurrentSession().createCriteria(KinPaymentEligibilityHistory.class);
			criteria.add(Restrictions.eq("id", Long.valueOf(savedPayBean.getIdPaymentHistory())));

			kinPaymentEligibilityHistory = (KinPaymentEligibilityHistory) criteria.uniqueResult();
			if (TypeConvUtil.isNullOrEmpty(kinPaymentEligibilityHistory)) {
				throw new DataNotFoundException(messageSource.getMessage("Common.noRecordFound", null, Locale.US));
			}
		} else {
			kinPaymentEligibilityHistory.setCreatedDate(today);
			kinPaymentEligibilityHistory.setCreatedPersonId(homeInfoDto.getUserId());
		}

		kinPaymentEligibilityHistory.setLastUpdatedDate(today);
		kinPaymentEligibilityHistory.setLastUpdatedPersonId(homeInfoDto.getUserId());
		kinPaymentEligibilityHistory.setCourtOrderedPayment(homeInfoDto.getIsPaymentCourtOrdered());
		kinPaymentEligibilityHistory.setCourtOrderedPaymentStartDate(childBean.getPaymentEligibilityStartDate());
		kinPaymentEligibilityHistory.setIncomeQualified(homeInfoDto.getIsHouseholdMeetFPL());
		kinPaymentEligibilityHistory.setPaymentEligibilityStatusCode(childBean.getPaymentEligibilityStatus());
		kinPaymentEligibilityHistory.setResourceId(homeInfoDto.getIdHomeResource());
		kinPaymentEligibilityHistory.setChildAge(childBean.getAge());
		kinPaymentEligibilityHistory.setSignedAgreement(homeInfoDto.getResourceSignedAgreement());
		kinPaymentEligibilityHistory.setTrainingBegun(homeInfoDto.getHasBegunTraining());

		kinPaymentEligibilityHistory.setPlacementEventId(childBean.getPlacementEventId());
		Event event = (Event) sessionFactory.getCurrentSession().get(Event.class, childBean.getPlacementEventId());
		if (TypeConvUtil.isNullOrEmpty(event)) {
			throw new DataNotFoundException(
					messageSource.getMessage("record.not.found.event", null, Locale.US));
		}
		kinPaymentEligibilityHistory.setEvent(event);

		kinPaymentEligibilityHistory.setSignedAgreementDate(homeInfoDto.getAgreementSignedDate());
		kinPaymentEligibilityHistory.setCourtOrderedPaymentDate(homeInfoDto.getCourtOrderedPaymentDate());
		kinPaymentEligibilityHistory.setLegalStatusCode(childBean.getCdLegalStatus());
		kinPaymentEligibilityHistory.setPaymentEligibilityEndDate(childBean.getPaymentEligibilityEndDate());

		sessionFactory.getCurrentSession().saveOrUpdate(kinPaymentEligibilityHistory);
		sessionFactory.getCurrentSession().flush();

		return kinPaymentEligibilityHistory.getId();
	}

	public boolean isFinalApproval (Long idApproval) {
		boolean finalApproval = false;
		int pedingAprvls = getPendingApprovalCount(idApproval);

		if (pedingAprvls == 0)  {
			//Final Approval
			finalApproval = true;
		}

		return finalApproval;
	}

	public int getPendingApprovalCount(Long idApproval) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Approvers.class);

		criteria.add(Restrictions.eq("idApproval", idApproval));
		criteria.add(Restrictions.eq("cdApproversStatus", CEVTSTAT_PEND));

		return criteria.list().size();
	}

	public KinMonthlyPaymentExtensionDto getMonthlyExtnBean(Long eventId) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getMonthlyExtnBeanSql)
				.addScalar("idMonthlyPaymntExtn", StandardBasicTypes.LONG)
				.addScalar("idChild", StandardBasicTypes.LONG)
				.addScalar("dtSvcStart", StandardBasicTypes.DATE)
				.addScalar("dtSvcEnd", StandardBasicTypes.DATE)
				.addScalar("cdGoodCause", StandardBasicTypes.STRING)
				.addScalar("txtComments", StandardBasicTypes.STRING)
				.setParameter("eventId", eventId)
				.setResultTransformer(Transformers.aliasToBean(KinMonthlyPaymentExtensionDto.class));

		return (KinMonthlyPaymentExtensionDto) query.uniqueResult();
	}

	public Long getCaseIdWithPersonId(Long personId) {
		Query query = ((Query) sessionFactory.getCurrentSession().createSQLQuery(getSubStagePersonIdSql)
				.addScalar("ID_CASE", StandardBasicTypes.LONG)
				.setParameter("personId", personId));
		return (Long) query.uniqueResult();
	}

	public void approveKinMonthlyPayment (KinHomeInfoDto kinHomeInfoDto, Long eventId,
										  Long appEventId, TodoDto todoDto) {
		Long serviceAuthDltId = null;
		double rate = 0.0;
		Long contractId = null;
		int lineItem = 0;
		Long serviceAuthId = null;
		Date  startDate = null;
		Long idCaseFromSAEvent = null;
		HashMap<Long, Long> map = new HashMap<Long, Long>();
		Long idServiceauth  = null;
		Long caseId = null;

		KinMonthlyPaymentExtensionDto childBean =  getMonthlyExtnBean(eventId);
		contractId = getContractId(kinHomeInfoDto.getIdHomeResource());
		serviceAuthId = serviceAuthorizationDao.getServiceAuthId(kinHomeInfoDto.getIdHomeResource(), childBean.getIdChild());
		if(ObjectUtils.isEmpty(serviceAuthId)){
			caseId = getCaseIdWithPersonId(childBean.getIdChild());
			// we need to check if there is a SA header with 68O or 68P exists
			Set<Long> serviceAuthSet = serviceAuthorizationDao.getSAHeaderIdSet(kinHomeInfoDto.getIdHomeResource(), contractId, caseId);

			Iterator<Long> it = serviceAuthSet.iterator();
			while (it.hasNext()) {
				Long sadId = it.next();

				idCaseFromSAEvent = getSAEventLinkCaseId(sadId);

				map.put(idCaseFromSAEvent,sadId);
			}
			if (map != null) {
				if(map.containsKey(idCaseFromSAEvent)){
					serviceAuthId = map.get(idCaseFromSAEvent);
					if(!isStageOpen(serviceAuthId)){
						serviceAuthId = null;
					}
				}
			}
		}
		startDate = childBean.getDtSvcStart();

		Date endDate = childBean.getDtSvcEnd();
		int numOfUnitsRemaining = (int) Math.round(DateHelper.daysDifference(endDate, startDate))+1;
		int totalUnitsAllowed = numOfUnitsRemaining;

		rate = getFosterCareRate(CSVCCODE_68P, startDate);

		lineItem = getContractLineItem(contractId, CSVCCODE_68P, startDate);
		if (!ObjectUtils.isEmpty(serviceAuthId)) {
			serviceAuthDltId = insertMonthlyExtensionSADetail(childBean, serviceAuthId, rate, totalUnitsAllowed, lineItem);
		}
		updateMonthlyPayment(serviceAuthDltId, eventId);
	}

	public int updateMonthlyPayment(Long serviceAuthDetailId, Long eventId) {
		Query queryUpdate = sessionFactory.getCurrentSession().createSQLQuery(updateMonthlyPaymentSql);
		queryUpdate.setParameter("serviceAuthDetailId", serviceAuthDetailId);
		queryUpdate.setParameter("eventId", eventId);
		return queryUpdate.executeUpdate();
	}

	public Long  insertMonthlyExtensionSADetail(KinMonthlyPaymentExtensionDto childBean,
											   Long serviceAuthId, double rate,
											   int totalUnitsAllowed, int lineItem ) {
		double totalAmount = totalUnitsAllowed * rate;
		Date today = Calendar.getInstance().getTime();
		SvcAuthDetail svcAuthDetail = new SvcAuthDetail();

		ServiceAuthorization serviceAuthorization = (ServiceAuthorization) sessionFactory.getCurrentSession().get(ServiceAuthorization.class, serviceAuthId);
		if (TypeConvUtil.isNullOrEmpty(serviceAuthorization)) {
			throw new DataNotFoundException(
					messageSource.getMessage("record.not.found.serviceAuthorization", null, Locale.US));
		}
		svcAuthDetail.setServiceAuthorization(serviceAuthorization);

		Person person = (Person) sessionFactory.getCurrentSession().get(Person.class, childBean.getIdChild());
		if (TypeConvUtil.isNullOrEmpty(person)) {
			throw new DataNotFoundException(
					messageSource.getMessage("record.not.found.person", null, Locale.US));
		}
		svcAuthDetail.setPerson(person);

		svcAuthDetail.setDtLastUpdate(today);
		svcAuthDetail.setCreatedPersonId(Long.valueOf(String.valueOf(KINSHIP_AUTOMATED_SYSTEM_ID)));
		svcAuthDetail.setCreatedDate(today);
		svcAuthDetail.setLastUpdatedPersonId(Long.valueOf(String.valueOf(KINSHIP_AUTOMATED_SYSTEM_ID)));

		svcAuthDetail.setCdSvcAuthDtlAuthType(CSVATYPE_INI);
		svcAuthDetail.setCdSvcAuthDtlPeriod(CNPERIOD_DAY);
		svcAuthDetail.setCdSvcAuthDtlSvc(CSVCCODE_68P);
		svcAuthDetail.setCdSvcAuthDtlUnitType(CINVUTYP_DA2);
		svcAuthDetail.setDtSvcAuthDtl(today);
		svcAuthDetail.setDtSvcAuthDtlBegin(childBean.getDtSvcStart());
		svcAuthDetail.setDtSvcAuthDtlEnd(childBean.getDtSvcEnd());
		svcAuthDetail.setDtSvcAuthDtlTerm(childBean.getDtSvcEnd());
		svcAuthDetail.setDtSvcAuthDtlShow(today);
		svcAuthDetail.setAmtSvcAuthDtlAmtReq(totalAmount);
		svcAuthDetail.setNbrSvcAuthDtlFreq(Long.valueOf(1));
		svcAuthDetail.setNbrSvcAuthDtlLineItm(Long.valueOf(String.valueOf(lineItem)));
		svcAuthDetail.setNbrSvcAuthDtlSugUnit(Long.valueOf(String.valueOf(totalUnitsAllowed)));
		svcAuthDetail.setNbrSvcAuthDtlUnitsReq(Double.valueOf(String.valueOf(totalUnitsAllowed)));
		svcAuthDetail.setNbrSvcAuthDtlUnitRate(rate);

		sessionFactory.getCurrentSession().save(svcAuthDetail);

		return svcAuthDetail.getIdSvcAuthDtl();
	}

	@Override
	public Long getCaseIdForHomeInfo(Long personId) {
		SQLQuery query = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getCaseIdForHomeInfoSql)
				.addScalar("caseId", StandardBasicTypes.LONG)
				.setParameter("personId", personId);
		return (Long) query.uniqueResult();
	}

	public KinHomeInfoDto getContractValidationFlag(KinHomeInfoDto homeInfoDto, Long contractId) {
		Long validContractId = null;
		int contractPeriodNumber = 0;
		int activeContractPeriodNumber = 0;
		int contractVersionNumber = 0;
		int lineItem68O = 0;
		int lineItem68P = 0;
		int lineItem68Q= 0;
		int lineItem68R = 0;
		int count = 0;
		boolean existsCountycode68O = false;
		boolean existsCountycode68P = false;
		boolean existsCountycode68Q = false;
		boolean existsCountycode68R = false;

		boolean existsResourceService68O = false;
		boolean existsResourceService68P = false;
		boolean existsResourceService68Q = false;
		boolean existsResourceService68R = false;

		boolean hasValidContract = false;
		boolean hasValidContractPeriod = false;
		boolean hasValidContractVersion = false;
		boolean hasValidContractService = false;
		boolean hasValidContractCounty = false;
		boolean hasValidResourceService = false;
		boolean splitCP = false;
		boolean splitCV = false;

		Long resourceId = homeInfoDto.getIdHomeResource();
		String countyCode = homeInfoDto.getResourceAddressCounty();
		String programType = CRSCPROG_01;
		String state = homeInfoDto.getResourceAddressState();
		String resourceRegion = homeInfoDto.getResourceRegion();
		Date oneYearAfterPaymntDate = null;


		String unitTypeDa2 = CCONUNIT_DA2;
		String unitTypeOne = CCONUNIT_ONE;
		String paymntTypeUrt = CCONPAY_URT;
		String paymntTypeVur = CCONPAY_VUR;
		Date paymntstartDate = new Date();
		if(!ObjectUtils.isEmpty(paymntstartDate)){
			oneYearAfterPaymntDate = DateHelper.addToDate(paymntstartDate,1,0,0);
		}

		count = getValidContractCount(resourceId);

		if (count > 1) {
			validContractId = getActiveContract(resourceId);
		}

		if (!ObjectUtils.isEmpty(validContractId)){
			hasValidContract = true;
			contractId = validContractId;
		}

		splitCP = getActiveContractPeriod(contractId);

		SQLQuery activeContractPeriodNumberQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getActiveContractPeriodNumberSql)
				.addScalar("contractPeriodNumber", StandardBasicTypes.INTEGER)
				.setParameter("contractId", contractId)
				.setParameter("paymntstartDate", paymntstartDate)
				.setParameter("contractPeriodStatusCode", CCONSTAT_ACT);
		Object activeContractPeriodNumberObj = activeContractPeriodNumberQuery.uniqueResult();
		if(!ObjectUtils.isEmpty(activeContractPeriodNumberObj)) {
			activeContractPeriodNumber = (Integer) activeContractPeriodNumberObj;
		}

		SQLQuery contractPeriodNumberQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getValidContractPeriodSql)
				.addScalar("contractPeriodNumber", StandardBasicTypes.INTEGER)
				.setParameter("contractId", contractId)
				.setParameter("contractPeriodSigned", STRING_Y)
				.setParameter("contractPeriodStatus", CCONSTAT_ACT);
		List<Integer> contractPeriodNumberList = null;
		if (!CollectionUtils.isEmpty(contractPeriodNumberQuery.list())) {
			contractPeriodNumberList = (List<Integer>) contractPeriodNumberQuery.list();
		}

		if(!CollectionUtils.isEmpty(contractPeriodNumberList) ) {
			contractPeriodNumber = contractPeriodNumberList.get(0);
		}

		if (contractPeriodNumber > 0) {
			hasValidContractPeriod = true;
		}
		splitCV = getActiveContractVersion(contractId, activeContractPeriodNumber);

		SQLQuery contractVersionNumberQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getContractVersionNumberSql)
				.addScalar("contractVersionNumber", StandardBasicTypes.INTEGER)
				.setParameter("contractId", contractId)
				.setParameter("activeContractPeriodNumber", activeContractPeriodNumber)
				.setParameter("contractVersionLocked", STRING_Y)
				.setParameter("paymntstartDate", paymntstartDate);
		Object contractVersionNumberObj = contractVersionNumberQuery.uniqueResult();
		if(!ObjectUtils.isEmpty(contractVersionNumberObj)) {
			contractVersionNumber = (Integer) contractVersionNumberObj;
		}

		if (contractVersionNumber > 0) {
			hasValidContractVersion = true;
			lineItem68O = getLineItemNumber(contractId, activeContractPeriodNumber, contractVersionNumber, CSVCCODE_68O, paymntTypeUrt, unitTypeDa2);
			lineItem68P = getLineItemNumber(contractId, activeContractPeriodNumber, contractVersionNumber, CSVCCODE_68P, paymntTypeUrt, unitTypeDa2);
			lineItem68Q = getLineItemNumber(contractId, activeContractPeriodNumber, contractVersionNumber, CSVCCODE_68Q, paymntTypeVur, unitTypeOne);
			lineItem68R = getLineItemNumber(contractId, activeContractPeriodNumber, contractVersionNumber, CSVCCODE_68R, paymntTypeVur, unitTypeOne);
			if (lineItem68O > 0 && lineItem68P > 0 && lineItem68Q > 0 && lineItem68R > 0) {
				hasValidContractService = true;
			}

			existsCountycode68O = getContractCountyId(contractId, activeContractPeriodNumber, contractVersionNumber, CSVCCODE_68O, countyCode,  lineItem68O);
			existsCountycode68P = getContractCountyId(contractId, activeContractPeriodNumber, contractVersionNumber, CSVCCODE_68P, countyCode,  lineItem68P);
			existsCountycode68Q = getContractCountyId(contractId, activeContractPeriodNumber, contractVersionNumber, CSVCCODE_68Q, countyCode,  lineItem68Q);
			existsCountycode68R = getContractCountyId(contractId, activeContractPeriodNumber, contractVersionNumber, CSVCCODE_68R, countyCode,  lineItem68R);
			if (existsCountycode68O && existsCountycode68P && existsCountycode68Q && existsCountycode68R) {
				hasValidContractCounty = true;
			}
		}

		existsResourceService68O = getResourceService(resourceId, countyCode, resourceRegion, CSVCCODE_68O, state);
		existsResourceService68P = getResourceService(resourceId, countyCode, resourceRegion, CSVCCODE_68P, state);
		existsResourceService68Q = getResourceService(resourceId, countyCode, resourceRegion, CSVCCODE_68Q, state);
		existsResourceService68R = getResourceService(resourceId, countyCode, resourceRegion, CSVCCODE_68R, state);
		if(existsResourceService68O && existsResourceService68P && existsResourceService68Q && existsResourceService68R) {
			hasValidResourceService = true;
		}

		homeInfoDto.setIndValidContractExists(hasValidContract);
		homeInfoDto.setIndSplitContract(splitCP);
		homeInfoDto.setIndSplitContractVersion(splitCV);
		homeInfoDto.setIndValidContractPeriodExists(hasValidContractPeriod);
		homeInfoDto.setIndValidContractVersionExists(hasValidContractVersion);
		homeInfoDto.setIndValidContractServiceExists(hasValidContractService);
		homeInfoDto.setIndValidContractCountyExists(hasValidContractCounty);
		homeInfoDto.setIndValidResourceServiceExists(hasValidResourceService);

		return homeInfoDto;
	}

	public Integer getValidContractCount(Long resourceId) {
		SQLQuery sqlQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getValidContractCountSql)
				.addScalar("count", StandardBasicTypes.INTEGER)
				.setParameter("resourceId", resourceId);

		return (Integer) sqlQuery.uniqueResult();
	}

	public Long getActiveContract(Long resourceId) {
		SQLQuery query = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getActiveContractSql)
				.addScalar("contractId", StandardBasicTypes.LONG)
				.setParameter("resourceId", resourceId);
		List<Long> contractIds = (List<Long>) query.list();
		Long contractId = null;
			if(!CollectionUtils.isEmpty(contractIds)){
				contractId = contractIds.get(0);
			}
		return contractId;
	}

	public boolean getActiveContractPeriod(Long contractId) {
		Date oneYearAfterPaymntDate = null;
		Date paymntstartDate = new Date();

		if (!ObjectUtils.isEmpty(paymntstartDate)) {
			oneYearAfterPaymntDate = DateHelper.addToDate(paymntstartDate, 1, 0, 0);
		}

		SQLQuery query = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getSplitContractPeriodNumberSql)
				.addScalar("contractPeriodNumber", StandardBasicTypes.INTEGER)
				.setParameter("contractId", contractId)
				.setParameter("paymntstartDate", paymntstartDate)
				.setParameter("oneYearAfterPaymntDate", oneYearAfterPaymntDate);
		Integer activeContractPeriodNumber = (Integer) query.uniqueResult();
		return ObjectUtils.isEmpty(activeContractPeriodNumber) || ( !ObjectUtils.isEmpty(activeContractPeriodNumber) && activeContractPeriodNumber == 0);

	}

	public boolean getActiveContractVersion(Long contractId, int activeContractPeriodNumber) {
		Date oneYearAfterPaymntDate = null;
		Date paymntstartDate = new Date();
		boolean splitCVExists = false;
		if (!ObjectUtils.isEmpty(paymntstartDate)) {
			oneYearAfterPaymntDate = DateHelper.addToDate(paymntstartDate, 1, 0, 0);
		}

		SQLQuery query = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getSplitContractVersionNumberSql)
				.addScalar("contractVersionNumber", StandardBasicTypes.INTEGER)
				.setParameter("contractId", contractId)
				.setParameter("activeContractPeriodNumber", activeContractPeriodNumber)
				.setParameter("paymntstartDate", paymntstartDate)
				.setParameter("oneYearAfterPaymntDate", oneYearAfterPaymntDate)
				.setParameter("contractVersionLocked", STRING_Y);
		Integer activeContractVersionNumber = (Integer) query.uniqueResult();
		return ObjectUtils.isEmpty(activeContractVersionNumber) || (!ObjectUtils.isEmpty(activeContractVersionNumber) && activeContractVersionNumber == 0);

	}

	public int updateKinHomeStatus(KinHomeInfoDto homeInfoDto) {
		Query queryUpdate = sessionFactory.getCurrentSession().createSQLQuery(updateKinHomeStatusSql);
		queryUpdate.setParameter("homeStatusCode", homeInfoDto.getHomeStatus());
		queryUpdate.setParameter("resourceStatusCode", KIN_HOME_APPROVAL_STATUS);
		queryUpdate.setParameter("lastUpdateDate", new Date());
		queryUpdate.setParameter("stageId", homeInfoDto.getIdHomeStage());
		queryUpdate.setParameter("personFullName", homeInfoDto.getPersonFullName());

		return queryUpdate.executeUpdate();
	}

	public int updateKinHomeInfrmtnStatus(KinHomeInfoDto homeInfoDto) {
		Query queryUpdate = sessionFactory.getCurrentSession().createSQLQuery(updateKinHomeInfrmtnStatusSql);
		queryUpdate.setParameter("homeStatusCode", homeInfoDto.getHomeStatus());
		queryUpdate.setParameter("lastUpdateDate", new Date());
		queryUpdate.setParameter("stageId", homeInfoDto.getIdHomeStage());
		queryUpdate.setParameter("personFullName", homeInfoDto.getPersonFullName());

		return queryUpdate.executeUpdate();
	}

	public int closeStage(StageDto stageDto, Date closedDate) {
		Query queryUpdate = sessionFactory.getCurrentSession().createSQLQuery(closeStageSql);
		queryUpdate.setParameter("closedDate", closedDate);
		queryUpdate.setParameter("closedReason", stageDto.getCdStageReasonClosed());
		queryUpdate.setParameter("lastUpdateDate", new Date());
		queryUpdate.setParameter("stageId", stageDto.getIdStage());

		return queryUpdate.executeUpdate();
	}

	public StgPersonLinkDto getStagePersonInfo(StgPersonLinkDto stgPersonLinkDto) {
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(StagePersonLink.class);
		criteria.add(Restrictions.eq("idStage", stgPersonLinkDto.getIdStage()));
		criteria.add(Restrictions.eq("cdStagePersRole", stgPersonLinkDto.getCdStagePersRole()));

		StagePersonLink stagePersonLink = (StagePersonLink) criteria.uniqueResult();

		BeanUtils.copyProperties(stagePersonLink, stgPersonLinkDto);

		return stgPersonLinkDto;
	}

	public boolean checkPersonInOpenStage(Long stageId, Long personId) {
		SQLQuery query = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getPersonInOpenStageSql)
				.addScalar("stageId", StandardBasicTypes.LONG)
				.setParameter("stageId", stageId)
				.setParameter("personId", personId);
		List<Long> stageIds = (List<Long>) query.list();
		return CollectionUtils.isEmpty(stageIds) ? false : true;
	}

	public boolean checkOtherStageOpen(Long stageId, Long caseId) {
		SQLQuery query = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getSelectOtherOpenStageSql)
				.addScalar("stageId", StandardBasicTypes.LONG)
				.setParameter("stageId", stageId)
				.setParameter("caseId", caseId);
		List<Long> stageIds = (List<Long>) query.list();
		return CollectionUtils.isEmpty(stageIds) ? false : true;
	}

	public int updatePersonStatus(Long personId, String status) {
		Query queryUpdate = sessionFactory.getCurrentSession().createSQLQuery(updatePersonStatusSql);
		queryUpdate.setParameter("personStatus", status);
		queryUpdate.setParameter("lastUpdateDate", new Date());
		queryUpdate.setParameter("personId", personId);

		return queryUpdate.executeUpdate();
	}

	public Date getCaseClosedDate(Long caseId) {
		SQLQuery sqlQuery = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getCaseClosedDateSql)
				.addScalar("caseClosedDate", StandardBasicTypes.DATE)
				.setParameter("caseId", caseId);

		return (Date) sqlQuery.uniqueResult();
	}

	public EmployeeDto getSelectEmployee(Long personId) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getSelectEmployeeSql)
				.addScalar("idOffice", StandardBasicTypes.LONG)
				.addScalar("idEmpUnit", StandardBasicTypes.LONG)
				.setParameter("personId", personId)
				.setResultTransformer(Transformers.aliasToBean(EmployeeDto.class));

		return (EmployeeDto) query.uniqueResult();
	}

	public int updateStagePersonLink(StgPersonLinkDto stgPersonLinkDto, String personRole) {
		Query queryUpdate = sessionFactory.getCurrentSession().createSQLQuery(getUpdateStagePersonSql);
		queryUpdate.setParameter("personRole", personRole);
		queryUpdate.setParameter("indStagePersEmpNew", stgPersonLinkDto.getIndStagePersEmpNew());
		queryUpdate.setParameter("personId", stgPersonLinkDto.getIdPerson());
		queryUpdate.setParameter("lastUpdateDate", new Date());
		queryUpdate.setParameter("stagePersonLinkId", stgPersonLinkDto.getIdStagePersonLink());

		return queryUpdate.executeUpdate();
	}

	public int deleteStagePersonRecords(Long stageId) {
		Query queryUpdate = sessionFactory.getCurrentSession().createSQLQuery(deleteStagePersonRecordsSql);
		queryUpdate.setParameter("stageId", stageId);

		return queryUpdate.executeUpdate();
	}

	public int updateCase(Date dtCaseClosed, Long caseId) {
		Query queryUpdate = sessionFactory.getCurrentSession().createSQLQuery(updateCaseSql);
		queryUpdate.setParameter("dtCaseClosed", dtCaseClosed);
		queryUpdate.setParameter("lastUpdateDate", new Date());
		queryUpdate.setParameter("caseId", caseId);

		return queryUpdate.executeUpdate();
	}

	public int updateSituation(Date dtSituationClosed, Long caseId) {
		Query queryUpdate = sessionFactory.getCurrentSession().createSQLQuery(updateSituationSql);
		queryUpdate.setParameter("dtSituationClosed", dtSituationClosed);
		queryUpdate.setParameter("lastUpdateDate", new Date());
		queryUpdate.setParameter("caseId", caseId);

		return queryUpdate.executeUpdate();
	}

	public RecordsRetentionDto getRecordsRetention(Long idRecRtnCase) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getRecordsRetentionSql)
				.addScalar("idRecRtnCase", StandardBasicTypes.LONG)
				.addScalar("dtLastUpdate", StandardBasicTypes.DATE)
				.addScalar("cdRecRetentionType", StandardBasicTypes.STRING)
				.addScalar("dtRecRtnDestroyActual", StandardBasicTypes.DATE)
				.addScalar("dtRecRtnDestroyEligible", StandardBasicTypes.DATE)
				.addScalar("recRtnDestoryDtReason", StandardBasicTypes.STRING)
				.setParameter("idRecRtnCase", idRecRtnCase)
				.setResultTransformer(Transformers.aliasToBean(RecordsRetentionDto.class));

		return (RecordsRetentionDto) query.uniqueResult();
	}

	public int updateRecordsRetention(RecordsRetentionDto recordsRetentionDto) {
		Query queryUpdate = sessionFactory.getCurrentSession().createSQLQuery(updateRecordsRetentionSql);
		queryUpdate.setParameter("cdRecRetentionType", recordsRetentionDto.getCdRecRetentionType());
		queryUpdate.setParameter("dtRecRtnDestroyActual", recordsRetentionDto.getDtRecRtnDestroyActual());
		queryUpdate.setParameter("dtRecRtnDestroyEligible", recordsRetentionDto.getDtRecRtnDestroyEligible());
		queryUpdate.setParameter("recRtnDestoryDtReason", recordsRetentionDto.getRecRtnDestoryDtReason());
		queryUpdate.setParameter("dtLastUpdate", new Date());
		queryUpdate.setParameter("idRecRtnCase", recordsRetentionDto.getIdRecRtnCase());

		return queryUpdate.executeUpdate();
	}

	public int savePaymentInfo(KinHomeInfoDto kinHomeInfoDto) {
		Query queryUpdate = sessionFactory.getCurrentSession().createSQLQuery(updateCapsResourceForEligibilitySql);
		queryUpdate.setParameter("kinCaregiverPaymentEligStatusCode", CKNPYELG_NELG);
		//PD-3801 : using setDate to tell hibernate that a null Date is not a binary value
		queryUpdate.setDate("paymentStartDate", kinHomeInfoDto.getPaymentStartDate());
		queryUpdate.setParameter("resourceId", kinHomeInfoDto.getIdHomeResource());
		queryUpdate.setParameter("personFullname", kinHomeInfoDto.getPersonFullName());

		return queryUpdate.executeUpdate();
	}

	public KinHomeAssessmentDto getHomeAssessmentOverallStatusDate(Long idCase) {

		Query query = sessionFactory.getCurrentSession().createSQLQuery(getHomeApprovalDetailsSql)
				.addScalar("idEvent", StandardBasicTypes.LONG)
				.addScalar("cdStatus", StandardBasicTypes.STRING)
				.addScalar("dtStatusChanged", StandardBasicTypes.DATE)
				.setParameter("idCase", idCase)
				.setResultTransformer(Transformers.aliasToBean(KinHomeAssessmentDto.class));

		return (KinHomeAssessmentDto) query.uniqueResult();
	}

	public KinHomeAssessmentDto getDeclineStatusDate(Long idCase) {

		Query query = sessionFactory.getCurrentSession().createSQLQuery(getDeclineApprovalDateSql)
				.addScalar("dtStatusChanged", StandardBasicTypes.DATE)
				.setParameter("idCase", idCase)
				.setResultTransformer(Transformers.aliasToBean(KinHomeAssessmentDto.class));
		return (KinHomeAssessmentDto) query.uniqueResult();
	}

	public KinHomeAssessmentDto getIntermediateStatusDate(String intermediateStatusString, Long idCase) {

		Query query = sessionFactory.getCurrentSession().createSQLQuery(getHomeAssessmentIntermediateDateSql)
				.addScalar("dtStatusChanged", StandardBasicTypes.DATE)
				.setParameter("intermediateStatusString", intermediateStatusString)
				.setParameter("idCase", idCase)
				.setResultTransformer(Transformers.aliasToBean(KinHomeAssessmentDto.class));
		return (KinHomeAssessmentDto) query.uniqueResult();
	}

	public int updateCapsResource(KinHomeAssessmentDto kinHomeAssessmentDto, Long idStage ) {

		Query query = sessionFactory.getCurrentSession().createSQLQuery(getUpdateCapsResourceSql)
				.setParameter("cdStatus", kinHomeAssessmentDto.getCdStatus())
				.setParameter("dtStatusChanged", kinHomeAssessmentDto.getDtStatusChanged())
				.setParameter("idStage", idStage)
				.setParameter("personFullName", kinHomeAssessmentDto.getNmPersonFull());
		return query.executeUpdate();
	}

	public KinHomeAssessmentDto getLatestStatusCode(Long idCase, Date dtStatusChanged) {

		Query query = sessionFactory.getCurrentSession().createSQLQuery(getLatestStatusDateSql)
				.addScalar("dtStatusChanged", StandardBasicTypes.DATE)
				.setParameter("idCase", idCase)
				.setParameter("dateStatusChanged", dtStatusChanged)
				.setResultTransformer(Transformers.aliasToBean(KinHomeAssessmentDto.class));
		return (KinHomeAssessmentDto) query.uniqueResult();
	}

	public KinHomeAssessmentDto getStatusDate(Long caseId) {
		Query query = sessionFactory.getCurrentSession().createSQLQuery(getStatusDateSql)
				.addScalar("idEvent", StandardBasicTypes.LONG)
				.addScalar("cdStatus", StandardBasicTypes.STRING)
				.addScalar("dtStatusChanged", StandardBasicTypes.DATE)
				.setParameter("idCase", caseId)
				.setResultTransformer(Transformers.aliasToBean(KinHomeAssessmentDto.class));
		return (KinHomeAssessmentDto) query.uniqueResult();
	}
}