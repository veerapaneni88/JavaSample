package us.tx.state.dfps.service.forms.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.pcsphistoryform.dto.PcspHistoryDto;
import us.tx.state.dfps.service.casepackage.dto.PCSPAssessmentDto;
import us.tx.state.dfps.service.casepackage.dto.PCSPAssessmentQuestionDto;
import us.tx.state.dfps.service.casepackage.dto.PCSPAssessmentResponseDto;
import us.tx.state.dfps.service.casepackage.dto.PCSPAssessmentSectionDto;
import us.tx.state.dfps.service.casepackage.dto.PCSPPersonDto;
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
import us.tx.state.dfps.service.workload.dto.StageDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<Prefill
 * class for Tuxedo CSVC60S, getting data from pcspHistoryFormServiceImpl> Apr
 * 3, 2018- 10:30:08 AM © 2017 Texas Department of Family and Protective
 * Services
 */

@Component
public class PcspAssessmentFormPrefillData extends DocumentServiceUtil {

	/**
	 * Method description: pre-fill data fetched by PcspHistoryFormService for
	 * form pcspasmt and pcspandm.
	 */
	@Override
	public PreFillDataServiceDto returnPrefillData(Object parentDtoobj) {
		PcspHistoryDto pcspAssess = (PcspHistoryDto) parentDtoobj;
		List<FormDataGroupDto> formDataGroupList = new ArrayList<FormDataGroupDto>();
		List<BookmarkDto> bookmarkNonFrmGrpList = new ArrayList<BookmarkDto>();
		PreFillDataServiceDto preFillData = new PreFillDataServiceDto();

		if (!ObjectUtils.isEmpty(pcspAssess.getFirstSectionAssessmentDto())) {
			if (ServiceConstants.ORDER_ONE.equals(pcspAssess.getFirstSectionAssessmentDto().getIdPcspAsmntLookup())) {
				if ((ServiceConstants.PCSPANDM).equalsIgnoreCase(pcspAssess.getaFIStatementDto().getDocType())) {
					BookmarkDto http = createBookmark(BookmarkConstants.HTML_TITLE,
							ServiceConstants.UNDERSTANDING_TITLE);
					BookmarkDto title = createBookmark(BookmarkConstants.PRINT_TITLE,
							ServiceConstants.UNDERSTANDING_TITLE);
					bookmarkNonFrmGrpList.add(http);
					bookmarkNonFrmGrpList.add(title);
				} else if ((ServiceConstants.PCSPASMT).equalsIgnoreCase(pcspAssess.getaFIStatementDto().getDocType())) {
					BookmarkDto http = createBookmark(BookmarkConstants.HTML_TITLE, ServiceConstants.PCSP_TITLE);
					BookmarkDto title = createBookmark(BookmarkConstants.PRINT_TITLE, ServiceConstants.PCSP_TITLE);
					bookmarkNonFrmGrpList.add(http);
					bookmarkNonFrmGrpList.add(title);
				}
			}
			if (ServiceConstants.ORDER_TWO.equals(pcspAssess.getFirstSectionAssessmentDto().getIdPcspAsmntLookup())) {
				if ((ServiceConstants.PCSPANDM).equalsIgnoreCase(pcspAssess.getaFIStatementDto().getDocType())) {
					BookmarkDto http = createBookmark(BookmarkConstants.HTML_TITLE, ServiceConstants.UNDERSTANDING_TITLE);
					BookmarkDto title = createBookmark(BookmarkConstants.PRINT_TITLE, ServiceConstants.UNDERSTANDING_TITLE);
					bookmarkNonFrmGrpList.add(http);
					bookmarkNonFrmGrpList.add(title);
				} else if ((ServiceConstants.PCSPASMT).equalsIgnoreCase(pcspAssess.getaFIStatementDto().getDocType())) {
					BookmarkDto http = createBookmark(BookmarkConstants.HTML_TITLE,
							ServiceConstants.ADDENDUM_TITLE);
					BookmarkDto title = createBookmark(BookmarkConstants.PRINT_TITLE,
							ServiceConstants.ADDENDUM_TITLE);
					bookmarkNonFrmGrpList.add(http);
					bookmarkNonFrmGrpList.add(title);
				}
			}
		}

		// stage details
		if (!ObjectUtils.isEmpty(pcspAssess.getStageDto())) {
			for (StageDto dto : pcspAssess.getStageDto()) {
				List<BookmarkDto> parBook = new ArrayList<BookmarkDto>();
				FormDataGroupDto http = createFormDataGroup(FormGroupsConstants.TMPLAT_CASE,
						FormConstants.EMPTY_STRING);
				if ((ServiceConstants.PCSPASMT).equalsIgnoreCase(pcspAssess.getaFIStatementDto().getDocType())) {
					BookmarkDto child = createBookmark(BookmarkConstants.CASE_SENSITIVE,
							BookmarkConstants.CASE_SENSITIVETEXT);
					parBook.add(child);
					http.setBookmarkDtoList(parBook);
				}
				BookmarkDto book = createBookmark(BookmarkConstants.CASE_NAMEHEADER,
						BookmarkConstants.CASE_NAMEHEADERTEXT);
				BookmarkDto book1 = createBookmark(BookmarkConstants.CASE_NAME, dto.getNmCase());
				BookmarkDto book2 = createBookmark(BookmarkConstants.CASE_IDHEADER,
						BookmarkConstants.CASE_IDHEADERTEXT);
				BookmarkDto book3 = createBookmark(BookmarkConstants.CASE_ID, dto.getIdCase());
				BookmarkDto book4 = createBookmark(BookmarkConstants.STAGE_IDHEADER,
						BookmarkConstants.STAGE_IDHEADERTEXT);
				BookmarkDto book5 = createBookmark(BookmarkConstants.STAGE_ID, pcspAssess.getIdStage());

				if ((ServiceConstants.PCSPASMT).equalsIgnoreCase(pcspAssess.getaFIStatementDto().getDocType())) {
					BookmarkDto child2 = createBookmark(BookmarkConstants.FOOTER, BookmarkConstants.CASE_SENSITIVETEXT);
					bookmarkNonFrmGrpList.add(child2);
				}

				parBook.add(book);
				parBook.add(book1);
				parBook.add(book2);
				parBook.add(book3);
				parBook.add(book4);
				parBook.add(book5);
				http.setBookmarkDtoList(parBook);
				formDataGroupList.add(http);
			}
		}
		/*
		 * start: 2nd section populate principals sections. PRNs are arrayed in
		 * three groups: caregiver, child, other assessed data stored in
		 * firstSectionAssessmentDto in pcspAssess dto
		 */
		if (!ObjectUtils.isEmpty(pcspAssess.getFirstSectionAssessmentDto())) {

			// up to three banners for assessed PRNs
			String[] pcspPrnList = new String[] { BookmarkConstants.CAREGIVER_GROUP, BookmarkConstants.CHILD_GROUP,
					BookmarkConstants.OTHER_ASSESSED_GROUP };
			for (String pcspPrn : pcspPrnList) {
				List<PCSPPersonDto> prnList = new ArrayList<PCSPPersonDto>();
				if (BookmarkConstants.CAREGIVER_GROUP.equals(pcspPrn)) {
					if (!ObjectUtils.isEmpty(pcspAssess.getFirstSectionAssessmentDto().getPgAssessedList())) {

						// only one caregiver, then break;
						for (PCSPPersonDto personDto : pcspAssess.getFirstSectionAssessmentDto().getPgAssessedList()) {
							if (pcspAssess.getFirstSectionAssessmentDto().getIdPrsncrgvr()
									.equals(personDto.getIdPerson())) {
								prnList.add(personDto);
								break;
							}
						}
					}
				} else if (BookmarkConstants.CHILD_GROUP.equals(pcspPrn)) {
					if (!ObjectUtils.isEmpty(pcspAssess.getFirstSectionAssessmentDto().getChildrenAssessedList())) {
						for (PCSPPersonDto personDto : pcspAssess.getFirstSectionAssessmentDto()
								.getChildrenAssessedList()) {
							if (pcspAssess.getFirstSectionAssessmentDto().getSavedChildrenAssessed()
									.contains(Integer.valueOf(personDto.getIdPerson().intValue()))) {
								prnList.add(personDto);
							}
						}
					}
				} else if (BookmarkConstants.OTHER_ASSESSED_GROUP.equals(pcspPrn)) {
					if (!ObjectUtils.isEmpty(pcspAssess.getFirstSectionAssessmentDto().getOhmAssessedList())) {
						for (PCSPPersonDto personDto : pcspAssess.getFirstSectionAssessmentDto().getOhmAssessedList()) {
							if (pcspAssess.getFirstSectionAssessmentDto().getSavedOhmAssessed()
									.contains(Integer.valueOf((personDto.getIdPerson().intValue())))) {
								prnList.add(personDto);
							}
						}
					}
				}
				if (!ObjectUtils.isEmpty(prnList)) {
					FormDataGroupDto http = createFormDataGroup(FormGroupsConstants.TMPLAT_PRN,
							FormConstants.EMPTY_STRING);
					List<FormDataGroupDto> groupList = new ArrayList<FormDataGroupDto>();
					List<BookmarkDto> parBook = new ArrayList<BookmarkDto>();
					BookmarkDto http1 = createBookmark(BookmarkConstants.PRN_NAME, ServiceConstants.PRN_NAME_TEXT);
					BookmarkDto http2 = createBookmark(BookmarkConstants.PRN_AGE, ServiceConstants.PRN_AGE_TEXT);
					parBook.add(http1);
					parBook.add(http2);
					http.setBookmarkDtoList(parBook);

					FormDataGroupDto httpSub = createFormDataGroup(FormGroupsConstants.TMPLAT_PRN_GROUP,
							FormGroupsConstants.TMPLAT_PRN);
					List<BookmarkDto> subBook = new ArrayList<BookmarkDto>();
					BookmarkDto child2 = createBookmark(BookmarkConstants.PRN_GROUP, pcspPrn);
					subBook.add(child2);
					httpSub.setBookmarkDtoList(subBook);
					groupList.add(httpSub);
					http.setFormDataGroupList(groupList);

					if ((ServiceConstants.PCSPASMT).equalsIgnoreCase(pcspAssess.getaFIStatementDto().getDocType())) {
						BookmarkDto andm1 = createBookmark(BookmarkConstants.PRN_TYPE, ServiceConstants.PRN_TYPE_TEXT);
						BookmarkDto andm2 = createBookmark(BookmarkConstants.PRN_RELINT,
								ServiceConstants.PRN_RELINT_TEXT);
						parBook.add(andm1);
						parBook.add(andm2);
						http.setBookmarkDtoList(parBook);
					}
					// sub
					FormDataGroupDto prnContactGroup = null;// create
															// prnContactGroup
															// only if
															// CAREGIVER_GROUP
					List<BookmarkDto> subBook1 = new ArrayList<BookmarkDto>();
					if (BookmarkConstants.CAREGIVER_GROUP.equals(pcspPrn)) {
						prnContactGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_PRN_CONTACT,
								FormGroupsConstants.TMPLAT_PRN);
						BookmarkDto http11 = createBookmark(BookmarkConstants.PRN_CONTACT_PHONEHEADER,
								ServiceConstants.PRN_CONTACT_PHONEHEADER_TEXT);
						BookmarkDto http12 = createBookmark(BookmarkConstants.PRN_CONTACT_ADDRHEADER,
								ServiceConstants.PRN_CONTACT_ADDRHEADER_TEXT);
						subBook1.add(http11);
						subBook1.add(http12);
						prnContactGroup.setBookmarkDtoList(subBook1);

					}
					// sub
					if ((ServiceConstants.PCSPANDM).equalsIgnoreCase(pcspAssess.getaFIStatementDto().getDocType())
							&& BookmarkConstants.CHILD_GROUP.equals(pcspPrn)) {
						FormDataGroupDto prnPlcmtGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_PRN_PLCMT,
								FormGroupsConstants.TMPLAT_PRN);
						List<BookmarkDto> subBook2 = new ArrayList<BookmarkDto>();
						BookmarkDto http11 = createBookmark(BookmarkConstants.PRN_PLCMT_LABL,
								ServiceConstants.SCTN_QSTN_S7Q1_TEXT);
						BookmarkDto http12 = createBookmark(BookmarkConstants.PRN_PLCMT_DATE,
								DateUtils.stringDt((pcspAssess.getFirstSectionAssessmentDto().getDtPlcmnt())));
						subBook2.add(http11);
						subBook2.add(http12);
						prnPlcmtGroup.setBookmarkDtoList(subBook2);
						groupList.add(prnPlcmtGroup);
						http.setFormDataGroupList(groupList);
					}
					for (PCSPPersonDto person : prnList) {
						FormDataGroupDto prnPlcmtGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_PRN_DETAIL,
								FormGroupsConstants.TMPLAT_PRN);
						List<BookmarkDto> subBook2 = new ArrayList<BookmarkDto>();
						BookmarkDto http11 = createBookmark(BookmarkConstants.PRN_DETAIL_NAME,
								person.getNmPersonFull());
						BookmarkDto http12 = createBookmark(BookmarkConstants.PRN_DETAIL_AGE,
								getPrnAgeAtDtDecsn(person, pcspAssess.getFirstSectionAssessmentDto()));
						subBook2.add(http11);
						subBook2.add(http12);
						prnPlcmtGroup.setBookmarkDtoList(subBook2);

						if ((ServiceConstants.PCSPASMT)
								.equalsIgnoreCase(pcspAssess.getaFIStatementDto().getDocType())) {
							BookmarkDto http13 = createBookmarkWithCodesTable(BookmarkConstants.PRN_DETAIL_TYPE,
									person.getStgPrsnType(), CodesConstant.CPRSNTYP);
							BookmarkDto http14 = createBookmarkWithCodesTable(BookmarkConstants.PRN_DETAIL_RELINT,
									person.getCdStagePersRelInt(), CodesConstant.CRPTRINT);
							subBook2.add(http13);
							subBook2.add(http14);
							prnPlcmtGroup.setBookmarkDtoList(subBook2);
						}
						groupList.add(prnPlcmtGroup);
						http.setFormDataGroupList(groupList);
						if (BookmarkConstants.CAREGIVER_GROUP.equals(pcspPrn)) {
							BookmarkDto care2 = createBookmark(BookmarkConstants.PRN_CONTACT_PHONE,
									TypeConvUtil.formatPhone(person.getPhone()));
							BookmarkDto care3 = createBookmark(BookmarkConstants.PRN_CONTACT_ADDR, person.getAddress());
							subBook1.add(care2);
							subBook1.add(care3);
							prnContactGroup.setBookmarkDtoList(subBook1);
						}

						groupList.add(prnContactGroup);
						http.setFormDataGroupList(groupList);
					}
					formDataGroupList.add(http);
				}
			}
		}

		/**
		 * populate questions answers sections. The bean delivers the
		 * appropriate sections and questions for either assessment or addendum,
		 * but the numbering must be changed here for the sections/questions
		 * based on assessment lookup id.
		 */
		if (!ObjectUtils.isEmpty(pcspAssess.getFirstSectionAssessmentDto().getSections())) {
			int counter = 0;
			for (PCSPAssessmentSectionDto sctn : pcspAssess.getFirstSectionAssessmentDto().getSections()) {
				if ((ServiceConstants.PCSPASMT).equalsIgnoreCase(pcspAssess.getaFIStatementDto().getDocType())
						|| ((ServiceConstants.PCSPANDM).equalsIgnoreCase(pcspAssess.getaFIStatementDto().getDocType())
								&& sctn.getSctnName().equalsIgnoreCase(ServiceConstants.STRING_EIGHT))) {
					FormDataGroupDto sectionGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_SCTN,
							FormConstants.EMPTY_STRING);
					List<FormDataGroupDto> workerAddrGroupDtoList = new ArrayList<FormDataGroupDto>();
					List<BookmarkDto> sectionBook = new ArrayList<BookmarkDto>();
					BookmarkDto section1 = createBookmark(BookmarkConstants.SCTN_TEXT, sctn.getSctn());
					sectionBook.add(section1);
					sectionGroup.setBookmarkDtoList(sectionBook);
					if ((ServiceConstants.PCSPASMT).equalsIgnoreCase(pcspAssess.getaFIStatementDto().getDocType())) {
						if (counter++ == 0) {// only supply the PCSP Assessment
												// section header on the first
												// sctn
							// create sub group of TMPLAT_SCTN
							FormDataGroupDto asmtHeader = createFormDataGroup(
									FormGroupsConstants.TMPLAT_SCTN_ALLSECTIONLABEL, FormGroupsConstants.TMPLAT_SCTN);
							List<BookmarkDto> subsectionBook = new ArrayList<BookmarkDto>();
							BookmarkDto section2 = createBookmark(BookmarkConstants.SCTN_ALLSECTIONLABEL,
									BookmarkConstants.SCTN_ALLSECTIONLABEL_TEXT);
							subsectionBook.add(section2);
							asmtHeader.setBookmarkDtoList(subsectionBook);
							workerAddrGroupDtoList.add(asmtHeader);
							sectionGroup.setFormDataGroupList(workerAddrGroupDtoList);

						}
						BookmarkDto section3 = createBookmark(BookmarkConstants.SCTN_LABL,
								BookmarkConstants.SCTN_LABLTEXT);
						sectionBook.add(section3);
						sectionGroup.setBookmarkDtoList(sectionBook);

						if (ServiceConstants.ORDER_ONE
								.equals(pcspAssess.getFirstSectionAssessmentDto().getIdPcspAsmntLookup())) {
							BookmarkDto section4 = createBookmark(BookmarkConstants.SCTN_NAME, sctn.getSctnName());
							sectionBook.add(section4);
							sectionGroup.setBookmarkDtoList(sectionBook);
						} else { // if is ADDENDUM, the section names(numbers)
									// need to be fixed
							if (sctn.getSctnName().equalsIgnoreCase(ServiceConstants.STRING_NINE)) {
								BookmarkDto section5 = createBookmark(BookmarkConstants.SCTN_NAME,
										ServiceConstants.STRING_ONE);
								sectionBook.add(section5);
								sectionGroup.setBookmarkDtoList(sectionBook);
							}
							if (sctn.getSctnName().equalsIgnoreCase(ServiceConstants.STRING_THREE)) {
								BookmarkDto section5 = createBookmark(BookmarkConstants.SCTN_NAME,
										ServiceConstants.STRING_TWO);
								sectionBook.add(section5);
								sectionGroup.setBookmarkDtoList(sectionBook);
							}
							if (sctn.getSctnName().equalsIgnoreCase(ServiceConstants.STRING_SEVEN)) {
								BookmarkDto section5 = createBookmark(BookmarkConstants.SCTN_NAME,
										ServiceConstants.STRING_THREE);
								sectionBook.add(section5);
								sectionGroup.setBookmarkDtoList(sectionBook);
							}
							if (sctn.getSctnName().equalsIgnoreCase(ServiceConstants.STRING_EIGHT)) {
								BookmarkDto section5 = createBookmark(BookmarkConstants.SCTN_NAME,
										ServiceConstants.STRING_FOUR);
								sectionBook.add(section5);
								sectionGroup.setBookmarkDtoList(sectionBook);
							}
						}
					}
					if (sctn.getSctnName().equalsIgnoreCase(ServiceConstants.STRING_SEVEN)) {
						/*
						 * Section 7 needs to build its space with the following
						 * several fields before building its question/answer
						 */
						FormDataGroupDto sectionSevenGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_SCTN_7,
								FormGroupsConstants.TMPLAT_SCTN);
						List<BookmarkDto> sectionBook7 = new ArrayList<BookmarkDto>();
						BookmarkDto section7 = createBookmark(BookmarkConstants.SCTN_7_DCSNLABEL,
								BookmarkConstants.SCTN_7_DCSNTEXT);
						sectionBook7.add(section7);
						sectionSevenGroup.setBookmarkDtoList(sectionBook7);
						if (ServiceConstants.REJT
								.equalsIgnoreCase(pcspAssess.getFirstSectionAssessmentDto().getCdDecsn())) {
							BookmarkDto section701 = createBookmark(BookmarkConstants.SCTN_7_DCSN,
									ServiceConstants.REJT_TEXT);
							sectionBook7.add(section701);
							sectionSevenGroup.setBookmarkDtoList(sectionBook7);
						} else {
							BookmarkDto section701 = createBookmarkWithCodesTable(BookmarkConstants.SCTN_7_DCSN,
									pcspAssess.getFirstSectionAssessmentDto().getCdDecsn(), CodesConstant.CAPPDESG);
							sectionBook7.add(section701);
							sectionSevenGroup.setBookmarkDtoList(sectionBook7);
						}
						BookmarkDto section701 = createBookmark(BookmarkConstants.SCTN_7_DATELABEL,
								BookmarkConstants.SCTN_7_DATETEXT);
						BookmarkDto section702 = createBookmark(BookmarkConstants.SCTN_7_DATE,
								DateUtils.stringDt(pcspAssess.getFirstSectionAssessmentDto().getDtDecsn()));
						BookmarkDto section703 = createBookmark(BookmarkConstants.SCTN_7_SUPVLABEL,
								BookmarkConstants.SCTN_7_SUPVTEXT);
						BookmarkDto section704 = createBookmark(BookmarkConstants.SCTN_7_SUPV,
								pcspAssess.getFirstSectionAssessmentDto().getSupFullName());
						BookmarkDto section705 = createBookmark(BookmarkConstants.SCTN_7_PDIRLABEL,
								BookmarkConstants.SCTN_7_PDIRTEXT);
						BookmarkDto section706 = createBookmark(BookmarkConstants.SCTN_7_PDIR,
								pcspAssess.getFirstSectionAssessmentDto().getPdFullName());
						BookmarkDto section707 = createBookmark(BookmarkConstants.SCTN_7_PLCMTCONFLABEL,
								BookmarkConstants.SCTN_7_PLCMTCONF);
						sectionBook7.add(section701);
						sectionBook7.add(section702);
						sectionBook7.add(section703);
						sectionBook7.add(section704);
						sectionBook7.add(section705);
						sectionBook7.add(section706);
						sectionBook7.add(section707);
						sectionSevenGroup.setBookmarkDtoList(sectionBook7);
						workerAddrGroupDtoList.add(sectionSevenGroup);
						sectionGroup.setFormDataGroupList(workerAddrGroupDtoList);
					}
					if (!ObjectUtils.isEmpty(sctn.getQuestions())) {
						for (PCSPAssessmentQuestionDto question : sctn.getQuestions()) {

							String questionPart1 = ServiceConstants.EMPTY_STRING;
							String questionPart2 = ServiceConstants.EMPTY_STRING;

							// sub group of TMPLAT_SCTN
							FormDataGroupDto sectQuestionGroup = createFormDataGroup(
									FormGroupsConstants.TMPLAT_SCTN_QSTN, FormGroupsConstants.TMPLAT_SCTN);
							List<BookmarkDto> sectQuestionBookmarkList = new ArrayList<BookmarkDto>();
							List<FormDataGroupDto> sectQuestionGroupList = new ArrayList<FormDataGroupDto>();
							// drop the question numbering for Sections 5, 6, 7
							if (!sctn.getSctnName().equalsIgnoreCase(ServiceConstants.STRING_FIVE)
									&& !sctn.getSctnName().equalsIgnoreCase(ServiceConstants.STRING_SIX)
									&& !sctn.getSctnName().equalsIgnoreCase(ServiceConstants.STRING_SEVEN)) {
								BookmarkDto section7 = createBookmark(BookmarkConstants.SCTN_QSTN_NUMBER,
										question.getSctnQstnOrder() + ServiceConstants.PERIOD);
								sectQuestionBookmarkList.add(section7);
								sectQuestionGroup.setBookmarkDtoList(sectQuestionBookmarkList);

							}
							// sections 5, 6, 7 have radio choice questions -
							// answers if 0 < question.getResponses
							if (!ObjectUtils.isEmpty(question.getResponses())) {
								for (PCSPAssessmentResponseDto response : question.getResponses()) {
									// create question sub group of asmtHeader
									FormDataGroupDto checkBoxGroup = createFormDataGroup(
											FormGroupsConstants.TMPLAT_SCTN_QSTN_CBOX,
											FormGroupsConstants.TMPLAT_SCTN_QSTN);
									List<FormDataGroupDto> checkBoxGroupFormList = new ArrayList<FormDataGroupDto>();
									if (sctn.getSctnName().equalsIgnoreCase(ServiceConstants.STRING_SEVEN)) {
										if (question.getSctnQstnOrder().equals(ServiceConstants.ORDER_TWO)) {
											// except s7q2 uses QuestionText
											// instead of ResponseText
											// sub group of
											// TMPLAT_SCTN_QSTN_CBOX
											FormDataGroupDto checkboxValueGroup = createFormDataGroup(
													FormGroupsConstants.TMPLAT_SCTN_QSTN_CBOX_VAL,
													FormGroupsConstants.TMPLAT_SCTN_QSTN_CBOX);
											BookmarkDto section7 = createBookmark(BookmarkConstants.SCTN_QSTN_TEXT,
													question.getQstn());
											sectQuestionBookmarkList.add(section7);
											checkBoxGroupFormList.add(checkboxValueGroup);
											checkBoxGroup.setFormDataGroupList(checkBoxGroupFormList);
											sectQuestionGroupList.add(checkBoxGroup);
										}
										if (question.getSctnQstnOrder().equals(ServiceConstants.ORDER_ONE)) {
											// check the box
											// PlacemntDecisionCode =
											// PLCMTDSN_010 unless dropped in
											// processPcspAssmtDB()
											// sub group of
											// TMPLAT_SCTN_QSTN_CBOX
											FormDataGroupDto checkboxValueGroup = createFormDataGroup(
													FormGroupsConstants.TMPLAT_SCTN_QSTN_CBOX_VAL,
													FormGroupsConstants.TMPLAT_SCTN_QSTN_CBOX);
											checkBoxGroupFormList.add(checkboxValueGroup);
											checkBoxGroup.setFormDataGroupList(checkBoxGroupFormList);
											sectQuestionGroupList.add(checkBoxGroup);

											// use response.getRspns in this
											// section 7.1
											BookmarkDto section7 = createBookmark(BookmarkConstants.SCTN_QSTN_TEXT,
													response.getRspns());
											sectQuestionBookmarkList.add(section7);

											// sub group of TMPLAT_SCTN_QSTN
											FormDataGroupDto placementDateGroup = createFormDataGroup(
													FormGroupsConstants.TMPLAT_SCTN_QSTN_S7Q1,
													FormGroupsConstants.TMPLAT_SCTN_QSTN);
											List<BookmarkDto> sub2 = new ArrayList<BookmarkDto>();
											BookmarkDto section8 = createBookmark(
													BookmarkConstants.SCTN_QSTN_S7Q1_LABEL,
													BookmarkConstants.SCTN_QSTN_S7Q1_TEXT);
											BookmarkDto section9 = createBookmark(BookmarkConstants.SCTN_QSTN_S7Q1_DATE,
													DateUtils.stringDt(
															pcspAssess.getFirstSectionAssessmentDto().getDtPlcmnt()));
											sub2.add(section8);
											sub2.add(section9);
											placementDateGroup.setBookmarkDtoList(sub2);
											sectQuestionGroupList.add(placementDateGroup);
										}
										if (question.getSctnQstnOrder().equals(ServiceConstants.ORDER_THREE)) {
											// check the box
											// PlacemntDecisionCode =
											// PLCMTDSN_020 unless dropped in
											// processPcspAssmtDB()
											// sub group of
											// TMPLAT_SCTN_QSTN_CBOX
											FormDataGroupDto checkboxValueGroup = createFormDataGroup(
													FormGroupsConstants.TMPLAT_SCTN_QSTN_CBOX_VAL,
													FormGroupsConstants.TMPLAT_SCTN_QSTN_CBOX);
											checkBoxGroupFormList.add(checkboxValueGroup);
											checkBoxGroup.setFormDataGroupList(checkBoxGroupFormList);
											sectQuestionGroupList.add(checkBoxGroup);

											BookmarkDto section7 = createBookmark(BookmarkConstants.SCTN_QSTN_TEXT,
													question.getQstn());
											sectQuestionBookmarkList.add(section7);
										}

										sectQuestionGroup.setBookmarkDtoList(sectQuestionBookmarkList);
										sectQuestionGroup.setFormDataGroupList(sectQuestionGroupList);
									} else { // sections 5, 6
										BookmarkDto section7 = createBookmark(BookmarkConstants.SCTN_QSTN_TEXT,
												response.getRspns());
										sectQuestionBookmarkList.add(section7);
										sectQuestionGroup.setBookmarkDtoList(sectQuestionBookmarkList);
										if (response.getIdPcspRspnsLookup().equals(question.getCdResponse())) {
											FormDataGroupDto checkboxValueGroup = createFormDataGroup(
													FormGroupsConstants.TMPLAT_SCTN_QSTN_CBOX_VAL,
													FormGroupsConstants.TMPLAT_SCTN_QSTN_CBOX);
											checkBoxGroupFormList.add(checkboxValueGroup);
										}

										checkBoxGroup.setFormDataGroupList(checkBoxGroupFormList);
										sectQuestionGroupList.add(checkBoxGroup);
										sectQuestionGroup.setFormDataGroupList(sectQuestionGroupList);
									}
								}
							} else { // other sections have radio choice answers
										// or checkboxes - answers in
										// question.getAnswerCode
								FormDataGroupDto checkboxGroup = null;// s8 has
																		// checkboxes
								List<FormDataGroupDto> subCheckForm = new ArrayList<FormDataGroupDto>();
								if (sctn.getSctnName().equalsIgnoreCase(ServiceConstants.STRING_EIGHT)) {
									checkboxGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_SCTN_QSTN_CBOX,
											FormGroupsConstants.TMPLAT_SCTN_QSTN);
									sectQuestionGroupList.add(checkboxGroup);
									sectQuestionGroup.setFormDataGroupList(sectQuestionGroupList);
								}
								if (!ObjectUtils.isEmpty(question.getCdAnswr())) {
									if (sctn.getSctnName().equalsIgnoreCase(ServiceConstants.STRING_EIGHT)) {
										// sub group of TMPLAT_SCTN_QSTN_CBOX
										FormDataGroupDto subcheckboxGroup = createFormDataGroup(
												FormGroupsConstants.TMPLAT_SCTN_QSTN_CBOX_VAL,
												FormGroupsConstants.TMPLAT_SCTN_QSTN_CBOX);
										subCheckForm.add(subcheckboxGroup);
										checkboxGroup.setFormDataGroupList(subCheckForm);
									} else {// other sections get the radio
											// Yes/No/NA answers
										BookmarkDto section7 = createBookmarkWithCodesTable(
												BookmarkConstants.SCTN_QSTN_ANSW, question.getCdAnswr(),
												CodesConstant.CINVACAN);
										sectQuestionBookmarkList.add(section7);
									}
								} else {// fill the blank space in the template
									if (!sctn.getSctnName().equalsIgnoreCase(ServiceConstants.STRING_EIGHT)) {
										FormDataGroupDto answerSpace = createFormDataGroup(
												FormGroupsConstants.TMPLAT_SCTN_QSTN_SPACE,
												FormGroupsConstants.TMPLAT_SCTN_QSTN);
										sectQuestionGroupList.add(answerSpace);
										sectQuestionGroup.setFormDataGroupList(sectQuestionGroupList);
									}
								}

								// processQuestionText before filling the
								// SCTN_QSTN_TEXT bookmark
								String[] questionText = processQuestionText(question, sctn,
										pcspAssess.getFirstSectionAssessmentDto());
								for (int index = 0; index < questionText.length
										&& index < ServiceConstants.MAX_SPLIT_NUM.intValue(); index++) {
									if (0 == index)
										questionPart1 = questionText[index];
									if (1 == index)
										questionPart2 = questionText[index];
								}

								BookmarkDto questionPart111 = createBookmark(BookmarkConstants.SCTN_QSTN_TEXT,
										questionPart1);
								sectQuestionBookmarkList.add(questionPart111);
								sectQuestionGroup.setBookmarkDtoList(sectQuestionBookmarkList);

								// processQuestionText before filling the
								// SCTN_QSTN_TEXT bookmark
								if ((question.getSctnQstnOrder() < sctn.getQuestions().size())
										|| sctn.getSctn().equalsIgnoreCase(ServiceConstants.EIGHT)) {
									FormDataGroupDto lineGroup = createFormDataGroup(
											FormGroupsConstants.TMPLAT_SCTN_QSTN_LINE,
											FormGroupsConstants.TMPLAT_SCTN_QSTN);
									sectQuestionGroupList.add(lineGroup);
									sectQuestionGroup.setFormDataGroupList(sectQuestionGroupList);
								}
							}
							// questions with added comment, checkbox and
							// questionPart2 fields
							if (StringUtils.isNotBlank(questionPart2)) {
								FormDataGroupDto s1q4q5Group = createFormDataGroup(
										FormGroupsConstants.TMPLAT_SCTN_QSTN_S1Q4Q5,
										FormGroupsConstants.TMPLAT_SCTN_QSTN);
								List<FormDataGroupDto> s1q4q5other = new ArrayList<FormDataGroupDto>();
								List<BookmarkDto> s1q4q5book = new ArrayList<BookmarkDto>();
								BookmarkDto section7 = createBookmark(BookmarkConstants.SCTN_QSTN_S1Q4Q5_Q,
										questionPart2);
								BookmarkDto section8 = createBookmark(BookmarkConstants.SCTN_QSTN_S1Q4Q5_COMMENT,
										alterCssNewline(question.getOtherDscrptn()));
								s1q4q5book.add(section7);
								s1q4q5book.add(section8);
								s1q4q5Group.setBookmarkDtoList(s1q4q5book);

								FormDataGroupDto s1q4q5CheckboxGroup = createFormDataGroup(
										FormGroupsConstants.TMPLAT_SCTN_QSTN_S1Q4Q5_CBOX,
										FormGroupsConstants.TMPLAT_SCTN_QSTN_S1Q4Q5);
								List<FormDataGroupDto> s1q4q5other2 = new ArrayList<FormDataGroupDto>();
								if (ServiceConstants.Y.equals(question.getIndCrmnlAbuseHist())) {
									FormDataGroupDto s1q4q5CheckboxValueGroup = createFormDataGroup(
											FormGroupsConstants.TMPLAT_SCTN_QSTN_S1Q4Q5_CBOX_VAL,
											FormGroupsConstants.TMPLAT_SCTN_QSTN_S1Q4Q5_CBOX);
									s1q4q5other2.add(s1q4q5CheckboxValueGroup);
									s1q4q5CheckboxGroup.setFormDataGroupList(s1q4q5other2);
								}
								s1q4q5other.add(s1q4q5CheckboxGroup);
								s1q4q5Group.setFormDataGroupList(s1q4q5other);
								sectQuestionGroupList.add(s1q4q5Group);
								sectQuestionGroup.setFormDataGroupList(sectQuestionGroupList);
							}
							// questions with added comment
							if (sctn.getSctnName().equalsIgnoreCase(ServiceConstants.STRING_TWO)
									&& question.getSctnQstnOrder().equals(ServiceConstants.ORDER_THREE)
									&& ServiceConstants.NO.equals(question.getCdAnswr())) {
								FormDataGroupDto s2q3Group = createFormDataGroup(
										FormGroupsConstants.TMPLAT_SCTN_QSTN_S2Q3,
										FormGroupsConstants.TMPLAT_SCTN_QSTN);
								List<BookmarkDto> s2q3Book = new ArrayList<BookmarkDto>();
								BookmarkDto s2 = createBookmark(BookmarkConstants.SCTN_QSTN_S2Q3_TEXT,
										alterCssNewline(question.getOtherDscrptn()));
								s2q3Book.add(s2);
								s2q3Group.setBookmarkDtoList(s2q3Book);
								sectQuestionGroupList.add(s2q3Group);
								sectQuestionGroup.setFormDataGroupList(sectQuestionGroupList);
							}
							if (sctn.getSctnName().equalsIgnoreCase(ServiceConstants.STRING_FOUR)
									&& ServiceConstants.NO.equals(question.getCdAnswr())) {
								FormDataGroupDto s4q1Group = createFormDataGroup(
										FormGroupsConstants.TMPLAT_SCTN_QSTN_S4Q1,
										FormGroupsConstants.TMPLAT_SCTN_QSTN);
								List<BookmarkDto> s2q3Book = new ArrayList<BookmarkDto>();
								BookmarkDto s2 = createBookmark(BookmarkConstants.SCTN_QSTN_S4Q1_TEXT,
										alterCssNewline(question.getOtherDscrptn()));
								s2q3Book.add(s2);
								s4q1Group.setBookmarkDtoList(s2q3Book);
								sectQuestionGroupList.add(s4q1Group);
								sectQuestionGroup.setFormDataGroupList(sectQuestionGroupList);
							}

							// the comment field is populated in the final
							// section for Q5 in assessment, but Q3 in addendum
							if (sctn.getSctnName().equalsIgnoreCase(ServiceConstants.STRING_EIGHT) && (question
									.getSctnQstnOrder().equals(ServiceConstants.ORDER_FIVE)
									|| (ServiceConstants.ORDER_TWO
											.equals(pcspAssess.getFirstSectionAssessmentDto().getIdPcspAsmntLookup())
											&& question.getSctnQstnOrder().equals(ServiceConstants.ORDER_THREE)))) {
								FormDataGroupDto s8q5Group = createFormDataGroup(
										FormGroupsConstants.TMPLAT_SCTN_QSTN_S8Q5,
										FormGroupsConstants.TMPLAT_SCTN_QSTN);
								List<BookmarkDto> s2q3Book = new ArrayList<BookmarkDto>();
								BookmarkDto s2 = createBookmark(BookmarkConstants.SCTN_QSTN_S8Q5_TEXT,
										alterCssNewline(question.getOtherDscrptn()));
								s2q3Book.add(s2);
								s8q5Group.setBookmarkDtoList(s2q3Book);
								sectQuestionGroupList.add(s8q5Group);
								sectQuestionGroup.setFormDataGroupList(sectQuestionGroupList);
							}
							// the right justified checkbox is populated in the
							// final section for Q7 in assessment, but Q5 in
							// addendum
							if (sctn.getSctnName().equalsIgnoreCase(ServiceConstants.STRING_EIGHT) && (question
									.getSctnQstnOrder().equals(ServiceConstants.ORDER_SEVEN)
									|| (ServiceConstants.ORDER_TWO
											.equals(pcspAssess.getFirstSectionAssessmentDto().getIdPcspAsmntLookup())
											&& question.getSctnQstnOrder().equals(ServiceConstants.ORDER_FIVE)))) {
								FormDataGroupDto s8q7Group = createFormDataGroup(
										FormGroupsConstants.TMPLAT_SCTN_QSTN_S8Q7,
										FormGroupsConstants.TMPLAT_SCTN_QSTN);
								List<FormDataGroupDto> parS8q7GroupList = new ArrayList<FormDataGroupDto>();
								FormDataGroupDto s8q7checkboxGroup = createFormDataGroup(
										FormGroupsConstants.TMPLAT_SCTN_QSTN_S8Q7_CBOX,
										FormGroupsConstants.TMPLAT_SCTN_QSTN_S8Q7);
								List<FormDataGroupDto> s8q7GroupList = new ArrayList<FormDataGroupDto>();
								if (ServiceConstants.Y.equals(question.getIndCrmnlAbuseHist())) {
									FormDataGroupDto s1q4q5CheckboxValueGroup = createFormDataGroup(
											FormGroupsConstants.TMPLAT_SCTN_QSTN_S8Q7_CBOX_VAL,
											FormGroupsConstants.TMPLAT_SCTN_QSTN_S8Q7_CBOX);
									s8q7GroupList.add(s1q4q5CheckboxValueGroup);
									s8q7checkboxGroup.setFormDataGroupList(s8q7GroupList);
								}
								parS8q7GroupList.add(s8q7checkboxGroup);
								s8q7Group.setFormDataGroupList(parS8q7GroupList);
								sectQuestionGroupList.add(s8q7Group);
								sectQuestionGroup.setFormDataGroupList(sectQuestionGroupList);
							}
							if (sctn.getSctnName().equalsIgnoreCase(ServiceConstants.STRING_NINE)) {
								FormDataGroupDto s9q1Group = createFormDataGroup(
										FormGroupsConstants.TMPLAT_SCTN_QSTN_S9Q1,
										FormGroupsConstants.TMPLAT_SCTN_QSTN);
								List<BookmarkDto> s2q3Book = new ArrayList<BookmarkDto>();
								BookmarkDto s2 = createBookmark(BookmarkConstants.SCTN_QSTN_S9Q1_TEXT,
										alterCssNewline(question.getOtherDscrptn()));
								s2q3Book.add(s2);
								s9q1Group.setBookmarkDtoList(s2q3Book);
								sectQuestionGroupList.add(s9q1Group);
								sectQuestionGroup.setFormDataGroupList(sectQuestionGroupList);
							}
							workerAddrGroupDtoList.add(sectQuestionGroup);
						}
					}
					if (sctn.getSctnName().equalsIgnoreCase(ServiceConstants.STRING_EIGHT)) {
						FormDataGroupDto sectionEightGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_SCTN_8,
								FormGroupsConstants.TMPLAT_SCTN);
						List<BookmarkDto> s8q3Book = new ArrayList<BookmarkDto>();
						BookmarkDto s2 = createBookmark(BookmarkConstants.SCTN_8_LABEL, BookmarkConstants.SCTN_8_TEXT);
						BookmarkDto s3 = createBookmark(BookmarkConstants.SCTN_8_COMMENTS,
								alterCssNewline(pcspAssess.getFirstSectionAssessmentDto().getCmnts()));
						s8q3Book.add(s2);
						s8q3Book.add(s3);
						sectionEightGroup.setBookmarkDtoList(s8q3Book);
						workerAddrGroupDtoList.add(sectionEightGroup);
					}
					sectionGroup.setFormDataGroupList(workerAddrGroupDtoList);
					formDataGroupList.add(sectionGroup);
				}
			}
		}
		/**
		 * Populates the signatory section
		 */

		if ((ServiceConstants.PCSPANDM).equalsIgnoreCase(pcspAssess.getaFIStatementDto().getDocType())) {
			BookmarkDto s3 = createBookmark(BookmarkConstants.SIGNTITLE, BookmarkConstants.UNDERSTANDING_TITLE);
			bookmarkNonFrmGrpList.add(s3);
		} else if (!ObjectUtils.isEmpty(pcspAssess.getFirstSectionAssessmentDto()) && ServiceConstants.ORDER_ONE
				.equals(pcspAssess.getFirstSectionAssessmentDto().getIdPcspAsmntLookup())) {
			BookmarkDto s4 = createBookmark(BookmarkConstants.SIGNTITLE, BookmarkConstants.PCSP_TITLE);
			bookmarkNonFrmGrpList.add(s4);
		} else if (!ObjectUtils.isEmpty(pcspAssess.getFirstSectionAssessmentDto()) && ServiceConstants.ORDER_TWO
				.equals(pcspAssess.getFirstSectionAssessmentDto().getIdPcspAsmntLookup())) {
			BookmarkDto s5 = createBookmark(BookmarkConstants.SIGNTITLE, BookmarkConstants.ADDENDUM_TITLE);
			bookmarkNonFrmGrpList.add(s5);
		}
		BookmarkDto s1 = createBookmark(BookmarkConstants.SIGNBEFORE, BookmarkConstants.SIGNBEFORETEXT);
		BookmarkDto s2 = createBookmark(BookmarkConstants.SIGNNOTICE, BookmarkConstants.SIGNNOTICETEXT);
		BookmarkDto s3 = createBookmark(BookmarkConstants.SIGNNOTE, BookmarkConstants.SIGNNOTETEXT);
		BookmarkDto s4 = createBookmark(BookmarkConstants.SIGNAGREE, BookmarkConstants.SIGNAGREETEXT);
		bookmarkNonFrmGrpList.add(s1);
		bookmarkNonFrmGrpList.add(s2);
		bookmarkNonFrmGrpList.add(s3);
		bookmarkNonFrmGrpList.add(s4);

		// up to five signatories for agreement
		String[] signatoryList = new String[] { BookmarkConstants.SIGN_PARENT1, BookmarkConstants.SIGN_PARENT2,
				BookmarkConstants.SIGN_PRIMARYCG, BookmarkConstants.SIGN_SECONDARYCG,
				BookmarkConstants.SIGN_CASEWORKER };
		for (String signatory : signatoryList) {
			FormDataGroupDto signGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_SIGN,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> signBook = new ArrayList<BookmarkDto>();
			if (BookmarkConstants.SIGN_PARENT1.equals(signatory)) {
				BookmarkDto b1 = createBookmark(BookmarkConstants.SIGN_NAME, BookmarkConstants.SIGN_PARENT1);
				BookmarkDto b2 = createBookmark(BookmarkConstants.SIGN_PHONE, BookmarkConstants.SIGN_PHONEBEST);
				signBook.add(b1);
				signBook.add(b2);
				signGroup.setBookmarkDtoList(signBook);
			}
			if (BookmarkConstants.SIGN_PARENT2.equals(signatory)) {
				BookmarkDto b1 = createBookmark(BookmarkConstants.SIGN_NAME, BookmarkConstants.SIGN_PARENT2);
				BookmarkDto b2 = createBookmark(BookmarkConstants.SIGN_PHONE, BookmarkConstants.SIGN_PHONEBEST);
				signBook.add(b1);
				signBook.add(b2);
				signGroup.setBookmarkDtoList(signBook);
			}
			if (BookmarkConstants.SIGN_PRIMARYCG.equals(signatory)) {
				BookmarkDto b1 = createBookmark(BookmarkConstants.SIGN_NAME, BookmarkConstants.SIGN_PRIMARYCG);
				BookmarkDto b2 = createBookmark(BookmarkConstants.SIGN_PHONE, BookmarkConstants.SIGN_PHONEBEST);
				signBook.add(b1);
				signBook.add(b2);
				signGroup.setBookmarkDtoList(signBook);
			}
			if (BookmarkConstants.SIGN_SECONDARYCG.equals(signatory)) {
				BookmarkDto b1 = createBookmark(BookmarkConstants.SIGN_NAME, BookmarkConstants.SIGN_SECONDARYCG);
				BookmarkDto b2 = createBookmark(BookmarkConstants.SIGN_PHONE, BookmarkConstants.SIGN_PHONEBEST);
				signBook.add(b1);
				signBook.add(b2);
				signGroup.setBookmarkDtoList(signBook);
			}
			if (BookmarkConstants.SIGN_CASEWORKER.equals(signatory)) {
				BookmarkDto b1 = createBookmark(BookmarkConstants.SIGN_NAME, BookmarkConstants.SIGN_CASEWORKER);
				BookmarkDto b2 = createBookmark(BookmarkConstants.SIGN_PHONE, BookmarkConstants.SIGN_PHONEOFFICE);
				signBook.add(b1);
				signBook.add(b2);
				signGroup.setBookmarkDtoList(signBook);
			}
			BookmarkDto date = createBookmark(BookmarkConstants.SIGN_DATE, BookmarkConstants.SIGN_DATETEXT);
			signBook.add(date);
			signGroup.setBookmarkDtoList(signBook);
			formDataGroupList.add(signGroup);
		}

		preFillData.setFormDataGroupList(formDataGroupList);
		preFillData.setBookmarkDtoList(bookmarkNonFrmGrpList);
		return preFillData;

	}

	/**
	 * This method returns a string for the PRN_DETAIL_AGE bookmark
	 * 
	 */
	private String getPrnAgeAtDtDecsn(PCSPPersonDto person, PCSPAssessmentDto pcspAssessmentDB) {
		StringBuilder prnAge = new StringBuilder();
		Date compareDate = new Date();
		if (!ObjectUtils.isEmpty(pcspAssessmentDB.getDtDecsn())) {
			compareDate = pcspAssessmentDB.getDtDecsn();
		}
		// If the person has no date of birth, then display blank
		if (!ObjectUtils.isEmpty(person.getDtPersonBirth())) {
			Long nbrAgeMonths = (long) DateUtils.getAgeInMonths(person.getDtPersonBirth(), compareDate);
			//14000, Instead of years to compare, using months to eliminate false negatives.
			if (nbrAgeMonths>= 12) {
				Long nbrAgeYears = nbrAgeMonths / ServiceConstants.ORDER_TWELVE;
				prnAge.append(nbrAgeYears);
			} else {// If the person is < 1 year old, then display their age in
					// months.
				if (1 >= nbrAgeMonths % ServiceConstants.ORDER_TWELVE) {
					prnAge.append(ServiceConstants.ONE_MONTH);
				} else {
					prnAge.append(nbrAgeMonths % ServiceConstants.ORDER_TWELVE);
					prnAge.append(ServiceConstants.MONTHS);
				}
			}
		}
		return prnAge.toString();
	}

	/**
	 * This method adjusts css of comment strings that require BRs where they
	 * have newlines which don't render in cfp.
	 * 
	 */
	private String alterCssNewline(String questionText) {
		String returnString = ServiceConstants.EMPTY_STRING;
		if (StringUtils.isNotBlank(questionText)) {
			returnString = questionText.replaceAll("(\r\n|\n\r|\r|\n)", "<br />");
		}
		return returnString;
	}

	/**
	 * This method adjusts css of question strings that require two BRs where
	 * they have one and italics after the BRs.
	 * 
	 */
	private String alterCssBr(String questionText) {
		String returnString = null;
		returnString = questionText.replaceAll("<BR><BR>", ""); // get rid of
																// this first
		returnString = returnString.replaceAll("<BR>", "<br><br><em>");
		StringBuffer sb = new StringBuffer();
		sb.append(returnString);
		sb.append("</em>");
		return sb.toString();
	}

	/**
	 * This method adjusts css of question strings like those in section 8 that
	 * have html list feature that would otherwise cause expansive space waste
	 * on the form
	 * 
	 */
	private String alterCssLi(String questionText) {
		String returnString = null;
		returnString = questionText.replaceAll("<UL", "<UL style=\"font-size: 0;padding: 0;margin: 0;\"");
		returnString = returnString.replaceAll("<BR>", "");
		returnString = returnString.replaceAll("<LI",
				"<LI style=\"padding: 0;margin: 0;height: 10px;list-style-position:inside;font-size: 9pt;text-align: left;font-family: Verdana;\"");
		return returnString;
	}

	/**
	 * This method adjusts css of question string that requires two BRs instead
	 * of one and font size change.
	 * 
	 */
	private String alterCssFont(String questionText) {
		String returnString = null;
		returnString = questionText.replaceAll("<I>", "<em><font size=" + '"' + "1" + '"' + ">");
		returnString = returnString.replaceAll("</I>", "</em></font>");
		return returnString;
	}

	/**
	 * Populate the question text bookmark. Some questions require special
	 * processing. for questions like 4 & 5 in section 1 with compound question
	 * texts separated by regex parameter for String.split() method.
	 */
	private String[] processQuestionText(PCSPAssessmentQuestionDto question, PCSPAssessmentSectionDto sctn,
			PCSPAssessmentDto pcspAssessmentDto) {
		String[] questionText = question.getQstn().split("regex");
		if (questionText[0].equals(question.getQstn())) {
			if (sctn.getSctnName().equalsIgnoreCase(ServiceConstants.STRING_EIGHT)
					&& (question.getSctnQstnOrder().equals(ServiceConstants.ORDER_ONE)
							|| question.getSctnQstnOrder().equals(ServiceConstants.ORDER_FOUR)
							|| question.getSctnQstnOrder().equals(ServiceConstants.ORDER_EIGHT))
					&& (ServiceConstants.ORDER_ONE.equals(pcspAssessmentDto.getIdPcspAsmntLookup()))) {
				questionText[0] = alterCssLi(question.getQstn());
			}
			if (sctn.getSctnName().equalsIgnoreCase(ServiceConstants.STRING_EIGHT)
					&& question.getSctnQstnOrder().equals(ServiceConstants.ORDER_SEVEN)) {
				questionText[0] = alterCssFont(question.getQstn());
			}
			if (sctn.getSctnName().equalsIgnoreCase(ServiceConstants.STRING_EIGHT)
					&& question.getSctnQstnOrder().equals(ServiceConstants.ORDER_FIVE)
					&& ServiceConstants.ORDER_TWO.equals(pcspAssessmentDto.getIdPcspAsmntLookup())) {
				questionText[0] = alterCssFont(question.getQstn());
			}
			if (sctn.getSctnName().equalsIgnoreCase(ServiceConstants.STRING_TWO)
					&& question.getSctnQstnOrder().equals(ServiceConstants.ORDER_THREE)) {
				questionText[0] = alterCssBr(question.getQstn());
			}
			if (sctn.getSctnName().equalsIgnoreCase(ServiceConstants.STRING_FOUR)
					&& question.getSctnQstnOrder().equals(ServiceConstants.ORDER_ONE)) {
				questionText[0] = alterCssBr(question.getQstn());
			}
			if (sctn.getSctnName().equalsIgnoreCase(ServiceConstants.STRING_NINE)) {
				questionText[0] = alterCssBr(question.getQstn());
			}
		}
		return questionText;
	}

}
