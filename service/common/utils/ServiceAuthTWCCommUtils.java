package us.tx.state.dfps.service.common.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.DateUtils;
import us.tx.state.dfps.service.dcr.dao.DayCareRequestDao;
import us.tx.state.dfps.service.dcr.dto.DayCarePersonDto;
import us.tx.state.dfps.service.dcr.dto.DayCareRequestDto;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.financial.dao.ServiceAuthExtCommDao;
import us.tx.state.dfps.service.financial.dto.ServiceAuthExtCommDto;
import us.tx.state.dfps.service.financial.dto.ServiceAuthTWCBaselineDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:this class
 * is for ServiceAuthTWCCommHelper Sep 22, 2017- 11:56:57 AM © 2017 Texas
 * Department of Family and Protective Services
 */
@Repository
public class ServiceAuthTWCCommUtils {

	@Autowired
	DayCareRequestDao dayCareRequestDao;

	@Autowired
	ServiceAuthExtCommDao serviceAuthExtCommDao;

	private static final Logger log = Logger.getLogger("ServiceBusiness-ServiceAuthorizationDetailServiceLog");

	public enum SvcAuthDtlTWCChange {
		NOCHANGE, INITIAL, UPDATE, TERMINATE
	};

	/**
	 * This method finds if any of the Service Authorization Details or Day Care
	 * Request Details have been modified after Previous Service Authorization
	 * has been sent to TWC.
	 * 
	 * 
	 * @param idSvcAuthEvent
	 * @param idSvcAuth
	 */
	public SvcAuthDtlTWCChange isTWCBaselineModified(Long idSvcAuthEvent, Long idSvcAuth)

	{

		SvcAuthDtlTWCChange baselineChanged = SvcAuthDtlTWCChange.NOCHANGE;

		// First Check if Service Auth has ever been sent to TWC.
		try {
			ServiceAuthExtCommDto serviceAuthExtCommDto = serviceAuthExtCommDao
					.selectLatestServiceAuthExtComm(idSvcAuth);

			if (serviceAuthExtCommDto == null || serviceAuthExtCommDto.getIdSvcauthExtComm() == 0) {// Initial
																									// Communication.
				baselineChanged = SvcAuthDtlTWCChange.INITIAL;
			} else {
				Map<Integer, SvcAuthDtlTWCChange> modifiedSvcAuthDtlIds = getModifiedSvcAuthDtlForTWC(idSvcAuthEvent,
						idSvcAuth);
				for (SvcAuthDtlTWCChange typeOfChange : modifiedSvcAuthDtlIds.values()) {
					if (typeOfChange != SvcAuthDtlTWCChange.NOCHANGE) {
						baselineChanged = typeOfChange;
						if (typeOfChange == SvcAuthDtlTWCChange.TERMINATE) {
							break;
						}
					}
				}
			}
		} catch (DataNotFoundException e) {
			log.error(e.getMessage());
		}
		return baselineChanged;
	}

	/**
	 * This method returns all the changes to Service Authorization Details
	 * Records after last Communication sent to TWC.
	 * 
	 * @param idSvcAuthEvent
	 * @param idSvcAuth
	 * 
	 * @return modifiedSvcAuthDtlIds - Map<Integer, SvcAuthDtlTWCChange>
	 */
	public Map<Integer, SvcAuthDtlTWCChange> getModifiedSvcAuthDtlForTWC(Long idSvcAuthEvent, Long idSvcAuth)

