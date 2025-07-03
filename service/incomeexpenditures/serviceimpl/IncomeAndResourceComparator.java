package us.tx.state.dfps.service.incomeexpenditures.serviceimpl;

import java.util.Comparator;

import us.tx.state.dfps.common.domain.IncomeAndResources;

/**
 * Class Description: Comparator class to compare two {@link IncomeAndResources}
 * object
 */
public class IncomeAndResourceComparator implements Comparator<IncomeAndResources> {

	/**
	 * 
	 * Method Description: This method compares two IncomeAndResources object
	 * based on From , TO date date and Income of the IncomeAndResources object
	 * 
	 * @param o1
	 * @param o2
	 * @return
	 */
	public int compare(IncomeAndResources o1, IncomeAndResources o2) {
		return compareIncomeAndResource(o1, o2) == 0
				? (compareFromDate(o1, o2) == 0 ? compareToDate(o1, o2) : compareFromDate(o1, o2))
				: compareIncomeAndResource(o1, o2);
	}

	/**
	 * 
	 * Method Description: This method compares two IncomeAndResources object
	 * based on Income (CD_INC_RSRC_INCOME)
	 * 
	 * @param o1
	 * @param o2
	 * @return
	 */
	private int compareIncomeAndResource(IncomeAndResources o1, IncomeAndResources o2) {
		int comparision1 = o1.getCdIncRsrcIncome().compareTo(o2.getCdIncRsrcIncome());
		return comparision1;
	}

	/**
	 * 
	 * Method Description: This method compares two IncomeAndResources object
	 * based on Date Income and Resource TO (DT_INC_RSRC_TO)
	 * 
	 * @param o1
	 * @param o2
	 * @return
	 */
	private int compareToDate(IncomeAndResources o1, IncomeAndResources o2) {
		int comparision3 = o1.getDtIncRsrcTo().compareTo(o2.getDtIncRsrcTo());
		return comparision3;
	}

	/**
	 * 
	 * Method Description: This method compares two IncomeAndResources object
	 * based on Date Income and Resource FROM (DT_INC_RSRC_FROM)
	 * 
	 * @param o1
	 * @param o2
	 * @return
	 */
	private int compareFromDate(IncomeAndResources o1, IncomeAndResources o2) {
		int comparision2 = o1.getDtIncRsrcFrom().compareTo(o2.getDtIncRsrcFrom());
		return comparision2;
	}

}
