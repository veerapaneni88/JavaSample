package us.tx.state.dfps.service.forms.util;
import org.apache.commons.codec.binary.Base64;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import us.tx.state.dfps.service.common.BookmarkConstants;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.FormGroupsConstants;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.forms.dto.BookmarkDto;

import us.tx.state.dfps.service.forms.dto.CrpRecordNotifAndDetailsDto;
import us.tx.state.dfps.service.forms.dto.FormDataGroupDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.person.dto.CentralRegistryCheckDto;
import us.tx.state.dfps.service.person.dto.CrpPersonNameDto;

import us.tx.state.dfps.service.person.dto.CrpRecordNotifDto;
import us.tx.state.dfps.service.person.dto.PublicCentralRegistryDto;
import us.tx.state.dfps.service.recordscheck.dto.*;
import us.tx.state.dfps.service.workload.dto.EmployeePersPhNameDto;

import java.lang.reflect.Array;
import java.util.*;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:*
 * CrpRecordNotifPrefillData will implemente returnPrefillData operation
 * defined in DocumentServiceUtil Interface to populate the prefill data for
 * CRP Record Notif forms. Jan 25, 2024- 08:45:00 AM © 2024 Texas
 * Department of Family and Protective Services
 *
 * ********Change History**********
 * 01/25/2024 thompswa Initial.
 */
@Component
public class CrpRecordNotifPrefillData extends DocumentServiceUtil {

	private static final Logger logger = Logger.getLogger(CrpRecordNotifPrefillData.class);
	// various literals
	private static final String UE_GROUPID = "UE_GROUPID";// document
															// architecture
															// requirement for
															// repeaters with
															// default data
	private static final String RECCHECKNOTIF_TEXT = "Records Check Notification Email";
	private static final String HYPHEN = " - ";
	private static final String NO_FINDING = "none determined";
	private static final String QUOTE = "\"";
	private static final String ENV = "devl";
	public static final String COMMA_SPACE_STRING = ", ";
	public static final char SPACE_STRING = ' ';
	public static final String ENVIRONMENT = "ENV";
	private static final String PROD = "PROD";
	private static final String DEVL="DEVL";
	private static final String TEST="TEST";
	private static final String UAT= "UAT";
	private static final String VISTA= "VISTA";

	public static final String UPLOAD_URL_DEVL = "https://portalazdev.dfps.texas.gov/wps/portal/abcs/uploadfile?crpReqId=";
	public static final String UPLOAD_URL_DEFAULT = "https://portalazdev.dfps.texas.gov/wps/portal/abcs/uploadfile?crpReqId=";
	public static final String UPLOAD_URL_TEST = "https://portalaztest.dfps.texas.gov/wps/portal/abcs/uploadfile?crpReqId=";
	public static final String UPLOAD_URL_QA = "https://portalazuat.dfps.texas.gov/wps/portal/abcs/uploadfile?crpReqId=";
	public static final String UPLOAD_URL_PROD = "https://portalext.dfps.texas.gov/wps/portal/abcs/uploadfile?crpReqId=";
	public static final String UPLOAD_URL_VISTA = "https://portalazdev.dfps.texas.gov/wps/portal/abcs/uploadfile?crpReqId=";

