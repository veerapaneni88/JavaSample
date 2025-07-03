package us.tx.state.dfps.service.pcaeligdetermination.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.service.admin.dto.LegalStatusDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.exception.ServiceLayerException;
import us.tx.state.dfps.service.legalstatus.dao.LegalStatusDao;
import us.tx.state.dfps.service.pca.dto.PcaAppAndBackgroundDto;
import us.tx.state.dfps.service.placement.service.PlacementService;
import us.tx.state.dfps.service.subcare.dto.PlacementDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:PcaUtility
 * Oct 16, 2017- 12:19:42 PM © 2017 Texas Department of Family and Protective
 * Services
 */
@Component
public class PcaUtility {

	@Autowired
	PlacementService placementService;

	@Autowired
	LegalStatusDao legalStatusDao;

	public int findPlacementDuration(Long idPlcmtEvent) {
		int monthsDiff = 0;
		PlacementDto plcmtValueBean = placementService.fetchPlacement(idPlcmtEvent);
		if (plcmtValueBean != null && plcmtValueBean.getIdPlcmtEvent() != 0) {
			monthsDiff = getMonthDifferenceForDates(plcmtValueBean.getDtParentPlcmtStart(),
					plcmtValueBean.getDtPlcmtEnd());
		}

		return monthsDiff;

	}

	/**
	 * Method Name: getMonthDifferenceForDates Method Description:.
	 *
	 * @param fromDate
	 *            the from date
	 * @param toDate
	 *            the to date
	 * @return the month difference for dates
	 */
	public int getMonthDifferenceForDates(Date fromDate, Date toDate) {
		int count = 0;
		if (fromDate != null && toDate != null) {
			if (toDate.toString().equals(ServiceConstants.MAX_JAVA_DATE)) {
				toDate = new java.util.Date();
			}

			Calendar fromCal = new GregorianCalendar();
			fromCal.setTime(fromDate);

			Calendar toCal = new GregorianCalendar();
			toCal.setTime(toDate);

			for (fromCal.add(Calendar.MONTH, 1); fromCal.compareTo(toCal) <= 0; fromCal.add(Calendar.MONTH, 1)) {
				count++;
			}
		}
		return count;
	}

	@SuppressWarnings("unused")
	public int findPlacementDuration(List idPlcmtEventList)

	{
		int monthsDiff = 0;
		PlacementDto plcmtValueBean = new PlacementDto();

		Iterator it = idPlcmtEventList.iterator();
		String dtStart = ServiceConstants.NULL_JAVA_DATE;
		String dtEnd = ServiceConstants.NULL_JAVA_DATE;
		String dtStartCurrent = ServiceConstants.NULL_JAVA_DATE;
		String dtEndCurrent = ServiceConstants.NULL_JAVA_DATE;
		String dtStartPrev = ServiceConstants.NULL_JAVA_DATE;
		int index = 0;
		boolean gapFound = false;

		while (it.hasNext() && !gapFound) {
			dtStartPrev = dtStartCurrent;

			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(ServiceConstants.SLASH_DATE_MASK);

			plcmtValueBean = placementService.fetchPlacement(Long.valueOf((it.next().toString())));

			if (index == 0) {
				dtEnd = dtEndCurrent;
			}

			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

			if (sdf.format(dtStartPrev).equals(sdf.format(dtEndCurrent)) || index == 0) {
				dtStart = dtStartCurrent;
			} else {
				gapFound = true;
			}

			index++;
		}

		if (plcmtValueBean != null && plcmtValueBean.getIdPlcmtEvent() != 0) {
			monthsDiff = getMonthDifference(dtStart, dtEnd);
		}

		return monthsDiff;

	}

