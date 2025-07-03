package us.tx.state.dfps.service.fostercarereview.daoimpl;

import org.springframework.stereotype.Repository;

import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.exception.DataLayerException;
import us.tx.state.dfps.service.fce.EligibilitySummaryDto;
import us.tx.state.dfps.service.fostercarereview.dao.FceHelperDao;

/**
 * service-ejb-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter
 * the description of class> Nov 15, 2017- 10:59:08 AM © 2017 Texas Department
 * of Family and Protective Services
 */

// this class for FceHelper
@Repository
public class FceHelperDaoImpl implements FceHelperDao {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * us.tx.state.dfps.service.fce.dao.FceHelperDao#verifyCanSave(us.tx.state.
	 * dfps.service.fce.EligibilitySummaryDto)
	 */
	@Override
	public void verifyCanSave(EligibilitySummaryDto eligibilitySummaryDto) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see us.tx.state.dfps.service.fce.dao.FceHelperDao#verifyOpenStage(long)
	 */
	@Override
	public void verifyOpenStage(long idStage) {
		// TODO Auto-generated method stub

	}

	@Override
	public long verifyNonZero(String propertyName, long value) {

		if (value == 0) {
			String exceptionMessage = ServiceConstants.PROPERTY + ServiceConstants.SPACE + propertyName
					+ ServiceConstants.NONZERO;
			throw new DataLayerException(exceptionMessage);
		}
		return value;
	}

}
