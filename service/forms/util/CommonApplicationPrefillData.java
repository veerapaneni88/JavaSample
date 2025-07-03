package us.tx.state.dfps.service.forms.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.populateletter.dto.CaseInfoDto;
import us.tx.state.dfps.service.admin.dto.AddrPersonLinkPhoneOutDto;
import us.tx.state.dfps.service.admin.dto.LegalStatusPersonMaxStatusDtOutDto;
import us.tx.state.dfps.service.common.BookmarkConstants;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.FormConstants;
import us.tx.state.dfps.service.common.FormGroupsConstants;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.forms.dto.BlobDataDto;
import us.tx.state.dfps.service.forms.dto.BookmarkDto;
import us.tx.state.dfps.service.forms.dto.FormDataGroupDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.person.dto.PersonDtlDto;
import us.tx.state.dfps.service.person.dto.PersonIdDto;
import us.tx.state.dfps.service.placement.dto.AllegationCpsInvstDtlDto;
import us.tx.state.dfps.service.placement.dto.CommonApplicationDto;
import us.tx.state.dfps.service.placement.dto.PersonLocDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:*
 * CommonApplicationPrefillData will implemented returnPrefillData operation
 * defined in DocumentServiceUtil Interface to populate the prefill data for
 * form csc11o00. Feb 9, 2018- 2:19:28 PM © 2017 Texas Department of Family and
 * Protective Services
 */
@Component
public class CommonApplicationPrefillData extends DocumentServiceUtil {

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
		CommonApplicationDto commonApplicationDto = (CommonApplicationDto) parentDtoobj;

