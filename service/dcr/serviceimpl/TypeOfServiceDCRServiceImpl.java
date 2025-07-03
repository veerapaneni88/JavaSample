package us.tx.state.dfps.service.dcr.serviceimpl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import us.tx.state.dfps.common.dto.ErrorDto;
import us.tx.state.dfps.service.admin.dto.FetchEventDto;
import us.tx.state.dfps.service.admin.dto.FetchEventRowDto;
import us.tx.state.dfps.service.admin.dto.TodoCreateInDto;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.EventDao;
import us.tx.state.dfps.service.common.dao.EventFetDao;
import us.tx.state.dfps.service.common.request.DayCareRequestReq;
import us.tx.state.dfps.service.common.response.DayCareRequestRes;
import us.tx.state.dfps.service.common.utils.ServiceAuthTWCCommUtils;
import us.tx.state.dfps.service.common.utils.ServiceAuthTWCCommUtils.SvcAuthDtlTWCChange;
import us.tx.state.dfps.service.common.utils.TypeConvUtil;
import us.tx.state.dfps.service.commontodofunction.service.CommonToDoFunctionService;
import us.tx.state.dfps.service.dcr.dao.DayCareReqPersonDao;
import us.tx.state.dfps.service.dcr.dao.DayCareRequestDao;
import us.tx.state.dfps.service.dcr.dao.TypeOfServiceDCRDao;
import us.tx.state.dfps.service.dcr.dto.DayCareFacilityDto;
import us.tx.state.dfps.service.dcr.dto.DayCarePersonDto;
import us.tx.state.dfps.service.dcr.dto.DayCareRequestBean;
import us.tx.state.dfps.service.dcr.dto.DayCareRequestDto;
import us.tx.state.dfps.service.dcr.dto.DayCareSearchListDto;
import us.tx.state.dfps.service.dcr.service.DayCareRequestService;
import us.tx.state.dfps.service.dcr.service.TypeOfServiceDCRService;
import us.tx.state.dfps.service.exception.ServiceLayerException;
import us.tx.state.dfps.service.financial.dao.ServiceAuthExtCommDao;
import us.tx.state.dfps.service.lookup.dao.LookupDao;
import us.tx.state.dfps.service.sscc.dao.SSCCRefDao;
import us.tx.state.dfps.service.workload.dao.TodoDao;
import us.tx.state.dfps.xmlstructs.inputstructs.MergeSplitToDoDto;
import us.tx.state.dfps.xmlstructs.inputstructs.ServiceInputDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This class
 * is the service implementation of the Type of service DCI page Apr 3, 2018-
 * 12:13:47 PM © 2017 Texas Department of Family and Protective Services
 */
@Service
@Transactional
public class TypeOfServiceDCRServiceImpl implements TypeOfServiceDCRService {

	@Autowired
	TypeOfServiceDCRDao typeOfServiceDCRDao;

	@Autowired
	DayCareReqPersonDao dayCareReqPersonDao;

	@Autowired
	DayCareRequestDao dayCareRequestDao;

	@Autowired
	LookupDao lookupDao;

	@Autowired
	ServiceAuthExtCommDao serviceAuthExtCommDao;

	@Autowired
	EventFetDao eventFetDao;

	@Autowired
	EventDao eventDao;

	@Autowired
	ServiceAuthTWCCommUtils serviceAuthTWCCommUtils;

	@Autowired
	private CommonToDoFunctionService commonToDoFunctionService;

	@Autowired
	DayCareRequestService dayCareRequestService;

	@Autowired
	SSCCRefDao sSCCRefDao;

	@Autowired
	TodoDao todoDao;

	private static final Logger log = Logger.getLogger(TypeOfServiceDCRServiceImpl.class);
	private static final String SLASH_DATE_MASK = "MM/dd/yyyy";
	private static final int MSG_DAYCARE_SERVICE_OVERLAP = 56110;

