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

import us.tx.state.dfps.service.admin.dao.AllegationStageCaseDao;
import us.tx.state.dfps.service.admin.dto.AllegationStageCaseInDto;
import us.tx.state.dfps.service.admin.dto.AllegationStageCaseOutDto;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:
 * Cinvg1dDaoImpl Aug 6, 2017- 6:56:14 PM © 2017 Texas Department of Family and
 * Protective Services
 */
@Repository
public class AllegationStageCaseDaoImpl implements AllegationStageCaseDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${AllegationStageCaseDaoImpl.caseExistDtls}")
	private transient String caseExistDtls;

	private static final Logger log = Logger.getLogger(AllegationStageCaseDaoImpl.class);

	public AllegationStageCaseDaoImpl() {
		super();
	}

	/**
	 * 
	 * Method Name: caseExistDtls Method Description: Retrieves data from
	 * Allegation and Stage tables.
	 * 
	 * @param pInputDataRec
	 * @return List<AllegationStageCaseOutDto> @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<AllegationStageCaseOutDto> caseExistDtls(AllegationStageCaseInDto pInputDataRec) {
		log.debug("Entering method AlegationStageCaseQUERYdam in AllegationStageCaseDaoImpl");
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(caseExistDtls)
				.setResultTransformer(Transformers.aliasToBean(AllegationStageCaseOutDto.class)));
		sQLQuery1.addScalar("indChildSexLaborTrafficExists", StandardBasicTypes.STRING);
		sQLQuery1.setParameter("hI_ulIdStage", pInputDataRec.getIdStage());
		List<AllegationStageCaseOutDto> liCinvg1doDto = (List<AllegationStageCaseOutDto>) sQLQuery1.list();
		if (TypeConvUtil.isNullOrEmpty(liCinvg1doDto)) {
			throw new DataNotFoundException(
					messageSource.getMessage("Cinvg1dDaoImpl.not.found.ulIdStage", null, Locale.US));
		}
		log.debug("Exiting method AlegationStageCaseQUERYdam in AllegationStageCaseDaoImpl");
		return liCinvg1doDto;
	}
}
