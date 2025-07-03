package us.tx.state.dfps.service.forms.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import us.tx.state.dfps.service.common.BookmarkConstants;
import us.tx.state.dfps.service.common.FormConstants;
import us.tx.state.dfps.service.common.FormGroupsConstants;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.forms.dto.BookmarkDto;
import us.tx.state.dfps.service.forms.dto.FormDataGroupDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.workload.dto.EmployeePersPhNameDto;
import us.tx.state.dfps.service.workload.dto.MrefCssContactDtlsDto;
import us.tx.state.dfps.service.workload.dto.MrefSecondaryDtlsDto;
import us.tx.state.dfps.service.workload.dto.MrefStatusFormDtlsDto;
import us.tx.state.dfps.service.workload.dto.MrefStatusFormDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:AssignWorkloadPrefillData will implement returnPrefillData
 * operation defined in DocumentServiceUtil Interface to populate the prefill
 * data for form WKLD0100. March 8, 2018- 2:04:05 PM © 2017 Texas Department of
 * Family and Protective Services
 */

@Component
public class AssignWorkloadPrefillData extends DocumentServiceUtil {

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

		MrefStatusFormDto mrefStatusFormDto = (MrefStatusFormDto) parentDtoobj;
		if (null == mrefStatusFormDto.getEmployeePersPhNameDto()) {
			mrefStatusFormDto.setEmployeePersPhNameDto(new EmployeePersPhNameDto());
		}
		if (null == mrefStatusFormDto.getMrefStatusFormDtlsDto()) {
			List<MrefStatusFormDtlsDto> mrefStatusFormDtlslist = null;
			mrefStatusFormDto.setMrefStatusFormDtlsDto(mrefStatusFormDtlslist);
		}

		List<FormDataGroupDto> formDataGroupList = new ArrayList<FormDataGroupDto>();

		/**
		 * prefill data for group wkld0101 - TMPLAT_SUFFIX_COMMA
		 */

