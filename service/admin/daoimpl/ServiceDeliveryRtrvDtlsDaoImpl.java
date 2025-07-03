package us.tx.state.dfps.service.admin.daoimpl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.util.ObjectUtils;
import us.tx.state.dfps.service.admin.dao.ServiceDeliveryRtrvDtlsDao;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.lookup.dao.LookupDao;
import us.tx.state.dfps.service.servicedelivery.dto.ServiceDeliveryRtrvDtlsInDto;
import us.tx.state.dfps.service.servicedelivery.dto.ServiceDeliveryRtrvDtlsOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:ServiceDeliveryRtrvDtlsDaoImpl Jul 13, 2018- 2:43:26 PM © 2017
 * Texas Department of Family and Protective Services
 */
@Repository
public class ServiceDeliveryRtrvDtlsDaoImpl implements ServiceDeliveryRtrvDtlsDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	LookupDao lookupDao;

	@Value("${ServiceDeliveryRtrvDtlsDaoImpl.getDecodeTableName}")
	private String getDecodeTableNameBySql;

	@Value("${ServiceDeliveryRtrvDtlsDaoImpl.getNarrativeExists}")
	private String getNarrativeExistsSql;

	@Value("${ServiceDeliveryRtrvDtlsDaoImpl.getNarrativeExistsWhereClause}")
	private String getNarrativeExistsWhereClauseSql;

	@Value("${ServiceDeliveryRtrvDtlsDaoImpl.getSrvNarrByRsrc}")
	private String getSrvNarrByRsrc;

	@Value("${ServiceDeliveryRtrvDtlsDaoImpl.getSrvNarrByPrsn}")
	private String getSrvNarrByPrsn;

	/**
	 * Method Name: getNarrExists Method Description:Retrieves Narrative blob
	 * and dtLastUpdate from database when Narrative is present. Csys06s
	 * 
	 * @param serviceDeliveryRtrvDtlsInDto
	 * @return List<ServiceDeliveryRtrvDtlsOutDto>
	 * 
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<ServiceDeliveryRtrvDtlsOutDto> getNarrExists(
			ServiceDeliveryRtrvDtlsInDto serviceDeliveryRtrvDtlsInDto) {
		List<ServiceDeliveryRtrvDtlsOutDto> list = new ArrayList<ServiceDeliveryRtrvDtlsOutDto>();

		if (!ObjectUtils.isEmpty(serviceDeliveryRtrvDtlsInDto) &&
				!ObjectUtils.isEmpty(serviceDeliveryRtrvDtlsInDto.getSysTxtTablename())) {
			StringBuffer sql = new StringBuffer(getNarrativeExistsSql);
			sql.append(" " + serviceDeliveryRtrvDtlsInDto.getSysTxtTablename() + " ");
			sql.append(getNarrativeExistsWhereClauseSql);
			Query query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString())
					.addScalar("blob", StandardBasicTypes.BLOB).addScalar("dtLastUpdate", StandardBasicTypes.TIMESTAMP)
					.addScalar("idDocumentTemplate", StandardBasicTypes.LONG)
					.setParameter("idEvent", serviceDeliveryRtrvDtlsInDto.getIdEvent())
					.setResultTransformer(Transformers.aliasToBean(ServiceDeliveryRtrvDtlsOutDto.class));
			list.addAll(query.list());
		}
		return list;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see us.tx.state.dfps.service.admin.dao.ServiceDeliveryRtrvDtlsDao#
	 * csys09dQUERYdam (us.tx.state.dfps.service.servicedelivery.dto.
	 * ServiceDeliveryRtrvDtlsInDto)
	 */
	@Override
	public List<ServiceDeliveryRtrvDtlsOutDto> getServiceDeliveryDtls(ServiceDeliveryRtrvDtlsInDto pInputDataRec) {
		String selectSql = ServiceConstants.EMPTY_STRING;
		if (pInputDataRec.getIdResource() > 0) {
			selectSql = getSrvNarrByRsrc.replaceFirst("%table", pInputDataRec.getSysTxtTablename())
					.replaceFirst("%rsrc", pInputDataRec.getIdResource().toString());
		} else if (pInputDataRec.getIdPerson() > 0) {
			selectSql = getSrvNarrByPrsn.replaceFirst("%table", pInputDataRec.getSysTxtTablename())
					.replaceFirst("%prsn", pInputDataRec.getIdPerson().toString());
		}

		StringBuilder hostszDynamicQry = new StringBuilder(selectSql);
		Query query = sessionFactory.getCurrentSession().createSQLQuery(hostszDynamicQry.toString())
				.addScalar("blob", StandardBasicTypes.BLOB).addScalar("dtLastUpdate", StandardBasicTypes.DATE)
				.addScalar("idEvent", StandardBasicTypes.LONG)
				.setResultTransformer(Transformers.aliasToBean(ServiceDeliveryRtrvDtlsOutDto.class));

		return (List<ServiceDeliveryRtrvDtlsOutDto>) query.list();
	}

	/**
	 * Method Description: This method will retrieve the id tablename of
	 * narrative of the event
	 * 
	 * @param eventType
	 * @param codeTableName
	 * @return String @
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public String getDecodeTableName(String eventType, String codeTableName) {
		String decodeTableName = "";
		Query queryEmployee = (sessionFactory.getCurrentSession().createSQLQuery(getDecodeTableNameBySql));
		queryEmployee.setParameter("codeTableName", codeTableName);
		queryEmployee.setParameter("eventType", eventType);
		decodeTableName = (String) queryEmployee.uniqueResult();
		return decodeTableName;
	}
}
