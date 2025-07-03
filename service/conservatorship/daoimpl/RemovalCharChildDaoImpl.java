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
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import us.tx.state.dfps.service.conservatorship.dao.RemovalCharChildDao;
import us.tx.state.dfps.service.conservatorship.dto.RemovalCharChildDto;

/**
 * 
 * ImpactWS - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name: CSUB14S Class
 * Description: This Method extends BaseDao and implements RemovalCharChildDao.
 * This is used to retrieve Removal Char Child details from database. May 1,
 * 2017 - 10:50:39 AM
 */
@Repository
public class RemovalCharChildDaoImpl implements RemovalCharChildDao {

	@Value("${RemovalCharChildDaoImpl.getRemCharChildDtl}")
	private String remCharChildDtl;

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	public RemovalCharChildDaoImpl() {

	}

	/**
	 * Method Description: This Method will be used for full row retreival from
	 * the Removal Char Child using IdEvent to return the records. Service
	 * Name:CSUB14S DAM:CLSS22D
	 * 
	 * @param idEvent
	 * @return List<RemovalCharChildDto> @
	 */
	@SuppressWarnings("unchecked")
	@Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true, propagation = Propagation.REQUIRED)
	public List<RemovalCharChildDto> getRemCharChildDtl(List<Long> idEvents) {

		List<RemovalCharChildDto> rmvCharChildDtlList = new ArrayList<RemovalCharChildDto>();

		rmvCharChildDtlList = (List<RemovalCharChildDto>) sessionFactory.getCurrentSession()
				.createSQLQuery(remCharChildDtl).addScalar("idEvent", StandardBasicTypes.LONG)
				.addScalar("dtLastUpdate", StandardBasicTypes.TIMESTAMP)
				.addScalar("indCharChildCurrent", StandardBasicTypes.STRING)
				.addScalar("cdRemovChildChar", StandardBasicTypes.STRING).addScalar("idPerson", StandardBasicTypes.LONG)
				.addScalar("nmPersonFull", StandardBasicTypes.STRING).setParameterList("idRemEvents", idEvents)
				.setResultTransformer(Transformers.aliasToBean(RemovalCharChildDto.class)).list();

		return rmvCharChildDtlList;
	}

}
