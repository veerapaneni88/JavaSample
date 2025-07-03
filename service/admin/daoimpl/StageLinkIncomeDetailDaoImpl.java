package us.tx.state.dfps.service.admin.daoimpl;

import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.service.admin.dao.StageLinkIncomeDetailDao;
import us.tx.state.dfps.service.admin.dto.StageLinkIncomeDetailInDto;
import us.tx.state.dfps.service.admin.dto.StageLinkIncomeDetailOutDto;
/**
 * 
 *service-business- IMPACT PHASE 2 MODERNIZATION
 *
 *Class Description: Clsc84dDaoImpl
 *
 *Aug 6, 2017- 5:31:04 PM
 *© 2017 Texas Department of Family and Protective Services
 */
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Aug 14, 2017- 1:44:14 PM © 2017 Texas Department of
 * Family and Protective Services
 */
@Repository
public class StageLinkIncomeDetailDaoImpl implements StageLinkIncomeDetailDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${StageLinkIncomeDetailDaoImpl.stageIncomingDtls}")
	private transient String stageIncomingDtls;

	private static final Logger log = Logger.getLogger(StageLinkIncomeDetailDaoImpl.class);

	public StageLinkIncomeDetailDaoImpl() {
		super();
	}

	/**
	 * 
	 * Method Name: stageIncomingDtls Method Description: Get data from Stage
	 * Link and Incoming Detail tables.
	 * 
	 * @param pInputDataRec
	 * @return List<StageLinkIncomeDetailOutDto> @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<StageLinkIncomeDetailOutDto> stageIncomingDtls(StageLinkIncomeDetailInDto pInputDataRec) {
		log.debug("Entering method StageLinkIncomeDetailQUERYdam in StageLinkIncomeDetailDaoImpl");
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(stageIncomingDtls)
				.setResultTransformer(Transformers.aliasToBean(StageLinkIncomeDetailOutDto.class)));
		sQLQuery1.addScalar("dtIncomingCall", StandardBasicTypes.TIMESTAMP);
		sQLQuery1.addScalar("idPriorStage", StandardBasicTypes.LONG);
		sQLQuery1.addScalar("indIncmgSuspMeth", StandardBasicTypes.STRING);
		sQLQuery1.setParameter("hI_ulIdStage", pInputDataRec.getIdStage());
		List<StageLinkIncomeDetailOutDto> liClsc84doDto = (List<StageLinkIncomeDetailOutDto>) sQLQuery1.list();
		if (TypeConvUtil.isNullOrEmpty(liClsc84doDto)) {
			throw new DataNotFoundException(
					messageSource.getMessage("Clsc84dDaoImpl.not.found.ulIdStage", null, Locale.US));
		}
		log.debug("Exiting method StageLinkIncomeDetailQUERYdam in StageLinkIncomeDetailDaoImpl");
		return liClsc84doDto;
	}
}
