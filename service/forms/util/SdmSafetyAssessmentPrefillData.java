/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:
 *May 4, 2018- 4:06:10 PM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.forms.util;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.service.casepackage.dto.PCSPPersonDto;
import us.tx.state.dfps.service.common.BookmarkConstants;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.FormConstants;
import us.tx.state.dfps.service.common.FormGroupsConstants;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.CodesDao;
import us.tx.state.dfps.service.common.daoimpl.CodesDaoImpl;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.util.FormattingUtils;
import us.tx.state.dfps.service.forms.dto.BookmarkDto;
import us.tx.state.dfps.service.forms.dto.FormDataGroupDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.investigation.dto.SDMSafetyAssessmentDto;
import us.tx.state.dfps.service.investigation.dto.SDMSafetyAssessmentFollowupDto;
import us.tx.state.dfps.service.investigation.dto.SDMSafetyAssessmentResponseDto;
import us.tx.state.dfps.service.investigation.dto.SDMSafetyAssmentDto;
import us.tx.state.dfps.service.lookup.dao.LookupDao;
import us.tx.state.dfps.service.person.dao.PersonDao;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> May 4, 2018- 4:06:10 PM © 2017 Texas Department of
 * Family and Protective Services
 */
@Component
public class SdmSafetyAssessmentPrefillData extends DocumentServiceUtil {

	@Autowired
	private LookupDao lookupDao;
	
	@Autowired
	private PersonDao personDao;
	
	@Autowired
	private CodesDao codesDao;

	public static final Set<String> ITEMS_WITH_DOUBLE_ROWS = new HashSet<String>(
			Arrays.asList(new String[] { "SA4_FSI_Q1", "SA4_FSI_Q4", "SA4_FSI_Q9" }));
	public static final int SECTION_3_ROW_COUNT = 5;

