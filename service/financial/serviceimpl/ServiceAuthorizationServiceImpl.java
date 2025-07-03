package us.tx.state.dfps.service.financial.serviceimpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.dto.ErrorDto;
import us.tx.state.dfps.common.dto.TodoCommonFunctionDto;
import us.tx.state.dfps.common.dto.TodoCommonFunctionInputDto;
import us.tx.state.dfps.service.admin.dao.EventPersonLinkProcessDao;
import us.tx.state.dfps.service.admin.dto.EventLinkInDto;
import us.tx.state.dfps.service.admin.dto.LegalActionEventInDto;
import us.tx.state.dfps.service.adoptionasstnc.service.AdoptionAsstncService;
import us.tx.state.dfps.service.casepackage.dao.StagePersonLinkDao;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.EventPersonLinkDao;
import us.tx.state.dfps.service.common.dao.StageDao;
import us.tx.state.dfps.service.common.request.ServiceAuthDetailReq;
import us.tx.state.dfps.service.common.request.ServiceAuthorizationDetailReq;
import us.tx.state.dfps.service.common.response.CommonStringRes;
import us.tx.state.dfps.service.common.response.ServiceAuthDetailRes;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.utils.ReviewUtils;
import us.tx.state.dfps.service.common.utils.TypeConvUtil;
import us.tx.state.dfps.service.dcr.dao.DayCareRequestDao;
import us.tx.state.dfps.service.dcr.dto.DayCarePersonDto;
import us.tx.state.dfps.service.dcr.dto.DayCareRequestDto;
import us.tx.state.dfps.service.dcr.service.DayCareRequestService;
import us.tx.state.dfps.service.fce.dao.FceDao;
import us.tx.state.dfps.service.financial.dao.SvcAuthDetailDao;
import us.tx.state.dfps.service.financial.dto.ContractServiceDto;
import us.tx.state.dfps.service.financial.dto.LegalStatusValueDto;
import us.tx.state.dfps.service.financial.dto.ServiceAuthorizationDetailDto;
import us.tx.state.dfps.service.financial.service.ServiceAuthorizationService;
import us.tx.state.dfps.service.legal.dto.LegalStatusDto;
import us.tx.state.dfps.service.legalstatus.dao.LegalStatusDao;
import us.tx.state.dfps.service.lookup.dao.LookupDao;
import us.tx.state.dfps.service.person.dao.PersonDao;
import us.tx.state.dfps.service.person.dto.PersonInfoDto;
import us.tx.state.dfps.service.placement.dao.CommonApplicationDao;
import us.tx.state.dfps.service.placement.dto.StagePersonLinkCaseDto;
import us.tx.state.dfps.service.resource.dto.ResourceDto;
import us.tx.state.dfps.service.securityauthoriztion.dto.EquivalentSvcDetailDto;
import us.tx.state.dfps.service.securityauthoriztion.dto.KinshipDto;
import us.tx.state.dfps.service.securityauthoriztion.dto.ServiceAuthDetailDto;
import us.tx.state.dfps.service.securityauthoriztion.dto.ServiceAuthPersonDto;
import us.tx.state.dfps.service.subcare.dao.CapsResourceDao;
import us.tx.state.dfps.service.subcare.dto.PlacementDto;
import us.tx.state.dfps.service.workload.dto.EventIdDto;
import us.tx.state.dfps.service.workload.dto.PersonDto;
import us.tx.state.dfps.service.workload.dto.StageDto;
import us.tx.state.dfps.service.workload.dto.StagePersonLinkDto;
import us.tx.state.dfps.service.workload.dto.StagePrincipalDto;
import us.tx.state.dfps.service.workload.service.TodoCommonFunctionService;
import us.tx.state.dfps.xmlstructs.inputstructs.ServiceInputDto;
import us.tx.state.dfps.xmlstructs.outputstructs.RowQtyDto;

/**
 * 
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name: CCMN12S Class
 * Description: This class is doing service Implementation for to do list Mar
 * 30, 2017 - 4:23:07 PM
 */
@Service
@Transactional
public class ServiceAuthorizationServiceImpl implements ServiceAuthorizationService {

	@Autowired
	private SvcAuthDetailDao serviceAuthorizationDetailDao;
	@Autowired
	private PersonDao personDao;
	@Autowired
	private FceDao fceDao;
	@Autowired
	private CapsResourceDao capsResourceDao;
	@Autowired
	private CommonApplicationDao commonAppDao;
	@Autowired
	private EventPersonLinkProcessDao eventPersonLinkProcessDao;
	@Autowired
	private TodoCommonFunctionService todoCommonFunctionService;
	@Autowired
	private StagePersonLinkDao stagePersonLinkDao;
	@Autowired
	private DayCareRequestDao dayCareRequestDao;
	@Autowired
	DayCareRequestService dayCareRequestService;
	@Autowired
	ReviewUtils reviewUtils;
	@Autowired
	AdoptionAsstncService adoptionAsstncService;
	@Autowired
	LookupDao lookupDao;
	@Autowired
	StageDao stageDao;
	@Autowired
	LegalStatusDao legalStatusDao;
	
	@Autowired
	EventPersonLinkDao eventPersonLinkDao;

	public static final Date ROLL_OUT_DATE = DateUtils.date(2007, 8, 26);
	public static final List<String> FICTIVE_KINSHIP_LIST = Collections.unmodifiableList(
			Arrays.asList(ServiceConstants.CSVCCODE_68F, ServiceConstants.CSVCCODE_68G, ServiceConstants.CSVCCODE_68H,
					ServiceConstants.CSVCCODE_68I, ServiceConstants.CSVCCODE_68L, ServiceConstants.CSVCCODE_68M));
	public static final List<String> KINSHIP_LIST_FOR_EQUIVALENCY = Collections
			.unmodifiableList(Arrays.asList(ServiceConstants.CSVCCODE_68B, ServiceConstants.CSVCCODE_68D,
					ServiceConstants.CSVCCODE_68F, ServiceConstants.CSVCCODE_68H, ServiceConstants.CSVCCODE_68N));

	private static final Logger log = Logger.getLogger(ServiceAuthorizationServiceImpl.class);

	public ServiceAuthorizationServiceImpl() {
	}

