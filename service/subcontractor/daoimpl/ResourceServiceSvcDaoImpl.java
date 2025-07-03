package us.tx.state.dfps.service.subcontractor.daoimpl;

import java.util.List;

import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.kin.dto.ResourceServiceDto;
import us.tx.state.dfps.service.subcontractor.dao.ResourceServiceSvcDao;
import us.tx.state.dfps.service.subcontractor.dto.ResourceServiceSvcInDto;
import us.tx.state.dfps.service.subcontractor.dto.ResourceServiceSvcOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION
 * 
 * Class Description:Class to perform CRUD operations on resource
 * 
 * Feb 9, 2018- 11:32:01 AM © 2017 Texas Department of Family and Protective
 * Services
 */
@Repository
public class ResourceServiceSvcDaoImpl implements ResourceServiceSvcDao {

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${ResourceServiceSvcDaoImpl.getResourceServiceDetails}")
	private String getResourceServiceDetails;

	@Value("${ResourceServiceSvcDaoImpl.getResourceServiceById}")
	private String getResourceServiceById;

	/**
	 * 
	 * Method Name: getResourceServiceDetails Method Description: This method
	 * will get data from RESOURCE_SERVICE table.
	 * 
	 * @param pInputDataRec
	 * @return List<ResourceServiceSvcOutDto> @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<ResourceServiceSvcOutDto> getResourceServiceDetails(ResourceServiceSvcInDto pInputDataRec) {
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getResourceServiceDetails)
				.addScalar("cdRsrcSvcService", StandardBasicTypes.STRING)
				.setParameter("idResource", pInputDataRec.getIdResource())
				.setResultTransformer(Transformers.aliasToBean(ResourceServiceSvcOutDto.class)));
		return (List<ResourceServiceSvcOutDto>) sQLQuery1.list();
	}

	/**
	 * 
	 * Method Name: getResourceServiceById
	 * 
	 * Method Description:This returns all rows from the RESOURCE_SERVICE table
	 * for a specified resource ID. DAM Name : CRES10D
	 * 
	 * @param pInputDataRec
	 * @return @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<ResourceServiceDto> getResourceServiceById(ResourceServiceSvcInDto pInputDataRec) {
		SQLQuery query = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getResourceServiceById)
				.addScalar("idResourceService", StandardBasicTypes.LONG)
				.addScalar("dtLastUpdate", StandardBasicTypes.DATE)
				.addScalar("indRsrcSvcShowRow", StandardBasicTypes.STRING)
				.addScalar("indRsrcSvcIncomeBsed", StandardBasicTypes.STRING)
				.addScalar("cdRsrcSvcCategRsrc", StandardBasicTypes.STRING)
				.addScalar("cdRsrcSvcCnty", StandardBasicTypes.STRING)
				.addScalar("cdRsrcSvcProgram", StandardBasicTypes.STRING)
				.addScalar("cdRsrcSvcRegion", StandardBasicTypes.STRING)
				.addScalar("cdRsrcSvcService", StandardBasicTypes.STRING)
				.addScalar("cdRsrcSvcState", StandardBasicTypes.STRING)
				.addScalar("indRsrcSvcCntyPartial", StandardBasicTypes.STRING)
				.addScalar("indKinshipTraining", StandardBasicTypes.STRING)
				.addScalar("indKinshipHomeAssmnt", StandardBasicTypes.STRING)
				.addScalar("indKinshipIncome", StandardBasicTypes.STRING)
				.addScalar("indKinshipAgreement", StandardBasicTypes.STRING)
				.setParameter("idResource", pInputDataRec.getIdResource())
				.setParameter("indRsrcSvcShowRow", ServiceConstants.STRING_IND_Y)
				.setResultTransformer(Transformers.aliasToBean(ResourceServiceDto.class)));
		return query.list();
	}
}