	/**
	 * Method Description: This method is used to prefill the data from the
	 * different Dao by passing Dao output Dtos and bookmark and form group
	 * bookmark Dto as objects as input request
	 * 
	 * @param parentDtoobj
	 * @param bookmarkDtoObj
	 * @return PreFillData
	 */
	@Override
	public PreFillDataServiceDto returnPrefillData(Object parentDtoobj) {
		SDMSafetyAssmentDto safetyAssment = (SDMSafetyAssmentDto) parentDtoobj;

		if (ObjectUtils.isEmpty(safetyAssment.getSafetAssessmentList())) {
			safetyAssment.setSafetAssessmentList(new SDMSafetyAssessmentDto());
		}
		if (null == safetyAssment.getEventPersonLink()) {
			safetyAssment.setEventPersonLink(new ArrayList<Long>());
		}
		if (null == safetyAssment.getNameList()) {
			safetyAssment.setNameList(new ArrayList<PCSPPersonDto>());
		}
		if (null == safetyAssment.getSafetyResponses()) {
			safetyAssment.setSafetyResponses(new ArrayList<SDMSafetyAssessmentResponseDto>());
		}

		List<FormDataGroupDto> formDataGroupList = new ArrayList<FormDataGroupDto>();
		List<BookmarkDto> bookmarkNonFrmGrpList = new ArrayList<BookmarkDto>();

		boolean isDangerIndicatorYesAvailable = false;
		boolean isDangerIndicatorSectionIncomplete = false;
		safetyAssment.getSafetAssessmentList().setPersonAssessedList2(safetyAssment.getNameList());

		// Setup Lists that will be used throughout the form generation
		List<SDMSafetyAssessmentResponseDto> section1List = safetyAssment.getSafetAssessmentList()
				.getSafetyResponseBySectionMap().get(BookmarkConstants.SECTION_1);
		List<SDMSafetyAssessmentResponseDto> section2List = safetyAssment.getSafetAssessmentList()
				.getSafetyResponseBySectionMap().get(BookmarkConstants.SECTION_2);
		Map<String, List<SDMSafetyAssessmentResponseDto>> section3SubSectionMap = getSubSectionMap(safetyAssment
				.getSafetAssessmentList().getSafetyResponseBySectionMap().get(BookmarkConstants.SECTION_3));
		Map<String, List<SDMSafetyAssessmentResponseDto>> section4SubSectionMap = getSubSectionMap(safetyAssment
				.getSafetAssessmentList().getSafetyResponseBySectionMap().get(BookmarkConstants.SECTION_4));
		
		BookmarkDto printTitle=new BookmarkDto();
		BookmarkDto htmlTitle=new BookmarkDto();
		
		/***QCR 62702 SDM Removal-Change of title depending on Date of Assessment completed
		 * Before or After 9/1/2020
		 * artf159084, artf159085, artf159406, artf158968
		 * **/		
		String taskLong = safetyAssment.getSafetAssessmentList().getCdTask();
		boolean showSDMInTitle=false;
		String strPrintTitle=null;
		String strHTMLTitle=null;
		switch(taskLong){
			case BookmarkConstants.SDM_TASK_CODE_FSU:
					showSDMInTitle=showSDM(safetyAssment);
					strPrintTitle=showSDMInTitle? BookmarkConstants.SDM_PAGE_TITLE_FSU:BookmarkConstants.PAGE_TITLE_FSU_NOSDM;
					strHTMLTitle=showSDMInTitle?BookmarkConstants.SDM_PAGE_TITLE_FSU:BookmarkConstants.PAGE_TITLE_FSU_NOSDM;
					printTitle=createBookmark(BookmarkConstants.PRINT_TITLE, strPrintTitle);
					htmlTitle = createBookmark(BookmarkConstants.HTML_TITLE, strHTMLTitle);;
				break;
			case BookmarkConstants.SDM_TASK_CODE_FRE:
					showSDMInTitle=showSDM(safetyAssment);
					strPrintTitle=showSDMInTitle? BookmarkConstants.SDM_PAGE_TITLE_FRE:BookmarkConstants.PAGE_TITLE_FRE_NOSDM;
					strHTMLTitle=showSDMInTitle?BookmarkConstants.SDM_PAGE_TITLE_FRE:BookmarkConstants.PAGE_TITLE_FRE_NOSDM;
					printTitle = createBookmark(BookmarkConstants.PRINT_TITLE, strPrintTitle);
					htmlTitle = createBookmark(BookmarkConstants.HTML_TITLE, strHTMLTitle);
				break;
			case BookmarkConstants.SDM_TASK_CODE_FPR:
					showSDMInTitle=showSDM(safetyAssment);
					strPrintTitle=showSDMInTitle? BookmarkConstants.SDM_PAGE_TITLE_FPR:BookmarkConstants.PAGE_TITLE_FPR_NOSDM;
					strHTMLTitle=showSDMInTitle?BookmarkConstants.SDM_PAGE_TITLE_FPR:BookmarkConstants.PAGE_TITLE_FPR_NOSDM;
					printTitle = createBookmark(BookmarkConstants.PRINT_TITLE, strPrintTitle);
					htmlTitle = createBookmark(BookmarkConstants.HTML_TITLE, strHTMLTitle);
				break;
			case BookmarkConstants.SDM_TASK_CODE_INV:				
					showSDMInTitle=showSDM(safetyAssment);
					strPrintTitle=showSDMInTitle? BookmarkConstants.SDM_PAGE_TITLE_INV:BookmarkConstants.PAGE_TITLE_INV_NOSDM;
					strHTMLTitle=showSDMInTitle?BookmarkConstants.SDM_PAGE_TITLE_INV:BookmarkConstants.PAGE_TITLE_INV_NOSDM;
					printTitle=createBookmark(BookmarkConstants.PRINT_TITLE, strPrintTitle);
					htmlTitle = createBookmark(BookmarkConstants.HTML_TITLE, strHTMLTitle);
				break;
			default:
					//display title for A-R stage
					showSDMInTitle=showSDM(safetyAssment);
					strPrintTitle=showSDMInTitle? BookmarkConstants.SA_TITLE:BookmarkConstants.SA_TITLE_AR_NOSDM;
					strHTMLTitle=showSDMInTitle?BookmarkConstants.SA_TITLE:BookmarkConstants.SA_TITLE_AR_NOSDM;
					printTitle = createBookmark(BookmarkConstants.PRINT_TITLE, strPrintTitle);
					htmlTitle = createBookmark(BookmarkConstants.HTML_TITLE, strHTMLTitle);				
				break;
		}		
		//End of QCR 62702 SDM Removal change
		
		bookmarkNonFrmGrpList.add(htmlTitle);
		bookmarkNonFrmGrpList.add(printTitle);
		BookmarkDto idCaseBk = createBookmark(BookmarkConstants.ID_CASE,
				safetyAssment.getSafetAssessmentList().getIdCase());
		BookmarkDto nmCaseBk = createBookmark(BookmarkConstants.NM_CASE,
				safetyAssment.getSafetAssessmentList().getCaseName());
		// Warranty Defect - 12431 - To set the HouseHold Person Id
		if(!ObjectUtils.isEmpty(safetyAssment.getSafetAssessmentList().getIdHouseHoldPerson()))
		{
		BookmarkDto hhCaseBk = createBookmark(BookmarkConstants.CASE_HOUSEHOLD,
				personDao.getPerson(safetyAssment.getSafetAssessmentList().getIdHouseHoldPerson()).getNmPersonFull());
		bookmarkNonFrmGrpList.add(hhCaseBk);
		}
		bookmarkNonFrmGrpList.add(idCaseBk);
		bookmarkNonFrmGrpList.add(nmCaseBk);
		

		BookmarkDto assmentDate = createBookmark(BookmarkConstants.ASSMT_DATE,
				FormattingUtils.formatDate(safetyAssment.getSafetAssessmentList().getDtSafetyAssessed()));
		BookmarkDto assmentTime = createBookmark(BookmarkConstants.ASSMT_TIME,
				FormattingUtils.formatString(safetyAssment.getSafetAssessmentList().getTimeSafetyAssessed()));
		bookmarkNonFrmGrpList.add(assmentDate);
		bookmarkNonFrmGrpList.add(assmentTime);

		// Get Assmt Type
		if (CodesConstant.CSDMASMT_INIT.equalsIgnoreCase(safetyAssment.getSafetAssessmentList().getAssessmentType())) {
			BookmarkDto initBox = createBookmark(BookmarkConstants.INIT_BOX, BookmarkConstants.X);
			bookmarkNonFrmGrpList.add(initBox);
		} else {
			FormDataGroupDto initSpace = createFormDataGroup(FormGroupsConstants.TMPLAT_INIT_SPACE,
					FormConstants.EMPTY_STRING);
			formDataGroupList.add(initSpace);
		}

		if (CodesConstant.CSDMASMT_REAS.equalsIgnoreCase(safetyAssment.getSafetAssessmentList().getAssessmentType())) {
			BookmarkDto reasBox = createBookmark(BookmarkConstants.REAS_BOX, BookmarkConstants.X);
			bookmarkNonFrmGrpList.add(reasBox);
		} else {
			FormDataGroupDto reasSpace = createFormDataGroup(FormGroupsConstants.TMPLAT_REAS_SPACE,
					FormConstants.EMPTY_STRING);
			formDataGroupList.add(reasSpace);
		}

		if (CodesConstant.CSDMASMT_CLOS.equalsIgnoreCase(safetyAssment.getSafetAssessmentList().getAssessmentType())) {
			BookmarkDto invCloseBox = createBookmark(BookmarkConstants.INV_CLOSURE_BOX, BookmarkConstants.X);
			bookmarkNonFrmGrpList.add(invCloseBox);
		} else {
			FormDataGroupDto invClose = createFormDataGroup(FormGroupsConstants.TMPLAT_INV_CLOSURE_SPACE,
					FormConstants.EMPTY_STRING);
			formDataGroupList.add(invClose);
		}

		/**
		 * Populate the list of Children and caregiver(s) assessed.
		 * 
		 * @param preFillData
		 * @param sdmSafetyAssessmentDB
		 */
		List<PCSPPersonDto> childrenList = safetyAssment.getNameList();
		if (!ObjectUtils.isEmpty(childrenList)) {
			for (PCSPPersonDto childDB : childrenList) {
				List<FormDataGroupDto> formDataChildGroupList = new ArrayList<FormDataGroupDto>();
				FormDataGroupDto childGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_CHILD,
						FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkChildGroupList = new ArrayList<BookmarkDto>();
				boolean childSelected = Boolean.FALSE;
				if (!ObjectUtils.isEmpty(safetyAssment.getEventPersonLink())
						&& safetyAssment.getEventPersonLink().size() > 0) {
					childSelected = safetyAssment.getEventPersonLink().contains(childDB.getIdPerson());
				}
				if (childSelected) {
					BookmarkDto childBox = createBookmark(BookmarkConstants.CHILD_BOX, BookmarkConstants.X);
					bookmarkChildGroupList.add(childBox);
				} else {
					FormDataGroupDto childSpace = createFormDataGroup(FormGroupsConstants.TMPLAT_CHILD_SPACE,
							FormConstants.EMPTY_STRING);
					formDataChildGroupList.add(childSpace);
				}
				BookmarkDto childFullName = createBookmark(BookmarkConstants.NM_PERSON_FULL, childDB.getNmPersonFull());
				BookmarkDto childPersonType = createBookmark(BookmarkConstants.PERSON_TYPE,
						lookupDao.decode(CodesConstant.CPRSNTYP, childDB.getStgPrsnType()));
				BookmarkDto childPersonRelInt = createBookmark(BookmarkConstants.PERSON_REL_INT,
						lookupDao.decode(CodesConstant.CRPTRINT, childDB.getCdStagePersRelInt()));
				bookmarkChildGroupList.add(childFullName);
				bookmarkChildGroupList.add(childPersonType);
				bookmarkChildGroupList.add(childPersonRelInt);
				if (!taskLong.equalsIgnoreCase(BookmarkConstants.SDM_TASK_CODE_FSU)
						&& !taskLong.equalsIgnoreCase(BookmarkConstants.SDM_TASK_CODE_FRE)) {
					BookmarkDto childPersonRole = createBookmark(BookmarkConstants.PERSON_ROLE,
							lookupDao.decode(CodesConstant.CINVROLE, childDB.getCdStagePersRole()));
					bookmarkChildGroupList.add(childPersonRole);
				}
				childGroup.setBookmarkDtoList(bookmarkChildGroupList);
				childGroup.setFormDataGroupList(formDataChildGroupList);
				formDataGroupList.add(childGroup);
			}
			// formDataGroupList.addAll(formDataChildGroupList);
		}

		/**
		 * Populate Section 1 : Factors Influencing Child Vulnerability
		 * 
		 * @param preFillData
		 * @param section1List
		 */
		if (!ObjectUtils.isEmpty(section1List) && section1List.size() > 0) {
			for (SDMSafetyAssessmentResponseDto responseDB : section1List) {
				List<FormDataGroupDto> formDataSection1FactorList = new ArrayList<FormDataGroupDto>();
				FormDataGroupDto section1Factor = createFormDataGroup(FormGroupsConstants.TMPLAT_SECTION_1,
						FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkSection1FactorList = new ArrayList<BookmarkDto>();
				if (BookmarkConstants.Y.equalsIgnoreCase(responseDB.getCdQuestionResponse())) {
					BookmarkDto section1FactorBK = createBookmark(BookmarkConstants.CHECK_BOX, BookmarkConstants.X);
					bookmarkSection1FactorList.add(section1FactorBK);
				} else {
					FormDataGroupDto checkSpace = createFormDataGroup(FormGroupsConstants.TMPLAT_CHECK_SPACE,
							FormConstants.EMPTY_STRING);
					formDataSection1FactorList.add(checkSpace);
				}
				BookmarkDto section1FactorBK2 = createBookmark(BookmarkConstants.SECTION_1_FACTOR,
						responseDB.getQuestionText());
				bookmarkSection1FactorList.add(section1FactorBK2);
				section1Factor.setBookmarkDtoList(bookmarkSection1FactorList);
				section1Factor.setFormDataGroupList(formDataSection1FactorList);
				formDataGroupList.add(section1Factor);
			}
		}

		/**
		 * Populate Section 2: Current Danger Indicators
		 * 
		 * @param preFillData
		 * @param section2List
		 */
		if (!ObjectUtils.isEmpty(section2List) && section2List.size() > 0) {
			for (SDMSafetyAssessmentResponseDto responseDB : section2List) {
				List<FormDataGroupDto> formDataSection2FactorList = new ArrayList<FormDataGroupDto>();
				FormDataGroupDto section2Factor = createFormDataGroup(FormGroupsConstants.TMPLAT_SECTION_2,
						FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkSection2FactorList = new ArrayList<BookmarkDto>();
				if (BookmarkConstants.Y.equalsIgnoreCase(responseDB.getCdQuestionResponse())) {
					BookmarkDto section2FactorYes = createBookmark(BookmarkConstants.YES_NO, BookmarkConstants.YES);
					bookmarkSection2FactorList.add(section2FactorYes);
				} else if (BookmarkConstants.N.equalsIgnoreCase(responseDB.getCdQuestionResponse())) {
					BookmarkDto section2FactorNo = createBookmark(BookmarkConstants.YES_NO, BookmarkConstants.NO);
					bookmarkSection2FactorList.add(section2FactorNo);
				}
				if (BookmarkConstants.SECTION_2_QUESTION_14.equalsIgnoreCase(responseDB.getCdQuestion())) {
					BookmarkDto otherTexts = createBookmark(BookmarkConstants.OTHER_TEXT,
							responseDB.getOtherDescriptionText());
					bookmarkSection2FactorList.add(otherTexts);
				}
				BookmarkDto onOrder = createBookmark(BookmarkConstants.SECTION2_QNORDER, responseDB.getQuestionOrder());
				BookmarkDto factor = createBookmark(BookmarkConstants.SECTION_2_FACTOR, responseDB.getQuestionText());
				bookmarkSection2FactorList.add(onOrder);
				bookmarkSection2FactorList.add(factor);
				if (!ObjectUtils.isEmpty(responseDB.getFollowupResponseList())
						&& responseDB.getFollowupResponseList().size() > 0) {
					List<FormDataGroupDto> formDataSection2FollowUpList = new ArrayList<FormDataGroupDto>();
					FormDataGroupDto section2FollowUp = createFormDataGroup(
							FormGroupsConstants.TMPLAT_SECTION2_FOLLOWUP, FormConstants.EMPTY_STRING);
					for (SDMSafetyAssessmentFollowupDto followupDB : responseDB.getFollowupResponseList()) {
						List<FormDataGroupDto> formDataSection2FollowUpResList = new ArrayList<FormDataGroupDto>();
						FormDataGroupDto section2FollowUpRes = createFormDataGroup(
								FormGroupsConstants.TMPLAT_FOLLOWUP_RESPONSE, FormConstants.EMPTY_STRING);
						List<BookmarkDto> bookmarkSection2FollowUpResList = new ArrayList<BookmarkDto>();
						if (BookmarkConstants.Y.equalsIgnoreCase(followupDB.getCdFollowupResponse())) {
							BookmarkDto checkBox = createBookmark(BookmarkConstants.CHECK_BOX, BookmarkConstants.X);
							bookmarkSection2FollowUpResList.add(checkBox);
						} else {
							FormDataGroupDto dISpace = createFormDataGroup(FormGroupsConstants.TMPLAT_DI_SPACE,
									FormConstants.EMPTY_STRING);
							formDataSection2FollowUpResList.add(dISpace);
						}
						BookmarkDto section2FollowUpBK = createBookmark(BookmarkConstants.SECTION2_FOLLOWUP,
								followupDB.getFollowupQuestionText());
						bookmarkSection2FollowUpResList.add(section2FollowUpBK);
						section2FollowUpRes.setBookmarkDtoList(bookmarkSection2FollowUpResList);
						section2FollowUpRes.setFormDataGroupList(formDataSection2FollowUpResList);
						formDataSection2FollowUpList.add(section2FollowUpRes);
					}
					section2FollowUp.setFormDataGroupList(formDataSection2FollowUpList);
					formDataSection2FactorList.add(section2FollowUp);
				}
				section2Factor.setFormDataGroupList(formDataSection2FactorList);
				section2Factor.setBookmarkDtoList(bookmarkSection2FactorList);
				formDataGroupList.add(section2Factor);
			}
		}

		for (SDMSafetyAssessmentResponseDto responseDB : section2List) {
			if (BookmarkConstants.Y.equalsIgnoreCase(responseDB.getCdQuestionResponse())) {
				isDangerIndicatorYesAvailable = Boolean.TRUE;
				break;
			}
		}

		for (SDMSafetyAssessmentResponseDto responseDB : section2List) {
			if (!BookmarkConstants.Y.equalsIgnoreCase(responseDB.getCdQuestionResponse())
					&& !BookmarkConstants.N.equalsIgnoreCase(responseDB.getCdQuestionResponse())) {
				isDangerIndicatorSectionIncomplete = Boolean.TRUE;
				break;
			}
		}

		/**
		 * Populate Section 3: Household Strengths and Actions of Protection
		 * 
		 * @param preFillData
		 * @param section3SubSectionMap
		 * @param sdmSafetyAssessmentDB
		 */
		if (Boolean.FALSE.equals(isDangerIndicatorSectionIncomplete)
				&& Boolean.TRUE.equals(isDangerIndicatorYesAvailable)) {
			if (!ObjectUtils.isEmpty(section3SubSectionMap)) {
				List<FormDataGroupDto> formDataTableGroup3List = new ArrayList<FormDataGroupDto>();
				FormDataGroupDto tableGroup3 = createFormDataGroup(FormGroupsConstants.TMPLAT_SECTION3_TABLE,
						FormConstants.EMPTY_STRING);
				for (int i = 0; i < SECTION_3_ROW_COUNT; i++) {
					List<FormDataGroupDto> formDataSection3FactorList = new ArrayList<FormDataGroupDto>();
					FormDataGroupDto section3Factor = createFormDataGroup(FormGroupsConstants.TMPLAT_SECTION_3,
							FormConstants.EMPTY_STRING);
					List<BookmarkDto> bookmarkSection3FactorList = new ArrayList<BookmarkDto>();
					BookmarkDto subsecName = createBookmark(BookmarkConstants.SUBSECTION_NAME,
							getSection3SubSectionName(i));
					bookmarkSection3FactorList.add(subsecName);
					List<SDMSafetyAssessmentResponseDto> houseHoldStrengthsList = section3SubSectionMap
							.get(BookmarkConstants.HOUSEHOLD_STRENGTHS + (i + 1));
					if (!ObjectUtils.isEmpty(houseHoldStrengthsList)) {
						List<FormDataGroupDto> formDataRowHSList = new ArrayList<FormDataGroupDto>();
						FormDataGroupDto rowHS = createFormDataGroup(FormGroupsConstants.HS_ROW,
								FormConstants.EMPTY_STRING);
						for (SDMSafetyAssessmentResponseDto responseDB : houseHoldStrengthsList) {
							List<FormDataGroupDto> formDataHsGroupList = new ArrayList<FormDataGroupDto>();
							FormDataGroupDto hsGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_HS,
									FormConstants.EMPTY_STRING);
							List<BookmarkDto> bookmarkHsGroupList = new ArrayList<BookmarkDto>();
							if (BookmarkConstants.Y.equalsIgnoreCase(responseDB.getCdQuestionResponse())) {
								BookmarkDto checkBox = createBookmark(BookmarkConstants.CHECK_BOX, BookmarkConstants.X);
								bookmarkHsGroupList.add(checkBox);
							} else {
								FormDataGroupDto checkSpace = createFormDataGroup(
										FormGroupsConstants.TMPLAT_CHECK_SPACE, FormConstants.EMPTY_STRING);
								formDataHsGroupList.add(checkSpace);
							}
							BookmarkDto houseStrength = createBookmark(BookmarkConstants.HOUSEHOLD_STRENGTH,
									responseDB.getQuestionText());
							bookmarkHsGroupList.add(houseStrength);
							if (!ObjectUtils.isEmpty(responseDB.getOtherDescriptionText())) {
								//responseDB.setOtherDescriptionText(FormConstants.EMPTY_STRING);
								if (BookmarkConstants.SECTION_3_HS_OTHER.equalsIgnoreCase(responseDB.getCdQuestion())) {
									List<FormDataGroupDto> formDataHsFollowupGrpList = new ArrayList<FormDataGroupDto>();
									FormDataGroupDto hsFollowupGrp = createFormDataGroup(
											FormGroupsConstants.TMPLAT_FOLLOWUP_HS, FormConstants.EMPTY_STRING);
									List<BookmarkDto> bookmarkHsFollowupGrpList = new ArrayList<BookmarkDto>();
									BookmarkDto hsResponse = createBookmark(
											BookmarkConstants.FOLLOWUP_HS_RESPONSE,
											responseDB.getOtherDescriptionText());
									bookmarkHsFollowupGrpList.add(hsResponse);
									hsFollowupGrp.setBookmarkDtoList(bookmarkHsFollowupGrpList);
									hsFollowupGrp.setFormDataGroupList(formDataHsFollowupGrpList);
									formDataHsGroupList.add(hsFollowupGrp);
								}
							}
							hsGroup.setBookmarkDtoList(bookmarkHsGroupList);
							hsGroup.setFormDataGroupList(formDataHsGroupList);
							formDataRowHSList.add(hsGroup);
						}
						rowHS.setFormDataGroupList(formDataRowHSList);
						formDataSection3FactorList.add(rowHS);
					}
					List<SDMSafetyAssessmentResponseDto> actionsOfProtectionList = section3SubSectionMap
							.get(BookmarkConstants.ACTIONS_OF_PROTECTION + (i + 1));
					if (!ObjectUtils.isEmpty(actionsOfProtectionList)) {
						List<FormDataGroupDto> formDataRowAPList = new ArrayList<FormDataGroupDto>();
						FormDataGroupDto rowAP = createFormDataGroup(FormGroupsConstants.AP_ROW,
								FormConstants.EMPTY_STRING);
						List<BookmarkDto> bookmarkRowAPList = new ArrayList<BookmarkDto>();
						for (SDMSafetyAssessmentResponseDto responseDB : actionsOfProtectionList) {
							List<FormDataGroupDto> formDataApGroupList = new ArrayList<FormDataGroupDto>();
							FormDataGroupDto apGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_AP,
									FormConstants.EMPTY_STRING);
							List<BookmarkDto> bookmarkaApGroupList = new ArrayList<BookmarkDto>();
							if (BookmarkConstants.Y.equalsIgnoreCase(responseDB.getCdQuestionResponse())) {
								BookmarkDto checkBox = createBookmark(BookmarkConstants.CHECK_BOX, BookmarkConstants.X);
								bookmarkaApGroupList.add(checkBox);
							} else {
								FormDataGroupDto checkSpace = createFormDataGroup(
										FormGroupsConstants.TMPLAT_CHECK_SPACE, FormConstants.EMPTY_STRING);
								formDataApGroupList.add(checkSpace);
							}
							BookmarkDto areaOfProtection = createBookmark(BookmarkConstants.ACTION_OF_PROTECTION,
									responseDB.getQuestionText());
							bookmarkaApGroupList.add(areaOfProtection);
							if (!ObjectUtils.isEmpty(responseDB.getOtherDescriptionText())) {
								//responseDB.setOtherDescriptionText(FormConstants.EMPTY_STRING);
								if (BookmarkConstants.SECTION_3_AP_OTHER.equalsIgnoreCase(responseDB.getCdQuestion())) {
									List<FormDataGroupDto> formDataApFollowupGrpList = new ArrayList<FormDataGroupDto>();
									FormDataGroupDto apFollowupGrp = createFormDataGroup(
											FormGroupsConstants.TMPLAT_FOLLOWUP_AP, FormConstants.EMPTY_STRING);
									List<BookmarkDto> bookmarkApFollowupGrpList = new ArrayList<BookmarkDto>();
									BookmarkDto apResponse = createBookmark(
											BookmarkConstants.FOLLOWUP_AP_RESPONSE,
											responseDB.getOtherDescriptionText());
									bookmarkApFollowupGrpList.add(apResponse);
									apFollowupGrp.setBookmarkDtoList(bookmarkApFollowupGrpList);
									apFollowupGrp.setFormDataGroupList(formDataApFollowupGrpList);
									formDataApGroupList.add(apFollowupGrp);
								}
							}
							apGroup.setBookmarkDtoList(bookmarkaApGroupList);
							apGroup.setFormDataGroupList(formDataApGroupList);
							formDataRowAPList.add(apGroup);
						}
						rowAP.setBookmarkDtoList(bookmarkRowAPList);
						rowAP.setFormDataGroupList(formDataRowAPList);
						formDataSection3FactorList.add(rowAP);
					}
					section3Factor.setBookmarkDtoList(bookmarkSection3FactorList);
					section3Factor.setFormDataGroupList(formDataSection3FactorList);
					formDataTableGroup3List.add(section3Factor);
				}
				tableGroup3.setFormDataGroupList(formDataTableGroup3List);
				formDataGroupList.add(tableGroup3);
			}

			/**
			 * Populate Section 4: Safety Interventions
			 * 
			 * @param preFillData
			 * @param section4SubSectionMap
			 */
			if (!ObjectUtils.isEmpty(section4SubSectionMap)) {
				List<FormDataGroupDto> formDataTableGroup4List = new ArrayList<FormDataGroupDto>();
				FormDataGroupDto tableGroup4 = createFormDataGroup(FormGroupsConstants.TMPLAT_SECTION4_TABLE,
						FormConstants.EMPTY_STRING);
				for (SDMSafetyAssessmentResponseDto responseDB : section4SubSectionMap
						.get(BookmarkConstants.FAMILY_SAFETY_INTERVENTIONS)) {
					List<FormDataGroupDto> formDataSection4FactorList = new ArrayList<FormDataGroupDto>();
					FormDataGroupDto section4Factor = createFormDataGroup(FormGroupsConstants.TMPLAT_SECTION_4_FSI,
							FormConstants.EMPTY_STRING);
					List<BookmarkDto> bookmarkSection4FactorList = new ArrayList<BookmarkDto>();
					if (BookmarkConstants.Y.equalsIgnoreCase(responseDB.getCdQuestionResponse())) {
						BookmarkDto checkBox = createBookmark(BookmarkConstants.CHECK_BOX, BookmarkConstants.X);
						bookmarkSection4FactorList.add(checkBox);
					} else {
						FormDataGroupDto checkSpace = createFormDataGroup(FormGroupsConstants.TMPLAT_CHECK_SPACE,
								FormConstants.EMPTY_STRING);
						formDataSection4FactorList.add(checkSpace);
					}
					BookmarkDto onOrder = createBookmark(BookmarkConstants.SECTION4_QNORDER,
							responseDB.getQuestionOrder());
					bookmarkSection4FactorList.add(onOrder);
					if (ITEMS_WITH_DOUBLE_ROWS.contains(responseDB.getCdQuestion())) {
						FormDataGroupDto doubleRow = createFormDataGroup(FormGroupsConstants.TMPLAT_DOUBLE_ROW,
								FormConstants.EMPTY_STRING);
						FormDataGroupDto doubleRowSpan = createFormDataGroup(FormGroupsConstants.TMPLAT_DOUBLE_ROW_SPAN,
								FormConstants.EMPTY_STRING);
						BookmarkDto factorFSI = createBookmark(BookmarkConstants.SECTION_4_FACTOR_FSI,
								responseDB.getQuestionText());
						bookmarkSection4FactorList.add(factorFSI);
						formDataSection4FactorList.add(doubleRow);
						formDataSection4FactorList.add(doubleRowSpan);

					} else if (BookmarkConstants.SECTION_4_FSI_QUESTION_8
							.equalsIgnoreCase(responseDB.getCdQuestion())) {
						/*
						 * This is some conditional formatting that will allow
						 * the X marker for the 'Other' intervention to show
						 * correctly in the event the text needs two rows to
						 * display. 90 characters is an approximation
						 */
						if (!ObjectUtils.isEmpty(responseDB.getOtherDescriptionText())
								&& responseDB.getOtherDescriptionText().length() > 90) {
							FormDataGroupDto doubleRow90 = createFormDataGroup(FormGroupsConstants.TMPLAT_DOUBLE_ROW,
									FormConstants.EMPTY_STRING);
							FormDataGroupDto doubleRowSpan90 = createFormDataGroup(
									FormGroupsConstants.TMPLAT_DOUBLE_ROW_SPAN, FormConstants.EMPTY_STRING);
							BookmarkDto factorFSI90 = createBookmark(BookmarkConstants.SECTION_4_FACTOR_FSI,
									responseDB.getQuestionText());
							BookmarkDto otherDiscription90 = createBookmark(BookmarkConstants.OTHER_TEXT,
									responseDB.getOtherDescriptionText());
							formDataSection4FactorList.add(doubleRowSpan90);
							formDataSection4FactorList.add(doubleRow90);
							bookmarkSection4FactorList.add(factorFSI90);
							bookmarkSection4FactorList.add(otherDiscription90);
						} else {
							BookmarkDto factorFSI90 = createBookmark(BookmarkConstants.SECTION_4_FACTOR_FSI,
									responseDB.getQuestionText());
							BookmarkDto otherDiscription90 = createBookmark(BookmarkConstants.OTHER_TEXT,
									responseDB.getOtherDescriptionText());
							bookmarkSection4FactorList.add(factorFSI90);
							bookmarkSection4FactorList.add(otherDiscription90);
						}
					} else {
						BookmarkDto factorFSI = createBookmark(BookmarkConstants.SECTION_4_FACTOR_FSI,
								responseDB.getQuestionText());
						bookmarkSection4FactorList.add(factorFSI);
					}
					section4Factor.setBookmarkDtoList(bookmarkSection4FactorList);
					section4Factor.setFormDataGroupList(formDataSection4FactorList);
					formDataTableGroup4List.add(section4Factor);
				}
				for (SDMSafetyAssessmentResponseDto responseDB : section4SubSectionMap
						.get(BookmarkConstants.CPS_SAFETY_INTERVENTIONS)) {
					List<FormDataGroupDto> formDataSection4Factor2List = new ArrayList<FormDataGroupDto>();
					FormDataGroupDto section4Factor2 = createFormDataGroup(FormGroupsConstants.TMPLAT_SECTION_4_CSI,
							FormConstants.EMPTY_STRING);
					List<BookmarkDto> bookmarkSection4Factor2List = new ArrayList<BookmarkDto>();
					if (BookmarkConstants.Y.equalsIgnoreCase(responseDB.getCdQuestionResponse())) {
						BookmarkDto checkBox = createBookmark(BookmarkConstants.CHECK_BOX, BookmarkConstants.X);
						bookmarkSection4Factor2List.add(checkBox);
					} else {
						FormDataGroupDto checkSpace = createFormDataGroup(FormGroupsConstants.TMPLAT_CHECK_SPACE,
								FormConstants.EMPTY_STRING);
						formDataSection4Factor2List.add(checkSpace);
					}
					BookmarkDto onOrder = createBookmark(BookmarkConstants.SECTION4_QNORDER,
							responseDB.getQuestionOrder());
					bookmarkSection4Factor2List.add(onOrder);
					if (ITEMS_WITH_DOUBLE_ROWS.contains(responseDB.getCdQuestion())) {
						FormDataGroupDto doubleRow = createFormDataGroup(FormGroupsConstants.TMPLAT_DOUBLE_ROW,
								FormConstants.EMPTY_STRING);
						FormDataGroupDto doubleRowSpan = createFormDataGroup(FormGroupsConstants.TMPLAT_DOUBLE_ROW_SPAN,
								FormConstants.EMPTY_STRING);
						BookmarkDto factorCSI = createBookmark(BookmarkConstants.SECTION_4_FACTOR_CSI,
								responseDB.getQuestionText());
						bookmarkSection4Factor2List.add(factorCSI);
						formDataSection4Factor2List.add(doubleRow);
						formDataSection4Factor2List.add(doubleRowSpan);
					} else {
						BookmarkDto factorCSI = createBookmark(BookmarkConstants.SECTION_4_FACTOR_CSI,
								responseDB.getQuestionText());
						bookmarkSection4Factor2List.add(factorCSI);
					}
					section4Factor2.setBookmarkDtoList(bookmarkSection4Factor2List);
					section4Factor2.setFormDataGroupList(formDataSection4Factor2List);
					formDataTableGroup4List.add(section4Factor2);
				}
				tableGroup4.setFormDataGroupList(formDataTableGroup4List);
				formDataGroupList.add(tableGroup4);
			}
		}

		boolean isSection4Complete = Boolean.FALSE;
		if (CodesConstant.CSDMDCSN_SAFE.equals(safetyAssment.getSafetAssessmentList().getCdCurrSafetyDecision())) {
			isSection4Complete = Boolean.TRUE;
		} else {
			List<SDMSafetyAssessmentResponseDto> section4ResponseList = safetyAssment.getSafetAssessmentList()
					.getSafetyResponseBySectionMap().get(BookmarkConstants.SECTION_4);
			if (null != section4ResponseList && section4ResponseList.size() > 0) {
				for (SDMSafetyAssessmentResponseDto responseDB : section4ResponseList) {
					if (BookmarkConstants.Y.equals(responseDB.getCdQuestionResponse())) {
						isSection4Complete = Boolean.TRUE;
						break;
					}
				}
			}
		}

		/**
		 * Populate Section 5: Safety Decision
		 * 
		 * @param preFillData
		 * @param sdmSafetyAssessmentDB
		 */
		List<FormDataGroupDto> formDataTableGroup5List = new ArrayList<FormDataGroupDto>();
		FormDataGroupDto tableGroup5 = createFormDataGroup(FormGroupsConstants.TMPLAT_SECTION5_TABLE,
				FormConstants.EMPTY_STRING);
		List<BookmarkDto> bookmarkTableGroup5List = new ArrayList<BookmarkDto>();
		// Populate safety decision if all the other available sections are
		// complete
		if (Boolean.FALSE.equals(isDangerIndicatorSectionIncomplete)
				&& (Boolean.TRUE.equals(CodesConstant.CSDMDCSN_SAFE.equalsIgnoreCase(
						safetyAssment.getSafetAssessmentList().getCdCurrSafetyDecision()) || (isSection4Complete)))) {
			if (CodesConstant.CSDMDCSN_UNSAFE
					.equalsIgnoreCase(safetyAssment.getSafetAssessmentList().getCdCurrSafetyDecision())) {
				List<FormDataGroupDto> formDataUnsafeGroupList = new ArrayList<FormDataGroupDto>();
				FormDataGroupDto unsafeGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_UNSAFE,
						FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkUnsafeGroupList = new ArrayList<BookmarkDto>();
				if (CodesConstant.CUNSACTN_ALLREMOVED
						.equalsIgnoreCase(safetyAssment.getSafetAssessmentList().getUnsafeDecisionAction())) {
					BookmarkDto checkBox = createBookmark(BookmarkConstants.CHECK_BOX, BookmarkConstants.X);
					bookmarkUnsafeGroupList.add(checkBox);
				} else {
					FormDataGroupDto checkSpace = createFormDataGroup(FormGroupsConstants.TMPLAT_CHECK_SPACE,
							FormConstants.EMPTY_STRING);
					formDataUnsafeGroupList.add(checkSpace);
				}
				if (CodesConstant.CUNSACTN_PLANREQD
						.equalsIgnoreCase(safetyAssment.getSafetAssessmentList().getUnsafeDecisionAction())) {
					FormDataGroupDto doubleRow = createFormDataGroup(FormGroupsConstants.TMPLAT_DOUBLE_ROW,
							FormConstants.EMPTY_STRING);
					FormDataGroupDto doubleRowSpan = createFormDataGroup(FormGroupsConstants.TMPLAT_DOUBLE_ROW_SPAN,
							FormConstants.EMPTY_STRING);
					BookmarkDto checkBox1 = createBookmark(BookmarkConstants.CHECK_BOX_1, BookmarkConstants.X);
					bookmarkUnsafeGroupList.add(checkBox1);
					formDataUnsafeGroupList.add(doubleRowSpan);
					formDataUnsafeGroupList.add(doubleRow);
				} else {
					FormDataGroupDto checkSpace = createFormDataGroup(FormGroupsConstants.TMPLAT_CHECK_SPACE,
							FormConstants.EMPTY_STRING);
					formDataUnsafeGroupList.add(checkSpace);
				}
				unsafeGroup.setBookmarkDtoList(bookmarkUnsafeGroupList);
				unsafeGroup.setFormDataGroupList(formDataUnsafeGroupList);
				formDataTableGroup5List.add(unsafeGroup);
			} else if (CodesConstant.CSDMDCSN_SAFE
					.equalsIgnoreCase(safetyAssment.getSafetAssessmentList().getCdCurrSafetyDecision())) {
				List<FormDataGroupDto> formDataSafeGroupList = new ArrayList<FormDataGroupDto>();
				FormDataGroupDto safeGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_SAFE,
						FormConstants.EMPTY_STRING);
				safeGroup.setFormDataGroupList(formDataSafeGroupList);
				formDataTableGroup5List.add(safeGroup);
				tableGroup5.setFormDataGroupList(formDataTableGroup5List);
			} else if (CodesConstant.CSDMDCSN_SAFEWITHPLAN
					.equalsIgnoreCase(safetyAssment.getSafetAssessmentList().getCdCurrSafetyDecision())) {
				List<FormDataGroupDto> formDataCondSafeGroupList = new ArrayList<FormDataGroupDto>();
				FormDataGroupDto condsafeGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_CONDSAFE,
						FormConstants.EMPTY_STRING);
				condsafeGroup.setFormDataGroupList(formDataCondSafeGroupList);
				formDataTableGroup5List.add(condsafeGroup);
				tableGroup5.setFormDataGroupList(formDataTableGroup5List);
			}
			tableGroup5.setFormDataGroupList(formDataTableGroup5List);
			BookmarkDto discussion = createBookmark(BookmarkConstants.DISCUSSION,
					safetyAssment.getSafetAssessmentList().getTxtAssmtDiscussion());
			bookmarkTableGroup5List.add(discussion);
			tableGroup5.setBookmarkDtoList(bookmarkTableGroup5List);
			tableGroup5.setFormDataGroupList(formDataTableGroup5List);
			formDataGroupList.add(tableGroup5);
		}

		PreFillDataServiceDto preFillData = new PreFillDataServiceDto();
		preFillData.setFormDataGroupList(formDataGroupList);
		preFillData.setBookmarkDtoList(bookmarkNonFrmGrpList);
		return preFillData;
	}

	private Map<String, List<SDMSafetyAssessmentResponseDto>> getSubSectionMap(
			List<SDMSafetyAssessmentResponseDto> list) {
		Map<String, List<SDMSafetyAssessmentResponseDto>> subSectionMap = new HashMap<String, List<SDMSafetyAssessmentResponseDto>>();
		for (SDMSafetyAssessmentResponseDto responseDB : list) {
			String questionCode = responseDB.getCdQuestion();
			String subSectionCode = questionCode.substring(
					questionCode.indexOf(BookmarkConstants.UNDERSCORE_SYMBOL) + 1,
					questionCode.lastIndexOf(BookmarkConstants.UNDERSCORE_SYMBOL));
			List<SDMSafetyAssessmentResponseDto> subSectionResponseList = subSectionMap.get(subSectionCode);
			if (subSectionResponseList == null) {
				subSectionResponseList = new ArrayList<SDMSafetyAssessmentResponseDto>();
				subSectionMap.put(subSectionCode, subSectionResponseList);
			}
			subSectionResponseList.add(responseDB);
		}
		return subSectionMap;
	}

	private String getSection3SubSectionName(int rowId) {
		String strSubSectionName = null;
		if (rowId == 0) {
			strSubSectionName = "Caregiver problem solving";
		} else if (rowId == 1) {
			strSubSectionName = "Caregiver support network";
		} else if (rowId == 2) {
			strSubSectionName = "Child problem solving";
		} else if (rowId == 3) {
			strSubSectionName = "Child support network";
		} else if (rowId == 4) {
			strSubSectionName = "Other";
		}
		return strSubSectionName;
	}
	
	//QCR 62702 - SDM Removal to show/hide SDM word in the Form title
	private boolean showSDM(SDMSafetyAssmentDto safetyAssment){
		boolean showSDMInTitle=false;
		Date dtSDMRemoval=codesDao.getAppRelDate(ServiceConstants.CRELDATE_SDM_REMOVAL_2020);	
		
		if (!ObjectUtils.isEmpty(safetyAssment.getSafetAssessmentList().getDtAssessmentCompleted())) {
			if (safetyAssment.getSafetAssessmentList().getDtAssessmentCompleted().compareTo(dtSDMRemoval) < 0) {
				showSDMInTitle = true;
			}
		} else {
			if (!ObjectUtils.isEmpty(safetyAssment.getSafetAssessmentList().getDtSafetyAssessed())
					&& safetyAssment.getSafetAssessmentList().getDtSafetyAssessed().compareTo(dtSDMRemoval) < 0
					&& !ObjectUtils.isEmpty(safetyAssment.getSafetAssessmentList().getDtStageClosure())
					&& safetyAssment.getSafetAssessmentList().getDtStageClosure().compareTo(dtSDMRemoval) < 0) {
				showSDMInTitle = true;
			}
		}
		return showSDMInTitle;	
	}
}
			
	

