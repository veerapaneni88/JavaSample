package us.tx.state.dfps.service.forms.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.dto.ChildPlanRecordDto;
import us.tx.state.dfps.common.dto.HseghDto;
import us.tx.state.dfps.common.dto.PersonOnHseghDto;
import us.tx.state.dfps.common.dto.ServicePlanDto;
import us.tx.state.dfps.service.admin.dto.LegalStatusPersonMaxStatusDtOutDto;
import us.tx.state.dfps.service.common.BookmarkConstants;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.FormConstants;
import us.tx.state.dfps.service.common.FormGroupsConstants;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.conservatorship.dto.CharacteristicsDto;
import us.tx.state.dfps.service.conservatorship.dto.CnsrvtrshpRemovalDto;
import us.tx.state.dfps.service.forms.dto.BlobDataDto;
import us.tx.state.dfps.service.forms.dto.BookmarkDto;
import us.tx.state.dfps.service.forms.dto.FormDataGroupDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.person.dto.PersonDtlDto;
import us.tx.state.dfps.service.person.dto.ServicePackageDtlDto;
import us.tx.state.dfps.service.placement.dto.AllegationCpsInvstDtlDto;
import us.tx.state.dfps.service.placement.dto.NameDetailDto;
import us.tx.state.dfps.service.placement.dto.PersonLocDto;
import us.tx.state.dfps.service.placement.dto.StagePersonLinkCaseDto;

import static us.tx.state.dfps.service.common.ServiceConstants.SERVICE_PACKAGE_RECOMMENDED;
import static us.tx.state.dfps.service.common.ServiceConstants.SERVICE_PACKAGE_SELECTED;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:DisasterPlanFormPrefillData will implemented returnPrefillData
 * operation defined in DocumentServiceUtil Interface to populate the prefill
 * data for form CSC32O00. April 02, 2018- 2:04:05 PM © 2017 Texas Department of
 * Family and Protective Services
 */
@Component
public class HseghFormPrefillData extends DocumentServiceUtil {

