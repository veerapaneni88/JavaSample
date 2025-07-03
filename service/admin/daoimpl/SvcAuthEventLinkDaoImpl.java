package us.tx.state.dfps.service.admin.daoimpl;

import java.math.BigDecimal;
import java.util.ArrayList;
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

import us.tx.state.dfps.service.admin.dao.SvcAuthEventLinkDao;
import us.tx.state.dfps.service.admin.dto.SvcAuthEventLinkInDto;
import us.tx.state.dfps.service.admin.dto.SvcAuthEventLinkOutDto;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<DAO Impl
 * for fetching auth event link> Aug 4, 2017- 2:35:40 PM © 2017 Texas Department
 * of Family and Protective Services
 */
@Repository
public class SvcAuthEventLinkDaoImpl implements SvcAuthEventLinkDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${SvcAuthEventLinkDaoImpl.getAuthEventLink}")
	private transient String getAuthEventLink;

	@Value("${SvcAuthEventLinkDaoImpl.checkSvcAuthExists}")
	private transient String checkSvcAuthExistsSql;
	
	@Value("${SvcAuthEventLinkDaoImpl.getAuthEventLinkByCase}")
	private transient String getAuthEventLinkByCase;
	
	private static final Logger log = Logger.getLogger(SvcAuthEventLinkDaoImpl.class);

	public SvcAuthEventLinkDaoImpl() {
		super();
	}

	/**
	 * 
	 * Method Name: getAuthEventLink Method Description: This method will get
	 * data from SVC_AUTH_EVENT_LINK table.
	 * 
	 * @param pInputDataRec
	 * @return List<SvcAuthEventLinkOutDto> @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<SvcAuthEventLinkOutDto> getAuthEventLink(SvcAuthEventLinkInDto pInputDataRec) {
		log.debug("Entering method SvcAuthEventLinkQUERYdam in SvcAuthEventLinkDaoImpl");
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getAuthEventLink)
				.setResultTransformer(Transformers.aliasToBean(SvcAuthEventLinkOutDto.class)));
		sQLQuery1.addScalar("idSvcAuthEvent", StandardBasicTypes.LONG).addScalar("idSvcAuth", StandardBasicTypes.LONG)
				.addScalar("tsLastUpdate", StandardBasicTypes.TIMESTAMP)
				.setParameter("hI_ulIdSvcAuthEvent", pInputDataRec.getIdSvcAuthEvent());
		List<SvcAuthEventLinkOutDto> liCses24doDto = new ArrayList<>();
		liCses24doDto = (List<SvcAuthEventLinkOutDto>) sQLQuery1.list();
		if (TypeConvUtil.isNullOrEmpty(liCses24doDto)) {
			throw new DataNotFoundException(
					messageSource.getMessage("Cses24dDaoImpl.not.found.authLink", null, Locale.US));
		}
		log.debug("Exiting method SvcAuthEventLinkQUERYdam in SvcAuthEventLinkDaoImpl");
		return liCses24doDto;
	}
	
	/**
	 * 
	 * Method Name: getAuthEventLink Method Description: This method will get
	 * data from SVC_AUTH_EVENT_LINK table based on case id.
	 * added for defect 2337 artf55938
	 * @param pInputDataRec
	 * @return List<SvcAuthEventLinkOutDto> @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<SvcAuthEventLinkOutDto> getAuthEventLinkByCase(SvcAuthEventLinkInDto pInputDataRec) {
		log.debug("Entering method SvcAuthEventLinkQUERYdam in SvcAuthEventLinkDaoImpl");
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getAuthEventLinkByCase)
				.setResultTransformer(Transformers.aliasToBean(SvcAuthEventLinkOutDto.class)));
		sQLQuery1.addScalar("idSvcAuthEvent", StandardBasicTypes.LONG).addScalar("idSvcAuth", StandardBasicTypes.LONG)
				.addScalar("tsLastUpdate", StandardBasicTypes.TIMESTAMP)
				.setParameter("hI_ulIdCase", pInputDataRec.getCaseId());
		List<SvcAuthEventLinkOutDto> liCses24doDto = new ArrayList<>();
		liCses24doDto = (List<SvcAuthEventLinkOutDto>) sQLQuery1.list();
		if (TypeConvUtil.isNullOrEmpty(liCses24doDto)) {
			throw new DataNotFoundException(
					messageSource.getMessage("not.found.authLink", null, Locale.US));
		}
		log.debug("Exiting method getAuthEventLinkByCase in SvcAuthEventLinkDaoImpl");
		return liCses24doDto;
	}
	
	@Override
	public int checkSvcAuthExists(Long idSvcAuth) {
		SQLQuery sqlQuery = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(checkSvcAuthExistsSql));
		sqlQuery.addScalar("idSvcAuth", StandardBasicTypes.INTEGER);
		return (Integer) sqlQuery.uniqueResult();
	}
}