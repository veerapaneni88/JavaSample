package us.tx.state.dfps.service.forms.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.populateletter.dto.CaseInfoDto;
import us.tx.state.dfps.populateletter.dto.PopulateLetterDto;
import us.tx.state.dfps.service.admin.dto.CpsInvstDetailStageIdOutDto;
import us.tx.state.dfps.service.admin.dto.StageSituationOutDto;
import us.tx.state.dfps.service.common.BookmarkConstants;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.FormConstants;
import us.tx.state.dfps.service.common.FormGroupsConstants;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.forms.dto.BookmarkDto;
import us.tx.state.dfps.service.forms.dto.FormDataGroupDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;

/**
 * 
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Description:
 * PopulateLetterPrefillData will implemented returnPrefillData operation
 * defined in DocumentServiceUtil Interface to populate the prefill data for
 * form CFIV05O00. Jan 30, 2018 - 03:26:29 PM
 * 
 * @author lubabanuzhat.t
 * ********Change History**********
 * 07/11/2023 thompswa artf250175 TMPLAT_CLOSE_RO receives R/O from TmScrTmGeneric9.
 *
 * */
@Component
public class PopulateLetterPrefillData extends DocumentServiceUtil {

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

		PopulateLetterDto populateLetterDto = (PopulateLetterDto) parentDtoobj;
		if (null == populateLetterDto.getStageSituationOutDtoList()) {
			populateLetterDto.setStageSituationOutDtoList(new ArrayList<StageSituationOutDto>());
		}

		List<FormDataGroupDto> formDataGroupList = new ArrayList<FormDataGroupDto>();

		/**
		 * Checking the stage type equal to PRN or CH. If equals set the prefill
		 * data for group cfiv0521
		 */

		for (CaseInfoDto caseInfoDtoprincipalType : populateLetterDto.getCaseInfoDtoprincipal()) {
			if (ServiceConstants.CPRSNTYP_PRN.equalsIgnoreCase(caseInfoDtoprincipalType.getCdStagePersType())
					&& ServiceConstants.CACTTYPE_CH_CH
							.equalsIgnoreCase(caseInfoDtoprincipalType.getCdPersonMaritalStatus())) {
				FormDataGroupDto tempChildNameFrmDataGrpDto = createFormDataGroup(
						FormGroupsConstants.TMPLAT_CHILDS_NAME, FormConstants.EMPTY_STRING);

				List<FormDataGroupDto> tempChildNameFrmDataGrpList = new ArrayList<FormDataGroupDto>();

				List<BookmarkDto> bookmarkChildNameList = new ArrayList<BookmarkDto>();
				
				BookmarkDto bookmarkCdNameSuffixDto = createBookmarkWithCodesTable(BookmarkConstants.CHILD_NAME_SUFFIX,
						caseInfoDtoprincipalType.getCdNameSuffix(), CodesConstant.CSUFFIX);
				BookmarkDto bookmarkNmNameFirstDto = createBookmark(BookmarkConstants.CHILD_NAME_FIRST,
						caseInfoDtoprincipalType.getNmNameFirst());
				BookmarkDto bookmarkNmNameLastDto = createBookmark(BookmarkConstants.CHILD_NAME_LAST,
						caseInfoDtoprincipalType.getNmNameLast());
				BookmarkDto bookmarkNmNameMiddleDto = createBookmark(BookmarkConstants.CHILD_NAME_MIDDLE,
						caseInfoDtoprincipalType.getNmNameMiddle());
				bookmarkChildNameList.add(bookmarkCdNameSuffixDto);
				bookmarkChildNameList.add(bookmarkNmNameFirstDto);
				bookmarkChildNameList.add(bookmarkNmNameLastDto);
				bookmarkChildNameList.add(bookmarkNmNameMiddleDto);
				tempChildNameFrmDataGrpDto.setBookmarkDtoList(bookmarkChildNameList);

				// Set prefill data for the subgroup - cfiv0522

				if (FormConstants.EMPTY_STRING.equals(caseInfoDtoprincipalType.getCdNameSuffix())) {
					FormDataGroupDto tempCommaChildFrmDataGrpDto = createFormDataGroup(
							FormGroupsConstants.TMPLAT_COMMA_CHILD, FormGroupsConstants.TMPLAT_CHILDS_NAME);
					tempChildNameFrmDataGrpList.add(tempCommaChildFrmDataGrpDto);
				}

				tempChildNameFrmDataGrpDto.setFormDataGroupList(tempChildNameFrmDataGrpList);
				formDataGroupList.add(tempChildNameFrmDataGrpDto);

			}
		}