	@Override
	public PreFillDataServiceDto returnPrefillData(Object parentDtoobj) {
		HseghDto hseghDto = (HseghDto) parentDtoobj;
		if (ObjectUtils.isEmpty(hseghDto.getAllegationCpsInvsDtlDtoList())) {
			hseghDto.setAllegationCpsInvsDtlDtoList(new ArrayList<AllegationCpsInvstDtlDto>());
		}
		if (ObjectUtils.isEmpty(hseghDto.getAllPeopleOnHseghDtoList())) {
			hseghDto.setAllPeopleOnHseghDtoList(new ArrayList<PersonOnHseghDto>());
		}

		if (ObjectUtils.isEmpty(hseghDto.getCharacteristicDtoList())) {
			hseghDto.setCharacteristicDtoList(new ArrayList<CharacteristicsDto>());
		}

		if (ObjectUtils.isEmpty(hseghDto.getChildPlanRecordDtoList())) {
			hseghDto.setChildPlanRecordDtoList(new ArrayList<ChildPlanRecordDto>());
		}

		if (ObjectUtils.isEmpty(hseghDto.getCnsrvtrshpRemovalDtoList())) {
			hseghDto.setCnsrvtrshpRemovalDtoList(new ArrayList<CnsrvtrshpRemovalDto>());
		}

		if (ObjectUtils.isEmpty(hseghDto.getCnsrvtrshpRemovalReasonDtoList())) {
			hseghDto.setCnsrvtrshpRemovalReasonDtoList(new ArrayList<CnsrvtrshpRemovalDto>());

		}

		if (ObjectUtils.isEmpty(hseghDto.getLegalStatusPersonMaxStatusList())) {
			hseghDto.setLegalStatusPersonMaxStatusList(new ArrayList<LegalStatusPersonMaxStatusDtOutDto>());
		}

		if (ObjectUtils.isEmpty(hseghDto.getNameDetailDto())) {
			hseghDto.setNameDetailDto(new NameDetailDto());
		}
		if (ObjectUtils.isEmpty(hseghDto.getPersonDtlDtoList())) {
			hseghDto.setPersonDtlDtoList(new ArrayList<PersonDtlDto>());
		}

		if (ObjectUtils.isEmpty(hseghDto.getPersonLocDto())) {
			hseghDto.setPersonLocDto(new PersonLocDto());
		}

		if (ObjectUtils.isEmpty(hseghDto.getServicePlanDtoList())) {
			hseghDto.setServicePlanDtoList(new ArrayList<ServicePlanDto>());
		}

		if (ObjectUtils.isEmpty(hseghDto.getStagePersonLinkCaseDto())) {
			hseghDto.setStagePersonLinkCaseDto(new StagePersonLinkCaseDto());
		}

		if (ObjectUtils.isEmpty(hseghDto.getServicePackageDetails())) {
			hseghDto.setServicePackageDetails(new ArrayList<ServicePackageDtlDto>());
		}

		if (ObjectUtils.isEmpty(hseghDto.getServicePackages())) {
			hseghDto.setServicePackages(new ArrayList<ServicePackageDtlDto>());
		}
		List<FormDataGroupDto> formDataGroupList = new ArrayList<FormDataGroupDto>();

		// set prefill data for csc32o22 group

		for (AllegationCpsInvstDtlDto allegationCpsInvstDtlDto : hseghDto.getAllegationCpsInvsDtlDtoList()) {
			FormDataGroupDto allegationCpsInvFrmDataGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_ALLEGATIONS,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> tempAllegBookMarkList = new ArrayList<BookmarkDto>();

			BookmarkDto bookMarkAddCdAllegDisposition = createBookmarkWithCodesTable(
					BookmarkConstants.CD_ALLEG_DISPOSITION, allegationCpsInvstDtlDto.getCdAllegDisposition(),
					CodesConstant.CDISPSTN);
			BookmarkDto bookMarkAddCdAllegType = createBookmarkWithCodesTable(BookmarkConstants.CD_ALLEG_TYPE,
					allegationCpsInvstDtlDto.getCdAllegType(), CodesConstant.CABALTYP);
			tempAllegBookMarkList.add(bookMarkAddCdAllegDisposition);
			tempAllegBookMarkList.add(bookMarkAddCdAllegType);
			allegationCpsInvFrmDataGrpDto.setBookmarkDtoList(tempAllegBookMarkList);
			formDataGroupList.add(allegationCpsInvFrmDataGrpDto);

		}

		// set prefill data for group csc32o23

		for (CnsrvtrshpRemovalDto cnsrvtrshpRemovalDto : hseghDto.getCnsrvtrshpRemovalDtoList()) {
			FormDataGroupDto removalFrmDataGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_REMOVAL,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> rmvlDtBookMarkList = new ArrayList<BookmarkDto>();
			List<FormDataGroupDto> tempGroupDtoLi = new ArrayList<FormDataGroupDto>();
			BookmarkDto bookMarkAddRmvlDt = createBookmark(BookmarkConstants.DT_REMOVAL,
					TypeConvUtil.formDateFormat(cnsrvtrshpRemovalDto.getDtRemoval()));
			rmvlDtBookMarkList.add(bookMarkAddRmvlDt);
			removalFrmDataGrpDto.setBookmarkDtoList(rmvlDtBookMarkList);

			// set prefill data for group csc32o24
			for (CnsrvtrshpRemovalDto cnsrvtrshpRemovalReaDto : hseghDto.getCnsrvtrshpRemovalReasonDtoList()) {
				if (cnsrvtrshpRemovalReaDto.getIdRemovalEvent().intValue() == cnsrvtrshpRemovalDto.getIdRemovalEvent()
						.intValue()) {
					FormDataGroupDto removalReasonFrmDataGrpDto = createFormDataGroup(
							FormGroupsConstants.TMPLAT_CD_REMOVAL_REASON, "csc32o24");
					List<BookmarkDto> rmvlReasonBookMarkList = new ArrayList<BookmarkDto>();

					BookmarkDto bookMarkAddCdRmvlReason = createBookmarkWithCodesTable(
							BookmarkConstants.CD_REMOVAL_REASON, cnsrvtrshpRemovalReaDto.getCdRemovalReason(),
							CodesConstant.CREMFRHR);
					rmvlReasonBookMarkList.add(bookMarkAddCdRmvlReason);
					removalReasonFrmDataGrpDto.setBookmarkDtoList(rmvlReasonBookMarkList);
					tempGroupDtoLi.add(removalReasonFrmDataGrpDto);
				}
			}
			removalFrmDataGrpDto.setFormDataGroupList(tempGroupDtoLi);
			formDataGroupList.add(removalFrmDataGrpDto);
		}

		// set prefill data for group csc32o01

		for (PersonOnHseghDto allPeopleOnHseghDto : hseghDto.getAllPeopleOnHseghDtoList()) {
			if (!ObjectUtils.isEmpty(allPeopleOnHseghDto.getCdStagePersRelInt())
					&& (FormConstants.AB.equals(allPeopleOnHseghDto.getCdStagePersRelInt())
							|| FormConstants.PB.equals(allPeopleOnHseghDto.getCdStagePersRelInt())
							|| FormConstants.PD.equals(allPeopleOnHseghDto.getCdStagePersRelInt())
							|| FormConstants.ST.equals(allPeopleOnHseghDto.getCdStagePersRelInt())
							|| FormConstants.PA.equals(allPeopleOnHseghDto.getCdStagePersRelInt()))) {

				FormDataGroupDto famParentFrmDataGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_FAMILY_PARENTS,
						FormConstants.EMPTY_STRING);
				List<BookmarkDto> famParentBookMarkList = new ArrayList<BookmarkDto>();
				BookmarkDto bookMarkAddCdPersonSex = createBookmarkWithCodesTable(BookmarkConstants.CD_PERSON_SEX,
						allPeopleOnHseghDto.getCdPersonSex(), CodesConstant.CSEX);
				famParentBookMarkList.add(bookMarkAddCdPersonSex);
				BookmarkDto bookMarkAddDtPersonBirth = createBookmark(BookmarkConstants.DT_PERSON_BIRTH,
						TypeConvUtil.formDateFormat(allPeopleOnHseghDto.getDtPersonBirth()));

				famParentBookMarkList.add(bookMarkAddDtPersonBirth);
				BookmarkDto bookMarkAddDtPersonDeath = createBookmark(BookmarkConstants.DT_PERSON_DEATH,
						TypeConvUtil.formDateFormat(allPeopleOnHseghDto.getDtPersonDeath()));
				famParentBookMarkList.add(bookMarkAddDtPersonDeath);

				BookmarkDto bookMarkAddQtyPersonWeight = createBookmark(BookmarkConstants.QTY_PERSON_WEIGHT,
						TypeConvUtil.convertToTwoDecimalPlace(allPeopleOnHseghDto.getQtyPersonWeight() == null
								? ServiceConstants.ZERO_VAL : allPeopleOnHseghDto.getQtyPersonWeight()));
				famParentBookMarkList.add(bookMarkAddQtyPersonWeight);
				BookmarkDto bookMarkAddQtyPersonHeightFeet = createBookmark(BookmarkConstants.QTY_PERSON_HEIGHT_FEET,
						allPeopleOnHseghDto.getQtyPersonHeightFeet() == null ? ServiceConstants.ZERO_VAL
								: allPeopleOnHseghDto.getQtyPersonHeightFeet());
				famParentBookMarkList.add(bookMarkAddQtyPersonHeightFeet);
				BookmarkDto bookMarkAddQtyPersonHeightInches = createBookmark(
						BookmarkConstants.QTY_PERSON_HEIGHT_INCHES,
						allPeopleOnHseghDto.getQtyPersonHeightInches() == null ? ServiceConstants.ZERO_VAL
								: allPeopleOnHseghDto.getQtyPersonHeightInches());
				famParentBookMarkList.add(bookMarkAddQtyPersonHeightInches);
				BookmarkDto bookMarkaddCdNameSuffix = createBookmarkWithCodesTable(BookmarkConstants.CD_NAME_SUFFIX,
						allPeopleOnHseghDto.getCdNameSuffix(), CodesConstant.CSUFFIX2);
				famParentBookMarkList.add(bookMarkaddCdNameSuffix);
				BookmarkDto bookMarkAddCdPersonBirthCity = createBookmark(BookmarkConstants.CD_PERSON_BIRTH_CITY,
						allPeopleOnHseghDto.getCdPersonBirthCity());
				famParentBookMarkList.add(bookMarkAddCdPersonBirthCity);
				BookmarkDto bookMarkaddCdPersonBirthCountry = createBookmarkWithCodesTable(
						BookmarkConstants.TXT_PERSON_BIRTH_COUNTRY, allPeopleOnHseghDto.getCdPersonBirthCountry(),
						CodesConstant.CCOUNTRY);
				famParentBookMarkList.add(bookMarkaddCdPersonBirthCountry);
				BookmarkDto bookMarkaddCdPersonBirthState = createBookmarkWithCodesTable(
						BookmarkConstants.CD_PERSON_BIRTH_STATE, allPeopleOnHseghDto.getCdPersonBirthState(),
						CodesConstant.CSTATE);
				famParentBookMarkList.add(bookMarkaddCdPersonBirthState);
				BookmarkDto bookMarkaddCdPersonCitizenship = createBookmarkWithCodesTable(
						BookmarkConstants.CD_PERSON_CITIZENSHIP, allPeopleOnHseghDto.getCdPersonCitizenship(),
						CodesConstant.CCTZNSTA);
				famParentBookMarkList.add(bookMarkaddCdPersonCitizenship);
				BookmarkDto bookMarkaddCdPersonDeath = createBookmarkWithCodesTable(BookmarkConstants.CD_PERSON_DEATH,
						allPeopleOnHseghDto.getCdPersonDeath(), CodesConstant.CRSNFDTH);
				famParentBookMarkList.add(bookMarkaddCdPersonDeath);
				BookmarkDto bookMarkaddCdPersonEthnicGroup = createBookmarkWithCodesTable(
						BookmarkConstants.CD_PERSON_ETHNIC_GROUP, allPeopleOnHseghDto.getCdPersonEthnicGroup(),
						CodesConstant.CETHNIC);
				famParentBookMarkList.add(bookMarkaddCdPersonEthnicGroup);
				BookmarkDto bookMarkaddCdPersonEyeColor = createBookmarkWithCodesTable(
						BookmarkConstants.CD_PERSON_EYE_COLOR, allPeopleOnHseghDto.getCdPersonEyeColor(),
						CodesConstant.CEYECOLR);
				famParentBookMarkList.add(bookMarkaddCdPersonEyeColor);

				BookmarkDto bookMarkaddCdPersonHairColor = createBookmarkWithCodesTable(
						BookmarkConstants.CD_PERSON_HAIR_COLOR, allPeopleOnHseghDto.getCdPersonHairColor(),
						CodesConstant.CHAIRCLR);
				famParentBookMarkList.add(bookMarkaddCdPersonHairColor);

				BookmarkDto bookMarkaddCdPersonHighEdu = createBookmarkWithCodesTable(
						BookmarkConstants.CD_PERSON_HIGHEST_EDUC, allPeopleOnHseghDto.getCdPersonHighestEduc(),
						CodesConstant.CEDUCLVL);
				famParentBookMarkList.add(bookMarkaddCdPersonHighEdu);

				BookmarkDto bookMarkaddCdPersonReligion = createBookmarkWithCodesTable(
						BookmarkConstants.CD_PERSON_RELIGION, allPeopleOnHseghDto.getCdPersonReligion(),
						CodesConstant.CRELIGNS);
				famParentBookMarkList.add(bookMarkaddCdPersonReligion);

				BookmarkDto bookMarkaddCdPerRelInt = createBookmarkWithCodesTable(
						BookmarkConstants.CD_STAGE_PERS_REL_INT, allPeopleOnHseghDto.getCdStagePersRelInt(),
						CodesConstant.CRPTRINT);
				famParentBookMarkList.add(bookMarkaddCdPerRelInt);

				BookmarkDto bookMarkAddPersonIdNumber = createBookmark(BookmarkConstants.NBR_PERSON_ID_NUMBER,
						allPeopleOnHseghDto.getPersonIdNumber());
				famParentBookMarkList.add(bookMarkAddPersonIdNumber);

				BookmarkDto bookMarkAddNmFirst = createBookmark(BookmarkConstants.NM_NAME_FIRST,
						allPeopleOnHseghDto.getNmNameFirst());
				famParentBookMarkList.add(bookMarkAddNmFirst);

				BookmarkDto bookMarkAddNmLast = createBookmark(BookmarkConstants.NM_NAME_LAST,
						allPeopleOnHseghDto.getNmNameLast());
				famParentBookMarkList.add(bookMarkAddNmLast);
				BookmarkDto bookMarkAddNmMiddle = createBookmark(BookmarkConstants.NM_NAME_MIDDLE,
						allPeopleOnHseghDto.getNmNameMiddle());
				famParentBookMarkList.add(bookMarkAddNmMiddle);
				BookmarkDto bookMarkAddPersonLastEmployer = createBookmark(BookmarkConstants.NM_PERSON_LAST_EMPLOYER,
						allPeopleOnHseghDto.getNmPersonLastEmployer());
				famParentBookMarkList.add(bookMarkAddPersonLastEmployer);
				BookmarkDto bookMarkAddTxtOccupation = createBookmark(BookmarkConstants.TXT_PERSON_OCCUPATION,
						allPeopleOnHseghDto.getTxtPersonOccupation());
				famParentBookMarkList.add(bookMarkAddTxtOccupation);
				BookmarkDto bookMarkAddIdPerson = createBookmark(BookmarkConstants.UE_GROUPID,
						allPeopleOnHseghDto.getIdPerson());
				famParentBookMarkList.add(bookMarkAddIdPerson);
				// create subgroup cfzco00 and add prefill data
				List<FormDataGroupDto> tempSubGroup = new ArrayList<FormDataGroupDto>();
				if (allPeopleOnHseghDto.getCdNameSuffix() != null) {
					FormDataGroupDto nmSuffixFrmDataGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_COMMA_1,
							"cfzco00");
					// formDataGroupList.add(nmSuffixFrmDataGrpDto);
					tempSubGroup.add(nmSuffixFrmDataGrpDto);
				}

				famParentFrmDataGrpDto.setFormDataGroupList(tempSubGroup);
				famParentFrmDataGrpDto.setBookmarkDtoList(famParentBookMarkList);
				formDataGroupList.add(famParentFrmDataGrpDto);

			}
			// set prefill data for group csc32o02
			if (!ObjectUtils.isEmpty(allPeopleOnHseghDto.getCdStagePersRelInt())
					&& (FormConstants.SB.equals(allPeopleOnHseghDto.getCdStagePersRelInt())
							|| FormConstants.SS.equals(allPeopleOnHseghDto.getCdStagePersRelInt()))) {

				FormDataGroupDto famSibFrmDataGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_FAMILY_SIBLINGS,
						FormConstants.EMPTY_STRING);
				List<BookmarkDto> famSibBookMarkList = new ArrayList<BookmarkDto>();
				BookmarkDto bookMarkAddCdPersonSex = createBookmarkWithCodesTable(BookmarkConstants.CD_PERSON_SEX_1,
						allPeopleOnHseghDto.getCdPersonSex(), CodesConstant.CSEX);
				famSibBookMarkList.add(bookMarkAddCdPersonSex);
				BookmarkDto bookMarkAddDtPersonBirth = createBookmark(BookmarkConstants.DT_PERSON_BIRTH_1,
						TypeConvUtil.formDateFormat(allPeopleOnHseghDto.getDtPersonBirth()));

