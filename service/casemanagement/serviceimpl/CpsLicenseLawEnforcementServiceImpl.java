package us.tx.state.dfps.service.casemanagement.serviceimpl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import us.tx.state.dfps.common.dto.IncomingStageDetailsDto;
import us.tx.state.dfps.common.dto.NameDto;
import us.tx.state.dfps.common.dto.PhoneInfoDto;
import us.tx.state.dfps.service.casemanagement.dao.CpsIntakeNotificationDao;
import us.tx.state.dfps.service.casemanagement.service.CpsLicenseLawEnforcementService;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.StageDao;
import us.tx.state.dfps.service.common.request.CpsIntakeNotificationReq;
import us.tx.state.dfps.service.common.response.FacilRtrvRes;
import us.tx.state.dfps.service.cpsintakereport.dao.CpsIntakeReportDao;
import us.tx.state.dfps.service.cpsinv.dto.OfficePhoneDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.forms.util.CpsLicenseLawEnforcementPrefillData;
import us.tx.state.dfps.service.intake.dto.IncmgDetermFactorsDto;
import us.tx.state.dfps.service.person.dao.PersonDao;
import us.tx.state.dfps.service.person.dto.IntakeAllegationDto;
import us.tx.state.dfps.service.person.dto.PersonAddrLinkDto;
import us.tx.state.dfps.service.workload.dto.CpsIntakeNotificationDto;
import us.tx.state.dfps.service.workload.dto.PersonDto;

@Service
public class CpsLicenseLawEnforcementServiceImpl implements CpsLicenseLawEnforcementService {
	@Autowired
	private CpsIntakeNotificationDao cpsIntakeDao;

	@Autowired
	private PersonDao personDao;

	@Autowired
	CpsIntakeReportDao cpsInvestigationReportDao;

	@Autowired
	CpsLicenseLawEnforcementPrefillData cpsLicenseLawEnforcementPrefillData;

	@Autowired
	StageDao stageDao;

	private static final Logger log = Logger.getLogger(CpsLicenseLawEnforcementServiceImpl.class);

	public CpsLicenseLawEnforcementServiceImpl() {

	}

	/**
	 * Method Name:getCpsIntkLicenseLawReport Method Description: This service
	 * produces the data required for the CPS Intake notification law
	 * enforcement report. Service Name: CINT42S
	 * 
	 * @param cpsIntakeNotificationReq
	 * @return cpsIntakeNotificationRes
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
	public PreFillDataServiceDto getCpsIntkLicenseLawReport(CpsIntakeNotificationReq cpsIntakeNotificationReq) {
		CpsIntakeNotificationDto cpsIntakeNotificationRes = new CpsIntakeNotificationDto();

		log.info("Entering the getCpsIntkLicenseLawReport Method");
		// CINT65D
		IncomingStageDetailsDto incomingStageDetails = cpsInvestigationReportDao
				.getStageIncomingDetails(cpsIntakeNotificationReq.getIdStage());
		cpsIntakeNotificationRes.setIncomingStageDetailsDto(incomingStageDetails);
		// CINT66D
		List<PersonDto> personListVictim = cpsInvestigationReportDao
				.getPersonList(cpsIntakeNotificationReq.getIdStage(), ServiceConstants.VICTIM_TYPE);
		cpsIntakeNotificationRes.setPersonListVictim(personListVictim);

		List<PersonDto> personListPerperator = cpsInvestigationReportDao
				.getPersonList(cpsIntakeNotificationReq.getIdStage(), ServiceConstants.PERPETRATOR_TYPE);
		cpsIntakeNotificationRes.setPersonListPerperator(personListPerperator);

		List<PersonDto> personListPrnType = cpsInvestigationReportDao
				.getPersonList(cpsIntakeNotificationReq.getIdStage(), ServiceConstants.OTHER_PRN_TYPE);
		cpsIntakeNotificationRes.setOther(personListPrnType);

		List<PersonDto> personListCollatlType = cpsInvestigationReportDao
				.getPersonList(cpsIntakeNotificationReq.getIdStage(), ServiceConstants.COLLATERAL_TYPE);
		cpsIntakeNotificationRes.setPersonListCollateral(personListCollatlType);
		// CINT09D
		FacilRtrvRes facilRtrvRes = stageDao.getFacilityDetail(cpsIntakeNotificationReq.getIdStage());
		cpsIntakeNotificationRes.setFacilRtrvRes(facilRtrvRes);
		// CINT62D
		List<PhoneInfoDto> personPhDetail = cpsInvestigationReportDao
				.getPhoneInfo(cpsIntakeNotificationReq.getIdStage());
		cpsIntakeNotificationRes.setPersonPhoneList(personPhDetail);
		// CINT63D
		List<PersonAddrLinkDto> addrLinkDto = cpsIntakeDao.getAddrInfoByStageId(cpsIntakeNotificationReq.getIdStage());
		cpsIntakeNotificationRes.setPersonAddressLinkList(addrLinkDto);
		// CINT19D
		List<IntakeAllegationDto> intakeAllegationDtoList = personDao
				.getIntakeAllegationByStageId(cpsIntakeNotificationReq.getIdStage());
		cpsIntakeNotificationRes.setIntakeAllegationList(intakeAllegationDtoList);
		// CINT15D
		List<IncmgDetermFactorsDto> incmgDetermFactorsList = cpsIntakeDao
				.getincmgDetermFactorsById(cpsIntakeNotificationReq.getIdStage());
		cpsIntakeNotificationRes.setIncmgDetermFactorsDto(incmgDetermFactorsList);
		// CINT64D
		List<NameDto> nameDto = cpsIntakeDao.getNameAliases(cpsIntakeNotificationReq.getIdStage());
		cpsIntakeNotificationRes.setNameDto(nameDto);
		// CINT69D
		List<IntakeAllegationDto> intakeAllegationType = cpsIntakeDao
				.getAllegationTypeByStageId(cpsIntakeNotificationReq.getIdStage());
		cpsIntakeNotificationRes.setIntakeAllegationDto(intakeAllegationType);
		// CINT70D
		PersonAddrLinkDto personAddrLinkDto = cpsIntakeDao.getResidenceAddress(cpsIntakeNotificationReq.getIdStage());
		cpsIntakeNotificationRes.setPersonAddrLinkDto(personAddrLinkDto);
		// CINT71D
		String indOfficePrimary = ServiceConstants.Y;
		OfficePhoneDto officePhoneDto = cpsIntakeDao.getWorkerOfficeDetail(cpsIntakeNotificationReq.getIdStage(),
				indOfficePrimary);
		cpsIntakeNotificationRes.setOfficePhoneDto(officePhoneDto);

		return cpsLicenseLawEnforcementPrefillData.returnPrefillData(cpsIntakeNotificationRes);
	}

}
