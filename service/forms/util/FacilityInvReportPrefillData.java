package us.tx.state.dfps.service.forms.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.notiftolawenforcement.dto.MultiAddressDto;
import us.tx.state.dfps.service.common.BookmarkConstants;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.FormConstants;
import us.tx.state.dfps.service.common.FormGroupsConstants;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.facilityinvcnclsn.dto.FacilityAllegationInfoDto;
import us.tx.state.dfps.service.forms.dto.BlobDataDto;
import us.tx.state.dfps.service.forms.dto.BookmarkDto;
import us.tx.state.dfps.service.forms.dto.FormDataGroupDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.investigation.dto.ContactNarrDto;
import us.tx.state.dfps.service.investigation.dto.FacilityInvRepDto;
import us.tx.state.dfps.service.workload.dto.ContactDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:CpsInvReportPrefillData will implement returnPrefillData
 * operation defined in DocumentServiceUtil Interface to populate the prefill
 * data for Form CpsInvReport Apr 9, 2018- 10:53:14 AM © 2017 Texas Department
 * of Family and Protective Services
 */
@Component
public class FacilityInvReportPrefillData extends DocumentServiceUtil {

	public FacilityInvReportPrefillData() {

	}

	/**
	 * Method Description: This method is used to prefill the data from the
	 * different Dao by passing Dao output Dtos and bookmark and form group
	 * bookmark Dto as objects as input request
	 * 
	 * @param parentDtoobj
	 * @param bookmarkDtoObj
	 * @return PreFillData
	 * 
	 */
	@Override
	public PreFillDataServiceDto returnPrefillData(Object parentDtoobj) {

		FacilityInvRepDto facilityInvRepDto = (FacilityInvRepDto) parentDtoobj;

		if (ObjectUtils.isEmpty(facilityInvRepDto.getContactList())) {
			facilityInvRepDto.setContactList(new ArrayList<ContactDto>());
		}
		if (ObjectUtils.isEmpty(facilityInvRepDto.getContactNarrList())) {
			facilityInvRepDto.setContactNarrList(new ArrayList<ContactNarrDto>());
		}
		if (ObjectUtils.isEmpty(facilityInvRepDto.getFacilityAllegationInfoDtoType())) {
			facilityInvRepDto.setFacilityAllegationInfoDtoType(new ArrayList<FacilityAllegationInfoDto>());
		}
		if (ObjectUtils.isEmpty(facilityInvRepDto.getMultiAddressDtoList())) {
			facilityInvRepDto.setMultiAddressDtoList(new ArrayList<MultiAddressDto>());
		}

		List<FormDataGroupDto> formDataGroupList = new ArrayList<FormDataGroupDto>();

		// parent group cfiv1607
		if (DateUtils.isBefore(facilityInvRepDto.getGenericCaseInfoDto().getDtStageStart(),
				DateUtils.date(2010, 6, 7))) {
			FormDataGroupDto dispPriorJuneGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_DISP_PRIOR_JUNE,
					FormConstants.EMPTY_STRING);
			List<FormDataGroupDto> dispPriorJuneGroupList = new ArrayList<FormDataGroupDto>();

			// sub group cfiv1608
			FormDataGroupDto priorJuneGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_PRIOR_JUNE,
					FormGroupsConstants.TMPLAT_DISP_PRIOR_JUNE);
			List<BookmarkDto> bookmarkPriorJuneList = new ArrayList<BookmarkDto>();
			BookmarkDto bookmarkFacName = createBookmark(BookmarkConstants.SUM_FAC_NAME,
					facilityInvRepDto.getFacilInvDtlDto().getNmFacilinvstFacility());
			bookmarkPriorJuneList.add(bookmarkFacName);
			priorJuneGroupDto.setBookmarkDtoList(bookmarkPriorJuneList);

			dispPriorJuneGroupList.add(priorJuneGroupDto);