	/**
	 * Method Name: getXmlResponsesLast Method Description: This method gets an
	 * XML string that contains the answers to the displayed questions in the
	 * Decision Tree.
	 * 
	 * @param dayCareRequestReq
	 * @return String
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public DayCareRequestBean getTypeOfServiceDetail(DayCareRequestReq dayCareRequestReq) {
		Long idPerson = dayCareRequestReq.getIdPerson();

		Long idUser = dayCareRequestReq.getIdUser();
		String dateStr = ServiceConstants.EMPTY_STRING;

		// Get the day care request bean and person DTO
		DayCareRequestBean dayCareRequestBean = ObjectUtils.isEmpty(dayCareRequestReq.getDayCareRequestBean())
				? new DayCareRequestBean() : dayCareRequestReq.getDayCareRequestBean();
		DayCarePersonDto dayCarePersonDto = ObjectUtils
				.isEmpty(dayCareRequestReq.getDayCareRequestBean().getDayCarePersonDto()) ? new DayCarePersonDto()
						: dayCareRequestReq.getDayCareRequestBean().getDayCarePersonDto();

		// Event status
		dayCareRequestBean.setEventDto(eventDao.getEventByid(dayCareRequestReq.getIdEvent()));

		// Is the person logged in an SSCC user?
		dayCareRequestBean.setSSCCUser(sSCCRefDao.isUserSSCCExternal(idUser, ServiceConstants.EMPTY_STR));

		// Set the Day care request response from Day Care Service
		dayCareRequestBean.setDayCareRequestRes(dayCareRequestService.retrieveDayCareRequestDetailsForDisplay(
				dayCareRequestReq.getIdStage(), dayCareRequestReq.getIdEvent(), dayCareRequestReq.getIdUser()));

		// Set the Person DTO
		DayCareRequestDto dayCareRequestDto = new DayCareRequestDto();
		dayCareRequestDto.setIdEvent(dayCareRequestReq.getIdEvent());
		dayCareRequestDto = dayCareRequestService.retrieveDayCareRequestDetail(dayCareRequestReq.getIdEvent());

		dayCareRequestBean.setDayCareRequestDto(dayCareRequestDto);

		Long idDayCareRequest = dayCareRequestDto.getIdDayCareRequest();
		dayCareRequestReq.setIdDayCareRequest(idDayCareRequest);

		List<DayCarePersonDto> dayCarePersonList = dayCareRequestService.retrieveDayCarePersonLink(idDayCareRequest);
		dayCareRequestDto.setDayCarePersonDtoList(dayCarePersonList);
		List<DayCarePersonDto> dayCarePersonValueBeanList = dayCareRequestDto.getDayCarePersonDtoList();
		for (DayCarePersonDto dayCarePerson : dayCarePersonValueBeanList) {
			if (0 == dayCarePerson.getIdPerson().compareTo(idPerson)) {
				dayCarePersonDto = dayCarePerson;
				break;
			}
		}

		// Set the Day care Facility List
		dayCarePersonDto.setDayCareFacilityDtoList(
				typeOfServiceDCRDao.retrieveDayCarePersonFacilLink(dayCareRequestReq).getDayCareFacilityDtoList());

		// Set the Day care Search list
		List<DayCareSearchListDto> dayCareSearchListDtoList = new ArrayList();
		for (DayCareFacilityDto dayCareFacilityValueBean : dayCarePersonDto.getDayCareFacilityDtoList()) {
			DayCareSearchListDto dayCareSearchValueBean = dayCareRequestService
					.getFacilityById(dayCareFacilityValueBean.getIdFacility());
			// if this is the active primary facility, insert first in list
			if (0 == dayCareFacilityValueBean.getIdFacility().compareTo(dayCarePersonDto.getIdFacilityActive())) {
				dayCareSearchListDtoList.add(0, dayCareSearchValueBean);
			} else {
				dayCareSearchListDtoList.add(dayCareSearchValueBean);
			}
		}
		dayCareRequestBean.setDayCareSearchListDtoList(dayCareSearchListDtoList);

		// Set the XML Responses user last saved
		dayCarePersonDto.setXmlResponsesUser(
				typeOfServiceDCRDao.getXmlResponsesLast(idPerson.intValue(), idDayCareRequest.intValue()));

		// Set the XML Responses system for Decision Tree XML
		dayCarePersonDto.setXmlResponsesSystem(typeOfServiceDCRDao.getXmlResponsesSystem(idPerson.intValue(),
				idDayCareRequest.intValue(), idUser.intValue()));

		// Set the Approval Date String
		Date dciApprovalDate;
		dciApprovalDate = typeOfServiceDCRDao.getApprovalDate(idDayCareRequest.intValue());
		if (!ObjectUtils.isEmpty(dciApprovalDate))
			dateStr = dateString(dciApprovalDate);
		dayCarePersonDto.setTxtApprovalDate(dateStr);

		// Set the person DTO back to request bean
		dayCareRequestBean.setDayCarePersonDto(dayCarePersonDto);

		return dayCareRequestBean;
	}

	/**
	 * Method Name: deleteTypeOfService Method Description: This method deletes
	 * child day care service type
	 * 
	 * @param dayCareRequestDto
	 * @return
	 */
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public void deleteTypeOfService(DayCareRequestReq dayCareRequestReq) {
		// Delete From DAY CARE PERSON LINK Table
		typeOfServiceDCRDao.deleteTypeOfService(dayCareRequestReq.getIdPerson(),
				dayCareRequestReq.getIdDayCareRequest(), dayCareRequestReq.getIdPersonLastUpdated());

		// Delete the response through procedure
		typeOfServiceDCRDao.deleteResponses(dayCareRequestReq.getIdDayCareRequest().intValue(),
				dayCareRequestReq.getIdPerson().intValue());

		// Delete the Day care person Facility link
		if (!CollectionUtils.isEmpty(dayCareRequestReq.getDayCareSearchListDtoList())) {
			for (DayCareSearchListDto dcrSearchListDto : dayCareRequestReq.getDayCareSearchListDtoList()) {
				typeOfServiceDCRDao.deleteDayCarePersonFacilLink(dayCareRequestReq.getIdPerson(),
						dayCareRequestReq.getIdDayCareRequest(), dcrSearchListDto.getIdFacility());
			}
		}
	}

