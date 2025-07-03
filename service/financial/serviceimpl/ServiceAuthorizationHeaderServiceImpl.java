package us.tx.state.dfps.service.financial.serviceimpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import us.tx.state.dfps.common.domain.ContractPeriod;
import us.tx.state.dfps.common.domain.KinHomeAssessmentDetail;
import us.tx.state.dfps.common.domain.Person;
import us.tx.state.dfps.common.domain.StagePersonLink;
import us.tx.state.dfps.common.dto.*;
import us.tx.state.dfps.service.admin.dao.*;
import us.tx.state.dfps.service.admin.dto.*;
import us.tx.state.dfps.service.admin.service.ApprovalCommonService;
import us.tx.state.dfps.service.admin.service.PostEventService;
import us.tx.state.dfps.service.casemanagement.dao.CpsCheckListDao;
import us.tx.state.dfps.service.casemanagement.dto.CpsCheckListInDto;
import us.tx.state.dfps.service.casemanagement.dto.CpsCheckListOutDto;
import us.tx.state.dfps.service.casepackage.dao.ServiceAuthorizationDao;
import us.tx.state.dfps.service.casepackage.dao.StagePersonLinkDao;
import us.tx.state.dfps.service.casepackage.dto.ServiceAuthorizationHeaderDto;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.EventDao;
import us.tx.state.dfps.service.common.dao.StageDao;
import us.tx.state.dfps.service.common.request.PostEventReq;
import us.tx.state.dfps.service.common.request.ServiceAuthorizationDetailReq;
import us.tx.state.dfps.service.common.request.ServiceAuthorizationHeaderReq;
import us.tx.state.dfps.service.common.request.SubcontrListRtrvReq;
import us.tx.state.dfps.service.common.response.PostEventRes;
import us.tx.state.dfps.service.common.response.ServiceAuthorizationHeaderRes;
import us.tx.state.dfps.service.common.response.SubcontrListRtrvRes;
import us.tx.state.dfps.service.common.service.CheckStageEventStatusService;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.event.controller.EventController;
import us.tx.state.dfps.service.financial.dao.ServiceAuthHeaderDao;
import us.tx.state.dfps.service.financial.dao.SvcAuthDetailDao;
import us.tx.state.dfps.service.financial.dto.ServiceAuthorizationDetailDto;
import us.tx.state.dfps.service.financial.service.ServiceAuthorizationHeaderService;
import us.tx.state.dfps.service.financial.service.ServiceAuthorizationService;
import us.tx.state.dfps.service.investigation.dao.FacilAllgDtlDao;
import us.tx.state.dfps.service.person.dao.PersonDao;
import us.tx.state.dfps.service.person.dto.PersonDtlDto;
import us.tx.state.dfps.service.person.dto.PersonGenderSpanishDto;
import us.tx.state.dfps.service.placement.dao.CommonApplicationDao;
import us.tx.state.dfps.service.placement.dao.ContractDao;
import us.tx.state.dfps.service.placement.dto.ContractContractPeriodInDto;
import us.tx.state.dfps.service.placement.dto.ContractContractPeriodOutDto;
import us.tx.state.dfps.service.placement.dto.ContractCountyOutDto;
import us.tx.state.dfps.service.placement.dto.ContractCountyPeriodInDto;
import us.tx.state.dfps.service.placement.dto.NameDetailDto;
import us.tx.state.dfps.service.populateform.dao.PopulateFormDao;
import us.tx.state.dfps.service.resource.dto.ResourceDto;
import us.tx.state.dfps.service.securityauthoriztion.dto.SelectForwardPersonDto;
import us.tx.state.dfps.service.securityauthoriztion.dto.ServiceAuthDetailDto;
import us.tx.state.dfps.service.servicauthform.dao.ServiceAuthFormDao;
import us.tx.state.dfps.service.subcare.dao.CapsResourceDao;
import us.tx.state.dfps.service.subcare.dao.PlacementDao;
import us.tx.state.dfps.service.subcare.dto.PlacementDto;
import us.tx.state.dfps.service.subcontractor.service.SbcntrListService;
import us.tx.state.dfps.service.workload.dto.*;
import us.tx.state.dfps.service.workload.service.CloseOpenStageService;
import us.tx.state.dfps.service.workload.service.TodoCommonFunctionService;
import us.tx.state.service.servicedlvryclosure.dto.OutcomeMatrixDto;

import java.util.*;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<Service
 * which handles service logic and do multiple dao calls> June 27, 2018- 3:05:39
 * PM © 2017 Texas Department of Family and Protective Services.
 */
@Service
@Transactional
public class ServiceAuthorizationHeaderServiceImpl implements ServiceAuthorizationHeaderService {

	private static final Logger log = LoggerFactory.getLogger(ServiceAuthorizationHeaderServiceImpl.class);
	@Autowired
	private StageUnitDao stageUnitDao;
	@Autowired
	private StagePersonLinkDao stagePersonLinkDao;
	@Autowired
	private EventDao eventDao;
	@Autowired
	private EventStagePersonLinkInsUpdDao eventStagePersonLinkInsUpdDao;
	@Autowired
	private SvcAuthEventLinkDao svcAuthEventLinkDao;
	@Autowired
	private ServiceAuthorizationDao serviceAuthorizationDao;
	@Autowired
	private ServiceAuthFormDao serviceAuthFormDao;
	@Autowired
	private CheckStageEventStatusService checkStageEventStatusService;
	@Autowired
	private ContractDao contractDao;
	@Autowired
	private CapsResourceDao capsResourceDao;
	@Autowired
	private StageDao stageDao;
	@Autowired
	private CpsCheckListDao cpsCheckListDao;
	@Autowired
	private ServiceAuthHeaderDao serviceAuthHeaderDao;
	@Autowired
	private SvcAuthDetailDao svcAuthDetailDao;
	@Autowired
	private PersonDao personDao;
	@Autowired
	private PostEventService postEventService;
	@Autowired
	private FacilAllgDtlDao facilAllgDtlDao;
	@Autowired
	private ApprovalCommonService approvalCommonService;
	@Autowired
	private UpdateToDoDao updateToDoDao;
	@Autowired
	private TodoCommonFunctionService todoCommonFunctionService;
	@Autowired
	private PopulateFormDao populateFormDao;
	@Autowired
	private PlacementDao placementDao;
	@Autowired
	private CloseOpenStageService closeOpenStageService;
	@Autowired
	private ServiceAuthorizationService serviceAuthorizationService;
	@Autowired
	SbcntrListService sbcntrListService;

	@Autowired
	CommonApplicationDao commonApplicationDao;

	@Autowired
	private ApsInhomeTasksDao apsInhomeTasksDao;

	@Autowired
	private EventController eventController;

	@Autowired
	private KinHomeAssessmentDetailDao kinHomeAssessmentDetailDao;

	public static final String FAMILY_PRES = "FPR";
	public static final String SUBCARE = "SUB";
	public static final String FAMILY_SUBCARE = "FSU";
	public static final String FAM_REUNIFICATION = "FRE";
	public static final String ADOPTION = "ADO";
	public static final String POST_ADOPTION = "PAD";
	public static final String INV_CCL_TYPE = "CCL";
	public static final String FPR_CCL_TYPE = "STG";
	public static final String EVENT_STATUS_PENDING = "PEND";
	public static final String PAL = "PAL";
	public static final String PAL_CCL_TYPE = "STG";
	public static final String STRENGTHEN_FAMILY = "SFI";
	public static final String CPS_PROGRAM = "CPS";
	public static final String SFI_STIPEND_69A = "69A";
	public static final String SFI_STIPEND_69B = "69B";
	public static final String SFI_STIPEND_69C = "69C";
	public static final String SFI_STIPEND_69D = "69D";
	public static final String SFI_STIPEND_69E = "69E";
	public static final String SFI_STIPEND_69F = "69F";
	public static final String SFI_STIPEND_69G = "69G";
	public static final String SFI_STIPEND_69H = "69H";
	public static final String APS = "APS";
	public static final String NEW = "NEW";
	public static final String INV_OUTCOME_MATRIX = "2090";
	public static final String AOC_OUTCOME_MATRIX = "5030";
	public static final String SVC_OUTCOME_MATRIX = "6070";
	public static final String INVESTIGATION = "INV";
	public static final String AGING_OUT = "AOC";
	public static final String SERVICE_DELIVERY = "SVC";
	public static final String VC_PERP = "VP";
	public static final String PRINCIPAL = "PRN";
	public static final String CLIENT = "CL";
	public static final String VICTIM = "VC";
	public static final String FAMILY_REUNIF = "FRE";
	public static final String PRIMARY_CHILD = "PC";
	public static final String FICTIVE_KIN = "FK";
	public static final String CON1 = "CON001";
	public static final String CON2 = "CON002";
	public static final String CON7 = "CON007";
	public static final String AUTHORIZATION = "AUT";
	public static final String SERVICES = "CSVCCODE";
	public static final String SERVICE_CODE_INT = "67A";
	public static final String SERVICE_CODE_FLX = "67B";
	public static final String SERVICE_CATEGORY = "24";
	public static final String SVC_AUTH_PRIMARY = "68B";
	public static final String SVC_AUTH_FICTIVE = "68D";
	public static final String SVC_AUTH_68N = "68N";
	public static final int NUMBER_OF_REL_INT = 11;
	// service codes for Non-TANF - Out of State/Non-citizen
	public static final String SVC_AUTH_REL_INTEG_NONTANF_68F = "68F";
	public static final String SVC_AUTH_REL_FLEX_NONTANF_68G = "68G";
	public static final String SVC_AUTH_OTH_INTEG_NONTANF_68H = "68H";
	public static final String SVC_AUTH_OTH_FLEX_NONTANF_68I = "68I";
	public static final String SVC_AUTH_REL_FLEX_68C = "68C";
	public static final String SVC_AUTH_OTH_FLEX_68E = "68E";
	public static final String SVC_AUTH_OTH_FLEX_68A = "68A";
	public static final String EVENT_TYPE_KAM = "KAM";
	public static final List<String> KINSHIP_SERVICES = Collections
			.unmodifiableList(Arrays.asList(SFI_STIPEND_69A, SFI_STIPEND_69B, SFI_STIPEND_69C, SFI_STIPEND_69D,
					SFI_STIPEND_69E, SFI_STIPEND_69F, SFI_STIPEND_69G, SFI_STIPEND_69H));

