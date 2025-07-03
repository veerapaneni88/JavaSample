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

import us.tx.state.dfps.service.admin.dao.CaseMergeToDao;
import us.tx.state.dfps.service.admin.dto.CaseMergeToInDto;
import us.tx.state.dfps.service.admin.dto.CaseMergeToOutDto;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Aug 14, 2017- 1:54:01 PM © 2017 Texas Department of
 * Family and Protective Services
 */
@Repository
public class CaseMergeToDaoImpl implements CaseMergeToDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	private static final Logger log = Logger.getLogger(CaseMergeToDaoImpl.class);

	@Value("${CaseMergeToDaoImpl.caseMergeDtls}")
	private transient String caseMergeDtls;

	public CaseMergeToDaoImpl() {
		super();
	}

	/**
	 * 
	 * Method Name: getCaseMergeDtls Method Description: This method will
	 * retrieve case merge details.
	 * 
	 * @param pInputDataRec
	 * @return List<CaseMergeToOutDto> @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<CaseMergeToOutDto> getCaseMergeDtls(CaseMergeToInDto pInputDataRec) {
		log.debug("Entering method CaseMergeToQUERYdam in CaseMergeToDaoImpl");
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(caseMergeDtls)
				.setResultTransformer(Transformers.aliasToBean(CaseMergeToOutDto.class)));
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
		sQLQuery1.setParameter("hI_ulIdCaseMergeTo", pInputDataRec.getIdCaseMergeTo());
		List<CaseMergeToOutDto> liClsc68doDto = (List<CaseMergeToOutDto>) sQLQuery1.list();
		if (TypeConvUtil.isNullOrEmpty(liClsc68doDto)) {
			throw new DataNotFoundException(
					messageSource.getMessage("Clsc68dDaoImpl.not.found.ulIdCaseMergeTo", null, Locale.US));
		}
		log.debug("Exiting method CaseMergeToQUERYdam in CaseMergeToDaoImpl");
		return liClsc68doDto;
	}
}