	@Override
	public PreFillDataServiceDto returnPrefillData(Object parentDtoobj) {
		List<BookmarkDto> bookmarkDtoList = new ArrayList<BookmarkDto>();
		List<FormDataGroupDto> formDataGroupList = new ArrayList<FormDataGroupDto>();
		CrpRecordNotifFormsDto crpDto = (CrpRecordNotifFormsDto) parentDtoobj;

		if (null == crpDto.getEmpRequestorDto()) {
			crpDto.setEmpRequestorDto(new EmployeePersonDto());
		}
		if (null == crpDto.getAbcsRecordsCheckDto()) {
			crpDto.setAbcsRecordsCheckDto(new AbcsRecordsCheckDto());
		}
		if (null == crpDto.getResourceContractInfoDto()) {
			crpDto.setResourceContractInfoDto(new ResourceContractInfoDto());
		}
		if (null == crpDto.getCentralRegistryCheckDto()) {
			crpDto.setCentralRegistryCheckDto(new CentralRegistryCheckDto());
		}
		if (null == crpDto.getCrpPersonNameList()) {
			crpDto.setCrpPersonNameList(
					Arrays.asList(new CrpPersonNameDto()));
		}
		if (null == crpDto.getCrpBatchResultsList()) {
			crpDto.setCrpBatchResultsList(
					Arrays.asList(new PublicCentralRegistryDto()));
		}
		if (null == crpDto.getCrpRecordNotifAndDetailsDto()) {
			crpDto.setCrpRecordNotifAndDetailsDto(new CrpRecordNotifAndDetailsDto());
		}
		if (null == crpDto.getEmployeePersPhNameDto()) {
			crpDto.setEmployeePersPhNameDto(new EmployeePersPhNameDto());
		}
		if (!ObjectUtils.isEmpty(crpDto)) {
			/** TITLE SECTION **/
			populateTitleSection(crpDto, bookmarkDtoList);

			/** ADDRESSEE SECTION **/
			populateAddresseeSection(crpDto, bookmarkDtoList, formDataGroupList);

			/** BODY SECTION **/
			populateBodySection(crpDto, bookmarkDtoList, formDataGroupList);

			/** CLOSING SECTION **/
			populateClosingSection(crpDto, bookmarkDtoList, formDataGroupList);

			/** EMAIL SECTION **/
			populateEmailSection(crpDto, bookmarkDtoList, formDataGroupList);
		}

		PreFillDataServiceDto preFillData = new PreFillDataServiceDto();
		preFillData.setBookmarkDtoList(bookmarkDtoList);
		preFillData.setFormDataGroupList(formDataGroupList);
		return preFillData;
	}

	/**
	 * populate title section.
	 */
	private void populateTitleSection(CrpRecordNotifFormsDto crpDto, List<BookmarkDto> bookmarkDtoList) {
		bookmarkDtoList.addAll(Arrays.asList(createBookmark(BookmarkConstants.HTMLTITLE, RECCHECKNOTIF_TEXT)
				, createBookmark(BookmarkConstants.TESTENVMESSAGE, TESTENVMESSAGE_WHITESPACE)// artf205247 Note: emailaction replaces the whitespace with devl message.
				, createBookmarkWithCodesTable(BookmarkConstants.DIRECTOR_TITLE, CodesConstant.CBRDTTLE_001, CodesConstant.CBRDTTLE)
				, createBookmarkWithCodesTable(BookmarkConstants.DIRECTOR_NAME, CodesConstant.CBRDNAME_001, CodesConstant.CBRDNAME)
				, createBookmark(BookmarkConstants.CURRENTDATE, TypeConvUtil.emailFormDateFormat(new Date()))
				, createBookmark(BookmarkConstants.CONFIDENTIAL, BookmarkConstants.CONFIDENTIAL_TEXT)
		));
	}

