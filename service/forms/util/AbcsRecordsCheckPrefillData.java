package us.tx.state.dfps.service.forms.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.service.common.BookmarkConstants;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.FormGroupsConstants;
import us.tx.state.dfps.service.common.request.AbcsRecordCheckReq;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.forms.dto.BookmarkDto;
import us.tx.state.dfps.service.forms.dto.FormDataGroupDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.forms.dto.RecordsCheckNotifDto;
import us.tx.state.dfps.service.lookup.dao.LookupDao;
import us.tx.state.dfps.service.person.dao.RecordsCheckDao;
import us.tx.state.dfps.service.person.service.AbcsRecordsCheckService;
import us.tx.state.dfps.service.person.service.RecordsCheckService;
import us.tx.state.dfps.service.recordscheck.dto.AbcsDto;
import us.tx.state.dfps.service.recordscheck.dto.AbcsRecordsCheckDto;
import us.tx.state.dfps.service.recordscheck.dto.AddressPersonLinkDto;
import us.tx.state.dfps.service.recordscheck.dto.EmployeePersonDto;
import us.tx.state.dfps.service.recordscheck.dto.PopulateNotificationDto;
import us.tx.state.dfps.service.recordscheck.dto.ResourceContractInfoDto;
import us.tx.state.dfps.service.workload.dto.PersonDto;

import javax.mail.MessagingException;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:*
 * AbcsRecordsCheckPrefillData will implemented returnPrefillData operation
 * defined in DocumentServiceUtil Interface to populate the prefill data for
 * form Abcs Record Check Detail screen. Feb 9, 2018- 2:19:28 PM © 2017 Texas
 * Department of Family and Protective Services
 * ********Change History**********
 * 04/22/2022 thompswa artf205247 display TESTENVMESSAGE as whitespace for prod, test, uat.
 */
@Component
public class AbcsRecordsCheckPrefillData extends DocumentServiceUtil {

	private static final Logger logger = Logger.getLogger(AbcsRecordsCheckPrefillData.class);

	public static final ResourceBundle abcsDocumentProperties = ResourceBundle.getBundle("ABCSDocumentsEndPoint");

	@Autowired
	LookupDao lookupDao;

	@Autowired
	AbcsRecordsCheckService abcsRecordsCheckService;

	// various literals
	private static final String UE_GROUPID = "UE_GROUPID";// document
															// architecture
															// requirement for
															// repeaters with
															// default data
	private static final String RECCHECKNOTIF_TEXT = "Records Check Notification Email";
	private static final String HYPHEN = " - ";
	private static final String QUOTE = "\"";
	private static final String TRUE = "true";
	private static final String FALSE = "false";
	public static final String COMMA_SPACE_STRING = ", ";
	public static final char SPACE_STRING = ' ';
	public static final String ENVIRONMENT = "ENV";
	private static final String PROD = "PROD";
	private static final String DEVL="DEVL";
	private static final String TEST="TEST";
	private static final String UAT= "UAT";

	private static final String recordsCheckDetail_uploadABCSDocument_DEVL = "recordsCheckDetail.uploadABCSDocument.DEVL";
	private static final String recordsCheckDetail_uploadABCSDocument_TEST = "recordsCheckDetail.uploadABCSDocument.TEST";
	private static final String recordsCheckDetail_uploadABCSDocument_UAT =  "recordsCheckDetail.uploadABCSDocument.QA";
	private static final String recordsCheckDetail_uploadABCSDocument_PROD = "recordsCheckDetail.uploadABCSDocument.PROD";


	@Override
	public PreFillDataServiceDto returnPrefillData(Object parentDtoobj) {
		List<BookmarkDto> bookmarkDtoList = new ArrayList<BookmarkDto>();
		List<FormDataGroupDto> formDataGroupList = new ArrayList<FormDataGroupDto>();
		AbcsDto abcsDto = (AbcsDto) parentDtoobj;
		if (null == abcsDto.getResourceContractInfoDto()) {
			abcsDto.setResourceContractInfoDto(new ResourceContractInfoDto());
		}
		if (null == abcsDto.getPopulateNotificationDto()) {
			abcsDto.setPopulateNotificationDto(new PopulateNotificationDto());
		}
		if (null == abcsDto.getRecordsCheckNotifDto()) {
			abcsDto.setRecordsCheckNotifDto(new RecordsCheckNotifDto());
		}
		if (null == abcsDto.getEmployeePersonDto()) {
			abcsDto.setEmployeePersonDto(new EmployeePersonDto());
		}
		if (null == abcsDto.getEmployeePersonDto()) {
			abcsDto.setEmployeeWorkerPhoneDto(new EmployeePersonDto());
		}
		if (null == abcsDto.getEmployeeSupervisorDtoList()) {
			List<EmployeePersonDto> employeeSupervisorDtoList = new ArrayList<EmployeePersonDto>();
			EmployeePersonDto employeeSupervisorDto = new EmployeePersonDto();
			employeeSupervisorDtoList.add(employeeSupervisorDto);
			abcsDto.setEmployeeSupervisorDtoList(employeeSupervisorDtoList);
		}
		if (null == abcsDto.getPersonDto()) {
			abcsDto.setPersonDto(new PersonDto());
		}
		if (null == abcsDto.getAddressPersonLinkDto()) {
			abcsDto.setAddressPersonLinkDto(new AddressPersonLinkDto());
		}
		if (null == abcsDto.getAbcsRecordsCheckReqDto()) {
			abcsDto.setAbcsRecordsCheckReqDto(new AbcsRecordsCheckDto());
		}
		if (!ObjectUtils.isEmpty(abcsDto)) {
			/** TITLE SECTION **/
			populateTitleSection(abcsDto, bookmarkDtoList);

			/** ADDRESSEE SECTION **/
			populateAddresseeSection(abcsDto, bookmarkDtoList, formDataGroupList);

			/** BODY SECTION **/
			populateBodySection(abcsDto, bookmarkDtoList, formDataGroupList);

			/** CLOSING SECTION **/
			populateClosingSection(abcsDto, bookmarkDtoList);

			/** EMAIL SECTION **/
			populateEmailSection(abcsDto, bookmarkDtoList);
		}

		PreFillDataServiceDto preFillData = new PreFillDataServiceDto();
		preFillData.setBookmarkDtoList(bookmarkDtoList);
		preFillData.setFormDataGroupList(formDataGroupList);
		return preFillData;
	}

