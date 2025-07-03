package us.tx.state.dfps.service.forms.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.populateletter.dto.CodesTablesDto;
import us.tx.state.dfps.service.common.BookmarkConstants;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.FormConstants;
import us.tx.state.dfps.service.common.FormGroupsConstants;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.forms.dto.BookmarkDto;
import us.tx.state.dfps.service.forms.dto.FormDataGroupDto;
import us.tx.state.dfps.service.forms.dto.PermanencyPlanMeetEnglishDto;
import us.tx.state.dfps.service.forms.dto.PptDetailsOutDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.lookup.dao.LookupDao;
import us.tx.state.dfps.service.person.dto.AddressDto;
import us.tx.state.dfps.service.person.dto.PPTParticipantDto;
import us.tx.state.dfps.service.placement.dto.NameDetailDto;
import us.tx.state.dfps.service.placement.dto.StagePersonLinkCaseDto;
import us.tx.state.dfps.service.workload.dto.EmployeePersPhNameDto;
import us.tx.state.dfps.service.workload.dto.StagePersonDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:PermanencyPlanMeetEnglishPrefillData prefill data population for
 * Form CSC06o0 -- english Feb 12, 2018- 4:57:03 PM © 2017 Texas Department of
 * Family and Protective Services
 */
@Component
public class PermanencyPlanMeetEnglishPrefillData extends DocumentServiceUtil {
	/**
	 * 
	 * Method Name: returnPrefillData Method Description:This method is used to
	 * prefill the data from the different Dao by passing Dao output Dtos and
	 * bookmark and form group bookmark Dto as objects as input request
	 * 
	 * @param parentDtoobj
	 * @return prefillData
	 * 
	 */

	public static final String COMMA_SPACE_STRING = ", ";
	public static final char SPACE_STRING = ' ';

	@Autowired
	LookupDao lookupDao;

