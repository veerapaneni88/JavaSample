package us.tx.state.dfps.service.forms.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.service.common.BookmarkConstants;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.FormConstants;
import us.tx.state.dfps.service.common.FormGroupsConstants;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.util.FormattingUtils;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.forms.dto.BookmarkDto;
import us.tx.state.dfps.service.forms.dto.FormDataGroupDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.icpforms.dto.IcpFormsDto;
import us.tx.state.dfps.service.icpforms.dto.IcpcChildDetailsDto;
import us.tx.state.dfps.service.lookup.dao.LookupDao;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<Prefill
 * data class for Interstate Compact cover letter form> May 2, 2018- 10:40:44 AM
 * © 2017 Texas Department of Family and Protective Services
 */

@Component
public class ICcoverLetterPrefillData extends DocumentServiceUtil {

	/**
	 * Method Name: returnPrefillData Method Description: prefill data from
	 * ICPformsServiceImpl for interstate compact cover letter form.
	 * 
	 * @param parentDtoobj
	 * @return PreFillDataServiceDto
	 */

	@Autowired
	LookupDao lookUpDao;

	@Override
	public PreFillDataServiceDto returnPrefillData(Object parentDtoobj) {
		IcpFormsDto icpcFormsDto = (IcpFormsDto) parentDtoobj;

		List<FormDataGroupDto> formDataGroupList = new ArrayList<FormDataGroupDto>();
		List<BookmarkDto> bookmarkNonFrmGrpList = new ArrayList<BookmarkDto>();

		// group 18000
		if (!ObjectUtils.isEmpty(icpcFormsDto.getEmployeePersPhNameDto().getCdNameSuffix())) {
			FormDataGroupDto workerNameGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_WORKER_NAME,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> workerNamebookmarkList = new ArrayList<BookmarkDto>();
			BookmarkDto rsrcCity = createBookmark(BookmarkConstants.WORKER_NAME_SNAME,
					icpcFormsDto.getEmployeePersPhNameDto().getCdNameSuffix());
			workerNamebookmarkList.add(rsrcCity);
			workerNameGroup.setBookmarkDtoList(workerNamebookmarkList);
			formDataGroupList.add(workerNameGroup);
		}

		// group 18002
		if (!ObjectUtils.isEmpty(icpcFormsDto.getEmployeePersPhNameDto().getAddrMailCodeStLn2())) {
			FormDataGroupDto workerAddrGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_WORKER_ADDR,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> workerAddrbookmarkList = new ArrayList<BookmarkDto>();
			BookmarkDto addr2 = createBookmark(BookmarkConstants.WORKER_ADDR_ADDR2,
					icpcFormsDto.getEmployeePersPhNameDto().getAddrMailCodeStLn2());
			workerAddrbookmarkList.add(addr2);
			workerAddrGroup.setBookmarkDtoList(workerAddrbookmarkList);
			formDataGroupList.add(workerAddrGroup);
		}

		// group 18040
		//Defect# 12758 - Added a condition to set the placement details only for the care type related to resource
		if (!ObjectUtils.isEmpty(icpcFormsDto.getIcpcNameAndAddr()) 
				&& (ServiceConstants.CPL_30.equals(icpcFormsDto.getIcpcDetailsDto().getCdCareType())
						|| ServiceConstants.CFACTYP2_50.equals(icpcFormsDto.getIcpcDetailsDto().getCdCareType())
						|| ServiceConstants.CPL_60.equals(icpcFormsDto.getIcpcDetailsDto().getCdCareType())
						|| ServiceConstants.CCH_90.equals(icpcFormsDto.getIcpcDetailsDto().getCdCareType()))) {
			FormDataGroupDto rsrcAddrGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_RSRC_ADDR,
					FormConstants.EMPTY_STRING);
			List<FormDataGroupDto> rsrcAddrGroupList = new ArrayList<FormDataGroupDto>();
			List<BookmarkDto> rsrcAddrGroupbookmarkList = new ArrayList<BookmarkDto>();
			BookmarkDto rsrcCity = createBookmark(BookmarkConstants.RSRC_ADDR_CITY,
					icpcFormsDto.getIcpcNameAndAddr().getAddrCity());
			BookmarkDto rsrcZip = createBookmark(BookmarkConstants.RSRC_ADDR_ZIP,
					icpcFormsDto.getIcpcNameAndAddr().getAddrZip());
			BookmarkDto rsrcAddr = createBookmark(BookmarkConstants.RSRC_ADDR_1,
					icpcFormsDto.getIcpcNameAndAddr().getAddrStLn1());
			BookmarkDto rsrcState = createBookmark(BookmarkConstants.RSRC_ADDR_STATE,
					icpcFormsDto.getIcpcNameAndAddr().getAddrState());
			rsrcAddrGroupbookmarkList.add(rsrcCity);
			rsrcAddrGroupbookmarkList.add(rsrcZip);
			rsrcAddrGroupbookmarkList.add(rsrcAddr);
			rsrcAddrGroupbookmarkList.add(rsrcState);
			// sub group 18041
			if (!ObjectUtils.isEmpty(icpcFormsDto.getIcpcNameAndAddr().getAddrStLn2())) {
				FormDataGroupDto rsrcAddrSubGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_RSRC_ADDR_LN,
						FormGroupsConstants.TMPLAT_RSRC_ADDR);
				List<BookmarkDto> rsrcAddrsubList = new ArrayList<BookmarkDto>();
				BookmarkDto ln2 = createBookmark(BookmarkConstants.RSRC_ADDR_LN_2,
						icpcFormsDto.getIcpcNameAndAddr().getAddrStLn2());
				rsrcAddrsubList.add(ln2);
				rsrcAddrSubGroup.setBookmarkDtoList(rsrcAddrsubList);
				rsrcAddrGroupList.add(rsrcAddrSubGroup);
			}
			rsrcAddrGroup.setBookmarkDtoList(rsrcAddrGroupbookmarkList);
			rsrcAddrGroup.setFormDataGroupList(rsrcAddrGroupList);
			formDataGroupList.add(rsrcAddrGroup);