		if (null == commonApplicationDto.getPersonBlocLocDto()) {
			commonApplicationDto.setPersonBlocLocDto(new PersonLocDto());
		}
		if (null == commonApplicationDto.getPersonRlocLocDto()) {
			commonApplicationDto.setPersonRlocLocDto(new PersonLocDto());
		}
		if (null == commonApplicationDto.getPersonDtlDto()) {
			commonApplicationDto.setPersonDtlDto(new PersonDtlDto());
		}
		if (ObjectUtils.isEmpty(commonApplicationDto.getPersonIdDto())) {
			commonApplicationDto.setPersonIdDto(new PersonIdDto());
		}
		List<FormDataGroupDto> formDataGroupList = new ArrayList<FormDataGroupDto>();
		/**
		 * Checking the cdStagePersRole type not equal to PC. If equals set the
		 * prefill data for group csc11o02.
		 */
		if (commonApplicationDto.getCaseInfoDtolist().size() > ServiceConstants.Zero) {
			for (CaseInfoDto caseInfoDto : commonApplicationDto.getCaseInfoDtolist()) {
				if (!ServiceConstants.PRIMARY_CHILD.equalsIgnoreCase(caseInfoDto.getCdStagePersRole())) {

					List<FormDataGroupDto> tempPrnInfoFrmDataGroupList = new ArrayList<FormDataGroupDto>();

					FormDataGroupDto tempPrnInfoFrmDataGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_PRN_INFO,
							FormConstants.EMPTY_STRING);
					/**
					 * Populate the Bookmark List field values for group
					 * csc11o02 from DAM CLSC01D
					 */

					FormDataGroupDto tempHomeYesCheckFrmDataGrpDto = createFormDataGroup(
							FormGroupsConstants.TMPLAT_HOME_YES_CHECKBOX, FormGroupsConstants.TMPLAT_PRN_INFO);
					tempPrnInfoFrmDataGroupList.add(tempHomeYesCheckFrmDataGrpDto);
					FormDataGroupDto tempHomeNoCheckFrmDataGrpDto = createFormDataGroup(
							FormGroupsConstants.TMPLAT_HOME_NO_CHECKBOX, FormGroupsConstants.TMPLAT_PRN_INFO);
					tempPrnInfoFrmDataGroupList.add(tempHomeNoCheckFrmDataGrpDto);
					FormDataGroupDto tempInvYesCheckFrmDataGrpDto = createFormDataGroup(
							FormGroupsConstants.TMPLAT_INV_YES_CHECKBOX, FormGroupsConstants.TMPLAT_PRN_INFO);
					tempPrnInfoFrmDataGroupList.add(tempInvYesCheckFrmDataGrpDto);
					FormDataGroupDto tempInvNoCheckFrmDataGrpDto = createFormDataGroup(
							FormGroupsConstants.TMPLAT_INV_NO_CHECKBOX, FormGroupsConstants.TMPLAT_PRN_INFO);
					tempPrnInfoFrmDataGroupList.add(tempInvNoCheckFrmDataGrpDto);

					List<BookmarkDto> bookmarkPrnInfoList = new ArrayList<BookmarkDto>();
					BookmarkDto bookmarkdtPersBirthDto = createBookmark(BookmarkConstants.PRINCIPAL_DTL_DOB,
							TypeConvUtil.formDateFormat(caseInfoDto.getDtPersonBirth()));
					bookmarkPrnInfoList.add(bookmarkdtPersBirthDto);
					BookmarkDto bookmarkdtPersDeathDto = createBookmark(BookmarkConstants.PRINCIPAL_DTL_DOD,
							TypeConvUtil.formDateFormat(caseInfoDto.getDtPersonDeath()));
					bookmarkPrnInfoList.add(bookmarkdtPersDeathDto);
					BookmarkDto bookmarkAddrZipDto = createBookmark(BookmarkConstants.PRINCIPAL_ADDR_ZIP,
							caseInfoDto.getAddrPersonAddrZip());
					bookmarkPrnInfoList.add(bookmarkAddrZipDto);
					BookmarkDto bookmarkAddrCityDto = createBookmark(BookmarkConstants.PRINCIPAL_ADDR_CITY,
							caseInfoDto.getAddrPersonAddrCity());
					bookmarkPrnInfoList.add(bookmarkAddrCityDto);
					BookmarkDto bookmarkPersAddrStLn1Dto = createBookmark(BookmarkConstants.PRINCIPAL_ADDR_STREET_1,
							caseInfoDto.getAddrPersAddrStLn1());
					bookmarkPrnInfoList.add(bookmarkPersAddrStLn1Dto);
					BookmarkDto bookmarkPersAddrStLn2Dto = createBookmark(BookmarkConstants.PRINCIPAL_ADDR_STREET_2,
							caseInfoDto.getAddrPersAddrStLn2());
					bookmarkPrnInfoList.add(bookmarkPersAddrStLn2Dto);
					BookmarkDto bookmarkCdAddrStateDto = createBookmark(BookmarkConstants.PRINCIPAL_ADDR_STATE,
							caseInfoDto.getCdPersonAddrState());
					bookmarkPrnInfoList.add(bookmarkCdAddrStateDto);
					BookmarkDto bookmarkCdNameSufDto = createBookmarkWithCodesTable(
							BookmarkConstants.PRINCIPAL_DTL_NAME_SUFFIX, caseInfoDto.getCdNameSuffix(),
							CodesConstant.CSUFFIX2);
					bookmarkPrnInfoList.add(bookmarkCdNameSufDto);
					BookmarkDto bookmarkCdSatgeRelIntDto = createBookmarkWithCodesTable(
							BookmarkConstants.PRINCIPAL_DTL_REL_INT, caseInfoDto.getCdStagePersRelInt(),
							CodesConstant.CRPTRINT);
					bookmarkPrnInfoList.add(bookmarkCdSatgeRelIntDto);
					BookmarkDto bookmarkNmNameFirstDto = createBookmark(BookmarkConstants.PRINCIPAL_DTL_NAME_FIRST,
							caseInfoDto.getNmNameFirst());
					bookmarkPrnInfoList.add(bookmarkNmNameFirstDto);
					BookmarkDto bookmarkNmNameLastDto = createBookmark(BookmarkConstants.PRINCIPAL_DTL_NAME_LAST,
							caseInfoDto.getNmNameLast());
					bookmarkPrnInfoList.add(bookmarkNmNameLastDto);
					BookmarkDto bookmarkNmNameMidDto = createBookmark(BookmarkConstants.PRINCIPAL_DTL_NAME_MIDDLE,
							caseInfoDto.getNmNameMiddle());
					bookmarkPrnInfoList.add(bookmarkNmNameMidDto);
					BookmarkDto bookmarkIdPersonDto = createBookmark(BookmarkConstants.UE_GROUPID,
							caseInfoDto.getIdPerson());
					bookmarkPrnInfoList.add(bookmarkIdPersonDto);
					tempPrnInfoFrmDataGrpDto.setBookmarkDtoList(bookmarkPrnInfoList);
					tempPrnInfoFrmDataGrpDto.setFormDataGroupList(tempPrnInfoFrmDataGroupList);
					formDataGroupList.add(tempPrnInfoFrmDataGrpDto);
				}
			}
		}

		/**
		 * Populate the Bookmark List field values for group csc11o01 from DAM
		 * CLSS29D
		 */
		if (!ObjectUtils.isEmpty(commonApplicationDto.getAllegationCpsInvstDtlDtoList())
				&& commonApplicationDto.getAllegationCpsInvstDtlDtoList().size() > ServiceConstants.Zero) {
			for (AllegationCpsInvstDtlDto allegationCpsInvstDtlDto : commonApplicationDto
					.getAllegationCpsInvstDtlDtoList()) {
				// Set prefill data for the group - csc11o01
				FormDataGroupDto tempAllegationFrmDataGrpDto = createFormDataGroup(
						FormGroupsConstants.TMPLAT_ALLEGATION, FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkAllegationList = new ArrayList<BookmarkDto>();
				BookmarkDto bookmarkCdAllegDispDto = createBookmarkWithCodesTable(BookmarkConstants.ALLEG_DTL_DISP,
						allegationCpsInvstDtlDto.getCdAllegDisposition(), CodesConstant.CDISPSTN);
				bookmarkAllegationList.add(bookmarkCdAllegDispDto);
				BookmarkDto bookmarkCdAllegTypeDto = createBookmarkWithCodesTable(BookmarkConstants.ALLEG_DTL_ALLEG,
						allegationCpsInvstDtlDto.getCdAllegType(), CodesConstant.CABALTYP);
				bookmarkAllegationList.add(bookmarkCdAllegTypeDto);
				tempAllegationFrmDataGrpDto.setBookmarkDtoList(bookmarkAllegationList);
				formDataGroupList.add(tempAllegationFrmDataGrpDto);
			}
		}
		/**
		 * Populating the non form group data into prefill data
		 */
		List<BookmarkDto> bookmarkNonFrmGrpList = new ArrayList<BookmarkDto>();
		List<BlobDataDto> blobDataDtoNonFrmGrpList = new ArrayList<BlobDataDto>();
		// Populate dtPersonBirth value from DAM CSEC15D
		BookmarkDto bookmarkDtpersonBirth = createBookmark(BookmarkConstants.TITLE_1_CHILD_DOB,
				TypeConvUtil.formDateFormat(commonApplicationDto.getStagePersonLinkCaseDto().getDtPersonBirth()));
		bookmarkNonFrmGrpList.add(bookmarkDtpersonBirth);
		// Populate dtCurrent value from DAM CSEC15D
		BookmarkDto bookmarkCurrentDate = createBookmark(BookmarkConstants.DATE_COMPLETED,
				TypeConvUtil.formDateFormat(commonApplicationDto.getStagePersonLinkCaseDto().getDtCurrent()));
		bookmarkNonFrmGrpList.add(bookmarkCurrentDate);
		// Populate cdNameSuffix value from DAM CSEC15D
		BookmarkDto bookmarkCdNameSuffix = createBookmarkWithCodesTable(BookmarkConstants.TITLE_1_CHILD_NAME_SUFFIX,
				commonApplicationDto.getStagePersonLinkCaseDto().getCdPersonSuffix(), CodesConstant.CSUFFIX2);
		bookmarkNonFrmGrpList.add(bookmarkCdNameSuffix);
		// Populate cdStage value from DAM CSEC15D
		BookmarkDto bookmarkCdStage = createBookmark(BookmarkConstants.ABUSE_NEG_YES,
				commonApplicationDto.getStagePersonLinkCaseDto().getCdStage());
		bookmarkNonFrmGrpList.add(bookmarkCdStage);
		// Populate cdStageProgram value from DAM CSEC15D
		BookmarkDto bookmarkCdStagePrg = createBookmark(BookmarkConstants.ABUSE_NEG_UNKNOWN,
				commonApplicationDto.getStagePersonLinkCaseDto().getCdStageProgram());
		bookmarkNonFrmGrpList.add(bookmarkCdStagePrg);
		// Populate cdStageType value from DAM CSEC15D
		BookmarkDto bookmarkCdStageType = createBookmark(BookmarkConstants.ABUSE_NEG_NO,
				commonApplicationDto.getStagePersonLinkCaseDto().getCdStageType());
		bookmarkNonFrmGrpList.add(bookmarkCdStageType);
		// Populate nmCase value from DAM CSEC15D
		BookmarkDto bookmarkNmCase = createBookmark(BookmarkConstants.TITLE_1_CASE_NAME,
				commonApplicationDto.getStagePersonLinkCaseDto().getNmCase());
		bookmarkNonFrmGrpList.add(bookmarkNmCase);
		// Populate nmNameFirst value from DAM CSEC15D
		BookmarkDto bookmarkNmNameFirst = createBookmark(BookmarkConstants.TITLE_1_CHILD_NAME_FIRST,
				commonApplicationDto.getStagePersonLinkCaseDto().getNmPersonFirst());
		bookmarkNonFrmGrpList.add(bookmarkNmNameFirst);
		// Populate nmNameLast value from DAM CSEC15D
		BookmarkDto bookmarkNmNameLast = createBookmark(BookmarkConstants.TITLE_1_CHILD_NAME_LAST,
				commonApplicationDto.getStagePersonLinkCaseDto().getNmPersonLast());
		bookmarkNonFrmGrpList.add(bookmarkNmNameLast);
		// Populate nmNameMid value from DAM CSEC15D
		BookmarkDto bookmarkNmNameMid = createBookmark(BookmarkConstants.TITLE_1_CHILD_NAME_MIDDLE,
				commonApplicationDto.getStagePersonLinkCaseDto().getNmPersonMiddle());
		bookmarkNonFrmGrpList.add(bookmarkNmNameMid);
		// Populate idCase value from DAM CSEC15D
		BookmarkDto bookmarkIdCase = createBookmark(BookmarkConstants.TITLE_1_CASE_ID,
				commonApplicationDto.getStagePersonLinkCaseDto().getIdCase());
		bookmarkNonFrmGrpList.add(bookmarkIdCase);
		// Populate idPerson value from DAM CSEC15D
		BookmarkDto bookmarkIdPerson = createBookmark(BookmarkConstants.CHILD_AGENCY_ID,
				commonApplicationDto.getStagePersonLinkCaseDto().getIdPerson());
		bookmarkNonFrmGrpList.add(bookmarkIdPerson);
		if (commonApplicationDto.getChildLegalStatusDtoList().size() > ServiceConstants.Zero) {
			for (LegalStatusPersonMaxStatusDtOutDto childLegalStatusDtoList : commonApplicationDto
					.getChildLegalStatusDtoList()) {
				// Populate idPerson value from DAM CSEC15D
				BookmarkDto bookmarkdtLegalStatStatus = createBookmark(BookmarkConstants.LEGAL_STATUS_DATE,
						TypeConvUtil.formDateFormat(childLegalStatusDtoList.getDtLegalStatStatusDt()));
				bookmarkNonFrmGrpList.add(bookmarkdtLegalStatStatus);
				// Populate cdLegalStatus value from DAM CSES32D
				BookmarkDto bookmarkCdLegalStat = createBookmarkWithCodesTable(BookmarkConstants.LEGAL_STATUS,
						childLegalStatusDtoList.getCdLegalStatStatus(), CodesConstant.CLEGSTAT);
				bookmarkNonFrmGrpList.add(bookmarkCdLegalStat);
			}
		}
		// Populate nbrPhone value from DAM CSEC01D
		BookmarkDto bookmarkNbrPhone = createBookmark(BookmarkConstants.WORKER_PHONE_NBR,
				TypeConvUtil.formatPhone(commonApplicationDto.getEmployeePersPhNameDto().getNbrPhone()));
		bookmarkNonFrmGrpList.add(bookmarkNbrPhone);
		// Populate nbrPhoneExtn value from DAM CSEC01D
		BookmarkDto bookmarkNbrPhoneExtn = createBookmark(BookmarkConstants.WORKER_PHONE_EXTENSION,
				TypeConvUtil.formatPhone(commonApplicationDto.getEmployeePersPhNameDto().getNbrPhoneExtension()));
		bookmarkNonFrmGrpList.add(bookmarkNbrPhoneExtn);
		// Populate addrmailCodeCity value from DAM CSEC01D
		BookmarkDto bookmarkAddrMailCity = createBookmark(BookmarkConstants.WORKER_ADDR_CITY,
				commonApplicationDto.getEmployeePersPhNameDto().getAddrMailCodeCity());
		bookmarkNonFrmGrpList.add(bookmarkAddrMailCity);
		// Populate AddrMailStLn1 value from DAM CSEC01D
		BookmarkDto bookmarkAddrMailStLn1 = createBookmark(BookmarkConstants.WORKER_ADDR_ST_LN1,
				commonApplicationDto.getEmployeePersPhNameDto().getAddrMailCodeStLn1());
		bookmarkNonFrmGrpList.add(bookmarkAddrMailStLn1);
		// Populate AddrMailStLn2 value from DAM CSEC01D
		BookmarkDto bookmarkAddrMailStLn2 = createBookmark(BookmarkConstants.WORKER_ADDR_ST_LN2,
				commonApplicationDto.getEmployeePersPhNameDto().getAddrMailCodeStLn2());
		bookmarkNonFrmGrpList.add(bookmarkAddrMailStLn2);
		// Populate AddrMailZip value from DAM CSEC01D
		BookmarkDto bookmarkAddrMailZip = createBookmark(BookmarkConstants.WORKER_ADDR_ZIP,
				commonApplicationDto.getEmployeePersPhNameDto().getAddrMailCodeZip());
		bookmarkNonFrmGrpList.add(bookmarkAddrMailZip);
		// Populate cdJobClassDecode value from DAM CSEC01D
		BookmarkDto bookmarkCdJobClassDecode = createBookmark(BookmarkConstants.WORKER_COMP_TITLE,
				commonApplicationDto.getEmployeePersPhNameDto().getTxtJobDesc());
		bookmarkNonFrmGrpList.add(bookmarkCdJobClassDecode);
		// Populate cdNameSuffix value from DAM CSEC01D
		BookmarkDto bookmarkCdNameSufx = createBookmarkWithCodesTable(BookmarkConstants.WORKER_NAME_SUFFIX,
				commonApplicationDto.getEmployeePersPhNameDto().getCdNameSuffix(), CodesConstant.CSUFFIX2);
		bookmarkNonFrmGrpList.add(bookmarkCdNameSufx);
		// Populate NmNameFirst value from DAM CSEC01D
		BookmarkDto bookmarkNameFirst = createBookmark(BookmarkConstants.WORKER_NAME_FIRST,
				commonApplicationDto.getEmployeePersPhNameDto().getNmNameFirst());
		bookmarkNonFrmGrpList.add(bookmarkNameFirst);
		// Populate NmNameLast value from DAM CSEC01D
		BookmarkDto bookmarkNameLast = createBookmark(BookmarkConstants.WORKER_NAME_LAST,
				commonApplicationDto.getEmployeePersPhNameDto().getNmNameLast());
		bookmarkNonFrmGrpList.add(bookmarkNameLast);
		// Populate NmNameMiddle value from DAM CSEC01D
		BookmarkDto bookmarkNameMid = createBookmark(BookmarkConstants.WORKER_NAME_MIDDLE,
				commonApplicationDto.getEmployeePersPhNameDto().getNmNameMiddle());
		bookmarkNonFrmGrpList.add(bookmarkNameMid);
		// Populate cdPlocChild value from DAM CSES35D
		BookmarkDto bookmarkCdPlocChild = createBookmarkWithCodesTable(BookmarkConstants.CHILD_BILLING_LOC,
				commonApplicationDto.getPersonBlocLocDto().getCdPlocChild(), CodesConstant.CBILPLOC);
		bookmarkNonFrmGrpList.add(bookmarkCdPlocChild);
		// Populate cdPlocChild value from DAM CSES35D
		BookmarkDto bookmarkCdPlocChildBillLoc = createBookmarkWithCodesTable(BookmarkConstants.BILLING_LOC,
				commonApplicationDto.getPersonBlocLocDto().getCdPlocChild(), CodesConstant.CBILPLOC);
		bookmarkNonFrmGrpList.add(bookmarkCdPlocChildBillLoc);
		// Populate qtyPersonWeight value from DAM CSES31D
		if (ObjectUtils.isEmpty(commonApplicationDto.getPersonDtlDto().getQtyPersonWeight())) {
			BookmarkDto bookmarkQtyPersonWeight = createBookmark(BookmarkConstants.CHILD_WEIGHT, ServiceConstants.Zero);
			bookmarkNonFrmGrpList.add(bookmarkQtyPersonWeight);
		} else {
			BookmarkDto bookmarkQtyPersonWeight = createBookmark(BookmarkConstants.CHILD_WEIGHT,
					commonApplicationDto.getPersonDtlDto().getQtyPersonWeight());
			bookmarkNonFrmGrpList.add(bookmarkQtyPersonWeight);
		}
		// Populate qtyPersonHeight value from DAM CSES31D
		if (ObjectUtils.isEmpty(commonApplicationDto.getPersonDtlDto().getQtyPersonHeightFeet())) {
			BookmarkDto bookmarkQtyPersonHeight = createBookmark(BookmarkConstants.CHILD_HEIGHT_FEET,
					ServiceConstants.Zero);
			bookmarkNonFrmGrpList.add(bookmarkQtyPersonHeight);
		} else {
			BookmarkDto bookmarkQtyPersonHeight = createBookmark(BookmarkConstants.CHILD_HEIGHT_FEET,
					commonApplicationDto.getPersonDtlDto().getQtyPersonHeightFeet());
			bookmarkNonFrmGrpList.add(bookmarkQtyPersonHeight);
		}
		// Populate qtyPersonInches value from DAM CSES31D

		if (ObjectUtils.isEmpty(commonApplicationDto.getPersonDtlDto().getQtyPersonHeightInches())) {
			BookmarkDto bookmarkQtyPersonInchces = createBookmark(BookmarkConstants.CHILD_HEIGHT_INCHES,
					ServiceConstants.Zero);
			bookmarkNonFrmGrpList.add(bookmarkQtyPersonInchces);
		} else {
			BookmarkDto bookmarkQtyPersonInchces = createBookmark(BookmarkConstants.CHILD_HEIGHT_INCHES,
					commonApplicationDto.getPersonDtlDto().getQtyPersonHeightInches());
			bookmarkNonFrmGrpList.add(bookmarkQtyPersonInchces);
		}

		// Populate cdPersonBirthCity value from DAM CSES31D
		BookmarkDto bookmarkCdPersonBirthCity = createBookmark(BookmarkConstants.CHILD_BIRTH_CITY,
				commonApplicationDto.getPersonDtlDto().getCdPersonBirthCity());
		bookmarkNonFrmGrpList.add(bookmarkCdPersonBirthCity);
		// Populate cdPersonBirthCountry value from DAM CSES31D
		BookmarkDto bookmarkCdPersonBirthCountry = createBookmarkWithCodesTable(BookmarkConstants.CHILD_BIRTH_COUNTRY,
				commonApplicationDto.getPersonDtlDto().getCdPersonBirthCountry(), CodesConstant.CCOUNTRY);
		bookmarkNonFrmGrpList.add(bookmarkCdPersonBirthCountry);
		// Populate cdPersonBirthState value from DAM CSES31D
		BookmarkDto bookmarkCdPersonBirthState = createBookmarkWithCodesTable(BookmarkConstants.CHILD_BIRTH_STATE,
				commonApplicationDto.getPersonDtlDto().getCdPersonBirthState(), CodesConstant.CSTATE);
		bookmarkNonFrmGrpList.add(bookmarkCdPersonBirthState);
		// Populate cdPersonBirthCitizenship value from DAM CSES31D
		BookmarkDto bookmarkCdPersonBirthCitizenship = createBookmarkWithCodesTable(BookmarkConstants.CHILD_CITIZEN,
				commonApplicationDto.getPersonDtlDto().getCdPersonCitizenship(), CodesConstant.CCTZNSTA);
		bookmarkNonFrmGrpList.add(bookmarkCdPersonBirthCitizenship);
		if (!ObjectUtils.isEmpty(commonApplicationDto.getEventChildPlanDto())) {
			// Populate idEventCpPsyNarr value from DAM CSEC20D
			BlobDataDto blobDataDtoIdEventPsyNarr = createBlobData(BookmarkConstants.CP_NARR_PSYCH_NEEDS, "CP_PSY_NARR",
					commonApplicationDto.getEventChildPlanDto().getIdEvent().intValue());
			blobDataDtoNonFrmGrpList.add(blobDataDtoIdEventPsyNarr);
			// Populate idEventCpSenNarr value from DAM CSEC20D
			BlobDataDto blobDataDtoIdEventSenNarr = createBlobData(BookmarkConstants.CP_NARR_SOC_EM_NEEDS,
					"CP_SEN_NARR", commonApplicationDto.getEventChildPlanDto().getIdEvent().intValue());
			blobDataDtoNonFrmGrpList.add(blobDataDtoIdEventSenNarr);
			// Populate idEventCpDvnNarr value from DAM CSEC20D
			BlobDataDto blobDataDtoIdEventDvnNarr = createBlobData(BookmarkConstants.CP_NARR_DEV_NEEDS, "CP_DVN_NARR",
					commonApplicationDto.getEventChildPlanDto().getIdEvent().intValue());
			blobDataDtoNonFrmGrpList.add(blobDataDtoIdEventDvnNarr);
			// Populate idEventCpIshNarr2 value from DAM CSEC20D
			BlobDataDto blobDataDtoIdEventIshNarr2 = createBlobData(BookmarkConstants.CP_NARR_INITIAL_SOCIAL_2,
					"CP_ISH_NARR", commonApplicationDto.getEventChildPlanDto().getIdEvent().intValue());
			blobDataDtoNonFrmGrpList.add(blobDataDtoIdEventIshNarr2);
			// Populate idEventCpPhyNarr value from DAM CSEC20D
			BlobDataDto blobDataDtoIdEventPhyNarr = createBlobData(BookmarkConstants.CP_NARR_PHYSICAL_NEEDS,
					"CP_PHY_NARR", commonApplicationDto.getEventChildPlanDto().getIdEvent().intValue());
			blobDataDtoNonFrmGrpList.add(blobDataDtoIdEventPhyNarr);
			// Populate idEventCpEdnNarr value from DAM CSEC20D
			BlobDataDto blobDataDtoIdEventEdnNarr = createBlobData(BookmarkConstants.CP_NARR_EDUC_NEEDS, "CP_EDN_NARR",
					commonApplicationDto.getEventChildPlanDto().getIdEvent().intValue());
			blobDataDtoNonFrmGrpList.add(blobDataDtoIdEventEdnNarr);
			// Populate idEventCpMdnNarr value from DAM CSEC20D
			BlobDataDto blobDataDtoIdEventMdnNarr = createBlobData(BookmarkConstants.CP_NARR_MEDICAL_NEEDS,
					"CP_MDN_NARR", commonApplicationDto.getEventChildPlanDto().getIdEvent().intValue());
			blobDataDtoNonFrmGrpList.add(blobDataDtoIdEventMdnNarr);
			// Populate idEventCpPsyNarr1 value from DAM CSEC20D
			BlobDataDto blobDataDtoIdEventIshNarr1 = createBlobData(BookmarkConstants.CP_NARR_INITIAL_SOCIAL_1,
					"CP_ISH_NARR", commonApplicationDto.getEventChildPlanDto().getIdEvent().intValue());
			blobDataDtoNonFrmGrpList.add(blobDataDtoIdEventIshNarr1);
		}
		// Populate cdPlocChildRLoc value from DAM CSES35D
		BookmarkDto bookmarkCdPlocChildRLoc = createBookmarkWithCodesTable(BookmarkConstants.RECOMMENDED_LOC,
				commonApplicationDto.getPersonRlocLocDto().getCdPlocChild(), CodesConstant.CREQPLOC);
		bookmarkNonFrmGrpList.add(bookmarkCdPlocChildRLoc);
		// Populate cdNameSuffixWrk value from DAM CSEC01D

		BookmarkDto bookmarkCdNameSufxWrk = createBookmarkWithCodesTable(BookmarkConstants.WORKER_COMP_NM_SUFFIX,
				commonApplicationDto.getEmployeePersPhNameDtoPW() != null
						? commonApplicationDto.getEmployeePersPhNameDtoPW().getCdNameSuffix() : null,
				CodesConstant.CSUFFIX2);
		bookmarkNonFrmGrpList.add(bookmarkCdNameSufxWrk);
		// Populate NmNameFirstWrk value from DAM CSEC01D
		BookmarkDto bookmarkNameFirstWrk = createBookmark(BookmarkConstants.WORKER_COMP_NM_FIRST,
				commonApplicationDto.getEmployeePersPhNameDtoPW() != null
						? commonApplicationDto.getEmployeePersPhNameDtoPW().getNmNameFirst() : "");
		bookmarkNonFrmGrpList.add(bookmarkNameFirstWrk);
		// Populate NmNameLastWrk value from DAM CSEC01D
		BookmarkDto bookmarkNameLastWrk = createBookmark(BookmarkConstants.WORKER_COMP_NM_LAST,
				commonApplicationDto.getEmployeePersPhNameDtoPW() != null
						? commonApplicationDto.getEmployeePersPhNameDtoPW().getNmNameLast() : "");
		bookmarkNonFrmGrpList.add(bookmarkNameLastWrk);
		// Populate NmNameMiddleWrk value from DAM CSEC01D
		BookmarkDto bookmarkNameMidWrk = createBookmark(BookmarkConstants.WORKER_COMP_NM_MIDDLE,
				commonApplicationDto.getEmployeePersPhNameDtoPW() != null
						? commonApplicationDto.getEmployeePersPhNameDtoPW().getNmNameMiddle() : "");
		bookmarkNonFrmGrpList.add(bookmarkNameMidWrk);
		// Populate cdNameSufx1 value from DAM CSEC35D
		BookmarkDto bookmarkCdNameSufx1 = createBookmarkWithCodesTable(BookmarkConstants.CHILD_NAME_SUFFIX_1,
				commonApplicationDto.getNameDetailDto().getCdNameSuffix(), CodesConstant.CSUFFIX2);
		bookmarkNonFrmGrpList.add(bookmarkCdNameSufx1);
		// Populate cdNameSufx2 value from DAM CSEC35D
		BookmarkDto bookmarkCdNameSufx2 = createBookmarkWithCodesTable(BookmarkConstants.CHILD_NAME_SUFFIX,
				commonApplicationDto.getNameDetailDto().getCdNameSuffix(), CodesConstant.CSUFFIX2);
		bookmarkNonFrmGrpList.add(bookmarkCdNameSufx2);
		// Populate nmNameFirst1 value from DAM CSEC35D
		BookmarkDto bookmarkNmNameFirst1 = createBookmark(BookmarkConstants.CHILD_NAME_FIRST_1,
				commonApplicationDto.getNameDetailDto().getNmNameFirst());
		bookmarkNonFrmGrpList.add(bookmarkNmNameFirst1);
		// Populate nmNameFirst value from DAM CSEC35D
		BookmarkDto bookmarkNameFirstNm = createBookmark(BookmarkConstants.CHILD_NAME_FIRST,
				commonApplicationDto.getNameDetailDto().getNmNameFirst());
		bookmarkNonFrmGrpList.add(bookmarkNameFirstNm);
		// Populate nmNameLast value from DAM CSEC35D
		BookmarkDto bookmarkNameLastNm = createBookmark(BookmarkConstants.CHILD_NAME_LAST,
				commonApplicationDto.getNameDetailDto().getNmNameLast());
		bookmarkNonFrmGrpList.add(bookmarkNameLastNm);
		// Populate nmNameLast1 value from DAM CSEC35D
		BookmarkDto bookmarkNmNameLast1 = createBookmark(BookmarkConstants.CHILD_NAME_LAST_1,
				commonApplicationDto.getNameDetailDto().getNmNameLast());
		bookmarkNonFrmGrpList.add(bookmarkNmNameLast1);
		// Populate nmNameMiddle1 value from DAM CSEC35D
		BookmarkDto bookmarkNmNameMiddle1 = createBookmark(BookmarkConstants.CHILD_NAME_MIDDLE_1,
				commonApplicationDto.getNameDetailDto().getNmNameMiddle());
		bookmarkNonFrmGrpList.add(bookmarkNmNameMiddle1);
		// Populate nmNameMiddle value from DAM CSEC35D
		BookmarkDto bookmarkNameMiddelNm = createBookmark(BookmarkConstants.CHILD_NAME_MIDDLE,
				commonApplicationDto.getNameDetailDto().getNmNameMiddle());
		bookmarkNonFrmGrpList.add(bookmarkNameMiddelNm);
		// Populate cdPersSex value from DAM CCMN44D
		BookmarkDto bookmarkCdPersSex = createBookmarkWithCodesTable(BookmarkConstants.CHILD_NAME_SEX,
				commonApplicationDto.getPersonDto().getCdPersonSex(), CodesConstant.CSEX);
		bookmarkNonFrmGrpList.add(bookmarkCdPersSex);
		// Populate dtPersBirth value from DAM CCMN44D
		BookmarkDto bookmarkdtPersBirth = createBookmark(BookmarkConstants.CHILD_NAME_DOB,
				TypeConvUtil.formDateFormat(commonApplicationDto.getPersonDto().getDtPersonBirth()));
		bookmarkNonFrmGrpList.add(bookmarkdtPersBirth);
		// Populate nbrPersAge value from DAM CCMN44D
		BookmarkDto bookmarkNbrPersAge = createBookmark(BookmarkConstants.CHILD_NAME_AGE,
				commonApplicationDto.getPersonDto().getNbrPersonAge());
		bookmarkNonFrmGrpList.add(bookmarkNbrPersAge);
		// Populate cdPersEthnicGrp value from DAM CCMN44D
		BookmarkDto bookmarkCdPersEthnicGrp = createBookmarkWithCodesTable(BookmarkConstants.CHILD_NAME_ETHNIC,
				commonApplicationDto.getPersonDto().getCdPersonEthnicGroup(), CodesConstant.CETHNIC);
		bookmarkNonFrmGrpList.add(bookmarkCdPersEthnicGrp);
		// Populate cdPersLang value from DAM CCMN44D
		BookmarkDto bookmarkCdPersLang = createBookmarkWithCodesTable(BookmarkConstants.CHILD_LANG,
				commonApplicationDto.getPersonDto().getCdPersonLanguage(), CodesConstant.CLANG);
		bookmarkNonFrmGrpList.add(bookmarkCdPersLang);
		// Populate CdPersLivArr value from DAM CCMN44D
		BookmarkDto bookmarkCdPersLivArr = createBookmark(BookmarkConstants.PAL,
				commonApplicationDto.getPersonDto().getCdPersonLivArr());
		bookmarkNonFrmGrpList.add(bookmarkCdPersLivArr);
		// Populate cdPersReligion value from DAM CCMN44D
		BookmarkDto bookmarkCdPersReligion = createBookmarkWithCodesTable(BookmarkConstants.CHILD_RELIGION,
				commonApplicationDto.getPersonDto().getCdPersonReligion(), CodesConstant.CRELIGNS);
		bookmarkNonFrmGrpList.add(bookmarkCdPersReligion);
		// Populate PersonIdnumber value from DAM CCMN72D
		BookmarkDto bookmarkPersonIdnumber = createBookmark(BookmarkConstants.CHILD_NAME_SSN,
				commonApplicationDto.getPersonIdDto().getPersonIdNumber());
		bookmarkNonFrmGrpList.add(bookmarkPersonIdnumber);
		if (commonApplicationDto.getAddrPersonLinkPhoneOutDtoList().size() > ServiceConstants.Zero) {
			for (AddrPersonLinkPhoneOutDto addrPersonLinkPhoneOutDto : commonApplicationDto
					.getAddrPersonLinkPhoneOutDtoList()) {
				// Populate addrZip value from DAM CINV46D
				BookmarkDto bookmarkAddrZip = createBookmark(BookmarkConstants.HOME_ADDR_ZIP,
						addrPersonLinkPhoneOutDto.getAddrZip());
				bookmarkNonFrmGrpList.add(bookmarkAddrZip);
				// Populate Phone value from DAM CINV46D
				BookmarkDto bookmarkPhone = createBookmark(BookmarkConstants.HOME_PHONE,
						TypeConvUtil.formatPhone(addrPersonLinkPhoneOutDto.getPhone()));
				bookmarkNonFrmGrpList.add(bookmarkPhone);
				// Populate addrCity value from DAM CINV46D
				BookmarkDto bookmarkAddrCity = createBookmark(BookmarkConstants.HOME_ADDR_CITY,
						addrPersonLinkPhoneOutDto.getAddrCity());
				bookmarkNonFrmGrpList.add(bookmarkAddrCity);
				// Populate addrstLn1 value from DAM CINV46D
				BookmarkDto bookmarkAddrstLn1 = createBookmark(BookmarkConstants.HOME_ADDR_ST_1,
						addrPersonLinkPhoneOutDto.getAddrPersAddrStLn1());
				bookmarkNonFrmGrpList.add(bookmarkAddrstLn1);
				// Populate addrstLn2 value from DAM CINV46D
				BookmarkDto bookmarkAddrstLn2 = createBookmark(BookmarkConstants.HOME_ADDR_ST_2,
						addrPersonLinkPhoneOutDto.getAddrPersAddrStLn2());
				bookmarkNonFrmGrpList.add(bookmarkAddrstLn2);
				// Populate addrstate value from DAM CINV46D
				BookmarkDto bookmarkAddrState = createBookmark(BookmarkConstants.HOME_ADDR_STATE,
						addrPersonLinkPhoneOutDto.getCdAddrState());
				bookmarkNonFrmGrpList.add(bookmarkAddrState);
			}
		}

		// Populate cdCharacteristic value from DAM CLSS60D
		BookmarkDto bookmarkCdCharacteristic = createBookmark(BookmarkConstants.SEXAGGRESSIONYES,
				commonApplicationDto.getCharacteristicsDto().getCdCharacCode());
		bookmarkNonFrmGrpList.add(bookmarkCdCharacteristic);
		// Populate CdCharCategory value from DAM CLSS60D
		BookmarkDto bookmarkCdCharCategory = createBookmark(BookmarkConstants.SEXAGGRESSIONNO,
				commonApplicationDto.getCharacteristicsDto().getCdCharacCategory());
		bookmarkNonFrmGrpList.add(bookmarkCdCharCategory);

		PreFillDataServiceDto preFillData = new PreFillDataServiceDto();
		preFillData.setBookmarkDtoList(bookmarkNonFrmGrpList);
		preFillData.setBlobDataDtoList(blobDataDtoNonFrmGrpList);
		preFillData.setFormDataGroupList(formDataGroupList);
		return preFillData;
	}

}