	/**
	 * populate addressee section.
	 */
	private void populateAddresseeSection(CrpRecordNotifFormsDto crpDto, List<BookmarkDto> bookmarkDtoList,
			List<FormDataGroupDto> formDataGroupList) {
		if (ServiceConstants.TRUE.equals(crpDto.getContentEditableStatus())) {
			formDataGroupList.add( createFormDataGroup(FormGroupsConstants.TMPLAT_POCFULLNAME, ServiceConstants.EMPTY_STRING));
			formDataGroupList.add( createFormDataGroup(FormGroupsConstants.TMPLAT_RESOURCEADDRLINE3, ServiceConstants.EMPTY_STRING));
			formDataGroupList.add( createFormDataGroup(FormGroupsConstants.TMPLAT_DEARPOCFULLNAME, ServiceConstants.EMPTY_STRING));
		}
		String pocFullName =  TypeConvUtil.getNameFirstMiddleInitialLast(
				crpDto.getCentralRegistryCheckDto().getNmFirst(), ServiceConstants.EMPTY_STRING
				, crpDto.getCentralRegistryCheckDto().getNmLast(), false);
		/** artf283638, artf284014 preprocess line 1 of the two or three lines of address from centralregistrycheckdto */
		StringBuilder threeLineAddress = new StringBuilder();
		threeLineAddress.append( /** initialize the first line of the address if null, as string of (editable)spaces */
				TypeConvUtil.isNullOrEmpty(crpDto.getCentralRegistryCheckDto().getAddrStLn1())
				? TESTENVMESSAGE_WHITESPACE
				: crpDto.getCentralRegistryCheckDto().getAddrStLn1());
		bookmarkDtoList.addAll(Arrays.asList(
				createBookmark(BookmarkConstants.POCFULLNAME, pocFullName)
				, createBookmark(BookmarkConstants.RESOURCEADDRLINE3,
						TypeConvUtil.isNullOrEmpty( crpDto.getCentralRegistryCheckDto().getAddrStLn2())
						? threeLineAddress /** if line2 is blank, just newline and line 3 */
								.append( ServiceConstants.EMPTY_LINE)
								.append( getAddrLine3(crpDto.getCentralRegistryCheckDto())).toString()
						: threeLineAddress /** ...append a newline and line2 */
								.append( ServiceConstants.EMPTY_LINE)
								.append(  crpDto.getCentralRegistryCheckDto().getAddrStLn2())
								.append( ServiceConstants.EMPTY_LINE)
								.append( getAddrLine3(crpDto.getCentralRegistryCheckDto())).toString())
				, createBookmark(BookmarkConstants.BGCID, crpDto.getCentralRegistryCheckDto().getIdRequest())
				, createBookmark(BookmarkConstants.DEARPOC, ServiceConstants.DEAR_GREETING_TEXT)
				, createBookmark(BookmarkConstants.DEARPOCFULLNAME,
						new StringBuilder(pocFullName).append(ServiceConstants.COMMA).toString())
				, createBookmark(BookmarkConstants.BATCHDATE, TypeConvUtil.emailFormDateFormat(crpDto.getCentralRegistryCheckDto().getDtSubmitted()))
				, createBookmark(BookmarkConstants.NAME, pocFullName)
				, createBookmark(BookmarkConstants.DOB, TypeConvUtil.formDateFormat(crpDto.getCentralRegistryCheckDto().getDtPersonBirth()))
				, createBookmark(BookmarkConstants.ALTERNATENAME, getAlternateNames(crpDto.getCrpPersonNameList()))
				, createBookmark(BookmarkConstants.CERTIFIEDMAILNUM, crpDto.getCrpRecordNotifAndDetailsDto().getIdMailTracking())
		));
	}

	/**
	 * populate body section.
	 */
	private void populateBodySection(CrpRecordNotifFormsDto crpDto, List<BookmarkDto> bookmarkDtoList,
			List<FormDataGroupDto> formDataGroupList)  {
		// enable txtBodyOfLetter input based on status
		if (ServiceConstants.TRUE.equals(crpDto.getContentEditableStatus())) {
			formDataGroupList.add( createFormDataGroup(FormGroupsConstants.TMPLAT_BODYOFLETTER, ServiceConstants.EMPTY_STRING));
		}
		// populate the default text for the txtBodyOfLetter input field. Works for acrqnot also.
		bookmarkDtoList.add( createBookmark(BookmarkConstants.BODYOFLETTER, BODYOFLETTER_LTRHNOT));
		// display the findings table
		for ( PublicCentralRegistryDto findingsDto : crpDto.getCrpBatchResultsList()) {
			FormDataGroupDto findingsGroup = createFormDataGroup(
					FormGroupsConstants.TMPLAT_FINDING, ServiceConstants.EMPTY_STRING);
			formDataGroupList.add(findingsGroup);
			findingsGroup.setBookmarkDtoList( Arrays.asList(
					createBookmark(BookmarkConstants.FINDING_DATE, TypeConvUtil.formDateFormat(findingsDto.getDtStageClosed()))
					, createBookmarkWithCodesTable(BookmarkConstants.FINDING_PROGRAM, findingsDto.getCdStageProgram(), CodesConstant.CPGRMSFM)
					, createBookmarkWithCodesTable(BookmarkConstants.FINDING_ROLE, findingsDto.getCdStagePersRole(), CodesConstant.CROLEALL)
					, createBookmarkWithCodesTable(BookmarkConstants.FINDING_ALLEG, findingsDto.getCdAllegType(),
							ServiceConstants.CRP_FINDINGS.contains(findingsDto.getCdAllegType())
							? CodesConstant.INCRPALG : CodesConstant.INCPSALG)
					, createBookmark(BookmarkConstants.FINDING_CASE, findingsDto.getIdCase())
			));
		} /** if no findings, display empty */
		if (ObjectUtils.isEmpty(crpDto.getCrpBatchResultsList())) {
			FormDataGroupDto findingsGroup = createFormDataGroup(
					FormGroupsConstants.TMPLAT_FINDING, ServiceConstants.EMPTY_STRING);
			formDataGroupList.add(findingsGroup);
			findingsGroup.setBookmarkDtoList( Arrays.asList(
					createBookmark(BookmarkConstants.FINDING_DATE, HYPHEN)
					, createBookmark(BookmarkConstants.FINDING_PROGRAM, HYPHEN)
					, createBookmark(BookmarkConstants.FINDING_ROLE, HYPHEN)
					, createBookmark(BookmarkConstants.FINDING_ALLEG, NO_FINDING)
					, createBookmark(BookmarkConstants.FINDING_CASE, HYPHEN)
			));
		}
		// arifnot , soahnot info links, other body bookmarks
		StringBuilder hyperlinkText = getUploadLink(crpDto);
		bookmarkDtoList.addAll(Arrays.asList(
				createBookmark(BookmarkConstants.FORM2234LINK, ServiceConstants.ADMIN_RVW_REQ)
				, createBookmark(BookmarkConstants.DFPSRMGLINK, ServiceConstants.DFPS_RMG_LINK)

				, createBookmark(BookmarkConstants.HYPERLINK, QUOTE +hyperlinkText+QUOTE)
				, createBookmark(BookmarkConstants.HYPERLINKTEXTACRQ, hyperlinkText)
				, createBookmark(BookmarkConstants.FORM8831LINK, ServiceConstants.DFPS_TX_CD_8831)
				, createBookmark(BookmarkConstants.FORM8855LINK, ServiceConstants.DFPS_TX_CD_8855)
				, createBookmark(BookmarkConstants.TWOWEEKSDATE, TypeConvUtil.emailFormDateFormat(
						new Date(new Date().getTime()+14*24*60*60*1000)))
		));
	}