			// Group icp18060
			FormDataGroupDto rsrcPhoneGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_RSRC_PHONE,
					FormConstants.EMPTY_STRING);
			List<FormDataGroupDto> rsrcPhoneGroupList = new ArrayList<FormDataGroupDto>();
			List<BookmarkDto> rsrcPhoneGroupbookmarkList = new ArrayList<BookmarkDto>();
			BookmarkDto rsrcPhone = createBookmark(BookmarkConstants.RSRC_PHONE,
					TypeConvUtil.formatPhone(icpcFormsDto.getIcpcNameAndAddr().getNbrPersonPhone()));
			rsrcPhoneGroupbookmarkList.add(rsrcPhone);
			// sub group icp18061
			if (!ObjectUtils.isEmpty(icpcFormsDto.getIcpcNameAndAddr().getNbrPersonPhoneExt())) {
				FormDataGroupDto rsrcPhoneExtGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_RSRC_PHONE_EXT,
						FormGroupsConstants.TMPLAT_RSRC_PHONE);
				List<BookmarkDto> rsrcPhoneExtGroupbookmarkList = new ArrayList<BookmarkDto>();
				BookmarkDto rsrcExtPhone = createBookmark(BookmarkConstants.RSRC_PHONE_EXT,
						icpcFormsDto.getIcpcNameAndAddr().getNbrPersonPhoneExt());
				rsrcPhoneExtGroupbookmarkList.add(rsrcExtPhone);
				rsrcPhoneExtGroup.setBookmarkDtoList(rsrcPhoneExtGroupbookmarkList);
				rsrcPhoneGroupList.add(rsrcPhoneExtGroup);
			}
			rsrcPhoneGroup.setBookmarkDtoList(rsrcPhoneGroupbookmarkList);
			rsrcPhoneGroup.setFormDataGroupList(rsrcPhoneGroupList);
			formDataGroupList.add(rsrcPhoneGroup);

			// Group 18020
			FormDataGroupDto rsrcGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_RSRC,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> rsrcGroupbookmarkList = new ArrayList<BookmarkDto>();
			BookmarkDto fullName = createBookmark(BookmarkConstants.RSRC_FULLNAME,
					icpcFormsDto.getIcpcNameAndAddr().getNmResource());
			rsrcGroupbookmarkList.add(fullName);
			rsrcGroup.setBookmarkDtoList(rsrcGroupbookmarkList);
			formDataGroupList.add(rsrcGroup);
		}

		//Defect# 12758 - Added a condition to set the placement details only for the care type related to person
		if (!ObjectUtils.isEmpty(icpcFormsDto.getPersonDetailByIdReqDtoList())
				&& !(ServiceConstants.CPL_30.equals(icpcFormsDto.getIcpcDetailsDto().getCdCareType())
						|| ServiceConstants.CFACTYP2_50.equals(icpcFormsDto.getIcpcDetailsDto().getCdCareType())
						|| ServiceConstants.CPL_60.equals(icpcFormsDto.getIcpcDetailsDto().getCdCareType())
						|| ServiceConstants.CCH_90.equals(icpcFormsDto.getIcpcDetailsDto().getCdCareType()))) {
			for (IcpcChildDetailsDto dto : icpcFormsDto.getPersonDetailByIdReqDtoList()) {
				if (ServiceConstants.CD_MEMBER.equals(dto.getCdPersonType())) {

					// Group 18030
					FormDataGroupDto personGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_PERSON,
							FormConstants.EMPTY_STRING);
					List<BookmarkDto> personGroupbookmarkList = new ArrayList<BookmarkDto>();
					BookmarkDto firstName = createBookmark(BookmarkConstants.PERSON_FNAME, dto.getNmNameFirst());
					BookmarkDto lastName = createBookmark(BookmarkConstants.PERSON_LNAME, dto.getNmNameLast());
					personGroupbookmarkList.add(firstName);
					personGroupbookmarkList.add(lastName);
					personGroup.setBookmarkDtoList(personGroupbookmarkList);
					formDataGroupList.add(personGroup);

					// Group 18050
					FormDataGroupDto personAddrGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_PERSON_ADDR,
							FormConstants.EMPTY_STRING);
					List<BookmarkDto> personAddrGroupbookmarkList = new ArrayList<BookmarkDto>();
					BookmarkDto zip = createBookmark(BookmarkConstants.PERSON_ADDR_ZIP, dto.getAddrZip());
					BookmarkDto city = createBookmark(BookmarkConstants.PERSON_ADDR_CITY, dto.getAddrCity());
					FormDataGroupDto personAddrLn2Grp = createFormDataGroup(FormGroupsConstants.TMPLAT_PERSON_ADDR_LN,
							FormConstants.EMPTY_STRING);
					List<BookmarkDto> personAddrLn2bookmarkList = new ArrayList<BookmarkDto>();
					if (!ObjectUtils.isEmpty(dto.getAddrStLn2())) {
						BookmarkDto personAddrLn2 = createBookmark(BookmarkConstants.PERSON_ADDR_LN_2,
								dto.getAddrStLn2());
						personAddrLn2bookmarkList.add(personAddrLn2);
						personAddrLn2Grp.setBookmarkDtoList(personAddrLn2bookmarkList);
						formDataGroupList.add(personAddrLn2Grp);
					}
					BookmarkDto addr1 = createBookmark(BookmarkConstants.PERSON_ADDR_1, dto.getAddrStLn1());
					BookmarkDto state = createBookmark(BookmarkConstants.PERSON_ADDR_STATE, dto.getAddrState());
					personAddrGroupbookmarkList.add(zip);
					personAddrGroupbookmarkList.add(city);
					personAddrGroupbookmarkList.add(addr1);
					personAddrGroupbookmarkList.add(state);
					personAddrGroup.setBookmarkDtoList(personAddrGroupbookmarkList);
					formDataGroupList.add(personAddrGroup);

					// Group 18070
					FormDataGroupDto personPhoneGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_PERSON_PHONE,
							FormConstants.EMPTY_STRING);
					List<FormDataGroupDto> personPhoneGroupList = new ArrayList<FormDataGroupDto>();
					List<BookmarkDto> personPhoneGroupbookmarkList = new ArrayList<BookmarkDto>();
					BookmarkDto personPhone = createBookmark(BookmarkConstants.PERSON_PHONE,
							TypeConvUtil.formatPhone(dto.getNbrPersonPhone()));
					personPhoneGroupbookmarkList.add(personPhone);
					personPhoneGroup.setBookmarkDtoList(personPhoneGroupbookmarkList);

					// 18070's sub group 18071
					if (!ObjectUtils.isEmpty(dto.getNbrPersonPhoneExt())) {
						FormDataGroupDto personPhoneExtGroup = createFormDataGroup(
								FormGroupsConstants.TMPLAT_PERSON_PHONE_EXT, FormGroupsConstants.TMPLAT_PERSON_PHONE);
						List<BookmarkDto> personPhoneExtGroupbookmarkList = new ArrayList<BookmarkDto>();
						BookmarkDto personExtPhone = createBookmark(BookmarkConstants.PERSON_PHONE_EXT,
								dto.getNbrPersonPhoneExt());
						personPhoneExtGroupbookmarkList.add(personExtPhone);
						personPhoneExtGroup.setBookmarkDtoList(personPhoneGroupbookmarkList);
						personPhoneGroupList.add(personPhoneExtGroup);
					}
					personPhoneGroup.setFormDataGroupList(personPhoneGroupList);
					formDataGroupList.add(personPhoneGroup);
				}

				// group 18010
				if (ServiceConstants.REMOVAL_STG_PERS_ROLL_PC.equals(dto.getCdPersonType())) {

					FormDataGroupDto childGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_CHILD,
							FormConstants.EMPTY_STRING);
					List<BookmarkDto> personPhoneExtGroupbookmarkList = new ArrayList<BookmarkDto>();
					// formatted the date
					BookmarkDto dob = createBookmark(BookmarkConstants.CHILD_DOB, DateUtils.stringDt(dto.getDtPersonBirth()));
					BookmarkDto ssn = createBookmark(BookmarkConstants.CHILD_SSN, dto.getNbrPersonIdNumber());
					BookmarkDto fname = createBookmark(BookmarkConstants.CHILD_FNAME, dto.getNmNameFirst());
					BookmarkDto lname = createBookmark(BookmarkConstants.CHILD_LNAME, dto.getNmNameLast());
					BookmarkDto mname = createBookmark(BookmarkConstants.CHILD_MNAME, dto.getNmNameMiddle());
					personPhoneExtGroupbookmarkList.add(dob);
					personPhoneExtGroupbookmarkList.add(ssn);
					personPhoneExtGroupbookmarkList.add(fname);
					personPhoneExtGroupbookmarkList.add(lname);
					personPhoneExtGroupbookmarkList.add(mname);
					childGroup.setBookmarkDtoList(personPhoneExtGroupbookmarkList);
					formDataGroupList.add(childGroup);
				}
			}
		}

		// Group 18080
		if (!ObjectUtils.isEmpty(icpcFormsDto.getFetchFullConcurrentDto())) {
			FormDataGroupDto concurrentGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_CONCURRENT,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> personPhoneExtGroupbookmarkList = new ArrayList<BookmarkDto>();
			BookmarkDto dob = createBookmark(BookmarkConstants.CONCURRENT_GOAL,
					icpcFormsDto.getFetchFullConcurrentDto().getCdConcurrentGoal());
			personPhoneExtGroupbookmarkList.add(dob);
			concurrentGroup.setBookmarkDtoList(personPhoneExtGroupbookmarkList);
			formDataGroupList.add(concurrentGroup);
		}

		// Group 18090
		if (!ObjectUtils.isEmpty(icpcFormsDto.getFetchFullConcurrentDto())) {
			FormDataGroupDto concurrentNoGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_CONCURRENTNO,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> personPhoneExtGroupbookmarkList = new ArrayList<BookmarkDto>();
			if (!ObjectUtils.isEmpty(icpcFormsDto.getFetchFullConcurrentDto().getTsLastUpdate())) {
				BookmarkDto goal = createBookmark(BookmarkConstants.CONCURRENTNO_GOAL,
						DateUtils.stringDt(icpcFormsDto.getFetchFullConcurrentDto().getTsLastUpdate()));
				personPhoneExtGroupbookmarkList.add(goal);
				concurrentNoGroup.setBookmarkDtoList(personPhoneExtGroupbookmarkList);
				formDataGroupList.add(concurrentNoGroup);
			}
		}

		if (!ObjectUtils.isEmpty(icpcFormsDto.getFetchFullConcurrentDto())) {

			// Warranty Defect Fix - 12063 - To create Prefill Bookmark different from UserEdits
			BookmarkDto txtPermanencyPlan = createBookmark(BookmarkConstants.PERMANENCY_PLAN,
					populatePermanencyPlan(icpcFormsDto));
			bookmarkNonFrmGrpList.add(txtPermanencyPlan);
		}

		// Populating the non form group data into prefill data. !!bookmarks
		if (!ObjectUtils.isEmpty(icpcFormsDto.getGenericCaseInfoDto())) {
			BookmarkDto currDate = createBookmark(BookmarkConstants.CURRENT_DATE,
					DateUtils.stringDt(icpcFormsDto.getGenericCaseInfoDto().getDtSysDtGenericSysdate()));
			bookmarkNonFrmGrpList.add(currDate);
		}
		if (!ObjectUtils.isEmpty(icpcFormsDto.getEmployeePersPhNameDto())) {
			if (!ObjectUtils.isEmpty(icpcFormsDto.getEmployeePersPhNameDto().getAddrMailCodeCity())) {
				BookmarkDto workerCity = createBookmark(BookmarkConstants.WORKER_CITY,
						icpcFormsDto.getEmployeePersPhNameDto().getAddrMailCodeCity());
				bookmarkNonFrmGrpList.add(workerCity);

			}
			if (!ObjectUtils.isEmpty(icpcFormsDto.getEmployeePersPhNameDto().getAddrMailCodeStLn1())) {
				BookmarkDto workerAddr1 = createBookmark(BookmarkConstants.WORKER_ADDR1,
						icpcFormsDto.getEmployeePersPhNameDto().getAddrMailCodeStLn1());
				bookmarkNonFrmGrpList.add(workerAddr1);

			}
			if (!ObjectUtils.isEmpty(icpcFormsDto.getEmployeePersPhNameDto().getAddrMailCodeZip())) {
				BookmarkDto workerZip = createBookmark(BookmarkConstants.WORKER_ZIP,
						icpcFormsDto.getEmployeePersPhNameDto().getAddrMailCodeZip());
				bookmarkNonFrmGrpList.add(workerZip);

			}
			String workerPhone = icpcFormsDto.getEmployeePersPhNameDto().getNbrPhone();
			if (!StringUtils.isEmpty(workerPhone)) {
				BookmarkDto workerPhoneBookMark = createBookmark(BookmarkConstants.WORKER_PHONE,
						FormattingUtils.formatPhone(workerPhone));
				bookmarkNonFrmGrpList.add(workerPhoneBookMark);
			}
			if (!ObjectUtils.isEmpty(icpcFormsDto.getEmployeePersPhNameDto().getNmNameFirst())) {
				BookmarkDto workerFname = createBookmark(BookmarkConstants.WORKER_FNAME,
						icpcFormsDto.getEmployeePersPhNameDto().getNmNameFirst());
				bookmarkNonFrmGrpList.add(workerFname);

			}
			if (!ObjectUtils.isEmpty(icpcFormsDto.getEmployeePersPhNameDto().getNmNameLast())) {
				BookmarkDto workerLname = createBookmark(BookmarkConstants.WORKER_LNAME,
						icpcFormsDto.getEmployeePersPhNameDto().getNmNameLast());
				bookmarkNonFrmGrpList.add(workerLname);

			}
			if (!ObjectUtils.isEmpty(icpcFormsDto.getEmployeePersPhNameDto().getNmNameMiddle())) {
				BookmarkDto workerMname = createBookmark(BookmarkConstants.WORKER_MNAME,
						icpcFormsDto.getEmployeePersPhNameDto().getNmNameMiddle());
				bookmarkNonFrmGrpList.add(workerMname);

			}
		}
		if (!ObjectUtils.isEmpty(icpcFormsDto.getIcpcDetailsDto())) {
			BookmarkDto careType = createBookmarkWithCodesTable(BookmarkConstants.STUDY_TYPE,
					icpcFormsDto.getIcpcDetailsDto().getCdCareType(), CodesConstant.ICPCCRTP);
			bookmarkNonFrmGrpList.add(careType);
		}
		if (!ObjectUtils.isEmpty(icpcFormsDto.getEventChildPlanDto())) {
			BookmarkDto permGoal = createBookmarkWithCodesTable(BookmarkConstants.PERMANENT_GOAL,
					icpcFormsDto.getEventChildPlanDto().getCdCspPlanPermGoal(), CodesConstant.CCPPRMGL);
			bookmarkNonFrmGrpList.add(permGoal);
		}

		PreFillDataServiceDto preFillData = new PreFillDataServiceDto();
		preFillData.setFormDataGroupList(formDataGroupList);
		preFillData.setBookmarkDtoList(bookmarkNonFrmGrpList);
		return preFillData;
	}

	public String populatePermanencyPlan(IcpFormsDto icpcFormsDto) {
		StringBuilder txtPermanencyPlan = new StringBuilder();
		if (!ObjectUtils.isEmpty(icpcFormsDto.getFetchFullConcurrentDto().getCdConcurrentGoal())) {
			txtPermanencyPlan.append(lookUpDao.decode(CodesConstant.CCPPRMGL,
					icpcFormsDto.getFetchFullConcurrentDto().getCdConcurrentGoal()));
			txtPermanencyPlan.append(ServiceConstants.SPACE);
		}
		if (!ObjectUtils.isEmpty(icpcFormsDto.getFetchFullConcurrentDto().getTsLastUpdate())) {
			txtPermanencyPlan
					.append(TypeConvUtil.formDateFormat(icpcFormsDto.getFetchFullConcurrentDto().getTsLastUpdate()));
		}
		return txtPermanencyPlan.toString();

	}
}
