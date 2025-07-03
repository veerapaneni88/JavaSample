package us.tx.state.dfps.service.forms.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.service.admin.dto.LegalStatusOutDto;
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
import us.tx.state.dfps.service.medcareconsenter.dto.MedicalCareConsenterDto;
import us.tx.state.dfps.service.medcareconsenter.dto.PersonPhoneMedCareDto;
import us.tx.state.dfps.service.person.dto.MedicalConsenterDto;
import us.tx.state.dfps.service.workload.dto.EmployeePersPhNameDto;
import us.tx.state.dfps.service.workload.dto.PersonDto;
import us.tx.state.dfps.service.workload.dto.StagePersonDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:MedicalCareConsenterPrefillData Feb 9, 2018- 1:58:57 PM © 2017
 * Texas Department of Family and Protective Services
 */
@Component
public class MedicalCareConsenterPrefillData extends DocumentServiceUtil {

	/**
	 * Method Description: The method is used to return the Prefill Data for the
	 * Medical Care Consenter form.
	 * 
	 * @param parentDtoobj
	 * @return PreFillDataServiceDto
	 * 
	 */
	@Override
	public PreFillDataServiceDto returnPrefillData(Object parentDtoobj) {

		MedicalCareConsenterDto medicalCareConsenterDto = (MedicalCareConsenterDto) parentDtoobj;
		if (null == medicalCareConsenterDto.getLegalStatusOutDto()) {
			medicalCareConsenterDto.setLegalStatusOutDto(new LegalStatusOutDto());
		}
		if (null == medicalCareConsenterDto.getMedicalConsenterDtlsList()) {
			medicalCareConsenterDto.setMedicalConsenterDtlsList(new ArrayList<MedicalConsenterDto>());
		}
		if (null == medicalCareConsenterDto.getMedicalConsenterRecsList()) {
			medicalCareConsenterDto.setMedicalConsenterRecsList(new ArrayList<MedicalConsenterDto>());
		}
		if (null == medicalCareConsenterDto.getEmployeePersPhNameDto()) {
			medicalCareConsenterDto.setEmployeePersPhNameDto(new EmployeePersPhNameDto());
		}
		if (null == medicalCareConsenterDto.getPersonDto()) {
			medicalCareConsenterDto.setPersonDto(new PersonDto());
		}
		if (null == medicalCareConsenterDto.getPersonPhoneMedCareDto()) {
			medicalCareConsenterDto.setPersonPhoneMedCareDto(new PersonPhoneMedCareDto());
		}
		if (null == medicalCareConsenterDto.getStagePersonDto()) {
			medicalCareConsenterDto.setStagePersonDto(new StagePersonDto());
		}

		/**
		 * Description: Populating the non form group data into prefill data
		 * GroupName: None BookMark: Condition: None
		 */
		List<BookmarkDto> bookmarkDtoDefaultDtoList = new ArrayList<BookmarkDto>();
		BookmarkDto bkSuffixNmDto = createBookmarkWithCodesTable(BookmarkConstants.CHILD_SUFFIX_NM,
				medicalCareConsenterDto.getPersonDto().getCdPersonSuffix(), CodesConstant.CSUFFIX);
		bookmarkDtoDefaultDtoList.add(bkSuffixNmDto);

		BookmarkDto bkSuffixDto = createBookmarkWithCodesTable(BookmarkConstants.CHILD_SUFFIX,
				medicalCareConsenterDto.getPersonDto().getCdPersonSuffix(), CodesConstant.CSUFFIX);
		bookmarkDtoDefaultDtoList.add(bkSuffixDto);

		BookmarkDto bkFirstDto = createBookmark(BookmarkConstants.CHILD_FIRST,
				medicalCareConsenterDto.getPersonDto().getNmPersonFirst());
		bookmarkDtoDefaultDtoList.add(bkFirstDto);
		BookmarkDto bkFirstNmDto = createBookmark(BookmarkConstants.CHILD_FIRST_NM,
				medicalCareConsenterDto.getPersonDto().getNmPersonFirst());
		bookmarkDtoDefaultDtoList.add(bkFirstNmDto);
		BookmarkDto bkLastDto = createBookmark(BookmarkConstants.CHILD_LAST_NM,
				medicalCareConsenterDto.getPersonDto().getNmPersonLast());
		bookmarkDtoDefaultDtoList.add(bkLastDto);
		BookmarkDto bkLastNmDto = createBookmark(BookmarkConstants.CHILD_LAST,
				medicalCareConsenterDto.getPersonDto().getNmPersonLast());
		bookmarkDtoDefaultDtoList.add(bkLastNmDto);
		BookmarkDto bkMiddleDto = createBookmark(BookmarkConstants.CHILD_MIDDLE,
				medicalCareConsenterDto.getPersonDto().getNmPersonMiddle());
		bookmarkDtoDefaultDtoList.add(bkMiddleDto);
		BookmarkDto bkMiddleNmDto = createBookmark(BookmarkConstants.CHILD_MIDDLE_NM,
				medicalCareConsenterDto.getPersonDto().getNmPersonMiddle());
		bookmarkDtoDefaultDtoList.add(bkMiddleNmDto);

		//Fixed Warranty Defect#12004 Issue to fix duplicate bookmark creation
		if(!ObjectUtils.isEmpty(medicalCareConsenterDto.getMedicalConsenterDtlsList())) {			
			MedicalConsenterDto medicalConsenterDto = medicalCareConsenterDto.getMedicalConsenterDtlsList().stream().
					max(Comparator.comparing(MedicalConsenterDto::getDtMedConsStart)).get();
			BookmarkDto bkdtMedStartDto = createBookmark(BookmarkConstants.DATE_START,
					DateUtils.stringDt(medicalConsenterDto.getDtMedConsStart()));
			bookmarkDtoDefaultDtoList.add(bkdtMedStartDto);
		}

		BookmarkDto bkCountyLegalDto = createBookmarkWithCodesTable(BookmarkConstants.COUNTY,
				medicalCareConsenterDto.getLegalStatusOutDto().getCdLegalStatCnty(), CodesConstant.CCOUNT);
		bookmarkDtoDefaultDtoList.add(bkCountyLegalDto);
		BookmarkDto bkLegalStartCauseDto = createBookmark(BookmarkConstants.CAUSE_NUMBER,
				medicalCareConsenterDto.getLegalStatusOutDto().getTxtLegalStatCauseNbr());
		bookmarkDtoDefaultDtoList.add(bkLegalStartCauseDto);

		BookmarkDto bkCourtNbrDto = createBookmark(BookmarkConstants.JUDICIAL_DISTRICT,
				medicalCareConsenterDto.getLegalStatusOutDto().getValueString());
		bookmarkDtoDefaultDtoList.add(bkCourtNbrDto);
		BookmarkDto bkAddrMailDto = createBookmark(BookmarkConstants.PW_ADDRESS_CITY,
				medicalCareConsenterDto.getEmployeePersPhNameDto().getAddrMailCodeCity());
		bookmarkDtoDefaultDtoList.add(bkAddrMailDto);

		BookmarkDto bkAddrStreetDto = createBookmark(BookmarkConstants.PW_ADDRESS_ST_LN_1,
				medicalCareConsenterDto.getEmployeePersPhNameDto().getAddrMailCodeStLn1());
		bookmarkDtoDefaultDtoList.add(bkAddrStreetDto);

		BookmarkDto bkAddrZipDto = createBookmark(BookmarkConstants.PW_ADDRESS_ZIP,
				medicalCareConsenterDto.getEmployeePersPhNameDto().getAddrMailCodeZip());
		bookmarkDtoDefaultDtoList.add(bkAddrZipDto);

		BookmarkDto bkcdSuffixDto = createBookmarkWithCodesTable(BookmarkConstants.PW_NAME_SUFFIX,
				medicalCareConsenterDto.getEmployeePersPhNameDto().getCdNameSuffix(), CodesConstant.CSUFFIX);
		bookmarkDtoDefaultDtoList.add(bkcdSuffixDto);

		BookmarkDto bkFirstNameDto = createBookmark(BookmarkConstants.PW_NAME_FIRST,
				medicalCareConsenterDto.getEmployeePersPhNameDto().getNmNameFirst());
		bookmarkDtoDefaultDtoList.add(bkFirstNameDto);

		BookmarkDto bkLastNameDto = createBookmark(BookmarkConstants.PW_NAME_LAST,
				medicalCareConsenterDto.getEmployeePersPhNameDto().getNmNameLast());
		bookmarkDtoDefaultDtoList.add(bkLastNameDto);

		BookmarkDto bkMnameDto = createBookmark(BookmarkConstants.PW_NAME_MIDDLE,
				medicalCareConsenterDto.getEmployeePersPhNameDto().getNmNameMiddle());
		bookmarkDtoDefaultDtoList.add(bkMnameDto);

		// creating groups using formDatagroup
		List<FormDataGroupDto> formDataGroupList = new ArrayList<FormDataGroupDto>();

		/**
		 * Creating the prefill data for group med0301-Child's Name Comma group
		 * depending on szCdPersonSuffix is null or not.
		 * 
		 */
		// Commenting out the below Group population, since per legacy form
		// generated(the pdf) there should be no comma for a suffix.
		/*
		 * if(null != medicalCareConsenterDto.getPersonDto() && null !=
		 * medicalCareConsenterDto.getPersonDto().getCdPersonSuffix()){
		 */
		/*
		 * FormDataGroupDto tempChildsNameFrmDataGrpDto =
		 * createFormDataGroup(FormGroupsConstants.TMPLAT_COMMA_CHILD,
		 * FormConstants.EMPTY_STRING);
		 * formDataGroupList.add(tempChildsNameFrmDataGrpDto);
		 */
		// }

		/**
		 * Description: Medical Consentor Name Group GroupName: med0302
		 * BookMark: MED_COMMA Condition: szCdPersonSuffix > null
		 */
		if (null != medicalCareConsenterDto.getMedicalConsenterDtlsList()) {
			for (MedicalConsenterDto medCarePersonDtl : medicalCareConsenterDto.getMedicalConsenterDtlsList()) {

				if (StringUtils.isNotBlank(medCarePersonDtl.getCdNameSuffix())) {
					FormDataGroupDto medConsentNameGrp = createFormDataGroup(FormGroupsConstants.MED_COMMA,
							FormConstants.EMPTY_STRING);
					formDataGroupList.add(medConsentNameGrp);
				}

			}

			/**
			 * Description: Primary Worker Name Comma Group GroupName: med0304
			 * BookMark: TMPLAT_COMMA_2 Condition: szCdPersonSuffix > null
			 */
			if (null != medicalCareConsenterDto.getEmployeePersPhNameDto()
					&& StringUtils.isNotBlank(medicalCareConsenterDto.getEmployeePersPhNameDto().getCdNameSuffix())) {
				FormDataGroupDto medPriWrkrName = createFormDataGroup(FormGroupsConstants.TMPLAT_COMMA_2,
						FormConstants.EMPTY_STRING);
				formDataGroupList.add(medPriWrkrName);
			}

		}

		/**
		 * Description: Medical Consenter Repeater Group GroupName: med0309
		 * BookMark: TMPLAT_CONSENTER Condition:None.
		 */

		if (null != medicalCareConsenterDto.getMedicalConsenterDtlsList()) {

			for (MedicalConsenterDto medCarePersonDtl : medicalCareConsenterDto.getMedicalConsenterDtlsList()) {
				FormDataGroupDto medConsentRepeatgrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_CONSENTER,
						FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkPersonDtlDtoList = new ArrayList<BookmarkDto>();

				BookmarkDto bookmarkPersonDtlMedStartDto = createBookmark(BookmarkConstants.MED_DATE_START,
						DateUtils.stringDt(medCarePersonDtl.getDtMedConsStart()));
				bookmarkPersonDtlDtoList.add(bookmarkPersonDtlMedStartDto);
				BookmarkDto bookmarkPersonDtlMedConTypeDto = createBookmark(BookmarkConstants.MED_CONSENTER_TYPE,
						medCarePersonDtl.getCdMedConsenterType());
				bookmarkPersonDtlDtoList.add(bookmarkPersonDtlMedConTypeDto);
				BookmarkDto bkPersDtlSuffix = createBookmark(BookmarkConstants.MED_NAME_SUFFIX,
						medCarePersonDtl.getCdNameSuffix());
				bookmarkPersonDtlDtoList.add(bkPersDtlSuffix);
				BookmarkDto bkPersDtlFirst = createBookmark(BookmarkConstants.MED_NAME_FIRST,
						medCarePersonDtl.getNmNameFirst());
				bookmarkPersonDtlDtoList.add(bkPersDtlFirst);
				BookmarkDto bkPersDtlLast = createBookmark(BookmarkConstants.MED_NAME_LAST,
						medCarePersonDtl.getNmNameLast());
				bookmarkPersonDtlDtoList.add(bkPersDtlLast);
				BookmarkDto bkPersDtlMiddle = createBookmark(BookmarkConstants.MED_NAME_MIDDLE,
						medCarePersonDtl.getNmNameMiddle());
				bookmarkPersonDtlDtoList.add(bkPersDtlMiddle);

				medConsentRepeatgrpDto.setBookmarkDtoList(bookmarkPersonDtlDtoList);
				formDataGroupList.add(medConsentRepeatgrpDto);
			}

		}

		/**
		 * Description: County Court at Law Group GroupName: med0306 BookMark:
		 * TMPLAT_CCL Condition: txtLegalStatCourtNbrPrefix = CCL
		 */
		if (null != medicalCareConsenterDto.getLegalStatusOutDto()
				&& null != medicalCareConsenterDto.getLegalStatusOutDto().getPrefixString()) {

			if (ServiceConstants.CCL
					.equalsIgnoreCase(medicalCareConsenterDto.getLegalStatusOutDto().getPrefixString())) {
				FormDataGroupDto medCourtLawGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_CCL,
						FormConstants.EMPTY_STRING);
				formDataGroupList.add(medCourtLawGroup);
			}

			/**
			 * Description: Judicial District Court Group GroupName: med0307
			 * BookMark: TMPLAT_JDC Condition: txtLegalStatCourtNbrPrefix = JDC
			 */
			else if (ServiceConstants.JDC
					.equalsIgnoreCase(medicalCareConsenterDto.getLegalStatusOutDto().getPrefixString())) {
				FormDataGroupDto medJudiCourtLawGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_JDC,
						FormConstants.EMPTY_STRING);
				formDataGroupList.add(medJudiCourtLawGroup);
			}

