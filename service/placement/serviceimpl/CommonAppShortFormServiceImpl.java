package us.tx.state.dfps.service.placement.serviceimpl;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import gov.texas.dfps.api.notification.client.NotificationApi;
import gov.texas.dfps.api.notification.client.model.Event;
import gov.texas.dfps.api.notification.client.model.EventAction;
import gov.texas.dfps.api.notification.client.model.EventObject;
import us.tx.state.dfps.common.domain.CaExCommonApplication;
import us.tx.state.dfps.common.domain.CaExEducationService;
import us.tx.state.dfps.common.domain.CaExHospitalization;
import us.tx.state.dfps.common.domain.CaExMedication;
import us.tx.state.dfps.common.domain.CaExPlacementLog;
import us.tx.state.dfps.common.domain.CaExReturnFromRunaway;
import us.tx.state.dfps.common.domain.CaExServicesProvided;
import us.tx.state.dfps.common.domain.CaExSiblings;
import us.tx.state.dfps.common.domain.CaExSpecialProgramming;
import us.tx.state.dfps.common.domain.CaExSubstanceUse;
import us.tx.state.dfps.populateletter.dto.CaseInfoDto;
import us.tx.state.dfps.service.admin.dao.EmployeeDao;
import us.tx.state.dfps.service.admin.dao.EventIdDao;
import us.tx.state.dfps.service.admin.dao.LegalStatusPersonMaxStatusDtDao;
import us.tx.state.dfps.service.admin.dto.EventIdInDto;
import us.tx.state.dfps.service.admin.dto.EventIdOutDto;
import us.tx.state.dfps.service.admin.dto.LegalStatusPersonMaxStatusDtInDto;
import us.tx.state.dfps.service.admin.dto.LegalStatusPersonMaxStatusDtOutDto;
import us.tx.state.dfps.service.admin.dto.PersonEthnicityDto;
import us.tx.state.dfps.service.admin.dto.PersonRaceDto;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.UnitDao;
import us.tx.state.dfps.service.common.request.CommonAppShortFormReq;
import us.tx.state.dfps.service.common.response.CommonAppShortFormRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.conservatorship.dto.CnsrvtrshpRemovalDto;
import us.tx.state.dfps.service.exception.ServiceLayerException;
import us.tx.state.dfps.service.fce.dto.FceApplicationDto;
import us.tx.state.dfps.service.fce.dto.FceEligibilityDto;
import us.tx.state.dfps.service.fcl.service.SexualVictimizationHistoryService;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.forms.util.CommonApplShortFormPrefillData;
import us.tx.state.dfps.service.lookup.dao.LookupDao;
import us.tx.state.dfps.service.pca.dto.PlacementDtlDto;
import us.tx.state.dfps.service.person.dao.PersonDao;
import us.tx.state.dfps.service.person.dao.PersonEthnicityDao;
import us.tx.state.dfps.service.person.dao.PersonRaceDao;
import us.tx.state.dfps.service.person.dao.TraffickingDao;
import us.tx.state.dfps.service.person.dto.ServicePackageDtlDto;
import us.tx.state.dfps.service.person.dao.ServicePackageDao;
import us.tx.state.dfps.service.person.dto.TraffickingDto;
import us.tx.state.dfps.service.person.dto.UnitDto;
import us.tx.state.dfps.service.placement.dao.CommonApplicationDao;
import us.tx.state.dfps.service.placement.dao.CommonApplicationShortFormDao;
import us.tx.state.dfps.service.placement.dto.CommonApplShortFormPrintDto;
import us.tx.state.dfps.service.placement.dto.CommonApplicationShortFormDto;
import us.tx.state.dfps.service.placement.dto.PersonLocDto;
import us.tx.state.dfps.service.placement.dto.ShortFormCsaEpisodeIncdntsDto;
import us.tx.state.dfps.service.placement.dto.ShortFormEducationSrvDto;
import us.tx.state.dfps.service.placement.dto.ShortFormMedicationDto;
import us.tx.state.dfps.service.placement.dto.ShortFormPsychiatricDto;
import us.tx.state.dfps.service.placement.dto.ShortFormRtnRunawayDto;
import us.tx.state.dfps.service.placement.dto.ShortFormSiblingsDto;
import us.tx.state.dfps.service.placement.dto.ShortFormSpecialProgrammingDto;
import us.tx.state.dfps.service.placement.dto.ShortFormTherapyDto;
import us.tx.state.dfps.service.placement.dto.StagePersonLinkCaseDto;
import us.tx.state.dfps.service.placement.service.CommonAppShortFormService;
import us.tx.state.dfps.service.populateletter.dao.PopulateLetterDao;
import us.tx.state.dfps.service.workload.dao.AssignDao;
import us.tx.state.dfps.service.workload.dao.WorkLoadDao;
import us.tx.state.dfps.service.workload.dto.AssignmentGroupDto;
import us.tx.state.dfps.service.workload.dto.EmployeePersPhNameDto;
import us.tx.state.dfps.service.workload.dto.PersonDto;
import us.tx.state.dfps.web.fcl.dto.SexualVictimHistoryDto;
import us.tx.state.dfps.web.fcl.dto.SexualVictimIncidentDto;
import us.tx.state.dfps.web.placement.bean.ShortFormSubstanceDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:CommonApplicationController will have all operation which are
 * mapped to Placement module. Feb 9, 2018- 2:13:13 PM © 2017 Texas Department
 * of Family and Protective Services
 *********************************  Change History *********************************
 * 06/18/2019  muddur    artf113241 : Interoperability Project initial. 
 * 01/08/2020  anantj    artf113794 : Changes for Common App Short Form Sub-tables for Webservice API. 
 * 01/15/2020  thompswa  artf113241 : add getCommonApplShortForm for print. 
 */
@Service
@Transactional
public class CommonAppShortFormServiceImpl implements CommonAppShortFormService {

	@Autowired
	private CommonApplicationDao commonApplicationDao;

	@Autowired
	private CommonApplicationShortFormDao commonApplicationShortFormDao;

	@Autowired
	private WorkLoadDao workLoadDao;

	@Autowired
	private EmployeeDao employeeDao;

	@Autowired
	private UnitDao unitDao;

	@Autowired
	private PersonDao personDao;

	@Autowired
	private PersonEthnicityDao personEthnicityDao;

	@Autowired
	LookupDao lookupDao;

	@Autowired
	private PersonRaceDao personRaceDao;
	
	@Autowired
	private LegalStatusPersonMaxStatusDtDao legalStatusPersonMaxStatusDtDao;
	
	@Autowired
	EventIdDao eventIdDao;
	
	@Autowired
	private PopulateLetterDao populateLetterDao;
	
	@Autowired
	private AssignDao assignDao;
	
	@Autowired
	NotificationApi notificationApi;
	
	@Autowired
	SexualVictimizationHistoryService sexualVictimizationHistoryService;
	
	@Autowired
	TraffickingDao trfckngDao;

	@Autowired
	private CommonApplShortFormPrefillData commonApplShortFormPrefillData;

	@Autowired
	private ServicePackageDao servicePackageDao;

	private static final Logger log = Logger.getLogger(CommonAppShortFormServiceImpl.class);
	
	DateFormat formDateFormatter = new SimpleDateFormat("MM/dd/yyyy");


