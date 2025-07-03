package us.tx.state.dfps.service.forms.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.service.common.BookmarkConstants;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.FormConstants;
import us.tx.state.dfps.service.common.FormGroupsConstants;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.dao.CodesDao;
import us.tx.state.dfps.service.common.response.SDMRiskReassessmentRes;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.forms.dto.BookmarkDto;
import us.tx.state.dfps.service.forms.dto.FormDataGroupDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.investigation.dto.SDMSafetyAssmentDto;
import us.tx.state.dfps.service.riskreasmnt.SDMRiskReasmntAnswerDto;
import us.tx.state.dfps.service.riskreasmnt.SDMRiskReasmntDto;
import us.tx.state.dfps.service.riskreasmnt.SDMRiskReasmntFollowupDto;
import us.tx.state.dfps.service.riskreasmnt.SDMRiskReasmntQstnDto;
import us.tx.state.dfps.service.workload.dto.PersonDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:
 * FbssReassessmentPrefillData will implement returnPrefillData operation
 * defined in DocumentServiceUtil Interface to populate the prefill data for
 * Form FBSSRISK Aug 16, 2018- 5:14:11 PM © 2017 Texas Department of Family and
 * Protective Services
 */
@Component
public class FbssReassessmentPrefillData extends DocumentServiceUtil {

	// Class Constants
	private static final String PRIMARY_CAREGIVER = "Primary Caregiver";
	private static final String SECONDARY_CAREGIVER = "Secondary Caregiver";
	private static final String PRIMARY_PARENT = "Primary Parent";
	private static final String SECONDARY_PARENT = "Secondary Parent";
	private static final String PARENT = "Parent";
	private static final String CAREGIVER = "Caregiver";

	public FbssReassessmentPrefillData() {
		super();
	}
	
	@Autowired
	private CodesDao codesDao;

