/**
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *Class Description:class to update stage 
 *Apr 18, 2018- 11:41:06 AM
 *© 2017 Texas Department of Family and Protective Services 
 */
package us.tx.state.dfps.service.casemanagement.daoimpl;

import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.service.casemanagement.dao.StageUpdIDByStageTypeDao;
import us.tx.state.dfps.service.casemanagement.dto.StageUpdIDByStageTypeInDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.TypeConvUtil;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:CSUB64S Aug
 * 21, 2017- 3:19:25 PM © 2017 Texas Department of Family and Protective
 * Services.
 */
@Repository
public class StageUpdIDByStageTypeDaoImpl implements StageUpdIDByStageTypeDao {

	/** The session factory. */
	@Autowired
	private SessionFactory sessionFactory;

	/** The caud 42 d AU ddam. */
	@Value("${Caud42dDaoImpl.caud42dAUDdam}")
	private String caud42dAUDdam;

	/**
	 * This method will use IdStage to update the CdStageType on the Stage
	 * Table.
	 *
	 * @param pInputDataRec
	 *            the input data rec
	 * @return RelVal
	 */
	@Override
	public String caud42dAUDdam(StageUpdIDByStageTypeInDto pInputDataRec) {
		String relVal = ServiceConstants.FND_FAIL;
		switch (pInputDataRec.getReqFuncCd()) {
		case ServiceConstants.REQ_FUNC_CD_ADD:
			relVal = ServiceConstants.ARC_ERR_BAD_FUNC_CD;
			break;
		case ServiceConstants.REQ_FUNC_CD_UPDATE:
			SQLQuery sQLQuery1 = sessionFactory.getCurrentSession().createSQLQuery(caud42dAUDdam);
			sQLQuery1.setParameter("idStage", pInputDataRec.getIdStage());
			sQLQuery1.setParameter("cdStageType", pInputDataRec.getCdStageType());
			sQLQuery1.setParameter("tsLastUpdate", pInputDataRec.getDtLastUpdate());
			int rowCount = sQLQuery1.executeUpdate();
			if (!TypeConvUtil.isNullOrEmpty(rowCount)) {
				relVal = ServiceConstants.SQL_SUCCESS;
			}
			break;
		case ServiceConstants.REQ_FUNC_CD_DELETE:
			relVal = ServiceConstants.ARC_ERR_BAD_FUNC_CD;
			break;
		default:
			relVal = ServiceConstants.ARC_ERR_BAD_FUNC_CD;
			break;
		}
		return relVal;
	}
}
