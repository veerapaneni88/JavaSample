package us.tx.state.dfps.service.admin.daoimpl;

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

import us.tx.state.dfps.service.admin.dao.SvcAuthDetailNameDao;
import us.tx.state.dfps.service.admin.dto.SvcAuthDetailNameInDto;
import us.tx.state.dfps.service.admin.dto.SvcAuthDetailNameOutDto;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<DAO Impl
 * for fetching ServiceAuthentication details> Aug 4, 2017- 2:41:10 PM © 2017
 * Texas Department of Family and Protective Services
 */
@Repository
public class SvcAuthDetailNameDaoImpl implements SvcAuthDetailNameDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	private static final Logger log = Logger.getLogger(SvcAuthDetailNameDaoImpl.class);

	@Value("${SvcAuthDetailNameDaoImpl.getServiceAuthentication}")
	private transient String getServiceAuthentication;

	public SvcAuthDetailNameDaoImpl() {
		super();
	}

	/**
	 * 
	 * Method Name: getServiceAuthentication Method Description: This method
	 * will get data from SVC_AUTH_DETAIL,SERVICE_AUTHORIZATION and NAME table.
	 * 
	 * @param pInputDataRec
	 * @return List<SvcAuthDetailNameOutDto> @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<SvcAuthDetailNameOutDto> getServiceAuthentication(SvcAuthDetailNameInDto pInputDataRec) {
		log.debug("Entering method SvcAuthDetailNameQUERYdam in SvcAuthDetailNameDaoImpl");
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getServiceAuthentication)
				.setResultTransformer(Transformers.aliasToBean(SvcAuthDetailNameOutDto.class)));
		sQLQuery1.addScalar("idSvcAuthDtl", StandardBasicTypes.LONG).addScalar("idSvcAuth", StandardBasicTypes.LONG)
				.addScalar("idPerson", StandardBasicTypes.LONG).addScalar("tsLastUpdate", StandardBasicTypes.TIMESTAMP)
				.addScalar("cdSvcAuthDtlAuthType", StandardBasicTypes.STRING)
				.addScalar("cdSvcAuthDtlPeriod", StandardBasicTypes.STRING)
				.addScalar("cdSvcAuthDtlSvc", StandardBasicTypes.STRING)
				.addScalar("cdSvcAuthDtlUnitType", StandardBasicTypes.STRING)
				.addScalar("dtSvcAuthDtl", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtSvcAuthDtlBegin", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtSvcAuthDtlEnd", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtSvcAuthDtlTerm", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtSvcAuthDtlShow", StandardBasicTypes.TIMESTAMP)
				.addScalar("nbrSvcAuthDtlFreq", StandardBasicTypes.SHORT)
				.addScalar("svcAuthDtlLineItm", StandardBasicTypes.SHORT)
				.addScalar("nbrSvcAuthDtlSugUnit", StandardBasicTypes.LONG)
				.addScalar("nbrSvcAuthDtlUnitReq", StandardBasicTypes.FLOAT)
				.addScalar("amtSvcAuthDtlAmtReq", StandardBasicTypes.DOUBLE)
				.addScalar("amtSvcAuthDtlAmtUsed", StandardBasicTypes.DOUBLE)
				.addScalar("nbrSvcAuthDtlUnitRate", StandardBasicTypes.FLOAT)
				.addScalar("nbrSvcAuthDtlUnitUsed", StandardBasicTypes.FLOAT)
				.addScalar("indSvcAuthComplete", StandardBasicTypes.STRING).addScalar("idName", StandardBasicTypes.LONG)
				.addScalar("scrNmNameFirst", StandardBasicTypes.STRING)
				.addScalar("scrNmNameMiddle", StandardBasicTypes.STRING)
				.addScalar("scrNmNameLast", StandardBasicTypes.STRING)
				.addScalar("scrCdNameSuffix", StandardBasicTypes.STRING)
				.setParameter("idSvcAuth", pInputDataRec.getIdSvcAuth());
		List<SvcAuthDetailNameOutDto> liClss24doDto = new ArrayList<>();
		liClss24doDto = (List<SvcAuthDetailNameOutDto>) sQLQuery1.list();
		if (TypeConvUtil.isNullOrEmpty(liClss24doDto)) {
			throw new DataNotFoundException(
					messageSource.getMessage("Clss24dDaoImpl.not.found.serviceAuth", null, Locale.US));
		}
		log.debug("Exiting method SvcAuthDetailNameQUERYdam in SvcAuthDetailNameDaoImpl");
		return liClss24doDto;
	}
}
