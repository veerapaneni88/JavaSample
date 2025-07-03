package us.tx.state.dfps.service.workload.daoimpl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.service.workload.dao.CityDao;
import us.tx.state.dfps.service.workload.dto.CityDto;

/**
 * 
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name: Class
 * Description: Operations on table CITY will be here Apr 12, 2017 - 7:35:47 PM
 */
@Repository
public class CityDaoImpl implements CityDao {
	@Value("${RetrieveCountyDaoImpl.searchCountyByCitySql}")
	private String searchCountyByCitySql;

	@Autowired
	private SessionFactory sessionFactory;

	public CityDaoImpl() {

	}

	/**
	 * 
	 * Method Description: This Method will retrieve county code based on given
	 * inputs Dam Name: CCMNE2D
	 * 
	 * @param szAddrCity
	 * @return List<CityDto> @
	 */

	@SuppressWarnings("unchecked")
	public List<CityDto> getCountyList(String szAddrCity) {
		List<CityDto> countyOutput = new ArrayList<CityDto>();

		Query queryCity = sessionFactory.getCurrentSession().createSQLQuery(searchCountyByCitySql)
				.addScalar("cdCounty", StandardBasicTypes.STRING)
				.setResultTransformer(Transformers.aliasToBean(CityDto.class));
		queryCity.setString("addrCity", szAddrCity);
		countyOutput = queryCity.list();

		return countyOutput;

	}

}
