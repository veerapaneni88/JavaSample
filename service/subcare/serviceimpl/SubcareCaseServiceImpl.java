package us.tx.state.dfps.service.subcare.serviceimpl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.populateletter.dto.CaseInfoDto;
import us.tx.state.dfps.service.admin.dto.PlacementActPlannedOutDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.FacilityInvSumReq;
import us.tx.state.dfps.service.forms.dao.SubcareLOCFormDao;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.forms.util.SubcareCasePrefillData;
import us.tx.state.dfps.service.person.dto.PersonIdDto;
import us.tx.state.dfps.service.placement.dao.CommonApplicationDao;
import us.tx.state.dfps.service.placement.dao.PersonIdDtlsDao;
import us.tx.state.dfps.service.placement.dto.PersonLocDto;
import us.tx.state.dfps.service.placement.dto.StagePersonLinkCaseDto;
import us.tx.state.dfps.service.populateletter.dao.PopulateLetterDao;
import us.tx.state.dfps.service.subcare.dao.SubcareCaseDao;
import us.tx.state.dfps.service.subcare.dto.SubcareCaseDto;
import us.tx.state.dfps.service.subcare.dto.SubcareChildContactDto;
import us.tx.state.dfps.service.subcare.dto.SubcareLegalEnrollDto;
import us.tx.state.dfps.service.subcare.service.SubcareCaseService;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This service
 * will populate the Subcare Case Reading Tool.CSUB79S May 9, 2018- 9:26:35 AM ©
 * 2017 Texas Department of Family and Protective Services
 */
@Service
@Transactional
public class SubcareCaseServiceImpl implements SubcareCaseService {

	@Autowired
	private SubcareCaseDao subcareCaseDao;

	@Autowired
	private CommonApplicationDao commonApplicationDao;

	@Autowired
	private SubcareLOCFormDao subcareLOCFormDao;

	@Autowired
	private PersonIdDtlsDao personIdDtlsDao;

	@Autowired
	private PopulateLetterDao populateLetterDao;

	@Autowired
	private SubcareCasePrefillData subcareCasePrefillData;

