package us.tx.state.dfps.service.investigation.serviceimpl;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import us.tx.state.dfps.common.dto.GenericCaseInfoDto;
import us.tx.state.dfps.notiftolawenforcement.dto.FacilInvDtlDto;
import us.tx.state.dfps.notiftolawenforcement.dto.MultiAddressDto;
import us.tx.state.dfps.service.admin.dao.EmployeeDao;
import us.tx.state.dfps.service.admin.dto.EmployeeDetailDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.CommonApplicationReq;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.common.utils.Base64;
import us.tx.state.dfps.service.common.utils.FacilityAbuseInvUtil;
import us.tx.state.dfps.service.contact.dto.ContactListSearchDto;
import us.tx.state.dfps.service.contacts.dao.AllegFacilDao;
import us.tx.state.dfps.service.contacts.dao.ContactNarrativeDao;
import us.tx.state.dfps.service.contacts.dao.ContactSearchDao;
import us.tx.state.dfps.service.disasterplan.dao.DisasterPlanDao;
import us.tx.state.dfps.service.extreq.ExtreqDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.forms.util.FacilityAbuseInvReportPrefillData;
import us.tx.state.dfps.service.investigation.dao.FacilityAbuseInvReportDao;
import us.tx.state.dfps.service.investigation.dao.FacilityInvSumDao;
import us.tx.state.dfps.service.investigation.dao.FacilityInvestigationDao;
import us.tx.state.dfps.service.investigation.dto.ContactNarrDto;
import us.tx.state.dfps.service.investigation.dto.FacilityAbuseInvReportDto;
import us.tx.state.dfps.service.investigation.service.FacilityAbuseInvReportService;
import us.tx.state.dfps.service.notiftolawenforce.dao.NotifToLawEnforcementDao;
import us.tx.state.dfps.service.person.dto.AllegationWithVicDto;
import us.tx.state.dfps.service.populateform.dao.PopulateFormDao;
import us.tx.state.dfps.service.resource.dto.ResourceDto;
import us.tx.state.dfps.service.subcare.dao.CapsResourceDao;
import us.tx.state.dfps.service.workload.dao.ApprovalEventLinkDao;
import us.tx.state.dfps.service.workload.dto.ApprovalEventLinkDto;
import us.tx.state.dfps.service.workload.dto.ContactDto;
import us.tx.state.dfps.service.workload.dto.StagePersDto;
import us.tx.state.dfps.xmlstructs.inputstructs.AllegationFacilAllegPersonDto;
import us.tx.state.dfps.xmlstructs.inputstructs.FacilityInvestigationDto;
import us.tx.state.dfps.xmlstructs.outputstructs.AllegationStageVictimDto;
import us.tx.state.dfps.xmlstructs.outputstructs.FacilAllegPersonDto;
import us.tx.state.dfps.xmlstructs.outputstructs.FacilInvstInfoDto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Apr 30, 2018- 4:47:04 PM © 2017 Texas Department of
 * Family and Protective Services
 */
@Service
@Transactional
public class FacilityAbuseInvReportServiceImpl implements FacilityAbuseInvReportService {

	@Autowired
	private DisasterPlanDao disasterPlanDao;

	@Autowired
	private NotifToLawEnforcementDao notifToLawEnforcementDao;

	@Autowired
	private PopulateFormDao populateFormDao;

	@Autowired
	private FacilityAbuseInvReportDao facilityAbuseInvReportDao;

	@Autowired
	private AllegFacilDao allegFacilDao;

	@Autowired
	private ContactSearchDao contactSearchDao;

	@Autowired
	private FacilityInvestigationDao facilityInvestigationDao;

	@Autowired
	private FacilityInvSumDao facilityInvSumDao;

	@Autowired
	private EmployeeDao employeeDao;

	@Autowired
	private ApprovalEventLinkDao approvalEventLinkDao;