	/**
	 * Method Description: This method is used to prefill the data from the
	 * different Dao by passing Dao output Dtos and bookmark and form group bookmark
	 * Dto as objects as input request
	 * 
	 * @param parentDtoobj
	 * @param bookmarkDtoObj
	 * @return PreFillData
	 * 
	 */
	@Override
	public PreFillDataServiceDto returnPrefillData(Object parentDtoobj) {

		SDMRiskReassessmentRes sdmRiskReassessmentRes = (SDMRiskReassessmentRes) parentDtoobj;
		SDMRiskReasmntDto sdmRiskReasmntDto = sdmRiskReassessmentRes.getSdmRiskReasmntDto();

		boolean indCurrentStageFRE = CodesConstant.CSTAGES_FRE.equals(sdmRiskReasmntDto.getCdStage());
		boolean indCurrentStageFPR = CodesConstant.CSTAGES_FPR.equals(sdmRiskReasmntDto.getCdStage());

		if (ObjectUtils.isEmpty(sdmRiskReasmntDto.getHouseholdMembers())) {
			sdmRiskReasmntDto.setHouseholdMembers(new ArrayList<Long>());
		}

		// Get person map for easy access
		Map<Long, PersonDto> personsMap = new LinkedHashMap<Long, PersonDto>();
		if (!ObjectUtils.isEmpty(sdmRiskReasmntDto.getPersonList())) {
			sdmRiskReasmntDto.getPersonList().stream().forEach(o -> personsMap.put(o.getIdPerson(), o));
		}

		List<FormDataGroupDto> formDataGroupList = new ArrayList<FormDataGroupDto>();
		
		/**QCR 62702 SDM Removal-Change of title depending on Date of Assessment completed
		 * Before or After 9/1/2020
		 * **/
		// bookmarks without groups
		List<BookmarkDto> bookmarkNonFormGrpList = new ArrayList<BookmarkDto>();
		BookmarkDto addBookmarkPrintTitle = null;
		
		boolean showSDMTitle=showSDM(sdmRiskReasmntDto);
		if(showSDMTitle){
			addBookmarkPrintTitle = createBookmark(BookmarkConstants.TXT_SDM_TITLE_RISKRE, FormConstants.TXT_SDMTITLE);
		}else{
			addBookmarkPrintTitle = createBookmark(BookmarkConstants.TXT_SDM_TITLE_RISKRE,FormConstants.EMPTY_STRING);
		}	
		
		bookmarkNonFormGrpList.add(addBookmarkPrintTitle);
		/**End QCR 62702***/
		
		// Household members are required for FPR
		if (indCurrentStageFPR) {			
			
			// FormDataGroups for Household Members
			for (Long currentHouseHold : sdmRiskReasmntDto.getHouseholdMembers()) {
				PersonDto personDto = personsMap.get(currentHouseHold);
				FormDataGroupDto childAssessedGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_HOUSEHOLD,
						FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkChildAssessedList = new ArrayList<BookmarkDto>();

				BookmarkDto bookmarkNm = createBookmark(BookmarkConstants.HOUSEHOLD_NAME, personDto.getNmPersonFull());
				bookmarkChildAssessedList.add(bookmarkNm);

				BookmarkDto bookmarkAge = createBookmark(BookmarkConstants.HOUSEHOLD_AGE, personDto.getPersonAge());
				bookmarkChildAssessedList.add(bookmarkAge);

				BookmarkDto bookmarkRelatn = createBookmarkWithCodesTable(BookmarkConstants.HOUSEHOLD_REL,
						personDto.getCdStagePersRelInt(), CodesConstant.CRELVICT);
				bookmarkChildAssessedList.add(bookmarkRelatn);

				childAssessedGroupDto.setBookmarkDtoList(bookmarkChildAssessedList);
				formDataGroupList.add(childAssessedGroupDto);
			}
		}

		// Populate groups for question and response

		if (!ObjectUtils.isEmpty(sdmRiskReasmntDto.getQuestions())) {

			for (SDMRiskReasmntQstnDto currentQuestion : sdmRiskReasmntDto.getQuestions()) {
				FormDataGroupDto questionsGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_QUESTIONS_SECTION,
						FormConstants.EMPTY_STRING);
				List<BookmarkDto> questionsBookmarkList = new ArrayList<BookmarkDto>();
				List<FormDataGroupDto> questionChildGroups = new ArrayList<FormDataGroupDto>();

				// Create store for followup responses
				List<SDMRiskReasmntFollowupDto> commonFollowUpDtoList = new ArrayList<SDMRiskReasmntFollowupDto>();
				List<SDMRiskReasmntFollowupDto> primaryFollowUpDtoList = new ArrayList<SDMRiskReasmntFollowupDto>();
				List<SDMRiskReasmntFollowupDto> secondaryFollowUpDtoList = new ArrayList<SDMRiskReasmntFollowupDto>();

				// question number
				BookmarkDto bookmarkQuestionNbr = createBookmark(BookmarkConstants.QSTN_NBR,
						currentQuestion.getCdQstn());
				questionsBookmarkList.add(bookmarkQuestionNbr);

				// question label
				String questionLabel = currentQuestion.getTxtQstn();

				// If stage is FRE, replace caregiver text with parent
				if (indCurrentStageFRE) {
					questionLabel = questionLabel.replace(CAREGIVER, PARENT);
				}
				BookmarkDto bookmarkQuestionlabel = createBookmark(BookmarkConstants.QSTN_LABEL, questionLabel);
				questionsBookmarkList.add(bookmarkQuestionlabel);

				// Response loop
				if (!ObjectUtils.isEmpty(currentQuestion.getAnswers())) {
					boolean indPriMSecHeaderAdded = false;
					for (SDMRiskReasmntAnswerDto currentAnswer : currentQuestion.getAnswers()) {
						FormDataGroupDto reponseGroupDto = createFormDataGroup(
								FormGroupsConstants.TMPLAT_RESPONSE_SECTION,
								FormGroupsConstants.TMPLAT_QUESTIONS_SECTION);
						List<FormDataGroupDto> responseChildDtolist = new ArrayList<FormDataGroupDto>();

						// Handle differently for two questions, that has
						// primary and secondary reponse
						if (ServiceConstants.STRING_SIX.equals(currentQuestion.getCdQstn())
								|| ServiceConstants.STRING_TEN.equals(currentQuestion.getCdQstn())) {
							FormDataGroupDto reponsePrimSecGroupDto = createFormDataGroup(
									FormGroupsConstants.TMPLAT_RSPNS_WIT_PRIM_SEC,
									FormGroupsConstants.TMPLAT_RESPONSE_SECTION);

							// Add header for question 6 and 10 only once
							if (!indPriMSecHeaderAdded) {
								FormDataGroupDto reponsePrimSecHeaderDto = createFormDataGroup(
										FormGroupsConstants.TMPLAT_PRIM_SEC_LABEL,
										FormGroupsConstants.TMPLAT_RESPONSE_SECTION);
								responseChildDtolist.add(reponsePrimSecHeaderDto);
								indPriMSecHeaderAdded = true;
							}

							List<BookmarkDto> responseBookmarkList = new ArrayList<BookmarkDto>();

							String primResponseChecked = ServiceConstants.EMPTY_STRING;
							String secResponseChecked = ServiceConstants.EMPTY_STRING;

							if (!ObjectUtils.isEmpty(currentAnswer.getCdAnswer())
									&& !ObjectUtils.isEmpty(currentQuestion.getCdAnsPrimary())
									&& currentAnswer.getCdAnswer().equals(currentQuestion.getCdAnsPrimary())) {
								primResponseChecked = ServiceConstants.CHECKED;
							}

							if (!ObjectUtils.isEmpty(currentAnswer.getCdAnswer())
									&& !ObjectUtils.isEmpty(currentQuestion.getCdAnsSecondary())
									&& currentAnswer.getCdAnswer().equals(currentQuestion.getCdAnsSecondary())) {
								secResponseChecked = ServiceConstants.CHECKED;
							}

							// answer checked control
							BookmarkDto bookmarkPrimAnswerCheck = createBookmark(
									BookmarkConstants.IP_CHECK_PRIM_RSPNS_CHECKED, primResponseChecked);
							responseBookmarkList.add(bookmarkPrimAnswerCheck);
							BookmarkDto bookmarkSecAnswerCheck = createBookmark(
									BookmarkConstants.IP_CHECK_SECN_RSPNS_CHECKED, secResponseChecked);
							responseBookmarkList.add(bookmarkSecAnswerCheck);

							// answer label
							BookmarkDto bookmarkAnswerlabel = createBookmark(BookmarkConstants.RSPNS_LABEL_PRIM,
									currentAnswer.getTxtAnswer());
							responseBookmarkList.add(bookmarkAnswerlabel);

							reponsePrimSecGroupDto.setBookmarkDtoList(responseBookmarkList);
							responseChildDtolist.add(reponsePrimSecGroupDto);

						} else {
							FormDataGroupDto reponseNonPrimSecGroupDto = createFormDataGroup(
									FormGroupsConstants.TMPLAT_RSPNS_WITHOUT_PRIM_SEC,
									FormGroupsConstants.TMPLAT_RESPONSE_SECTION);
							List<BookmarkDto> responseBookmarkList = new ArrayList<BookmarkDto>();

							String responseChecked = ServiceConstants.EMPTY_STRING;

							if (!ObjectUtils.isEmpty(currentQuestion.getCdAnswer())
									&& currentAnswer.getCdAnswer().equals(currentQuestion.getCdAnswer())) {
								responseChecked = ServiceConstants.CHECKED;
							}
							// answer checked control
							BookmarkDto bookmarkAnswerCheck = createBookmark(BookmarkConstants.IP_CHECK_RSPNS_CHECKED,
									responseChecked);
							responseBookmarkList.add(bookmarkAnswerCheck);

							// answer label
							BookmarkDto bookmarkAnswerlabel = createBookmark(BookmarkConstants.RSPNS_LABEL,
									currentAnswer.getTxtAnswer());
							responseBookmarkList.add(bookmarkAnswerlabel);

							reponseNonPrimSecGroupDto.setBookmarkDtoList(responseBookmarkList);

							responseChildDtolist.add(reponseNonPrimSecGroupDto);

						}
						// FollowUps store
						if (!ObjectUtils.isEmpty(currentAnswer.getFollowupQuestions())) {
							if (!ObjectUtils.isEmpty(currentQuestion.getCdAnsPrimary())
									|| !ObjectUtils.isEmpty(currentQuestion.getCdAnsSecondary())) {
								if (!ObjectUtils.isEmpty(currentQuestion.getCdAnsPrimary())) {
									primaryFollowUpDtoList = currentAnswer.getFollowupQuestions();
								}
								if (!ObjectUtils.isEmpty(currentQuestion.getCdAnsSecondary())) {
									secondaryFollowUpDtoList = currentAnswer.getFollowupQuestionSec();
								}
							} else {
								commonFollowUpDtoList = currentAnswer.getFollowupQuestions();
							}
						}
						reponseGroupDto.setFormDataGroupList(responseChildDtolist);
						questionChildGroups.add(reponseGroupDto);
					}

				}

				// Followup for question 10
				if (ServiceConstants.STRING_TEN.equals(currentQuestion.getCdQstn())) {
					String followupTxt = sdmRiskReasmntDto.getReasonForQstn10();
					FormDataGroupDto followUp10 = createFormDataGroup(FormGroupsConstants.TMPLAT_TXT_FOLLOW_SECTION,
							FormGroupsConstants.TMPLAT_QUESTIONS_SECTION);
					List<BookmarkDto> followUp10BookmarkList = new ArrayList<BookmarkDto>();

					// FollowUp Text
					BookmarkDto bookmarkTextDescription = createBookmark(BookmarkConstants.REASON_FOR_FOLLOWUP_TXT,
							followupTxt);
					followUp10BookmarkList.add(bookmarkTextDescription);

					followUp10.setBookmarkDtoList(followUp10BookmarkList);
					questionChildGroups.add(followUp10);

				}
				// FollowUp Display
				if (!ObjectUtils.isEmpty(primaryFollowUpDtoList) || !ObjectUtils.isEmpty(secondaryFollowUpDtoList)
						|| !ObjectUtils.isEmpty(commonFollowUpDtoList)) {
					if (ServiceConstants.STRING_SIX.equals(currentQuestion.getCdQstn())) {
						SDMRiskReasmntFollowupDto selectedAnswer = null;

						if (!ObjectUtils.isEmpty(primaryFollowUpDtoList)) {
							selectedAnswer = primaryFollowUpDtoList.stream()
									.filter(p -> !ObjectUtils.isEmpty(p.getCdAnswerSelected())).findFirst()
									.orElse(null);
						}
						if (ObjectUtils.isEmpty(selectedAnswer) && !ObjectUtils.isEmpty(secondaryFollowUpDtoList)) {
							selectedAnswer = secondaryFollowUpDtoList.stream()
									.filter(p -> !ObjectUtils.isEmpty(p.getCdAnswerSelected())).findFirst()
									.orElse(null);
						}
						if (!ObjectUtils.isEmpty(primaryFollowUpDtoList) && !ObjectUtils.isEmpty(selectedAnswer)) {

							boolean primNameadded = false;

							// Moved this Code outside to Display Section 6 Primary - Checkbox Selection Only Once
							FormDataGroupDto reponseFollowUpGroupDto = createFormDataGroup(
									FormGroupsConstants.TMPLAT_RESPONSE_FOLLOW_SECTION_DUP,
									FormGroupsConstants.TMPLAT_QUESTIONS_SECTION_DUP);
							
							List<BookmarkDto> responseFollowUpBookmarkList = new ArrayList<BookmarkDto>();
							List<FormDataGroupDto> followupResponseGroup = new ArrayList<FormDataGroupDto>();
							
							for (SDMRiskReasmntFollowupDto currentFollowUp : primaryFollowUpDtoList) {								

								// Display Parent/Caregiver Name
								FormDataGroupDto followUpGroupNameDto = createFormDataGroup(
										FormGroupsConstants.TMPLAT_PARENT_NAME_DISPLAY_DUP,
										FormGroupsConstants.TMPLAT_RESPONSE_FOLLOW_SECTION_DUP);
								List<BookmarkDto> followUpNameBookmarkList = new ArrayList<BookmarkDto>();

								String parentType = indCurrentStageFPR ? PRIMARY_CAREGIVER : PRIMARY_PARENT;
								// FollowUp Name Type
								BookmarkDto bookmarknameType = createBookmark("FOLLOWUP_PARENT_TYPE_DUP",
										parentType);
								followUpNameBookmarkList.add(bookmarknameType);

								// answer label
								BookmarkDto followupDisplayname = createBookmark(
										BookmarkConstants.FOLLOWUP_PARENT_NM_FULL_DUP,
										personsMap.get(sdmRiskReasmntDto.getIdPrmryPrsn()).getNmPersonFull());
								followUpNameBookmarkList.add(followupDisplayname);

								followUpGroupNameDto.setBookmarkDtoList(followUpNameBookmarkList);

								String followupChecked = ServiceConstants.EMPTY_STRING;

								if (!ObjectUtils.isEmpty(currentFollowUp.getCdAnswerSelected())) {
									followupChecked = ServiceConstants.CHECKED;
								}

								if(currentFollowUp.getTxtFollowupQuestion().equalsIgnoreCase("Alcohol"))
								{
									// FollowUp checked
									BookmarkDto bookmarkAnswerCheck = createBookmark(
											BookmarkConstants.IP_CHECK_RSPNS_FOLLOWUP_CHECKED_ALC, followupChecked);
									responseFollowUpBookmarkList.add(bookmarkAnswerCheck);
								}else if(currentFollowUp.getTxtFollowupQuestion().equalsIgnoreCase("Heroin"))
								{
									// FollowUp checked
									BookmarkDto bookmarkAnswerCheck = createBookmark(
											BookmarkConstants.IP_CHECK_RSPNS_FOLLOWUP_CHECKED_HER, followupChecked);
									responseFollowUpBookmarkList.add(bookmarkAnswerCheck);
								}else if(currentFollowUp.getTxtFollowupQuestion().equalsIgnoreCase("Marijuana"))
								{// FollowUp checked
									BookmarkDto bookmarkAnswerCheck = createBookmark(
											BookmarkConstants.IP_CHECK_RSPNS_FOLLOWUP_CHECKED_MAR, followupChecked);
									responseFollowUpBookmarkList.add(bookmarkAnswerCheck);
									
								}else if(currentFollowUp.getTxtFollowupQuestion().equalsIgnoreCase("Cocaine"))
								{
									// FollowUp checked
									BookmarkDto bookmarkAnswerCheck = createBookmark(
											BookmarkConstants.IP_CHECK_RSPNS_FOLLOWUP_CHECKED_COC, followupChecked);
									responseFollowUpBookmarkList.add(bookmarkAnswerCheck);
								}else if(currentFollowUp.getTxtFollowupQuestion().equalsIgnoreCase("Methamphetamine"))
								{
									// FollowUp checked
									BookmarkDto bookmarkAnswerCheck = createBookmark(
											BookmarkConstants.IP_CHECK_RSPNS_FOLLOWUP_CHECKED_MAT, followupChecked);
									responseFollowUpBookmarkList.add(bookmarkAnswerCheck);
								}else if(currentFollowUp.getTxtFollowupQuestion().equalsIgnoreCase("Other"))
								{
									// FollowUp checked
									BookmarkDto bookmarkAnswerCheck = createBookmark(
											BookmarkConstants.IP_CHECK_RSPNS_FOLLOWUP_CHECKED_OTHR, followupChecked);
									responseFollowUpBookmarkList.add(bookmarkAnswerCheck);
									
									if(!ObjectUtils.isEmpty(currentFollowUp.getTxtSbstncAbusePrmry()))
									{
									BookmarkDto bookmarkOtherComment = createBookmark(
											BookmarkConstants.OTHER_COMMENT, currentFollowUp.getTxtSbstncAbusePrmry());
									responseFollowUpBookmarkList.add(bookmarkOtherComment);
									}
									else
									{
										BookmarkDto bookmarkOtherComment = createBookmark(
												BookmarkConstants.OTHER_COMMENT, FormConstants.EMPTY_SPACE);
										responseFollowUpBookmarkList.add(bookmarkOtherComment);
									}
								}

								if (!primNameadded) {
									followupResponseGroup.add(followUpGroupNameDto);
									reponseFollowUpGroupDto.setFormDataGroupList(followupResponseGroup);
									primNameadded = true;
								}
								
							}
							reponseFollowUpGroupDto.setBookmarkDtoList(responseFollowUpBookmarkList);
							questionChildGroups.add(reponseFollowUpGroupDto);
						}
						if (!ObjectUtils.isEmpty(secondaryFollowUpDtoList) && !ObjectUtils.isEmpty(selectedAnswer)) {

							// Moved this Code outside to Display Section 6 Secondary - Checkbox Selection Only Once
							
							FormDataGroupDto reponseFollowUpGroupDto = createFormDataGroup(
									FormGroupsConstants.TMPLAT_RESPONSE_FOLLOW_SECTION_DUP,
									FormGroupsConstants.TMPLAT_QUESTIONS_SECTION_DUP);
							List<BookmarkDto> responseFollowUpBookmarkList = new ArrayList<BookmarkDto>();
							List<FormDataGroupDto> followupResponseGroup = new ArrayList<FormDataGroupDto>();
							
							boolean secNameAdded = false;
							for (SDMRiskReasmntFollowupDto currentFollowUp : secondaryFollowUpDtoList) {
								
								// Display Parent/Caregiver Name
								String parentType = indCurrentStageFPR ? SECONDARY_CAREGIVER : SECONDARY_PARENT;

								FormDataGroupDto followUpGroupNameDto = createFormDataGroup(
										FormGroupsConstants.TMPLAT_PARENT_NAME_DISPLAY_DUP,
										FormGroupsConstants.TMPLAT_RESPONSE_FOLLOW_SECTION_DUP);
								List<BookmarkDto> followUpNameBookmarkList = new ArrayList<BookmarkDto>();

								// FollowUp Name Type
								BookmarkDto bookmarknameType = createBookmark(BookmarkConstants.FOLLOWUP_PARENT_TYPE_DUP,
										parentType);
								followUpNameBookmarkList.add(bookmarknameType);

								// answer label
								BookmarkDto followupDisplayname = createBookmark(
										BookmarkConstants.FOLLOWUP_PARENT_NM_FULL_DUP,
										personsMap.get(sdmRiskReasmntDto.getIdScndryPrsn()).getNmPersonFull());
								followUpNameBookmarkList.add(followupDisplayname);

								followUpGroupNameDto.setBookmarkDtoList(followUpNameBookmarkList);

								String followupChecked = ServiceConstants.EMPTY_STRING;

								if (!ObjectUtils.isEmpty(currentFollowUp.getCdAnswerSelected())) {
									followupChecked = ServiceConstants.CHECKED;
								}

								if(currentFollowUp.getTxtFollowupQuestion().equalsIgnoreCase("Alcohol"))
								{
									// FollowUp checked
									BookmarkDto bookmarkAnswerCheck = createBookmark(
											BookmarkConstants.IP_CHECK_RSPNS_FOLLOWUP_CHECKED_ALC, followupChecked);
									responseFollowUpBookmarkList.add(bookmarkAnswerCheck);
								}else if(currentFollowUp.getTxtFollowupQuestion().equalsIgnoreCase("Heroin"))
								{
									// FollowUp checked
									BookmarkDto bookmarkAnswerCheck = createBookmark(
											BookmarkConstants.IP_CHECK_RSPNS_FOLLOWUP_CHECKED_HER, followupChecked);
									responseFollowUpBookmarkList.add(bookmarkAnswerCheck);
								}else if(currentFollowUp.getTxtFollowupQuestion().equalsIgnoreCase("Marijuana"))
								{// FollowUp checked
									BookmarkDto bookmarkAnswerCheck = createBookmark(
											BookmarkConstants.IP_CHECK_RSPNS_FOLLOWUP_CHECKED_MAR, followupChecked);
									responseFollowUpBookmarkList.add(bookmarkAnswerCheck);
									
								}else if(currentFollowUp.getTxtFollowupQuestion().equalsIgnoreCase("Cocaine"))
								{
									// FollowUp checked
									BookmarkDto bookmarkAnswerCheck = createBookmark(
											BookmarkConstants.IP_CHECK_RSPNS_FOLLOWUP_CHECKED_COC, followupChecked);
									responseFollowUpBookmarkList.add(bookmarkAnswerCheck);
								}else if(currentFollowUp.getTxtFollowupQuestion().equalsIgnoreCase("Methamphetamine"))
								{
									// FollowUp checked
									BookmarkDto bookmarkAnswerCheck = createBookmark(
											BookmarkConstants.IP_CHECK_RSPNS_FOLLOWUP_CHECKED_MAT, followupChecked);
									responseFollowUpBookmarkList.add(bookmarkAnswerCheck);
								}else if(currentFollowUp.getTxtFollowupQuestion().equalsIgnoreCase("Other"))
								{
									// FollowUp checked
									BookmarkDto bookmarkAnswerCheck = createBookmark(
											BookmarkConstants.IP_CHECK_RSPNS_FOLLOWUP_CHECKED_OTHR, followupChecked);
									responseFollowUpBookmarkList.add(bookmarkAnswerCheck);
									
									if(!ObjectUtils.isEmpty(currentFollowUp.getTxtSbstncAbuseSec()))
									{
									BookmarkDto bookmarkOtherComment = createBookmark(
											BookmarkConstants.OTHER_COMMENT, currentFollowUp.getTxtSbstncAbuseSec());
									responseFollowUpBookmarkList.add(bookmarkOtherComment);
									}
									else
									{
										BookmarkDto bookmarkOtherComment = createBookmark(
												BookmarkConstants.OTHER_COMMENT, FormConstants.EMPTY_SPACE);
										responseFollowUpBookmarkList.add(bookmarkOtherComment);
									}							
									
								}

								if (!secNameAdded) {
									followupResponseGroup.add(followUpGroupNameDto);
									reponseFollowUpGroupDto.setFormDataGroupList(followupResponseGroup);
									secNameAdded = true;
								}								
							}
							reponseFollowUpGroupDto.setBookmarkDtoList(responseFollowUpBookmarkList);
							questionChildGroups.add(reponseFollowUpGroupDto);
						}
					} else {
						if (!ObjectUtils.isEmpty(commonFollowUpDtoList)) {
							for (SDMRiskReasmntFollowupDto currentFollowUp : commonFollowUpDtoList) {
								FormDataGroupDto reponseFollowUpGroupDto = createFormDataGroup(
										FormGroupsConstants.TMPLAT_RESPONSE_FOLLOW_SECTION,
										FormGroupsConstants.TMPLAT_QUESTIONS_SECTION);
								List<BookmarkDto> responseFollowUpBookmarkList = new ArrayList<BookmarkDto>();

								String followupChecked = ServiceConstants.EMPTY_STRING;

								if (!ObjectUtils.isEmpty(currentFollowUp.getCdAnswerSelected())) {
									followupChecked = ServiceConstants.CHECKED;
								}

								// FollowUp checked
								BookmarkDto bookmarkAnswerCheck = createBookmark(
										BookmarkConstants.IP_CHECK_RSPNS_FOLLOWUP_CHECKED, followupChecked);
								responseFollowUpBookmarkList.add(bookmarkAnswerCheck);

								// answer label
								BookmarkDto bookmarkAnswerlabel = createBookmark(BookmarkConstants.FOLLOWUP_LABEL,
										currentFollowUp.getTxtFollowupQuestion());
								responseFollowUpBookmarkList.add(bookmarkAnswerlabel);

								reponseFollowUpGroupDto.setBookmarkDtoList(responseFollowUpBookmarkList);
								questionChildGroups.add(reponseFollowUpGroupDto);
							}
						}
					}
				}

				// add question number and question label bookmarks and children
				// to questionsBookmarkList
				questionsGroupDto.setFormDataGroupList(questionChildGroups);
				questionsGroupDto.setBookmarkDtoList(questionsBookmarkList);
				formDataGroupList.add(questionsGroupDto);
			}

		}

