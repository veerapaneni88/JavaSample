package us.tx.state.dfps.service.cpsintakereport.serviceimpl;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.beanutils.converters.DateConverter;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.util.ObjectUtils;
import us.tx.state.dfps.common.domain.IncmgDetermFactors;
import us.tx.state.dfps.common.dto.ApprovalDto;
import us.tx.state.dfps.common.dto.IncomingStageDetailsDto;
import us.tx.state.dfps.common.dto.NameDto;
import us.tx.state.dfps.common.dto.PhoneInfoDto;
import us.tx.state.dfps.common.dto.PriorityChangeInfoDto;
import us.tx.state.dfps.common.dto.WorkerInfoDto;
import us.tx.state.dfps.service.admin.dao.EmployeeDao;
import us.tx.state.dfps.service.admin.dao.FetchEventDetailDao;
import us.tx.state.dfps.service.admin.dto.FetchEventAdminDto;
import us.tx.state.dfps.service.admin.dto.FetchEventDetailDto;
import us.tx.state.dfps.service.casemanagement.dao.CpsIntakeNotificationDao;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.CpsIntakeReportReq;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.cpsintakereport.dao.CpsIntakeReportDao;
import us.tx.state.dfps.service.cpsintakereport.service.CpsIntakeReportService;
import us.tx.state.dfps.service.cpsintakereportform.dto.CpsIntakeReportFormDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.forms.util.CpsIntakeReportFormPrefillData;
import us.tx.state.dfps.service.intake.dao.IncmgDtrmFactorsDao;
import us.tx.state.dfps.service.intake.dto.IncmgDetermFactorsDto;
import us.tx.state.dfps.service.lookup.dao.LookupDao;
import us.tx.state.dfps.service.person.dao.PersonDao;
import us.tx.state.dfps.service.person.dto.IntakeAllegationDto;
import us.tx.state.dfps.service.person.dto.PersonAddrLinkDto;
import us.tx.state.dfps.service.person.dto.PersonListEventDto;
import us.tx.state.dfps.service.workload.dto.EmployeePersPhNameDto;
import us.tx.state.dfps.service.workload.dto.PersonDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:
 * CpsIntakeReportServiceImpl will implemented all operation defined in
 * CpsIntakeReportService Interface related CpsIntakeReportService module. Feb
 * 9, 2018- 2:01:28 PM Service Name: CINT39S - CPS INTAKE REPORT © 2017 Texas
 * Department of Family and Protective Services
 *  * **********  Change History *********************************
 * 09/28/2020 thompswa artf164166 : Use incoming_detail when Reporter Rel-Int is FPS Staff
 * 04/06/2021 olivea3  artf176887 Priority Change Comments use txtStagePriorityCmnts : defect 16905
 * 04/09/2024 thompswa artf261922 : Reporter Email to display on the Intake Report Form
 * 08/26/2024 thompswa artf268014 : BR 40.2 Intake Report Form Question
 */

@Service
@Transactional
public class CpsIntakeReportServiceImpl implements CpsIntakeReportService {

	@Autowired
	CpsIntakeReportDao cpsIntakeReportDao;

	@Autowired
	CpsIntakeNotificationDao cpsIntakeNotificationDao;

	@Autowired
	PersonDao personDao;

	@Autowired
	IncmgDtrmFactorsDao incmgDtrmFactorsDao;

	@Autowired
	FetchEventDetailDao fetchEventDetailDao;

	@Autowired
	EmployeeDao employeeDao;

	@Autowired
	LookupDao lookupDao;

	@Autowired
	CpsIntakeReportFormPrefillData cpsIntakeReportFormPrefillData;

