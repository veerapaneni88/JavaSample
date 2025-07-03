package us.tx.state.dfps.service.forms.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.service.common.BookmarkConstants;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.FormConstants;
import us.tx.state.dfps.service.common.FormGroupsConstants;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.forms.dto.BookmarkDto;
import us.tx.state.dfps.service.forms.dto.FormDataGroupDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.icpforms.dto.IcpFormsDto;
import us.tx.state.dfps.service.icpforms.dto.IcpcChildDetailsDto;
import us.tx.state.dfps.service.icpforms.dto.IcpcInfoDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<This class
 * is for prefilling data from ICPformsService to populate form ICP02O00> May 8,
 * 2018- 11:23:48 AM © 2017 Texas Department of Family and Protective Services
 */

@Component
public class ICplacementStatusPrefillData extends DocumentServiceUtil {

	/**
	 * Method Name: returnPrefillData Method Description: prefill data from
	 * ICPformsServiceImpl for Interstate compact financial and Medical form
	 * 
	 * @param parentDtoobj
	 * @return PreFillDataServiceDto
	 */
	@Override
	public PreFillDataServiceDto returnPrefillData(Object parentDtoobj) {
		IcpFormsDto icpcFormsDto = (IcpFormsDto) parentDtoobj;

		List<FormDataGroupDto> formDataGroupList = new ArrayList<FormDataGroupDto>();
		List<BookmarkDto> bookmarkNonFrmGrpList = new ArrayList<BookmarkDto>();

		// Groups
		if (!ObjectUtils.isEmpty(icpcFormsDto.getIcpcInfoDto())) {

			// Group 02030
			if (!ObjectUtils.isEmpty(icpcFormsDto.getIcpcInfoDto().getCdPlacementStatus())
					&& icpcFormsDto.getIcpcInfoDto().getCdPlacementStatus().equals(ServiceConstants.CPL_30)) {
				FormDataGroupDto withDrawnYes = createFormDataGroup(FormGroupsConstants.TMPLAT_PLCMT_WITHDRAWN_YES,
						FormConstants.EMPTY_STRING);
				formDataGroupList.add(withDrawnYes);
			}

			// Group 02040
			if (!ObjectUtils.isEmpty(icpcFormsDto.getIcpcInfoDto().getCdPlacementStatus())
					&& icpcFormsDto.getIcpcInfoDto().getCdPlacementStatus().equals(ServiceConstants.CPL_10)) {
				FormDataGroupDto initialPlacment = createFormDataGroup(FormGroupsConstants.TMPLAT_INITIAL_PLCMT_YES,
						FormConstants.EMPTY_STRING);
				formDataGroupList.add(initialPlacment);
			}

			// Group 02050
			if (!ObjectUtils.isEmpty(icpcFormsDto.getIcpcInfoDto().getCdPlacementStatus())
					&& icpcFormsDto.getIcpcInfoDto().getCdPlacementStatus().equals(ServiceConstants.CPL_20)) {
				FormDataGroupDto changePlacment = createFormDataGroup(FormGroupsConstants.TMPLAT_PLCMT_CHANGE_YES,
						FormConstants.EMPTY_STRING);
				formDataGroupList.add(changePlacment);
			}

			// Group 02060
			if (!ObjectUtils.isEmpty(icpcFormsDto.getIcpcInfoDto().getCdCompactTermRsn())
					&& icpcFormsDto.getIcpcInfoDto().getCdCompactTermRsn().equals(ServiceConstants.CPL_10)) {
				FormDataGroupDto finalRecieve = createFormDataGroup(FormGroupsConstants.TMPLAT_ADOPT_FINAL_RECIEVE_YES,
						FormConstants.EMPTY_STRING);
				formDataGroupList.add(finalRecieve);
			}

			// Group 02070
			if (!ObjectUtils.isEmpty(icpcFormsDto.getIcpcInfoDto().getCdCompactTermRsn())
					&& icpcFormsDto.getIcpcInfoDto().getCdCompactTermRsn().equals(ServiceConstants.CPL_20)) {
				FormDataGroupDto sendingYes = createFormDataGroup(FormGroupsConstants.TMPLAT_ADOPT_FINAL_SENDING_YES,
						FormConstants.EMPTY_STRING);
				formDataGroupList.add(sendingYes);
			}

			// Group 02080
			if (!ObjectUtils.isEmpty(icpcFormsDto.getIcpcInfoDto().getCdCompactTermRsn())
					&& icpcFormsDto.getIcpcInfoDto().getCdCompactTermRsn().equals(ServiceConstants.CPL_40)) {
				FormDataGroupDto legalYes = createFormDataGroup(FormGroupsConstants.TMPLAT_CHILD_LEGAL_EMANCIP_YES,
						FormConstants.EMPTY_STRING);
				formDataGroupList.add(legalYes);
			}
			//[PD-3953]PD 93195: 100b form not displaying correctly
			if (!ObjectUtils.isEmpty(icpcFormsDto.getIcpcInfoDto().getCdCompactTermRsn())
					&& icpcFormsDto.getIcpcInfoDto().getCdCompactTermRsn().equals(ServiceConstants.CPL_50)) {
				FormDataGroupDto returnSendingYes = createFormDataGroup(FormGroupsConstants.TMPLAT_CHILD_RETURN_SEND_STATE_YES,
						FormConstants.EMPTY_STRING);
				formDataGroupList.add(returnSendingYes);
			}
			if (!ObjectUtils.isEmpty(icpcFormsDto.getIcpcInfoDto().getCdCompactTermRsn())
					&& (icpcFormsDto.getIcpcInfoDto().getCdCompactTermRsn().equals(ServiceConstants.CPL_100)
					|| icpcFormsDto.getIcpcInfoDto().getCdCompactTermRsn().equals(ServiceConstants.CPL_110)
					|| icpcFormsDto.getIcpcInfoDto().getCdCompactTermRsn().equals(ServiceConstants.CPL_120))) {
				FormDataGroupDto legalYes = createFormDataGroup(FormGroupsConstants.TMPLAT_LEGAL_CUSTODY_YES,
						FormConstants.EMPTY_STRING);
				formDataGroupList.add(legalYes);
			}
			if (!ObjectUtils.isEmpty(icpcFormsDto.getIcpcInfoDto().getCdCompactTermRsn())
					&& icpcFormsDto.getIcpcInfoDto().getCdCompactTermRsn().equals(ServiceConstants.CPL_130)) {
				FormDataGroupDto completeYes = createFormDataGroup(FormGroupsConstants.TMPLAT_UNILATERAL_TERMINATION,
						FormConstants.EMPTY_STRING);
				formDataGroupList.add(completeYes);
			}
			if (!ObjectUtils.isEmpty(icpcFormsDto.getIcpcInfoDto().getCdCompactTermRsn())
					&& icpcFormsDto.getIcpcInfoDto().getCdCompactTermRsn().equals(ServiceConstants.CPL_160)) {
				FormDataGroupDto completeYes = createFormDataGroup(FormGroupsConstants.TMPLAT_PROPOSED_PLACEMENT_REQUEST_WITHDRAWN,
						FormConstants.EMPTY_STRING);
				formDataGroupList.add(completeYes);
			}
			if (!ObjectUtils.isEmpty(icpcFormsDto.getIcpcInfoDto().getCdCompactTermRsn())
					&& icpcFormsDto.getIcpcInfoDto().getCdCompactTermRsn().equals(ServiceConstants.CPL_150)) {
				FormDataGroupDto completeYes = createFormDataGroup(FormGroupsConstants.TMPLAT_CHILD_MOVED_TO_OTHER_STATE,
						FormConstants.EMPTY_STRING);
				formDataGroupList.add(completeYes);
			}

			// Group 02090
			if (!ObjectUtils.isEmpty(icpcFormsDto.getIcpcInfoDto().getCdCompactTermRsn())
					&& icpcFormsDto.getIcpcInfoDto().getCdCompactTermRsn().equals(ServiceConstants.CPL_60)) {
				FormDataGroupDto legalYes = createFormDataGroup(FormGroupsConstants.TMPLAT_LEGAL_CUSTODY_YES,
						FormConstants.EMPTY_STRING);
				formDataGroupList.add(legalYes);
			}

			// Group 02100
			if (!ObjectUtils.isEmpty(icpcFormsDto.getIcpcInfoDto().getCdCompactTermRsn())
					&& icpcFormsDto.getIcpcInfoDto().getCdCompactTermRsn().equals(ServiceConstants.CPL_80)) {
				FormDataGroupDto completeYes = createFormDataGroup(FormGroupsConstants.TMPLAT_TREAT_COMPLETE_YES,
						FormConstants.EMPTY_STRING);
				formDataGroupList.add(completeYes);
			}

			// Group 02110
			if (!ObjectUtils.isEmpty(icpcFormsDto.getIcpcInfoDto().getCdCompactTermRsn())
					&& icpcFormsDto.getIcpcInfoDto().getCdCompactTermRsn().equals(ServiceConstants.CPL_70)) {
				FormDataGroupDto completeYes = createFormDataGroup(FormGroupsConstants.TMPLAT_SEND_STATE_JURIS_TERM_YES,
						FormConstants.EMPTY_STRING);
				formDataGroupList.add(completeYes);
			}

			// Group 02120
			if (!ObjectUtils.isEmpty(icpcFormsDto.getIcpcInfoDto().getCdCompactTermRsn())
					&& icpcFormsDto.getIcpcInfoDto().getCdCompactTermRsn().equals(ServiceConstants.CPL_30)) {
				FormDataGroupDto completeYes = createFormDataGroup(
						FormGroupsConstants.TMPLAT_APRV_RES_NOT_USED_PLCMT_YES, FormConstants.EMPTY_STRING);
				formDataGroupList.add(completeYes);
			}

			// Group 02130
			if (!ObjectUtils.isEmpty(icpcFormsDto.getIcpcInfoDto().getCdCompactTermRsn())
					&& icpcFormsDto.getIcpcInfoDto().getCdCompactTermRsn().equals(ServiceConstants.CSSCCSTA_90)) {
				FormDataGroupDto otherYes = createFormDataGroup(FormGroupsConstants.TMPLAT_OTHERS_YES,
						FormConstants.EMPTY_STRING);
				formDataGroupList.add(otherYes);
			}

			// Group 02180
			if (!ObjectUtils.isEmpty(icpcFormsDto.getIcpcInfoDto().getCdPlacementStatus())
					&& icpcFormsDto.getIcpcInfoDto().getCdPlacementStatus().equals(ServiceConstants.CPL_10)) {
				FormDataGroupDto initPers = createFormDataGroup(FormGroupsConstants.TMPLAT_DISP_INIT_PERS,
						FormConstants.EMPTY_STRING);
				List<FormDataGroupDto> initPersGroupDtoList = new ArrayList<FormDataGroupDto>();
				FormDataGroupDto personDetailGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_INIT_ADD_PER,
						FormGroupsConstants.TMPLAT_DISP_INIT_PERS);
				// sub group 02190
				if (!ObjectUtils.isEmpty(icpcFormsDto.getDetailedPersonInfo())) {
					List<BookmarkDto> personGroupbookmarkList = new ArrayList<BookmarkDto>();
					BookmarkDto city = createBookmark(BookmarkConstants.INIT_PERS_ADD_CITY,
							icpcFormsDto.getDetailedPersonInfo().getAddrCity());
					BookmarkDto ln1 = createBookmark(BookmarkConstants.INIT_PERS_ADD_LN1,
							icpcFormsDto.getDetailedPersonInfo().getAddrStLn1());
					BookmarkDto ln2 = createBookmark(BookmarkConstants.INIT_PERS_ADD_LN2,
							icpcFormsDto.getDetailedPersonInfo().getAddrStLn2());
					BookmarkDto state = createBookmark(BookmarkConstants.INIT_PERS_ADD_STATE,
							icpcFormsDto.getDetailedPersonInfo().getAddrState());
					BookmarkDto zip = createBookmark(BookmarkConstants.INIT_PERS_ADD_ZIP,
							icpcFormsDto.getDetailedPersonInfo().getAddrZip());
					personGroupbookmarkList.add(city);
					personGroupbookmarkList.add(ln1);
					personGroupbookmarkList.add(ln2);
					personGroupbookmarkList.add(state);
					personGroupbookmarkList.add(zip);
					personDetailGroup.setBookmarkDtoList(personGroupbookmarkList);
				}
				initPersGroupDtoList.add(personDetailGroup);
				initPers.setFormDataGroupList(initPersGroupDtoList);
				formDataGroupList.add(initPers);
			}

			// Group 2200, 2260, 2280
			if (!ObjectUtils.isEmpty(icpcFormsDto.getIcpcInfoDto().getCdPlacementStatus())
					&& icpcFormsDto.getIcpcInfoDto().getCdPlacementStatus().equals(ServiceConstants.CPL_10)) {
				FormDataGroupDto initPers = createFormDataGroup(FormGroupsConstants.TMPLAT_DISP_INIT_RES,
						FormConstants.EMPTY_STRING);
				List<FormDataGroupDto> initPersGroupDtoList = new ArrayList<FormDataGroupDto>();
				// sub group 02210
				FormDataGroupDto personInfoWithNmGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_INIT_ADD_RES,
						FormGroupsConstants.TMPLAT_DISP_INIT_RES);
				if (!ObjectUtils.isEmpty(icpcFormsDto.getDetailedPersonInfoWithNmResource())) {
					List<BookmarkDto> personInfobookmarkList = new ArrayList<BookmarkDto>();
					BookmarkDto city = createBookmark(BookmarkConstants.INIT_RES_ADD_CITY,
							icpcFormsDto.getDetailedPersonInfoWithNmResource().getAddrCity());
					BookmarkDto ln1 = createBookmark(BookmarkConstants.INIT_RES_ADD_LN1,
							icpcFormsDto.getDetailedPersonInfoWithNmResource().getAddrStLn1());
					BookmarkDto ln2 = createBookmark(BookmarkConstants.INIT_RES_ADD_LN2,
							icpcFormsDto.getDetailedPersonInfoWithNmResource().getAddrStLn2());
					BookmarkDto state = createBookmark(BookmarkConstants.INIT_RES_ADD_STATE,
							icpcFormsDto.getDetailedPersonInfoWithNmResource().getAddrState());
					BookmarkDto zip = createBookmark(BookmarkConstants.INIT_PERS_ADD_ZIP,
							icpcFormsDto.getDetailedPersonInfoWithNmResource().getAddrZip());
					personInfobookmarkList.add(city);
					personInfobookmarkList.add(ln1);
					personInfobookmarkList.add(ln2);
					personInfobookmarkList.add(state);
					personInfobookmarkList.add(zip);
					personInfoWithNmGroup.setBookmarkDtoList(personInfobookmarkList);
				}
				initPersGroupDtoList.add(personInfoWithNmGroup);
				initPers.setFormDataGroupList(initPersGroupDtoList);

				// Group 2260
				FormDataGroupDto persNm = createFormDataGroup(FormGroupsConstants.TMPLAT_DISP_INI_PERS_NM,
						FormConstants.EMPTY_STRING);
				List<FormDataGroupDto> persNmDtoList = new ArrayList<FormDataGroupDto>();
				// sub group 02270
				FormDataGroupDto cngPersonGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_INI_PLCMT_PERS_NM,
						FormGroupsConstants.TMPLAT_DISP_INI_PERS_NM);
				if (!ObjectUtils.isEmpty(icpcFormsDto.getDetailedPersonInfo())) {
					List<BookmarkDto> cngPersonGroupbookmarkList = new ArrayList<BookmarkDto>();
					BookmarkDto fullName = createBookmark(BookmarkConstants.INTIAL_PLCMT_PER_NM,
							icpcFormsDto.getDetailedPersonInfo().getNmPersonFull());
					cngPersonGroupbookmarkList.add(fullName);
					cngPersonGroup.setBookmarkDtoList(cngPersonGroupbookmarkList);
				}
				persNmDtoList.add(cngPersonGroup);
				persNm.setFormDataGroupList(persNmDtoList);
				// Group 2280
				FormDataGroupDto resNm = createFormDataGroup(FormGroupsConstants.TMPLAT_DISP_INI_RES_NM,
						FormConstants.EMPTY_STRING);
				List<FormDataGroupDto> resNmList = new ArrayList<FormDataGroupDto>();
				// sub group 2290
				FormDataGroupDto plcmtNmGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_INI_PLCMT_RES_NM,
						FormGroupsConstants.TMPLAT_DISP_INI_RES_NM);
				if (!ObjectUtils.isEmpty(icpcFormsDto.getDetailedPersonInfoWithNmResource())) {
					List<BookmarkDto> plcmtNmbookmarkList = new ArrayList<BookmarkDto>();
					BookmarkDto resNmbook = createBookmark(BookmarkConstants.INTIAL_PLCMT_RES_NM,
							icpcFormsDto.getDetailedPersonInfoWithNmResource().getNmResource());
					plcmtNmbookmarkList.add(resNmbook);
					plcmtNmGroup.setBookmarkDtoList(plcmtNmbookmarkList);
				}
				resNmList.add(plcmtNmGroup);
				resNm.setFormDataGroupList(resNmList);
				// Group 2340
				FormDataGroupDto careInit = createFormDataGroup(FormGroupsConstants.TMPLAT_TYPE_OF_CARE_INITIAL,
						FormConstants.EMPTY_STRING);
				if (!ObjectUtils.isEmpty(icpcFormsDto.getIcpcInfoDto())) {
					List<BookmarkDto> cdCareTypebookmarkList = new ArrayList<BookmarkDto>();
					BookmarkDto cdCareTypebook = createBookmarkWithCodesTable(BookmarkConstants.TYPE_OF_CARE_INITIAL,
							icpcFormsDto.getIcpcInfoDto().getCdCareType(), CodesConstant.ICPCCRTP);
					cdCareTypebookmarkList.add(cdCareTypebook);
					careInit.setBookmarkDtoList(cdCareTypebookmarkList);
				}
				// 2370
				FormDataGroupDto plcmtInit = createFormDataGroup(FormGroupsConstants.TMPLAT_DT_PLCMT_INIT,
						FormConstants.EMPTY_STRING);
				if (!ObjectUtils.isEmpty(icpcFormsDto.getIcpcInfoDto())) {
					List<BookmarkDto> dtplcmtbookmarkList = new ArrayList<BookmarkDto>();
					BookmarkDto dtPlcmntbook = createBookmark(BookmarkConstants.DT_PLCMT_INITIAL,
							DateUtils.stringDt(icpcFormsDto.getIcpcInfoDto().getDtPlacement()));
					dtplcmtbookmarkList.add(dtPlcmntbook);
					plcmtInit.setBookmarkDtoList(dtplcmtbookmarkList);
				}
				formDataGroupList.add(plcmtInit);
				formDataGroupList.add(careInit);
				formDataGroupList.add(resNm);
				formDataGroupList.add(initPers);
				formDataGroupList.add(persNm);
			}

			// Group 02220 and Group 02240, 02300, 02320, 02350, 02360
			if (!ObjectUtils.isEmpty(icpcFormsDto.getIcpcInfoDto().getCdPlacementStatus())
					&& icpcFormsDto.getIcpcInfoDto().getCdPlacementStatus().equals(ServiceConstants.CPL_20)) {
				FormDataGroupDto cngPer = createFormDataGroup(FormGroupsConstants.TMPLAT_DISP_CNG_PER,
						FormConstants.EMPTY_STRING);
				List<FormDataGroupDto> cngPerGroupDtoList = new ArrayList<FormDataGroupDto>();
				// sub group 02230
				FormDataGroupDto cngPersonGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_CNG_PERS,
						FormGroupsConstants.TMPLAT_DISP_CNG_PER);
				if (!ObjectUtils.isEmpty(icpcFormsDto.getDetailedPersonInfo())) {
					List<BookmarkDto> cngPersonGroupbookmarkList = new ArrayList<BookmarkDto>();
					BookmarkDto city = createBookmark(BookmarkConstants.INIT_PERS_ADD_CITY,
							icpcFormsDto.getDetailedPersonInfo().getAddrCity());
					BookmarkDto ln1 = createBookmark(BookmarkConstants.INIT_PERS_ADD_LN1,
							icpcFormsDto.getDetailedPersonInfo().getAddrStLn1());
					BookmarkDto ln2 = createBookmark(BookmarkConstants.INIT_PERS_ADD_LN2,
							icpcFormsDto.getDetailedPersonInfo().getAddrStLn2());
					BookmarkDto state = createBookmark(BookmarkConstants.INIT_PERS_ADD_STATE,
							icpcFormsDto.getDetailedPersonInfo().getAddrState());
					BookmarkDto zip = createBookmark(BookmarkConstants.INIT_PERS_ADD_ZIP,
							icpcFormsDto.getDetailedPersonInfo().getAddrZip());
					cngPersonGroupbookmarkList.add(city);
					cngPersonGroupbookmarkList.add(ln1);
					cngPersonGroupbookmarkList.add(ln2);
					cngPersonGroupbookmarkList.add(state);
					cngPersonGroupbookmarkList.add(zip);
					cngPersonGroup.setBookmarkDtoList(cngPersonGroupbookmarkList);
				}
				cngPerGroupDtoList.add(cngPersonGroup);
				cngPer.setFormDataGroupList(cngPerGroupDtoList);
				// Group 2240
				FormDataGroupDto cngRes = createFormDataGroup(FormGroupsConstants.TMPLAT_DISP_CNG_RES,
						FormConstants.EMPTY_STRING);
				List<FormDataGroupDto> cngResGroupDtoList = new ArrayList<FormDataGroupDto>();
				// sub group 02250
				FormDataGroupDto cngResGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_CNG_RES,
						FormGroupsConstants.TMPLAT_DISP_CNG_RES);
				if (!ObjectUtils.isEmpty(icpcFormsDto.getDetailedPersonInfoWithNmResource())) {
					List<BookmarkDto> cngPersonGroupbookmarkList = new ArrayList<BookmarkDto>();
					BookmarkDto city = createBookmark(BookmarkConstants.CNG_RES_ADD_CITY,
							icpcFormsDto.getDetailedPersonInfoWithNmResource().getAddrCity());
					BookmarkDto ln1 = createBookmark(BookmarkConstants.CNG_RES_ADD_LN1,
							icpcFormsDto.getDetailedPersonInfoWithNmResource().getAddrStLn1());
					BookmarkDto ln2 = createBookmark(BookmarkConstants.CNG_RES_ADD_LN2,
							icpcFormsDto.getDetailedPersonInfoWithNmResource().getAddrStLn2());
					BookmarkDto state = createBookmark(BookmarkConstants.CNG_RES_ADD_STATE,
							icpcFormsDto.getDetailedPersonInfoWithNmResource().getAddrState());
					BookmarkDto zip = createBookmark(BookmarkConstants.CNG_RES_ADD_ZIP,
							icpcFormsDto.getDetailedPersonInfoWithNmResource().getAddrZip());
					cngPersonGroupbookmarkList.add(city);
					cngPersonGroupbookmarkList.add(ln1);
					cngPersonGroupbookmarkList.add(ln2);
					cngPersonGroupbookmarkList.add(state);
					cngPersonGroupbookmarkList.add(zip);
					cngResGroup.setBookmarkDtoList(cngPersonGroupbookmarkList);
				}
				cngResGroupDtoList.add(cngResGroup);
				cngRes.setFormDataGroupList(cngResGroupDtoList);
				// Group 2300
				FormDataGroupDto perNm = createFormDataGroup(FormGroupsConstants.TMPLAT_DISP_CNG_PER_NM,
						FormConstants.EMPTY_STRING);
				List<FormDataGroupDto> perNmDtoList = new ArrayList<FormDataGroupDto>();
				// sub group 2310
				FormDataGroupDto chngPlcmntPerNmGroup = createFormDataGroup(
						FormGroupsConstants.TMPLAT_CHNG_PLCMT_PER_NM, FormGroupsConstants.TMPLAT_DISP_CNG_PER_NM);
				if (!ObjectUtils.isEmpty(icpcFormsDto.getDetailedPersonInfo())) {
					List<BookmarkDto> cngPersonGroupbookmarkList = new ArrayList<BookmarkDto>();
					BookmarkDto fullName = createBookmark(BookmarkConstants.PLCMT_CNG_PER_NAME,
							icpcFormsDto.getDetailedPersonInfo().getNmPersonFull());
					cngPersonGroupbookmarkList.add(fullName);
					chngPlcmntPerNmGroup.setBookmarkDtoList(cngPersonGroupbookmarkList);
				}
				perNmDtoList.add(chngPlcmntPerNmGroup);
				perNm.setFormDataGroupList(perNmDtoList);
				// Group 2320
				FormDataGroupDto resNm = createFormDataGroup(FormGroupsConstants.TMPLAT_DISP_CNG_RES_NM,
						FormConstants.EMPTY_STRING);
				List<FormDataGroupDto> resNmList = new ArrayList<FormDataGroupDto>();
				// sub group 02330
				FormDataGroupDto chngplcmtResNmGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_CHNG_PLCMT_RES_NM,
						FormGroupsConstants.TMPLAT_DISP_CNG_RES_NM);
				if (!ObjectUtils.isEmpty(icpcFormsDto.getDetailedPersonInfoWithNmResource())) {
					List<BookmarkDto> plcmtNmbookmarkList = new ArrayList<BookmarkDto>();
					BookmarkDto resNmbook = createBookmark(BookmarkConstants.PLCMT_CNG_RES_NAME,
							icpcFormsDto.getDetailedPersonInfoWithNmResource().getNmResource());
					plcmtNmbookmarkList.add(resNmbook);
					chngplcmtResNmGroup.setBookmarkDtoList(plcmtNmbookmarkList);
				}
				resNmList.add(chngplcmtResNmGroup);
				resNm.setFormDataGroupList(resNmList);
				// Group 2350
				FormDataGroupDto carePlcmt = createFormDataGroup(FormGroupsConstants.TMPLAT_TYPE_OF_CARE_PLCMT,
						FormConstants.EMPTY_STRING);
				if (!ObjectUtils.isEmpty(icpcFormsDto.getIcpcInfoDto())) {
					List<BookmarkDto> cdCareTypbookmarkList = new ArrayList<BookmarkDto>();
					BookmarkDto cdCareTypbook = createBookmarkWithCodesTable(BookmarkConstants.TYPE_OF_CARE_CHNG_PLCMT,
							icpcFormsDto.getIcpcInfoDto().getCdCareType(), CodesConstant.ICPCCRTP);
					cdCareTypbookmarkList.add(cdCareTypbook);
					carePlcmt.setBookmarkDtoList(cdCareTypbookmarkList);
				}
				// Group 2360
				FormDataGroupDto plcmtChange = createFormDataGroup(FormGroupsConstants.TMPLAT_DT_PLCMT_CHANGE,
						FormConstants.EMPTY_STRING);
				if (!ObjectUtils.isEmpty(icpcFormsDto.getIcpcInfoDto())) {
					List<BookmarkDto> dtplcmtbookmarkList = new ArrayList<BookmarkDto>();
					BookmarkDto dtPlcmntbook = createBookmark(BookmarkConstants.DT_PLCMT_CHANGE,
							DateUtils.stringDt(icpcFormsDto.getIcpcInfoDto().getDtPlacement()));
					dtplcmtbookmarkList.add(dtPlcmntbook);
					plcmtChange.setBookmarkDtoList(dtplcmtbookmarkList);
				}
				formDataGroupList.add(plcmtChange);
				formDataGroupList.add(carePlcmt);
				formDataGroupList.add(resNm);
				formDataGroupList.add(perNm);
				formDataGroupList.add(cngRes);
				formDataGroupList.add(cngPer);
			}
		}

		// Group 02380
		if (!ObjectUtils.isEmpty(icpcFormsDto.getNmResourceList())) {
			for (IcpcInfoDto dto : icpcFormsDto.getNmResourceList()) {
				if (!ObjectUtils.isEmpty(dto.getNmResource())) {
					FormDataGroupDto plcmtRes = createFormDataGroup(FormGroupsConstants.TMPLAT_NM_PLCMT_RES,
							FormConstants.EMPTY_STRING);
					List<BookmarkDto> plcmtResbookmarkList = new ArrayList<BookmarkDto>();
					BookmarkDto nmResource = createBookmark(BookmarkConstants.NM_PLCMT_RESOURCE, dto.getNmResource());
					plcmtResbookmarkList.add(nmResource);
					plcmtRes.setBookmarkDtoList(plcmtResbookmarkList);
					formDataGroupList.add(plcmtRes);
				}

			}
		}

		// Group 02010
		if (!ObjectUtils.isEmpty(icpcFormsDto.getPersonNmTypeList())) {
			for (IcpcChildDetailsDto dto : icpcFormsDto.getPersonNmTypeList()) {
				if (ServiceConstants.CPL_30.equals(dto.getCdPersonType())) {
					FormDataGroupDto tmplatMother = createFormDataGroup(FormGroupsConstants.TMPLAT_MOTHER,
							FormConstants.EMPTY_STRING);
					List<BookmarkDto> tmplatMotherbookmarkList = new ArrayList<BookmarkDto>();
					BookmarkDto motherFullName = createBookmark(BookmarkConstants.MOTHER_FULL_NAME,
							dto.getNmPersonFull());
					tmplatMotherbookmarkList.add(motherFullName);
					tmplatMother.setBookmarkDtoList(tmplatMotherbookmarkList);
					formDataGroupList.add(tmplatMother);
				}

				// Group 02020
				if (ServiceConstants.CPL_10.equals(dto.getCdPersonType())) {
					FormDataGroupDto tmplatFather = createFormDataGroup(FormGroupsConstants.TMPLAT_FATHER,
							FormConstants.EMPTY_STRING);
					List<BookmarkDto> tmplatFatherbookmarkList = new ArrayList<BookmarkDto>();
					BookmarkDto fatherFullName = createBookmark(BookmarkConstants.FATHER_FULL_NAME,
							dto.getNmPersonFull());
					tmplatFatherbookmarkList.add(fatherFullName);
					tmplatFather.setBookmarkDtoList(tmplatFatherbookmarkList);
					formDataGroupList.add(tmplatFather);
				}

				// Group 02050
				if (ServiceConstants.CPL_10.equals(dto.getCdPersonType())) {
					FormDataGroupDto testPerson = createFormDataGroup(FormGroupsConstants.TMPLAT_TEST,
							FormConstants.EMPTY_STRING);
					List<BookmarkDto> testPersonbookmarkList = new ArrayList<BookmarkDto>();
					BookmarkDto testerFullName = createBookmark(BookmarkConstants.TEST_NM_PERSON,
							dto.getNmPersonFull());
					testPersonbookmarkList.add(testerFullName);
					testPerson.setBookmarkDtoList(testPersonbookmarkList);
					formDataGroupList.add(testPerson);
				}

				// Group 02390
				if (ServiceConstants.CPL_60.equals(dto.getCdPersonType())) {
					FormDataGroupDto nmPlcmtPersGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_NM_PLCMT_PERS,
							FormConstants.EMPTY_STRING);
					List<BookmarkDto> nmPlcmtPersbookmarkList = new ArrayList<BookmarkDto>();
					BookmarkDto nmPlcmtPers = createBookmark(BookmarkConstants.NM_PLCMT_PERS, dto.getNmPersonFull());
					nmPlcmtPersbookmarkList.add(nmPlcmtPers);
					nmPlcmtPersGroup.setBookmarkDtoList(nmPlcmtPersbookmarkList);
					formDataGroupList.add(nmPlcmtPersGroup);
				}

				// Group 02410
				if (ServiceConstants.CPL_20.equals(dto.getCdPersonType())) {
					FormDataGroupDto legalCustGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_LEGAL_CUST_NM,
							FormConstants.EMPTY_STRING);
					List<BookmarkDto> legalCustGroupbookmarkList = new ArrayList<BookmarkDto>();
					BookmarkDto legalCust = createBookmark(BookmarkConstants.LEGAL_CUST_NM, dto.getNmPersonFull());
					legalCustGroupbookmarkList.add(legalCust);
					legalCustGroup.setBookmarkDtoList(legalCustGroupbookmarkList);
					formDataGroupList.add(legalCustGroup);
				}
			}
		}

		// Populating the non form group data into prefill data. !!bookmarks
		if (!ObjectUtils.isEmpty(icpcFormsDto.getIcpcInfoDto())) {
			BookmarkDto dtPlacement = createBookmark(BookmarkConstants.DT_PLCMT_INITIAL,
					DateUtils.stringDt(icpcFormsDto.getIcpcInfoDto().getDtPlacement()));
			BookmarkDto dtPlacementWithdrawn = createBookmark(BookmarkConstants.DT_WITHDRAWN,
					DateUtils.stringDt(icpcFormsDto.getIcpcInfoDto().getDtPlacementWithDrawn()));
			BookmarkDto dtplcmntTerm = createBookmark(BookmarkConstants.DT_TERMIN,
					DateUtils.stringDt(icpcFormsDto.getIcpcInfoDto().getDtPlacementTerm()));
			BookmarkDto cdReceivingState = createBookmarkWithCodesTable(BookmarkConstants.TXT_TO_STATE,
					icpcFormsDto.getIcpcInfoDto().getCdReceivingState(),CodesConstant.ICPCST);
			BookmarkDto cdSendingState = createBookmarkWithCodesTable(BookmarkConstants.TXT_FROM_STATE,
					icpcFormsDto.getIcpcInfoDto().getCdSendingState(),CodesConstant.ICPCST);
			BookmarkDto txtReasonOther = createBookmark(BookmarkConstants.OTHERS_COMMENTS,
					icpcFormsDto.getIcpcInfoDto().getTxtReasonOther());
			bookmarkNonFrmGrpList.add(dtPlacement);
			bookmarkNonFrmGrpList.add(dtPlacementWithdrawn);
			bookmarkNonFrmGrpList.add(dtplcmntTerm);
			bookmarkNonFrmGrpList.add(cdReceivingState);
			bookmarkNonFrmGrpList.add(cdSendingState);
			bookmarkNonFrmGrpList.add(txtReasonOther);
		}

		if (!ObjectUtils.isEmpty(icpcFormsDto.getNmResourceList())) {
			for (IcpcInfoDto dto : icpcFormsDto.getNmResourceList()) {
				BookmarkDto nmResource = createBookmark(BookmarkConstants.NM_PLCMT_RESOURCE, dto.getNmResource());
				BookmarkDto nmCase = createBookmark(BookmarkConstants.NM_CASE_WORKER_SUPERVISOR, dto.getNmResource());
				bookmarkNonFrmGrpList.add(nmResource);
				bookmarkNonFrmGrpList.add(nmCase);
			}
		}

		if (!ObjectUtils.isEmpty(icpcFormsDto.getPersonInfoList())) {
			for (IcpcChildDetailsDto dto : icpcFormsDto.getPersonInfoList()) {
				BookmarkDto dob = createBookmark(BookmarkConstants.DATE_OF_BIRTH,
						DateUtils.stringDt(dto.getDtPersonBirth()));
				BookmarkDto fullname = createBookmark(BookmarkConstants.CHILD_FULL_NAME, dto.getNmPersonFull());
				bookmarkNonFrmGrpList.add(dob);
				bookmarkNonFrmGrpList.add(fullname);
			}
		}

		BookmarkDto cdRelation = createBookmark(BookmarkConstants.LEGAL_CUST_RELATION, icpcFormsDto.getCdRelation());
		bookmarkNonFrmGrpList.add(cdRelation);

		PreFillDataServiceDto preFillData = new PreFillDataServiceDto();
		preFillData.setFormDataGroupList(formDataGroupList);
		preFillData.setBookmarkDtoList(bookmarkNonFrmGrpList);
		return preFillData;
	}

}