	/**
	 * populate title section.
	 */
	private void populateTitleSection(AbcsDto abcsDto, List<BookmarkDto> bookmarkDtoList) {
		BookmarkDto bookmarkTitleDto = createBookmark(BookmarkConstants.HTMLTITLE, RECCHECKNOTIF_TEXT);
		bookmarkDtoList.add(bookmarkTitleDto);

		// artf205247 Note: emailaction replaces the whitespace with devl message.
		bookmarkDtoList.add(createBookmark(BookmarkConstants.TESTENVMESSAGE, TESTENVMESSAGE_WHITESPACE));

		BookmarkDto bookmarkDirTitleDto = createBookmarkWithCodesTable(BookmarkConstants.DIRECTOR_TITLE,
				CodesConstant.CBRDTTLE_001, CodesConstant.CBRDTTLE);
		bookmarkDtoList.add(bookmarkDirTitleDto);
		BookmarkDto bookmarkDirNameDto = createBookmarkWithCodesTable(BookmarkConstants.DIRECTOR_NAME,
				CodesConstant.CBRDNAME_001, CodesConstant.CBRDNAME);
		bookmarkDtoList.add(bookmarkDirNameDto);
		BookmarkDto bookmarkDfpsTitleDto = createBookmark(BookmarkConstants.DFPSTITLE,
				BookmarkConstants.DFPSTITLE_TEXT);
		bookmarkDtoList.add(bookmarkDfpsTitleDto);
		BookmarkDto bookmarkCurrDateDto = createBookmark(BookmarkConstants.CURRENTDATE,
				TypeConvUtil.emailFormDateFormat(new Date()));
		bookmarkDtoList.add(bookmarkCurrDateDto);
		BookmarkDto bookmarkConfdDto = createBookmark(BookmarkConstants.CONFIDENTIAL,
				BookmarkConstants.CONFIDENTIAL_TEXT);
		bookmarkDtoList.add(bookmarkConfdDto);
	}