	private StringBuilder getUploadLink(CrpRecordNotifFormsDto crpDto) {
		String envName = System.getProperty(ENVIRONMENT);
		logger.info("The envName is " + envName);
		String uploadUrl = null;
		long requestId = crpDto != null && crpDto.getCentralRegistryCheckDto() != null
				&& !ObjectUtils.isEmpty(crpDto.getCentralRegistryCheckDto().getIdRequest())
				? crpDto.getCentralRegistryCheckDto().getIdRequest() : 0L;
		String encodedRequestId = new String(Base64.encodeBase64(Long.toString(requestId).getBytes()));

		if (PROD.equalsIgnoreCase(envName)) {
			uploadUrl = UPLOAD_URL_PROD;
		} else if (DEVL.equalsIgnoreCase(envName)) {
			uploadUrl = UPLOAD_URL_DEVL;
		} else if (TEST.equalsIgnoreCase(envName)) {
			uploadUrl = UPLOAD_URL_TEST;
		} else if (UAT.equalsIgnoreCase(envName)) {
			uploadUrl = UPLOAD_URL_QA;
		} else if (VISTA.equalsIgnoreCase(envName)) {
			uploadUrl = UPLOAD_URL_VISTA;
		} else {
			uploadUrl = UPLOAD_URL_DEFAULT;
		}

		return new StringBuilder().append(uploadUrl)
				.append(encodedRequestId);
	}


