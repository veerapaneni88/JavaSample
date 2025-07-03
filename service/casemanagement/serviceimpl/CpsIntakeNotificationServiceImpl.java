package us.tx.state.dfps.service.casemanagement.serviceimpl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.dto.IncomingStageDetailsDto;
import us.tx.state.dfps.common.dto.NameDto;
import us.tx.state.dfps.common.dto.PhoneInfoDto;
import us.tx.state.dfps.service.casemanagement.dao.CpsIntakeNotificationDao;
import us.tx.state.dfps.service.casemanagement.service.CpsIntakeNotificationService;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.CpsIntakeNotificationReq;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.cpsintakereport.dao.CpsIntakeReportDao;
import us.tx.state.dfps.service.cpsinv.dto.CaseDtlsDto;
import us.tx.state.dfps.service.cpsinv.dto.OfficePhoneDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.forms.util.NotificationLawEnforcementAgencyPrefillData;
import us.tx.state.dfps.service.intake.dto.IncmgDetermFactorsDto;
import us.tx.state.dfps.service.lookup.dao.LookupDao;
import us.tx.state.dfps.service.person.dao.PersonDao;
import us.tx.state.dfps.service.person.dto.IntakeAllegationDto;
import us.tx.state.dfps.service.person.dto.PersonAddrLinkDto;
import us.tx.state.dfps.service.workload.dto.CpsIntakeNotificationDto;
import us.tx.state.dfps.service.workload.dto.PersonDto;

/**
 * Class name:CpsIntakeNotificationServiceImpl Class Description:This service is
 * used to launch the CPS Notification Forms Oct 30, 2017- 3:29:29 PM © 2017
 * Texas Department of Family and Protective Services
 */
@Service
public class CpsIntakeNotificationServiceImpl implements CpsIntakeNotificationService {

	@Autowired
	private CpsIntakeNotificationDao cpsIntakeDao;

	@Autowired
	private PersonDao personDao;

	@Autowired
	CpsIntakeReportDao cpsInvestigationReportDao;

	@Autowired
	NotificationLawEnforcementAgencyPrefillData notifcatnLawEnforcementAgencyPrefillData;

	@Autowired
	LookupDao lookupDao;

	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(CpsIntakeNotificationServiceImpl.class);

	public CpsIntakeNotificationServiceImpl() {

	}

