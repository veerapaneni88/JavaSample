package us.tx.state.dfps.service.servicauthform.serviceimpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.approval.dto.ApprovalFormDataDto;
import us.tx.state.dfps.common.domain.SvcAuthEventLink;
import us.tx.state.dfps.common.dto.GenericCaseInfoDto;
import us.tx.state.dfps.common.dto.ServiceReqHeaderDto;
import us.tx.state.dfps.service.admin.dao.AddrPersonLinkPhoneDao;
import us.tx.state.dfps.service.admin.dao.EmployeeDao;
import us.tx.state.dfps.service.admin.dao.SvcAuthEventLinkDao;
import us.tx.state.dfps.service.admin.dto.AddrPersonLinkPhoneInDto;
import us.tx.state.dfps.service.admin.dto.AddrPersonLinkPhoneOutDto;
import us.tx.state.dfps.service.admin.dto.SvcAuthEventLinkInDto;
import us.tx.state.dfps.service.admin.dto.SvcAuthEventLinkOutDto;
import us.tx.state.dfps.service.casepackage.dao.ServiceAuthorizationDao;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.EventDao;
import us.tx.state.dfps.service.common.dao.EventPersonLinkDao;
import us.tx.state.dfps.service.common.dao.StageDao;
import us.tx.state.dfps.service.common.request.CommonHelperReq;
import us.tx.state.dfps.service.common.utils.TypeConvUtil;
import us.tx.state.dfps.service.disasterplan.dao.DisasterPlanDao;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.forms.util.ServiceAuthorizationFormPrefillData;
import us.tx.state.dfps.service.placement.dao.CommonApplicationDao;
import us.tx.state.dfps.service.placement.dto.NameDetailDto;
import us.tx.state.dfps.service.resource.dto.ResourceDto;
import us.tx.state.dfps.service.securityauthoriztion.dto.ClientInfoServiceAuthDto;
import us.tx.state.dfps.service.securityauthoriztion.dto.DayCareDetailsDto;
import us.tx.state.dfps.service.securityauthoriztion.dto.DayCareFacilServiceAuthDto;
import us.tx.state.dfps.service.securityauthoriztion.dto.KinshipGroupInfoDto;
import us.tx.state.dfps.service.securityauthoriztion.dto.MedicaidServiceAuthDto;
import us.tx.state.dfps.service.securityauthoriztion.dto.OldestVictimNameDto;
import us.tx.state.dfps.service.securityauthoriztion.dto.SelectForwardPersonDto;
import us.tx.state.dfps.service.securityauthoriztion.dto.ServiceAuthFormDataDto;
import us.tx.state.dfps.service.servicauthform.dao.ServiceAuthFormDao;
import us.tx.state.dfps.service.servicauthform.service.ServiceAuthFormService;
import us.tx.state.dfps.service.subcare.dao.CapsResourceDao;
import us.tx.state.dfps.service.workload.dto.*;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Service
 * Authorization form used by CPS to refer clients for paid services under PRS
 * contracts. Mar 1, 2018- 1:52:54 PM © 2017 Texas Department of Family and
 * Protective Services
 */
@Repository
public class ServiceAuthFormServiceImpl implements ServiceAuthFormService {

	@Autowired
	private EventDao eventDao;

	@Autowired
	private DisasterPlanDao disasterPlanDao;

	@Autowired
	ServiceAuthFormDao serviceAuthFormDao;

	@Autowired
	SvcAuthEventLinkDao svcAuthEventLinkDao;

	@Autowired
	ServiceAuthorizationDao serviceAuthorizationDao;

	@Autowired
	CapsResourceDao capsResourceDao;

	@Autowired
	AddrPersonLinkPhoneDao addrPersonLinkPhoneDao;

	@Autowired
	CommonApplicationDao commonApplicationDao;

	@Autowired
	StageDao stageDao;

	@Autowired
	EmployeeDao employeeDao;

	@Autowired
	ServiceAuthorizationFormPrefillData serviceAuthorizationFormPrefillData;