	/**
	 * Method Name: getSubcareCase Method Description: Populates form csc40o00,
	 * which Populates the Subcare Case Reading Tool.
	 * 
	 * @return PreFillDataServiceDto
	 */
	@Override
	public PreFillDataServiceDto getSubcareCase(FacilityInvSumReq facilityInvSumReq) {

		SubcareCaseDto subcareCaseDto = new SubcareCaseDto();
		/*
		 ** Call DAM CSEC15D to get a full row from the person table for the
		 * primary child using id stage.
		 */

		StagePersonLinkCaseDto stagePersonLinkCaseDto = commonApplicationDao
				.getStagePersonCaseDtl(facilityInvSumReq.getIdStage(), ServiceConstants.PRIMARY_CHILD);
		subcareCaseDto.setStagePersonLinkCaseDto(stagePersonLinkCaseDto);
		/*
		 ** Set dummy fields to NULL to ensure that no data appears on the form
		 * in the YES, NO, or UNKNOWN bookmarks for the dummy fields.
		 */
		stagePersonLinkCaseDto.setCdStage(null);
		stagePersonLinkCaseDto.setCdStageProgram(null);
		stagePersonLinkCaseDto.setCdStageType(null);
		if (!ObjectUtils.isEmpty(stagePersonLinkCaseDto.getIdPerson())) {

			/*
			 ** Call DAM CCMN72D to get the Social Security Number of the primary
			 * child based on the id person from CSEC15D.
			 */

			PersonIdDto personIdDtoSsn = subcareLOCFormDao.getMedicaidNbrByPersonId(
					stagePersonLinkCaseDto.getIdPerson(), ServiceConstants.SOCIAL_SECURITY,
					ServiceConstants.SPL_REQ_TYPE_N, ServiceConstants.MAX_DATE);
			subcareCaseDto.setPersonIdDtoSsn(personIdDtoSsn);
			/*
			 ** Call DAM CCMN72D to get the Medicaid Number of the primary child
			 * based on the id person from CSEC15D.
			 */

			PersonIdDto personIdDtoMed = subcareLOCFormDao.getMedicaidNbrByPersonId(
					stagePersonLinkCaseDto.getIdPerson(), ServiceConstants.CNUMTYPE_MEDICAID_NUMBER,
					ServiceConstants.SPL_REQ_TYPE_N, ServiceConstants.MAX_DATE);
			subcareCaseDto.setPersonIdDtoMed(personIdDtoMed);
			/*
			 ** Call DAM CLSS64D to get any "PMC/Rts Term (Mother)" Legal Status
			 ** events
			 */
			SubcareLegalEnrollDto subcareLegalEnrollDtoMom = subcareCaseDao
					.getStatusDeterm(stagePersonLinkCaseDto.getIdPerson(), ServiceConstants.CLOCMAP_070);
			subcareCaseDto.setSubcareLegalEnrollDtoMom(subcareLegalEnrollDtoMom);
			/*
			 ** Call DAM CLSS64D to get any "PMC/Rts Last Father" Legal Status
			 ** events
			 */
			SubcareLegalEnrollDto subcareLegalEnrollDtoDad = subcareCaseDao
					.getStatusDeterm(stagePersonLinkCaseDto.getIdPerson(), ServiceConstants.LEVEL_CARE_50);
			subcareCaseDto.setSubcareLegalEnrollDtoDad(subcareLegalEnrollDtoDad);
			/*
			 ** Call DAM CLSS64D to get any "PMC/Rts Term (All)" Legal Status
			 * events
			 */

			SubcareLegalEnrollDto subcareLegalEnrollDtoAll = subcareCaseDao
					.getStatusDeterm(stagePersonLinkCaseDto.getIdPerson(), ServiceConstants.CPLMNTYP_040);
			subcareCaseDto.setSubcareLegalEnrollDtoAll(subcareLegalEnrollDtoAll);
			/*
			 ** Call DAM CLSS64D to get any "TMC" Legal Status events
			 */

			SubcareLegalEnrollDto subcareLegalEnrollDtoTmc = subcareCaseDao
					.getStatusDeterm(stagePersonLinkCaseDto.getIdPerson(), ServiceConstants.CLEGSTAT_020);
			subcareCaseDto.setSubcareLegalEnrollDtoTmc(subcareLegalEnrollDtoTmc);
			/*
			 ** Call DAM CSES34D to get the placement information
			 */
			PlacementActPlannedOutDto placementActPlannedOutDto = personIdDtlsDao
					.getPlacementRecord(stagePersonLinkCaseDto.getIdPerson());
			subcareCaseDto.setPlacementActPlannedOutDto(placementActPlannedOutDto);
			/*
			 ** Call DAM CSES33D to get the school information
			 */

			SubcareLegalEnrollDto subcareLegalEnrollDtoSchool = subcareCaseDao
					.getGreatestEnroll(stagePersonLinkCaseDto.getIdPerson());
			subcareCaseDto.setSubcareLegalEnrollDtoSchool(subcareLegalEnrollDtoSchool);

			/*
			 ** To get portfolio indication
			 */
			String indicator = subcareCaseDao.getIndication(stagePersonLinkCaseDto.getIdPerson());
			subcareCaseDto.setIndicator(indicator);

			/*
			 * To get the school programs
			 */
			List<String> schoolProgramsList = subcareCaseDao.getSchoolProgramList(stagePersonLinkCaseDto.getIdPerson());
			subcareCaseDto.setSchoolPrograms(schoolProgramsList);

			/*
			 ** Call DAM CSES35D to get the level of care info
			 */
			if (!ObjectUtils.isEmpty(commonApplicationDao.getPersonLocDtls(stagePersonLinkCaseDto.getIdPerson(),
					ServiceConstants.CARE_LEVEL))) {
				PersonLocDto personBlocLocDto = commonApplicationDao
						.getPersonLocDtls(stagePersonLinkCaseDto.getIdPerson(), ServiceConstants.CARE_LEVEL).get(0);
				subcareCaseDto.setPersonBlocLocDto(personBlocLocDto);
			}
			/*
			 ** Call DAM CSVC46D to get the CPOS and Permanency Plan Goal info
			 */

			SubcareChildContactDto subcareChildContactDtoChild = subcareCaseDao
					.getChildPlan(stagePersonLinkCaseDto.getIdPerson());
			subcareCaseDto.setSubcareChildContactDtoChild(subcareChildContactDtoChild);
			/*
			 ** Get the concurrent goals
			 */

			List<String> goals = subcareCaseDao.getConcurrentGoals(stagePersonLinkCaseDto.getIdCase());
			subcareCaseDto.setConcurrentGoals(goals);
			/*
			 ** Call DAM CSECB3D to get the date of the last doctor appt
			 */

			Date apptDate = subcareCaseDao.getAppointment(stagePersonLinkCaseDto.getIdPerson(),
					ServiceConstants.APPOINTMENT_MEDICAL);
			subcareCaseDto.setApptDate(apptDate);
			/*
			 ** Call DAM CSECB3D to get the date of the last dentist appt
			 */

			Date apptDateDental = subcareCaseDao.getAppointment(stagePersonLinkCaseDto.getIdPerson(),
					ServiceConstants.APPOINTMENT_DENTAL);
			subcareCaseDto.setApptDateDental(apptDateDental);
			/*
			 ** Call DAM CSECB3D to get the date of the last psych eval
			 */

			Date apptDatePsych = subcareCaseDao.getAppointment(stagePersonLinkCaseDto.getIdPerson(),
					ServiceConstants.APPOINTMENT_PSYCH);
			subcareCaseDto.setApptDatePsych(apptDatePsych);
			/*
			 ** Call DAM CSECB8D to get the family plan approval date
			 */

			Date childFpos = subcareCaseDao.getChildFpos(stagePersonLinkCaseDto.getIdPerson());
			subcareCaseDto.setChildFpos(childFpos);
		}

		if (!ObjectUtils.isEmpty(stagePersonLinkCaseDto.getIdPerson())
				&& !ObjectUtils.isEmpty(stagePersonLinkCaseDto.getIdCase())) {

			/*
			 ** Call DAM CSECB4D to get the Conservatorship Date
			 */
			SubcareChildContactDto subcareChildContactDtoRem = subcareCaseDao
					.getRemoval(stagePersonLinkCaseDto.getIdPerson(), stagePersonLinkCaseDto.getIdCase());
			subcareCaseDto.setSubcareChildContactDtoRem(subcareChildContactDtoRem);
			/*
			 ** Call DAM CSES78D to get the Legal Status
			 */

			SubcareLegalEnrollDto subcareLegalEnrollDtoLeg = subcareCaseDao
					.getLegalStatus(stagePersonLinkCaseDto.getIdCase(), stagePersonLinkCaseDto.getIdPerson());
			subcareCaseDto.setSubcareLegalEnrollDtoLeg(subcareLegalEnrollDtoLeg);
		}

		/*
		 ** Call DAM CSECB5D to get the review hearing date
		 */

		Date hearingDate = subcareCaseDao.getHearingDate(facilityInvSumReq.getIdStage());
		subcareCaseDto.setHearingDate(hearingDate);

		/*
		 ** To get the next hearing date
		 */
		Date nextHearingDate = subcareCaseDao.getNextDate(facilityInvSumReq.getIdStage());
		subcareCaseDto.setNextHearingDate(nextHearingDate);

		/*
		 ** Call DAM CSECB7D to get the PPT date
		 */

		Date pptDate = subcareCaseDao.getLatestPpt(facilityInvSumReq.getIdStage());
		subcareCaseDto.setPptDate(pptDate);
		/*
		 ** Call DAM CSECB2D to get the Last Visit Date
		 */

		Date plcmntDate = subcareCaseDao.getPlcmntContact(facilityInvSumReq.getIdStage());
		subcareCaseDto.setPlcmntDate(plcmntDate);
		/*
		 ** Call DAM CSECB1D to get the TCM Contact Date
		 */

		Date tcmDate = subcareCaseDao.getTcmContact(facilityInvSumReq.getIdStage());
		subcareCaseDto.setTcmDate(tcmDate);
		/*
		 ** Call DAM CSVC47D to get the TCM Contact Date
		 */

		SubcareChildContactDto subcareChildContactDtoGmth = subcareCaseDao
				.getGmthContact(facilityInvSumReq.getIdStage());
		subcareCaseDto.setSubcareChildContactDtoGmth(subcareChildContactDtoGmth);
		/*
		 ** Call DAM CLSC01D to get all principals in the stage
		 */

		List<CaseInfoDto> caseInfoDtoPrincipal = populateLetterDao.getCaseInfoById(facilityInvSumReq.getIdStage(),
				ServiceConstants.PRINCIPAL);
		subcareCaseDto.setCaseInfoDtoPrincipal(caseInfoDtoPrincipal);
		List<Long> selectedIdPersonList = new ArrayList<>();
		if (!ObjectUtils.isEmpty(facilityInvSumReq.getSelectedPersons())) {
			String[] selectPersonListStr = facilityInvSumReq.getSelectedPersons().split(",");
			for (String idPersonStr : selectPersonListStr) {
				selectedIdPersonList.add(Long.valueOf(idPersonStr));
			}
		}

		Long currentIdPerson;
		for (Iterator<CaseInfoDto> iterPrn = caseInfoDtoPrincipal.iterator(); iterPrn.hasNext();) {
			currentIdPerson = iterPrn.next().getIdPerson();
			if (!selectedIdPersonList.contains(currentIdPerson)) {
				iterPrn.remove();
			}
		}

		/*
		 * Loop through array to determine if PRNs are in 'parent' set then
		 * populate IndPersCancelHist variable to mean either PARENT_TYPE or
		 * NOT_PARENT_TYPE
		 **
		 ** (IndPersCancelHist used to flag PARENTS because Rel/Int is output on
		 * form)
		 */

		for (CaseInfoDto caseInfoDto : caseInfoDtoPrincipal) {
			if (ServiceConstants.ABSENT_PARENT.equals(caseInfoDto.getCdStagePersRelInt())
					|| ServiceConstants.ADO_FOSTER_PARENT.equals(caseInfoDto.getCdStagePersRelInt())
					|| ServiceConstants.ADOPTIVE_PARENT.equals(caseInfoDto.getCdStagePersRelInt())
					|| ServiceConstants.FOSTER_PARENT.equals(caseInfoDto.getCdStagePersRelInt())
					|| ServiceConstants.PARENT.equals(caseInfoDto.getCdStagePersRelInt())
					|| ServiceConstants.PARENT_ALLEGED.equals(caseInfoDto.getCdStagePersRelInt())
					|| ServiceConstants.PARENT_BIRTH.equals(caseInfoDto.getCdStagePersRelInt())
					|| ServiceConstants.SELF.equals(caseInfoDto.getCdStagePersRelInt())
					|| ServiceConstants.PARENT_LEGAL_ONLY.equals(caseInfoDto.getCdStagePersRelInt())) {
				caseInfoDto.setIndPersCancelHist(ServiceConstants.PARENT_TYPE_SUB);
			} else {
				caseInfoDto.setIndPersCancelHist(ServiceConstants.NOT_PARENT_TYPE);
			}
		}

		/*
		 ** Call DAM CLSC01D again to get all collaterals in the stage
		 */
		List<CaseInfoDto> caseInfoDtoCollateral = populateLetterDao.getCaseInfoById(facilityInvSumReq.getIdStage(),
				ServiceConstants.COLLATERAL);
		subcareCaseDto.setCaseInfoDtoCollateral(caseInfoDtoCollateral);

		for (Iterator<CaseInfoDto> iterCol = caseInfoDtoCollateral.iterator(); iterCol.hasNext();) {
			currentIdPerson = iterCol.next().getIdPerson();
			if (!selectedIdPersonList.contains(currentIdPerson)) {
				iterCol.remove();
			}
		}

		for (CaseInfoDto caseInfoDto : caseInfoDtoPrincipal) {
			if (ServiceConstants.FEMALE.equals(caseInfoDto.getCdPersonSex())
					&& ServiceConstants.PLACEMENT_PERSON_TYPE.equals(caseInfoDto.getIndPersCancelHist())) {
				List<Date> CVSMonthlyList = subcareCaseDao.getCVSMonthlyList(facilityInvSumReq.getIdStage(),
						caseInfoDto.getIdPerson());
				SimpleDateFormat format = new SimpleDateFormat();
				format = new SimpleDateFormat("MM/dd/yyyy");
				if (!CVSMonthlyList.isEmpty()) {
					String DateToStr = format.format(CVSMonthlyList.get(0));
					caseInfoDto.setCurrentContactDate(DateToStr);
				}
				if (!CVSMonthlyList.isEmpty() && CVSMonthlyList.size() > 1) {
					String DateToStr = format.format(CVSMonthlyList.get(1));
					caseInfoDto.setLastContactDate(DateToStr);
				}
			}
		}

		for (CaseInfoDto caseInfoDto : caseInfoDtoPrincipal) {
			if (ServiceConstants.MALE.equals(caseInfoDto.getCdPersonSex())
					&& ServiceConstants.PLACEMENT_PERSON_TYPE.equals(caseInfoDto.getIndPersCancelHist())) {
				List<Date> CVSMonthlyList = subcareCaseDao.getCVSMonthlyList(facilityInvSumReq.getIdStage(),
						caseInfoDto.getIdPerson());
				SimpleDateFormat format = new SimpleDateFormat();
				format = new SimpleDateFormat("MM/dd/yyyy");
				if (!CVSMonthlyList.isEmpty()) {
					String DateToStr = format.format(CVSMonthlyList.get(0));
					caseInfoDto.setCurrentContactDate(DateToStr);
				}
				if (!CVSMonthlyList.isEmpty() && CVSMonthlyList.size() > 1) {
					String DateToStr = format.format(CVSMonthlyList.get(1));
					caseInfoDto.setLastContactDate(DateToStr);
				}
			}
		}

		if (ServiceConstants.YES.equalsIgnoreCase(facilityInvSumReq.getIncldToDo())) {
			List<CaseInfoDto> getToDoListType = subcareCaseDao.getToDoListType(stagePersonLinkCaseDto.getIdCase());
			subcareCaseDto.setTodoList(getToDoListType);
		}

		return subcareCasePrefillData.returnPrefillData(subcareCaseDto);
	}
}
