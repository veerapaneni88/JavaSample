package us.tx.state.dfps.service.forms.util;

import java.util.ArrayList;
import java.util.List;


import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import org.springframework.util.StringUtils;
import us.tx.state.dfps.service.casepackage.dto.CsaEpisodesIncdntDto;
import us.tx.state.dfps.service.common.BookmarkConstants;
import us.tx.state.dfps.service.common.CodesConstant;
import us.tx.state.dfps.service.common.FormConstants;
import us.tx.state.dfps.service.common.FormGroupsConstants;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.forms.dto.AttachmentADto;
import us.tx.state.dfps.service.forms.dto.BookmarkDto;
import us.tx.state.dfps.service.forms.dto.FormDataGroupDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.person.dto.TraffickingDto;
import us.tx.state.dfps.web.fcl.dto.SexualVictimIncidentDto;

/**
 * Service-Forms Prefill for FCL Name: CshAttachmentAPrefillData Description:
 * CSHAttachmentAPrefillData will be implemented and perform returnPrefillData
 * operation defined in DocumentServiceUtil Interface to populate the prefill
 * data for Child Sexual History Report - Attachment A form © 2019 Texas
 * Department of Family and Protective Services FCL Artifact ID: artf128756
 ***/
@Component
public class CshAttachmentAPrefillData extends DocumentServiceUtil {

	public static final String SXTR = "SXTR";
	public static final String SUSP = "SUSP";
	public static final String CONF = "CONF";


	@Override
	public PreFillDataServiceDto returnPrefillData(Object parentDtoobj) {
		List<BookmarkDto> bookmarkNonFrmGrpList = new ArrayList<BookmarkDto>();
		List<FormDataGroupDto> formDataGroupList = new ArrayList<FormDataGroupDto>();

		// Add non group bookmarks and form data groups to prefill
		PreFillDataServiceDto preFillData = new PreFillDataServiceDto();
		AttachmentADto inputPrefillRes = (AttachmentADto) parentDtoobj;

		BookmarkDto bookmarkChildNm = createBookmark(BookmarkConstants.TITLE_CHILD_NAME, inputPrefillRes.getNmPerson());
		bookmarkNonFrmGrpList.add(bookmarkChildNm);
		BookmarkDto bookmarkChildIDPerson = createBookmark(BookmarkConstants.TITLE_CHILD_ID_PERS,
				inputPrefillRes.getIdPerson());
		bookmarkNonFrmGrpList.add(bookmarkChildIDPerson);


		if (inputPrefillRes.getIndSbp() != null) {
            FormDataGroupDto sexualBehaviorProblemGroup = createFormDataGroup("TMPLAT_SBP_GRP",
                FormConstants.EMPTY_STRING);
            List<BookmarkDto> sexualBehaviorProblemGroupList = new ArrayList<BookmarkDto>();
			if ("Y".equalsIgnoreCase(inputPrefillRes.getIndSbp())) { // missing from release branch
				BookmarkDto bookmarSbpIndicator = createBookmark("TXT_SBP_IND", FormConstants.YES);
				sexualBehaviorProblemGroupList.add(bookmarSbpIndicator);
				BookmarkDto bookmarSbpText = createBookmark("TXT_SBP_TXT", inputPrefillRes.getTxtSbp());
				sexualBehaviorProblemGroupList.add(bookmarSbpText);
			} else if("N".equalsIgnoreCase(inputPrefillRes.getIndSbp())){
				BookmarkDto bookmarSbpIndicator = createBookmark("TXT_SBP_IND", FormConstants.NO);
				sexualBehaviorProblemGroupList.add(bookmarSbpIndicator);
			}
            sexualBehaviorProblemGroup.setBookmarkDtoList(sexualBehaviorProblemGroupList);
            formDataGroupList.add(sexualBehaviorProblemGroup);
            preFillData.setFormDataGroupList(formDataGroupList);
        }

		/**
		 * CSA history bookmarks existing method called and used for Prefill
		 * data Populates CSA history for a given Person ID in attachment A form
		 **/

			FormDataGroupDto formdatagroupTmplatCSAIncdnt = createFormDataGroup(FormGroupsConstants.TMPLAT_INCIDENT_GRP,
					FormConstants.EMPTY_STRING);

					List<FormDataGroupDto> episodeChildGroups = new ArrayList<FormDataGroupDto>();
					FormDataGroupDto episodesGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_EPISODE_GRP,
							FormConstants.EMPTY_STRING);

