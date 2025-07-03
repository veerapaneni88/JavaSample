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

import us.tx.state.dfps.service.admin.dao.NamePrimayEndDateDao;
import us.tx.state.dfps.service.admin.dto.NamePrimayEndDateInDto;
import us.tx.state.dfps.service.admin.dto.NamePrimayEndDateOutDto;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: This
 * Retrieves a full row from the name person table Aug 5, 2017- 12:44:44 PM ©
 * 2017 Texas Department of Family and Protective Services
 */
@Repository
public class NamePrimayEndDateDaoImpl implements NamePrimayEndDateDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${NamePrimayEndDateDaoImpl.getFullName}")
	private String getFullName;

	private static final Logger log = Logger.getLogger(NamePrimayEndDateDaoImpl.class);

	public NamePrimayEndDateDaoImpl() {
		super();
	}

	/**
	 * 
	 * Method Name: getFullName Method Description: This method will get data
	 * from Name table. Ccmn40d
	 * 
	 * @param namePrimayEndDateInDto
	 * @return List<NamePrimayEndDateOutDto>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<NamePrimayEndDateOutDto> getFullName(NamePrimayEndDateInDto namePrimayEndDateInDto) {
		log.debug("Entering method getFullName in NamePrimayEndDateDaoImpl");
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getFullName)
				.addScalar("idPerson", StandardBasicTypes.LONG).addScalar("cdNameSuffix", StandardBasicTypes.STRING)
				.addScalar("dtNameEnd", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtNameStart", StandardBasicTypes.TIMESTAMP).addScalar("idName", StandardBasicTypes.LONG)
				.addScalar("indNameInvalid", StandardBasicTypes.STRING)
				.addScalar("indNamePrimary", StandardBasicTypes.STRING)
				.addScalar("nmNameFirst", StandardBasicTypes.STRING).addScalar("nmNameLast", StandardBasicTypes.STRING)
				.addScalar("nmNameMiddle", StandardBasicTypes.STRING)
				.addScalar("tsLastUpdate", StandardBasicTypes.STRING)
				.setResultTransformer(Transformers.aliasToBean(NamePrimayEndDateOutDto.class)));
		sQLQuery1.setParameter("hI_ulIdPerson", namePrimayEndDateInDto.getIdPerson());
		List<NamePrimayEndDateOutDto> namePrimayEndDateOutDtos = (List<NamePrimayEndDateOutDto>) sQLQuery1.list();
		if (TypeConvUtil.isNullOrEmpty(namePrimayEndDateOutDtos) && namePrimayEndDateOutDtos.size() == 0) {
			throw new DataNotFoundException(messageSource.getMessage("person.name.record.notfound", null, Locale.US));
		}
		log.debug("Exiting method getFullName in NamePrimayEndDateDaoImpl");
		return namePrimayEndDateOutDtos;
	}
}