	@Override
	public StagePersonLinkCaseDto retrieveHeaderInfo(Long stageId) {
		StagePersonLinkCaseDto stagePersonLinkCaseDto = commonApplicationDao.getStagePersonCaseDtl(stageId, ServiceConstants.PRIMARY_CHILD);

		return stagePersonLinkCaseDto;
	}
	
	@Override
	public PersonLocDto retrieveLevelOfCare(Long personId) {
		PersonLocDto servicelvlPersonLocDto = commonApplicationShortFormDao
				.getServiceLevelInfo(personId);

		return servicelvlPersonLocDto;
	}

	public EmployeePersPhNameDto getSupervisorInfo(Long supervisorId) {

		EmployeePersPhNameDto employeePersSuprvsrDto = employeeDao.searchPersonPhoneName(supervisorId);
		return employeePersSuprvsrDto;

	}

	@Override
	public UnitDto getUnitInfo(Long unitId) {

		UnitDto unitDto = unitDao.searchUnitDtls(unitId);

		return unitDto;
	}

	public EmployeePersPhNameDto geCaseworkerInfo(Long stageId) {
		Long idPersonCaseWrkr = workLoadDao.getPersonIdByRole(stageId, ServiceConstants.STAGE_PERS_ROLE_PR);
		if (!ObjectUtils.isEmpty(idPersonCaseWrkr)) {

			EmployeePersPhNameDto employeePersCsWrkrDto = employeeDao.searchPersonPhoneName(idPersonCaseWrkr);

			return employeePersCsWrkrDto;
		}
		return null;
	}
	
	@Override
	public PersonDto getChildInfo(Long personId) {
		return personDao.getPersonById(personId);
	}

	@Override
	public FceEligibilityDto getCitizenInfo(Long personId) {
		return commonApplicationDao.getFceEligibility(personId);

	}
	
	@Override
	public List<PlacementDtlDto> getPlacementLog(Long personId) {
		List<PlacementDtlDto> plcmntLogDtlLst = commonApplicationDao
				.getPlacementLogDtl(personId);
		
		return plcmntLogDtlLst;

	}
	
	@Override
	public List<ShortFormSiblingsDto> getSiblingsList(Long stageId,  CommonApplicationShortFormDto shortFormDto) {
		List<CaseInfoDto> siblingsList = populateLetterDao.getCaseInfoById(stageId,
				ServiceConstants.PRINCIPAL);
	
		return convertSiblingsToDto(siblingsList, null != shortFormDto ? shortFormDto.getIdCaExCommonApplication() : null);

	}
	
	private Map<Long,CaExSiblings> convertcaExSiblingsToDto(List<CaExSiblings> caExSibling) {
		Map<Long,CaExSiblings> map = new HashMap<Long,CaExSiblings>();
		if(null != caExSibling && caExSibling.size() > 0){
			for(CaExSiblings caEx : caExSibling){
				map.put(caEx.getIdPerson(), caEx);
			}
		}
		
		return map;
	}
	
	private List<ShortFormSiblingsDto> convertSiblingsToDto(List<CaseInfoDto> siblingsList, Long caExId) {
		List<ShortFormSiblingsDto> siblingLst = new ArrayList<ShortFormSiblingsDto>();
		for(CaseInfoDto caseInfo : siblingsList){
			if (CodesConstant.CRPTRINT_SB.equalsIgnoreCase(caseInfo.getCdStagePersRelInt())) {
				ShortFormSiblingsDto sibling = new ShortFormSiblingsDto();
				
				sibling.setSiblingsName(TypeConvUtil.formatFullName(caseInfo.getNmNameFirst(), caseInfo.getNmNameMiddle(),
						caseInfo.getNmNameLast()));
				//sibling.setDob(TypeConvUtil.formDateFormat(caseInfo.getDtPersonBirth()));
				sibling.setDob(caseInfo.getDtPersonBirth());
				String siblingAddr = !ObjectUtils.isEmpty(caseInfo.getAddrPersAddrStLn1())
						? caseInfo.getAddrPersAddrStLn1()
						: ServiceConstants.EMPTY_STRING
								.concat(!ObjectUtils.isEmpty(caseInfo.getAddrPersAddrStLn2())
										? caseInfo.getAddrPersAddrStLn2() : ServiceConstants.EMPTY_STRING)
								.concat(!ObjectUtils.isEmpty(caseInfo.getAddrPersonAddrCity())
										? caseInfo.getAddrPersonAddrCity() : ServiceConstants.EMPTY_STRING)
								.concat(!ObjectUtils.isEmpty(caseInfo.getCdPersonAddrState())
										? caseInfo.getCdPersonAddrState() : ServiceConstants.EMPTY_STRING)
								.concat(!ObjectUtils.isEmpty(caseInfo.getAddrPersonAddrZip())
										? caseInfo.getAddrPersonAddrZip() : ServiceConstants.EMPTY_STRING);
				
				sibling.setAddress(siblingAddr);
				sibling.setPersonId(caseInfo.getIdPerson());
				
				if(null != caExId){
					List<CaExSiblings> caExSibling = commonApplicationShortFormDao.retrieveSiblingList(caExId);
					if(null != caExSibling && caExSibling.size() > 0){
						Map<Long,CaExSiblings> lst = convertcaExSiblingsToDto(caExSibling);
						//[artf162683] Defect: 15365 - 2087 short Common App 8888
						if(null != lst.get(caseInfo.getIdPerson())){
							sibling.setDfpsCare(lst.get(caseInfo.getIdPerson()).getIndInDfpsCare());
						}
					}
				}
				
				siblingLst.add(sibling);
			}
			
		}
		
		return siblingLst;
	}

	@Override
	public List<LegalStatusPersonMaxStatusDtOutDto> getLegalCounty(Long personId){
		
		LegalStatusPersonMaxStatusDtInDto childLegalStatusDto = new LegalStatusPersonMaxStatusDtInDto();
		childLegalStatusDto.setIdPerson(personId);
		List<LegalStatusPersonMaxStatusDtOutDto> childLegalStatusDtoList = legalStatusPersonMaxStatusDtDao
				.getRecentLegelStatusRecord(childLegalStatusDto);
		
		return childLegalStatusDtoList;
	}

