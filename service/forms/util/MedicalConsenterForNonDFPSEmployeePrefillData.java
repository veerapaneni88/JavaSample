package us.tx.state.dfps.service.forms.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.service.admin.dto.LegalStatusOutDto;
import us.tx.state.dfps.service.common.BookmarkConstants;
import us.tx.state.dfps.service.common.FormConstants;
import us.tx.state.dfps.service.common.FormGroupsConstants;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.common.utils.PersonUtil;
import us.tx.state.dfps.service.forms.dto.BookmarkDto;
import us.tx.state.dfps.service.forms.dto.FormDataGroupDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.person.dao.PersonDao;
import us.tx.state.dfps.service.person.dto.MedicalConsenterDto;
import us.tx.state.dfps.service.person.dto.MedicalDto;
import us.tx.state.dfps.service.workload.dto.PersonDto;

@Component
/**
 * Name:MedicalConsenterForNonDFPSEmployeePrefillData Description:
 * MedicalConsenterForNonDFPSEmployeePrefillData will implemented
 * returnPrefillData operation defined in DocumentServiceUtil Interface to
 * populate the prefill data for form med01o00.(Medical Consenter for Non-DFPS
 * Employee - 2085B) Jan 04, 2018 - 04:40:29 PM
 */
public class MedicalConsenterForNonDFPSEmployeePrefillData extends DocumentServiceUtil {

	@Autowired
	PersonUtil personUtil;

	@Autowired
	PersonDao personDao;

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
		MedicalDto medicalDto = (MedicalDto) parentDtoobj;

		if (null == medicalDto.getLegalStatusOutDto()) {
			medicalDto.setLegalStatusOutDto(new LegalStatusOutDto());
		}
		if (null == medicalDto.getPersonDto()) {
			medicalDto.setPersonDto(new PersonDto());
		}
		if (medicalDto.getMedicalConsenterDtoList().isEmpty()) {
			medicalDto.setMedicalConsenterDtoList(new ArrayList<MedicalConsenterDto>());
		}
		if (medicalDto.getMedicalConsnterDto().isEmpty()) {
			medicalDto.setMedicalConsnterDto(new ArrayList<MedicalConsenterDto>());
		}

		List<FormDataGroupDto> formDataGroupList = new ArrayList<FormDataGroupDto>();

		List<BookmarkDto> bookmarkNonFrmGrpList = new ArrayList<BookmarkDto>();

