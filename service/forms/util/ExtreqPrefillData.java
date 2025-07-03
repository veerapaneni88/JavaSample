package us.tx.state.dfps.service.forms.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.notiftolawenforcement.dto.MultiAddressDto;
import us.tx.state.dfps.service.common.BookmarkConstants;
import us.tx.state.dfps.service.common.FormConstants;
import us.tx.state.dfps.service.common.FormGroupsConstants;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.extreq.ApsextreqDto;
import us.tx.state.dfps.service.forms.dto.BookmarkDto;
import us.tx.state.dfps.service.forms.dto.FormDataGroupDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.investigation.dto.AllegationDetailDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:ExtreqPrefillData will implemented returnPrefillData operation
 * defined in DocumentServiceUtil Interface to populate the prefill data for
 * form extreq Mar 15, 2018- 11:19:59 AM © 2017 Texas Department of Family and
 * Protective Services
 */

@Component
public class ExtreqPrefillData extends DocumentServiceUtil {

	public ExtreqPrefillData() {

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

		ApsextreqDto apsextreqDto = (ApsextreqDto) parentDtoobj;

		if (null == apsextreqDto) {
			apsextreqDto = new ApsextreqDto();
		}

		List<FormDataGroupDto> formDataGroupList = new ArrayList<FormDataGroupDto>();
		
		List<BookmarkDto> bookmarkNonFrmGrpList = new ArrayList<BookmarkDto>();
		/**
		 * Checking the dtDtApproversDetermination and szCdApproversStatus. If
		 * condition satisfies set the prefill data for group ext01
		 */
		if (!ObjectUtils.isEmpty(apsextreqDto.getExtreqDto())) {

			if (!ObjectUtils.isEmpty(apsextreqDto.getExtreqDto().getDtApproversDetermination())
					&& ServiceConstants.EVENTSTATUS_APPROVE
							.equalsIgnoreCase(apsextreqDto.getExtreqDto().getCdApproversStatus())) {
				FormDataGroupDto tempApprovalFrmDataGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_APPROVAL,
						FormConstants.EMPTY_STRING);

				List<FormDataGroupDto> tempChildNameFrmDataGrpList = new ArrayList<FormDataGroupDto>();

				List<BookmarkDto> bookmarkChildNameList = new ArrayList<BookmarkDto>();
				BookmarkDto bookmarkdtDtApproversDeterminationDto = createBookmark(BookmarkConstants.DT_APPROVAL,
						DateUtils.stringDt(apsextreqDto.getExtreqDto().getDtApproversDetermination()));
				BookmarkDto bookmarkCdNameSuffixDto = createBookmark(BookmarkConstants.NM_SUP_SUFFIX,
						apsextreqDto.getExtreqDto().getCdEmployeeSuffix());
				BookmarkDto bookmarkNmNameLastDto = createBookmark(BookmarkConstants.NM_SUP_LAST,
						apsextreqDto.getExtreqDto().getNmEmployeeLast());
				BookmarkDto bookmarkNmNameMiddleDto = createBookmark(BookmarkConstants.NM_SUP_MIDDLE,
						apsextreqDto.getExtreqDto().getNmEmployeeMiddle());
				BookmarkDto bookmarkNmNameFirstDto = createBookmark(BookmarkConstants.NM_SUP_FIRST,
						apsextreqDto.getExtreqDto().getNmEmployeeFirst());
				bookmarkChildNameList.add(bookmarkdtDtApproversDeterminationDto);
				bookmarkChildNameList.add(bookmarkCdNameSuffixDto);
				bookmarkChildNameList.add(bookmarkNmNameLastDto);
				bookmarkChildNameList.add(bookmarkNmNameMiddleDto);
				bookmarkChildNameList.add(bookmarkNmNameFirstDto);
				tempApprovalFrmDataGrpDto.setBookmarkDtoList(bookmarkChildNameList);

				// Set prefill data for the subgroup - ext02

				if (!ObjectUtils.isEmpty(apsextreqDto.getExtreqDto().getCdEmployeeSuffix())) {
					FormDataGroupDto tempCommaChildFrmDataGrpDto = createFormDataGroup(
							FormGroupsConstants.TMPLAT_COMMA1, FormGroupsConstants.TMPLAT_APPROVAL);
					tempChildNameFrmDataGrpList.add(tempCommaChildFrmDataGrpDto);
				}

				tempApprovalFrmDataGrpDto.setFormDataGroupList(tempChildNameFrmDataGrpList);
				formDataGroupList.add(tempApprovalFrmDataGrpDto);
			}
		}

