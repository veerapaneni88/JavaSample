package us.tx.state.dfps.service.forms.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import us.tx.state.dfps.common.exception.FormsException;
import us.tx.state.dfps.common.web.WebConstants;
import us.tx.state.dfps.service.casepackage.dto.ContactNarrativeDto;
import us.tx.state.dfps.service.common.BookmarkConstants;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.FormGroupsConstants;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.contact.dto.ContactNarrGuideDto;
import us.tx.state.dfps.service.contact.dto.ContactNarraLogFormDto;
import us.tx.state.dfps.service.cpsinv.dto.CpsInvComDto;
import us.tx.state.dfps.service.cpsinv.dto.CpsInvContactSdmSafetyAssessDto;
import us.tx.state.dfps.service.forms.dto.BlobDataDto;
import us.tx.state.dfps.service.forms.dto.BookmarkDto;
import us.tx.state.dfps.service.forms.dto.FormDataGroupDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.xmlstructs.outputstructs.InrSafetyFieldDto;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<Prefill
 * class for Contact Log Narrative form (cfsd0700)> Feb 23, 2018- 12:52:07 PM ©
 * 2017 Texas Department of Family and Protective Services
 */
@Component
public class ContactLogNarrativeFormPrefill extends DocumentServiceUtil {
	private static final String CFIN0400 = "CFIN0400";
	private static final String CFIN0100 = "CFIN0100";
	private static final String CFIN0200 = "CFIN0200";
	private static final String CIV36O00 = "civ36o00";
	private static final String ARREPORT = "ARREPORT";
	
	private static final String CONTACT = "CON";
	private static final String NULL_STRING = "";
	private static final Set<String> KINSHIP_NOTIFICATION = new HashSet<String>(
			Arrays.asList(new String[] { CodesConstant.CCNTCTYP_GKIN, // Kinship
					// Notification
					CodesConstant.CCNTCTYP_GKNS // Kinship Notification Spanish
			}));

	private static final Set<String> GUARDIANSHIP_REFERRAL = new HashSet<String>(
			Arrays.asList(new String[] { CodesConstant.CCNTCTYP_CBGR, // APS
																		// Guardianship
																		// Referral
					CodesConstant.CCNTCTYP_CCGR // CPS Guardianship Referral
			}));
	private static final String CONTACTS_NAMESLABELTEXT = "Names:";
	private static final String CONTACTS_FTFEXRSNLABELTEXT = "Principals/Collaterals\u00A0 Not\u00A0 Contacted:"; // artf129590
	public static final Set<String> CONTACTS_AR = new HashSet<String>(Arrays.asList(
			new String[] { CodesConstant.CCNTCTYP_LREG, CodesConstant.CCNTCTYP_LNOT, CodesConstant.CCNTCTYP_LTRN }));

	public static final Set<String> GUIDE_TOPICS_CONTACTS = new HashSet<String>(
			Arrays.asList(new String[] { CodesConstant.CCNTCTYP_GREG, CodesConstant.CCNTCTYP_BREG }));

	private static final String DATE_TIME_FORMAT = "MM/dd/yyyy hh:mm a";
	private static final String DATE_FORMAT = "MM/dd/yyyy";
  private static final String INVOLVED_YOUTH = "Involved Children/Youth";
	@Autowired
	MessageSource messageSource;

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
	@Override
	public PreFillDataServiceDto returnPrefillData(Object parentDtoobj) {

		ContactNarraLogFormDto contactNarraLogFormDto = (ContactNarraLogFormDto) parentDtoobj;
		PreFillDataServiceDto preFillDataServiceDto = new PreFillDataServiceDto();
			
		if (null == contactNarraLogFormDto.getContactNarrGuideDtoList()) {
			contactNarraLogFormDto.setContactNarrGuideDtoList(new ArrayList<ContactNarrGuideDto>());
		}
		// creating a form data group list for the primary group(pr)
		List<FormDataGroupDto> tmpFormDataGrpContactList = new ArrayList<FormDataGroupDto>();

		// Populating the non-form group data into prefill data. !!bookmarks
		List<BookmarkDto> bookmarkDtoDefaultDtoList = new ArrayList<BookmarkDto>();

		BookmarkDto bkTitleCaseName = createBookmark(BookmarkConstants.TITLE_CASE_NAME,
				contactNarraLogFormDto.getStageDto().getNmCase());
		bookmarkDtoDefaultDtoList.add(bkTitleCaseName);

		BookmarkDto bkTitleCaseNumber = createBookmark(BookmarkConstants.TITLE_CASE_NUMBER,
				contactNarraLogFormDto.getStageDto().getIdCase());
		bookmarkDtoDefaultDtoList.add(bkTitleCaseNumber);

		if (!ObjectUtils.isEmpty(contactNarraLogFormDto.getDtSampleFrom())) {
			BookmarkDto bkContactDateFrom = createBookmark(BookmarkConstants.CONTACT_DATE_FROM,
					dateFormat(contactNarraLogFormDto.getDtSampleFrom(), DATE_FORMAT));
			bookmarkDtoDefaultDtoList.add(bkContactDateFrom);
		}

		if (!ObjectUtils.isEmpty(contactNarraLogFormDto.getDtSampleTo())) {
			BookmarkDto bkContactDateTo = createBookmark(BookmarkConstants.CONTACT_DATE_TO,
					dateFormat(contactNarraLogFormDto.getDtSampleTo(), DATE_FORMAT));
			bookmarkDtoDefaultDtoList.add(bkContactDateTo);
		}
		// end non-form group
		List<CpsInvContactSdmSafetyAssessDto> contacts = contactNarraLogFormDto.getContactSdmSafetyAssessDtoList();
		Date dtSampleFrom = contactNarraLogFormDto.getDtSampleFrom();
		Date dtSampleTo = contactNarraLogFormDto.getDtSampleTo();
		//Warranty Defect - 11939  - To include the ToDate in the Contact Fetch Query
		Calendar cal = Calendar.getInstance(); 
		cal.setTime(dtSampleTo); 
		cal.add(Calendar.DATE, 1);
		dtSampleTo = cal.getTime();
		
		if (!ObjectUtils.isEmpty(contacts) && contacts.size() > 0) {
			for (CpsInvContactSdmSafetyAssessDto contact : contacts) {
				if ((contact.getDtOccured().after(dtSampleFrom)
						|| contact.getDtOccured().equals(dtSampleFrom))
						&& (contact.getDtOccured().before(dtSampleTo))) {
					// set the linked names
					List<String> contactNamesList = new ArrayList<String>();
					if (!ObjectUtils.isEmpty(contactNarraLogFormDto.getCpsInvComDtoList())
							&& contactNarraLogFormDto.getCpsInvComDtoList().size() > 0) {
						for (CpsInvComDto names : contactNarraLogFormDto.getCpsInvComDtoList()) {
							if (contact.getIdEvent().equals(names.getIdEvent())) {
								contactNamesList.add(names.getNmPersonFull());
							}
						}
						contact.setContactNamesList(contactNamesList);
					} // contactNames
						// contact is either contact or sdm as contact
					if (CONTACT.equals(contact.getCdScrType())) {
						// sdm contacts from dao not used here (in INV, A-R
						// reports use same dao to display sdm as contacts)
						populateContactPrefill(tmpFormDataGrpContactList, contact, contactNarraLogFormDto);

					}
				}
			}
		}
		preFillDataServiceDto.setBookmarkDtoList(bookmarkDtoDefaultDtoList);
		preFillDataServiceDto.setFormDataGroupList(tmpFormDataGrpContactList);
		if(CollectionUtils.isEmpty(preFillDataServiceDto.getFormDataGroupList())
				&& !CFIN0400.equalsIgnoreCase(contactNarraLogFormDto.getDocType())
				&& !CFIN0100.equalsIgnoreCase(contactNarraLogFormDto.getDocType())
				&& !CFIN0200.equalsIgnoreCase(contactNarraLogFormDto.getDocType())
				&& !CIV36O00.equalsIgnoreCase(contactNarraLogFormDto.getDocType())
				&& !ARREPORT.equalsIgnoreCase(contactNarraLogFormDto.getDocType())){
			throw new FormsException(messageSource.getMessage("contactSearchNarrative.noRecord", null, Locale.US));
		}
		return preFillDataServiceDto;
	}