	/**
	 * Method name: retrieveServiceAuthHeaderInfo Method Description: This
	 * method is used to get Service Authorization Header information. This
	 * service will perform the retrieval of Service Authorization Header
	 * information as well as some validation logic. Using ID EVENT it will
	 * retrieve from the SVC_AUTH_LINK table the ID SVC AUTH. If the Window mode
	 * is Modify or Browse then this ID will be used to retrieve Header
	 * information from the SVC AUTH table. Also using the ID EVENT, the CD
	 * EVENT STATUS will be retrieved from the EVENT table. Using ID RESOURCE
	 * the NM RESOURCE will be retrieved from the CAPS_RESOURCE table. If the
	 * mode is NEW then certain validations will have to be performed:
	 * 
	 * @param serviceAuthorizationHeaderReq
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public ServiceAuthorizationHeaderRes retrieveServiceAuthHeaderInfo(
			ServiceAuthorizationHeaderReq serviceAuthorizationHeaderReq) {
		String indRetVal = ServiceConstants.FND_SUCCESS;
		boolean indFoundSub = false;
		ServiceAuthorizationHeaderRes serviceAuthorizationHeaderRes = new ServiceAuthorizationHeaderRes();
		ServiceAuthorizationHeaderDto serviceAuthorizationHeaderDto = new ServiceAuthorizationHeaderDto();
		InCheckStageEventStatusDto inCheckStageEventStatusDto = new InCheckStageEventStatusDto();
		inCheckStageEventStatusDto.setCdTask(serviceAuthorizationHeaderReq.getCdTask());
		inCheckStageEventStatusDto.setIdStage(serviceAuthorizationHeaderReq.getIdStage());
		// This call is used to get the indicator if the stage is closed.
		// Execution of all the service logic is based on the indicator.
		Boolean eventStageStatus = checkStageEventStatusService.chkStgEventStatus(inCheckStageEventStatusDto);
		// if true continue with the service logic
		if (eventStageStatus) {
			// condition if the event is new event and not APS stage
			if ((ObjectUtils.isEmpty(serviceAuthorizationHeaderReq.getIdEvent())
					|| serviceAuthorizationHeaderReq.getIdEvent() == 0L)
					&& !APS.equals(serviceAuthorizationHeaderReq.getCdStageProgram())) {
				// This call is used to check if the stage is Subcare and open
				// stages.
				checkIfSubcareOpenStagesExists(serviceAuthorizationHeaderReq, indRetVal, indFoundSub);
			}
			if (ServiceConstants.FND_SUCCESS.equals(indRetVal)) {

				setClientListAndPrimaryClient(serviceAuthorizationHeaderReq, indRetVal, serviceAuthorizationHeaderRes, serviceAuthorizationHeaderDto);
				//11/16/2021: The following code has been commented to show APS program service authorization list
				// check if the stage is APS
				/*if (APS.equals(serviceAuthorizationHeaderReq.getCdStageProgram())) {
					for (StagePrincipalDto o : serviceAuthorizationHeaderRes.getStagePrincipalDto()) {
						if (VICTIM.equals(o.getCdStagePersRole()) || CLIENT.equals(o.getCdStagePersRole())
								|| VC_PERP.equals(o.getCdStagePersRole())) {
							// Now copy the Temp variable values back into the
							// structure at row(0)
							List<StagePrincipalDto> stagePrincipalDtoListTemp = new ArrayList<>();
							stagePrincipalDtoListTemp.set(0, o);
							serviceAuthorizationHeaderRes.setStagePrincipalDto(stagePrincipalDtoListTemp);
						}

					}
				}
*/
				// check of Unit Program to be against entire string "APS"
				if (ObjectUtils.isEmpty(serviceAuthorizationHeaderReq.getIdEvent())
						&& APS.equals(serviceAuthorizationHeaderReq.getCdStageProgram())) {
					// CLSS90D
					// OUTCOME MATRIX LIST FOR GIVEN STAGE ID
					List<OutcomeMatrixDto> outcomeMatrixDtoList = serviceAuthHeaderDao
							.getOutcomeMatrixListBystageId(serviceAuthorizationHeaderReq.getIdStage());
					if (ObjectUtils.isEmpty(outcomeMatrixDtoList) || outcomeMatrixDtoList.isEmpty()) {
						serviceAuthorizationHeaderRes.setErrorDto(addError(8144));
						indRetVal = ServiceConstants.FND_SUCCESS;
					} else {
						indRetVal = ServiceConstants.FND_FAILURE;
					}
				} else {
					if (!ObjectUtils.isEmpty(serviceAuthorizationHeaderReq.getIdEvent())) {
						// CCMN45D
						// This call is used to get the event details when event
						// id is passed.
						EventDto eventDto = eventDao.getEventByid(serviceAuthorizationHeaderReq.getIdEvent());
						if (!ObjectUtils.isEmpty(eventDto)) {
							serviceAuthorizationHeaderDto.setIdEvent(eventDto.getIdEvent());
							serviceAuthorizationHeaderDto.setCdEventStatus(eventDto.getCdEventStatus());
							serviceAuthorizationHeaderDto.setCdTask(eventDto.getCdTask());
							serviceAuthorizationHeaderDto.setCdEventType(eventDto.getCdEventType());
							serviceAuthorizationHeaderDto.setCdStage(eventDto.getCdStage());
							serviceAuthorizationHeaderDto.setIdStage(eventDto.getIdStage());
							serviceAuthorizationHeaderRes.setEventDto(eventDto);
						}
						// check if the stage is APS
						if (APS.equals(serviceAuthorizationHeaderReq.getCdStageProgram())
								&& !ObjectUtils.isEmpty(eventDto) && NEW.equals(eventDto.getCdEventStatus())) {
							EventStagePersonLinkInsUpdInDto eventStagePersonLinkInsUpdInDto = new EventStagePersonLinkInsUpdInDto();
							eventStagePersonLinkInsUpdInDto.setCdReqFunction(ServiceConstants.REQ_FUNC_CD_UPDATE);
							if (INVESTIGATION.equals(serviceAuthorizationHeaderReq.getCdStage())) {
								eventStagePersonLinkInsUpdInDto.setCdTask(INV_OUTCOME_MATRIX);
							} else if (AGING_OUT.equals(serviceAuthorizationHeaderReq.getCdStage())) {
								eventStagePersonLinkInsUpdInDto.setCdTask(AOC_OUTCOME_MATRIX);
							} else {
								eventStagePersonLinkInsUpdInDto.setCdTask(SVC_OUTCOME_MATRIX);
							}
							eventStagePersonLinkInsUpdInDto.setIdStage(eventDto.getIdStage());
							// CCMN87D
							// This method is used to get the event and stage
							// information.
							List<EventStagePersonLinkInsUpdOutDto> eventStagePersonLinkInsUpdOutDtoList = eventStagePersonLinkInsUpdDao
									.getEventAndStatusDtls(eventStagePersonLinkInsUpdInDto);
							if (ObjectUtils.isEmpty(eventStagePersonLinkInsUpdOutDtoList)) {
								serviceAuthorizationHeaderRes.setErrorDto(addError(8144));
								indRetVal = ServiceConstants.FND_SUCCESS;
							} else {
								indRetVal = ServiceConstants.FND_FAILURE;
							}
						}
						// check if the event exists and event status is not
						// new.
						if (!ObjectUtils.isEmpty(eventDto) && !NEW.equals(eventDto.getCdEventStatus())) {
							SvcAuthEventLinkInDto svcAuthEventLinkInDto = new SvcAuthEventLinkInDto();
							svcAuthEventLinkInDto.setIdSvcAuthEvent(serviceAuthorizationHeaderReq.getIdEvent());
							// CSES24D
							// retrieve a row from the SVC_AUTH_EVENT_LINK table
							// based on ID EVENT.
							List<SvcAuthEventLinkOutDto> svcAuthEventLinkOutDtoList = svcAuthEventLinkDao
									.getAuthEventLink(svcAuthEventLinkInDto);
							if (!ObjectUtils.isEmpty(svcAuthEventLinkOutDtoList)
									&& !svcAuthEventLinkOutDtoList.isEmpty()) {
								serviceAuthorizationHeaderDto
										.setIdSvcAuth(svcAuthEventLinkOutDtoList.get(0).getIdSvcAuth().intValue());
								// CSES23D
								// This performs a full row retrieval from the
								// SERVICE_AUTHORIZATION_TABLE when IDSVC_AUth
								// is equal to the input variable.
								ServiceAuthorizationDto serviceAuthorizationDto = serviceAuthorizationDao
										.getServiceAuthorizationById(svcAuthEventLinkOutDtoList.get(0).getIdSvcAuth());
								// if the serviceAuthorizationDto is not null
								// copy all the values to the
								// serviceAuthorizationHeaderDto which takes to
								// the front end.
								if (!ObjectUtils.isEmpty(serviceAuthorizationDto)) {
									serviceAuthorizationHeaderDto
											.setIdResource(serviceAuthorizationDto.getIdResource().intValue());
									serviceAuthorizationHeaderDto
											.setIndSvcAuthComplete(serviceAuthorizationDto.getIndSvcAuthComplete());
									serviceAuthorizationHeaderDto
											.setIdContract(serviceAuthorizationDto.getIdContract());
									serviceAuthorizationHeaderDto
											.setIndDntdCmmtySvc(serviceAuthorizationDto.getIndDontdComntySvc());
									serviceAuthorizationHeaderDto
											.setCdSvcAuthCounty(serviceAuthorizationDto.getCdSvcAuthCounty());
									serviceAuthorizationHeaderDto
											.setCdSvcAuthRegion(serviceAuthorizationDto.getCdSvcAuthRegion());
									serviceAuthorizationHeaderDto
											.setCdSvcAuthService(serviceAuthorizationDto.getCdSvcAuthService());
									serviceAuthorizationHeaderDto
											.setCdSvcAuthCategory(serviceAuthorizationDto.getCdSvcAuthCategory());
									serviceAuthorizationHeaderDto
											.setCommentsSerAuthHeader(serviceAuthorizationDto.getSvcAuthComments());
									serviceAuthorizationHeaderDto
											.setPreferredSubCon(serviceAuthorizationDto.getSvcAuthSecProvdr());
									serviceAuthorizationHeaderDto
											.setSvcAuthSecProvdr(serviceAuthorizationDto.getSvcAuthSecProvdr());
									serviceAuthorizationHeaderDto
											.setDtSvcAuthEff(serviceAuthorizationDto.getDtSvcAuthEff());
									serviceAuthorizationHeaderDto
											.setDtLastUpdate(serviceAuthorizationDto.getDtLastUpdate());
									serviceAuthorizationHeaderDto
											.setIdPrimaryClient(serviceAuthorizationDto.getIdPrimaryClient());
									serviceAuthorizationHeaderDto
											.setPrimaryClient(serviceAuthorizationDto.getIdPrimaryClient().toString());

									//Setting the data for Service Authorization APS details section
									serviceAuthorizationHeaderDto.setTxtDtDtSvcAuthVerbalReferl(serviceAuthorizationDto.getDtSvcAuthVerbalReferl());
									serviceAuthorizationHeaderDto.setTxtSzTxtMedicalConditions(serviceAuthorizationDto.getSvcAuthMedCond());
									serviceAuthorizationHeaderDto.setTxtSzTxtDirectToHome(serviceAuthorizationDto.getSvcAuthDirToHome());
									serviceAuthorizationHeaderDto.setTxtSzTxtHomeEnviron(serviceAuthorizationDto.getSvcAuthHomeEnviron());
									serviceAuthorizationHeaderDto.setSelSzCdSvcAuthAbilToRespond(serviceAuthorizationDto.getCdSvcAuthAbilToRespond());
									serviceAuthorizationHeaderDto.setPhysicianPersonId(serviceAuthorizationDto.getIdPerson());
									if(ServiceConstants.APS_PROGRAM.equals(serviceAuthorizationHeaderReq.getCdStageProgram())) {
										//In home task list data for Service Authorization APS details
										getInhomeDetails(serviceAuthorizationHeaderDto);
										//Physician List for Service Authorization APS details
										getPhysicanListByStageId(serviceAuthorizationHeaderDto);
										//Persons Living Arrangement data from persons table
										getPersLivingArrangmentForPersTable(serviceAuthorizationHeaderDto);

									}
									// CLSS71D
									// get list of Forward Person ID's. If no
									// rows returned, the person has not been
									// merged.
									List<SelectForwardPersonDto> selectForwardPersonDtoList = serviceAuthFormDao
											.getSelectForwardPerson(serviceAuthorizationDto.getIdPrimaryClient());
									if (!ObjectUtils.isEmpty(selectForwardPersonDtoList) && !ObjectUtils
											.isEmpty(selectForwardPersonDtoList.get(0).getIdPersonMergeForward())) {
										selectForwardPersonDtoList.forEach(o -> {
											// CSES86D
											// see if an IdPersonForward is
											// linked to the current IdStage
											int personCount = serviceAuthFormDao.getPersonInStageLink(
													o.getIdPersonMergeForward(),
													serviceAuthorizationHeaderReq.getIdStage());
											if (personCount != 0) {
												serviceAuthorizationDto.setIdPrimaryClient(o.getIdPersonMergeForward());
												serviceAuthorizationHeaderDto.setPrimaryClient(String.valueOf(o.getIdPersonMergeForward()));
											}
										});

									}

									if(serviceAuthorizationHeaderDto.getClientList().get(serviceAuthorizationHeaderDto.getIdPrimaryClient()) == null){
										NameDetailDto nameDetailDto = commonApplicationDao.getNameDetails(serviceAuthorizationHeaderDto.getIdPrimaryClient());
										serviceAuthorizationHeaderDto.getClientList().put(String.valueOf(serviceAuthorizationHeaderDto.getIdPrimaryClient()),
												nameDetailDto.getNmNameFirst() + " " + nameDetailDto.getNmNameLast() + " " + (nameDetailDto.getCdNameSuffix() != null ? nameDetailDto.getCdNameSuffix() : ""));
									}

									if (!ObjectUtils.isEmpty(serviceAuthorizationHeaderRes.getStagePrincipalDto())) {
										serviceAuthorizationHeaderRes.getStagePrincipalDto().forEach(o -> {
											// see if the person obtained is the
											// primary client.
											if (o.getIdPerson().equals(serviceAuthorizationDto.getIdPrimaryClient())) {
												// Now copy the Temp variable
												// values back into the
												// structure at row(0)
												List<StagePrincipalDto> stagePrincipalDtoListTemp = new ArrayList<>();
												stagePrincipalDtoListTemp.add(o);
												serviceAuthorizationHeaderRes
														.setStagePrincipalDto(stagePrincipalDtoListTemp);
											}
										});
									}
									ServiceAuthorizationDetailReq serviceAuthorizationDetailReq = new ServiceAuthorizationDetailReq();
									serviceAuthorizationDetailReq
											.setIdSvcAuth(serviceAuthorizationHeaderDto.getIdSvcAuth().longValue());
									// Method to get service authorization
									// detail list
									getServiceAuthorizationDetailList(serviceAuthorizationDetailReq,
											serviceAuthorizationHeaderDto);
									ContractCountyPeriodInDto contractCntyPeriodInDto = new ContractCountyPeriodInDto();
									contractCntyPeriodInDto
											.setIdResource(serviceAuthorizationHeaderDto.getIdResource().longValue());
									contractCntyPeriodInDto
											.setCdCncntyCounty(serviceAuthorizationHeaderDto.getCdSvcAuthCounty());
									contractCntyPeriodInDto
											.setCdCncntyService(serviceAuthorizationHeaderDto.getCdSvcAuthService());
									if (!ObjectUtils.isEmpty(serviceAuthorizationHeaderDto.getDtSvcAuthEff())) {
										contractCntyPeriodInDto
												.setDtplcmtStart(serviceAuthorizationHeaderDto.getDtSvcAuthEff());
									}
									// This call is used to get the contract
									// information
									getContractInformation(serviceAuthorizationHeaderDto, contractCntyPeriodInDto);
									// CRES04D
									// performs a full row select from the
									// CAPS_RESOURCE table with ID_RESOURCE as
									// input
									ResourceDto resourceDto = capsResourceDao
											.getResourceById(serviceAuthorizationHeaderDto.getIdResource().longValue());
									serviceAuthorizationHeaderDto.setNmResource(resourceDto.getNmResource());
								}

							}
						}
						serviceAuthorizationHeaderDto.setDayCareRequestDto(serviceAuthorizationService
								.retrieveDayCareReqAndPersonDetail(serviceAuthorizationHeaderReq.getIdEvent()));
					}
				}
			}

			// condition to check if the indRetVal is true
			if (ServiceConstants.FND_SUCCESS.equals(indRetVal)) {
				// CSES26D
				// Using ID_STAGE to outer join the STAGE and SITUATION tables,
				// this will retrieve the DT_SITUATION_OPENED.
				serviceAuthorizationHeaderDto.setDtSituationOpened(
						serviceAuthHeaderDao.getDtSituationOpened(serviceAuthorizationHeaderReq.getIdStage()));
				// CINT21D
				// retrieve the stage details based on id_stage
				StageDto stageDto = stageDao.getStageById(serviceAuthorizationHeaderReq.getIdStage());
				// check if the stage is closed and set the indStageClosed nased
				// on the stage closure date.
				if (!ObjectUtils.isEmpty(stageDto) && !(ObjectUtils.isEmpty(stageDto.getDtStageClose())
						|| ServiceConstants.MAX_DATE_STRING.equals(DateUtils.stringDate(stageDto.getDtStageClose())))) {
					serviceAuthorizationHeaderDto.setIndStageClose(ServiceConstants.STRING_IND_Y);
					serviceAuthorizationHeaderDto.setDtStageClose(stageDto.getDtStageClose());
				} else {
					serviceAuthorizationHeaderDto.setIndStageClose(ServiceConstants.STRING_IND_N);
				}
				EventStagePersonLinkInsUpdInDto eventStagePersonLinkInsUpdInDto = new EventStagePersonLinkInsUpdInDto();
				eventStagePersonLinkInsUpdInDto.setCdReqFunction(ServiceConstants.REQ_FUNC_CD_UPDATE);
				eventStagePersonLinkInsUpdInDto.setIdStage(serviceAuthorizationHeaderReq.getIdStage());
				// CCMN87D
				// populate the cdStage for eventStagePersonLinkInsUpdInDto
				// as CCL if the cdStage obtained from stageDto is
				// Investigation/Subcare/Family Subcare/Family
				// Reunification/Adoption/Post Adoption/Aging
				// Out
				if (INVESTIGATION.equals(stageDto.getCdStage()) || SUBCARE.equals(stageDto.getCdStage())
						|| FAMILY_SUBCARE.equals(stageDto.getCdStage())
						|| FAM_REUNIFICATION.equals(stageDto.getCdStage()) || ADOPTION.equals(stageDto.getCdStage())
						|| POST_ADOPTION.equals(stageDto.getCdStage()) || AGING_OUT.equals(stageDto.getCdStage())) {
					eventStagePersonLinkInsUpdInDto.setCdStage(INV_CCL_TYPE);
				}
				// populate the cdStage for eventStagePersonLinkInsUpdInDto
				// as STG if the cdStage obtained from stageDto is
				// Service Delivery/Family Preservation
				else if (FAMILY_PRES.equals(stageDto.getCdStage()) || SERVICE_DELIVERY.equals(stageDto.getCdStage())) {
					eventStagePersonLinkInsUpdInDto.setCdStage(FPR_CCL_TYPE);
				}
				// populate the cdStage for eventStagePersonLinkInsUpdInDto
				// as STG if the cdStage obtained from stageDto is
				// PAL
				else if (PAL.equals(stageDto.getCdStage())) {
					eventStagePersonLinkInsUpdInDto.setCdStage(PAL_CCL_TYPE);
				}
				List<EventListDto> events = eventDao.getEventDtls(eventStagePersonLinkInsUpdInDto.getIdStage(),
						eventStagePersonLinkInsUpdInDto.getCdStage());
				if (!ObjectUtils.isEmpty(events) && !events.isEmpty()
						&& EVENT_STATUS_PENDING.equals(events.get(0).getCdEventStatus())) {
					serviceAuthorizationHeaderDto.setIdEvent(events.get(0).getIdEvent());
				} else {
					indRetVal = ServiceConstants.FND_FAILURE;
				}
				indRetVal = ServiceConstants.FND_SUCCESS;
			}

			// call the service if CDSTAGE is FPR or FRE
			if (FAMILY_REUNIF.equals(serviceAuthorizationHeaderReq.getCdStage())
					|| FAMILY_PRES.equals(serviceAuthorizationHeaderReq.getCdStage())) {
				CpsCheckListInDto cpsCheckListInDto = new CpsCheckListInDto();
				cpsCheckListInDto.setIdStage(serviceAuthorizationHeaderReq.getIdStage());
				// CSESD8D
				// retrieve SFI effective and end dates from CPS_CHECKLIST table
				// given Idstage
				List<CpsCheckListOutDto> cpsCheckListOutDto = cpsCheckListDao.csesc9dQUERYdam(cpsCheckListInDto);
				if (!ObjectUtils.isEmpty(cpsCheckListOutDto) && !cpsCheckListOutDto.isEmpty()) {
					serviceAuthorizationHeaderDto.setDtEligStart(cpsCheckListOutDto.get(0).getDtEligStart());
					serviceAuthorizationHeaderDto.setDtEligEnd(cpsCheckListOutDto.get(0).getDtEligEnd());
				}
			}

		}
		if(!ObjectUtils.isEmpty(serviceAuthorizationHeaderDto.getIdResource())){
			SubcontrListRtrvReq subcontrListRtrvReq = new SubcontrListRtrvReq();
			subcontrListRtrvReq.setIdRsrcLinkParent(serviceAuthorizationHeaderDto.getIdResource().longValue());
			subcontrListRtrvReq.setIndSbcntrPredisplay(null);
			SubcontrListRtrvRes subcontrListRtrvRes = sbcntrListService.findSubcontractor(subcontrListRtrvReq);
			serviceAuthorizationHeaderDto.setSubContractorList(subcontrListRtrvRes.getSbcntrListRtrvoDtoList());
		}
		serviceAuthorizationHeaderRes.setServiceAuthorizationHeaderDto(serviceAuthorizationHeaderDto);
		return serviceAuthorizationHeaderRes;
	}

	/**
	 * Method Name: saveServiceAuthHeader Method Description: This method is
	 * used to save the service Authorization Header information
	 * 
	 * This service performs Service Authorization header save functionality as
	 * well as the creation and modification of events, Approval Invalidation
	 * and ToDo creation. If the Window Mode is New then a new record will be
	 * saved to the SERVICE AUTHORIZATION table and a new record inserted into
	 * the SVC_AUTH_EVENT_LINK table. If the Window Mode is Modify and the Event
	 * Status is 'PEND' then the approval will have to be invalidated and the
	 * Event Status demoted to 'COMP'. If this is the first time that the
	 * Service Authorization has been marked complete then the appropriate
	 * alerts will be generated. ToDo's also have to be generated if the worker
	 * is authorizing services that are contracted in a region different to
	 * their unit region.
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public ServiceAuthorizationHeaderRes saveServiceAuthHeader(
			ServiceAuthorizationHeaderReq serviceAuthorizationHeaderReq) {
		ServiceAuthorizationHeaderRes serviceAuthorizationHeaderRes = new ServiceAuthorizationHeaderRes();
		ServiceAuthorizationHeaderDto serviceAuthorizationHeaderDto = serviceAuthorizationHeaderReq
				.getServiceAuthorizationHeaderDto();
		// This method is used to check for validations
		if (!ServiceConstants.REQ_FUNC_CD_DELETE.equals(serviceAuthorizationHeaderReq.getReqFuncCd()))
			checkForValidations(serviceAuthorizationHeaderDto);
		// condition to check if the error exists, if doesn't exists save
		// services are called.
		if (ObjectUtils.isEmpty(serviceAuthorizationHeaderDto.getErrorDto())) {
			InCheckStageEventStatusDto inCheckStageEventStatusDto = new InCheckStageEventStatusDto();
			inCheckStageEventStatusDto.setCdReqFunction(serviceAuthorizationHeaderReq.getReqFuncCd());
			inCheckStageEventStatusDto.setIdStage(serviceAuthorizationHeaderDto.getIdStage());
			inCheckStageEventStatusDto.setCdTask(serviceAuthorizationHeaderDto.getCdTask());
			Boolean eventStageStatus = checkStageEventStatusService.chkStgEventStatus(inCheckStageEventStatusDto);
			// check if the action is delete.
			if (eventStageStatus
					&& ServiceConstants.REQ_FUNC_CD_DELETE.equals(serviceAuthorizationHeaderReq.getReqFuncCd())) {
				// CAUDE7D
				// calls the DELETE_SERVICE_AUTH procedure in the COMPLEX_DELETE
				// package, given an ID_SVC_AUTH
				serviceAuthHeaderDao.deleteServiceAuth(serviceAuthorizationHeaderDto.getIdSvcAuth().longValue());
			} else if (eventStageStatus
					&& !ServiceConstants.REQ_FUNC_CD_DELETE.equals(serviceAuthorizationHeaderReq.getReqFuncCd())) {
				if (ObjectUtils.isEmpty(serviceAuthorizationHeaderDto.getIdEvent())
						|| serviceAuthorizationHeaderDto.getIdEvent() == 0) {
					serviceAuthorizationHeaderReq.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_ADD);
				} else {
					serviceAuthorizationHeaderReq.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_UPDATE);
				}

				PostEventOPDto postEventOPDto = null;

					// This call is used to populate input to call the postEvent and
					// call the postEvent
					postEventOPDto = callPostEvent(serviceAuthorizationHeaderReq,
							serviceAuthorizationHeaderDto);

				if (!ObjectUtils.isEmpty(postEventOPDto) ) {
					if (!ServiceConstants.REQ_FUNC_CD_UPDATE.equals(serviceAuthorizationHeaderReq.getReqFuncCd())) {
						serviceAuthorizationHeaderRes.setIdEvent(postEventOPDto.getIdEvent()); // re-check
					} else {
						serviceAuthorizationHeaderRes.setIdEvent(serviceAuthorizationHeaderDto.getIdEvent());
					}
					// Re-Set value of cReqFuncCd based on Id Svc Auth
					if (ObjectUtils.isEmpty(serviceAuthorizationHeaderDto.getIdSvcAuth())
							|| serviceAuthorizationHeaderDto.getIdSvcAuth() == 0) {
						serviceAuthorizationHeaderReq.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_ADD);
					} else {
						serviceAuthorizationHeaderReq.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_UPDATE);
					}
					// Add invalidate approval common function to invalidate the
					// conclusion event if it has been submitted and the user
					// has added a new record or modified an existing record
					if (!ObjectUtils.isEmpty(serviceAuthorizationHeaderReq.getIdEvent())
							&& 0 < serviceAuthorizationHeaderReq.getIdEvent()) {
						String cdEventStatus;
						// check if the request is having the approval mode. if
						// approval mode exists populate the status with PEND
						// else populate with COMP.
						if (serviceAuthorizationHeaderReq.getIsApprovalMode()) {
							cdEventStatus = ServiceConstants.STATUS_PEND;
						} else {
							cdEventStatus = ServiceConstants.EVENT_STATUS_COMP;
						}
						// CCMN62D
						// This call is used to update the event details with
						// updated event status.
						facilAllgDtlDao.getEventDetailsUpdate(serviceAuthorizationHeaderReq.getIdEvent().longValue(),
								cdEventStatus);

						// see if the approval mode is false invalidat the
						// pending approvals.
						if (!serviceAuthorizationHeaderReq.getIsApprovalMode()) {
							ApprovalCommonInDto approvalCommonInDto = new ApprovalCommonInDto();
							approvalCommonInDto.setIdEvent(serviceAuthorizationHeaderReq.getIdEvent().longValue());
							// CCMN05U
							// This method invokes InvalidateAprvl method
							approvalCommonService.callCcmn05uService(approvalCommonInDto);
						}
					}

					// CAUD33D
					// perform a row update and row add to the
					// SERVICE_AUTHORIZATION table
					serviceAuthorizationHeaderRes.setIdSvcAuth(
							serviceAuthHeaderDao.saveServiceAuthorization(serviceAuthorizationHeaderDto, null));
					if(serviceAuthorizationHeaderReq.getSaveAndSubmit() != null && serviceAuthorizationHeaderReq.getSaveAndSubmit()) {
						Long kinHomeAvcAuthId = kinHomeAssessmentDetailDao.getKinHomeAssesmentDtl(serviceAuthorizationHeaderRes.getIdSvcAuth());

						if (SVC_AUTH_OTH_FLEX_68A.equalsIgnoreCase(serviceAuthorizationHeaderReq.getCdSvcAuthService()) && kinHomeAvcAuthId == null) {
							createKinHomeAssessmentDtl(serviceAuthorizationHeaderRes.getIdSvcAuth(), serviceAuthorizationHeaderReq);
						}
					}

					if (!ObjectUtils.isEmpty(serviceAuthorizationHeaderRes.getIdSvcAuth())) {
						serviceAuthorizationDao
								.getServiceAuthorizationById(serviceAuthorizationHeaderRes.getIdSvcAuth());
						UpdateToDoDto updateToDoDto = new UpdateToDoDto();
						updateToDoDto.setIdEvent(serviceAuthorizationHeaderRes.getIdEvent());
						updateToDoDao.completeTodo(updateToDoDto);
						if (ServiceConstants.REQ_FUNC_CD_ADD.equals(serviceAuthorizationHeaderReq.getReqFuncCd())) {
							// CAUD34D
							// updates the svc_auth_event_link
							closeOpenStageService.svcAuthEventLinkSave(serviceAuthorizationHeaderRes.getIdEvent(),
									serviceAuthorizationHeaderRes.getIdSvcAuth());
						}
					}
				}
				// create a todo for that region's supervisor.
				// Only create the todo if the Service Authorization has been
				// completed.
				callToDoFunction(serviceAuthorizationHeaderDto);

				// logic to invalid the pending approval when it is not approval
				// mode.
				if (!serviceAuthorizationHeaderReq.getIsApprovalMode() && ServiceConstants.EVENTSTATUS_PENDING
						.equals(serviceAuthorizationHeaderReq.getCdEventStatus())) {
					ApprovalCommonInDto approvalCommonInDto = new ApprovalCommonInDto();
					approvalCommonInDto.setIdEvent(serviceAuthorizationHeaderDto.getIdEvent());
					// CCMN05U
					// This call invokes InvalidateAprvl method
					approvalCommonService.callCcmn05uService(approvalCommonInDto);
				}

				// check if the stage is APS and first time complete is checked.
				if (APS.equals(serviceAuthorizationHeaderDto.getCdStageProgram())
						&& ServiceConstants.STRING_IND_Y.equals(serviceAuthorizationHeaderDto.getIndFrstTmComp())) {
					// CLSS24D
					// selects a full row from the svc_auth_detail with
					// id_svc_auth as input
					List<SVCAuthDetailDto> svcAuthDetailDtoList = serviceAuthorizationDao
							.getSVCAuthDetailDtoById(serviceAuthorizationHeaderDto.getIdSvcAuth().longValue());
					if (!ObjectUtils.isEmpty(svcAuthDetailDtoList) && !svcAuthDetailDtoList.isEmpty()) {
						// This method is used to call the todocommon function
						// for all the person.
						callToDoForPerson(svcAuthDetailDtoList, serviceAuthorizationHeaderReq,
								serviceAuthorizationHeaderDto, serviceAuthorizationHeaderRes);
					}
				}
				if(null != serviceAuthorizationHeaderDto.getCbxInHome()){
					apsInhomeTasksDao.saveAPSInhomeTasks( serviceAuthorizationHeaderDto.getCbxInHome(), serviceAuthorizationHeaderDto.getIdSvcAuth().longValue());
				}

			}
			serviceAuthorizationHeaderDto.setIdEvent(serviceAuthorizationHeaderRes.getIdEvent());
		}
		serviceAuthorizationHeaderRes.setServiceAuthorizationHeaderDto(serviceAuthorizationHeaderDto);
		return serviceAuthorizationHeaderRes;
	}


	public void createKinHomeAssessmentDtl(Long idSvcAuth, ServiceAuthorizationHeaderReq serviceAuthorizationHeaderReq) {
		PostEventRes postEventRes = null;
		Long idPrimaryClient = serviceAuthorizationHeaderReq.getServiceAuthorizationHeaderDto().getIdPrimaryClient();
		Person person = personDao.getPerson(idPrimaryClient);
		PostEventReq postEventReq = new PostEventReq();
		postEventReq.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_ADD);
		postEventReq.setSzCdTask(ServiceConstants.KINSHIP_HOME_TASK);
		postEventReq.setCdStage(serviceAuthorizationHeaderReq.getCdStage());
		postEventReq.setUlIdCase(serviceAuthorizationHeaderReq.getIdCase());
		postEventReq.setUlIdStage(serviceAuthorizationHeaderReq.getIdStage());
		postEventReq.setUlIdPerson(serviceAuthorizationHeaderReq.getIdPerson());
		postEventReq.setSzCdEventStatus(ServiceConstants.NEW_EVENT_STATUS);
		postEventReq.setSzTxtEventDescr(ServiceConstants.KINSHIP_HOME.concat(person.getNmPersonFull()));
		postEventReq.setSzCdEventType(EVENT_TYPE_KAM);

		List<PostEventPersonDto> postEventPersonDtos = new ArrayList<>();
		PostEventPersonDto postEventPersonDto = new PostEventPersonDto();
		postEventPersonDto.setIdPerson(idPrimaryClient);
		postEventPersonDtos.add(postEventPersonDto);
		postEventReq.setPostEventPersonList(postEventPersonDtos);

		postEventRes = eventController.postEvent(postEventReq);

		KinHomeAssessmentDetail kinHomeAssessmentDetail = new KinHomeAssessmentDetail();
		kinHomeAssessmentDetail.setEventId(postEventRes.getUlIdEvent());
		kinHomeAssessmentDetail.setCaseId(serviceAuthorizationHeaderReq.getIdCase());
		kinHomeAssessmentDetail.setKinCaregiverId(idPrimaryClient);
		kinHomeAssessmentDetail.setStageId(serviceAuthorizationHeaderReq.getIdStage());
		kinHomeAssessmentDetail.setAutoPopulated(ServiceConstants.STRING_IND_Y);
		kinHomeAssessmentDetail.setCreatedDate(new Date());
		kinHomeAssessmentDetail.setSvcAuthId(idSvcAuth);
		kinHomeAssessmentDetail.setServiceAuthorizedDate(new Date());
		kinHomeAssessmentDetail.setCreatedPersonId(serviceAuthorizationHeaderReq.getIdPerson());
		kinHomeAssessmentDetail.setLastUpdatedPersonId(serviceAuthorizationHeaderReq.getIdPerson());
		Long id = kinHomeAssessmentDetailDao.saveKinHomeAssessmentDetail(kinHomeAssessmentDetail);

		log.info("Kinhome Assessment Id" + id);

	}


	/**
	 * Method Name: validateContractForResource Method Description: This service
	 * serves to determine whether a contract for a particular resource is
	 * valid, and if so, to retrieve certain information for that contract.
	 * 
	 * @param serviceAuthorizationHeaderReq
	 * @return
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public ServiceAuthorizationHeaderRes validateContractForResource(ServiceAuthorizationHeaderReq serviceAuthorizationHeaderReq) {
		ServiceAuthorizationHeaderRes serviceAuthorizationHeaderRes = new ServiceAuthorizationHeaderRes();
		ServiceAuthorizationHeaderDto serviceAuthorizationHeaderDto = serviceAuthorizationHeaderReq
				.getServiceAuthorizationHeaderDto();
		ContractCountyPeriodInDto contractCountyPeriodInDto = new ContractCountyPeriodInDto();
		contractCountyPeriodInDto.setIdResource(serviceAuthorizationHeaderDto.getIdResource().longValue());
		contractCountyPeriodInDto.setCdCncntyCounty(serviceAuthorizationHeaderDto.getCdSvcAuthCounty());
		contractCountyPeriodInDto.setCdCncntyService(serviceAuthorizationHeaderDto.getCdSvcAuthService());
		contractCountyPeriodInDto.setDtplcmtStart(serviceAuthorizationHeaderDto.getDtSvcAuthEff());
		// CSEC10D
		// retrieve a row from Contract_County given the host variable match
		// when it falls within a specific date period
		List<ContractCountyOutDto> contractCountyOutDtoList = contractDao.getContarctCounty(contractCountyPeriodInDto);
		if (!ObjectUtils.isEmpty(contractCountyOutDtoList) && !contractCountyOutDtoList.isEmpty()) {
			serviceAuthorizationHeaderDto.setIdContract(contractCountyOutDtoList.get(0).getIdContract());
			serviceAuthorizationHeaderDto.setCnperPeriod(contractCountyOutDtoList.get(0).getNbrCncntyPeriod());
			serviceAuthorizationHeaderDto.setCncntyVersion(contractCountyOutDtoList.get(0).getNbrCncntyVersion());
			ContractContractPeriodInDto contractContractPeriodInDto = new ContractContractPeriodInDto();
			contractContractPeriodInDto.setIdContract(serviceAuthorizationHeaderDto.getIdContract());
			contractContractPeriodInDto.setNbrCnperPeriod(serviceAuthorizationHeaderDto.getCnperPeriod());
			// CSEC11D
			// select a full row from the contract & contract period table
			ContractContractPeriodOutDto contractContractPeriodOutDto = contractDao
					.getCntrctByIdPeriodAndStatus(contractContractPeriodInDto);
			if (!ObjectUtils.isEmpty(contractContractPeriodOutDto)) {
				serviceAuthorizationHeaderDto.setIdCntrctManager(contractContractPeriodOutDto.getIdCntrctManager());
				serviceAuthorizationHeaderDto.setIdContract(contractContractPeriodOutDto.getIdContract());
				serviceAuthorizationHeaderDto.setIndCnperRenewal(contractContractPeriodOutDto.getIndCnperRenewal());
				serviceAuthorizationHeaderDto.setDtCnperStart(contractContractPeriodOutDto.getDtCnperStart());
				serviceAuthorizationHeaderDto.setDtCnperClosure(contractContractPeriodOutDto.getDtCnperClosure());
				serviceAuthorizationHeaderDto.setCdCntrctRegion(contractContractPeriodOutDto.getCdCntrctRegion());
				serviceAuthorizationHeaderDto.setCdCnperStatus(contractContractPeriodOutDto.getCdCnperStatus());
				serviceAuthorizationHeaderDto
						.setIndCntrctBudgLimit(contractContractPeriodOutDto.getIndCntrctBudgLimit());
				if (1 != contractContractPeriodOutDto.getNbrCnperPeriod()) {
					// CSEC12D
					// retrieve a full row from the CONTRACT PERIOD table based
					// on an ID CONTRACT and Period Nbr
					ContractPeriod contractPeriod = contractDao
							.getContractPeriodById(serviceAuthorizationHeaderDto.getIdContract(), (byte) 1);
					if (!ObjectUtils.isEmpty(contractPeriod)) {
						serviceAuthorizationHeaderDto.setDtCnperStart(contractPeriod.getDtCnperStart());
					}
				}
			} else {
				serviceAuthorizationHeaderDto.setErrorDto(addError(8146));
			}
		} else {
			serviceAuthorizationHeaderDto.setErrorDto(addError(8146));
		}
		if(ObjectUtils.isEmpty(serviceAuthorizationHeaderDto.getErrorDto())){
			SubcontrListRtrvReq subcontrListRtrvReq = new SubcontrListRtrvReq();
			subcontrListRtrvReq.setIdRsrcLinkParent(serviceAuthorizationHeaderDto.getIdResource().longValue());
			subcontrListRtrvReq.setIndSbcntrPredisplay(null);
			SubcontrListRtrvRes subcontrListRtrvRes = sbcntrListService.findSubcontractor(subcontrListRtrvReq);
			serviceAuthorizationHeaderDto.setSubContractorList(subcontrListRtrvRes.getSbcntrListRtrvoDtoList());
		}
		serviceAuthorizationHeaderRes.setServiceAuthorizationHeaderDto(serviceAuthorizationHeaderDto);
		return serviceAuthorizationHeaderRes;
	}

	/**
	 * Adds the error.
	 *
	 * @param errorCode
	 *            the error code
	 */
	private ErrorDto addError(int errorCode) {
		ErrorDto error = new ErrorDto();
		error.setErrorCode(errorCode);
		return error;
	}

	/**
	 * Method Name: checkIfSubcareOpenStagesExists Method Description: This
	 * method is used to see if the stage is open for Subcare of family subcare
	 * stages.
	 * 
	 * @param serviceAuthorizationHeaderReq
	 * @param indRetVal
	 * @param indFoundSub
	 */
	private void checkIfSubcareOpenStagesExists(ServiceAuthorizationHeaderReq serviceAuthorizationHeaderReq,
			String indRetVal, boolean indFoundSub) {

		StageUnitInDto stageUnitInDto = new StageUnitInDto();
		stageUnitInDto.setIdCase(serviceAuthorizationHeaderReq.getIdCase());
		// retrieve a full row from STAGE using ID_CASE as the input
		// CLSC59D
		List<StageUnitOutDto> stageUnitOutDtoList = stageUnitDao.getStageDetails(stageUnitInDto);
		// Loop through all the rows returned and see if
		// there have been any SUBcare or Family SUbcare stages opened
		if (!ObjectUtils.isEmpty(stageUnitOutDtoList) && !stageUnitOutDtoList.isEmpty()) {
			for (StageUnitOutDto stageUnitOutDto : stageUnitOutDtoList) {
				// 1. Investigation stage
				// 2. Subcare stage Two if's here, one if the Family
				// Reunification stage is open and one if there is an
				// Adoption stage open.
				if (((INVESTIGATION.equals(serviceAuthorizationHeaderReq.getCdStage()))
						&& (SUBCARE.equals(stageUnitOutDto.getCdStage())
								|| FAMILY_SUBCARE.equals(stageUnitOutDto.getCdStage())
								|| FAMILY_REUNIF.equals(stageUnitOutDto.getCdStage())
								|| ADOPTION.equals(stageUnitOutDto.getCdStage())
								|| FAMILY_PRES.equals(stageUnitOutDto.getCdStage()))
						&& (ObjectUtils.isEmpty(stageUnitOutDto.getDtStageClose()) || ServiceConstants.MAX_DATE_STRING
								.equals(DateUtils.stringDate(stageUnitOutDto.getDtStageClose()))))
						|| ((SUBCARE.equals(serviceAuthorizationHeaderReq.getCdStage()))
								&& (FAMILY_REUNIF.equals(stageUnitOutDto.getCdStage()))
								&& (ObjectUtils.isEmpty(stageUnitOutDto.getDtStageClose())
										|| ServiceConstants.MAX_DATE_STRING
												.equals(DateUtils.stringDate(stageUnitOutDto.getDtStageClose()))))) {
					indRetVal = ServiceConstants.FND_FAILURE;
					indFoundSub = true;
					break;
				}
				// Subcare with an Adoption stage open
				// If the Adoption stage is open,
				// we have to make sure it's an Adoption stage for that
				// child.
				else if ((SUBCARE.equals(serviceAuthorizationHeaderReq.getCdStage()))
						&& (ADOPTION.equals(stageUnitOutDto.getCdStage()))
						&& (ObjectUtils.isEmpty(stageUnitOutDto.getDtStageClose()) || ServiceConstants.MAX_DATE_STRING
								.equals(DateUtils.stringDate(stageUnitOutDto.getDtStageClose())))) {
					// CLSC18D
					// This call retrieves all principals linked to stage along
					// with their county, region, name, stage role, & stage
					// relation
					List<StagePrincipalDto> stagePrincipalDtoList1 = stagePersonLinkDao
							.getStagePrincipalByIdStageType(serviceAuthorizationHeaderReq.getIdStage(), PRINCIPAL);
					if (!ObjectUtils.isEmpty(stagePrincipalDtoList1) && !stagePrincipalDtoList1.isEmpty()) {
						StagePrincipalDto stagePrincipalDto = stagePrincipalDtoList1.stream()
								.filter(o -> PRIMARY_CHILD.equals(o.getCdStagePersRole())).findAny().orElse(null);
						// Call stagePersonLinkDao again passing in the
						// Adoption
						// stage id to determine the PC in the Adoption
						// stage
						List<StagePrincipalDto> stagePrincipalDtoList2 = stagePersonLinkDao
								.getStagePrincipalByIdStageType(stageUnitOutDto.getIdStage(), PRINCIPAL);
						if (!ObjectUtils.isEmpty(stagePrincipalDtoList2) && !stagePrincipalDtoList2.isEmpty()
								&& !ObjectUtils.isEmpty(stagePrincipalDto)) {
							indFoundSub = stagePrincipalDtoList2.stream()
									.anyMatch(o -> (PRIMARY_CHILD.equals(o.getCdStagePersRole())
											&& o.getIdPerson().equals(stagePrincipalDto.getIdPerson())));
							if (indFoundSub)
								indRetVal = ServiceConstants.FND_FAILURE;
							break;
						}
					}

				}
				// Family Preservation stage, Family Subcare Stage,
				// Should not be able to create a new Service Auth in
				// FSU when an FRE stage is open.
				else if (((FAMILY_PRES.equals(serviceAuthorizationHeaderReq.getCdStage()))
						&& (FAMILY_SUBCARE.equals(stageUnitOutDto.getCdStage()))
						&& (ObjectUtils.isEmpty(stageUnitOutDto.getDtStageClose()) || ServiceConstants.MAX_DATE_STRING
								.equals(DateUtils.stringDate(stageUnitOutDto.getDtStageClose()))))
						|| ((FAMILY_SUBCARE.equals(serviceAuthorizationHeaderReq.getCdStage()))
								&& (FAMILY_REUNIF.equals(stageUnitOutDto.getCdStage()))
								&& (ObjectUtils.isEmpty(stageUnitOutDto.getDtStageClose())
										|| ServiceConstants.MAX_DATE_STRING
												.equals(DateUtils.stringDate(stageUnitOutDto.getDtStageClose()))))) {
					indRetVal = ServiceConstants.FND_FAILURE;
					indFoundSub = true;
					break;
				}

			}

		}

	}

	/**
	 * Method Name: getServiceAuthorizationDetailList Method description: this
	 * method is used to get the list of service authorization detail.
	 * 
	 * @param serviceAuthorizationDetailReq
	 * @param serviceAuthorizationHeaderDto
	 */
	private void getServiceAuthorizationDetailList(ServiceAuthorizationDetailReq serviceAuthorizationDetailReq,
			ServiceAuthorizationHeaderDto serviceAuthorizationHeaderDto) {
		// CLSS24D
		// get the list of service authorization
		// detail for particular idSvcAuth
		List<ServiceAuthorizationDetailDto> svcAuthDtlList = svcAuthDetailDao
				.getSerAuthDetail(serviceAuthorizationDetailReq);
		// see if the list obtained is not empty and copy all the values to the
		// other dto which can be set to response later.
		if (!ObjectUtils.isEmpty(svcAuthDtlList)) {
			List<ServiceAuthDetailDto> serviceAuthDetailDtoLs = new ArrayList<>();
			svcAuthDtlList.forEach(o -> {
				ServiceAuthDetailDto serviceAuthDetailDto = new ServiceAuthDetailDto();
				serviceAuthDetailDto.setIdSvcAuthDtl(o.getIdSvcAuthDtl());
				serviceAuthDetailDto.setNmPersonFull(o.getNmPersonFull());
				serviceAuthDetailDto.setAuthDtlSvc(o.getCdSvcAuthDtlSvc());
				serviceAuthDetailDto.setAuthDtlUnitReq(o.getSvcAuthDtlUnitsReq().doubleValue());
				serviceAuthDetailDto.setDtSvcAuthDtlBegin(o.getDtSvcAuthDtlBegin());
				serviceAuthDetailDto.setDtSvcAuthDtlTerm(o.getDtSvcAuthDtlTerm());
				serviceAuthDetailDto.setDtSvcAuthDtlEnd(o.getDtSvcAuthDtlEnd());
				serviceAuthDetailDto.setAuthDtlAuthType(o.getCdSvcAuthDtlAuthType());
				if (!ObjectUtils.isEmpty(o.getAmtSvcAuthDtlAmtReq()))
					serviceAuthDetailDto.setAuthDtlAmtReq(o.getAmtSvcAuthDtlAmtReq().doubleValue());
				serviceAuthDetailDtoLs.add(serviceAuthDetailDto);
			});
			serviceAuthorizationHeaderDto.setServiceAuthDetailDtoLs(serviceAuthDetailDtoLs);
		}
	}

	/**
	 * Method name:getContractInformation Method Description: THis method is
	 * used to get the contract information.
	 * 
	 * @param serviceAuthorizationHeaderDto
	 * @param contractCntyPeriodInDto
	 */
	private void getContractInformation(ServiceAuthorizationHeaderDto serviceAuthorizationHeaderDto,
			ContractCountyPeriodInDto contractCntyPeriodInDto) {
		// CSEC10D
		// retrieve a row from Contract_County given
		// the host variable match when it falls
		// within a specific date period.
		List<ContractCountyOutDto> contractCountyOutDtoList = contractDao.getContarctCounty(contractCntyPeriodInDto);
		if (!ObjectUtils.isEmpty(contractCountyOutDtoList)) {
			serviceAuthorizationHeaderDto.setCnperPeriod(contractCountyOutDtoList.get(0).getNbrCncntyPeriod());
			serviceAuthorizationHeaderDto.setCncntyVersion(contractCountyOutDtoList.get(0).getNbrCncntyVersion());
			ContractContractPeriodInDto contractContractPeriodInDto = new ContractContractPeriodInDto();
			contractContractPeriodInDto.setIdContract(contractCountyOutDtoList.get(0).getIdContract());
			contractContractPeriodInDto.setNbrCnperPeriod(contractCountyOutDtoList.get(0).getNbrCncntyPeriod());
			// CSEC57D
			// select a full row from the contract &
			// contract period table.
			ContractContractPeriodOutDto contractContractPeriodOutDto = contractDao
					.getContarctContractPeriod(contractContractPeriodInDto);
			if (!ObjectUtils.isEmpty(contractContractPeriodOutDto)) {
				serviceAuthorizationHeaderDto.setIdCntrctManager(contractContractPeriodOutDto.getIdCntrctManager());
				serviceAuthorizationHeaderDto.setDtCnperStart(contractContractPeriodOutDto.getDtCnperStart());
				serviceAuthorizationHeaderDto.setDtCnperClosure(contractContractPeriodOutDto.getDtCnperClosure());
				serviceAuthorizationHeaderDto.setIndCnperRenewal(contractContractPeriodOutDto.getIndCnperRenewal());
				serviceAuthorizationHeaderDto.setCdCnperStatus(contractContractPeriodOutDto.getCdCnperStatus());
				serviceAuthorizationHeaderDto.setCdCntrctRegion(contractContractPeriodOutDto.getCdCntrctRegion());
				serviceAuthorizationHeaderDto
						.setIndCntrctBudgLimit(contractContractPeriodOutDto.getIndCntrctBudgLimit());
			}
		}
	}

	/**
	 * Method Name:checkForValidations Method description: This method is used
	 * to get the errors after checking multiple conditions.
	 * 
	 * @param serviceAuthorizationHeaderDto
	 */
	private void checkForValidations(ServiceAuthorizationHeaderDto serviceAuthorizationHeaderDto) {
		// to return the sum of all completed athorizations for
		// SFI services in a given stage.
		Date latestLastUpdate = null;
		boolean eventExists = false;
		serviceAuthorizationHeaderDto.setErrorDto(null);
		if (!ObjectUtils.isEmpty(serviceAuthorizationHeaderDto.getIdEvent())
				&& serviceAuthorizationHeaderDto.getIdEvent() != 0L
				&& ObjectUtils.isEmpty(serviceAuthorizationHeaderDto.getIdSvcAuth())) {
			eventExists = serviceAuthorizationDao
					.checkSvcAuthEventLinkExists(serviceAuthorizationHeaderDto.getIdEvent());
			if(eventExists){
				serviceAuthorizationHeaderDto.setCdEventStatus(ServiceConstants.SVC_CD_EVENT_STATUS_NEW);
			}
		}
		if (!ObjectUtils.isEmpty(serviceAuthorizationHeaderDto.getIdSvcAuth())) {
			latestLastUpdate = serviceAuthHeaderDao
					.getDtLastUpdateForServAuthHeader(serviceAuthorizationHeaderDto.getIdSvcAuth().longValue());
		}
		if ((!ObjectUtils.isEmpty(serviceAuthorizationHeaderDto.getIdSvcAuth())
				&& serviceAuthorizationHeaderDto.getIdSvcAuth() != 0
				&& !serviceAuthorizationHeaderDto.getDtLastUpdate().equals(latestLastUpdate)) || eventExists) {
			// MSG_CMN_TMSTAMP_MISMATCH
			serviceAuthorizationHeaderDto.setErrorDto(addError(2046));
		} else {
			EventDto eventDto = null;
			if (!ObjectUtils.isEmpty(serviceAuthorizationHeaderDto.getIdEvent())
					&& serviceAuthorizationHeaderDto.getIdEvent() != 0L) {
				eventDto = eventDao.getEventByid(serviceAuthorizationHeaderDto.getIdEvent());
			}
			if (KINSHIP_SERVICES.contains(serviceAuthorizationHeaderDto.getCdSvcAuthService())) {
				CpsCheckListInDto cpsCheckListInDto = new CpsCheckListInDto();
				cpsCheckListInDto.setIdStage(serviceAuthorizationHeaderDto.getIdStage());
				// CSESD8D
				// retrieve SFI effective and ebd dates from CPS_CHECKLIST table
				// given Idstage
				// to retrieve the eligibility dates
				List<CpsCheckListOutDto> cpsCheckListOutDtoList = cpsCheckListDao.csesc9dQUERYdam(cpsCheckListInDto);
				if (!ObjectUtils.isEmpty(cpsCheckListOutDtoList) && !cpsCheckListOutDtoList.isEmpty()) {
					// see if the eligible dates are empty of max dates
					if (ObjectUtils.isEmpty(cpsCheckListOutDtoList.get(0).getDtEligStart())
							|| ServiceConstants.MAX_DATE_STRING.equals(cpsCheckListOutDtoList.get(0).getDtEligStart())
							|| ObjectUtils.isEmpty(cpsCheckListOutDtoList.get(0).getDtEligEnd())
							|| ServiceConstants.MAX_DATE_STRING.equals(cpsCheckListOutDtoList.get(0).getDtEligEnd())) {
						// MSG_SVC_AUTH_NO_SFI
						serviceAuthorizationHeaderDto.setErrorDto(addError(55461));
					}
				} else {
					// MSG_SVC_AUTH_NO_SFI
					serviceAuthorizationHeaderDto.setErrorDto(addError(55461));
				}

			}

			// checks the Rel/Int of a care giver from the Stage_person_link
			// Table
			// using IdPrimaryClient as input) if service code is one in
			// Non-TANF
			// service codes and we are adding new service auth. See also
			// Kinship
			// design document.
			if ((SVC_AUTH_PRIMARY.equals(serviceAuthorizationHeaderDto.getCdSvcAuthService())
					|| SVC_AUTH_REL_INTEG_NONTANF_68F.equals(serviceAuthorizationHeaderDto.getCdSvcAuthService())
					|| SVC_AUTH_FICTIVE.equals(serviceAuthorizationHeaderDto.getCdSvcAuthService())
					|| SVC_AUTH_68N.equals(serviceAuthorizationHeaderDto.getCdSvcAuthService())
					|| SVC_AUTH_OTH_INTEG_NONTANF_68H.equals(serviceAuthorizationHeaderDto.getCdSvcAuthService()))
					&& (ObjectUtils.isEmpty(serviceAuthorizationHeaderDto.getIdEvent())
							|| serviceAuthorizationHeaderDto.getIdEvent() == 0)) {
				boolean indErrorMessage = true;
				List<String> stagePerRelint = Collections.unmodifiableList(
						Arrays.asList("SS", "AS", "AU", "CO", "FL", "GD", "GE", "GF", "NN", "SB", "ST"));
				// CLSS91D
				// to check the Rel/Int of a care giver from the
				// Stage_person_link
				// Table using IdPrimaryClient as input
				StagePersonLink stagePersonLink = stagePersonLinkDao.getStagePersonLink(
						serviceAuthorizationHeaderDto.getIdStage(),
						Long.valueOf(serviceAuthorizationHeaderDto.getPrimaryClient()));
				if (!ObjectUtils.isEmpty(stagePersonLink)) {
					// check if the services are Kinship services
					if (((SVC_AUTH_PRIMARY.equals(serviceAuthorizationHeaderDto.getCdSvcAuthService())
							|| SVC_AUTH_REL_INTEG_NONTANF_68F
									.equals(serviceAuthorizationHeaderDto.getCdSvcAuthService())
							|| SVC_AUTH_68N.equals(serviceAuthorizationHeaderDto.getCdSvcAuthService()))
							&& stagePerRelint.contains(stagePersonLink.getCdStagePersRelInt()))
							|| ((SVC_AUTH_FICTIVE.equals(serviceAuthorizationHeaderDto.getCdSvcAuthService())
									|| SVC_AUTH_OTH_INTEG_NONTANF_68H
											.equals(serviceAuthorizationHeaderDto.getCdSvcAuthService()))
									&& FICTIVE_KIN.equals(stagePersonLink.getCdStagePersRelInt()))) {
						indErrorMessage = false;
					}
					if (indErrorMessage) {
						// MSG_SVC_AUTH_RELINT_CODE_MISMATCH
						serviceAuthorizationHeaderDto.setErrorDto(addError(55216));
						if (!ObjectUtils.isEmpty(eventDto)) {
							serviceAuthorizationHeaderDto.setCdEventStatus(eventDto.getCdEventStatus());
						} else {
							serviceAuthorizationHeaderDto.setCdEventStatus(ServiceConstants.SVC_CD_EVENT_STATUS_NEW);
						}
					}
				} else {
					// MSG_SVC_AUTH_RELINT_CODE_MISMATCH
					serviceAuthorizationHeaderDto.setErrorDto(addError(55216));
					if (!ObjectUtils.isEmpty(eventDto)) {
						serviceAuthorizationHeaderDto.setCdEventStatus(eventDto.getCdEventStatus());
					} else {
						serviceAuthorizationHeaderDto.setCdEventStatus(ServiceConstants.SVC_CD_EVENT_STATUS_NEW);
					}
				}
			}

			// Add edit for NON-TANF Kinship Service Codes If service code
			// selected
			// is 68B, 68C, 68D, 68E and Resource selected is not in Texas then
			// issue error message.
			if (ObjectUtils.isEmpty(serviceAuthorizationHeaderDto.getErrorDto())
					&& (SVC_AUTH_PRIMARY.equals(serviceAuthorizationHeaderDto.getCdSvcAuthService())
							|| SVC_AUTH_REL_FLEX_68C.equals(serviceAuthorizationHeaderDto.getCdSvcAuthService())
							|| SVC_AUTH_FICTIVE.equals(serviceAuthorizationHeaderDto.getCdSvcAuthService())
							|| SVC_AUTH_68N.equals(serviceAuthorizationHeaderDto.getCdSvcAuthService())
							|| SVC_AUTH_OTH_FLEX_68E.equals(serviceAuthorizationHeaderDto.getCdSvcAuthService()))) {
				// CRES04D
				// to retrieve Resource Address State
				ResourceDto resourceDto = capsResourceDao
						.getResourceById(serviceAuthorizationHeaderDto.getIdResource().longValue());
				if (!ObjectUtils.isEmpty(resourceDto)
						&& !ServiceConstants.CSTATE_TX.equals(resourceDto.getCdRsrcState())) {
					// MSG_SVCCODE_RESIDENCY_INVLD
					serviceAuthorizationHeaderDto.setErrorDto(addError(55527));
				}
			}

			// if the Category is Other and service is either of the two
			// services
			// 67A or 67B and the function is ADD then find out if a relative
			// placement has been made otherwise skip.
			if (ObjectUtils.isEmpty(serviceAuthorizationHeaderDto.getErrorDto())
					&& SERVICE_CATEGORY.equals(serviceAuthorizationHeaderDto.getCdSvcAuthCategory())
					&& (ObjectUtils.isEmpty(serviceAuthorizationHeaderDto.getIdEvent())
							|| serviceAuthorizationHeaderDto.getIdEvent() == 0)
					&& (SERVICE_CODE_INT.equals(serviceAuthorizationHeaderDto.getCdSvcAuthService())
							|| SERVICE_CODE_FLX.equals(serviceAuthorizationHeaderDto.getCdSvcAuthService()))) {
				// CSEC58D
				// Retrieves ID PERSON of primiary child, ID_CASE, and and
				// DT_PERSON_BIRTH using ID_STAGE
				List<PersonDtlDto> personDtlDtoList = personDao.getPersonDtlByIdStage(
						serviceAuthorizationHeaderDto.getIdStage(), ServiceConstants.PRIMARY_CHILD_ROLE);
				if (!ObjectUtils.isEmpty(personDtlDtoList) && !personDtlDtoList.isEmpty()) {
					// CSECC1D
					// retreive a Relative Placement from the PLACEMENT table
					// where
					// ID PERSON = the host and Dt Plcmt Strt <= input date and
					// input date =< Max and IND PLCMT ACT PLANNED = true
					PlacementDto placementDto = placementDao.retrieveRelativePlacement(
							personDtlDtoList.get(0).getIdPerson(), serviceAuthorizationHeaderDto.getDtSvcAuthEff());
					if (!ObjectUtils.isEmpty(placementDto)) {
						// check if the dtDtPlcmtEnd retrieved by placementDto
						// occurred prior to the Svc Auth effective date. If so,
						// then the Placement has ended and the no Placement
						// message
						// should be displayed.
						if ((!ObjectUtils.isEmpty(placementDto.getDtPlcmtEnd()) && DateUtils
								.isAfter(placementDto.getDtPlcmtEnd(), serviceAuthorizationHeaderDto.getDtSvcAuthEff()))
								|| !ServiceConstants.PLCMT_LIV_ARR_PRIMARY.equals(placementDto.getCdPlcmtLivArr())) {
							// MSG_SVC_AUTH_NO_PLCMT
							serviceAuthorizationHeaderDto.setErrorDto(addError(55012));
							if (!ObjectUtils.isEmpty(eventDto)) {
								serviceAuthorizationHeaderDto.setCdEventStatus(eventDto.getCdEventStatus());
							} else {
								serviceAuthorizationHeaderDto
										.setCdEventStatus(ServiceConstants.SVC_CD_EVENT_STATUS_NEW);
							}
						}
					} else {
						// MSG_SVC_AUTH_NO_PLCMT
						serviceAuthorizationHeaderDto.setErrorDto(addError(55012));
						if (!ObjectUtils.isEmpty(eventDto)) {
							serviceAuthorizationHeaderDto.setCdEventStatus(eventDto.getCdEventStatus());
						} else {
							serviceAuthorizationHeaderDto.setCdEventStatus(ServiceConstants.SVC_CD_EVENT_STATUS_NEW);
						}
					}
				}
			}
		}
	}

	/**
	 * Method Name:callPostEvent Method Description: This call is used to
	 * populate input to call the postEvent and call the postEvent
	 * 
	 * @param serviceAuthorizationHeaderReq
	 * @param serviceAuthorizationHeaderDto
	 * @return
	 */
	private PostEventOPDto callPostEvent(ServiceAuthorizationHeaderReq serviceAuthorizationHeaderReq,
			ServiceAuthorizationHeaderDto serviceAuthorizationHeaderDto) {
		PostEventIPDto postEventIPDto = new PostEventIPDto();
		postEventIPDto.setCdEventStatus(serviceAuthorizationHeaderDto.getCdEventStatus());
		postEventIPDto.setCdTask(serviceAuthorizationHeaderDto.getCdTask());
		postEventIPDto.setCdEventType(serviceAuthorizationHeaderDto.getCdEventType());
		postEventIPDto.setEventDescr(serviceAuthorizationHeaderDto.getTxtEventDescription());
		postEventIPDto.setIdEvent(serviceAuthorizationHeaderDto.getIdEvent());
		postEventIPDto.setIdStage(serviceAuthorizationHeaderDto.getIdStage());
		postEventIPDto.setDtEventOccurred(serviceAuthorizationHeaderDto.getDtEventOccurred());
		postEventIPDto.setIdCase(serviceAuthorizationHeaderDto.getIdCase());
		if (!ObjectUtils.isEmpty(serviceAuthorizationHeaderReq.getIdPerson())) {
			postEventIPDto.setIdPerson(serviceAuthorizationHeaderReq.getIdPerson());
		}
		// postEventIPDto.setTsLastUpdate(/*To-do*/);
		ServiceReqHeaderDto ServiceReqHeaderDto = new ServiceReqHeaderDto();
		ServiceReqHeaderDto.setReqFuncCd(serviceAuthorizationHeaderReq.getReqFuncCd());
		// CCMN01U
		PostEventOPDto postEventOPDto = postEventService.checkPostEventStatus(postEventIPDto, ServiceReqHeaderDto);
		return postEventOPDto;
	}

	/**
	 * Method Name: callToDoFunction Method Description: This Method is used to
	 * pupulate the input to call todocommonfunction and call the service.
	 * 
	 * @param serviceAuthorizationHeaderDto
	 */
	private void callToDoFunction(ServiceAuthorizationHeaderDto serviceAuthorizationHeaderDto) {
		// If authorization occurs from a contract outside the users
		// region, create a todo for that region's supervisor.
		// Only create the todo if the Service Authorization has been
		// completed.
		if (ServiceConstants.STRING_IND_Y.equals(serviceAuthorizationHeaderDto.getIndAuthDiffRegion())
				&& ServiceConstants.STRING_IND_Y.equals(serviceAuthorizationHeaderDto.getIndFrstTmComp())) {
			TodoCommonFunctionInputDto todoCommonFunctionInputDto = new TodoCommonFunctionInputDto();
			TodoCommonFunctionDto todoCommonFunctionDto = new TodoCommonFunctionDto();
			todoCommonFunctionDto.setSysIdTodoCfStage(serviceAuthorizationHeaderDto.getIdStage());
			todoCommonFunctionDto.setSysIdTodoCfPersAssgn(serviceAuthorizationHeaderDto.getIdCntrctManager());
			todoCommonFunctionDto.setSysCdTodoCf(CON7);
			todoCommonFunctionInputDto.setTodoCommonFunctionDto(todoCommonFunctionDto);
			// CSUB40U
			todoCommonFunctionService.TodoCommonFunction(todoCommonFunctionInputDto);
		}
	}

	/**
	 * Method Name: callToDoForPerson Method Desctiption: This method is used to
	 * call the todocommon function for all the person.
	 * 
	 * @param svcAuthDetailDtoList
	 * @param serviceAuthorizationHeaderReq
	 * @param serviceAuthorizationHeaderDto
	 * @param serviceAuthorizationHeaderRes
	 */
	private void callToDoForPerson(List<SVCAuthDetailDto> svcAuthDetailDtoList,
			ServiceAuthorizationHeaderReq serviceAuthorizationHeaderReq,
			ServiceAuthorizationHeaderDto serviceAuthorizationHeaderDto,
			ServiceAuthorizationHeaderRes serviceAuthorizationHeaderRes) {
		svcAuthDetailDtoList.forEach(o -> {
			// CINV81D
			// retrieves the person information
			PersonGenderSpanishDto personGenderSpanishDto = populateFormDao.isSpanGender(o.getIdPerson());
			if (!ObjectUtils.isEmpty(personGenderSpanishDto)) {
				TodoCommonFunctionInputDto todoCommonFunctionInputDto = new TodoCommonFunctionInputDto();
				TodoCommonFunctionDto todoCommonFunctionDto = new TodoCommonFunctionDto();
				todoCommonFunctionDto.setSysIdTodoCfStage(serviceAuthorizationHeaderDto.getIdStage());
				todoCommonFunctionDto.setSysIdTodoCfPersAssgn(serviceAuthorizationHeaderReq.getIdPerson());
				todoCommonFunctionInputDto.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_ADD);
				todoCommonFunctionDto.setSysIdTodoCfEvent(serviceAuthorizationHeaderRes.getIdEvent());
				todoCommonFunctionDto.setSysCdTodoCf(CON2);
				todoCommonFunctionDto.setDtSysDtTodoCfDueFrom(o.getDtSvcAuthDtlShow());
				todoCommonFunctionInputDto.setTodoCommonFunctionDto(todoCommonFunctionDto);
				// CSUB40U
				todoCommonFunctionService.TodoCommonFunction(todoCommonFunctionInputDto);
			}
		});

	}

	/**
	 *
	 * @param serviceAuthorizationHeaderDto
	 * Rendering the list of saved Inhome details from DB
	 */
	private void getInhomeDetails(ServiceAuthorizationHeaderDto serviceAuthorizationHeaderDto) {
		List<ApsInHomeTasksDto>  resultList =  apsInhomeTasksDao.getInhomeTasksBySrvicsId(Long.parseLong(serviceAuthorizationHeaderDto.getIdSvcAuth().toString()));
	    List<String> inHomeList = new ArrayList<String>();
		resultList.forEach(dto -> inHomeList.add(dto.getInHomeSvcAuthTask()) );
		serviceAuthorizationHeaderDto.setCbxInHome(inHomeList);
	}

	/**
	 *
	 * @param serviceAuthorizationHeaderDto
	 * Rendering the persons form stage person table and filtering CD statge pers relation is DR to display as Physican
	 * in Service Authorization APS details screen
	 */
	private void getPhysicanListByStageId(ServiceAuthorizationHeaderDto serviceAuthorizationHeaderDto) {
		Map<Long, String> resultsMap = personDao.getPhysicanNamesForServiceAuthHeaderByStageId(serviceAuthorizationHeaderDto.getIdStage());
		serviceAuthorizationHeaderDto.setPhysicanList(resultsMap);
	}

	/**
	 *
	 * @param serviceAuthorizationHeaderDto
	 * Getting and setting the Person's Living Arrangment by Person id for primary client in Event
	 */

	private void getPersLivingArrangmentForPersTable( ServiceAuthorizationHeaderDto serviceAuthorizationHeaderDto) {
		Person personResult = personDao.getPerson(serviceAuthorizationHeaderDto.getIdPrimaryClient());
		serviceAuthorizationHeaderDto.setPrimaryPersLivingArrangment(personResult.getCdPersonLivArr());
	}

	/**
	 * For APS program set Primary client and Client map other programs  only populate client map
	 *
	 * @param serviceAuthorizationHeaderReq - requested for add or view
	 * @param indRetVal - return value
	 * @param serviceAuthorizationHeaderRes - Response back to UI
	 * @param serviceAuthorizationHeaderDto - DB data
	 */
	private void setClientListAndPrimaryClient(ServiceAuthorizationHeaderReq serviceAuthorizationHeaderReq, String indRetVal, ServiceAuthorizationHeaderRes serviceAuthorizationHeaderRes, ServiceAuthorizationHeaderDto serviceAuthorizationHeaderDto) {
		// regardless of Event Status return all Principals for the Id
		// Stage
		List<StagePrincipalDto> stagePrincipalDtoList = stagePersonLinkDao
				.getStagePrincipalByIdStageType(serviceAuthorizationHeaderReq.getIdStage(), PRINCIPAL);
		List<String> codes = Arrays.asList(ServiceConstants.STAGE_PERS_ROLE_VP, ServiceConstants.STAGE_PERS_ROLE_VC, ServiceConstants.STAGE_PERS_ROLE_CL);
		if (!ObjectUtils.isEmpty(stagePrincipalDtoList) && !stagePrincipalDtoList.isEmpty()) {
			Map<String, String> clientList = new HashMap<>();
			if (CodesConstant.CPGRMS_APS.equals(serviceAuthorizationHeaderReq.getCdStageProgram())) {
				for (StagePrincipalDto stagePrincipalDto : stagePrincipalDtoList) {
					if (codes.contains(stagePrincipalDto.getCdStagePersRole())) {
						clientList.put(String.valueOf(stagePrincipalDto.getIdPerson()),
								stagePrincipalDto.getNmPersonFull() + " " +
										setPersonSuffix(stagePrincipalDto));
						serviceAuthorizationHeaderDto.setPrimaryClient(String.valueOf(stagePrincipalDto.getIdPerson()));
						break;
					}
				}
			} else {
				stagePrincipalDtoList.forEach(o -> clientList.put(String.valueOf(o.getIdPerson()),
						o.getNmPersonFull() + " " + setPersonSuffix(o)));
			}
			serviceAuthorizationHeaderDto.setClientList(clientList);
			serviceAuthorizationHeaderRes.setStagePrincipalDto(stagePrincipalDtoList);
		} else {
			indRetVal = ServiceConstants.FND_FAILURE;
		}
	}

	/**
	 * Set Person suffix if exists else empty string
	 *
	 * @param stagePrincipalDto - requested dto
	 * @return return string
	 */
	private String setPersonSuffix(StagePrincipalDto stagePrincipalDto) {
		return stagePrincipalDto.getCdPersonSuffix() != null ? stagePrincipalDto.getCdPersonSuffix() : "";
	}

}
