package us.tx.state.dfps.service.forms.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import us.tx.state.dfps.common.dto.FamAssmtDto;
import us.tx.state.dfps.common.dto.FamAssmtFactorDto;
import us.tx.state.dfps.common.dto.FamilyPlanAssmtDto;
import us.tx.state.dfps.common.dto.GenericCaseInfoDto;
import us.tx.state.dfps.common.dto.WorkerDetailDto;
import us.tx.state.dfps.service.common.BookmarkConstants;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.FormConstants;
import us.tx.state.dfps.service.common.FormGroupsConstants;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.cvs.dto.NameStagePersonLinkPersonOutDto;
import us.tx.state.dfps.service.forms.dto.BookmarkDto;
import us.tx.state.dfps.service.forms.dto.FormDataGroupDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.workload.dto.StagePersonDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:DisasterPlanFormPrefillData will implemented returnPrefillData
 * operation defined in DocumentServiceUtil Interface to populate the prefill
 * data for form cfsd0100. March 9, 2018- 2:04:05 PM © 2017 Texas Department of
 * Family and Protective Services
 */
@Component
public class FamilyAssmtFormPrefillData extends DocumentServiceUtil {

	@SuppressWarnings("unchecked")
	@Override
	public PreFillDataServiceDto returnPrefillData(Object parentDtoobj) {

		FamilyPlanAssmtDto familyPlanAssmtDto = (FamilyPlanAssmtDto) parentDtoobj;

		if (null == familyPlanAssmtDto.getFamAssmtDto()) {
			familyPlanAssmtDto.setFamAssmtDto(new FamAssmtDto());
		}

		if (null == familyPlanAssmtDto.getFamAssmtFactorList()) {
			familyPlanAssmtDto.setFamAssmtFactorList(new ArrayList[FormConstants.NUMBER_OF_SUBJECT]);
		}
		if (null == familyPlanAssmtDto.getGenericCaseInfoDto()) {
			familyPlanAssmtDto.setGenericCaseInfoDto(new GenericCaseInfoDto());
		}

		if (null == familyPlanAssmtDto.getStagePersonDto()) {
			familyPlanAssmtDto.setStagePersonDto(new StagePersonDto());
		}

		if (null == familyPlanAssmtDto.getWorkerDetailDto()) {
			familyPlanAssmtDto.setWorkerDetailDto(new WorkerDetailDto());
		}

		if (null == familyPlanAssmtDto.getStagePersonLinkList()) {
			familyPlanAssmtDto.setStagePersonLinkList(new ArrayList<NameStagePersonLinkPersonOutDto>());
		}

		List<FormDataGroupDto> formDataGroupList = new ArrayList<FormDataGroupDto>();

		/**
		 * Checking the bCdPersonChar equals C. if equals we set the prefill
		 * data for group cfsd0101
		 */

		for (NameStagePersonLinkPersonOutDto personLink : familyPlanAssmtDto.getStagePersonLinkList()) {
			if (personLink.getBCdPersonChar().equals(FormConstants.C)) {
				FormDataGroupDto tempIndChildDataGrpDto = createFormDataGroup(
						FormGroupsConstants.TMPLAT_INDIVIDUAL_CHILD, FormConstants.EMPTY_STRING);

				List<BookmarkDto> tempWrkBookmarkList = new ArrayList<BookmarkDto>();
				BookmarkDto personAge = createBookmark(BookmarkConstants.CHILD_CHILD_AGE,
						personLink.getLNbrPersonAge());
				BookmarkDto nameSuffix = createBookmarkWithCodesTable(BookmarkConstants.CHILD_NAME_SUFFIX,
						personLink.getCdNameSuffix(), CodesConstant.CSUFFIX2);

				BookmarkDto nameFirst = createBookmark(BookmarkConstants.CHILD_NAME_FIRST, personLink.getNameFirst());
				BookmarkDto nameLast = createBookmark(BookmarkConstants.CHILD_NAME_LAST, personLink.getNameLast());
				BookmarkDto nameMiddle = createBookmark(BookmarkConstants.CHILD_NAME_MIDDLE,
						personLink.getNameMiddle());

				tempWrkBookmarkList.add(personAge);
				tempWrkBookmarkList.add(nameSuffix);
				tempWrkBookmarkList.add(nameFirst);
				tempWrkBookmarkList.add(nameLast);
				tempWrkBookmarkList.add(nameMiddle);
				tempIndChildDataGrpDto.setBookmarkDtoList(tempWrkBookmarkList);

				List<FormDataGroupDto> tempGroup = new ArrayList<FormDataGroupDto>();

				// check if cdNameSuffix is not null, if not null create group
				// cfzco00 and set
				// prefill data
				if (null != personLink.getCdNameSuffix()) {
					FormDataGroupDto tempCommaDataGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_COMMA,
							FormGroupsConstants.TMPLAT_INDIVIDUAL_CHILD);
					tempGroup.add(tempCommaDataGrpDto);
				}

				// create group cfsd0105 and set prefill data
				for (FamAssmtFactorDto famAssmtFactorDto : familyPlanAssmtDto
						.getFamAssmtFactorList()[FormConstants.CHILD_VALUE]) {
					if (famAssmtFactorDto.getIdFamAssmtPrincipal().equals(personLink.getIdPerson())
							&& famAssmtFactorDto.getCdFamAssmtCategory().equals(FormConstants.CS)) {
						FormDataGroupDto RsrcDataGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_STRENGTH_RSRC,
								FormGroupsConstants.TMPLAT_INDIVIDUAL_CHILD);
						List<BookmarkDto> RsrcBookmarkList = new ArrayList<BookmarkDto>();
						BookmarkDto cdFamAssmtFactr = createBookmarkWithCodesTable(BookmarkConstants.ABUSE_STRNGTH_RSRC,
								famAssmtFactorDto.getCdFamAssmtFactr(), CodesConstant.CFMASFCT);
						RsrcBookmarkList.add(cdFamAssmtFactr);
						RsrcDataGrpDto.setBookmarkDtoList(RsrcBookmarkList);
						tempGroup.add(RsrcDataGrpDto);

					}
				}

				// create group cfsd0109 and set prefill data
				for (FamAssmtFactorDto famAssmtFactorDto : familyPlanAssmtDto
						.getFamAssmtFactorList()[FormConstants.CHILD_VALUE]) {
					if (famAssmtFactorDto.getIdFamAssmtPrincipal().equals(personLink.getIdPerson())
							&& famAssmtFactorDto.getCdFamAssmtCategory().equals(FormConstants.CV)) {
						FormDataGroupDto parViewDataGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_PARENT_VIEW,
								FormGroupsConstants.TMPLAT_INDIVIDUAL_CHILD);
						List<BookmarkDto> parViewBookmarkList = new ArrayList<BookmarkDto>();
						BookmarkDto cdFamAssmtFactr = createBookmarkWithCodesTable(BookmarkConstants.PARENTAL_VIEW,
								famAssmtFactorDto.getCdFamAssmtFactr(), CodesConstant.CFMASFCT);
						parViewBookmarkList.add(cdFamAssmtFactr);
						parViewDataGrpDto.setBookmarkDtoList(parViewBookmarkList);
						tempGroup.add(parViewDataGrpDto);

					}
				}

