package us.tx.state.dfps.service.placement.serviceimpl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import us.tx.state.dfps.common.domain.FceApplication;
import us.tx.state.dfps.populateletter.dto.CaseInfoDto;
import us.tx.state.dfps.service.admin.dao.AddrPersonLinkPhoneDao;
import us.tx.state.dfps.service.admin.dao.EmployeeDao;
import us.tx.state.dfps.service.admin.dao.LegalStatusPersonMaxStatusDtDao;
import us.tx.state.dfps.service.admin.dto.AddrPersonLinkPhoneInDto;
import us.tx.state.dfps.service.admin.dto.AddrPersonLinkPhoneOutDto;
import us.tx.state.dfps.service.admin.dto.LegalStatusPersonMaxStatusDtInDto;
import us.tx.state.dfps.service.admin.dto.LegalStatusPersonMaxStatusDtOutDto;
import us.tx.state.dfps.service.admin.dto.PersonEthnicityDto;
import us.tx.state.dfps.service.admin.dto.PersonRaceDto;
import us.tx.state.dfps.service.casepackage.dto.CSAEpisodeDto;
import us.tx.state.dfps.service.casepackage.dto.CsaEpisodesIncdntDto;
import us.tx.state.dfps.service.childplan.dto.ChildPlanAdtnlSctnDtlDto;
import us.tx.state.dfps.service.childplan.dto.ChildPlanEducationDto;
import us.tx.state.dfps.service.childplan.dto.ChildPlanEmtnlThrptcDtlDto;
import us.tx.state.dfps.service.childplan.dto.ChildPlanHealthCareSummaryDto;
import us.tx.state.dfps.service.childplan.dto.ChildPlanQrtpPrmnncyMeetingDto;
import us.tx.state.dfps.service.childplan.dto.ChildPlanLegalGrdnshpDto;
import us.tx.state.dfps.service.childplan.dto.ChildPlanQrtpPrmnncyMeetingDto;
import us.tx.state.dfps.service.childplan.dto.ChildPlanTransAdltAbvDtlDto;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.FormConstants;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.EventDao;
import us.tx.state.dfps.service.common.dao.UnitDao;
import us.tx.state.dfps.service.common.request.CommonApplicationReq;
import us.tx.state.dfps.service.common.response.CommonFormRes;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.fce.dao.FceDao;
import us.tx.state.dfps.service.fce.dto.FceApplicationDto;
import us.tx.state.dfps.service.fce.dto.FceEligibilityDto;
import us.tx.state.dfps.service.fcl.service.SexualVictimizationHistoryService;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.forms.util.CommonApplicationPrefillData;
import us.tx.state.dfps.service.forms.util.PlacementApplicationPrefillData;
import us.tx.state.dfps.service.lookup.dao.LookupDao;
import us.tx.state.dfps.service.pca.dto.PlacementDtlDto;
import us.tx.state.dfps.service.person.dao.PersonDao;
import us.tx.state.dfps.service.person.dao.PersonEthnicityDao;
import us.tx.state.dfps.service.person.dao.PersonRaceDao;
import us.tx.state.dfps.service.person.dao.ServicePackageDao;
import us.tx.state.dfps.service.person.dao.TraffickingDao;
import us.tx.state.dfps.service.person.dto.CharacteristicsDto;
import us.tx.state.dfps.service.person.dto.EducationHistoryDto;
import us.tx.state.dfps.service.person.dto.PersonDtlDto;
import us.tx.state.dfps.service.person.dto.PersonIdDto;
import us.tx.state.dfps.service.person.dto.ServicePackageDtlDto;
import us.tx.state.dfps.service.person.dto.TraffickingDto;
import us.tx.state.dfps.service.person.dto.UnitDto;
import us.tx.state.dfps.service.placement.dao.CommonApplicationDao;
import us.tx.state.dfps.service.placement.dto.AllegationCpsInvstDtlDto;
import us.tx.state.dfps.service.placement.dto.CPAdoptionDto;
import us.tx.state.dfps.service.placement.dto.CommonApplicationDto;
import us.tx.state.dfps.service.placement.dto.EventChildPlanDto;
import us.tx.state.dfps.service.placement.dto.NameDetailDto;
import us.tx.state.dfps.service.placement.dto.PersonLocDto;
import us.tx.state.dfps.service.placement.dto.RsrcAddrPhoneDto;
import us.tx.state.dfps.service.placement.dto.StagePersonLinkCaseDto;
import us.tx.state.dfps.service.placement.dto.TemporaryAbsenceDto;
import us.tx.state.dfps.service.placement.dto.VisitationPlanInfoDtlsDto;
import us.tx.state.dfps.service.placement.service.CommonApplicationService;
import us.tx.state.dfps.service.populateletter.dao.PopulateLetterDao;
import us.tx.state.dfps.service.workload.dao.WorkLoadDao;
import us.tx.state.dfps.service.workload.dto.EmployeePersPhNameDto;
import us.tx.state.dfps.service.workload.dto.EventDto;
import us.tx.state.dfps.service.workload.dto.PersonDto;
import us.tx.state.dfps.web.fcl.dto.SexualVictimHistoryDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:CommonApplicationServiceImpl will implement all operation defined
 * in CommonApplicationService Interface related Placement module Feb 9, 2018-
 * 2:17:34 PM © 2017 Texas Department of Family and Protective Services
 *  * **********  Change History *********************************
 * 10/21/2019 thompswa artf128758 : FCL Project - Add trafficking and sexual victimization histories
 * 11/13/2019 thompswa artf130251 : Trafficking History section not sorting properly
 * 11/25/2019 thompswa artf130779 : set IsAfterFclRelease by the R2 template ID 
 * 03/03/2022 suris PPM 65015 removing any code to show CSA_EPISODE data and only showing CSA_EPISODE_INCDNT data, the bookmark still exists for CSA_EPISODE on the template. This was done so no new template is required
 */
@Service
@Transactional
public class CommonApplicationServiceImpl implements CommonApplicationService {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private PersonDao personDao;

	@Autowired
	private EmployeeDao employeeDao;

	@Autowired
	private WorkLoadDao workLoadDao;

	@Autowired
	AddrPersonLinkPhoneDao addrPersonLinkPhoneDao;

	@Autowired
	private LegalStatusPersonMaxStatusDtDao legalStatusPersonMaxStatusDtDao;

	@Autowired
	private CommonApplicationDao commonApplicationDao;

	@Autowired
	private PopulateLetterDao populateLetterDao;

	@Autowired
	private CommonApplicationPrefillData commonApplicationPrefillData;

	@Autowired
	private UnitDao unitDao;

	@Autowired
	private FceDao fceDao;

	@Autowired
	private PlacementApplicationPrefillData placementApplicationPrefillData;

	@Autowired
	private PersonRaceDao personRaceDao;

