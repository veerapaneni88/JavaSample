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

import us.tx.state.dfps.service.admin.dao.CaseMergeFromDao;
import us.tx.state.dfps.service.admin.dto.CaseMergeFromInDto;
import us.tx.state.dfps.service.admin.dto.CaseMergeFromOutDto;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Aug 14, 2017- 1:56:31 PM © 2017 Texas Department of
 * Family and Protective Services
 */
@Repository
public class CaseMergeFromDaoImpl implements CaseMergeFromDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${CaseMergeFromDaoImpl.caseMergeDtls}")
	private transient String caseExistDtls;

	private static final Logger log = Logger.getLogger(CaseMergeFromDaoImpl.class);

	public CaseMergeFromDaoImpl() {
		super();
	}

	/**
	 * 
	 * Method Name: getCaseMergeDtls Method Description: This method will
	 * retrieve case merge details.
	 * 
	 * @param pInputDataRec
	 * @return List<CaseMergeFromOutDto> @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<CaseMergeFromOutDto> getCaseMergeDtls(CaseMergeFromInDto pInputDataRec) {
		log.debug("Entering method CaseMergeFromQUERYdam in CaseMergeFromDaoImpl");
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(caseExistDtls)
				.setResultTransformer(Transformers.aliasToBean(CaseMergeFromOutDto.class)));
		sQLQuery1.addScalar("idCaseMerge", StandardBasicTypes.LONG);
		sQLQuery1.addScalar("idCaseMergeTo", StandardBasicTypes.LONG);
		sQLQuery1.addScalar("idCaseMergeFrom", StandardBasicTypes.LONG);
		sQLQuery1.addScalar("tsLastUpdate", StandardBasicTypes.TIMESTAMP);
		sQLQuery1.addScalar("idCasMergeSitFrom", StandardBasicTypes.LONG);
		sQLQuery1.addScalar("idCaseMergeStageFrom", StandardBasicTypes.LONG);
		sQLQuery1.addScalar("idCaseMergePersMrg", StandardBasicTypes.LONG);
		sQLQuery1.addScalar("idCaseMergePersSplit", StandardBasicTypes.LONG);
		sQLQuery1.addScalar("indCaseMergeInv", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("indCaseMergePending", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("indCaseMergeStageSwap", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("dtCaseMerge", StandardBasicTypes.TIMESTAMP);
		sQLQuery1.addScalar("dtCaseMergeSplit", StandardBasicTypes.TIMESTAMP);
		sQLQuery1.setParameter("hI_ulIdCaseMergeFrom", pInputDataRec.getIdCaseMergeFrom());
		List<CaseMergeFromOutDto> liClsc67doDto = (List<CaseMergeFromOutDto>) sQLQuery1.list();
		if (TypeConvUtil.isNullOrEmpty(liClsc67doDto)) {
			throw new DataNotFoundException(
					messageSource.getMessage("Clsc67dDaoImpl.not.found.ulIdCaseMergeFrom", null, Locale.US));
		}
		log.debug("Exiting method CaseMergeFromQUERYdam in CaseMergeFromDaoImpl");
		return liClsc67doDto;
	}
}
