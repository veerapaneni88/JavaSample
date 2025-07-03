package us.tx.state.dfps.service.forms.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
import us.tx.state.dfps.service.lookup.dao.LookupDao;
import us.tx.state.dfps.service.workload.dto.EmployeePersPhNameDto;
import us.tx.state.dfps.service.workload.dto.SijsEventContactDtlsDto;
import us.tx.state.dfps.service.workload.dto.SijsEventIdDtlsDto;
import us.tx.state.dfps.service.workload.dto.SijsSecondaryDtlsDto;
import us.tx.state.dfps.service.workload.dto.SijsStatusFormDtlsDto;
import us.tx.state.dfps.service.workload.dto.SijsStatusFormDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:SijsStatusFormPrefillData will implement returnPrefillData
 * operation defined in DocumentServiceUtil Interface to populate the prefill
 * data for form WKLD0200. March 19, 2018- 2:04:05 PM © 2017 Texas Department of
 * Family and Protective Services
 */

@Component
public class SijsStatusFormPrefillData extends DocumentServiceUtil {

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

	@Autowired
	LookupDao lookupDao;

	@Override
	public PreFillDataServiceDto returnPrefillData(Object parentDtoobj) {

		/**
		 * TypeCasting parentDtoobj to SijsStatusFormDto
		 */

		SijsStatusFormDto sijsStatusFormDto = (SijsStatusFormDto) parentDtoobj;

		/**
		 * Mapping Null Values if there is no data in the sijsStatusFormDto
		 */

		if (null == sijsStatusFormDto.getEmployeePersPhNameDto()) {
			sijsStatusFormDto.setEmployeePersPhNameDto(new EmployeePersPhNameDto());
		}
		if (null == sijsStatusFormDto.getSijsStatusFormDtlsDto()) {
			List<SijsStatusFormDtlsDto> sijsStatusFormDtlsDto = null;
			sijsStatusFormDto.setSijsStatusFormDtlsDto(sijsStatusFormDtlsDto);
		}

		List<FormDataGroupDto> formDataGroupList = new ArrayList<FormDataGroupDto>();

		/**
		 * prefill data for group wkld0210 - TMPLAT_WORKER_NAME_SUFFIX
		 */
		if (null != sijsStatusFormDto.getEmployeePersPhNameDto().getCdNameSuffix()) {
			FormDataGroupDto tempWorkerNameSuffiFrmDataGrpDto = createFormDataGroup(
					FormGroupsConstants.TMPLAT_WORKER_NAME_SUFFIX, FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkNameSuffix = new ArrayList<BookmarkDto>();
			BookmarkDto bookmarkWorkerNameSuffix = createBookmark(BookmarkConstants.WORKER_NAME_SUFFIX,
					sijsStatusFormDto.getEmployeePersPhNameDto().getCdNameSuffix());
			bookmarkNameSuffix.add(bookmarkWorkerNameSuffix);
			tempWorkerNameSuffiFrmDataGrpDto.setBookmarkDtoList(bookmarkNameSuffix);
			formDataGroupList.add(tempWorkerNameSuffiFrmDataGrpDto);
		}

		/**
		 * prefill data for group wkld0220 - TMPLAT_SIJS
		 */

		List<SijsStatusFormDtlsDto> SijsStatusFormDtlslist = sijsStatusFormDto.getSijsStatusFormDtlsDto();
		if (SijsStatusFormDtlslist.size() > ServiceConstants.Zero_INT) {
			for (SijsStatusFormDtlsDto sijsStatusFormDtlsDto : SijsStatusFormDtlslist) {

				List<FormDataGroupDto> tempSIJSformDataGroupList = new ArrayList<FormDataGroupDto>();

				List<FormDataGroupDto> tempSIJSNotesformDataGroupList = new ArrayList<FormDataGroupDto>();
				
				List<FormDataGroupDto> tempSIJSNotesNameformDataGroupList = new ArrayList<FormDataGroupDto>();

				FormDataGroupDto tempSIJSFrmDataGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_SIJS,
						FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkSIJS = new ArrayList<BookmarkDto>();
				BookmarkDto bookmarkSIJSEval = createBookmark(BookmarkConstants.SIJS_EVAL,
						sijsStatusFormDtlsDto.getSijsDtlsDto().getIndEvaluationConclusion());
				BookmarkDto bookmarkSIJSDob = createBookmark(BookmarkConstants.SIJS_DOB,
						TypeConvUtil.formDateFormat(sijsStatusFormDtlsDto.getSijsDtlsDto().getDt_PersonBirth()));
				BookmarkDto bookmarkSIJSEighteenth = createBookmark(BookmarkConstants.SIJS_EIGHTEENTH,
						sijsStatusFormDtlsDto.getSijsDtlsDto().getNbrDaysToMajority());
				BookmarkDto bookmarkSIJSAge = createBookmark(BookmarkConstants.SIJS_AGE,
						sijsStatusFormDtlsDto.getSijsDtlsDto().getNbrPersonAge());
				BookmarkDto bookmarkSIJSFce = createBookmark(BookmarkConstants.SIJS_FCE,
						lookupDao.decode(ServiceConstants.CCTZNSTA,
								sijsStatusFormDtlsDto.getPersonDtlDto().getCdPersonCitizenship()));

				BookmarkDto bookmarkSIJSLegalStatus = null;
				BookmarkDto bookmarkSIJSPersonCountry = null;
				BookmarkDto bookmarkSIJSCvs = null;
				BookmarkDto bookmarkSIJSCounty = null;

				if (!TypeConvUtil.isNullOrEmpty(sijsStatusFormDtlsDto.getSijsLegalDtlsDto().getCdLegalStatStatus())) {
					bookmarkSIJSLegalStatus = createBookmark(BookmarkConstants.SIJS_LEGAL_STATUS,
							lookupDao.decode(ServiceConstants.CLEGSTAT,
									sijsStatusFormDtlsDto.getSijsLegalDtlsDto().getCdLegalStatStatus()));
				} else {
					bookmarkSIJSLegalStatus = createBookmark(BookmarkConstants.SIJS_LEGAL_STATUS,
							FormConstants.EMPTY_STRING);

				}

				if (!TypeConvUtil.isNullOrEmpty(sijsStatusFormDtlsDto.getPersonDtlDto().getCdPersonBirthCountry())) {
					bookmarkSIJSPersonCountry = createBookmark(BookmarkConstants.SIJS_PERSON_COUNTRY,
							lookupDao.decode(ServiceConstants.CCOUNTRY,
									sijsStatusFormDtlsDto.getPersonDtlDto().getCdPersonBirthCountry()));
				} else {
					bookmarkSIJSPersonCountry = createBookmark(BookmarkConstants.SIJS_PERSON_COUNTRY,
							FormConstants.EMPTY_STRING);

				}

				if (!TypeConvUtil.isNullOrEmpty(sijsStatusFormDtlsDto.getPersonDtlDto().getCdPersonCitizenship())) {
					bookmarkSIJSCvs = createBookmark(BookmarkConstants.SIJS_CVS,
							lookupDao.decode(ServiceConstants.CCTZNSTA,
									sijsStatusFormDtlsDto.getPersonDtlDto().getCdPersonCitizenship()));
				} else {
					bookmarkSIJSCvs = createBookmark(BookmarkConstants.SIJS_CVS, FormConstants.EMPTY_STRING);

				}

				if (!TypeConvUtil.isNullOrEmpty(sijsStatusFormDtlsDto.getSijsDtlsDto().getCdStageCnty())) {
					bookmarkSIJSCounty = createBookmark(BookmarkConstants.SIJS_COUNTY, lookupDao
							.decode(ServiceConstants.CCOUNT2, sijsStatusFormDtlsDto.getSijsDtlsDto().getCdStageCnty()));
				} else {
					bookmarkSIJSCounty = createBookmark(BookmarkConstants.SIJS_COUNTY, FormConstants.EMPTY_STRING);

				}

				BookmarkDto bookmarkSIJSPhone = createBookmark(BookmarkConstants.SIJS_PHONE,
						TypeConvUtil.formatPhone(sijsStatusFormDtlsDto.getSijsDtlsDto().getNbrPersonPhone()));
				BookmarkDto bookmarkSIJSUnit = createBookmark(BookmarkConstants.SIJS_UNIT,
						sijsStatusFormDtlsDto.getSijsDtlsDto().getNbrUnit());
				BookmarkDto bookmarkSIJSNameFirst = createBookmark(BookmarkConstants.SIJS_NAME_FIRST,
						sijsStatusFormDtlsDto.getSijsDtlsDto().getNmPersonFirst());
				BookmarkDto bookmarkSIJSPrimaryName = createBookmark(BookmarkConstants.SIJS_PRIMARY_NAME,
						sijsStatusFormDtlsDto.getSijsDtlsDto().getNmPersonFull());
				BookmarkDto bookmarkSIJSNameLast = createBookmark(BookmarkConstants.SIJS_NAME_LAST,
						sijsStatusFormDtlsDto.getSijsDtlsDto().getNmPersonLast());
				BookmarkDto bookmarkSIJSNameMiddle = createBookmark(BookmarkConstants.SIJS_NAME_MIDDLE,
						sijsStatusFormDtlsDto.getSijsDtlsDto().getNmPersonMiddle());
				BookmarkDto bookmarkSIJSCaseNo = createBookmark(BookmarkConstants.SIJS_CASE_NUMBER,
						sijsStatusFormDtlsDto.getSijsDtlsDto().getIdCase());
				BookmarkDto bookmarkSIJSPid = createBookmark(BookmarkConstants.SIJS_PID,
						sijsStatusFormDtlsDto.getSijsDtlsDto().getIdPerson());
				BookmarkDto bookmarkSIJSUEGroupId = createBookmark(BookmarkConstants.UE_GROUPID,
						sijsStatusFormDtlsDto.getSijsDtlsDto().getIdStage());
				bookmarkSIJS.add(bookmarkSIJSEval);
				bookmarkSIJS.add(bookmarkSIJSDob);
				bookmarkSIJS.add(bookmarkSIJSEighteenth);
				bookmarkSIJS.add(bookmarkSIJSAge);
				bookmarkSIJS.add(bookmarkSIJSFce);
				bookmarkSIJS.add(bookmarkSIJSLegalStatus);
				bookmarkSIJS.add(bookmarkSIJSPersonCountry);
				bookmarkSIJS.add(bookmarkSIJSCvs);
				bookmarkSIJS.add(bookmarkSIJSCounty);
				bookmarkSIJS.add(bookmarkSIJSPhone);
				bookmarkSIJS.add(bookmarkSIJSUnit);
				bookmarkSIJS.add(bookmarkSIJSNameFirst);
				bookmarkSIJS.add(bookmarkSIJSPrimaryName);
				bookmarkSIJS.add(bookmarkSIJSNameLast);
				bookmarkSIJS.add(bookmarkSIJSNameMiddle);
				bookmarkSIJS.add(bookmarkSIJSCaseNo);
				bookmarkSIJS.add(bookmarkSIJSPid);
				bookmarkSIJS.add(bookmarkSIJSUEGroupId);
				tempSIJSFrmDataGrpDto.setBookmarkDtoList(bookmarkSIJS);

				/**
				 * prefill data for group wkld0221 - TMPLAT_SIJS_NAME_SUFFIX
				 */
				if (null != sijsStatusFormDtlsDto.getSijsDtlsDto().getCdPersonSuffix()) {
					FormDataGroupDto tempSIJSNmSuffixFrmDataGrpDto = createFormDataGroup(
							FormGroupsConstants.TMPLAT_SIJS_NAME_SUFFIX, FormGroupsConstants.TMPLAT_SIJS);
					List<BookmarkDto> bookmarkSIJSNmSuffix = new ArrayList<BookmarkDto>();
					BookmarkDto bookmarkSIJSNameSuffix = createBookmark(BookmarkConstants.SIJS_NAME_SUFFIX,
							sijsStatusFormDtlsDto.getSijsDtlsDto().getCdPersonSuffix());
					bookmarkSIJSNmSuffix.add(bookmarkSIJSNameSuffix);
					tempSIJSNmSuffixFrmDataGrpDto.setBookmarkDtoList(bookmarkSIJSNmSuffix);
					tempSIJSformDataGroupList.add(tempSIJSNmSuffixFrmDataGrpDto);
				}

				/**
				 * prefill data for group wkld0230 - TMPLAT_SIJS_SECONDARY
				 */

				List<SijsSecondaryDtlsDto> SijsSecondaryDtlslist = sijsStatusFormDtlsDto.getSijsSecondaryDtlsDto();

				FormDataGroupDto tempSIJSSecondaryFrmDataGrpDto = createFormDataGroup(
						FormGroupsConstants.TMPLAT_SIJS_SECONDARY, FormGroupsConstants.TMPLAT_SIJS);
				for (SijsSecondaryDtlsDto sijsSecondaryDtlsDto : SijsSecondaryDtlslist) {
					List<BookmarkDto> bookmarkSIJSSecondary = new ArrayList<BookmarkDto>();
					BookmarkDto bookmarkSIJSSecondaryAssgn = createBookmark(BookmarkConstants.SIJS_SECONDARY_ASSIGNED,
							TypeConvUtil.formDateFormat(sijsSecondaryDtlsDto.getDtAssgnd()));
					BookmarkDto bookmarkSIJSSecondaryUnAssgn = createBookmark(
							BookmarkConstants.SIJS_SECONDARY_UNASSIGNED,
							TypeConvUtil.formDateFormat(sijsSecondaryDtlsDto.getDtUnAssgnd()));
					BookmarkDto bookmarkSIJSSecondaryDays = createBookmark(BookmarkConstants.SIJS_SECONDARY_DAYS,
							sijsSecondaryDtlsDto.getNbrDaysAssigned());
					BookmarkDto bookmarkSIJSSecondaryJobClass = createBookmark(
							BookmarkConstants.SIJS_SECONDARY_JOBCLASS, sijsSecondaryDtlsDto.getCdJobClassDecode());
					BookmarkDto bookmarkSIJSSecondaryName = createBookmark(BookmarkConstants.SIJS_SECONDARY_NAME,
							sijsSecondaryDtlsDto.getNmPersonFull());
					bookmarkSIJSSecondary.add(bookmarkSIJSSecondaryAssgn);
					bookmarkSIJSSecondary.add(bookmarkSIJSSecondaryUnAssgn);
					bookmarkSIJSSecondary.add(bookmarkSIJSSecondaryDays);
					bookmarkSIJSSecondary.add(bookmarkSIJSSecondaryJobClass);
					bookmarkSIJSSecondary.add(bookmarkSIJSSecondaryName);
					tempSIJSSecondaryFrmDataGrpDto.setBookmarkDtoList(bookmarkSIJSSecondary);
					tempSIJSformDataGroupList.add(tempSIJSSecondaryFrmDataGrpDto);
				}

				tempSIJSFrmDataGrpDto.setFormDataGroupList(tempSIJSformDataGroupList);

				/**
				 * prefill data for group wkld0240 - TMPLAT_SIJS_NOTES
				 */

				List<SijsEventIdDtlsDto> SijsEventIdDtlslist = sijsStatusFormDtlsDto.getSijsEventIdDtlsDto();
				
			

				FormDataGroupDto tempSIJSNotesFrmDataGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_SIJS_NOTES,
						FormGroupsConstants.TMPLAT_SIJS);
				List<BlobDataDto> bookmarkBlobDataList = new ArrayList<BlobDataDto>();
				
				if (SijsEventIdDtlslist.size() > ServiceConstants.Zero_INT) {
					for (SijsEventIdDtlsDto sijsEventIdDtlsDto : SijsEventIdDtlslist) {

						if (sijsEventIdDtlsDto.getIdContactWorker().longValue() == 41864505
								|| sijsEventIdDtlsDto.getIdContactWorker().longValue() == 24609814
								|| sijsEventIdDtlsDto.getIdContactWorker().longValue() == 35685776
								|| sijsEventIdDtlsDto.getIdContactWorker().longValue() == 6485) {

							List<BookmarkDto> bookmarkSIJSNotes = new ArrayList<BookmarkDto>();
							BookmarkDto bookmarkSIJSNotesDate = createBookmark(BookmarkConstants.SIJS_NOTES_DATE,
									TypeConvUtil.formDateFormat(sijsEventIdDtlsDto.getDtContactOccured()));
							BookmarkDto bookmarkSIJSNotesTime = createBookmark(BookmarkConstants.SIJS_NOTES_TIME,
									sijsEventIdDtlsDto.getDtContactOccured());
							BookmarkDto bookmarkSIJSNotesWorkerId = createBookmark(
									BookmarkConstants.SIJS_NOTES_WORKERID, sijsEventIdDtlsDto.getIdContactWorker());
							
							BlobDataDto bookmarkBlobIdEvent = createBlobData(BookmarkConstants.SIJS_NOTES_NARR,
									CodesConstant.CONTACT_NARRATIVE, sijsEventIdDtlsDto.getIdEvent().toString());
							
							bookmarkBlobDataList.add(bookmarkBlobIdEvent);
							
							bookmarkSIJSNotes.add(bookmarkSIJSNotesDate);
							bookmarkSIJSNotes.add(bookmarkSIJSNotesTime);
							bookmarkSIJSNotes.add(bookmarkSIJSNotesWorkerId);

							tempSIJSNotesFrmDataGrpDto.setBookmarkDtoList(bookmarkSIJSNotes);
							tempSIJSNotesFrmDataGrpDto.setBlobDataDtoList(bookmarkBlobDataList);

						}
					}
				}

				/**
				 * prefill data for group wkld0240 - TMPLAT_SIJS_NOTES
				 */

				List<SijsEventContactDtlsDto> SijsEventContactDtlslist = sijsStatusFormDtlsDto
						.getSijsEventContactDtlsDto();

				
				if (SijsEventContactDtlslist.size() > ServiceConstants.Zero_INT) {
					for (SijsEventContactDtlsDto sijsEventContactDtlsDto : SijsEventContactDtlslist) {

						FormDataGroupDto tempSIJSNotesNamesFrmDataGrpDto = createFormDataGroup(
								FormGroupsConstants.TMPLAT_SIJS_NOTES_NAMES, FormGroupsConstants.TMPLAT_SIJS_NOTES);
						List<BookmarkDto> bookmarkSIJSNotesNames = new ArrayList<BookmarkDto>();
						BookmarkDto bookmarkSIJSNoteName = createBookmark(BookmarkConstants.SIJS_NOTES_NAMES,
								sijsEventContactDtlsDto.getFullName());
						bookmarkSIJSNotesNames.add(bookmarkSIJSNoteName);
						tempSIJSNotesNamesFrmDataGrpDto.setBookmarkDtoList(bookmarkSIJSNotesNames);
						tempSIJSNotesNameformDataGroupList.add(tempSIJSNotesNamesFrmDataGrpDto);
					}	
					
				}
				
				tempSIJSNotesFrmDataGrpDto.setFormDataGroupList(tempSIJSNotesNameformDataGroupList);
				tempSIJSNotesformDataGroupList.add(tempSIJSNotesFrmDataGrpDto);				
				
				
				tempSIJSFrmDataGrpDto.setFormDataGroupList(tempSIJSNotesformDataGroupList);
				formDataGroupList.add(tempSIJSFrmDataGrpDto);
			}
		}

		/**
		 * Populating the non form group data into prefill data
		 */

		List<BookmarkDto> bookmarkNonFrmGrpList = new ArrayList<BookmarkDto>();
		BookmarkDto bookmarkWorkerNameFirst = createBookmark(BookmarkConstants.WORKER_NAME_FIRST,
				sijsStatusFormDto.getEmployeePersPhNameDto().getNmNameFirst());
		BookmarkDto bookmarkWorkerNameLast = createBookmark(BookmarkConstants.WORKER_NAME_LAST,
				sijsStatusFormDto.getEmployeePersPhNameDto().getNmNameLast());
		BookmarkDto bookmarkWorkerNameMiddle = createBookmark(BookmarkConstants.WORKER_NAME_MIDDLE,
				sijsStatusFormDto.getEmployeePersPhNameDto().getNmNameMiddle());
		/*
		 * Code as per C BookmarkDto bookmarkWorkerSearchQuantity =
		 * createBookmark(BookmarkConstants.WORKER_SEARCH_QUANTITY,
		 * sijsStatusFormDto.getEmployeePersPhNameDto().getIdJobPersSupv());
		 */
		BookmarkDto bookmarkWorkerSearchQuantity = createBookmark(BookmarkConstants.WORKER_SEARCH_QUANTITY,
				SijsStatusFormDtlslist.size());
		BookmarkDto bookmarkWorkerNumber = createBookmark(BookmarkConstants.WORKER_NUMBER,
				sijsStatusFormDto.getEmployeePersPhNameDto().getIdPerson());
		bookmarkNonFrmGrpList.add(bookmarkWorkerNameFirst);
		bookmarkNonFrmGrpList.add(bookmarkWorkerNameLast);
		bookmarkNonFrmGrpList.add(bookmarkWorkerNameMiddle);
		bookmarkNonFrmGrpList.add(bookmarkWorkerSearchQuantity);
		bookmarkNonFrmGrpList.add(bookmarkWorkerNumber);

		PreFillDataServiceDto preFillData = new PreFillDataServiceDto();
		preFillData.setFormDataGroupList(formDataGroupList);
		preFillData.setBookmarkDtoList(bookmarkNonFrmGrpList);
		return preFillData;

	}
}