	/**
	 * 
	 * Method Name: populateContactContacts Method Description:
	 * 
	 * @param preFillDataServiceDto
	 * @param contact
	 * @param contactNarraLogFormDto
	 */
	private void populateContactPrefill(List<FormDataGroupDto> tmpFormDataGrpList,
			CpsInvContactSdmSafetyAssessDto contact, ContactNarraLogFormDto contactNarraLogFormDto) {
		List<String> contactsConNames = contact.getContactNamesList();
		List<ContactNarrGuideDto> contactGuideList = contactNarraLogFormDto.getContactNarrGuideDtoList();
		List<FormDataGroupDto> tmpFormDataGrpContactList = new ArrayList<FormDataGroupDto>();
		boolean typeNameLabel = true;
		FormDataGroupDto formDataGroupDto = createFormDataGroup(FormGroupsConstants.TMPLAT_CONTACTS, NULL_STRING);
		List<BookmarkDto> bookmarkTmplatContacts = new ArrayList<BookmarkDto>();
		BookmarkDto bkConId = createBookmark(BookmarkConstants.CONTACTS_ID, contact.getIdEvent());
		BookmarkDto bkCdStage = createBookmark(BookmarkConstants.CONTACTS_CDSTAGE, contact.getCdStage());
		if (!(GUARDIANSHIP_REFERRAL.contains(contact.getCdContactType())
				|| KINSHIP_NOTIFICATION.contains(contact.getCdContactType()))) {
			BookmarkDto bkCdNarr = createBookmark(BookmarkConstants.CONTACTS_INDNARR, contact.getIndNarr());
			bookmarkTmplatContacts.add(bkCdNarr);
		}
    bookmarkTmplatContacts.add(bkCdStage);
    bookmarkTmplatContacts.add(bkConId);

    // CANIRSP-495 Bug a few fields are not needed - only display these fields if contact type is not INR version 2.
    if (!WebConstants.INR_CONTACT_TYPES.contains(contact.getCdContactType()) || ObjectUtils.isEmpty(contact.getDtNotification())) {
      // Contact Date and Time
      FormDataGroupDto contactsNonInrGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_CONTACTS_NON_INR_GRP,
          FormGroupsConstants.TMPLAT_CONTACTS);
      tmpFormDataGrpContactList.add(contactsNonInrGroup);
      List<BookmarkDto> bkStaffingList = new ArrayList<>();
      contactsNonInrGroup.setBookmarkDtoList(bkStaffingList);

      BookmarkDto bkContactOccurred = createBookmark(BookmarkConstants.CONTACTS_OCCURRED,
          dateFormat(contact.getDtOccured(), DATE_TIME_FORMAT));
      BookmarkDto bkConBy = createBookmark(BookmarkConstants.CONTACTS_BY, contact.getNmEmployeeFull());
      // note CON_NAME_FIRST and CON_NAME_LAST seem unused
      bkStaffingList.add(bkContactOccurred);
      bkStaffingList.add(bkConBy);
    }