				famSibBookMarkList.add(bookMarkAddDtPersonBirth);
				BookmarkDto bookMarkAddDtPersonDeath = createBookmark(BookmarkConstants.DT_PERSON_DEATH_1,
						TypeConvUtil.formDateFormat(allPeopleOnHseghDto.getDtPersonDeath()));
				famSibBookMarkList.add(bookMarkAddDtPersonDeath);

				BookmarkDto bookMarkAddQtyPersonWeight = createBookmark(BookmarkConstants.QTY_PERSON_WEIGHT_1,
						TypeConvUtil.convertToTwoDecimalPlace(allPeopleOnHseghDto.getQtyPersonWeight() == null
								? ServiceConstants.ZERO_VAL : allPeopleOnHseghDto.getQtyPersonWeight()));
				famSibBookMarkList.add(bookMarkAddQtyPersonWeight);
				BookmarkDto bookMarkAddQtyPersonHeightFeet = createBookmark(BookmarkConstants.QTY_PERSON_HEIGHT_FEET_1,
						allPeopleOnHseghDto.getQtyPersonHeightFeet() == null ? ServiceConstants.ZERO_VAL
								: allPeopleOnHseghDto.getQtyPersonHeightFeet());
				famSibBookMarkList.add(bookMarkAddQtyPersonHeightFeet);
				BookmarkDto bookMarkAddQtyPersonHeightInches = createBookmark(
						BookmarkConstants.QTY_PERSON_HEIGHT_INCHES_1,
						allPeopleOnHseghDto.getQtyPersonHeightInches() == null ? ServiceConstants.ZERO_VAL
								: allPeopleOnHseghDto.getQtyPersonHeightInches());
				famSibBookMarkList.add(bookMarkAddQtyPersonHeightInches);
				BookmarkDto bookMarkaddCdNameSuffix = createBookmarkWithCodesTable(BookmarkConstants.CD_NAME_SUFFIX_1,
						allPeopleOnHseghDto.getCdNameSuffix(), CodesConstant.CSUFFIX2);
				famSibBookMarkList.add(bookMarkaddCdNameSuffix);
				BookmarkDto bookMarkAddCdPersonBirthCity = createBookmark(BookmarkConstants.CD_PERSON_BIRTH_CITY_1,
						allPeopleOnHseghDto.getCdPersonBirthCity());
				famSibBookMarkList.add(bookMarkAddCdPersonBirthCity);
				BookmarkDto bookMarkaddCdPersonBirthCountry = createBookmarkWithCodesTable(
						BookmarkConstants.TXT_PERSON_BIRTH_COUNTRY_1, allPeopleOnHseghDto.getCdPersonBirthCountry(),
						CodesConstant.CCOUNTRY);
				famSibBookMarkList.add(bookMarkaddCdPersonBirthCountry);
				BookmarkDto bookMarkaddCdPersonBirthState = createBookmarkWithCodesTable(
						BookmarkConstants.CD_PERSON_BIRTH_STATE_1, allPeopleOnHseghDto.getCdPersonBirthState(),
						CodesConstant.CSTATE);
				famSibBookMarkList.add(bookMarkaddCdPersonBirthState);
				BookmarkDto bookMarkaddCdPersonDeath = createBookmarkWithCodesTable(BookmarkConstants.CD_PERSON_DEATH_1,
						allPeopleOnHseghDto.getCdPersonDeath(), CodesConstant.CRSNFDTH);
				famSibBookMarkList.add(bookMarkaddCdPersonDeath);
				BookmarkDto bookMarkaddCdPersonEthnicGroup = createBookmarkWithCodesTable(
						BookmarkConstants.CD_PERSON_ETHNIC_GROUP_1, allPeopleOnHseghDto.getCdPersonEthnicGroup(),
						CodesConstant.CETHNIC);
				famSibBookMarkList.add(bookMarkaddCdPersonEthnicGroup);
				BookmarkDto bookMarkaddCdPersonEyeColor = createBookmarkWithCodesTable(
						BookmarkConstants.CD_PERSON_EYE_COLOR_1, allPeopleOnHseghDto.getCdPersonEyeColor(),
						CodesConstant.CEYECOLR);
				famSibBookMarkList.add(bookMarkaddCdPersonEyeColor);

				BookmarkDto bookMarkaddCdPersonHairColor = createBookmarkWithCodesTable(
						BookmarkConstants.CD_PERSON_HAIR_COLOR_1, allPeopleOnHseghDto.getCdPersonHairColor(),
						CodesConstant.CHAIRCLR);
				famSibBookMarkList.add(bookMarkaddCdPersonHairColor);

				BookmarkDto bookMarkaddCdPerRelInt = createBookmarkWithCodesTable(
						BookmarkConstants.CD_STAGE_PERS_REL_INT_1, allPeopleOnHseghDto.getCdStagePersRelInt(),
						CodesConstant.CRPTRINT);
				famSibBookMarkList.add(bookMarkaddCdPerRelInt);
				BookmarkDto bookMarkAddNmFirst = createBookmark(BookmarkConstants.NM_NAME_FIRST_1,
						allPeopleOnHseghDto.getNmNameFirst());
				famSibBookMarkList.add(bookMarkAddNmFirst);