			dispPriorJuneGroupDto.setFormDataGroupList(dispPriorJuneGroupList);
			formDataGroupList.add(dispPriorJuneGroupDto);
		}

		// parent group cfiv1509
		else {
			FormDataGroupDto dispPostJuneGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_DISP_POST_JUNE,
					FormConstants.EMPTY_STRING);
			List<FormDataGroupDto> dispPostJuneGroupList = new ArrayList<FormDataGroupDto>();

			List<BookmarkDto> bookmarkPostJuneList = new ArrayList<BookmarkDto>();
			if (!ObjectUtils.isEmpty(facilityInvRepDto.getGenericCaseInfoDto().getIdStage())) {
				BookmarkDto bookmarkIdStage = createBookmark(BookmarkConstants.UE_GROUPID,
						facilityInvRepDto.getGenericCaseInfoDto().getIdStage());
				bookmarkPostJuneList.add(bookmarkIdStage);
			}
			dispPostJuneGroupDto.setBookmarkDtoList(bookmarkPostJuneList);
			

			// sub group cfiv1510
			for (MultiAddressDto addressDto : facilityInvRepDto.getMultiAddressDtoList()) {
				FormDataGroupDto postJuneGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_FAC_NAME,
						FormGroupsConstants.TMPLAT_DISP_POST_JUNE);
				List<BookmarkDto> bookmarkFacNameList = new ArrayList<BookmarkDto>();

				/*
				if (!ObjectUtils.isEmpty(addressDto.getaFilCdMhmrCode())) {
					BookmarkDto bookmarkCdMhmrCode = createBookmark(BookmarkConstants.UE_GROUPID,
							addressDto.getaFilCdMhmrCode());
					bookmarkFacNameList.add(bookmarkCdMhmrCode);
				}
				*/
				if (!ObjectUtils.isEmpty(addressDto.getaCpNmResource())) {
					BookmarkDto bookmarkNmNameResource = createBookmark(BookmarkConstants.FACILITY_NAME,
							addressDto.getaCpNmResource());
					bookmarkFacNameList.add(bookmarkNmNameResource);
				}

				postJuneGroupDto.setBookmarkDtoList(bookmarkFacNameList);
				dispPostJuneGroupList.add(postJuneGroupDto);

			}
			
			dispPostJuneGroupDto.setFormDataGroupList(dispPostJuneGroupList);
			formDataGroupList.add(dispPostJuneGroupDto);
		}

		// cfiv1501
		for (FacilityAllegationInfoDto facilityAllegationInfoDto : facilityInvRepDto
				.getFacilityAllegationInfoDtoType()) {
			FormDataGroupDto allegationDataGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_ALLEGATION,
					FormConstants.EMPTY_STRING);
			
			List<BookmarkDto> bookmarkAllegTypeList = new ArrayList<BookmarkDto>();
			BookmarkDto bookmarkAllegType = createBookmarkWithCodesTable(BookmarkConstants.ALLEG_DTL_ALLEG,
					facilityAllegationInfoDto.getCdAllegType(), CodesConstant.CABALTYP);
			bookmarkAllegTypeList.add(bookmarkAllegType);
			allegationDataGroup.setBookmarkDtoList(bookmarkAllegTypeList);
			formDataGroupList.add(allegationDataGroup);
		}

		// cfiv1502
		for (FacilityAllegationInfoDto facilityAllegationInfoDto : facilityInvRepDto
				.getFacilityAllegationInfoDtoType()) {
			FormDataGroupDto dispAllegType = createFormDataGroup(FormGroupsConstants.TMPLAT_ALLEGATION_PERP,
					FormConstants.EMPTY_STRING);
			
			List<BookmarkDto> bookmarkAllegTypeList = new ArrayList<BookmarkDto>();
			BookmarkDto bookmarkAllegType = createBookmark(BookmarkConstants.ALLEG_DTL_AP,
					facilityAllegationInfoDto.getNmPersonFull());
			bookmarkAllegTypeList.add(bookmarkAllegType);
			dispAllegType.setBookmarkDtoList(bookmarkAllegTypeList);
			formDataGroupList.add(dispAllegType);
		}

		// cfiv1505
		if (!ObjectUtils.isEmpty(facilityInvRepDto.getEmployeePersPhNameDto())
				&& !ObjectUtils.isEmpty(facilityInvRepDto.getEmployeePersPhNameDto().getNbrPhoneExtension())) {
			FormDataGroupDto dispExt = createFormDataGroup(FormGroupsConstants.TMPLAT_EXT, FormConstants.EMPTY_STRING);
			formDataGroupList.add(dispExt);
			List<BookmarkDto> bookmarkExtList = new ArrayList<BookmarkDto>();
			BookmarkDto bookmarkPhoneExtension = createBookmark(BookmarkConstants.WORKER_PHONE_EXTENSION,
					facilityInvRepDto.getEmployeePersPhNameDto().getNbrPhoneExtension());
			bookmarkExtList.add(bookmarkPhoneExtension);
			dispExt.setBookmarkDtoList(bookmarkExtList);
		}

		// parent group cfiv1503
		for (ContactNarrDto contactDto : facilityInvRepDto.getContactNarrList()) {
			if (ServiceConstants.EREG.equals(contactDto.getCdContactType())) {
				FormDataGroupDto evidenceListGroupDto = createFormDataGroup(
						FormGroupsConstants.TMPLAT_INTERVIEWS_CONDUCTED, FormConstants.EMPTY_STRING);
				List<BlobDataDto> blobEvidenceListList = new ArrayList<BlobDataDto>();
				BlobDataDto blobEvidenceListContacts = createBlobData(BookmarkConstants.INTERVIEWS_CONDUCTED,
						BookmarkConstants.CONTACT_NARRATIVE, contactDto.getIdEvent().toString());
				blobEvidenceListList.add(blobEvidenceListContacts);

				evidenceListGroupDto.setBlobDataDtoList(blobEvidenceListList);
				formDataGroupList.add(evidenceListGroupDto);
			}
		}

		// parent group cfiv1504
		for (ContactNarrDto contactDto : facilityInvRepDto.getContactNarrList()) {
			if (!ServiceConstants.ECOM.equals(contactDto.getCdContactType())
					&& !ServiceConstants.EREG.equals(contactDto.getCdContactType())
					&& !ServiceConstants.EFAC.equals(contactDto.getCdContactType())
					&& !ServiceConstants.EEXR.equals(contactDto.getCdContactType())
					&& !ServiceConstants.EIFF.equals(contactDto.getCdContactType())
					&& !ServiceConstants.EREV.equals(contactDto.getCdContactType())) {
				FormDataGroupDto evidenceListGroupDto = createFormDataGroup(
						FormGroupsConstants.TMPLAT_EVIDENCE_COLLECTED, FormConstants.EMPTY_STRING);
				List<BlobDataDto> blobEvidenceListList = new ArrayList<BlobDataDto>();
				BlobDataDto blobEvidenceListContacts = createBlobData(BookmarkConstants.EVIDENCE_COLLECTED,
						BookmarkConstants.CONTACT_NARRATIVE, contactDto.getIdEvent().toString());
				blobEvidenceListList.add(blobEvidenceListContacts);

				evidenceListGroupDto.setBlobDataDtoList(blobEvidenceListList);
				formDataGroupList.add(evidenceListGroupDto);
			}
		}

		// parent group cfiv1506
		for (ContactNarrDto contactDto : facilityInvRepDto.getContactNarrList()) {
			if (ServiceConstants.EIFF.equals(contactDto.getCdContactType())) {
				FormDataGroupDto evidenceListGroupDto = createFormDataGroup(
						FormGroupsConstants.TMPLAT_INTERVIEW_INITIAL, FormConstants.EMPTY_STRING);
				List<BlobDataDto> blobEvidenceListList = new ArrayList<BlobDataDto>();
				if (!ObjectUtils.isEmpty(contactDto.getIdEvent())) {
					BlobDataDto blobEvidenceListContacts = createBlobData(BookmarkConstants.INTERVIEW_INITIAL,
							BookmarkConstants.CONTACT_NARRATIVE, contactDto.getIdEvent().toString());
					blobEvidenceListList.add(blobEvidenceListContacts);
				}

				evidenceListGroupDto.setBlobDataDtoList(blobEvidenceListList);
				formDataGroupList.add(evidenceListGroupDto);
			}
		}

		// parent group cfiv1520
		for (ContactNarrDto contactDto : facilityInvRepDto.getContactNarrList()) {
			if (ServiceConstants.ECOM.equals(contactDto.getCdContactType())) {
				FormDataGroupDto evidenceListGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_ECOM,
						FormConstants.EMPTY_STRING);
				List<BlobDataDto> blobEvidenceListList = new ArrayList<BlobDataDto>();
				BlobDataDto blobEvidenceListContacts = createBlobData(BookmarkConstants.ECOM_CCNTCTYP,
						BookmarkConstants.CONTACT_NARRATIVE, contactDto.getIdEvent().toString());
				blobEvidenceListList.add(blobEvidenceListContacts);

				evidenceListGroupDto.setBlobDataDtoList(blobEvidenceListList);
				formDataGroupList.add(evidenceListGroupDto);

				// cfiv1521 : sub group of 1520
				List<FormDataGroupDto> evidenceGroupList = new ArrayList<FormDataGroupDto>();
				evidenceListGroupDto.setFormDataGroupList(evidenceGroupList);

				FormDataGroupDto pageBreakGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_ECOM_PAGEBREAK,
						FormGroupsConstants.TMPLAT_ECOM);
				evidenceGroupList.add(pageBreakGroupDto);
			}
		}

		// bookmarks without groups
		List<BookmarkDto> bookmarkNonFormGrpList = new ArrayList<BookmarkDto>();

		// CSEC02D GenericCaseInfoDto
		BookmarkDto bookmarkNmCase = createBookmark(BookmarkConstants.FACILITY_5DAY_CASE_NAME,
				facilityInvRepDto.getGenericCaseInfoDto().getNmCase());
		bookmarkNonFormGrpList.add(bookmarkNmCase);
		BookmarkDto bookmarkIdCase = createBookmark(BookmarkConstants.FACILITY_5DAY_CASE_NUMBER,
				facilityInvRepDto.getGenericCaseInfoDto().getIdCase());
		bookmarkNonFormGrpList.add(bookmarkIdCase);

		// CCMN30D
		BookmarkDto bookmarkNmPersonFull = createBookmark(BookmarkConstants.NM_PERSON_FULL,
				!ObjectUtils.isEmpty(facilityInvRepDto.getStagePersDto())?facilityInvRepDto.getStagePersDto().getNmPersonFull():null);
		bookmarkNonFormGrpList.add(bookmarkNmPersonFull);

		// CSES39D
		if (!ObjectUtils.isEmpty(facilityInvRepDto.getFacilInvDtlDto().getDtFacilInvstIntake())) {
			BookmarkDto bookmarkDateInitialNotif = createBookmark(BookmarkConstants.DATE_INITIAL_NOTIF,
					DateUtils.stringDt(facilityInvRepDto.getFacilInvDtlDto().getDtFacilInvstIntake()));
			bookmarkNonFormGrpList.add(bookmarkDateInitialNotif);
			BookmarkDto bookmarkTimeInitialNotif = createBookmark(BookmarkConstants.TIME_INITIAL_NOTIF,
					DateUtils.getTime(facilityInvRepDto.getFacilInvDtlDto().getDtFacilInvstIntake()));
			bookmarkNonFormGrpList.add(bookmarkTimeInitialNotif);
		}
		BookmarkDto bookmarkFacName = createBookmark(BookmarkConstants.SUM_FAC_NAME,
				facilityInvRepDto.getFacilInvDtlDto().getNmFacilinvstFacility());
		bookmarkNonFormGrpList.add(bookmarkFacName);

		// CDYN03D
		if (!ObjectUtils.isEmpty(facilityInvRepDto.getContactNarrList())
				&& ServiceConstants.Zero < facilityInvRepDto.getContactNarrList().size()) {
			ContactNarrDto contactDto = facilityInvRepDto.getContactNarrList().get(0);
			BookmarkDto bookmarkDateDirectorNotif = createBookmark(BookmarkConstants.DATE_DIRECTOR_NOTIF,
					DateUtils.stringDt(contactDto.getDtContactOccurred()));
			bookmarkNonFormGrpList.add(bookmarkDateDirectorNotif);
			BookmarkDto bookmarkTimeDirectorNotif = createBookmark(BookmarkConstants.TIME_DIRECTOR_NOTIF,
					DateUtils.getTime(contactDto.getDtContactOccurred()));
			bookmarkNonFormGrpList.add(bookmarkTimeDirectorNotif);
		}

		// CSEC01D
		String workerPhone = !ObjectUtils.isEmpty(facilityInvRepDto.getEmployeePersPhNameDto())
				? TypeConvUtil.formatPhone(facilityInvRepDto.getEmployeePersPhNameDto().getNbrPhone())
				: null;
		BookmarkDto bookmarkNbrPhone = createBookmark(BookmarkConstants.WORKER_PHONE, workerPhone);
		bookmarkNonFormGrpList.add(bookmarkNbrPhone);

		PreFillDataServiceDto preFillData = new PreFillDataServiceDto();
		preFillData.setFormDataGroupList(formDataGroupList);
		preFillData.setBookmarkDtoList(bookmarkNonFormGrpList);

		return preFillData;

	}

}