	{

		Map<Integer, SvcAuthDtlTWCChange> modifiedSvcAuthDtlIds = new HashMap<Integer, SvcAuthDtlTWCChange>();

		try {
			// Retrieve Baseline Records for the given Service Authorization Id.
			List<ServiceAuthTWCBaselineDto> svcAuthTwcBaselineList = serviceAuthExtCommDao
					.selectSvcAuthTWCBaselineForSvcAuth(idSvcAuth);

			// Retrieve Current Service Authorization and DayCare Request
			// Details.
			DayCareRequestDto dayCareRequestDto = dayCareRequestDao
					.fetchDayCareDetailsForSvcAuthEventId(idSvcAuthEvent);

			// Compare Day Care Request and Service Authorization Values with
			// the Baseline.
			List<DayCarePersonDto> dayCarePersonList = dayCareRequestDto.getDayCarePersonDtoList();
			for (int i = 0; i < dayCarePersonList.size(); i++) {
				DayCarePersonDto dayCarePersonDto = (DayCarePersonDto) dayCarePersonList.get(i);
				ServiceAuthTWCBaselineDto serviceAuthTWCBaselineDto = getBaselineRecFromList(svcAuthTwcBaselineList,
						dayCarePersonDto.getIdSvcAuthDtl());
				SvcAuthDtlTWCChange typeOfChange = compareTwcBaseline(dayCarePersonDto, serviceAuthTWCBaselineDto);
				modifiedSvcAuthDtlIds.put(Integer.valueOf(dayCarePersonDto.getIdSvcAuthDtl().intValue()), typeOfChange);
			}
		} catch (DataNotFoundException e) {
			log.error(e.getMessage());
		}

		return modifiedSvcAuthDtlIds;
	}

	/**
	 * This method returns Baseline Record from the List using Service
	 * Authorization Details Id
	 * 
	 * @param List
	 *            <ServiceAuthTWCBaselineDto>
	 * @param idSvcAuthDtl
	 * 
	 * @return ServiceAuthTWCBaselineDto
	 */
	private static ServiceAuthTWCBaselineDto getBaselineRecFromList(
			List<ServiceAuthTWCBaselineDto> svcAuthTwcBaselineList, Long idSvcAuthDtl) {

		ServiceAuthTWCBaselineDto serviceAuthTWCBaselineDto = new ServiceAuthTWCBaselineDto();

		for (ServiceAuthTWCBaselineDto tmpSvcAuthTwcBaseline : svcAuthTwcBaselineList) {
			if (!ObjectUtils.isEmpty(tmpSvcAuthTwcBaseline.getIdSvcAuthDtl())
					&& tmpSvcAuthTwcBaseline.getIdSvcAuthDtl().equals(idSvcAuthDtl)) {
				serviceAuthTWCBaselineDto = tmpSvcAuthTwcBaseline;
				break;
			}
		}

		return serviceAuthTWCBaselineDto;
	}