	/**
	 * populate addressee section.
	 */
	private void populateAddresseeSection(AbcsDto abcsDto, List<BookmarkDto> bookmarkDtoList,
			List<FormDataGroupDto> formDataGroupList) {
		new ArrayList<FormDataGroupDto>();
		new ArrayList<BookmarkDto>();
		String status = getContentEditableStatus(abcsDto.getRecordsCheckNotifDto().getCdNotifctnStat());
		if (TRUE.equals(status)) {
			FormDataGroupDto tempPocFullNameFrmDataGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_POCFULLNAME,
					"txtPocFullName");
			formDataGroupList.add(tempPocFullNameFrmDataGrpDto);
			FormDataGroupDto tempRsrcNameFrmDataGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_RESOURCENAME,
					"txtResourceName");
			formDataGroupList.add(tempRsrcNameFrmDataGrpDto);
			FormDataGroupDto tempRsrcSt1FrmDataGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_RESOURCESTREET1,
					"txtResourceStreet1");
			formDataGroupList.add(tempRsrcSt1FrmDataGrpDto);
			FormDataGroupDto tempRsrcAddrLn3FrmDataGrpDto = createFormDataGroup(
					FormGroupsConstants.TMPLAT_RESOURCEADDRLINE3, "txtResourceAddrLine3");
			formDataGroupList.add(tempRsrcAddrLn3FrmDataGrpDto);
			FormDataGroupDto tempDearPocFNFrmDataGrpDto = createFormDataGroup(
					FormGroupsConstants.TMPLAT_DEARPOCFULLNAME, "txtDearPocFullName");
			formDataGroupList.add(tempDearPocFNFrmDataGrpDto);
		}
		// fill the addressee section from values
		if (CodesConstant.NOTIFTYP_LTTRNOT.equals(abcsDto.getRecordsCheckNotifDto().getCdNotifType())) {
			BookmarkDto bookmarkPocFullNameDto = createBookmark(BookmarkConstants.POCFULLNAME,
					BookmarkConstants.POCDEFAULTTEXT);
			bookmarkDtoList.add(bookmarkPocFullNameDto);
			BookmarkDto bookmarkDearPocFNDto = createBookmark(BookmarkConstants.DEARPOCFULLNAME,
					BookmarkConstants.POCDEFAULTTEXT);
			bookmarkDtoList.add(bookmarkDearPocFNDto);
		} else if (CodesConstant.NOTIFTYP_CREGNOT.equals(abcsDto.getRecordsCheckNotifDto().getCdNotifType())) {

			BookmarkDto bookmarkPocFullNameDto = createBookmark(BookmarkConstants.POCFULLNAME,
					abcsDto.getPopulateNotificationDto().getCheckFullName());
			bookmarkDtoList.add(bookmarkPocFullNameDto);
			if (!ObjectUtils.isEmpty(abcsDto.getPopulateNotificationDto().getPocFirstAndLastName())) {
				BookmarkDto bookmarkDearPocFNDto = createBookmark(BookmarkConstants.DEARPOCFULLNAME,
						abcsDto.getPopulateNotificationDto().getCheckFullName() + COMMA_SPACE_STRING);// artf18911
				bookmarkDtoList.add(bookmarkDearPocFNDto);
			} else {
				BookmarkDto bookmarkDearPocFNDto = createBookmark(BookmarkConstants.DEARPOCFULLNAME,
						ServiceConstants.SPACE + COMMA_SPACE_STRING);// artf18911
				bookmarkDtoList.add(bookmarkDearPocFNDto);
			}

		} else {
			String pocFullName = CodesConstant.NOTIFTYP_NLCMNOT.equals(abcsDto.getRecordsCheckNotifDto()
					.getCdNotifType()) ? "" : abcsDto.getPopulateNotificationDto().getPocFirstAndLastName();
			BookmarkDto bookmarkPocFullNameDto = createBookmark(BookmarkConstants.POCFULLNAME, pocFullName);
			bookmarkDtoList.add(bookmarkPocFullNameDto);
			if (!ObjectUtils.isEmpty(abcsDto.getPopulateNotificationDto().getPocFirstAndLastName())) {
				BookmarkDto bookmarkDearPocFNDto = createBookmark(BookmarkConstants.DEARPOCFULLNAME,
						abcsDto.getPopulateNotificationDto().getPocFirstAndLastName() + COMMA_SPACE_STRING);// artf18911
				bookmarkDtoList.add(bookmarkDearPocFNDto);
			} else {
				BookmarkDto bookmarkDearPocFNDto = createBookmark(BookmarkConstants.DEARPOCFULLNAME,
						ServiceConstants.SPACE + COMMA_SPACE_STRING);// artf18911
				bookmarkDtoList.add(bookmarkDearPocFNDto);
			}

		}
		if (CodesConstant.NOTIFTYP_CREGNOT.equals(abcsDto.getRecordsCheckNotifDto().getCdNotifType())) {
			BookmarkDto bookmarkRsrcNameDto = createBookmark(BookmarkConstants.RESOURCENAME,
					abcsDto.getPopulateNotificationDto().getCheckFullName());
			bookmarkDtoList.add(bookmarkRsrcNameDto);
			BookmarkDto bookmarkRsrcSt1Dto = createBookmark(BookmarkConstants.RESOURCESTREET1,
					abcsDto.getPopulateNotificationDto().getRsrcSt1());
			bookmarkDtoList.add(bookmarkRsrcSt1Dto);
		} else {
			BookmarkDto bookmarkRsrcNameDto = createBookmark(BookmarkConstants.RESOURCENAME,
					abcsDto.getResourceContractInfoDto().getNmResource());
			bookmarkDtoList.add(bookmarkRsrcNameDto);
			BookmarkDto bookmarkRsrcSt1Dto = createBookmark(BookmarkConstants.RESOURCESTREET1,
					abcsDto.getResourceContractInfoDto().getRsrcAddrStLn1());
			bookmarkDtoList.add(bookmarkRsrcSt1Dto);
		}
		// if address line 2 exists a group displays data in an html row that is
		// editable based on status
		if (!StringUtils.isEmpty(abcsDto.getResourceContractInfoDto().getRsrcAddrStLn2())) {
			FormDataGroupDto tempRsrcSt2FrmDataGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_RESOURCESTREET2,
					"reptxtResourceStreet2");
			List<FormDataGroupDto> tempRsrcSt2FrmDataGrpList = new ArrayList<FormDataGroupDto>();
			if (TRUE.equals(status)) {
				FormDataGroupDto tempRsrcSt2EditFrmDataGrpDto = createFormDataGroup(
						FormGroupsConstants.TMPLAT_RESOURCESTREET2_EDIT, FormGroupsConstants.TMPLAT_RESOURCESTREET2);
				tempRsrcSt2FrmDataGrpList.add(tempRsrcSt2EditFrmDataGrpDto);
			}
			FormDataGroupDto tempRsrcSt2Addr2FrmDataGrpDto = createFormDataGroup(
					FormGroupsConstants.TMPLAT_RESOURCESTREET2_ADDR2, FormGroupsConstants.TMPLAT_RESOURCESTREET2);
			tempRsrcSt2FrmDataGrpList.add(tempRsrcSt2Addr2FrmDataGrpDto);
			tempRsrcSt2FrmDataGrpDto.setFormDataGroupList(tempRsrcSt2FrmDataGrpList);

			List<BookmarkDto> bookmarkRsrcSt2List = new ArrayList<BookmarkDto>();
			BookmarkDto bookmarkUeGrpDto = createBookmark(UE_GROUPID,
					abcsDto.getAbcsRecordCheckReq().getIdRecordsCheckNotif());// artf18907
			BookmarkDto bookmarkRsrcSt2Addr2Dto = createBookmark(BookmarkConstants.RESOURCESTREET2_ADDR2,
					abcsDto.getResourceContractInfoDto().getRsrcAddrStLn2());
			bookmarkRsrcSt2List.add(bookmarkUeGrpDto);
			bookmarkRsrcSt2List.add(bookmarkRsrcSt2Addr2Dto);
			tempRsrcSt2FrmDataGrpDto.setBookmarkDtoList(bookmarkRsrcSt2List);
			formDataGroupList.add(tempRsrcSt2FrmDataGrpDto);
		}

		if (CodesConstant.NOTIFTYP_CREGNOT.equals(abcsDto.getRecordsCheckNotifDto().getCdNotifType())) {
			BookmarkDto bookmarkRsrcAddrLane3Dto = createBookmark(BookmarkConstants.RESOURCEADDRLINE3,
					getAddrLine3(abcsDto.getPopulateNotificationDto().getRsrcCity(),
							abcsDto.getPopulateNotificationDto().getRsrcState(),
							abcsDto.getPopulateNotificationDto().getRsrcZip()));
			bookmarkDtoList.add(bookmarkRsrcAddrLane3Dto);
		} else {
			BookmarkDto bookmarkRsrcAddrLn3Dto = createBookmark(BookmarkConstants.RESOURCEADDRLINE3,
					getAddrLine3(abcsDto.getResourceContractInfoDto().getRsrcAddrCity(),
							abcsDto.getResourceContractInfoDto().getCdRsrcAddrState(),
							abcsDto.getResourceContractInfoDto().getRsrcAddrZip()));
			bookmarkDtoList.add(bookmarkRsrcAddrLn3Dto);
		}

		BookmarkDto bookmarkDearPocDto = createBookmark(BookmarkConstants.DEARPOC, BookmarkConstants.DEARPOCTEXT);
		BookmarkDto bookmarkRcpntIdDto = createBookmark(BookmarkConstants.RECIPIENTID,
				abcsDto.getResourceContractInfoDto().getIdrecCheckRequestor());

		bookmarkDtoList.add(bookmarkDearPocDto);
		bookmarkDtoList.add(bookmarkRcpntIdDto);
	}

	/**
	 * populate body section.
	 */
	private void populateBodySection(AbcsDto abcsDto, List<BookmarkDto> bookmarkDtoList,
			List<FormDataGroupDto> formDataGroupList)  {
		String status = getContentEditableStatus(abcsDto.getRecordsCheckNotifDto().getCdNotifctnStat());
		if (TRUE.equals(status)) {
			FormDataGroupDto tempBdyLetterFrmDataGrpDto = createFormDataGroup(FormGroupsConstants.TMPLAT_BODYOFLETTER,
					"txtBodyOfLetter");
			formDataGroupList.add(tempBdyLetterFrmDataGrpDto);
		}
		if (!ObjectUtils.isEmpty(abcsDto.getRecordsCheckNotifDto().getCdNotifType())) {
			BookmarkDto bookmarkBdyLetterDto = createBookmark(BookmarkConstants.BODYOFLETTER,
					getBodyOfLetter(abcsDto.getRecordsCheckNotifDto().getCdNotifType())/* artf21570 */.replaceAll(
							BookmarkConstants.CHECKFULLNAME,
							abcsDto.getPopulateNotificationDto().getCheckedPersonFnameLname()));
			bookmarkDtoList.add(bookmarkBdyLetterDto);
			if (!ObjectUtils.isEmpty(abcsDto.getPopulateNotificationDto().getCheckedPersonFnameLname())) {
				BookmarkDto bookmarkChkFNameDto = createBookmark(BookmarkConstants.CHECKFULLNAME,
						abcsDto.getPopulateNotificationDto().getCheckedPersonFnameLname());
				bookmarkDtoList.add(bookmarkChkFNameDto);
			}

			if (CodesConstant.NOTIFTYP_ACTRNOT.equals(abcsDto.getRecordsCheckNotifDto().getCdNotifType())) {
				String hyperlinkText = getUploadLink(abcsDto);
				BookmarkDto bookmarkHyprLinkDto = createBookmark(BookmarkConstants.HYPERLINK,
						QUOTE + hyperlinkText + QUOTE);
				bookmarkDtoList.add(bookmarkHyprLinkDto);
				BookmarkDto bookmarkHyprLnkTxtDto = createBookmark(BookmarkConstants.HYPERLINKTEXT, hyperlinkText);// artf18784
				bookmarkDtoList.add(bookmarkHyprLnkTxtDto);
			}
			if(CodesConstant.NOTIFTYP_FBIHVEL.equalsIgnoreCase(abcsDto.getRecordsCheckNotifDto().getCdNotifType())
				|| CodesConstant.NOTIFTYP_FBIVIEL.equalsIgnoreCase(abcsDto.getRecordsCheckNotifDto().getCdNotifType())){
				AbcsRecordsCheckDto abcsRecordsCheckDto = abcsRecordsCheckService.getAbcsRecordsCheckDetails(abcsDto.getAbcsRecordCheckReq().getIdRecCheck());
				BookmarkDto bookmarkSubjNameDto = createBookmark(BookmarkConstants.SUBJECT_NAME,abcsRecordsCheckDto.getSubjectName());
				bookmarkDtoList.add(bookmarkSubjNameDto);

				BookmarkDto bookmarkAbcsAccDto = createBookmark(BookmarkConstants.ABCS_ACCOUNT, abcsRecordsCheckDto.getAbcsAccount());
				bookmarkDtoList.add(bookmarkAbcsAccDto);

				BookmarkDto bookmarkBgcSubDateDto = createBookmark(BookmarkConstants.BGC_SUBMISSION_DATE, TypeConvUtil.emailFormDateFormat(abcsRecordsCheckDto.getDtRecCheckRequest()));
				bookmarkDtoList.add(bookmarkBgcSubDateDto);
			}

			if(CodesConstant.NOTIFTYP_FBIENOT.equalsIgnoreCase(abcsDto.getRecordsCheckNotifDto().getCdNotifType())
					|| CodesConstant.NOTIFTYP_FBIINOT.equalsIgnoreCase(abcsDto.getRecordsCheckNotifDto().getCdNotifType())
					|| CodesConstant.NOTIFTYP_PCSINEN.equalsIgnoreCase(abcsDto.getRecordsCheckNotifDto().getCdNotifType())
			){
				AbcsRecordsCheckDto abcsRecordsCheckDto = abcsRecordsCheckService.getAbcsRecordsCheckDetails(abcsDto.getAbcsRecordCheckReq().getIdRecCheck());
				BookmarkDto bookmarkSubjNameDto = createBookmark(BookmarkConstants.SUBJECT_NAME,abcsRecordsCheckDto.getSubjectName());
				bookmarkDtoList.add(bookmarkSubjNameDto);

				BookmarkDto bookmarkPidDto = createBookmark(BookmarkConstants.PID,abcsRecordsCheckDto.getPid());
				bookmarkDtoList.add(bookmarkPidDto);

				BookmarkDto bookmarkResourceIdDto = createBookmark(BookmarkConstants.ID_RESOURCE,abcsRecordsCheckDto.getResourceId());
				bookmarkDtoList.add(bookmarkResourceIdDto);

				BookmarkDto bookmarkAbcsAccDto = createBookmark(BookmarkConstants.ABCS_ACCOUNT, abcsRecordsCheckDto.getAbcsAccount());
				bookmarkDtoList.add(bookmarkAbcsAccDto);

				BookmarkDto bookmarkAbcsAgencyNameDto = createBookmark(BookmarkConstants.AGENCY_NAM, abcsDto.getResourceContractInfoDto().getNmResource());
				bookmarkDtoList.add(bookmarkAbcsAgencyNameDto);

				String subjectName = abcsRecordsCheckDto.getSubjectName();
				String[] subjectNameArray = StringUtils.isNotBlank(subjectName)?subjectName.split(" "):null;
				if(Objects.nonNull(subjectNameArray) && subjectNameArray.length>=2){
					subjectName = subjectNameArray[1]+", "+subjectNameArray[0];
				}

				BookmarkDto bookmarkLastFirstNameDto = createBookmark(BookmarkConstants.LAST_FIRST,subjectName);
				bookmarkDtoList.add(bookmarkLastFirstNameDto);


				BookmarkDto bookmarkBgcSubDateDto = createBookmark(BookmarkConstants.BGC_SUBMISSION_DATE, TypeConvUtil.emailFormDateFormat(abcsRecordsCheckDto.getDtRecCheckRequest()));
				bookmarkDtoList.add(bookmarkBgcSubDateDto);

				BookmarkDto bookmarkContactDto = createBookmark(BookmarkConstants.DYNAMIC_EMAIL,
						determineSenderByContractType(abcsDto.getResourceContractInfoDto().getCdCntrctType(), abcsDto));
				bookmarkDtoList.add(bookmarkContactDto);

			}
		}
	}

	/**
	 * populate closing section.
	 */
	private void populateClosingSection(AbcsDto abcsDto, List<BookmarkDto> bookmarkDtoList) {
		BookmarkDto bookmarkSincrlyDto = createBookmark(BookmarkConstants.SINCERELY, BookmarkConstants.SINCERELYTEXT);
		bookmarkDtoList.add(bookmarkSincrlyDto);
		BookmarkDto bookmarkSndrFNameDto = createBookmark(BookmarkConstants.SENDERFULLNAME,
				abcsDto.getPopulateNotificationDto().getSndrFullName());
		bookmarkDtoList.add(bookmarkSndrFNameDto);
		BookmarkDto bookmarkCbcuNameDto = createBookmark(BookmarkConstants.CBCUNAME, BookmarkConstants.CBCUNAMETEXT);
		bookmarkDtoList.add(bookmarkCbcuNameDto);
		BookmarkDto bookmarkCbcuAddrDto = createBookmark(BookmarkConstants.CBCUADDR, BookmarkConstants.CBCUADDRTEXT);
		bookmarkDtoList.add(bookmarkCbcuAddrDto);
		BookmarkDto bookmarkCbcuLn3Dto = createBookmark(BookmarkConstants.CBCULINE3, BookmarkConstants.CBCULINE3TEXT);
		bookmarkDtoList.add(bookmarkCbcuLn3Dto);
		BookmarkDto bookmarkSndrPhnDto = createBookmark(BookmarkConstants.SENDERPHONE,
				abcsDto.getPopulateNotificationDto().getSndrPhone());
		BookmarkDto bookmarkSndrEML = createBookmark(BookmarkConstants.SENDEREMAIL,
			abcsDto.getPopulateNotificationDto().getSedSenderEmail());

		bookmarkDtoList.add(bookmarkSndrPhnDto);
		bookmarkDtoList.add(bookmarkSndrEML);
		BookmarkDto bookmarkFooterDto = createBookmark(BookmarkConstants.FOOTER, BookmarkConstants.FOOTERTEXT);// artf21496
		bookmarkDtoList.add(bookmarkFooterDto);
	}

	/**
	 * populate email section.
	 */
	private void populateEmailSection(AbcsDto abcsDto, List<BookmarkDto> bookmarkDtoList) {
		BookmarkDto bookmarkStripToLblDto = createBookmark(BookmarkConstants.STRIPTOLABEL,
				BookmarkConstants.STRIPTOLABELTEXT);
		bookmarkDtoList.add(bookmarkStripToLblDto);
		// populate EMAILTO except for cregnot, lttrnot

		if ((!ObjectUtils.isEmpty(abcsDto.getRecordsCheckNotifDto().getCdNotifType()))
				&& (CodesConstant.NOTIFTYP_CREGNOT.equals(abcsDto.getRecordsCheckNotifDto().getCdNotifType())
						|| CodesConstant.NOTIFTYP_LTTRNOT.equals(abcsDto.getRecordsCheckNotifDto().getCdNotifType())
						|| CodesConstant.NOTIFTYP_FBIHVEL.equals(abcsDto.getRecordsCheckNotifDto().getCdNotifType())
						|| CodesConstant.NOTIFTYP_FBIVIEL.equals(abcsDto.getRecordsCheckNotifDto().getCdNotifType())
						|| CodesConstant.NOTIFTYP_FBIINOT.equals(abcsDto.getRecordsCheckNotifDto().getCdNotifType())
						|| CodesConstant.NOTIFTYP_FBIENOT.equals(abcsDto.getRecordsCheckNotifDto().getCdNotifType()))){
			BookmarkDto bookmarkEmailToDto = createBookmark(BookmarkConstants.EMAILTO,
					BookmarkConstants.EMAILTODEFAULTTEXT);
			bookmarkDtoList.add(bookmarkEmailToDto);
		} else {
			BookmarkDto bookmarkEmailToDto = createBookmark(BookmarkConstants.EMAILTO,
					abcsDto.getPopulateNotificationDto().getRecpntEmail());
			bookmarkDtoList.add(bookmarkEmailToDto);
		}

		BookmarkDto bookmarkStripFrmLblDto = createBookmark(BookmarkConstants.STRIPFROMLABEL,
				BookmarkConstants.STRIPFROMLABELTEXT);
		bookmarkDtoList.add(bookmarkStripFrmLblDto);
		BookmarkDto bookmarkEmailFrmDto = createBookmark(BookmarkConstants.EMAILFROM,
				determineSenderByContractType(abcsDto.getResourceContractInfoDto().getCdCntrctType(), abcsDto));
		bookmarkDtoList.add(bookmarkEmailFrmDto);
		BookmarkDto bookmarkStripSubLblDto = createBookmark(BookmarkConstants.STRIPSUBJECTLABEL,
				BookmarkConstants.STRIPSUBJECTLABELTEXT);
		bookmarkDtoList.add(bookmarkStripSubLblDto);
		if (!ObjectUtils.isEmpty(abcsDto.getRecordsCheckNotifDto())) {
			BookmarkDto bookmarkEmailSubDto = createBookmark(BookmarkConstants.EMAILSUBJECT,
					getSubjectLine(abcsDto));
			bookmarkDtoList.add(bookmarkEmailSubDto);
		}
	}

	/**
	 * Returns the sender email based on Contract Type with default OTHR
	 * 
	 * @param contractType
	 * @return sender
	 */
	private String determineSenderByContractType(String contractType, AbcsDto abcsDto) {
		String sender = null == (lookupDao.simpleDecodeSafe(CodesConstant.CCBCUEML, contractType))
				? lookupDao.simpleDecodeSafe(CodesConstant.CCBCUEML, CodesConstant.CCBCUEML_OTHR)
				: lookupDao.simpleDecodeSafe(CodesConstant.CCBCUEML, contractType);
		abcsDto.getAbcsRecordsCheckReqDto().setTxtSndrEmail(sender);
		return sender;
	}

	/**
	 * artf21480 Returns the BODYOFLETTER based on cdNotifType
	 * 
	 * @param cdNotifType
	 * @return
	 */
	private String getBodyOfLetter(String cdNotifType) {
		Map<String, String> bodyOfLetter = new HashMap<String, String>();
		bodyOfLetter.put(CodesConstant.NOTIFTYP_ACTRNOT, BODYOFLETTER_ACTRNOT);
		bodyOfLetter.put(CodesConstant.NOTIFTYP_BARRNOT, BODYOFLETTER_BARRNOT);
		bodyOfLetter.put(CodesConstant.NOTIFTYP_CREGNOT, BODYOFLETTER_CREGNOT);
		bodyOfLetter.put(CodesConstant.NOTIFTYP_LTTRNOT, BODYOFLETTER_LTTRNOT);
		bodyOfLetter.put(CodesConstant.NOTIFTYP_NLCMNOT, BODYOFLETTER_NLCMNOT);
		bodyOfLetter.put(CodesConstant.NOTIFTYP_PCSRNOT, BODYOFLETTER_PCSRNOT);
		bodyOfLetter.put(CodesConstant.NOTIFTYP_FBIHVEL, BODYOFLETTER_FBIHVEL);
		bodyOfLetter.put(CodesConstant.NOTIFTYP_FBIVIEL, BODYOFLETTER_FBIVIEL);
		bodyOfLetter.put(CodesConstant.NOTIFTYP_FBIINOT, BODYOFLETTER_FBIINOT);
		bodyOfLetter.put(CodesConstant.NOTIFTYP_PCSINEN, BODYOFLETTER_PCSINEN);
		return bodyOfLetter.get(cdNotifType);
	}

	private String getSubjectLine(AbcsDto abcsDto) {
		AbcsRecordCheckReq abcsRecordCheckReq = abcsDto.getAbcsRecordCheckReq();
		String checkFullName = abcsDto.getPopulateNotificationDto().getCheckFullName();
		String cdNotifType = abcsDto.getRecordsCheckNotifDto().getCdNotifType();
		StringBuilder subject = new StringBuilder();
		subject.append(BookmarkConstants.EMAILSUBJECTLINETEXT).append(HYPHEN);
		if (!ObjectUtils.isEmpty(checkFullName)) {
			subject.append(checkFullName);
		} else {
			subject.append(ServiceConstants.SPACE);
		}
		if (!CodesConstant.NOTIFTYP_LTTRNOT.equals(cdNotifType) && !CodesConstant.NOTIFTYP_PCSINEN.equalsIgnoreCase(cdNotifType)) {// artf21492
			subject.append(HYPHEN).append(createBookmarkWithCodesTable("", cdNotifType,
					CodesConstant.NOTIFTYP).getBookmarkData());
		}
		if (CodesConstant.NOTIFTYP_NLCMNOT.equals(cdNotifType)) {
			subject = new StringBuilder();
			subject.append("#");
			subject.append(abcsDto.getResourceContractInfoDto().getIdContract());
			subject.append(" ");
			subject.append(abcsDto.getResourceContractInfoDto().getNmResource());
			subject.append(" ");
			subject.append(checkFullName);
		}
		return subject.toString();
	}

	private String getContentEditableStatus(String cdNotifStat) {
		String contentEditable = FALSE;
		if (CodesConstant.CNOTSTAT_NEW.equals(cdNotifStat) || CodesConstant.CNOTSTAT_DRFT.equals(cdNotifStat)) {
			contentEditable = TRUE;
		}
		return contentEditable;
	}

	private String getUploadLink(AbcsDto abcsDto)  {
		String envName = System.getProperty(ENVIRONMENT);
		logger.info("The envName is " + envName);
		String uploadUrl = null;
		if (PROD.equalsIgnoreCase(envName)) {
			uploadUrl = abcsDocumentProperties.getString(recordsCheckDetail_uploadABCSDocument_PROD) ;
		}
		else if (DEVL.equalsIgnoreCase(envName)) {
			uploadUrl = abcsDocumentProperties.getString(recordsCheckDetail_uploadABCSDocument_DEVL) ;
		}
		else if (TEST.equalsIgnoreCase(envName)) {
			uploadUrl = abcsDocumentProperties.getString(recordsCheckDetail_uploadABCSDocument_TEST) ;
		}
		else if (UAT.equalsIgnoreCase(envName)) {
			uploadUrl = abcsDocumentProperties.getString(recordsCheckDetail_uploadABCSDocument_UAT) ;
		}
		else
		{
			uploadUrl = abcsDocumentProperties.getString(recordsCheckDetail_uploadABCSDocument_DEVL) ;
		}

		return new StringBuilder().append(uploadUrl)
				.append(abcsDto.getResourceContractInfoDto().getBackGroundCheckReqid()).toString();

	}

	private static final String TESTENVMESSAGE_WHITESPACE = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"; // artf205247 unlikely white space search sequence

	// default data
	private static final String BODYOFLETTER_ACTRNOT = new StringBuilder().append("<br>")
			.append("<b>[SELECT ONE OR MORE OF THE FOLLOWING OPTIONS:]</b>").append("<br>")
			.append("<br><b><u>Risk Evaluation Required</u></b>").append("<br>")
			.append("<br>The results of the criminal history check from the [INSERT BACKGROUND CHECK TYPE] indicate that this person has the following criminal history. <b>This criminal history is eligible for a risk evaluation.</b>")
			.append("<br>").append("<br>[INSERT EDITED RAP SHEET]").append("<br>")
			.append("<br>If you wish to request a risk evaluation, please submit Form 2973c \"Request for Risk Evaluation based on Criminal History Results\" and the supporting documents as listed on Form 2973c.  The form can be located at: <a href=\"http://www.dfps.texas.gov/Handbooks/CBCU/Files/CBCU_pg_2000.asp#CBCU_2453\">http://www.dfps.texas.gov/Handbooks/CBCU/Files/CBCU_pg_2000.asp#CBCU_2453</a>.")
			.append("<br>")
			.append("<br>Should you choose to complete a risk evaluation request, you have thirty days from the date of this notice to submit your documentation to DFPS.  If additional time is required to comply with this request, please contact me to discuss a possible extension before the due date.")
			.append("<br>").append("<br>").append("<br><b><u>Court Documents Needed</u></b>").append("<br>")
			.append("<br>The results of the criminal history check from the [INSERT BACKGROUND CHECK TYPE] indicate that <b>this person may have the following criminal history</b>.  Please note that the final disposition(s) of any offenses were not available when the results were processed.")
			.append("<br>").append("<br>[INSERT EDITED RAP SHEET]").append("<br>")
			.append("<br>The individual is required to provide court documentation that clarifies the final disposition. If the individual does not have this documentation, the necessary documentation must be obtained from the County Clerk or District Clerk in the county where the offense occurred.")
			.append("<br>")
			.append("<br>Please submit this official documentation regarding the final disposition, level, and degree regarding this charge within 30 days from the date of this notice, so that DFPS may determine what, if any, additional action will be required. If additional time is required to comply with this request, please contact me to discuss a possible extension before the due date.")
			.append("<br>").append("<br>").append("<br><b><u>Out of State Criminal History Check Required</u></b>")
			.append("<br>")
			.append("<br>The background check request indicates that this person has lived out of state during the last two years.  As a result, a state criminal history check is required from the following sources:")
			.append("<br>").append("<br>[INSERT TEXT REGARDING OUT OF STATE CRIMINAL HISTORY SOURCE]").append("<br>")
			.append("<br>Please submit the official results (clearance or criminal history) returned within 30 days from the date of this notice, so that DFPS may determine what, if any, additional action will be required. If additional time is required to comply with this request, please contact me to discuss a possible extension before the due date.")
			.toString();

	private static final String BODYOFLETTER_BARRNOT = new StringBuilder()
			.append("<br><b>[SELECT ONE OF THE FOLLOWING OPTIONS:]</b>").append("<br><br><b><u>PCS</u></b>")
			.append("<br><br><b>The information found during the course of this person's background check currently constitutes a bar from working with DFPS clients. </b>")
			.append(" Please ensure that this person does not have any direct client contact with DFPS clients, including access to DFPS client records. Please note that this decision does not affect this person's employment with your agency but rather relates to the DFPS-funded contract under which the background check was submitted.")
			.append("<br><br><b><u>External Access</u></b>")
			.append("<br><br><b>Based on the information found during the course of this person's background check, this person is denied approval to provide services for DFPS or to have access to DFPS resources. </b>")// artf21569
			.append(" Please note that this decision does not affect this person's employment but rather relates to providing services for DFPS.")
			.toString();

	private static final String BODYOFLETTER_CREGNOT = new StringBuilder().append("<br><br>Case ID:")
			.append("<br><br>Program:").append("<br><br>Role:")
			.append("<br><br>Some <b>designated perpetrators</b> may have the right to an administrative review of the findings in the case. To determine if you are eligible for an administrative review, please contact the Resolution Specialist, CCL Contact or APS Contact listed below.  All requests or inquiries regarding eligibility must be made in writing within 15 days of receipt of this letter, and must include the Program and Case ID listed above. You will be notified regarding your eligibility for an administrative review of a finding by the contact listed below or a designee.  Send the request for administrative review to:")
			.append("<br><br>[INSERT CPS RESOLUTION SPECIALISTS INFO HERE]").toString();

	private static final String BODYOFLETTER_LTTRNOT = new StringBuilder().append("[INSERT BODY OF LETTER HERE]")
			.toString();

	private static final String BODYOFLETTER_NLCMNOT = new StringBuilder().append("<br>")
			.append("<b>[SELECT ONE OF THE FOLLOWING OPTIONS:]</b>").append("<br>")
			.append("<br><b><u>Criminal History</u></b>").append("<br>")
			.append("<br>The results of the criminal history check from the Department of Public Safety indicate that this person has the following criminal history:")
			.append("<br>").append("<br>[INSERT EDITED RAP SHEET]").append("<br>")
			.append("<br>DFPS must provide the subject of the background check with information on how to obtain a copy of and/or contest the information found in his or her DPS results.  If the subject of this background check is interested in obtaining more information about this process, please contact me at the number below.")
			.append("<br>").append("<br>").append("<br><b><u>DFPS History</u></b>").append("<br>")
			.append("<br>The results of the search of the Texas Department of Family and Protective Services case management system indicate that this person has or may have the following history:")
			.append("<br>").append("<br>[INSERT DFPS HISTORY DETAILS]").append("<br>")
			.append("<br>If the subject of the background check denies having the above background check history this individual can obtain copies of the information and/or contest the results. Please direct this person to contact me at the number below if he or she would like more information.")
			.toString();

	private static final String BODYOFLETTER_PCSRNOT = new StringBuilder().append("<br>")
			.append("<b>[SELECT ONE OF THE FOLLOWING OPTIONS:]</b>").append("<br>")
			.append("<br><b><u>Risk Evaluation Approved</u></b>").append("<br>")
			.append("<br>I have reviewed your request for a risk evaluation on <b>CHECKFULLNAME</b> regarding the criminal history below:")
			.append("<br>").append("<br>[INSERT DATE, STATUS, LEVEL, and CRIME]").append("<br>")
			.append("<br>Based upon your recommendation and after careful consideration of the information submitted in your request, it has been determined that this risk evaluation is approved for the position below:")
			.append("<br>").append("<br>[INSERT CURRENT POSITION AND ANY CONDITIONS FOR THIS POSITION]").append("<br>")
			.append("<br>This person is cleared to work with DFPS clients under your contract.").append("<br>")
			.append("<br>It is your responsibility to notify DFPS if you learn of any new arrest or abuse/neglect investigation by an HHSC agency involving this person.")
			.append("<br>").append("<br>").append("<br><b><u>Risk Evaluation Denied</u></b>").append("<br>")
			.append("<br>I have reviewed your request for a risk evaluation on <b>CHECKFULLNAME</b> regarding the criminal history below:")
			.append("<br>").append("<br>[INSERT DATE, STATUS, LEVEL, and CRIME]").append("<br>")
			.append("<br>After careful consideration of the information submitted in your request, it has been determined that this risk evaluation is denied. Please ensure that this person does not have any direct client contact with DFPS clients, including access to DFPS client records. Please note that this decision does not affect this person's employment with your agency but rather relates to the DFPS-funded contract under which the background check was submitted.")
			.toString();

	private static final String BODYOFLETTER_FBIHVEL = new StringBuilder().append("<br>")
			.append("<br>[INSERT CRIMINAL HISTORY HERE]").toString();

	private static final String BODYOFLETTER_FBIVIEL = new StringBuilder().append("<br>")
			.append("<br>[INSERT CRIMINAL HISTORY HERE]").toString();

	private static final String BODYOFLETTER_FBIENOT = new StringBuilder().append("<br>")
			.toString();

	private static final String BODYOFLETTER_FBIINOT = new StringBuilder().append("<br>")
			.toString();

	private static final String BODYOFLETTER_PCSINEN = new StringBuilder().append("<br>")
			.append("<br>[Fillable comment box that can be used if needed]").toString();

	public static String getAddrLine3(String city, String state, String zip) {
		StringBuilder sb = new StringBuilder();
		if (null != city) {
			sb.append(city);
			if (null != state) {
				sb.append(COMMA_SPACE_STRING);
			}
		}
		if (null != state) {
			sb.append(state);
		}
		if (null != zip) {
			sb.append(SPACE_STRING).append(zip);
		}
		return sb.toString();
	}

}
