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
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.forms.dto.BookmarkDto;
import us.tx.state.dfps.service.forms.dto.FormDataGroupDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.icpforms.dto.IcpFormsDto;
import us.tx.state.dfps.service.icpforms.dto.IcpcChildDetailsDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<Prefill
 * data class for Priority Home study request form> May 3, 2018- 2:33:34 PM ©
 * 2017 Texas Department of Family and Protective Services
 */

@Component
public class ICpriorityHomePrefillData extends DocumentServiceUtil {

	/**
	 * Method Name: returnPrefillData Method Description: prefill data from
	 * ICPformsServiceImpl for Priority Home study request form
	 * 
	 * @param parentDtoobj
	 * @return PreFillDataServiceDto
	 */
	@Override
	public PreFillDataServiceDto returnPrefillData(Object parentDtoobj) {
		IcpFormsDto icpcFormsDto = (IcpFormsDto) parentDtoobj;

		List<FormDataGroupDto> formDataGroupList = new ArrayList<FormDataGroupDto>();
		List<BookmarkDto> bookmarkNonFrmGrpList = new ArrayList<BookmarkDto>();

		// Group 14001
		if (!ObjectUtils.isEmpty(icpcFormsDto.getEmployeePersPhNameDto().getCdNameSuffix())) {
			FormDataGroupDto workerNameGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_WORKER,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> workerNamebookmarkList = new ArrayList<BookmarkDto>();
			BookmarkDto suffix = createBookmark(BookmarkConstants.WORKER_SNAME,
					icpcFormsDto.getEmployeePersPhNameDto().getCdNameSuffix());
			workerNamebookmarkList.add(suffix);
			workerNameGroup.setBookmarkDtoList(workerNamebookmarkList);
			formDataGroupList.add(workerNameGroup);
		}

		// Group 14002
		if (!ObjectUtils.isEmpty(icpcFormsDto.getSupervisorPersPhNameDto().getCdNameSuffix())) {
			FormDataGroupDto workerNameGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_SUPERVISOR,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> workerNamebookmarkList = new ArrayList<BookmarkDto>();
			BookmarkDto suffix = createBookmark(BookmarkConstants.SUPERVISOR_SNAME,
					icpcFormsDto.getSupervisorPersPhNameDto().getCdNameSuffix());
			workerNamebookmarkList.add(suffix);
			workerNameGroup.setBookmarkDtoList(workerNamebookmarkList);
			formDataGroupList.add(workerNameGroup);
		}

		//Defect# 12758 - Added a condition to set the placement details only for the care type related to resourcer
		if (!ObjectUtils.isEmpty(icpcFormsDto.getIcpcNameAndAddr())
				&& (ServiceConstants.CPL_30.equals(icpcFormsDto.getIcpcDetailsDto().getCdCareType())
						|| ServiceConstants.CFACTYP2_50.equals(icpcFormsDto.getIcpcDetailsDto().getCdCareType())
						|| ServiceConstants.CPL_60.equals(icpcFormsDto.getIcpcDetailsDto().getCdCareType())
						|| ServiceConstants.CCH_90.equals(icpcFormsDto.getIcpcDetailsDto().getCdCareType()))) {

			// Group 14040
			FormDataGroupDto rsrcGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_RSRC,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> rsrcGroupbookmarkList = new ArrayList<BookmarkDto>();
			BookmarkDto fullName = createBookmark(BookmarkConstants.RSRC_FULLNAME,
					icpcFormsDto.getIcpcNameAndAddr().getNmResource());
			rsrcGroupbookmarkList.add(fullName);
			rsrcGroup.setBookmarkDtoList(rsrcGroupbookmarkList);
			formDataGroupList.add(rsrcGroup);

			// Group 14070
			FormDataGroupDto rsrcAddrGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_RSRC_ADDR,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> rsrcAddrGroupbookmarkList = new ArrayList<BookmarkDto>();
			BookmarkDto rsrcCity = createBookmark(BookmarkConstants.RSRC_ADDR_CITY,
					icpcFormsDto.getIcpcNameAndAddr().getAddrCity());
			BookmarkDto rsrcZip = createBookmark(BookmarkConstants.RSRC_ADDR_ZIP,
					icpcFormsDto.getIcpcNameAndAddr().getAddrZip());
			BookmarkDto rsrcAddr = createBookmark(BookmarkConstants.RSRC_ADDR_1,
					icpcFormsDto.getIcpcNameAndAddr().getAddrStLn1());
			BookmarkDto rsrcAddr2 = createBookmark(BookmarkConstants.RSRC_ADDR_2,
					icpcFormsDto.getIcpcNameAndAddr().getAddrStLn2());
			BookmarkDto rsrcState = createBookmark(BookmarkConstants.RSRC_ADDR_STATE,
					icpcFormsDto.getIcpcNameAndAddr().getAddrState());
			rsrcAddrGroupbookmarkList.add(rsrcCity);
			rsrcAddrGroupbookmarkList.add(rsrcZip);
			rsrcAddrGroupbookmarkList.add(rsrcAddr);
			rsrcAddrGroupbookmarkList.add(rsrcAddr2);
			rsrcAddrGroupbookmarkList.add(rsrcState);
			rsrcAddrGroup.setBookmarkDtoList(rsrcAddrGroupbookmarkList);
			formDataGroupList.add(rsrcAddrGroup);

			// Group icp14090
			FormDataGroupDto rsrcPhoneGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_RSRC_PHONE,
					FormConstants.EMPTY_STRING);
			List<FormDataGroupDto> rsrcPhoneGroupList = new ArrayList<FormDataGroupDto>();
			List<BookmarkDto> rsrcPhoneGroupbookmarkList = new ArrayList<BookmarkDto>();
			BookmarkDto rsrcPhone = createBookmark(BookmarkConstants.RSRC_PHONE,
					TypeConvUtil.formatPhone(icpcFormsDto.getIcpcNameAndAddr().getNbrPersonPhone()));
			rsrcPhoneGroupbookmarkList.add(rsrcPhone);
			rsrcPhoneGroup.setBookmarkDtoList(rsrcPhoneGroupbookmarkList);

			// sub group icp14091
			if (!ObjectUtils.isEmpty(icpcFormsDto.getIcpcNameAndAddr().getNbrPersonPhoneExt())) {
				FormDataGroupDto rsrcPhoneExtGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_RSRC_PHONE_EXT,
						FormGroupsConstants.TMPLAT_RSRC_PHONE);
				List<BookmarkDto> rsrcPhoneExtGroupbookmarkList = new ArrayList<BookmarkDto>();
				BookmarkDto rsrcExtPhone = createBookmark(BookmarkConstants.RSRC_PHONE_EXT,
						icpcFormsDto.getIcpcNameAndAddr().getNbrPersonPhoneExt());
				rsrcPhoneExtGroupbookmarkList.add(rsrcExtPhone);
				rsrcPhoneExtGroup.setBookmarkDtoList(rsrcPhoneExtGroupbookmarkList);
				rsrcPhoneGroupList.add(rsrcPhoneExtGroup);
				rsrcPhoneGroup.setFormDataGroupList(rsrcPhoneGroupList);
			}
			formDataGroupList.add(rsrcPhoneGroup);
		}

		// Group 14010
		if (!ObjectUtils.isEmpty(icpcFormsDto.getPersonDetailByIdReqDtoList())) {
			for (IcpcChildDetailsDto dto : icpcFormsDto.getPersonDetailByIdReqDtoList()) {
				if (ServiceConstants.PRIMARY_CHILD_ROLE.equals(dto.getCdPersonType())) {
					FormDataGroupDto childGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_CHILD,
							FormConstants.EMPTY_STRING);
					List<BookmarkDto> personGroupbookmarkList = new ArrayList<BookmarkDto>();
					BookmarkDto dob = createBookmark(BookmarkConstants.CHILD_DOB,
							DateUtils.stringDt(dto.getDtPersonBirth()));
					BookmarkDto firstName = createBookmark(BookmarkConstants.CHILD_FNAME, dto.getNmNameFirst());
					BookmarkDto lastName = createBookmark(BookmarkConstants.CHILD_LNAME, dto.getNmNameLast());
					BookmarkDto middleName = createBookmark(BookmarkConstants.CHILD_MNAME, dto.getNmNameMiddle());
					personGroupbookmarkList.add(firstName);
					personGroupbookmarkList.add(lastName);
					personGroupbookmarkList.add(dob);
					personGroupbookmarkList.add(middleName);
					childGroup.setBookmarkDtoList(personGroupbookmarkList);
					formDataGroupList.add(childGroup);
				}

				// Group 14020
				if (ServiceConstants.UNIT_MEMBER_ROLE_30.equals(dto.getCdPersonType())) {
					FormDataGroupDto motherGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_MOTHER,
							FormConstants.EMPTY_STRING);
					List<BookmarkDto> personGroupbookmarkList = new ArrayList<BookmarkDto>();
					BookmarkDto firstName = createBookmark(BookmarkConstants.MOTHER_FNAME, dto.getNmNameFirst());
					BookmarkDto lastName = createBookmark(BookmarkConstants.MOTHER_LNAME, dto.getNmNameLast());
					personGroupbookmarkList.add(firstName);
					personGroupbookmarkList.add(lastName);
					motherGroup.setBookmarkDtoList(personGroupbookmarkList);
					formDataGroupList.add(motherGroup);
				}

				// Group 14030
				if (ServiceConstants.CPRMGLTY_10.equals(dto.getCdPersonType())) {
					FormDataGroupDto fatherGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_FATHER,
							FormConstants.EMPTY_STRING);
					List<BookmarkDto> personGroupbookmarkList = new ArrayList<BookmarkDto>();
					BookmarkDto firstName = createBookmark(BookmarkConstants.FATHER_FNAME, dto.getNmNameFirst());
					BookmarkDto lastName = createBookmark(BookmarkConstants.FATHER_LNAME, dto.getNmNameLast());
					personGroupbookmarkList.add(firstName);
					personGroupbookmarkList.add(lastName);
					fatherGroup.setBookmarkDtoList(personGroupbookmarkList);
					formDataGroupList.add(fatherGroup);
				}

				// Group 14050
				//Defect# 12758 - Added a condition to set the placement details only for the care type related to person
				if (ServiceConstants.CCH_60.equals(dto.getCdPersonType())
						&& !(ServiceConstants.CPL_30.equals(icpcFormsDto.getIcpcDetailsDto().getCdCareType())
								|| ServiceConstants.CFACTYP2_50.equals(icpcFormsDto.getIcpcDetailsDto().getCdCareType())
								|| ServiceConstants.CPL_60.equals(icpcFormsDto.getIcpcDetailsDto().getCdCareType())
								|| ServiceConstants.CCH_90.equals(icpcFormsDto.getIcpcDetailsDto().getCdCareType()))) {
					FormDataGroupDto personGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_PERSON,
							FormConstants.EMPTY_STRING);
					List<BookmarkDto> personGroupbookmarkList = new ArrayList<BookmarkDto>();
					BookmarkDto firstName = createBookmark(BookmarkConstants.PERSON_FNAME, dto.getNmNameFirst());
					BookmarkDto lastName = createBookmark(BookmarkConstants.PERSON_LNAME, dto.getNmNameLast());
					personGroupbookmarkList.add(firstName);
					personGroupbookmarkList.add(lastName);
					personGroup.setBookmarkDtoList(personGroupbookmarkList);
					formDataGroupList.add(personGroup);
				}

				// Group 14060
				if (ServiceConstants.CCH_60.equals(dto.getCdPersonType())) {
					FormDataGroupDto personalGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_PERSONAL,
							FormConstants.EMPTY_STRING);
					List<BookmarkDto> personGroupbookmarkList = new ArrayList<BookmarkDto>();
					//date formatted
					BookmarkDto dob = createBookmark(BookmarkConstants.PERSONAL_DOB, DateUtils.dateStringInSlashFormat(dto.getDtPersonBirth()));
					personGroupbookmarkList.add(dob);
					personalGroup.setBookmarkDtoList(personGroupbookmarkList);
					formDataGroupList.add(personalGroup);
				}

				// Group 14080
				//Defect# 12758 - Added a condition to set the placement details only for the care type related to person address
				if (ServiceConstants.CCH_60.equals(dto.getCdPersonType())
						&& !(ServiceConstants.CPL_30.equals(icpcFormsDto.getIcpcDetailsDto().getCdCareType())
								|| ServiceConstants.CFACTYP2_50.equals(icpcFormsDto.getIcpcDetailsDto().getCdCareType())
								|| ServiceConstants.CPL_60.equals(icpcFormsDto.getIcpcDetailsDto().getCdCareType())
								|| ServiceConstants.CCH_90.equals(icpcFormsDto.getIcpcDetailsDto().getCdCareType()))) {
					FormDataGroupDto personGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_PERSON_ADDR,
							FormConstants.EMPTY_STRING);
					List<BookmarkDto> personGroupbookmarkList = new ArrayList<BookmarkDto>();
					BookmarkDto zip = createBookmark(BookmarkConstants.PERSON_ADDR_ZIP, dto.getAddrZip());
					BookmarkDto city = createBookmark(BookmarkConstants.PERSON_ADDR_CITY, dto.getAddrCity());
					BookmarkDto addr1 = createBookmark(BookmarkConstants.PERSON_ADDR_1, dto.getAddrStLn1());
					BookmarkDto addr2 = createBookmark(BookmarkConstants.PERSON_ADDR_2, dto.getAddrStLn2());
					BookmarkDto state = createBookmark(BookmarkConstants.PERSON_ADDR_STATE, dto.getAddrState());
					personGroupbookmarkList.add(zip);
					personGroupbookmarkList.add(city);
					personGroupbookmarkList.add(addr1);
					personGroupbookmarkList.add(addr2);
					personGroupbookmarkList.add(state);
					personGroup.setBookmarkDtoList(personGroupbookmarkList);
					formDataGroupList.add(personGroup);
				}

				// Group 14100
				//Defect# 12758 - Added a condition to set the placement details only for the care type related to person phone number
				if (ServiceConstants.CCH_60.equals(dto.getCdPersonType())
						&& !(ServiceConstants.CPL_30.equals(icpcFormsDto.getIcpcDetailsDto().getCdCareType())
								|| ServiceConstants.CFACTYP2_50.equals(icpcFormsDto.getIcpcDetailsDto().getCdCareType())
								|| ServiceConstants.CPL_60.equals(icpcFormsDto.getIcpcDetailsDto().getCdCareType())
								|| ServiceConstants.CCH_90.equals(icpcFormsDto.getIcpcDetailsDto().getCdCareType()))) {
					FormDataGroupDto personGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_PERSON_PHONE,
							FormConstants.EMPTY_STRING);
					List<FormDataGroupDto> rsrcPhoneGroupList = new ArrayList<FormDataGroupDto>();
					List<BookmarkDto> personGroupbookmarkList = new ArrayList<BookmarkDto>();
					BookmarkDto phone = createBookmark(BookmarkConstants.PERSON_PHONE, TypeConvUtil.formatPhone(dto.getNbrPersonPhone()));
					personGroupbookmarkList.add(phone);
					personGroup.setBookmarkDtoList(personGroupbookmarkList);
					// sub group 14101
					if (!ObjectUtils.isEmpty(dto.getNbrPersonPhoneExt())) {
						FormDataGroupDto phoneExtGroup = createFormDataGroup(
								FormGroupsConstants.TMPLAT_PERSON_PHONE_EXT, FormConstants.EMPTY_STRING);
						List<BookmarkDto> phoneExtbookmarkList = new ArrayList<BookmarkDto>();
						BookmarkDto phoneExt = createBookmark(BookmarkConstants.PERSON_PHONE_EXT,
								TypeConvUtil.formatPhone(dto.getNbrPersonPhone()));
						phoneExtbookmarkList.add(phoneExt);
						phoneExtGroup.setBookmarkDtoList(phoneExtbookmarkList);
						rsrcPhoneGroupList.add(phoneExtGroup);
					}
					personGroup.setFormDataGroupList(rsrcPhoneGroupList);
					formDataGroupList.add(personGroup);
				}

				// Group 14110
				if (ServiceConstants.CCH_60.equals(dto.getCdPersonType())) {
					FormDataGroupDto relationshipGroup = createFormDataGroup(
							FormGroupsConstants.TMPLAT_PERSON_RELATIONSHIP, FormConstants.EMPTY_STRING);
					List<BookmarkDto> relationshipGroupbookmarkList = new ArrayList<BookmarkDto>();
					////Defect# 12758 - setting the decode value
					BookmarkDto persInt = createBookmarkWithCodesTable(BookmarkConstants.PERSON_RELATIONSHIP_RELINT,
							dto.getCdStagePersRelInt(),CodesConstant.CRELPRN2);
					relationshipGroupbookmarkList.add(persInt);
					relationshipGroup.setBookmarkDtoList(relationshipGroupbookmarkList);
					formDataGroupList.add(relationshipGroup);
				}
			}
		}

		// Populating the non form group data into prefill data. !!bookmarks
		BookmarkDto workerDate = createBookmark(BookmarkConstants.WORKER_DATE,
				DateUtils.stringDt(icpcFormsDto.getGenericCaseInfoDto().getDtSysDtGenericSysdate()));
		BookmarkDto supDate = createBookmark(BookmarkConstants.SUPERVISOR_DATE,
				DateUtils.stringDt(icpcFormsDto.getGenericCaseInfoDto().getDtSysDtGenericSysdate()));
		BookmarkDto workerFname = createBookmark(BookmarkConstants.WORKER_FNAME,
				icpcFormsDto.getEmployeePersPhNameDto().getNmNameFirst());
		BookmarkDto workerLname = createBookmark(BookmarkConstants.WORKER_LNAME,
				icpcFormsDto.getEmployeePersPhNameDto().getNmNameLast());
		BookmarkDto workerMname = createBookmark(BookmarkConstants.WORKER_MNAME,
				icpcFormsDto.getEmployeePersPhNameDto().getNmNameMiddle());
		BookmarkDto supFname = createBookmark(BookmarkConstants.SUPERVISOR_FNAME,
				icpcFormsDto.getSupervisorPersPhNameDto().getNmNameFirst());
		BookmarkDto supLname = createBookmark(BookmarkConstants.SUPERVISOR_LNAME,
				icpcFormsDto.getSupervisorPersPhNameDto().getNmNameLast());
		BookmarkDto supMname = createBookmark(BookmarkConstants.SUPERVISOR_MNAME,
				icpcFormsDto.getSupervisorPersPhNameDto().getNmNameMiddle());

		bookmarkNonFrmGrpList.add(workerDate);
		bookmarkNonFrmGrpList.add(supDate);
		bookmarkNonFrmGrpList.add(workerFname);
		bookmarkNonFrmGrpList.add(workerLname);
		bookmarkNonFrmGrpList.add(workerMname);
		bookmarkNonFrmGrpList.add(supFname);
		bookmarkNonFrmGrpList.add(supLname);
		bookmarkNonFrmGrpList.add(supMname);

		PreFillDataServiceDto preFillData = new PreFillDataServiceDto();
		preFillData.setFormDataGroupList(formDataGroupList);
		preFillData.setBookmarkDtoList(bookmarkNonFrmGrpList);
		return preFillData;
	}
}