	@Autowired
	private CapsResourceDao capsResourceDao;

	@Autowired
	private FacilityAbuseInvReportPrefillData prefillData;

	@Autowired
	private ContactNarrativeDao contactNarrativeDao;

	/**
	 * Method Name: getAbuseReport Method Description: Gets information about
	 * abuse report from database and returns prefill string
	 * 
	 * @param req
	 * @return PreFillDataServiceDto
	 */
	@Override
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public PreFillDataServiceDto getAbuseReport(CommonApplicationReq req) {
		// Declare prefill dto and global variables
		FacilityAbuseInvReportDto prefillDto = new FacilityAbuseInvReportDto();
		Long idCase = ServiceConstants.ZERO_VAL;
		Long idEvent = ServiceConstants.ZERO_VAL;
		Long idFacilResource = ServiceConstants.ZERO_VAL;
		Long idApproval = ServiceConstants.ZERO_VAL;

		// CSEC02D
		GenericCaseInfoDto genericCaseInfoDto = disasterPlanDao.getGenericCaseInfo(req.getIdStage());
		if (!ObjectUtils.isEmpty(genericCaseInfoDto)) {
			prefillDto.setGenericCaseInfoDto(genericCaseInfoDto);
			idCase = genericCaseInfoDto.getIdCase();
		}

		// CSES39D
		FacilInvDtlDto facilInvDtlDto = notifToLawEnforcementDao.getFacilityInvDtlbyId(req.getIdStage());
		if (!ObjectUtils.isEmpty(facilInvDtlDto)) {
			prefillDto.setFacilInvDtlDto(facilInvDtlDto);
			idEvent = facilInvDtlDto.getIdEvent();
			idFacilResource = facilInvDtlDto.getIdFacilResource();
		}

		// CLSC05D
		List<AllegationWithVicDto> allegationList = populateFormDao.getAllegationById(req.getIdStage());
		if (!ObjectUtils.isEmpty(allegationList)) {
			for (AllegationWithVicDto allegationDto : allegationList) {
				if (StringUtils.isBlank(allegationDto.getcNmPersonFull())
						&& (ServiceConstants.FAC_SOURCE.equals(allegationDto.getfCdFacilAllegSrc())
								|| ServiceConstants.FAC_SOURCE.equals(allegationDto.getfCdFacilAllegSrcSupr()))) {
					allegationDto.setcNmPersonFull(ServiceConstants.SYSTEM_ISSUE);
				}
			}
			prefillDto.setAllegationList(allegationList);
		}

		// CLSC13D
		List<ContactNarrDto> contactNarrList = facilityAbuseInvReportDao.getContactNarr(req.getIdStage(),
				ServiceConstants.MIN_DATE, ServiceConstants.MAX_DATE);
		prefillDto.setContactNarrList(contactNarrList);

        // Artifact: artf147216 - SD 56377 : R2 Sev 5 Defect 10107
        // Check the EvidenceList (EEVL contact type), If exists then pre-fill that in Evidence list
        // of the report.
        if (!CollectionUtils.isEmpty(contactNarrList)) {
            List<Long> idEEVLEvents =
                    contactNarrList.stream().filter(c -> ServiceConstants.EEVL.equals(c.getCdContactType()))
                            .map(ContactNarrDto::getIdEvent).collect(Collectors.toList());
            // if the EEVL events exists, fetech evidence from narrative.
            if (!CollectionUtils.isEmpty(idEEVLEvents)) {
                List<byte[]> eevlDocs = contactNarrativeDao.getContactNarrativeDocs(idEEVLEvents);
                List<String> evidenceList = new ArrayList<>();
                if (!CollectionUtils.isEmpty(eevlDocs)) {
                    eevlDocs.forEach(b -> {
                        String encodedEvidenceVal =
                                FacilityAbuseInvUtil.getFieldValue(b, ServiceConstants.TXT_BLANK_NARRATIVE);
                        if (!StringUtils.isEmpty(encodedEvidenceVal)) {
                            evidenceList.add(new String(Base64.decode(encodedEvidenceVal)));
                        }
                    });
                }
                prefillDto.setEvidenceList(evidenceList);
            }
        }
        // END: artf147216

		// CLSC16D
		AllegationFacilAllegPersonDto allegationFacilAllegPersonDto = new AllegationFacilAllegPersonDto();
		allegationFacilAllegPersonDto.setUlIdAllegationStage(req.getIdStage().intValue());
		FacilAllegPersonDto facilAllegPersonDto = allegFacilDao
				.getAllegationFacilAllegPerson(allegationFacilAllegPersonDto);
		if (!ObjectUtils.isEmpty(facilAllegPersonDto)
				&& !ObjectUtils.isEmpty(facilAllegPersonDto.getAllegFacilPersonDto())) {
			List<AllegationStageVictimDto> allegationStageVictimList = facilAllegPersonDto.getAllegFacilPersonDto()
					.getAllegationStageVictimDtoList();
			for (AllegationStageVictimDto vicDto : allegationStageVictimList) {
				/*
				 ** For all allegations in the case, CLSC16D retrieves the
				 * allegation Incident Date, the Incident Time, and any comments
				 * that were written. This data is then used to populate the
				 * Facility Abuse/Neglect Report. If any of the Incident Dates
				 * are NULL or 01/01/1850 (which is the earliest system date
				 * allowed), copy "unknown" into the date variable so that it
				 * can be printed onto the Facility Abuse/Neglect Report.
				 * Otherwise, copy the date from the date variable used by the
				 * DAM (which is defined as FND_INT3DATE) into the date variable
				 * that will be printed onto the report (which is defined as
				 * CHAR). The date variable (defined as CHAR) that will be
				 * printed onto the Facility Abuse/Negelct Report is
				 * "szScrDtGeneric1". This variable (defined as CHAR) will be
				 ** used because it can hold either a date (converted to a
				 * string) or the string "unknown".
				 */
				if (ObjectUtils.isEmpty(vicDto.getDtFacilAllegIncident())
						|| ServiceConstants.MIN_DATE.equals(vicDto.getDtScrDtLastUpdate())) {
					vicDto.setScrDtGeneric1(ServiceConstants.LT_UNKNOWN);
					vicDto.setTmScrTmGeneric8(ServiceConstants.LT_UNKNOWN);
				}else{
					vicDto.setScrDtGeneric1(TypeConvUtil.formDateFormat(vicDto.getDtFacilAllegIncident()));
					vicDto.setTmScrTmGeneric8(TypeConvUtil.formatTimeAMPM(vicDto.getDtFacilAllegIncident()));
				}				
			}
			prefillDto.setAllegationStageVictimList(allegationStageVictimList);
		}

		// CDYN03D - Purpose: ANOT; Type: Blank; Others: CMHM
		ContactListSearchDto contactListSearchDto = new ContactListSearchDto();
		contactListSearchDto.setIdStage(req.getIdStage());
		contactListSearchDto.setCdContactPurposeList(Arrays.asList(ServiceConstants.ANOT));
		contactListSearchDto.setCdContactOthersList(Arrays.asList(ServiceConstants.CMHM));
		List<ContactDto> contactListA = contactSearchDao.searchContactList(contactListSearchDto);
		if (ObjectUtils.isEmpty(contactListA)) {
			// CDYN03D - Purpose: ANOT; Type: Blank; Others: CSPR (only if CMHM
			// returns no rows)
			contactListSearchDto.setCdContactOthersList(Arrays.asList(ServiceConstants.CSPR));
			contactListA = contactSearchDao.searchContactList(contactListSearchDto);
		}
		prefillDto.setContactListA(contactListA);

		// CDYN03D - Purpose: ANOT; Type: EREG; Others: CLAW
		contactListSearchDto.setCdContactTypeList(Arrays.asList(ServiceConstants.EREG));
		contactListSearchDto.setCdContactOthersList(Arrays.asList(ServiceConstants.CLAW));
		List<ContactDto> contactListB = contactSearchDao.searchContactList(contactListSearchDto);
		prefillDto.setContactListB(contactListB);

		// CDYN03D - Purpose: Blank; Type: EFAC; Others: Blank
		contactListSearchDto.setCdContactPurposeList(null);
		contactListSearchDto.setCdContactTypeList(Arrays.asList(ServiceConstants.EFAC));
		contactListSearchDto.setCdContactOthersList(null);
		List<ContactDto> contactListC = contactSearchDao.searchContactList(contactListSearchDto);
		prefillDto.setContactListC(contactListC);

		// CINV17D
		FacilityInvestigationDto facilityInvestigationDto = new FacilityInvestigationDto();
		facilityInvestigationDto.setUlIdStage(req.getIdStage().intValue());
		FacilInvstInfoDto facilInvstInfoDto = facilityInvestigationDao
				.getFacilityInvestigationDetail(facilityInvestigationDto);
		prefillDto.setFacilInvstInfoDto(facilInvstInfoDto);

		// CINVB8D
		Date contactDate = facilityInvSumDao.getContactDate(req.getIdStage());
		prefillDto.setContactDate(contactDate);

		// CCMN30D
		StagePersDto stagePersDto = facilityAbuseInvReportDao.getPrimaryWorker(req.getIdStage());
		if (!ObjectUtils.isEmpty(stagePersDto)) {
			// CCMN69D
			EmployeeDetailDto employeeDetailDto = employeeDao.getEmployeeById(stagePersDto.getIdPerson());
			prefillDto.setEmployeeDetailDto(employeeDetailDto);
			prefillDto.setStagePersDto(stagePersDto);
		}

		// CCMN55D
		ApprovalEventLinkDto approvalEventLinkDto = approvalEventLinkDao.getApprovalEventLinkID(idEvent);
		if (!ObjectUtils.isEmpty(approvalEventLinkDto)) {
			idApproval = approvalEventLinkDto.getIdApproval();
			prefillDto.setApprovalEventLinkDto(approvalEventLinkDto);
		}

		// CSESF5D
		Date approvalDate = facilityAbuseInvReportDao.getApprovalDate(idApproval, ServiceConstants.APPROVED);
		if (ObjectUtils.isEmpty(approvalDate)) {
			approvalDate = new Date();
		}
		prefillDto.setApprovalDate(approvalDate);

		// CLSCGCD
		List<MultiAddressDto> multiAddressList = notifToLawEnforcementDao.getMultiAddress(req.getIdStage(), idCase);
		prefillDto.setMultiAddressList(multiAddressList);

		// CSECF0D
		ExtreqDto extreqDto = facilityAbuseInvReportDao.getApprover(idEvent);
		if (!ObjectUtils.isEmpty(extreqDto) && ServiceConstants.APPROVED.equals(extreqDto.getCdApproversStatus())) {
			extreqDto.setTxtApproverStatement(ServiceConstants.APPROVER_STATEMENT);
		}
		prefillDto.setExtreqDto(extreqDto);

		// CRES04D
		ResourceDto resourceDto = capsResourceDao.getResourceById(idFacilResource);
		prefillDto.setResourceDto(resourceDto);

		return prefillData.returnPrefillData(prefillDto);
	}
	

	/**
	 * Method Name: getStageIdForDataFix Method Description: Return the idStage to Launch the 
	 * APS Abuse and Neglect in Editable Mode
	 *  
	 * @param idStage
	 * @return Long
	 */
	@Override
	public Long getStageIdForDataFix(Long idStage) {		
		return facilityAbuseInvReportDao.getStageIdForDataFix(idStage);
	}

}