	/**
	 * Method Name:getCpsIntkNotificnLawEnfrcemntReport Method Description: This
	 * service produces the data required for the CPS Intake notification law
	 * enforcement report. Service Name: CINT42S
	 * 
	 * @param cpsIntakeNotificationReq
	 * @return returnPrefillData
	 */

	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public PreFillDataServiceDto getCpsIntkNotificnLawEnfrcemntReport(
			CpsIntakeNotificationReq cpsIntakeNotificationReq) {

		CpsIntakeNotificationDto cpsIntakeNotificationDto = new CpsIntakeNotificationDto();

		String indOfficePrimary = ServiceConstants.Y;

		/** Start***CINT65D ******/
		IncomingStageDetailsDto incomingStageDetails = cpsInvestigationReportDao
				.getStageIncomingDetails(cpsIntakeNotificationReq.getIdStage());

		// Checking DtIncomingCall is not null and spliting the Time Stamp into
		// Time and
		// Date
		if (!ObjectUtils.isEmpty(incomingStageDetails)
				&& !ObjectUtils.isEmpty(incomingStageDetails.getDtIncomingCall())) {
			incomingStageDetails.setTmIncmgCall(DateUtils.getTime(incomingStageDetails.getDtIncomingCall()));
			incomingStageDetails.setDtIncoming(DateUtils.stringDt(incomingStageDetails.getDtIncomingCall()));
		}
		// Checking IncmgAllegType is not null and decoding the value
		if (!ObjectUtils.isEmpty(incomingStageDetails)
				&& !ObjectUtils.isEmpty(incomingStageDetails.getCdIncmgAllegType())) {
			incomingStageDetails.setCdIncmgAllegType(
					lookupDao.decode(ServiceConstants.CCLICALT, incomingStageDetails.getCdIncmgAllegType()));
		}
		cpsIntakeNotificationDto.setIncomingStageDetailsDto(incomingStageDetails);
		/** End***CINT65D ******/

		/** Start***CINT66D ******/

		// Setting the value where the person type is Victim
		List<PersonDto> personListVictim = cpsInvestigationReportDao
				.getPersonList(cpsIntakeNotificationReq.getIdStage(), ServiceConstants.VICTIM_TYPE);
		List<PersonDto> persnVictmList = new ArrayList<>();
		if (!ObjectUtils.isEmpty(personListVictim)) {
			for (PersonDto persnVictm : personListVictim) {
				if (!ObjectUtils.isEmpty(persnVictm.getCdStagePersRelInt())) {
					persnVictm.setCdStagePersRelInt(
							lookupDao.decode(ServiceConstants.CRELVICT, persnVictm.getCdStagePersRelInt()));
				}
				if (!ObjectUtils.isEmpty(persnVictm.getDtPersonBirth())) {
					persnVictm.setDob(DateUtils.stringDt(persnVictm.getDtPersonBirth()));
				}
				if (!ObjectUtils.isEmpty(persnVictm.getCdPersonLanguage())) {
					persnVictm.setCdPersonLanguage(
							lookupDao.decode(ServiceConstants.CLANG, persnVictm.getCdPersonLanguage()));
				}
				if (!ObjectUtils.isEmpty(persnVictm.getCdPersonSex())) {
					persnVictm.setCdPersonSex(lookupDao.decode(ServiceConstants.CSEX, persnVictm.getCdPersonSex()));
				}
				if (!ObjectUtils.isEmpty(persnVictm.getCdPersonEthnicGroup())) {
					persnVictm.setCdPersonEthnicGroup(
							lookupDao.decode(ServiceConstants.CETHNIC, persnVictm.getCdPersonEthnicGroup()));
				}
				if (!ObjectUtils.isEmpty(persnVictm.getCdPersonMaritalStatus())) {
					persnVictm.setCdPersonMaritalStatus(
							lookupDao.decode(ServiceConstants.CMARSTAT, persnVictm.getCdPersonMaritalStatus()));
				}
				persnVictmList.add(persnVictm);
			}
		}
		cpsIntakeNotificationDto.setPersonListVictim(personListVictim);

		// Setting the value where the person type is Perpetrator
		List<PersonDto> personListPerperator = cpsInvestigationReportDao
				.getPersonList(cpsIntakeNotificationReq.getIdStage(), ServiceConstants.PERPETRATOR_TYPE);
		List<PersonDto> persnPerpList = new ArrayList<>();
		if (!ObjectUtils.isEmpty(personListPerperator)) {
			for (PersonDto persnperp : personListPerperator) {
				if (!ObjectUtils.isEmpty(persnperp.getCdStagePersRelInt())) {
					persnperp.setCdStagePersRelInt(
							lookupDao.decode(ServiceConstants.CRELVICT, persnperp.getCdStagePersRelInt()));
				}
				if (!ObjectUtils.isEmpty(persnperp.getDtPersonBirth())) {
					persnperp.setDob(DateUtils.stringDt(persnperp.getDtPersonBirth()));
				}
				if (!ObjectUtils.isEmpty(persnperp.getCdPersonLanguage())) {
					persnperp.setCdPersonLanguage(
							lookupDao.decode(ServiceConstants.CLANG, persnperp.getCdPersonLanguage()));
				}
				if (!ObjectUtils.isEmpty(persnperp.getCdPersonSex())) {
					persnperp.setCdPersonSex(lookupDao.decode(ServiceConstants.CSEX, persnperp.getCdPersonSex()));
				}
				if (!ObjectUtils.isEmpty(persnperp.getCdPersonEthnicGroup())) {
					persnperp.setCdPersonEthnicGroup(
							lookupDao.decode(ServiceConstants.CETHNIC, persnperp.getCdPersonEthnicGroup()));
				}
				if (!ObjectUtils.isEmpty(persnperp.getCdPersonMaritalStatus())) {
					persnperp.setCdPersonMaritalStatus(
							lookupDao.decode(ServiceConstants.CMARSTAT, persnperp.getCdPersonMaritalStatus()));
				}
				persnPerpList.add(persnperp);
			}
		}
		cpsIntakeNotificationDto.setPersonListPerperator(persnPerpList);

		// Setting the value where the person type is Principle
		List<PersonDto> personListPrnType = cpsInvestigationReportDao
				.getPersonList(cpsIntakeNotificationReq.getIdStage(), ServiceConstants.OTHER_PRN_TYPE);
		List<PersonDto> persnOthrList = new ArrayList<>();
		if (!ObjectUtils.isEmpty(personListPrnType)) {
			for (PersonDto persnOthr : personListPrnType) {
				if (!ObjectUtils.isEmpty(persnOthr.getCdStagePersRelInt())) {
					persnOthr.setCdStagePersRelInt(
							lookupDao.decode(ServiceConstants.CRELVICT, persnOthr.getCdStagePersRelInt()));
				}
				if (!ObjectUtils.isEmpty(persnOthr.getDtPersonBirth())) {
					persnOthr.setDob(DateUtils.stringDt(persnOthr.getDtPersonBirth()));
				}
				if (!ObjectUtils.isEmpty(persnOthr.getCdPersonLanguage())) {
					persnOthr.setCdPersonLanguage(
							lookupDao.decode(ServiceConstants.CLANG, persnOthr.getCdPersonLanguage()));
				}
				if (!ObjectUtils.isEmpty(persnOthr.getCdPersonSex())) {
					persnOthr.setCdPersonSex(lookupDao.decode(ServiceConstants.CSEX, persnOthr.getCdPersonSex()));
				}
				if (!ObjectUtils.isEmpty(persnOthr.getCdPersonEthnicGroup())) {
					persnOthr.setCdPersonEthnicGroup(
							lookupDao.decode(ServiceConstants.CETHNIC, persnOthr.getCdPersonEthnicGroup()));
				}
				if (!ObjectUtils.isEmpty(persnOthr.getCdPersonMaritalStatus())) {
					persnOthr.setCdPersonMaritalStatus(
							lookupDao.decode(ServiceConstants.CMARSTAT, persnOthr.getCdPersonMaritalStatus()));
				}
				persnOthrList.add(persnOthr);
			}
		}
		cpsIntakeNotificationDto.setOther(persnOthrList);

		// Setting the value where the person type is Collateral
		List<PersonDto> personListCollatlType = cpsInvestigationReportDao
				.getPersonList(cpsIntakeNotificationReq.getIdStage(), ServiceConstants.COLLATERAL_TYPE);
		List<PersonDto> persnCollList = new ArrayList<>();
		if (!ObjectUtils.isEmpty(personListCollatlType)) {
			for (PersonDto persnColl : personListCollatlType) {
				if (!ObjectUtils.isEmpty(persnColl.getCdStagePersRelInt())) {
					persnColl.setCdStagePersRelInt(
							lookupDao.decode(ServiceConstants.CRELVICT, persnColl.getCdStagePersRelInt()));
				}
				if (!ObjectUtils.isEmpty(persnColl.getDtPersonBirth())) {
					persnColl.setDob(DateUtils.stringDt(persnColl.getDtPersonBirth()));
				}
				if (ObjectUtils.isEmpty(persnColl.getCdPersonLanguage())) {
					persnColl.setCdPersonLanguage(
							lookupDao.decode(ServiceConstants.CLANG, persnColl.getCdPersonLanguage()));
				}
				if (!ObjectUtils.isEmpty(persnColl.getCdPersonSex())) {
					persnColl.setCdPersonSex(lookupDao.decode(ServiceConstants.CSEX, persnColl.getCdPersonSex()));
				}
				if (!ObjectUtils.isEmpty(persnColl.getCdPersonEthnicGroup())) {
					persnColl.setCdPersonEthnicGroup(
							lookupDao.decode(ServiceConstants.CETHNIC, persnColl.getCdPersonEthnicGroup()));
				}
				if (!ObjectUtils.isEmpty(persnColl.getCdPersonMaritalStatus())) {
					persnColl.setCdPersonMaritalStatus(
							lookupDao.decode(ServiceConstants.CMARSTAT, persnColl.getCdPersonMaritalStatus()));
					persnCollList.add(persnColl);
				}
			}
		}
		cpsIntakeNotificationDto.setPersonListCollateral(persnCollList);
		/** End***CINT66D ******/

		// CINT62D
		List<PhoneInfoDto> personPhDetail = cpsInvestigationReportDao
				.getPhoneInfo(cpsIntakeNotificationReq.getIdStage());
		cpsIntakeNotificationDto.setPersonPhoneList(personPhDetail);

		// CINT63D
		List<PersonAddrLinkDto> addrLinkDto = cpsIntakeDao.getAddrInfoByStageId(cpsIntakeNotificationReq.getIdStage());
		cpsIntakeNotificationDto.setPersonAddressLinkList(addrLinkDto);

		// CINT19D
		List<IntakeAllegationDto> intakeAllegationDtoList = personDao
				.getIntakeAllegationByStageId(cpsIntakeNotificationReq.getIdStage());
		cpsIntakeNotificationDto.setIntakeAllegationList(intakeAllegationDtoList);

		// CINT15D
		List<IncmgDetermFactorsDto> incmgDetermFactorsDto = cpsIntakeDao
				.getincmgDetermFactorsById(cpsIntakeNotificationReq.getIdStage());
		List<IncmgDetermFactorsDto> incmgFactorList = new ArrayList<IncmgDetermFactorsDto>();
		if (!ObjectUtils.isEmpty(incmgDetermFactorsDto)) {
			for (IncmgDetermFactorsDto inc : incmgDetermFactorsDto) {
				inc.setCdIncmgDeterm(lookupDao.decode(ServiceConstants.CDETFACT, inc.getCdIncmgDeterm()));
				incmgFactorList.add(inc);
			}
		}
		cpsIntakeNotificationDto.setIncmgDetermFactorsDto(incmgFactorList);

		// CINT64D
		List<NameDto> nameDto = cpsIntakeDao.getNameAliases(cpsIntakeNotificationReq.getIdStage());
		cpsIntakeNotificationDto.setNameDto(nameDto);

		// CINT69D
		List<IntakeAllegationDto> intakeAllegationType = cpsIntakeDao
				.getAllegationTypeByStageId(cpsIntakeNotificationReq.getIdStage());
		List<IntakeAllegationDto> intakeAllegationTypeList = new ArrayList<IntakeAllegationDto>();
		if (!ObjectUtils.isEmpty(intakeAllegationType)) {
			for (IntakeAllegationDto intake : intakeAllegationType) {
				intake.setCdIntakeAllegType(lookupDao.decode(ServiceConstants.CCLICALT, intake.getCdIntakeAllegType()));
				intakeAllegationTypeList.add(intake);
			}
		}
		cpsIntakeNotificationDto.setIntakeAllegationDto(intakeAllegationTypeList);

		// CINT70D
		PersonAddrLinkDto personAddrLinkDto = cpsIntakeDao.getResidenceAddress(cpsIntakeNotificationReq.getIdStage());
		cpsIntakeNotificationDto.setPersonAddrLinkDto(personAddrLinkDto);

		// CINT71D
		OfficePhoneDto officePhoneDto = cpsIntakeDao.getWorkerOfficeDetail(cpsIntakeNotificationReq.getIdStage(),
				indOfficePrimary);
		cpsIntakeNotificationDto.setOfficePhoneDto(officePhoneDto);

		// CSEC68D
		CaseDtlsDto caseDtlsDto = cpsIntakeDao.getCaseDetails(cpsIntakeNotificationReq.getIdStage());
		cpsIntakeNotificationDto.setCaseDtlsDto(caseDtlsDto);

		return notifcatnLawEnforcementAgencyPrefillData.returnPrefillData(cpsIntakeNotificationDto);
	}

}