					// Prefill Incidents if exists
					List<CsaEpisodesIncdntDto> csaIncdntList = inputPrefillRes.getCsaEpisodesIncdntDtoList();
					if (!ObjectUtils.isEmpty(csaIncdntList)
							&& csaIncdntList.size() > 0) {
						for (CsaEpisodesIncdntDto currentIncident : csaIncdntList) {
							FormDataGroupDto incidentGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_INCIDENT_GRP,
									FormGroupsConstants.TMPLAT_EPISODE_GRP);
							List<BookmarkDto> bookmarkIncidentList = new ArrayList<BookmarkDto>();

							BookmarkDto bookmarkDtIncident = createBookmark(BookmarkConstants.DT_INCIDENT,
									TypeConvUtil.formDateFormat(currentIncident.getDtIncdnt()));
							bookmarkIncidentList.add(bookmarkDtIncident);

							if (ServiceConstants.Y.equals(currentIncident.getIndAppxDt())) {
								BookmarkDto bookmarkDtApprox = createBookmark(BookmarkConstants.IND_APPROX_DT,
										FormConstants.APPROXIMATE_DATE);
								bookmarkIncidentList.add(bookmarkDtApprox);
							}

							BookmarkDto bookmarkVictimIncident = createBookmark(BookmarkConstants.NM_VICTIM,
									currentIncident.getTxtVctmInfo());
							bookmarkIncidentList.add(bookmarkVictimIncident);
							BookmarkDto bookmarkDescIncident = createBookmark(BookmarkConstants.DESC_INCIDENT,
									currentIncident.getTxtIncdntDesc());
							bookmarkIncidentList.add(bookmarkDescIncident);

							incidentGroup.setBookmarkDtoList(bookmarkIncidentList);
							episodeChildGroups.add(incidentGroup);
						}