	@Override
	public String getEthnicityInfo(Long personId) {
		List<PersonEthnicityDto> personEthnicityDtoList = personEthnicityDao.getPersonEthnicityByPersonId(personId);
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
			return ethinicityString.toString();
		}
		return null;
	}

	@Override
	public String getRaceInfo(Long personId) {
		List<PersonRaceDto> personRaceDtoList = personRaceDao.getPersonRaceByPersonId(personId);
		if (!ObjectUtils.isEmpty(personRaceDtoList) && personRaceDtoList.size() > ServiceConstants.Zero) {
			StringBuffer raceString = new StringBuffer();
			int raceCount = ServiceConstants.Zero;
			for (PersonRaceDto personRaceDto : personRaceDtoList) {
				String raceDecode = lookupDao.simpleDecodeSafe(CodesConstant.CRACE, personRaceDto.getCdPersonRace());
				if (raceCount > ServiceConstants.Zero) {
					raceString.append(ServiceConstants.COMMA);
				}
				raceString.append(raceDecode);
				raceCount++;
			}
			return raceString.toString();
		}
		return null;
	}
	
	@Override
	public FceApplicationDto getRemovalAddr(Long personId){
		return commonApplicationShortFormDao.getRemovalAddr(personId);
	}
	
	@Override
	public CnsrvtrshpRemovalDto getRemovalDate(Long personId){
		return commonApplicationShortFormDao.getRemovalDate(personId);
	}
	
	@Override
	public List<ShortFormCsaEpisodeIncdntsDto> getCSAEpisodeIncdntDtls(Long personId){
		return commonApplicationShortFormDao.getCSAEpisodeIncdntDtls(personId);
	}
	
	@Override
	public SexualVictimHistoryDto getSexualVictimization(Long personId){
		return sexualVictimizationHistoryService.getSexualVictimHistoryDto(personId);
	}
	
	@Override
	public List<SexualVictimIncidentDto> getSFSexualVictimization(Long idCaEx) {
		
		if(null != idCaEx && idCaEx > 0){
			List<SexualVictimIncidentDto> caExSexualVictim = commonApplicationShortFormDao.getSFSexualVictimization(idCaEx);
			return caExSexualVictim;
		}
		return null;
	}
	
	@Override
	public List<TraffickingDto> getSFTraffickingHistory(Long idCaEx) {
		
		if(null != idCaEx && idCaEx > 0){
			List<TraffickingDto> caExTrafficking = commonApplicationShortFormDao.getSFTraffickingHistory(idCaEx);
			return caExTrafficking;
		}
		return null;
	}
	
	@Override
	public List<ShortFormCsaEpisodeIncdntsDto> getSFEpisodeIncidents(Long idCaEx) {
		
		if(null != idCaEx && idCaEx > 0){
			List<ShortFormCsaEpisodeIncdntsDto> caExEpisodeIncidents = commonApplicationShortFormDao.getSFEpisodeIncidents(idCaEx);
			return caExEpisodeIncidents;
		}
		return null;
	}
	
	

	@Override
	public List<TraffickingDto> getTrafficking(Long personId){
		TraffickingDto traffickingDto=new TraffickingDto();
		traffickingDto.setIdPerson(BigDecimal.valueOf(personId));
		List<TraffickingDto> trfckngDtoLst = trfckngDao.getTraffickingList(traffickingDto);
		
		return trfckngDtoLst;
	}
	
	
	
	@Override
	public CommonApplicationShortFormDto getCommonAppShortFormById(Long caExCommonApplicationId) {

		CaExCommonApplication caExCommonApplication = commonApplicationShortFormDao
				.retrieveCommonById(caExCommonApplicationId);
		
		return caExCommonApplication!=null ? covertEntityToDTO(caExCommonApplication): null;
	}

	@Override
	public CommonApplicationShortFormDto getCommonAppShortFormByEventId(Long eventId) {
		
		if(eventId > 0){
			CaExCommonApplication caExCommonApplication = commonApplicationShortFormDao.retrieveCommonByEventId(eventId);
			return caExCommonApplication!=null ? covertEntityToDTO(caExCommonApplication): null;
		}
		return null;
	}
	
	@Override
	public ShortFormSpecialProgrammingDto getSplProgByCaExId(Long idCaEx) {
		
		if(null != idCaEx && idCaEx > 0){
			List<CaExSpecialProgramming> caExSpecialProgramming = commonApplicationShortFormDao.retrieveSplProgById(idCaEx);
			return covertSplProgToDTO(caExSpecialProgramming);
		}
		return null;
	}
	
	@Override
	public ShortFormRtnRunawayDto getRtnRunawayByCaExId(Long idCaEx) {
		
		if(null != idCaEx && idCaEx > 0){
			List<CaExReturnFromRunaway> caExRtnRunawayg = commonApplicationShortFormDao.retrieveReturnRunaway(idCaEx);
			return covertRunawayToDTO(caExRtnRunawayg);
		}
		return null;
	}
	
	@Override
	public ShortFormEducationSrvDto getEduSrvByCaExId(Long idCaEx) {
		
		if(null != idCaEx && idCaEx > 0){
			List<CaExEducationService> caExEduSrv = commonApplicationShortFormDao.retrieveEducationService(idCaEx);
			return covertEduServToDTO(caExEduSrv);
		}
		return null;
	}
	
	@Override
	public List<ShortFormSiblingsDto> getSiblingsByCaExId(Long idCaEx) {
		
		if(null != idCaEx && idCaEx > 0){
			List<ShortFormSiblingsDto> siblingsList = commonApplicationShortFormDao.retrieveSiblingListByExId(idCaEx);
			return siblingsList;
		}
		return null;
	}
	
	@Override
	public List<ShortFormMedicationDto> getMedicationByCaExId(Long idCaEx) {
		
		if(null != idCaEx && idCaEx > 0){
			List<CaExMedication> caExMedication = commonApplicationShortFormDao.retrieveMedicationLst(idCaEx);
			return covertMedicationToDTO(caExMedication);
		}
		return null;
	}
	
	@Override
	public List<ShortFormTherapyDto> getTherapyDtlByCaExId(Long idCaEx) {
		
		if(null != idCaEx && idCaEx > 0){
			List<CaExServicesProvided> caExTherapy = commonApplicationShortFormDao.retrieveTherapyLst(idCaEx);
			return covertTherapyToDTO(caExTherapy);
		}
		return null;
	}

	private List<ShortFormTherapyDto> covertTherapyToDTO(List<CaExServicesProvided> caExTherapy) {
		List<ShortFormTherapyDto> therapyList = new ArrayList<ShortFormTherapyDto>();
		if(null != caExTherapy && caExTherapy.size() > 0){
			for(CaExServicesProvided  caExsrv : caExTherapy){
				ShortFormTherapyDto therapyDto = new ShortFormTherapyDto();
				
				therapyDto.setAgencyName(caExsrv.getNmAgencyProvidedService());
				therapyDto.setTherapy(caExsrv.getCdTherapyType());
				therapyDto.setDtStart(caExsrv.getDtStartService());
				therapyDto.setFrequency(caExsrv.getTxtFrequencyOfTherapy());
				therapyDto.setOtherTxt(caExsrv.getTxtOtherTherapy());
				
				therapyList.add(therapyDto);
				
			}
		}
		return therapyList;
	}
	
	@Override
	public List<ShortFormPsychiatricDto> getHospitalizationByCaExId(Long idCaEx) {
		
		if(null != idCaEx && idCaEx > 0){
			List<CaExHospitalization> caExHospitalization = commonApplicationShortFormDao.retrieveHospitalizationByExId(idCaEx);
			return covertHospitalizationToDTO(caExHospitalization);
		}
		return null;
	}
                                                                                         
	private List<ShortFormPsychiatricDto> covertHospitalizationToDTO(List<CaExHospitalization> caExHospitalization) {
		List<ShortFormPsychiatricDto> psychiatricList = new ArrayList<ShortFormPsychiatricDto>();
		for(CaExHospitalization hosp : caExHospitalization){
			ShortFormPsychiatricDto dto = new ShortFormPsychiatricDto();
			if(null != hosp.getDtHospitalization()){
				dto.setDtHospitalized(hosp.getDtHospitalization());
			}
			dto.setLenHospitalized(hosp.getTxtLengthOfHospitalization());
			dto.setBehvAdmitted(hosp.getTxtAdmittedForBehavior());
			
			psychiatricList.add(dto);
		}
		
		return psychiatricList;
	}

	private List<ShortFormMedicationDto> covertMedicationToDTO(List<CaExMedication> caExMedication) {
		List<ShortFormMedicationDto> medList = new ArrayList<ShortFormMedicationDto>();
		for(CaExMedication  caExMed : caExMedication){
			ShortFormMedicationDto medDto = new ShortFormMedicationDto();
			
			medDto.setMedName(caExMed.getNmMedication());
			medDto.setDosage(caExMed.getTxtDosage());
			medDto.setFrequency(caExMed.getTxtFrequency());
			if(null != caExMed.getDtPrescribed()){
				medDto.setDtPrescribed(caExMed.getDtPrescribed());
			}
			medDto.setCondition(caExMed.getTxtTreatingCondition());
			
			medList.add(medDto);
			
		}
		return medList;
	}

	private ShortFormEducationSrvDto covertEduServToDTO(List<CaExEducationService> caExEduSrv) {
		ShortFormEducationSrvDto dto = new ShortFormEducationSrvDto();
		
		for(CaExEducationService eduSrv : caExEduSrv){
			
			switch(eduSrv.getCdEducationServiceType()){
			
				case ServiceConstants.SF_EDU_REGCLS:
					dto.setRegularClass(eduSrv.getCdEducationServiceType());
					break;
				case ServiceConstants.SF_EDU_BILSSL:
					dto.setBilingualESL(eduSrv.getCdEducationServiceType());
					break;
				case ServiceConstants.SF_EDU_GIFTED:
					dto.setGiftedTalented(eduSrv.getCdEducationServiceType());
					break;
				case ServiceConstants.SF_EDU_SLFCNT:
					dto.setSelfContained(eduSrv.getCdEducationServiceType());
					break;
				case ServiceConstants.SF_EDU_SPLTRAN:
					dto.setSplTransport(eduSrv.getCdEducationServiceType());
					break;
				case ServiceConstants.SF_EDU_CRDRECV:
					dto.setCreditRecovery(eduSrv.getCdEducationServiceType());
					break;
				case ServiceConstants.SF_EDU_VOCTNL:
					dto.setVocational(eduSrv.getCdEducationServiceType());
					break;
				case ServiceConstants.SF_EDU_CNSLSVC:
					dto.setCounselingSrv(eduSrv.getCdEducationServiceType());
					break;
				case ServiceConstants.SF_EDU_ADVPLC:
					dto.setAdvPlacement(eduSrv.getCdEducationServiceType());
					break;
				case ServiceConstants.SF_EDU_SPLEDU:
					dto.setSplEducation(eduSrv.getCdEducationServiceType());
					break;
				case ServiceConstants.SF_EDU_DAEP:
					dto.setDaepJjaep(eduSrv.getCdEducationServiceType());
					break;
				case ServiceConstants.SF_EDU_OTH:
					dto.setOtherSpec(eduSrv.getCdEducationServiceType());
					dto.setOtherTxt(eduSrv.getTxtOtherEducationService());
					break;
				case ServiceConstants.SF_EDU_SLFPCD:
					dto.setSelfPaced(eduSrv.getCdEducationServiceType());
					break;
				case ServiceConstants.SF_EDU_504MOD:
					dto.setModification504(eduSrv.getCdEducationServiceType());
					break;
			}
		}
	
		return dto;
		
	}

	private ShortFormSpecialProgrammingDto covertSplProgToDTO(List<CaExSpecialProgramming> caExSpecialProgramming) {
		ShortFormSpecialProgrammingDto dto = new ShortFormSpecialProgrammingDto();
		
		for(CaExSpecialProgramming splProg : caExSpecialProgramming){
			switch(splProg.getCdSpecializedProgramming()){
			
				case ServiceConstants.SF_SPL_PROG_PREG:
					dto.setPregnant("Y");
					break;
				case ServiceConstants.SF_SPL_PROG_PARE:
					dto.setParent("Y");
					break;
				case ServiceConstants.SF_SPL_PROG_NONE:
					dto.setNone("Y");
					break;					
			}
		}
		
		return dto;
	}
	
	private ShortFormRtnRunawayDto covertRunawayToDTO(List<CaExReturnFromRunaway> caExRtnRunaway) {
		ShortFormRtnRunawayDto dto = new ShortFormRtnRunawayDto();
		
		for(CaExReturnFromRunaway runaway : caExRtnRunaway){
			
			switch(runaway.getCdReturnFromRunaway()){
				case ServiceConstants.SF_RUNAWAY_VOLTRY:
					dto.setVoluntary(runaway.getCdReturnFromRunaway());
					break;
				case ServiceConstants.SF_RUNAWAY_FACSTF:
					dto.setFacilityStaff(runaway.getCdReturnFromRunaway());
					break;
				case ServiceConstants.SF_RUNAWAY_CPSSTF:
					dto.setCpsStaff(runaway.getCdReturnFromRunaway());
					break;
				case ServiceConstants.SF_RUNAWAY_LLENF:
					dto.setLocalLawEnforcement(runaway.getCdReturnFromRunaway());
					break;
				case ServiceConstants.SF_RUNAWAY_TYCSTF:
					dto.setTycStaff(runaway.getCdReturnFromRunaway());
					break;
				case ServiceConstants.SF_RUNAWAY_JPCSTF:
					dto.setJpcStaff(runaway.getCdReturnFromRunaway());
					break;
				case ServiceConstants.SF_RUNAWAY_ICJSTF:
					dto.setIcjStaff(runaway.getCdReturnFromRunaway());
					break;
				case ServiceConstants.SF_RUNAWAY_OTH:
					dto.setOtherRunaway(runaway.getCdReturnFromRunaway());
					dto.setTxtRunaway(runaway.getTxtOtherReturnRunaway());
					break;
			}
		}
		
		
		return dto;
	}
	
	@Override
	public List<PlacementDtlDto> getPlacementLogByExId(Long caExCommonApplicationId) {

		List<CaExPlacementLog> placementLogList = commonApplicationShortFormDao
				.retrievePlacementLogByExId(caExCommonApplicationId);
		
		return convertPlacementToDto(placementLogList);
	}

	private List<PlacementDtlDto> convertPlacementToDto(List<CaExPlacementLog> placementLogList) {
		List<PlacementDtlDto> plcmtList = new ArrayList<PlacementDtlDto>();
		for(CaExPlacementLog plcmtlist : placementLogList){
			PlacementDtlDto dto = new PlacementDtlDto();
			
			dto.setNmPlcmtFacil(plcmtlist.getNmPlacement());
			dto.setCdPlcmtType(plcmtlist.getCdPlacementType());
			dto.setDtPlcmtStart(plcmtlist.getDtStart());
			dto.setDtPlcmtEnd(plcmtlist.getDtEnd());
			dto.setCdPlcmtLivArr(plcmtlist.getCdPlcmtLivArr());
			
			plcmtList.add(dto);
		}
		return plcmtList;
	}
	
	@Override
	public ShortFormSubstanceDto getSubstanceAbuseByExId(Long caExCommonApplicationId) {

		List<CaExSubstanceUse> substanceList = commonApplicationShortFormDao
				.retrieveSubstanceAbuse(caExCommonApplicationId);
		
		return convertSubstanceAbuseToDto(substanceList);
	}

	private ShortFormSubstanceDto convertSubstanceAbuseToDto(List<CaExSubstanceUse> substanceList) {
		ShortFormSubstanceDto dto = new ShortFormSubstanceDto();
		if(null != substanceList && substanceList.size() > 0){
		for(CaExSubstanceUse substance : substanceList){
			
			switch(substance.getCdSubstanceType()){
				
				case ServiceConstants.SF_SUBSTANCE_ALC:
					dto.setIndAlcohol(substance.getIndSubstanceUse());
					if(null != substance.getNbrAgeFirstUse() && substance.getNbrAgeFirstUse() >= 0){
						dto.setAlcoholFrstUse(String.valueOf(substance.getNbrAgeFirstUse()));
					}
					dto.setAlcoholFreq(substance.getTxtFrequencyOfUse());
					if(null != substance.getDtUseLast()){
						dto.setAlcoholLastUse(formDateFormatter.format(substance.getDtUseLast()));
					}
					break;
				case ServiceConstants.SF_SUBSTANCE_MRJ: 
					dto.setIndMarijuana(substance.getIndSubstanceUse());
					if(null != substance.getNbrAgeFirstUse() && substance.getNbrAgeFirstUse() >= 0){
						dto.setMarijuanaFrstUse(String.valueOf(substance.getNbrAgeFirstUse()));
					}
					dto.setMarijuanaFreq(substance.getTxtFrequencyOfUse());
					if(null != substance.getDtUseLast()){
						dto.setMarijuanaLastUse(formDateFormatter.format(substance.getDtUseLast()));
					}
					break;
				case ServiceConstants.SF_SUBSTANCE_INH:
					dto.setIndInhalant(substance.getIndSubstanceUse());
					if(null != substance.getNbrAgeFirstUse() && substance.getNbrAgeFirstUse() >= 0){
						dto.setInhalantFrstUse(String.valueOf(substance.getNbrAgeFirstUse()));
					}
					dto.setInhalantFreq(substance.getTxtFrequencyOfUse());
					if(null != substance.getDtUseLast()){
						dto.setInhalantLastUse(formDateFormatter.format(substance.getDtUseLast()));
					}
					break;
				case ServiceConstants.SF_SUBSTANCE_COC:
					dto.setIndCocaine(substance.getIndSubstanceUse());
					if(null != substance.getNbrAgeFirstUse() && substance.getNbrAgeFirstUse() >= 0){
						dto.setCocaineFrstUse(String.valueOf(substance.getNbrAgeFirstUse()));
					}
					dto.setCocaineFreq(substance.getTxtFrequencyOfUse());
					if(null != substance.getDtUseLast()){
						dto.setCocaineLastUse(formDateFormatter.format(substance.getDtUseLast()));
					}
					break;
				case ServiceConstants.SF_SUBSTANCE_HRN:
					dto.setIndHeroin(substance.getIndSubstanceUse());
					if(null != substance.getNbrAgeFirstUse() && substance.getNbrAgeFirstUse() >= 0){
						dto.setHeroinFrstUse(String.valueOf(substance.getNbrAgeFirstUse()));
					}
					dto.setHeroinFreq(substance.getTxtFrequencyOfUse());
					if(null != substance.getDtUseLast()){
						dto.setHeroinLastUse(formDateFormatter.format(substance.getDtUseLast()));
					}
					break;
				case ServiceConstants.SF_SUBSTANCE_MTH:
					dto.setIndMeth(substance.getIndSubstanceUse());
					if(null != substance.getNbrAgeFirstUse() && substance.getNbrAgeFirstUse() >= 0){
						dto.setMethFrstUse(String.valueOf(substance.getNbrAgeFirstUse()));
					}
					dto.setMethFreq(substance.getTxtFrequencyOfUse());
					if(null != substance.getDtUseLast()){
						dto.setMethLastUse(formDateFormatter.format(substance.getDtUseLast()));
					}
					break;
				case ServiceConstants.SF_SUBSTANCE_OPI:
					dto.setIndOpioids(substance.getIndSubstanceUse());
					if(null != substance.getNbrAgeFirstUse() && substance.getNbrAgeFirstUse() >= 0){
						dto.setOpioidsFrstUse(String.valueOf(substance.getNbrAgeFirstUse()));
					}
					dto.setOpioidsFreq(substance.getTxtFrequencyOfUse());
					if(null != substance.getDtUseLast()){
						dto.setOpioidsLastUse(formDateFormatter.format(substance.getDtUseLast()));
					}
					break;
				case ServiceConstants.SF_SUBSTANCE_ECS:
					dto.setIndEcstasy(substance.getIndSubstanceUse());
					if(null != substance.getNbrAgeFirstUse() && substance.getNbrAgeFirstUse() >= 0){
						dto.setEcstasyFrstUse(String.valueOf(substance.getNbrAgeFirstUse()));
					}
					dto.setEcstasyFreq(substance.getTxtFrequencyOfUse());
					if(null != substance.getDtUseLast()){
						dto.setEcstasyLastUse(formDateFormatter.format(substance.getDtUseLast()));
					}
					break;
				case ServiceConstants.SF_SUBSTANCE_DRG:
					dto.setIndDrugs(substance.getIndSubstanceUse());
					if(null != substance.getNbrAgeFirstUse() && substance.getNbrAgeFirstUse() >= 0){
						dto.setDrugsFrstUse(String.valueOf(substance.getNbrAgeFirstUse()));
					}
					dto.setDrugsFreq(substance.getTxtFrequencyOfUse());
					if(null != substance.getDtUseLast()){
						dto.setDrugsLastUse(formDateFormatter.format(substance.getDtUseLast()));
					}
					break;
			}
		}
		}
		
		return dto;
	}

	private CommonApplicationShortFormDto covertEntityToDTO(CaExCommonApplication caExCommonApplication) {
		CommonApplicationShortFormDto dto = new CommonApplicationShortFormDto();
		
		//Header
		dto.setIdCaExCommonApplication(caExCommonApplication.getIdCaExCommonApplication());
		dto.setNmCase(caExCommonApplication.getNmCase());
		dto.setIdSsccReferral(caExCommonApplication.getIdSsccReferral());
		dto.setIdStage(caExCommonApplication.getIdStage());
		dto.setIdEvent(caExCommonApplication.getIdEvent());
		dto.setNmChild(caExCommonApplication.getNmChildPerson());
		dto.setDtChildBirth(caExCommonApplication.getDtBirthChild());
		dto.setDtCompletedApplication(caExCommonApplication.getDtCompletedApplication());
		
		//Section1
		dto.setNmDfpsCaseWorker(caExCommonApplication.getNmDfpsCaseWorker());
		dto.setCdEmpUnit(caExCommonApplication.getNbrEmpUnitEmpIn());
		dto.setNmDfpsSupervisor(caExCommonApplication.getNmDfpsSupervisor());
		
		//Section2
		dto.setIdPerson(caExCommonApplication.getIdChildPerson());
		dto.setCdPersonSex(caExCommonApplication.getCdPersonSex());
		dto.setTxtGenderIdentification(caExCommonApplication.getTxtGenderIdentification());
		dto.setCdLegalRegion(caExCommonApplication.getCdLegalRegion());
		dto.setCdPersonCitizenship(caExCommonApplication.getTxtPersonCitizenship());
		dto.setTxtEthnicity(caExCommonApplication.getTxtEthnicity());
		dto.setTxtRace(caExCommonApplication.getTxtRace());
		dto.setCdPersonLanguage(caExCommonApplication.getTxtPersonLanguage());
		dto.setCdLegalCounty(caExCommonApplication.getCdLegalCounty());
		dto.setTxtRemovalAddress(caExCommonApplication.getTxtRemovalAddress());
		dto.setRemovalDt(caExCommonApplication.getDtRemoval());
		dto.setTxtLevlOfCare(caExCommonApplication.getTxtAuthorizedLevelOfCare());
		dto.setCdServicePackage(caExCommonApplication.getCdServicePackage());
		dto.setSexualVictim(caExCommonApplication.getIndHistOfSexualVictim());
		dto.setUnconfirmedSexualVictim(caExCommonApplication.getIndUnconfirmedVictimHistory());
		dto.setTxtTraumaAbuseNeglect(caExCommonApplication.getTxtTraumaAbuseNeglect());
		dto.setTxtTraumaOthAbuseNeglect(caExCommonApplication.getTxtTraumaOthAbuseNeglect());
		dto.setTxtTraumaOthTraumaticExp(caExCommonApplication.getTxtTraumaOthTraumaticExp());
		
		dto.setIndSuspVictimSexTrfckng(caExCommonApplication.getIndSuspVictimSexTrfckng());
		dto.setIndCnfrmVictimSexTrfckng(caExCommonApplication.getIndCnfrmVictimSexTrfckng());
		dto.setIndSuspVictimLaborTrfckng(caExCommonApplication.getIndSuspVictimLaborTrfckng());
		dto.setIndCnfrmVictimLaborTrfckng(caExCommonApplication.getIndCnfrmVictimLaborTrfckng());
		dto.setTxtTrafficking(caExCommonApplication.getTxtTrafficking());
		
		dto.setDtHealthStepsMedExam(caExCommonApplication.getDtHealthStepsMedExam());
		dto.setDtHealthStepsDentalExam(caExCommonApplication.getDtHealthStepsDentalExam());
		dto.setDtLastTbTest(caExCommonApplication.getDtLastTbTest());
		dto.setTxtAllergies(caExCommonApplication.getTxtAllergies());
		dto.setTxtMedicationReactions(caExCommonApplication.getTxtMedicationReactions());
		dto.setIndCurrentlyMedications(caExCommonApplication.getIndUsingAnyMeds());
		dto.setIndImmunizationsCurrent(caExCommonApplication.getIndImmunizationsCurrent());
		dto.setIndSelfMedicalConsenter(caExCommonApplication.getIndSelfMedicalConsenter());
		
		dto.setTxtDiagPhyHlthConditions(caExCommonApplication.getTxtDiagPhyHlthConditions());
		dto.setTxtSpecialistAppointments(caExCommonApplication.getTxtSpecialistAppointments());
		dto.setTxtDevelopmentalFunctioning(caExCommonApplication.getTxtDevelopmentalFunctioning());
		dto.setIndEciServicesReceived(caExCommonApplication.getIndEciServicesReceived());
		dto.setTxtEciServicesReceived(caExCommonApplication.getTxtEciServicesReceived());
		dto.setIndOtPtStTherapy(caExCommonApplication.getIndOtPtStTherapy());
		dto.setIndEncopresisPast90days(caExCommonApplication.getIndEncopresisPast90days());
		dto.setIndEnuresisPast90days(caExCommonApplication.getIndEnuresisPast90days());
		dto.setTxtEncopresisEnuresis(caExCommonApplication.getTxtEncopresisEnuresis());
		
		dto.setIndPmn(caExCommonApplication.getIndPmn());
		dto.setTxtPmnDiagnosis(caExCommonApplication.getTxtPmnDiagnosis());
		dto.setTxtPmnMedSpecialist(caExCommonApplication.getTxtPmnMedSpecialist());
		dto.setTxtPmnPrimaryHospital(caExCommonApplication.getTxtPmnPrimaryHospital());
		dto.setTxtNursingHours(caExCommonApplication.getTxtNursingHours());
		dto.setTxtHomeHealthAgency(caExCommonApplication.getTxtHomeHealthAgency());
		dto.setIndDmeRequired(caExCommonApplication.getIndDmeRequired());
		dto.setTxtDme(caExCommonApplication.getTxtDme());
		dto.setIndAmbulance(caExCommonApplication.getIndAmbulance());
		dto.setIndDnr(caExCommonApplication.getIndDnr());
		dto.setIndDeafHearingImpaired(caExCommonApplication.getIndDeafHearingImpaired());
		dto.setTxtCommunicationType(caExCommonApplication.getTxtCommunicationType());
		dto.setIndBlindVisuallyImpaired(caExCommonApplication.getIndBlindVisuallyImpaired());
		dto.setTxtBlindVisuallyImpaired(caExCommonApplication.getTxtBlindVisuallyImpaired());
		
		dto.setTxtEmotionalStrengths(caExCommonApplication.getTxtEmotionalStrengths());
		dto.setTxtTraumaTriggers(caExCommonApplication.getTxtTraumaTriggers());
		dto.setTxtTherapistImpressions(caExCommonApplication.getTxtTherapistImpressions());
		dto.setTxtMhBhServices(caExCommonApplication.getTxtMhBhServices());
		dto.setIndCansAssessment(caExCommonApplication.getIndCansAssessment());
		dto.setDtCansAssessment(caExCommonApplication.getDtCansAssessment());
		dto.setTxtCansRecommendations(caExCommonApplication.getTxtCansRecommendations());
		dto.setDtPsychologicalEvaluation(caExCommonApplication.getDtPsychologicalEvaluation());
		dto.setDtPsychiatricEvaluation(caExCommonApplication.getDtPsychiatricEvaluation());
		dto.setTxtCurrentDiagnosis(caExCommonApplication.getTxtCurrentDiagnosis());
		dto.setIndMhCrisisPast6months(caExCommonApplication.getIndMhCrisisPast6months());
		dto.setTxtMhCrisis(caExCommonApplication.getTxtMhCrisis());
		dto.setIndPsychiatricHospital(caExCommonApplication.getIndPsychiatricHospital());
		
		dto.setIndSubstanceUse(caExCommonApplication.getIndSubstanceUse());
		
		dto.setIndCurrentPregnant(caExCommonApplication.getIndCurrentPregnant());
		dto.setTxtBabyDue(caExCommonApplication.getTxtBabyDue());
		dto.setTxtPlanForBaby(caExCommonApplication.getTxtPlanForBaby());
		dto.setIndCurrentParent(caExCommonApplication.getIndCurrentParent());
		dto.setTxtChildReside(caExCommonApplication.getTxtChildReside());
		dto.setTxtParentingRole(caExCommonApplication.getTxtParentingRole());
		
		dto.setIndSelfHarmingBehavior(caExCommonApplication.getIndSelfHarmingBehavior());
		dto.setTxtSelfHarmingBehavior(caExCommonApplication.getTxtSelfHarmingBehavior());
		dto.setIndSuicidalAttempts(caExCommonApplication.getIndSuicidalAttempts());
		dto.setIndHistRunaway(caExCommonApplication.getIndHistRunaway());
		dto.setTxtHistRunaway(caExCommonApplication.getTxtHistRunaway());
		dto.setDtLastRunaway(caExCommonApplication.getDtLastRunaway());
		dto.setTxtRunawayDetails(caExCommonApplication.getTxtRunawayDetails());
		dto.setIndHistSettingFires(caExCommonApplication.getIndHistSettingFires());
		dto.setTxtHistSettingFires(caExCommonApplication.getTxtHistSettingFires());
		dto.setIndHistCrueltyToAnimals(caExCommonApplication.getIndHistCrueltyToAnimals());
		dto.setTxtHistCrueltyToAnimals(caExCommonApplication.getTxtHistCrueltyToAnimals());
		dto.setIndOtherBehaviorProblems(caExCommonApplication.getIndOtherBehaviorProblems());
		dto.setTxtOtherBehaviorProblems(caExCommonApplication.getTxtOtherBehaviorProblems());

		dto.setIndSexualAggresBehavior(caExCommonApplication.getIndSexualAggresBehavior());
		dto.setTxtSexualAggresBehavior(caExCommonApplication.getTxtSexualAggresBehavior());
		dto.setIndSexualBehaviorProblem(caExCommonApplication.getIndSexualBehaviorProblem());
		dto.setTxtSexualBehaviorProblem(caExCommonApplication.getTxtSexualBehaviorProblem());

		dto.setIndCurrentlyEnrolSchool(caExCommonApplication.getIndCurrentlyEnrolSchool());
		dto.setNmSchool(caExCommonApplication.getNmSchool());
		dto.setTxtAddressSchool(caExCommonApplication.getTxtAddressSchool());
		dto.setNmCitySchool(caExCommonApplication.getNmCitySchool());
		dto.setNmStateSchool(caExCommonApplication.getNmStateSchool());
		dto.setDtWithdrawn(caExCommonApplication.getDtWithdrawn());
		dto.setNmSchoolContact(caExCommonApplication.getNmSchoolContact());
		dto.setNbrSchoolContactPhone(caExCommonApplication.getNbrSchoolContactPhone());
		dto.setNbrSchoolContactPhoneExt(caExCommonApplication.getNbrSchoolContactPhoneExt());
		dto.setTxtSchoolContactEmail(caExCommonApplication.getTxtSchoolContactEmail());
		dto.setTxtCurrentGrade(caExCommonApplication.getTxtCurrentGrade());
		dto.setDtCurrentGrade(caExCommonApplication.getDtCurrentGrade());
		dto.setIndOnGradeLevel(caExCommonApplication.getIndOnGradeLevel());
		dto.setNmLastSchoolAttended(caExCommonApplication.getNmLastSchoolAttended());
		dto.setTxtLastSchoolAddress(caExCommonApplication.getTxtLastSchoolAddress());
		dto.setTxtLastSchoolContact(caExCommonApplication.getTxtLastSchoolContact());
		dto.setIndHistoryOfTruancy(caExCommonApplication.getIndHistoryOfTruancy());
		
		dto.setTxtLifeSkillProgress(caExCommonApplication.getTxtLifeSkillProgress());
		dto.setIndLifeSkillAssessment(caExCommonApplication.getIndLifeSkillAssessment());
		dto.setIndPalSkillTraining(caExCommonApplication.getIndPalSkillTraining());
		dto.setTxtRegionalPalStaff(caExCommonApplication.getTxtRegionalPalStaff());
		dto.setIndCircleOfSupport(caExCommonApplication.getIndCircleOfSupport());
		dto.setTxtExtendedFcOptions(caExCommonApplication.getTxtExtendedFcOptions());
		
		dto.setIndJjInvolvement(caExCommonApplication.getIndJjInvolvement());
		dto.setTxtJjInvolvement(caExCommonApplication.getTxtJjInvolvement());
		
		dto.setIndPlacedOutOfHome(caExCommonApplication.getIndPlacedOutOfHome());
		dto.setTxtPlacedOutOfHome(caExCommonApplication.getTxtPlacedOutOfHome());
		dto.setIndAdoptedDomestically(caExCommonApplication.getIndAdoptedDomestically());
		dto.setTxtDomesticConsummated(caExCommonApplication.getTxtDomesticConsummated());
		dto.setIndAdoptedInternationally(caExCommonApplication.getIndAdoptedInternationally());
		dto.setTxtInternConsummated(caExCommonApplication.getTxtInternConsummated());
		dto.setIndOtherLegalCustody(caExCommonApplication.getIndOtherLegalCustody());
		dto.setTxtOtherLegalCustody(caExCommonApplication.getTxtOtherLegalCustody());
		dto.setIdCreatedPerson(caExCommonApplication.getIdCreatedPerson());
		dto.setDtCreated(caExCommonApplication.getDtCreated());
		dto.setDtLastUpdate(caExCommonApplication.getDtLastUpdate());
		dto.setIdLastUpdatePerson(caExCommonApplication.getIdLastUpdatePerson());
		dto.setTxtRejectReason(caExCommonApplication.getTxtRejectReason());
		dto.setCdAcknowledgeType(caExCommonApplication.getCdAcknowledgeType());
		dto.setDtAcknowledged(caExCommonApplication.getDtAcknowledged());
		
		//code added for 61082
		dto.setRemovalAddrStLn1(caExCommonApplication.getRemovalAddrStLn1());
		dto.setRemovalAddrStLn2(caExCommonApplication.getRemovalAddrStLn2());
		dto.setRemovalAddrCity(caExCommonApplication.getRemovalAddrCity());
		dto.setAddrRemovalAddrZip(caExCommonApplication.getAddrRemovalAddrZip());
		dto.setCdRemovalAddrCounty(caExCommonApplication.getCdRemovalAddrCounty());
		dto.setCdRemovalAddrState(caExCommonApplication.getCdRemovalAddrState());
		dto.setNbrGcdLat(caExCommonApplication.getNbrGcdLat());
		dto.setNbrGcdLong(caExCommonApplication.getNbrGcdLong());
		dto.setCdAddrRtrn(caExCommonApplication.getCdAddrRtrn());
		dto.setCdGcdRtrn(caExCommonApplication.getCdGcdRtrn());
		dto.setNmCntry(caExCommonApplication.getNmCntry());
		dto.setNmCnty(caExCommonApplication.getNmCnty());
		dto.setIndValdtd(caExCommonApplication.getIndValdtd());
		dto.setDtValdtd(caExCommonApplication.getDtValdtd());
		dto.setTxtMailbltyScore(caExCommonApplication.getTxtMailbltyScore());
		dto.setIndRmvlAddrDisabled(caExCommonApplication.getIndRmvlAddrDisabled());
		
		return dto;
	}

	@Override
	public CommonAppShortFormRes saveShortForm(CommonAppShortFormReq commonAppShortFormReq) {
		CommonAppShortFormRes res =  commonApplicationShortFormDao.saveShortFormData(commonAppShortFormReq);
		
		return res;
	}

	@Override
	public EventIdOutDto getEventDetails(Long eventId){
		if(null != eventId && eventId >0 ){
			EventIdInDto eventIdInDto = new EventIdInDto();
			eventIdInDto.setIdEvent(eventId);
			List<EventIdOutDto> eventDtoList = eventIdDao.getEventDetailList(eventIdInDto);
			
			return eventDtoList.get(0);
		}
		return null;
	}

	@Override
	public CommonAppShortFormRes approveShortForm(CommonApplicationShortFormDto commonApplicationShortFormDto, Long approverId) {
		CommonAppShortFormRes res =  commonApplicationShortFormDao.approveShortForm(commonApplicationShortFormDto,approverId);
		return res;
	}

	@Override
	public CommonAppShortFormRes rejectShortForm(CommonApplicationShortFormDto commonApplicationShortFormDto,
			Long approverId, String rejectReason) {
		CommonAppShortFormRes res =  commonApplicationShortFormDao.rejectShortForm(commonApplicationShortFormDto,approverId, rejectReason);
		return res;
	}

	
	private String getAssignedPersonsIdForStage(Long stageId) {
		String personsIds = null;
		List<AssignmentGroupDto> assignmentGroupDto = new ArrayList<AssignmentGroupDto>();
		try {
			assignmentGroupDto = assignDao.getAssignmentgroup(stageId);
		} catch (ServiceLayerException e) {
			log.fatal(e.getMessage());
		}
		if(!CollectionUtils.sizeIsEmpty(assignmentGroupDto)){
			personsIds = assignmentGroupDto.stream().map(assignmentGroup -> assignmentGroup.getIdPerson().toString()).collect(Collectors.joining(","));
		}
		return personsIds;
	}

	@Override
	public void createEventForNotification(CommonAppShortFormReq commonAppShortFormReq) {
		Event event = new Event();
		String personsAssignedToStage = getAssignedPersonsIdForStage(commonAppShortFormReq.getStageId());
		try{
			event.setObjectId(commonAppShortFormReq.getCaExCommonApplicationId());
			event.setObject(EventObject.CA_EX_COMMON_APPLICATION);
			event.setPersonsId(personsAssignedToStage);
			event.setAction(EventAction.CREATED);
			event.setCreatedPersonId(commonAppShortFormReq.getCreateUserId());
			event.setLastUpdatePersonId(commonAppShortFormReq.getCreateUserId());
			notificationApi.createEvent(event);
			log.info("Created Notification event for common application short form : "+commonAppShortFormReq.getCaExCommonApplicationId()+ " assigned to "+personsAssignedToStage);
		}catch (Exception e) {
			log.fatal(e.getMessage() + " for common application short form : "+commonAppShortFormReq.getCaExCommonApplicationId()+ " assigned to "+personsAssignedToStage );
		}

	}

	/**
	 * Method Description: This method is used to retrieve for print
	 * the common application short form. This form fully documents the Child 
	 * Information, Trauma and Trafficking History, Health Care Summary, Substance 
	 * Abuse, Risk and sexualized behavior, Education, Family and Placement history 
	 * emotional, Transition planning for a Adulthood, Juvenile Justice Involvement 
	 * on account of the child by passing IdEvent as input request
	 * 
	 * @param CommonAppShortFormReq
	 * @return PreFillDataServiceDto
	 */
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
			Exception.class })
	public PreFillDataServiceDto getCommonApplShortForm(CommonAppShortFormReq commonAppShortFormReq) {
		log.info("commonAppShortFormReq.getIdEvent() =  "+commonAppShortFormReq.getEventId());
		CommonApplShortFormPrintDto commonApplShortFormPrintDto = new CommonApplShortFormPrintDto();
		CommonAppShortFormRes res = new CommonAppShortFormRes();
		List<CaExSubstanceUse> substanceList = new ArrayList<CaExSubstanceUse>();

		CommonApplicationShortFormDto shortFormDto = null;
		StagePersonLinkCaseDto caseInfo = null;
		ShortFormSpecialProgrammingDto splProg = null;
		ShortFormRtnRunawayDto rtnRunaway = null;
		ShortFormEducationSrvDto eduSrv = null;
		List<PlacementDtlDto> placementLog = null;
		List<ShortFormSiblingsDto> siblingList = null;
		List<ShortFormMedicationDto> medList = null;
		List<ShortFormTherapyDto> therapyList = null;
		List<ShortFormPsychiatricDto> psychiatricList = null;
		List<SexualVictimIncidentDto> sexualVictimList = null;
		List<TraffickingDto> traffickingList = null;
		List<TraffickingDto> sfTraffickingList = null;
		List<ShortFormCsaEpisodeIncdntsDto> episodeIncdntDtls = null;
		List<ShortFormCsaEpisodeIncdntsDto> sfEpisodeIncdntDtls = null;
		
		shortFormDto = this.getCommonAppShortFormByEventId(commonAppShortFormReq.getEventId());
		caseInfo = this.retrieveHeaderInfo(commonAppShortFormReq.getStageId());
		splProg = this.getSplProgByCaExId(shortFormDto.getIdCaExCommonApplication());
		rtnRunaway = this.getRtnRunawayByCaExId(shortFormDto.getIdCaExCommonApplication());
		eduSrv = this.getEduSrvByCaExId(shortFormDto.getIdCaExCommonApplication());
		placementLog = this.getPlacementLogByExId(shortFormDto.getIdCaExCommonApplication());
		siblingList = this.getSiblingsByCaExId(shortFormDto.getIdCaExCommonApplication());
		medList = this.getMedicationByCaExId(shortFormDto.getIdCaExCommonApplication());
		therapyList = this.getTherapyDtlByCaExId(shortFormDto.getIdCaExCommonApplication());
		psychiatricList = this.getHospitalizationByCaExId(shortFormDto.getIdCaExCommonApplication());
		sexualVictimList = this.getSFSexualVictimization(shortFormDto.getIdCaExCommonApplication());
		sfTraffickingList = this.getSFTraffickingHistory(shortFormDto.getIdCaExCommonApplication());
		sfEpisodeIncdntDtls = this.getSFEpisodeIncidents(shortFormDto.getIdCaExCommonApplication());
		
		res.setCommonApplicationShortFormDto(shortFormDto);
		res.setStagePersonLinkCaseDto(caseInfo);
		res.setSplProgDto(splProg);
		res.setRtnRunaway(rtnRunaway);
		res.setEduSrv(eduSrv);
		res.setPlcmtLog(placementLog);
		res.setSiblingList(siblingList);
		res.setMedList(medList);
		res.setTherapyList(therapyList);
		res.setPsychiatricList(psychiatricList);
		res.setSexualVictimList(sexualVictimList);
		res.setTraffickingLst(traffickingList);
		res.setSfTraffickingLst(sfTraffickingList);
		res.setEpisodeIncdntDtls(episodeIncdntDtls);
		res.setSfEpisodeIncdntDtls(sfEpisodeIncdntDtls);
		
		commonApplShortFormPrintDto.setCommonAppShortFormRes(res);
		substanceList = commonApplicationShortFormDao
				.retrieveSubstanceAbuse(res.getCommonApplicationShortFormDto().getIdCaExCommonApplication());
		commonApplShortFormPrintDto.setSubstanceList(substanceList);
		// Calling Prefill Data Implementation
		log.info("TransactionId :" + commonAppShortFormReq.getTransactionId());
		return commonApplShortFormPrefillData.returnPrefillData(commonApplShortFormPrintDto);
	}

	public ServicePackageDtlDto retrieveRecommendedServicePackage(Long stageId) {
		//artf263992 : BR 15.4 Form - Common Application for Placement of Children in Residential Care
		List<ServicePackageDtlDto> recmServicePackageDtlDtos = servicePackageDao.getRecommendedServicePackageByIdStage(stageId);
		return (org.springframework.util.CollectionUtils.isEmpty(recmServicePackageDtlDtos) ? null : recmServicePackageDtlDtos.size() == 1 ? recmServicePackageDtlDtos.get(0) :
				recmServicePackageDtlDtos.stream().max(Comparator.comparing(ServicePackageDtlDto::getDtSvcStart)).orElse(null));

	}



}
