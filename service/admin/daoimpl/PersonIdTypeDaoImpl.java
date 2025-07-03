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
import org.springframework.util.ObjectUtils;

import us.tx.state.dfps.common.domain.Stage;
import us.tx.state.dfps.service.admin.dao.PersonIdTypeDao;
import us.tx.state.dfps.service.admin.dto.PersonDiDto;
import us.tx.state.dfps.service.admin.dto.PersonIdTypeInDto;
import us.tx.state.dfps.service.admin.dto.PersonIdTypeOutDto;
import us.tx.state.dfps.service.common.ServiceConstants;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: This
 * Retrieves SSN row from the person_id table if one exists Aug 5, 2017-
 * 11:28:32 AM © 2017 Texas Department of Family and Protective Services
 */
@Repository
public class PersonIdTypeDaoImpl implements PersonIdTypeDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${PersonIdTypeDaoImpl.getSSNforPersonID}")
	private String getSSNforPersonID;
	
	@Value("${PersonIdTypeDaoImpl.getYouthInNYDTSurvey}")
	private String getYouthInNYDTSurvey;
	

	private static final Logger log = Logger.getLogger(PersonIdTypeDaoImpl.class);

	public PersonIdTypeDaoImpl() {
		super();
	}

	/**
	 * 
	 * Method Name: getSSNforPersonID Method Description: This method will
	 * retrieve SSN for a Person from Person ID table. Csesf7d
	 * 
	 * @param personIdTypeInDto
	 * @return List<PersonIdTypeOutDto>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<PersonIdTypeOutDto> getSSNforPersonID(PersonIdTypeInDto personIdTypeInDto) {
		log.debug("Entering method getSSNforPersonID in PersonIdTypeDaoImpl");
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getSSNforPersonID)
				.addScalar("nbrPersonIdNumber", StandardBasicTypes.STRING)
				.addScalar("cdSsnSource", StandardBasicTypes.STRING)
				.addScalar("cdSsnVerifMeth", StandardBasicTypes.STRING)
				.setResultTransformer(Transformers.aliasToBean(PersonIdTypeOutDto.class)));
		sQLQuery1.setParameter("hI_ulIdPerson", personIdTypeInDto.getIdPerson());
		List<PersonIdTypeOutDto> personIdTypeOutDtos = (List<PersonIdTypeOutDto>) sQLQuery1.list();
		log.debug("Exiting method getSSNforPersonID in PersonIdTypeDaoImpl");
		return personIdTypeOutDtos;
	}
	/**
	 * 
	 *Method Name:	verifyYouthInNYDTSurvey
	 *Method Description: This method checks for a Youth in NYTD survey.
	 *@param idStage
	 *@return
	 */
	@Override
	public Boolean verifyYouthInNYDTSurvey(Long idStage) {
		Boolean isYouthInSurvey = Boolean.FALSE;
		log.debug("Entering method verifyYouthInNYDTSurvey in PersonIdTypeDaoImpl");
		Stage stage = (Stage) sessionFactory.getCurrentSession().get(Stage.class, idStage);
		if(!ObjectUtils.isEmpty(stage) && !ObjectUtils.isEmpty(stage.getCdStage()) && ServiceConstants.CSTAGES_PAL.equalsIgnoreCase(stage.getCdStage())){
			SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getYouthInNYDTSurvey)
					.addScalar("idPerson", StandardBasicTypes.LONG)
					.setResultTransformer(Transformers.aliasToBean(PersonDiDto.class)));
			sQLQuery1.setParameter("idStage", idStage);
			List<PersonDiDto> personDiDtoList = (List<PersonDiDto>) sQLQuery1.list();
			log.debug("Exiting method verifyYouthInNYDTSurvey in PersonIdTypeDaoImpl");
			if(!ObjectUtils.isEmpty(personDiDtoList) && 0 < personDiDtoList.size()){
				isYouthInSurvey = Boolean.TRUE;
			}
		}
		return isYouthInSurvey;
	}
}