		// Score level
		if (!ObjectUtils.isEmpty(sdmRiskReasmntDto.getRiskScore())) {
			BookmarkDto bookmarkRiskScore = createBookmark(BookmarkConstants.ASSMT_SCORE_LEVEL,
					sdmRiskReasmntDto.getRiskScore());
			bookmarkNonFormGrpList.add(bookmarkRiskScore);
		}

		// Risk level
		if (!ObjectUtils.isEmpty(sdmRiskReasmntDto.getCdScoredRiskLevel())) {
			BookmarkDto bookmarkRiskLevel = createBookmarkWithCodesTable(BookmarkConstants.ASSMT_RISK_LEVEL,
					sdmRiskReasmntDto.getCdScoredRiskLevel(), CodesConstant.CSDMRLVL);
			bookmarkNonFormGrpList.add(bookmarkRiskLevel);
		}

		// No Overrides / Policy Override / Discretionary
		if (!ObjectUtils.isEmpty(sdmRiskReasmntDto.getCdOverride())) {
			if (CodesConstant.CDOVRIDE_NOOVERRIDE.equals(sdmRiskReasmntDto.getCdOverride())) {
				// IP_CHECK_NO_OVERRIDES
				BookmarkDto bookmarNoOverride = createBookmark(BookmarkConstants.IP_CHECK_NO_OVERRIDES,
						ServiceConstants.CHECKED);
				bookmarkNonFormGrpList.add(bookmarNoOverride);

			}
			if (CodesConstant.CDOVRIDE_POLICYOVERRIDE.equals(sdmRiskReasmntDto.getCdOverride())) {
				// IP_CHECK_OVERRIDES
				BookmarkDto bookmarOverride = createBookmark(BookmarkConstants.IP_CHECK_OVERRIDES,
						ServiceConstants.CHECKED);
				bookmarkNonFormGrpList.add(bookmarOverride);

				// policy Override Options
				if (!ObjectUtils.isEmpty(sdmRiskReasmntDto.getIndPOChildLessThanThreeInjured())
						&& ServiceConstants.Y.equals(sdmRiskReasmntDto.getIndPOChildLessThanThreeInjured())) {
					// IP_CHECK_OVERRIDE_INJURY3
					BookmarkDto bookmarOverrideOption1 = createBookmark(BookmarkConstants.IP_CHECK_OVERRIDE_INJURY3,
							ServiceConstants.CHECKED);
					bookmarkNonFormGrpList.add(bookmarOverrideOption1);
				}
				if (!ObjectUtils.isEmpty(sdmRiskReasmntDto.getIndPOChildSexualAbuse())
						&& ServiceConstants.Y.equals(sdmRiskReasmntDto.getIndPOChildSexualAbuse())) {
					// IP_CHECK_OVERRIDE_INJURY3
					BookmarkDto bookmarOverrideOption2 = createBookmark(BookmarkConstants.IP_CHECK_OVERRIDE_SEX_ABUSE,
							ServiceConstants.CHECKED);
					bookmarkNonFormGrpList.add(bookmarOverrideOption2);
				}
				if (!ObjectUtils.isEmpty(sdmRiskReasmntDto.getIndPOChildLessThanSixteenInjured())
						&& ServiceConstants.Y.equals(sdmRiskReasmntDto.getIndPOChildLessThanSixteenInjured())) {
					// IP_CHECK_OVERRIDE_INJURY3
					BookmarkDto bookmarOverrideOption3 = createBookmark(BookmarkConstants.IP_CHECK_OVERRIDE_INJURY16,
							ServiceConstants.CHECKED);
					bookmarkNonFormGrpList.add(bookmarOverrideOption3);
				}
				if (!ObjectUtils.isEmpty(sdmRiskReasmntDto.getIndPOChildDeath())
						&& ServiceConstants.Y.equals(sdmRiskReasmntDto.getIndPOChildDeath())) {
					// IP_CHECK_OVERRIDE_INJURY3
					BookmarkDto bookmarOverrideOption4 = createBookmark(BookmarkConstants.IP_CHECK_OVERRIDE_DEATH,
							ServiceConstants.CHECKED);
					bookmarkNonFormGrpList.add(bookmarOverrideOption4);
				}
			}
			if (CodesConstant.CDOVRIDE_DISCRETIONARYOVERRID.equals(sdmRiskReasmntDto.getCdOverride())) {
				// IP_CHECK_OVERRIDES_DESCR
				BookmarkDto bookmarDescrOverride = createBookmark(BookmarkConstants.IP_CHECK_OVERRIDES_DESCR,
						ServiceConstants.CHECKED);
				bookmarkNonFormGrpList.add(bookmarDescrOverride);

				// Discretionary override option
				if (!ObjectUtils.isEmpty(sdmRiskReasmntDto.getCdRiskLevelDiscOvrride())) {
					String bookmarkToSelect = ServiceConstants.EMPTY_STRING;

					switch (sdmRiskReasmntDto.getCdRiskLevelDiscOvrride()) {
					case CodesConstant.CSDMRLVL_LOW:
						bookmarkToSelect = BookmarkConstants.IP_CHECK_DESCR_LOW;
						break;
					case CodesConstant.CSDMRLVL_MOD:
						bookmarkToSelect = BookmarkConstants.IP_CHECK_DESCR_MODERATE;
						break;
					case CodesConstant.CSDMRLVL_HIGH:
						bookmarkToSelect = BookmarkConstants.IP_CHECK_DESCR_HIGH;
						break;
					case CodesConstant.CSDMRLVL_VERYHIGH:
						bookmarkToSelect = BookmarkConstants.IP_CHECK_DESCR_VERYHIGH;
						break;
					}

					if (!ObjectUtils.isEmpty(bookmarkToSelect)) {
						BookmarkDto bookmarDiscSelection = createBookmark(bookmarkToSelect, ServiceConstants.CHECKED);
						bookmarkNonFormGrpList.add(bookmarDiscSelection);
					}
					// Discretionary override reason
					if (!ObjectUtils.isEmpty(sdmRiskReasmntDto.getTxtOverrideReason())) {
						// DISCR_OVERRIDE_REASON
						BookmarkDto bookmarOverrideReason = createBookmark(BookmarkConstants.DISCR_OVERRIDE_REASON,
								sdmRiskReasmntDto.getTxtOverrideReason());
						bookmarkNonFormGrpList.add(bookmarOverrideReason);
					}
				}
			}
		}

