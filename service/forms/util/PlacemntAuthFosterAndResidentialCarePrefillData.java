package us.tx.state.dfps.service.forms.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.service.admin.dto.LegalStatusOutDto;
import us.tx.state.dfps.service.admin.dto.PlacementActPlannedOutDto;
import us.tx.state.dfps.service.common.BookmarkConstants;
import us.tx.state.dfps.service.common.FormConstants;
import us.tx.state.dfps.service.common.FormGroupsConstants;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.forms.dto.BookmarkDto;
import us.tx.state.dfps.service.forms.dto.FormDataGroupDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.placement.dto.PlacementFormDto;
import us.tx.state.dfps.service.workload.dto.EmployeePersPhNameDto;
import us.tx.state.dfps.service.workload.dto.PersonDto;

/**
 * Name:PlacemntAuthFosterAndResidentialCarePrefillData Method Description:This
 * class implements returnPrefillData operation defined in DocumentServiceUtil
 * Interface to populate the prefill data for below forms Form Name
 * :cvs01o00(PlacementAuthorization Foster Care/Residential Care - 2085FC) Form
 * Name:cvs02o00(Placement Authorization Kinship or Other Non-Foster Caregiver
 * -2085KO) Form Name :cvs03o00(Placement Authorization Legal Risk - 2085LR)
 */
@Component
public class PlacemntAuthFosterAndResidentialCarePrefillData extends DocumentServiceUtil {

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

		PlacementFormDto placementFormDto = (PlacementFormDto) parentDtoobj;
		List<FormDataGroupDto> formDataGroupList = new ArrayList<FormDataGroupDto>();

		if (null == placementFormDto.getPersonDto()) {
			placementFormDto.setPersonDto(new PersonDto());
		}
		if (null == placementFormDto.getLegalStatusOutDto()) {
			placementFormDto.setLegalStatusOutDto(new LegalStatusOutDto());
		}
		if (null == placementFormDto.getPlacementActPlannedOutDto()) {
			placementFormDto.setPlacementActPlannedOutDto(new PlacementActPlannedOutDto());
		}

		if (null == placementFormDto.getEmployeePersCsWrkrDto()) {
			placementFormDto.setEmployeePersCsWrkrDto(new EmployeePersPhNameDto());
		}

		if (null == placementFormDto.getEmployeePersSuprvsrDto()) {
			placementFormDto.setEmployeePersSuprvsrDto(new EmployeePersPhNameDto());
		}
		if (FormConstants.EMPTY_STRING != placementFormDto.getPersonDto().getCdPersonSuffix()) {
			FormDataGroupDto tempChildCommaDto = createFormDataGroup(FormGroupsConstants.TMPLAT_CHILD_COMMA,
					FormConstants.EMPTY_STRING);
			formDataGroupList.add(tempChildCommaDto);
		}
		/**
		 * Populating the independent BookMarks into prefill data
		 */
		List<BookmarkDto> bookmarkNonFrmGrpList = new ArrayList<BookmarkDto>();
		// Populate the DOB from CSECD3D

		BookmarkDto bookmarkDOB = createBookmark(BookmarkConstants.DOB,
				TypeConvUtil.formDateFormat(placementFormDto.getPersonDto().getDtPersonBirth()));
		bookmarkNonFrmGrpList.add(bookmarkDOB);

		// Populate the Child Suffix from CSECD3D
		if (!ObjectUtils.isEmpty(placementFormDto.getPersonDto())
				&& !ObjectUtils.isEmpty(placementFormDto.getPersonDto().getNbrPersonIdNumber())) {
			BookmarkDto bookmarkMedNo = createBookmark(BookmarkConstants.MEDNO,
					placementFormDto.getPersonDto().getNbrPersonIdNumber());
			bookmarkNonFrmGrpList.add(bookmarkMedNo);
		} else {
			BookmarkDto bookmarkMedNo = createBookmark(BookmarkConstants.MEDNO,
					placementFormDto.getPersonDto().getPersonIdNumber());
			bookmarkNonFrmGrpList.add(bookmarkMedNo);
		}

