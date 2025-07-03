package us.tx.state.dfps.service.casemanagement.daoimpl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import us.tx.state.dfps.service.casemanagement.dao.FetchTaskEventDao;
import us.tx.state.dfps.service.casepackage.dto.GetEventInDto;
import us.tx.state.dfps.service.casepackage.dto.GetEventOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:FetchTaskEventImpl Feb 7, 2018- 5:52:55 PM © 2017 Texas
 * Department of Family and Protective Services
 */
@Repository
public class FetchTaskEventImpl implements FetchTaskEventDao {
	@Autowired
	private SessionFactory sessionFactory;

	@Value("${FetchTaskEventImpl.strCCMN45D_CURSORQuery}")
	private transient String strCCMN45D_CURSORQuery;

	private static final Logger log = Logger.getLogger(FetchTaskEventImpl.class);

	/**
	 * Method Name: fetchTaskEvent Method Description:This Method is used to
	 * fetch TaskEvent DAM: ccmn45d
	 * 
	 * @param getEventInDto
	 * @param getEventOutDto
	 * @return @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void fetchTaskEvent(GetEventInDto getEventInDto, GetEventOutDto getEventOutDto) {
		log.debug("Entering method fetchTaskEvent in FetchTaskEventImpl");

		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(strCCMN45D_CURSORQuery)
				.addScalar("szCdTask", StandardBasicTypes.STRING)
				.addScalar("tsLastUpdate", StandardBasicTypes.TIMESTAMP)
				.addScalar("szCdEventStatus", StandardBasicTypes.STRING)
				.addScalar("szCdEventType", StandardBasicTypes.STRING)
				.addScalar("dtDtEventOccurred", StandardBasicTypes.TIMESTAMP)
				.addScalar("ulIdEvent", StandardBasicTypes.LONG).addScalar("ulIdStage", StandardBasicTypes.LONG)
				.addScalar("ulIdPerson", StandardBasicTypes.LONG)
				.addScalar("szTxtEventDescr", StandardBasicTypes.STRING)
				.addScalar("dtDtEventCreated", StandardBasicTypes.TIMESTAMP)
				.setParameter("hI_ulIdEvent", getEventInDto.getIdEvent())
				.setResultTransformer(Transformers.aliasToBean(GetEventOutDto.class)));

		List<GetEventOutDto> getEventOutDtos = new ArrayList<>();
		getEventOutDtos = (List<GetEventOutDto>) sQLQuery1.list();

		if (!CollectionUtils.isEmpty(getEventOutDtos)) {
			BeanUtils.copyProperties(getEventOutDtos.get(0), getEventOutDto);
		}
		log.debug("Exiting method fetchTaskEvent in FetchTaskEventImpl");
	}

}