		/**
		 * Checking sysCode > 001. If condition satisfies then set the prefill
		 * data for group cfiv0531
		 */

		if (populateLetterDto.getCodesTablesDto().getaCode().compareTo(FormConstants.SYSCODE) > 0) {
			FormDataGroupDto tempHeaderBoardFrmDataGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_HEADER_BOARD,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkHeaderBoardList = new ArrayList<BookmarkDto>();
			BookmarkDto bookmarkSysDecodeDto = createBookmark(BookmarkConstants.HEADER_BOARD_CITY,
					populateLetterDto.getCodesTablesDto().getaDecode());
			BookmarkDto bookmarkSysDecode2Dto = createBookmark(BookmarkConstants.HEADER_BOARD_NAME,
					populateLetterDto.getCodesTablesDto().getbDecode());
			bookmarkHeaderBoardList.add(bookmarkSysDecodeDto);
			bookmarkHeaderBoardList.add(bookmarkSysDecode2Dto);

			tempHeaderBoardFrmDataGrpDto.setBookmarkDtoList(bookmarkHeaderBoardList);
			formDataGroupList.add(tempHeaderBoardFrmDataGrpDto);
		}

		/**
		 * Checking sysCode == 001. If condition satisfies then set the prefill
		 * data for group cfiv0532
		 */