	@Autowired
	private PersonEthnicityDao personEthnicityDao;

	@Autowired
	LookupDao lookupDao;
	
	@Autowired
	EventDao eventDao;

	@Autowired
	SexualVictimizationHistoryService sexualVictimizationHistoryService;
	
	@Autowired
	TraffickingDao trfckngDao;

	@Autowired
	ServicePackageDao servicePackageDao;

	private static final Logger logger = Logger.getLogger(CommonApplicationServiceImpl.class);
	private static final String DT_INCDNT= "Date of Incident:";
	private static final String INCDNT_DESCRPTN= "Incident Description:";
	public static final String DT_EPISODE_START= "Episode Start Date:";
	public static final String DT_EPISODE_END= "Episode End Date:";

	/**
	 * Method Description: This method is used to retrieve the common
	 * application form. This form fully documents the historical social,
	 * emotional, educational, medical, and family account of the child by
	 * passing IdStage and IdPerson as input request
	 * 
	 * @param commonApplicationReq
	 * @return PreFillDataServiceDto
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
			Exception.class })
	public PreFillDataServiceDto getCommonApplicationForm(CommonApplicationReq commonApplicationReq) {

		Long idPersonRole = ServiceConstants.NULL_VAL;
		CommonApplicationDto commonApplicationDto = new CommonApplicationDto();
		PersonDto personDto = null;
		List<AllegationCpsInvstDtlDto> allegationCpsInvstDtlDtoList = null;
		// Call CSEC15D
		StagePersonLinkCaseDto stagePersonLinkCaseDto = commonApplicationDao
				.getStagePersonCaseDtl(commonApplicationReq.getIdStage(), ServiceConstants.PRIMARY_CHILD);
		/**
		 * Set dummy fields to NULL to ensure that no data appears on the form
		 * in the YES, NO, or UNKNOWN bookmarks for the dummy fields.
		 */
		stagePersonLinkCaseDto.setCdStage(ServiceConstants.NULL_STRING);
		stagePersonLinkCaseDto.setCdStageProgram(ServiceConstants.NULL_STRING);
		stagePersonLinkCaseDto.setCdStageType(ServiceConstants.NULL_STRING);

		if (!ObjectUtils.isEmpty(stagePersonLinkCaseDto)
				&& !ObjectUtils.isEmpty(stagePersonLinkCaseDto.getIdPerson())) {
			// Call CSES31D
			PersonDtlDto personDtlDto = commonApplicationDao.getPersonDtlById(stagePersonLinkCaseDto.getIdPerson());
			commonApplicationDto.setPersonDtlDto(personDtlDto);
			// Call CSES35D
			if (!ObjectUtils.isEmpty(commonApplicationDao.getPersonLocDtls(stagePersonLinkCaseDto.getIdPerson(),
					ServiceConstants.LOC_TYPE_BLOC))) {
				PersonLocDto personBlocLocDto = commonApplicationDao
						.getPersonLocDtls(stagePersonLinkCaseDto.getIdPerson(), ServiceConstants.LOC_TYPE_BLOC).get(0);
				commonApplicationDto.setPersonBlocLocDto(personBlocLocDto);
			}
			// Call CSES32D.
			// This retrieves the most recent legal status for child
			LegalStatusPersonMaxStatusDtInDto childLegalStatusDto = new LegalStatusPersonMaxStatusDtInDto();
			childLegalStatusDto.setIdPerson(stagePersonLinkCaseDto.getIdPerson());
			List<LegalStatusPersonMaxStatusDtOutDto> childLegalStatusDtoList = legalStatusPersonMaxStatusDtDao
					.getRecentLegelStatusRecord(childLegalStatusDto);
			commonApplicationDto.setChildLegalStatusDtoList(childLegalStatusDtoList);
			// Call CSES35D
			if (!ObjectUtils.isEmpty(commonApplicationDao.getPersonLocDtls(stagePersonLinkCaseDto.getIdPerson(),
					ServiceConstants.RLOC_TYPE))) {
				PersonLocDto personRlocLocDto = commonApplicationDao
						.getPersonLocDtls(stagePersonLinkCaseDto.getIdPerson(), ServiceConstants.RLOC_TYPE).get(0);
				commonApplicationDto.setPersonRlocLocDto(personRlocLocDto);
			}
			// Call CLSS29D
			allegationCpsInvstDtlDtoList = commonApplicationDao
					.getAllegationCpsInvstDetails(stagePersonLinkCaseDto.getIdPerson());
			commonApplicationDto.setAllegationCpsInvstDtlDtoList(allegationCpsInvstDtlDtoList);
			// Call CSEC20D
			EventChildPlanDto eventChildPlanDto = commonApplicationDao
					.getEventChildPlanDtls(stagePersonLinkCaseDto.getIdPerson());
			commonApplicationDto.setEventChildPlanDto(eventChildPlanDto);
			// Call CSEC35D
			NameDetailDto nameDetailDto = commonApplicationDao.getNameDetails(stagePersonLinkCaseDto.getIdPerson());
			commonApplicationDto.setNameDetailDto(nameDetailDto);
			// Call CCMN44D
			personDto = personDao.getPersonById(stagePersonLinkCaseDto.getIdPerson());
			// Call CCMN72D
			PersonIdDto personIdDto = commonApplicationDao.getMedicaidNbrByPersonId(
					stagePersonLinkCaseDto.getIdPerson(), ServiceConstants.PERSON_ID_TYPE_SSN,
					ServiceConstants.PERSON_ID_NOT_INVALID, ServiceConstants.GENERIC_END_DATE);
			commonApplicationDto.setPersonIdDto(personIdDto);
			// Call CINV46D
			AddrPersonLinkPhoneInDto addrPersonLinkPhoneInDto = new AddrPersonLinkPhoneInDto();
			addrPersonLinkPhoneInDto.setUlIdPerson(stagePersonLinkCaseDto.getIdPerson());
			addrPersonLinkPhoneInDto.setDtMaxDate(ServiceConstants.MAX_DATE_STRING);
			List<AddrPersonLinkPhoneOutDto> addrPersonLinkPhoneOutDtoList = addrPersonLinkPhoneDao
					.cinv46dQUERYdam(addrPersonLinkPhoneInDto);
			commonApplicationDto.setAddrPersonLinkPhoneOutDtoList(addrPersonLinkPhoneOutDtoList);
			// Call CLSS60D
			List<CharacteristicsDto> characteristicsDtoList = personDao
					.fetchPersonCharaceristics(stagePersonLinkCaseDto.getIdPerson());
			Boolean characteristicValue = Boolean.FALSE;
			if (characteristicsDtoList.size() > ServiceConstants.ZERO_SHORT) {
				for (CharacteristicsDto characteristicsDto : characteristicsDtoList) {
					if (FormConstants.CHILD_SEXUAL_AGGRESSION.equalsIgnoreCase(characteristicsDto.getCdCharacCode())) {
						/**
						 * cdCharacteristic is being "dummied" for use as YES
						 * bookmark
						 */
						characteristicsDto.setCdCharacCode(FormConstants.DISPLAY_X);
						characteristicsDto.setCdCharacCategory(FormConstants.DISPLAY_BLANK);
						commonApplicationDto.setCharacteristicsDto(characteristicsDto);
						characteristicValue = Boolean.TRUE;
						break;
					}
				}
				if (!characteristicValue) {
					CharacteristicsDto characteristicsDto = new CharacteristicsDto();
					characteristicsDto.setCdCharacCategory(FormConstants.DISPLAY_X);
					characteristicsDto.setCdCharacCode(FormConstants.DISPLAY_BLANK);
					commonApplicationDto.setCharacteristicsDto(characteristicsDto);
				}
			} else {
				// cdCharacteristic is being "dummied" for use as No bookmark
				CharacteristicsDto characteristicsDto = new CharacteristicsDto();
				characteristicsDto.setCdCharacCategory(FormConstants.DISPLAY_X);
				characteristicsDto.setCdCharacCode(FormConstants.DISPLAY_BLANK);
				commonApplicationDto.setCharacteristicsDto(characteristicsDto);
			}
		}
		// Call CLSC01D
		List<CaseInfoDto> caseInfoDtoList = populateLetterDao.getCaseInfoById(commonApplicationReq.getIdStage(),
				ServiceConstants.PRINCIPAL);
		commonApplicationDto.setCaseInfoDtolist(caseInfoDtoList);