	@Autowired
	EventPersonLinkDao eventPersonLinkDao;

	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public PreFillDataServiceDto getServiceAuthFormData(CommonHelperReq commonHelperReq) {
		ServiceAuthFormDataDto serviceAuthFormDataDto = new ServiceAuthFormDataDto();

		GenericCaseInfoDto genericCaseInfoDto = null;
		List<DayCareDetailsDto> dayCareDetailsDtoList = null;
		boolean bDaycareAuth = false;
		boolean bPersonMergeFound = false;
		boolean svcAuthDetails = false;
		OldestVictimNameDto oldestVictimNameDto = null;
		Long idResource = null;
		Long idPrimaryClient = null;
		List<SvcAuthEventLinkOutDto> svcAuthEventLinkOutDtoList = null;
		List<SVCAuthDetailDto> svcAuthDetailDtoList = null;
		List<SVCAuthDetailRecDto> svcAuthDetailRecDtoList = null;
		Long minIdSvcAuthEvent = null;
		StagePersonDto stagePersonDto = null;
		ServiceAuthorizationDto serviceAuthorizationDto = null;

		// CallCCMN45D Retrieves ID STAGE from the EVENT table using input ID
		// EVENT.
		EventDto eventValueDto = eventDao.getEventByid(commonHelperReq.getIdEvent());
		serviceAuthFormDataDto.setEventValueDto(eventValueDto);
		if (!TypeConvUtil.isNullOrEmpty(eventValueDto) && ServiceConstants.Zero_Value != eventValueDto.getIdStage()) {

			// call CallCSEC02D Retreives ID CASE and NM CASE from STAGE and
			// CAPS CASE tables using input ID STAGE from DAM CCMN45D.

			genericCaseInfoDto = disasterPlanDao.getGenericCaseInfo(eventValueDto.getIdStage());
			if (StringUtils.isEmpty(genericCaseInfoDto.getCdStageReasonClosed())) {
				genericCaseInfoDto.setCdStageReasonClosed("");
			}
			serviceAuthFormDataDto.setGenericCaseInfoDto(genericCaseInfoDto);

			// CallCSEC78D Retrieves Oldest Victim Name using ID STAGE as input
			// from eventValueDto.

			if (!TypeConvUtil.isNullOrEmpty(genericCaseInfoDto.getCdStage())
					&& ServiceConstants.A_R_STAGE.equalsIgnoreCase(genericCaseInfoDto.getCdStage())) {
				oldestVictimNameDto = serviceAuthFormDao.getOldestVicName(eventValueDto.getIdStage(),
						ServiceConstants.REFERENCE_CHILD);
				serviceAuthFormDataDto.setOldestVictimNameDto(oldestVictimNameDto);
			} else {
				oldestVictimNameDto = serviceAuthFormDao.getOldestVicName(eventValueDto.getIdStage(),
						ServiceConstants.PERSON_OLDEST_VICTIM);
				serviceAuthFormDataDto.setOldestVictimNameDto(oldestVictimNameDto);
			}
		}

		// call CallCSES24D Retrieves ID SVC AUTH from the SVC AUTH EVENT LINK
		// table using input ID EVENT.
		SvcAuthEventLinkInDto svcAuthEventLink = new SvcAuthEventLinkInDto();
		svcAuthEventLink.setIdSvcAuthEvent(commonHelperReq.getIdEvent());
		svcAuthEventLinkOutDtoList = svcAuthEventLinkDao.getAuthEventLink(svcAuthEventLink);
		serviceAuthFormDataDto.setSvcAuthEventLinkOutDtoList(svcAuthEventLinkOutDtoList);

		if (!TypeConvUtil.isNullOrEmpty(svcAuthEventLinkOutDtoList)) {

			for (SvcAuthEventLinkOutDto svcAuth : svcAuthEventLinkOutDtoList) {
				if (!TypeConvUtil.isNullOrEmpty(svcAuth.getIdSvcAuth())) {
					// CallCSES23D Retrieves a row from SVC AUTH table using ID
					// SVC AUTH as input from DAM CSES24D.
					serviceAuthorizationDto = serviceAuthorizationDao
							.getServiceAuthorizationById(svcAuth.getIdSvcAuth());
					svcAuthDetails = true;
					serviceAuthFormDataDto.setServiceAuthorizationDto(serviceAuthorizationDto);

					if (!TypeConvUtil.isNullOrEmpty(serviceAuthorizationDto.getIdPrimaryClient())
							&& !TypeConvUtil.isNullOrEmpty(serviceAuthorizationDto.getIdResource())) {
						idResource = serviceAuthorizationDto.getIdResource();
						idPrimaryClient = serviceAuthorizationDto.getIdPrimaryClient();
					}

					if (!TypeConvUtil.isNullOrEmpty(serviceAuthorizationDto.getIdContract())) {
						// CallCMSC63D Retrieves nbr_contract_scor from
						// contract_period to pass using TxtStagePriorityCmnts
						if (!TypeConvUtil.isNullOrEmpty(serviceAuthorizationDto.getDtSvcAuthEff())) {
							String txScorContractNumber = serviceAuthFormDao.getNbrContractScor(
									serviceAuthorizationDto.getIdContract(), serviceAuthorizationDto.getDtSvcAuthEff());
							serviceAuthFormDataDto.setTxScorContractNumber(txScorContractNumber);
						} else {
							String txScorContractNumber = serviceAuthFormDao
									.getNbrContractScor(serviceAuthorizationDto.getIdContract(), null);
							serviceAuthFormDataDto.setTxScorContractNumber(txScorContractNumber);
						}
						/*
						 * if( 0 < strlen(
						 * pCMSC63DOutputRec->szTxScorContractNumber ) ) {
						 * COPYSZ( pCSEC02DOutputRec->szTxtStagePriorityCmnts,
						 * pCMSC63DOutputRec->szTxScorContractNumber );}
						 */
					}
					// CallCSEC0DD Get the original progressed svc auth event
					minIdSvcAuthEvent = serviceAuthFormDao.getMinIdSvcAuthEvent(svcAuth.getIdSvcAuth());

					// call CSEC23D
					// Retreives approval information for the authorization
					// using input ID EVENT.

					// CallCLSS24D

					svcAuthDetailDtoList = serviceAuthorizationDao.getSVCAuthDetailDtoById(svcAuth.getIdSvcAuth());
					serviceAuthFormDataDto.setSvcAuthDetailDtoList(svcAuthDetailDtoList);

					svcAuthDetailRecDtoList = serviceAuthorizationDao
							.getSVCAuthDetailRecord(svcAuth.getIdSvcAuthEvent());
					serviceAuthFormDataDto.setSVCDetailRec(svcAuthDetailRecDtoList);
				}
			}
		}
		if (null != idResource) {
			// call CRES04D
			ResourceDto resourceDto = capsResourceDao.getResourceById(idResource);
			serviceAuthFormDataDto.setResourceDto(resourceDto);
		}

		// set the auth flags by looping over svcAuthEventLinkOutDtoList
		if (svcAuthDetails) {
			if (!TypeConvUtil.isNullOrEmpty(svcAuthDetailDtoList)) {
				for (SVCAuthDetailDto svcAuthDetail : svcAuthDetailDtoList) {
					if (!TypeConvUtil.isNullOrEmpty(svcAuthDetail.getCdSvcAuthDtlSvc())
							&& ServiceConstants.CONCRETE_SERVICES
									.equalsIgnoreCase(svcAuthDetail.getCdSvcAuthDtlSvc())) {
						genericCaseInfoDto.setCdStageReasonClosed(svcAuthDetail.getCdSvcAuthDtlSvc());
					}
					if (!TypeConvUtil.isNullOrEmpty(svcAuthDetail.getCdSvcAuthDtlSvc())) {
						String cdSvcAuthDtlSvc = svcAuthDetail.getCdSvcAuthDtlSvc().substring(0, 2);
						if (ServiceConstants.DAYCARE_SERVICES.equalsIgnoreCase(cdSvcAuthDtlSvc)) {
							bDaycareAuth = true;
							genericCaseInfoDto.setIdSituation(svcAuthDetail.getIdSvcAuth());
						}
					}

					if (!TypeConvUtil.isNullOrEmpty(svcAuthDetail.getCdSvcAuthDtlSvc())) {

						String cdSvcAuthDtlSvc = svcAuthDetail.getCdSvcAuthDtlSvc().substring(0, 2);
						if (ServiceConstants.KINSHIP_SERVICES.equalsIgnoreCase(cdSvcAuthDtlSvc)) {
							genericCaseInfoDto.setCdStageReasonClosed(svcAuthDetail.getCdSvcAuthDtlSvc());
						}
					}
				}
			}
		}

		if (bDaycareAuth) {
			// Call CLSC27D
			dayCareDetailsDtoList = serviceAuthFormDao.getDayCareDetails(minIdSvcAuthEvent);
			serviceAuthFormDataDto.setDayCareDetailsDtoList(dayCareDetailsDtoList);

			if (ObjectUtils.isEmpty(dayCareDetailsDtoList) || (!ObjectUtils.isEmpty(dayCareDetailsDtoList)
					&& null != dayCareDetailsDtoList.get(ServiceConstants.Zero_INT).getIndInvalid()
					&& ServiceConstants.Y
							.equalsIgnoreCase(dayCareDetailsDtoList.get(ServiceConstants.Zero_INT).getIndInvalid()))) {
				bDaycareAuth = false;
			} else {
				genericCaseInfoDto.setCdStageReasonClosed(ServiceConstants.DAYCARE_SERVICES);				
				
				for (DayCareDetailsDto dayCare : dayCareDetailsDtoList) {					
					if (ServiceConstants.CAREGIVER.equalsIgnoreCase(dayCare.getCdPersonType())) {
						// append the idCaregiver here to the list of the
						// SVCAuthDetailDto to fetch details from 77d later
						SVCAuthDetailDto svcAuthDetailDto = new SVCAuthDetailDto();
						svcAuthDetailDto.setIdPerson(dayCare.getIdPerson());
						svcAuthDetailDtoList.add(svcAuthDetailDto);
					}
				}
				
				
				for (SVCAuthDetailDto svcauth : svcAuthDetailDtoList) {
					for (DayCareDetailsDto dayCare : dayCareDetailsDtoList) {
						if (null != svcauth.getIdPerson() && null != dayCare.getIdPerson()
								&& null != dayCare.getCdPersonType()
								&& (!ObjectUtils.isEmpty(svcauth.getIdPerson())
										&& svcauth.getIdPerson().equals(dayCare.getIdPerson()))) {
							svcauth.setNmNameFirst(dayCare.getNmPersonFirst());
							svcauth.setNmNameMiddle(dayCare.getNmPersonMiddle());
							svcauth.setNmNameLast(dayCare.getNmPersonLast());
							svcauth.setCdNameSuffix(dayCare.getCdPersonSuffix());
							dayCare.setDtBegin(svcauth.getDtSvcAuthDtlBegin());
							dayCare.setDtEnd(svcauth.getDtSvcAuthDtlEnd());
							dayCare.setCdRequestType(svcauth.getCdSvcAuthDtlAuthType());
							dayCare.setDtSvcTerm(svcauth.getDtSvcAuthDtlTerm());
						}
					}
				}
			}
		}
		int rowCount = 0;
		// supposed to loop over the CSES23D Pass ID PERSON as input from DAM
		// CSES23D(has been copied to pCLSC36DOutputRec).
		if (null != svcAuthDetailDtoList && ServiceConstants.Zero < svcAuthDetailDtoList.size()) {
			for (SVCAuthDetailDto svcauth : svcAuthDetailDtoList) {
				if (null != svcauth && null != svcauth.getIdPerson()) {
					// CallCLSS71D Determine if the SVC AUTH DETAIL person
					// exists on the PERSON MERGE table.
					List<SelectForwardPersonDto> persMergeFwdList = serviceAuthFormDao
							.getSelectForwardPerson(svcauth.getIdPerson());
					serviceAuthFormDataDto.setPersMergeFwdList(persMergeFwdList);
					if (!ObjectUtils.isEmpty(persMergeFwdList)) {
						bPersonMergeFound = false;
						for (SelectForwardPersonDto idPerson : persMergeFwdList) {
							if (bPersonMergeFound == false) {
								// call CSES86D This DAM checks to see if a
								// given PersMergeForward ID is on the Stage
								// Person Link
								// table for a given Stage.
								if (null != idPerson.getIdPersonMergeForward()) {
									rowCount = serviceAuthFormDao.getPersonInStageLink(
											idPerson.getIdPersonMergeForward(), eventValueDto.getIdStage());

								}

								if (rowCount > 0) {
									bPersonMergeFound = true;
									if (!ObjectUtils.isEmpty(dayCareDetailsDtoList)) {
										for (DayCareDetailsDto dayCare : dayCareDetailsDtoList) {
											if (dayCare.getIdPerson().equals(svcauth.getIdPerson())) {
												dayCare.setIdPerson(idPerson.getIdPersonMergeForward());
											}
										}
									}
									svcauth.setIdPerson(idPerson.getIdPersonMergeForward());
								}
							}
						}
					}
				}
			}
		} // end of the CLSS71D and

		if (bDaycareAuth) {
			if (null != dayCareDetailsDtoList) {
				for (DayCareDetailsDto dayCare : dayCareDetailsDtoList) {
					if (null != dayCare.getCdPersonType()
							&& ServiceConstants.CAREGIVER.equalsIgnoreCase(dayCare.getCdPersonType())) {
						// call CINV46D for every caregiver.
						AddrPersonLinkPhoneInDto addrDto = new AddrPersonLinkPhoneInDto();
						addrDto.setUlIdPerson(dayCare.getIdPerson());
						List<AddrPersonLinkPhoneOutDto> addr = addrPersonLinkPhoneDao.cinv46dQUERYdam(addrDto);
						serviceAuthFormDataDto.setAddr(addr);

						for (AddrPersonLinkPhoneOutDto address : addr) {
							if (null != address.getPhone()) {
								dayCare.setNbrTelephone(address.getPhone());
							}
							if (null != address.getPhoneExtension()) {
								dayCare.setlNbrPhoneExtension(address.getPhoneExtension());
							}
							if (null != address.getAddrPersAddrStLn1()) {
								dayCare.setAddrLn1(address.getAddrPersAddrStLn1());
							}
							if (null != address.getAddrPersAddrStLn2()) {
								dayCare.setAddrLn2(address.getAddrPersAddrStLn2());
							}
							if (null != address.getAddrCity()) {
								dayCare.setAddrCity(address.getAddrCity());
							}
							if (null != address.getCdAddrState()) {
								dayCare.setCdAddrState(address.getCdAddrState());
							}
							if (null != address.getAddrZip()) {
								dayCare.setAddrZip1(address.getAddrZip());
							}
						}
					}
				}
			}
		}
		if (svcAuthDetails) {

			if (!TypeConvUtil.isNullOrEmpty(svcAuthDetailDtoList)) {
				List<ClientInfoServiceAuthDto> clientDtoList = new ArrayList<ClientInfoServiceAuthDto>();
				for (SVCAuthDetailDto svcAuthDetail : svcAuthDetailDtoList) {
					// CallCSEC77D For each row Retrieved from a SVC AUTH DETAIL
					// record Retrieve the corresponding client information from
					// the PERSON, and PERSON ID tables using ID PERSON as input
					// from DAM CLSS24D/CSES86D. (Get the information for the
					// SSN row from the PERSON_ID table)
					ClientInfoServiceAuthDto clientDto = serviceAuthFormDao
							.getClientInfoServiceAuth(svcAuthDetail.getIdPerson(), eventValueDto.getIdStage());
					
					// CSES51D get medicaid info
					if(!ObjectUtils.isEmpty(clientDto)) {
						List<MedicaidServiceAuthDto> medicaidServiceAuthDto = serviceAuthFormDao
								.getMedicaidServiceAuth(svcAuthDetail.getIdPerson());
						clientDto.setMedicaidServiceAuthDtoList(medicaidServiceAuthDto);
						
						clientDtoList.add(clientDto);
						serviceAuthFormDataDto.setClientDtoList(clientDtoList);
					}
					
				}
			}
		}

		if (null != idPrimaryClient) {
			// call CallCSEC35D Retrieve a name from PERSON using input ID
			// PRIMARY CLIENT.
			NameDetailDto nameDetailDto = commonApplicationDao.getNameDetails(idPrimaryClient);
			serviceAuthFormDataDto.setNameDetailDto(nameDetailDto);
		}

		if (bDaycareAuth == false) {
			// CallCINV46D
			AddrPersonLinkPhoneInDto addrDto = new AddrPersonLinkPhoneInDto();
			addrDto.setUlIdPerson(idPrimaryClient);
			List<AddrPersonLinkPhoneOutDto> addr = addrPersonLinkPhoneDao.cinv46dQUERYdam(addrDto);
			serviceAuthFormDataDto.setAddr(addr);
		}

		if (null != genericCaseInfoDto.getIdStage()) {
			// CallCCMN19D Retreives information about the primary caseworker
			stagePersonDto = stageDao.getStagePersonLinkDetails(genericCaseInfoDto.getIdStage(),
					ServiceConstants.PRIMARY);
			serviceAuthFormDataDto.setStagePersonDto(stagePersonDto);
		}
		// CallCSEC01D Retreives Employee information about the primary
		// caseworker
		if (null != stagePersonDto.getIdTodoPersWorker()) {
			EmployeePersPhNameDto employeePersPhNameDto = employeeDao
					.searchPersonPhoneName(stagePersonDto.getIdTodoPersWorker());
			serviceAuthFormDataDto.setEmployeePersPhNameDto(employeePersPhNameDto);
		}
		
		// CallCSECD1D RETRIEVE PERSON ID OF CASEWORKER WHEN SVC AUTH CREATED
		if (null != genericCaseInfoDto.getIdStage() && null != eventValueDto.getIdEvent()) {
			Long resultId = serviceAuthFormDao.getPRPersonId(eventValueDto.getIdEvent());
			serviceAuthFormDataDto.setResultId(resultId);

		}
		
		// Original worker details
		if (!ObjectUtils.isEmpty(serviceAuthFormDataDto.getResultId())) {
			EmployeePersPhNameDto employeePersPhNameDto = employeeDao
					.searchPersonPhoneName(serviceAuthFormDataDto.getResultId());
			serviceAuthFormDataDto.setHistoyEmployeePersPhNameDto(employeePersPhNameDto);
		}

		if (!TypeConvUtil.isNullOrEmpty(svcAuthEventLinkOutDtoList)) {

			for (SvcAuthEventLinkOutDto svcAuth : svcAuthEventLinkOutDtoList) {
				if (!TypeConvUtil.isNullOrEmpty(svcAuth.getIdSvcAuth())) {
					// CallCLSCD3D Retreives kinship group using id_svc_auth as
					// input.
					List<KinshipGroupInfoDto> kinshipList = serviceAuthFormDao
							.getKinshipDetails(svcAuth.getIdSvcAuth());
					serviceAuthFormDataDto.setKinshipList(kinshipList);
				}
			}
		}

		

		if (bDaycareAuth) {
			// CallCLSC2AD Only query alternate daycare facility for the
			// DAYCARE_CHILD in pCLSC27DOutputRec
			List<DayCareFacilServiceAuthDto> dayCareFaciListFinal = new ArrayList<DayCareFacilServiceAuthDto>(); // this
																			// is
																			// pCLSC28DOutputRec
																			// in
																			// legacy.

			if (null != dayCareDetailsDtoList) {
				for (DayCareDetailsDto daycare : dayCareDetailsDtoList) {
					if (null != daycare.getIdDaycarePersonLink() && null != daycare.getCdPersonType()
							&& null != daycare.getIdDaycareRequest()
							&& ServiceConstants.DAYCARE_CHILD.equals(daycare.getCdPersonType())) {
						List<DayCareFacilServiceAuthDto> dayCareFaciList = serviceAuthFormDao
								.getDayCareFacility(daycare.getIdDaycarePersonLink(), daycare.getIdDaycareRequest());

						if (null != dayCareFaciList) {
							for (DayCareFacilServiceAuthDto facilDay : dayCareFaciList) {
								if (!ObjectUtils.isEmpty(facilDay.getIdFacility())
										&& !ObjectUtils.isEmpty(daycare.getIdFacility())
										&& facilDay.getIdFacility().equals(daycare.getIdFacility())) {
									if (null != facilDay.getDtBeginProvider()) {
										daycare.setDtBeginProvider(facilDay.getDtBeginProvider());
									}
								} else {
									// else the pCLSC28DOutputRec is
									dayCareFaciListFinal.add(facilDay);
								}
							}
							serviceAuthFormDataDto.setDayCareFaciListFinal(dayCareFaciListFinal);
						}
					}
				}
			}
		}
		// Call CSEC23D Retrieves approval information for the authorization
		ApprovalFormDataDto approvalFormDto = serviceAuthFormDao.getApprovalFormData(eventValueDto.getIdEvent());
		if(!ObjectUtils.isEmpty(approvalFormDto) && ServiceConstants.APPROVE_EVENT_STATUS.equalsIgnoreCase(approvalFormDto.getCdApproversStatus())) {
			serviceAuthFormDataDto.setApprovalFormDataDto(approvalFormDto);
		}
		
		return serviceAuthorizationFormPrefillData.returnPrefillData(serviceAuthFormDataDto);

	}