	/**
	 * Method Name: saveDayCarePersonInfo Method Description: This method
	 * saves(insert/update) Day Care Request Person Information
	 * 
	 * @param dayCareRequestReq
	 * @return
	 */
	@Override
	@Transactional(readOnly = false, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public DayCareRequestRes saveTypeofService(DayCareRequestReq dayCareRequestReq){
		// Response object
		DayCareRequestRes dayCareRequestRes = new DayCareRequestRes();

		// Day Care request
		DayCareRequestDto dayCareRequestValueDto = dayCareRequestReq.getDayCareRequestValueDto();
		DayCarePersonDto dayCarePersonDto = dayCareRequestValueDto.getDayCarePersonDtoList().get(ServiceConstants.Zero);
		
		if (!StringUtils.isEmpty(dayCarePersonDto.getCdDetermService())
				&& (!CodesConstant.CSVCCODE_40M.equalsIgnoreCase(dayCarePersonDto.getCdDetermService())
						&& !ServiceConstants.EMPTY_STR.equals(dayCarePersonDto.getCdDetermService())
						&& !ServiceConstants.NO_DAYCARE.equals(dayCarePersonDto.getCdDetermService()))) {
			// verify there is no overlap with existing service
			//Defect#11403 To  allow the apporved day care request to save if already service auth approved
			boolean isOverlapExists = checkOverlapRecsForSvcAuth(dayCarePersonDto, dayCareRequestReq.getIdPerson(),dayCareRequestReq.getDayCareRequestValueDto().getIdEvent());
			if (isOverlapExists) {
				ErrorDto errorDto = new ErrorDto();
				List<DayCarePersonDto> dayCarePersonDtos = new ArrayList<DayCarePersonDto>();
				dayCarePersonDtos.add(dayCarePersonDto);
				errorDto.setErrorCode(MSG_DAYCARE_SERVICE_OVERLAP);
				dayCareRequestRes.setErrorDto(errorDto);
				dayCareRequestRes.setDayCarePersonDtos(dayCarePersonDtos);
				return dayCareRequestRes;
			}
		}
		Long idDaycarePersonLink = dayCarePersonDto.getIdDaycarePersonLink();
		Long idEvent = dayCareRequestValueDto.getIdEvent();
		if (idDaycarePersonLink == ServiceConstants.LongZero) {
			idDaycarePersonLink = dayCareReqPersonDao.insertDayCarePersonLink(dayCarePersonDto);
			dayCarePersonDto.setIdDaycarePersonLink(idDaycarePersonLink);
		} else {

			dayCareReqPersonDao.updateDayCarePersonLink(dayCarePersonDto);

			if (dayCarePersonDto.getDecisionRedetermined()
					|| TypeConvUtil.isNullOrEmpty(dayCarePersonDto.getCdDetermService())
					|| dayCarePersonDto.getCdDetermService().equals(ServiceConstants.NO_DAYCARE)) {
				dayCareReqPersonDao.deleteDayCarePersonFacilLink(idDaycarePersonLink);
				dayCarePersonDto.getDayCareFacilityDtoList().clear();
			}

			if (dayCareRequestValueDto.getInvalidateApproval()) {
				dayCareRequestDao.updateApproversStatus(idEvent);
				Long idApprovalEvent = dayCareRequestDao.getAppEventId(idEvent);
				dayCareRequestDao.updateAppEventStatus(idApprovalEvent);
			}

			createTWCTransmissionUpdateAlert(idEvent, dayCareRequestValueDto.getIdlastUpdatePerson());
		}

		saveDayCareFacilityInfo(dayCarePersonDto);
		// Call the Save XML Response Internally
		typeOfServiceDCRDao.saveXmlResponses(dayCareRequestReq.getIdDayCareRequest().intValue(),
				dayCareRequestReq.getIdPerson().intValue(), dayCareRequestReq.getIdPersonLastUpdated().intValue(),
				dayCareRequestReq.getXmlResponses());

		log.debug("Exiting method saveDayCarePersonInfo in DayCareRequestService");
		return dayCareRequestRes;

	}

	/**
	 * 
	 * Method Name: createTWCTransmissionUpdateAlert Method Description:This
	 * function creates Alert to the RDCC notifying them that the Approved Day
	 * Care Request has been modified and the Update needs to be Transmitted to
	 * TWC.
	 * 
	 * Approved Day Care Request can only be modified by RDCC. So the Alert
	 * needs to be displayed for the logged in user.
	 * 
	 * Since RDCC can modify Day Care Request multiple times before sending the
	 * Servive Authorization Xml to TWC, we need to check if there is an
	 * existing Alert before creating new Alert to avoid creating a new Alert
	 * every time the page has been saved.
	 * 
	 * @param idDayCareEvent
	 * @param userId
	 */
	private void createTWCTransmissionUpdateAlert(Long idDayCareEvent, Long userId){

		Long idSvcAuth = serviceAuthExtCommDao.retrieveDayCareSvcAuthId(idDayCareEvent);
		if (idSvcAuth != ServiceConstants.LongZero) {
			Long idSvcAuthEvent = serviceAuthExtCommDao.retrieveDayCareSvcAuthEventId(idDayCareEvent);

			FetchEventDto fetchEventDto = new FetchEventDto();
			fetchEventDto.setIdEvent(idDayCareEvent);
			FetchEventRowDto fetchEventRowDto = eventFetDao.fetchEventDetails(fetchEventDto).getFetchEventRowDto();

			if (isDayCareReqAfterNov2013Rel(idSvcAuthEvent)
					&& ServiceConstants.CEVTSTAT_APRV.equals(fetchEventRowDto.getCdEventStatus())) {
				SvcAuthDtlTWCChange baselineChanged = serviceAuthTWCCommUtils.isTWCBaselineModified(idSvcAuthEvent,
						idSvcAuth);
				if (baselineChanged == SvcAuthDtlTWCChange.UPDATE) {

					Boolean todoExists = todoDao.isTodoExists(idSvcAuthEvent,
							ServiceConstants.CD_TODO_SA_TWC_TRNS_UPDATE, userId);
					if (todoExists == ServiceConstants.FALSEVAL) {
						String shortDesc = ServiceConstants.DAY_CARE_SERVICE_AUTH_RDCC_TWC;
						String longDesc = ServiceConstants.DAY_CARE_SERVICE_AUTH_RDCC_TWC;
						createRdccToDoFunc(shortDesc, longDesc, ServiceConstants.CD_TODO_SA_TWC_TRNS_UPDATE, userId,
								userId, fetchEventRowDto.getIdStage(), idSvcAuthEvent);
					}
				}
			}
		}

	}

	/**
	 * 
	 * Method Name: isDayCareReqAfterNov2013Rel Method Description:This function
	 * checks the following Conditions for generating Service Authorization
	 * Transmission Alerts to TWC.
	 * 
	 * 1. Is this Day Care Service Authorization Request? 2. Day Care Request
	 * has been created After Nov 2013 Release Date. 3. Online Parameter value
	 * is set to Y.
	 * 
	 * @param idSvcAuthEvent
	 * @return Boolean
	 */
	private Boolean isDayCareReqAfterNov2013Rel(Long idSvcAuthEvent){
		Boolean dcReqPostNov13 = ServiceConstants.FALSEVAL;

		Long idDayCareReqEvent = dayCareRequestDao.retrieveDCReqEventForSvcAuthEvent(idSvcAuthEvent);
		if (idDayCareReqEvent != ServiceConstants.LongZero) {

			FetchEventDto fetchEventDto = new FetchEventDto();
			fetchEventDto.setIdEvent(idDayCareReqEvent);
			FetchEventRowDto fetchEventRowDto = eventFetDao.fetchEventDetails(fetchEventDto).getFetchEventRowDto();

			String nov13RelDt = lookupDao.simpleDecode(ServiceConstants.CRELDATE,
					ServiceConstants.CRELDATE_NOV_2013_IMPACT);
			Date date1= null;
			try {
				date1 = new SimpleDateFormat("MM/dd/yyyy").parse(nov13RelDt);
			} catch (ParseException e) {
				new ServiceLayerException(e.getMessage());
			}
			if (!TypeConvUtil.isNullOrEmpty(date1) && fetchEventRowDto.getDtDtEventCreated().after(date1)) {

				String onlineParamTwcEnabled = serviceAuthExtCommDao
						.selectOnlineParameterValue(ServiceConstants.ONLINE_PARAM_TWC_AUT_TRANS);
				if (ServiceConstants.Y.equals(onlineParamTwcEnabled)) {
					dcReqPostNov13 = ServiceConstants.TRUEVAL;
				}

			}
		}

		return dcReqPostNov13;
	}

	/**
	 * 
	 * Method Name: createRdccToDo Method Description:This method retrieves data
	 * from todocommunication method
	 * 
	 * @param toDoDesc
	 * @param toDoLongDesc
	 * @param cdTodoInfoType
	 * @param idPrsnAssgn
	 * @param idUser
	 * @param idStage
	 * @param idEvent
	 * @
	 */
	private void createRdccToDoFunc(String toDoDesc, String toDoLongDesc, String cdTodoInfoType, Long idPrsnAssgn,
			Long idUser, Long idStage, Long idEvent) {

		TodoCreateInDto todoCreateInDto = new TodoCreateInDto();
		MergeSplitToDoDto mergeSplitToDoDto = new MergeSplitToDoDto();
		todoCreateInDto.setServiceInputDto(new ServiceInputDto());
		mergeSplitToDoDto.setCdTodoCf(cdTodoInfoType);
		mergeSplitToDoDto.setDtTodoCfDueFrom(null);
		mergeSplitToDoDto.setIdTodoCfPersCrea(idUser);
		mergeSplitToDoDto.setIdTodoCfStage(idStage);
		mergeSplitToDoDto.setIdTodoCfPersAssgn(idPrsnAssgn);
		mergeSplitToDoDto.setIdTodoCfPersWkr(idUser);
		if (toDoDesc != null)
			mergeSplitToDoDto.setTodoCfDesc(toDoDesc);
		if (toDoLongDesc != null)
			mergeSplitToDoDto.setTodoCfLongDesc(toDoLongDesc);
		if (idEvent > 0) {
			mergeSplitToDoDto.setIdTodoCfEvent(idEvent);
		}
		todoCreateInDto.setMergeSplitToDoDto(mergeSplitToDoDto);
		commonToDoFunctionService.TodoCommonFunction(todoCreateInDto);
	}

	/**
	 * 
	 * Method Name: saveDayCareFacilityInfo Method Description:This method saves
	 * Day Care Facility information into database.
	 * 
	 * @param dayCarePersonDto
	 */
	private void saveDayCareFacilityInfo(DayCarePersonDto dayCarePersonDto) {
		// Get the Id Day care person Link
		Long idDaycarePersonLink = dayCarePersonDto.getIdDaycarePersonLink();

		// Get the Day care facility list already selected from DB
		List<DayCareFacilityDto> dayCareFacilityListFromDb = dayCareReqPersonDao
				.retrieveDayCarePersonFacilLink(idDaycarePersonLink, ServiceConstants.TRUEVAL);

		// Get the Day care facility list currently selected from Screen
		List<DayCareFacilityDto> dayCareFacilityList = dayCarePersonDto.getDayCareFacilityDtoList();

		// To insert the DTO which is not in DB
		if (!ObjectUtils.isEmpty(dayCareFacilityList)) {
			dayCareFacilityList.forEach(screenDto -> {
				DayCareFacilityDto dcfacilityDto = null;
				if (!ObjectUtils.isEmpty(dayCareFacilityListFromDb)) {
					dcfacilityDto = dayCareFacilityListFromDb.stream()
							.filter(dbDto -> dbDto.getIdFacility().equals(screenDto.getIdFacility())).findAny()
							.orElse(null);
				}
				if (ObjectUtils.isEmpty(dcfacilityDto)) {
					// call to insert entity
					dayCareReqPersonDao.insertDayCarePersonFacilLink(screenDto);
				}
			});
		}
		;

		// To Delete the DTO which is selected from Screen
		if (!ObjectUtils.isEmpty(dayCareFacilityListFromDb)) {
			dayCareFacilityListFromDb.forEach(dbDto -> {
				DayCareFacilityDto dcfacilityDto = null;
				if (!ObjectUtils.isEmpty(dayCareFacilityList)) {
					dcfacilityDto = dayCareFacilityList.stream()
							.filter(screenDto -> screenDto.getIdFacility().equals(dbDto.getIdFacility())).findAny()
							.orElse(null);
				}
				if (ObjectUtils.isEmpty(dcfacilityDto)) {
					// call to delete entity
					dayCareReqPersonDao.deleteDayCarePersonFacilLink(dbDto.getIdDaycarePersonFacilLink());
				}
			});
		}
	}
	
	
	/**
	 * Method Name: getOverlapRecsForSvcAuth Method Description: for a service
	 * authorization detail record being saved or added check if its
	 * DT_SVC_AUTH_DTL_BEGIN and DT_SVC_AUTH_DTL_TERM overlap with an existing
	 * record in svc_auth_detail table.
	 * 
	 * @param dayCarePersonDto
	 * @param idPerson
	 * @return
	 */
	public List<DayCarePersonDto> getOverlapRecsForSvcAuth(DayCarePersonDto dayCarePersonDto, Long idPerson) {
		List<DayCarePersonDto> svcAuthDtlList = typeOfServiceDCRDao.getOverlapRecsForSvcAuth(dayCarePersonDto,
				idPerson);				
		return svcAuthDtlList;
	}

	/**
	 * Method Name: checkOverlapRecsForSvcAuth Method Description: for a service
	 * authorization detail record being saved or added check if its
	 * DT_SVC_AUTH_DTL_BEGIN and DT_SVC_AUTH_DTL_TERM overlap with an existing
	 * record in svc_auth_detail table.
	 * 
	 * @param dayCarePersonDto
	 * @param idPerson
	 * @param idEvent
	 * @return boolean
	 */
	public boolean checkOverlapRecsForSvcAuth(DayCarePersonDto dayCarePersonDto, Long idPerson,Long idEvent) {
		//Defect# 11403 added extra check to allow daya care request to save
		List<DayCarePersonDto> svcAuthDtlList = typeOfServiceDCRDao.getOverlapRecsForSvcAuth(dayCarePersonDto,
				idPerson);				
		return typeOfServiceDCRDao.getSvcAuthLink(svcAuthDtlList,dayCarePersonDto,idEvent);
	}

	/**
	 * Method Name: dateString Method Description: for a Java date to change it
	 * to string
	 * 
	 * @param date
	 * @return String
	 */
	public static String dateString(java.util.Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat(SLASH_DATE_MASK);
		String dateString = "";
		dateString = sdf.format(date);
		return dateString;
	}
}
