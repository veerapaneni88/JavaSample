package us.tx.state.dfps.service.notifications.serviceimpl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.dto.GenericCaseInfoDto;
import us.tx.state.dfps.populateletter.dto.CaseInfoDto;
import us.tx.state.dfps.populateletter.dto.CodesTablesDto;
import us.tx.state.dfps.service.admin.dao.EmployeeDao;
import us.tx.state.dfps.service.admin.dao.IncomingDetailStageDao;
import us.tx.state.dfps.service.admin.dao.NamePrimayEndDateDao;
import us.tx.state.dfps.service.admin.dao.StagePersonLinkRecordDao;
import us.tx.state.dfps.service.admin.dto.IncomingDetailStageInDto;
import us.tx.state.dfps.service.admin.dto.IncomingDetailStageOutDto;
import us.tx.state.dfps.service.admin.dto.NamePrimayEndDateInDto;
import us.tx.state.dfps.service.admin.dto.NamePrimayEndDateOutDto;
import us.tx.state.dfps.service.admin.dto.StagePersonLinkRecordInDto;
import us.tx.state.dfps.service.admin.dto.StagePersonLinkRecordOutDto;
import us.tx.state.dfps.service.casemanagement.dao.CaseMaintenanceFetchStageDtlDao;
import us.tx.state.dfps.service.casepackage.dto.RtrvStageInDto;
import us.tx.state.dfps.service.casepackage.dto.RtrvStageOutDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.NotificationsReq;
import us.tx.state.dfps.service.disasterplan.dao.DisasterPlanDao;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.forms.util.NotifToReporterPrefillData;
import us.tx.state.dfps.service.notification.dto.NotifToReporterDto;
import us.tx.state.dfps.service.notifications.service.NotifToReporterService;
import us.tx.state.dfps.service.person.dao.PersonDao;
import us.tx.state.dfps.service.person.dto.PersonListDto;
import us.tx.state.dfps.service.populateletter.dao.PopulateLetterDao;
import us.tx.state.dfps.service.workload.dto.EmployeePersPhNameDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:
 * Notification To Reporter, Tuxedo Service: cint32s May 25, 2018- 9:38:45 AM ©
 * 2017 Texas Department of Family and Protective Services
 */
@Service
@Transactional
public class NotifToReporterServiceImpl implements NotifToReporterService {

	@Autowired
	MessageSource messageSource;

	@Autowired
	PopulateLetterDao populateLetterDao;

	@Autowired
	EmployeeDao employeeDao;

	@Autowired
	CaseMaintenanceFetchStageDtlDao caseMaintenanceFetchStageDtlDao;

	@Autowired
	StagePersonLinkRecordDao stagePersonLinkRecordDao;

	@Autowired
	PersonDao personDao;

	@Autowired
	IncomingDetailStageDao incomingDetailStageDao;

	@Autowired
	NamePrimayEndDateDao namePrimayEndDateDao;

	@Autowired
	DisasterPlanDao disasterPlanDao;

	@Autowired
	NotifToReporterPrefillData notifToReporterPrefillData;

	private static final Logger log = Logger.getLogger(NotifToReporterServiceImpl.class);