		// Call CINV51D
		idPersonRole = workLoadDao.getPersonIdByRole(commonApplicationReq.getIdStage(),
				ServiceConstants.STAGE_PERS_ROLE_PR);
		if (ServiceConstants.NULL_VAL != idPersonRole && ServiceConstants.ZERO_VAL < idPersonRole) {
			// Call CSEC01D
			EmployeePersPhNameDto employeePersPhNameDto = employeeDao.searchPersonPhoneName(idPersonRole);
			if (null != employeePersPhNameDto) {
				if (!ServiceConstants.BUSINESS_PHONE.equalsIgnoreCase(employeePersPhNameDto.getCdPhoneType())
						|| ServiceConstants.BUSINESS_CELL.equalsIgnoreCase(employeePersPhNameDto.getCdPhoneType())) {
					// changes to fix defect 6023
					if (StringUtils.isEmpty(employeePersPhNameDto.getNbrPhone())) {
						employeePersPhNameDto.setNbrPhone(employeePersPhNameDto.getMailCodePhone());
					}
					if (StringUtils.isEmpty(employeePersPhNameDto.getNbrPhoneExtension())) {
						employeePersPhNameDto.setNbrPhoneExtension(employeePersPhNameDto.getMailCodePhoneExt());
					}

				}
			}
			commonApplicationDto.setEmployeePersPhNameDto(employeePersPhNameDto);

		}
		Long idPersonPW = commonApplicationReq.getIdPerson();
		// Call CSEC01D
		EmployeePersPhNameDto employeePersPhNameDtoPW = employeeDao.searchPersonPhoneName(idPersonPW);
		if (null != employeePersPhNameDtoPW) {
			if (!ServiceConstants.BUSINESS_PHONE.equalsIgnoreCase(employeePersPhNameDtoPW.getCdPhoneType())
					|| ServiceConstants.BUSINESS_CELL.equalsIgnoreCase(employeePersPhNameDtoPW.getCdPhoneType())) {
				employeePersPhNameDtoPW.setNbrPhone(employeePersPhNameDtoPW.getMailCodePhone());
				employeePersPhNameDtoPW.setNbrPhoneExtension(employeePersPhNameDtoPW.getMailCodePhoneExt());
			}
		}
		commonApplicationDto.setEmployeePersPhNameDtoPW(employeePersPhNameDtoPW);
		// Calculate the personAge from date of birth of the person
		if (!TypeConvUtil.isNullOrEmpty(personDto.getDtPersonBirth())) {
			Integer prsnAge = DateUtils.getAge(personDto.getDtPersonBirth());
			personDto.setNbrPersonAge(prsnAge.shortValue());
		}
		/**
		 * If PersonAge > 16 set the cdpersonLivArr value as string "X" or null
		 * value
		 */
		personDto.setCdPersonLivArr(personDto.getNbrPersonAge() > FormConstants.CHILD_AGE_16 ? FormConstants.DISPLAY_X
				: ServiceConstants.NULL_STRING);
		commonApplicationDto.setPersonDto(personDto);
		Boolean bANYesFlag = Boolean.FALSE;
		Boolean bANUnknownFlag = Boolean.FALSE;
		/**
		 * Logic to fill one of the Abuse/Neglect bookmarks. if any child in the
		 * case has a history of abuse/neglect, all children will have the same
		 * even if allegations where ruled out for the other childern in the
		 * case
		 */
		if (null != allegationCpsInvstDtlDtoList && allegationCpsInvstDtlDtoList.size() > ServiceConstants.Zero) {
			for (AllegationCpsInvstDtlDto allegationCpsInvstDtlDto : allegationCpsInvstDtlDtoList) {
				if (!ObjectUtils.isEmpty(allegationCpsInvstDtlDto.getCdAllegDisposition())) {
					if (!allegationCpsInvstDtlDto.getCdAllegDisposition()
							.equalsIgnoreCase(ServiceConstants.REASON_TO_BELIEVE)) {
						bANYesFlag = Boolean.TRUE;
						break;
					} else if (!allegationCpsInvstDtlDto.getCdAllegDisposition()
							.equalsIgnoreCase(ServiceConstants.UNABLE_TO_DETRMN_DISP)
							|| !allegationCpsInvstDtlDto.getCdAllegDisposition()
									.equalsIgnoreCase(ServiceConstants.FAMILY_MOVED)
							|| !allegationCpsInvstDtlDto.getCdAllegDisposition()
									.equalsIgnoreCase(ServiceConstants.UNABLE_TO_COMPLETE)) {
						bANUnknownFlag = Boolean.TRUE;
						continue;
					}
				}
			}
		}
		if (bANYesFlag) {
			// cdStage is being "dummied" for use as YES bookmark
			stagePersonLinkCaseDto.setCdStage(FormConstants.DISPLAY_X);
		} else if (bANUnknownFlag) {
			// cdStageProgram is being "dummied" for use as UNKNOWN bookmark
			stagePersonLinkCaseDto.setCdStageProgram(FormConstants.DISPLAY_X);
		} else {
			// cdStageType is being "dummied" for use as NO bookmark
			stagePersonLinkCaseDto.setCdStageType(FormConstants.DISPLAY_X);
		}
		commonApplicationDto.setStagePersonLinkCaseDto(stagePersonLinkCaseDto);
		// Calling Prefill Data Implementation
		logger.info("TransactionId :" + commonApplicationReq.getTransactionId());
		return commonApplicationPrefillData.returnPrefillData(commonApplicationDto);
	}

	/**
	 * Method Description: This method is used to retrieve the placement
	 * application form. This form fully documents the Child Information, Trauma
	 * and Trafficking History, Health Care Summary, Substance Abuse,Risk and
	 * sexualized behavior,Education,Family and Placement history emotional,
	 * Transition planning for a Adulthood,Juvenile Justice Involvement account
	 * of the child by passing IdStage and IdPerson as input request
	 * 
	 * @param commonApplicationReq
	 * @return PreFillDataServiceDto
	 */
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
			Exception.class })
	public PreFillDataServiceDto getPlacementApplication(CommonApplicationReq commonApplicationReq, boolean docExists) {

		CommonApplicationDto commonApplicationDto = new CommonApplicationDto();
		
		/**
		 *  getting boolean for release date for FCL changes artf128758
		 */
		Boolean isAfterFcl = true; // artf130779 change boolean from creldate to template version
		if (!ObjectUtils.isEmpty(commonApplicationReq.getIdTemplate()) 
				&& FormConstants.CSC11O00_TEMPLATE_VERSN_R2.equals(commonApplicationReq.getIdTemplate())) {
			isAfterFcl = false;
		}
		logger.info("commonApplicationReq.getIdTemplate() =  "+commonApplicationReq.getIdTemplate() + "  isAfterFcl = " + isAfterFcl);
		commonApplicationDto.setIsAfterFclRelease(isAfterFcl); // artf128758 end 
		// Fetch DFPS Case Worker & Supervisor Details - Call CINV51D
		Long idPersonCaseWrkr = workLoadDao.getPersonIdByRole(commonApplicationReq.getIdStage(),
				ServiceConstants.STAGE_PERS_ROLE_PR);
		if (!ObjectUtils.isEmpty(idPersonCaseWrkr)) {
			// Call CSEC01D
			EmployeePersPhNameDto employeePersCsWrkrDto = employeeDao.searchPersonPhoneName(idPersonCaseWrkr);
			commonApplicationDto.setEmployeePersCsWrkrDto(employeePersCsWrkrDto);
			if (!ObjectUtils.isEmpty(employeePersCsWrkrDto)
					&& !ObjectUtils.isEmpty(employeePersCsWrkrDto.getIdJobPersSupv())) {
				EmployeePersPhNameDto employeePersSuprvsrDto = employeeDao
						.searchPersonPhoneName(employeePersCsWrkrDto.getIdJobPersSupv());
				commonApplicationDto.setEmployeePersSuprvsrDto(employeePersSuprvsrDto);
			}
			if (!ObjectUtils.isEmpty(employeePersCsWrkrDto)
					&& !ObjectUtils.isEmpty(employeePersCsWrkrDto.getIdUnit())) {
				UnitDto unitDto = unitDao.searchUnitDtls(employeePersCsWrkrDto.getIdUnit());
				commonApplicationDto.setUnitDto(unitDto);
			}
		}
		// Fetch child informations and idPerson details - Call CSEC15D
		StagePersonLinkCaseDto stagePersonLinkCaseDto = commonApplicationDao
				.getStagePersonCaseDtl(commonApplicationReq.getIdStage(), ServiceConstants.PRIMARY_CHILD);
		if (!ObjectUtils.isEmpty(stagePersonLinkCaseDto)
				&& !ObjectUtils.isEmpty(stagePersonLinkCaseDto.getIdPerson())) {
			// This retrieves the Child Name details - Call CSEC35D
			NameDetailDto nameDetailDto = commonApplicationDao.getNameDetails(stagePersonLinkCaseDto.getIdPerson());
			commonApplicationDto.setNameDetailDto(nameDetailDto);

			// Retrieves the person Detail information of the child - Call
			// CCMN44D
			PersonDto personDto = personDao.getPersonById(stagePersonLinkCaseDto.getIdPerson());
			commonApplicationDto.setPersonDto(personDto);

			List<PersonRaceDto> personRaceDtoList = personRaceDao
					.getPersonRaceByPersonId(stagePersonLinkCaseDto.getIdPerson());
			if (!ObjectUtils.isEmpty(personRaceDtoList) && personRaceDtoList.size() > ServiceConstants.Zero) {
				StringBuffer raceString = new StringBuffer();
				int raceCount = ServiceConstants.Zero;
				for (PersonRaceDto personRaceDto : personRaceDtoList) {
					String raceDecode = lookupDao.simpleDecodeSafe(CodesConstant.CRACE,
							personRaceDto.getCdPersonRace());
					if (raceCount > ServiceConstants.Zero) {
						raceString.append(ServiceConstants.COMMA);
					}
					raceString.append(raceDecode);
					raceCount++;
				}
				commonApplicationDto.setRace(raceString.toString());
			}
			List<PersonEthnicityDto> personEthnicityDtoList = personEthnicityDao
					.getPersonEthnicityByPersonId(stagePersonLinkCaseDto.getIdPerson());
			if (!ObjectUtils.isEmpty(personEthnicityDtoList) && personEthnicityDtoList.size() > ServiceConstants.Zero) {
				StringBuffer ethinicityString = new StringBuffer();
				int ethinicityCount = ServiceConstants.Zero;
				for (PersonEthnicityDto personEthnicityDto : personEthnicityDtoList) {
					String ethinicityDecode = lookupDao.simpleDecodeSafe(CodesConstant.CINDETHN,
							personEthnicityDto.getCdPersonEthnicity());
					if (ethinicityCount > ServiceConstants.Zero) {
						ethinicityString.append(ServiceConstants.COMMA);
					}
					ethinicityString.append(ethinicityDecode);
					ethinicityCount++;
				}
				commonApplicationDto.setEthinicity(ethinicityString.toString());
			}
			// This retrieves the most recent legal status for child - Call
			// CSES32D.
			LegalStatusPersonMaxStatusDtInDto childLegalStatusDto = new LegalStatusPersonMaxStatusDtInDto();
			childLegalStatusDto.setIdPerson(stagePersonLinkCaseDto.getIdPerson());
			List<LegalStatusPersonMaxStatusDtOutDto> childLegalStatusDtoList = legalStatusPersonMaxStatusDtDao
					.getRecentLegelStatusRecord(childLegalStatusDto);
			commonApplicationDto.setChildLegalStatusDtoList(childLegalStatusDtoList);

			// Retrieves the most recent foster care eligibility for the child
			FceEligibilityDto fceEligibilityDto = commonApplicationDao
					.getFceEligibility(stagePersonLinkCaseDto.getIdPerson());
			if (!ObjectUtils.isEmpty(fceEligibilityDto)) {
				FceApplicationDto fceApplicationDto = getFceApplicationDto(
						fceDao.getFceApplication(fceEligibilityDto.getIdFceApplication()));
				commonApplicationDto.setFceApplicationDto(fceApplicationDto);
			}
			commonApplicationDto.setFceEligibilityDto(fceEligibilityDto);

			// Retrieves the most recent Child Service Plan Details Information
			// - Call CSEC20D
			EventChildPlanDto eventChildPlanDto = commonApplicationDao
					.getEventChildPlanDtls(stagePersonLinkCaseDto.getIdPerson());
			commonApplicationDto.setEventChildPlanDto(eventChildPlanDto);

			//code written for PPM 65015 to not read CSA_EPISODE data any more but only CSA_EPISODE_INCDNT
			List<CsaEpisodesIncdntDto> csaEpisodesIncdntDtoLst = commonApplicationDao
					.getCSAEpisodesIncdntDtlByIdPerson(stagePersonLinkCaseDto.getIdPerson());
			if(!ObjectUtils.isEmpty(csaEpisodesIncdntDtoLst)){
				commonApplicationDto.setChildCSABhvr(Boolean.TRUE);
				commonApplicationDto.setCsaEpisodesIncdntDtoLst(csaEpisodesIncdntDtoLst);
			}
			
			
			// artf128758 retrieves child sexual victimization episodes/incidents
			SexualVictimHistoryDto sexualVictimHistoryDto = sexualVictimizationHistoryService.getSexualVictimHistoryDto(stagePersonLinkCaseDto.getIdPerson());
			if ( !ObjectUtils.isEmpty(sexualVictimHistoryDto) 
					&& !TypeConvUtil.isNullOrEmpty(sexualVictimHistoryDto.getIndChildHasSxVictimHistory())
					&& ServiceConstants.Y.equals(sexualVictimHistoryDto.getIndChildHasSxVictimHistory())) {
				commonApplicationDto.setIndSxVictimHist(Boolean.TRUE);
			}
			commonApplicationDto.setSexVicHistDto(sexualVictimHistoryDto);

			// Retrieves the most recent child Placement details
			PlacementDtlDto placementDtlDto = commonApplicationDao
					.getPlacementDtl(stagePersonLinkCaseDto.getIdPerson());
			commonApplicationDto.setPlacementDtlDto(placementDtlDto);

			// Retrieves the all placement details available for the child.
			List<PlacementDtlDto> plcmntLogDtlLst = commonApplicationDao
					.getPlacementLogDtl(stagePersonLinkCaseDto.getIdPerson());
			commonApplicationDto.setPlcmntLogDtlLst(plcmntLogDtlLst);

			// PPM 65209 - Temporary Absences in placement
			// Retrieves the all Temporary Absences in placement available for the child.
			List<TemporaryAbsenceDto> tempAbsenceLst = commonApplicationDao
					.getTemporaryAbsenceList(stagePersonLinkCaseDto.getIdPerson());
			commonApplicationDto.setTempAbsenceList(tempAbsenceLst);

			//artf263992 : BR 15.4 Form - Common Application for Placement of Children in Residential Care
			List<ServicePackageDtlDto> recmServicePackageDtlDtos = servicePackageDao.getRecommendedServicePackageByIdStage(commonApplicationReq.getIdStage());
			commonApplicationDto.setRmdServicePackageDtlDto(CollectionUtils.isEmpty(recmServicePackageDtlDtos) ? null : recmServicePackageDtlDtos.size() == 1 ? recmServicePackageDtlDtos.get(0) :
					recmServicePackageDtlDtos.stream().max(Comparator.comparing(ServicePackageDtlDto::getDtSvcStart)).orElse(null));

			// Retrieves the most recent child Service Level details for
			// Placement
			if(ObjectUtils.isEmpty(commonApplicationDto.getRmdServicePackageDtlDto())){
				PersonLocDto servicelvlPersonLocDto = commonApplicationDao
						.getServiceLevelInfo(stagePersonLinkCaseDto.getIdPerson());
				commonApplicationDto.setServicelvlPersonLocDto(servicelvlPersonLocDto);
			}

			// Retrieves the most recent child Trafficking History Dtls
            // artf128758 using enhanced TraffickingDao, removing : TraffickingDto traffickingDto = commonApplicationDao.getTrfckngDtl(stagePersonLinkCaseDto.getIdPerson());
			TraffickingDto traffickingDto=new TraffickingDto();
			traffickingDto.setIdPerson(BigDecimal.valueOf(stagePersonLinkCaseDto.getIdPerson()));
			List<TraffickingDto> trfckngDtoLst = trfckngDao.getTraffickingList(traffickingDto);
            // set the Boolean sex and labor trafficking question answers 
			List<TraffickingDto> sxtrSuspDtoLst = trfckngDtoLst
					.stream().filter( sxtrSusp -> sxtrSusp.getCdtrfckngType().equals(CodesConstant.CTRFTYP_SXTR)
					&& sxtrSusp.getCdtrfckngStat().equals(CodesConstant.CTRFSTAT_SUSP))
					.collect(Collectors.toList());
			if (!ObjectUtils.isEmpty(sxtrSuspDtoLst) && ServiceConstants.Zero < sxtrSuspDtoLst.size()) {
				commonApplicationDto.setIndChildSuspdSxTrfckng(Boolean.TRUE);
			}
			List<TraffickingDto> sxtrConfDtoLst = trfckngDtoLst
					.stream().filter( sxtrConf -> sxtrConf.getCdtrfckngType().equals(CodesConstant.CTRFTYP_SXTR)
					&& sxtrConf.getCdtrfckngStat().equals(CodesConstant.CTRFSTAT_CONF))
					.collect(Collectors.toList());
			if (!ObjectUtils.isEmpty(sxtrConfDtoLst) && ServiceConstants.Zero < sxtrConfDtoLst.size()) {
				commonApplicationDto.setIndChildCnfrmdSxTrfckng(Boolean.TRUE);
			}
			List<TraffickingDto> lbtrSuspDtoLst = trfckngDtoLst
					.stream().filter( lbtrSusp -> lbtrSusp.getCdtrfckngType().equals(CodesConstant.CTRFTYP_LBTR)
					&& lbtrSusp.getCdtrfckngStat().equals(CodesConstant.CTRFSTAT_SUSP))
					.collect(Collectors.toList());
			if (!ObjectUtils.isEmpty(lbtrSuspDtoLst) && ServiceConstants.Zero < lbtrSuspDtoLst.size()) {
				commonApplicationDto.setIndChildSuspdLbrTrfckng(Boolean.TRUE);
			}
			List<TraffickingDto> lbtrConfDtoLst = trfckngDtoLst
					.stream().filter( lbtrConf -> lbtrConf.getCdtrfckngType().equals(CodesConstant.CTRFTYP_LBTR)
					&& lbtrConf.getCdtrfckngStat().equals(CodesConstant.CTRFSTAT_CONF))
					.collect(Collectors.toList());
			if (!ObjectUtils.isEmpty(lbtrConfDtoLst) && ServiceConstants.Zero < lbtrConfDtoLst.size()) {
				commonApplicationDto.setIndChildCnfrmdLbrTrfckng(Boolean.TRUE);
			}
			// artf128758 set child sexual trafficking list in the form dto
			// filter for confirmed sex, skipping the suspected and labor trafficking incidents
			//Fix for defect 13954
			List<TraffickingDto> traffickingDtoLst = trfckngDtoLst.stream()
					.filter(type -> type.getCdtrfckngType().equals(CodesConstant.CTRFTYP_SXTR))
					.filter(status -> status.getCdtrfckngStat().equals(CodesConstant.CTRFSTAT_CONF))
					.sorted(Comparator
							.comparing(TraffickingDto::getDtOfIncdnt, Comparator.nullsLast(Comparator.naturalOrder()))
							.reversed()) // artf130251
					.collect(Collectors.toList());
			if (!ObjectUtils.isEmpty(traffickingDtoLst)) {
				commonApplicationDto.setTrfckngDtoLst(traffickingDtoLst);
			} // artf128758 end

			if (!ObjectUtils.isEmpty(eventChildPlanDto)
					&& !ObjectUtils.isEmpty(eventChildPlanDto.getIdChildPlanEvent())) {
				// Retrieves the most recent child plan EmtnlThrptcDtl
				ChildPlanEmtnlThrptcDtlDto cpEmtnlThrptcDtlDto = commonApplicationDao
						.getChildPlanEmtnlSectnDtl(eventChildPlanDto.getIdChildPlanEvent());
				commonApplicationDto.setCpEmtnlThrptcDtlDto(cpEmtnlThrptcDtlDto);
				// Retrieves the most recent child plan HlthCareSummDtl
				ChildPlanHealthCareSummaryDto cpHealthCareSummaryDto = commonApplicationDao
						.getChildPlanHlthCareSummDtl(eventChildPlanDto.getIdChildPlanEvent());
				commonApplicationDto.setCpHealthCareSummaryDto(cpHealthCareSummaryDto);
				// Retrieves the most recent child plan QrtpPtmDtl
				ChildPlanQrtpPrmnncyMeetingDto cpQrtpPtmDto = commonApplicationDao
						.getChildPlanQrtpPtmDtl(eventChildPlanDto.getIdChildPlanEvent());
				commonApplicationDto.setCpQrtpPtmDto(cpQrtpPtmDto);
				// Retrieves the most recent child plan Education Details
				ChildPlanEducationDto cpEducationDto = commonApplicationDao
						.getChildPlanEducationDtl(eventChildPlanDto.getIdChildPlanEvent());
				commonApplicationDto.setCpEducationDto(cpEducationDto);
				// Retrieves the most recent child plan Transition Planning for
				// a successful Adulthood Details.
				ChildPlanTransAdltAbvDtlDto cpTransAdltAbvDtlDto = commonApplicationDao
						.getChildPlanTranstmAdultAbv(eventChildPlanDto.getIdChildPlanEvent());
				commonApplicationDto.setCpTransAdltAbvDtlDto(cpTransAdltAbvDtlDto);
				// Retrieves the most recent child plan Juvenile Justice
				// Involvement
				ChildPlanAdtnlSctnDtlDto cpAdtnlSctnDtlDto = commonApplicationDao
						.getChildPlanAdtnlSctnDtls(eventChildPlanDto.getIdChildPlanEvent());
				commonApplicationDto.setCpAdtnlSctnDtlDto(cpAdtnlSctnDtlDto);
				// Retrieves the most recent child plan adoption details
				List<CPAdoptionDto> cpAdoptionDtoList = commonApplicationDao
						.getChildPlanAdoptnDtls(eventChildPlanDto.getIdChildPlanEvent());
				commonApplicationDto.setCpAdoptionDto(cpAdoptionDtoList);
				// Retrieves the most recent child plan Legal gardianship
				// details
				ChildPlanLegalGrdnshpDto cpLegalGrdnshpDto = commonApplicationDao
						.getChildPlanLegalGrdnShpDtl(eventChildPlanDto.getIdChildPlanEvent());
				commonApplicationDto.setCpLegalGrdnshpDto(cpLegalGrdnshpDto);
			}
			// Retrieves the child characteristic information
			List<CharacteristicsDto> personCharList = personDao
					.fetchPersonCharaceristics(stagePersonLinkCaseDto.getIdPerson());
			//Modified the code to check the size >0 for Warrant Defect 11524
			if (!ObjectUtils.isEmpty(personCharList) && personCharList.size() > ServiceConstants.Zero_INT) {
				for (CharacteristicsDto characteristicsDto : personCharList) {
					if (CodesConstant.CPL.equalsIgnoreCase(characteristicsDto.getCdCharacCategory())
							&& CodesConstant.CPL_70.equalsIgnoreCase(characteristicsDto.getCdCharacCode())
							&& (ServiceConstants.GENERIC_END_DATE.equals(characteristicsDto.getDtCharacEnd())
									|| !ObjectUtils.isEmpty(characteristicsDto.getDtCharacEnd()))) {
						commonApplicationDto.setIndChildPrblmSexualBhvr(Boolean.TRUE);
					}
				}
			}
			// Retrieves the child Education detail informations
			List<EducationHistoryDto> educationHistoryDtoList = commonApplicationDao
					.getEducationDtls(stagePersonLinkCaseDto.getIdPerson());
			if (!ObjectUtils.isEmpty(educationHistoryDtoList)) {
				commonApplicationDto.setEducationHistoryDto(educationHistoryDtoList.get(ServiceConstants.Zero));
				if (educationHistoryDtoList.size() > ServiceConstants.One && !ObjectUtils.isEmpty(educationHistoryDtoList.get(ServiceConstants.One))
						&& ObjectUtils.isEmpty(educationHistoryDtoList.get(ServiceConstants.One).getIdResource())) {
					String schoolAddr = !ObjectUtils
							.isEmpty(educationHistoryDtoList.get(ServiceConstants.One).getAddrEdHistStreetLn1())
									? educationHistoryDtoList.get(ServiceConstants.One).getAddrEdHistStreetLn1()
									: ServiceConstants.EMPTY_STRING.concat(!ObjectUtils.isEmpty(
											educationHistoryDtoList.get(ServiceConstants.One).getAddrEdHistStreetLn2())
													? educationHistoryDtoList.get(ServiceConstants.One)
															.getAddrEdHistStreetLn2()
													: ServiceConstants.EMPTY_STRING);
					String schoolCity = !ObjectUtils
							.isEmpty(educationHistoryDtoList.get(ServiceConstants.One).getAddrEdHistCity())
									? educationHistoryDtoList.get(ServiceConstants.One).getAddrEdHistCity()
									: ServiceConstants.EMPTY_STRING;
					String schoolState = !ObjectUtils
							.isEmpty(educationHistoryDtoList.get(ServiceConstants.One).getAddrEdHistState())
									? educationHistoryDtoList.get(ServiceConstants.One).getAddrEdHistState()
									: CodesConstant.CSTATE_TX;
					commonApplicationDto.setPrevSchoolAddr(schoolAddr.concat(ServiceConstants.COMMA).concat(schoolCity)
							.concat(ServiceConstants.COMMA).concat(schoolState));
					commonApplicationDto
							.setPrevSchoolCntct(educationHistoryDtoList.get(ServiceConstants.One).getEdHistPhone());
				} else {
					if ((educationHistoryDtoList.size() > ServiceConstants.One && !ObjectUtils.isEmpty(educationHistoryDtoList.get(ServiceConstants.One)) && !ObjectUtils
							.isEmpty(educationHistoryDtoList.get(ServiceConstants.One).getIdResource()))) {
						RsrcAddrPhoneDto rsrcAddrPhoneDto = commonApplicationDao
								.getRsrcAddrPhoneDtl(educationHistoryDtoList.get(ServiceConstants.One).getIdResource());
						if (!ObjectUtils.isEmpty(rsrcAddrPhoneDto)) {
							String schoolAddr = !ObjectUtils.isEmpty(rsrcAddrPhoneDto.getAddrRsrcAddrStLn1())
									? rsrcAddrPhoneDto.getAddrRsrcAddrStLn1()
									: ServiceConstants.EMPTY_STRING
											.concat(!ObjectUtils.isEmpty(rsrcAddrPhoneDto.getAddrRsrcAddrStLn2())
													? rsrcAddrPhoneDto.getAddrRsrcAddrStLn2()
													: ServiceConstants.EMPTY_STRING);
							String schoolCity = !ObjectUtils.isEmpty(rsrcAddrPhoneDto.getAddrRsrcAddrCity())
									? rsrcAddrPhoneDto.getAddrRsrcAddrCity() : ServiceConstants.EMPTY_STRING;
							String schoolState = !ObjectUtils.isEmpty(rsrcAddrPhoneDto.getCdRsrcAddrState())
									? rsrcAddrPhoneDto.getCdRsrcAddrState() : ServiceConstants.EMPTY_STRING;
							commonApplicationDto.setPrevSchoolAddr(schoolAddr.concat(ServiceConstants.COMMA)
									.concat(schoolCity).concat(ServiceConstants.COMMA).concat(schoolState));
							commonApplicationDto.setPrevSchoolCntct(rsrcAddrPhoneDto.getNbrRsrcPhone());
						}
					}
				}
			}

			// Retrieves the Resources detail informations
			if (!ObjectUtils.isEmpty(educationHistoryDtoList)
					&& !ObjectUtils.isEmpty(educationHistoryDtoList.get(ServiceConstants.Zero).getIdResource())) {
				RsrcAddrPhoneDto rsrcAddrPhoneDto = commonApplicationDao
						.getRsrcAddrPhoneDtl(educationHistoryDtoList.get(ServiceConstants.Zero).getIdResource());
				commonApplicationDto.setRsrcAddrPhoneDto(rsrcAddrPhoneDto);
			}

			List<VisitationPlanInfoDtlsDto> visitationPlanInfoDtlsDtoLst = commonApplicationDao
					.getVisitationPlanInfoDtl(stagePersonLinkCaseDto.getIdPerson(), commonApplicationReq.getIdStage());
			commonApplicationDto.setVisitationPlanInfoDtlsDtoLst(visitationPlanInfoDtlsDtoLst);
		}
		// Retrieves the pricipal parents family History information for the
		// child - Call CLSC01D
		List<CaseInfoDto> caseInfoDtoList = populateLetterDao.getCaseInfoById(commonApplicationReq.getIdStage(),
				ServiceConstants.PRINCIPAL);
		commonApplicationDto.setCaseInfoDtolist(caseInfoDtoList);

		commonApplicationDto.setStagePersonLinkCaseDto(stagePersonLinkCaseDto);
		// Calling Prefill Data Implementation
		logger.info("TransactionId :" + commonApplicationReq.getTransactionId());
		return placementApplicationPrefillData.returnPrefillData(commonApplicationDto);
	}

	/**
	 * Method Name: getFceApplicationDto. Method Description: This method is
	 * used to populate the value from Entity to Dto.
	 * 
	 * @param fceApplication
	 * @return FceApplicationDto
	 */
	private FceApplicationDto getFceApplicationDto(FceApplication fceApplication) {
		FceApplicationDto fceApplicationDto = new FceApplicationDto();
		if (!ObjectUtils.isEmpty(fceApplication)) {
			fceApplicationDto.setIdFceApplication(fceApplication.getIdFceApplication());
			fceApplicationDto.setIdEvent(fceApplication.getEvent().getIdEvent());
			fceApplicationDto.setIdPerson(fceApplication.getPersonByIdPerson().getIdPerson());
			fceApplicationDto.setIdFceEligibility(fceApplication.getFceEligibility().getIdFceEligibility());
			fceApplicationDto.setAddrRemovalStLn1(fceApplication.getAddrRemovalStLn1());
			fceApplicationDto.setAddrRemovalStLn2(fceApplication.getAddrRemovalStLn2());
			fceApplicationDto.setAddrRemovalCity(fceApplication.getAddrRemovalCity());
			fceApplicationDto.setAddrRemovalAddrZip(fceApplication.getAddrRemovalAddrZip());
		}
		return fceApplicationDto;
	}
	
	/**
	 * Method Description: This method is used to retrieve the placement
	 * application form Status and CSA page latest value. 
	 * 
	 * @param commonApplicationReq
	 * @return PreFillDataServiceDto
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
			Exception.class })
	public CommonFormRes getCommonAppStatusAndCSADtls(CommonApplicationReq commonApplicationReq) {
		CommonFormRes commonFormRes = new CommonFormRes();
		EventDto eventDto = eventDao.getEventByid(commonApplicationReq.getIdEvent());
		commonFormRes.setEventStatus(eventDto.getCdEventStatus());
		StagePersonLinkCaseDto stagePersonLinkCaseDto = commonApplicationDao
				.getStagePersonCaseDtl(commonApplicationReq.getIdStage(), ServiceConstants.PRIMARY_CHILD);
		if (!ObjectUtils.isEmpty(stagePersonLinkCaseDto)
				&& !ObjectUtils.isEmpty(stagePersonLinkCaseDto.getIdPerson())) {
			// Retrieves the most recent child CSA Episode details
			CSAEpisodeDto csaEpisodeDto = commonApplicationDao.getCSAEpisodesDtl(stagePersonLinkCaseDto.getIdPerson());
			// Set Child CSA behavior as True if Episode start date is entered
			// and episode end is empty or else set as False.
			if (!ObjectUtils.isEmpty(csaEpisodeDto)) {
				if (!ObjectUtils.isEmpty(csaEpisodeDto.getDtEpisodeStart())
						&& ObjectUtils.isEmpty(csaEpisodeDto.getDtEpisodeEnd()))
					commonFormRes.setChildCSABhvr(ServiceConstants.YES_TEXT);
				List<CsaEpisodesIncdntDto> csaEpisodesIncdntDtoLst = commonApplicationDao
						.getCSAEpisodesIncdntDtl(csaEpisodeDto.getIdCsaEpisodes());
				if (!ObjectUtils.isEmpty(csaEpisodeDto) && !ObjectUtils.isEmpty(csaEpisodeDto.getDtEpisodeStart())) {
					commonFormRes.setCsaEpisodeStartDt(TypeConvUtil.formDateFormat(csaEpisodeDto.getDtEpisodeStart()));
				}
				if (!ObjectUtils.isEmpty(csaEpisodesIncdntDtoLst)
						&& csaEpisodesIncdntDtoLst.size() > ServiceConstants.Zero) {
					StringBuffer csaEpisodeIncdntDtl = new StringBuffer();
					for (CsaEpisodesIncdntDto csaEpisodesIncdntDto : csaEpisodesIncdntDtoLst) {
						String incdntDtls = DT_INCDNT
								.concat(TypeConvUtil.formDateFormat(csaEpisodesIncdntDto.getDtIncdnt()))
								.concat(ServiceConstants.EMPTY_LINE).concat(INCDNT_DESCRPTN)
								.concat(!ObjectUtils.isEmpty(csaEpisodesIncdntDto.getTxtIncdntDesc())
										? csaEpisodesIncdntDto.getTxtIncdntDesc() : ServiceConstants.EMPTY_STRING)
								.concat(ServiceConstants.EMPTY_LINE).concat(ServiceConstants.EMPTY_LINE);
						csaEpisodeIncdntDtl.append(incdntDtls);
					}
					commonFormRes.setCsaEpisodesIncdntDtl(csaEpisodeIncdntDtl.toString());
				}
			}
			// Added the code to show the refresh data for child problematic
			// sexual behavior and episode incident summary detail for warranty
			// defect 11524
			// Retrieves the child characteristic information
			List<CharacteristicsDto> personCharList = personDao
					.fetchPersonCharaceristics(stagePersonLinkCaseDto.getIdPerson());
			if (!ObjectUtils.isEmpty(personCharList) && personCharList.size() > ServiceConstants.Zero_INT) {
				for (CharacteristicsDto characteristicsDto : personCharList) {
					if (CodesConstant.CPL.equalsIgnoreCase(characteristicsDto.getCdCharacCategory())
							&& CodesConstant.CPL_70.equalsIgnoreCase(characteristicsDto.getCdCharacCode())
							&& (ServiceConstants.GENERIC_END_DATE.equals(characteristicsDto.getDtCharacEnd())
									|| !ObjectUtils.isEmpty(characteristicsDto.getDtCharacEnd()))) {
						commonFormRes.setChildCSAPrblmtcBhvr(ServiceConstants.YES_TEXT);
					}
				}
			}

			// Retrieves the most recent child CSA Episode Closed details
			List<CSAEpisodeDto> csaEpisodeClosedDtoLst = commonApplicationDao
					.getCSAEpisodesClosedDtl(stagePersonLinkCaseDto.getIdPerson());
			if (!ObjectUtils.isEmpty(csaEpisodeClosedDtoLst)) {
				List<Long> idCsaEpisodeLst = new ArrayList<Long>();
				for (CSAEpisodeDto csaEpisodeClosedDto : csaEpisodeClosedDtoLst) {
					idCsaEpisodeLst.add(csaEpisodeClosedDto.getIdCsaEpisodes());
				}
				List<CsaEpisodesIncdntDto> csaEpisodesClosedIncdntDtoLst = commonApplicationDao
						.getCSAEpisodesClosedIncdntDtl(idCsaEpisodeLst);
				// Populate the description behavior, when it happened, and how
				// it was
				// managed
				if (!ObjectUtils.isEmpty(csaEpisodesClosedIncdntDtoLst) && !ObjectUtils.isEmpty(csaEpisodeClosedDtoLst)
						&& csaEpisodeClosedDtoLst.size() > ServiceConstants.Zero
						&& csaEpisodesClosedIncdntDtoLst.size() > ServiceConstants.Zero) {
					StringBuffer incdntSummaryDtls = new StringBuffer();
					for (CSAEpisodeDto csaEpisodeClosedDto : csaEpisodeClosedDtoLst) {
						List<CsaEpisodesIncdntDto> csaEpisodesClosedIncdntLst = new ArrayList<CsaEpisodesIncdntDto>();
						csaEpisodesClosedIncdntLst = csaEpisodesClosedIncdntDtoLst.stream().filter(
								child -> child.getIdCsaEpisodes().equals(csaEpisodeClosedDto.getIdCsaEpisodes()))
								.collect(Collectors.toList());
						for (CsaEpisodesIncdntDto csaEpisodesClosedIncdntDto : csaEpisodesClosedIncdntLst) {
							String incdntDtls = DT_EPISODE_START
									.concat(TypeConvUtil.formDateFormat(csaEpisodeClosedDto.getDtEpisodeStart()))
									.concat(ServiceConstants.EMPTY_LINE).concat(DT_EPISODE_END)
									.concat(TypeConvUtil.formDateFormat(csaEpisodeClosedDto.getDtEpisodeEnd()))
									.concat(ServiceConstants.EMPTY_LINE).concat(DT_INCDNT)
									.concat(TypeConvUtil.formDateFormat(csaEpisodesClosedIncdntDto.getDtIncdnt()))
									.concat(ServiceConstants.EMPTY_LINE).concat(INCDNT_DESCRPTN)
									.concat(!ObjectUtils.isEmpty(csaEpisodesClosedIncdntDto.getTxtIncdntDesc())
											? csaEpisodesClosedIncdntDto.getTxtIncdntDesc()
											: ServiceConstants.EMPTY_STRING)
									.concat(ServiceConstants.EMPTY_LINE).concat(ServiceConstants.EMPTY_LINE);
							incdntSummaryDtls.append(incdntDtls);
						}
					}
					commonFormRes.setCsaEpisodesIncdntSummaryDtl(!ObjectUtils.isEmpty(incdntSummaryDtls)
							? incdntSummaryDtls.toString() : ServiceConstants.EMPTY_STRING);
				}
			}
		}

		return commonFormRes;
	}

}
