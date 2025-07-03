package us.tx.state.dfps.service.conservatorship.serviceimpl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import us.tx.state.dfps.common.dto.EmailDetailsDto;
import us.tx.state.dfps.common.exception.InvalidRequestException;
import us.tx.state.dfps.service.childplan.dao.ChildPlanDtlDao;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.EventDao;
import us.tx.state.dfps.service.common.dao.StageDao;
import us.tx.state.dfps.service.common.request.CnsrvtrshpRemovalAlertReq;
import us.tx.state.dfps.service.common.request.CnsrvtrshpRemovalReq;
import us.tx.state.dfps.service.common.request.CommonEventIdReq;
import us.tx.state.dfps.service.common.request.CommonHelperReq;
import us.tx.state.dfps.service.common.response.CnsrvtrshpRemovalAlertRes;
import us.tx.state.dfps.service.common.response.CnsrvtrshpRemovalRes;
import us.tx.state.dfps.service.common.response.CommonBooleanRes;
import us.tx.state.dfps.service.common.response.CommonHelperRes;
import us.tx.state.dfps.service.common.response.EmployeeMailRes;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.util.JNDIUtil;
import us.tx.state.dfps.service.common.util.JNDIUtil.EncryptionException;
import us.tx.state.dfps.service.common.util.OutlookUtil;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.conservatorship.dao.CharacteristicsDao;
import us.tx.state.dfps.service.conservatorship.dao.CnsrvtrshpRemovalAlertDao;
import us.tx.state.dfps.service.conservatorship.dao.CnsrvtrshpRemovalDao;
import us.tx.state.dfps.service.conservatorship.dao.RemovalCharAdultDao;
import us.tx.state.dfps.service.conservatorship.dao.RemovalCharChildDao;
import us.tx.state.dfps.service.conservatorship.dao.RemovalReasonDao;
import us.tx.state.dfps.service.conservatorship.dto.CharacteristicsDto;
import us.tx.state.dfps.service.conservatorship.dto.CnsrvtrshpDtlDto;
import us.tx.state.dfps.service.conservatorship.dto.CnsrvtrshpRemovalDto;
import us.tx.state.dfps.service.conservatorship.dto.ConservatorshipCommonDto;
import us.tx.state.dfps.service.conservatorship.dto.RemovalCharAdultDto;
import us.tx.state.dfps.service.conservatorship.dto.RemovalCharChildDto;
import us.tx.state.dfps.service.conservatorship.dto.RemovalReasonDto;
import us.tx.state.dfps.service.conservatorship.dto.StageIncomingDto;
import us.tx.state.dfps.service.conservatorship.service.ConservatorshipService;
import us.tx.state.dfps.service.outlook.AppointmentDto;
import us.tx.state.dfps.service.person.dao.PersonDao;
import us.tx.state.dfps.service.workload.dto.EventDto;
import us.tx.state.dfps.service.workload.dto.EventListDto;
import us.tx.state.dfps.service.workload.dto.PersonDto;
import us.tx.state.dfps.service.workload.dto.StageDto;

/**
 * 
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name: CSUB14S Class
 * Description: This is the retrieve service used by Conservatorship Removal to
 * retrieve information regarding the Removal Event. It retrieves the detail for
 * the event, as well as the removal reason, child removal characteristics, and
 * adult removal characteristics. May 1,2017 - 5:10:51 PM
 */
@Service
@Transactional
public class ConservatorshipServiceImpl implements ConservatorshipService {

	private static final String TRANSACTION_ID = "TransactionId :";

	@Autowired
	CharacteristicsDao characteristicsDao;

	@Autowired
	CnsrvtrshpRemovalDao cnsrvtrshpRemovalDao;

	@Autowired
	RemovalReasonDao removalReasonDao;

	@Autowired
	RemovalCharChildDao removalCharChildDao;

	@Autowired
	RemovalCharAdultDao removalCharAdultDao;

	@Autowired
	EventDao eventDao;

	@Autowired
	PersonDao personDao;

	@Autowired
	StageDao stageDao;

	@Autowired
	OutlookUtil outlookUtil;

	@Autowired
	CnsrvtrshpRemovalAlertDao cnsrvtrshpRemovalAlertDao;

	@Autowired
	ChildPlanDtlDao childPlanDtlDao;

