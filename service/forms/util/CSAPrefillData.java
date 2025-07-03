package us.tx.state.dfps.service.forms.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.service.casepackage.dto.CSADto;
import us.tx.state.dfps.service.casepackage.dto.CSAEpisodeDto;
import us.tx.state.dfps.service.casepackage.dto.CsaEpisodesIncdntDto;
import us.tx.state.dfps.service.common.BookmarkConstants;
import us.tx.state.dfps.service.common.FormConstants;
import us.tx.state.dfps.service.common.FormGroupsConstants;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.forms.dto.BookmarkDto;
import us.tx.state.dfps.service.forms.dto.FormDataGroupDto;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;

@Component
/**
 * Name:MedicalConsenterForNonDFPSEmployeePrefillData Description:
 * MedicalConsenterForNonDFPSEmployeePrefillData will implemented
 * returnPrefillData operation defined in DocumentServiceUtil Interface to
 * populate the prefill data for FSNA forms in stages FPR, FSU, FRE Jan 04, 2018
 * - 04:40:29 PM
 */
public class CSAPrefillData extends DocumentServiceUtil {

	/**
	 * Method Description: This method is used to prefill the data from the
	 * different Dao by passing Dao output Dtos and bookmark and form group
	 * bookmark Dto as objects as input request. No longer used.
	 * 
	 * @param parentDtoobj
	 * @param bookmarkDtoObj
	 * @return PreFillData
	 * 
	 */
	@Override
	public PreFillDataServiceDto returnPrefillData(Object parentDtoObj) {
		List<BookmarkDto> bookmarkNonFrmGrpList = new ArrayList<BookmarkDto>();
		List<FormDataGroupDto> formDataGroupList = new ArrayList<FormDataGroupDto>();

		CSADto inputPrefillRes = (CSADto) parentDtoObj;

		if (!ObjectUtils.isEmpty(inputPrefillRes)) {

			// Set case details
			BookmarkDto bookmarkChildNm = createBookmark(BookmarkConstants.TITLE_CHILD_NAME,
					inputPrefillRes.getNmPerson());
			bookmarkNonFrmGrpList.add(bookmarkChildNm);
			BookmarkDto bookmarkChildIDPerson = createBookmark(BookmarkConstants.TITLE_CHILD_ID_PERS,
					inputPrefillRes.getIdPerson());
			bookmarkNonFrmGrpList.add(bookmarkChildIDPerson);

			// Check if data exists and set the section title for episode list.
			// Else title
			// should be empty
			if (!ObjectUtils.isEmpty(inputPrefillRes.getCsaIncidents())) {
				// Episode(s) will be displayed when we have data for episode
				// group
				BookmarkDto episodeTitle = createBookmark(BookmarkConstants.TITLE_FORM,
						FormConstants.CSA_EPISODES_TITLE);
				bookmarkNonFrmGrpList.add(episodeTitle);

				List<CsaEpisodesIncdntDto> inputPrefillList = inputPrefillRes.getCsaIncidents();

					// create top level group containing everything
					List<FormDataGroupDto> episodeChildGroups = new ArrayList<FormDataGroupDto>();
					FormDataGroupDto episodesGroup = createFormDataGroup(FormGroupsConstants.TMPLAT_EPISODE_GRP,
							FormConstants.EMPTY_STRING);


					// Prefill Incidents if exists

					for (CsaEpisodesIncdntDto currentIncident : inputPrefillList) {
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
			} else {
				// No episodes exists. Display No Episode/Incident message
				// This is a group, so aff to form data group
				FormDataGroupDto noIncident = createFormDataGroup(FormGroupsConstants.TMPLAT_NO_INCIDENT_GRP,
						FormConstants.EMPTY_STRING);
				List<BookmarkDto> bookmarkNoIncidentList = new ArrayList<BookmarkDto>();

				BookmarkDto bookmarkNoData = createBookmark(BookmarkConstants.TXT_NO_INCIDENT,
						FormConstants.CSA_NO_INCIDENT_MSG);
				bookmarkNoIncidentList.add(bookmarkNoData);
				noIncident.setBookmarkDtoList(bookmarkNoIncidentList);
				formDataGroupList.add(noIncident);

			}
		}

		// Add non group bookmarks and form data groups to prefill
		PreFillDataServiceDto preFillData = new PreFillDataServiceDto();
		preFillData.setFormDataGroupList(formDataGroupList);
		preFillData.setBookmarkDtoList(bookmarkNonFrmGrpList);
		return preFillData;
	}

}
