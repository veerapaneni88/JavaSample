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

import us.tx.state.dfps.service.admin.dao.StagePersonLinkNmStageDao;
import us.tx.state.dfps.service.admin.dto.StagePersonLinkNmStageInDto;
import us.tx.state.dfps.service.admin.dto.StagePersonLinkNmStageOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This dao is
 * designed to verify that a person being deleted is not a designated case name
 * for a given stage id. Aug 10, 2017- 11:00:30 AM © 2017 Texas Department of
 * Family and Protective Services
 */
@Repository
public class StagePersonLinkNmStageDaoImpl implements StagePersonLinkNmStageDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${StagePersonLinkNmStageDaoImpl.verifyPersonDeleted}")
	private String verifyPersonDeleted;

	private static final Logger log = Logger.getLogger(StagePersonLinkNmStageDaoImpl.class);

	public StagePersonLinkNmStageDaoImpl() {
		super();
	}

	/**
	 * 
	 * Method Name: verifyPersonDeleted Method Description: Verify the person
	 * has case name Clscf9d
	 * 
	 * @param stagePersonLinkNmStageInDto
	 * @return List<Clscf9doDto> @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<StagePersonLinkNmStageOutDto> verifyPersonDeleted(
			StagePersonLinkNmStageInDto stagePersonLinkNmStageInDto) {
		log.debug("Entering method verifyPersonDeleted in StagePersonLinkNmStageDaoImpl");
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(verifyPersonDeleted)
				.addScalar("indindNameStage", StandardBasicTypes.STRING)
				.setParameter("hI_ulIdStage", stagePersonLinkNmStageInDto.getIdStage())
				.setParameter("hI_ulIdPerson", stagePersonLinkNmStageInDto.getIdPerson())
				.setResultTransformer(Transformers.aliasToBean(StagePersonLinkNmStageOutDto.class)));
		List<StagePersonLinkNmStageOutDto> stagePersonLinkNmStageOutDtos = (List<StagePersonLinkNmStageOutDto>) sQLQuery1
				.list();
		log.debug("Exiting method verifyPersonDeleted in StagePersonLinkNmStageDaoImpl");
		return stagePersonLinkNmStageOutDtos;
	}
}
