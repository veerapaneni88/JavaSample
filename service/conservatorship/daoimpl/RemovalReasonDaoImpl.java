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

import us.tx.state.dfps.service.conservatorship.dao.RemovalReasonDao;
import us.tx.state.dfps.service.conservatorship.dto.RemovalReasonDto;

/**
 * 
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name: CSUB14S Class
 * Description: This Method extends BaseDao and implements RemovalReasonDao.
 * This is used to retrieve RemovalReason details from database. May 1, 2017 -
 * 10:26:39 AM
 */
@Repository
public class RemovalReasonDaoImpl implements RemovalReasonDao {

	@Value("${RemovalReasonDaoImpl.getRemReasonDtl}")
	private String remReasonDtl;

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	public RemovalReasonDaoImpl() {

	}

	/**
	 * Method Description: This Method will be used to Retrieves a full row from
	 * the Removal Reason table. Service Name:CSUB14S DAM:CLSS21D
	 * 
	 * @param idEvent
	 * @return List<RemovalReasonDto> @
	 */

	@SuppressWarnings("unchecked")
	public List<RemovalReasonDto> getRemReasonDtl(List<Long> idEventList) {
		List<RemovalReasonDto> remReasonList = new ArrayList<RemovalReasonDto>();

		remReasonList = (List<RemovalReasonDto>) sessionFactory.getCurrentSession().createSQLQuery(remReasonDtl)
				.addScalar("idRemovalEvent", StandardBasicTypes.LONG)
				.addScalar("dtLastUpdate", StandardBasicTypes.TIMESTAMP).addScalar("cdRemovalReason")
				.setParameterList("idRemEvent", idEventList)
				.setResultTransformer(Transformers.aliasToBean(RemovalReasonDto.class)).list();

		return remReasonList;
	}

}