		// Recommended Decision - Final Risk
		if (!ObjectUtils.isEmpty(sdmRiskReasmntDto.getCdFinalRiskLevel())) {
			BookmarkDto bookmarkFinalRiskLevel = createBookmarkWithCodesTable(BookmarkConstants.FINAL_RISK_LEVEL,
					sdmRiskReasmntDto.getCdFinalRiskLevel(), CodesConstant.CSDMRLVL);
			bookmarkNonFormGrpList.add(bookmarkFinalRiskLevel);
		}

		// Recommended Decision - Recommendation
		if (!ObjectUtils.isEmpty(sdmRiskReasmntDto.getTxtRecommendation())) {
			BookmarkDto bookmarktxtRecommmendation = createBookmark(BookmarkConstants.FINAL_RECOMMENDATION,
					sdmRiskReasmntDto.getTxtRecommendation());
			bookmarkNonFormGrpList.add(bookmarktxtRecommmendation);
		}

		// Planned action Continue Services/Close Case
		if (!ObjectUtils.isEmpty(sdmRiskReasmntDto.getCdPlndActn())) {
			// Continue
			if (CodesConstant.PLNDACTN_010.equals(sdmRiskReasmntDto.getCdPlndActn())) {
				BookmarkDto bookmarkActionContinue = createBookmark(BookmarkConstants.IP_CHECK_ACTION_CONTINUE,
						ServiceConstants.CHECKED);
				bookmarkNonFormGrpList.add(bookmarkActionContinue);
			}

			// Close
			if (CodesConstant.PLNDACTN_020.equals(sdmRiskReasmntDto.getCdPlndActn())) {
				BookmarkDto bookmarkActionClose = createBookmark(BookmarkConstants.IP_CHECK_ACTION_CLOSECASE,
						ServiceConstants.CHECKED);
				bookmarkNonFormGrpList.add(bookmarkActionClose);
			}
		}