					// Add group and bookmarks to episode group
					episodesGroup.setFormDataGroupList(episodeChildGroups);
					formDataGroupList.add(episodesGroup);
					formdatagroupTmplatCSAIncdnt.setFormDataGroupList(formDataGroupList);
					}
		else {
			// No episodes exists. Display No Episode/Incident message
			FormDataGroupDto noIncident = createFormDataGroup(FormGroupsConstants.TMPLAT_NO_INCIDENT_CSA_GRP,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkNoIncidentList = new ArrayList<BookmarkDto>();

			BookmarkDto bookmarkNoData = createBookmark(BookmarkConstants.TXT_NO_INCIDENT,
					FormConstants.TXT_NO_CSA_INCIDENT);
			bookmarkNoIncidentList.add(bookmarkNoData);
			noIncident.setBookmarkDtoList(bookmarkNoIncidentList);
			formDataGroupList.add(noIncident);
			preFillData.setFormDataGroupList(formDataGroupList);
		}

		/**
		 * Sexual Victimization History: to populate bookmarks and used for
		 * Prefill data Populates Sexual Victimization history for a given
		 * Person ID in attachment A form
		 **/
		List<FormDataGroupDto> incidentGroups = new ArrayList<FormDataGroupDto>();

		// List all persons for whom the child must be closely supervised
		BookmarkDto bookmarkTxtSuperContactDesc = createBookmark(BookmarkConstants.TXT_SUPERV_CONTACT_DESC,
				ObjectUtils.isEmpty(inputPrefillRes.getSxVictimHistoryDtoSpvsnCmts()) ? "" :
						inputPrefillRes.getSxVictimHistoryDtoSpvsnCmts().replaceAll("\n", "<br/>"));
		bookmarkNonFrmGrpList.add(bookmarkTxtSuperContactDesc);
		
		String currentIndChildSxVctmztnHist = inputPrefillRes.getIndChildSxVctmztnHist();		
		
		// to display Indicator to answer if there are incidents, no incidents or indicator is null
		if (ObjectUtils.isEmpty(currentIndChildSxVctmztnHist)){
			BookmarkDto bookmarkTxtConfirmation = createBookmark(BookmarkConstants.TXT_CONFIRMATION, FormConstants.EMPTY_STRING);
			bookmarkNonFrmGrpList.add(bookmarkTxtConfirmation);
			BookmarkDto bookmarkTxtHistoryConfirmation = createBookmark(BookmarkConstants.TXT_CONFIRMED_HISTORY_YES_NO, FormConstants.EMPTY_STRING);
			bookmarkNonFrmGrpList.add(bookmarkTxtHistoryConfirmation);
		} else {
			if ("N".equalsIgnoreCase(currentIndChildSxVctmztnHist)) {
				BookmarkDto bookmarkTxtConfirmation = createBookmark(BookmarkConstants.TXT_CONFIRMATION, FormConstants.NO);
				bookmarkNonFrmGrpList.add(bookmarkTxtConfirmation);
			} else {
				BookmarkDto bookmarkTxtConfirmation = createBookmark(BookmarkConstants.TXT_CONFIRMATION, FormConstants.YES);
				bookmarkNonFrmGrpList.add(bookmarkTxtConfirmation);
				boolean isConfirmed = isConfirmedSexTrafficIncidentsAdded(inputPrefillRes.getTrfckngDtoList());

				FormDataGroupDto templateConfirmedYesGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_CONFIRMED_YES_VICTIMIZATION_GRP,
						FormConstants.EMPTY_STRING);
				formDataGroupList.add(templateConfirmedYesGroup);
				List<BookmarkDto> bookmarkConfiremdList = new ArrayList<BookmarkDto>();

				BookmarkDto bookmarkTxtHistoryConfirmation = createBookmark(BookmarkConstants.TXT_CONFIRMED_HISTORY_YES_NO,
						isConfirmed ? FormConstants.YES : FormConstants.NO);
				bookmarkConfiremdList.add(bookmarkTxtHistoryConfirmation);
				templateConfirmedYesGroup.setBookmarkDtoList(bookmarkConfiremdList);
			}
		}

		String indUnConfirmedHist = StringUtils.hasText(inputPrefillRes.getIndUnconfirmedVictim()) ? inputPrefillRes.getIndUnconfirmedVictim(): "N";
		boolean isSuspectedUnconfirmed = isSuspectedUnConfirmedSexTrafficIncidentsAdded(inputPrefillRes.getTrfckngDtoList());

		BookmarkDto bookmarkTxtUnConfirmed = createBookmark(BookmarkConstants.TXT_UNCONFIRMED_YES_NO,
				"N".equalsIgnoreCase(indUnConfirmedHist) ? FormConstants.NO : FormConstants.YES);
		bookmarkNonFrmGrpList.add(bookmarkTxtUnConfirmed);

		if("Y".equalsIgnoreCase(indUnConfirmedHist)){
			FormDataGroupDto templateUnConfirmedYesGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_UNCONFIRMED_YES_VICTIMIZATION_GRP,
					FormConstants.EMPTY_STRING);
			formDataGroupList.add(templateUnConfirmedYesGroup);
			List<BookmarkDto> bookmarkUnConfiremdList = new ArrayList<BookmarkDto>();

			BookmarkDto bookmarkTxtHistoryConfirmation = createBookmark(BookmarkConstants.TXT_SUSPECTED_UNCONFIRMED_YES_NO,
					isSuspectedUnconfirmed ? FormConstants.YES : FormConstants.NO);
			bookmarkUnConfiremdList.add(bookmarkTxtHistoryConfirmation);
			templateUnConfirmedYesGroup.setBookmarkDtoList(bookmarkUnConfiremdList);
			preFillData.setFormDataGroupList(formDataGroupList);
		}

		if (!ObjectUtils.isEmpty(inputPrefillRes.getSxIcdntDtoList())) {
			FormDataGroupDto incidentParentGroup = createFormDataGroup(
					FormGroupsConstants.TMPLAT_INCIDENT_VICTIMIZATION_PARENT_GRP,
					FormGroupsConstants.TMPLAT_INCIDENT_VICTIMIZATION_PARENT_GRP);

			for (SexualVictimIncidentDto currentIncident : inputPrefillRes.getSxIcdntDtoList()) {
				List<BookmarkDto> bookmarkIncidentList = new ArrayList<BookmarkDto>();

				FormDataGroupDto incidentGroup = createFormDataGroup(
						FormGroupsConstants.TMPLAT_INCIDENT_VICTIMIZATION_GRP,
						FormGroupsConstants.TMPLAT_INCIDENT_VICTIMIZATION_GRP);

				BookmarkDto bookmarkDtIncident = createBookmark(BookmarkConstants.DT_INCIDENT,
						TypeConvUtil.formDateFormat(currentIncident.getDtIncident()));
				bookmarkIncidentList.add(bookmarkDtIncident);

				if (ServiceConstants.Y.equals(currentIncident.getIndApproxDate())) {
					BookmarkDto bookmarkDtApprox = createBookmark(BookmarkConstants.IND_APPROX_DT,
							FormConstants.APPROXIMATE_DATE);
					bookmarkIncidentList.add(bookmarkDtApprox);
				}

				BookmarkDto bookmarkDtRespCmmts = createBookmark(BookmarkConstants.TXT_RESPONSIBLE_COMMENTS,
						ObjectUtils.isEmpty(currentIncident.getResponsibleComments()) ? FormConstants.EMPTY_STRING :
								currentIncident.getResponsibleComments().replaceAll("\n", "<br/>"));
				bookmarkIncidentList.add(bookmarkDtRespCmmts);

				BookmarkDto bookmarkDtVctmztnCmmts = createBookmark(BookmarkConstants.TXT_VICTIMIZATION_COMMENTS,
						ObjectUtils.isEmpty(currentIncident.getVictimComments()) ? FormConstants.EMPTY_STRING :
								currentIncident.getVictimComments().replaceAll("\n", "<br/>"));
				bookmarkIncidentList.add(bookmarkDtVctmztnCmmts);
			

				incidentGroup.setBookmarkDtoList(bookmarkIncidentList);
				incidentGroups.add(incidentGroup);

				//formDataGroupList.add(incidentGroup);
			}
			// adding parent template group to display heading INCIDENT(S) only
			// once
			// but not show when there are no incidents
			incidentParentGroup.setFormDataGroupList(incidentGroups);
			formDataGroupList.add(incidentParentGroup);

		} else {
			// No incidents exists. Display No Incident message

			FormDataGroupDto noIncident = createFormDataGroup(FormGroupsConstants.TMPLAT_NO_INCIDENT_VICTIMIZATION_GRP,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkNoIncidentList = new ArrayList<BookmarkDto>();

			BookmarkDto bookmarkNoData = createBookmark(BookmarkConstants.TXT_NO_VICTIMIZATION_INCIDENT,
					FormConstants.ATTACHMENTA_NO_INCIDENT_MSG);
			bookmarkNoIncidentList.add(bookmarkNoData);		

			noIncident.setBookmarkDtoList(bookmarkNoIncidentList);
			formDataGroupList.add(noIncident);
		}
		/**
		 * Trafficking History: to populate bookmarks and used for Prefill data
		 * Populates Trafficking details history for a given Person ID in
		 * attachment A form
		 **/
		boolean incidentDisplayed = false;
		Boolean unconfirmedTraficcking = false;
		unconfirmedTraficcking = inputPrefillRes.getTrfckngDtoList().stream().
				filter(data->data.getCdtrfckngType().equals("SXTR")).
				anyMatch(item->item.getCdtrfckngStat().equals("SUSP"));

		if (unconfirmedTraficcking) {
			FormDataGroupDto tempPocFullNameFrmDataGrpDto = createFormDataGroup("TMPLAT_UNCONFIRMED_TRAFFICKING_GRP",
					FormConstants.EMPTY_STRING);
			formDataGroupList.add(tempPocFullNameFrmDataGrpDto);
			preFillData.setFormDataGroupList(formDataGroupList);
		}else {
			FormDataGroupDto tempPocFullNameFrmDataGrpDto = createFormDataGroup("TMPLAT_UNCONFIRMED_NO_TRAFFICKING_GRP",
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> unconfirmList = new ArrayList<BookmarkDto>();
			BookmarkDto bookmarkTxtUnConfirmation = createBookmark(BookmarkConstants.TXT_NO_CONFIRMATION, FormConstants.NO);
			unconfirmList.add(bookmarkTxtUnConfirmation);

			tempPocFullNameFrmDataGrpDto.setBookmarkDtoList(unconfirmList);
			formDataGroupList.add(tempPocFullNameFrmDataGrpDto);
			preFillData.setFormDataGroupList(formDataGroupList);
		}
		if (!ObjectUtils.isEmpty(inputPrefillRes.getTrfckngDtoList())) {
			for (TraffickingDto currentIncident : inputPrefillRes.getTrfckngDtoList()) {
			    // artf130752 Attachment A - do not show unconfirmed and labor trafficking
				if (CodesConstant.CTRFTYP_SXTR.equals(currentIncident.getCdtrfckngType()) &&
						CodesConstant.CTRFSTAT_CONF.equals(currentIncident.getCdtrfckngStat())) {
					List<BookmarkDto> bookmarkIncidentList = new ArrayList<BookmarkDto>();
					FormDataGroupDto incidentGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_TRAFFICKING_DTLS_GRP,
							FormGroupsConstants.TMPLAT_TRAFFICKING_DTLS_GRP);

					//artf224134 : use dtOfIncdnt not Date LE Notified
					BookmarkDto bookmarkDtIncident = createBookmark(BookmarkConstants.DT_INCIDENT,
							TypeConvUtil.formDateFormat(currentIncident.getDtOfIncdnt()));
					bookmarkIncidentList.add(bookmarkDtIncident);

					BookmarkDto bookmarkTrfkngCmts = createBookmarkWithCodesTable(BookmarkConstants.TRAFFICKING_TYPE,
							currentIncident.getCdtrfckngType(), CodesConstant.CTRFTYP);
					bookmarkIncidentList.add(bookmarkTrfkngCmts);

					if (ServiceConstants.Y.equals(currentIncident.getIndApproxDate())) {
						BookmarkDto bookmarkDtApprox = createBookmark(BookmarkConstants.IND_APPROX_DT,
								FormConstants.APPROXIMATE_DATE);
						bookmarkIncidentList.add(bookmarkDtApprox);
					}

					BookmarkDto bookmarkDtRespCmts = createBookmark(BookmarkConstants.TXT_TRFCKNG_RESPONSIBLE_COMMENTS,
							currentIncident.getTxtRespComments());
					bookmarkIncidentList.add(bookmarkDtRespCmts);

					BookmarkDto bookmarkVctCmts = createBookmark(BookmarkConstants.TXT_TRFCKNG_VICTIMIZATION_COMMENTS,
							currentIncident.getTxtVictimizationComments());
					bookmarkIncidentList.add(bookmarkVctCmts);

					// Add group and bookmarks to episode group
					incidentGroup.setBookmarkDtoList(bookmarkIncidentList);
					incidentGroups.add(incidentGroup);
					incidentDisplayed = true;
					//formDataGroupList.add(incidentGroup);
				}
			}
		}
		if (incidentDisplayed) {
            FormDataGroupDto incidentParentGroup = createFormDataGroup(
                    FormGroupsConstants.TMPLAT_TRAFFICKING_DTLS_PARENT_GRP,
                    FormGroupsConstants.TMPLAT_TRAFFICKING_DTLS_PARENT_GRP);
			incidentParentGroup.setFormDataGroupList(incidentGroups);
			formDataGroupList.add(incidentParentGroup);
		} else {
			// No incidents exists. Display No Incident message
			FormDataGroupDto noIncident = createFormDataGroup(FormGroupsConstants.TMPLAT_NO_TRAFFICKING_INCIDENT_GRP,
					FormConstants.EMPTY_STRING);
			List<BookmarkDto> bookmarkNoIncidentList = new ArrayList<BookmarkDto>();

			BookmarkDto bookmarkNoData = createBookmark(BookmarkConstants.TXT_NO_TRAFFICKING_INCIDENT,
					FormConstants.TXT_NO_TRAFFICKING_INCIDENT);
			bookmarkNoIncidentList.add(bookmarkNoData);

			noIncident.setBookmarkDtoList(bookmarkNoIncidentList);
			formDataGroupList.add(noIncident);
		}

		preFillData.setFormDataGroupList(formDataGroupList);
		preFillData.setBookmarkDtoList(bookmarkNonFrmGrpList);
		return preFillData;
	}

	private boolean isConfirmedSexTrafficIncidentsAdded(List<TraffickingDto> trfckngDtoList) {
		boolean isConfirmedSexTrafficIncidentsAdded = false;
		if(!CollectionUtils.isEmpty(trfckngDtoList)){
			isConfirmedSexTrafficIncidentsAdded = trfckngDtoList.stream()
					.anyMatch(trf-> SXTR.equalsIgnoreCase(trf.getCdtrfckngType()) && CONF.equalsIgnoreCase(trf.getCdtrfckngStat()));
		}
		return isConfirmedSexTrafficIncidentsAdded;
	}

	private boolean isSuspectedUnConfirmedSexTrafficIncidentsAdded(List<TraffickingDto> trfckngDtoList) {
		boolean isSuspectedUnConfirmedSexTrafficIncidentsAdded = false;
		if(!CollectionUtils.isEmpty(trfckngDtoList)){
			isSuspectedUnConfirmedSexTrafficIncidentsAdded = trfckngDtoList.stream()
					.anyMatch(trf-> SXTR.equalsIgnoreCase(trf.getCdtrfckngType()) && SUSP.equalsIgnoreCase(trf.getCdtrfckngStat()));
		}
		return isSuspectedUnConfirmedSexTrafficIncidentsAdded;
	}
}