			/**
			 * Description: Other Court Group GroupName: med0308 BookMark:
			 * TMPLAT_COURT_OTHER Condition: txtLegalStatCourtNbrPrefix = JDC
			 */
			else {
				FormDataGroupDto medOtherCourtLawGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_COURT_OTHER,
						FormConstants.EMPTY_STRING);
				formDataGroupList.add(medOtherCourtLawGroup);
			}
		}

		/**
		 * Description: Primary Worker Address Street Line 2 Group GroupName:
		 * med0303 BookMark: TMPLAT_STREET2 Condition: addrMailCodeStLn2 > null
		 */
		if (null != medicalCareConsenterDto.getEmployeePersPhNameDto()
				&& null != medicalCareConsenterDto.getEmployeePersPhNameDto().getAddrMailCodeStLn2()) {
			FormDataGroupDto medCarePriAddrLn = createFormDataGroup(FormGroupsConstants.TMPLAT_STREET2,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> bkPriWrksDtoList = new ArrayList<BookmarkDto>();
			BookmarkDto bkPriWrkrAddrLn = createBookmark(BookmarkConstants.PW_ADDRESS_ST_LN_2,
					medicalCareConsenterDto.getEmployeePersPhNameDto().getAddrMailCodeStLn2());
			bkPriWrksDtoList.add(bkPriWrkrAddrLn);
			medCarePriAddrLn.setBookmarkDtoList(bkPriWrksDtoList);
			formDataGroupList.add(medCarePriAddrLn);
		}

		/**
		 * Description: Primary Worker Business Phone Group GroupName: med0305
		 * BookMark: TMPLAT_PW_BUSINESS_PHONE Condition: nbrPhone > null
		 */
		if (null != medicalCareConsenterDto.getPersonPhoneMedCareDto()
				&& null != medicalCareConsenterDto.getPersonPhoneMedCareDto().getNbrPhone()) {
			FormDataGroupDto medPriWrkrPh = createFormDataGroup(FormGroupsConstants.TMPLAT_PW_BUSINESS_PHONE,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> bkPriWrksPhDtoList = new ArrayList<BookmarkDto>();
			BookmarkDto bkPriWrkrBusinessPh = createBookmark(BookmarkConstants.PW_BUSINESS_PHONE,
					TypeConvUtil.formatPhone(medicalCareConsenterDto.getPersonPhoneMedCareDto().getNbrPhone()));
			bkPriWrksPhDtoList.add(bkPriWrkrBusinessPh);
			medPriWrkrPh.setBookmarkDtoList(bkPriWrksPhDtoList);
			formDataGroupList.add(medPriWrkrPh);
		}

		PreFillDataServiceDto preFillData = new PreFillDataServiceDto();
		preFillData.setFormDataGroupList(formDataGroupList);
		preFillData.setBookmarkDtoList(bookmarkDtoDefaultDtoList);
		return preFillData;
	}

}
