package us.tx.state.dfps.service.forms.util;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.populateletter.dto.CaseInfoDto;
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
import us.tx.state.dfps.service.investigation.dto.InvstRestraintDto;
import us.tx.state.dfps.service.investigation.dto.LicensingInvstSumDto;
import us.tx.state.dfps.service.person.dto.AllegationWithVicDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<Prefill
 * data class for service FacilityInvSummaryService/#cinv68s> Mar 27, 2018-
 * 11:45:30 AM © 2017 Texas Department of Family and Protective Services
 */
@Component
public class LicensingInvSummaryPrefillData extends DocumentServiceUtil {

	private static final Logger logger = Logger.getLogger(LicensingInvSummaryPrefillData.class);
	@Override
	public PreFillDataServiceDto returnPrefillData(Object parentDtoobj) {
		LicensingInvstSumDto licInvDto = (LicensingInvstSumDto) parentDtoobj;
		List<FormDataGroupDto> formDataGroupList = new ArrayList<FormDataGroupDto>();
		// cfiv1001
		if (!ObjectUtils.isEmpty(licInvDto.getCaseInfoDtoList())) {
			for (CaseInfoDto caseInfo : licInvDto.getCaseInfoDtoList()) {
				if ((ServiceConstants.ALL_VICT).equals(caseInfo.getIndPersCancelHist())) {
					FormDataGroupDto parentGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_VICTIM,
							FormConstants.EMPTY_STRING);
					List<BookmarkDto> bookmarkRrlList = new ArrayList<BookmarkDto>();
					List<FormDataGroupDto> groupDtoList = new ArrayList<FormDataGroupDto>();
					BookmarkDto dobApprox = createBookmark(BookmarkConstants.VICTIM_DOB_APPROX,
							caseInfo.getIndPersonDobApprox());
					BookmarkDto personSex = createBookmark(BookmarkConstants.VICTIM_SEX, caseInfo.getCdPersonSex());
					BookmarkDto dtPersonbirth = createBookmark(BookmarkConstants.VICTIM_DOB,
							DateUtils.stringDt(caseInfo.getDtPersonBirth()));
					BookmarkDto dtPersonDeath = createBookmark(BookmarkConstants.VICTIM_DOD,
							DateUtils.stringDt(caseInfo.getDtPersonDeath()));
					BookmarkDto addZip = createBookmark(BookmarkConstants.ADDR_ZIP, caseInfo.getAddrPersonAddrZip());
					BookmarkDto victimAge = createBookmark(BookmarkConstants.VICTIM_AGE, caseInfo.getNbrPersonAge());
					BookmarkDto phone = createBookmark(BookmarkConstants.PHONE_NUMBER,
							TypeConvUtil.formatPhone(caseInfo.getNbrPersonPhone()));
					BookmarkDto phoneExtension = createBookmark(BookmarkConstants.PHONE_NUM_EXTENSION,
							caseInfo.getNbrPersonPhoneExtension());
					BookmarkDto city = createBookmark(BookmarkConstants.ADDR_CITY, caseInfo.getAddrPersonAddrCity());
					BookmarkDto addrAttn = createBookmark(BookmarkConstants.ADDR_ATTN,
							caseInfo.getAddrPersonAddrAttn());
					BookmarkDto ln1 = createBookmark(BookmarkConstants.ADDR_LN_1, caseInfo.getAddrPersAddrStLn1());
					BookmarkDto ln2 = createBookmark(BookmarkConstants.ADDR_LN_2, caseInfo.getAddrPersAddrStLn2());
					BookmarkDto county = createBookmarkWithCodesTable(BookmarkConstants.ADDR_COUNTY,
							caseInfo.getCdPersonAddrCounty(), CodesConstant.CCOUNT);
					BookmarkDto addrState = createBookmark(BookmarkConstants.ADDR_STATE,
							caseInfo.getCdPersonAddrState());
					BookmarkDto nameSuf = createBookmarkWithCodesTable(BookmarkConstants.VICTIM_NAME_SUFFIX,
							caseInfo.getCdNameSuffix(), CodesConstant.CSUFFIX2);
					BookmarkDto addrType = createBookmarkWithCodesTable(BookmarkConstants.ADDR_TYPE,
							caseInfo.getCdPersAddrLinkType(), CodesConstant.CADDRTYP);
					BookmarkDto victimRsn = createBookmarkWithCodesTable(BookmarkConstants.VICTIM_RSN,
							caseInfo.getCdPersonDeath(), CodesConstant.CRSNFDTH);
					BookmarkDto victimEth = createBookmarkWithCodesTable(BookmarkConstants.VICTIM_ETHNCTY,
							caseInfo.getCdPersonEthnicGroup(), CodesConstant.CETHNIC);
					BookmarkDto victimLang = createBookmarkWithCodesTable(BookmarkConstants.VICTIM_LANG,
							caseInfo.getCdPersonLanguage(), CodesConstant.CLANG);
					BookmarkDto maritalStatus = createBookmarkWithCodesTable(BookmarkConstants.VICTIM_MARITAL,
							caseInfo.getCdPersonMaritalStatus(), CodesConstant.CMARSTAT);
					BookmarkDto phoneType = createBookmarkWithCodesTable(BookmarkConstants.PHONE_TYPE,
							caseInfo.getCdPersonPhoneType(), CodesConstant.CPHNTYP);
					BookmarkDto cdStagePers = createBookmarkWithCodesTable(BookmarkConstants.VICTIM_RELTNSP,
							caseInfo.getCdStagePersRelInt(), CodesConstant.CRELVICT);
					BookmarkDto cdStageRole = createBookmarkWithCodesTable(BookmarkConstants.VICTIM_ROLE,
							caseInfo.getCdStagePersRole(), CodesConstant.CINVROLE);
					BookmarkDto num = createBookmark(BookmarkConstants.VICTIM_SSN, caseInfo.getPersonIdNumber());
					BookmarkDto nameFirst = createBookmark(BookmarkConstants.VICTIM_NAME_FIRST,
							caseInfo.getNmNameFirst());
					BookmarkDto last = createBookmark(BookmarkConstants.VICTIM_NAME_LAST, caseInfo.getNmNameLast());
					BookmarkDto middel = createBookmark(BookmarkConstants.VICTIM_NAME_MIDDLE,
							caseInfo.getNmNameMiddle());
					BookmarkDto addrNotes = createBookmark(BookmarkConstants.ADDR_NOTES, caseInfo.getPersAddrCmnts());
					BookmarkDto notes = createBookmark(BookmarkConstants.PHONE_NOTES,
							caseInfo.getTxtPersonPhoneComments());
					BookmarkDto victimNotes = createBookmark(BookmarkConstants.VICTIM_NOTES,
							caseInfo.getStagePersNotes());

					// sub group cfzco00
					if ((ServiceConstants.DOLLAR_SIGN).equals(caseInfo.getCdNameSuffix())) {
						FormDataGroupDto subGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_COMMA,
								FormGroupsConstants.TMPLAT_VICTIM);
						groupDtoList.add(subGroupDto);
					}

					bookmarkRrlList.add(dobApprox);
					bookmarkRrlList.add(personSex);
					bookmarkRrlList.add(dtPersonbirth);
					bookmarkRrlList.add(dtPersonDeath);
					bookmarkRrlList.add(addZip);
					bookmarkRrlList.add(victimAge);
					bookmarkRrlList.add(phone);
					bookmarkRrlList.add(phoneExtension);
					bookmarkRrlList.add(city);
					bookmarkRrlList.add(addrAttn);
					bookmarkRrlList.add(ln1);
					bookmarkRrlList.add(ln2);
					bookmarkRrlList.add(county);
					bookmarkRrlList.add(addrState);
					bookmarkRrlList.add(nameSuf);
					bookmarkRrlList.add(addrType);
					bookmarkRrlList.add(victimRsn);
					bookmarkRrlList.add(victimEth);
					bookmarkRrlList.add(victimLang);
					bookmarkRrlList.add(maritalStatus);
					bookmarkRrlList.add(phoneType);
					bookmarkRrlList.add(cdStagePers);
					bookmarkRrlList.add(cdStageRole);
					bookmarkRrlList.add(num);
					bookmarkRrlList.add(nameFirst);
					bookmarkRrlList.add(last);
					bookmarkRrlList.add(middel);
					bookmarkRrlList.add(addrNotes);
					bookmarkRrlList.add(notes);
					bookmarkRrlList.add(victimNotes);

					parentGroupDto.setBookmarkDtoList(bookmarkRrlList);
					parentGroupDto.setFormDataGroupList(groupDtoList);
					formDataGroupList.add(parentGroupDto);
				}
			}
		}
		// cfiv1002
		if (!ObjectUtils.isEmpty(licInvDto.getCaseInfoDtoList())) {
			for (CaseInfoDto caseInfo : licInvDto.getCaseInfoDtoList()) {
				if ((ServiceConstants.PERP).equals(caseInfo.getIndPersCancelHist())) {
					FormDataGroupDto parentGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_ALLEG_PERP,
							FormConstants.EMPTY_STRING);
					List<BookmarkDto> bookmarkRrlList = new ArrayList<BookmarkDto>();
					List<FormDataGroupDto> groupDtoList = new ArrayList<FormDataGroupDto>();
					BookmarkDto dobApprox = createBookmark(BookmarkConstants.AP_DOB_APPROX,
							caseInfo.getIndPersonDobApprox());
					BookmarkDto personSex = createBookmarkWithCodesTable(BookmarkConstants.AP_SEX,
							caseInfo.getCdPersonSex(), CodesConstant.CSEX);
					BookmarkDto dtPersonbirth = createBookmark(BookmarkConstants.AP_DOB,
							DateUtils.stringDt(caseInfo.getDtPersonBirth()));
					BookmarkDto dtPersonDeath = createBookmark(BookmarkConstants.AP_DOD,
							DateUtils.stringDt(caseInfo.getDtPersonDeath()));
					BookmarkDto addZip = createBookmark(BookmarkConstants.ADDR_ZIP, caseInfo.getAddrPersonAddrZip());
					BookmarkDto victimAge = createBookmark(BookmarkConstants.AP_AGE, caseInfo.getNbrPersonAge());
					BookmarkDto phone = createBookmark(BookmarkConstants.PHONE_NUMBER,
							TypeConvUtil.formatPhone(caseInfo.getNbrPersonPhone()));
					BookmarkDto phoneExtension = createBookmark(BookmarkConstants.PHONE_NUM_EXTENSION,
							caseInfo.getNbrPersonPhoneExtension());
					BookmarkDto city = createBookmark(BookmarkConstants.ADDR_CITY, caseInfo.getAddrPersonAddrCity());
					BookmarkDto addrAttn = createBookmark(BookmarkConstants.ADDR_ATTN,
							caseInfo.getAddrPersonAddrAttn());
					BookmarkDto ln1 = createBookmark(BookmarkConstants.ADDR_LN_1, caseInfo.getAddrPersAddrStLn1());
					BookmarkDto ln2 = createBookmark(BookmarkConstants.ADDR_LN_2, caseInfo.getAddrPersAddrStLn2());
					BookmarkDto county = createBookmarkWithCodesTable(BookmarkConstants.ADDR_COUNTY,
							caseInfo.getCdPersonAddrCounty(), CodesConstant.CCOUNT);
					BookmarkDto nameSuf = createBookmarkWithCodesTable(BookmarkConstants.AP_NAME_SUFFIX,
							caseInfo.getCdNameSuffix(), CodesConstant.CSUFFIX2);
					BookmarkDto addrType = createBookmarkWithCodesTable(BookmarkConstants.ADDR_TYPE,
							caseInfo.getCdPersAddrLinkType(), CodesConstant.CADDRTYP);
					BookmarkDto victimRsn = createBookmarkWithCodesTable(BookmarkConstants.AP_RSN,
							caseInfo.getCdPersonDeath(), CodesConstant.CRSNFDTH);
					BookmarkDto victimEth = createBookmarkWithCodesTable(BookmarkConstants.AP_ETHNCTY,
							caseInfo.getCdPersonEthnicGroup(), CodesConstant.CETHNIC);
					BookmarkDto victimLang = createBookmarkWithCodesTable(BookmarkConstants.AP_LANG,
							caseInfo.getCdPersonLanguage(), CodesConstant.CLANG);
					BookmarkDto maritalStatus = createBookmarkWithCodesTable(BookmarkConstants.AP_MARITAL,
							caseInfo.getCdPersonMaritalStatus(), CodesConstant.CMARSTAT);
					BookmarkDto phoneType = createBookmarkWithCodesTable(BookmarkConstants.PHONE_TYPE,
							caseInfo.getCdPersonPhoneType(), CodesConstant.CPHNTYP);
					BookmarkDto cdStagePers = createBookmarkWithCodesTable(BookmarkConstants.AP_RELTNSP,
							caseInfo.getCdStagePersRelInt(), CodesConstant.CRELVICT);
					BookmarkDto cdStageRole = createBookmarkWithCodesTable(BookmarkConstants.AP_ROLE,
							caseInfo.getCdStagePersRole(), CodesConstant.CINVROLE);
					BookmarkDto num = createBookmark(BookmarkConstants.AP_SSN, caseInfo.getPersonIdNumber());
					BookmarkDto nameFirst = createBookmark(BookmarkConstants.AP_NAME_FIRST, caseInfo.getNmNameFirst());
					BookmarkDto last = createBookmark(BookmarkConstants.AP_NAME_LAST, caseInfo.getNmNameLast());
					BookmarkDto middel = createBookmark(BookmarkConstants.AP_NAME_MIDDLE, caseInfo.getNmNameMiddle());
					BookmarkDto addrNotes = createBookmark(BookmarkConstants.ADDR_NOTES, caseInfo.getPersAddrCmnts());
					BookmarkDto notes = createBookmark(BookmarkConstants.PHONE_NOTES,
							caseInfo.getTxtPersonPhoneComments());
					BookmarkDto victimNotes = createBookmark(BookmarkConstants.AP_NOTES, caseInfo.getStagePersNotes());

					// SUB GROUP CFZCO00
					if ((ServiceConstants.DOLLAR_SIGN).equals(caseInfo.getCdNameSuffix())) {
						FormDataGroupDto subGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_COMMA,
								FormGroupsConstants.TMPLAT_ALLEG_PERP);
						groupDtoList.add(subGroupDto);
					}

					bookmarkRrlList.add(dobApprox);
					bookmarkRrlList.add(personSex);
					bookmarkRrlList.add(dtPersonbirth);
					bookmarkRrlList.add(dtPersonDeath);
					bookmarkRrlList.add(addZip);
					bookmarkRrlList.add(victimAge);
					bookmarkRrlList.add(phone);
					bookmarkRrlList.add(phoneExtension);
					bookmarkRrlList.add(city);
					bookmarkRrlList.add(addrAttn);
					bookmarkRrlList.add(ln1);
					bookmarkRrlList.add(ln2);
					bookmarkRrlList.add(county);
					bookmarkRrlList.add(nameSuf);
					bookmarkRrlList.add(addrType);
					bookmarkRrlList.add(victimRsn);
					bookmarkRrlList.add(victimEth);
					bookmarkRrlList.add(victimLang);
					bookmarkRrlList.add(maritalStatus);
					bookmarkRrlList.add(phoneType);
					bookmarkRrlList.add(cdStagePers);
					bookmarkRrlList.add(cdStageRole);
					bookmarkRrlList.add(num);
					bookmarkRrlList.add(nameFirst);
					bookmarkRrlList.add(last);
					bookmarkRrlList.add(middel);
					bookmarkRrlList.add(addrNotes);
					bookmarkRrlList.add(notes);
					bookmarkRrlList.add(victimNotes);

					parentGroupDto.setBookmarkDtoList(bookmarkRrlList);
					parentGroupDto.setFormDataGroupList(groupDtoList);
					formDataGroupList.add(parentGroupDto);
				}
			}
		}
		// cfiv1003
		if (!ObjectUtils.isEmpty(licInvDto.getCaseInfoDtoList())) {
			for (CaseInfoDto caseInfo : licInvDto.getCaseInfoDtoList()) {
				if ((ServiceConstants.OTHER_O).equals(caseInfo.getIndPersCancelHist())) {
					FormDataGroupDto parentGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_PRINC_OTHER,
							FormConstants.EMPTY_STRING);
					List<BookmarkDto> bookmarkRrlList = new ArrayList<BookmarkDto>();
					List<FormDataGroupDto> groupDtoList = new ArrayList<FormDataGroupDto>();
					BookmarkDto dobApprox = createBookmark(BookmarkConstants.PRINC_DOB_APPROX,
							caseInfo.getIndPersonDobApprox());
					BookmarkDto personSex = createBookmarkWithCodesTable(BookmarkConstants.PRINC_SEX,
							caseInfo.getCdPersonSex(), CodesConstant.CSEX);
					BookmarkDto dtPersonbirth = createBookmark(BookmarkConstants.PRINC_DOB,
							DateUtils.stringDt(caseInfo.getDtPersonBirth()));
					BookmarkDto dtPersonDeath = createBookmark(BookmarkConstants.PRINC_DOD,
							DateUtils.stringDt(caseInfo.getDtPersonDeath()));
					BookmarkDto addZip = createBookmark(BookmarkConstants.ADDR_ZIP, caseInfo.getAddrPersonAddrZip());
					BookmarkDto victimAge = createBookmark(BookmarkConstants.PRINC_AGE, caseInfo.getNbrPersonAge());
					BookmarkDto phone = createBookmark(BookmarkConstants.PHONE_NUMBER,
							TypeConvUtil.formatPhone(caseInfo.getNbrPersonPhone()));
					BookmarkDto phoneExtension = createBookmark(BookmarkConstants.PHONE_NUM_EXTENSION,
							caseInfo.getNbrPersonPhoneExtension());
					BookmarkDto city = createBookmark(BookmarkConstants.ADDR_CITY, caseInfo.getAddrPersonAddrCity());
					BookmarkDto addrAttn = createBookmark(BookmarkConstants.ADDR_ATTN,
							caseInfo.getAddrPersonAddrAttn());
					BookmarkDto ln1 = createBookmark(BookmarkConstants.ADDR_LN_1, caseInfo.getAddrPersAddrStLn1());
					BookmarkDto ln2 = createBookmark(BookmarkConstants.ADDR_LN_2, caseInfo.getAddrPersAddrStLn2());
					BookmarkDto county = createBookmarkWithCodesTable(BookmarkConstants.ADDR_COUNTY,
							caseInfo.getCdPersonAddrCounty(), CodesConstant.CCOUNT);
					BookmarkDto addrState = createBookmark(BookmarkConstants.ADDR_STATE,
							caseInfo.getCdPersonAddrState());
					BookmarkDto nameSuf = createBookmarkWithCodesTable(BookmarkConstants.PRINC_NAME_SUFFIX,
							caseInfo.getCdNameSuffix(), CodesConstant.CSUFFIX2);
					BookmarkDto addrType = createBookmarkWithCodesTable(BookmarkConstants.ADDR_TYPE,
							caseInfo.getCdPersAddrLinkType(), CodesConstant.CADDRTYP);
					BookmarkDto victimRsn = createBookmarkWithCodesTable(BookmarkConstants.PRINC_ETHNCTY,
							caseInfo.getCdPersonEthnicGroup(), CodesConstant.CETHNIC);
					BookmarkDto victimLang = createBookmarkWithCodesTable(BookmarkConstants.PRINC_LANG,
							caseInfo.getCdPersonLanguage(), CodesConstant.CLANG);
					BookmarkDto maritalStatus = createBookmarkWithCodesTable(BookmarkConstants.PRINC_MARITAL,
							caseInfo.getCdPersonMaritalStatus(), CodesConstant.CMARSTAT);
					BookmarkDto phoneType = createBookmarkWithCodesTable(BookmarkConstants.PHONE_TYPE,
							caseInfo.getCdPersonPhoneType(), CodesConstant.CPHNTYP);
					BookmarkDto cdStagePers = createBookmarkWithCodesTable(BookmarkConstants.PRINC_RELTNSP,
							caseInfo.getCdStagePersRelInt(), CodesConstant.CRELVICT);
					BookmarkDto cdStageRole = createBookmarkWithCodesTable(BookmarkConstants.PRINC_ROLE,
							caseInfo.getCdStagePersRole(), CodesConstant.CINVROLE);
					BookmarkDto num = createBookmark(BookmarkConstants.PRINC_SSN, caseInfo.getPersonIdNumber());
					BookmarkDto nameFirst = createBookmark(BookmarkConstants.PRINC_NAME_FIRST,
							caseInfo.getNmNameFirst());
					BookmarkDto last = createBookmark(BookmarkConstants.PRINC_NAME_LAST, caseInfo.getNmNameLast());
					BookmarkDto middel = createBookmark(BookmarkConstants.PRINC_NAME_MIDDLE,
							caseInfo.getNmNameMiddle());
					BookmarkDto addrNotes = createBookmark(BookmarkConstants.ADDR_NOTES, caseInfo.getPersAddrCmnts());
					BookmarkDto notes = createBookmark(BookmarkConstants.PHONE_NOTES,
							caseInfo.getTxtPersonPhoneComments());
					BookmarkDto victimNotes = createBookmark(BookmarkConstants.PRINC_NOTES,
							caseInfo.getStagePersNotes());

					// SUB GROUP CFZCO00
					if ((ServiceConstants.DOLLAR_SIGN).equals(caseInfo.getCdNameSuffix())) {
						FormDataGroupDto subGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_COMMA,
								FormGroupsConstants.TMPLAT_PRINC_OTHER);
						groupDtoList.add(subGroupDto);
					}

					bookmarkRrlList.add(dobApprox);
					bookmarkRrlList.add(personSex);
					bookmarkRrlList.add(dtPersonbirth);
					bookmarkRrlList.add(dtPersonDeath);
					bookmarkRrlList.add(addZip);
					bookmarkRrlList.add(victimAge);
					bookmarkRrlList.add(phone);
					bookmarkRrlList.add(phoneExtension);
					bookmarkRrlList.add(city);
					bookmarkRrlList.add(addrAttn);
					bookmarkRrlList.add(ln1);
					bookmarkRrlList.add(ln2);
					bookmarkRrlList.add(county);
					bookmarkRrlList.add(addrState);
					bookmarkRrlList.add(nameSuf);
					bookmarkRrlList.add(addrType);
					bookmarkRrlList.add(victimRsn);
					bookmarkRrlList.add(victimLang);
					bookmarkRrlList.add(maritalStatus);
					bookmarkRrlList.add(phoneType);
					bookmarkRrlList.add(cdStagePers);
					bookmarkRrlList.add(cdStageRole);
					bookmarkRrlList.add(num);
					bookmarkRrlList.add(nameFirst);
					bookmarkRrlList.add(last);
					bookmarkRrlList.add(middel);
					bookmarkRrlList.add(addrNotes);
					bookmarkRrlList.add(notes);
					bookmarkRrlList.add(victimNotes);

					parentGroupDto.setBookmarkDtoList(bookmarkRrlList);
					parentGroupDto.setFormDataGroupList(groupDtoList);
					formDataGroupList.add(parentGroupDto);
				}
			}
		}
		// cfiv1004
		if (!ObjectUtils.isEmpty(licInvDto.getCaseInfoDtoList())) {
			for (CaseInfoDto caseInfo : licInvDto.getCaseInfoDtoList()) {
				if ((ServiceConstants.STRING_IND_Y).equals(caseInfo.getIndStagePersReporter())) {
					FormDataGroupDto parentGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_REPORTER_1,
							FormConstants.EMPTY_STRING);
					List<BookmarkDto> bookmarkRrlList = new ArrayList<BookmarkDto>();
					List<FormDataGroupDto> groupDtoList = new ArrayList<FormDataGroupDto>();

					BookmarkDto addZip = createBookmark(BookmarkConstants.ADDR_ZIP, caseInfo.getAddrPersonAddrZip());
					BookmarkDto phone = createBookmark(BookmarkConstants.PHONE_NUMBER,
							TypeConvUtil.formatPhone(caseInfo.getNbrPersonPhone()));
					BookmarkDto phoneExtension = createBookmark(BookmarkConstants.PHONE_NUM_EXTENSION,
							caseInfo.getNbrPersonPhoneExtension());
					BookmarkDto city = createBookmark(BookmarkConstants.ADDR_CITY, caseInfo.getAddrPersonAddrCity());
					BookmarkDto addrAttn = createBookmark(BookmarkConstants.ADDR_ATTN,
							caseInfo.getAddrPersonAddrAttn());
					BookmarkDto ln1 = createBookmark(BookmarkConstants.ADDR_LN_1, caseInfo.getAddrPersAddrStLn1());
					BookmarkDto ln2 = createBookmark(BookmarkConstants.ADDR_LN_2, caseInfo.getAddrPersAddrStLn2());
					BookmarkDto county = createBookmarkWithCodesTable(BookmarkConstants.ADDR_COUNTY,
							caseInfo.getCdPersonAddrCounty(), CodesConstant.CCOUNT);
					BookmarkDto addrState = createBookmark(BookmarkConstants.ADDR_STATE,
							caseInfo.getCdPersonAddrState());
					BookmarkDto nameSuf = createBookmarkWithCodesTable(BookmarkConstants.RPT_NAME_SUFFIX,
							caseInfo.getCdNameSuffix(), CodesConstant.CSUFFIX2);
					BookmarkDto addrType = createBookmarkWithCodesTable(BookmarkConstants.ADDR_TYPE,
							caseInfo.getCdPersAddrLinkType(), CodesConstant.CADDRTYP);
					BookmarkDto phoneType = createBookmarkWithCodesTable(BookmarkConstants.PHONE_TYPE,
							caseInfo.getCdPersonPhoneType(), CodesConstant.CPHNTYP);
					BookmarkDto cdStagePers = createBookmarkWithCodesTable(BookmarkConstants.RPT_RELTNSP,
							caseInfo.getCdStagePersRelInt(), CodesConstant.CRPTRINT);
					BookmarkDto nameFirst = createBookmark(BookmarkConstants.RPT_NAME_FIRST, caseInfo.getNmNameFirst());
					BookmarkDto last = createBookmark(BookmarkConstants.RPT_NAME_LAST, caseInfo.getNmNameLast());
					BookmarkDto middel = createBookmark(BookmarkConstants.RPT_NAME_MIDDLE, caseInfo.getNmNameMiddle());
					BookmarkDto addrNotes = createBookmark(BookmarkConstants.ADDR_NOTES, caseInfo.getPersAddrCmnts());
					BookmarkDto notes = createBookmark(BookmarkConstants.PHONE_NOTES,
							caseInfo.getTxtPersonPhoneComments());
					BookmarkDto victimNotes = createBookmark(BookmarkConstants.RPT_NOTES, caseInfo.getStagePersNotes());

					// SUB GROUP CFZCO00
					if ((ServiceConstants.DOLLAR_SIGN).equals(caseInfo.getCdNameSuffix())) {
						FormDataGroupDto subGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_COMMA,
								FormGroupsConstants.TMPLAT_REPORTER_1);
						groupDtoList.add(subGroupDto);
					}

					bookmarkRrlList.add(addZip);
					bookmarkRrlList.add(phone);
					bookmarkRrlList.add(phoneExtension);
					bookmarkRrlList.add(city);
					bookmarkRrlList.add(addrAttn);
					bookmarkRrlList.add(ln1);
					bookmarkRrlList.add(ln2);
					bookmarkRrlList.add(county);
					bookmarkRrlList.add(addrState);
					bookmarkRrlList.add(nameSuf);
					bookmarkRrlList.add(addrType);
					bookmarkRrlList.add(phoneType);
					bookmarkRrlList.add(cdStagePers);
					bookmarkRrlList.add(nameFirst);
					bookmarkRrlList.add(last);
					bookmarkRrlList.add(middel);
					bookmarkRrlList.add(addrNotes);
					bookmarkRrlList.add(notes);
					bookmarkRrlList.add(victimNotes);

					parentGroupDto.setBookmarkDtoList(bookmarkRrlList);
					parentGroupDto.setFormDataGroupList(groupDtoList);
					formDataGroupList.add(parentGroupDto);
				}
			}
		}

		// primary group cfzco00 _from employee
		if (!ObjectUtils.isEmpty(licInvDto.getEmployeePersPhNameDto())) {
			if ((ServiceConstants.DOLLAR_SIGN).equals(licInvDto.getEmployeePersPhNameDto().getCdNameSuffix())) {
				FormDataGroupDto parentGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_COMMA,
						FormConstants.EMPTY_STRING);
				formDataGroupList.add(parentGroupDto);
			}
		}
		// primary group cfzco00 _from employee supervisor
		if (!ObjectUtils.isEmpty(licInvDto.getEmployeePersPhNameDto())) {
			if ((ServiceConstants.DOLLAR_SIGN).equals(licInvDto.getEmployeeSuperInfo().getCdNameSuffix())) {
				FormDataGroupDto parentGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_COMMA_2,
						FormConstants.EMPTY_STRING);
				formDataGroupList.add(parentGroupDto);
			}
		}

		// primary group cfiv1006
		if (!ObjectUtils.isEmpty(licInvDto.getAllegationWithVicDtolist())) {
			for (AllegationWithVicDto allegDto : licInvDto.getAllegationWithVicDtolist()) {
				FormDataGroupDto parentGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_ALLEGATION,
						FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkRrlList = new ArrayList<BookmarkDto>();
				List<FormDataGroupDto> groupDtoList = new ArrayList<FormDataGroupDto>();
				BookmarkDto allegD = createBookmarkWithCodesTable(BookmarkConstants.ALLEG_DTL_DISP,
						allegDto.getaCdAllegDisposition(), CodesConstant.CCIVALDS);
				BookmarkDto dtl = createBookmarkWithCodesTable(BookmarkConstants.ALLEG_DTL_ALLEG,
						allegDto.getaCdAllegType(), CodesConstant.CABALTYP);
				BookmarkDto fatal = createBookmark(BookmarkConstants.ALLEG_DTL_FATALITY, allegDto.getaIndFatality());
				BookmarkDto ap = createBookmark(BookmarkConstants.ALLEG_DTL_AP, allegDto.getcNmPersonFull());
				BookmarkDto vic = createBookmark(BookmarkConstants.ALLEG_DTL_VICTIM, allegDto.getB_nmPersonFull());

				// sub group cfiv1107
				if (ObjectUtils.isEmpty(allegDto.getbDtPersonDeath())) {
					FormDataGroupDto subGroupDto = createFormDataGroup(
							FormGroupsConstants.TMPLAT_ALG_CHILD_FATALITY_WITHOUT_DOD,
							FormGroupsConstants.TMPLAT_ALLEGATION);
					groupDtoList.add(subGroupDto);
				}
				// sub group cfiv1108
				if (!ObjectUtils.isEmpty(allegDto.getbDtPersonDeath())) {
					FormDataGroupDto subGroupDto = createFormDataGroup(
							FormGroupsConstants.TMPLAT_ALG_CHILD_FATALITY_WITH_DOD,
							FormGroupsConstants.TMPLAT_ALLEGATION);
					List<BookmarkDto> bookmarkList = new ArrayList<BookmarkDto>();
					BookmarkDto fatality = createBookmark(BookmarkConstants.ALG_CHILD_FATALITY,
							allegDto.getaIndFatality());
					bookmarkList.add(fatality);
					subGroupDto.setBookmarkDtoList(bookmarkList);
					groupDtoList.add(subGroupDto);
				}
				bookmarkRrlList.add(allegD);
				bookmarkRrlList.add(dtl);
				bookmarkRrlList.add(fatal);
				bookmarkRrlList.add(ap);
				bookmarkRrlList.add(vic);
				parentGroupDto.setBookmarkDtoList(bookmarkRrlList);
				parentGroupDto.setFormDataGroupList(groupDtoList);
				formDataGroupList.add(parentGroupDto);
			}
		}

		// cfiv1005
		if (!ObjectUtils.isEmpty(licInvDto.getCaseInfoDtoCollateral())) {
			for (CaseInfoDto caseInfo : licInvDto.getCaseInfoDtoCollateral()) {
				if ((ServiceConstants.STRING_IND_N).equals(caseInfo.getIndStagePersReporter())) {
					FormDataGroupDto parentGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_COLLATERAL,
							FormConstants.EMPTY_STRING);
					List<BookmarkDto> bookmarkRrlList = new ArrayList<BookmarkDto>();
					List<FormDataGroupDto> groupDtoList = new ArrayList<FormDataGroupDto>();

					BookmarkDto personSex = createBookmarkWithCodesTable(BookmarkConstants.COL_SEX,
							caseInfo.getCdPersonSex(), CodesConstant.CSEX);
					BookmarkDto addZip = createBookmark(BookmarkConstants.ADDR_ZIP, caseInfo.getAddrPersonAddrZip());
					BookmarkDto phone = createBookmark(BookmarkConstants.PHONE_NUMBER, caseInfo.getNbrPersonPhone());
					BookmarkDto phoneExtension = createBookmark(BookmarkConstants.PHONE_NUM_EXTENSION,
							caseInfo.getNbrPersonPhoneExtension());
					BookmarkDto city = createBookmark(BookmarkConstants.ADDR_CITY, caseInfo.getAddrPersonAddrCity());
					BookmarkDto addrAttn = createBookmark(BookmarkConstants.ADDR_ATTN,
							caseInfo.getAddrPersonAddrAttn());
					BookmarkDto ln1 = createBookmark(BookmarkConstants.ADDR_LN_1, caseInfo.getAddrPersAddrStLn1());
					BookmarkDto ln2 = createBookmark(BookmarkConstants.ADDR_LN_2, caseInfo.getAddrPersAddrStLn2());
					BookmarkDto county = createBookmarkWithCodesTable(BookmarkConstants.ADDR_COUNTY,
							caseInfo.getCdPersonAddrCounty(), CodesConstant.CCOUNT);
					BookmarkDto addrState = createBookmark(BookmarkConstants.ADDR_STATE,
							caseInfo.getCdPersonAddrState());
					BookmarkDto nameSuf = createBookmarkWithCodesTable(BookmarkConstants.COL_NAME_SUFFIX,
							caseInfo.getCdNameSuffix(), CodesConstant.CSUFFIX2);
					BookmarkDto addrType = createBookmarkWithCodesTable(BookmarkConstants.ADDR_TYPE,
							caseInfo.getCdPersAddrLinkType(), CodesConstant.CADDRTYP);
					BookmarkDto phoneType = createBookmarkWithCodesTable(BookmarkConstants.PHONE_TYPE,
							caseInfo.getCdPersonPhoneType(), CodesConstant.CPHNTYP);
					BookmarkDto cdStagePers = createBookmarkWithCodesTable(BookmarkConstants.COL_RELTNSP,
							caseInfo.getCdStagePersRelInt(), CodesConstant.CRPTRINT);
					BookmarkDto nameFirst = createBookmark(BookmarkConstants.COL_NAME_FIRST, caseInfo.getNmNameFirst());
					BookmarkDto last = createBookmark(BookmarkConstants.COL_NAME_LAST, caseInfo.getNmNameLast());
					BookmarkDto middel = createBookmark(BookmarkConstants.COL_NAME_MIDDLE, caseInfo.getNmNameMiddle());
					BookmarkDto addrNotes = createBookmark(BookmarkConstants.ADDR_NOTES, caseInfo.getPersAddrCmnts());
					BookmarkDto notes = createBookmark(BookmarkConstants.PHONE_NOTES,
							caseInfo.getTxtPersonPhoneComments());
					BookmarkDto victimNotes = createBookmark(BookmarkConstants.COL_NOTES, caseInfo.getStagePersNotes());

					// SUB GROUP CFZCO00
					if ((ServiceConstants.DOLLAR_SIGN).equals(caseInfo.getCdNameSuffix())) {
						FormDataGroupDto subGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_COMMA,
								FormGroupsConstants.TMPLAT_COLLATERAL);
						groupDtoList.add(subGroupDto);
					}

					bookmarkRrlList.add(personSex);
					bookmarkRrlList.add(addZip);
					bookmarkRrlList.add(phone);
					bookmarkRrlList.add(phoneExtension);
					bookmarkRrlList.add(city);
					bookmarkRrlList.add(addrAttn);
					bookmarkRrlList.add(ln1);
					bookmarkRrlList.add(ln2);
					bookmarkRrlList.add(county);
					bookmarkRrlList.add(addrState);
					bookmarkRrlList.add(nameSuf);
					bookmarkRrlList.add(addrType);
					bookmarkRrlList.add(phoneType);
					bookmarkRrlList.add(cdStagePers);
					bookmarkRrlList.add(nameFirst);
					bookmarkRrlList.add(last);
					bookmarkRrlList.add(middel);
					bookmarkRrlList.add(addrNotes);
					bookmarkRrlList.add(notes);
					bookmarkRrlList.add(victimNotes);

					parentGroupDto.setBookmarkDtoList(bookmarkRrlList);
					parentGroupDto.setFormDataGroupList(groupDtoList);
					formDataGroupList.add(parentGroupDto);
				}
			}
		}

		// cfiv1007
		if (!ObjectUtils.isEmpty(licInvDto.getCaseInfoDtoCollateral())) {
			for (CaseInfoDto caseInfo2 : licInvDto.getCaseInfoDtoCollateral()) {
				if ((ServiceConstants.STRING_IND_Y).equals(caseInfo2.getIndStagePersReporter())) {
					FormDataGroupDto parentGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_REPORTER_2,
							FormConstants.EMPTY_STRING);
					List<BookmarkDto> bookmarkRrlList = new ArrayList<BookmarkDto>();
					List<FormDataGroupDto> groupDtoList = new ArrayList<FormDataGroupDto>();

					BookmarkDto addZip = createBookmark(BookmarkConstants.ADDR_ZIP, caseInfo2.getAddrPersonAddrZip());
					BookmarkDto phone = createBookmark(BookmarkConstants.PHONE_NUMBER,
							TypeConvUtil.formatPhone(caseInfo2.getNbrPersonPhone()));
					BookmarkDto phoneExtension = createBookmark(BookmarkConstants.PHONE_NUM_EXTENSION,
							caseInfo2.getNbrPersonPhoneExtension());
					BookmarkDto city = createBookmark(BookmarkConstants.ADDR_CITY, caseInfo2.getAddrPersonAddrCity());
					BookmarkDto addrAttn = createBookmark(BookmarkConstants.ADDR_ATTN,
							caseInfo2.getAddrPersonAddrAttn());
					BookmarkDto ln1 = createBookmark(BookmarkConstants.ADDR_LN_1, caseInfo2.getAddrPersAddrStLn1());
					BookmarkDto ln2 = createBookmark(BookmarkConstants.ADDR_LN_2, caseInfo2.getAddrPersAddrStLn2());
					BookmarkDto county = createBookmarkWithCodesTable(BookmarkConstants.ADDR_COUNTY,
							caseInfo2.getCdPersonAddrCounty(), CodesConstant.CCOUNT);
					BookmarkDto addrState = createBookmark(BookmarkConstants.ADDR_STATE,
							caseInfo2.getCdPersonAddrState());
					BookmarkDto nameSuf = createBookmarkWithCodesTable(BookmarkConstants.RPT_NAME_SUFFIX,
							caseInfo2.getCdNameSuffix(), CodesConstant.CSUFFIX2);
					BookmarkDto addrType = createBookmarkWithCodesTable(BookmarkConstants.ADDR_TYPE,
							caseInfo2.getCdPersAddrLinkType(), CodesConstant.CADDRTYP);
					BookmarkDto phoneType = createBookmarkWithCodesTable(BookmarkConstants.PHONE_TYPE,
							caseInfo2.getCdPersonPhoneType(), CodesConstant.CPHNTYP);
					BookmarkDto cdStagePers = createBookmarkWithCodesTable(BookmarkConstants.RPT_RELTNSP,
							caseInfo2.getCdStagePersRelInt(), CodesConstant.CRPTRINT);
					BookmarkDto nameFirst = createBookmark(BookmarkConstants.RPT_NAME_FIRST,
							caseInfo2.getNmNameFirst());
					BookmarkDto last = createBookmark(BookmarkConstants.RPT_NAME_LAST, caseInfo2.getNmNameLast());
					BookmarkDto middel = createBookmark(BookmarkConstants.RPT_NAME_MIDDLE, caseInfo2.getNmNameMiddle());
					BookmarkDto addrNotes = createBookmark(BookmarkConstants.ADDR_NOTES, caseInfo2.getPersAddrCmnts());
					BookmarkDto notes = createBookmark(BookmarkConstants.PHONE_NOTES,
							caseInfo2.getTxtPersonPhoneComments());
					BookmarkDto victimNotes = createBookmark(BookmarkConstants.RPT_NOTES,
							caseInfo2.getStagePersNotes());

					// SUB GROUP CFZCO00
					if ((ServiceConstants.DOLLAR_SIGN).equals(caseInfo2.getCdNameSuffix())) {
						FormDataGroupDto subGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_COMMA,
								FormGroupsConstants.TMPLAT_COLLATERAL);
						groupDtoList.add(subGroupDto);
					}

					bookmarkRrlList.add(addZip);
					bookmarkRrlList.add(phone);
					bookmarkRrlList.add(phoneExtension);
					bookmarkRrlList.add(city);
					bookmarkRrlList.add(addrAttn);
					bookmarkRrlList.add(ln1);
					bookmarkRrlList.add(ln2);
					bookmarkRrlList.add(county);
					bookmarkRrlList.add(addrState);
					bookmarkRrlList.add(nameSuf);
					bookmarkRrlList.add(addrType);
					bookmarkRrlList.add(phoneType);
					bookmarkRrlList.add(cdStagePers);
					bookmarkRrlList.add(nameFirst);
					bookmarkRrlList.add(last);
					bookmarkRrlList.add(middel);
					bookmarkRrlList.add(addrNotes);
					bookmarkRrlList.add(notes);
					bookmarkRrlList.add(victimNotes);

					parentGroupDto.setBookmarkDtoList(bookmarkRrlList);
					parentGroupDto.setFormDataGroupList(groupDtoList);
					formDataGroupList.add(parentGroupDto);
				}
			}
		}

		// Primary group cfiv1008
		if (!ObjectUtils.isEmpty(licInvDto.getInvstRestraintDtoList())) {
			for (InvstRestraintDto dto : licInvDto.getInvstRestraintDtoList()) {
				if (ObjectUtils.isEmpty(dto.getCdRstraint())) {
					// Primary group cfiv1009
					FormDataGroupDto parentGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_RESTRAINT_NONE,
							FormConstants.EMPTY_STRING);
					List<BookmarkDto> bookmarkRrlList = new ArrayList<BookmarkDto>();
					BookmarkDto type = createBookmarkWithCodesTable(BookmarkConstants.RESTRAINT_TYPE,
							dto.getCdRstraint(), CodesConstant.CRESTRNT);
					bookmarkRrlList.add(type);
					parentGroupDto.setBookmarkDtoList(bookmarkRrlList);
					formDataGroupList.add(parentGroupDto);
				}
				FormDataGroupDto parentGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_RESTRAINT,
						FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkRrlList = new ArrayList<BookmarkDto>();
				BookmarkDto type = createBookmarkWithCodesTable(BookmarkConstants.RESTRAINT_TYPE, dto.getCdRstraint(),
						CodesConstant.CRESTRNT);
				bookmarkRrlList.add(type);
				parentGroupDto.setBookmarkDtoList(bookmarkRrlList);
				formDataGroupList.add(parentGroupDto);
			}
		}

		/*
		 * Populating the non form group data into prefill data. !!bookmarks
		 */
		List<BookmarkDto> bookmarkNonFrmGrpList = new ArrayList<BookmarkDto>();
		// CSEC01D
		if (!ObjectUtils.isEmpty(licInvDto.getEmployeePersPhNameDto())) {
			BookmarkDto addrMailCodeCity = createBookmark(BookmarkConstants.SUM_OFFICE_ADDR_CITY,
					licInvDto.getEmployeePersPhNameDto().getAddrMailCodeCity());
			BookmarkDto addrMailCodeCounty = createBookmarkWithCodesTable(BookmarkConstants.SUM_OFFICE_ADDR_COUNTY,
					licInvDto.getEmployeePersPhNameDto().getAddrMailCodeCounty(), CodesConstant.CCOUNT);
			BookmarkDto stLn1 = createBookmark(BookmarkConstants.SUM_OFFICE_ADDR_LINE_1,
					licInvDto.getEmployeePersPhNameDto().getAddrMailCodeStLn1());
			BookmarkDto stLn2 = createBookmark(BookmarkConstants.SUM_OFFICE_ADDR_LINE_2,
					licInvDto.getEmployeePersPhNameDto().getAddrMailCodeStLn2());
			BookmarkDto codeZip = createBookmark(BookmarkConstants.SUM_OFFICE_ADDR_ZIP,
					licInvDto.getEmployeePersPhNameDto().getAddrMailCodeZip());
			BookmarkDto nameSuffix = createBookmarkWithCodesTable(BookmarkConstants.SUM_WORKER_NAME_SUFFIX,
					licInvDto.getEmployeePersPhNameDto().getCdNameSuffix(), CodesConstant.CSUFFIX2);
			BookmarkDto nmNameFirst = createBookmark(BookmarkConstants.SUM_WORKER_NAME_FIRST,
					licInvDto.getEmployeePersPhNameDto().getNmNameFirst());
			BookmarkDto nmNameLast = createBookmark(BookmarkConstants.SUM_WORKER_NAME_LAST,
					licInvDto.getEmployeePersPhNameDto().getNmNameLast());
			BookmarkDto nmNameMiddle = createBookmark(BookmarkConstants.SUM_WORKER_NAME_MIDDLE,
					licInvDto.getEmployeePersPhNameDto().getNmNameMiddle());
			BookmarkDto mref = createBookmark(BookmarkConstants.MULTI_REF, licInvDto.getGenericCaseInfoDto().getRcciMref());
			bookmarkNonFrmGrpList.add(addrMailCodeCity);
			bookmarkNonFrmGrpList.add(addrMailCodeCounty);
			bookmarkNonFrmGrpList.add(stLn1);
			bookmarkNonFrmGrpList.add(stLn2);
			bookmarkNonFrmGrpList.add(codeZip);
			bookmarkNonFrmGrpList.add(nameSuffix);
			bookmarkNonFrmGrpList.add(nmNameFirst);
			bookmarkNonFrmGrpList.add(nmNameLast);
			bookmarkNonFrmGrpList.add(nmNameMiddle);
			bookmarkNonFrmGrpList.add(mref);
		}

		// CSEC02D
		if (!ObjectUtils.isEmpty(licInvDto.getGenericCaseInfoDto())) {
			BookmarkDto recAction = createBookmarkWithCodesTable(BookmarkConstants.SUM_RECOMMENDED_ACTION,
					licInvDto.getGenericCaseInfoDto().getCdStageReasonClosed(), CodesConstant.CLCRECAT);
			BookmarkDto nmStage = createBookmark(BookmarkConstants.TITLE_CASE_NAME,
					licInvDto.getGenericCaseInfoDto().getNmStage());
			BookmarkDto ulIdCase = createBookmark(BookmarkConstants.TITLE_CASE_NUMBER,
					licInvDto.getGenericCaseInfoDto().getIdCase());
			bookmarkNonFrmGrpList.add(recAction);
			bookmarkNonFrmGrpList.add(nmStage);
			bookmarkNonFrmGrpList.add(ulIdCase);
		}

		// CINV74D
		if (!ObjectUtils.isEmpty(licInvDto.getLicensingInvstDtl())) {
			BookmarkDto dtlicNoncomp = createBookmark(BookmarkConstants.SUM_NON_COMPLIANCE,
					licInvDto.getLicensingInvstDtl().getTxtLicngInvstNoncomp());
			BookmarkDto dtLicAssigned = createBookmark(BookmarkConstants.SUM_DT_INTAKE_ASSIGNED,
					DateUtils.stringDt(licInvDto.getLicensingInvstDtl().getDtLicngInvstAssigned()));
			BookmarkDto dtcomplt = createBookmark(BookmarkConstants.SUM_DT_INVEST_ACTIONS,
					DateUtils.stringDt(licInvDto.getLicensingInvstDtl().getDtLicngInvstComplt()));
			BookmarkDto dtlicBegun = createBookmark(BookmarkConstants.SUM_DT_INVEST_BEGUN,
					DateUtils.stringDt(licInvDto.getLicensingInvstDtl().getDtLicngInvstBegun()));
			BookmarkDto dtIntake = createBookmark(BookmarkConstants.SUM_DT_INTAKE_RECEIVED,
					DateUtils.stringDt(licInvDto.getLicensingInvstDtl().getDtLicngInvstIntake()));
			BookmarkDto cdCoractn = createBookmarkWithCodesTable(BookmarkConstants.SUM_CORRECTIVE_ACTION,
					licInvDto.getLicensingInvstDtl().getCdLicngInvstCoractn(), CodesConstant.CCORACTN);
			BookmarkDto cdDisp = createBookmarkWithCodesTable(BookmarkConstants.SUM_OVERALL_DISPOSITION,
					licInvDto.getLicensingInvstDtl().getCdLicngInvstOvrallDisp(), CodesConstant.CDISPSTN);
			//artf218948 : LBTR/SXTR allegations questions
			BookmarkDto bookmarkChildSexTrafficQ = createBookmark(BookmarkConstants.STAGE_PROGRAM,
					licInvDto.getGenericCaseInfoDto().getCdStageProgram());
			BookmarkDto bookmarkChildSexTraffic = createBookmarkWithCodesTable(BookmarkConstants.INV_SEX_TRAFFIC,
					licInvDto.getLicensingInvstDtl().getIndChildSexTraffic(), CodesConstant.CINVACAN);
			BookmarkDto bookmarkChildLaborTraffic = createBookmarkWithCodesTable(BookmarkConstants.INV_LABOR_TRAFFIC,
					licInvDto.getLicensingInvstDtl().getIndChildLaborTraffic(), CodesConstant.CINVACAN);

			Date crelNotifRgtsDate = null;
			try {
				crelNotifRgtsDate = DateUtils.toJavaDateFromInput(lookupDao.simpleDecode(ServiceConstants.CRELDATE,
						CodesConstant.CRELDATE_JUNE_24_NTFCTN_RGHTS));
			} catch (ParseException e) {
				logger.error(e.getMessage());
			}
			if (!ObjectUtils.isEmpty(licInvDto.getGenericCaseInfoDto()) &&
					!ObjectUtils.isEmpty(crelNotifRgtsDate) && (ObjectUtils.isEmpty(licInvDto.getGenericCaseInfoDto().getDtStageClose())
					|| DateUtils.isAfter(licInvDto.getGenericCaseInfoDto().getDtStageClose(), crelNotifRgtsDate))) {
				FormDataGroupDto formForNotifRights = createFormDataGroup(FormGroupsConstants.TMPLAT_NOTIFICATION_RIGHTS,
						FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkDtoNotifRightsList = new ArrayList<BookmarkDto>();
				BookmarkDto bookmarkVrblWrtnNtfcnRgts = createBookmarkWithCodesTable(BookmarkConstants.INV_VRB_WRT_NOTIF,
						licInvDto.getLicensingInvstDtl().getIndVrblWrtnNotifRights(), CodesConstant.CINVACAN);
				bookmarkDtoNotifRightsList.add(bookmarkVrblWrtnNtfcnRgts);
				BookmarkDto bookmarkNtfcnRgtsFormUpld = createBookmarkWithCodesTable(BookmarkConstants.INV_NOTIF_FRM_UPD,
						licInvDto.getLicensingInvstDtl().getIndNotifRightsUpld(), CodesConstant.CINVACAN);
				bookmarkDtoNotifRightsList.add(bookmarkNtfcnRgtsFormUpld);
				formForNotifRights.setBookmarkDtoList(bookmarkDtoNotifRightsList);
				formDataGroupList.add(formForNotifRights);
			}

			bookmarkNonFrmGrpList.add(dtlicNoncomp);
			bookmarkNonFrmGrpList.add(dtLicAssigned);
			bookmarkNonFrmGrpList.add(dtcomplt);
			bookmarkNonFrmGrpList.add(dtlicBegun);
			bookmarkNonFrmGrpList.add(dtIntake);
			bookmarkNonFrmGrpList.add(cdCoractn);
			bookmarkNonFrmGrpList.add(cdDisp);
			bookmarkNonFrmGrpList.add(bookmarkChildSexTrafficQ);
			bookmarkNonFrmGrpList.add(bookmarkChildSexTraffic);
			bookmarkNonFrmGrpList.add(bookmarkChildLaborTraffic);
		}

		// CSEC01D employeeSuperInfo
		if (!ObjectUtils.isEmpty(licInvDto.getEmployeeSuperInfo())) {
			BookmarkDto suffix = createBookmarkWithCodesTable(BookmarkConstants.SUM_SUPERVISOR_NAME_SUFFIX,
					licInvDto.getEmployeeSuperInfo().getCdNameSuffix(), CodesConstant.CSUFFIX2);
			BookmarkDto supfirst = createBookmark(BookmarkConstants.SUM_SUPERVISOR_NAME_FIRST,
					licInvDto.getEmployeeSuperInfo().getNmNameFirst());
			BookmarkDto suplast = createBookmark(BookmarkConstants.SUM_SUPERVISOR_NAME_LAST,
					licInvDto.getEmployeeSuperInfo().getNmNameLast());
			BookmarkDto supmiddle = createBookmark(BookmarkConstants.SUM_SUPERVISOR_NAME_MIDDLE,
					licInvDto.getEmployeeSuperInfo().getNmNameMiddle());
			bookmarkNonFrmGrpList.add(suffix);
			bookmarkNonFrmGrpList.add(supfirst);
			bookmarkNonFrmGrpList.add(suplast);
			bookmarkNonFrmGrpList.add(supmiddle);
		}

		PreFillDataServiceDto preFillData = new PreFillDataServiceDto();
		preFillData.setFormDataGroupList(formDataGroupList);
		preFillData.setBookmarkDtoList(bookmarkNonFrmGrpList);
		return preFillData;
	}
}