				BookmarkDto bookMarkAddNmLast = createBookmark(BookmarkConstants.NM_NAME_LAST_1,
						allPeopleOnHseghDto.getNmNameLast());
				famSibBookMarkList.add(bookMarkAddNmLast);
				BookmarkDto bookMarkAddNmMiddle = createBookmark(BookmarkConstants.NM_NAME_MIDDLE_1,
						allPeopleOnHseghDto.getNmNameMiddle());
				famSibBookMarkList.add(bookMarkAddNmMiddle);
				BookmarkDto bookMarkAddIdPerson = createBookmark(BookmarkConstants.UE_GROUPID,
						allPeopleOnHseghDto.getIdPerson());
				famSibBookMarkList.add(bookMarkAddIdPerson);
				// create subgroup cfzco00 and add prefill data
				List<FormDataGroupDto> tempSubGroup = new ArrayList<FormDataGroupDto>();
				if (allPeopleOnHseghDto.getCdNameSuffix() != null) {
					FormDataGroupDto nmSuffixFrmDataGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_COMMA_6,
							"cfzco00");
					tempSubGroup.add(nmSuffixFrmDataGrpDto);
					// formDataGroupList.add(nmSuffixFrmDataGrpDto);
				}

				// create subgroup csc32o05 and add prefill data

				for (ChildPlanRecordDto childPlanRecord : hseghDto.getChildPlanRecordDtoList()) {
					if (childPlanRecord.getIdPerson().intValue() == allPeopleOnHseghDto.getIdPerson().intValue()) {
						FormDataGroupDto narrCpEdnDataGrpDto = createFormDataGroup(
								FormGroupsConstants.TMPLAT_NARR_CP_EDN_1, "csc32o05");
						List<BlobDataDto> blobForSibBookMarkList = new ArrayList<BlobDataDto>();

						BlobDataDto bookMarkAddChildPlanEvent = createBlobData(BookmarkConstants.NARR_CP_EDN_1,
								CodesConstant.CP_EDN_NARR, childPlanRecord.getIdChildPlanEvent().intValue());
						blobForSibBookMarkList.add(bookMarkAddChildPlanEvent);
						narrCpEdnDataGrpDto.setBlobDataDtoList(blobForSibBookMarkList);
						tempSubGroup.add(narrCpEdnDataGrpDto);

					}

				}

				// create subgroup csc32o07 and add prefill data

				for (ChildPlanRecordDto childPlanRecord : hseghDto.getChildPlanRecordDtoList()) {
					if (childPlanRecord.getIdPerson().intValue() == allPeopleOnHseghDto.getIdPerson().intValue()) {
						FormDataGroupDto narrCpPsyDataGrpDto = createFormDataGroup(
								FormGroupsConstants.TMPLAT_NARR_CP_PSY_1, "csc32o07");
						List<BlobDataDto> blobForCpPSYBookMarkList = new ArrayList<BlobDataDto>();

						BlobDataDto bookMarkAddChildPlanEvent = createBlobData(BookmarkConstants.NARR_CP_PSY_1,
								CodesConstant.CP_PSY_NARR, childPlanRecord.getIdChildPlanEvent().intValue());
						blobForCpPSYBookMarkList.add(bookMarkAddChildPlanEvent);
						narrCpPsyDataGrpDto.setBlobDataDtoList(blobForCpPSYBookMarkList);

						tempSubGroup.add(narrCpPsyDataGrpDto);

					}

				}

				// create subgroup csc32o08 and add prefill data

				for (ChildPlanRecordDto childPlanRecord : hseghDto.getChildPlanRecordDtoList()) {
					if (childPlanRecord.getIdPerson().intValue() == allPeopleOnHseghDto.getIdPerson().intValue()) {
						FormDataGroupDto narrCpDvnDataGrpDto = createFormDataGroup(
								FormGroupsConstants.TMPLAT_NARR_CP_DVN_1, "csc32o08");
						List<BlobDataDto> blobForCpDvnBookMarkList = new ArrayList<BlobDataDto>();

						BlobDataDto bookMarkAddChildPlanEvent = createBlobData(BookmarkConstants.NARR_CP_DVN_1,
								CodesConstant.CP_DVN_NARR, childPlanRecord.getIdChildPlanEvent().intValue());
						blobForCpDvnBookMarkList.add(bookMarkAddChildPlanEvent);
						narrCpDvnDataGrpDto.setBlobDataDtoList(blobForCpDvnBookMarkList);

						tempSubGroup.add(narrCpDvnDataGrpDto);

					}

				}

				// create subgroup csc32o04 and add prefill data

				for (ChildPlanRecordDto childPlanRecord : hseghDto.getChildPlanRecordDtoList()) {
					if (childPlanRecord.getIdPerson().intValue() == allPeopleOnHseghDto.getIdPerson().intValue()) {
						FormDataGroupDto narrCpIbnDataGrpDto = createFormDataGroup(
								FormGroupsConstants.TMPLAT_NARR_CP_IBP_1, "csc32o04");
						List<BlobDataDto> blobForCpIbnBookMarkList = new ArrayList<BlobDataDto>();

						BlobDataDto bookMarkAddChildPlanEvent = createBlobData(BookmarkConstants.NARR_CP_IBP_1,
								CodesConstant.CP_IBP_NARR, childPlanRecord.getIdChildPlanEvent().intValue());
						blobForCpIbnBookMarkList.add(bookMarkAddChildPlanEvent);
						narrCpIbnDataGrpDto.setBlobDataDtoList(blobForCpIbnBookMarkList);
						tempSubGroup.add(narrCpIbnDataGrpDto);

					}

				}

				// create subgroup csc32o06 and add prefill data

				for (ChildPlanRecordDto childPlanRecord : hseghDto.getChildPlanRecordDtoList()) {
					if (childPlanRecord.getIdPerson().intValue() == allPeopleOnHseghDto.getIdPerson().intValue()) {
						FormDataGroupDto narrCpMdnDataGrpDto = createFormDataGroup(
								FormGroupsConstants.TMPLAT_NARR_CP_MDN_1, "csc32o06");
						List<BlobDataDto> blobForCpMdnBookMarkList = new ArrayList<BlobDataDto>();

						BlobDataDto bookMarkAddChildPlanEvent = createBlobData(BookmarkConstants.NARR_CP_MDN_1,
								CodesConstant.CP_MDN_NARR, childPlanRecord.getIdChildPlanEvent().intValue());
						blobForCpMdnBookMarkList.add(bookMarkAddChildPlanEvent);
						narrCpMdnDataGrpDto.setBlobDataDtoList(blobForCpMdnBookMarkList);
						tempSubGroup.add(narrCpMdnDataGrpDto);

					}

				}

				famSibFrmDataGrpDto.setBookmarkDtoList(famSibBookMarkList);
				famSibFrmDataGrpDto.setFormDataGroupList(tempSubGroup);
				formDataGroupList.add(famSibFrmDataGrpDto);

			}
			// create group csc32o03 and set prefill data
			if (!ObjectUtils.isEmpty(allPeopleOnHseghDto.getCdStagePersRelInt())
					&& (FormConstants.AU.equals(allPeopleOnHseghDto.getCdStagePersRelInt())
							|| FormConstants.CO.equals(allPeopleOnHseghDto.getCdStagePersRelInt())
							|| FormConstants.FM.equals(allPeopleOnHseghDto.getCdStagePersRelInt())
							|| FormConstants.GD.equals(allPeopleOnHseghDto.getCdStagePersRelInt())
							|| FormConstants.GP.equals(allPeopleOnHseghDto.getCdStagePersRelInt())
							|| FormConstants.GF.equals(allPeopleOnHseghDto.getCdStagePersRelInt())
							|| FormConstants.GE.equals(allPeopleOnHseghDto.getCdStagePersRelInt()))) {

				FormDataGroupDto othersFrmDataGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_FAMILY_OTHER,
						FormConstants.EMPTY_STRING);
				List<BookmarkDto> famOthersBookMarkList = new ArrayList<BookmarkDto>();
				BookmarkDto bookMarkAddCdPersonSex = createBookmarkWithCodesTable(BookmarkConstants.CD_PERSON_SEX_2,
						allPeopleOnHseghDto.getCdPersonSex(), CodesConstant.CSEX);
				famOthersBookMarkList.add(bookMarkAddCdPersonSex);
				BookmarkDto bookMarkAddDtPersonBirth = createBookmark(BookmarkConstants.DT_PERSON_BIRTH_2,
						TypeConvUtil.formDateFormat(allPeopleOnHseghDto.getDtPersonBirth()));

				famOthersBookMarkList.add(bookMarkAddDtPersonBirth);
				BookmarkDto bookMarkAddDtPersonDeath = createBookmark(BookmarkConstants.DT_PERSON_DEATH_2,
						TypeConvUtil.formDateFormat(allPeopleOnHseghDto.getDtPersonDeath()));
				famOthersBookMarkList.add(bookMarkAddDtPersonDeath);

				BookmarkDto bookMarkAddQtyPersonWeight = createBookmark(BookmarkConstants.QTY_PERSON_WEIGHT_2,
						TypeConvUtil.convertToTwoDecimalPlace(allPeopleOnHseghDto.getQtyPersonWeight() == null
								? ServiceConstants.ZERO_VAL : allPeopleOnHseghDto.getQtyPersonWeight()));
				famOthersBookMarkList.add(bookMarkAddQtyPersonWeight);
				BookmarkDto bookMarkAddQtyPersonHeightFeet = createBookmark(BookmarkConstants.QTY_PERSON_HEIGHT_FEET_2,
						allPeopleOnHseghDto.getQtyPersonHeightFeet() == null ? ServiceConstants.ZERO_VAL
								: allPeopleOnHseghDto.getQtyPersonHeightFeet());
				famOthersBookMarkList.add(bookMarkAddQtyPersonHeightFeet);
				BookmarkDto bookMarkAddQtyPersonHeightInches = createBookmark(
						BookmarkConstants.QTY_PERSON_HEIGHT_INCHES_2,
						allPeopleOnHseghDto.getQtyPersonHeightInches() == null ? ServiceConstants.ZERO_VAL
								: allPeopleOnHseghDto.getQtyPersonHeightInches());
				famOthersBookMarkList.add(bookMarkAddQtyPersonHeightInches);
				BookmarkDto bookMarkaddCdNameSuffix = createBookmarkWithCodesTable(BookmarkConstants.CD_NAME_SUFFIX_2,
						allPeopleOnHseghDto.getCdNameSuffix(), CodesConstant.CSUFFIX2);
				famOthersBookMarkList.add(bookMarkaddCdNameSuffix);
				BookmarkDto bookMarkAddCdPersonBirthCity = createBookmark(BookmarkConstants.CD_PERSON_BIRTH_CITY_2,
						allPeopleOnHseghDto.getCdPersonBirthCity());
				famOthersBookMarkList.add(bookMarkAddCdPersonBirthCity);
				BookmarkDto bookMarkaddCdPersonBirthCountry = createBookmarkWithCodesTable(
						BookmarkConstants.TXT_PERSON_BIRTH_COUNTRY_2, allPeopleOnHseghDto.getCdPersonBirthCountry(),
						CodesConstant.CCOUNTRY);
				famOthersBookMarkList.add(bookMarkaddCdPersonBirthCountry);
				BookmarkDto bookMarkaddCdPersonBirthState = createBookmarkWithCodesTable(
						BookmarkConstants.CD_PERSON_BIRTH_STATE_2, allPeopleOnHseghDto.getCdPersonBirthState(),
						CodesConstant.CSTATE);
				famOthersBookMarkList.add(bookMarkaddCdPersonBirthState);
				BookmarkDto bookMarkaddCdPersonCitizenship = createBookmarkWithCodesTable(
						BookmarkConstants.CD_PERSON_CITIZENSHIP_2, allPeopleOnHseghDto.getCdPersonCitizenship(),
						CodesConstant.CCTZNSTA);
				famOthersBookMarkList.add(bookMarkaddCdPersonCitizenship);
				BookmarkDto bookMarkaddCdPersonDeath = createBookmarkWithCodesTable(BookmarkConstants.CD_PERSON_DEATH_2,
						allPeopleOnHseghDto.getCdPersonDeath(), CodesConstant.CRSNFDTH);
				famOthersBookMarkList.add(bookMarkaddCdPersonDeath);
				BookmarkDto bookMarkaddCdPersonEthnicGroup = createBookmarkWithCodesTable(
						BookmarkConstants.CD_PERSON_ETHNIC_GROUP_2, allPeopleOnHseghDto.getCdPersonEthnicGroup(),
						CodesConstant.CETHNIC);
				famOthersBookMarkList.add(bookMarkaddCdPersonEthnicGroup);
				BookmarkDto bookMarkaddCdPersonEyeColor = createBookmarkWithCodesTable(
						BookmarkConstants.CD_PERSON_EYE_COLOR_2, allPeopleOnHseghDto.getCdPersonEyeColor(),
						CodesConstant.CEYECOLR);
				famOthersBookMarkList.add(bookMarkaddCdPersonEyeColor);

				BookmarkDto bookMarkaddCdPersonHairColor = createBookmarkWithCodesTable(
						BookmarkConstants.CD_PERSON_HAIR_COLOR_2, allPeopleOnHseghDto.getCdPersonHairColor(),
						CodesConstant.CHAIRCLR);
				famOthersBookMarkList.add(bookMarkaddCdPersonHairColor);

				BookmarkDto bookMarkaddCdPersonHighEdu = createBookmarkWithCodesTable(
						BookmarkConstants.CD_PERSON_HIGHEST_EDUC_2, allPeopleOnHseghDto.getCdPersonHighestEduc(),
						CodesConstant.CEDUCLVL);
				famOthersBookMarkList.add(bookMarkaddCdPersonHighEdu);

				BookmarkDto bookMarkaddCdPersonReligion = createBookmarkWithCodesTable(
						BookmarkConstants.CD_PERSON_RELIGION_2, allPeopleOnHseghDto.getCdPersonReligion(),
						CodesConstant.CRELIGNS);
				famOthersBookMarkList.add(bookMarkaddCdPersonReligion);

				BookmarkDto bookMarkaddCdPerRelInt = createBookmarkWithCodesTable(
						BookmarkConstants.CD_STAGE_PERS_REL_INT_2, allPeopleOnHseghDto.getCdStagePersRelInt(),
						CodesConstant.CRPTRINT);
				famOthersBookMarkList.add(bookMarkaddCdPerRelInt);

				BookmarkDto bookMarkAddNmFirst = createBookmark(BookmarkConstants.NM_NAME_FIRST_2,
						allPeopleOnHseghDto.getNmNameFirst());
				famOthersBookMarkList.add(bookMarkAddNmFirst);

				BookmarkDto bookMarkAddNmLast = createBookmark(BookmarkConstants.NM_NAME_LAST_2,
						allPeopleOnHseghDto.getNmNameLast());
				famOthersBookMarkList.add(bookMarkAddNmLast);
				BookmarkDto bookMarkAddNmMiddle = createBookmark(BookmarkConstants.NM_NAME_MIDDLE_2,
						allPeopleOnHseghDto.getNmNameMiddle());
				famOthersBookMarkList.add(bookMarkAddNmMiddle);
				BookmarkDto bookMarkAddTxtOccupation = createBookmark(BookmarkConstants.TXT_PERSON_OCCUPATION_2,
						allPeopleOnHseghDto.getTxtPersonOccupation());
				famOthersBookMarkList.add(bookMarkAddTxtOccupation);
				BookmarkDto bookMarkAddIdPerson = createBookmark(BookmarkConstants.UE_GROUPID,
						allPeopleOnHseghDto.getIdPerson());
				famOthersBookMarkList.add(bookMarkAddIdPerson);
				List<FormDataGroupDto> tempSubGroup = new ArrayList<FormDataGroupDto>();
				// create subgroup cfzco00 and add prefill data
				if (allPeopleOnHseghDto.getCdNameSuffix() != null) {
					FormDataGroupDto nmSuffixFrmDataGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_COMMA_8,
							"cfzco00");
					tempSubGroup.add(nmSuffixFrmDataGrpDto);
					// formDataGroupList.add(nmSuffixFrmDataGrpDto);
				}
				othersFrmDataGrpDto.setBookmarkDtoList(famOthersBookMarkList);
				othersFrmDataGrpDto.setFormDataGroupList(tempSubGroup);
				formDataGroupList.add(othersFrmDataGrpDto);

			}
			// create group csc32o26 and set prefill data
			if (!ObjectUtils.isEmpty(allPeopleOnHseghDto.getCdStagePersRole())
					&& allPeopleOnHseghDto.getCdStagePersRole().equals(FormConstants.PC)) {

				FormDataGroupDto childFrmDataGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_CHILD,
						FormConstants.EMPTY_STRING);
				List<BookmarkDto> childBookMarkList = new ArrayList<BookmarkDto>();
				BookmarkDto bookMarkAddCdPersonSex = createBookmarkWithCodesTable(BookmarkConstants.CD_PERSON_SEX_3,
						allPeopleOnHseghDto.getCdPersonSex(), CodesConstant.CSEX);
				childBookMarkList.add(bookMarkAddCdPersonSex);
				BookmarkDto bookMarkAddQtyPersonWeight = createBookmark(BookmarkConstants.QTY_PERSON_WEIGHT_3,
						TypeConvUtil.convertToTwoDecimalPlace(allPeopleOnHseghDto.getQtyPersonWeight() == null
								? ServiceConstants.ZERO_VAL : allPeopleOnHseghDto.getQtyPersonWeight()));
				childBookMarkList.add(bookMarkAddQtyPersonWeight);
				BookmarkDto bookMarkAddQtyPersonHeightFeet = createBookmark(BookmarkConstants.QTY_PERSON_HEIGHT_FEET_3,
						allPeopleOnHseghDto.getQtyPersonHeightFeet() == null ? ServiceConstants.ZERO_VAL
								: allPeopleOnHseghDto.getQtyPersonHeightFeet());
				childBookMarkList.add(bookMarkAddQtyPersonHeightFeet);
				BookmarkDto bookMarkAddQtyPersonHeightInches = createBookmark(
						BookmarkConstants.QTY_PERSON_HEIGHT_INCHES_3,
						allPeopleOnHseghDto.getQtyPersonHeightInches() == null ? ServiceConstants.ZERO_VAL
								: allPeopleOnHseghDto.getQtyPersonHeightInches());
				childBookMarkList.add(bookMarkAddQtyPersonHeightInches);
				BookmarkDto bookMarkAddCdPersonBirthCity = createBookmark(BookmarkConstants.CD_PERSON_BIRTH_CITY_3,
						allPeopleOnHseghDto.getCdPersonBirthCity());
				childBookMarkList.add(bookMarkAddCdPersonBirthCity);
				BookmarkDto bookMarkaddCdPersonBirthCountry = createBookmarkWithCodesTable(
						BookmarkConstants.TXT_PERSON_BIRTH_COUNTRY_3, allPeopleOnHseghDto.getCdPersonBirthCountry(),
						CodesConstant.CCOUNTRY);
				childBookMarkList.add(bookMarkaddCdPersonBirthCountry);
				BookmarkDto bookMarkaddCdPersonBirthState = createBookmarkWithCodesTable(
						BookmarkConstants.CD_PERSON_BIRTH_STATE_3, allPeopleOnHseghDto.getCdPersonBirthState(),
						CodesConstant.CSTATE);
				childBookMarkList.add(bookMarkaddCdPersonBirthState);
				BookmarkDto bookMarkaddCdPersonCitizenship = createBookmarkWithCodesTable(
						BookmarkConstants.CD_PERSON_CITIZENSHIP_3, allPeopleOnHseghDto.getCdPersonCitizenship(),
						CodesConstant.CCTZNSTA);
				childBookMarkList.add(bookMarkaddCdPersonCitizenship);
				BookmarkDto bookMarkaddCdPersonEthnicGroup = createBookmarkWithCodesTable(
						BookmarkConstants.CD_PERSON_ETHNIC_GROUP_3, allPeopleOnHseghDto.getCdPersonEthnicGroup(),
						CodesConstant.CETHNIC);
				childBookMarkList.add(bookMarkaddCdPersonEthnicGroup);
				BookmarkDto bookMarkaddCdPersonEyeColor = createBookmarkWithCodesTable(
						BookmarkConstants.CD_PERSON_EYE_COLOR_3, allPeopleOnHseghDto.getCdPersonEyeColor(),
						CodesConstant.CEYECOLR);
				childBookMarkList.add(bookMarkaddCdPersonEyeColor);

				BookmarkDto bookMarkaddCdPersonHairColor = createBookmarkWithCodesTable(
						BookmarkConstants.CD_PERSON_HAIR_COLOR_3, allPeopleOnHseghDto.getCdPersonHairColor(),
						CodesConstant.CHAIRCLR);
				childBookMarkList.add(bookMarkaddCdPersonHairColor);

				BookmarkDto bookMarkaddCdPersonReligion = createBookmarkWithCodesTable(
						BookmarkConstants.CD_PERSON_RELIGION_3, allPeopleOnHseghDto.getCdPersonReligion(),
						CodesConstant.CRELIGNS);
				childBookMarkList.add(bookMarkaddCdPersonReligion);

				BookmarkDto bookMarkAddPersonIdNumber = createBookmark(BookmarkConstants.NBR_PERSON_ID_NUMBER_3,
						allPeopleOnHseghDto.getPersonIdNumber());
				childBookMarkList.add(bookMarkAddPersonIdNumber);

				BookmarkDto bookMarkAddIdPerson = createBookmark(BookmarkConstants.UE_GROUPID,
						allPeopleOnHseghDto.getIdPerson());
				childBookMarkList.add(bookMarkAddIdPerson);

				childFrmDataGrpDto.setBookmarkDtoList(childBookMarkList);
				formDataGroupList.add(childFrmDataGrpDto);
			}
		}

		for (PersonOnHseghDto allPeopleOnHseghDto : hseghDto.getAllPeopleOnHseghDtoList()) {
			if (!ObjectUtils.isEmpty(allPeopleOnHseghDto.getCdStagePersRole())
					&& FormConstants.PC.equals(allPeopleOnHseghDto.getCdStagePersRole())) {
				// create prefill data for group csc32o27
				FormDataGroupDto tempInfoFrmDataGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_INFO,
						FormConstants.EMPTY_STRING);
				BookmarkDto bookMarkAddIdPerson = createBookmark(BookmarkConstants.UE_GROUPID,
						allPeopleOnHseghDto.getIdPerson());
				List<BookmarkDto> tempInfoBookMarkList = new ArrayList<BookmarkDto>();
				tempInfoBookMarkList.add(bookMarkAddIdPerson);
				tempInfoFrmDataGrpDto.setBookmarkDtoList(tempInfoBookMarkList);

				List<FormDataGroupDto> tempSubGroup = new ArrayList<FormDataGroupDto>();

				// create group csc32o21 and add prefill data

				for (ChildPlanRecordDto childPlanRecord : hseghDto.getChildPlanRecordDtoList()) {
					if (childPlanRecord.getIdPerson().intValue() == allPeopleOnHseghDto.getIdPerson().intValue()) {
						FormDataGroupDto narrCpPvpDataGrpDto = createFormDataGroup(
								FormGroupsConstants.TMPLAT_NARR_CP_PVP, "csc32o21");
						List<BlobDataDto> blobForCpPvpBookMarkList = new ArrayList<BlobDataDto>();

						BlobDataDto bookMarkAddChildPlanEvent = createBlobData(BookmarkConstants.NARR_CP_PVP,
								CodesConstant.CP_PVP_NARR, childPlanRecord.getIdChildPlanEvent().intValue());
						blobForCpPvpBookMarkList.add(bookMarkAddChildPlanEvent);
						narrCpPvpDataGrpDto.setBlobDataDtoList(blobForCpPvpBookMarkList);
						tempSubGroup.add(narrCpPvpDataGrpDto);

					}

				}

				// create group csc32o09 and add prefill data

				for (ChildPlanRecordDto childPlanRecord : hseghDto.getChildPlanRecordDtoList()) {
					if (childPlanRecord.getIdPerson().intValue() == allPeopleOnHseghDto.getIdPerson().intValue()) {
						FormDataGroupDto narrCpIbpDataGrpDto = createFormDataGroup(
								FormGroupsConstants.TMPLAT_NARR_CP_IBP, "csc32o09");
						List<BlobDataDto> blobForCpIbpBookMarkList = new ArrayList<BlobDataDto>();

						BlobDataDto bookMarkAddChildPlanEvent = createBlobData(BookmarkConstants.NARR_CP_IBP,
								CodesConstant.CP_IBP_NARR, childPlanRecord.getIdChildPlanEvent().intValue());
						blobForCpIbpBookMarkList.add(bookMarkAddChildPlanEvent);
						narrCpIbpDataGrpDto.setBlobDataDtoList(blobForCpIbpBookMarkList);
						tempSubGroup.add(narrCpIbpDataGrpDto);

					}

				}

				// create group csc32o10 and add prefill data

				for (ChildPlanRecordDto childPlanRecord : hseghDto.getChildPlanRecordDtoList()) {
					if (childPlanRecord.getIdPerson().intValue() == allPeopleOnHseghDto.getIdPerson().intValue()) {
						FormDataGroupDto narrCpPsyDataGrpDto = createFormDataGroup(
								FormGroupsConstants.TMPLAT_NARR_CP_PHY, "csc32o10");
						List<BlobDataDto> blobForCpPhyBookMarkList = new ArrayList<BlobDataDto>();
						BlobDataDto bookMarkAddChildPlanEvent = createBlobData(BookmarkConstants.NARR_CP_PHY,
								CodesConstant.CP_PHY_NARR, childPlanRecord.getIdChildPlanEvent().intValue());
						blobForCpPhyBookMarkList.add(bookMarkAddChildPlanEvent);
						narrCpPsyDataGrpDto.setBlobDataDtoList(blobForCpPhyBookMarkList);
						tempSubGroup.add(narrCpPsyDataGrpDto);

					}

				}

				// create group csc32o11 and add prefill data

				for (ChildPlanRecordDto childPlanRecord : hseghDto.getChildPlanRecordDtoList()) {
					if (childPlanRecord.getIdPerson().intValue() == allPeopleOnHseghDto.getIdPerson().intValue()) {
						FormDataGroupDto narrCpDvnDataGrpDto = createFormDataGroup(
								FormGroupsConstants.TMPLAT_NARR_CP_DVN, "csc32o11");
						List<BlobDataDto> blobForCpDvnBookMarkList = new ArrayList<BlobDataDto>();

						BlobDataDto bookMarkAddChildPlanEvent = createBlobData(BookmarkConstants.NARR_CP_DVN,
								CodesConstant.CP_DVN_NARR, childPlanRecord.getIdChildPlanEvent().intValue());
						blobForCpDvnBookMarkList.add(bookMarkAddChildPlanEvent);
						narrCpDvnDataGrpDto.setBlobDataDtoList(blobForCpDvnBookMarkList);
						tempSubGroup.add(narrCpDvnDataGrpDto);

					}
				}

				// create group csc32o12 and add prefill data

				for (ChildPlanRecordDto childPlanRecord : hseghDto.getChildPlanRecordDtoList()) {
					if (childPlanRecord.getIdPerson().intValue() == allPeopleOnHseghDto.getIdPerson().intValue()) {
						FormDataGroupDto narrCpIghDataGrpDto = createFormDataGroup(
								FormGroupsConstants.TMPLAT_NARR_CP_IGH, "csc32o12");
						List<BlobDataDto> blobForCpIghBookMarkList = new ArrayList<BlobDataDto>();

						BlobDataDto bookMarkAddChildPlanEvent = createBlobData(BookmarkConstants.NARR_CP_IGH,
								CodesConstant.CP_IGH_NARR, childPlanRecord.getIdChildPlanEvent().intValue());
						blobForCpIghBookMarkList.add(bookMarkAddChildPlanEvent);
						narrCpIghDataGrpDto.setBlobDataDtoList(blobForCpIghBookMarkList);
						tempSubGroup.add(narrCpIghDataGrpDto);

					}
				}

				// create group csc32o13 and add prefill data

				for (ChildPlanRecordDto childPlanRecord : hseghDto.getChildPlanRecordDtoList()) {
					if (childPlanRecord.getIdPerson().intValue() == allPeopleOnHseghDto.getIdPerson().intValue()) {
						FormDataGroupDto narrCpMdnDataGrpDto = createFormDataGroup(
								FormGroupsConstants.TMPLAT_NARR_CP_MDN, "csc32o13");
						List<BlobDataDto> blobForCpMdnBookMarkList = new ArrayList<BlobDataDto>();

						BlobDataDto bookMarkAddChildPlanEvent = createBlobData(BookmarkConstants.NARR_CP_MDN,
								CodesConstant.CP_MDN_NARR, childPlanRecord.getIdChildPlanEvent().intValue());
						blobForCpMdnBookMarkList.add(bookMarkAddChildPlanEvent);
						narrCpMdnDataGrpDto.setBlobDataDtoList(blobForCpMdnBookMarkList);
						tempSubGroup.add(narrCpMdnDataGrpDto);

					}

				}
				// create group csc32o14 and add prefill data

				for (ChildPlanRecordDto childPlanRecord : hseghDto.getChildPlanRecordDtoList()) {
					if (childPlanRecord.getIdPerson().intValue() == allPeopleOnHseghDto.getIdPerson().intValue()) {
						FormDataGroupDto narrCpSenDataGrpDto = createFormDataGroup(
								FormGroupsConstants.TMPLAT_NARR_CP_SEN, "csc32o14");
						List<BlobDataDto> blobForCpSenBookMarkList = new ArrayList<BlobDataDto>();

						BlobDataDto bookMarkAddChildPlanEvent = createBlobData(BookmarkConstants.NARR_CP_SEN,
								CodesConstant.CP_SEN_NARR, childPlanRecord.getIdChildPlanEvent().intValue());
						blobForCpSenBookMarkList.add(bookMarkAddChildPlanEvent);
						narrCpSenDataGrpDto.setBlobDataDtoList(blobForCpSenBookMarkList);
						tempSubGroup.add(narrCpSenDataGrpDto);

					}

				}

				// create group csc32o15 and add prefill data

				for (ChildPlanRecordDto childPlanRecord : hseghDto.getChildPlanRecordDtoList()) {
					if (childPlanRecord.getIdPerson().intValue() == allPeopleOnHseghDto.getIdPerson().intValue()) {
						FormDataGroupDto narrCpPsyDataGrpDto2 = createFormDataGroup(
								FormGroupsConstants.TMPLAT_NARR_CP_PSY, "csc32o15");
						List<BlobDataDto> blobForCpPsyBookMarkList2 = new ArrayList<BlobDataDto>();

						BlobDataDto bookMarkAddChildPlanEvent = createBlobData(BookmarkConstants.NARR_CP_PSY,
								CodesConstant.CP_PSY_NARR, childPlanRecord.getIdChildPlanEvent().intValue());
						blobForCpPsyBookMarkList2.add(bookMarkAddChildPlanEvent);
						narrCpPsyDataGrpDto2.setBlobDataDtoList(blobForCpPsyBookMarkList2);
						tempSubGroup.add(narrCpPsyDataGrpDto2);

					}
				}

				// create group csc32o16 and add prefill data

				for (ChildPlanRecordDto childPlanRecord : hseghDto.getChildPlanRecordDtoList()) {
					if (childPlanRecord.getIdPerson().intValue() == allPeopleOnHseghDto.getIdPerson().intValue()) {
						FormDataGroupDto narrCpEdnDataGrpDto = createFormDataGroup(
								FormGroupsConstants.TMPLAT_NARR_CP_EDN, "csc32o16");
						List<BlobDataDto> blobForCpEdnBookMarkList = new ArrayList<BlobDataDto>();

						BlobDataDto bookMarkAddChildPlanEvent = createBlobData(BookmarkConstants.NARR_CP_EDN,
								CodesConstant.CP_EDN_NARR, childPlanRecord.getIdChildPlanEvent().intValue());
						blobForCpEdnBookMarkList.add(bookMarkAddChildPlanEvent);
						narrCpEdnDataGrpDto.setBlobDataDtoList(blobForCpEdnBookMarkList);
						tempSubGroup.add(narrCpEdnDataGrpDto);

					}

				}

				// create group csc32o17 and add prefill data

				for (ChildPlanRecordDto childPlanRecord : hseghDto.getChildPlanRecordDtoList()) {
					if (childPlanRecord.getIdPerson().intValue() == allPeopleOnHseghDto.getIdPerson().intValue()) {
						FormDataGroupDto narrCpRecDataGrpDto = createFormDataGroup(
								FormGroupsConstants.TMPLAT_NARR_CP_REC, "csc32o17");
						List<BlobDataDto> blobForCpRecBookMarkList = new ArrayList<BlobDataDto>();

						BlobDataDto bookMarkAddChildPlanEvent = createBlobData(BookmarkConstants.NARR_CP_REC,
								CodesConstant.CP_REC_NARR, childPlanRecord.getIdChildPlanEvent().intValue());
						blobForCpRecBookMarkList.add(bookMarkAddChildPlanEvent);
						narrCpRecDataGrpDto.setBlobDataDtoList(blobForCpRecBookMarkList);
						tempSubGroup.add(narrCpRecDataGrpDto);

					}

				}

				// create group csc32o18 and add prefill data

				for (ChildPlanRecordDto childPlanRecord : hseghDto.getChildPlanRecordDtoList()) {
					if (childPlanRecord.getIdPerson().intValue() == allPeopleOnHseghDto.getIdPerson().intValue()) {
						FormDataGroupDto narrCpDscDataGrpDto = createFormDataGroup(
								FormGroupsConstants.TMPLAT_NARR_CP_DSC, "csc32o18");
						List<BlobDataDto> blobForCpDscBookMarkList = new ArrayList<BlobDataDto>();

						BlobDataDto bookMarkAddChildPlanEvent = createBlobData(BookmarkConstants.NARR_CP_DSC,
								CodesConstant.CP_DSC_NARR, childPlanRecord.getIdChildPlanEvent().intValue());
						blobForCpDscBookMarkList.add(bookMarkAddChildPlanEvent);
						narrCpDscDataGrpDto.setBlobDataDtoList(blobForCpDscBookMarkList);
						tempSubGroup.add(narrCpDscDataGrpDto);

					}

				}

				// create group csc32o19 and add prefill data

				for (ChildPlanRecordDto childPlanRecord : hseghDto.getChildPlanRecordDtoList()) {
					if (childPlanRecord.getIdPerson().intValue() == allPeopleOnHseghDto.getIdPerson().intValue()) {
						FormDataGroupDto narrCpSupDataGrpDto = createFormDataGroup(
								FormGroupsConstants.TMPLAT_NARR_CP_SUP, "csc32o19");
						List<BlobDataDto> blobForCpSupBookMarkList = new ArrayList<BlobDataDto>();

						BlobDataDto bookMarkAddChildPlanEvent = createBlobData(BookmarkConstants.NARR_CP_SUP,
								CodesConstant.CP_SUP_NARR, childPlanRecord.getIdChildPlanEvent().intValue());
						blobForCpSupBookMarkList.add(bookMarkAddChildPlanEvent);
						narrCpSupDataGrpDto.setBlobDataDtoList(blobForCpSupBookMarkList);
						tempSubGroup.add(narrCpSupDataGrpDto);

					}

				}

				// create group csc32o20 and add prefill data

				for (ChildPlanRecordDto childPlanRecord : hseghDto.getChildPlanRecordDtoList()) {
					if (childPlanRecord.getIdPerson().intValue() == allPeopleOnHseghDto.getIdPerson().intValue()) {
						FormDataGroupDto narrCpIshDataGrpDto2 = createFormDataGroup(
								FormGroupsConstants.TMPLAT_NARR_CP_ISH, "csc32o20");
						List<BlobDataDto> blobForCpIshBookMarkList2 = new ArrayList<BlobDataDto>();
						BlobDataDto bookMarkAddChildPlanEvent = createBlobData(BookmarkConstants.NARR_CP_ISH,
								CodesConstant.CP_ISH_NARR, childPlanRecord.getIdChildPlanEvent().intValue());
						blobForCpIshBookMarkList2.add(bookMarkAddChildPlanEvent);
						narrCpIshDataGrpDto2.setBlobDataDtoList(blobForCpIshBookMarkList2);
						tempSubGroup.add(narrCpIshDataGrpDto2);

					}

				}

				// create group csc32o25 and add prefill data

				for (CharacteristicsDto characteristicsDto : hseghDto.getCharacteristicDtoList()) {
					FormDataGroupDto FrmCharateristicsDataGrpDto = createFormDataGroup(
							FormGroupsConstants.TMPLAT_CHARACTERISTICS, "csc32o25");
					List<BookmarkDto> chracticticsBookMarkList = new ArrayList<BookmarkDto>();
					BookmarkDto bookMarkAddCdCharacteristic = createBookmarkWithCodesTable(
							BookmarkConstants.CD_CHARACTERISTIC, characteristicsDto.getCdCharacteristic(),
							CodesConstant.CPL);
					chracticticsBookMarkList.add(bookMarkAddCdCharacteristic);
					FrmCharateristicsDataGrpDto.setBookmarkDtoList(chracticticsBookMarkList);
					tempSubGroup.add(FrmCharateristicsDataGrpDto);

				}

				// create group csc32o28 and add prefill data

				for (ChildPlanRecordDto childPlanRecord : hseghDto.getChildPlanRecordDtoList()) {
					if (childPlanRecord.getIdPerson().intValue() == allPeopleOnHseghDto.getIdPerson().intValue()) {
						FormDataGroupDto narrCpIshDataGrpDto = createFormDataGroup(
								FormGroupsConstants.TMPLAT_NARR_CP_ISH_1, "csc32o28");
						List<BlobDataDto> blobForCpIshBookMarkList = new ArrayList<BlobDataDto>();

						BlobDataDto bookMarkAddChildPlanEvent = createBlobData(BookmarkConstants.NARR_CP_ISH_1,
								CodesConstant.CP_ISH_NARR, childPlanRecord.getIdChildPlanEvent().intValue());
						blobForCpIshBookMarkList.add(bookMarkAddChildPlanEvent);
						narrCpIshDataGrpDto.setBlobDataDtoList(blobForCpIshBookMarkList);
						tempSubGroup.add(narrCpIshDataGrpDto);

					}
				}

				tempInfoFrmDataGrpDto.setFormDataGroupList(tempSubGroup);
				formDataGroupList.add(tempInfoFrmDataGrpDto);
			}
		}

		// create group cfzco00 and add prefill data
		if (!ObjectUtils.isEmpty(hseghDto.getNameDetailDto().getCdNameSuffix())) {
			FormDataGroupDto FrmCommaDataGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_COMMA,
					FormConstants.EMPTY_STRING);
			formDataGroupList.add(FrmCommaDataGrpDto);
		}

		/**
		 * Populating the non form group data into prefill data
		 */
		List<BookmarkDto> bookmarkNonFrmGrpList = new ArrayList<BookmarkDto>();
		//Fixed warranty defect#11991 to avoid duplicating the bookmark
		if(!ObjectUtils.isEmpty(hseghDto.getServicePlanDtoList())) {
			BookmarkDto bookMarkAddTxtSvcPlan = createBookmark(BookmarkConstants.TXT_SVC_PLAN_RSN_INVLVMNT,
					hseghDto.getServicePlanDtoList().get(0).getTxtSvcPlanRsnInvlvmnt());
			bookmarkNonFrmGrpList.add(bookMarkAddTxtSvcPlan);
		}
		//Fixed warranty defect#11991 to avoid duplicating the bookmark
		if(!ObjectUtils.isEmpty(hseghDto.getLegalStatusPersonMaxStatusList())) {
			BookmarkDto bookMarkAddDtLegalStatus = createBookmark(BookmarkConstants.DT_LEGAL_STAT_STATUS_DT,
					TypeConvUtil.formDateFormat(hseghDto.getLegalStatusPersonMaxStatusList().get(0).getDtLegalStatStatusDt()));
			bookmarkNonFrmGrpList.add(bookMarkAddDtLegalStatus);
			BookmarkDto bookMarkAddCdLegalStatus = createBookmarkWithCodesTable(BookmarkConstants.CD_LEGAL_STAT_STATUS,
					hseghDto.getLegalStatusPersonMaxStatusList().get(0).getCdLegalStatStatus(), CodesConstant.CLEGSTAT);
			bookmarkNonFrmGrpList.add(bookMarkAddCdLegalStatus);

		}
		BookmarkDto bookMarkAddCdPlocChild = createBookmarkWithCodesTable(BookmarkConstants.CD_PLOC_CHILD,
				hseghDto.getPersonLocDto().getCdPlocChild(), CodesConstant.CATHPLOC);
		bookmarkNonFrmGrpList.add(bookMarkAddCdPlocChild);

		if (!ObjectUtils.isEmpty(hseghDto.getServicePackageDetails())) {
			String decode = hseghDto.getServicePackageDetails().stream().
					map(pkg -> (String) getDecodedValue(pkg.getSvcPkgAddonCd(), CodesConstant.CSVCCODE)).
					collect(Collectors.joining(","));
			bookmarkNonFrmGrpList.add(createBookmark(BookmarkConstants.ADDON_SERVICE_PACKAGES, decode));
		}

		if (!ObjectUtils.isEmpty(hseghDto.getServicePackages())) {

			ServicePackageDtlDto selectedServicePackageDtlDto = hseghDto.getServicePackages().stream().
					filter(pkg -> SERVICE_PACKAGE_SELECTED.equalsIgnoreCase(pkg.getSvcPkgTypeCd()))
					.max(Comparator.comparing(ServicePackageDtlDto::getDtSvcStart)).orElse(null);

			ServicePackageDtlDto recommendedServicePackageDtlDto = hseghDto.getServicePackages().stream().
					filter(pkg -> SERVICE_PACKAGE_RECOMMENDED.equalsIgnoreCase(pkg.getSvcPkgTypeCd()))
					.max(Comparator.comparing(ServicePackageDtlDto::getDtSvcStart)).orElse(null);
			if (!ObjectUtils.isEmpty(selectedServicePackageDtlDto)) {
				bookmarkNonFrmGrpList.add(createBookmarkWithCodesTable(
						BookmarkConstants.LATEST_SELECTED_SERVICE_PACKAGE,
						selectedServicePackageDtlDto.getSvcPkgCd(), CodesConstant.CSVCCODE));
			}

			if (!ObjectUtils.isEmpty(recommendedServicePackageDtlDto)) {
				bookmarkNonFrmGrpList.add(createBookmarkWithCodesTable(
						BookmarkConstants.RECOMMENDED_SERVICE_PACKAGE,
						recommendedServicePackageDtlDto.getSvcPkgCd(), CodesConstant.CSVCCODE));
			}

		}

		BookmarkDto bookMarkAddCdNameSuffix = createBookmarkWithCodesTable(BookmarkConstants.NM_NAME_SUFFIX,
				hseghDto.getNameDetailDto().getCdNameSuffix(), CodesConstant.CSUFFIX2);
		bookmarkNonFrmGrpList.add(bookMarkAddCdNameSuffix);

		BookmarkDto bookMarkAddCdNameFirst = createBookmark(BookmarkConstants.NM_NAME_FIRST_3,
				hseghDto.getNameDetailDto().getNmNameFirst());
		bookmarkNonFrmGrpList.add(bookMarkAddCdNameFirst);
		BookmarkDto bookMarkAddCdNameLast = createBookmark(BookmarkConstants.NM_NAME_LAST_3,
				hseghDto.getNameDetailDto().getNmNameLast());
		bookmarkNonFrmGrpList.add(bookMarkAddCdNameLast);

		BookmarkDto bookMarkAddCdNameMiddle = createBookmark(BookmarkConstants.NM_NAME_MIDDLE_3,
				hseghDto.getNameDetailDto().getNmNameMiddle());
		bookmarkNonFrmGrpList.add(bookMarkAddCdNameMiddle);
		
		for (PersonDtlDto personDtlDto : hseghDto.getPersonDtlDtoList()) {
			//Fixed warranty defect#11991 to avoid duplicating the bookmark
			if(personDtlDto.getIdPerson().equals(hseghDto.getNameDetailDto().getIdPerson())){
			BookmarkDto bookMarkAddDtPersonBirth = createBookmark(BookmarkConstants.DT_PERSON_BIRTH_3,
					TypeConvUtil.formDateFormat(personDtlDto.getDtPersonBirth()));
			bookmarkNonFrmGrpList.add(bookMarkAddDtPersonBirth);
			break;
			}
		}

		PreFillDataServiceDto preFillData = new PreFillDataServiceDto();
		preFillData.setFormDataGroupList(formDataGroupList);
		preFillData.setBookmarkDtoList(bookmarkNonFrmGrpList);
		
		return preFillData;

	}

}