	/**
	 * 
	 * Method Name: getNotificationsService Method Description:This service will
	 * return the data needed for the form letter to reporter (cfin0900
	 * Notification to Reporter).
	 * 
	 * @param NotificationsReq
	 * @return PreFillDataServiceDto @
	 */
	@Override
	public PreFillDataServiceDto getNotificationsService(NotificationsReq notificationsReq) {

		NotifToReporterDto notifToReporter = new NotifToReporterDto();
		List<Long> idVictims = new ArrayList<Long>();
		List<NamePrimayEndDateOutDto> namePrimaryEndDateOutDto = new ArrayList<NamePrimayEndDateOutDto>();
		List<CaseInfoDto> caseInfoDtoList = new ArrayList<CaseInfoDto>();
		// CallCSES71D

		RtrvStageInDto rtrvStageInDto = new RtrvStageInDto();
		RtrvStageOutDto rtrvStageOutDto = new RtrvStageOutDto();

		rtrvStageInDto.setIdStage(notificationsReq.getIdStage());
		caseMaintenanceFetchStageDtlDao.fetchStageDtl(rtrvStageInDto, rtrvStageOutDto);
		notifToReporter.setRtrvStageOutDto(rtrvStageOutDto);

		/*
		 ** Get the Intake info needed for the form letter. Info needed is
		 * dtDtIncomingCall [[REP_CALL_DATE]] and ulIdStage [[REP_INTAKE_NUM]]
		 */
		// CINT07D

		IncomingDetailStageInDto incomingDetailStageInDto = new IncomingDetailStageInDto();
		incomingDetailStageInDto.setIdStage(notificationsReq.getIdStage());
		List<IncomingDetailStageOutDto> incomingDetailStageOutDtoList = incomingDetailStageDao
				.getIncomingDetail(incomingDetailStageInDto);

		notifToReporter.setIncomingDetailStageOutDtoList(incomingDetailStageOutDtoList);
		/*
		 ** CSEC01D gets the name of the worker who launched the notification.
		 * Worker's ID is in pInputMsg->ulIdEventPerson.
		 */

		EmployeePersPhNameDto employeePersPhNameDto = employeeDao
				.searchPersonPhoneName(notificationsReq.getIdEventPerson());

		notifToReporter.setEmployeePersPhNameDto(employeePersPhNameDto);
		/*
		 ** Dam to retrieve 50 rows at a time from the STAGE_PERSON_LINK table
		 * given an ID_STAGE.
		 **
		 ** NOTE fraserkr, 02/15/08 - Names of Victims no longer output on letter
		 * (this service did not need modification for that change).
		 */
		// CINV34D
		List<PersonListDto> personDtoList = personDao.getPersonListByIdStage(notificationsReq.getIdStage(),
				ServiceConstants.STAFF_PERSON);
		notifToReporter.setPersonDtoList(personDtoList);

		/*
		 ** Sort returned rows into temporary array
		 */
		// Warranty Defect - 12419 - Changed the Array to ArrayList logic which was causing the Null Pointer Exception
		if (!ObjectUtils.isEmpty(personDtoList)) {
			
			for (PersonListDto person : personDtoList) {
				if (ServiceConstants.ALLEGED_VICTIM.regionMatches(0, person.getStagePersRole(), 0,
						ServiceConstants.TWO_INT)
						|| ServiceConstants.ALLEGED_VICTIM_PERP.regionMatches(0, person.getStagePersRole(), 0,
								ServiceConstants.TWO_INT)
						|| ServiceConstants.DESIGNATED_VICTIM.regionMatches(0, person.getStagePersRole(), 0,
								ServiceConstants.TWO_INT)
						|| ServiceConstants.DESIGNATED_VICTIM_PERP.regionMatches(0, person.getStagePersRole(), 0,
								ServiceConstants.TWO_INT)) {					
					idVictims.add(person.getIdPerson());				
				}
			}

			/*
			 ** DAM to retrieve a person's first, middle and last name from the
			 * NAME table given an ID_PERSON.
			 */
			// CallCCMN40D

			for (Long idVictimPerson:idVictims) {
				/*
				 ** Call DAM to retrieve name of the i'th Victim
				 */
				NamePrimayEndDateInDto namePrimayEndDateInDto = new NamePrimayEndDateInDto();
				namePrimayEndDateInDto.setIdPerson(idVictimPerson);
				namePrimaryEndDateOutDto = namePrimayEndDateDao.getFullName(namePrimayEndDateInDto);

				if (ObjectUtils.isEmpty(namePrimaryEndDateOutDto)) {
					NamePrimayEndDateOutDto name = new NamePrimayEndDateOutDto();
					name.setNmNameFirst(ServiceConstants.UNKNOWN_FIRST_NAME);
					name.setNmNameLast(ServiceConstants.UNKNOWN_LAST_NAME);
					namePrimaryEndDateOutDto.add(name);
				}
			}

			notifToReporter.setNamePrimaryEndDateOutDto(namePrimaryEndDateOutDto);
		}

		if (!ObjectUtils.isEmpty(notificationsReq.getIdPerson())) {
			/*
			 * CINV39D used to retrieves a row from STAGE_PERSON_LINK to
			 * determine if the person passed to this service is school
			 * personnel.
			 */
			StagePersonLinkRecordInDto stagePersonLinkRecordInDto = new StagePersonLinkRecordInDto();
			stagePersonLinkRecordInDto.setIdPerson(notificationsReq.getIdPerson());
			stagePersonLinkRecordInDto.setIdStage(notificationsReq.getIdStage());
			List<StagePersonLinkRecordOutDto> stageRecDto = stagePersonLinkRecordDao
					.getStagePersonLinkRecord(stagePersonLinkRecordInDto);

			notifToReporter.setStageRecDto(stageRecDto);
			/*
			 ** Since the person is a reporter, call CSEC18D to retrieve the
			 * reporter's address from PERSON_ADDRESS.
			 */

			for (StagePersonLinkRecordOutDto stagePerson : stageRecDto) {
				caseInfoDtoList = populateLetterDao.getReporterInfoById(notificationsReq.getIdStage());

				/*
				 ** Populate output message. Loop through all of the rows
				 ** returned from the DAM, and copy the address of the reporter
				 ** into the appropriate variables to be passed back to the
				 ** calling window.
				 */

				if (!ObjectUtils.isEmpty(caseInfoDtoList)) {
					notifToReporter.setCaseInfoDto(caseInfoDtoList.stream().filter(caseInfo->notificationsReq.getIdPerson().equals(caseInfo.getIdPerson())).findFirst().get());
				}
			}
		}

		/*
		 * retrieves letter head info CLSC03D to retrieve the board members and
		 * the executive director information for the header.
		 */
		CodesTablesDto codesTablesDto = populateLetterDao
				.getPersonInfoByCode(ServiceConstants.BOARD_TITLE_CODESTABLE, ServiceConstants.BOARD_NAME_CODESTABLE)
				.get(0);

		notifToReporter.setCodesTablesDto(codesTablesDto);

		/*
		 * retrieves stage and caps_case table provides dtSysDtGenericSysdate
		 * [[REP_CURRENT_DATE]] and szNmCase [[REP_CASE_NAME]]
		 */

		GenericCaseInfoDto genericCaseInfoDto = disasterPlanDao.getGenericCaseInfo(notificationsReq.getIdStage());

		notifToReporter.setGenericCaseInfoDto(genericCaseInfoDto);
		// CallCINTA1D
		
		if(!ObjectUtils.isEmpty(notificationsReq.getIdCase())&&
				!ObjectUtils.isEmpty(notificationsReq.getIdPerson()))
		{
		
		Long addressMatchId = populateLetterDao.getAddressMatchById(notificationsReq.getIdCase(),
				notificationsReq.getIdPerson());

		notifToReporter.setAddressMatchId(addressMatchId);
		}
		/*
		 * Here is where I pass the cd_stage_reason_closed to dam cses71d to
		 * determine which of the sub-templates should be called. I needed to
		 * also pass the name of the jurisdiction to the dam also which was
		 * retrieved by cint07d.
		 */

		/*
		 * Clear the stage reason closed field prior to moving the reason on the
		 * window to the field
		 */
		rtrvStageOutDto.setCdStageReasonClosed(ServiceConstants.CLEAR);
		rtrvStageOutDto.setTxtStageClosureCmnts(ServiceConstants.CLEAR);

		rtrvStageOutDto.setTxtStageClosureCmnts(incomingDetailStageOutDtoList.get(0).getNmJurisdiction());

		/* look into checking the reason closed passed in from the window */

		if (ServiceConstants.CD_REASON_CLOSED_NO_ABUSE.equals(notificationsReq.getCdEventType())
				|| ServiceConstants.CD_REASON_CLOSED_OUT_OF_STATE.equals(notificationsReq.getCdEventType())) {
			rtrvStageOutDto.setCdStageReasonClosed(ServiceConstants.LET_REP2);
		} else {
			rtrvStageOutDto.setCdStageReasonClosed(notificationsReq.getCdEventType());
		}

		return notifToReporterPrefillData.returnPrefillData(notifToReporter);
	}

}