		FormDataGroupDto tempSuffixCommaFrmDataGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_SUFFIX_COMMA,
				FormConstants.EMPTY_STRING);
		formDataGroupList.add(tempSuffixCommaFrmDataGrpDto);

		List<MrefStatusFormDtlsDto> mrefStatusFormDtlslist = mrefStatusFormDto.getMrefStatusFormDtlsDto();

		for (MrefStatusFormDtlsDto mrefStatusFormDtlsDto : mrefStatusFormDtlslist) {

			List<FormDataGroupDto> tempMrefformDataGroupList = new ArrayList<FormDataGroupDto>();

			/**
			 * set prefill data for the parent group - wkld0110 - TMPLAT_MREF
			 */

			FormDataGroupDto tempMrefFrmDataGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_MREF,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkMrefList = new ArrayList<BookmarkDto>();
			BookmarkDto bookmarkDateDue = createBookmark(BookmarkConstants.DATE_DUE,
					TypeConvUtil.formDateFormat(mrefStatusFormDtlsDto.getMrefDtlsDto().getDtMrefDue()));
			BookmarkDto bookmarkStgName = createBookmark(BookmarkConstants.STAGE_NAME,
					mrefStatusFormDtlsDto.getMrefDtlsDto().getNmStage());
			BookmarkDto bookmarkCaseNo = createBookmark(BookmarkConstants.CASE_NUMBER,
					mrefStatusFormDtlsDto.getMrefDtlsDto().getIdCase());
			bookmarkMrefList.add(bookmarkDateDue);
			bookmarkMrefList.add(bookmarkStgName);
			bookmarkMrefList.add(bookmarkCaseNo);
			tempMrefFrmDataGrpDto.setBookmarkDtoList(bookmarkMrefList);

			/**
			 * Set prefill data for the subgroup - wkld0120 - TMPLAT_SECONDARY
			 */

			FormDataGroupDto tempSecondaryFrmDataGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_SECONDARY,
					FormGroupsConstants.TMPLAT_MREF);
			for (MrefSecondaryDtlsDto mrefSecondaryDtlsDto : mrefStatusFormDtlsDto.getMrefSecondaryDtlsDto()) {
				List<BookmarkDto> bookmarkSecondaryList = new ArrayList<BookmarkDto>();
				BookmarkDto bookmarkDateAssgn = createBookmark(BookmarkConstants.DATE_ASSIGNED,
						TypeConvUtil.formDateFormat(mrefSecondaryDtlsDto.getDtSecondaryAssgnd()));
				BookmarkDto bookmarkDateUnAssgn = createBookmark(BookmarkConstants.DATE_UNASSIGNED,
						TypeConvUtil.formDateFormat(mrefSecondaryDtlsDto.getDtSecondaryUnassgnd()));
				BookmarkDto bookmarkJobDesc = createBookmark(BookmarkConstants.JOB_DESCRIPTION,
						mrefSecondaryDtlsDto.getCdJobClassDecode());
				BookmarkDto bookmarkSecondaryNm = createBookmark(BookmarkConstants.SECONDARY_NAME,
						mrefSecondaryDtlsDto.getNmPersonFull());
				bookmarkSecondaryList.add(bookmarkDateAssgn);
				bookmarkSecondaryList.add(bookmarkDateUnAssgn);
				bookmarkSecondaryList.add(bookmarkJobDesc);
				bookmarkSecondaryList.add(bookmarkSecondaryNm);
				tempSecondaryFrmDataGrpDto.setBookmarkDtoList(bookmarkSecondaryList);
			}
			tempMrefformDataGroupList.add(tempSecondaryFrmDataGrpDto);

			/**
			 * Set prefill data for the subgroup - wkld0130 - TMPLAT_CONTACT
			 */

			FormDataGroupDto tempContactFrmDataGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_CONTACT,
					FormGroupsConstants.TMPLAT_MREF);
			for (MrefCssContactDtlsDto mrefCssContactDtlsDto : mrefStatusFormDtlsDto.getMrefCssContactDtlsDto()) {
				List<BookmarkDto> bookmarkContactList = new ArrayList<BookmarkDto>();
				BookmarkDto bookmarkContactDate = createBookmark(BookmarkConstants.CONTACT_DATE,
						TypeConvUtil.formDateFormat(mrefCssContactDtlsDto.getDtContactOccured()));
				BookmarkDto bookmarkContactPurpose = createBookmark(BookmarkConstants.CONTACT_PURPOSE,
						mrefCssContactDtlsDto.getCdContactPurposeDecode());
				createBookmark(BookmarkConstants.CONTACT_TYPE, mrefCssContactDtlsDto.getCdContactType());
				BookmarkDto bookmarkContactWorker = createBookmark(BookmarkConstants.CONTACT_WORKER,
						mrefCssContactDtlsDto.getNmPersonFull());
				bookmarkContactList.add(bookmarkContactDate);
				bookmarkContactList.add(bookmarkContactPurpose);
				bookmarkContactList.add(bookmarkContactWorker);
				tempContactFrmDataGrpDto.setBookmarkDtoList(bookmarkContactList);
			}
			tempMrefformDataGroupList.add(tempContactFrmDataGrpDto);

			tempMrefFrmDataGrpDto.setFormDataGroupList(tempMrefformDataGroupList);
			formDataGroupList.add(tempMrefFrmDataGrpDto);

		}

		/**
		 * Populating the non form group data into prefill data
		 */

		List<BookmarkDto> bookmarkNonFrmGrpList = new ArrayList<BookmarkDto>();
		BookmarkDto bookmarkWorkerNameSuffix = createBookmark(BookmarkConstants.WORKER_NAME_SUFFIX,
				mrefStatusFormDto.getEmployeePersPhNameDto().getCdNameSuffix());
		BookmarkDto bookmarkWorkerNameFirst = createBookmark(BookmarkConstants.WORKER_NAME_FIRST,
				mrefStatusFormDto.getEmployeePersPhNameDto().getNmNameFirst());
		BookmarkDto bookmarkWorkerNameLast = createBookmark(BookmarkConstants.WORKER_NAME_LAST,
				mrefStatusFormDto.getEmployeePersPhNameDto().getNmNameLast());
		BookmarkDto bookmarkWorkerNameMiddle = createBookmark(BookmarkConstants.WORKER_NAME_MIDDLE,
				mrefStatusFormDto.getEmployeePersPhNameDto().getNmNameMiddle());
		BookmarkDto bookmarkWorkerNumber = createBookmark(BookmarkConstants.WORKER_NUMBER,
				mrefStatusFormDto.getEmployeePersPhNameDto().getIdPerson());
		bookmarkNonFrmGrpList.add(bookmarkWorkerNameSuffix);
		bookmarkNonFrmGrpList.add(bookmarkWorkerNameFirst);
		bookmarkNonFrmGrpList.add(bookmarkWorkerNameLast);
		bookmarkNonFrmGrpList.add(bookmarkWorkerNameMiddle);
		bookmarkNonFrmGrpList.add(bookmarkWorkerNumber);

		PreFillDataServiceDto preFillData = new PreFillDataServiceDto();
		preFillData.setFormDataGroupList(formDataGroupList);
		preFillData.setBookmarkDtoList(bookmarkNonFrmGrpList);
		return preFillData;

	}

}