				// create group cfsd0110 and set prefill data
				for (FamAssmtFactorDto famAssmtFactorDto : familyPlanAssmtDto
						.getFamAssmtFactorList()[FormConstants.CHILD_VALUE]) {
					if (famAssmtFactorDto.getIdFamAssmtPrincipal().equals(personLink.getIdPerson())
							&& famAssmtFactorDto.getCdFamAssmtCategory().equals(FormConstants.CC)) {
						FormDataGroupDto chrtDataGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_CHILD_CHRCTR,
								FormGroupsConstants.TMPLAT_INDIVIDUAL_CHILD);
						List<BookmarkDto> chrBookmarkList = new ArrayList<BookmarkDto>();
						BookmarkDto cdFamAssmtFactr = createBookmarkWithCodesTable(
								BookmarkConstants.CHILD_CHARACTERISTIC, famAssmtFactorDto.getCdFamAssmtFactr(),
								CodesConstant.CFMASFCT);
						chrBookmarkList.add(cdFamAssmtFactr);
						chrtDataGrpDto.setBookmarkDtoList(chrBookmarkList);
						tempGroup.add(chrtDataGrpDto);

					}
				}

				tempIndChildDataGrpDto.setFormDataGroupList(tempGroup);
				formDataGroupList.add(tempIndChildDataGrpDto);

			} // create group cfsd0102 and set prefill data
			else if (personLink.getBCdPersonChar().equals(FormConstants.P)) {
				FormDataGroupDto tempIndParentDataGrpDto = createFormDataGroup(
						FormGroupsConstants.TMPLAT_INDIVIDUAL_PARENT, FormConstants.EMPTY_STRING);

				List<BookmarkDto> indParanetBookmarkList = new ArrayList<BookmarkDto>();
				BookmarkDto personAge = createBookmarkWithCodesTable(BookmarkConstants.PARENT_RELTNSP_TO_CHILD,
						personLink.getCdStagePersRelInt(), CodesConstant.CRPTRINT);
				BookmarkDto nameSuffix = createBookmarkWithCodesTable(BookmarkConstants.ADULT_NAME_SUFFIX,
						personLink.getCdNameSuffix(), CodesConstant.CSUFFIX2);

				BookmarkDto nameFirst = createBookmark(BookmarkConstants.ADULT_NAME_FIRST, personLink.getNameFirst());
				BookmarkDto nameLast = createBookmark(BookmarkConstants.ADULT_NAME_LAST, personLink.getNameLast());
				BookmarkDto nameMiddle = createBookmark(BookmarkConstants.ADULT_NAME_MIDDLE,
						personLink.getNameMiddle());

				indParanetBookmarkList.add(personAge);
				indParanetBookmarkList.add(nameSuffix);
				indParanetBookmarkList.add(nameFirst);
				indParanetBookmarkList.add(nameLast);
				indParanetBookmarkList.add(nameMiddle);
				tempIndParentDataGrpDto.setBookmarkDtoList(indParanetBookmarkList);

				List<FormDataGroupDto> tempGroup = new ArrayList<FormDataGroupDto>();

				// check if cdNameSuffix is not null, if not null create group
				// cfzco00 and set
				// prefill data
				if (null != personLink.getCdNameSuffix()) {
					FormDataGroupDto tempCommaDataGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_COMMA,
							FormGroupsConstants.TMPLAT_INDIVIDUAL_PARENT);
					tempGroup.add(tempCommaDataGrpDto);
				}

				// create group cfsd0112 and set prefill data
				for (FamAssmtFactorDto famAssmtFactorDto : familyPlanAssmtDto
						.getFamAssmtFactorList()[FormConstants.ADULT_VALUE]) {
					if (famAssmtFactorDto.getIdFamAssmtPrincipal().equals(personLink.getIdPerson())
							&& famAssmtFactorDto.getCdFamAssmtCategory().equals(FormConstants.AH)) {
						FormDataGroupDto parentHistDataGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_HISTORY,
								FormGroupsConstants.TMPLAT_INDIVIDUAL_PARENT);
						List<BookmarkDto> parentHistBookmarkList = new ArrayList<BookmarkDto>();
						BookmarkDto cdFamAssmtFactr = createBookmarkWithCodesTable(BookmarkConstants.PARENT_HISTORY,
								famAssmtFactorDto.getCdFamAssmtFactr(), CodesConstant.CFMASFCT);
						parentHistBookmarkList.add(cdFamAssmtFactr);
						parentHistDataGrpDto.setBookmarkDtoList(parentHistBookmarkList);
						tempGroup.add(parentHistDataGrpDto);

					}
				}

				// create group cfsd0105 and set prefill data
				for (FamAssmtFactorDto famAssmtFactorDto : familyPlanAssmtDto
						.getFamAssmtFactorList()[FormConstants.ADULT_VALUE]) {
					if (famAssmtFactorDto.getIdFamAssmtPrincipal().equals(personLink.getIdPerson())
							&& famAssmtFactorDto.getCdFamAssmtCategory().equals(FormConstants.AS)) {
						FormDataGroupDto rsrcDataGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_STRENGTH_RSRC,
								FormGroupsConstants.TMPLAT_INDIVIDUAL_PARENT);
						List<BookmarkDto> rsrcBookmarkList = new ArrayList<BookmarkDto>();
						BookmarkDto cdFamAssmtFactr = createBookmarkWithCodesTable(BookmarkConstants.ABUSE_STRNGTH_RSRC,
								famAssmtFactorDto.getCdFamAssmtFactr(), CodesConstant.CFMASFCT);
						rsrcBookmarkList.add(cdFamAssmtFactr);
						rsrcDataGrpDto.setBookmarkDtoList(rsrcBookmarkList);
						tempGroup.add(rsrcDataGrpDto);

					}
				}

				// create group cfsd0111 and set prefill data
				for (FamAssmtFactorDto famAssmtFactorDto : familyPlanAssmtDto
						.getFamAssmtFactorList()[FormConstants.ADULT_VALUE]) {
					if (famAssmtFactorDto.getIdFamAssmtPrincipal().equals(personLink.getIdPerson())
							&& famAssmtFactorDto.getCdFamAssmtCategory().equals(FormConstants.AB)) {
						FormDataGroupDto bhvIssueDataGrpDto = createFormDataGroup(
								FormGroupsConstants.TMPLAT_BEHAV_ISSUES, FormGroupsConstants.TMPLAT_INDIVIDUAL_PARENT);
						List<BookmarkDto> bhvIssueBookmarkList = new ArrayList<BookmarkDto>();
						BookmarkDto cdFamAssmtFactr = createBookmarkWithCodesTable(BookmarkConstants.BEHAVIOR_ISSUES,
								famAssmtFactorDto.getCdFamAssmtFactr(), CodesConstant.CFMASFCT);
						bhvIssueBookmarkList.add(cdFamAssmtFactr);
						bhvIssueDataGrpDto.setBookmarkDtoList(bhvIssueBookmarkList);
						tempGroup.add(bhvIssueDataGrpDto);

					}
				}

				// create group cfsd0113 and set prefill data
				for (FamAssmtFactorDto famAssmtFactorDto : familyPlanAssmtDto
						.getFamAssmtFactorList()[FormConstants.ADULT_VALUE]) {
					if (famAssmtFactorDto.getIdFamAssmtPrincipal().equals(personLink.getIdPerson())
							&& famAssmtFactorDto.getCdFamAssmtCategory().equals(FormConstants.AP)) {
						FormDataGroupDto parentIssueDataGrpDto = createFormDataGroup(
								FormGroupsConstants.TMPLAT_PARENTING_ISSUES,
								FormGroupsConstants.TMPLAT_INDIVIDUAL_PARENT);
						List<BookmarkDto> parentIssueBookmarkList = new ArrayList<BookmarkDto>();
						BookmarkDto cdFamAssmtFactr = createBookmarkWithCodesTable(BookmarkConstants.PARENTING_ISSUES,
								famAssmtFactorDto.getCdFamAssmtFactr(), CodesConstant.CFMASFCT);
						parentIssueBookmarkList.add(cdFamAssmtFactr);
						parentIssueDataGrpDto.setBookmarkDtoList(parentIssueBookmarkList);
						tempGroup.add(parentIssueDataGrpDto);

					}
				}

				tempIndParentDataGrpDto.setFormDataGroupList(tempGroup);
				formDataGroupList.add(tempIndParentDataGrpDto);
			}
		}

		// check if cdNameSuffix is not null, if not null create group cfzco00
		// and set
		// prefill data
		if (null != familyPlanAssmtDto.getWorkerDetailDto().getCdNameSuffix()) {
			FormDataGroupDto tempCommaDataGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_COMMA,
					FormConstants.EMPTY_STRING);
			formDataGroupList.add(tempCommaDataGrpDto);
		}

		// create group cfsd0103 and set prefill data

		for (int i = 0; i < FormConstants.NUMBER_OF_SUBJECT; i++) {
			for (int j = 0; j < familyPlanAssmtDto.getFamAssmtFactorList()[i].size(); j++) {
				if (familyPlanAssmtDto.getFamAssmtFactorList()[i].get(j).getCdFamAssmtCategory()
						.equals(FormConstants.IM)) {
					FormDataGroupDto tempMotivationDataGrpDto = createFormDataGroup(
							FormGroupsConstants.TMPLAT_MOTIVATION, FormConstants.EMPTY_STRING);
					List<BookmarkDto> tempMotivationBookmarkList = new ArrayList<BookmarkDto>();
					BookmarkDto cdFamAssmtFactr = createBookmarkWithCodesTable(BookmarkConstants.ABUSE_MOTIVATION,
							familyPlanAssmtDto.getFamAssmtFactorList()[i].get(j).getCdFamAssmtFactr(),
							CodesConstant.CFMASFCT);
					tempMotivationBookmarkList.add(cdFamAssmtFactr);
					tempMotivationDataGrpDto.setBookmarkDtoList(tempMotivationBookmarkList);
					formDataGroupList.add(tempMotivationDataGrpDto);

				}
			}
		}

		// create group cfsd0104 and set prefill data

		for (int i = 0; i < FormConstants.NUMBER_OF_SUBJECT; i++) {
			for (int j = 0; j < familyPlanAssmtDto.getFamAssmtFactorList()[i].size(); j++) {
				if (familyPlanAssmtDto.getFamAssmtFactorList()[i].get(j).getCdFamAssmtCategory()
						.equals(FormConstants.IC)) {
					FormDataGroupDto chrctrDataGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_CHRCTR,
							FormConstants.EMPTY_STRING);
					List<BookmarkDto> chrctrBookmarkList = new ArrayList<BookmarkDto>();
					BookmarkDto cdFamAssmtFactr = createBookmarkWithCodesTable(BookmarkConstants.ABUSE_CHRCTR,
							familyPlanAssmtDto.getFamAssmtFactorList()[i].get(j).getCdFamAssmtFactr(),
							CodesConstant.CFMASFCT);
					chrctrBookmarkList.add(cdFamAssmtFactr);
					chrctrDataGrpDto.setBookmarkDtoList(chrctrBookmarkList);
					formDataGroupList.add(chrctrDataGrpDto);

				}
			}
		}

		// create group cfsd0105 and set prefill data
		for (int i = 0; i < FormConstants.NUMBER_OF_SUBJECT; i++) {
			for (int j = 0; j < familyPlanAssmtDto.getFamAssmtFactorList()[i].size(); j++) {
				if (familyPlanAssmtDto.getFamAssmtFactorList()[i].get(j).getCdFamAssmtCategory()
						.equals(FormConstants.IS)) {
					FormDataGroupDto strengthRsrcDataGrpDto = createFormDataGroup(
							FormGroupsConstants.TMPLAT_STRENGTH_RSRC_ABUSE, FormConstants.EMPTY_STRING);
					List<BookmarkDto> strengthRsrcBookmarkList = new ArrayList<BookmarkDto>();
					BookmarkDto cdFamAssmtFactr = createBookmarkWithCodesTable(BookmarkConstants.ABUSE_STRNGTH_RSRC,
							familyPlanAssmtDto.getFamAssmtFactorList()[i].get(j).getCdFamAssmtFactr(),
							CodesConstant.CFMASFCT);
					strengthRsrcBookmarkList.add(cdFamAssmtFactr);
					strengthRsrcDataGrpDto.setBookmarkDtoList(strengthRsrcBookmarkList);
					formDataGroupList.add(strengthRsrcDataGrpDto);

				}
			}
		}

		// create group cfsd0106 and set prefill data
		for (int i = 0; i < FormConstants.NUMBER_OF_SUBJECT; i++) {
			for (int j = 0; j < familyPlanAssmtDto.getFamAssmtFactorList()[i].size(); j++) {
				if (familyPlanAssmtDto.getFamAssmtFactorList()[i].get(j).getCdFamAssmtCategory()
						.equals(FormConstants.FC)) {
					FormDataGroupDto famCircumstanceDataGrpDto = createFormDataGroup(
							FormGroupsConstants.TMPLAT_EXTRA_CIRCUMSTANCES, FormConstants.EMPTY_STRING);
					List<BookmarkDto> famCircumstanceBookmarkList = new ArrayList<BookmarkDto>();
					BookmarkDto cdFamAssmtFactr = createBookmarkWithCodesTable(BookmarkConstants.FAMILY_CIRCUMSTANCES,
							familyPlanAssmtDto.getFamAssmtFactorList()[i].get(j).getCdFamAssmtFactr(),
							CodesConstant.CFMASFCT);
					famCircumstanceBookmarkList.add(cdFamAssmtFactr);
					famCircumstanceDataGrpDto.setBookmarkDtoList(famCircumstanceBookmarkList);
					formDataGroupList.add(famCircumstanceDataGrpDto);

				}
			}
		}

		// create group cfsd0107 and set prefill data
		for (int i = 0; i < FormConstants.NUMBER_OF_SUBJECT; i++) {
			for (int j = 0; j < familyPlanAssmtDto.getFamAssmtFactorList()[i].size(); j++) {
				if (familyPlanAssmtDto.getFamAssmtFactorList()[i].get(j).getCdFamAssmtCategory()
						.equals(FormConstants.FF)) {
					FormDataGroupDto funcDataGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_FUNCTIONING,
							FormConstants.EMPTY_STRING);
					List<BookmarkDto> funcBookmarkList = new ArrayList<BookmarkDto>();
					BookmarkDto cdFamAssmtFactr = createBookmarkWithCodesTable(BookmarkConstants.FAMILY_FUNCTIONING,
							familyPlanAssmtDto.getFamAssmtFactorList()[i].get(j).getCdFamAssmtFactr(),
							CodesConstant.CFMASFCT);
					funcBookmarkList.add(cdFamAssmtFactr);
					funcDataGrpDto.setBookmarkDtoList(funcBookmarkList);
					formDataGroupList.add(funcDataGrpDto);

				}
			}
		}

		// create group cfsd0108 and set prefill data
		for (int i = 0; i < FormConstants.NUMBER_OF_SUBJECT; i++) {
			for (int j = 0; j < familyPlanAssmtDto.getFamAssmtFactorList()[i].size(); j++) {
				if (familyPlanAssmtDto.getFamAssmtFactorList()[i].get(j).getCdFamAssmtCategory()
						.equals(FormConstants.FS)) {
					FormDataGroupDto strRsrcDataGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_FAMILY_STR_RSRC,
							FormConstants.EMPTY_STRING);
					List<BookmarkDto> strRsrcBookmarkList = new ArrayList<BookmarkDto>();
					BookmarkDto cdFamAssmtFactr = createBookmarkWithCodesTable(BookmarkConstants.ABUSE_STRNGTH_RSRC,
							familyPlanAssmtDto.getFamAssmtFactorList()[i].get(j).getCdFamAssmtFactr(),
							CodesConstant.CFMASFCT);
					strRsrcBookmarkList.add(cdFamAssmtFactr);
					strRsrcDataGrpDto.setBookmarkDtoList(strRsrcBookmarkList);
					formDataGroupList.add(strRsrcDataGrpDto);

				}
			}
		}

		/**
		 * Populating the non form group data into prefill data
		 */
		List<BookmarkDto> bookmarkNonFrmGrpList = new ArrayList<BookmarkDto>();
		// Populate nmStage value from DAM CSEC02D
		BookmarkDto bookmarkNmCase = createBookmark(BookmarkConstants.TITLE_CASE_NAME,
				familyPlanAssmtDto.getGenericCaseInfoDto().getNmStage());
		bookmarkNonFrmGrpList.add(bookmarkNmCase);

		// Populate idCase value from DAM CSEC02D
		BookmarkDto bookmarkIdCase = createBookmark(BookmarkConstants.TITLE_CASE_NUMBER,
				familyPlanAssmtDto.getGenericCaseInfoDto().getIdCase());
		bookmarkNonFrmGrpList.add(bookmarkIdCase);

		// Populate idCase value from DAM CSEC01D
		BookmarkDto bookmarkWorkerNameSuffix = createBookmarkWithCodesTable(BookmarkConstants.WORKER_SUFFIX,
				familyPlanAssmtDto.getWorkerDetailDto().getCdNameSuffix(), CodesConstant.CSUFFIX2);
		bookmarkNonFrmGrpList.add(bookmarkWorkerNameSuffix);

		// Populate idCase value from DAM CSEC01D
		BookmarkDto bookmarkWorkerNameFirst = createBookmark(BookmarkConstants.WORKER_FIRST_NAME,
				familyPlanAssmtDto.getWorkerDetailDto().getNmNameFirst());
		bookmarkNonFrmGrpList.add(bookmarkWorkerNameFirst);

		// Populate idCase value from DAM CSEC01D
		BookmarkDto bookmarkWorkerNameLast = createBookmark(BookmarkConstants.WORKER_LAST_NAME,
				familyPlanAssmtDto.getWorkerDetailDto().getNmNameLast());
		bookmarkNonFrmGrpList.add(bookmarkWorkerNameLast);

		// Populate idCase value from DAM CSEC01D
		BookmarkDto bookmarkWorkerNameMiddle = createBookmark(BookmarkConstants.WORKER_MIDDLE_NAME,
				familyPlanAssmtDto.getWorkerDetailDto().getNmNameMiddle());
		bookmarkNonFrmGrpList.add(bookmarkWorkerNameMiddle);

		// Populate DtFamAssmtComplt value from DAM CSES05D
		BookmarkDto bookmarkDtFamAssmtComplt = createBookmark(BookmarkConstants.SUM_DATE_COMPLT,
				TypeConvUtil.formDateFormat(familyPlanAssmtDto.getFamAssmtDto().getDtFamAssmtComplt()));
		bookmarkNonFrmGrpList.add(bookmarkDtFamAssmtComplt);

		PreFillDataServiceDto preFillData = new PreFillDataServiceDto();
		preFillData.setFormDataGroupList(formDataGroupList);
		preFillData.setBookmarkDtoList(bookmarkNonFrmGrpList);
		return preFillData;

	}

}