	public int getMonthDifference(String fromDateString, String toDateString) {

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(ServiceConstants.SLASH_DATE_MASK);

		Date fromDate;
		Date toDate;
		try {
			fromDate = simpleDateFormat.parse(fromDateString.toString());
			toDate = simpleDateFormat.parse(toDateString.toString());
		} catch (ParseException e) {
			ServiceLayerException serviceLayerException = new ServiceLayerException(e.getMessage());
			serviceLayerException.initCause(e);
			throw serviceLayerException;
		}

		int count = 0;
		if (fromDate != null && toDate != null) {
			if (toDate.toString().equals(ServiceConstants.MAX_JAVA_DATE)) {
				toDate = new Date();
			}

			Calendar fromCal = new GregorianCalendar();
			fromCal.setTime(fromDate);

			Calendar toCal = new GregorianCalendar();
			toCal.setTime(toDate);

			for (fromCal.add(Calendar.MONTH, 1); fromCal.compareTo(toCal) <= 0; fromCal.add(Calendar.MONTH, 1)) {
				count++;
			}
		}
		return count;
	}

	/**
	 * 
	 * Method Name: clearAppWithdrawal Method Description: This method clears
	 * PCA Application Withdrawal values.
	 * 
	 * @param appValueBean
	 */
	public static void clearAppWithdrawal(PcaAppAndBackgroundDto appValueBean) {
		if (appValueBean != null) {
			appValueBean.setCdWithdrawRsn("");
			appValueBean.setWithdrawRsn("");
		}
	}

	/**
	 * 
	 * Method Name: findLatestLegalStatusForChild Method Description:This method
	 * is used to get the most recent legal status of the child
	 * 
	 * @param idPerson
	 * @return
	 */
	public String findLatestLegalStatusForChild(Long idPerson) {
		String legalStatusCode = ServiceConstants.EMPTY_STR;
		LegalStatusDto legalStatusDto = legalStatusDao.getRecentLegalStatusForChild(idPerson);
		if (legalStatusDto != null) {
			legalStatusCode = legalStatusDto.getCdLegalStatStatus();
		}
		return legalStatusCode;
	}

	/**
	 * This method returns Placement Duration of the given Placement.
	 * 
	 * @param idPlcmtEventList
	 * 
	 * @return int Placement Duration
	 * 
	 */
	public int findPlacementDuration(List<Long> idPlcmtEventList, PlacementDto selectedPlcmtValueBean) {
		int monthsDiff = 0;

		PlacementDto plcmtValueBean = null;

		// get start date of earliest placement and end date of latest placement
		Iterator it = idPlcmtEventList.iterator();
		Date dtStart = null;
		Date dtEnd = null;
		Date dtStartCurrent = null;
		Date dtEndCurrent = null;
		Date dtStartPrev = null;
		int index = 0;
		boolean gapFound = false;

		while (it.hasNext() && !gapFound) {
			dtStartPrev = dtStartCurrent;
			plcmtValueBean = placementService.fetchPlacement(((Long) it.next()));

			if (!ObjectUtils.isEmpty(plcmtValueBean.getIdPlcmtEvent())
					&& plcmtValueBean.getIdPlcmtEvent().equals(selectedPlcmtValueBean.getIdPlcmtEvent())) {
				dtStartCurrent = selectedPlcmtValueBean.getDtPlcmtStart();
				dtEndCurrent = selectedPlcmtValueBean.getDtPlcmtEnd();
			} else {
				dtStartCurrent = plcmtValueBean.getDtPlcmtStart();
				dtEndCurrent = plcmtValueBean.getDtPlcmtEnd();
			}
			if (index == 0) {
				dtEnd = dtEndCurrent;
			}

			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

			if (sdf.format(dtStartPrev).equals(sdf.format(dtEndCurrent)) || index == 0) {
				dtStart = dtStartCurrent;
			} else {
				gapFound = true;
			}

			index++;
		}

		if (plcmtValueBean != null && plcmtValueBean.getIdPlcmtEvent() != 0) {
			monthsDiff = getMonthDifferenceForDates(dtStart, dtEnd);
		}

		return monthsDiff;
	}

}
