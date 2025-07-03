package us.tx.state.dfps.service.admin.daoimpl;

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

import us.tx.state.dfps.service.admin.dao.ContactSearchDtContactOccDao;
import us.tx.state.dfps.service.admin.dto.ContactInDto;
import us.tx.state.dfps.service.admin.dto.ContactOutDto;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION
 *
 * Class Description:DAO Impl for fetching date occured details Aug 6, 2017-
 * 3:47:50 PM © 2017 Texas Department of Family and Protective Services
 */
@Repository
public class ContactSearchDtContactOccDaoImpl implements ContactSearchDtContactOccDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${ContactSearchDtContactOccDaoImpl.getDtContactOcc}")
	private transient String getDtContactOcc;

	private static final Logger log = Logger.getLogger(ContactSearchDtContactOccDaoImpl.class);

	public ContactSearchDtContactOccDaoImpl() {
		super();
	}

	/**
	 * 
	 * Method Name: getDateOccured Method Description: This method will gate the
	 * Date Occurred from Contact table.
	 * 
	 * @param pInputDataRec
	 * @return List<ContactOutDto> @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<ContactOutDto> getDateOccured(ContactInDto pInputDataRec) {
		log.debug("Entering method ContactQUERYdam in ContactSearchDtContactOccDaoImpl");
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getDtContactOcc)
				.setResultTransformer(Transformers.aliasToBean(ContactOutDto.class)));
		sQLQuery1.addScalar("dtDTContactOccurred", StandardBasicTypes.TIMESTAMP);
		sQLQuery1.setParameter("hI_ulIdStage", pInputDataRec.getIdStage());
		List<ContactOutDto> liCsys22doDto = (List<ContactOutDto>) sQLQuery1.list();
		if (TypeConvUtil.isNullOrEmpty(liCsys22doDto)) {
			throw new DataNotFoundException(
					messageSource.getMessage("Csys22dDaoImpl.not.found.ulIdStage", null, Locale.US));
		}
		log.debug("Exiting method ContactQUERYdam in ContactSearchDtContactOccDaoImpl");
		return liCsys22doDto;
	}
}