	/**
	 * This method compares Day Care and Service Authorization fields with Last
	 * TWC baseline.
	 * 
	 * Fields that can be modified after Service Authorization has been
	 * Approved. - Day Care Request Page. - Days of Week. - Summer Type. -
	 * Weekend Type. - Variable Schedule Indicator. - Max Days - Variable
	 * Schedule. - Hours Needed, Comments. - Day Care Provider.
	 * 
	 * - Service Authorization Details Page. - Service Authorization Details
	 * Termination.
	 * 
	 * @param DayCarePersonDto
	 * @param ServiceAuthTWCBaselineDto
	 * 
	 * @return SvcAuthDtlTWCChange
	 */
	private static SvcAuthDtlTWCChange compareTwcBaseline(DayCarePersonDto dayCarePersonDto,
			ServiceAuthTWCBaselineDto serviceAuthTWCBaselineDto) {
		SvcAuthDtlTWCChange typeOfChange = SvcAuthDtlTWCChange.NOCHANGE;

		if (serviceAuthTWCBaselineDto.getIdSvcAuthDtl() == 0) {
			typeOfChange = SvcAuthDtlTWCChange.INITIAL;
		}
		// Day Care Request Page - Days Of Week.
		else if (!compare(dayCarePersonDto.getIndSun(), serviceAuthTWCBaselineDto.getIndSun())
				|| !compare(dayCarePersonDto.getIndMon(), serviceAuthTWCBaselineDto.getIndMon())
				|| !compare(dayCarePersonDto.getIndTue(), serviceAuthTWCBaselineDto.getIndTue())
				|| !compare(dayCarePersonDto.getIndWed(), serviceAuthTWCBaselineDto.getIndWed())
				|| !compare(dayCarePersonDto.getIndThu(), serviceAuthTWCBaselineDto.getIndThu())
				|| !compare(dayCarePersonDto.getIndFri(), serviceAuthTWCBaselineDto.getIndFri())
				|| !compare(dayCarePersonDto.getIndSat(), serviceAuthTWCBaselineDto.getIndSat())) {
			typeOfChange = SvcAuthDtlTWCChange.UPDATE;
		}
		// Day Care Request Page- Summer Type, Week End Type, Variable
		// schedule Indicator.
		else if (!compare(dayCarePersonDto.getCdSummerType(), serviceAuthTWCBaselineDto.getCdSummerType())
				|| !compare(dayCarePersonDto.getCdWeekendType(), serviceAuthTWCBaselineDto.getCdWeekendType())
				|| !compare(dayCarePersonDto.getIndVarSch(), serviceAuthTWCBaselineDto.getIndVarSch())
				|| !compare(dayCarePersonDto.getCdVarSchMaxDays(), serviceAuthTWCBaselineDto.getCdVarSchMaxDays())) {
			typeOfChange = SvcAuthDtlTWCChange.UPDATE;
		}
		// Day Care Request Page - Hours Needed, Comments.
		else if (!compare(dayCarePersonDto.getDayHoursNeeded(), serviceAuthTWCBaselineDto.getTxtHoursNeeded())
				|| !compare(dayCarePersonDto.getTxtComments(), serviceAuthTWCBaselineDto.getTxtComments())) {
			typeOfChange = SvcAuthDtlTWCChange.UPDATE;
		}
		// Day Care Request Page - Day Care Provider.
		else if ((!ObjectUtils.isEmpty(dayCarePersonDto.getIdFacilityActive())
				&& !dayCarePersonDto.getIdFacilityActive().equals(serviceAuthTWCBaselineDto.getIdPrevFacility()))
				|| ObjectUtils.isEmpty(dayCarePersonDto.getIdFacilityActive())
						&& !ObjectUtils.isEmpty(serviceAuthTWCBaselineDto.getIdPrevFacility())) {
			typeOfChange = SvcAuthDtlTWCChange.UPDATE;
		}
		// Service Authorization Page - Terminate Date.
		else if (DateUtils.minutesDifference(dayCarePersonDto.getDtSvcAuthDtlTerm(),
				serviceAuthTWCBaselineDto.getDtSvcAuthDtlTerm()) != 0) {
			typeOfChange = SvcAuthDtlTWCChange.TERMINATE;
		}
		// Service Authorization Page - Authorization Type to Terminate.
		else if (ServiceConstants.CSVATYPE_TRM.equals(dayCarePersonDto.getCdSvcAuthDtlAuthType())
				&& !compare(dayCarePersonDto.getCdSvcAuthDtlAuthType(),
						serviceAuthTWCBaselineDto.getCdSvcAuthDtlAuthType())) {
			typeOfChange = SvcAuthDtlTWCChange.TERMINATE;
		}

		return typeOfChange;
	}

	/**
	 * This method compares two given strings.
	 * 
	 * @param String1
	 * @param String2
	 * 
	 * @return True if they are equal False if they are not Equal returns True
	 *         if both are null or empty.
	 */
	private static boolean compare(String str1, String str2) {
		boolean same = true;
		// Both are not null, compare the values.
		if (isValid(str1) && isValid(str2)) {
			same = str1.equals(str2) ? true : false;
		}
		// If Both are Null, return true.
		else if (!isValid(str1) && !isValid(str2)) {
			same = true;
		}
		// If one of them are Null, return false
		else {
			same = false;
		}

		return same;
	}

	/**
	 * Checks to see if a given string is valid. This includes checking that the
	 * string is not null or empty.
	 *
	 * @param value
	 *            - the string that is being evaluated
	 * @return boolean - whether the string is valid
	 */
	private static boolean isValid(String value) {
		if (value == null) {
			return false;
		}
		String trimmedString = value.trim();
		return (trimmedString.length() > 0);
	}

}