		// Populate the MedID from CSEC3D
		BookmarkDto bookmarkChildSuffix = new BookmarkDto();
		String suffix = "";
		if (ServiceConstants.JR.equalsIgnoreCase(placementFormDto.getPersonDto().getCdPersonSuffix())
				&& (StringUtils.isNotEmpty(placementFormDto.getPersonDto().getCdPersonSuffix()))) {
			suffix = ServiceConstants.JUNIOR;
		} else if (ServiceConstants.SR.equalsIgnoreCase(placementFormDto.getPersonDto().getCdPersonSuffix())
				&& (StringUtils.isNotEmpty(placementFormDto.getPersonDto().getCdPersonSuffix()))) {
			suffix = ServiceConstants.SENIOR;
		}
		bookmarkChildSuffix = createBookmark(BookmarkConstants.CHILD_SUFFIX, suffix);
		bookmarkNonFrmGrpList.add(bookmarkChildSuffix);

		// Populate the First Name from CSEC3D
		BookmarkDto bookmarkFirstName = createBookmark(BookmarkConstants.CHILD_FIRST,
				placementFormDto.getPersonDto().getNmPersonFirst());
		bookmarkNonFrmGrpList.add(bookmarkFirstName);

		// Populate the Last Name from CSEC3D
		BookmarkDto bookmarkLastName = createBookmark(BookmarkConstants.CHILD_LAST,
				placementFormDto.getPersonDto().getNmPersonLast());
		bookmarkNonFrmGrpList.add(bookmarkLastName);

		// Populate the Middle Name from CSEC3D
		BookmarkDto bookmarkMiddleName = createBookmark(BookmarkConstants.CHILD_MIDDLE,
				placementFormDto.getPersonDto().getNmPersonMiddle());
		bookmarkNonFrmGrpList.add(bookmarkMiddleName);

		// Populate the Person ID from CSEC3D
		BookmarkDto bookmarkIdPerson = createBookmark(BookmarkConstants.CHILDPERSONID,
				placementFormDto.getPlacementActPlannedOutDto().getIdPlcmtChild());
		bookmarkNonFrmGrpList.add(bookmarkIdPerson);

		// Populate the Legal County from CSECD5D
		BookmarkDto bookmarkCounty = createBookmark(BookmarkConstants.CNTY,
				placementFormDto.getLegalStatusOutDto().getCdLegalStatCnty());
		bookmarkNonFrmGrpList.add(bookmarkCounty);

		// Populate the Cause Number from CSECD5D
		BookmarkDto bookmarkCauseNumber = createBookmark(BookmarkConstants.CAUSNO,
				placementFormDto.getLegalStatusOutDto().getTxtLegalStatCauseNbr());
		bookmarkNonFrmGrpList.add(bookmarkCauseNumber);

		// Populate the Court Number from CSECD5D
		BookmarkDto bookmarkCourtNumber = createBookmark(BookmarkConstants.CRTNO,
				placementFormDto.getLegalStatusOutDto().getTxtLegalStatCourtNbr());
		bookmarkNonFrmGrpList.add(bookmarkCourtNumber);

		// Populate the placementStartDate from CSES34D

		BookmarkDto bookmarkPlcmtStartDt = createBookmark(BookmarkConstants.DT_PLCMT,
				TypeConvUtil.formDateFormat(placementFormDto.getPlacementActPlannedOutDto().getDtPlcmtStart()));
		bookmarkNonFrmGrpList.add(bookmarkPlcmtStartDt);

