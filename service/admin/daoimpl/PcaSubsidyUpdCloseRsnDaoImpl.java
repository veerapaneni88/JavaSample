package us.tx.state.dfps.service.admin.daoimpl;

import java.util.Locale;

import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.service.admin.dao.PcaSubsidyUpdCloseRsnDao;
import us.tx.state.dfps.service.admin.dto.PcaSubsidyUpdCloseRsnInDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Update PCA
 * subsidy Aug 10, 2017- 8:21:19 PM © 2017 Texas Department of Family and
 * Protective Services
 */
@Repository
public class PcaSubsidyUpdCloseRsnDaoImpl implements PcaSubsidyUpdCloseRsnDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${PcaSubsidyUpdCloseRsnDaoImpl.updatePCASubsidy}")
	private String updatePCASubsidy;

	private static final Logger log = Logger.getLogger(PcaSubsidyUpdCloseRsnDaoImpl.class);

	public PcaSubsidyUpdCloseRsnDaoImpl() {
		super();
	}

	/**
	 * 
	 * Method Name: updatePCASubsidy Method Description: Update the PCA record
	 * for the Person
	 * 
	 * @param pInputDataRec
	 * @return int
	 * @,DataNotFoundException
	 */
	@SuppressWarnings("unchecked")
	@Override
	public int updatePCASubsidy(PcaSubsidyUpdCloseRsnInDto pInputDataRec) {
		log.debug("Entering method PcaSubsidyUpdCloseRsnQUERYdam in PcaSubsidyUpdCloseRsnDaoImpl");
		int rowCount = 0;
		switch (pInputDataRec.getReqFuncCd()) {
		case ServiceConstants.REQ_FUNC_CD_UPDATE:
			SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(updatePCASubsidy)
					.setParameter("hI_dtDtEnd", TypeConvUtil.isDateNullCheck(pInputDataRec.getDtEnd()))
					.setParameter("hI_ulIdPCASubsidy", pInputDataRec.getIdPCASubsidy())
					.setParameter("hI_szCdCloseRsn", pInputDataRec.getCdCloseRsn())
					.setParameter("hI_tsLastUpdate", pInputDataRec.getTsLastUpdate()));
			rowCount = sQLQuery1.executeUpdate();
			break;
		}
		if (rowCount == 0) {
			throw new DataNotFoundException(
					messageSource.getMessage("Caudm5dDaoImpl.pca.record.not.update", null, Locale.US));
		}
		log.debug("Exiting method PcaSubsidyUpdCloseRsnQUERYdam in PcaSubsidyUpdCloseRsnDaoImpl");
		return rowCount;
	}
}
