package us.tx.state.dfps.service.forms.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.fsna.dto.CpsFsnaDomainLookupDto;
import us.tx.state.dfps.fsna.dto.CpsFsnaDto;
import us.tx.state.dfps.fsna.dto.CpsFsnaPrtyStrngthNeedDto;
import us.tx.state.dfps.fsna.dto.CpsFsnaRspnDto;
import us.tx.state.dfps.service.common.BookmarkConstants;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.FormConstants;
import us.tx.state.dfps.service.common.FormGroupsConstants;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.CodesDao;
import us.tx.state.dfps.service.common.response.FSNAAssessmentDtlGetRes;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.common.utils.CaseUtils;
import us.tx.state.dfps.service.forms.dto.BookmarkDto;
import us.tx.state.dfps.service.forms.dto.FormDataGroupDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.person.dao.PersonDao;
import us.tx.state.dfps.service.person.dto.PersonListDto;
import us.tx.state.dfps.service.workload.dto.PersonDto;

@Component
/**
 * Name:MedicalConsenterForNonDFPSEmployeePrefillData Description:
 * MedicalConsenterForNonDFPSEmployeePrefillData will implemented
 * returnPrefillData operation defined in DocumentServiceUtil Interface to
 * populate the prefill data for FSNA forms in stages FPR, FSU, FRE Jan 04, 2018
 * - 04:40:29 PM
 */
public class SDMFamilyStrengthsAndNeedsAssessmentFBSSPrefillData extends DocumentServiceUtil {

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
	private CaseUtils caseUtils;

	@Autowired
	private PersonDao personDao;
	
	@Autowired
	private CodesDao codesDao;
	
	// various literals
	private static final String INITIAL = "Initial";
	private static final String REASSESSMENT = "Reassessment";

	@Override
	public PreFillDataServiceDto returnPrefillData(Object parentDtoobj){

		FSNAAssessmentDtlGetRes prefillDto = (FSNAAssessmentDtlGetRes) parentDtoobj;

		List<BookmarkDto> bookmarkNonFrmGrpList = new ArrayList<BookmarkDto>();
		List<FormDataGroupDto> formDataGroupList = new ArrayList<FormDataGroupDto>();

		// Get person map for easy access
		Map<Long, PersonListDto> personsMap = new LinkedHashMap<Long, PersonListDto>();
		if (!ObjectUtils.isEmpty(prefillDto.getPersons())) {
			prefillDto.getPersons().stream().forEach(o -> personsMap.put(o.getIdPerson(), o));
		}

		/**QCR 62702 SDM Removal-Change of title depending on Date of Assessment completed
		 * Before or After 9/1/2020
		 * artf159087, artf159088,artf160001 **/
		BookmarkDto bookmarkTitleForForm = null;
		
		boolean showSDMTitle=showSDM(prefillDto.getCpsFsnaDto());
		if(showSDMTitle){
			bookmarkTitleForForm = createBookmark(BookmarkConstants.TXT_SDM_FSNA_TITLE, FormConstants.TXT_SDMTITLE);
		}else{
			bookmarkTitleForForm = createBookmark(BookmarkConstants.TXT_SDM_FSNA_TITLE,FormConstants.EMPTY_STRING);
		}		
		
		bookmarkNonFrmGrpList.add(bookmarkTitleForForm);
		/**End of QCR 62702**/
		
		// Assessment type CVS/FBSS
		boolean isCVSAssessment = ServiceConstants.CSTAGES_FSU.equals(prefillDto.getCpsFsnaDto().getCdStage())
				|| ServiceConstants.CSTAGES_FRE.equals(prefillDto.getCpsFsnaDto().getCdStage());

		boolean isFBSSAssessment = !isCVSAssessment;

		// Populate the Case Nm
		BookmarkDto bookmarkCaseNm = createBookmark(BookmarkConstants.TITLE_CASE_NAME,
				caseUtils.getNmCase(prefillDto.getCpsFsnaDto().getIdCase()));
		bookmarkNonFrmGrpList.add(bookmarkCaseNm);

		// Populate Case ID
		BookmarkDto bookmarkCaseID = createBookmark(BookmarkConstants.TITLE_CASE_NBR,
				prefillDto.getCpsFsnaDto().getIdCase());
		bookmarkNonFrmGrpList.add(bookmarkCaseID);

		BookmarkDto bookmarkDtAssessment = createBookmark(BookmarkConstants.DATE_OF_ASSESSMENT,
				TypeConvUtil.formDateFormat(prefillDto.getCpsFsnaDto().getDtOfAsgnmnt()));
		bookmarkNonFrmGrpList.add(bookmarkDtAssessment);

		// get primary caregiver
		PersonDto primaryCaregiver = personDao.getPersonById(prefillDto.getCpsFsnaDto().getIdPrmryCrgvrPrnt());

		// Populate Primary CareGiver First Name
		BookmarkDto bookmarkCareGiverFirstNm = createBookmark(BookmarkConstants.TITLE_NAME_FIRST,
				primaryCaregiver.getNmPersonFirst());
		bookmarkNonFrmGrpList.add(bookmarkCareGiverFirstNm);

		// Populate Primary CareGiver Last Name
		BookmarkDto bookmarkCareGiverLastNm = createBookmark(BookmarkConstants.TITLE_NAME_LAST,
				primaryCaregiver.getNmPersonLast());
		bookmarkNonFrmGrpList.add(bookmarkCareGiverLastNm);

		// Populate Primary CareGiver Middle Name
		BookmarkDto bookmarkCareGiverMiddleNm = createBookmark(BookmarkConstants.TITLE_NAME_MIDDLE,
				primaryCaregiver.getNmPersonMiddle());
		bookmarkNonFrmGrpList.add(bookmarkCareGiverMiddleNm);

		// Populate Primary CareGiver Suffix
		if (StringUtils.isNotBlank(primaryCaregiver.getCdPersonSuffix())) {
			FormDataGroupDto commaGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_COMMA,
					FormConstants.EMPTY_STRING);
			formDataGroupList.add(commaGroupDto);
		}
		BookmarkDto bookmarkCareGiverSuffix = createBookmark(BookmarkConstants.TITLE_NAME_SUFFIX,
				primaryCaregiver.getCdPersonSuffix());
		bookmarkNonFrmGrpList.add(bookmarkCareGiverSuffix);

