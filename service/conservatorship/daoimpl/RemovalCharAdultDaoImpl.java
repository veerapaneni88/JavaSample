package us.tx.state.dfps.service.conservatorship.daoimpl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.service.conservatorship.dao.RemovalCharAdultDao;
import us.tx.state.dfps.service.conservatorship.dto.RemovalCharAdultDto;

/**
 * 
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name: CSUB14S Class
 * Description: This Method extends BaseDao and implements RemovalCharAdultDao.
 * This is used to retrieve Removal Char Adult details from database. May 1,
 * 2017 - 11:25:39 AM
 */
@Repository
public class RemovalCharAdultDaoImpl implements RemovalCharAdultDao {

	@Value("${RemovalCharAdultDaoImpl.getRemCharAdultDtl}")
	private String RemCharAdultDtl;

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	public RemovalCharAdultDaoImpl() {

	}

	/**
	 * Method Description: This Method will be used for full row retreival from
	 * the Removal Char Adult using IdEvent to return the records. Service
	 * Name:CSUB14S DAM:CLSS23D
	 * 
	 * @param idEvent
	 * @return List<RemovalCharAdultDto> @
	 */

	@SuppressWarnings("unchecked")
	public List<RemovalCharAdultDto> getRemCharAdultDtl(List<Long> idEventList) {

		List<RemovalCharAdultDto> rmvCharAdultDtlList = new ArrayList<RemovalCharAdultDto>();

		rmvCharAdultDtlList = (List<RemovalCharAdultDto>) sessionFactory.getCurrentSession()
				.createSQLQuery(RemCharAdultDtl).addScalar("idEvent", StandardBasicTypes.LONG)
				.addScalar("dtLastUpdate", StandardBasicTypes.TIMESTAMP).addScalar("cdRemovAdultChar")
				.setParameterList("idRemEvent", idEventList)
				.setResultTransformer(Transformers.aliasToBean(RemovalCharAdultDto.class)).list();

		return rmvCharAdultDtlList;
	}
}