	/**
	 * Artifact ID: artf151569
	 * Method Name: copyOpenServiceAuthToFPR
	 * Method Description: Copies the open Service Authorizations from given stage to new stage if detail term date is after
	 * system date and event status is Approve
	 *
	 * @param idStage
	 * @param idNewStage
	 * @param idUser
	 * @param cdTask
	 */
	@Override
	public void copyOpenServiceAuthToNewStage(Long idStage, Long idNewStage, Long idUser, String cdTask) {

		List<EventStagePersonDto> eventStagePersonDtoList = stageDao.getEventStagePersonListByAttributes(idStage,
				null, ServiceConstants.SERVICE_AUTH_TYPE);

		if (!CollectionUtils.isEmpty(eventStagePersonDtoList)) {

			// Function to retrieve the SVC Auth Event DTO from EventDAO by idEvent
			Function<Long, SVCAuthEventDto> svcAuthEventDtoFunction = (idEvent) -> eventDao
					.getSVCAuthEventDtoByIdSVCAuthEvent(idEvent);

			// Function to retrieve the SVC Auth Detail List from ServiceAuthorizationDAO by idSVCAuth
			Function<Long, List<SVCAuthDetailDto>> svcAuthDetailDtoListFunction = (idSVCAuth) -> serviceAuthorizationDao
					.getSVCAuthDetailDtoById(idSVCAuth);

			// To check if the new stage already has event dto created or not
			EventDto newStageEventDto = null;

			List<EventDto> eventDtos = eventDao.getEventBystagenTask(idNewStage, cdTask);

			List<Long> newStageIdSvcAuthList = null;

			// If new Stage event present, then Retrieve the SVC Auth already mapped to the Event
			if (!CollectionUtils.isEmpty(eventDtos)) {

				newStageEventDto = eventDtos.get(0);
				if (!ObjectUtils.isEmpty(newStageEventDto) && !ObjectUtils.isEmpty(newStageEventDto.getIdEvent())) {
					SVCAuthEventDto newStageSvcAuthEventDto = svcAuthEventDtoFunction.apply(newStageEventDto
							.getIdEvent());

					List<SVCAuthDetailDto> newStageSvcAuthDetailDtoList = svcAuthDetailDtoListFunction.apply(
							newStageSvcAuthEventDto.getIdSVCAuth());

					if (!CollectionUtils.isEmpty(newStageSvcAuthDetailDtoList)) {
						newStageIdSvcAuthList = newStageSvcAuthDetailDtoList.stream().map(SVCAuthDetailDto::getIdSvcAuth)
								.collect(Collectors.toList());
					}
				}
			}

			// Filter only Approved Service Authorizations
			for (EventStagePersonDto eventStagePersonDto : eventStagePersonDtoList) {

				if (!ObjectUtils.isEmpty(eventStagePersonDto.getIdEvent())
						&& ServiceConstants.EVENTSTATUS_APPROVE.equals(eventStagePersonDto.getCdEventStatus())) {

					SVCAuthEventDto svcAuthEventDto = svcAuthEventDtoFunction.apply(eventStagePersonDto
							.getIdEvent());

					List<SVCAuthDetailDto> svcAuthDetailDtoList = svcAuthDetailDtoListFunction.apply(svcAuthEventDto
							.getIdSVCAuth());

					// Check for Future Termination date
					if (!CollectionUtils.isEmpty(svcAuthDetailDtoList) && svcAuthDetailDtoList.stream()
							.anyMatch(dto -> (new Date()).before(dto.getDtSvcAuthDtlTerm()))
							&& (CollectionUtils.isEmpty(newStageIdSvcAuthList) || !newStageIdSvcAuthList
							.contains(svcAuthDetailDtoList.get(0).getIdSvcAuth()))) {

						// Get the first instance to create the event and link the SvcAuth
						SVCAuthDetailDto svcAuthDetailDto = svcAuthDetailDtoList.get(0);

						EventDto eventDto = null;

						// Creates new Event
						if (ObjectUtils.isEmpty(newStageEventDto)) {
							eventDto = new EventDto();
							eventDto.setIdStage(idNewStage);
							eventDto.setIdCase(eventStagePersonDto.getIdCase());
							eventDto.setCdEventStatus(eventStagePersonDto.getCdEventStatus());
							eventDto.setDtEventOccurred(eventStagePersonDto.getDtEventOccurred());
							eventDto.setEventDescr(eventStagePersonDto.getEventDescr());
							eventDto.setCdEventType(eventStagePersonDto.getCdEventType());
							eventDto.setCdTask(cdTask);
						} else {
							eventDto = newStageEventDto;
						}

						ServiceReqHeaderDto serviceReqHeaderDto = new ServiceReqHeaderDto();
						serviceReqHeaderDto.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_ADD);
						// Persist new event
						EventDto newEventDto = eventDao.eventAUDFunc(serviceReqHeaderDto, eventDto);

						// Creates new Service Auth Event Link
						SvcAuthEventLink svcAuthEventLink = new SvcAuthEventLink();
						svcAuthEventLink.setIdSvcAuthEvent(newEventDto.getIdEvent());
						svcAuthEventLink.setServiceAuthorization(serviceAuthorizationDao.
								getServiceAuthorizationEntityById(svcAuthDetailDto.getIdSvcAuth()));
						svcAuthEventLink.setDtLastUpdate(new Date());
						// Persist new Service Auth Event Link
						serviceAuthorizationDao.svcAuthEventLinkSave(svcAuthEventLink);

						// Retrieve Person List by Event ID
						List<PersonDto> personDtoList = eventDao.getPersonFromEventPlanLinkByIdEvent(
								eventStagePersonDto.getIdEvent());

						EventPersonLinkDto eventPersonLinkDto = new EventPersonLinkDto();
						eventPersonLinkDto.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_ADD);
						eventPersonLinkDto.setIdEvent(newEventDto.getIdEvent());

						if (!CollectionUtils.isEmpty(personDtoList)) {

							personDtoList.forEach(personDto -> {
								eventPersonLinkDto.setIdPerson(personDto.getIdPerson());
								eventPersonLinkDao.getEventPersonLinkAUD(eventPersonLinkDto);
							});
						}
					}
				}
			}
		}

	}
}
