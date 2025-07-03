package us.tx.state.dfps.service.admin.daoimpl;

import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.service.admin.dao.TletsCheckDao;
import us.tx.state.dfps.service.admin.dto.TletsCheckInDto;
import us.tx.state.dfps.service.admin.dto.TletsCheckOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This dao is
 * designed to verify that a person being deleted is not a Tlets person for a
 * given stage id. Aug 10, 2017- 12:31:54 PM © 2017 Texas Department of Family
 * and Protective Services
 */
@Repository
public class TletsCheckDaoImpl implements TletsCheckDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${TletsCheckDaoImpl.verifyTLETSPerson}")
	private String verifyTLETSPerson;

	private static final Logger log = Logger.getLogger(TletsCheckDaoImpl.class);

	public TletsCheckDaoImpl() {
		super();
	}

	/**
	 * 
	 * Method Name: verifyTLETSPerson Method Description: This dao is designed
	 * to check for existing person from the Tlets table.
	 * 
	 * @param pInputDataRec
	 * @return List<Clsch6doDto> @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<TletsCheckOutDto> verifyTLETSPerson(TletsCheckInDto pInputDataRec) {
		log.debug("Entering method TletsCheckQUERYdam in TletsCheckDaoImpl");
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(verifyTLETSPerson)
				.addScalar("idPerson", StandardBasicTypes.LONG)
				.setParameter("hI_ulIdPerson", pInputDataRec.getIdPerson())
				.setResultTransformer(Transformers.aliasToBean(TletsCheckOutDto.class)));
		List<TletsCheckOutDto> liClsch6doDto = (List<TletsCheckOutDto>) sQLQuery1.list();
		log.debug("Exiting method TletsCheckQUERYdam in TletsCheckDaoImpl");
		return liClsch6doDto;
	}
}
