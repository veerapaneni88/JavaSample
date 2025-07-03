package us.tx.state.dfps.service.investigation.serviceimpl;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.dto.GenericCaseInfoDto;
import us.tx.state.dfps.notiftolawenforcement.dto.FacilInvDtlDto;
import us.tx.state.dfps.notiftolawenforcement.dto.MultiAddressDto;
import us.tx.state.dfps.service.admin.dao.EmployeeDao;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.FacilityInvSumReq;
import us.tx.state.dfps.service.contact.dto.ContactListSearchDto;
import us.tx.state.dfps.service.contacts.dao.ContactSearchDao;
import us.tx.state.dfps.service.disasterplan.dao.DisasterPlanDao;
import us.tx.state.dfps.service.facilityinvcnclsn.dto.FacilityAllegationInfoDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.forms.util.FacilityInvReportPrefillData;
import us.tx.state.dfps.service.investigation.dao.FacilityAbuseInvReportDao;
import us.tx.state.dfps.service.investigation.dao.FacilityInvSumDao;
import us.tx.state.dfps.service.investigation.dto.ContactNarrDto;
import us.tx.state.dfps.service.investigation.dto.FacilityInvRepDto;
import us.tx.state.dfps.service.investigation.service.FacilityInvRepService;
import us.tx.state.dfps.service.notiftolawenforce.dao.NotifToLawEnforcementDao;
import us.tx.state.dfps.service.workload.dto.ContactDto;
import us.tx.state.dfps.service.workload.dto.EmployeePersPhNameDto;
import us.tx.state.dfps.service.workload.dto.StagePersDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:CINV81S May
 * 3, 2018- 3:25:07 PM © 2017 Texas Department of Family and Protective Services
 */
@Service
@Transactional
public class FacilityInvRepServiceImpl implements FacilityInvRepService {

	@Autowired
	private DisasterPlanDao disasterPlanDao;

	@Autowired
	private NotifToLawEnforcementDao notifToLawEnforcementDao;

	@Autowired
	private FacilityInvSumDao facilityInvSumDao;

	@Autowired
	private EmployeeDao employeeDao;

	@Autowired
	private FacilityAbuseInvReportDao facilityAbuseInvReportDao;

	@Autowired
	private ContactSearchDao contactSearchDao;

	@Autowired
	private FacilityInvReportPrefillData facilityInvReportPrefillData;

	/**
	 * Method Name: getFacilityInvReport Method Description: Populates form
	 * cfiv1500, which Populates the APS ICF-MR FACILITY INVESTIGATIVE REPORT
	 ** aka 5-DAY STATUS REPORT.
	 * 
	 * @return PreFillDataServiceDto
	 */
	@Override
	public PreFillDataServiceDto getFacilityInvReport(FacilityInvSumReq facilityInvSumReq) {

		FacilityInvRepDto facilityInvRepDto = new FacilityInvRepDto();
		EmployeePersPhNameDto employeePersPhNameDto = null;
		Long idCase = ServiceConstants.ZERO_VAL;

		/* retrieves stage and caps_case table */
		// CSEC02D
		GenericCaseInfoDto genericCaseInfoDto = disasterPlanDao.getGenericCaseInfo(facilityInvSumReq.getIdStage());
		if (!ObjectUtils.isEmpty(genericCaseInfoDto)) {
			idCase = genericCaseInfoDto.getIdCase();
		}

		/* Facility Investigation Detail */
		// CSES39D
		FacilInvDtlDto facilInvDtlDto = notifToLawEnforcementDao.getFacilityInvDtlbyId(facilityInvSumReq.getIdStage());

		/* retrieves Allegation list info */
		List<FacilityAllegationInfoDto> facilityAllegationInfoDtoType = facilityInvSumDao
				.getAllegationType(facilityInvSumReq.getIdStage());

		for (FacilityAllegationInfoDto facilityAllegationInfoDto : facilityAllegationInfoDtoType) {
			if (ObjectUtils.isEmpty(facilityAllegationInfoDto.getNmPersonFull())) {
				facilityAllegationInfoDto.setNmPersonFull(ServiceConstants.SYSTEM_ISSUE);
			}
		}
		/*
		 ** Retrieves the name of the Primary worker for the case given an id
		 * stage
		 */
		// CCMN30D
		StagePersDto stagePersDto = facilityAbuseInvReportDao.getPrimaryWorker(facilityInvSumReq.getIdStage());

		/* Contact info */

		// CDYN03D - Purpose: ANOT; Type: Blank; Others: CMHM
		ContactListSearchDto contactListSearchDto = new ContactListSearchDto();
		contactListSearchDto.setIdStage(facilityInvSumReq.getIdStage());
		contactListSearchDto.setCdContactPurposeList(Arrays.asList(ServiceConstants.ANOT));
		contactListSearchDto.setCdContactTypeList(Arrays.asList(ServiceConstants.EMPTY_STR));
		contactListSearchDto.setCdContactOthersList(Arrays.asList(ServiceConstants.CMHM));
		List<ContactDto> contactList = contactSearchDao.searchContactList(contactListSearchDto);
		if (ObjectUtils.isEmpty(contactList)) {
			// CDYN03D - Purpose: ANOT; Type: Blank; Others: CSPR (only if CMHM
			// returns no rows)
			contactListSearchDto.setCdContactOthersList(Arrays.asList(ServiceConstants.CSPR));
			contactList = contactSearchDao.searchContactList(contactListSearchDto);
		}

		/* Contact Narr */
		// CLSC13D
		List<ContactNarrDto> contactNarrList = facilityAbuseInvReportDao.getContactNarr(facilityInvSumReq.getIdStage(),
				ServiceConstants.MIN_DATE, ServiceConstants.MAX_DATE);

		/*
		 ** Call to the pCSEC01D dam to get the worker information
		 */
		if (!ObjectUtils.isEmpty(stagePersDto)) {
			employeePersPhNameDto = employeeDao.searchPersonPhoneName(stagePersDto.getIdPerson());
			if (!ObjectUtils.isEmpty(employeePersPhNameDto)) {
				if (!ObjectUtils.isEmpty(employeePersPhNameDto.getCdPhoneType())) {

					if (!(employeePersPhNameDto.getCdPhoneType().equals(ServiceConstants.BUSINESS_PHONE)
							|| employeePersPhNameDto.getCdPhoneType().equals(ServiceConstants.BUSINESS_CELL))) {
						employeePersPhNameDto.setNbrPhone(employeePersPhNameDto.getMailCodePhone());
						employeePersPhNameDto.setNbrPhoneExtension(employeePersPhNameDto.getMailCodePhoneExt());
					}
				}
			}
		}

		// CallCLSCGCD
		List<MultiAddressDto> multiAddressDtoList = notifToLawEnforcementDao
				.getMultiAddress(facilityInvSumReq.getIdStage(), idCase);

		facilityInvRepDto.setContactList(contactList);
		facilityInvRepDto.setContactNarrList(contactNarrList);
		facilityInvRepDto.setEmployeePersPhNameDto(employeePersPhNameDto);
		facilityInvRepDto.setFacilInvDtlDto(facilInvDtlDto);
		facilityInvRepDto.setFacilityAllegationInfoDtoType(facilityAllegationInfoDtoType);
		facilityInvRepDto.setGenericCaseInfoDto(genericCaseInfoDto);
		facilityInvRepDto.setMultiAddressDtoList(multiAddressDtoList);
		facilityInvRepDto.setStagePersDto(stagePersDto);

		return facilityInvReportPrefillData.returnPrefillData(facilityInvRepDto);
	}

}