		if (populateLetterDto.getCodesTablesDto().getaCode().compareTo(FormConstants.SYSCODE) == 0) {
			FormDataGroupDto tempHeaderDirectorFrmDataGrpDto = createFormDataGroup(
					FormGroupsConstants.TMPLAT_HEADER_DIRECTOR, FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkHeaderDirectorList = new ArrayList<BookmarkDto>();
			BookmarkDto bookmarkSysDecodeDto = createBookmark(BookmarkConstants.HEADER_DIRECTOR_TITLE,
					populateLetterDto.getCodesTablesDto().getaDecode());
			BookmarkDto bookmarkSysDecode2Dto = createBookmark(BookmarkConstants.HEADER_DIRECTOR_NAME,
					populateLetterDto.getCodesTablesDto().getbDecode());
			bookmarkHeaderDirectorList.add(bookmarkSysDecodeDto);
			bookmarkHeaderDirectorList.add(bookmarkSysDecode2Dto);

			tempHeaderDirectorFrmDataGrpDto.setBookmarkDtoList(bookmarkHeaderDirectorList);
			formDataGroupList.add(tempHeaderDirectorFrmDataGrpDto);
		}

		/**
		 * Checking AddrMailCodeStLn2 empty or not. If condition satisfies then
		 * set the prefill data for group cfiv0542
		 */
		if (!TypeConvUtil.isNullOrEmpty(populateLetterDto.getEmployeePersPhNameDto())) {
			if (!TypeConvUtil.isNullOrEmpty(populateLetterDto.getEmployeePersPhNameDto().getAddrMailCodeStLn2())) {
				FormDataGroupDto tempWorkerStreet2FrmDataGrpDto = createFormDataGroup(
						FormGroupsConstants.TMPLAT_WORKER_STREET2, FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkWorkerStreet2List = new ArrayList<BookmarkDto>();
				BookmarkDto bookmarkWorkerAddressLine2Dto = createBookmark(BookmarkConstants.WORKER_ADDRESS_LINE_2,
						populateLetterDto.getEmployeePersPhNameDto().getAddrMailCodeStLn2());

				bookmarkWorkerStreet2List.add(bookmarkWorkerAddressLine2Dto);
				tempWorkerStreet2FrmDataGrpDto.setBookmarkDtoList(bookmarkWorkerStreet2List);
				formDataGroupList.add(tempWorkerStreet2FrmDataGrpDto);
			}
		}

		/**
		 * Checking NbrPhoneExtension empty or not. If condition satisfies then
		 * set the prefill data for group cfiv0544
		 */

		if (!TypeConvUtil.isNullOrEmpty(populateLetterDto.getEmployeePersPhNameDto())) {
			if (!TypeConvUtil.isNullOrEmpty(populateLetterDto.getEmployeePersPhNameDto().getNbrPhoneExtension())) {
				FormDataGroupDto tempWorkerExtFrmDataGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_WORKER_EXT,
						FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkWorkerExtList = new ArrayList<BookmarkDto>();
				BookmarkDto bookmarkWorkerExtDto = createBookmark(BookmarkConstants.WORKER_PHONE_EXT,
						populateLetterDto.getEmployeePersPhNameDto().getNbrPhoneExtension());

				bookmarkWorkerExtList.add(bookmarkWorkerExtDto);
				tempWorkerExtFrmDataGrpDto.setBookmarkDtoList(bookmarkWorkerExtList);
				formDataGroupList.add(tempWorkerExtFrmDataGrpDto);
			}
		}

		/**
		 * Checking CdNameSuffix empty or not. If condition satisfies then set
		 * the prefill data for group cfiv0545
		 */

		if (!TypeConvUtil.isNullOrEmpty(populateLetterDto.getEmployeePersPhNameDto())) {
			if (!TypeConvUtil.isNullOrEmpty(populateLetterDto.getEmployeePersPhNameDto().getCdNameSuffix())) {
				FormDataGroupDto tempCommaWorkerDataGrpDto = createFormDataGroup(
						FormGroupsConstants.TMPLAT_COMMA_WORKER, FormConstants.EMPTY_STRING);
				formDataGroupList.add(tempCommaWorkerDataGrpDto);
			}
		}

		/**
		 * Checking tmScrTmGeneric9 "OPN" or not. If condition satisfies then
		 * set the prefill data for group cfiv05ov
		 */
		CaseInfoDto caseInfoDtoType = populateLetterDto.getCaseInfoDto().get(0);

		/**
		 * Checking CdNameSuffix empty or not. If condition satisfies then set
		 * the prefill data for group cfiv0541
		 */

		if (!TypeConvUtil.isNullOrEmpty(populateLetterDto.getEmployeePersPhNameDto())) {
			if (!TypeConvUtil.isNullOrEmpty(populateLetterDto.getEmployeePersPhNameDto().getCdNameSuffix())) {
				FormDataGroupDto tempCommaRepDataGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_COMMA_REP,
						FormConstants.EMPTY_STRING);
				formDataGroupList.add(tempCommaRepDataGrpDto);
			}
		}

		/**
		 * No condition , Adding prefill data for group cfiv05ae
		 */

		FormDataGroupDto tempRepAddressFrmDataGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_REP_ADDRESS,
				FormConstants.EMPTY_STRING);
		List<FormDataGroupDto> tempRepAddressFrmDataGrpList = new ArrayList<FormDataGroupDto>();
		List<BookmarkDto> bookmarkRepAddressList = new ArrayList<BookmarkDto>();
		// for (CaseInfoDto caseInfoDtoType :
		// populateLetterDto.getCaseInfoDto()) {
		BookmarkDto bookmarkRepAddressZipDto = createBookmark(BookmarkConstants.REP_ADDRESS_ZIP,
				caseInfoDtoType.getAddrPersonAddrZip());
		BookmarkDto bookmarkRepAddressCityDto = createBookmark(BookmarkConstants.REP_ADDRESS_CITY,
				caseInfoDtoType.getAddrPersonAddrCity());
		BookmarkDto bookmarkRepAddressStLn1Dto = createBookmark(BookmarkConstants.REP_ADDRESS_LINE_1,
				caseInfoDtoType.getAddrPersAddrStLn1());
		BookmarkDto bookmarkRepAddressStateDto = createBookmark(BookmarkConstants.REP_ADDRESS_STATE,
				caseInfoDtoType.getCdPersonAddrState());

		bookmarkRepAddressList.add(bookmarkRepAddressZipDto);
		bookmarkRepAddressList.add(bookmarkRepAddressCityDto);
		bookmarkRepAddressList.add(bookmarkRepAddressStLn1Dto);
		bookmarkRepAddressList.add(bookmarkRepAddressStateDto);

		// }
		tempRepAddressFrmDataGrpDto.setBookmarkDtoList(bookmarkRepAddressList);

		// Set prefill data for the subgroup - cfiv05a2