		// Populating group data into prefill data
		// med0103
		if (!ObjectUtils.isEmpty(medicalDto.getPersonDto().getCdPersonSuffix())) {
			FormDataGroupDto formdatagroupChildSuffix = createFormDataGroup(FormGroupsConstants.TMPLAT_CHILD_SUFFIX,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkAddResourcemed0103List = new ArrayList<BookmarkDto>();
			BookmarkDto bookmarkChildSuffix = createBookmark(BookmarkConstants.CHILD_SUFFIX,
					medicalDto.getPersonDto().getCdPersonSuffix());
			bookmarkAddResourcemed0103List.add(bookmarkChildSuffix);
			formdatagroupChildSuffix.setBookmarkDtoList(bookmarkAddResourcemed0103List);
			formDataGroupList.add(formdatagroupChildSuffix);
		}

		// Prefill data med0101 is the parent of med0104
		// med0101
		for (MedicalConsenterDto medCondnterType : medicalDto.getMedicalConsenterDtoList()) {
			if (FormConstants.PRIMARY.equalsIgnoreCase(medCondnterType.getCdMedConsenterType())) {
				List<FormDataGroupDto> formDataGroupFpriList = new ArrayList<FormDataGroupDto>();
				FormDataGroupDto formDataGroupFpri = createFormDataGroup(FormGroupsConstants.TMPLAT_FPRI,
						FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkListFPRI = new ArrayList<BookmarkDto>();
				// bookmarks for med0101
				BookmarkDto bookmarkFrstNm = createBookmark(BookmarkConstants.FPRI_FIRST,
						medCondnterType.getNmNameFirst());
				BookmarkDto bookmarkLastNm = createBookmark(BookmarkConstants.FPRI_LAST,
						medCondnterType.getNmNameLast());
				BookmarkDto bookmarkMiddleNm = createBookmark(BookmarkConstants.FPRI_MIDDLE,
						medCondnterType.getNmNameMiddle());
				BookmarkDto bookmarkConsntrPid = createBookmark(BookmarkConstants.FPRI_PID,
						medCondnterType.getIdMedConsenterPerson());
				bookmarkListFPRI.add(bookmarkFrstNm);
				bookmarkListFPRI.add(bookmarkLastNm);
				bookmarkListFPRI.add(bookmarkMiddleNm);
				bookmarkListFPRI.add(bookmarkConsntrPid);
				formDataGroupFpri.setBookmarkDtoList(bookmarkListFPRI);

				// Non Group Bookmark for Primary Medical Consenter Name
				if (!ObjectUtils.isEmpty(medCondnterType.getIdMedConsenterPerson())) {
					BookmarkDto bookmarkPrmyMedCnNm = createBookmark(BookmarkConstants.NM_PRMY_MED_CONSENT,
							personUtil.getPersonFullName(medCondnterType.getIdMedConsenterPerson()));
					bookmarkNonFrmGrpList.add(bookmarkPrmyMedCnNm);
				} else {
					BookmarkDto bookmarkPrmyMedCnNm = createBookmark(BookmarkConstants.NM_PRMY_MED_CONSENT,
							ServiceConstants.EMPTY_LINE);
					bookmarkNonFrmGrpList.add(bookmarkPrmyMedCnNm);
				}

				// Populate the Primary Medical Consenter Phone Number
				FormDataGroupDto tempPrmyMedCnPhn = createFormDataGroup(FormGroupsConstants.TMPLAT_PRMY_PHONE,
						FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkPrmyMedCnPhnGrpList = new ArrayList<BookmarkDto>();
				BookmarkDto bookmarkPrmyMedCnPhn = createBookmark(BookmarkConstants.TXT_PRMY_PHONE,
						TypeConvUtil.formatPhone(
								personDao.getPerson(medCondnterType.getIdMedConsenterPerson()).getNbrPersonPhone()));
				bookmarkNonFrmGrpList.add(bookmarkPrmyMedCnPhn);
				bookmarkPrmyMedCnPhnGrpList.add(bookmarkPrmyMedCnPhn);
				tempPrmyMedCnPhn.setBookmarkDtoList(bookmarkPrmyMedCnPhnGrpList);
				formDataGroupList.add(tempPrmyMedCnPhn);

				/*
				 * Checking if the Suffix name is not null prefill data for the
				 * subgroup med0104
				 */
				if (FormConstants.EMPTY_STRING != medCondnterType.getCdNameSuffix()) {
					FormDataGroupDto formDataGroupFpriSufx = createFormDataGroup(FormGroupsConstants.TMPLAT_FPRI_SUFFIX,
							FormGroupsConstants.TMPLAT_FPRI);
					List<BookmarkDto> bookmarkFpriSufxList = new ArrayList<BookmarkDto>();
					BookmarkDto bookmarkFpriSufx = createBookmark(BookmarkConstants.FPRI_SUFFIX,
							medCondnterType.getCdNameSuffix());
					bookmarkFpriSufxList.add(bookmarkFpriSufx);
					formDataGroupFpriSufx.setBookmarkDtoList(bookmarkFpriSufxList);
					formDataGroupFpriList.add(formDataGroupFpriSufx);

				}
				formDataGroupFpri.setFormDataGroupList(formDataGroupFpriList);
				formDataGroupList.add(formDataGroupFpri);

			}
		}

		// med0106 will have two child sub groups i.e med0107 and med0108
		// MED0106
		for (MedicalConsenterDto medCondnterType : medicalDto.getMedicalConsenterDtoList()) {
			if (FormConstants.SECOND_PRIMARY.equalsIgnoreCase(medCondnterType.getCdMedConsenterType())) {
				FormDataGroupDto formDataGroupSpri = createFormDataGroup(FormGroupsConstants.TMPLAT_SPRI,
						FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkDtoSpriList = new ArrayList<BookmarkDto>();
				BookmarkDto bookmarkFrstNm = createBookmark(BookmarkConstants.SPRI_FIRST,
						medCondnterType.getNmNameFirst());
				BookmarkDto bookmarkLastNm = createBookmark(BookmarkConstants.SPRI_LAST,
						medCondnterType.getNmNameLast());
				BookmarkDto bookmarkMiddleNm = createBookmark(BookmarkConstants.SPRI_MIDDLE,
						medCondnterType.getNmNameMiddle());
				BookmarkDto bookmarkConsntrPid = createBookmark(BookmarkConstants.SPRI_PID,
						medCondnterType.getIdMedConsenterPerson());
				bookmarkDtoSpriList.add(bookmarkFrstNm);
				bookmarkDtoSpriList.add(bookmarkLastNm);
				bookmarkDtoSpriList.add(bookmarkMiddleNm);
				bookmarkDtoSpriList.add(bookmarkConsntrPid);
				formDataGroupSpri.setBookmarkDtoList(bookmarkDtoSpriList);

				// Non Group Bookmark for Secondary Primary Medical Consenter
				// Name
				if (!ObjectUtils.isEmpty(medCondnterType.getIdMedConsenterPerson())) {
					BookmarkDto bookmarkSecMedCnNm = createBookmark(BookmarkConstants.NM_SEC_MED_CONSENT,
							personUtil.getPersonFullName(medCondnterType.getIdMedConsenterPerson()));
					bookmarkNonFrmGrpList.add(bookmarkSecMedCnNm);
				} else {
					BookmarkDto bookmarkSecMedCnNm = createBookmark(BookmarkConstants.NM_SEC_MED_CONSENT,
							ServiceConstants.EMPTY_LINE);
					bookmarkNonFrmGrpList.add(bookmarkSecMedCnNm);
				}

				// Populate the Secondary Primary Medical Consenter Phone Number
				FormDataGroupDto tempSecMedCnPhn = createFormDataGroup(FormGroupsConstants.TMPLAT_SEC_PHONE,
						FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkSecMedCnGrpList = new ArrayList<BookmarkDto>();
				BookmarkDto bookmarkSecMedCnPhn = createBookmark(BookmarkConstants.TXT_SEC_PHONE,
						TypeConvUtil.formatPhone(
								personDao.getPerson(medCondnterType.getIdMedConsenterPerson()).getNbrPersonPhone()));
				bookmarkNonFrmGrpList.add(bookmarkSecMedCnPhn);
				bookmarkSecMedCnGrpList.add(bookmarkSecMedCnPhn);
				tempSecMedCnPhn.setBookmarkDtoList(bookmarkSecMedCnGrpList);
				formDataGroupList.add(tempSecMedCnPhn);

				// MED0107
				List<FormDataGroupDto> formDataGroupSpriAndList = new ArrayList<FormDataGroupDto>();
				FormDataGroupDto formDataGroupSpriAnd = createFormDataGroup(FormGroupsConstants.TMPLAT_SPRI_AND,
						FormGroupsConstants.TMPLAT_SPRI);
				formDataGroupSpriAndList.add(formDataGroupSpriAnd);

				// MED0108
				if (FormConstants.EMPTY_STRING != medCondnterType.getCdNameSuffix()) {
					// List<FormDataGroupDto> formDataGroupSpriSufxList = new
					// ArrayList<FormDataGroupDto>();
					FormDataGroupDto formDataGroupSpriSufx = createFormDataGroup(FormGroupsConstants.TMPLAT_SPRI_SUFFIX,
							FormGroupsConstants.TMPLAT_SPRI);
					List<BookmarkDto> bookmarkSpriSufxList = new ArrayList<BookmarkDto>();
					BookmarkDto bookmarkSpriSufx = createBookmark(BookmarkConstants.FPRI_SUFFIX,
							medCondnterType.getCdNameSuffix());
					bookmarkSpriSufxList.add(bookmarkSpriSufx);
					formDataGroupSpriSufx.setBookmarkDtoList(bookmarkSpriSufxList);
					formDataGroupSpriAndList.add(formDataGroupSpriSufx);
					formDataGroupSpri.setFormDataGroupList(formDataGroupSpriAndList);

				}
				formDataGroupList.add(formDataGroupSpri);
			}

		}

		// med0112
		for (MedicalConsenterDto medCondnterType : medicalDto.getMedicalConsenterDtoList()) {
			if (FormConstants.PRIMARY.equalsIgnoreCase(medCondnterType.getCdMedConsenterType())) {
				FormDataGroupDto formdatagroupFprID = createFormDataGroup(FormGroupsConstants.TMPLAT_FPRID,
						FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkAddResourcemed0112List = new ArrayList<BookmarkDto>();
				BookmarkDto bookmarkidFPR = createBookmark(BookmarkConstants.FPRID_PID,
						medCondnterType.getIdMedConsenterPerson());
				bookmarkAddResourcemed0112List.add(bookmarkidFPR);
				formdatagroupFprID.setBookmarkDtoList(bookmarkAddResourcemed0112List);
				formDataGroupList.add(formdatagroupFprID);
			}
		}

		// med0120 is the parent of med0121
		for (MedicalConsenterDto medCondnterType : medicalDto.getMedicalConsenterDtoList()) {
			if (FormConstants.BACKUP.equalsIgnoreCase(medCondnterType.getCdMedConsenterType())) {

				List<FormDataGroupDto> formDataGroupFbupList = new ArrayList<FormDataGroupDto>();
				FormDataGroupDto formDataGroupFbup = createFormDataGroup(FormGroupsConstants.TMPLAT_FBUP,
						FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkListFBUP = new ArrayList<BookmarkDto>();
				// bookmarks for med0120
				BookmarkDto bookmarkFrstNm = createBookmark(BookmarkConstants.FBUP_FIRST,
						medCondnterType.getNmNameFirst());
				BookmarkDto bookmarkLastNm = createBookmark(BookmarkConstants.FBUP_LAST,
						medCondnterType.getNmNameLast());
				BookmarkDto bookmarkMiddleNm = createBookmark(BookmarkConstants.FBUP_MIDDLE,
						medCondnterType.getNmNameMiddle());
				BookmarkDto bookmarkConsntrPid = createBookmark(BookmarkConstants.FBUP_PID,
						medCondnterType.getIdMedConsenterPerson());
				bookmarkListFBUP.add(bookmarkFrstNm);
				bookmarkListFBUP.add(bookmarkLastNm);
				bookmarkListFBUP.add(bookmarkMiddleNm);
				bookmarkListFBUP.add(bookmarkConsntrPid);
				formDataGroupFbup.setBookmarkDtoList(bookmarkListFBUP);

				// Non Group Bookmark for Backup Medical Consenter Name
				if (!ObjectUtils.isEmpty(medCondnterType.getIdMedConsenterPerson())) {
					BookmarkDto bookmarkBkpMedCnNm = createBookmark(BookmarkConstants.NM_BKP_MED_CONSENT,
							personUtil.getPersonFullName(medCondnterType.getIdMedConsenterPerson()));
					bookmarkNonFrmGrpList.add(bookmarkBkpMedCnNm);
				} else {
					BookmarkDto bookmarkBkpMedCnNm = createBookmark(BookmarkConstants.NM_BKP_MED_CONSENT,
							ServiceConstants.EMPTY_LINE);
					bookmarkNonFrmGrpList.add(bookmarkBkpMedCnNm);
				}

				// Populate the Backup Medical Consenter Phone Number
				FormDataGroupDto tempBkpMedCnPhn = createFormDataGroup(FormGroupsConstants.TMPLAT_BKP_PRMY_PHONE,
						FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkBkpMedCnGrpList = new ArrayList<BookmarkDto>();
				BookmarkDto bookmarkBkpMedCnPhn = createBookmark(BookmarkConstants.TXT_BKP_PRMY_PHONE,
						TypeConvUtil.formatPhone(
								personDao.getPerson(medCondnterType.getIdMedConsenterPerson()).getNbrPersonPhone()));
				bookmarkNonFrmGrpList.add(bookmarkBkpMedCnPhn);
				bookmarkBkpMedCnGrpList.add(bookmarkBkpMedCnPhn);
				tempBkpMedCnPhn.setBookmarkDtoList(bookmarkBkpMedCnGrpList);
				formDataGroupList.add(tempBkpMedCnPhn);

				/*
				 * Checking if the Suffix name is not null prefill data for the
				 * subgroup med0121
				 */
				if (FormConstants.EMPTY_STRING != medCondnterType.getCdNameSuffix()) {
					FormDataGroupDto formDataGroupFbupSufx = createFormDataGroup(FormGroupsConstants.TMPLAT_FBUP_SUFFIX,
							FormGroupsConstants.TMPLAT_FBUP);
					List<BookmarkDto> bookmarkFbupSufxList = new ArrayList<BookmarkDto>();
					BookmarkDto bookmarkFbupSufx = createBookmark(BookmarkConstants.FBUP_SUFFIX,
							medCondnterType.getCdNameSuffix());
					bookmarkFbupSufxList.add(bookmarkFbupSufx);
					formDataGroupFbupSufx.setBookmarkDtoList(bookmarkFbupSufxList);
					formDataGroupFbupList.add(formDataGroupFbupSufx);

				}
				formDataGroupFbup.setFormDataGroupList(formDataGroupFbupList);
				formDataGroupList.add(formDataGroupFbup);
			}
		}

		// med0130 is the parent med0131 and med0132

		for (MedicalConsenterDto medCondnterType : medicalDto.getMedicalConsenterDtoList()) {
			if (FormConstants.SECOND_BACKUP.equalsIgnoreCase(medCondnterType.getCdMedConsenterType())) {
				FormDataGroupDto formDataGroupSbup = createFormDataGroup(FormGroupsConstants.TMPLAT_SBUP,
						FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkListSBUPList = new ArrayList<BookmarkDto>();
				BookmarkDto bookmarkFrstNm = createBookmark(BookmarkConstants.SBUP_FIRST,
						medCondnterType.getNmNameFirst());
				BookmarkDto bookmarkLastNm = createBookmark(BookmarkConstants.SBUP_LAST,
						medCondnterType.getNmNameLast());
				BookmarkDto bookmarkMiddleNm = createBookmark(BookmarkConstants.SBUP_MIDDLE,
						medCondnterType.getNmNameMiddle());
				BookmarkDto bookmarkConsntrPid = createBookmark(BookmarkConstants.SBUP_PID,
						medCondnterType.getIdMedConsenterPerson());
				bookmarkListSBUPList.add(bookmarkFrstNm);
				bookmarkListSBUPList.add(bookmarkLastNm);
				bookmarkListSBUPList.add(bookmarkMiddleNm);
				bookmarkListSBUPList.add(bookmarkConsntrPid);
				formDataGroupSbup.setBookmarkDtoList(bookmarkListSBUPList);

				// Non Group Bookmark for Second Backup Medical Consenter Name
				if (!ObjectUtils.isEmpty(medCondnterType.getIdMedConsenterPerson())) {
					BookmarkDto bookmarkSecBkpMedCnNm = createBookmark(BookmarkConstants.NM_SEC_BKP_MED_CONSENT,
							personUtil.getPersonFullName(medCondnterType.getIdMedConsenterPerson()));
					bookmarkNonFrmGrpList.add(bookmarkSecBkpMedCnNm);
				} else {
					BookmarkDto bookmarkSecBkpMedCnNm = createBookmark(BookmarkConstants.NM_SEC_BKP_MED_CONSENT,
							ServiceConstants.EMPTY_LINE);
					bookmarkNonFrmGrpList.add(bookmarkSecBkpMedCnNm);
				}

				// Populate the Second Backup Medical Consenter Phone Number
				FormDataGroupDto tempSecBkpMedCnPhn = createFormDataGroup(FormGroupsConstants.TMPLAT_BKP_SEC_PHONE,
						FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkSecBkpMedCnGrpList = new ArrayList<BookmarkDto>();
				BookmarkDto bookmarkSecBkpMedCnPhn = createBookmark(BookmarkConstants.TXT_BKP_SEC_PHONE,
						TypeConvUtil.formatPhone(
								personDao.getPerson(medCondnterType.getIdMedConsenterPerson()).getNbrPersonPhone()));
				bookmarkNonFrmGrpList.add(bookmarkSecBkpMedCnPhn);
				bookmarkSecBkpMedCnGrpList.add(bookmarkSecBkpMedCnPhn);
				tempSecBkpMedCnPhn.setBookmarkDtoList(bookmarkSecBkpMedCnGrpList);
				formDataGroupList.add(tempSecBkpMedCnPhn);

				// MED0131
				if (FormConstants.EMPTY_STRING != medCondnterType.getCdNameSuffix()) {
					List<FormDataGroupDto> formDataGroupSbupSufxList = new ArrayList<>();
					FormDataGroupDto formDataGroupSbupSufx = createFormDataGroup(FormGroupsConstants.TMPLAT_SBUP_SUFFIX,
							FormGroupsConstants.TMPLAT_SBUP);
					List<BookmarkDto> bookmarkListSBUPSufxList = new ArrayList<BookmarkDto>();
					BookmarkDto bookmarkpersonSuffix = createBookmark(BookmarkConstants.SBUP_SUFFIX,
							medCondnterType.getCdNameSuffix());
					bookmarkListSBUPSufxList.add(bookmarkpersonSuffix);
					formDataGroupSbupSufx.setBookmarkDtoList(bookmarkListSBUPSufxList);
					formDataGroupSbupSufxList.add(formDataGroupSbupSufx);
					formDataGroupSbup.setFormDataGroupList(formDataGroupSbupSufxList);
				}
				// MED0132
				FormDataGroupDto formDataGroupSbupSufxAnd = createFormDataGroup(FormGroupsConstants.TMPLAT_SBUP_AND,
						FormGroupsConstants.TMPLAT_SBUP);
				List<FormDataGroupDto> formDataGroupSbupSufxAndList = new ArrayList<FormDataGroupDto>();
				formDataGroupSbupSufxAndList.add(formDataGroupSbupSufxAnd);
				formDataGroupSbup.setFormDataGroupList(formDataGroupSbupSufxAndList);

				formDataGroupList.add(formDataGroupSbup);
			}
		}

		/**
		 * Populating the non form group data into prefill data
		 */

		// Populate the DOB from CSECD3D
		BookmarkDto bookmarkDOB = createBookmark(BookmarkConstants.DOB,
				TypeConvUtil.formDateFormat(medicalDto.getPersonDto().getDtPersonBirth()));
		bookmarkNonFrmGrpList.add(bookmarkDOB);

		// Populate the Med Number from CSECD3D
		BookmarkDto bookmarkMedNo = createBookmark(BookmarkConstants.MEDNO,
				medicalDto.getPersonDto().getNbrPersonIdNumber());
		bookmarkNonFrmGrpList.add(bookmarkMedNo);

		// Populate the First Name from CSECD3D
		BookmarkDto bookmarkFirstName = createBookmark(BookmarkConstants.CHILD_FIRST,
				medicalDto.getPersonDto().getNmPersonFirst());
		bookmarkNonFrmGrpList.add(bookmarkFirstName);

		// Populate the Last Name from CSEC3D
		BookmarkDto bookmarkLastName = createBookmark(BookmarkConstants.CHILD_LAST,
				medicalDto.getPersonDto().getNmPersonLast());
		bookmarkNonFrmGrpList.add(bookmarkLastName);

		// Populate the Middle Name from CSEC3D
		BookmarkDto bookmarkMiddleName = createBookmark(BookmarkConstants.CHILD_MIDDLE,
				medicalDto.getPersonDto().getNmPersonMiddle());
		bookmarkNonFrmGrpList.add(bookmarkMiddleName);

		// Populate the Person ID from CSEC3D
		BookmarkDto bookmarkIdPerson = createBookmark(BookmarkConstants.CHILDPERSONID, medicalDto.getIdPerson());
		bookmarkNonFrmGrpList.add(bookmarkIdPerson);

		// Populate the Legal County from CSECD5D
		BookmarkDto bookmarkCounty = createBookmark(BookmarkConstants.CNTY,
				medicalDto.getLegalStatusOutDto().getCdLegalStatCnty());
		bookmarkNonFrmGrpList.add(bookmarkCounty);

		// Populate the Cause Number from CSECD5D
		BookmarkDto bookmarkCauseNumber = createBookmark(BookmarkConstants.CAUSNO,
				medicalDto.getLegalStatusOutDto().getTxtLegalStatCauseNbr());
		bookmarkNonFrmGrpList.add(bookmarkCauseNumber);

		// Populate the Court Number from CSECD5D
		BookmarkDto bookmarkCourtNumber = createBookmark(BookmarkConstants.CRTNO,
				medicalDto.getLegalStatusOutDto().getTxtLegalStatCourtNbr());
		bookmarkNonFrmGrpList.add(bookmarkCourtNumber);

		// Populate the Primary Case worker Phone Number
		FormDataGroupDto tempCsWrkrPhn = createFormDataGroup(FormGroupsConstants.TMPLAT_CSWRKR_PHONE,
				FormConstants.EMPTY_STRING);
		List<BookmarkDto> bookmarkCsWrkrPhnGrpList = new ArrayList<BookmarkDto>();
		if(!ObjectUtils.isEmpty(medicalDto.getEmployeePersCsWrkrDto())) {
		BookmarkDto bookmarkPlcmtCsWrkrPhn = createBookmark(BookmarkConstants.CSWRKR_PHONE,
				TypeConvUtil.formatPhone(medicalDto.getEmployeePersCsWrkrDto().getNbrPhone()));
		bookmarkNonFrmGrpList.add(bookmarkPlcmtCsWrkrPhn);
		bookmarkCsWrkrPhnGrpList.add(bookmarkPlcmtCsWrkrPhn);
		tempCsWrkrPhn.setBookmarkDtoList(bookmarkCsWrkrPhnGrpList);
		formDataGroupList.add(tempCsWrkrPhn);
		}

		// Populate the Supervisor worker Phone Number
		FormDataGroupDto tempSuprVsrPhn = createFormDataGroup(FormGroupsConstants.TMPLAT_SUPRVSR_PHONE,
				FormConstants.EMPTY_STRING);
		List<BookmarkDto> bookmarkSuprVsrPhnGrpList = new ArrayList<BookmarkDto>();
		if(!ObjectUtils.isEmpty(medicalDto.getEmployeePersSuprvsrDto())) {
		BookmarkDto bookmarkPlcmtSuprvsrPhn = createBookmark(BookmarkConstants.SUPRVSR_PHONE,
				TypeConvUtil.formatPhone(medicalDto.getEmployeePersSuprvsrDto().getNbrPhone()));
		bookmarkNonFrmGrpList.add(bookmarkPlcmtSuprvsrPhn);
		bookmarkSuprVsrPhnGrpList.add(bookmarkPlcmtSuprvsrPhn);
		tempSuprVsrPhn.setBookmarkDtoList(bookmarkSuprVsrPhnGrpList);
		formDataGroupList.add(tempSuprVsrPhn);
		}
		PreFillDataServiceDto preFillData = new PreFillDataServiceDto();
		preFillData.setFormDataGroupList(formDataGroupList);
		preFillData.setBookmarkDtoList(bookmarkNonFrmGrpList);
		return preFillData;
	}

}