		// Populate the Primary Case worker Name and Phone Number
		FormDataGroupDto tempCsWrkrName = createFormDataGroup(FormGroupsConstants.TMPLAT_CSWRKR_NAME,
				FormConstants.EMPTY_STRING);
		List<BookmarkDto> bookmarkCsWrkrNameGrpList = new ArrayList<BookmarkDto>();
		BookmarkDto bookmarkPlcmtCsWrkrNm = createBookmark(BookmarkConstants.CSWRKR_NAME,
				TypeConvUtil.formatFullName(placementFormDto.getEmployeePersCsWrkrDto().getNmNameFirst(),
						placementFormDto.getEmployeePersCsWrkrDto().getNmNameMiddle(),
						placementFormDto.getEmployeePersCsWrkrDto().getNmNameLast()));
		bookmarkCsWrkrNameGrpList.add(bookmarkPlcmtCsWrkrNm);
		tempCsWrkrName.setBookmarkDtoList(bookmarkCsWrkrNameGrpList);
		formDataGroupList.add(tempCsWrkrName);
		FormDataGroupDto tempCsWrkrPhn = createFormDataGroup(FormGroupsConstants.TMPLAT_CSWRKR_PHONE,
				FormConstants.EMPTY_STRING);
		List<BookmarkDto> bookmarkCsWrkrPhnGrpList = new ArrayList<BookmarkDto>();
		BookmarkDto bookmarkPlcmtCsWrkrPhn = createBookmark(BookmarkConstants.CSWRKR_PHONE,
				TypeConvUtil.formatPhone(placementFormDto.getEmployeePersCsWrkrDto().getNbrPhone()));
		bookmarkCsWrkrPhnGrpList.add(bookmarkPlcmtCsWrkrPhn);
		tempCsWrkrPhn.setBookmarkDtoList(bookmarkCsWrkrPhnGrpList);
		formDataGroupList.add(tempCsWrkrPhn);

		// Populate the Supervisor worker Name and Phone Number
		FormDataGroupDto tempSuprvsrName = createFormDataGroup(FormGroupsConstants.TMPLAT_SUPRVSR_NAME,
				FormConstants.EMPTY_STRING);
		List<BookmarkDto> bookmarkSuprvsrNameGrpList = new ArrayList<BookmarkDto>();
		BookmarkDto bookmarkPlcmtSuprvsrNm = createBookmark(BookmarkConstants.SUPRVSR_NAME,
				TypeConvUtil.formatFullName(placementFormDto.getEmployeePersSuprvsrDto().getNmNameFirst(),
						placementFormDto.getEmployeePersSuprvsrDto().getNmNameMiddle(),
						placementFormDto.getEmployeePersSuprvsrDto().getNmNameLast()));
		bookmarkSuprvsrNameGrpList.add(bookmarkPlcmtSuprvsrNm);
		tempSuprvsrName.setBookmarkDtoList(bookmarkSuprvsrNameGrpList);
		formDataGroupList.add(tempSuprvsrName);
		FormDataGroupDto tempSuprVsrPhn = createFormDataGroup(FormGroupsConstants.TMPLAT_SUPRVSR_PHONE,
				FormConstants.EMPTY_STRING);
		List<BookmarkDto> bookmarkSuprVsrPhnGrpList = new ArrayList<BookmarkDto>();
		BookmarkDto bookmarkPlcmtSuprvsrPhn = createBookmark(BookmarkConstants.SUPRVSR_PHONE,
				TypeConvUtil.formatPhone(placementFormDto.getEmployeePersSuprvsrDto().getNbrPhone()));
		bookmarkSuprVsrPhnGrpList.add(bookmarkPlcmtSuprvsrPhn);
		tempSuprVsrPhn.setBookmarkDtoList(bookmarkSuprVsrPhnGrpList);
		formDataGroupList.add(tempSuprVsrPhn);

		// Setting the BookMarkDtoList and FormDataGroupList into PrefillData
		PreFillDataServiceDto preFillData = new PreFillDataServiceDto();
		preFillData.setFormDataGroupList(formDataGroupList);
		preFillData.setBookmarkDtoList(bookmarkNonFrmGrpList);
		return preFillData;

	}

}
