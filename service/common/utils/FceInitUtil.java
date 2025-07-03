package us.tx.state.dfps.service.common.utils;

import java.io.Serializable;
import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.FceAfdcIncomeLimit;
import us.tx.state.dfps.common.domain.FcePweUnderemployed;
import us.tx.state.dfps.common.domain.FceStepparentAllowance;

@Repository
public class FceInitUtil {

	public static final String TRACE_TAG = "FceInit";
	public static final String JNDI_NAME = "";
	protected static FceLookupArrays fceLookupArrays = null;
	private static final int MAX_PARENTS = 2;
	private static final int MAX_NBR_CERTIFIED = 99;

	@Autowired
	SessionFactory sessionFactory;

	public double getFceAfdcIncomeLimit(int nbrParents, int nbrCertifiedGroup) {
		initialize();

		// Kathy Campbell said sometimes they don't know how many/who are the
		// parents
		if (nbrParents > 2) {
			nbrParents = 2;
		}

		if (nbrCertifiedGroup > MAX_NBR_CERTIFIED) {
			nbrCertifiedGroup = MAX_NBR_CERTIFIED;
		}

		return fceLookupArrays.fceAfdcIncomeLimitArray[nbrParents][nbrCertifiedGroup];
	}

	public double getFceAfdcIncomeLimit100(int nbrParents, int nbrCertifiedGroup) {
		initialize();

		// Kathy Campbell said sometimes they don't know how many/who are the
		// parents
		if (nbrParents > 2) {
			nbrParents = 2;
		}

		if (nbrCertifiedGroup > MAX_NBR_CERTIFIED) {
			nbrCertifiedGroup = MAX_NBR_CERTIFIED;
		}

		return fceLookupArrays.fceAfdcIncomeLimitArray100[nbrParents][nbrCertifiedGroup];
	}

	public double getFceStepparentAllowance(int nbrNotCertifiedGroup) {
		initialize();

		if (nbrNotCertifiedGroup == 0) {
			return 0;
		}

		if (nbrNotCertifiedGroup > MAX_NBR_CERTIFIED) {
			nbrNotCertifiedGroup = MAX_NBR_CERTIFIED;
		}

		return fceLookupArrays.fceStepparentAllowanceArray[nbrNotCertifiedGroup];
	}

	public double getFcePweUnderemployed(int nbrCertifiedGroup) {
		initialize();

		if (nbrCertifiedGroup > MAX_NBR_CERTIFIED) {
			nbrCertifiedGroup = MAX_NBR_CERTIFIED;
		}
		return fceLookupArrays.fcePweUnderemployedArray[nbrCertifiedGroup];
	}

	/**
	 * Method called by the initialization framework. Place initialization code
	 * here.
	 *
	 * @throws BasePrsException
	 *             or subclasses thereof.
	 */
	public void initialize()

	{
		if (ObjectUtils.isEmpty(fceLookupArrays)) {
			fceLookupArrays = new FceLookupArrays();
			// merged loadFceAfdcIncomeLimit and loadFceAfdcIncomeLimit100
			loadFceAfdcIncomeLimit();
			loadFceStepparentAllowance();
			loadFcePweUnderemployed();
		}
	}

	private void loadFceAfdcIncomeLimit() {

		@SuppressWarnings("unchecked")
		List<FceAfdcIncomeLimit> fceAfdcIncomeLimits = (List<FceAfdcIncomeLimit>) sessionFactory.getCurrentSession()
				.createCriteria(FceAfdcIncomeLimit.class).addOrder(Order.asc("nbrCrtfdGrp"))
				.setFetchSize(MAX_NBR_CERTIFIED).list();

		int certifiedGroupIndex = 1;
		for (FceAfdcIncomeLimit f : fceAfdcIncomeLimits) {
			fceLookupArrays.fceAfdcIncomeLimitArray[0][certifiedGroupIndex] = f.getAmtNoParentCrtfdGrp();

			fceLookupArrays.fceAfdcIncomeLimitArray[1][certifiedGroupIndex] = f.getAmtOneParentCrtfdGrp();

			fceLookupArrays.fceAfdcIncomeLimitArray[2][certifiedGroupIndex] = f.getAmtTwoParentCrtfdGrp();

			fceLookupArrays.fceAfdcIncomeLimitArray100[0][certifiedGroupIndex] = f.getAmtNoParentCrtfdGrp100();

			fceLookupArrays.fceAfdcIncomeLimitArray100[1][certifiedGroupIndex] = f.getAmtOneParentCrtfdGrp100();

			fceLookupArrays.fceAfdcIncomeLimitArray100[2][certifiedGroupIndex] = f.getAmtTwoParentCrtfdGrp100();

			certifiedGroupIndex++;
		}
	}

	private void loadFceStepparentAllowance() {

		List<FceStepparentAllowance> fceStepparentAllowances = (List<FceStepparentAllowance>) sessionFactory
				.getCurrentSession().createCriteria(FceStepparentAllowance.class)
				.addOrder(Order.asc("nbrFamilyNotCertified")).setFetchSize(MAX_NBR_CERTIFIED).list();

		int certifiedGroupIndex = 1;
		for (FceStepparentAllowance f : fceStepparentAllowances) {

			fceLookupArrays.fceStepparentAllowanceArray[certifiedGroupIndex] = f.getAmtAllowanceDeduction();

			certifiedGroupIndex++;
		}
	}

	private void loadFcePweUnderemployed() {

		List<FcePweUnderemployed> fcePweUnderemployedList = (List<FcePweUnderemployed>) sessionFactory
				.getCurrentSession().createCriteria(FcePweUnderemployed.class)
				.addOrder(Order.asc("nbrFamilyCertifiedGrp")).setFetchSize(MAX_NBR_CERTIFIED).list();

		// mcclain, 08/05/2003; off by one
		int certifiedGroupIndex = 1;
		for (FcePweUnderemployed f : fcePweUnderemployedList) {
			fceLookupArrays.fcePweUnderemployedArray[certifiedGroupIndex] = f.getAmtIncomeLimit();

			certifiedGroupIndex++;
		}

	}

	private class FceLookupArrays implements Serializable {

		// first index is nbrParents
		// second index is nbrCertifiedGroup
		private double[][] fceAfdcIncomeLimitArray = new double[MAX_PARENTS + 1][MAX_NBR_CERTIFIED + 1];

		// SIR 1010395 Added Array for 100% Income Limits
		private double[][] fceAfdcIncomeLimitArray100 = new double[MAX_PARENTS + 1][MAX_NBR_CERTIFIED + 1];
		// end SIR 1010395

		// index is number NOT in the certified group
		private double[] fceStepparentAllowanceArray = new double[MAX_NBR_CERTIFIED + 1];

		// index is nbrCertifiedGroup
		private double[] fcePweUnderemployedArray = new double[MAX_NBR_CERTIFIED + 1];
	}
}