	/**
	 * 
	 * Method Description: This Method will retrieve a list of Service
	 * Authorization Detail records based upon IdSvcAuth from the Service
	 * Authorization Detail window. It will also retrieve NmPersonFull based
	 * upon IdPerson from the Person table. Service Name: CCON21S
	 * 
	 * @param serviceAuthorizationDetailReq
	 * @return List<ServiceAuthorizationDetailDto>
	 * @throws Exception
	 */
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
			Exception.class })
	public List<ServiceAuthorizationDetailDto> getSerAuthDetail(
			ServiceAuthorizationDetailReq serviceAuthorizationDetailReq) {

		List<ServiceAuthorizationDetailDto> servAuthServiceOutput = serviceAuthorizationDetailDao
				.getSerAuthDetail(serviceAuthorizationDetailReq);
		log.info("TransactionId :" + serviceAuthorizationDetailReq.getTransactionId());
		return servAuthServiceOutput;

	}

	/**
	 * Method Name: selectLatestLegalStatus Method Description: This method
	 * fetches Latest Legal Status Record for the given Person and Legal Status
	 * from the database.
	 * 
	 * @param idPerson
	 * @param cdLegalStatStatus
	 * @return LegalStatusValueDto @
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = false, propagation = Propagation.REQUIRED)
	public LegalStatusValueDto selectLatestLegalStatus(Long idPerson, String cdLegalStatStatus) {
		LegalStatusValueDto statusValueDto = new LegalStatusValueDto();
		statusValueDto = serviceAuthorizationDetailDao.selectLatestLegalStatus(idPerson, cdLegalStatStatus);

		return statusValueDto;
	}

	/**
	 * Method Name: saveServiceAuthDetail Method Description: This is the Save
	 * Service for Service Authorization Detail. First it will check whether or
	 * not the Person has already been authorized services for the resource
	 * during the specified time period. It will then retrieve Budget
	 * information and validate against the Amount Requested. If the Amount
	 * Requested decreases the current budget to less than 15%, then a To Do is
	 * initiated. If the above passes validation, then the Save Dam is called
	 * for Svc Auth Dtl and Event Person Link tables.
	 * 
	 * @param serviceAuthDtlReq
	 *            - contains ServiceAuthorization Detail and list of person to
	 *            be saved
	 * @return ServiceResHeaderDto - gives Error message or Success message
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public ServiceAuthDetailRes saveServiceAuthDetail(ServiceAuthDetailReq serviceAuthDtlReq) {
		ServiceAuthDetailRes serviceAuthDetailRes = new ServiceAuthDetailRes();

		if (!TypeConvUtil.isNullOrEmpty(serviceAuthDtlReq.getServiceAuthDtlDto().getIdSvcAuthDtl())) {
			ErrorDto errorDto = new ErrorDto();
			serviceAuthorizationDetailDao.checkForTimeMisMatchException(serviceAuthDtlReq.getServiceAuthDtlDto(),
					errorDto);
			if (errorDto.getErrorCode() == ServiceConstants.MSG_CMN_TMSTAMP_MISMATCH) {
				serviceAuthDetailRes.setErrorDto(errorDto);
				return serviceAuthDetailRes;
			}
		}
		ErrorDto errorDto = new ErrorDto();
		boolean dupCheckServAuth = false;
		if (!ObjectUtils.isEmpty(serviceAuthDtlReq) && !ObjectUtils.isEmpty(serviceAuthDtlReq.getServiceAuthDtlDto())
				&& !ObjectUtils.isEmpty(serviceAuthDtlReq.getPersonList())) {
			//ALM#13760 : Fix for No Msg Displayed Deletes Prsn from Svc Auth
			   for(Long persId: serviceAuthDtlReq.getPersonList()) {
				
				Boolean bIndCaregiverTXResident = Boolean.FALSE;
				ServiceAuthDetailDto serviceAuthDetailDto = serviceAuthDtlReq.getServiceAuthDtlDto();
				serviceAuthDetailDto.setIdPerson(persId);
				// Kinship processing is done one time to save the
				// Primary care giver or the Fictive kin to the svcAuthDtl
				// table. Other
				// service codes save children to this table. Kinship saves
				// kinship group
				// to the kinship table.

				// to check the age of a given child from the person Table and
				// check each selected child's citizenship in PERSON_DTL
				errorDto = checkFlexibleAgeAndCitizenShip(serviceAuthDetailDto);
				// call CallCRES04D to retrieve Resource Address State
				if (StringUtils.isNotEmpty(serviceAuthDetailDto.getAuthDtlSvc())) {
					if (FICTIVE_KINSHIP_LIST.contains(serviceAuthDetailDto.getAuthDtlSvc())
							&& (ObjectUtils.isEmpty(errorDto.getErrorCode())
									|| ServiceConstants.Zero == errorDto.getErrorCode())) {
						// CRES04D
						ResourceDto resourceDto = capsResourceDao.getResourceById(serviceAuthDtlReq.getIdResource());
						if (!ObjectUtils.isEmpty(resourceDto) && StringUtils.isNotEmpty(resourceDto.getCdRsrcState())
								&& CodesConstant.CSTATE_TX.equals(resourceDto.getCdRsrcState())) {
							bIndCaregiverTXResident = Boolean.TRUE;
						}
						// Non-TANF service codes 68F, 68G, 68H, 68I require
						// that either the
						// caregiver resides OUT OF STATE or the child is NOT a
						// US citizen.
						// If CRES04D shows the caregiver resides in TX, then
						// next check the child's
						// citizenship
						// which must be TMR. If not, issue error message
						// MSG_SVCCODE_NONTANF_INVLD.
						// Check citizenship of PC Primary Child for non-TANF
						// Integration Service Codes
						// 68F and 68H.
						// Check citizenship of each child selected for non-TANF
						// Flex Service Codes 68G
						// and 68I.
						if (bIndCaregiverTXResident) {
							if (!(CodesConstant.CSVCCODE_68G.equalsIgnoreCase(serviceAuthDetailDto.getAuthDtlSvc())
									|| CodesConstant.CSVCCODE_68I
											.equalsIgnoreCase(serviceAuthDetailDto.getAuthDtlSvc()))) {
								// CallCSEC15D Get this stage's Primary Child
								// 'PC'
								// Check the Citizenship of the Primary Child
								// 'PC' If citizenship of PC is not
								// TMR,
								// issue error message (CSES31D) Check
								// Citizenship of Primary Child.
								// Process for non-TANF Integration Service
								// Codes 68F/68H
								errorDto = checkPrimaryChild(serviceAuthDtlReq.getIdStage(), errorDto);
							} else {
								// Check citizenship of each selected child
								// for nonTANF Flexible Service Codes 68G, 68I
								String citizenShip = fceDao.getCdPersonCitizenship(serviceAuthDetailDto.getIdPerson());
								if (StringUtils.isNotEmpty(citizenShip)
										&& !CodesConstant.CCTZNSTA_TMR.equalsIgnoreCase(citizenShip)) {
									errorDto.setErrorCode(ServiceConstants.MSG_SVCCODE_NONTANF_INVLD);
								}
							}
						}
					}

				}
				// check Service code exists in the Equivalency table for the
				// given time period
				// and open stages for the client
				String message = checkWhetherServiceCodeExists(serviceAuthDetailDto, errorDto, serviceAuthDtlReq);
				if (StringUtils.isNotEmpty(message) && !ObjectUtils.isEmpty(serviceAuthDetailDto.getAuthDtlSvc())
						&& StringUtils.isNotEmpty(serviceAuthDetailDto.getAuthDtlSvc())) {
					switch (message) {
					case ServiceConstants.FND_SUCCESS:
						// ALM#13760 : Fix for No Msg Displayed Deletes Prsn from Svc Auth
						if (!dupCheckServAuth) {
							for (Long personId : serviceAuthDtlReq.getPersonList()) {
								if(!ObjectUtils.isEmpty(errorDto.getErrorCode()) && (ServiceConstants.Zero == errorDto.getErrorCode())) {
									ServiceAuthDetailDto serviceAuthDetaildto = serviceAuthDtlReq
											.getServiceAuthDtlDto();
									serviceAuthDetaildto.setIdPerson(personId);
									errorDto = callVerifyDublicate(serviceAuthDetaildto, errorDto, serviceAuthDtlReq);
								}
							}
					}
						
						dupCheckServAuth = true;
						if(!ObjectUtils.isEmpty(errorDto.getErrorCode()) && !(ServiceConstants.Zero == errorDto.getErrorCode())) {
								
							serviceAuthDetailRes.setErrorDto(errorDto);
							break;
						}
						serviceAuthDetailDto.setIdPerson(persId);
						//End of code changes for  ALM#13760 fix
						if (ObjectUtils.isEmpty(errorDto) || ObjectUtils.isEmpty(errorDto.getErrorCode())
								|| ServiceConstants.Zero == errorDto.getErrorCode() && KINSHIP_LIST_FOR_EQUIVALENCY
										.contains(serviceAuthDetailDto.getAuthDtlSvc())) {
							// Retrieves budget amount available for a service
							// CSES36D
							ContractServiceDto contractServiceDto = serviceAuthorizationDetailDao
									.retrieveBudgetAmount(serviceAuthDetailDto, serviceAuthDtlReq);
							message = ServiceConstants.EMPTY_STR;
							if (!ObjectUtils.isEmpty(contractServiceDto)
									&& !ObjectUtils.isEmpty(contractServiceDto.getAmtCnsvcUnitRate())
									&& !ObjectUtils.isEmpty(contractServiceDto.getAmtCnsvcUnitRateUsed())
									&& !ObjectUtils.isEmpty(serviceAuthDetailDto.getAuthDtlAmtReq())
									&& !StringUtils.isEmpty(serviceAuthDtlReq.getCntrctBudgLimit())
									&& ServiceConstants.Y.equalsIgnoreCase(serviceAuthDtlReq.getCntrctBudgLimit())) {
								if ((contractServiceDto.getAmtCnsvcUnitRate()
										- contractServiceDto.getAmtCnsvcUnitRateUsed() < serviceAuthDetailDto
												.getAuthDtlAmtReq())) {
									message = ServiceConstants.FND_SUCCESS;
								} // if the Amount requested plus the Unit Rate
									// Used divided
									// by the Unit rate is greater than 85%
									// processing
									// Note: The Amount requested cannot
									// decrease to remaining
									// balance to less than 15% of the total
									// balance
								else if (ServiceConstants.BUDGET_PCT
										.equals((contractServiceDto.getAmtCnsvcUnitRateUsed()
												+ serviceAuthDetailDto.getAuthDtlAmtReq())
												/ contractServiceDto.getAmtCnsvcUnitRate())) {
									TodoCommonFunctionInputDto todoCommonFunctionInputDto = new TodoCommonFunctionInputDto();
									TodoCommonFunctionDto todoCommonFunctionDto = new TodoCommonFunctionDto();
									todoCommonFunctionDto.setSysCdTodoCf(serviceAuthDtlReq.getSysCdTodoCf());
									todoCommonFunctionDto
											.setSysIdTodoCfPersAssgn(serviceAuthDtlReq.getIdCntrctManager());
									todoCommonFunctionDto.setSysIdTodoCfPersCrea(serviceAuthDtlReq.getIdPerson());
									todoCommonFunctionDto.setSysIdTodoCfStage(serviceAuthDtlReq.getIdStage());
									todoCommonFunctionDto.setDtSysDtTodoCfDueFrom(new Date());
									StringBuilder sysTxtTodoCfDesc = new StringBuilder();
									sysTxtTodoCfDesc.append(ServiceConstants.SVC_TODO_DESC_FIRST)
											.append(serviceAuthDtlReq.getIdCntrctManager())
											.append(ServiceConstants.SVC_TODO_DESC_MIDDLE)
											.append(serviceAuthDtlReq.getSvcAuthDtlSvc())
											.append(ServiceConstants.SVC_TODO_DESC_END);
									todoCommonFunctionDto.setSysTxtTodoCfDesc(sysTxtTodoCfDesc.toString());
									todoCommonFunctionInputDto.setTodoCommonFunctionDto(todoCommonFunctionDto);
									todoCommonFunctionService.TodoCommonFunction(todoCommonFunctionInputDto);
								}

							}
						}
						// For Integration Kinship Service Codes TANF 68B or
						// 68D,
						// check to see if Primary Child's Citizenship is TMR.
						// If so, issue error
						// message.
						if (ObjectUtils.isEmpty(errorDto) || ObjectUtils.isEmpty(errorDto.getErrorCode())
								|| ServiceConstants.Zero == errorDto.getErrorCode()
										&& (CodesConstant.CSVCCODE_68B
												.equalsIgnoreCase(serviceAuthDetailDto.getAuthDtlSvc())
												|| CodesConstant.CSVCCODE_68D
														.equalsIgnoreCase(serviceAuthDetailDto.getAuthDtlSvc())
												|| ServiceConstants.CSVCCODE_68N
														.equalsIgnoreCase(serviceAuthDetailDto.getAuthDtlSvc()))
										&& (ObjectUtils.isEmpty(serviceAuthDetailDto.getIdSvcAuthDtl())
												|| ServiceConstants.ZERO == serviceAuthDetailDto.getIdSvcAuthDtl())) {
							// CallCSEC15D Get this stage's Primary Child 'PC'
							// Check the Citizenship of the Primary Child 'PC'
							// If citizenship of PC is not
							// TMR,
							// issue error message (CSES31D) Check Citizenship
							// of Primary Child.
							// Process for non-TANF Integration Service Codes
							// 68F/68H
							errorDto = checkPrimaryChild(serviceAuthDtlReq.getIdStage(), errorDto);
						}
						if (ObjectUtils.isEmpty(errorDto) || ObjectUtils.isEmpty(errorDto.getErrorCode())
								|| ServiceConstants.ZERO == errorDto.getErrorCode()) {
							// call performAUDForServiceAuthDetail (CAUD13DI) to
							// save Service Authorization
							// Detail in SVC_AUTH_DTL Table
							Long idSvcAuthDtl = serviceAuthorizationDetailDao
									.performAUDForServiceAuthDetail(serviceAuthDetailDto);
							if (StringUtils.isNotEmpty(serviceAuthDetailDto.getAuthDtlAuthType())
									&& (CodesConstant.CSVATYPE_INI
											.equalsIgnoreCase(serviceAuthDetailDto.getAuthDtlAuthType())
											|| CodesConstant.CSVATYPE_ONT
													.equalsIgnoreCase(serviceAuthDetailDto.getAuthDtlAuthType()))
									&& ((ObjectUtils.isEmpty(serviceAuthDetailDto.getIdSvcAuthDtl())
											|| ServiceConstants.ZERO == serviceAuthDetailDto.getIdSvcAuthDtl()))
									&& ServiceConstants.KINSHIP_LIST.contains(serviceAuthDetailDto.getAuthDtlSvc())) {
								// call CAUDJ9D service to update or insert
								// Kinship
								KinshipDto kinshipDto = new KinshipDto();
								kinshipDto.setDtSvcAuthDtlBegin(serviceAuthDetailDto.getDtSvcAuthDtlBegin());
								kinshipDto.setIdPerson(serviceAuthDetailDto.getIdPerson());
								kinshipDto.setDtSvcAuthDtlTerm(serviceAuthDetailDto.getDtSvcAuthDtlTerm());
								kinshipDto.setIdSvcAuthDtl(idSvcAuthDtl);
								kinshipDto.setReqFuncCd(ServiceConstants.REQ_FUNC_CD_ADD);
								serviceAuthorizationDetailDao.performAUDForKinship(kinshipDto);
							}
						if ((ObjectUtils.isEmpty(serviceAuthDetailDto.getIdSvcAuthDtl())
									|| ServiceConstants.ZERO == serviceAuthDetailDto.getIdSvcAuthDtl())) {
								EventLinkInDto eventLinkDto = new EventLinkInDto();
								ServiceInputDto serviceInputDto = new ServiceInputDto();
								//Defect 10923 - Check for the existence of event link
								List<PersonInfoDto> personInfoDto = eventPersonLinkDao
										.getPrsnListByEventId(serviceAuthDtlReq.getIdEvent());
								Boolean entityExists = Boolean.FALSE;
								if (!ObjectUtils.isEmpty(personInfoDto))
									entityExists = personInfoDto.stream()
											.anyMatch(dto -> dto.getIdPerson().equals(serviceAuthDetailDto.getIdPerson()));
								if (!entityExists)
									serviceInputDto.setCreqFuncCd(ServiceConstants.REQ_FUNC_CD_ADD);
								else
									serviceInputDto.setCreqFuncCd(ServiceConstants.REQ_FUNC_CD_UPDATE);
								eventLinkDto.setArchInputStruct(serviceInputDto);
								eventLinkDto.setIdPerson(serviceAuthDetailDto.getIdPerson());
								eventLinkDto.setIdEvent(serviceAuthDtlReq.getIdEvent());
								// service which make an entry in
								// EventPersonLink Table by calling
								// EventPersonLinkProcessDao(CCMN68D)
								eventPersonLinkProcessDao.ccmn68dAUDdam(eventLinkDto);
							}
						}
						break;
					case ServiceConstants.FND_FAILURE:
						break;
					}
					
					if(!ObjectUtils.isEmpty(errorDto.getErrorCode()) && !(ServiceConstants.Zero == errorDto.getErrorCode())) {
						serviceAuthDetailRes.setErrorDto(errorDto);
						break;
					}
				}
				serviceAuthDetailRes.setErrorDto(errorDto);
			}
		}
		return serviceAuthDetailRes;
	}

	/**
	 * 
	 * Method Name: checkFlexibleAgeAndCitizenShip Method Description: This
	 * Function is designed to check the age of a given child from the person
	 * Table using IdplmntChild as input (CLSS94D Dam)and citizenship of each
	 * child(CSES31 Dam) for flex service codes 68C, 68E.
	 * 
	 * @param personId
	 * @param ServiceAuthDetailDto
	 *            - Service Auth Detail to get Begin date and Service code
	 * @return errorDto - return error codes if there is any
	 */
	private ErrorDto checkFlexibleAgeAndCitizenShip(ServiceAuthDetailDto serviceAuthDetailDto) {
		PersonDto personDto = personDao.getPersonById(serviceAuthDetailDto.getIdPerson());
		Integer personAge = ServiceConstants.Zero;
		ErrorDto errorDto = new ErrorDto();
		// Kinship processing is done one time to save the
		// Primary care giver or the Fictive kin to the svcAuthDtl table. Other
		// service codes save children to this table. Kinship saves kinship
		// group
		// to the kinship table.
		if (StringUtils.isNotEmpty(serviceAuthDetailDto.getAuthDtlSvc())
				&& ServiceConstants.FLEXIBLE_BENEFIT_LIST.contains(serviceAuthDetailDto.getAuthDtlSvc())
				&& (ObjectUtils.isEmpty(serviceAuthDetailDto.getIdSvcAuthDtl())
						|| ServiceConstants.ZERO == serviceAuthDetailDto.getIdSvcAuthDtl())) {
			if (!ObjectUtils.isEmpty(personDto.getDtPersonBirth())
					&& !ObjectUtils.isEmpty(serviceAuthDetailDto.getDtSvcAuthDtlBegin())) {
				personAge = DateUtils.calculatePersonsAgeInYears(personDto.getDtPersonBirth(),
						serviceAuthDetailDto.getDtSvcAuthDtlBegin());
				if (personAge >= ServiceConstants.EIGHTEEN) {
					errorDto.setErrorCode(ServiceConstants.MSG_SVC_AUTH_CHILD_OVER_LIMIT);
				}
			} else {
				errorDto.setErrorCode(ServiceConstants.MSG_SVC_AUTH_CHILD_OVER_LIMIT);
			}
			// check each selected child's citizenship in PERSON_DTL for 68C and
			// 68E.
			// If child's citizenship is TMR, issue error message.
			if (StringUtils.isNotEmpty(serviceAuthDetailDto.getAuthDtlSvc())
					&& (CodesConstant.CSVCCODE_68C.equalsIgnoreCase(serviceAuthDetailDto.getAuthDtlSvc())
							|| CodesConstant.CSVCCODE_68E.equalsIgnoreCase(serviceAuthDetailDto.getAuthDtlSvc()))
					&& (ObjectUtils.isEmpty(errorDto.getErrorCode())
							|| ServiceConstants.Zero == errorDto.getErrorCode())) {
				String citizenShip = fceDao.getCdPersonCitizenship(serviceAuthDetailDto.getIdPerson());
				// check each selected child's citizenship in PERSON_DTL for 68C
				// and 68E.
				// If child's citizenship is TMR, issue error message.
				if (StringUtils.isNotEmpty(citizenShip) && CodesConstant.CCTZNSTA_TMR.equalsIgnoreCase(citizenShip)) {
					errorDto.setErrorCode(ServiceConstants.MSG_SVCCODE_CITIZENSHIP_INVLD);
				}
			}
		}
		return errorDto;
	}

	/**
	 * 
	 * Method Name: checkWhetherServiceCodeExists Method Description:Service
	 * Authorization Detail Window when the save pushbutton is clicked. Service
	 * code exists in the Equivalency table for the given time period and open
	 * stages for the client. This DAM will also be used to check if the service
	 * code is exempt from the Equivalency table edit by querying the
	 * Non_Equivalency table. Finally, this service will be used to see if the
	 * service code exists in the Equivalency table for the given time period
	 * when a user adds new Contract Services to a Contract.
	 * 
	 * @param serviceAuthDetailDto
	 * @param errorDto
	 * @param serviceAuthDtlReq
	 * @return
	 */
	private String checkWhetherServiceCodeExists(ServiceAuthDetailDto serviceAuthDetailDto, ErrorDto errorDto,
			ServiceAuthDetailReq serviceAuthDtlReq) {
		EquivalentSvcDetailDto equivalentSvcDto = new EquivalentSvcDetailDto();
		String message = ServiceConstants.FND_SUCCESS;
		RowQtyDto rowQty = new RowQtyDto();
		String reqFunc = ServiceConstants.EMPTY_STRING;
		if (StringUtils.isNotEmpty(serviceAuthDetailDto.getAuthDtlSvc())
				&& (CodesConstant.CSVATYPE_INI.equalsIgnoreCase(serviceAuthDetailDto.getAuthDtlAuthType())
						|| CodesConstant.CSVATYPE_ONT.equalsIgnoreCase(serviceAuthDetailDto.getAuthDtlAuthType()))) {
			reqFunc = ServiceConstants.THREE;
			equivalentSvcDto.setIdEvent(serviceAuthDtlReq.getIdEvent());
			equivalentSvcDto.setIdPerson(serviceAuthDetailDto.getIdPerson());
			equivalentSvcDto.setCdEquivSvcDtlService(serviceAuthDetailDto.getAuthDtlSvc());
			equivalentSvcDto.setDtEquivStartDate(serviceAuthDetailDto.getDtSvcAuthDtlBegin());
			equivalentSvcDto.setDtEquivEndDate(serviceAuthDetailDto.getDtSvcAuthDtlEnd());
			equivalentSvcDto.setCdStage(serviceAuthDtlReq.getSzStage());
			equivalentSvcDto.setReqFuncCd(reqFunc);
			// CMSC52D
			rowQty = serviceAuthorizationDetailDao.getCountSVCCCodeExists(equivalentSvcDto, reqFunc);
			if (!ObjectUtils.isEmpty(rowQty) && !ObjectUtils.isEmpty(rowQty.getRowQty())
					&& ServiceConstants.ZERO < rowQty.getRowQty()) {
				// This means the Service exists on the Non_Equivalency
				// table and is therefore exempt from the new equivalency edits
				message = ServiceConstants.FND_SUCCESS;
			} else if (!ObjectUtils.isEmpty(serviceAuthDetailDto)
					&& !ObjectUtils.isEmpty(serviceAuthDetailDto.getDtSvcAuthDtlBegin())
					&& !ObjectUtils.isEmpty(serviceAuthDtlReq)
					&& !ObjectUtils.isEmpty(serviceAuthDtlReq.getDtStageStart())
					&& DateUtils.daysDifference(serviceAuthDetailDto.getDtSvcAuthDtlBegin(),
							serviceAuthDtlReq.getDtStageStart()) >= ServiceConstants.ZERO) {
				// This means the svc_auth begin date is on or after the stage
				// start date; we
				// will only need to call the Equivalency table once
				reqFunc = ServiceConstants.ONE;
				equivalentSvcDto.setReqFuncCd(reqFunc);
				rowQty = serviceAuthorizationDetailDao.getCountSVCCCodeExists(equivalentSvcDto, reqFunc);
				if (!ObjectUtils.isEmpty(rowQty) && !ObjectUtils.isEmpty(rowQty.getRowQty())
						&& ServiceConstants.ZERO < rowQty.getRowQty()) {
					message = ServiceConstants.FND_SUCCESS;
				} else {
					errorDto.setErrorCode(ServiceConstants.MSG_NO_EQUIV_MATCH);
					message = ServiceConstants.FND_FAILURE;
				}
			} else if (!ObjectUtils.isEmpty(serviceAuthDetailDto)
					&& !ObjectUtils.isEmpty(serviceAuthDetailDto.getDtSvcAuthDtlEnd())
					&& !ObjectUtils.isEmpty(serviceAuthDtlReq)
					&& !ObjectUtils.isEmpty(serviceAuthDtlReq.getDtStageStart())) {

				if (DateUtils.daysDifference(serviceAuthDetailDto.getDtSvcAuthDtlEnd(),
						serviceAuthDtlReq.getDtStageStart()) >= ServiceConstants.ZERO) {
					// Check to make sure the Svc Auth Ends after the Stage
					// Start Date
					reqFunc = ServiceConstants.ONE;
					equivalentSvcDto.setReqFuncCd(reqFunc);
					equivalentSvcDto.setDtEquivStartDate(serviceAuthDtlReq.getDtStageStart());
					rowQty = serviceAuthorizationDetailDao.getCountSVCCCodeExists(equivalentSvcDto, reqFunc);
					reqFunc = ServiceConstants.TWO;
				} else {
					rowQty.setRowQty(ServiceConstants.ONE_LONG);
					reqFunc = ServiceConstants.TWO;
				}
			}
			// check whether Rowqty is greater than zero and requiredFunction is
			// two
			if (StringUtils.isNotEmpty(message) && ServiceConstants.TWO.equals(reqFunc) && !ObjectUtils.isEmpty(rowQty)
					&& !ObjectUtils.isEmpty(rowQty.getRowQty())) {
				// The first call to the Equiv was successful - This means that
				// the time period
				// from the stage start to the svc auth
				// end was validated; now we need to validate from the svc auth
				// start to the
				// start start
				equivalentSvcDto.setDtEquivStartDate(serviceAuthDetailDto.getDtSvcAuthDtlBegin());
				reqFunc = ServiceConstants.TWO;
				equivalentSvcDto.setReqFuncCd(reqFunc);
				rowQty = serviceAuthorizationDetailDao.getCountSVCCCodeExists(equivalentSvcDto, reqFunc);
				if (!ObjectUtils.isEmpty(rowQty) && !ObjectUtils.isEmpty(rowQty.getRowQty())
						&& ServiceConstants.ZERO < rowQty.getRowQty()) {
					message = ServiceConstants.FND_SUCCESS;
				} else {
					errorDto.setErrorCode(ServiceConstants.MSG_NO_EQUIV_MATCH);
					message = ServiceConstants.FND_FAILURE;
				}
			}
		}
		return message;
	}

	/**
	 * 
	 * Method Name: callVerifyDublicate Method Description: To verify that a
	 * completed service_auth does not exist for a given stage for service code
	 * 69A (call Service CSESD9D).
	 * 
	 * @param serviceAuthDetailDto
	 * @param errorDto
	 * @param serviceAuthDtlReq
	 * @return
	 */
	private ErrorDto callVerifyDublicate(ServiceAuthDetailDto serviceAuthDetaildto, ErrorDto errorDto,
			ServiceAuthDetailReq serviceAuthDtlReq) {
		Long count = serviceAuthorizationDetailDao.callVerifyDuplicate(serviceAuthDetaildto, serviceAuthDtlReq);
		if (!ObjectUtils.isEmpty(count) && count > 0) {
			if (StringUtils.isNotEmpty(serviceAuthDetaildto.getAuthDtlSvc())
					&& ServiceConstants.REQ_FUNC_CD_ADD.equalsIgnoreCase(serviceAuthDetaildto.getScrDataAction())
					&& CodesConstant.CSVCCODE_69A.equalsIgnoreCase(serviceAuthDetaildto.getAuthDtlSvc())) {
				errorDto.setErrorCode(55460);
			} else {
				errorDto.setErrorCode(8179);
			}
		}
		return errorDto;
	}

	/**
	 * 
	 * Method Name: checkPrimaryChild Method Description: CallCSEC15D Get this
	 * stage's Primary Child 'PC'
	 * 
	 * @param idStage
	 * @return idPrimaryChild
	 */
	private ErrorDto checkPrimaryChild(Long idStage, ErrorDto errorDto) {
		Long idPrimaryChild = ServiceConstants.ZERO;
		// CallCSEC15D Get this stage's Primary Child 'PC'
		StagePersonLinkCaseDto stagePersonLinkCaseDto = commonAppDao.getStagePersonCaseDtl(idStage,
				CodesConstant.CROLES_PC);
		if (!ObjectUtils.isEmpty(stagePersonLinkCaseDto) && !ObjectUtils.isEmpty(stagePersonLinkCaseDto.getIdPerson())
				&& ServiceConstants.Zero < stagePersonLinkCaseDto.getIdPerson()) {
			idPrimaryChild = stagePersonLinkCaseDto.getIdPerson();
		}
		String citizenShip = fceDao.getCdPersonCitizenship(idPrimaryChild);
		if (StringUtils.isEmpty(citizenShip)) {
			errorDto.setErrorCode(ServiceConstants.MSG_NO_ROWS_RETURNED);
		} else if (!CodesConstant.CCTZNSTA_TMR.equalsIgnoreCase(citizenShip)) {
			errorDto.setErrorCode(ServiceConstants.MSG_SVCCODE_NONTANF_INVLD);
		}
		return errorDto;
	}

	/**
	 * 
	 * Method Name: retrieveServiceAuthDetail Method Description: his retrieval
	 * service will * either populate the Service combo box, Persons Listbox *
	 * and/or Svc Auth Dtl Listbox. If the Window Mode is * Inquire, then a
	 * single row for Svc Auth Dtl Listbox will * be retrieved; if the Window
	 * Mode is Modify, then a single * row for Svc Auth Dtl Listbox will be
	 * retrieved, and a list * of Services will also be retrieved; if the Window
	 * Mode is * New and no detail record exists, then a list of Services * will
	 * be retrieved, a list of Persons will be retrieved and * the Dt Situation
	 * Opened will be retrieved. However, if the * window mode is New and a
	 * detail record does exist, then * a single row for Svc Auth Dtl Listbox, a
	 * list os Service * and Dt Situation Opened will be retrieved.
	 * 
	 * @param serviceAuthDtlReq
	 *            - Contains idStage , idContract to retrieve the data
	 * @return ServiceAuthDetailRes - this gets data to display service
	 *         authorization detail
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public ServiceAuthDetailRes retrieveServiceAuthDetail(ServiceAuthDetailReq serviceAuthDtlReq) {
		ServiceAuthDetailRes serviceAuthDetailRes = new ServiceAuthDetailRes();
		ServiceAuthDetailDto serviceAuthDetailDto = new ServiceAuthDetailDto();
		ErrorDto errorDto = new ErrorDto();
		serviceAuthDetailRes.setErrorDto(errorDto);
		if (!ObjectUtils.isEmpty(serviceAuthDtlReq) && StringUtils.isNotEmpty(serviceAuthDtlReq.getReqFuncCd())) {
			if (ServiceConstants.WINDOW_MODE_MODIFY.equals(serviceAuthDtlReq.getReqFuncCd())
					|| ServiceConstants.WINDOW_MODE_INQUIRE.equals(serviceAuthDtlReq.getReqFuncCd())
					|| (ServiceConstants.WINDOW_MODE_NEW.equals(serviceAuthDtlReq.getReqFuncCd())
							&& !ObjectUtils.isEmpty(serviceAuthDtlReq.getIdSvcAuthDtl())
							&& ServiceConstants.ZERO < serviceAuthDtlReq.getIdSvcAuthDtl())) {
				// Call Service(Cses25D) To retrieve ServiceAuthDetail Data by
				// using
				// idSvcAuthDetail which is unique key for SVC_AUTH_DTL table
				serviceAuthDetailDto = serviceAuthorizationDetailDao
						.retrieveServiceAuthDetail(serviceAuthDtlReq.getIdSvcAuthDtl());
				// call PersonDao to get Person Full Name CINV81D
				PersonDto personDto = personDao.getPersonById(serviceAuthDetailDto.getIdPerson());
				serviceAuthDetailDto.setNmPersonFull(!StringUtils.isEmpty(personDto.getCdPersonSuffix())
						? personDto.getNmPersonFull() + " " + personDto.getCdPersonSuffix()
						: personDto.getNmPersonFull());
				if (ObjectUtils.isEmpty(serviceAuthDetailDto)
						|| ObjectUtils.isEmpty(serviceAuthDetailDto.getIdSvcAuthDtl())) {
					serviceAuthDetailRes.getErrorDto().setErrorCode(ServiceConstants.MSG_DETAIL_DELETED);
				}

			}
			if ((!ObjectUtils.isEmpty(serviceAuthDetailRes.getErrorDto().getErrorCode())
					|| ServiceConstants.ZERO == serviceAuthDetailRes.getErrorDto().getErrorCode())
					&& (ServiceConstants.WINDOW_MODE_MODIFY.equals(serviceAuthDtlReq.getReqFuncCd())
							|| ServiceConstants.WINDOW_MODE_NEW.equals(serviceAuthDtlReq.getReqFuncCd()))) {
				// retrieve details from Contract_Service, Contract_County
				// table, SITUATION,
				// Stage, Stage_Person_Link and Person (CLSC19D, CSES26D,
				// Clsc18d)
				serviceAuthDetailRes = serviceAuthorizationDetailDao.rtrvCntrctSvcAndCntyList(serviceAuthDtlReq);
			}
			// Kinship
			if ((ObjectUtils.isEmpty(serviceAuthDetailRes.getErrorDto().getErrorCode())
					|| serviceAuthDetailRes.getErrorDto().getErrorCode() == 0)
					&& StringUtils.isNotEmpty(serviceAuthDtlReq.getSvcAuthDtlSvc())) {
				if ((ServiceConstants.KINSHIP_LIST.contains(serviceAuthDtlReq.getSvcAuthDtlSvc())
						|| ServiceConstants.FLEXIBLE_BENEFIT_LIST.contains(serviceAuthDtlReq.getSvcAuthDtlSvc()))
						&& ServiceConstants.WINDOW_MODE_NEW.equals(serviceAuthDtlReq.getReqFuncCd())
						&& (ObjectUtils.isEmpty(serviceAuthDtlReq.getIdSvcAuthDtl())
								|| ServiceConstants.ZERO == serviceAuthDtlReq.getIdSvcAuthDtl())) {
					processKinship(serviceAuthDtlReq, serviceAuthDetailRes);
				}
				if (ServiceConstants.KINSHIP_LIST.contains(serviceAuthDtlReq.getSvcAuthDtlSvc())
						&& (ServiceConstants.WINDOW_MODE_MODIFY.equals(serviceAuthDtlReq.getReqFuncCd())
								|| ServiceConstants.WINDOW_MODE_INQUIRE.equals(serviceAuthDtlReq.getReqFuncCd())
								|| (ServiceConstants.WINDOW_MODE_NEW.equals(serviceAuthDtlReq.getReqFuncCd())
										&& !ObjectUtils.isEmpty(serviceAuthDtlReq.getIdSvcAuthDtl())
										&& ServiceConstants.ZERO < serviceAuthDtlReq.getIdSvcAuthDtl()))) {
					callRedisplayKinship(serviceAuthDtlReq, serviceAuthDetailRes);
				}
			}
		}
		serviceAuthDetailRes.setServiceAuthDetailDto(serviceAuthDetailDto);
		// DayCare
		return serviceAuthDetailRes;
	}

	/**
	 * Method Name: callRedisplayKinship Method Description: This function finds
	 * existing kinship Records.CLSS92D DAM Added Unit rate and service code to
	 * the list.
	 * 
	 * @param serviceAuthDtlReq
	 * @param serviceAuthDetailRes
	 */
	private void callRedisplayKinship(ServiceAuthDetailReq serviceAuthDtlReq,
			ServiceAuthDetailRes serviceAuthDetailRes) {
		List<ServiceAuthPersonDto> serviceAuthPrsnList = new ArrayList<ServiceAuthPersonDto>();
		List<PersonDto> personDtoList = serviceAuthorizationDetailDao
				.retrieveKinshipForExisting(serviceAuthDtlReq.getIdSvcAuthDtl());
		if (!ObjectUtils.isEmpty(personDtoList)) {
			personDtoList.parallelStream().forEach(person -> {
				ServiceAuthPersonDto servAuthPrsn = new ServiceAuthPersonDto();
				servAuthPrsn.setNmPersonFull(!StringUtils.isEmpty(person.getCdPersonSuffix())
						? person.getNmPersonFull() + " " + person.getCdPersonSuffix() : person.getNmPersonFull());
				servAuthPrsn.setIdPerson(person.getIdPerson());
				servAuthPrsn.setCdInvoPhase(ServiceConstants.EMPTY_STR);
				servAuthPrsn.setStagePersRelInt(ServiceConstants.EMPTY_STR);
				servAuthPrsn.setStagePersRole(ServiceConstants.EMPTY_STR);
				if (!ObjectUtils.isEmpty(serviceAuthDetailRes.getServiceAuthDetailDto())
						&& !ObjectUtils.isEmpty(serviceAuthDetailRes.getServiceAuthDetailDto().getAuthDtlUnitRate())) {
					servAuthPrsn
							.setAuthDtlUnitRate(serviceAuthDetailRes.getServiceAuthDetailDto().getAuthDtlUnitRate());
				}
				if (!ObjectUtils.isEmpty(serviceAuthDetailRes.getServiceAuthDetailDto())
						&& StringUtils.isNotEmpty(serviceAuthDetailRes.getServiceAuthDetailDto().getAuthDtlSvc())) {
					servAuthPrsn.setCdSvcAuthDtlSvc(serviceAuthDetailRes.getServiceAuthDetailDto().getAuthDtlSvc());
				}
				serviceAuthPrsnList.add(servAuthPrsn);
			});
			serviceAuthDetailRes.setServiceAuthPersonList(serviceAuthPrsnList);
		} else {
			serviceAuthDetailRes.getErrorDto().setErrorCode(ServiceConstants.MSG_NO_KIN_GRP);
		}
	}

	/**
	 * Method Name: processKinship Method Description:
	 * 
	 * @param serviceAuthDtlReq
	 * @param serviceAuthDetailRes
	 */
	private void processKinship(ServiceAuthDetailReq serviceAuthDtlReq, ServiceAuthDetailRes serviceAuthDetailRes) {
		List<ServiceAuthPersonDto> serviceAuthPersonList = new ArrayList<ServiceAuthPersonDto>();
		// Retrieve stage person from stagePersonLink(Clscd2d)
		List<StagePrincipalDto> stagePrincipalDtoList = stagePersonLinkDao
				.getStagePrincipalByIdStageType(serviceAuthDtlReq.getIdStage(), serviceAuthDtlReq.getStagePersType());
		if (!ObjectUtils.isEmpty(stagePrincipalDtoList)) {
			for (StagePrincipalDto stagePrincipal : stagePrincipalDtoList) {
				if (!ObjectUtils.isEmpty(serviceAuthDetailRes.getErrorDto())
						&& (ObjectUtils.isEmpty(serviceAuthDetailRes.getErrorDto().getErrorCode())
								|| serviceAuthDetailRes.getErrorDto().getErrorCode() == 0)
						&& !ObjectUtils.isEmpty(stagePrincipal.getDtPersonBirth())) {
					Integer age = DateUtils.calculatePersonsAgeInYears(stagePrincipal.getDtPersonBirth(),
							serviceAuthDtlReq.getDtSvcAuthEff());
					if (!ObjectUtils.isEmpty(age) && age < ServiceConstants.EIGHTEEN) {
						String cdPlcmntLvgArr = getPlcmtLivArrCode(serviceAuthDtlReq.getSvcAuthDtlSvc());
						List<PlacementDto> placementDtoList = serviceAuthorizationDetailDao.getPlacementHistory(
								stagePrincipal.getIdPerson(), serviceAuthDtlReq.getIdPrimaryClient(),
								serviceAuthDtlReq.getIdResource(), serviceAuthDtlReq.getSvcAuthDtlSvc(),
								cdPlcmntLvgArr);

						ServiceAuthPersonDto serviceAuthPerson = checkAndProcessKinshipOrFlexBenf(stagePrincipal,
								placementDtoList, serviceAuthDtlReq, serviceAuthDetailRes, cdPlcmntLvgArr);
						if (!ObjectUtils.isEmpty(serviceAuthPerson)) {
							serviceAuthPersonList.add(serviceAuthPerson);
						}
					}
				}
			}
			if (ObjectUtils.isEmpty(serviceAuthPersonList)) {
				if(ObjectUtils.isEmpty(serviceAuthDetailRes.getErrorDto().getErrorCode())){
					serviceAuthDetailRes.getErrorDto().setErrorCode(ServiceConstants.MSG_SVC_AUTH_NO_CHLDREN);
				}
			} else {
				serviceAuthDetailRes.setServiceAuthPersonList(serviceAuthPersonList);
			}
		} else {
			serviceAuthDetailRes.getErrorDto().setErrorCode(ServiceConstants.MSG_SVC_AUTH_NO_CHLDREN);
		}
	}

	/**
	 * Method Name: checkAndProcessKinshipOrFlexBenf Method Description: This
	 * method Check whether service Code is Kinship/FlexBenefit and retrieve
	 * kinship children / Flexible Benefit children.
	 * 
	 * @param stagePrincipal
	 * @param placementDtoList
	 * @param serviceAuthDetailRes
	 * @param cdPlcmntLvgArr
	 * @param String
	 */
	private ServiceAuthPersonDto checkAndProcessKinshipOrFlexBenf(StagePrincipalDto stagePrincipal,
			List<PlacementDto> placementDtoList, ServiceAuthDetailReq serviceAuthDtlReq,
			ServiceAuthDetailRes serviceAuthDetailRes, String cdPlcmntLvgArr) {
		Boolean isBenefitProcessed = Boolean.FALSE;
		ServiceAuthPersonDto servicePersonDto = null;
		if (!ObjectUtils.isEmpty(placementDtoList)) {
			for (PlacementDto placement : placementDtoList) {
				if (!ObjectUtils.isEmpty(placement.getDtPlcmtStart())
						&& !ObjectUtils.isEmpty(placement.getDtPlcmtEnd())) {
					if ((serviceAuthDtlReq.getDtSvcAuthEff().equals(placement.getDtPlcmtStart())
							|| serviceAuthDtlReq.getDtSvcAuthEff().after(placement.getDtPlcmtStart()))
							&& serviceAuthDtlReq.getDtSvcAuthEff().before(placement.getDtPlcmtEnd())) {
						serviceAuthDtlReq.setPlacementProcesssed(ServiceConstants.TRUE_VALUE);
					}
				}
			}
		}
		// This function calls three functions in other to retrieve * kinship
		// children
		if (ServiceConstants.KINSHIP_LIST.contains(serviceAuthDtlReq.getSvcAuthDtlSvc())) {
			if (serviceAuthDtlReq.isPlacementProcesssed()) {
				servicePersonDto = new ServiceAuthPersonDto();
				servicePersonDto.setStagePersRole(stagePrincipal.getCdStagePersRole());
				servicePersonDto.setStagePersRelInt(stagePrincipal.getCdStagePersRelInt());
				servicePersonDto.setIdPerson(stagePrincipal.getIdPerson());
				servicePersonDto.setNmPersonFull(!StringUtils.isEmpty(stagePrincipal.getCdPersonSuffix())
						? stagePrincipal.getNmPersonFull() + " " + stagePrincipal.getCdPersonSuffix()
						: stagePrincipal.getNmPersonFull());
				servicePersonDto.setDtPersonBirth(stagePrincipal.getDtPersonBirth());
				// this will retrieve Kinship Detail
				KinshipDto kinshipDto = serviceAuthorizationDetailDao
						.retrieveKinshipDetail(stagePrincipal.getIdPerson());

				if (!ObjectUtils.isEmpty(kinshipDto)) {
					if (!ObjectUtils.isEmpty(kinshipDto.getDtKinshipEnd())) {
						servicePersonDto.setCdInvoPhase(ServiceConstants.EMPTY_STR);
					} else {
						// this will retrieve List Of Invoice Phase
						List<String> cdInvoPhaseList = serviceAuthorizationDetailDao
								.retrieveInvoiceDetail(kinshipDto.getIdSvcAuthDtl());
						if (!ObjectUtils.isEmpty(cdInvoPhaseList)) {
							for (String cdInvoPhase : cdInvoPhaseList) {
								servicePersonDto.setCdInvoPhase(cdInvoPhase);
								if (cdInvoPhase.equals(CodesConstant.CINVPHSE_PAD)
										|| cdInvoPhase.equals(CodesConstant.CINVPHSE_SBT)
										|| cdInvoPhase.equals(CodesConstant.CINVPHSE_VWO)) {
									break;
								}
							}
						} else {
							servicePersonDto.setCdInvoPhase(ServiceConstants.INVOICE_PHASE_INV);
						}
					}
				}
			}
			isBenefitProcessed = Boolean.TRUE;
		}
		// This function calls a function in other to retrieve * Flexible
		// Benefit
		// children.
		else if (ServiceConstants.FLEXIBLE_BENEFIT_LIST.contains(serviceAuthDtlReq.getSvcAuthDtlSvc())) {
			if (serviceAuthDtlReq.isPlacementProcesssed()) {
				servicePersonDto = new ServiceAuthPersonDto();
				servicePersonDto.setStagePersRole(stagePrincipal.getCdStagePersRole());
				servicePersonDto.setStagePersRelInt(stagePrincipal.getCdStagePersRelInt());
				servicePersonDto.setIdPerson(stagePrincipal.getIdPerson());
				servicePersonDto.setNmPersonFull(!StringUtils.isEmpty(stagePrincipal.getCdPersonSuffix())
						? stagePrincipal.getNmPersonFull() + " " + stagePrincipal.getCdPersonSuffix()
						: stagePrincipal.getNmPersonFull());
				servicePersonDto.setDtPersonBirth(stagePrincipal.getDtPersonBirth());
				isBenefitProcessed = Boolean.TRUE;

			} else {
				PlacementDto placementDto = serviceAuthorizationDetailDao.getMaxPlacementEndDtRecord(
						stagePrincipal.getIdPerson(), serviceAuthDtlReq.getIdResource(),
						serviceAuthDtlReq.getSvcAuthDtlSvc(), cdPlcmntLvgArr);
				if (!ObjectUtils.isEmpty(placementDto.getIdPlcmtChild())
						&& !ObjectUtils.isEmpty(placementDto.getDtPlcmtEnd())
						&& !ObjectUtils.isEmpty(placementDto.getCdPlcmtRemovalRsn())) {
					Double dayDiffEndEff = DateUtils.daysDifference(serviceAuthDtlReq.getDtSvcAuthEff(),
							placementDto.getDtPlcmtEnd());
					if ((dayDiffEndEff.equals(ServiceConstants.DoubleZero)
							|| dayDiffEndEff > ServiceConstants.DoubleZero)
							&& CodesConstant.CPLREMRO_220.equalsIgnoreCase(placementDto.getCdPlcmtRemovalRsn())) {
						servicePersonDto = new ServiceAuthPersonDto();
						servicePersonDto.setStagePersRole(stagePrincipal.getCdStagePersRole());
						servicePersonDto.setStagePersRelInt(stagePrincipal.getCdStagePersRelInt());
						servicePersonDto.setIdPerson(stagePrincipal.getIdPerson());
						servicePersonDto.setNmPersonFull(!StringUtils.isEmpty(stagePrincipal.getCdPersonSuffix())
								? stagePrincipal.getNmPersonFull() + " " + stagePrincipal.getCdPersonSuffix()
								: stagePrincipal.getNmPersonFull());
						servicePersonDto.setDtPersonBirth(stagePrincipal.getDtPersonBirth());
						isBenefitProcessed = Boolean.TRUE;
					}
				}
			}
		}
		if (!isBenefitProcessed) {
			serviceAuthDetailRes.getErrorDto().setErrorCode(ServiceConstants.MSG_SVC_FLEX_NO_CHILDREN);
		}
		return servicePersonDto;

	}

	/**
	 * 
	 * Method Name: getPlcmtLivArrCode Method Description: get Placement Living
	 * Arrangement from service code
	 * 
	 * @param serviceCode
	 *            - Service
	 * @return String - cdPlcmntLvgArr
	 */
	private String getPlcmtLivArrCode(String serviceCode) {
		String cdPlcmntLvgArr = ServiceConstants.EMPTY_STRING;
		if (serviceCode.equals(CodesConstant.CSVCCODE_68B) || serviceCode.equals(CodesConstant.CSVCCODE_68F)
				|| serviceCode.equals(ServiceConstants.CSVCCODE_68J)
				|| serviceCode.equals(ServiceConstants.CSVCCODE_68L)
				|| serviceCode.equals(ServiceConstants.CSVCCODE_68N) || serviceCode.equals(CodesConstant.CSVCCODE_68C)
				|| serviceCode.equals(CodesConstant.CSVCCODE_68G)) {
			cdPlcmntLvgArr = CodesConstant.CLAKNL_DD;
		} else if (serviceCode.equals(CodesConstant.CSVCCODE_68D) || serviceCode.equals(CodesConstant.CSVCCODE_68H)
				|| serviceCode.equals(ServiceConstants.CSVCCODE_68K)
				|| serviceCode.equals(ServiceConstants.CSVCCODE_68M) || serviceCode.equals(CodesConstant.CSVCCODE_68E)
				|| serviceCode.equals(CodesConstant.CSVCCODE_68I)) {
			cdPlcmntLvgArr = CodesConstant.CLAKNL_DF;
		}
		return cdPlcmntLvgArr;

	}

	/**
	 * This method is used to retrieve the day care information and person
	 * details who are part of day care
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public DayCareRequestDto retrieveDayCareReqAndPersonDetail(Long idEvent) {
		// retrieve day care using event id
		DayCareRequestDto dayCareRequestDto = null;
		EventIdDto eventIdDto = dayCareRequestService.retrieveDayCareRequestSvcAuth(idEvent);
		// if day care exists for service authorization retrieve day care
		// request detail with day care event id.
		if (!ObjectUtils.isEmpty(eventIdDto) && !ObjectUtils.isEmpty(eventIdDto.getIdEvent())) {
			dayCareRequestDto = dayCareRequestService.retrieveDayCareRequestDetail(eventIdDto.getIdEvent());
			if (!ObjectUtils.isEmpty(dayCareRequestDto.getIdDayCareRequest())) {
				List<DayCarePersonDto> dayCarePersonDtoList = dayCareRequestService
						.retrieveDayCarePersonLink(dayCareRequestDto.getIdDayCareRequest());
				dayCareRequestDto.setDayCarePersonDtoList(dayCarePersonDtoList);
				dayCareRequestDto.setIdEvent(eventIdDto.getIdEvent());
			}
		}
		return dayCareRequestDto;
	}

	/**
	 * Method Name: dayCarePersonList Method Description: This method is to
	 * retrieve the person list for day care.
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public ServiceAuthDetailRes dayCarePersonList(DayCareRequestDto dayCareRequestDto, Long idEvent) {
		ServiceAuthDetailRes serviceAuthDetailRes = new ServiceAuthDetailRes();
		if (!ObjectUtils.isEmpty(dayCareRequestDto) && !ObjectUtils.isEmpty(dayCareRequestDto.getIdEvent())) {
			serviceAuthDetailRes.setDayCarePersonList(dayCareRequestService.retrieveDayCareRequestSvcAuthPersonDtl(
					dayCareRequestDto.getIdEvent(), dayCareRequestDto.getIdStage()));
			serviceAuthDetailRes.setServiceAuthPrs(dayCareRequestDao.retrieveSvcAuthPerson(idEvent));
		}
		return serviceAuthDetailRes;
	}

	/**
	 * Method Name: dayCarePersonListForSvcAuthDtlId Method Description: This
	 * method is used to retrive the person information based on event id and
	 * stage id and see if the service is terminated.
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public ServiceAuthDetailRes dayCarePersonListForSvcAuthDtlId(DayCareRequestDto dayCareRequestDto, Long idEvent,
			Long idSvcAuthDtl) {
		ServiceAuthDetailRes serviceAuthDetailRes = new ServiceAuthDetailRes();
		if (!ObjectUtils.isEmpty(dayCareRequestDto) && !ObjectUtils.isEmpty(dayCareRequestDto.getIdEvent())) {
			serviceAuthDetailRes.setDayCarePersonList(dayCareRequestService.retrieveDayCareRequestSvcAuthPersonDtl(
					dayCareRequestDto.getIdEvent(), dayCareRequestDto.getIdStage()));
		}
		serviceAuthDetailRes.setIsTermDayCareSA(serviceAuthorizationDetailDao.getTermDayCareSvcAuth(idSvcAuthDtl));
		return serviceAuthDetailRes;
	}

	/**
	 * MethodName:updateSSCCListDC MethodDescrition: This Method Updates
	 * SSCC_LIST table with IND_SSCC_DAYCARE = 'Y', DT_SSCC_DAYCARE = system
	 * date for Day Care Requests.
	 * 
	 * EJB Name : ServiceAuthBean.java
	 * 
	 * @param idSSCCReferral
	 * @return long
	 * 
	 */
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
			Exception.class })
	public long updateSSCCListDC(long idSSCCReferral) {
		serviceAuthorizationDetailDao.updateSSCCListDCDao(idSSCCReferral);
		return idSSCCReferral;
	}

	/**
	 * This method is used to validate if the detail entered is having correct
	 * legal status and living arrangement based on case id, person id and
	 * resource id
	 * 
	 * @param idCase
	 * @param idPerson
	 * @param idResource
	 * @return
	 */
	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public ServiceAuthDetailRes validateLegalStatusAndLivingArr(Long idCase, Long idPerson, Long idResource) {
		ServiceAuthDetailRes serviceAuthDetailRes = new ServiceAuthDetailRes();
		List<ErrorDto> errorDtoList = new ArrayList<>();
		String legalStatus = "";
		LegalStatusDto legalStatusDto = reviewUtils.findLegalStatusForChild(idCase, idPerson);
		if (!ObjectUtils.isEmpty(legalStatusDto) && !ObjectUtils.isEmpty(legalStatusDto.getCdLegalStatStatus())) {
			legalStatus = legalStatusDto.getCdLegalStatStatus();
			if (!CodesConstant.CLEGSTAT_090.equals(legalStatus)) {
				ErrorDto errorDto = new ErrorDto();
				errorDto.setErrorMsg(lookupDao.getMessage(8198));
				errorDto.setErrorCode(8198);
				errorDtoList.add(errorDto);
			}
		} else {
			ErrorDto errorDto = new ErrorDto();
			errorDto.setErrorMsg(lookupDao.getMessage(8198));
			errorDto.setErrorCode(8198);
			errorDtoList.add(errorDto);
		}
		String livingArrangement = adoptionAsstncService.getPlacementWithGreatestStartDate(idPerson, idResource);
		if (!(CodesConstant.CPLCMT_71.equals(livingArrangement)
				|| CodesConstant.CLAPRSFA_GT.equals(livingArrangement))) {
			ErrorDto errorDto = new ErrorDto();
			errorDto.setErrorMsg(lookupDao.getMessage(8269));
			errorDto.setErrorCode(8269);
			errorDtoList.add(errorDto);
		}
		serviceAuthDetailRes.setErrorDtoList(errorDtoList);
		return serviceAuthDetailRes;
	}

	/**
	 * Method Name: validateChildInformation Method Description: This method is
	 * used to validate the child information for non-TANF KINSHIP CODEs
	 * 
	 * @param serviceAuthorizationDetailReq
	 * @return
	 */
	@Override
	public boolean validateChildInformation(ServiceAuthorizationDetailReq serviceAuthorizationDetailReq) {
		boolean isValid = true;
		for (ServiceAuthPersonDto p : serviceAuthorizationDetailReq.getPersonList()) {
			String cdPlcmntLvgArr = getPlcmtLivArrCode(serviceAuthorizationDetailReq.getCdSvcAuthService());
			List<PlacementDto> placementDtoList = serviceAuthorizationDetailDao.getPlacementHistory(p.getIdPerson(),
					serviceAuthorizationDetailReq.getIdPrimaryClient(), serviceAuthorizationDetailReq.getIdResource(),
					serviceAuthorizationDetailReq.getCdSvcAuthService(), cdPlcmntLvgArr);
			if (!ObjectUtils.isEmpty(placementDtoList) && !placementDtoList.isEmpty() && placementDtoList.size() > 0) {
				StageDto stageDto = stageDao.getStageById(serviceAuthorizationDetailReq.getIdStage());
				Long idCase = serviceAuthorizationDetailReq.getIdCase();
				Long idStage = serviceAuthorizationDetailReq.getIdStage();
				String legalActionSubTypeCode = null;
				List<String> legalActionsSubType = Collections.unmodifiableList(
						Arrays.asList(ServiceConstants.PMC_TO_RELATIVE, ServiceConstants.PMC_TO_RELATIVE_KINCAREGIVER,
								ServiceConstants.PMC_TO_OTHER, ServiceConstants.PMC_TO_OTHER_KINCAREGIVER));
				if (stagePersonLinkDao.isChildPrimary(p.getIdPerson(), serviceAuthorizationDetailReq.getIdStage(),
						serviceAuthorizationDetailReq.getIdCase())) {
					LegalActionEventInDto legalActionEventInDto = new LegalActionEventInDto();
					legalActionEventInDto.setIdCase(idCase);
					legalActionEventInDto.setIdPerson(p.getIdPerson());
					legalActionEventInDto.setIdStage(idStage);
					legalActionEventInDto.setLegalActionSubTypes(legalActionsSubType);
					legalActionEventInDto.setLegalActionType(ServiceConstants.CCOR);
					CommonStringRes commonStringRes = legalStatusDao.selectLatestLegalActionSubType(legalActionEventInDto);
					if(!ObjectUtils.isEmpty(commonStringRes)){
						legalActionSubTypeCode = commonStringRes.getCommonRes();
					}
				} else {
					StagePersonLinkDto stagePersonLinkDto = stagePersonLinkDao.getChildPrimaryInfo(p.getIdPerson(),
							serviceAuthorizationDetailReq.getIdCase());
					idCase = stagePersonLinkDto.getIdCase();
					idStage = stagePersonLinkDto.getIdStage();
					LegalActionEventInDto legalActionEventInDto = new LegalActionEventInDto();
					legalActionEventInDto.setIdCase(idCase);
					legalActionEventInDto.setIdPerson(p.getIdPerson());
					legalActionEventInDto.setIdStage(idStage);
					legalActionEventInDto.setLegalActionSubTypes(legalActionsSubType);
					legalActionEventInDto.setLegalActionType(ServiceConstants.CCOR);
					CommonStringRes commonStringRes = legalStatusDao.selectLatestLegalActionSubType(legalActionEventInDto);
					if(!ObjectUtils.isEmpty(commonStringRes)){
						legalActionSubTypeCode = commonStringRes.getCommonRes();
					}
				}
				Date dtEffective = serviceAuthorizationDetailReq.getDtEffective();
				boolean isFound = placementDtoList.stream()
						.anyMatch(o -> ((dtEffective.compareTo(o.getDtPlcmtStart()) == 0
								|| dtEffective.compareTo(o.getDtPlcmtStart()) > 0)
								&& dtEffective.compareTo(o.getDtPlcmtEnd()) < 0));
				if (isFound) {
					isValid = validateServiceAuth(serviceAuthorizationDetailReq.getCdSvcAuthService(),
							legalActionSubTypeCode, dtEffective, stageDto.getIndStageClose(), placementDtoList);
					if (!isValid)
						break;
				} else {
					PlacementDto placementDto = serviceAuthorizationDetailDao.getMaxPlacementEndDtRecord(
							p.getIdPerson(), serviceAuthorizationDetailReq.getIdResource(),
							serviceAuthorizationDetailReq.getCdSvcAuthService(), cdPlcmntLvgArr);
					if (!ObjectUtils.isEmpty(placementDto) && !ObjectUtils.isEmpty(placementDto.getDtPlcmtEnd())
							&& (dtEffective.compareTo(placementDto.getDtPlcmtEnd()) == 0
									|| dtEffective.compareTo(placementDto.getDtPlcmtEnd()) > 0)
							&& !ObjectUtils.isEmpty(placementDto.getCdPlcmtRemovalRsn())
							&& "220".equals(placementDto.getCdPlcmtRemovalRsn())) {
						isValid = validateServiceAuthWithReason(legalActionSubTypeCode, placementDto);
						if (!isValid)
							break;
					}
				}
			}
		}
		return isValid;
	}

	private boolean validateServiceAuth(String cdSvcAuthService, String legalActionSubTypeCode, Date dtEffective,
			String subStageClosed, List<PlacementDto> placementDtoList) {
		if ("68C".equals(cdSvcAuthService) || "68G".equals(cdSvcAuthService)) {
			if (ObjectUtils.isEmpty(legalActionSubTypeCode)
					|| (!ServiceConstants.PMC_TO_OTHER.equals(legalActionSubTypeCode)
							&& !ServiceConstants.PMC_TO_OTHER_KINCAREGIVER.equals(legalActionSubTypeCode)
							&& !ServiceConstants.PMC_TO_RELATIVE.equals(legalActionSubTypeCode))) {
				return true;
			} else if (ServiceConstants.PMC_TO_RELATIVE.equals(legalActionSubTypeCode)) {
				Date pmcDate = null; // to - do
				Date dtPlacemntStart = placementDtoList.stream()
						.filter(o -> ((dtEffective.compareTo(o.getDtPlcmtStart()) == 0
								|| dtEffective.compareTo(o.getDtPlcmtStart()) > 0)
								&& dtEffective.compareTo(o.getDtPlcmtEnd()) < 0))
						.findFirst().get().getDtPlcmtStart();
				if (!ObjectUtils.isEmpty(dtPlacemntStart) && !ObjectUtils.isEmpty(pmcDate)) {
					// to -do
					return true;
				}
			} else if (ServiceConstants.PMC_TO_OTHER.equals(legalActionSubTypeCode)
					|| ServiceConstants.PMC_TO_OTHER_KINCAREGIVER.equals(legalActionSubTypeCode)) {
				ErrorDto errorDto = new ErrorDto();
				errorDto.setErrorMsg(
						"One or more children have a mis-match between the living arrangement and the PMC legal action subtype.");
			}
		} else if ("68E".equals(cdSvcAuthService) || "68I".equals(cdSvcAuthService)) {

			if (ObjectUtils.isEmpty(legalActionSubTypeCode)
					|| (!ServiceConstants.PMC_TO_OTHER.equals(legalActionSubTypeCode)
							&& !ServiceConstants.PMC_TO_OTHER_KINCAREGIVER.equals(legalActionSubTypeCode)
							&& !ServiceConstants.PMC_TO_RELATIVE.equals(legalActionSubTypeCode))) {
				return true;
			} else if (ServiceConstants.PMC_TO_OTHER.equals(legalActionSubTypeCode)
					&& ServiceConstants.Y.equals(subStageClosed)) {
				Date pmcDate = null; // to - do
				Date dtPlacemntStart = placementDtoList.stream()
						.filter(o -> ((dtEffective.compareTo(o.getDtPlcmtStart()) == 0
								|| dtEffective.compareTo(o.getDtPlcmtStart()) > 0)
								&& dtEffective.compareTo(o.getDtPlcmtEnd()) < 0))
						.findFirst().get().getDtPlcmtStart();
				if (!ObjectUtils.isEmpty(dtPlacemntStart) && !ObjectUtils.isEmpty(pmcDate)) {
					// to -do
					return true;
				}
			} else if (ServiceConstants.PMC_TO_OTHER.equals(legalActionSubTypeCode)
					|| ServiceConstants.PMC_TO_OTHER_KINCAREGIVER.equals(legalActionSubTypeCode)) {
				ErrorDto errorDto = new ErrorDto();
				errorDto.setErrorMsg(
						"One or more children have a mis-match between the living arrangement and the PMC legal action subtype.");
			}
		}
		return false;
	}

	private boolean validateServiceAuthWithReason(String legalActionSubTypeCode, PlacementDto placementDto) {
		if (!ServiceConstants.PMC_TO_OTHER.equals(legalActionSubTypeCode)
				&& !ServiceConstants.PMC_TO_OTHER_KINCAREGIVER.equals(legalActionSubTypeCode)
				&& !ServiceConstants.PMC_TO_RELATIVE.equals(legalActionSubTypeCode)) {
			ErrorDto errorDto = new ErrorDto();
			errorDto.setErrorCode(55351);
		} else if (ServiceConstants.PMC_TO_RELATIVE.equals(legalActionSubTypeCode)) {
			Date pmcDate = null; // to - do
			Date dtPlacemntStart = placementDto.getDtPlcmtStart();
			if (!ObjectUtils.isEmpty(dtPlacemntStart) && !ObjectUtils.isEmpty(pmcDate)) {
				// to -do
				return true;
			}
		} else if (ServiceConstants.PMC_TO_OTHER.equals(legalActionSubTypeCode)
				|| ServiceConstants.PMC_TO_OTHER_KINCAREGIVER.equals(legalActionSubTypeCode)) {
			ErrorDto errorDto = new ErrorDto();
			errorDto.setErrorMsg(
					"One or more children have a mis-match between the living arrangement and the PMC legal action subtype.");
		}
		return false;
	}

	/**
	 * 
	 * Method Name: getLegalEpisodePaymentDate Method Description: Retrieve the
	 * Kinship child ID_SVC_AUTH_DTL from Kinship table
	 * 
	 * @param idPersonInput
	 * @return
	 */
	@Override
	public ServiceAuthDetailRes getLegalEpisodePaymentDate(Long idPersonInput, Date dtEffective, Long idResource, String cdSvcAuthService) {
		ServiceAuthDetailRes serviceAuthDetailRes = new ServiceAuthDetailRes();
		boolean paymentExists = false;
		ServiceAuthDetailDto serviceAuthDetailDto = new ServiceAuthDetailDto();
		ServiceAuthDetailDto serviceAuthDetailDtoKin = serviceAuthorizationDetailDao.selectPaymentDate(idPersonInput);
		ServiceAuthDetailDto serviceAuthDetailDtoEpisode = null;
		if (!ObjectUtils.isEmpty(serviceAuthDetailDtoKin)) {
			serviceAuthDetailDtoEpisode = serviceAuthorizationDetailDao.getLegalEpisodeOfCare(idPersonInput,
					dtEffective);
		}
		if (!ObjectUtils.isEmpty(serviceAuthDetailDtoEpisode)) {
			if (!ObjectUtils.isEmpty(serviceAuthDetailDtoKin.getIdSvcAuthDtl())) {
				serviceAuthDetailDto.setIdSvcAuthDtl(serviceAuthDetailDtoKin.getIdSvcAuthDtl());
			}
			if (!ObjectUtils.isEmpty(serviceAuthDetailDtoKin.getDtSvcAuthDtlBegin())) {
				serviceAuthDetailDto.setDtSvcAuthDtlBegin(serviceAuthDetailDtoKin.getDtSvcAuthDtlBegin());
			}
			if (!ObjectUtils.isEmpty(serviceAuthDetailDtoEpisode.getDtLegalEpisodeEnterls())) {
				serviceAuthDetailDto.setDtLegalEpisodeEnterls(serviceAuthDetailDtoEpisode.getDtLegalEpisodeEnterls());
			}
			if (!ObjectUtils.isEmpty(serviceAuthDetailDtoEpisode.getDtLegalEpisodeTermls())) {
				serviceAuthDetailDto.setDtLegalEpisodeTermls(serviceAuthDetailDtoEpisode.getDtLegalEpisodeTermls());
				paymentExists = serviceAuthorizationDetailDao.isLegalEpisodePaymentExists(serviceAuthDetailDto);
			}
		}
		
		if(CodesConstant.CSVCCODE_68J.equals(cdSvcAuthService) || CodesConstant.CSVCCODE_68K.equals(cdSvcAuthService) 
				|| CodesConstant.CSVCCODE_68L.equals(cdSvcAuthService) || CodesConstant.CSVCCODE_68M.equals(cdSvcAuthService)){
			List<ServiceAuthDetailDto> siblingGroup = serviceAuthorizationDetailDao.getExistingSiblingGrpForResouce(idResource);
			if(!ObjectUtils.isEmpty(siblingGroup)){
				serviceAuthDetailRes.setSiblinggroup(siblingGroup);
			}
		}
		serviceAuthDetailRes.setPaymentExists(paymentExists);
		return serviceAuthDetailRes;
	}
}
