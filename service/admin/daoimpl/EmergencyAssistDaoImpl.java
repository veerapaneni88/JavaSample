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

import us.tx.state.dfps.service.admin.dao.EmergencyAssistDao;
import us.tx.state.dfps.service.admin.dto.EmergencyAssistInDto;
import us.tx.state.dfps.service.admin.dto.EmergencyAssistOutDto;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This class
 * is used to fetch the questions based on the events in ascending order Aug
 * 5,2017- 5:31:17 PM © 2017 Texas Department of Family and Protective Services
 */
@Repository
public class EmergencyAssistDaoImpl implements EmergencyAssistDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${EmergencyAssistDaoImpl.getQuestDtls}")
	private transient String getQuestDtls;

	private static final Logger log = Logger.getLogger(EmergencyAssistDaoImpl.class);

	public EmergencyAssistDaoImpl() {
		super();
	}

	/**
	 * 
	 * Method Name: fetchQues Method Description: This method fetches questions
	 * based on the event from emergency assist.
	 * 
	 * @param pInputDataRec
	 * @return List<EmergencyAssistOutDto> @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<EmergencyAssistOutDto> fetchQues(EmergencyAssistInDto pInputDataRec) {
		log.debug("Entering method EmergencyAssistQUERYdam in EmergencyAssistDaoImpl");
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getQuestDtls)
				.setResultTransformer(Transformers.aliasToBean(EmergencyAssistOutDto.class)));
		sQLQuery1.addScalar("cdEaQuestion", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("idEmergencyAssist", StandardBasicTypes.LONG);
		sQLQuery1.addScalar("indEaResponse", StandardBasicTypes.STRING);
		sQLQuery1.addScalar("tsLastUpdate", StandardBasicTypes.TIMESTAMP);
		sQLQuery1.setParameter("hI_ulIdEvent", pInputDataRec.getIdEvent());
		List<EmergencyAssistOutDto> liCinv15doDto = (List<EmergencyAssistOutDto>) sQLQuery1.list();
		if (TypeConvUtil.isNullOrEmpty(liCinv15doDto)) {
			throw new DataNotFoundException(
					messageSource.getMessage("EmergencyAssistDaoImpl.ulIdEvent.notFound", null, Locale.US));
		}
		log.debug("Exiting method EmergencyAssistQUERYdam in EmergencyAssistDaoImpl");
		return liCinv15doDto;
	}
}
