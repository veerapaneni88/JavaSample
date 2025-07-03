package us.tx.state.dfps.service.forms.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.dto.IncomingStageDetailsDto;
import us.tx.state.dfps.common.dto.NameDto;
import us.tx.state.dfps.common.dto.PhoneInfoDto;
import us.tx.state.dfps.service.common.BookmarkConstants;
import us.tx.state.dfps.service.common.FormConstants;
import us.tx.state.dfps.service.common.FormGroupsConstants;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.common.util.FormattingUtils;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.cpsinv.dto.CaseDtlsDto;
import us.tx.state.dfps.service.forms.dto.BlobDataDto;
import us.tx.state.dfps.service.forms.dto.BookmarkDto;
import us.tx.state.dfps.service.forms.dto.FormDataGroupDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.intake.dto.IncmgDetermFactorsDto;
import us.tx.state.dfps.service.person.dto.IntakeAllegationDto;
import us.tx.state.dfps.service.person.dto.PersonAddrLinkDto;
import us.tx.state.dfps.service.workload.dto.CpsIntakeNotificationDto;
import us.tx.state.dfps.service.workload.dto.PersonDto;

@Component
public class NotificationLawEnforcementAgencyPrefillData extends DocumentServiceUtil {

	@SuppressWarnings("unlikely-arg-type")
	@Override
	public PreFillDataServiceDto returnPrefillData(Object parentDtoobj) {
		CpsIntakeNotificationDto cpsIntakeNotificationDto = (CpsIntakeNotificationDto) parentDtoobj;

		if (ObjectUtils.isEmpty(cpsIntakeNotificationDto.getIncomingStageDetailsDto()))
			cpsIntakeNotificationDto.setIncomingStageDetailsDto(new IncomingStageDetailsDto());

		if (ObjectUtils.isEmpty(cpsIntakeNotificationDto.getPersonAddrLinkDto()))
			cpsIntakeNotificationDto.setPersonAddrLinkDto(new PersonAddrLinkDto());

		if (ObjectUtils.isEmpty(cpsIntakeNotificationDto.getCaseDtlsDto()))
			cpsIntakeNotificationDto.setCaseDtlsDto(new CaseDtlsDto());

		List<FormDataGroupDto> formDataGroupList = new ArrayList<FormDataGroupDto>();

		// cfin0106 independent
		
		for (IncmgDetermFactorsDto incDetermFactr : cpsIntakeNotificationDto.getIncmgDetermFactorsDto()) {
			FormDataGroupDto formdatagroupTmplatDetFactr = createFormDataGroup(FormGroupsConstants.TMPLAT_DET_FACTR,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkTmplatDetFactrList = new ArrayList<BookmarkDto>();
			// CINT15D DETERM_FACTR Codetable CDETFACT
			BookmarkDto bookmarkTmplatDetFactr = createBookmark(BookmarkConstants.DETERM_FACTR,
					incDetermFactr.getCdIncmgDeterm());
			bookmarkTmplatDetFactrList.add(bookmarkTmplatDetFactr);
			formdatagroupTmplatDetFactr.setBookmarkDtoList(bookmarkTmplatDetFactrList);
			formDataGroupList.add(formdatagroupTmplatDetFactr);
		}
		

		// cfin0107 independent		
		List<FormDataGroupDto> formdatagroupTmpltAllegtnList = new ArrayList<FormDataGroupDto>();
		for (IntakeAllegationDto intkAllgnDto : cpsIntakeNotificationDto.getIntakeAllegationList()) {
			FormDataGroupDto formdatagroupTmpltAllegtn = createFormDataGroup(FormGroupsConstants.TMPLAT_ALLEGATION,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkTmpltAllegtnList = new ArrayList<BookmarkDto>();

			// CINT19D ALLEG_DTL_ALLEG Codetable CCLICALT
			BookmarkDto bookmarkTmpltAllegtn = createBookmarkWithCodesTable(BookmarkConstants.ALLEG_DTL_ALLEG,
					intkAllgnDto.getCdIntakeAllegType(),ServiceConstants.CCLICALT);
			bookmarkTmpltAllegtnList.add(bookmarkTmpltAllegtn);

			// CINT19D ALLEG_DTL_AP
			BookmarkDto bookmarkAllegDtlAp = createBookmark(BookmarkConstants.ALLEG_DTL_AP,
					intkAllgnDto.getNmPerpetrator());
			bookmarkTmpltAllegtnList.add(bookmarkAllegDtlAp);

			// CINT19D ALLEG_DTL_VICTIM
			BookmarkDto bookmarkAllegDtlVictim = createBookmark(BookmarkConstants.ALLEG_DTL_VICTIM,
					intkAllgnDto.getNmVictim());
			bookmarkTmpltAllegtnList.add(bookmarkAllegDtlVictim);
			
			formdatagroupTmpltAllegtn.setBookmarkDtoList(bookmarkTmpltAllegtnList);
			formdatagroupTmpltAllegtnList.add(formdatagroupTmpltAllegtn);
		}

		formDataGroupList.addAll(formdatagroupTmpltAllegtnList);

		// cfin0501 independent
		for (IntakeAllegationDto intkAllgnDto : cpsIntakeNotificationDto.getIntakeAllegationDto()) {
			FormDataGroupDto formdatagroupTmpltAllegNotify = createFormDataGroup(FormGroupsConstants.TMPLAT_ALLEG_NOTIFY,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkTmpltAllegNotifyList = new ArrayList<BookmarkDto>();
			// CINT69D ALLEGATION Codetable CCLICALT
			BookmarkDto bookmarkTmpltAllegNotify = createBookmark(BookmarkConstants.ALLEGATION,
					intkAllgnDto.getCdIntakeAllegType());
			bookmarkTmpltAllegNotifyList.add(bookmarkTmpltAllegNotify);
			formdatagroupTmpltAllegNotify.setBookmarkDtoList(bookmarkTmpltAllegNotifyList);
			formDataGroupList.add(formdatagroupTmpltAllegNotify);
		}

		/******************************
		 * cfin0101 --> cfzz0201--> cfzz0101 -->cfin0109-->cfzco00
		 ***************************************************************************/

		// CFIN0101
		List<FormDataGroupDto> fdPrimaryGrpVictimInfoList = new ArrayList<FormDataGroupDto>();
		for (PersonDto victims : cpsIntakeNotificationDto.getPersonListVictim()) {
			FormDataGroupDto formdataTmplatVictim = createFormDataGroup(FormGroupsConstants.TMPLAT_VICTIM,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarktmplatVictimList = new ArrayList<BookmarkDto>();
			// CINT66D VICTIM_AGE
			BookmarkDto bookmarkVictimAge = createBookmark(BookmarkConstants.VICTIM_AGE, victims.getNbrPersonAge());
			bookmarktmplatVictimList.add(bookmarkVictimAge);

			// CINT66D VICTIM_DOB_APPROX
			BookmarkDto bookmarkVictimDobApprx = createBookmark(BookmarkConstants.VICTIM_DOB_APPROX,
					victims.getIndPersonDobApprox());
			bookmarktmplatVictimList.add(bookmarkVictimDobApprx);

			// CINT66D VICTIM_IN_LAW
			BookmarkDto bookmarkVictimInLaw = createBookmark(BookmarkConstants.VICTIM_IN_LAW,
					victims.getIndStagePersInLaw());
			bookmarktmplatVictimList.add(bookmarkVictimInLaw);

			// CINT66D VICTIM_SEX CodeTable CSEX
			BookmarkDto bookmarkSex = createBookmark(BookmarkConstants.VICTIM_SEX, victims.getCdPersonSex());
			bookmarktmplatVictimList.add(bookmarkSex);

			// CINT66D VICTIM_DOB
			BookmarkDto bookmarkVictimDob = createBookmark(BookmarkConstants.VICTIM_DOB, victims.getDob());
			bookmarktmplatVictimList.add(bookmarkVictimDob);

			// CINT66D VICTIM_DOD
			BookmarkDto bookmarkVictimDod = createBookmark(BookmarkConstants.VICTIM_DOD, victims.getDtPersonDeath());
			bookmarktmplatVictimList.add(bookmarkVictimDod);

			// CINT66D VICTIM_NAME_SUFFIX Codetable CSUFFIX2
			BookmarkDto bookmarkVictimNmSx = createBookmark(BookmarkConstants.VICTIM_NAME_SUFFIX,
					victims.getCdPersonSuffix());
			bookmarktmplatVictimList.add(bookmarkVictimNmSx);

			// CINT66D VICTIM_RSN Codetable CRSNFDTH
			BookmarkDto bookmarkVictimRSN = createBookmark(BookmarkConstants.VICTIM_RSN, victims.getCdPersonDeath());
			bookmarktmplatVictimList.add(bookmarkVictimRSN);

			// CINT66D VICTIM_ETHNCTY Codetable CETHNIC
			BookmarkDto bookmarkVictimEthnicity = createBookmark(BookmarkConstants.VICTIM_ETHNCTY,
					victims.getCdPersonEthnicGroup());
			bookmarktmplatVictimList.add(bookmarkVictimEthnicity);

			// CINT66D VICTIM_LANG Codetable CLANG
			BookmarkDto bookmarkVictimLang = createBookmark(BookmarkConstants.VICTIM_LANG,
					victims.getCdPersonLanguage());
			bookmarktmplatVictimList.add(bookmarkVictimLang);

			// CINT66D VICTIM_MARITAL Codetable CMARSTAT
			BookmarkDto bookmarkVictimMarital = createBookmark(BookmarkConstants.VICTIM_MARITAL,
					victims.getCdPersonMaritalStatus());
			bookmarktmplatVictimList.add(bookmarkVictimMarital);

			// CINT66D VICTIM_RELTNSP Codetable CRELVICT
			BookmarkDto bookmarkVictimRltnsp = createBookmark(BookmarkConstants.VICTIM_RELTNSP,
					victims.getCdStagePersRelInt());
			bookmarktmplatVictimList.add(bookmarkVictimRltnsp);

			// CINT66D VICTIM_NAME_FIRST
			BookmarkDto bookmarkVictimNmFirst = createBookmark(BookmarkConstants.VICTIM_NAME_FIRST,
					victims.getNmPersonFirst());
			bookmarktmplatVictimList.add(bookmarkVictimNmFirst);

			// CINT66D VICTIM_NAME_LAST
			BookmarkDto bookmarkNmLast = createBookmark(BookmarkConstants.VICTIM_NAME_LAST, victims.getNmPersonLast());
			bookmarktmplatVictimList.add(bookmarkNmLast);

			// CINT66D VICTIM_NAME_MIDDLE
			BookmarkDto bookmarkVictimNmMiddle = createBookmark(BookmarkConstants.VICTIM_NAME_MIDDLE,
					victims.getNmPersonMiddle());
			bookmarktmplatVictimList.add(bookmarkVictimNmMiddle);

			// CINT66D VICTIM_NOTES
			BookmarkDto bookmarkVictimNotes = createBookmark(BookmarkConstants.VICTIM_NOTES,
					victims.getTxtStagePersNotes());
			bookmarktmplatVictimList.add(bookmarkVictimNotes);
			formdataTmplatVictim.setBookmarkDtoList(bookmarktmplatVictimList);

			List<FormDataGroupDto> allSubGroups = new ArrayList<FormDataGroupDto>();

			// CFZZ0201
			List<FormDataGroupDto> formdataTmplatPhList = new ArrayList<FormDataGroupDto>();
			FormDataGroupDto formdataTmplatPh = createFormDataGroup(FormGroupsConstants.TMPLAT_PHONE,
					FormGroupsConstants.TMPLAT_VICTIM);

			List<Long> phoneList = cpsIntakeNotificationDto.getPersonPhoneList().stream().map(p -> p.getIdPerson())
					.collect(Collectors.toList());

			if (!TypeConvUtil.isNullOrEmpty(phoneList) && phoneList.contains(victims.getIdPerson())) {
				int i = phoneList.indexOf(victims.getIdPerson());
				PhoneInfoDto personInfoDto = cpsIntakeNotificationDto.getPersonPhoneList().get(i);
				List<BookmarkDto> bookmarkTmpltPhList = new ArrayList<BookmarkDto>();

				// CINT62D PHONE_NUMBER
				BookmarkDto bookmarkPhNum = createBookmark(BookmarkConstants.PHONE_NUMBER,
						FormattingUtils.formatPhoneWithEmptyPhone(personInfoDto.getNbrPersonPhone()));
				bookmarkTmpltPhList.add(bookmarkPhNum);

				// CINT62D PHONE_NUM_EXTENSION
				BookmarkDto bookmarkPhNumExtnsn = createBookmark(BookmarkConstants.PHONE_NUM_EXTENSION,
						personInfoDto.getNbrPersonPhoneExtension());
				bookmarkTmpltPhList.add(bookmarkPhNumExtnsn);

				// CINT62D PHONE_TYPE Codetable CPHNTYP
				BookmarkDto bookmarkPhType = createBookmarkWithCodesTable(BookmarkConstants.PHONE_TYPE,
						personInfoDto.getCdPersonPhoneType(),ServiceConstants.CPHNTYP);
				bookmarkTmpltPhList.add(bookmarkPhType);

				// CINT62D PHONE_NOTES
				BookmarkDto bookmarkPhNotes = createBookmark(BookmarkConstants.PHONE_NOTES,
						personInfoDto.getTxtPersonPhoneComments());

				bookmarkTmpltPhList.add(bookmarkPhNotes);
				formdataTmplatPh.setBookmarkDtoList(bookmarkTmpltPhList);

			}
			formdataTmplatPhList.add(formdataTmplatPh);
			allSubGroups.addAll(formdataTmplatPhList);
			// CFZZ0101
			FormDataGroupDto formdataAddrFull = createFormDataGroup(FormGroupsConstants.TMPLAT_ADDR_FULL,
					FormGroupsConstants.TMPLAT_VICTIM);
			List<FormDataGroupDto> formdataAddrFullList = new ArrayList<FormDataGroupDto>();
			List<Long> addressList = cpsIntakeNotificationDto.getPersonAddressLinkList().stream()
					.map(addr -> addr.getIdPerson()).collect(Collectors.toList());
			if (!TypeConvUtil.isNullOrEmpty(addressList) && addressList.contains(victims.getIdPerson())) {
				int j = addressList.indexOf(victims.getIdPerson());
				PersonAddrLinkDto personAddrLinkDto = cpsIntakeNotificationDto.getPersonAddressLinkList().get(j);
				List<BookmarkDto> bookmarkAddrFullList = new ArrayList<BookmarkDto>();
				// CINT63D ADDR_ZIP
				BookmarkDto bookmarkAddrZip = createBookmark(BookmarkConstants.ADDR_ZIP,
						personAddrLinkDto.getAddrPersonAddrZip());
				bookmarkAddrFullList.add(bookmarkAddrZip);
				// CINT63D ADDR_CITY
				BookmarkDto bookmarkAddrCity = createBookmark(BookmarkConstants.ADDR_CITY,
						personAddrLinkDto.getAddrPersonAddrCity());
				bookmarkAddrFullList.add(bookmarkAddrCity);
				// CINT63D ADDR_ATTN
				BookmarkDto bookmarkAddrAttn = createBookmark(BookmarkConstants.ADDR_ATTN,
						personAddrLinkDto.getPersAddressAttention());
				bookmarkAddrFullList.add(bookmarkAddrAttn);
				// CINT63D ADDR_LN_1
				BookmarkDto bookmarkAddrLn1 = createBookmark(BookmarkConstants.ADDR_LN_1,
						personAddrLinkDto.getAddrPersAddrStLn1());
				bookmarkAddrFullList.add(bookmarkAddrLn1);
				// CINT63D ADDR_LN_2
				BookmarkDto bookmarkAddrLn2 = createBookmark(BookmarkConstants.ADDR_LN_2,
						personAddrLinkDto.getAddrPersAddrStLn2());
				bookmarkAddrFullList.add(bookmarkAddrLn2);
				// CINT63D ADDR_COUNTY Codetable CCOUNT
				BookmarkDto bookmarkAddrCounty = createBookmark(BookmarkConstants.ADDR_COUNTY,
						personAddrLinkDto.getCdPersonAddrCounty());
				bookmarkAddrFullList.add(bookmarkAddrCounty);
				// CINT63D ADDR_STATE
				BookmarkDto bookmarkAddrState = createBookmark(BookmarkConstants.ADDR_STATE,
						personAddrLinkDto.getCdPersonAddrState());
				bookmarkAddrFullList.add(bookmarkAddrState);
				// CINT63D ADDR_TYPE Codetable CADDRTYP
				BookmarkDto bookmarkAddrType = createBookmarkWithCodesTable(BookmarkConstants.ADDR_TYPE,
						personAddrLinkDto.getCdPersAddrLinkType(),ServiceConstants.CADDRTYP);
				bookmarkAddrFullList.add(bookmarkAddrType);
				// CINT63D ADDR_NOTES
				BookmarkDto bookmarkAddrNotes = createBookmark(BookmarkConstants.ADDR_NOTES,
						personAddrLinkDto.getPersAddrCmnts());
				bookmarkAddrFullList.add(bookmarkAddrNotes);
				formdataAddrFull.setBookmarkDtoList(bookmarkAddrFullList);

			}
			formdataAddrFullList.add(formdataAddrFull);
			allSubGroups.addAll(formdataAddrFullList);

			// CFIN0109
			FormDataGroupDto formdataAlias = createFormDataGroup(FormGroupsConstants.TMPLAT_ALIAS,
					FormGroupsConstants.TMPLAT_VICTIM);
			List<FormDataGroupDto> formdataAliasList = new ArrayList<FormDataGroupDto>();
			List<Integer> aliasVicList = cpsIntakeNotificationDto.getNameDto().stream()
					.map(alias -> alias.getIdPerson()).collect(Collectors.toList());
			if (aliasVicList.contains(victims.getIdPerson())) {
				int k = cpsIntakeNotificationDto.getNameDto().indexOf(victims.getIdPerson());
				NameDto nameDto = cpsIntakeNotificationDto.getNameDto().get(k);
				// CINT64D ALIAS_NAME_FIRST

				List<BookmarkDto> bookmarkAlias = new ArrayList<BookmarkDto>();
				BookmarkDto bookmarkAliasNmFrst = createBookmark(BookmarkConstants.ALIAS_NAME_FIRST,
						nameDto.getFirstName());
				bookmarkAlias.add(bookmarkAliasNmFrst);

				// CINT64D ALIAS_NAME_LAST
				BookmarkDto bookmarkAliasNmLast = createBookmark(BookmarkConstants.ALIAS_NAME_LAST,
						nameDto.getLastName());
				bookmarkAlias.add(bookmarkAliasNmLast);
				// CINT64D ALIAS_NAME_MIDDLE
				BookmarkDto bookmarkAliasNmMiddle = createBookmark(BookmarkConstants.ALIAS_NAME_MIDDLE,
						nameDto.getMiddleName());
				bookmarkAlias.add(bookmarkAliasNmMiddle);

				formdataAlias.setBookmarkDtoList(bookmarkAlias);

			}
			formdataAliasList.add(formdataAlias);
			allSubGroups.addAll(formdataAliasList);
			// CFZCo00;

			if (StringUtils.isNotBlank(victims.getCdPersonSuffix())) {

				FormDataGroupDto formdataComma = createFormDataGroup(FormGroupsConstants.TMPLAT_COMMA,
						FormGroupsConstants.TMPLAT_VICTIM);
				List<FormDataGroupDto> formdataCommaList = new ArrayList<FormDataGroupDto>();
				formdataCommaList.add(formdataComma);
				allSubGroups.addAll(formdataCommaList);
			}
			formdataTmplatVictim.setFormDataGroupList(allSubGroups);
			fdPrimaryGrpVictimInfoList.add(formdataTmplatVictim);
		}
		formDataGroupList.addAll(fdPrimaryGrpVictimInfoList);

		/*************************************
		 * cfin0102 -->cfzz0201 -->cfzz0101-->cfin0109-->cfzco00
		 ****************************************************************/

		// CFIN0102
		List<FormDataGroupDto> primaryGrpAllegPerpList = new ArrayList<FormDataGroupDto>();

		for (PersonDto allegPerp : cpsIntakeNotificationDto.getPersonListPerperator()) {
			FormDataGroupDto formdataTmplatPerp = createFormDataGroup(FormGroupsConstants.TMPLAT_ALLEG_PERP,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarktmplatPerpList = new ArrayList<BookmarkDto>();
			// CINT66D AP_AGE
			BookmarkDto bookmarkPerpAge = createBookmark(BookmarkConstants.AP_AGE, allegPerp.getNbrPersonAge());
			bookmarktmplatPerpList.add(bookmarkPerpAge);

			// CINT66D AP_DOB_APPROX
			BookmarkDto bookmarkPerpDobApprx = createBookmark(BookmarkConstants.AP_DOB_APPROX,
					allegPerp.getIndPersonDobApprox());
			bookmarktmplatPerpList.add(bookmarkPerpDobApprx);

			// CINT66D AP_IN_LAW
			BookmarkDto bookmarkPerpInLaw = createBookmark(BookmarkConstants.AP_IN_LAW,
					allegPerp.getIndStagePersInLaw());
			bookmarktmplatPerpList.add(bookmarkPerpInLaw);

			// CINT66D AP_SEX CodeTable CSEX
			BookmarkDto bookmarkPerpSex = createBookmark(BookmarkConstants.AP_SEX, allegPerp.getCdPersonSex());
			bookmarktmplatPerpList.add(bookmarkPerpSex);

			// CINT66D AP_DOB
			BookmarkDto bookmarkPerpDob = createBookmark(BookmarkConstants.AP_DOB, allegPerp.getDob());
			bookmarktmplatPerpList.add(bookmarkPerpDob);

			// CINT66D AP_DOD
			BookmarkDto bookmarkPerpDod = createBookmark(BookmarkConstants.AP_DOD, allegPerp.getDtPersonDeath());
			bookmarktmplatPerpList.add(bookmarkPerpDod);

			// CINT66D AP_NAME_SUFFIX Codetable CSUFFIX2
			BookmarkDto bookmarkPerpNmSx = createBookmark(BookmarkConstants.AP_NAME_SUFFIX,
					allegPerp.getCdPersonSuffix());
			bookmarktmplatPerpList.add(bookmarkPerpNmSx);

			// CINT66D AP_RSN Codetable CRSNFDTH
			BookmarkDto bookmarkPerpRSN = createBookmark(BookmarkConstants.AP_RSN, allegPerp.getCdPersonDeath());
			bookmarktmplatPerpList.add(bookmarkPerpRSN);

			// CINT66D AP_ETHNCTY Codetable CETHNIC
			BookmarkDto bookmarkPerpEthnicity = createBookmark(BookmarkConstants.AP_ETHNCTY,
					allegPerp.getCdPersonEthnicGroup());
			bookmarktmplatPerpList.add(bookmarkPerpEthnicity);

			// CINT66D AP_LANG Codetable CLANG
			BookmarkDto bookmarkPerpLang = createBookmark(BookmarkConstants.AP_LANG, allegPerp.getCdPersonLanguage());
			bookmarktmplatPerpList.add(bookmarkPerpLang);

			// CINT66D AP_MARITAL Codetable CMARSTAT
			BookmarkDto bookmarkPerpMarital = createBookmark(BookmarkConstants.AP_MARITAL,
					allegPerp.getCdPersonMaritalStatus());
			bookmarktmplatPerpList.add(bookmarkPerpMarital);

			// CINT66D AP_RELTNSP Codetable CRELVICT
			BookmarkDto bookmarkPerpRltnsp = createBookmark(BookmarkConstants.AP_RELTNSP,
					allegPerp.getCdStagePersRelInt());
			bookmarktmplatPerpList.add(bookmarkPerpRltnsp);

			// CINT66D AP_NAME_FIRST
			BookmarkDto bookmarkPerpNmFirst = createBookmark(BookmarkConstants.AP_NAME_FIRST,
					allegPerp.getNmPersonFirst());
			bookmarktmplatPerpList.add(bookmarkPerpNmFirst);

			// CINT66D AP_NAME_LAST
			BookmarkDto bookmarkPerpNmLast = createBookmark(BookmarkConstants.AP_NAME_LAST,
					allegPerp.getNmPersonLast());
			bookmarktmplatPerpList.add(bookmarkPerpNmLast);

			// CINT66D AP_NAME_MIDDLE
			BookmarkDto bookmarkPerpNmMiddle = createBookmark(BookmarkConstants.AP_NAME_MIDDLE,
					allegPerp.getNmPersonMiddle());
			bookmarktmplatPerpList.add(bookmarkPerpNmMiddle);

			// CINT66D AP_NOTES
			BookmarkDto bookmarkPerpNotes = createBookmark(BookmarkConstants.AP_NOTES,
					allegPerp.getTxtStagePersNotes());
			bookmarktmplatPerpList.add(bookmarkPerpNotes);
			formdataTmplatPerp.setBookmarkDtoList(bookmarktmplatPerpList);

			List<FormDataGroupDto> allAllegPerpSubGroups = new ArrayList<FormDataGroupDto>();

			// CFZZ0201
			List<FormDataGroupDto> formdataTmplatPhList = new ArrayList<FormDataGroupDto>();
			FormDataGroupDto formdataTmplatPh = createFormDataGroup(FormGroupsConstants.TMPLAT_PHONE,
					FormGroupsConstants.TMPLAT_ALLEG_PERP);

			List<Long> phoneList = cpsIntakeNotificationDto.getPersonPhoneList().stream().map(p -> p.getIdPerson())
					.collect(Collectors.toList());
			if (!TypeConvUtil.isNullOrEmpty(phoneList) && phoneList.contains(allegPerp.getIdPerson())) {
				int i = phoneList.indexOf(allegPerp.getIdPerson());

				PhoneInfoDto personInfoDto = cpsIntakeNotificationDto.getPersonPhoneList().get(i);

				List<BookmarkDto> bookmarkTmpltAllegPhList = new ArrayList<BookmarkDto>();
				// CINT62D PHONE_NUMBER
				BookmarkDto bookmarkPhNum = createBookmark(BookmarkConstants.PHONE_NUMBER,
						FormattingUtils.formatPhoneWithEmptyPhone(personInfoDto.getNbrPersonPhone()));
				bookmarkTmpltAllegPhList.add(bookmarkPhNum);
				// CINT62D PHONE_NUM_EXTENSION
				BookmarkDto bookmarkPhNumExtnsn = createBookmark(BookmarkConstants.PHONE_NUM_EXTENSION,
						personInfoDto.getNbrPersonPhoneExtension());
				bookmarkTmpltAllegPhList.add(bookmarkPhNumExtnsn);
				// CINT62D PHONE_TYPE Codetable CPHNTYP
				BookmarkDto bookmarkPhType = createBookmarkWithCodesTable(BookmarkConstants.PHONE_TYPE,
						personInfoDto.getCdPersonPhoneType(),ServiceConstants.CPHNTYP);
				bookmarkTmpltAllegPhList.add(bookmarkPhType);
				// CINT62D PHONE_NOTES
				BookmarkDto bookmarkPhNotes = createBookmark(BookmarkConstants.PHONE_NOTES,
						personInfoDto.getTxtPersonPhoneComments());
				bookmarkTmpltAllegPhList.add(bookmarkPhNotes);
				formdataTmplatPh.setBookmarkDtoList(bookmarkTmpltAllegPhList);

			}
			formdataTmplatPhList.add(formdataTmplatPh);
			allAllegPerpSubGroups.addAll(formdataTmplatPhList);

			// CFZZ0101
			FormDataGroupDto formdataAddrFull = createFormDataGroup(FormGroupsConstants.TMPLAT_ADDR_FULL,
					FormGroupsConstants.TMPLAT_ALLEG_PERP);
			List<FormDataGroupDto> formdataAddrFullList = new ArrayList<FormDataGroupDto>();

			List<Long> addressList = cpsIntakeNotificationDto.getPersonAddressLinkList().stream()
					.map(addr -> addr.getIdPerson()).collect(Collectors.toList());

			if (!TypeConvUtil.isNullOrEmpty(addressList) && addressList.contains(allegPerp.getIdPerson())) {
				int j = addressList.indexOf(allegPerp.getIdPerson());
				PersonAddrLinkDto personAddrLinkDto = cpsIntakeNotificationDto.getPersonAddressLinkList().get(j);

				List<BookmarkDto> bookmarkAddrFullList = new ArrayList<BookmarkDto>();
				// CINT63D ADDR_ZIP
				BookmarkDto bookmarkAddrZip = createBookmark(BookmarkConstants.ADDR_ZIP,
						personAddrLinkDto.getAddrPersonAddrZip());
				bookmarkAddrFullList.add(bookmarkAddrZip);
				// CINT63D ADDR_CITY
				BookmarkDto bookmarkAddrCity = createBookmark(BookmarkConstants.ADDR_CITY,
						personAddrLinkDto.getAddrPersonAddrCity());
				bookmarkAddrFullList.add(bookmarkAddrCity);
				// CINT63D ADDR_ATTN
				BookmarkDto bookmarkAddrAttn = createBookmark(BookmarkConstants.ADDR_ATTN,
						personAddrLinkDto.getPersAddressAttention());
				bookmarkAddrFullList.add(bookmarkAddrAttn);
				// CINT63D ADDR_LN_1
				BookmarkDto bookmarkAddrLn1 = createBookmark(BookmarkConstants.ADDR_LN_1,
						personAddrLinkDto.getAddrPersAddrStLn1());
				bookmarkAddrFullList.add(bookmarkAddrLn1);
				// CINT63D ADDR_LN_2
				BookmarkDto bookmarkAddrLn2 = createBookmark(BookmarkConstants.ADDR_LN_2,
						personAddrLinkDto.getAddrPersAddrStLn2());
				bookmarkAddrFullList.add(bookmarkAddrLn2);
				// CINT63D ADDR_COUNTY Codetable CCOUNT
				BookmarkDto bookmarkAddrCounty = createBookmark(BookmarkConstants.ADDR_COUNTY,
						personAddrLinkDto.getCdPersonAddrCounty());
				bookmarkAddrFullList.add(bookmarkAddrCounty);
				// CINT63D ADDR_STATE
				BookmarkDto bookmarkAddrState = createBookmark(BookmarkConstants.ADDR_STATE,
						personAddrLinkDto.getCdPersonAddrState());
				bookmarkAddrFullList.add(bookmarkAddrState);
				// CINT63D ADDR_TYPE Codetable CADDRTYP
				BookmarkDto bookmarkAddrType = createBookmarkWithCodesTable(BookmarkConstants.ADDR_TYPE,
						personAddrLinkDto.getCdPersAddrLinkType(),ServiceConstants.CADDRTYP);
				bookmarkAddrFullList.add(bookmarkAddrType);
				// CINT63D ADDR_NOTES
				BookmarkDto bookmarkAddrNotes = createBookmark(BookmarkConstants.ADDR_NOTES,
						personAddrLinkDto.getPersAddrCmnts());
				bookmarkAddrFullList.add(bookmarkAddrNotes);
				formdataAddrFull.setBookmarkDtoList(bookmarkAddrFullList);
			}
			formdataAddrFullList.add(formdataAddrFull);
			allAllegPerpSubGroups.addAll(formdataAddrFullList);

			// CFIN0109
			FormDataGroupDto formdataAlias = createFormDataGroup(FormGroupsConstants.TMPLAT_ALIAS,
					FormGroupsConstants.TMPLAT_ALLEG_PERP);
			List<FormDataGroupDto> formdataAliasList = new ArrayList<FormDataGroupDto>();

			List<Integer> aliasList = cpsIntakeNotificationDto.getNameDto().stream().map(alias -> alias.getIdPerson())
					.collect(Collectors.toList());

			if (aliasList.contains(allegPerp.getIdPerson())) {
				int k = cpsIntakeNotificationDto.getNameDto().indexOf(allegPerp.getIdPerson());
				NameDto nameDto = cpsIntakeNotificationDto.getNameDto().get(k);

				// CINT64D ALIAS_NAME_FIRST
				List<BookmarkDto> bookmarkAlias = new ArrayList<BookmarkDto>();
				BookmarkDto bookmarkAliasNmFrst = createBookmark(BookmarkConstants.ALIAS_NAME_FIRST,
						nameDto.getFirstName());
				bookmarkAlias.add(bookmarkAliasNmFrst);

				// CINT64D ALIAS_NAME_LAST
				BookmarkDto bookmarkAliasNmLast = createBookmark(BookmarkConstants.ALIAS_NAME_LAST,
						nameDto.getLastName());
				bookmarkAlias.add(bookmarkAliasNmLast);
				// CINT64D ALIAS_NAME_MIDDLE
				BookmarkDto bookmarkAliasNmMiddle = createBookmark(BookmarkConstants.ALIAS_NAME_MIDDLE,
						nameDto.getMiddleName());
				bookmarkAlias.add(bookmarkAliasNmMiddle);

				formdataAlias.setBookmarkDtoList(bookmarkAlias);
			}
			formdataAliasList.add(formdataAlias);
			allAllegPerpSubGroups.addAll(formdataAliasList);
			// CFZCo00;

			if (StringUtils.isNotBlank(allegPerp.getCdPersonSuffix())) {

				FormDataGroupDto formdataComma = createFormDataGroup(FormGroupsConstants.TMPLAT_COMMA,
						FormGroupsConstants.TMPLAT_ALLEG_PERP);
				List<FormDataGroupDto> formdataCommaList = new ArrayList<FormDataGroupDto>();
				formdataCommaList.add(formdataComma);
				allAllegPerpSubGroups.addAll(formdataCommaList);
			}
			formdataTmplatPerp.setFormDataGroupList(allAllegPerpSubGroups);
			primaryGrpAllegPerpList.add(formdataTmplatPerp);
		}
		formDataGroupList.addAll(primaryGrpAllegPerpList);
		/*********************************************
		 * cfin0103 --> cfzz0201--> cfzz0101 -->cfin0109-->cfzco00
		 *********************************************************/

		// CFIN0103
		List<FormDataGroupDto> priGrpOtherPrincipalList = new ArrayList<FormDataGroupDto>();
		FormDataGroupDto priGrpOtherPrincipal = createFormDataGroup(FormGroupsConstants.TMPLAT_PRINC_OTHER,
				FormConstants.EMPTY_STRING);
		for (PersonDto otherPrin : cpsIntakeNotificationDto.getOther()) {
			List<BookmarkDto> bookmarktmplatOtherPrinc = new ArrayList<BookmarkDto>();
			// CINT66D PRINC_AGE
			BookmarkDto bookmarkOthrAge = createBookmark(BookmarkConstants.PRINC_AGE, otherPrin.getNbrPersonAge());
			bookmarktmplatOtherPrinc.add(bookmarkOthrAge);

			// CINT66D PRINC_DOB_APPROX
			BookmarkDto bookmarkOthrDobApprx = createBookmark(BookmarkConstants.PRINC_DOB_APPROX,
					otherPrin.getIndPersonDobApprox());
			bookmarktmplatOtherPrinc.add(bookmarkOthrDobApprx);

			// CINT66D PRINC_IN_LAW
			BookmarkDto bookmarkOthrInLaw = createBookmark(BookmarkConstants.PRINC_IN_LAW,
					otherPrin.getIndStagePersInLaw());
			bookmarktmplatOtherPrinc.add(bookmarkOthrInLaw);

			// CINT66D PRINC_SEX CodeTable CSEX
			BookmarkDto bookmarkOthrSex = createBookmark(BookmarkConstants.PRINC_SEX, otherPrin.getCdPersonSex());
			bookmarktmplatOtherPrinc.add(bookmarkOthrSex);

			// CINT66D PRINC_DOB
			BookmarkDto bookmarkOthrDob = createBookmark(BookmarkConstants.PRINC_DOB, otherPrin.getDob());
			bookmarktmplatOtherPrinc.add(bookmarkOthrDob);

			// CINT66D PRINC_DOD
			BookmarkDto bookmarkOthrDod = createBookmark(BookmarkConstants.PRINC_DOD, otherPrin.getDtPersonDeath());
			bookmarktmplatOtherPrinc.add(bookmarkOthrDod);

			// CINT66D PRINC_NAME_SUFFIX Codetable CSUFFIX2
			BookmarkDto bookmarkOthrNmSx = createBookmark(BookmarkConstants.PRINC_NAME_SUFFIX,
					otherPrin.getCdPersonSuffix());
			bookmarktmplatOtherPrinc.add(bookmarkOthrNmSx);

			// CINT66D PRINC_RSN Codetable CRSNFDTH
			BookmarkDto bookmarkOthrRSN = createBookmark(BookmarkConstants.PRINC_RSN, otherPrin.getCdPersonDeath());
			bookmarktmplatOtherPrinc.add(bookmarkOthrRSN);

			// CINT66D PRINC_ETHNCTY Codetable CETHNIC
			BookmarkDto bookmarkOthrEthnicity = createBookmark(BookmarkConstants.PRINC_ETHNCTY,
					otherPrin.getCdPersonEthnicGroup());
			bookmarktmplatOtherPrinc.add(bookmarkOthrEthnicity);

			// CINT66D PRINC_LANG Codetable CLANG
			BookmarkDto bookmarkOthrLang = createBookmark(BookmarkConstants.PRINC_LANG,
					otherPrin.getCdPersonLanguage());
			bookmarktmplatOtherPrinc.add(bookmarkOthrLang);

			// CINT66D PRINC_MARITAL Codetable CMARSTAT
			BookmarkDto bookmarkOthrMarital = createBookmark(BookmarkConstants.PRINC_MARITAL,
					otherPrin.getCdPersonMaritalStatus());
			bookmarktmplatOtherPrinc.add(bookmarkOthrMarital);

			// CINT66D PRINC_RELTNSP Codetable CRELVICT
			BookmarkDto bookmarkOthrRltnsp = createBookmark(BookmarkConstants.PRINC_RELTNSP,
					otherPrin.getCdStagePersRelInt());
			bookmarktmplatOtherPrinc.add(bookmarkOthrRltnsp);

			// CINT66D PRINC_ROLE Codetable CROLES
			BookmarkDto bookmarkOthrRole = createBookmark(BookmarkConstants.PRINC_ROLE,
					otherPrin.getCdStagePersRelInt());
			bookmarktmplatOtherPrinc.add(bookmarkOthrRole);

			// CINT66D PRINC_NAME_FIRST
			BookmarkDto bookmarkOthrNmFirst = createBookmark(BookmarkConstants.PRINC_NAME_FIRST,
					otherPrin.getNmPersonFirst());
			bookmarktmplatOtherPrinc.add(bookmarkOthrNmFirst);

			// CINT66D PRINC_NAME_LAST
			BookmarkDto bookmarkOthrNmLast = createBookmark(BookmarkConstants.PRINC_NAME_LAST,
					otherPrin.getNmPersonLast());
			bookmarktmplatOtherPrinc.add(bookmarkOthrNmLast);

			// CINT66D PRINC_NAME_MIDDLE
			BookmarkDto bookmarkOthrNmMiddle = createBookmark(BookmarkConstants.PRINC_NAME_MIDDLE,
					otherPrin.getNmPersonMiddle());
			bookmarktmplatOtherPrinc.add(bookmarkOthrNmMiddle);

			// CINT66D PRINC_NOTES
			BookmarkDto bookmarkOthrNotes = createBookmark(BookmarkConstants.PRINC_NOTES,
					otherPrin.getTxtStagePersNotes());
			bookmarktmplatOtherPrinc.add(bookmarkOthrNotes);
			priGrpOtherPrincipal.setBookmarkDtoList(bookmarktmplatOtherPrinc);

			List<FormDataGroupDto> allOtherPrinSubGroupList = new ArrayList<FormDataGroupDto>();

			// CFZZ0201
			List<FormDataGroupDto> phoneOtherPrinSubGrpList = new ArrayList<FormDataGroupDto>();
			FormDataGroupDto formdataOthrTmplatPh = createFormDataGroup(FormGroupsConstants.TMPLAT_PHONE,
					FormGroupsConstants.TMPLAT_PRINC_OTHER);
			List<Long> phoneList = cpsIntakeNotificationDto.getPersonPhoneList().stream().map(p -> p.getIdPerson())
					.collect(Collectors.toList());

			if (!TypeConvUtil.isNullOrEmpty(phoneList) && phoneList.contains(otherPrin.getIdPerson())) {

				int i = phoneList.indexOf(otherPrin.getIdPerson());

				PhoneInfoDto personInfoDto = cpsIntakeNotificationDto.getPersonPhoneList().get(i);
				List<BookmarkDto> bookmarkTmpltPhList = new ArrayList<BookmarkDto>();
				// CINT62D PHONE_NUMBER
				BookmarkDto bookmarkPhNum = createBookmark(BookmarkConstants.PHONE_NUMBER,
						FormattingUtils.formatPhoneWithEmptyPhone(personInfoDto.getNbrPersonPhone()));
				bookmarkTmpltPhList.add(bookmarkPhNum);
				// CINT62D PHONE_NUM_EXTENSION
				BookmarkDto bookmarkPhNumExtnsn = createBookmark(BookmarkConstants.PHONE_NUM_EXTENSION,
						personInfoDto.getNbrPersonPhoneExtension());
				bookmarkTmpltPhList.add(bookmarkPhNumExtnsn);
				// CINT62D PHONE_TYPE Codetable CPHNTYP
				BookmarkDto bookmarkPhType = createBookmarkWithCodesTable(BookmarkConstants.PHONE_TYPE,
						personInfoDto.getCdPersonPhoneType(),ServiceConstants.CPHNTYP);
				bookmarkTmpltPhList.add(bookmarkPhType);
				// CINT62D PHONE_NOTES
				BookmarkDto bookmarkPhNotes = createBookmark(BookmarkConstants.PHONE_NOTES,
						personInfoDto.getTxtPersonPhoneComments());
				bookmarkTmpltPhList.add(bookmarkPhNotes);
				formdataOthrTmplatPh.setBookmarkDtoList(bookmarkTmpltPhList);
			}
			phoneOtherPrinSubGrpList.add(formdataOthrTmplatPh);
			allOtherPrinSubGroupList.addAll(phoneOtherPrinSubGrpList);

			// CFZZ0101
			FormDataGroupDto fdSubGrpOtherPrinAddressInfo = createFormDataGroup(FormGroupsConstants.TMPLAT_ADDR_FULL,
					FormGroupsConstants.TMPLAT_PRINC_OTHER);
			List<FormDataGroupDto> otherPrinAddressSubgroupList = new ArrayList<FormDataGroupDto>();

			List<Long> addressList = cpsIntakeNotificationDto.getPersonAddressLinkList().stream()
					.map(addr -> addr.getIdPerson()).collect(Collectors.toList());

			if (!TypeConvUtil.isNullOrEmpty(addressList) && addressList.contains(otherPrin.getIdPerson())) {
				int j = addressList.indexOf(otherPrin.getIdPerson());
				PersonAddrLinkDto addr = cpsIntakeNotificationDto.getPersonAddressLinkList().get(j);
				List<BookmarkDto> bookmarkAddrFullList = new ArrayList<BookmarkDto>();
				// CINT63D ADDR_ZIP
				BookmarkDto bookmarkAddrZip = createBookmark(BookmarkConstants.ADDR_ZIP, addr.getAddrPersonAddrZip());
				bookmarkAddrFullList.add(bookmarkAddrZip);
				// CINT63D ADDR_CITY
				BookmarkDto bookmarkAddrCity = createBookmark(BookmarkConstants.ADDR_CITY,
						addr.getAddrPersonAddrCity());
				bookmarkAddrFullList.add(bookmarkAddrCity);
				// CINT63D ADDR_ATTN
				BookmarkDto bookmarkAddrAttn = createBookmark(BookmarkConstants.ADDR_ATTN,
						addr.getPersAddressAttention());
				bookmarkAddrFullList.add(bookmarkAddrAttn);
				// CINT63D ADDR_LN_1
				BookmarkDto bookmarkAddrLn1 = createBookmark(BookmarkConstants.ADDR_LN_1, addr.getAddrPersAddrStLn1());
				bookmarkAddrFullList.add(bookmarkAddrLn1);
				// CINT63D ADDR_LN_2
				BookmarkDto bookmarkAddrLn2 = createBookmark(BookmarkConstants.ADDR_LN_2, addr.getAddrPersAddrStLn2());
				bookmarkAddrFullList.add(bookmarkAddrLn2);
				// CINT63D ADDR_COUNTY Codetable CCOUNT
				BookmarkDto bookmarkAddrCounty = createBookmark(BookmarkConstants.ADDR_COUNTY,
						addr.getCdPersonAddrCounty());
				bookmarkAddrFullList.add(bookmarkAddrCounty);
				// CINT63D ADDR_STATE
				BookmarkDto bookmarkAddrState = createBookmark(BookmarkConstants.ADDR_STATE,
						addr.getCdPersonAddrState());
				bookmarkAddrFullList.add(bookmarkAddrState);
				// CINT63D ADDR_TYPE Codetable CADDRTYP
				BookmarkDto bookmarkAddrType = createBookmarkWithCodesTable(BookmarkConstants.ADDR_TYPE,
						addr.getCdPersAddrLinkType(),ServiceConstants.CADDRTYP);
				bookmarkAddrFullList.add(bookmarkAddrType);
				// CINT63D ADDR_NOTES
				BookmarkDto bookmarkAddrNotes = createBookmark(BookmarkConstants.ADDR_NOTES, addr.getPersAddrCmnts());
				bookmarkAddrFullList.add(bookmarkAddrNotes);
				fdSubGrpOtherPrinAddressInfo.setBookmarkDtoList(bookmarkAddrFullList);
			}
			otherPrinAddressSubgroupList.add(fdSubGrpOtherPrinAddressInfo);
			allOtherPrinSubGroupList.addAll(otherPrinAddressSubgroupList);

			// CFIN0109
			FormDataGroupDto fdSubGrpallegPerpAliasInfo = createFormDataGroup(FormGroupsConstants.TMPLAT_ALIAS,
					FormGroupsConstants.TMPLAT_PRINC_OTHER);
			List<FormDataGroupDto> aliasallegPerpNameList = new ArrayList<FormDataGroupDto>();
			List<Integer> aliasList = cpsIntakeNotificationDto.getNameDto().stream().map(alias -> alias.getIdPerson())
					.collect(Collectors.toList());

			if (aliasList.contains(otherPrin.getIdPerson())) {
				int k = cpsIntakeNotificationDto.getNameDto().indexOf(otherPrin.getIdPerson());
				NameDto nameDto = cpsIntakeNotificationDto.getNameDto().get(k);

				// CINT64D ALIAS_NAME_FIRST

				List<BookmarkDto> bookmarPrinOthrAlias = new ArrayList<BookmarkDto>();
				BookmarkDto bookmarkAliasPrinOthrNmFrst = createBookmark(BookmarkConstants.ALIAS_NAME_FIRST,
						nameDto.getFirstName());
				bookmarPrinOthrAlias.add(bookmarkAliasPrinOthrNmFrst);

				// CINT64D ALIAS_NAME_LAST
				BookmarkDto bookmarkAliasPrinOthrNmLast = createBookmark(BookmarkConstants.ALIAS_NAME_LAST,
						nameDto.getLastName());
				bookmarPrinOthrAlias.add(bookmarkAliasPrinOthrNmLast);
				// CINT64D ALIAS_NAME_MIDDLE
				BookmarkDto bookmarkAliasPrinOthrNmMiddle = createBookmark(BookmarkConstants.ALIAS_NAME_MIDDLE,
						nameDto.getMiddleName());
				bookmarPrinOthrAlias.add(bookmarkAliasPrinOthrNmMiddle);

				fdSubGrpallegPerpAliasInfo.setBookmarkDtoList(bookmarPrinOthrAlias);
			}
			aliasallegPerpNameList.add(fdSubGrpallegPerpAliasInfo);

			allOtherPrinSubGroupList.addAll(aliasallegPerpNameList);

			// CFZCo00;

			if (StringUtils.isNotBlank(otherPrin.getCdPersonSuffix())) {

				FormDataGroupDto formdataOthrPrinComma = createFormDataGroup(FormGroupsConstants.TMPLAT_COMMA,
						FormGroupsConstants.TMPLAT_PRINC_OTHER);
				List<FormDataGroupDto> formdataOthrPrinCommaList = new ArrayList<FormDataGroupDto>();
				formdataOthrPrinCommaList.add(formdataOthrPrinComma);
				allOtherPrinSubGroupList.addAll(allOtherPrinSubGroupList);
			}
			priGrpOtherPrincipal.setFormDataGroupList(allOtherPrinSubGroupList);
			priGrpOtherPrincipalList.add(priGrpOtherPrincipal);
		}
		formDataGroupList.addAll(priGrpOtherPrincipalList);

		/******************************
		 * cfin0104 --> cfzz0201--> cfzz0101 -->cfin0109-->cfzco00
		 ****************************************************************/
		// CFIN0104
		List<FormDataGroupDto> formdataTmplatCollList = new ArrayList<FormDataGroupDto>();
		FormDataGroupDto formdataTmplatColl = createFormDataGroup(FormGroupsConstants.TMPLAT_COLL_OTHER,
				FormConstants.EMPTY_STRING);
		for (PersonDto personColl : cpsIntakeNotificationDto.getPersonListCollateral()) {
			List<BookmarkDto> bookmarktmplatCollList = new ArrayList<BookmarkDto>();
			// CINT66D COLL_AGE
			BookmarkDto bookmarkCollAge = createBookmark(BookmarkConstants.COLL_AGE, personColl.getNbrPersonAge());
			bookmarktmplatCollList.add(bookmarkCollAge);

			// CINT66D COLL_DOB_APPROX
			BookmarkDto bookmarkCollDobApprx = createBookmark(BookmarkConstants.COLL_DOB_APPROX,
					personColl.getIndPersonDobApprox());
			bookmarktmplatCollList.add(bookmarkCollDobApprx);

			// CINT66D COLL_IN_LAW
			BookmarkDto bookmarkCollInLaw = createBookmark(BookmarkConstants.COLL_IN_LAW,
					personColl.getIndStagePersInLaw());
			bookmarktmplatCollList.add(bookmarkCollInLaw);

			// CINT66D COLL_SEX CodeTable CSEX
			BookmarkDto bookmarkSex = createBookmark(BookmarkConstants.COLL_SEX, personColl.getCdPersonSex());
			bookmarktmplatCollList.add(bookmarkSex);

			// CINT66D COLL_DOB
			BookmarkDto bookmarkCollDob = createBookmark(BookmarkConstants.COLL_DOB, personColl.getDob());
			bookmarktmplatCollList.add(bookmarkCollDob);

			// CINT66D COLL_DOD
			BookmarkDto bookmarkCollDod = createBookmark(BookmarkConstants.COLL_DOD, personColl.getDtPersonDeath());
			bookmarktmplatCollList.add(bookmarkCollDod);

			// CINT66D COLL_NAME_SUFFIX Codetable CSUFFIX2
			BookmarkDto bookmarkCollNmSx = createBookmark(BookmarkConstants.COLL_NAME_SUFFIX,
					personColl.getCdPersonSuffix());
			bookmarktmplatCollList.add(bookmarkCollNmSx);

			// CINT66D COLL_RSN Codetable CRSNFDTH
			BookmarkDto bookmarkCollRSN = createBookmark(BookmarkConstants.COLL_RSN, personColl.getCdPersonDeath());
			bookmarktmplatCollList.add(bookmarkCollRSN);

			// CINT66D COLL_ETHNCTY Codetable CETHNIC
			BookmarkDto bookmarkCollEthnicity = createBookmark(BookmarkConstants.COLL_ETHNCTY,
					personColl.getCdPersonEthnicGroup());
			bookmarktmplatCollList.add(bookmarkCollEthnicity);

			// CINT66D COLL_LANG Codetable CLANG
			BookmarkDto bookmarkCollLang = createBookmark(BookmarkConstants.COLL_LANG,
					personColl.getCdPersonLanguage());
			bookmarktmplatCollList.add(bookmarkCollLang);

			// CINT66D COLL_MARITAL Codetable CMARSTAT
			BookmarkDto bookmarkCollMarital = createBookmark(BookmarkConstants.COLL_MARITAL,
					personColl.getCdPersonMaritalStatus());
			bookmarktmplatCollList.add(bookmarkCollMarital);

			// CINT66D COLL_RELTNSP Codetable CRELVICT
			BookmarkDto bookmarkCollRltnsp = createBookmark(BookmarkConstants.COLL_RELTNSP,
					personColl.getCdStagePersRelInt());
			bookmarktmplatCollList.add(bookmarkCollRltnsp);

			// CINT66D COLL_ROLE Codetable CROLES
			BookmarkDto bookmarkCollRole = createBookmarkWithCodesTable(BookmarkConstants.COLL_ROLE,
					personColl.getCdStagePersRole(),ServiceConstants.CROLES);
			bookmarktmplatCollList.add(bookmarkCollRole);

			// CINT66D COLL_NAME_FIRST
			BookmarkDto bookmarkCollNmFirst = createBookmark(BookmarkConstants.COLL_NAME_FIRST,
					personColl.getNmPersonFirst());
			bookmarktmplatCollList.add(bookmarkCollNmFirst);

			// CINT66D COLL_NAME_LAST
			BookmarkDto bookmarkNmLast = createBookmark(BookmarkConstants.COLL_NAME_LAST, personColl.getNmPersonLast());
			bookmarktmplatCollList.add(bookmarkNmLast);

			// CINT66D COLL_NAME_MIDDLE
			BookmarkDto bookmarkCollNmMiddle = createBookmark(BookmarkConstants.COLL_NAME_MIDDLE,
					personColl.getNmPersonMiddle());
			bookmarktmplatCollList.add(bookmarkCollNmMiddle);

			// CINT66D COLL_NOTES
			BookmarkDto bookmarkCollNotes = createBookmark(BookmarkConstants.COLL_NOTES,
					personColl.getTxtStagePersNotes());
			bookmarktmplatCollList.add(bookmarkCollNotes);
			formdataTmplatColl.setBookmarkDtoList(bookmarktmplatCollList);

			List<FormDataGroupDto> allCollateralGroupList = new ArrayList<FormDataGroupDto>();
			// CFZZ0201
			List<FormDataGroupDto> formdataCollTmplatPhList = new ArrayList<FormDataGroupDto>();
			FormDataGroupDto formdataTmplatCollPh = createFormDataGroup(FormGroupsConstants.TMPLAT_PHONE,
					FormGroupsConstants.TMPLAT_COLL_OTHER);
			List<Long> phoneList = cpsIntakeNotificationDto.getPersonPhoneList().stream().map(p -> p.getIdPerson())
					.collect(Collectors.toList());

			if (!TypeConvUtil.isNullOrEmpty(phoneList) && phoneList.contains(personColl.getIdPerson())) {

				int i = phoneList.indexOf(personColl.getIdPerson());

				PhoneInfoDto personInfoDto = cpsIntakeNotificationDto.getPersonPhoneList().get(i);
				List<BookmarkDto> bookmarkTmpltPhList = new ArrayList<BookmarkDto>();
				// CINT62D PHONE_NUMBER
				BookmarkDto bookmarkPhNum = createBookmark(BookmarkConstants.PHONE_NUMBER,
						FormattingUtils.formatPhoneWithEmptyPhone(personInfoDto.getNbrPersonPhone()));
				bookmarkTmpltPhList.add(bookmarkPhNum);
				// CINT62D PHONE_NUM_EXTENSION
				BookmarkDto bookmarkPhNumExtnsn = createBookmark(BookmarkConstants.PHONE_NUM_EXTENSION,
						personInfoDto.getNbrPersonPhoneExtension());
				bookmarkTmpltPhList.add(bookmarkPhNumExtnsn);
				// CINT62D PHONE_TYPE Codetable CPHNTYP
				BookmarkDto bookmarkPhType = createBookmarkWithCodesTable(BookmarkConstants.PHONE_TYPE,
						personInfoDto.getCdPersonPhoneType(),ServiceConstants.CPHNTYP);
				bookmarkTmpltPhList.add(bookmarkPhType);
				// CINT62D PHONE_NOTES
				BookmarkDto bookmarkPhNotes = createBookmark(BookmarkConstants.PHONE_NOTES,
						personInfoDto.getTxtPersonPhoneComments());
				bookmarkTmpltPhList.add(bookmarkPhNotes);
				formdataTmplatCollPh.setBookmarkDtoList(bookmarkTmpltPhList);
			}
			formdataCollTmplatPhList.add(formdataTmplatCollPh);
			allCollateralGroupList.addAll(formdataCollTmplatPhList);

			// CFZZ0101
			FormDataGroupDto formdataCollAddrFull = createFormDataGroup(FormGroupsConstants.TMPLAT_ADDR_FULL,
					FormGroupsConstants.TMPLAT_COLL_OTHER);
			List<FormDataGroupDto> formdataCollAddrFullList = new ArrayList<FormDataGroupDto>();

			List<Long> addressList = cpsIntakeNotificationDto.getPersonAddressLinkList().stream()
					.map(addr -> addr.getIdPerson()).collect(Collectors.toList());

			if (!TypeConvUtil.isNullOrEmpty(addressList) && addressList.contains(personColl.getIdPerson())) {
				int j = addressList.indexOf(personColl.getIdPerson());
				PersonAddrLinkDto personAddrLinkDto = cpsIntakeNotificationDto.getPersonAddressLinkList().get(j);

				List<BookmarkDto> bookmarkAddrCollFullList = new ArrayList<BookmarkDto>();
				// CINT63D ADDR_ZIP
				BookmarkDto bookmarkAddrZip = createBookmark(BookmarkConstants.ADDR_ZIP,
						personAddrLinkDto.getAddrPersonAddrZip());
				bookmarkAddrCollFullList.add(bookmarkAddrZip);
				// CINT63D ADDR_CITY
				BookmarkDto bookmarkAddrCity = createBookmark(BookmarkConstants.ADDR_CITY,
						personAddrLinkDto.getAddrPersonAddrCity());
				bookmarkAddrCollFullList.add(bookmarkAddrCity);
				// CINT63D ADDR_ATTN
				BookmarkDto bookmarkAddrAttn = createBookmark(BookmarkConstants.ADDR_ATTN,
						personAddrLinkDto.getPersAddressAttention());
				bookmarkAddrCollFullList.add(bookmarkAddrAttn);
				// CINT63D ADDR_LN_1
				BookmarkDto bookmarkAddrLn1 = createBookmark(BookmarkConstants.ADDR_LN_1,
						personAddrLinkDto.getAddrPersAddrStLn1());
				bookmarkAddrCollFullList.add(bookmarkAddrLn1);
				// CINT63D ADDR_LN_2
				BookmarkDto bookmarkAddrLn2 = createBookmark(BookmarkConstants.ADDR_LN_2,
						personAddrLinkDto.getAddrPersAddrStLn2());
				bookmarkAddrCollFullList.add(bookmarkAddrLn2);
				// CINT63D ADDR_COUNTY Codetable CCOUNT
				BookmarkDto bookmarkAddrCounty = createBookmark(BookmarkConstants.ADDR_COUNTY,
						personAddrLinkDto.getCdPersonAddrCounty());
				bookmarkAddrCollFullList.add(bookmarkAddrCounty);
				// CINT63D ADDR_STATE
				BookmarkDto bookmarkAddrState = createBookmark(BookmarkConstants.ADDR_STATE,
						personAddrLinkDto.getCdPersonAddrState());
				bookmarkAddrCollFullList.add(bookmarkAddrState);
				// CINT63D ADDR_TYPE Codetable CADDRTYP
				BookmarkDto bookmarkAddrType = createBookmarkWithCodesTable(BookmarkConstants.ADDR_TYPE,
						personAddrLinkDto.getCdPersAddrLinkType(),ServiceConstants.CADDRTYP);
				bookmarkAddrCollFullList.add(bookmarkAddrType);
				// CINT63D ADDR_NOTES
				BookmarkDto bookmarkAddrNotes = createBookmark(BookmarkConstants.ADDR_NOTES,
						personAddrLinkDto.getPersAddrCmnts());
				bookmarkAddrCollFullList.add(bookmarkAddrNotes);
				formdataCollAddrFull.setBookmarkDtoList(bookmarkAddrCollFullList);
			}
			formdataCollAddrFullList.add(formdataCollAddrFull);

			allCollateralGroupList.addAll(formdataCollAddrFullList);

			// CFIN0109
			FormDataGroupDto formdataCollAlias = createFormDataGroup(FormGroupsConstants.TMPLAT_ALIAS,
					FormGroupsConstants.TMPLAT_COLL_OTHER);
			List<FormDataGroupDto> formdataCollAliasList = new ArrayList<FormDataGroupDto>();
			List<Integer> aliasReporterList = cpsIntakeNotificationDto.getNameDto().stream()
					.map(alias -> alias.getIdPerson()).collect(Collectors.toList());

			if (aliasReporterList.contains(personColl.getIdPerson())) {
				int k = cpsIntakeNotificationDto.getNameDto().indexOf(personColl.getIdPerson());
				NameDto nameDto = cpsIntakeNotificationDto.getNameDto().get(k);
				// CINT64D ALIAS_NAME_FIRST

				List<BookmarkDto> bookmarCollAlias = new ArrayList<BookmarkDto>();
				BookmarkDto bookmarkAliasNmFrst = createBookmark(BookmarkConstants.ALIAS_NAME_FIRST,
						nameDto.getFirstName());
				bookmarCollAlias.add(bookmarkAliasNmFrst);

				// CINT64D ALIAS_NAME_LAST
				BookmarkDto bookmarkAliasNmLast = createBookmark(BookmarkConstants.ALIAS_NAME_LAST,
						nameDto.getLastName());
				bookmarCollAlias.add(bookmarkAliasNmLast);
				// CINT64D ALIAS_NAME_MIDDLE
				BookmarkDto bookmarkAliasNmMiddle = createBookmark(BookmarkConstants.ALIAS_NAME_MIDDLE,
						nameDto.getMiddleName());
				bookmarCollAlias.add(bookmarkAliasNmMiddle);

				formdataCollAlias.setBookmarkDtoList(bookmarCollAlias);
			}
			formdataCollAliasList.add(formdataCollAlias);
			allCollateralGroupList.addAll(formdataCollAliasList);

			// CFZCo00;

			if (FormConstants.EMPTY_STRING == personColl.getCdPersonSuffix()) {

				FormDataGroupDto formdataCommaColl = createFormDataGroup(FormGroupsConstants.TMPLAT_COMMA,
						FormGroupsConstants.TMPLAT_COLL_OTHER);
				List<FormDataGroupDto> formdataCommaCollList = new ArrayList<FormDataGroupDto>();
				formdataCommaCollList.add(formdataCommaColl);
				allCollateralGroupList.addAll(formdataCommaCollList);

			}
			formdataTmplatColl.setFormDataGroupList(allCollateralGroupList);
			formdataTmplatCollList.add(formdataTmplatColl);
		}
		formDataGroupList.addAll(formdataTmplatCollList);
		/**
		 * Populating the Fields data into prefill data
		 */
		List<BookmarkDto> bookmarkNonFrmGrpList = new ArrayList<BookmarkDto>();
		// CINT65D CALL_NARR_WORKER_SAFETY
		BookmarkDto bookmarkWorkerSafety = createBookmark(BookmarkConstants.CALL_NARR_WORKER_SAFETY,
				cpsIntakeNotificationDto.getIncomingStageDetailsDto().getTxtIncmgWorkerSafety());
		bookmarkNonFrmGrpList.add(bookmarkWorkerSafety);

		// CINT65D CALL_NARR_SENSITIVE_ISSUE
		BookmarkDto bookmarkSensitiveIssue = createBookmark(BookmarkConstants.CALL_NARR_SENSITIVE_ISSUE,
				cpsIntakeNotificationDto.getIncomingStageDetailsDto().getTxtIncmgSensitive());
		bookmarkNonFrmGrpList.add(bookmarkSensitiveIssue);

		// CINT65D CALL_NARR_SUSP_METH_LAB
		BookmarkDto bookmarkNarrSuspMeth = createBookmark(BookmarkConstants.CALL_NARR_SUSP_METH_LAB,
				cpsIntakeNotificationDto.getIncomingStageDetailsDto().getTxtIncmgSuspMeth());
		bookmarkNonFrmGrpList.add(bookmarkNarrSuspMeth);

		// CINT65D SUM_SENSITIVE_ISSUE
		BookmarkDto bookmarkSensitvIssue = createBookmark(BookmarkConstants.SUM_SENSITIVE_ISSUE,
				cpsIntakeNotificationDto.getIncomingStageDetailsDto().getIndIncmgSensitive());
		bookmarkNonFrmGrpList.add(bookmarkSensitvIssue);

		// CINT65D SUM_SUSP_METH_LAB
		BookmarkDto bookmarkSuspMeth = createBookmark(BookmarkConstants.SUM_SUSP_METH_LAB,
				cpsIntakeNotificationDto.getIncomingStageDetailsDto().getIndIncmgSuspMeth());
		bookmarkNonFrmGrpList.add(bookmarkSuspMeth);

		// CINT65D SUM_WORKER_SAFETY_ISSUES
		BookmarkDto bookmarkSafetyIssues = createBookmark(BookmarkConstants.SUM_WORKER_SAFETY_ISSUES,
				cpsIntakeNotificationDto.getIncomingStageDetailsDto().getIndIncmgWorkerSafety());
		bookmarkNonFrmGrpList.add(bookmarkSafetyIssues);

		// CINT65D SUM_DATE_RPTED
		BookmarkDto bookmarkSumDtRpted = createBookmark(BookmarkConstants.SUM_DATE_RPTED,
				cpsIntakeNotificationDto.getIncomingStageDetailsDto().getDtIncoming());
		bookmarkNonFrmGrpList.add(bookmarkSumDtRpted);

		// CINT65D CONF_DATE_OF_REPORT
		BookmarkDto bookmarkDtRpted = createBookmark(BookmarkConstants.CONF_DATE_OF_REPORT,
				cpsIntakeNotificationDto.getIncomingStageDetailsDto().getDtIncoming());
		bookmarkNonFrmGrpList.add(bookmarkDtRpted);

		// CINT65D SUM_WORKER_EXTENSION
		BookmarkDto bookmarkSumWrkrExtnsn = createBookmark(BookmarkConstants.SUM_WORKER_EXTENSION,
				cpsIntakeNotificationDto.getIncomingStageDetailsDto().getNbrIncmgWorkerExt());
		bookmarkNonFrmGrpList.add(bookmarkSumWrkrExtnsn);

		// CINT65D HEADER_EXTENSION
		BookmarkDto bookmarkSumHeaderExtnsn = createBookmark(BookmarkConstants.HEADER_EXTENSION,
				cpsIntakeNotificationDto.getIncomingStageDetailsDto().getNbrIncmgWorkerExt());
		bookmarkNonFrmGrpList.add(bookmarkSumHeaderExtnsn);

		// CINT65D SUM_WORKER_PHONE
		BookmarkDto bookmarkSumWrkrPh = createBookmark(BookmarkConstants.SUM_WORKER_PHONE,
				FormattingUtils.formatPhoneWithEmptyPhone(cpsIntakeNotificationDto.getIncomingStageDetailsDto().getNbrIncmgWorkerPhone()));
		bookmarkNonFrmGrpList.add(bookmarkSumWrkrPh);

		// CINT65D HEADER_PHONE
		BookmarkDto bookmarkHeaderPh = createBookmark(BookmarkConstants.HEADER_PHONE,
				FormattingUtils.formatPhoneWithEmptyPhone(cpsIntakeNotificationDto.getIncomingStageDetailsDto().getNbrIncmgWorkerPhone()));
		bookmarkNonFrmGrpList.add(bookmarkHeaderPh);

		// CINT65D SUM_WORKER_CITY
		BookmarkDto bookmarkSumWrkrCity = createBookmark(BookmarkConstants.SUM_WORKER_CITY,
				cpsIntakeNotificationDto.getIncomingStageDetailsDto().getAddrIncmgWorkerCity());
		bookmarkNonFrmGrpList.add(bookmarkSumWrkrCity);

		// CINT65D SUM_PRIM_ALLEG
		BookmarkDto bookmarkSumPrimAlleg = createBookmark(BookmarkConstants.SUM_PRIM_ALLEG,
				cpsIntakeNotificationDto.getIncomingStageDetailsDto().getCdIncmgAllegType());
		bookmarkNonFrmGrpList.add(bookmarkSumPrimAlleg);

		// CINT65D SUM_SPCL_HANDLING
		BookmarkDto bookmarkSumSpclHandling = createBookmark(BookmarkConstants.SUM_SPCL_HANDLING,
				cpsIntakeNotificationDto.getIncomingStageDetailsDto().getCdIncmgSpecHandling());
		bookmarkNonFrmGrpList.add(bookmarkSumSpclHandling);

		// CINT65D SUM_PRIORITY_DETERM
		BookmarkDto bookmarkSumPriorityDetrmntn = createBookmark(BookmarkConstants.SUM_PRIORITY_DETERM,
				cpsIntakeNotificationDto.getIncomingStageDetailsDto().getCdStageCurrPriority());
		bookmarkNonFrmGrpList.add(bookmarkSumPriorityDetrmntn);

		// CINT65D NOTIFY_ACTION_TAKEN_BY_CPS
		String closedReason = cpsIntakeNotificationDto.getIncomingStageDetailsDto().getCdStageReasonClosed();
		if (!ObjectUtils.isEmpty(closedReason) && !ServiceConstants.CCLOSUR1_00.equals(closedReason)
				&& !ServiceConstants.CCLOSUR1_01.equals(closedReason)
				&& !ServiceConstants.CCLOSUR1_02.equals(closedReason)) {
			BookmarkDto bookmarkNotifyCPSActn = createBookmarkWithCodesTable(
					BookmarkConstants.NOTIFY_ACTION_TAKEN_BY_CPS,
					cpsIntakeNotificationDto.getIncomingStageDetailsDto().getCdStageReasonClosed(),
					ServiceConstants.CCLOSUR2);
			bookmarkNonFrmGrpList.add(bookmarkNotifyCPSActn);

			BookmarkDto bookmarkSumRsnForClosr = createBookmarkWithCodesTable(BookmarkConstants.SUM_RSN_FOR_CLOSR,
					cpsIntakeNotificationDto.getIncomingStageDetailsDto().getCdStageReasonClosed(),
					ServiceConstants.CCLOSUR1);
			bookmarkNonFrmGrpList.add(bookmarkSumRsnForClosr);
		}
		
		// CINT65D SUM_WORKER_TAKING_INTAKE
		BookmarkDto bookmarkSumTakingIntk = createBookmark(BookmarkConstants.SUM_WORKER_TAKING_INTAKE,
				cpsIntakeNotificationDto.getIncomingStageDetailsDto().getNmIncmgWorkerName());
		bookmarkNonFrmGrpList.add(bookmarkSumTakingIntk);

		// CINT65D NOTIFY_TO
		BookmarkDto bookmarkNotifyTo = createBookmark(BookmarkConstants.NOTIFY_TO,
				cpsIntakeNotificationDto.getIncomingStageDetailsDto().getNmIncmgJurisdiction());
		bookmarkNonFrmGrpList.add(bookmarkNotifyTo);

		// CINT65D SUM_LE_JURIS
		BookmarkDto bookmarkSumLeJuris = createBookmark(BookmarkConstants.SUM_LE_JURIS,
				cpsIntakeNotificationDto.getIncomingStageDetailsDto().getNmIncmgJurisdiction());
		bookmarkNonFrmGrpList.add(bookmarkSumLeJuris);

		// CINT65D CONF_CASE_NAME
		BookmarkDto bookmarkConfCaseNm = createBookmark(BookmarkConstants.CONF_CASE_NAME,
				cpsIntakeNotificationDto.getIncomingStageDetailsDto().getNmStage());
		bookmarkNonFrmGrpList.add(bookmarkConfCaseNm);

		// CINT65D FAX_CASE_NAME
		BookmarkDto bookmarkFaxCaseNm = createBookmark(BookmarkConstants.FAX_CASE_NAME,
				cpsIntakeNotificationDto.getIncomingStageDetailsDto().getNmStage());
		bookmarkNonFrmGrpList.add(bookmarkFaxCaseNm);

		// CINT65D TITLE_CASE_NAME
		BookmarkDto bookmarkTitleCaseNm = createBookmark(BookmarkConstants.TITLE_CASE_NAME,
				cpsIntakeNotificationDto.getIncomingStageDetailsDto().getNmStage());
		bookmarkNonFrmGrpList.add(bookmarkTitleCaseNm);

		// CINT65D CONF_TIME_OF_REPORT
		BookmarkDto bookmarkConfTimeOfReport = createBookmark(BookmarkConstants.CONF_TIME_OF_REPORT,
				cpsIntakeNotificationDto.getIncomingStageDetailsDto().getTmIncmgCall());
		bookmarkNonFrmGrpList.add(bookmarkConfTimeOfReport);

		// CINT65D SUM_TIME_RPTED
		BookmarkDto bookmarkSumTimeRpted = createBookmark(BookmarkConstants.SUM_TIME_RPTED,
				cpsIntakeNotificationDto.getIncomingStageDetailsDto().getTmIncmgCall());
		bookmarkNonFrmGrpList.add(bookmarkSumTimeRpted);

		// CINT65D TITLE_CASE_NUMBER
		BookmarkDto bookmarkTitleCaseNumber = createBookmark(BookmarkConstants.TITLE_CASE_NUMBER,
				cpsIntakeNotificationDto.getIncomingStageDetailsDto().getIdCase());
		bookmarkNonFrmGrpList.add(bookmarkTitleCaseNumber);

		// CINT65D CONF_CASE_NUMBER
		BookmarkDto bookmarConfCaseNum = createBookmark(BookmarkConstants.CONF_CASE_NUMBER,
				cpsIntakeNotificationDto.getIncomingStageDetailsDto().getIdCase());
		bookmarkNonFrmGrpList.add(bookmarConfCaseNum);

		// CINT65D FAX_CASE_ID
		BookmarkDto bookmarkFaxCaseID = createBookmark(BookmarkConstants.FAX_CASE_ID,
				cpsIntakeNotificationDto.getIncomingStageDetailsDto().getIdCase());
		bookmarkNonFrmGrpList.add(bookmarkFaxCaseID);

		// CINT65D FAX_ID
		BookmarkDto bookmarkFaxId = createBookmark(BookmarkConstants.FAX_ID,
				cpsIntakeNotificationDto.getIncomingStageDetailsDto().getIdStage());
		bookmarkNonFrmGrpList.add(bookmarkFaxId);

		// CINT65D CALL_NARR_BLOB
		BookmarkDto bookmarkCallNarrBlob = createBookmark(BookmarkConstants.CALL_NARR_BLOB,
				cpsIntakeNotificationDto.getIncomingStageDetailsDto().getIdStage());
		bookmarkNonFrmGrpList.add(bookmarkCallNarrBlob);

		// CINT65D SUM_INTAKE_NUM
		BookmarkDto bookmarkSumIntakeNum = createBookmark(BookmarkConstants.SUM_INTAKE_NUM,
				cpsIntakeNotificationDto.getIncomingStageDetailsDto().getIdStage());
		bookmarkNonFrmGrpList.add(bookmarkSumIntakeNum);

		// CINT70D CONF_ADDR_STATE
		BookmarkDto bookmarkConfAddrState = createBookmark(BookmarkConstants.CONF_ADDR_STATE,
				cpsIntakeNotificationDto.getPersonAddrLinkDto().getCdPersonAddrState());
		bookmarkNonFrmGrpList.add(bookmarkConfAddrState);

		// CINT70D CONF_ADDR_ZIP
		BookmarkDto bookmarkConfAddrZip = createBookmark(BookmarkConstants.CONF_ADDR_ZIP,
				cpsIntakeNotificationDto.getPersonAddrLinkDto().getAddrPersonAddrZip());
		bookmarkNonFrmGrpList.add(bookmarkConfAddrZip);

		// CINT70D CONF_ADDR_CITY
		BookmarkDto bookmarkConfAddrCity = createBookmark(BookmarkConstants.CONF_ADDR_CITY,
				cpsIntakeNotificationDto.getPersonAddrLinkDto().getAddrPersonAddrCity());
		bookmarkNonFrmGrpList.add(bookmarkConfAddrCity);

		// CINT70D CONF_ADDR_LN_1
		BookmarkDto bookmarkConfAddrLn1 = createBookmark(BookmarkConstants.CONF_ADDR_LN_1,
				cpsIntakeNotificationDto.getPersonAddrLinkDto().getAddrPersAddrStLn1());
		bookmarkNonFrmGrpList.add(bookmarkConfAddrLn1);

		// CINT70D CONF_ADDR_LN_2
		BookmarkDto bookmarkConfAddrln2 = createBookmark(BookmarkConstants.CONF_ADDR_LN_2,
				cpsIntakeNotificationDto.getPersonAddrLinkDto().getAddrPersAddrStLn2());
		bookmarkNonFrmGrpList.add(bookmarkConfAddrln2);

		// CINT71D SUM_LE_NOTIFY_DATE officePhoneDto
		BookmarkDto bookmarkSumLeNotifyDt = createBookmark(BookmarkConstants.SUM_LE_NOTIFY_DATE,DateUtils.stringDt(new Date()));
		bookmarkNonFrmGrpList.add(bookmarkSumLeNotifyDt);

		// CINT71D NOTIFY_DATE
		BookmarkDto bookmarkNotifyDt = createBookmark(BookmarkConstants.NOTIFY_DATE,DateUtils.stringDt(new Date()));
		bookmarkNonFrmGrpList.add(bookmarkNotifyDt);

		if (!ObjectUtils.isEmpty(cpsIntakeNotificationDto.getOfficePhoneDto())) {
			String officePhoneNumber = ServiceConstants.EMPTY_STR;
			if(!ObjectUtils.isEmpty(cpsIntakeNotificationDto.getOfficePhoneDto().getOfficePhoneNumber())){
				officePhoneNumber = String.valueOf(cpsIntakeNotificationDto.getOfficePhoneDto().getOfficePhoneNumber());
			}
			// CINT71D NOTIFY_PHONE
			BookmarkDto bookmarkNotifyPh = createBookmark(BookmarkConstants.NOTIFY_PHONE,
					FormattingUtils.formatPhone(officePhoneNumber));
			bookmarkNonFrmGrpList.add(bookmarkNotifyPh);
			// CINT71D NOTIFY_EXTENSION
			BookmarkDto bookmarkNotifyExtnsn = createBookmark(BookmarkConstants.NOTIFY_EXTENSION,
					cpsIntakeNotificationDto.getOfficePhoneDto().getOfficePhoneExtension());
			bookmarkNonFrmGrpList.add(bookmarkNotifyExtnsn);

			// CINT71D NOTIFY_FROM
			if (!ObjectUtils.isEmpty(cpsIntakeNotificationDto.getOfficePhoneDto())) {
				BookmarkDto bookmarkNotifyFrom = createBookmark(BookmarkConstants.NOTIFY_FROM,
						cpsIntakeNotificationDto.getOfficePhoneDto().getNmOfficeName());
				bookmarkNonFrmGrpList.add(bookmarkNotifyFrom);
			}
		}

		// CSEC68D FAX_NUM
		BookmarkDto bookmarkFaxNum = createBookmark(BookmarkConstants.FAX_NUM,
				cpsIntakeNotificationDto.getCaseDtlsDto().getResourcePhoneNumber());
		bookmarkNonFrmGrpList.add(bookmarkFaxNum);

		// CSEC68D FAX_RSRC_NAME
		BookmarkDto bookmarkFaxRSRCNm = createBookmark(BookmarkConstants.FAX_RSRC_NAME,
				cpsIntakeNotificationDto.getCaseDtlsDto().getNmResource());
		bookmarkNonFrmGrpList.add(bookmarkFaxRSRCNm);
		List<BlobDataDto> blobDataDtoForCallNarrative = new ArrayList<BlobDataDto>();
		if (!ObjectUtils.isEmpty(cpsIntakeNotificationDto.getIncomingStageDetailsDto().getIdStage())) {
			BlobDataDto blobCallNarrative = createBlobData(BookmarkConstants.CALL_NARR_BLOB, "INCOMING_NARRATIVE_VIEW",
					cpsIntakeNotificationDto.getIncomingStageDetailsDto().getIdStage().intValue());
			blobDataDtoForCallNarrative.add(blobCallNarrative);
		}
		PreFillDataServiceDto preFillData = new PreFillDataServiceDto();
		preFillData.setBookmarkDtoList(bookmarkNonFrmGrpList);
		preFillData.setFormDataGroupList(formDataGroupList);
		preFillData.setBlobDataDtoList(blobDataDtoForCallNarrative);
		return preFillData;
	}

}