		// Reason Recommendation and Planned Action Do not Match
		if (!ObjectUtils.isEmpty(sdmRiskReasmntDto.getTxtRsnRecmndtnNotMatch())) {
			BookmarkDto bookmarktxtRecommmendation = createBookmark(BookmarkConstants.REASON_NO_MATCH,
					sdmRiskReasmntDto.getTxtRsnRecmndtnNotMatch());
			bookmarkNonFormGrpList.add(bookmarktxtRecommmendation);
		}

		// Title section
		BookmarkDto bookmarkNmCase = createBookmark(BookmarkConstants.TITLE_CASE_NAME, sdmRiskReasmntDto.getNmCase());
		bookmarkNonFormGrpList.add(bookmarkNmCase);
		BookmarkDto bookmarkIdCase = createBookmark(BookmarkConstants.TITLE_CASE_NBR, sdmRiskReasmntDto.getIdCase());
		bookmarkNonFormGrpList.add(bookmarkIdCase);

		// Household
		BookmarkDto bookmarkHousehold = createBookmark(BookmarkConstants.HOUSEHOLD_ASSESSED,
				personsMap.get(sdmRiskReasmntDto.getIdHshldAssessed()).getNmPersonFull());
		bookmarkNonFormGrpList.add(bookmarkHousehold);