	/**
	 * populate closing section.
	 */
	private void populateClosingSection(CrpRecordNotifFormsDto crpDto, List<BookmarkDto> bookmarkDtoList,
										List<FormDataGroupDto> formDataGroupList) {
		bookmarkDtoList.addAll(Arrays.asList(
				createBookmark(BookmarkConstants.SINCERELY, BookmarkConstants.SINCERELYTEXT)
				, createBookmark(BookmarkConstants.SENDERFULLNAME, crpDto.getEmployeePersPhNameDto().getNmNameFirst()
						+ SPACE_STRING + crpDto.getEmployeePersPhNameDto().getNmNameLast())
				, createBookmark(BookmarkConstants.SENDERPHONE, TypeConvUtil.getPhoneWithFormat(
						crpDto.getEmployeePersPhNameDto().getNbrPhone(), crpDto.getEmployeePersPhNameDto().getNbrPhoneExtension()))
				, createBookmark(BookmarkConstants.FOOTER, ServiceConstants.DFPSFOOTERTEXT)
		));
	}
	/**
	 * populate email section.
	 */
	private void populateEmailSection(CrpRecordNotifFormsDto crpDto, List<BookmarkDto> bookmarkDtoList,
									  List<FormDataGroupDto> formDataGroupList) {
		boolean singleUser =  ServiceConstants.Y.equals(crpDto.getCentralRegistryCheckDto().getIndSingleUser());
		if (singleUser) {
			FormDataGroupDto emailSingleUserGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_EMAILSINGLEUSER,ServiceConstants.EMPTY_STRING);
			formDataGroupList.add(emailSingleUserGroup);
			emailSingleUserGroup.setBookmarkDtoList(Arrays.asList(
					createBookmark(BookmarkConstants.STRIPTOLABEL, BookmarkConstants.STRIPTOLABELTEXT)
					, createBookmark(BookmarkConstants.EMAILTO, crpDto.getAbcsRecordsCheckDto().getRecipientEmail())
			));

		}
		else { // TODO for release2
			FormDataGroupDto orgUserGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_EMAILORGUSER, ServiceConstants.EMPTY_STRING);
			formDataGroupList.add(orgUserGroup);
			orgUserGroup.setBookmarkDtoList(Arrays.asList(
					createBookmark(BookmarkConstants.STRIPTOLABEL, BookmarkConstants.STRIPTOLABELTEXT)
					, createBookmark(BookmarkConstants.EMAILTO, crpDto.getAbcsRecordsCheckDto().getRecipientEmail())
					, createBookmark(BookmarkConstants.STRIPCCLABEL, ServiceConstants.STRIPCCLABELTEXT)
					, createBookmark(BookmarkConstants.EMAILCC
							, crpDto.getCrpRecordNotifAndDetailsDto().getCdNotifType().equalsIgnoreCase(CodesConstant.CRPNOTTY_ACRQNOT)
									? ServiceConstants.EMPTY_STRING
									: crpDto.getCentralRegistryCheckDto().getSubjectEmail())
			));
		}
		bookmarkDtoList.addAll(Arrays.asList(createBookmark(BookmarkConstants.STRIPFROMLABEL, BookmarkConstants.STRIPFROMLABELTEXT) /** TODO this text sh/be ServiceConstant but is used elsewhere */
				, createBookmark(BookmarkConstants.EMAILFROM, crpDto.getAbcsRecordsCheckDto().getTxtSndrEmail())
				, createBookmark(BookmarkConstants.STRIPSUBJECTLABEL, BookmarkConstants.STRIPSUBJECTLABELTEXT) /** TODO this too sh/be ServiceConstant */
				, createBookmark(BookmarkConstants.EMAILSUBJECT, getSubjectLine(crpDto))));
		BookmarkDto bookmarkRcpntIdDto = createBookmark(BookmarkConstants.RECIPIENTID,
				crpDto.getCentralRegistryCheckDto().getIdRequest());
		bookmarkDtoList.add(bookmarkRcpntIdDto);
	}

	private String getSubjectLine(CrpRecordNotifFormsDto crpDto) {
		String subjectLine = ServiceConstants.EMPTY_STRING;
		StringBuilder subject = new StringBuilder();
		/** artf284015 systest - org user will display the same subject line as single user. */
		subjectLine = subject.append(ServiceConstants.EMAILSUBJECTSINGLEUSERTEXT).toString()
				.replaceAll(ServiceConstants.ID_REQUEST
						, String.valueOf(crpDto.getCentralRegistryCheckDto().getIdRequest()));
		return subjectLine;
	}

	private static final String TESTENVMESSAGE_WHITESPACE = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"; // for actionClass - white space search sequence


	private static String getAddrLine3(CentralRegistryCheckDto dto) {
		String city = dto.getAddrCity();
		String state = dto.getCdAddrState();
		String zip = dto.getAddrZip();
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

	/**
	 *
 	 */
	public static String getAlternateNames(List<CrpPersonNameDto> crpPersonNameDtoList) {
		String crpNameListString = ServiceConstants.EMPTY_STRING;
		StringBuilder sb = new StringBuilder();
		for (CrpPersonNameDto crpNameDto : crpPersonNameDtoList) {
			String name = TypeConvUtil.getNameFirstMiddleInitialLast(crpNameDto.getNameFirst()
					, crpNameDto.getNameMiddle(), crpNameDto.getNameLast(), false);
			if (!ServiceConstants.LT_UNKNOWN.equals(name)) {
				sb.append(ServiceConstants.EMPTY_LINE).append(name);
			}
		}
		crpNameListString = sb.toString();
		return crpNameListString;
	}

	private static final String BODYOFLETTER_LTRHNOT = new StringBuilder().append("[INSERT BODY OF LETTER HERE]")
			.toString();


}