	public static final ResourceBundle EMAIL_CONFIG_BUNDLE = ResourceBundle.getBundle("EmailConfig");
	public static final String CVSREMOVAL_EMAIL_CONFIG_BASE = "CVSRemoval.emailId.";
	private static final String PROD = "Prod";
	private static final Logger log = Logger.getLogger(ConservatorshipServiceImpl.class);

	private static final String SUBJECT = "Initial CPOS due for ";
	private static final String SUBJECT_REVIEW = "CPOS Review due ";
	private static final String SUCCESS = "success";
	private static final String OUTLOOKSECURITY = "jndi/outlook_security";
	public static final String EXCEPTION_STRING_ONE = "Exception Occured while ";
	public static final String EXCEPTION_STRING_TWO = " of Class ";
	private static String NO_JNDI = "JNDI_NOT_PRESENT";

	/**
	 * Method Description: This is the retrieve service used by Conservatorship
	 * Removal to retrieve information regarding the Removal Event. It retrieves
	 * the detail for the event, as well as the removal reason, child removal
	 * characteristics, and adult removal characteristics. Service Name: CSUB14S
	 * 
	 * @param cnsrvtrshpRemovalReq
	 * @return CnsrvtrshpRemovalRes @
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public CnsrvtrshpRemovalRes getRmvlEventDtls(CnsrvtrshpRemovalReq cnsrvtrshpRemovalReq) {
		CnsrvtrshpRemovalRes rmvlEventOut = new CnsrvtrshpRemovalRes();
		List<RemovalCharChildDto> rmvlChrChildDtlList = new ArrayList<>();
		StageIncomingDto stgDtl = new StageIncomingDto();
		PersonDto personDtl = new PersonDto();
		Date sysCurrentDate = new Date();
		List<CnsrvtrshpRemovalDto> cnvRemovalDtlList = new ArrayList<>();
		CnsrvtrshpRemovalDto cnsrvtrshpRemovalDto = new CnsrvtrshpRemovalDto();
		CnsrvtrshpDtlDto cnsrvtrshpDtlDto = new CnsrvtrshpDtlDto();
		List<CnsrvtrshpDtlDto> cnsrvtrshpDtlDtoList = new ArrayList<>();
		stgDtl = stageDao.getEarliestIntakeDates(cnsrvtrshpRemovalReq.getIdStage());
		if (!TypeConvUtil.isNullOrEmpty(stgDtl) && !TypeConvUtil.isNullOrEmpty(stgDtl.getDtIncomingCall())) {
			rmvlEventOut.setDtIncomingCall(stgDtl.getDtIncomingCall());
			rmvlEventOut.setDtWCDDtSystemDate(sysCurrentDate);
		}
		if (!ObjectUtils.isEmpty(cnsrvtrshpRemovalReq.getIdEventList())
				&& cnsrvtrshpRemovalReq.getIdEventList().size() > 0) {
			EventDto evntDtl = new EventDto();
			List<ConservatorshipCommonDto> conservatorshipCommonDtoList = new ArrayList<>();
			ConservatorshipCommonDto conservatorshipCommonDto;
			List<RemovalCharChildDto> removalCharChildDtoList;
			evntDtl = eventDao.getEventByid(cnsrvtrshpRemovalReq.getIdEventList().get(0));
			rmvlEventOut.setEventDto(evntDtl);
			if (!TypeConvUtil.isNullOrEmpty(evntDtl)) {
				if (!(ServiceConstants.CD_EVENT_STATUS_NEW.equalsIgnoreCase(evntDtl.getCdEventStatus()))) {
					List<RemovalReasonDto> remReasonDtl = new ArrayList<>();
					List<RemovalCharAdultDto> rmvlChrAdltDtl = new ArrayList<>();
					cnvRemovalDtlList = cnsrvtrshpRemovalDao
							.getCnsrvtrshpRemovalDtl(cnsrvtrshpRemovalReq.getIdEventList());
					if (!TypeConvUtil.isNullOrEmpty(cnvRemovalDtlList)) {
						for (CnsrvtrshpRemovalDto cnsrvtrshpRmvlDto : cnvRemovalDtlList) {
							conservatorshipCommonDto = new ConservatorshipCommonDto();
							conservatorshipCommonDto.setIdPerson(cnsrvtrshpRmvlDto.getIdVictim());
							conservatorshipCommonDto.setNmPersonFull(cnsrvtrshpRmvlDto.getNmPersonFull());
							PersonDto personDto = new PersonDto();
							personDto = personDao.getPersonById(cnsrvtrshpRmvlDto.getIdVictim());
							cnsrvtrshpDtlDto = new CnsrvtrshpDtlDto();
							cnsrvtrshpDtlDto.setNmPersonFull(!StringUtils.isEmpty(personDto.getCdPersonSuffix())
									? personDto.getNmPersonFull() + " " + personDto.getCdPersonSuffix()
									: personDto.getNmPersonFull());
							conservatorshipCommonDto.setCnsrvtrshpDtlDto(cnsrvtrshpDtlDto);
							conservatorshipCommonDto.setCnsrvtrshpRemovalDto(cnsrvtrshpRmvlDto);
							cnsrvtrshpRemovalDto = cnsrvtrshpRmvlDto;
							cnsrvtrshpDtlDtoList.add(cnsrvtrshpDtlDto);
							conservatorshipCommonDtoList.add(conservatorshipCommonDto);
						}
						remReasonDtl = removalReasonDao.getRemReasonDtl(cnsrvtrshpRemovalReq.getIdEventList());
						rmvlEventOut.setRemovalReasonDto(remReasonDtl);
						rmvlChrChildDtlList = removalCharChildDao
								.getRemCharChildDtl(cnsrvtrshpRemovalReq.getIdEventList());
						int i = 0;
						for (ConservatorshipCommonDto conservatorshipCommonDto1 : conservatorshipCommonDtoList) {
							removalCharChildDtoList = new ArrayList<>();
							for (RemovalCharChildDto removalCharChildDto : rmvlChrChildDtlList) {
								if (conservatorshipCommonDto1.getIdPerson().equals(removalCharChildDto.getIdPerson())) {
									removalCharChildDtoList.add(removalCharChildDto);
								}
							}
							conservatorshipCommonDtoList.get(i).setRemovalCharChildDtoList(removalCharChildDtoList);
							i++;
						}
						rmvlEventOut.setConservatorshipCommonDtoList(conservatorshipCommonDtoList);
						if (!cnvRemovalDtlList.get(0).getIndRemovalNaCare().toString()
								.equals(ServiceConstants.IND_REMOVAL_NA_CARE_YES)) {
							rmvlChrAdltDtl = removalCharAdultDao
									.getRemCharAdultDtl(cnsrvtrshpRemovalReq.getIdEventList());
							rmvlEventOut.setRemovalCharAdultDto(rmvlChrAdltDtl);
						}
					}
				}
			}
		}
		if (((ServiceConstants.WINDOW_MODE_NEW).equalsIgnoreCase(cnsrvtrshpRemovalReq.getSysCdWinMode()))
				|| (ServiceConstants.WINDOW_MODE_NEW_USING).equalsIgnoreCase(cnsrvtrshpRemovalReq.getSysCdWinMode())) {
			List<Long> idPerson = cnsrvtrshpRemovalReq.getIdPersonList();
			List<ConservatorshipCommonDto> conservatorshipCommonDtoList = new ArrayList<>();
			ConservatorshipCommonDto conservatorshipCommonDto;
			List<RemovalCharChildDto> removalCharChildDtoList;
			if (!TypeConvUtil.isNullOrEmpty(idPerson)) {
				for (Long personid : idPerson) {
					conservatorshipCommonDto = new ConservatorshipCommonDto();
					personDtl = personDao.getPersonById(personid);
					conservatorshipCommonDto.setIdPerson(personid);
					conservatorshipCommonDto.setNmPersonFull(!StringUtils.isEmpty(personDtl.getCdPersonSuffix())
							? personDtl.getNmPersonFull() + " " + personDtl.getCdPersonSuffix()
							: personDtl.getNmPersonFull());
					cnsrvtrshpDtlDto = new CnsrvtrshpDtlDto();
					cnsrvtrshpDtlDto.setDtPersonBirth(personDtl.getDtPersonBirth());
					cnsrvtrshpDtlDto.setNmPersonFull(!StringUtils.isEmpty(personDtl.getCdPersonSuffix())
							? personDtl.getNmPersonFull() + " " + personDtl.getCdPersonSuffix()
							: personDtl.getNmPersonFull());
					cnsrvtrshpDtlDto.setCdPersonEthnicGroup(personDtl.getCdPersonEthnicGroup());
					if (!TypeConvUtil.isNullOrEmpty(personDtl)) {
						if (!TypeConvUtil.isNullOrEmpty(personDtl.getDtPersonBirth())) {
							if (DateUtils.calculatePersonsAgeInYears(personDtl.getDtPersonBirth(),
									sysCurrentDate) >= 18) {
								cnsrvtrshpDtlDto.setSubCareChildAge(ServiceConstants.MSG_SUB_CHILD_AGE);
							}
						}
						if (!TypeConvUtil.isNullOrEmpty(cnsrvtrshpRemovalReq.getIdPersonList())) {
							int subcareStgCount = stageDao.getStageCount(personid,
									ServiceConstants.REMOVAL_STG_PERS_ROLL_PC, ServiceConstants.CSTAGES_SUB);
							if (subcareStgCount > 0) {
								cnsrvtrshpDtlDto.setSubCareStgExists(ServiceConstants.SUBCARE_EXISTS);
							} else {
								if (ServiceConstants.NO_PERSON_CHAR_2.equalsIgnoreCase(personDtl.getCdPersonChar())) {
									cnsrvtrshpRemovalDto
											.setIndRemovalNaChild(ServiceConstants.IND_REMOVAL_NA_CHILD_YES);
									cnvRemovalDtlList.add(cnsrvtrshpRemovalDto);
								} else {
									List<CharacteristicsDto> charDtl = new ArrayList<>();
									charDtl = characteristicsDao.getCharDtls(personid, sysCurrentDate, sysCurrentDate);
									Boolean bFoundPlacement = Boolean.FALSE;
									removalCharChildDtoList = new ArrayList<>();
									for (int i = 0; i < charDtl.size(); i++) {
										if (charDtl.get(i).getCdCharCategory()
												.equalsIgnoreCase(ServiceConstants.CD_CHAR_CATEGORY_CPL)) {
											bFoundPlacement = Boolean.TRUE;
											RemovalCharChildDto removalCharChildDto = new RemovalCharChildDto();
											removalCharChildDto
													.setCdRemovChildChar(charDtl.get(i).getCdCharacteristic());
											removalCharChildDto.setNmPersonFull(charDtl.get(i).getNmPersonFull());
											removalCharChildDto.setIdPerson(charDtl.get(i).getIdPerson());
											rmvlChrChildDtlList.add(removalCharChildDto);
											removalCharChildDtoList.add(removalCharChildDto);
										}
									}
									conservatorshipCommonDto.setRemovalCharChildDtoList(removalCharChildDtoList);
									if (true == bFoundPlacement) {
										cnsrvtrshpRemovalDto
												.setIndRemovalNaChild(ServiceConstants.IND_REMOVAL_NA_CHILD_NO);
										cnvRemovalDtlList.add(cnsrvtrshpRemovalDto);
									} else {
										cnsrvtrshpDtlDto
												.setSubcarePlacementChar(ServiceConstants.MSG_SUB_PLCMT_CHAR_NEEDED);
									}
								}
							}
						}
					}
					cnsrvtrshpDtlDtoList.add(cnsrvtrshpDtlDto);
					conservatorshipCommonDto.setCnsrvtrshpDtlDto(cnsrvtrshpDtlDto);
					conservatorshipCommonDto.setCnsrvtrshpRemovalDto(cnsrvtrshpRemovalDto);
					conservatorshipCommonDtoList.add(conservatorshipCommonDto);
				}
				rmvlEventOut.setConservatorshipCommonDtoList(conservatorshipCommonDtoList);
			}
		}
		StageDto stg = new StageDto();
		List<EventListDto> eventList = new ArrayList<>();
		stg = stageDao.getStageById(cnsrvtrshpRemovalReq.getIdStage());
		if ((ServiceConstants.CSTAGES_INV.equalsIgnoreCase(stg.getCdStage()))
				|| (ServiceConstants.CSTAGES_FRE.equalsIgnoreCase(stg.getCdStage()))
				|| (ServiceConstants.CSTAGES_FSU.equalsIgnoreCase(stg.getCdStage()))) {
			eventList = eventDao.getEventDtls(cnsrvtrshpRemovalReq.getIdStage(),
					ServiceConstants.CD_EVENT_TYPE_INV_CCL_TYPE);
		} else if ((ServiceConstants.CSTAGES_FPR).equalsIgnoreCase(stg.getCdStage())) {
			eventList = eventDao.getEventDtls(cnsrvtrshpRemovalReq.getIdStage(),
					ServiceConstants.CD_EVENT_TYPE_FPR_CCL_TYPE);
		}
		if (eventList.size() > 0
				&& (ServiceConstants.EVENT_STATUS_PENDING).equalsIgnoreCase(eventList.get(0).getCdEventStatus())) {
			rmvlEventOut.setIdEvent(eventList.get(0).getIdEvent());
		} else {
			rmvlEventOut.setIdEvent(ServiceConstants.Event_ID_0);
		}
		log.info(TRANSACTION_ID + cnsrvtrshpRemovalReq.getTransactionId());
		return rmvlEventOut;
	}

	/**
	 * Method Name: updateIdRmvlGroup Method Description: This method will
	 * update group Id for the removed children
	 * 
	 * @param CommonEventIdReq
	 * @return CommonHelperRes
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public CommonHelperRes updateIdRmvlGroup(CommonEventIdReq eventIdList) {
		CommonHelperRes res = new CommonHelperRes();
		res = cnsrvtrshpRemovalDao.updateIdRmvlGroup(eventIdList);
		return res;
	}

	/**
	 * Method Name: CVSRemovalAlert Method Description: This method will trigger
	 * alert in “CVS Removal” page
	 * 
	 * @param cnsrvtrshpRemovalAlertReq
	 * @return CnsrvtrshpRemovalAlertRes @
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public CnsrvtrshpRemovalAlertRes getAlertForCVSRemoval(CnsrvtrshpRemovalAlertReq cnsrvtrshpRemovalAlertReq) {
		CnsrvtrshpRemovalAlertRes res = new CnsrvtrshpRemovalAlertRes();
		res.setStatusResponse(cnsrvtrshpRemovalAlertDao.getAlertForCVSRemoval(cnsrvtrshpRemovalAlertReq.getIdStage(),
				cnsrvtrshpRemovalAlertReq.getCdStageProgram(), cnsrvtrshpRemovalAlertReq.getIdCase(),
				cnsrvtrshpRemovalAlertReq.getIdTodoEvent(), cnsrvtrshpRemovalAlertReq.getIdToDoPersCreator()));
		return res;
	}

	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public CommonBooleanRes babyMosesRemovalReasonExists(CommonHelperReq commonHelperReq) {
		CommonBooleanRes resp = null;
		if (commonHelperReq != null && commonHelperReq.getIdCase() != null) {
			resp = cnsrvtrshpRemovalDao.babyMosesRemovalReasonExists(commonHelperReq.getIdCase());
		}
		if (null != commonHelperReq) {
			log.info(TRANSACTION_ID + commonHelperReq.getTransactionId());
		}
		return resp;
	}

	/**
	 * 
	 * Method Name: fetchEmployeeEmail Method Description: This Method is used
	 * for fetching the primary and secondary case-worker's employee email
	 * addresses based on the event id
	 * 
	 * @param commonHelperReq
	 * @return String
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public String fetchEmployeeEmail(CommonHelperReq commonHelperReq) {
		EmployeeMailRes employeeMailRes = new EmployeeMailRes();
		String message = "";
		Map<Long, List<EmailDetailsDto>> emailDetailMap = new HashMap<Long, List<EmailDetailsDto>>();
		// Calling the dao method to get the email dto list
		List<EmailDetailsDto> emailDetailsDtoList = cnsrvtrshpRemovalDao
				.fetchEmployeeEmail(commonHelperReq.getIdEventList());
		// Generating a map of the email dto list having key as id event
		for (EmailDetailsDto emailDetailsDto : emailDetailsDtoList) {
			Long idEventKey = emailDetailsDto.getIdEvent();
			List<EmailDetailsDto> emailDtoList = new ArrayList<EmailDetailsDto>();
			for (EmailDetailsDto emailListDto : emailDetailsDtoList) {
				if (0 == idEventKey.compareTo(emailListDto.getIdEvent())) {
					emailDtoList.add(emailListDto);
				}
			}
			emailDetailMap.put(emailDetailsDto.getIdEvent(), emailDtoList);
		}
		// calling the Send appointment method to generate and send the
		// appointment to the users
		employeeMailRes.setReciepentDetail(emailDetailMap);
		log.info(TRANSACTION_ID + commonHelperReq.getTransactionId());
		message = sendAppointment(employeeMailRes, commonHelperReq.getDtCVSRemoval(), commonHelperReq.getHostName(),
				commonHelperReq.isIndReview());
		return message;
	}

	/**
	 * 
	 * Method Name: sendAppointment Method Description: This Method is used for
	 * Send The Appointment Invite
	 * 
	 * @param employeeMailRes
	 * @param dtCVSRemoval
	 * @param hostName
	 * @return String
	 * @throws InvalidRequestException
	 * @
	 */
	public String sendAppointment(EmployeeMailRes employeeMailRes, Date dtCVSRemoval, String hostName,
			boolean indReview) {
		// generating a array of string to store the service account Name and
		// the password
		// which is provided from the JNDI configuration
		String[] outLookSecurity = null;
		outLookSecurity = JNDIUtil.lookUp(OUTLOOKSECURITY).split(",");

		String outLookService = outLookSecurity[0];
		String outLookPassWord = "";
		try {
			// encrypt password string coming from JNDI
			outLookPassWord = JNDIUtil.decrypt(outLookSecurity[1]);
		} catch (EncryptionException e) {
			log.fatal(EXCEPTION_STRING_ONE + e.getMessage());
			// catching the exception if the JNDI is not configured and sending
			// the message to web
			return NO_JNDI;
		}

		// below loop is used to generate the multiple appointment DTO to be
		// sent to outlook util
		// to generate and trigger the appointment for multiple users
		for (Long idEvent : employeeMailRes.getReciepentDetail().keySet()) {
			AppointmentDto appointmentDto = new AppointmentDto();
			appointmentDto.setAppointmentBody("");
			Calendar cal = Calendar.getInstance();
			cal.setTime(dtCVSRemoval); // Now use removal date to
										// calculate the invite
										// time.
			if (!indReview) {
				cal.add(Calendar.DATE, 45);// Adding 45 day's to the
											// removal date
			}
			Date startDate = cal.getTime();
			// below logic is to generate the end time of the day
			cal.setTime(startDate);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 59);
			cal.set(Calendar.SECOND, 59);
			cal.set(Calendar.MILLISECOND, 59);
			Date endDate = cal.getTime();
			appointmentDto.setDtStartDate(startDate);
			appointmentDto.setDtEndDate(endDate);
			appointmentDto.setIndAllDayEvent(Boolean.TRUE);
			appointmentDto.setRemainderMins(10080);

			for (List<EmailDetailsDto> emailDetailsDtolist : employeeMailRes.getReciepentDetail().values()) {
				List<String> emailAddress = new ArrayList<>();
				for (EmailDetailsDto emailDetailsDto : emailDetailsDtolist) {
					if (idEvent.equals(emailDetailsDto.getIdEvent())) {
						if (indReview) {
							appointmentDto.setAppointmentSubject(SUBJECT_REVIEW + emailDetailsDto.getStageName());
						} else {
							appointmentDto.setAppointmentSubject(SUBJECT + emailDetailsDto.getStageName());
						}
						// below condition is added to prevent the mail
						// triggering to actual user during multiple testing
						// phase
						if (PROD.equals(hostName)) {
							emailAddress.add(emailDetailsDto.getEmailAddress());
						}
						// remove below conditional if test email address needs
						// to be removed
						else {
							String emailProperty = CVSREMOVAL_EMAIL_CONFIG_BASE + hostName;
							emailAddress.add(EMAIL_CONFIG_BUNDLE.getString(emailProperty));
						}
					}
				}
				appointmentDto.setReceiverEmailAddress(emailAddress);
			} // calling the outlook utility method to send the appointment
				// meeting also we are calling the outlookExchange service
				// method to get
				// the the Exchange service passing the service account userName
				// and Password to get the
				// service
			outlookUtil.sendAppointment(appointmentDto,
					outlookUtil.getOutlookExchangeService(outLookService, outLookPassWord));
		}
		return SUCCESS;
	}
}