		// for (CaseInfoDto caseInfoDtoType :
		// populateLetterDto.getCaseInfoDto()) {
		if (!TypeConvUtil.isNullOrEmpty(caseInfoDtoType.getAddrPersAddrStLn2())) {
			FormDataGroupDto tempRepAddLine2FrmDataGrpDto = createFormDataGroup(
					FormGroupsConstants.TMPLAT_REP_ADDRESS_LINE_2, FormGroupsConstants.TMPLAT_REP_ADDRESS);
			List<BookmarkDto> bookmarkRepAddressLineList = new ArrayList<BookmarkDto>();
			BookmarkDto bookmarkRepAddressLine2Dto = createBookmark(BookmarkConstants.REP_ADDRESS_LINE_2,
					caseInfoDtoType.getAddrPersAddrStLn2());

			bookmarkRepAddressLineList.add(bookmarkRepAddressLine2Dto);
			tempRepAddLine2FrmDataGrpDto.setBookmarkDtoList(bookmarkRepAddressLineList);
			tempRepAddressFrmDataGrpList.add(tempRepAddLine2FrmDataGrpDto);
		}
		// }

		tempRepAddressFrmDataGrpDto.setFormDataGroupList(tempRepAddressFrmDataGrpList);
		formDataGroupList.add(tempRepAddressFrmDataGrpDto);

		if (!ObjectUtils.isEmpty(populateLetterDto.getCaseInfoDto())
				&& !ObjectUtils.isEmpty(populateLetterDto.getCaseInfoDto().get(0).getTmScrTmGeneric9())) {
			String outcome = populateLetterDto.getCaseInfoDto().get(0).getTmScrTmGeneric9();
			//Modified the code the for Warranty defect 11485
			if (ServiceConstants.RULED_OUT.equalsIgnoreCase( outcome )) { // artf250175 RO
				formDataGroupList.add(createFormDataGroup(FormGroupsConstants.TMPLAT_CLOSE_RO, FormConstants.EMPTY_STRING));
			} else if (ServiceConstants.CPS_INVST_DTL_OVRLL_DISPTN_UTC.equalsIgnoreCase( outcome )) {
				formDataGroupList.add(createFormDataGroup(FormGroupsConstants.TMPLAT_UTC, FormConstants.EMPTY_STRING));
			} else if (ServiceConstants.CD_STAGE_ADMIN.equalsIgnoreCase( outcome )) {
				formDataGroupList.add(createFormDataGroup(FormGroupsConstants.TMPLAT_ADMIN_CLOSE, FormConstants.EMPTY_STRING));
			} else if (!ObjectUtils.isEmpty(populateLetterDto.getStageProgDtoList())) {
				if (ServiceConstants.CLOSING_A_CASE.equals( outcome )
						&& ServiceConstants.STAGE_RSN_CLOSE.contains(populateLetterDto.getStageProgDtoList().get(0).getCdStageProgRsnClose())) {
					formDataGroupList.add(createFormDataGroup(FormGroupsConstants.TMPLAT_CLOSING_CASE, FormConstants.EMPTY_STRING));
			    } else if (ServiceConstants.OPENING_A_CASE.equals( outcome )
						&& ServiceConstants.STAGE_RSN_OPN.contains(populateLetterDto.getStageProgDtoList().get(0).getCdStageProgRsnClose())) {
					formDataGroupList.add(createFormDataGroup(FormGroupsConstants.TMPLAT_OPENING_CASE, FormConstants.EMPTY_STRING));
				}
			}
		}

		/*
		 * For CFIV1800 setting TMPLAT_DEAR_NONFEMALE,TMPLAT_DEAR_FEMALE
		 */

		if (populateLetterDto.isSpanish()) {

			// for (CaseInfoDto caseInfoDtoType :
			// populateLetterDto.getCaseInfoDto()) {
			if (!ServiceConstants.FOSTER.equalsIgnoreCase(caseInfoDtoType.getCdPersonSex())) {
				FormDataGroupDto tempDearNonFemaleDto = createFormDataGroup(FormGroupsConstants.TMPLAT_DEAR_NONFEMALE,
						FormConstants.EMPTY_STRING);
				formDataGroupList.add(tempDearNonFemaleDto);
			} else {
				FormDataGroupDto tempDearFemaleDto = createFormDataGroup(FormGroupsConstants.TMPLAT_DEAR_FEMALE,
						FormConstants.EMPTY_STRING);
				formDataGroupList.add(tempDearFemaleDto);
			}
			// }
		}

