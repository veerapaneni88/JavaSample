package us.tx.state.dfps.service.forms.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.dto.CentralRegistryDto;
import us.tx.state.dfps.populateletter.dto.CodesTablesDto;
import us.tx.state.dfps.service.common.BookmarkConstants;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.FormConstants;
import us.tx.state.dfps.service.common.FormGroupsConstants;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.forms.dto.BookmarkDto;
import us.tx.state.dfps.service.forms.dto.FormDataGroupDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.workload.dto.PersonDto;
import us.tx.state.dfps.xmlstructs.inputstructs.PersonRoleDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:DPSCrimHistResPrefillData will implement returnPrefillData
 * operation defined in DocumentServiceUtil Interface to populate the prefill
 * data for Form ccn04o00 May 3, 2018- 10:53:14 AM © 2017 Texas Department of
 * Family and Protective Services
 */
@Component
public class CentralRegistryFormPrefillData extends DocumentServiceUtil {

	@Override
	public PreFillDataServiceDto returnPrefillData(Object parentDtoobj) {

		CentralRegistryDto centralRegistryDto = (CentralRegistryDto) parentDtoobj;
		if (ObjectUtils.isEmpty(centralRegistryDto.getCodeTableDtoList())) {
			centralRegistryDto.setCodeTableDtoList(new ArrayList<CodesTablesDto>());
		}

		if (ObjectUtils.isEmpty(centralRegistryDto.getPersonDto())) {
			centralRegistryDto.setPersonDto(new PersonDto());
		}

		if (ObjectUtils.isEmpty(centralRegistryDto.getPersonRolesForOpenARInList())) {
			centralRegistryDto.setPersonRolesForOpenARInList(new ArrayList<PersonRoleDto>());
		}

		if (ObjectUtils.isEmpty(centralRegistryDto.getPersonRolesForOpenInvList())) {
			centralRegistryDto.setPersonRolesForOpenInvList(new ArrayList<PersonRoleDto>());
		}

		if (ObjectUtils.isEmpty(centralRegistryDto.getSpPersonRoleList())) {
			centralRegistryDto.setSpPersonRoleList(new ArrayList<PersonRoleDto>());
		}

		if (ObjectUtils.isEmpty(centralRegistryDto.getVictimPersonRolesList())) {
			centralRegistryDto.setVictimPersonRolesList(new ArrayList<PersonRoleDto>());
		}

		List<FormDataGroupDto> formDataGroupList = new ArrayList<FormDataGroupDto>();

		// create form group cfzz0601 and set prefill data
		for (CodesTablesDto codeTableDto : centralRegistryDto.getCodeTableDtoList()) {
			if (FormConstants.SYSCODE.equals(codeTableDto.getaCode())) {
				FormDataGroupDto tempHeaderDirFrmDataGrpDto = createFormDataGroup(
						FormGroupsConstants.TMPLAT_HEADER_DIRECTOR, FormConstants.EMPTY_STRING);
				List<BookmarkDto> tempHeaderBookMarkList = new ArrayList<BookmarkDto>();
				BookmarkDto dirTitle = createBookmark(BookmarkConstants.HEADER_DIRECTOR_TITLE,
						codeTableDto.getaDecode());
				BookmarkDto dirName = createBookmark(BookmarkConstants.HEADER_DIRECTOR_NAME, codeTableDto.getbDecode());
				tempHeaderBookMarkList.add(dirName);
				tempHeaderBookMarkList.add(dirTitle);
				tempHeaderDirFrmDataGrpDto.setBookmarkDtoList(tempHeaderBookMarkList);
				formDataGroupList.add(tempHeaderDirFrmDataGrpDto);

			}
		}

		// create form group cfzz0501 and set prefill data
		for (CodesTablesDto codeTableDto : centralRegistryDto.getCodeTableDtoList()) {
			if (Integer.parseInt(FormConstants.SYSCODE) < Integer.parseInt(codeTableDto.getaCode())) {
				FormDataGroupDto tempHeaderBoardFrmDataGrpDto = createFormDataGroup(
						FormGroupsConstants.TMPLAT_HEADER_BOARD, FormConstants.EMPTY_STRING);
				List<BookmarkDto> tempHeaderBookMarkList = new ArrayList<BookmarkDto>();
				BookmarkDto boardCity = createBookmark(BookmarkConstants.HEADER_BOARD_CITY, codeTableDto.getaDecode());
				BookmarkDto boardName = createBookmark(BookmarkConstants.HEADER_BOARD_NAME, codeTableDto.getbDecode());
				tempHeaderBookMarkList.add(boardCity);
				tempHeaderBookMarkList.add(boardName);
				tempHeaderBoardFrmDataGrpDto.setBookmarkDtoList(tempHeaderBookMarkList);
				formDataGroupList.add(tempHeaderBoardFrmDataGrpDto);

			}
		}

		// create form group ccn04o01 and set prefill data
		for (PersonRoleDto personRoleDto : centralRegistryDto.getPersonRolesForOpenInvList()) {
			FormDataGroupDto tempProgramFrmDataGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_PROGRAM,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> tempProgramBookMarkList = new ArrayList<BookmarkDto>();
			BookmarkDto cdStageProgram = createBookmarkWithCodesTable(BookmarkConstants.PROGRAM,
					personRoleDto.getCdStageProgram(), CodesConstant.CPGRMSFM);
			tempProgramBookMarkList.add(cdStageProgram);
			tempProgramFrmDataGrpDto.setBookmarkDtoList(tempProgramBookMarkList);

			formDataGroupList.add(tempProgramFrmDataGrpDto);
		}

		// create from group ccn04o02 and set prefill data

		for (PersonRoleDto personRoleDto : centralRegistryDto.getPersonRolesForOpenInvList()) {
			FormDataGroupDto tempCaseIdFrmDataGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_CASE_ID,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> tempCaseIdBookMarkList = new ArrayList<BookmarkDto>();
			BookmarkDto idCaseAddBookmark = createBookmark(BookmarkConstants.CASE_ID, personRoleDto.getIdCase());
			tempCaseIdBookMarkList.add(idCaseAddBookmark);
			tempCaseIdFrmDataGrpDto.setBookmarkDtoList(tempCaseIdBookMarkList);
			formDataGroupList.add(tempCaseIdFrmDataGrpDto);
		}

		// create form group ccn04o03 and set prefill data

		for (PersonRoleDto personRoleDto : centralRegistryDto.getSpPersonRoleList()) {
			FormDataGroupDto tempProgramFrmDataGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_SSTND_PRP_PROG,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> tempSStndBookMarkList = new ArrayList<BookmarkDto>();
			BookmarkDto cdStageProgram = createBookmarkWithCodesTable(BookmarkConstants.SSTND_PRP_PROG,
					personRoleDto.getCdStageProgram(), CodesConstant.CPGRMSFM);
			tempSStndBookMarkList.add(cdStageProgram);
			tempProgramFrmDataGrpDto.setBookmarkDtoList(tempSStndBookMarkList);

			formDataGroupList.add(tempProgramFrmDataGrpDto);
		}

		// create form group ccn04o04 and set prefill data

		for (PersonRoleDto personRoleDto : centralRegistryDto.getSpPersonRoleList()) {
			FormDataGroupDto tempProgramFrmDataGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_SST_ID_CASE,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> tempSStndBookMarkList = new ArrayList<BookmarkDto>();
			BookmarkDto caseIdAddBookmark = createBookmark(BookmarkConstants.SST_ID_CASE, personRoleDto.getIdCase());
			tempSStndBookMarkList.add(caseIdAddBookmark);
			tempProgramFrmDataGrpDto.setBookmarkDtoList(tempSStndBookMarkList);

			formDataGroupList.add(tempProgramFrmDataGrpDto);
		}

		// create form group ccn04o05 and set prefill data

		for (PersonRoleDto personRoleDto : centralRegistryDto.getVictimPersonRolesList()) {
			FormDataGroupDto tempProgramFrmDataGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_DSGNTD_PRP_PROG,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> tempDsgnBookMarkList = new ArrayList<BookmarkDto>();
			BookmarkDto cdStageProgram = createBookmarkWithCodesTable(BookmarkConstants.DSGNTD_PRP_PROG,
					personRoleDto.getCdStageProgram(), CodesConstant.CPGRMSFM);
			tempDsgnBookMarkList.add(cdStageProgram);
			tempProgramFrmDataGrpDto.setBookmarkDtoList(tempDsgnBookMarkList);

			formDataGroupList.add(tempProgramFrmDataGrpDto);
		}

		// create form group ccn04o06 and set prefill data

		for (PersonRoleDto personRoleDto : centralRegistryDto.getVictimPersonRolesList()) {
			FormDataGroupDto tempProgramFrmDataGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_DSG_ID_CASE,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> tempSStndBookMarkList = new ArrayList<BookmarkDto>();
			BookmarkDto caseIdAddBookmark = createBookmark(BookmarkConstants.DSG_ID_CASE, personRoleDto.getIdCase());
			tempSStndBookMarkList.add(caseIdAddBookmark);
			tempProgramFrmDataGrpDto.setBookmarkDtoList(tempSStndBookMarkList);

			formDataGroupList.add(tempProgramFrmDataGrpDto);
		}

		// create form group ccn04o07 and set prefill data

		for (PersonRoleDto personRoleDto : centralRegistryDto.getPersonRolesForOpenARInList()) {
			FormDataGroupDto tempProgramFrmDataGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_AR_PROGRAM,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> tempDsgnBookMarkList = new ArrayList<BookmarkDto>();
			BookmarkDto cdStageProgram = createBookmarkWithCodesTable(BookmarkConstants.AR_PROGRAM,
					personRoleDto.getCdStageProgram(), CodesConstant.CPGRMSFM);
			tempDsgnBookMarkList.add(cdStageProgram);
			tempProgramFrmDataGrpDto.setBookmarkDtoList(tempDsgnBookMarkList);
			formDataGroupList.add(tempProgramFrmDataGrpDto);
		}

		// create from group ccn04o08 and set prefill data

		for (PersonRoleDto personRoleDto : centralRegistryDto.getPersonRolesForOpenARInList()) {
			FormDataGroupDto tempCaseIdFrmDataGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_AR_CASE_ID,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> tempCaseIdBookMarkList = new ArrayList<BookmarkDto>();
			BookmarkDto idCaseAddBookmark = createBookmark(BookmarkConstants.AR_CASE_ID, personRoleDto.getIdCase());
			tempCaseIdBookMarkList.add(idCaseAddBookmark);
			tempCaseIdFrmDataGrpDto.setBookmarkDtoList(tempCaseIdBookMarkList);
			formDataGroupList.add(tempCaseIdFrmDataGrpDto);
		}

		/**
		 * Populating the non form group data into prefill data
		 */
		List<BookmarkDto> bookmarkNonFrmGrpList = new ArrayList<BookmarkDto>();

		// Populate txtFormX4 value from DAM CSES96D
		BookmarkDto bookmarkTxtFormX4 = createBookmark(BookmarkConstants.DSGNTD_PRP_CHCK,
				centralRegistryDto.getPersonDto().getTxtFormX4());
		bookmarkNonFrmGrpList.add(bookmarkTxtFormX4);

		// Populate dtPersonBirth value from DAM CSES96D
		BookmarkDto bookmarkDtPersonBirth = createBookmark(BookmarkConstants.INDIVIDUALS_DOB,
				TypeConvUtil.formDateFormat(centralRegistryDto.getPersonDto().getDtPersonBirth()));
		bookmarkNonFrmGrpList.add(bookmarkDtPersonBirth);

		// Populate sysDate value from DAM CSES96D
		BookmarkDto bookmarkSysDate = createBookmark(BookmarkConstants.SYS_DT,
				TypeConvUtil.formDateFormat(centralRegistryDto.getPersonDto().getDtGenericSysDate()));
		bookmarkNonFrmGrpList.add(bookmarkSysDate);

		// Populate cdPersonSuffix value from DAM CSES96D
		BookmarkDto bookmarkCdPersonSuffix = createBookmarkWithCodesTable(BookmarkConstants.INDVDL_NM_SFFX,
				centralRegistryDto.getPersonDto().getCdPersonSuffix(), CodesConstant.CSUFFIX);
		bookmarkNonFrmGrpList.add(bookmarkCdPersonSuffix);

		// Populate nmPersonFirst value from DAM CSES96D
		BookmarkDto bookmarkCdPersonFirst = createBookmark(BookmarkConstants.INDVDL_NM_FRST,
				centralRegistryDto.getPersonDto().getNmPersonFirst());
		bookmarkNonFrmGrpList.add(bookmarkCdPersonFirst);

		// Populate nmPersonLast value from DAM CSES96D
		BookmarkDto bookmarkCdPersonLast = createBookmark(BookmarkConstants.INDVDL_NM_LST,
				centralRegistryDto.getPersonDto().getNmPersonLast());
		bookmarkNonFrmGrpList.add(bookmarkCdPersonLast);

		// Populate nmPersonMiddle value from DAM CSES96D
		BookmarkDto bookmarkCdPersonMiddle = createBookmark(BookmarkConstants.INDVDL_NM_MDDL,
				centralRegistryDto.getPersonDto().getNmPersonMiddle());
		bookmarkNonFrmGrpList.add(bookmarkCdPersonMiddle);

		// Populate nbrPersonIdNumber value from DAM CSES96D
		BookmarkDto bookmarkNbrPersonIdNumber = createBookmark(BookmarkConstants.INDIVIDUALS_SSN,
				centralRegistryDto.getPersonDto().getNbrPersonIdNumber());
		bookmarkNonFrmGrpList.add(bookmarkNbrPersonIdNumber);

		// Populate txtFormAR value from DAM CSES96D
		BookmarkDto bookmarkTxtFormAR = createBookmark(BookmarkConstants.OPN_AR_CHCK,
				centralRegistryDto.getPersonDto().getTxtFormAR());
		bookmarkNonFrmGrpList.add(bookmarkTxtFormAR);

		// Populate txtFormX1 value from DAM CSES96D
		BookmarkDto bookmarkTxtFormX1 = createBookmark(BookmarkConstants.NO_CHCK,
				centralRegistryDto.getPersonDto().getTxtFormX1());
		bookmarkNonFrmGrpList.add(bookmarkTxtFormX1);

		// Populate txtFormX2 value from DAM CSES96D
		BookmarkDto bookmarkTxtFormX2 = createBookmark(BookmarkConstants.OPN_INV_CHCK,
				centralRegistryDto.getPersonDto().getTxtFormX2());
		bookmarkNonFrmGrpList.add(bookmarkTxtFormX2);

		// Populate txtFormX3 value from DAM CSES96D
		BookmarkDto bookmarkTxtFormX3 = createBookmark(BookmarkConstants.SSTND_PRP_CHCK,
				centralRegistryDto.getPersonDto().getTxtFormX3());
		bookmarkNonFrmGrpList.add(bookmarkTxtFormX3);

		// Populate idPerson value from DAM CSES96D
		BookmarkDto bookmarkIdPerson = createBookmark(BookmarkConstants.INDIVIDUALS_ID,
				centralRegistryDto.getPersonDto().getIdPerson());
		bookmarkNonFrmGrpList.add(bookmarkIdPerson);

		PreFillDataServiceDto preFillData = new PreFillDataServiceDto();
		preFillData.setFormDataGroupList(formDataGroupList);
		preFillData.setBookmarkDtoList(bookmarkNonFrmGrpList);
		return preFillData;

	}

}
