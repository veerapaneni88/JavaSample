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

import us.tx.state.dfps.service.admin.dao.UnitEmpLinkPersonUnitSelDao;
import us.tx.state.dfps.service.admin.dto.UnitEmpLinkPersonUnitSelInDto;
import us.tx.state.dfps.service.admin.dto.UnitEmpLinkPersonUnitSelOutDto;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:Retrieve
 * Person name on Person table. Aug 10, 2017- 3:14:21 PM © 2017 Texas Department
 * of Family and Protective Services
 */
@Repository
public class UnitEmpLinkPersonUnitSelDaoImpl implements UnitEmpLinkPersonUnitSelDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${UnitEmpLinkPersonUnitSelDaoImpl.getPersonName}")
	private transient String getPersonName;

	private static final Logger log = Logger.getLogger(UnitEmpLinkPersonUnitSelDaoImpl.class);

	public UnitEmpLinkPersonUnitSelDaoImpl() {
		super();
	}

	/**
	 *
	 * @param pInputDataRec
	 * @return List<Ccmn60doDto> @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<UnitEmpLinkPersonUnitSelOutDto> getPersonName(UnitEmpLinkPersonUnitSelInDto pInputDataRec) {
		log.debug("Entering method UnitEmpLinkPersonUnitSelQUERYdam in UnitEmpLinkPersonUnitSelDaoImpl");
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getPersonName)
				.addScalar("idPerson", StandardBasicTypes.LONG).addScalar("nmPersonFull")
				.setParameter("hI_ulIdPerson", pInputDataRec.getIdPerson())
				.setResultTransformer(Transformers.aliasToBean(UnitEmpLinkPersonUnitSelOutDto.class)));
		List<UnitEmpLinkPersonUnitSelOutDto> liCcmn60doDto = (List<UnitEmpLinkPersonUnitSelOutDto>) sQLQuery1.list();
		if (TypeConvUtil.isNullOrEmpty(liCcmn60doDto) || liCcmn60doDto.size() == 0) {
			throw new DataNotFoundException(
					messageSource.getMessage("Ccmn60dDaoImpl.person.not.found", null, Locale.US));
		}
		log.debug("Exiting method UnitEmpLinkPersonUnitSelQUERYdam in UnitEmpLinkPersonUnitSelDaoImpl");
		return liCcmn60doDto;
	}
}
