package us.tx.state.dfps.service.forms.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;


import us.tx.state.dfps.common.dto.GenericCaseInfoDto;
import us.tx.state.dfps.service.apsrora.dto.APSRoraAnswerDto;
import us.tx.state.dfps.service.apsrora.dto.APSRoraFollowupAnswerDto;
import us.tx.state.dfps.service.apsrora.dto.ApsRoraDto;
import us.tx.state.dfps.service.apsrora.dto.ApsRoraResponseDto;
import us.tx.state.dfps.service.apsrora.dto.ApsRoraServiceDto;
import us.tx.state.dfps.service.common.BookmarkConstants;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.FormGroupsConstants;
import us.tx.state.dfps.service.forms.dto.BookmarkDto;
import us.tx.state.dfps.service.forms.dto.FormDataGroupDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.lookup.service.LookupService;

/**
 * Prefill Data for- IMPACT PHASE 2 MODERNIZATION Class Description:
 * Aps Risk of recidivism Assessment -- apsrora.
 * Dec 29, 2021- 1:52:46 PM © 2021 Texas Department of Family and
 * Protective Services
 */

@Repository
public class ApsRoraReportPrefillData extends DocumentServiceUtil {

    // Questions that are not required when answering 'None' on question 1
    static final List<String> RORA_CONDITIONAL_QUESTIONS = Arrays.asList("R2", "R3", "R4", "R5", "R6", "R7", "R8", "R9", "R10");
    static final List<String> RORA_MULTI_ANSWER_QUESTIONS = Arrays.asList("R12", "R19");
    static final List<String> RORA_RECALIBRATION = Arrays.asList("R13", "R20", "R21", "R16", "R17", "R18");

    static final String OVERRIDE_INCREASE = "I";
    static final String OVERRIDE_DECREASE = "D";
    static final String OVERRIDE_NOCHANGE = "N";
    static final int MSG_APS_RORA_MAND_OVERRIDE_HIGH = 56503;
    static final int MSG_APS_RORA_MAND_OVERRIDE_NA = 56504;

    @Autowired
    LookupService lookupService;

    @Override
    public PreFillDataServiceDto returnPrefillData(Object parentDtoobj) {
        ApsRoraServiceDto apsRoraServiceDto = (ApsRoraServiceDto) parentDtoobj;

        // Initializing null DTOs
        if (ObjectUtils.isEmpty(apsRoraServiceDto.getGenericCaseInfoDto())) {
            apsRoraServiceDto.setGenericCaseInfoDto(new GenericCaseInfoDto());
        }
        if (ObjectUtils.isEmpty(apsRoraServiceDto.getApsRoraDto())) {
            apsRoraServiceDto.setApsRoraDto(new ApsRoraDto());
        }

        List<FormDataGroupDto> formDataGroupList = new ArrayList<>();
        List<BookmarkDto> bookmarkNonGroupList = new ArrayList<>();
        PreFillDataServiceDto preFillData = new PreFillDataServiceDto();

        // CSEC02D
        BookmarkDto bookmarkTitleCaseName = createBookmark(BookmarkConstants.NM_CASE, apsRoraServiceDto.getGenericCaseInfoDto().getNmCase());
        bookmarkNonGroupList.add(bookmarkTitleCaseName);
        BookmarkDto bookmarkTitleCaseNumber = createBookmark(BookmarkConstants.ID_CASE, apsRoraServiceDto.getGenericCaseInfoDto().getIdCase());
        bookmarkNonGroupList.add(bookmarkTitleCaseNumber);

        preFillApsRoraData(apsRoraServiceDto.getApsRoraDto(),formDataGroupList);

        preFillData.setBookmarkDtoList(bookmarkNonGroupList);
        preFillData.setFormDataGroupList(formDataGroupList);

        return preFillData;

    }

    private String formatkey(String input) {
        return input.replace("(", "").replace(")", "").replace("\\(", "").replace("\\)", "").replace(' ', '_').toUpperCase();
    }

