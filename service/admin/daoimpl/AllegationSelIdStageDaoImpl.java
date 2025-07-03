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

import us.tx.state.dfps.service.admin.dao.AllegationSelIdStageDao;
import us.tx.state.dfps.service.admin.dto.AllegationInDto;
import us.tx.state.dfps.service.admin.dto.AllegationOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: This
 * determines whether a given person is in any allegations for a given stage.
 * Aug 6, 2017- 8:47:54 AM © 2017 Texas Department of Family and Protective
 * Services
 */
@Repository
public class AllegationSelIdStageDaoImpl implements AllegationSelIdStageDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${AllegationSelIdStageDaoImpl.havAllegationsSID}")
	private String havAllegationsSID;

	@Value("${AllegationSelIdStageDaoImpl.intakeAllegationsSID}")
	private String intakeAllegationsSID;

	private static final Logger log = Logger.getLogger(AllegationSelIdStageDaoImpl.class);

	public AllegationSelIdStageDaoImpl() {
		super();
	}

	/**
	 * 
	 * Method Name: havAllegationsSID Method Description: This method will get
	 * ID_ALLEGATION_STAGE from ALLEGATION table. Cinvb5d
	 * 
	 * @param allegationInDto
	 * @return List<AllegationOutDto>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<AllegationOutDto> havAllegationsSID(AllegationInDto allegationInDto) {
		log.debug("Entering method havAllegationsSID in AlegationDaoImpl");
		if (allegationInDto.getIdStage() == null)
			allegationInDto.setIdStage(0L);
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(havAllegationsSID)
				.addScalar("idStage", StandardBasicTypes.LONG)
				.setParameter("hI_ulIdStage", allegationInDto.getIdStage())
				.setParameter("hI_ulIdPerson", allegationInDto.getIdPerson())
				.setResultTransformer(Transformers.aliasToBean(AllegationOutDto.class)));
		List<AllegationOutDto> allegationOutDtos = (List<AllegationOutDto>) sQLQuery1.list();
		log.debug("Exiting method havAllegationsSID in AlegationDaoImpl");
		return allegationOutDtos;
	}

	/**
	 * [artf251998] Defect: 156746 - Stop deletion of PRN in AR
	 *
	 * @param allegationInDto
	 * @return
	 */
	public List<Long> intakeAllegationsSID(AllegationInDto allegationInDto) {
		log.debug("Entering method intakeAllegationsSID in AllegationDaoImpl");
		if (allegationInDto.getIdStage() == null)
			allegationInDto.setIdStage(0L);
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(intakeAllegationsSID)
				.setParameter("hI_ulIdStage", allegationInDto.getIdStage())
				.setParameter("hI_ulIdPerson", allegationInDto.getIdPerson()));
		List<Long> intakeAllegationsSID = (List<Long>) sQLQuery1.list();
		log.debug("Exiting method intakeAllegationsSID in AllegationDaoImpl");
		return intakeAllegationsSID;
	}
}