	public PreFillDataServiceDto returnPrefillData(Object parentDtoobj) {

		PermanencyPlanMeetEnglishDto ppmEngDto = (PermanencyPlanMeetEnglishDto) parentDtoobj;

		if (null == ppmEngDto.getEmployeePersPhNameDto()) {
			ppmEngDto.setEmployeePersPhNameDto(new EmployeePersPhNameDto());
		}

		if (null == ppmEngDto.getAddressDtoList()) {
			ppmEngDto.setAddressDtoList(new ArrayList<AddressDto>());
		}

		if (null == ppmEngDto.getNameDetailDto()) {
			ppmEngDto.setNameDetailDto(new NameDetailDto());
		}
		if (null == ppmEngDto.getCodesTablesDto()) {
			ppmEngDto.setCodesTablesDto(new CodesTablesDto());
		}

		if (null == ppmEngDto.getPptDetailsOutDto()) {
			ppmEngDto.setPptDetailsOutDto(new PptDetailsOutDto());
		}

		if (null == ppmEngDto.getPptParticipantDto()) {
			ppmEngDto.setPptParticipantDto(new PPTParticipantDto());
		}

		if (null == ppmEngDto.getStagePersonDto()) {
			ppmEngDto.setStagePersonDto(new StagePersonDto());
		}
		if (null == ppmEngDto.getStagePersonLinkCaseDto()) {
			ppmEngDto.setStagePersonLinkCaseDto(new StagePersonLinkCaseDto());
		}
		/**
		 * Description: Populating the non form group data into prefill data
		 * GroupName: None BookMark: Condition: None
		 */

		List<BookmarkDto> bkDefaultDtoList = new ArrayList<BookmarkDto>();

		if (!TypeConvUtil.isNullOrEmpty(ppmEngDto.getEmployeePersPhNameDto())
				&& StringUtils.isNotBlank(ppmEngDto.getEmployeePersPhNameDto().getCdNameSuffix())) {
			BookmarkDto bkPptWorkerSuffix = createBookmarkWithCodesTable(BookmarkConstants.PPT_WORKER_SUFFIX,
					ppmEngDto.getEmployeePersPhNameDto().getCdNameSuffix(), CodesConstant.CSUFFIX2);
			bkDefaultDtoList.add(bkPptWorkerSuffix);
		} else {
			BookmarkDto bkPptWorkerSuffix = createBookmark(BookmarkConstants.PPT_WORKER_SUFFIX,
					FormConstants.EMPTY_STRING);
			bkDefaultDtoList.add(bkPptWorkerSuffix);
		}

		BookmarkDto bkPptNbrMailCode = createBookmark(BookmarkConstants.PPT_WORKER_PHONE,
				TypeConvUtil.formatPhone(ppmEngDto.getEmployeePersPhNameDto().getNbrPhone()));
		bkDefaultDtoList.add(bkPptNbrMailCode);

		BookmarkDto bkPptWorkerFirst = createBookmark(BookmarkConstants.PPT_WORKER_FIRST,
				ppmEngDto.getEmployeePersPhNameDto().getNmNameFirst());
		bkDefaultDtoList.add(bkPptWorkerFirst);

		BookmarkDto bkPptWorkerLast = createBookmark(BookmarkConstants.PPT_WORKER_LAST,
				ppmEngDto.getEmployeePersPhNameDto().getNmNameLast());
		bkDefaultDtoList.add(bkPptWorkerLast);

		BookmarkDto bkPptWorkerMiddle = createBookmark(BookmarkConstants.PPT_WORKER_MIDDLE,
				ppmEngDto.getEmployeePersPhNameDto().getNmNameMiddle());
		bkDefaultDtoList.add(bkPptWorkerMiddle);

		BookmarkDto bkPptDate = createBookmark(BookmarkConstants.PPT_DATE,
				DateUtils.stringDt(ppmEngDto.getPptDetailsOutDto().getDtPptDate()));
		bkDefaultDtoList.add(bkPptDate);

		BookmarkDto bkPptCity = createBookmark(BookmarkConstants.PPT_CITY,
				ppmEngDto.getPptDetailsOutDto().getAddrPptCity());
		bkDefaultDtoList.add(bkPptCity);

		BookmarkDto bkPptStreetOne = createBookmark(BookmarkConstants.PPT_STREET1,
				ppmEngDto.getPptDetailsOutDto().getAddrPptStLn1());
		bkDefaultDtoList.add(bkPptStreetOne);

		BookmarkDto bkPptStreetTwo = createBookmark(BookmarkConstants.PPT_STREET2,
				ppmEngDto.getPptDetailsOutDto().getAddrPptStLn2());
		bkDefaultDtoList.add(bkPptStreetTwo);

		BookmarkDto bkPptState = createBookmarkWithCodesTable(BookmarkConstants.PPT_STATE,
				ppmEngDto.getPptDetailsOutDto().getAddrPptState(), CodesConstant.CSTATE);
		bkDefaultDtoList.add(bkPptState);

		BookmarkDto bkPptZip = createBookmark(BookmarkConstants.PPT_ZIP,
				ppmEngDto.getPptDetailsOutDto().getAddrPptZip());
		bkDefaultDtoList.add(bkPptZip);
		// Check if tmScrTmGeneric1 is null or not.
		BookmarkDto bkPptTime = createBookmark(BookmarkConstants.PPT_TIME,
				ppmEngDto.getPptDetailsOutDto().getTmScrTmGeneric1());
		bkDefaultDtoList.add(bkPptTime);

		/**
		 * Concatenating the first , middle and last name to show the full name
		 * is correct format as expected on display.
		 */
		if (!ObjectUtils.isEmpty(ppmEngDto.getPptParticipantDto())
				&& !ObjectUtils.isEmpty(ppmEngDto.getPptParticipantDto().getNmPptPartFull())) {
			BookmarkDto bkPptDpFullName = createBookmark(BookmarkConstants.DP_NAME,
					ppmEngDto.getPptParticipantDto().getNmPptPartFull());
			bkDefaultDtoList.add(bkPptDpFullName);
			BookmarkDto bkPptHdFullName = createBookmark(BookmarkConstants.HD_NAME,
					ppmEngDto.getPptParticipantDto().getNmPptPartFull());
			bkDefaultDtoList.add(bkPptHdFullName);
		}

		BookmarkDto bkPptCurrDate = createBookmark(BookmarkConstants.CURRENT_DATE,
				DateUtils.stringDt(ppmEngDto.getStagePersonLinkCaseDto().getDtCurrent()));
		bkDefaultDtoList.add(bkPptCurrDate);

		BookmarkDto bkPptNbrAge = createBookmark(BookmarkConstants.PPT_CHILD_AGE,
				ppmEngDto.getStagePersonLinkCaseDto().getNbrPersonAge());
		bkDefaultDtoList.add(bkPptNbrAge);

		BookmarkDto bkPptChildSuffix = createBookmarkWithCodesTable(BookmarkConstants.PPT_CHILD_SUFFIX,
				ppmEngDto.getNameDetailDto().getCdNameSuffix(), CodesConstant.CSUFFIX2);

		bkDefaultDtoList.add(bkPptChildSuffix);

		BookmarkDto bkPptChildFirst = createBookmark(BookmarkConstants.PPT_CHILD_FIRST,
				ppmEngDto.getNameDetailDto().getNmNameFirst());
		bkDefaultDtoList.add(bkPptChildFirst);

		BookmarkDto bkPptChildLast = createBookmark(BookmarkConstants.PPT_CHILD_LAST,
				ppmEngDto.getNameDetailDto().getNmNameLast());
		bkDefaultDtoList.add(bkPptChildLast);

		BookmarkDto bkPptChildMiddle = createBookmark(BookmarkConstants.PPT_CHILD_MIDDLE,
				ppmEngDto.getNameDetailDto().getNmNameMiddle());
		bkDefaultDtoList.add(bkPptChildMiddle);

		if (!ObjectUtils.isEmpty(ppmEngDto.getNmChangeNameDetailDto())
				&& StringUtils.isNotBlank(ppmEngDto.getNmChangeNameDetailDto().getCdNameSuffix())) {
			BookmarkDto bkPptDpSuffix = createBookmarkWithCodesTable(BookmarkConstants.DP_SUFFIX_NAME,
					ppmEngDto.getNmChangeNameDetailDto().getCdNameSuffix(), CodesConstant.CSUFFIX2);
			bkDefaultDtoList.add(bkPptDpSuffix);

			BookmarkDto bkPptHdSuffix = createBookmarkWithCodesTable(BookmarkConstants.HD_SUFFIX_NAME,
					ppmEngDto.getNmChangeNameDetailDto().getCdNameSuffix(), CodesConstant.CSUFFIX2);
			bkDefaultDtoList.add(bkPptHdSuffix);
		}

		/**
		 * The Dp Details is fetched from the getPptParticipantDto(CSES40D)
		 * while the child details are fetched from (CSEC35D)getNameDetailDto.
		 * According the document manager, the DP_LAST_NAME should be fetched
		 * from CSEC35D. Not giving correct results, hence changing to fetch
		 * from CSES40D. Additionally, since the DP_NAME has the full name of
		 * the person, hence populating empty for the DP_LAST_NAME and
		 * DP_MIDDLE_NAME. Otherwise, per the html code, the last_name and
		 * full_name both are displayed.
		 * 
		 */
		if (!ObjectUtils.isEmpty(ppmEngDto.getNmChangeNameDetailDto())
				&& StringUtils.isNotBlank(ppmEngDto.getNmChangeNameDetailDto().getNmNameLast())) {
			BookmarkDto bkPptDpLast = createBookmark(BookmarkConstants.DP_LAST_NAME, ppmEngDto.getNmChangeNameDetailDto().getNmNameLast());
			bkDefaultDtoList.add(bkPptDpLast);
			BookmarkDto bkPptHdLast = createBookmark(BookmarkConstants.HD_LAST_NAME, ppmEngDto.getNmChangeNameDetailDto().getNmNameLast());
			bkDefaultDtoList.add(bkPptHdLast);
		}

		if (!ObjectUtils.isEmpty(ppmEngDto.getNmChangeNameDetailDto())
				&& StringUtils.isNotBlank(ppmEngDto.getNmChangeNameDetailDto().getNmNameMiddle())) {
			BookmarkDto bkPptDpMiddle = createBookmark(BookmarkConstants.DP_MIDDLE_NAME,
					ppmEngDto.getNmChangeNameDetailDto().getNmNameMiddle());
			bkDefaultDtoList.add(bkPptDpMiddle);
			BookmarkDto bkPptHdMiddle = createBookmark(BookmarkConstants.HD_MIDDLE_NAME,
					ppmEngDto.getNmChangeNameDetailDto().getNmNameMiddle());
			bkDefaultDtoList.add(bkPptHdMiddle);
		}

		/**
		 * Description: Places address information for recipient of letter on
		 * letter. GroupName: csc06o01 BookMark: TMPLAT_ADDRESS SubGroups:
		 * cfzz1701 Condition: IndPersAddrLinkPrimary = Y ,
		 * IndPersAddrLinkInvalid = N, DtPersAddrLinkEnd !=null
		 */
		List<FormDataGroupDto> formDataGroupList = new ArrayList<FormDataGroupDto>();
		if (!ObjectUtils.isEmpty(ppmEngDto.getAddressDtoList()) && ppmEngDto.getAddressDtoList().size() > ServiceConstants.Zero
				&& !TypeConvUtil.isNullOrEmpty(ppmEngDto.getAddressDtoList().get(ServiceConstants.Zero))) {
			AddressDto addr = ppmEngDto.getAddressDtoList().get(ServiceConstants.Zero);
			if (ServiceConstants.N.equalsIgnoreCase(addr.getIndPersAddrLinkInvalid())
					&& ServiceConstants.Y.equalsIgnoreCase(addr.getIndPersAddrLinkPrimary())
					&& !TypeConvUtil.isNullOrEmpty(addr.getDtPersAddrLinkEnd())) {
				FormDataGroupDto tmplatAddr = createFormDataGroup(FormGroupsConstants.TMPLAT_ADDRESS,
						FormConstants.EMPTY_STRING);
				List<BookmarkDto> bkAddrList = new ArrayList<BookmarkDto>();

				BookmarkDto bkAddrDpZip = createBookmark(BookmarkConstants.DP_ZIP, addr.getAddrZip());
				bkAddrList.add(bkAddrDpZip);

				BookmarkDto bkAddrDpCity = createBookmark(BookmarkConstants.DP_CITY, addr.getAddrCity());
				bkAddrList.add(bkAddrDpCity);

				BookmarkDto bkAddrDpStreetOne = createBookmark(BookmarkConstants.DP_STREET1,
						addr.getAddrPersAddrStLn1());
				bkAddrList.add(bkAddrDpStreetOne);

				BookmarkDto bkAddrDpState = createBookmark(BookmarkConstants.DP_STATE, addr.getCdAddrState());
				bkAddrList.add(bkAddrDpState);

				tmplatAddr.setBookmarkDtoList(bkAddrList);

				/**
				 * Description: GroupName: cfzz1701 BookMark: TMPLAT_STREET2
				 * SubGroup : None ParentGroup : csc06o01 Condition:
				 * szAddrPersAddrStLn2 != null ,dtDtPersAddrLinkEnd = 12/31/4712
				 * 12:00:00 AM
				 */
				List<FormDataGroupDto> tmplatStreetList = new ArrayList<FormDataGroupDto>();
				if (!TypeConvUtil.isNullOrEmpty(addr.getAddrPersAddrStLn2())
						&& addr.getDtPersAddrLinkEnd().equals(ServiceConstants.GENERIC_END_DATE)) {

					FormDataGroupDto tmplatStreet = createFormDataGroup(FormGroupsConstants.TMPLAT_STREET2,
							FormGroupsConstants.TMPLAT_ADDRESS);
					List<BookmarkDto> bkAddrStTwo = new ArrayList<BookmarkDto>();

					BookmarkDto bkAddrStreetTwo = createBookmark(BookmarkConstants.STREET_LN_2,
							addr.getAddrPersAddrStLn2());
					bkAddrStTwo.add(bkAddrStreetTwo);

					tmplatStreet.setBookmarkDtoList(bkAddrStTwo);
					tmplatStreetList.add(tmplatStreet);

				}
				tmplatAddr.setFormDataGroupList(tmplatStreetList);
				formDataGroupList.add(tmplatAddr);
			}
		}

		/**
		 * Description: Places a comma in the name of the person addressed in
		 * the letter, if that person's name suffix is non-blank. GroupName:
		 * cfzco00 BookMark: TMPLAT_COMMA SubGroup : None ParentGroup : None
		 * Condition: szCdNameSuffix != null
		 */

		if (!TypeConvUtil.isNullOrEmpty(ppmEngDto.getEmployeePersPhNameDto())
				&& StringUtils.isNotBlank(ppmEngDto.getEmployeePersPhNameDto().getCdNameSuffix())) {
			FormDataGroupDto tmplatComma = createFormDataGroup(FormGroupsConstants.TMPLAT_COMMA,
					FormConstants.EMPTY_STRING);
			formDataGroupList.add(tmplatComma);
		}

		/**
		 * Description: GroupName: csc06ac BookMark: TMPLAT_PPT_WORKER_PHONE_EXT
		 * SubGroup : None ParentGroup : None Condition: NbrMailCodePhoneExt !=
		 * null
		 */

		if (!TypeConvUtil.isNullOrEmpty(ppmEngDto.getEmployeePersPhNameDto())
				&& StringUtils.isNotBlank(ppmEngDto.getEmployeePersPhNameDto().getNbrPhoneExtension())) {
			FormDataGroupDto tmplatWorkerPhoneExt = createFormDataGroup(FormGroupsConstants.TMPLAT_PPT_WORKER_PHONE_EXT,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> bkPhoneDetailsList = new ArrayList<BookmarkDto>();

			BookmarkDto bkPhoneExtn = createBookmark(BookmarkConstants.PPT_WORKER_PHONE_EXT_EXTENSION,
					ppmEngDto.getEmployeePersPhNameDto().getNbrPhoneExtension());
			bkPhoneDetailsList.add(bkPhoneExtn);

			tmplatWorkerPhoneExt.setBookmarkDtoList(bkPhoneDetailsList);
			formDataGroupList.add(tmplatWorkerPhoneExt);
		}

		/**
		 * Description: Places name and city of board member on letter.
		 * GroupName: cfzz0501 BookMark: TMPLAT_HEADER_BOARD SubGroup : None
		 * ParentGroup : None Condition: szSysCode > 001
		 */

		if (!TypeConvUtil.isNullOrEmpty(ppmEngDto.getCodesTablesDto())
				&& StringUtils.isNotBlank(ppmEngDto.getCodesTablesDto().getaCode())
				&& ServiceConstants.CODE_TYPE_PPM < (Integer.valueOf(ppmEngDto.getCodesTablesDto().getaCode()))) {
			FormDataGroupDto tmplatHeaderBoard = createFormDataGroup(FormGroupsConstants.TMPLAT_HEADER_BOARD,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> bkHeaderBoardList = new ArrayList<BookmarkDto>();

			BookmarkDto bkHeaderCity = createBookmark(BookmarkConstants.HEADER_BOARD_CITY,
					ppmEngDto.getCodesTablesDto().getaDecode());
			bkHeaderBoardList.add(bkHeaderCity);

			BookmarkDto bkHeaderName = createBookmark(BookmarkConstants.HEADER_BOARD_NAME,
					ppmEngDto.getCodesTablesDto().getbDecode());
			bkHeaderBoardList.add(bkHeaderName);

			tmplatHeaderBoard.setBookmarkDtoList(bkHeaderBoardList);
			formDataGroupList.add(tmplatHeaderBoard);
		}

		/**
		 * Description: Places director title and name on letter. GroupName:
		 * cfzz0601 BookMark: TMPLAT_HEADER_DIRECTOR SubGroup : None ParentGroup
		 * : None Condition: szSysCode = 001
		 */
		if (!TypeConvUtil.isNullOrEmpty(ppmEngDto.getCodesTablesDto())
				&& StringUtils.isNotBlank(ppmEngDto.getCodesTablesDto().getaCode())
				&& ServiceConstants.CODE_TYPE_PPM == (Integer.valueOf(ppmEngDto.getCodesTablesDto().getaCode()))) {
			FormDataGroupDto tmplatHeaderBoardDir = createFormDataGroup(FormGroupsConstants.TMPLAT_HEADER_DIRECTOR,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> bkheaderDirectorList = new ArrayList<BookmarkDto>();

			BookmarkDto bkHeaderTitle = createBookmark(BookmarkConstants.HEADER_DIRECTOR_TITLE,
					ppmEngDto.getCodesTablesDto().getaDecode());
			bkheaderDirectorList.add(bkHeaderTitle);
			BookmarkDto bkHeaderName = createBookmark(BookmarkConstants.HEADER_DIRECTOR_NAME,
					ppmEngDto.getCodesTablesDto().getbDecode());
			bkheaderDirectorList.add(bkHeaderName);
			tmplatHeaderBoardDir.setBookmarkDtoList(bkheaderDirectorList);
			formDataGroupList.add(tmplatHeaderBoardDir);
		}

		/**
		 * Description: Primary Child is under 16 GroupName: csc06aa BookMark:
		 * TMPLAT_PC_UNDER_16 SubGroup : None ParentGroup : None Condition:
		 * CdPersonDeath = F
		 */
		if (!TypeConvUtil.isNullOrEmpty(ppmEngDto.getStagePersonLinkCaseDto())
				&& StringUtils.isNotBlank(ppmEngDto.getStagePersonLinkCaseDto().getCdPersonDeath())
				&& ServiceConstants.UNDER_SIXTEEN
						.equalsIgnoreCase(ppmEngDto.getStagePersonLinkCaseDto().getCdPersonDeath())) {
			FormDataGroupDto tmplatUnderSixteen = createFormDataGroup(FormGroupsConstants.TMPLAT_PC_UNDER_16,
					FormConstants.EMPTY_STRING);
			formDataGroupList.add(tmplatUnderSixteen);
		}

		/**
		 * Description: Primary Child is 16 years or older GroupName: csc06ab
		 * BookMark: TMPLAT_PC_16_OR_OLDER SubGroup : None ParentGroup : None
		 * Condition: CdPersonDeath = G
		 */
		if (!TypeConvUtil.isNullOrEmpty(ppmEngDto.getStagePersonLinkCaseDto())
				&& StringUtils.isNotBlank(ppmEngDto.getStagePersonLinkCaseDto().getCdPersonDeath())
				&& ServiceConstants.SIXTEEN_AND_OVER
						.equalsIgnoreCase(ppmEngDto.getStagePersonLinkCaseDto().getCdPersonDeath())) {
			FormDataGroupDto tmplatOverSixteen = createFormDataGroup(FormGroupsConstants.TMPLAT_PC_16_OR_OLDER,
					FormConstants.EMPTY_STRING);
			formDataGroupList.add(tmplatOverSixteen);
		}

		/**
		 * Description: Places a comma in the child's name, if the name suffix
		 * is non-blank GroupName: cfzco01 BookMark: TMPLAT_COMMA_2 SubGroup :
		 * None ParentGroup : None Condition: CdNameSuffix != null
		 */
		if (!TypeConvUtil.isNullOrEmpty(ppmEngDto.getNameDetailDto())
				&& StringUtils.isNotBlank(ppmEngDto.getNameDetailDto().getCdNameSuffix())) {
			FormDataGroupDto tmplatCommaTwo = createFormDataGroup(FormGroupsConstants.TMPLAT_COMMA_2,
					FormConstants.EMPTY_STRING);
			formDataGroupList.add(tmplatCommaTwo);
		}

		/**
		 * Description:PLaces a comma in the name in the salutation, if the name
		 * suffix is non-blank GroupName: cfzco02 BookMark: TMPLAT_COMMA_3
		 * SubGroup : None ParentGroup : None Condition: CdNameSuffix != null
		 */
		/*
		 * if(!TypeConvUtil.isNullOrEmpty(ppmEngDto.getNameDetailDto()) &&
		 * StringUtils.isNotBlank(ppmEngDto.getNameDetailDto().getCdNameSuffix()
		 * )){ FormDataGroupDto tmplatCommaThree=
		 * createFormDataGroup(FormGroupsConstants.TMPLAT_COMMA_3,
		 * FormConstants.EMPTY_STRING); formDataGroupList.add(tmplatCommaThree);
		 * }
		 */
		// Adding groups to parent and prefill data.
		PreFillDataServiceDto prefillData = new PreFillDataServiceDto();
		prefillData.setFormDataGroupList(formDataGroupList);
		prefillData.setBookmarkDtoList(bkDefaultDtoList);
		return prefillData;
	}
}