    public void preFillApsRoraData(ApsRoraDto apsRoraDto,List<FormDataGroupDto> formDataGroupList) {

        boolean boolDsplyCndQstns = false;
        boolean isgroupAlreadyCreated = false;
        FormDataGroupDto group;
        List<FormDataGroupDto> groupFormDataGroupList;
        List<BookmarkDto> groupBookmarkGroupList;

        // Go through the responses to generate the bookmarks and bookmark values
        List<ApsRoraResponseDto> responses = apsRoraDto.getResponses();
        for (ApsRoraResponseDto response: responses) {
            String responseCode = response.getResponseCode();
            groupFormDataGroupList = new ArrayList<>();
            groupBookmarkGroupList = new ArrayList<>();
            if (!RORA_CONDITIONAL_QUESTIONS.contains(response.getQuestionCode()) || boolDsplyCndQstns) {
                // Each question is surrounded by a repeating group so we can control if it will be displayed.
                //SR 42553 - Rora recalibration
                if (apsRoraDto.getVersionNumber() == FormGroupsConstants.VERSION_ONE && (RORA_RECALIBRATION.contains(response.getQuestionCode()))) {
                    group = createFormDataGroup(FormGroupsConstants.TMPLAT_ + response.getQuestionCode() + "_1", BookmarkConstants.EMPTY_STRING);
                    BookmarkDto bookmarkDto = createBookmark(response.getQuestionCode() + FormGroupsConstants._TEXT, response.getQuestionText());
                    groupBookmarkGroupList.add(bookmarkDto);
                }
                //end of SR 42553 - Rora recalibration
                else if (RORA_MULTI_ANSWER_QUESTIONS.contains(response.getQuestionCode()) && isgroupAlreadyCreated) {
                    //Group is already created for this question , need to append the answers to this group. edge case.(multi select answers)
                    Optional<FormDataGroupDto> groupObj = formDataGroupList.stream().
                            filter(grp -> grp.getFormDataGroupBookmark().equalsIgnoreCase(FormGroupsConstants.TMPLAT_ + response.getQuestionCode())).
                            findFirst();
                    if (groupObj.isPresent()) {
                        group = groupObj.get();
                        groupBookmarkGroupList.addAll(group.getBookmarkDtoList());
                        formDataGroupList.remove(group);
                    } else {
                        group = createFormDataGroup(FormGroupsConstants.TMPLAT_ + response.getQuestionCode(), BookmarkConstants.EMPTY_STRING);
                        BookmarkDto bookmarkDto = createBookmark(response.getQuestionCode() + FormGroupsConstants._TEXT, response.getQuestionText());
                        groupBookmarkGroupList.add(bookmarkDto);
                    }
                } else {
                    group = createFormDataGroup(FormGroupsConstants.TMPLAT_ + response.getQuestionCode(), BookmarkConstants.EMPTY_STRING);
                    BookmarkDto bookmarkDto = createBookmark(response.getQuestionCode() + FormGroupsConstants._TEXT, response.getQuestionText());
                    groupBookmarkGroupList.add(bookmarkDto);
                }
                // Go through each of the answers
                List<APSRoraAnswerDto> answers = response.getAnswers();
                for (APSRoraAnswerDto answer:answers) {
                    boolean checked = false;
                    int responseCodeInt =   responseCode != null && !responseCode.equals(" ") ?  Integer.parseInt(responseCode) : -1;
                    if (RORA_MULTI_ANSWER_QUESTIONS.contains(response.getQuestionCode())) {
                        isgroupAlreadyCreated = true;
                        checked = answer.getRoraAnswerId() ==  response.getAnswerLookupId();
                    } else {
                        if (answer.getRoraAnswerId() ==  responseCodeInt) {
                            checked = true;
                            // If the answer to Question 1 is not 'None' then set the flag to show conditional questions
                            if (FormGroupsConstants.R1.equals(response.getQuestionCode()) && !FormGroupsConstants.R1A.equals(answer.getRoraAnswerCode())) {
                                boolDsplyCndQstns = true;
                            }
                        }
                    }
                    if (checked) {
                        BookmarkDto bookmarkDto = createBookmark(answer.getRoraAnswerCode() + FormGroupsConstants._BOX, BookmarkConstants.X);
                        groupBookmarkGroupList.add(bookmarkDto);
                    } else {
                        groupFormDataGroupList.add(createFormDataGroup(FormGroupsConstants.TMPLAT_ + answer.getRoraAnswerCode() + FormGroupsConstants._SPACE, BookmarkConstants.EMPTY_STRING));
                    }
                    BookmarkDto bookmarkDto = createBookmark(answer.getRoraAnswerCode() + FormGroupsConstants._TEXT, answer.getAnswerText());
                    groupBookmarkGroupList.add(bookmarkDto);

                    // If there are follow-up questions based on an answer then show them.
                    List<APSRoraFollowupAnswerDto> followUp = answer.getFollowupAnswers();
                    for (APSRoraFollowupAnswerDto followupAnswer:followUp) {
                        boolean fChecked = followupAnswer.getIndApsRoraFollowup() != null && BookmarkConstants.Y.equals(followupAnswer.getIndApsRoraFollowup());
                        if (fChecked) {
                            groupBookmarkGroupList.add(createBookmark(answer.getRoraAnswerCode() + "_" + formatkey(followupAnswer.getAnswerText()) + FormGroupsConstants._BOX, BookmarkConstants.X));
                        } else {
                            groupFormDataGroupList.add(createFormDataGroup(FormGroupsConstants.TMPLAT_ + answer.getRoraAnswerCode() + "_" + formatkey(followupAnswer.getAnswerText()) + FormGroupsConstants._SPACE, BookmarkConstants.EMPTY_STRING));
                        }
                        groupBookmarkGroupList.add(createBookmark(answer.getRoraAnswerCode() + "_" + formatkey(followupAnswer.getAnswerText()), followupAnswer.getAnswerText()));
                    }
                }
                group.setFormDataGroupList(groupFormDataGroupList);
                group.setBookmarkDtoList(groupBookmarkGroupList);
                formDataGroupList.add(group);
            }
        }

        // If all required questions have been answered then show the Risk Score/Level Section
        if (apsRoraDto.getInCompleteList().isEmpty()) {
            List<BookmarkDto> bookmarkGroupIncompleteList = new ArrayList<>();
            FormDataGroupDto scoreLevelSection = createFormDataGroup(FormGroupsConstants.TMPLAT_RISK_SCORE_LEVEL, BookmarkConstants.EMPTY_STRING);
            bookmarkGroupIncompleteList.add(createBookmarkWithCodesTable(BookmarkConstants.SCORED_RISK_LEVEL, apsRoraDto.getScoredRiskLevelCode(), CodesConstant.CANRSKVL));
            bookmarkGroupIncompleteList.add(createBookmarkWithCodesTable(BookmarkConstants.RORAFORM_SCORED_RISK_LEVEL, apsRoraDto.getScoredRiskLevelCode(), CodesConstant.CANRSKVL));
            bookmarkGroupIncompleteList.add(createBookmarkWithCodesTable(BookmarkConstants.SN_RISK_LEVEL, apsRoraDto.getSelfNeglectRiskLevelCode(), CodesConstant.CANRSKVL));
            bookmarkGroupIncompleteList.add(createBookmark(BookmarkConstants.SN_RISK_SCORE, Integer.toString(apsRoraDto.getSelfNeglectScore())));
            bookmarkGroupIncompleteList.add(createBookmarkWithCodesTable(BookmarkConstants.ANE_RISK_LEVEL, apsRoraDto.getAneRiskLevelCode(), CodesConstant.CANRSKVL));
            bookmarkGroupIncompleteList.add(createBookmark(BookmarkConstants.ANE_RISK_SCORE, Integer.toString(apsRoraDto.getAneScore())));

            if (apsRoraDto.isMandatoryOverrideInd()) {
                bookmarkGroupIncompleteList.add(createBookmark(BookmarkConstants.MANDATORY_OVERRIDE,  lookupService.getMessages().get(MSG_APS_RORA_MAND_OVERRIDE_HIGH).getTxtMessage()));
            } else {
                bookmarkGroupIncompleteList.add(createBookmark(BookmarkConstants.MANDATORY_OVERRIDE, lookupService.getMessages().get(MSG_APS_RORA_MAND_OVERRIDE_NA).getTxtMessage()));
            }

            if (OVERRIDE_INCREASE.equals(apsRoraDto.getDiscretionaryOverrideCode())) {
                bookmarkGroupIncompleteList.add(createBookmark(BookmarkConstants.INCREASE_RISK_BOX, BookmarkConstants.X));
                bookmarkGroupIncompleteList.add(createBookmark(FormGroupsConstants.TMPLAT_DECREASE_RISK_SPACE, BookmarkConstants.EMPTY_STRING));
                bookmarkGroupIncompleteList.add(createBookmark(FormGroupsConstants.TMPLAT_NO_CHANGE_RISK_SPACE, BookmarkConstants.EMPTY_STRING));
            } else if (OVERRIDE_DECREASE.equals(apsRoraDto.getDiscretionaryOverrideCode())) {
                bookmarkGroupIncompleteList.add(createBookmark(BookmarkConstants.DECREASE_RISK_BOX, BookmarkConstants.X));
                bookmarkGroupIncompleteList.add(createBookmark(FormGroupsConstants.TMPLAT_INCREASE_RISK_SPACE, BookmarkConstants.EMPTY_STRING));
                bookmarkGroupIncompleteList.add(createBookmark(FormGroupsConstants.TMPLAT_NO_CHANGE_RISK_SPACE, BookmarkConstants.EMPTY_STRING));
            } else if (OVERRIDE_NOCHANGE.equals(apsRoraDto.getDiscretionaryOverrideCode())) {
                bookmarkGroupIncompleteList.add(createBookmark(BookmarkConstants.NO_CHANGE_RISK_BOX, BookmarkConstants.X));
                bookmarkGroupIncompleteList.add(createBookmark(FormGroupsConstants.TMPLAT_INCREASE_RISK_SPACE, BookmarkConstants.EMPTY_STRING));
                bookmarkGroupIncompleteList.add(createBookmark(FormGroupsConstants.TMPLAT_DECREASE_RISK_SPACE, BookmarkConstants.EMPTY_STRING));
            }

            bookmarkGroupIncompleteList.add(createBookmarkWithCodesTable(BookmarkConstants.FINAL_RISK_LEVEL, apsRoraDto.getFinalRiskLevelCode(), CodesConstant.CANRSKVL));
            bookmarkGroupIncompleteList.add(createBookmarkWithCodesTable(BookmarkConstants.RORAFORM_FINAL_RISK_LEVEL, apsRoraDto.getFinalRiskLevelCode(), CodesConstant.CANRSKVL));
            bookmarkGroupIncompleteList.add(createBookmark(BookmarkConstants.REASON_FOR_OVERRIDE, apsRoraDto.getReasonForOverrideText()));
            bookmarkGroupIncompleteList.add(createBookmark(BookmarkConstants.RSN_OVERRIDE, apsRoraDto.getReasonForOverrideText()));
            bookmarkGroupIncompleteList.add(createBookmark(BookmarkConstants.COMMENTS, apsRoraDto.getCommentText()));
            scoreLevelSection.setBookmarkDtoList(bookmarkGroupIncompleteList);
            formDataGroupList.add(scoreLevelSection);
        }

        // Set appropriate page breaks
        if (boolDsplyCndQstns) {
            formDataGroupList.add(createFormDataGroup(FormGroupsConstants.TMPLAT_ALL_QUESTIONS_PB_1, BookmarkConstants.EMPTY_STRING));
            formDataGroupList.add(createFormDataGroup(FormGroupsConstants.TMPLAT_ALL_QUESTIONS_PB_2, BookmarkConstants.EMPTY_STRING));
        } else {
            formDataGroupList.add(createFormDataGroup(FormGroupsConstants.TMPLAT_FEWER_QUESTIONS_PB_1, BookmarkConstants.EMPTY_STRING));
            formDataGroupList.add(createFormDataGroup(FormGroupsConstants.TMPLAT_BREAK, BookmarkConstants.EMPTY_STRING));
            formDataGroupList.add(createFormDataGroup(FormGroupsConstants.TMPLAT_FEWER_QUESTIONS_PB_2, BookmarkConstants.EMPTY_STRING));
        }
    }



}