		// get secondary caregiver
		PersonDto secondaryCaregiver = null;
		if (!ObjectUtils.isEmpty(prefillDto.getCpsFsnaDto().getIdSecndryCrgvrPrnt())) {
			secondaryCaregiver = personDao.getPersonById(prefillDto.getCpsFsnaDto().getIdSecndryCrgvrPrnt());
			if (!ObjectUtils.isEmpty(secondaryCaregiver)) {
				FormDataGroupDto secondaryCaregiverGroupDto = createFormDataGroup(
						FormGroupsConstants.TMPLAT_CAREGIVER_SECN, FormConstants.EMPTY_STRING);
				List<FormDataGroupDto> secondaryCaregiverGroupList = new ArrayList<FormDataGroupDto>();
				List<BookmarkDto> bookmarkSecondaryCaregiverList = new ArrayList<BookmarkDto>();

				// Populate Secondary CareGiver First Name
				BookmarkDto bookmarkCareGiverSecFirstNm = createBookmark(BookmarkConstants.TITLE_SECN_NAME_FIRST,
						secondaryCaregiver.getNmPersonFirst());
				bookmarkSecondaryCaregiverList.add(bookmarkCareGiverSecFirstNm);

				// Populate Secondary CareGiver Last Name
				BookmarkDto bookmarkCareGiverSecLastNm = createBookmark(BookmarkConstants.TITLE_SECN_NAME_LAST,
						secondaryCaregiver.getNmPersonLast());
				bookmarkSecondaryCaregiverList.add(bookmarkCareGiverSecLastNm);

				// Populate Secondary CareGiver Middle Name
				BookmarkDto bookmarkCareGiverSecMiddleNm = createBookmark(BookmarkConstants.TITLE_SECN_NAME_MIDDLE,
						secondaryCaregiver.getNmPersonMiddle());
				bookmarkSecondaryCaregiverList.add(bookmarkCareGiverSecMiddleNm);

				// Populate Secondary CareGiver Suffix
				if (StringUtils.isNotBlank(secondaryCaregiver.getCdPersonSuffix())) {
					FormDataGroupDto commaGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_COMMA,
							FormGroupsConstants.TMPLAT_CAREGIVER_SECN);
					secondaryCaregiverGroupList.add(commaGroupDto);
				}
				BookmarkDto bookmarkCareGiverSecSuffix = createBookmark(BookmarkConstants.TITLE_SECN_NAME_SUFFIX,
						secondaryCaregiver.getCdPersonSuffix());
				bookmarkSecondaryCaregiverList.add(bookmarkCareGiverSecSuffix);

				secondaryCaregiverGroupDto.setBookmarkDtoList(bookmarkSecondaryCaregiverList);
				secondaryCaregiverGroupDto.setFormDataGroupList(secondaryCaregiverGroupList);
				formDataGroupList.add(secondaryCaregiverGroupDto);
			}
		}

		// Populate Children Assessed
		if (!ObjectUtils.isEmpty(prefillDto.getSavedChildAssessed())) {
			for (Long idPersonChild : prefillDto.getSavedChildAssessed()) {
				// FormDataGroups for CHILDREN ASSESSED Section
				PersonListDto personDto = personsMap.get(idPersonChild);
				// Defect 13754, To fix null pointer exception issue if the
				// person is not a PRN anymore
				if (!ObjectUtils.isEmpty(personDto)) {
					FormDataGroupDto childAssessedGroupDto = createFormDataGroup(
							FormGroupsConstants.TMPLAT_CHILD_ASSESSED, FormConstants.EMPTY_STRING);
					List<BookmarkDto> bookmarkChildAssessedList = new ArrayList<BookmarkDto>();

					BookmarkDto bookmarkChAssessedFirstNm = createBookmark(BookmarkConstants.CHILD_NM_FIRST,
							personDto.getNmPersonFirst());
					bookmarkChildAssessedList.add(bookmarkChAssessedFirstNm);

					BookmarkDto bookmarkChAssessedLastNm = createBookmark(BookmarkConstants.CHILD_NM_LAST,
							personDto.getNmpersonLast());
					bookmarkChildAssessedList.add(bookmarkChAssessedLastNm);

					BookmarkDto bookmarkChildAge = createBookmark(BookmarkConstants.CHILD_AGE,
							personDto.getPersonAge());
					bookmarkChildAssessedList.add(bookmarkChildAge);

					BookmarkDto bookmarkChildRelatn = createBookmarkWithCodesTable(BookmarkConstants.CHILD_RELATIONSHIP,
							personDto.getStagePersRelInt(), CodesConstant.CRELVICT);
					bookmarkChildAssessedList.add(bookmarkChildRelatn);

					childAssessedGroupDto.setBookmarkDtoList(bookmarkChildAssessedList);
					formDataGroupList.add(childAssessedGroupDto);
				}
			}
		}

		if (ServiceConstants.Y.equals(prefillDto.getCpsFsnaDto().getIndExcptnExists())) {
			FormDataGroupDto exceptionGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_EXCEPTION,
					FormConstants.EMPTY_STRING);
			List<FormDataGroupDto> exceptionGroupList = new ArrayList<FormDataGroupDto>();
			List<BookmarkDto> bookmarkExceptionList = new ArrayList<BookmarkDto>();

			if (ServiceConstants.CCOR_040.equals(prefillDto.getCpsFsnaDto().getCdExcptnRsn())
					|| StringUtils.isNotEmpty(prefillDto.getCpsFsnaDto().getTxtOtherExcptnRsn())) {
				FormDataGroupDto exceptionOtherGroupDto = createFormDataGroup(
						FormGroupsConstants.TMPLAT_EXCEPTION_OTHER, FormGroupsConstants.TMPLAT_EXCEPTION);
				List<BookmarkDto> bookmarkExceptionOtherList = new ArrayList<BookmarkDto>();

				BookmarkDto bookmarkExceptnOther = createBookmark(BookmarkConstants.EXCEPTION_OTHER_DESC,
						formatTextValue(prefillDto.getCpsFsnaDto().getTxtOtherExcptnRsn()));
				bookmarkExceptionOtherList.add(bookmarkExceptnOther);

				exceptionOtherGroupDto.setBookmarkDtoList(bookmarkExceptionOtherList);
				exceptionGroupList.add(exceptionOtherGroupDto);
			} else {
				BookmarkDto bookmarkExceptionExists = createBookmarkWithCodesTable(BookmarkConstants.EXCEPTION_EXISTS,
						prefillDto.getCpsFsnaDto().getCdExcptnRsn(), "EXCPTRSN");
				bookmarkExceptionList.add(bookmarkExceptionExists);
			}

			exceptionGroupDto.setBookmarkDtoList(bookmarkExceptionList);
			exceptionGroupDto.setFormDataGroupList(exceptionGroupList);
			formDataGroupList.add(exceptionGroupDto);
		}

		// FPOS section only for FSU
		if (ServiceConstants.CSTAGES_FSU.equals(prefillDto.getCpsFsnaDto().getCdStage())
				&& ServiceConstants.Y.equals(prefillDto.getCpsFsnaDto().getIndFposReqrd())) {
			FormDataGroupDto fposGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_FPOS,
					FormConstants.EMPTY_STRING);
			List<FormDataGroupDto> fposGroupList = new ArrayList<FormDataGroupDto>();
			List<BookmarkDto> bookmarkFposList = new ArrayList<BookmarkDto>();

			if (ServiceConstants.CCOR_020.equals(prefillDto.getCpsFsnaDto().getCdFposReqrdRsn())) {
				FormDataGroupDto fposOtherGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_FPOS_OTHER,
						FormGroupsConstants.TMPLAT_FPOS);

				List<BookmarkDto> bookmarkFposOtherList = new ArrayList<BookmarkDto>();
				BookmarkDto bookmarkFposOtherDesc = createBookmark(BookmarkConstants.FPOS_OTHER_DESC,
						formatTextValue(prefillDto.getCpsFsnaDto().getTxtOtherFposRsn()));
				bookmarkFposOtherList.add(bookmarkFposOtherDesc);

				fposOtherGroupDto.setBookmarkDtoList(bookmarkFposOtherList);
				fposGroupList.add(fposOtherGroupDto);
			} else {
				BookmarkDto bookmarkFposRsn = createBookmarkWithCodesTable(BookmarkConstants.FPOS_RSN,
						prefillDto.getCpsFsnaDto().getCdFposReqrdRsn(), "FPOSRSN");
				bookmarkFposList.add(bookmarkFposRsn);
			}

			fposGroupDto.setBookmarkDtoList(bookmarkFposList);
			fposGroupDto.setFormDataGroupList(fposGroupList);
			formDataGroupList.add(fposGroupDto);
		}

		// Populate Assessment Type

		if (prefillDto.getCpsFsnaDto().getCdAsgnmntType().equals("INIT")) {
			BookmarkDto bookmarkAssessmentType = createBookmark(BookmarkConstants.ASSESSMENT_TYPE, INITIAL);
			bookmarkNonFrmGrpList.add(bookmarkAssessmentType);
		} else if (prefillDto.getCpsFsnaDto().getCdAsgnmntType().equals("REAS")) {
			BookmarkDto bookmarkAssessmentType = createBookmark(BookmarkConstants.ASSESSMENT_TYPE, REASSESSMENT);
			bookmarkNonFrmGrpList.add(bookmarkAssessmentType);
		}

		// Populate Danger/Worry from Past FSNA
		BookmarkDto bookmarkDangrWorryFSNA = createBookmark(BookmarkConstants.DANGER_PAST_FSNA,
				formatTextValue(prefillDto.getCpsFsnaDto().getPreviousTxtDngrWorry()));
		bookmarkNonFrmGrpList.add(bookmarkDangrWorryFSNA);

		// Populate Danger/Worry from Current FSNA
		BookmarkDto bookmarkDangrCurrWorryFSNA = createBookmark(BookmarkConstants.DANGER_CURRENT_FSNA,
				formatTextValue(prefillDto.getCpsFsnaDto().getTxtDngrWorry()));
		bookmarkNonFrmGrpList.add(bookmarkDangrCurrWorryFSNA);

		// Populate Goal Past FSNA
		BookmarkDto bookmarkPastFSNA = createBookmark(BookmarkConstants.GOAL_PAST_FSNA,
				formatTextValue(prefillDto.getCpsFsnaDto().getPrevioustxtGoalStatmnts()));
		bookmarkNonFrmGrpList.add(bookmarkPastFSNA);

		// Populate Goal Current FSNA
		BookmarkDto bookmarkCurrFSNA = createBookmark(BookmarkConstants.GOAL_CURRENT_FSNA,
				formatTextValue(prefillDto.getCpsFsnaDto().getTxtGoalStatmnts()));
		bookmarkNonFrmGrpList.add(bookmarkCurrFSNA);

		// Populate Caregiver sections
		Set<String> caregiverKeys = prefillDto.getCareGiverSectionsMap().keySet();
		for (String key : caregiverKeys) {
			FormDataGroupDto caregiverSectionGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_CAREGIVER_SCTN,
					FormConstants.EMPTY_STRING);
			List<FormDataGroupDto> caregiverSectionGroupList = new ArrayList<FormDataGroupDto>();
			List<BookmarkDto> bookmarkCaregiverSectionList = new ArrayList<BookmarkDto>();
			List<CpsFsnaDomainLookupDto> domainList = prefillDto.getCareGiverSectionsMap().get(key);

			// Section name

			BookmarkDto bookmarkSectionName = createBookmarkWithCodesTable(BookmarkConstants.SCTN_NAME, key,
					"FSNASCTN");

			if (isCVSAssessment) {
				bookmarkSectionName.setBookmarkData(
						StringUtils.replace(bookmarkSectionName.getBookmarkData(), "Caregiver", "Parent"));
			}

			if (ServiceConstants.CCOR_040.equals(key)) {
				bookmarkSectionName.setBookmarkData(ServiceConstants.OTHER);
			}

			bookmarkCaregiverSectionList.add(bookmarkSectionName);

			for (CpsFsnaDomainLookupDto domainDto : domainList) {
				FormDataGroupDto domainGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_DOMAIN,
						FormGroupsConstants.TMPLAT_CAREGIVER_SCTN);
				List<FormDataGroupDto> domainGroupList = new ArrayList<FormDataGroupDto>();
				List<BookmarkDto> bookmarkDomainList = new ArrayList<BookmarkDto>();

				// Domain name
				BookmarkDto bookmarkDomainName = createBookmarkWithCodesTable(BookmarkConstants.DOMN_NAME,
						domainDto.getCdFsnaDmn(), "FSNADOMN");

				if (isCVSAssessment) {
					bookmarkDomainName.setBookmarkData(
							StringUtils.replace(bookmarkDomainName.getBookmarkData(), "Caregiver", "Parent"));
				}

				String bookmarkData = bookmarkDomainName.getBookmarkData();
				bookmarkDomainName.setBookmarkData(domainDto.getTxtDmnDisplayOrder() + ". " + bookmarkData);
				bookmarkDomainList.add(bookmarkDomainName);

				// N/A column for domain 10
				// Defect# 12902 - Added a FBSSAssessment indicator to include
				// the NA check box in the Caregiver and Child/Youth section in
				// FSNA FBSS form
				if ((isFBSSAssessment || isCVSAssessment)
						&& ServiceConstants.STRING_TEN.equals(domainDto.getTxtDmnDisplayOrder())) {
					FormDataGroupDto naGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_NA,
							FormGroupsConstants.TMPLAT_DOMAIN);
					domainGroupList.add(naGroupDto);

					FormDataGroupDto naPrCheckBoxGroupDto = createFormDataGroup(
							FormGroupsConstants.TMPLAT_NA_PR_CHECKBOX, FormGroupsConstants.TMPLAT_DOMAIN);
					List<FormDataGroupDto> naPrCheckBoxGroupList = new ArrayList<FormDataGroupDto>();

					// Defect# 12902- Added a or condition NA check box for both
					// primary and secondary parents
					for (CpsFsnaRspnDto responseDto : domainDto.getFsnaRspns()) {
						if (responseDto.getIdRelPrsn().equals(prefillDto.getCpsFsnaDto().getIdPrmryCrgvrPrnt())
								&& ServiceConstants.NAAP.equals(responseDto.getCdAnswr())) {
							FormDataGroupDto primaryNaGroupDto = createFormDataGroup(
									FormGroupsConstants.TMPLAT_PRIMARY_NA, FormGroupsConstants.TMPLAT_NA_PR_CHECKBOX);
							naPrCheckBoxGroupList.add(primaryNaGroupDto);
						}
					}

					naPrCheckBoxGroupDto.setFormDataGroupList(naPrCheckBoxGroupList);
					domainGroupList.add(naPrCheckBoxGroupDto);
				}

				// Primary caregiver name
				BookmarkDto bookmarkPrimaryNmFirst = createBookmark(BookmarkConstants.PRIMARY_NAME_FIRST,
						primaryCaregiver.getNmPersonFirst());
				bookmarkDomainList.add(bookmarkPrimaryNmFirst);
				BookmarkDto bookmarkPrimaryNmMiddle = createBookmark(BookmarkConstants.PRIMARY_NAME_MIDDLE,
						primaryCaregiver.getNmPersonMiddle());
				bookmarkDomainList.add(bookmarkPrimaryNmMiddle);
				BookmarkDto bookmarkPrimaryNmLast = createBookmark(BookmarkConstants.PRIMARY_NAME_LAST,
						primaryCaregiver.getNmPersonLast());
				bookmarkDomainList.add(bookmarkPrimaryNmLast);
				BookmarkDto bookmarkPrimaryNmSuffix = createBookmark(BookmarkConstants.PRIMARY_NAME_SUFFIX,
						primaryCaregiver.getCdPersonSuffix());
				bookmarkDomainList.add(bookmarkPrimaryNmSuffix);
				if (StringUtils.isNotBlank(primaryCaregiver.getCdPersonSuffix())) {
					FormDataGroupDto commaGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_COMMA,
							FormGroupsConstants.TMPLAT_DOMAIN);
					domainGroupList.add(commaGroupDto);
				}

				// Strength/no need identified/needs identified check boxes
				// Defect# 12902 - Ten questions of primary cargiver section has
				// to be added to all the three stages hence moved the code to
				// outside the if else block
				for (CpsFsnaRspnDto responseDto : domainDto.getFsnaRspns()) {
					// Strength and No Need are the same column for FRE and FSU
					if (responseDto.getIdRelPrsn().equals(prefillDto.getCpsFsnaDto().getIdPrmryCrgvrPrnt())) {
						if (isCVSAssessment) {
							if (ServiceConstants.SNND.equals(responseDto.getCdAnswr())) {
								FormDataGroupDto primaryStrengthGroupDto = createFormDataGroup(
										FormGroupsConstants.TMPLAT_PRIMARY_STRENGTH, FormGroupsConstants.TMPLAT_DOMAIN);
								domainGroupList.add(primaryStrengthGroupDto);
							} else if (ServiceConstants.NEED.equals(responseDto.getCdAnswr())) {
								FormDataGroupDto primaryNeedGroupDto = createFormDataGroup(
										FormGroupsConstants.TMPLAT_PRIMARY_NEED, FormGroupsConstants.TMPLAT_DOMAIN);
								domainGroupList.add(primaryNeedGroupDto);
							}
						} else if (isFBSSAssessment) {
							if (ServiceConstants.STRENGTH.equals(responseDto.getCdAnswr())) {
								FormDataGroupDto primaryStrengthGroupDto = createFormDataGroup(
										FormGroupsConstants.TMPLAT_PRIMARY_STRENGTH, FormGroupsConstants.TMPLAT_DOMAIN);
								domainGroupList.add(primaryStrengthGroupDto);
							} else if (ServiceConstants.NEED.equals(responseDto.getCdAnswr())) {
								FormDataGroupDto primaryNeedGroupDto = createFormDataGroup(
										FormGroupsConstants.TMPLAT_PRIMARY_NEED, FormGroupsConstants.TMPLAT_DOMAIN);
								domainGroupList.add(primaryNeedGroupDto);
							} else if (ServiceConstants.NONEED.equals(responseDto.getCdAnswr())) {
								FormDataGroupDto primaryNoNeedGroupDto = createFormDataGroup(
										FormGroupsConstants.TMPLAT_PRIMARY_NONEED, FormGroupsConstants.TMPLAT_DOMAIN);
								domainGroupList.add(primaryNoNeedGroupDto);
							}
						}

						if (ServiceConstants.STRING_TEN.equals(domainDto.getTxtDmnDisplayOrder())) {
							BookmarkDto bookmarkColSpan = createBookmark(BookmarkConstants.PRIM_DMN_TXT_COL_SPAN, 4);
							bookmarkDomainList.add(bookmarkColSpan);
						} else {
							BookmarkDto bookmarkColSpan = createBookmark(BookmarkConstants.PRIM_DMN_TXT_COL_SPAN, 3);
							bookmarkDomainList.add(bookmarkColSpan);
						}

						BookmarkDto bookmarkDomainDesc = createBookmark(BookmarkConstants.DOMN_DESC,
								formatTextValue(responseDto.getTxtDesc()));
						bookmarkDomainList.add(bookmarkDomainDesc);
					}
				}

				// Secondary caregiver section
				if (!ObjectUtils.isEmpty(secondaryCaregiver)) {
					FormDataGroupDto domainSecondaryGroupDto = createFormDataGroup(
							FormGroupsConstants.TMPLAT_DOMAIN_SECONDARY, FormGroupsConstants.TMPLAT_DOMAIN);
					List<FormDataGroupDto> domainSecondaryGroupList = new ArrayList<FormDataGroupDto>();
					List<BookmarkDto> bookmarkDomainSecondaryList = new ArrayList<BookmarkDto>();

					BookmarkDto bookmarkCareGiverSecFirstNm = createBookmark(BookmarkConstants.SECONDARY_NAME_FIRST,
							secondaryCaregiver.getNmPersonFirst());
					bookmarkDomainSecondaryList.add(bookmarkCareGiverSecFirstNm);

					BookmarkDto bookmarkCareGiverSecLastNm = createBookmark(BookmarkConstants.SECONDARY_NAME_LAST,
							secondaryCaregiver.getNmPersonLast());
					bookmarkDomainSecondaryList.add(bookmarkCareGiverSecLastNm);

					BookmarkDto bookmarkCareGiverSecMiddleNm = createBookmark(BookmarkConstants.SECONDARY_NAME_MIDDLE,
							secondaryCaregiver.getNmPersonMiddle());
					bookmarkDomainSecondaryList.add(bookmarkCareGiverSecMiddleNm);

					if (StringUtils.isNotBlank(secondaryCaregiver.getCdPersonSuffix())) {
						FormDataGroupDto commaGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_COMMA_1,
								FormGroupsConstants.TMPLAT_DOMAIN_SECONDARY);
						domainSecondaryGroupList.add(commaGroupDto);
					}
					BookmarkDto bookmarkCareGiverSecSuffix = createBookmark(BookmarkConstants.SECONDARY_NAME_SUFFIX,
							secondaryCaregiver.getCdPersonSuffix());
					bookmarkDomainSecondaryList.add(bookmarkCareGiverSecSuffix);

					// N/A column for domain 10
					// Defect# 12902 - Added a FBSSAssessment indicator to
					// include the NA check box in the Caregiver and Child/Youth
					// section in FSNA FBSS form
					if ((isFBSSAssessment || isCVSAssessment)
							&& ServiceConstants.STRING_TEN.equals(domainDto.getTxtDmnDisplayOrder())) {
						FormDataGroupDto naSeCheckBoxGroupDto = createFormDataGroup(
								FormGroupsConstants.TMPLAT_NA_SE_CHECKBOX, FormGroupsConstants.TMPLAT_DOMAIN_SECONDARY);
						List<FormDataGroupDto> naSeCheckBoxGroupList = new ArrayList<FormDataGroupDto>();

						for (CpsFsnaRspnDto responseDto : domainDto.getFsnaRspns()) {
							if (responseDto.getIdRelPrsn().equals(prefillDto.getCpsFsnaDto().getIdSecndryCrgvrPrnt())
									&& ServiceConstants.NAAP.equals(responseDto.getCdAnswr())) {
								FormDataGroupDto secondaryNaGroupDto = createFormDataGroup(
										FormGroupsConstants.TMPLAT_SECONDARY_NA,
										FormGroupsConstants.TMPLAT_NA_SE_CHECKBOX);
								naSeCheckBoxGroupList.add(secondaryNaGroupDto);
							}
						}

						naSeCheckBoxGroupDto.setFormDataGroupList(naSeCheckBoxGroupList);
						domainSecondaryGroupList.add(naSeCheckBoxGroupDto);
					}

					// Strength/need checkboxes
					// Defect# 12902 - Ten questions of secondary cargiver
					// section has to be added to all the three stages hence
					// moved the code to outside the if else block
					for (CpsFsnaRspnDto responseDto : domainDto.getFsnaRspns()) {
						if (responseDto.getIdRelPrsn().equals(prefillDto.getCpsFsnaDto().getIdSecndryCrgvrPrnt())) {
							// Strength and No Need are in same column for FRE
							// and FPR

							if (isCVSAssessment) {
								if (ServiceConstants.SNND.equals(responseDto.getCdAnswr())) {
									FormDataGroupDto secondaryStrengthGroupDto = createFormDataGroup(
											FormGroupsConstants.TMPLAT_SECONDARY_STRENGTH,
											FormGroupsConstants.TMPLAT_DOMAIN);
									domainSecondaryGroupList.add(secondaryStrengthGroupDto);
								} else if (ServiceConstants.NEED.equals(responseDto.getCdAnswr())) {
									FormDataGroupDto secondaryNeedGroupDto = createFormDataGroup(
											FormGroupsConstants.TMPLAT_SECONDARY_NEED,
											FormGroupsConstants.TMPLAT_DOMAIN);
									domainSecondaryGroupList.add(secondaryNeedGroupDto);
								}

							} else if (isFBSSAssessment) {
								if (ServiceConstants.STRENGTH.equals(responseDto.getCdAnswr())) {
									FormDataGroupDto secondaryStrengthGroupDto = createFormDataGroup(
											FormGroupsConstants.TMPLAT_SECONDARY_STRENGTH,
											FormGroupsConstants.TMPLAT_DOMAIN);
									domainSecondaryGroupList.add(secondaryStrengthGroupDto);
								} else if (ServiceConstants.NEED.equals(responseDto.getCdAnswr())) {
									FormDataGroupDto secondaryNeedGroupDto = createFormDataGroup(
											FormGroupsConstants.TMPLAT_SECONDARY_NEED,
											FormGroupsConstants.TMPLAT_DOMAIN);
									domainSecondaryGroupList.add(secondaryNeedGroupDto);
								} else if (ServiceConstants.NONEED.equals(responseDto.getCdAnswr())) {
									FormDataGroupDto secondaryNoNeedGroupDto = createFormDataGroup(
											FormGroupsConstants.TMPLAT_SECONDARY_NONEED,
											FormGroupsConstants.TMPLAT_DOMAIN);
									domainSecondaryGroupList.add(secondaryNoNeedGroupDto);
								}
							}

							if (ServiceConstants.STRING_TEN.equals(domainDto.getTxtDmnDisplayOrder())) {
								BookmarkDto bookmarkColSpan = createBookmark(BookmarkConstants.SECN_DMN_TXT_COL_SPAN,
										4);
								bookmarkDomainSecondaryList.add(bookmarkColSpan);
							} else {
								BookmarkDto bookmarkColSpan = createBookmark(BookmarkConstants.SECN_DMN_TXT_COL_SPAN,
										3);
								bookmarkDomainSecondaryList.add(bookmarkColSpan);
							}

							BookmarkDto bookmarkDomainDesc = createBookmark(BookmarkConstants.DOMN_DESC_SECN,
									formatTextValue(responseDto.getTxtDesc()));
							bookmarkDomainSecondaryList.add(bookmarkDomainDesc);
						}
					}

					domainSecondaryGroupDto.setBookmarkDtoList(bookmarkDomainSecondaryList);
					domainSecondaryGroupDto.setFormDataGroupList(domainSecondaryGroupList);
					domainGroupList.add(domainSecondaryGroupDto);
				}

				domainGroupDto.setBookmarkDtoList(bookmarkDomainList);
				domainGroupDto.setFormDataGroupList(domainGroupList);
				caregiverSectionGroupList.add(domainGroupDto);
			}

			caregiverSectionGroupDto.setBookmarkDtoList(bookmarkCaregiverSectionList);
			caregiverSectionGroupDto.setFormDataGroupList(caregiverSectionGroupList);
			formDataGroupList.add(caregiverSectionGroupDto);
		}

		// Populate Child sections
		Set<String> childKeys = prefillDto.getChildSectionsMap().keySet();
		if (!ObjectUtils.isEmpty(childKeys)) {
			for (String key : childKeys) {
				FormDataGroupDto childSectionGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_CHILD_SCTN,
						FormConstants.EMPTY_STRING);
				List<FormDataGroupDto> childSectionGroupList = new ArrayList<FormDataGroupDto>();
				List<BookmarkDto> bookmarkChildSectionList = new ArrayList<BookmarkDto>();
				List<CpsFsnaDomainLookupDto> domainList = prefillDto.getChildSectionsMap().get(key);

				// Section name
				BookmarkDto bookmarkSectionName = createBookmarkWithCodesTable(BookmarkConstants.SCTN_NAME, key,
						"FSNASCTN");
				if (ServiceConstants.CCOR_080.equals(key)) {
					bookmarkSectionName.setBookmarkData(ServiceConstants.OTHER);
				}
				bookmarkChildSectionList.add(bookmarkSectionName);

				for (CpsFsnaDomainLookupDto domainDto : domainList) {
					FormDataGroupDto domainGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_DOMAIN,
							FormGroupsConstants.TMPLAT_CHILD_SCTN);
					List<FormDataGroupDto> domainGroupList = new ArrayList<FormDataGroupDto>();
					List<BookmarkDto> bookmarkDomainList = new ArrayList<BookmarkDto>();

					BookmarkDto bookmarkDomainName = createBookmarkWithCodesTable(BookmarkConstants.DOMN_NAME,
							domainDto.getCdFsnaDmn(), "FSNADOMN");
					String bookmarkData = bookmarkDomainName.getBookmarkData();
					bookmarkDomainName.setBookmarkData(domainDto.getTxtDmnDisplayOrder() + ". " + bookmarkData);
					bookmarkDomainList.add(bookmarkDomainName);

					// N/A column for domain 10
					// Defect# 12902 - Added a FBSSAssessment indicator to
					// include the NA check box in the Caregiver and Child/Youth
					// section in FSNA FBSS form
					if ((isFBSSAssessment || isCVSAssessment)
							&& ServiceConstants.STRING_TEN.equals(domainDto.getTxtDmnDisplayOrder())) {
						FormDataGroupDto naGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_NA,
								FormGroupsConstants.TMPLAT_DOMAIN);
						domainGroupList.add(naGroupDto);
					}

					for (Long idPersonChild : prefillDto.getSavedChildAssessed()) {
						PersonListDto personDto = personsMap.get(idPersonChild);
						// Defect 13754, To fix null pointer exception issue if
						// the person is not a PRN anymore
						if (!ObjectUtils.isEmpty(personDto)) {
							FormDataGroupDto childAnswersGroupDto = createFormDataGroup(
									FormGroupsConstants.TMPLAT_CHILD_ANSWERS, FormGroupsConstants.TMPLAT_DOMAIN);
							List<FormDataGroupDto> childAnswersGroupList = new ArrayList<FormDataGroupDto>();
							List<BookmarkDto> bookmarkChildAnswersList = new ArrayList<BookmarkDto>();

							BookmarkDto bookmarkChildNameFirst = createBookmark(BookmarkConstants.CHILD_NAME_FIRST,
									personDto.getNmPersonFirst());
							bookmarkChildAnswersList.add(bookmarkChildNameFirst);
							BookmarkDto bookmarkChildNameLast = createBookmark(BookmarkConstants.CHILD_NAME_LAST,
									personDto.getNmpersonLast());
							bookmarkChildAnswersList.add(bookmarkChildNameLast);

							if (ServiceConstants.STRING_TEN.equals(domainDto.getTxtDmnDisplayOrder())) {
								FormDataGroupDto naCheckBoxGroupDto = createFormDataGroup(
										FormGroupsConstants.TMPLAT_NA_CHECKBOX,
										FormGroupsConstants.TMPLAT_CHILD_ANSWERS);
								List<FormDataGroupDto> naCheckBoxGroupList = new ArrayList<FormDataGroupDto>();

								for (CpsFsnaRspnDto responseDto : domainDto.getFsnaRspns()) {
									if (responseDto.getIdRelPrsn().equals(personDto.getIdPerson())
											&& ServiceConstants.NAAP.equals(responseDto.getCdAnswr())) {
										FormDataGroupDto childNaGroupDto = createFormDataGroup(
												FormGroupsConstants.TMPLAT_CHILD_NA,
												FormGroupsConstants.TMPLAT_NA_CHECKBOX);
										naCheckBoxGroupList.add(childNaGroupDto);
									}
								}

								naCheckBoxGroupDto.setFormDataGroupList(naCheckBoxGroupList);
								childAnswersGroupList.add(naCheckBoxGroupDto);
							} else if (ServiceConstants.THREE.equals(domainDto.getTxtDmnDisplayOrder())) {
								FormDataGroupDto domain3GroupDto = createFormDataGroup(
										FormGroupsConstants.TMPLAT_DOMN3_EDUC,
										FormGroupsConstants.TMPLAT_CHILD_ANSWERS);
								// Check if at least one response has child edu
								// plan as
								// Y
								CpsFsnaRspnDto eduPlanDto = domainDto.getFsnaRspns().stream()
										.filter(p -> FormConstants.Y.equalsIgnoreCase(p.getIndChildEduPlanExists())
												&& p.getIdRelPrsn().equals(idPersonChild))
										.findFirst().orElse(null);
								String hasEduPlan = FormConstants.NO;
								if (!ObjectUtils.isEmpty(eduPlanDto)) {
									hasEduPlan = FormConstants.YES;
								}
								BookmarkDto educationPlanBookmark = createBookmark(BookmarkConstants.CHILD_EDUC_IND,
										hasEduPlan);
								List<BookmarkDto> educationBookmarkList = new ArrayList<BookmarkDto>();
								educationBookmarkList.add(educationPlanBookmark);
								domain3GroupDto.setBookmarkDtoList(educationBookmarkList);
								childAnswersGroupList.add(domain3GroupDto);
							}

							// Strength/need checkboxes
							List<CpsFsnaRspnDto> currentChildResponse = domainDto.getFsnaRspns().stream()
									.filter(p -> p.getIdRelPrsn().equals(idPersonChild)).collect(Collectors.toList());
							if (!ObjectUtils.isEmpty(currentChildResponse) && currentChildResponse.size() > 0) {
								for (CpsFsnaRspnDto responseDto : currentChildResponse) {
									if (isCVSAssessment) {
										if (ServiceConstants.SNND.equals(responseDto.getCdAnswr())) {
											FormDataGroupDto childStrengthGroupDto = createFormDataGroup(
													FormGroupsConstants.TMPLAT_CHILD_STRENGTH,
													FormGroupsConstants.TMPLAT_DOMAIN);
											childAnswersGroupList.add(childStrengthGroupDto);
										} else if (ServiceConstants.NEED.equals(responseDto.getCdAnswr())) {
											FormDataGroupDto childNeedGroupDto = createFormDataGroup(
													FormGroupsConstants.TMPLAT_CHILD_NEED,
													FormGroupsConstants.TMPLAT_DOMAIN);
											childAnswersGroupList.add(childNeedGroupDto);
										}

										if (ServiceConstants.STRING_TEN.equals(domainDto.getTxtDmnDisplayOrder())) {
											BookmarkDto bookmarkColSpan = createBookmark(
													BookmarkConstants.CHLD_DMN_TXT_COL_SPAN, 4);
											bookmarkChildAnswersList.add(bookmarkColSpan);
										} else {
											BookmarkDto bookmarkColSpan = createBookmark(
													BookmarkConstants.CHLD_DMN_TXT_COL_SPAN, 3);
											bookmarkChildAnswersList.add(bookmarkColSpan);
										}

									} else if (isFBSSAssessment) {
										if (ServiceConstants.STRENGTH.equals(responseDto.getCdAnswr())) {
											FormDataGroupDto childStrengthGroupDto = createFormDataGroup(
													FormGroupsConstants.TMPLAT_CHILD_STRENGTH,
													FormGroupsConstants.TMPLAT_DOMAIN);
											childAnswersGroupList.add(childStrengthGroupDto);
										} else if (ServiceConstants.NEED.equals(responseDto.getCdAnswr())) {
											FormDataGroupDto childNeedGroupDto = createFormDataGroup(
													FormGroupsConstants.TMPLAT_CHILD_NEED,
													FormGroupsConstants.TMPLAT_DOMAIN);
											childAnswersGroupList.add(childNeedGroupDto);

										} else if (ServiceConstants.NONEED.equals(responseDto.getCdAnswr())) {
											FormDataGroupDto childNoNeedGroupDto = createFormDataGroup(
													FormGroupsConstants.TMPLAT_CHILD_NONEED,
													FormGroupsConstants.TMPLAT_DOMAIN);
											childAnswersGroupList.add(childNoNeedGroupDto);
										}
									}
									BookmarkDto bookmarkDomainDesc = createBookmark(BookmarkConstants.DOMN_DESC,
											responseDto.getTxtDesc());
									bookmarkChildAnswersList.add(bookmarkDomainDesc);
								}
							}

							childAnswersGroupDto.setBookmarkDtoList(bookmarkChildAnswersList);
							childAnswersGroupDto.setFormDataGroupList(childAnswersGroupList);
							domainGroupList.add(childAnswersGroupDto);
						}

					}

					domainGroupDto.setBookmarkDtoList(bookmarkDomainList);
					domainGroupDto.setFormDataGroupList(domainGroupList);
					childSectionGroupList.add(domainGroupDto);
				}

				childSectionGroupDto.setBookmarkDtoList(bookmarkChildSectionList);
				childSectionGroupDto.setFormDataGroupList(childSectionGroupList);
				formDataGroupList.add(childSectionGroupDto);
			}
		}

		// only for fbss
		if (isFBSSAssessment) {
			// Primary caregiver name
			BookmarkDto bookmarkPrimaryNmFirst = createBookmark(BookmarkConstants.PRIMARY_NAME_FIRST,
					primaryCaregiver.getNmPersonFirst());
			bookmarkNonFrmGrpList.add(bookmarkPrimaryNmFirst);
			BookmarkDto bookmarkPrimaryNmMiddle = createBookmark(BookmarkConstants.PRIMARY_NAME_MIDDLE,
					primaryCaregiver.getNmPersonMiddle());
			bookmarkNonFrmGrpList.add(bookmarkPrimaryNmMiddle);
			BookmarkDto bookmarkPrimaryNmLast = createBookmark(BookmarkConstants.PRIMARY_NAME_LAST,
					primaryCaregiver.getNmPersonLast());
			bookmarkNonFrmGrpList.add(bookmarkPrimaryNmLast);
			BookmarkDto bookmarkPrimaryNmSuffix = createBookmark(BookmarkConstants.PRIMARY_NAME_SUFFIX,
					primaryCaregiver.getCdPersonSuffix());
			bookmarkNonFrmGrpList.add(bookmarkPrimaryNmSuffix);
			if (StringUtils.isNotBlank(primaryCaregiver.getCdPersonSuffix())) {
				FormDataGroupDto commaGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_COMMA_2,
						FormGroupsConstants.TMPLAT_DOMAIN);
				formDataGroupList.add(commaGroupDto);
			}

			if (!ObjectUtils.isEmpty(prefillDto.getStregthsNeedsMap()) && 0 < prefillDto.getStregthsNeedsMap().size()) {
				// Primary strengths/needs
				List<CpsFsnaPrtyStrngthNeedDto> primaryStrNeeds = prefillDto.getStregthsNeedsMap()
						.get(prefillDto.getCpsFsnaDto().getIdPrmryCrgvrPrnt());
				List<FormDataGroupDto> primStrengthsCollection = new ArrayList<FormDataGroupDto>();
				List<FormDataGroupDto> primNeedsCollection = new ArrayList<FormDataGroupDto>();
				if (!ObjectUtils.isEmpty(primaryStrNeeds) && 0 < primaryStrNeeds.size()) {
					for (CpsFsnaPrtyStrngthNeedDto strNeedDto : primaryStrNeeds) {
						if (ServiceConstants.STRENGTH.equals(strNeedDto.getCdStrengthOrNeed())) {
							FormDataGroupDto primaryCaregiverStrGroup = createFormDataGroup(
									FormGroupsConstants.TMPLAT_CAREGIVER_STRENGTHS, FormConstants.EMPTY_STRING);
							List<FormDataGroupDto> primaryCaregiverStrGroupList = new ArrayList<FormDataGroupDto>();
							List<BookmarkDto> bookmarkPrimaryCaregiverStrList = new ArrayList<BookmarkDto>();

							BookmarkDto bookmarkPriCaregiverStrNm = createBookmarkWithCodesTable(
									BookmarkConstants.PRI_CAREGIVER_STRENGTH_NM, strNeedDto.getNmDomain(), "FSNADOMN");
							bookmarkPrimaryCaregiverStrList.add(bookmarkPriCaregiverStrNm);

							if (ServiceConstants.Y.equals(strNeedDto.getIndInCurrPlan())) {
								FormDataGroupDto addrCurrGroupDto = createFormDataGroup(
										FormGroupsConstants.TMPLAT_ADDR_CURR,
										FormGroupsConstants.TMPLAT_CAREGIVER_STRENGTHS);
								primaryCaregiverStrGroupList.add(addrCurrGroupDto);
							}
							if (ServiceConstants.Y.equals(strNeedDto.getIndInSbsqntPlan())) {
								FormDataGroupDto addrSubGroupDto = createFormDataGroup(
										FormGroupsConstants.TMPLAT_ADDR_SUB,
										FormGroupsConstants.TMPLAT_CAREGIVER_STRENGTHS);
								primaryCaregiverStrGroupList.add(addrSubGroupDto);
							}
							if (ServiceConstants.Y.equals(strNeedDto.getIndCmntyRsrc())) {
								FormDataGroupDto commRsrcGroupDto = createFormDataGroup(
										FormGroupsConstants.TMPLAT_COMM_RSRC,
										FormGroupsConstants.TMPLAT_CAREGIVER_STRENGTHS);
								primaryCaregiverStrGroupList.add(commRsrcGroupDto);
							}

							primaryCaregiverStrGroup.setBookmarkDtoList(bookmarkPrimaryCaregiverStrList);
							primaryCaregiverStrGroup.setFormDataGroupList(primaryCaregiverStrGroupList);
							primStrengthsCollection.add(primaryCaregiverStrGroup);
						} else if (ServiceConstants.NEED.equals(strNeedDto.getCdStrengthOrNeed())) {
							FormDataGroupDto primaryCaregiverNeedGroup = createFormDataGroup(
									FormGroupsConstants.TMPLAT_CAREGIVER_NEEDS, FormConstants.EMPTY_STRING);
							List<FormDataGroupDto> primaryCaregiverNeedGroupList = new ArrayList<FormDataGroupDto>();
							List<BookmarkDto> bookmarkPrimaryCaregiverNeedList = new ArrayList<BookmarkDto>();

							BookmarkDto bookmarkPriCaregiverNeedNm = createBookmarkWithCodesTable(
									BookmarkConstants.PRI_CAREGIVER_NEEDS_NM, strNeedDto.getNmDomain(), "FSNADOMN");
							bookmarkPrimaryCaregiverNeedList.add(bookmarkPriCaregiverNeedNm);

							if (ServiceConstants.Y.equals(strNeedDto.getIndInCurrPlan())) {
								FormDataGroupDto addrCurrGroupDto = createFormDataGroup(
										FormGroupsConstants.TMPLAT_ADDR_CURR,
										FormGroupsConstants.TMPLAT_CAREGIVER_NEEDS);
								primaryCaregiverNeedGroupList.add(addrCurrGroupDto);
							}
							if (ServiceConstants.Y.equals(strNeedDto.getIndInSbsqntPlan())) {
								FormDataGroupDto addrSubGroupDto = createFormDataGroup(
										FormGroupsConstants.TMPLAT_ADDR_SUB,
										FormGroupsConstants.TMPLAT_CAREGIVER_NEEDS);
								primaryCaregiverNeedGroupList.add(addrSubGroupDto);
							}
							if (ServiceConstants.Y.equals(strNeedDto.getIndCmntyRsrc())) {
								FormDataGroupDto commRsrcGroupDto = createFormDataGroup(
										FormGroupsConstants.TMPLAT_COMM_RSRC,
										FormGroupsConstants.TMPLAT_CAREGIVER_NEEDS);
								primaryCaregiverNeedGroupList.add(commRsrcGroupDto);
							}

							primaryCaregiverNeedGroup.setBookmarkDtoList(bookmarkPrimaryCaregiverNeedList);
							primaryCaregiverNeedGroup.setFormDataGroupList(primaryCaregiverNeedGroupList);
							primNeedsCollection.add(primaryCaregiverNeedGroup);
						}
					}
				}

				formDataGroupList.addAll(primStrengthsCollection);
				formDataGroupList.addAll(primNeedsCollection);

				// Secondary Strengths/Needs
				boolean hasSecondaryParent = false;
				if (!ObjectUtils.isEmpty(secondaryCaregiver)) {
					hasSecondaryParent = true;
					List<CpsFsnaPrtyStrngthNeedDto> secStrNeeds = prefillDto.getStregthsNeedsMap()
							.get(prefillDto.getCpsFsnaDto().getIdSecndryCrgvrPrnt());

					FormDataGroupDto secondaryStrNeedsGroupDto = createFormDataGroup(
							FormGroupsConstants.TMPLAT_SECN_STR_NEEDS, FormConstants.EMPTY_STRING);
					List<FormDataGroupDto> secondaryStrNeedsGroupList = new ArrayList<FormDataGroupDto>();
					List<BookmarkDto> bookmarkSecondaryStrNeedsList = new ArrayList<BookmarkDto>();

					BookmarkDto bookmarkCareGiverSecFirstNm = createBookmark(BookmarkConstants.SECONDARY_NAME_FIRST,
							secondaryCaregiver.getNmPersonFirst());
					bookmarkSecondaryStrNeedsList.add(bookmarkCareGiverSecFirstNm);

					BookmarkDto bookmarkCareGiverSecLastNm = createBookmark(BookmarkConstants.SECONDARY_NAME_LAST,
							secondaryCaregiver.getNmPersonLast());
					bookmarkSecondaryStrNeedsList.add(bookmarkCareGiverSecLastNm);

					BookmarkDto bookmarkCareGiverSecMiddleNm = createBookmark(BookmarkConstants.SECONDARY_NAME_MIDDLE,
							secondaryCaregiver.getNmPersonMiddle());
					bookmarkSecondaryStrNeedsList.add(bookmarkCareGiverSecMiddleNm);

					if (!ObjectUtils.isEmpty(secStrNeeds) && 0 < secStrNeeds.size()) {
						for (CpsFsnaPrtyStrngthNeedDto strNeedDto : secStrNeeds) {
							if (ServiceConstants.STRENGTH.equals(strNeedDto.getCdStrengthOrNeed())) {
								FormDataGroupDto secondaryCaregiverStrGroup = createFormDataGroup(
										FormGroupsConstants.TMPLAT_SEC_CAREGIVER_STRENGTHS,
										FormGroupsConstants.TMPLAT_SECN_STR_NEEDS);
								List<FormDataGroupDto> secondaryCaregiverStrGroupList = new ArrayList<FormDataGroupDto>();
								List<BookmarkDto> bookmarkSecondaryCaregiverStrList = new ArrayList<BookmarkDto>();

								BookmarkDto bookmarkSecCaregiverStrNm = createBookmarkWithCodesTable(
										BookmarkConstants.SEC_CAREGIVER_STRENGTHS_NM, strNeedDto.getNmDomain(),
										"FSNADOMN");
								bookmarkSecondaryCaregiverStrList.add(bookmarkSecCaregiverStrNm);

								if (ServiceConstants.Y.equals(strNeedDto.getIndInCurrPlan())) {
									FormDataGroupDto addrCurrGroupDto = createFormDataGroup(
											FormGroupsConstants.TMPLAT_ADDR_CURR,
											FormGroupsConstants.TMPLAT_SEC_CAREGIVER_STRENGTHS);
									secondaryCaregiverStrGroupList.add(addrCurrGroupDto);
								}
								if (ServiceConstants.Y.equals(strNeedDto.getIndInSbsqntPlan())) {
									FormDataGroupDto addrSubGroupDto = createFormDataGroup(
											FormGroupsConstants.TMPLAT_ADDR_SUB,
											FormGroupsConstants.TMPLAT_SEC_CAREGIVER_STRENGTHS);
									secondaryCaregiverStrGroupList.add(addrSubGroupDto);
								}
								if (ServiceConstants.Y.equals(strNeedDto.getIndCmntyRsrc())) {
									FormDataGroupDto commRsrcGroupDto = createFormDataGroup(
											FormGroupsConstants.TMPLAT_COMM_RSRC,
											FormGroupsConstants.TMPLAT_SEC_CAREGIVER_STRENGTHS);
									secondaryCaregiverStrGroupList.add(commRsrcGroupDto);
								}

								secondaryCaregiverStrGroup.setBookmarkDtoList(bookmarkSecondaryCaregiverStrList);
								secondaryCaregiverStrGroup.setFormDataGroupList(secondaryCaregiverStrGroupList);
								secondaryStrNeedsGroupList.add(secondaryCaregiverStrGroup);
							} else if (ServiceConstants.NEED.equals(strNeedDto.getCdStrengthOrNeed())) {
								FormDataGroupDto secondaryCaregiverNeedGroup = createFormDataGroup(
										FormGroupsConstants.TMPLAT_SEC_CAREGIVER_NEEDS,
										FormGroupsConstants.TMPLAT_SECN_STR_NEEDS);
								List<FormDataGroupDto> secondaryCaregiverNeedGroupList = new ArrayList<FormDataGroupDto>();
								List<BookmarkDto> bookmarksecondaryCaregiverNeedList = new ArrayList<BookmarkDto>();

								BookmarkDto bookmarkSecCaregiverNeedNm = createBookmarkWithCodesTable(
										BookmarkConstants.SEC_CAREGIVER_NEEDS_NM, strNeedDto.getNmDomain(), "FSNADOMN");
								bookmarksecondaryCaregiverNeedList.add(bookmarkSecCaregiverNeedNm);

								if (ServiceConstants.Y.equals(strNeedDto.getIndInCurrPlan())) {
									FormDataGroupDto addrCurrGroupDto = createFormDataGroup(
											FormGroupsConstants.TMPLAT_ADDR_CURR,
											FormGroupsConstants.TMPLAT_SEC_CAREGIVER_NEEDS);
									secondaryCaregiverNeedGroupList.add(addrCurrGroupDto);
								}
								if (ServiceConstants.Y.equals(strNeedDto.getIndInSbsqntPlan())) {
									FormDataGroupDto addrSubGroupDto = createFormDataGroup(
											FormGroupsConstants.TMPLAT_ADDR_SUB,
											FormGroupsConstants.TMPLAT_SEC_CAREGIVER_NEEDS);
									secondaryCaregiverNeedGroupList.add(addrSubGroupDto);
								}
								if (ServiceConstants.Y.equals(strNeedDto.getIndCmntyRsrc())) {
									FormDataGroupDto commRsrcGroupDto = createFormDataGroup(
											FormGroupsConstants.TMPLAT_COMM_RSRC,
											FormGroupsConstants.TMPLAT_SEC_CAREGIVER_NEEDS);
									secondaryCaregiverNeedGroupList.add(commRsrcGroupDto);
								}
								secondaryCaregiverNeedGroup.setBookmarkDtoList(bookmarksecondaryCaregiverNeedList);
								secondaryCaregiverNeedGroup.setFormDataGroupList(secondaryCaregiverNeedGroupList);
								secondaryStrNeedsGroupList.add(secondaryCaregiverNeedGroup);
							}
						}
					}
					secondaryStrNeedsGroupDto.setBookmarkDtoList(bookmarkSecondaryStrNeedsList);
					secondaryStrNeedsGroupDto.setFormDataGroupList(secondaryStrNeedsGroupList);
					formDataGroupList.add(secondaryStrNeedsGroupDto);
				}

				// All children strength and needs
				for (Long key : prefillDto.getStregthsNeedsMap().keySet()) {
					// if not primary or secondary parent/caregiver
					// Defect 13754, To fix null pointer exception issue if the
					// person is not a PRN anymore
					if (!key.equals(prefillDto.getCpsFsnaDto().getIdPrmryCrgvrPrnt())
							&& (!hasSecondaryParent || !key.equals(prefillDto.getCpsFsnaDto().getIdSecndryCrgvrPrnt()))
							&& !ObjectUtils.isEmpty(personsMap.get(key))) {

						PersonListDto personDto = personsMap.get(key);
						FormDataGroupDto childStrNeedsGroupDto = createFormDataGroup(
								FormGroupsConstants.TMPLAT_CHILD_STRE_NEED, FormConstants.EMPTY_STRING);
						List<FormDataGroupDto> childStrNeedsGroupList = new ArrayList<FormDataGroupDto>();
						List<BookmarkDto> bookmarkChildStrNeedsList = new ArrayList<BookmarkDto>();

						BookmarkDto bookmarkChAssessedFirstNm = createBookmark(BookmarkConstants.CHILD_NM_FULL,
								personDto.getPersonFull());
						bookmarkChildStrNeedsList.add(bookmarkChAssessedFirstNm);

						List<CpsFsnaPrtyStrngthNeedDto> childStrNeeds = prefillDto.getStregthsNeedsMap().get(key);
						for (CpsFsnaPrtyStrngthNeedDto strNeedDto : childStrNeeds) {
							if (ServiceConstants.STRENGTH.equals(strNeedDto.getCdStrengthOrNeed())) {
								FormDataGroupDto childStrGroup = createFormDataGroup(
										FormGroupsConstants.TMPLAT_CHILD_DOMAIN_NM,
										FormGroupsConstants.TMPLAT_CHILD_STRE_NEED);
								List<FormDataGroupDto> childStrGroupList = new ArrayList<FormDataGroupDto>();
								List<BookmarkDto> bookmarkChildStrList = new ArrayList<BookmarkDto>();

								BookmarkDto bookmarkChildStrNm = createBookmarkWithCodesTable(
										BookmarkConstants.CHILD_STRENGTHS, strNeedDto.getNmDomain(), "FSNADOMN");
								bookmarkChildStrList.add(bookmarkChildStrNm);

								if (ServiceConstants.Y.equals(strNeedDto.getIndCmntyRsrc())) {
									FormDataGroupDto commRsrcGroupDto = createFormDataGroup(
											FormGroupsConstants.TMPLAT_COMM_RSRC,
											FormGroupsConstants.TMPLAT_CHILD_DOMAIN_NM);
									childStrGroupList.add(commRsrcGroupDto);
								}

								childStrGroup.setBookmarkDtoList(bookmarkChildStrList);
								childStrGroup.setFormDataGroupList(childStrGroupList);
								childStrNeedsGroupList.add(childStrGroup);
							} else if (ServiceConstants.NEED.equals(strNeedDto.getCdStrengthOrNeed())) {
								FormDataGroupDto childNeedGroup = createFormDataGroup(
										FormGroupsConstants.TMPLAT_CHILD_NEED_DOMAIN_NM,
										FormGroupsConstants.TMPLAT_CHILD_STRE_NEED);
								List<FormDataGroupDto> childNeedGroupList = new ArrayList<FormDataGroupDto>();
								List<BookmarkDto> bookmarkchildNeedList = new ArrayList<BookmarkDto>();

								BookmarkDto bookmarkChildNeedNm = createBookmarkWithCodesTable(
										BookmarkConstants.CHILD_NEEDS, strNeedDto.getNmDomain(), "FSNADOMN");
								bookmarkchildNeedList.add(bookmarkChildNeedNm);

								if (ServiceConstants.Y.equals(strNeedDto.getIndCmntyRsrc())) {
									FormDataGroupDto commRsrcGroupDto = createFormDataGroup(
											FormGroupsConstants.TMPLAT_COMM_RSRC,
											FormGroupsConstants.TMPLAT_CHILD_NEED_DOMAIN_NM);
									childNeedGroupList.add(commRsrcGroupDto);
								}

								childNeedGroup.setBookmarkDtoList(bookmarkchildNeedList);
								childNeedGroup.setFormDataGroupList(childNeedGroupList);
								childStrNeedsGroupList.add(childNeedGroup);
							}
						}
						childStrNeedsGroupDto.setBookmarkDtoList(bookmarkChildStrNeedsList);
						childStrNeedsGroupDto.setFormDataGroupList(childStrNeedsGroupList);
						formDataGroupList.add(childStrNeedsGroupDto);
					}
				}
			}
		}

		PreFillDataServiceDto preFillData = new PreFillDataServiceDto();
		preFillData.setFormDataGroupList(formDataGroupList);
		preFillData.setBookmarkDtoList(bookmarkNonFrmGrpList);
		return preFillData;

	}

	// Warranty Defect Fix - 11319 - To Replace the Carriage Return /Line Feed
	// with Break Tag
	public String formatTextValue(String txtToFormat) {
		String[] txtConcurrently = null;
		if (!ObjectUtils.isEmpty(txtToFormat)) {
			txtConcurrently = txtToFormat.split("\r\n");
		}
		StringBuffer txtConcrBuf = new StringBuffer();
		if (!ObjectUtils.isEmpty(txtConcurrently)) {
			for (String txtConcr : txtConcurrently) {
				txtConcrBuf.append(txtConcr);
				txtConcrBuf.append("</br>");
			}
		}
		return txtConcrBuf.toString();
	}
    
	//QCR 62702 - SDM Removal
	private boolean showSDM(CpsFsnaDto cpsFsnaDto){
	boolean showSDMInTitle=false;
	Date dtSDMRemoval=codesDao.getAppRelDate(ServiceConstants.CRELDATE_SDM_REMOVAL_2020);
		
		if (!ObjectUtils.isEmpty(cpsFsnaDto)) {
			if (!ObjectUtils.isEmpty(cpsFsnaDto.getDtAsgnmntCmpltd())) {
				if (cpsFsnaDto.getDtAsgnmntCmpltd().compareTo(dtSDMRemoval) < 0) {
					showSDMInTitle = true;
				}
			} else {
				if (!ObjectUtils.isEmpty(cpsFsnaDto.getDtOfAsgnmnt())
						&& !ObjectUtils.isEmpty(cpsFsnaDto.getdtStageClosure())
						&& cpsFsnaDto.getDtOfAsgnmnt().compareTo(dtSDMRemoval) < 0
						&& cpsFsnaDto.getdtStageClosure().compareTo(dtSDMRemoval) < 0) {
					showSDMInTitle = true;
				}
			}
		}
		
		return showSDMInTitle;
	}


}