	private static final Logger log = Logger.getLogger(CpsIntakeReportServiceImpl.class);

	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRES_NEW)
	public PreFillDataServiceDto getCpsIntakeReport(CpsIntakeReportReq cpsIntakeReportReq) {

		Long idEvent = null;
		Long idEventPerson = null;
		CpsIntakeReportFormDto cpsIntakeReportFormDto = new CpsIntakeReportFormDto();

		/**
		 * CINT65D
		 */
		IncomingStageDetailsDto incomingStageDetailsDto = cpsIntakeReportDao
				.getStageIncomingDetails(cpsIntakeReportReq.getIdStage());
		decodeValueIncmgStageDetails(incomingStageDetailsDto);
		cpsIntakeReportFormDto.setIncomingStageDetailsDto(incomingStageDetailsDto);

		if (!TypeConvUtil.isNullOrEmpty(incomingStageDetailsDto)
				&& !TypeConvUtil.isNullOrEmpty(incomingStageDetailsDto.getIdEvent())) {
			idEvent = incomingStageDetailsDto.getIdEvent();
		}

		// Get the victims CINT66D

		List<PersonDto> victims = cpsIntakeReportDao.getPersonList(cpsIntakeReportReq.getIdStage(),
				ServiceConstants.VICTIM_TYPE);
		cpsIntakeReportFormDto.setVictims(victims);

		// Get the perpetrators CINT66D

		List<PersonDto> perpetrators = cpsIntakeReportDao.getPersonList(cpsIntakeReportReq.getIdStage(),
				ServiceConstants.PERPETRATOR_TYPE);
		cpsIntakeReportFormDto.setPerpetrators(perpetrators);

		// Get other principles CINT66D

		List<PersonDto> otherPrinciples = cpsIntakeReportDao.getPersonList(cpsIntakeReportReq.getIdStage(),
				ServiceConstants.OTHER_PRN_TYPE);
		cpsIntakeReportFormDto.setOtherPrinciples(otherPrinciples);

		// Get the reporter CINT66D
		List<PersonDto> reporters = new ArrayList<PersonDto>(); 
		reporters = cpsIntakeReportDao.getPersonList(cpsIntakeReportReq.getIdStage(), ServiceConstants.REPORTER_TYPE);
		/**
		 * NOTE : public method getFpsStaffReporter called by all Programs' Intake Report forms artf164166
		 */
		reporters = getFpsStaffReporter(reporters, incomingStageDetailsDto);
		cpsIntakeReportFormDto.setReporters(reporters);

		// Get Collaterals CINT66D
		List<PersonDto> collaterals = cpsIntakeReportDao.getPersonList(cpsIntakeReportReq.getIdStage(),
				ServiceConstants.COLLATERAL_TYPE);
		cpsIntakeReportFormDto.setCollaterals(collaterals);

		// get all the phone numbers for all persons associated with the
		// stageID. CINT62D

		List<PhoneInfoDto> personPhoneList = cpsIntakeReportDao.getPhoneInfo(cpsIntakeReportReq.getIdStage());
		cpsIntakeReportFormDto.setPersonPhoneDtoList(personPhoneList);

		// get all the addresses numbers for all persons associated with the
		// stageID. // CINT63D
		List<PersonAddrLinkDto> personAddrLinkDtoList = cpsIntakeNotificationDao
				.getAddrInfoByStageId(cpsIntakeReportReq.getIdStage());
		cpsIntakeReportFormDto.setPersonAddrLinkDtoList(personAddrLinkDtoList);

		// Call the facility DAM to get all allegations associated with the
		// stageID //CINT19D
		List<IntakeAllegationDto> intakeAllegationDtoList = personDao
				.getIntakeAllegationByStageId(cpsIntakeReportReq.getIdStage());
		decodeAllegation(intakeAllegationDtoList);
		cpsIntakeReportFormDto.setIntakeAllegationDtoList(intakeAllegationDtoList);

		// get all determining factors associated with the stageID //CINT15D
		List<IncmgDetermFactors> incmgDetermFactors = incmgDtrmFactorsDao
				.getincmgDetermFactorsById(cpsIntakeReportReq.getIdStage());
		List<IncmgDetermFactorsDto> incmgDetermFactorsDtoList = new ArrayList<IncmgDetermFactorsDto>();

		try {
			for (IncmgDetermFactors factors : incmgDetermFactors) {
				IncmgDetermFactorsDto incmgDetermFactorsDto = new IncmgDetermFactorsDto();
				copyPropertiesWithNullDate(factors, incmgDetermFactorsDto);
				// BeanUtils.copyProperties(incmgDetermFactorsDto, factors);
				incmgDetermFactorsDtoList.add(incmgDetermFactorsDto);
			}
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			log.error(e.getMessage());
		}
		decodeIncmgDeterm(incmgDetermFactorsDtoList);
		cpsIntakeReportFormDto.setIncmgDetermFactors(incmgDetermFactorsDtoList);

		// get all of the aliases for all persons associated with the stageID.

		List<NameDto> nameAliasList = cpsIntakeNotificationDao.getNameAliases(cpsIntakeReportReq.getIdStage());
		cpsIntakeReportFormDto.setNameAliasList(nameAliasList);

		/**
		 ** Call the Event dam, so the event status can be used. associated with
		 * the EventID.
		 */
		if (null != idEvent) {
			FetchEventAdminDto pInputDataRec = new FetchEventAdminDto();
			pInputDataRec.setIdEvent(idEvent);
			List<FetchEventDetailDto> eventDtoList = new ArrayList<>();
			FetchEventDetailDto eventDto = fetchEventDetailDao.getEventDetail(pInputDataRec);
			eventDtoList.add(eventDto);
			cpsIntakeReportFormDto.setEventDtoList(eventDtoList);

			/**
			 ** Get the worker information associated with the stageID for the
			 * APPROVED event. CLSC52D
			 */
			List<ApprovalDto> approvalListDto = cpsIntakeReportDao.getApprovalList(idEvent);
			cpsIntakeReportFormDto.setApprovalDtoList(approvalListDto);
		}

		/*
		 ** Get the worker information associated with the stageID for the
		 * CHANGED/CLOSED event. CINT67D
		 */
		List<WorkerInfoDto> workerInfoDtoList = cpsIntakeReportDao.getWorkerInfo(cpsIntakeReportReq.getIdStage(),
				ServiceConstants.CHANGED_CODE);
		cpsIntakeReportFormDto.setWorkerInfoDtoList(workerInfoDtoList);

		// retrieves history of priority change information CINT68D
		List<PriorityChangeInfoDto> priorityChangeInfoDtoList = cpsIntakeReportDao
				.getPriorityChangeInfo(cpsIntakeReportReq.getIdStage());
		/** artf176887 defect 16905: for first (most-recent) Priority Change instance,
		 *  use txtStagePriorityCmnts from Stage to create the HIS_PRIORITY_EXPL Bookmark
		 */
		if (!ObjectUtils.isEmpty(priorityChangeInfoDtoList)) {
		priorityChangeInfoDtoList.get(0).setTxtStagePriorityCmnts(
				incomingStageDetailsDto.getTxtStagePriorityCmnts());
		}
		cpsIntakeReportFormDto.setPriorityChangeInfoDtoList(priorityChangeInfoDtoList);

		/** Get all of FSQ events associated with the stageID. */
		List<PersonListEventDto> personListEventDtos = personDao.getPersonListEvent(cpsIntakeReportReq.getIdStage(),
				ServiceConstants.FSQ_EVENT_TYPE);
		cpsIntakeReportFormDto.setPersonListEventDtos(personListEventDtos);

		for (PersonListEventDto personListEventDto : personListEventDtos) {
			if (personListEventDto.getEventDescr().indexOf(ServiceConstants.FORMALLY_SCREENED) != -1) {
				idEventPerson = personListEventDto.getIdEventPerson();
				break;
			}
		}
		/** call CSEC01D */
		if (null != idEventPerson) {
			EmployeePersPhNameDto employeePersPhNameDto = employeeDao.searchPersonPhoneName(idEventPerson);
			cpsIntakeReportFormDto.setEmployeePersPhNameDto(employeePersPhNameDto);
		}

		/** artf268014 previous open case family history */
		cpsIntakeReportFormDto.setShowScreener(ServiceConstants.TRUE);
		/** if all the indicators are null, hide the screener questions */
		if (TypeConvUtil.isNullOrEmpty(incomingStageDetailsDto.getIndFoundOpenCase())
				&& TypeConvUtil.isNullOrEmpty(incomingStageDetailsDto.getIndFoundOpenCaseAtIntake())
				&& TypeConvUtil.isNullOrEmpty(incomingStageDetailsDto.getIndFormallyScreened())
		) cpsIntakeReportFormDto.setShowScreener(ServiceConstants.FALSE);
		/** but show screener if there was a child under six */
		if (null != victims && 0 < victims.size()) {
			boolean victimAge6AndAbove = false;
			for ( PersonDto dto : victims) {
				if (StringUtils.isNotBlank(dto.getCdStagePersRole())
						&& (dto.getCdStagePersRole().equalsIgnoreCase(CodesConstant.CROLEALL_VC)
						|| dto.getCdStagePersRole().equalsIgnoreCase(CodesConstant.CROLEALL_VP))) {
					Long age = 0L; //artf158598
					if(dto.getNbrPersonAge() != null) age = Long.valueOf(dto.getNbrPersonAge());
					//artf199334
					if (age != 0 && age > 5 ) {
						victimAge6AndAbove = true;
					} else {
						victimAge6AndAbove = false;
						break;
					}
				}
			}
			if (! victimAge6AndAbove) cpsIntakeReportFormDto.setShowScreener(ServiceConstants.TRUE);
		} /**  end artf268014 */
		return cpsIntakeReportFormPrefillData.returnPrefillData(cpsIntakeReportFormDto);
	}

	private void decodeIncmgDeterm(List<IncmgDetermFactorsDto> incmgDetermFactorsDtoList) {
		if (!TypeConvUtil.isNullOrEmpty(incmgDetermFactorsDtoList)) {
			for (IncmgDetermFactorsDto factors : incmgDetermFactorsDtoList) {
				if (StringUtils.isNotBlank(factors.getCdIncmgDeterm())) {
					String incmgDeterm = lookupDao.decode(ServiceConstants.CDETFACT, factors.getCdIncmgDeterm());
					factors.setCdIncmgDeterm(incmgDeterm);
				}
			}
		}

	}

	public void copyPropertiesWithNullDate(Object src, Object dest)
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Date defaultValue = null;
		Converter converter = new DateConverter(defaultValue);
		BeanUtilsBean beanUtilsBean = BeanUtilsBean.getInstance();
		beanUtilsBean.getConvertUtils().register(converter, java.util.Date.class);
		BeanUtils.copyProperties(dest, src);
	}

	private void decodeAllegation(List<IntakeAllegationDto> intakeAllegationDtoList) {
		if (!TypeConvUtil.isNullOrEmpty(intakeAllegationDtoList)) {
			for (IntakeAllegationDto inAlleg : intakeAllegationDtoList) {
				if (StringUtils.isNotBlank(inAlleg.getCdIntakeAllegType())) {
					String allegType = lookupDao.decode(ServiceConstants.CCLICALT, inAlleg.getCdIntakeAllegType());
					inAlleg.setCdIntakeAllegType(allegType);
				}
			}
		}
	}
	/**
	 *
	 *Method Name:	getFpsStaffReporter  merged with get incoming person reporter
	 *Method Description: artf164166 If incoming details has FPS Staff reporter add from incoming details
	 *                    if there is not already FPS Staff reporter from stage_person_link
	 *                    artf261922  get txtEmail from incoming_person if not fps staff
	 *                    NOTE : this public method is used by all Programs' Intake Report forms
	 *@param reporters
	 *@param incomingStageDetailsDto
	 *@return returnList
	 */
	public List<PersonDto> getFpsStaffReporter(List<PersonDto> reporters, IncomingStageDetailsDto incomingStageDetailsDto) {
		List<PersonDto> returnPersonDtoList  = new ArrayList<>();
        // artf261922 get incoming_person reporter-email
		if (!ObjectUtils.isEmpty(reporters) && 0 < reporters.size()) {
			for (PersonDto rptr : reporters) {
				if (!CodesConstant.CPRSNALL_STF.equals(rptr.getCdStagePersType())) {
					String txtEmail = cpsIntakeReportDao.getIncomingPersonTxtEmail(rptr.getIdPerson(), rptr.getIdStage());
					if ( 0 < txtEmail.length()) {
						rptr.setTxtEmail(txtEmail);
					}
				}
				returnPersonDtoList.add(rptr);
			}
		}
		List<PersonDto> returnList = returnPersonDtoList;
		/**
		 * artf164166 add the staff reporter from incoming_detail
		 */
		List<PersonDto> tempReportersList = new ArrayList<>();
		int nbrFpsReporters = 0;

		// skip to return if incoming detail does not include the fps staff reporter
		if ( !TypeConvUtil.isNullOrEmpty(incomingStageDetailsDto.getCdIncmgCallerInt())
				&& ServiceConstants.CRPTRINT_SF.equals(incomingStageDetailsDto.getCdIncmgCallerInt())){
			tempReportersList = returnPersonDtoList.stream()
					.filter(p -> !p.getCdStagePersRelInt().equals(CodesConstant.CRPTRINT_SF)).collect(Collectors.toList());
			//add fps staff, but older cases have added the staff to stage_person_link, so don't add again
			nbrFpsReporters = (returnPersonDtoList.size() - tempReportersList.size());
			if ( nbrFpsReporters < 1 ){
				PersonDto reporter = new PersonDto();
				reporter.setIdPerson(FPS_STAFF_REPORTER);
				reporter.setNmPersonFirst(incomingStageDetailsDto.getNmIncomingCallerFirst());
				reporter.setNmPersonMiddle(incomingStageDetailsDto.getNmIncomingCallerMiddle());
				reporter.setNmPersonLast(incomingStageDetailsDto.getNmIncomingCallerLast());
				reporter.setCdPersonSuffix(incomingStageDetailsDto.getCdIncomingCallerSuffix());
				reporter.setCdStagePersRelInt(incomingStageDetailsDto.getCdIncmgCallerInt());
				reporter.setTxtStagePersNotes(incomingStageDetailsDto.getTxtReporterNotes());
				reporter.setTxtEmail(incomingStageDetailsDto.getTxtReporterEmail());
				tempReportersList.add(reporter);
				returnList = tempReportersList;
			}
		}
		return returnList;
	}

	private void decodeValueIncmgStageDetails(IncomingStageDetailsDto incomingStageDetailsDto) {
		if (!TypeConvUtil.isNullOrEmpty(incomingStageDetailsDto)) {
			if (StringUtils.isNotBlank(incomingStageDetailsDto.getCdIncmgAllegType())) {
				String allegType = lookupDao.decode(ServiceConstants.CCLICALT,
						incomingStageDetailsDto.getCdIncmgAllegType());
				incomingStageDetailsDto.setCdIncmgAllegType(allegType);
			}
			if (StringUtils.isNotBlank(incomingStageDetailsDto.getCdIncmgSpecHandling())) {
				String incmgSpecial = lookupDao.decode(ServiceConstants.CSPECHND,
						incomingStageDetailsDto.getCdIncmgSpecHandling());
				incomingStageDetailsDto.setCdIncmgSpecHandling(incmgSpecial);
			}
			if (StringUtils.isNotBlank(incomingStageDetailsDto.getCdStageCurrPriority())) {
				String currPrior = lookupDao.decode(ServiceConstants.CPRIORTY,
						incomingStageDetailsDto.getCdStageCurrPriority());
				incomingStageDetailsDto.setCdStageCurrPriority(currPrior);
			}
			if (StringUtils.isNotBlank(incomingStageDetailsDto.getCdStageCurrPriority())) {
				String currStagePrior = lookupDao.decode(ServiceConstants.CPRIORTY,
						incomingStageDetailsDto.getCdStageCurrPriority());
				incomingStageDetailsDto.setCdStageCurrPriority(currStagePrior);
			}
			if (StringUtils.isNotBlank(incomingStageDetailsDto.getCdStageInitialPriority())) {
				String currStageInitialPrior = lookupDao.decode(ServiceConstants.CPRIORTY,
						incomingStageDetailsDto.getCdStageInitialPriority());
				incomingStageDetailsDto.setCdStageInitialPriority(currStageInitialPrior);
			}
			if (StringUtils.isNotBlank(incomingStageDetailsDto.getCdStageReasonClosed())
					&& !(ServiceConstants.CCLOSUR1_02.equalsIgnoreCase(incomingStageDetailsDto.getCdStageReasonClosed())
							|| ServiceConstants.CCLOSUR1_01
									.equalsIgnoreCase(incomingStageDetailsDto.getCdStageReasonClosed()))) {
				String stageRsnClosed = lookupDao.decode(ServiceConstants.CCLOSUR1,
						incomingStageDetailsDto.getCdStageReasonClosed());
				incomingStageDetailsDto.setCdStageReasonClosed(stageRsnClosed);
			} else {
				incomingStageDetailsDto.setCdStageReasonClosed(ServiceConstants.EMPTY_STR);
			}
		}
	}
}