		// display if Client has mentioned contact Time
		List<FormDataGroupDto> tmpFormDatacontTimeNmGrpList = new ArrayList<FormDataGroupDto>();
		if (ServiceConstants.Y.equals(contact.getIndClientTime()) && !ObjectUtils.isEmpty(contact.getEstContactHours())
				&& (0 < contact.getEstContactHours() || 0 < contact.getEstContactMins())) {
			FormDataGroupDto contactsTimeGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_CONTACTS_CLIENTTIME,
					FormGroupsConstants.TMPLAT_CONTACTS);
			List<BookmarkDto> bookmarkContactTimeGroup = new ArrayList<BookmarkDto>();
			BookmarkDto bkConTimeHrs = createBookmark(BookmarkConstants.CONTACTS_CLIENTTIME_HRS,
					contact.getEstContactHours());
			BookmarkDto bkConTimeMin = createBookmark(BookmarkConstants.CONTACTS_CLIENTTIME_MIN,
					contact.getEstContactMins());
			bookmarkContactTimeGroup.add(bkConTimeHrs);
			bookmarkContactTimeGroup.add(bkConTimeMin);
			contactsTimeGroup.setBookmarkDtoList(bookmarkContactTimeGroup);
			// if displaying Client Time, also (if) display names label here
			// instead of after Type
			typeNameLabel = false;
			if (!ObjectUtils.isEmpty(contactsConNames) && 0 < contactsConNames.size()) {
				FormDataGroupDto contactsTimeNamesGroup = createFormDataGroup(
						FormGroupsConstants.TMPLAT_CONTACTS_CLIENTTIME_NAMES,
						FormGroupsConstants.TMPLAT_CONTACTS_CLIENTTIME);
				List<BookmarkDto> bookmarkCntTimeNamesGrp = new ArrayList<BookmarkDto>();
				BookmarkDto bkNameLabel = createBookmark(BookmarkConstants.CONTACTS_CLIENTTIME_NAMESLABEL,
						CONTACTS_NAMESLABELTEXT);
				bookmarkCntTimeNamesGrp.add(bkNameLabel);
				contactsTimeNamesGroup.setBookmarkDtoList(bookmarkCntTimeNamesGrp);
				tmpFormDatacontTimeNmGrpList.add(contactsTimeNamesGroup);
			}
			contactsTimeGroup.setFormDataGroupList(tmpFormDatacontTimeNmGrpList);
			tmpFormDataGrpContactList.add(contactsTimeGroup);
		}

		// artf129590 - FCL Contact Log Narraitve
		if (null != contact.getCdFtfExceptionReason()) {
			List<FormDataGroupDto> tmpFtfExceptionReasonGrpLst = new ArrayList<FormDataGroupDto>();

			FormDataGroupDto ftfExceptionReasonGrp = createFormDataGroup(FormGroupsConstants.TMPLAT_CONTACTS_FTFREASON,
					NULL_STRING);
			tmpFtfExceptionReasonGrpLst.add(ftfExceptionReasonGrp);

			String ftfExceptionReasonCode = CodesConstant.FTFEXRSN;

			if (CodesConstant.CCONPROG_CPS.equals(contactNarraLogFormDto.getStageDto().getCdStageProgram())) {
				ftfExceptionReasonCode = CodesConstant.EXCPRSN;
			}

			BookmarkDto ftfExceptionReason = createBookmarkWithCodesTable(BookmarkConstants.CONTACTS_FTFREASON,
					contact.getCdFtfExceptionReason(), ftfExceptionReasonCode);
			List<BookmarkDto> contactsFtfReasonList = new ArrayList<BookmarkDto>();
			contactsFtfReasonList.add(ftfExceptionReason);
			ftfExceptionReasonGrp.setBookmarkDtoList(contactsFtfReasonList);

			tmpFormDataGrpContactList.add(ftfExceptionReasonGrp);
		}

		// display if Purpose
		if (null != contact.getCdContactPurpose()) {
			List<FormDataGroupDto> tmpFormcntAttmptGrpList = new ArrayList<FormDataGroupDto>();
			List<FormDataGroupDto> tmpFormcntAttmptOmitGrpList = new ArrayList<FormDataGroupDto>();
			FormDataGroupDto contactsPurposeGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_CONTACTS_PURPOSE,
					FormGroupsConstants.TMPLAT_CONTACTS);
			List<BookmarkDto> bkTmplatContPurposeList = new ArrayList<BookmarkDto>();
			BookmarkDto bkContPurpose = createBookmarkWithCodesTable(BookmarkConstants.CONTACTS_PURPOSE,
					contact.getCdContactPurpose(), CodesConstant.CCNTPURP);
			bkTmplatContPurposeList.add(bkContPurpose);
			contactsPurposeGroup.setBookmarkDtoList(bkTmplatContPurposeList);
			// populate Attempted: for all but type: Kinship Disposition Summary
			List<BookmarkDto> bkContAttemptList = new ArrayList<BookmarkDto>();
			if (!CodesConstant.CCNTCTYP_BKDS.equals(contact.getCdContactType())) {
				FormDataGroupDto contactsAttemptedGroup = createFormDataGroup(
						FormGroupsConstants.TMPLAT_CONTACTS_ATTEMPTED, FormGroupsConstants.TMPLAT_CONTACTS_PURPOSE);
				BookmarkDto bkContAttempt = createBookmarkWithCodesTable(BookmarkConstants.CONTACTS_ATTEMPTED,
						contact.getIndContactAttempted(), CodesConstant.CINVACAN);
				bkContAttemptList.add(bkContAttempt);
				contactsAttemptedGroup.setBookmarkDtoList(bkContAttemptList);
				tmpFormcntAttmptGrpList.add(contactsAttemptedGroup);
				contactsPurposeGroup.setFormDataGroupList(tmpFormcntAttmptGrpList);
			} else {
				FormDataGroupDto contactsAttemptOmitGroup = createFormDataGroup(
						FormGroupsConstants.TMPLAT_CONTACTS_ATTEMPTOMIT, FormGroupsConstants.TMPLAT_CONTACTS_PURPOSE);
				tmpFormcntAttmptOmitGrpList.add(contactsAttemptOmitGroup);
				contactsPurposeGroup.setFormDataGroupList(tmpFormcntAttmptOmitGrpList);
			}

			tmpFormDataGrpContactList.add(contactsPurposeGroup);
		}
		// display if Method for all but Kinship Disposition Summary
		List<BookmarkDto> bkTmplatContactMethodList = new ArrayList<BookmarkDto>();
		if (!ObjectUtils.isEmpty(contact.getCdContactMethod())
				&& !CodesConstant.CCNTCTYP_BKDS.equals(contact.getCdContactType())) {
			List<FormDataGroupDto> tmpMethodAnncedFormGrpList = new ArrayList<FormDataGroupDto>();
			List<FormDataGroupDto> tmpMethodUnAnncedFormGrpList = new ArrayList<FormDataGroupDto>();
			FormDataGroupDto contactsMethodGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_CONTACTS_METHOD,
					FormGroupsConstants.TMPLAT_CONTACTS);
			BookmarkDto bkCdContactMeth = createBookmarkWithCodesTable(BookmarkConstants.CONTACTS_METHOD,
					contact.getCdContactMethod(), CodesConstant.CCNTMETH);
			bkTmplatContactMethodList.add(bkCdContactMeth);
			contactsMethodGroup.setBookmarkDtoList(bkTmplatContactMethodList);
			if (ServiceConstants.Y.equals(contact.getIndAnnounced())) {
				FormDataGroupDto contactsMethodAnnouncedGroup = createFormDataGroup(
						FormGroupsConstants.TMPLAT_CONTACTS_METHOD_ANNOUNCED,
						FormGroupsConstants.TMPLAT_CONTACTS_METHOD);
				tmpMethodAnncedFormGrpList.add(contactsMethodAnnouncedGroup);
				contactsMethodGroup.setFormDataGroupList(tmpMethodAnncedFormGrpList);
			} else if (ServiceConstants.N.equals(contact.getIndAnnounced())) {
				FormDataGroupDto contactsMethodUnannouncedGroup = createFormDataGroup(
						FormGroupsConstants.TMPLAT_CONTACTS_METHOD_UNANNOUNCED,
						FormGroupsConstants.TMPLAT_CONTACTS_METHOD);
				tmpMethodUnAnncedFormGrpList.add(contactsMethodUnannouncedGroup);
				contactsMethodGroup.setFormDataGroupList(tmpMethodUnAnncedFormGrpList);
			}
			tmpFormDataGrpContactList.add(contactsMethodGroup);
		}

		// artf129590 - hide location if displaying FTF Exception
		//artf156796 Working Narrative Log
		if (!ObjectUtils.isEmpty(contact.getCdContactLocation())) { // artf281954
			if (!Arrays.asList(CodesConstant.CCNTCTYP_AFFE, CodesConstant.CCNTCTYP_LFFE)
					.contains(contact.getCdContactType())) { // artf129590 - hide location if displaying FTF Exception
				FormDataGroupDto contactsLocationGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_CONTACTS_LOCATION,
						FormGroupsConstants.TMPLAT_CONTACTS);
				tmpFormDataGrpContactList.add(contactsLocationGroup);
				contactsLocationGroup.setBookmarkDtoList(Collections.singletonList(
                        createBookmarkWithCodesTable(BookmarkConstants.CONTACTS_LOCATION,
                                contact.getCdContactLocation(), CodesConstant.CCNCTLOC)
                ));
			}
		}

		// display if Merge
		List<BookmarkDto> bkContactMergeList = new ArrayList<BookmarkDto>();
		if (!contact.getIdStage().equals(contactNarraLogFormDto.getIdStage())
				&& !contact.getIdStage().equals(contactNarraLogFormDto.getIntakeStageId())
				&& CodesConstant.CCINVCLS_97.equals(contact.getCdStageRsnCLosed())) {
			FormDataGroupDto contactsMergeGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_CONTACTS_MERGE,
					FormGroupsConstants.TMPLAT_CONTACTS);
			BookmarkDto bkIdContStage = createBookmark(BookmarkConstants.CONTACTS_MERGE_STAGE, contact.getIdStage());
			bkContactMergeList.add(bkIdContStage);
			contactsMergeGroup.setBookmarkDtoList(bkContactMergeList);
			tmpFormDataGrpContactList.add(contactsMergeGroup);
		}

    // Type
    FormDataGroupDto contactsTypeGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_CONTACTS_TYPE_GRP,
        FormGroupsConstants.TMPLAT_CONTACTS);
    tmpFormDataGrpContactList.add(contactsTypeGroup);
    List<BookmarkDto> bkContactTypesBookmarkList = new ArrayList<>();
    List<FormDataGroupDto> bkContactTypesGroupList = new ArrayList<>();
    contactsTypeGroup.setBookmarkDtoList(bkContactTypesBookmarkList);
    contactsTypeGroup.setFormDataGroupList(bkContactTypesGroupList);

    BookmarkDto bkContactType = createBookmarkWithCodesTable(BookmarkConstants.CONTACTS_TYPE,
        contact.getCdContactType(), CodesConstant.CCNTCTYP);
    bkContactTypesBookmarkList.add(bkContactType);

    // CANIRSP-495 Bug a few fields are not needed
    if (!WebConstants.INR_CONTACT_TYPES.contains(contact.getCdContactType()) || ObjectUtils.isEmpty(contact.getDtNotification())) {

      // display if contact Names
      if (!ObjectUtils.isEmpty(contactsConNames) && 0 < contactsConNames.size()) {
        List<BookmarkDto> bkIdContStageList = new ArrayList<BookmarkDto>();
        if (typeNameLabel) {
          FormDataGroupDto contactsTypeNamesGroup = createFormDataGroup(
              FormGroupsConstants.TMPLAT_CONTACTS_NAMESLABEL, FormGroupsConstants.TMPLAT_CONTACTS_TYPE_GRP);
          BookmarkDto bkIdContStage = createBookmark(BookmarkConstants.CONTACTS_NAMESLABEL,
              getContactNamesLabelText(contact.getCdContactType())); //artf129590
          bkIdContStageList.add(bkIdContStage);
          contactsTypeNamesGroup.setBookmarkDtoList(bkIdContStageList);
          bkContactTypesGroupList.add(contactsTypeNamesGroup);
        }
        List<BookmarkDto> bkContNameList = null;
        for (String conName : contactsConNames) {
          bkContNameList = new ArrayList<BookmarkDto>();
          if(!ObjectUtils.isEmpty(conName))
          {
            FormDataGroupDto contactsNamesGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_CONTACTS_NAMES,
                FormGroupsConstants.TMPLAT_CONTACTS);
            BookmarkDto bkContName = createBookmark(BookmarkConstants.CONTACTS_NAMES, conName);
            bkContNameList.add(bkContName);
            contactsNamesGroup.setBookmarkDtoList(bkContNameList);
            tmpFormDataGrpContactList.add(contactsNamesGroup);
          }
        }
      }
    }


		// artf129590 sibling visit does not make sense on AFFE (Face-to-Face Exception) contacts. only display sibling if not AFFE
    //artf156796 Working Narrative Log
    // CANIRSP-495 Bug a few fields are not needed - also exclude sibling from I&R version 2 contacts
		if (!CodesConstant.CCNTCTYP_AFFE.equals(contact.getCdContactType()) && !CodesConstant.CCNTCTYP_LFFE.equals(contact.getCdContactType())
				&& !CodesConstant.CCNTPURP_BSTF.equals(contact.getCdContactType()) && (
            !WebConstants.INR_CONTACT_TYPES.contains(contact.getCdContactType()) || ObjectUtils.isEmpty(contact.getDtNotification()))) {
            List<BookmarkDto> bkSiblingVisitList = new ArrayList<BookmarkDto>();
            if (!ObjectUtils.isEmpty(contact.getIndSiblingVisit())) {
                FormDataGroupDto contactsSiblingGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_CONTACTS_SIBVISIT,
                        FormGroupsConstants.TMPLAT_CONTACTS);
                BookmarkDto bkContSibling = createBookmarkWithCodesTable(BookmarkConstants.CONTACTS_SIBVISIT,
                        contact.getIndSiblingVisit(), CodesConstant.CINVACAN);
                bkSiblingVisitList.add(bkContSibling);
                contactsSiblingGroup.setBookmarkDtoList(bkSiblingVisitList);
                tmpFormDataGrpContactList.add(contactsSiblingGroup);
            }
        }

		List<BookmarkDto> bkContARSafetyList = new ArrayList<BookmarkDto>();
		if (CONTACTS_AR.contains(contact.getCdContactType())) {
			FormDataGroupDto contactsArGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_CONTACTS_AR,
					FormGroupsConstants.TMPLAT_CONTACTS);
			BookmarkDto bkContARSafety = createBookmarkWithCodesTable(BookmarkConstants.CONTACTS_AR_SAFETY,
					contact.getIndSafPlanComp(), CodesConstant.CINVACAN);
			BookmarkDto bkContARFamily = createBookmarkWithCodesTable(BookmarkConstants.CONTACTS_AR_FAMILY,
					contact.getIndFamPlanComp(), CodesConstant.CINVACAN);
			bkContARSafetyList.add(bkContARSafety);
			bkContARSafetyList.add(bkContARFamily);
			contactsArGroup.setBookmarkDtoList(bkContARSafetyList);
			tmpFormDataGrpContactList.add(contactsArGroup);
		}

		List<BookmarkDto> bkContBkdsList = new ArrayList<BookmarkDto>();
		if (CodesConstant.CCNTCTYP_BKDS.contains(contact.getCdContactType())) {
			FormDataGroupDto contactsBkdsGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_CONTACTS_BKDS,
					FormGroupsConstants.TMPLAT_CONTACTS);
			BookmarkDto bkContKinCareGiver = createBookmark(BookmarkConstants.CONTACTS_BKDS_CAREGIVER,
					contact.getKinCaregiver());
			BookmarkDto bkContBkdsRso = createBookmarkWithCodesTable(BookmarkConstants.CONTACTS_BKDS_RSO,
					contact.getCdRsnScrout(), CodesConstant.CCNTRSO);
			bkContBkdsList.add(bkContKinCareGiver);
			bkContBkdsList.add(bkContBkdsRso);
			List<FormDataGroupDto> tmpCntBkdsAmtFormDataGrpList = new ArrayList<FormDataGroupDto>();
			if (!ObjectUtils.isEmpty(contact.getAmtNeeded()) && 0 < contact.getAmtNeeded()) {
				List<BookmarkDto> bkContAmtNeededList = new ArrayList<BookmarkDto>();
				FormDataGroupDto contactsBkdsAmtGroup = createFormDataGroup(
						FormGroupsConstants.TMPLAT_CONTACTS_BKDS_AMTNEEDED, FormGroupsConstants.TMPLAT_CONTACTS_BKDS);
				BookmarkDto bkContAmtNeeded = createBookmark(BookmarkConstants.CONTACTS_BKDS_AMTNEEDED,
						contact.getAmtNeeded());
				bkContAmtNeededList.add(bkContAmtNeeded);
				contactsBkdsAmtGroup.setBookmarkDtoList(bkContAmtNeededList);
				tmpCntBkdsAmtFormDataGrpList.add(contactsBkdsAmtGroup);
			}
			if (null != contact.getCdRsnAmtne()) {
				List<BookmarkDto> bkContBkdsRsnaAmtneList = new ArrayList<BookmarkDto>();
				FormDataGroupDto contactsBkdsRsnGroup = createFormDataGroup(
						FormGroupsConstants.TMPLAT_CONTACTS_BKDS_RSNAMTNE, FormGroupsConstants.TMPLAT_CONTACTS_BKDS);
				BookmarkDto bkContBkdsRsnaAmtne = createBookmarkWithCodesTable(BookmarkConstants.CONTACTS_BKDS_RSNAMTNE,
						contact.getCdRsnAmtne(), CodesConstant.CCNTRNA);
				bkContBkdsRsnaAmtneList.add(bkContBkdsRsnaAmtne);
				contactsBkdsRsnGroup.setBookmarkDtoList(bkContBkdsRsnaAmtneList);
				tmpCntBkdsAmtFormDataGrpList.add(contactsBkdsRsnGroup);
			}
			BookmarkDto bkContBkdsFurture = createBookmarkWithCodesTable(BookmarkConstants.CONTACTS_BKDS_FUTURE,
					contact.getIndRecCons(), CodesConstant.CAREDESG);
			bkContBkdsList.add(bkContBkdsFurture);
			contactsBkdsGroup.setBookmarkDtoList(bkContBkdsList);
			contactsBkdsGroup.setFormDataGroupList(tmpCntBkdsAmtFormDataGrpList);
			tmpFormDataGrpContactList.add(contactsBkdsGroup);
		}


		if (WebConstants.INR_CONTACT_TYPES.contains(contact.getCdContactType()) && !ObjectUtils.isEmpty(contact.getDtNotification())) {
			// CANIRSP-17 AC 1 - Date of Notifications; Date of Staffing
			FormDataGroupDto contactsInrDatesGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_CONTACTS_INR_DATES_GRP,
					FormGroupsConstants.TMPLAT_CONTACTS);
      tmpFormDataGrpContactList.add(contactsInrDatesGroup);
      List<BookmarkDto> bkStaffingList = new ArrayList<>();
      BookmarkDto bkStaffingDate = createBookmark(BookmarkConstants.INR_STAFFING_DATE,
              nullSafeDateFormat(contact.getDtDTContactOccurred(), DATE_TIME_FORMAT));
      bkStaffingList.add(bkStaffingDate);
      contactsInrDatesGroup.setBookmarkDtoList(bkStaffingList);

			// we can be put it all in one group since these sections are together at the bottom
			FormDataGroupDto contactsInrGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_CONTACTS_INR_GRP,
					FormGroupsConstants.TMPLAT_CONTACTS);
			tmpFormDataGrpContactList.add(contactsInrGroup); // works
			List<FormDataGroupDto> contactsInrGroupList = new ArrayList<>();
			List<BookmarkDto> contactsInrBookmarkList = new ArrayList<>();

      // CANIRSP-17 AC 2 - Involved Children/Youth (selected only)
      BookmarkDto bkInvYouthChildren =
          createBookmark(BookmarkConstants.INR_INV_CHILD_TXT, INVOLVED_YOUTH);
      contactsInrBookmarkList.add(bkInvYouthChildren);
      for (String contactName : contact.getContactNamesList()) {
        FormDataGroupDto involvedChildrenGroup =
            createFormDataGroup(
                FormGroupsConstants.TMPLAT_INR_INVLVD_CHILDREN_GRP,
                FormGroupsConstants.TMPLAT_CONTACTS_INR_GRP);
        contactsInrGroupList.add(involvedChildrenGroup);
        List<BookmarkDto> involvedChildernGroupBookmarkList = new ArrayList<>();
        involvedChildernGroupBookmarkList.add(
            createBookmark(BookmarkConstants.INR_INV_CHILD, contactName));
        involvedChildrenGroup.setBookmarkDtoList(involvedChildernGroupBookmarkList);
      }

      // CANIRSP-17 AC 4 - Staffing Participants
      BookmarkDto bkInrCaseworker =
          createBookmark(BookmarkConstants.INR_CASE_WORKER, contact.getNmCaseworker());
			contactsInrBookmarkList.add(bkInrCaseworker);
			BookmarkDto bkInrSupervisor = createBookmark(BookmarkConstants.INR_SUPERVISOR, contact.getNmSupervisor());
			contactsInrBookmarkList.add(bkInrSupervisor);
			BookmarkDto bkInrDirector = createBookmark(BookmarkConstants.INR_DIRECTOR, contact.getNmDirector());
			contactsInrBookmarkList.add(bkInrDirector);

      BookmarkDto bkNotificationDate = createBookmark(BookmarkConstants.INR_NOTIFICATION_DATE,
          nullSafeDateFormat(contact.getDtNotification(), DATE_FORMAT));
      contactsInrBookmarkList.add(bkNotificationDate);

      // CANIRSP-17 AC 5 - Out of State/Provider Investigation (overrides "Intake Narrative" above)
      String inrOos =
          !ObjectUtils.isEmpty(contact.getCdInrProviderRegType())
                  && "OS".equalsIgnoreCase(contact.getCdInrProviderRegType())
              ? "Yes"
              : "No";
			BookmarkDto bkInrOos = createBookmark(BookmarkConstants.INR_OOS_YN, inrOos);
			contactsInrBookmarkList.add(bkInrOos);

      String inrPi =
          !ObjectUtils.isEmpty(contact.getCdInrProviderRegType())
                  && "PI".equalsIgnoreCase(contact.getCdInrProviderRegType())
              ? "Yes"
              : "No";
			BookmarkDto kbInrPi = createBookmark(BookmarkConstants.INR_PI_YN, inrPi);
			contactsInrBookmarkList.add(kbInrPi);

			if (!ObjectUtils.isEmpty(contact.getCdInrProviderRegType())) {
				FormDataGroupDto intakeExceptionGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_INR_INTAKE_EXCEPTION_GRP,
						FormGroupsConstants.TMPLAT_CONTACTS_INR_GRP);
				contactsInrGroupList.add(intakeExceptionGroup);
				contactsInrGroup.setFormDataGroupList(contactsInrGroupList); // works

				String exceptionTitle = "PI".equals(contact.getCdInrProviderRegType()) ? "Provider Investigation Details" : "Out-of-State Details";

				BookmarkDto bkExcepTitle = createBookmark(BookmarkConstants.INR_INTAKE_EXCEPTION_TITLE, exceptionTitle);
				List<BookmarkDto> bkContNameList = new ArrayList<>();
				bkContNameList.add(bkExcepTitle);

				BookmarkDto bkExcepNarr = createBookmark(BookmarkConstants.INR_INTAKE_EXCEPTION_NARR, contact.getTxtNarrativeRpt());
				bkContNameList.add(bkExcepNarr);

				intakeExceptionGroup.setBookmarkDtoList(bkContNameList); // works
			}

      // CANIRSP-17 AC 3 - Intake Narrative
      if (inrPi.equalsIgnoreCase("No") && inrOos.equalsIgnoreCase("No")) {
        int index = 1;
        for (ContactNarrativeDto intake : contact.getIntakeNarrativeList()) {
          FormDataGroupDto intakeReportGroup =
              createFormDataGroup(
                  FormGroupsConstants.TMPLAT_INR_INTAKE_REPORT_GRP,
                  FormGroupsConstants.TMPLAT_CONTACTS_INR_GRP);

          contactsInrGroupList.add(intakeReportGroup);
          List<BookmarkDto> intakeReportGroupBookmarkList = new ArrayList<>();
          intakeReportGroupBookmarkList.add(
              createBookmark(BookmarkConstants.INR_INT_STAGE_INDEX, index));
          intakeReportGroupBookmarkList.add(
              createBookmark(BookmarkConstants.INR_INT_STAGE_ID, intake.getIdContactStage()));
          intakeReportGroupBookmarkList.add(
              createBookmark(BookmarkConstants.INR_INT_STAGE_NARR, intake.getStrNarrative()));
          intakeReportGroup.setBookmarkDtoList(intakeReportGroupBookmarkList);
          index++;
        }
      }

			// CANIRSP-17 AC 6 - "Discussion with investigator", "Identified Safety Concerns/Needs", "Follow-up Completion"
			BookmarkDto bkInrDiscussInv = createBookmark(BookmarkConstants.INR_DISCUSS_INV, contact.getTxtSummDiscuss());
			contactsInrBookmarkList.add(bkInrDiscussInv);
			BookmarkDto bkInrSafetyConc = createBookmark(BookmarkConstants.INR_SAFETY_CONC, contact.getTxtIdentfdSafetyConc());
			contactsInrBookmarkList.add(bkInrSafetyConc);

			// still CANIRSP-17 AC 6, "Follow-up Completion" is more ornate than its siblings.
			if (!ObjectUtils.isEmpty(contact.getInrSafetyFieldDtoList())) { // null proofing, but should never happen for this required field.
				if (contact.getInrSafetyFieldDtoList().size() == 1 &&
						"Y".equalsIgnoreCase(contact.getInrSafetyFieldDtoList().get(0).getIndFollowup())) {
					// begin TMPLAT_INR_FOLLOWUP_NOT_REQ_GRP
					FormDataGroupDto intakeExceptionGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_INR_FOLLOWUP_NOT_REQ_GRP,
							FormGroupsConstants.TMPLAT_CONTACTS_INR_GRP);
					contactsInrGroupList.add(intakeExceptionGroup);
					List<BookmarkDto> intakeExceptionGroupBookmarkList = new ArrayList<>();

					// MSG_INR_FOLLOWUP
					BookmarkDto bkMsgFollowup = createBookmark(BookmarkConstants.MSG_INR_FOLLOWUP, contact.getInrSafetyFieldDtoList().get(0).getTxtPlansFutureActns());
					intakeExceptionGroupBookmarkList.add(bkMsgFollowup);

					// final call in TMPLAT_INR_FOLLOWUP_NOT_REQ_GRP
					intakeExceptionGroup.setBookmarkDtoList(intakeExceptionGroupBookmarkList);
				} else {
					int index = 1;
					for (InrSafetyFieldDto currAction : contact.getInrSafetyFieldDtoList()) {
						// begin TMPLAT_INR_SAFETY_GRP
						FormDataGroupDto safetyActionGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_INR_SAFETY_GRP,
								FormGroupsConstants.TMPLAT_CONTACTS_INR_GRP);
						contactsInrGroupList.add(safetyActionGroup);
						List<BookmarkDto> safetyActionBookmarkList = new ArrayList<>();

						BookmarkDto bkInrSafetyNum = createBookmark(BookmarkConstants.INR_SAFETY_NUM, String.valueOf(index++));
						safetyActionBookmarkList.add(bkInrSafetyNum);
						BookmarkDto bkInrActionTitle = createBookmark(BookmarkConstants.INR_ACTION_TITLE,
								currAction.getTxtSafetyActionItem());
						safetyActionBookmarkList.add(bkInrActionTitle);
						BookmarkDto bkInrDueDate = createBookmark(BookmarkConstants.INR_DUE_DATE,
								nullSafeDateFormat(currAction.getDtSafetyActionDue(), DATE_FORMAT));
						safetyActionBookmarkList.add(bkInrDueDate);
						BookmarkDto bkInrCompletionDate = createBookmark(BookmarkConstants.INR_COMPLETION_DATE,
								nullSafeDateFormat(currAction.getDtFollowUp(), DATE_FORMAT));
						safetyActionBookmarkList.add(bkInrCompletionDate);
						BookmarkDto bkInrCompletionNarr = createBookmark(BookmarkConstants.INR_COMPLETION_NARR,
								currAction.getTxtPlansFutureActns());
						safetyActionBookmarkList.add(bkInrCompletionNarr);

						// final call in TMPLAT_INR_SAFETY_GRP
						safetyActionGroup.setBookmarkDtoList(safetyActionBookmarkList);
					}

				}
			}


			// push lists for subgroup into subgroup now that we're done populating them.
			contactsInrGroup.setFormDataGroupList(contactsInrGroupList);
			contactsInrGroup.setBookmarkDtoList(contactsInrBookmarkList);
		}

		if (ServiceConstants.INDICATOR_YES.equalsIgnoreCase(contact.getIndNarr())
				&& !(GUARDIANSHIP_REFERRAL.contains(contact.getCdContactType())
						|| KINSHIP_NOTIFICATION.contains(contact.getCdContactType()))) {
			BookmarkDto bkContNarrative = createBookmark(BookmarkConstants.CONTACTS_NARRATIVELABEL,
					BookmarkConstants.CONTACTS_NARRATIVELABELTEXT);
			bookmarkTmplatContacts.add(bkContNarrative);

			List<BlobDataDto> bkContNarrList = new ArrayList<BlobDataDto>();
			FormDataGroupDto contactsNarrGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_CONTACTS_NARR,
					FormGroupsConstants.TMPLAT_CONTACTS);

			List<FormDataGroupDto> tmpCntEcomFormDataGrpList = new ArrayList<FormDataGroupDto>();
			if (CodesConstant.CCNTCTYP_ECOM.equals(contact.getCdContactType())) {
				FormDataGroupDto contactsEcomGroup = createFormDataGroup(
						FormGroupsConstants.TMPLAT_CONTACTS_NARR_PAGEBREAK, FormGroupsConstants.TMPLAT_CONTACTS_NARR);
				tmpCntEcomFormDataGrpList.add(contactsEcomGroup);
			}

			if (!ObjectUtils.isEmpty(contact.getNarrative())){
				BlobDataDto blobContactNarr = createBlobValueData(BookmarkConstants.CONTACTS_NARR, 
						contact.getNarrative(), contact.getIdTemplate());
				bkContNarrList.add(blobContactNarr);
			}
			contactsNarrGroup.setBlobDataDtoList(bkContNarrList);
			contactsNarrGroup.setFormDataGroupList(tmpCntEcomFormDataGrpList);
			tmpFormDataGrpContactList.add(contactsNarrGroup);
		}

		if (GUIDE_TOPICS_CONTACTS.contains(contact.getCdContactType())) {
			if (!ObjectUtils.isEmpty(contactGuideList) && 0 < contactGuideList.size()) {
				List<FormDataGroupDto> tmpCntGuideFormDataGrpList = null;
				List<BlobDataDto> blobContGuideList = null;
				List<BookmarkDto> bkContGuideList = null;
				for (ContactNarrGuideDto contactNarrGuideDto : contactGuideList) {
					tmpCntGuideFormDataGrpList = new ArrayList<FormDataGroupDto>();
					blobContGuideList = new ArrayList<BlobDataDto>();
					bkContGuideList = new ArrayList<BookmarkDto>();
					if (contact.getIdEvent().equals(contactNarrGuideDto.getIdEvent())) {
						FormDataGroupDto contactsGuideGroup = createFormDataGroup(
								FormGroupsConstants.TMPLAT_CONTACTS_GUIDE, FormGroupsConstants.TMPLAT_CONTACTS);
						BookmarkDto bkContGuideRole = createBookmarkWithCodesTable(
								BookmarkConstants.CONTACTS_GUIDE_ROLE, contactNarrGuideDto.getCdGuideRole(),
								CodesConstant.CGPROLE);
						BookmarkDto bkContGuideType = createBookmarkWithCodesTable(
								BookmarkConstants.CONTACTS_GUIDE_TYPE, contactNarrGuideDto.getCdType(),
								CodesConstant.CGTXTTYP);
						BookmarkDto bkContGuideNm = createBookmark(BookmarkConstants.CONTACTS_GUIDE_FULLNAME,
								contactNarrGuideDto.getNmFullName());
						if (!ObjectUtils.isEmpty(contact.getNarrative())){
							BlobDataDto blobContactGuideNarr = createBlobValueData(BookmarkConstants.CONTACTS_GUIDE_NARR, 
									contactNarrGuideDto.getNarrative(), contactNarrGuideDto.getIdTemplate());
							blobContGuideList.add(blobContactGuideNarr);
						}

						bkContGuideList.add(bkContGuideRole);
						bkContGuideList.add(bkContGuideType);
						bkContGuideList.add(bkContGuideNm);
						
						contactsGuideGroup.setBlobDataDtoList(blobContGuideList);
						contactsGuideGroup.setBookmarkDtoList(bkContGuideList);
						tmpFormDataGrpContactList.add(contactsGuideGroup);
					}
				}
			}
		}

		formDataGroupDto.setBookmarkDtoList(bookmarkTmplatContacts);
		formDataGroupDto.setFormDataGroupList(tmpFormDataGrpContactList);
		tmpFormDataGrpList.add(formDataGroupDto);
	}

	/**
	 * 
	 * Method Name: dateFormat Method Description:
	 * 
	 * @param inputDate
	 * @return
	 */
	private String dateFormat(Date inputDate, String format) {
		String dtSampleFrom = ServiceConstants.SPACE;
		if (!ObjectUtils.isEmpty(dtSampleFrom)) {
			DateFormat formatter = new SimpleDateFormat(format);
			dtSampleFrom = formatter.format(inputDate);
		}
		return dtSampleFrom;

	}

	private String nullSafeDateFormat(Date inputDate, String format) {
		String retVal = null;
		if (inputDate != null) {
			retVal = dateFormat(inputDate, format);
		}
		return retVal;
	}

	private String getContactNamesLabelText(String contactType){
		String names = "";
        //artf156796 Working Narrative Log
		if( CodesConstant.CCNTCTYP_AFFE.equals(contactType) || CodesConstant.CCNTCTYP_LFFE.equals(contactType)){
			names = CONTACTS_FTFEXRSNLABELTEXT;
		} else {
			names = CONTACTS_NAMESLABELTEXT;
		}
		return names;
	}
}