		/**
		 * Populating the non form group data into prefill data
		 */
		List<BookmarkDto> bookmarkNonFrmGrpList = new ArrayList<BookmarkDto>();

		// Populate NbrPhone value from DAM CSEC01D
		if (!TypeConvUtil.isNullOrEmpty(populateLetterDto.getEmployeePersPhNameDto())) {
			if (!TypeConvUtil.isNullOrEmpty(populateLetterDto.getEmployeePersPhNameDto().getNbrPhone())) {
				BookmarkDto bookmarkNbrPhone = createBookmark(BookmarkConstants.WORKER_PHONE,
						TypeConvUtil.formatPhone(populateLetterDto.getEmployeePersPhNameDto().getNbrPhone()));

				bookmarkNonFrmGrpList.add(bookmarkNbrPhone);
			}
		}
		// Populate AddrMailCodeCity value from DAM CSEC01D
		if (!TypeConvUtil.isNullOrEmpty(populateLetterDto.getEmployeePersPhNameDto())) {
			if (!TypeConvUtil.isNullOrEmpty(populateLetterDto.getEmployeePersPhNameDto().getAddrMailCodeCity())) {
				BookmarkDto bookmarkAddrMailCodeCity = createBookmark(BookmarkConstants.WORKER_ADDRESS_CITY,
						populateLetterDto.getEmployeePersPhNameDto().getAddrMailCodeCity());
				bookmarkNonFrmGrpList.add(bookmarkAddrMailCodeCity);
			}
		}

		// Populate AddrMailCodeStLn1 value from DAM CSEC01D
		if (!TypeConvUtil.isNullOrEmpty(populateLetterDto.getEmployeePersPhNameDto())) {
			if (!TypeConvUtil.isNullOrEmpty(populateLetterDto.getEmployeePersPhNameDto().getAddrMailCodeStLn1())) {
				BookmarkDto bookmarkAddrMailCodeStLn1 = createBookmark(BookmarkConstants.WORKER_ADDRESS_LINE_1,
						populateLetterDto.getEmployeePersPhNameDto().getAddrMailCodeStLn1());
				bookmarkNonFrmGrpList.add(bookmarkAddrMailCodeStLn1);
			}
		}

		// Populate AddrMailCodeZip value from DAM CSEC01D
		if (!TypeConvUtil.isNullOrEmpty(populateLetterDto.getEmployeePersPhNameDto())) {
			if (!TypeConvUtil.isNullOrEmpty(populateLetterDto.getEmployeePersPhNameDto().getAddrMailCodeZip())) {
				BookmarkDto bookmarkAddrMailCodeZip = createBookmark(BookmarkConstants.WORKER_ADDRESS_ZIP,
						populateLetterDto.getEmployeePersPhNameDto().getAddrMailCodeZip());
				bookmarkNonFrmGrpList.add(bookmarkAddrMailCodeZip);
			}
		}

		// Populate CdNameSuffix value from DAM CSEC01D
		if (!TypeConvUtil.isNullOrEmpty(populateLetterDto.getEmployeePersPhNameDto())) {
			if (!TypeConvUtil.isNullOrEmpty(populateLetterDto.getEmployeePersPhNameDto().getCdNameSuffix())) {
				BookmarkDto bookmarkCdNameSuffix = createBookmark(BookmarkConstants.WORKER_NAME_SUFFIX,
						populateLetterDto.getEmployeePersPhNameDto().getCdNameSuffix());
				bookmarkNonFrmGrpList.add(bookmarkCdNameSuffix);

			}
		}

		// Populate NmNameFirst value from DAM CSEC01D
		if (!TypeConvUtil.isNullOrEmpty(populateLetterDto.getEmployeePersPhNameDto())) {
			if (!TypeConvUtil.isNullOrEmpty(populateLetterDto.getEmployeePersPhNameDto().getNmNameFirst())) {
				BookmarkDto bookmarkNmNameFirst = createBookmark(BookmarkConstants.WORKER_NAME_FIRST,
						populateLetterDto.getEmployeePersPhNameDto().getNmNameFirst());
				bookmarkNonFrmGrpList.add(bookmarkNmNameFirst);
			}
			if (!TypeConvUtil.isNullOrEmpty(populateLetterDto.getEmployeePersPhNameDto().getNmNameMiddle())) {
				BookmarkDto bookmarkNmNameFirst = createBookmark(BookmarkConstants.WORKER_NAME_MIDDLE,
						populateLetterDto.getEmployeePersPhNameDto().getNmNameMiddle());
				bookmarkNonFrmGrpList.add(bookmarkNmNameFirst);
			}
		}