		// Assessment Date
		BookmarkDto bookmarkDTAssess = createBookmark(BookmarkConstants.DATE_OF_ASSESSMENT,
				DateUtils.stringDt(sdmRiskReasmntDto.getDtAsmnt()));
		bookmarkNonFormGrpList.add(bookmarkDTAssess);

		// Primary caregiver or parent
		PersonDto primaryPerson = personsMap.get(sdmRiskReasmntDto.getIdPrmryPrsn());
		String nmPersonFullPrimary = !ObjectUtils.isEmpty(primaryPerson) ? primaryPerson.getNmPersonFull() : null;
		BookmarkDto bookmarkPrimaryCare = createBookmark(BookmarkConstants.TITLE_NAME_FULL, nmPersonFullPrimary);
		bookmarkNonFormGrpList.add(bookmarkPrimaryCare);

		// secondary caregiver or parent
		if (!ObjectUtils.isEmpty(sdmRiskReasmntDto.getIdScndryPrsn())) {

			FormDataGroupDto secCareGiverGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_SEC_CAREGIVER,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> secCareGiverList = new ArrayList<BookmarkDto>();

			PersonDto secondaryPerson = personsMap.get(sdmRiskReasmntDto.getIdScndryPrsn());
			String nmPersonFullSecondary = !ObjectUtils.isEmpty(secondaryPerson) ? secondaryPerson.getNmPersonFull()
					: null;
			BookmarkDto bookmarkSecondaryCare = createBookmark(BookmarkConstants.TITLE_SECN_NAME_FULL,
					nmPersonFullSecondary);
			secCareGiverList.add(bookmarkSecondaryCare);

			secCareGiverGroupDto.setBookmarkDtoList(secCareGiverList);
			formDataGroupList.add(secCareGiverGroupDto);

		}
		// Add non group bookmarks and form data groups to prefill data
		PreFillDataServiceDto preFillData = new PreFillDataServiceDto();
		preFillData.setFormDataGroupList(formDataGroupList);
		preFillData.setBookmarkDtoList(bookmarkNonFormGrpList);
		return preFillData;
	}
	
	
	//QCR 62702 - SDM Removal to show/hide SDM word in the Form title
		private boolean showSDM(SDMRiskReasmntDto sdmRiskReasmntDto){
			boolean showSDMInTitle=false;
			Date dtSDMRemoval=codesDao.getAppRelDate(ServiceConstants.CRELDATE_SDM_REMOVAL_2020);	
			
			if(!ObjectUtils.isEmpty(sdmRiskReasmntDto.getDtAsmnt())){ 
					if(sdmRiskReasmntDto.getDtAsmnt().compareTo(dtSDMRemoval)<0){
						showSDMInTitle=true;
					}
			}else{
					if(!ObjectUtils.isEmpty(sdmRiskReasmntDto.getDtCreatedOn()) &&
							sdmRiskReasmntDto.getDtCreatedOn().compareTo(dtSDMRemoval)<0 && 
							!ObjectUtils.isEmpty(sdmRiskReasmntDto.getDtStageClosure()) &&
							sdmRiskReasmntDto.getDtStageClosure().compareTo(dtSDMRemoval)<0){
								showSDMInTitle=true;
					}			
			}
			return showSDMInTitle;	
		}

}