		/**
		 * Populating the non form group data into prefill data
		 */

		

		// Bookmarks for CSEC23D

		if (!ObjectUtils.isEmpty(apsextreqDto.getExtreqDto())) {

			BookmarkDto bookmarkIdEvent = createBookmark(BookmarkConstants.ID_EVENT,
					apsextreqDto.getExtreqDto().getIdEvent());
			bookmarkNonFrmGrpList.add(bookmarkIdEvent);

		}
		String currentDate = new SimpleDateFormat("MM/dd/yyyy").format(new Date());
		String dtCaseOpened = new SimpleDateFormat("MM/dd/yyyy").format(apsextreqDto.getDtCaseOpened());

		// Bookmark for ADULT PROTECTIVE SERVICES EXTENSION REQUEST FORM
		//ALM ID: 93062 : use case intake date for Allegation Received date
		BookmarkDto bookmarkAllegDateDto = createBookmark(BookmarkConstants.ALLEG_DATE, dtCaseOpened);
		bookmarkNonFrmGrpList.add(bookmarkAllegDateDto);

		// Bookmark for ADULT PROTECTIVE SERVICES EXTENSION REQUEST FORM
		BookmarkDto bookmarkReqDateDto = createBookmark(BookmarkConstants.EXTN_REQUEST_DATE, currentDate);
		bookmarkNonFrmGrpList.add(bookmarkReqDateDto);

		// Bookmark for ADULT PROTECTIVE SERVICES EXTENSION REQUEST FORM
		BookmarkDto bookmarkCaseIdDto = createBookmark(BookmarkConstants.EXTN_CASE_NO, apsextreqDto.getIdCase());
		bookmarkNonFrmGrpList.add(bookmarkCaseIdDto);

		/*
		 * Prefill txtPersonServed for ADULT PROTECTIVE SERVICES EXTENSION
		 * REQUEST FORM
		 */
        		
		if (!ObjectUtils.isEmpty(apsextreqDto.getAllegationDetailDto())) {

			List<AllegationDetailDto> allegationDetailList = apsextreqDto.getAllegationDetailDto();
			if (allegationDetailList.size() > ServiceConstants.Zero_INT) {
				for (AllegationDetailDto allegationDetailDto : allegationDetailList) {
					BookmarkDto bookmarPersonName = createBookmark(BookmarkConstants.EXTN_PERSON_NAME,
							allegationDetailDto.getScrPersVictim());
					bookmarkNonFrmGrpList.add(bookmarPersonName);

					break;
				}
			}
		}
		
		
		/*
		 * Prefill txtFacilityName for ADULT PROTECTIVE SERVICES EXTENSION
		 * REQUEST FORM
		 */

		if (!ObjectUtils.isEmpty(apsextreqDto.getTxtFacilityName())) {
			BookmarkDto bookmarFacilityName = createBookmark(BookmarkConstants.EXTN_FACILITY_NAME,
					apsextreqDto.getTxtFacilityName());
			bookmarkNonFrmGrpList.add(bookmarFacilityName);

		} else {
			BookmarkDto bookmarFacilityName = createBookmark(BookmarkConstants.EXTN_FACILITY_NAME,
					FormConstants.EMPTY_STRING);
			bookmarkNonFrmGrpList.add(bookmarFacilityName);

		}

		
		if(!ObjectUtils.isEmpty(apsextreqDto.getMultiAddressDtoList()))
		{
		for(MultiAddressDto multiAddressDto:apsextreqDto.getMultiAddressDtoList())
		{
		FormDataGroupDto tempFacilityFrmDataGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_FACILITY,
				FormConstants.EMPTY_STRING);
		List<BookmarkDto> bookmarkProviderNameList = new ArrayList<BookmarkDto>();
		BookmarkDto bookmarFacilityName = createBookmark(BookmarkConstants.FACILITY_NAME,
				multiAddressDto.getaCpNmResource());
		bookmarkProviderNameList.add(bookmarFacilityName);
		tempFacilityFrmDataGrpDto.setBookmarkDtoList(bookmarkProviderNameList);	
		formDataGroupList.add(tempFacilityFrmDataGrpDto);
		
		}	
		}
		
		
		PreFillDataServiceDto preFillData = new PreFillDataServiceDto();
		preFillData.setFormDataGroupList(formDataGroupList);
		preFillData.setBookmarkDtoList(bookmarkNonFrmGrpList);

		return preFillData;
	}

}
