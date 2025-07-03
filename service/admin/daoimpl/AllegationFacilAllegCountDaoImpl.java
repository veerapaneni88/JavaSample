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

import us.tx.state.dfps.service.admin.dao.AllegationFacilAllegCountDao;
import us.tx.state.dfps.service.admin.dto.AllegationFacilAllegCountInDto;
import us.tx.state.dfps.service.admin.dto.AllegationFacilAllegCountOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This
 * Retrieves all allegations where the cinv04si id_person is equal to id_victim
 * in the facility allegation. Aug 5, 2017- 4:16:44 PM © 2017 Texas Department
 * of Family and Protective Services
 */
@Repository
public class AllegationFacilAllegCountDaoImpl implements AllegationFacilAllegCountDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${AllegationFacilAlegCountDaoImpl.getAllAllegationsPID}")
	private String getAllAllegationsPID;

	private static final Logger log = Logger.getLogger(AllegationFacilAllegCountDaoImpl.class);

	public AllegationFacilAllegCountDaoImpl() {
		super();
	}

	/**
	 * 
	 * Method Name: getAllAllegationsPID Method Description: This method will
	 * get the count of ID_ALLEGATION meeting the criteria. Cinv87d
	 * 
	 * @param allegationFacilAllegCountInDto
	 * @return List<AllegationFacilAllegCountOutDto>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<AllegationFacilAllegCountOutDto> getAllAllegationsPID(
			AllegationFacilAllegCountInDto allegationFacilAllegCountInDto) {
		log.debug("Entering method getAllAllegationsPID in AllegationFacilAllegCountDaoImpl");
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getAllAllegationsPID)
				.addScalar("fatalFacilAllegCount", StandardBasicTypes.LONG)
				.setResultTransformer(Transformers.aliasToBean(AllegationFacilAllegCountOutDto.class)));
		sQLQuery1.setParameter("hI_ulIdPerson", allegationFacilAllegCountInDto.getIdPerson());
		List<AllegationFacilAllegCountOutDto> allegationFacilAllegCountOutDtos = (List<AllegationFacilAllegCountOutDto>) sQLQuery1
				.list();
		log.debug("Exiting method getAllAllegationsPID in AllegationFacilAllegCountDaoImpl");
		return allegationFacilAllegCountOutDtos;
	}
}
