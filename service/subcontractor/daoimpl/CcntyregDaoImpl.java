package us.tx.state.dfps.service.subcontractor.daoimpl;

import java.util.List;

import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.service.subcontractor.dao.CcntyregDao;
import us.tx.state.dfps.service.subcontractor.dto.CcntyregDto;
import us.tx.state.dfps.service.subcontractor.dto.CcntyregiDto;

/**
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Jan 20, 2018- 2:26:24 PM © 2017 Texas Department of
 * Family and Protective Services
 */
@Repository
public class CcntyregDaoImpl implements CcntyregDao {

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${CcntyregDaoImpl.getRegionCounty}")
	private String getRegionCounty;

	@Value("${CcntyregDaoImpl.getRegionFromCounty}")
	private String getRegionFromCounty;

	/**
	 * Method Name: getRegionCnty Method Description: Retrieves REGION_COUNTY
	 * based on county.
	 * 
	 * DAM: CSES82D Service Name: CCMN35S
	 * 
	 * @param pInputDataRec
	 * @return List<String>
	 */

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getRegionCnty(CcntyregiDto pInputDataRec) {
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getRegionCounty)
				.addScalar("scrRsrcSvcCntyCode", StandardBasicTypes.STRING)
				.setParameter("decode", pInputDataRec.getCdRsrcSvcRegion()));
		return sQLQuery1.list();
	}

	/**
	 * Method Name: getRegionFromCounty
	 * Method Description: Retrieves region from CCNTYREG table based on the county.
	 *
	 * @param ccntyRegDto
	 * @return String
	 */
	@Override
	public String getRegionFromCounty(CcntyregDto ccntyRegDto) {
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getRegionFromCounty)
				.addScalar("scrRsrcSvcRegionCode", StandardBasicTypes.STRING)
				.setParameter("code", ccntyRegDto.getCode()));
		return (String)sQLQuery1.uniqueResult();
	}

}