		// Populate NmNameLast value from DAM CSEC01D
		if (!TypeConvUtil.isNullOrEmpty(populateLetterDto.getEmployeePersPhNameDto())) {
			if (!TypeConvUtil.isNullOrEmpty(populateLetterDto.getEmployeePersPhNameDto().getNmNameLast())) {
				BookmarkDto bookmarkNmNameLast = createBookmark(BookmarkConstants.WORKER_NAME_LAST,
						populateLetterDto.getEmployeePersPhNameDto().getNmNameLast());
				bookmarkNonFrmGrpList.add(bookmarkNmNameLast);
			}
		}

		// Populate SysDtGenericSysdate value from DAM CSEC02D
		BookmarkDto bookmarkSysDtGenericSysdate = createBookmark(BookmarkConstants.SYSTEM_DATE,
				DateUtils.stringDt(populateLetterDto.getGenCaseInfoDto().getDtSysDtGenericSysdate()));
		bookmarkNonFrmGrpList.add(bookmarkSysDtGenericSysdate);

		// Populate IdCase value from DAM CSEC02D
		BookmarkDto bookmarklIdCase = createBookmark(BookmarkConstants.CASE_NUMBER,
				populateLetterDto.getGenCaseInfoDto().getIdCase());
		bookmarkNonFrmGrpList.add(bookmarklIdCase);

		// for (CaseInfoDto caseInfoDtoType :
		// populateLetterDto.getCaseInfoDto()) {

		// Populate AddrPersAddrStLn1 value from DAM CSEC18D
		BookmarkDto bookmarkAddrPersAddrStLn1 = createBookmark(BookmarkConstants.REP_ADDRESS_LINE_1,
				caseInfoDtoType.getAddrPersAddrStLn1());
		bookmarkNonFrmGrpList.add(bookmarkAddrPersAddrStLn1);

		// Populate CdNameSuffix value from DAM CSEC18D
		BookmarkDto bookmarkCdNameSuffix2 = createBookmarkWithCodesTable(BookmarkConstants.REP_NAME_SUFFIX,
				caseInfoDtoType.getCdNameSuffix(), CodesConstant.CSUFFIX);
		bookmarkNonFrmGrpList.add(bookmarkCdNameSuffix2);

		// Populate AddrPersAddrStLn1 value from DAM CSEC18D
		BookmarkDto bookmarkNmNameFirst2 = createBookmark(BookmarkConstants.REP_NAME_FIRST,
				caseInfoDtoType.getNmNameFirst());
		bookmarkNonFrmGrpList.add(bookmarkNmNameFirst2);

		// Populate NmNameLast value from DAM CSEC18D
		BookmarkDto bookmarkNmNameLast2 = createBookmark(BookmarkConstants.REP_NAME_LAST,
				caseInfoDtoType.getNmNameLast());
		bookmarkNonFrmGrpList.add(bookmarkNmNameLast2);

		// Populate NmNameMiddle value from DAM CSEC18D
		BookmarkDto bookmarkNmNameMiddle = createBookmark(BookmarkConstants.REP_NAME_MIDDLE,
				caseInfoDtoType.getNmNameMiddle());
		bookmarkNonFrmGrpList.add(bookmarkNmNameMiddle);

		// }

		// Populate SysNbrGenericCntr value from DAM CINTA1D
		// Warranty Defect Fix - 12154 - To set Value for the Reporter's Confidential Pop-up
		BookmarkDto bookmarkSysNbrGenericCntr = createBookmark(BookmarkConstants.POP_UP,
				!ObjectUtils.isEmpty(populateLetterDto.getUlSysNbrGenericCntr())?ServiceConstants.TRUE:ServiceConstants.FALSE);
		bookmarkNonFrmGrpList.add(bookmarkSysNbrGenericCntr);
	

		PreFillDataServiceDto preFillData = new PreFillDataServiceDto();
		preFillData.setFormDataGroupList(formDataGroupList);
		preFillData.setBookmarkDtoList(bookmarkNonFrmGrpList);

		return preFillData;

	}

}
