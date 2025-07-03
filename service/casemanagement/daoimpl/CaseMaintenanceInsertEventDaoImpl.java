package us.tx.state.dfps.service.casemanagement.daoimpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.service.casemanagement.dao.CaseMaintenanceInsertEventDao;
import us.tx.state.dfps.service.casepackage.dto.UpdateEventInDto;
import us.tx.state.dfps.service.casepackage.dto.UpdateEventOutDto;
import us.tx.state.dfps.service.common.ServiceConstants;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:CaseMaintenanceInsertEventDaoImpl Feb 7, 2018- 5:51:03 PM © 2017
 * Texas Department of Family and Protective Services
 */
@Repository
public class CaseMaintenanceInsertEventDaoImpl implements CaseMaintenanceInsertEventDao {

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${CaseMaintenanceInsertEventDaoImpl.strQuery1}")
	private transient String strQuery1;

	@Value("${CaseMaintenanceInsertEventDaoImpl.strQuery2}")
	private transient String strQuery2;

	@Value("${CaseMaintenanceInsertEventDaoImpl.strQuery3}")
	private transient String strQuery3;

	@Value("${CaseMaintenanceInsertEventDaoImpl.strQuery4}")
	private transient String strQuery4;

	@Value("${CaseMaintenanceInsertEventDaoImpl.strQuery5}")
	private transient String strQuery5;

	public static final String PLA_EVENT = "PLA";
	public static final String REF_EVENT = "REF";
	public static final String LES_EVENT = "LES";
	public static final String REMOVAL_EVENT = "REM";
	public static final String LOC_EVENT = "LOC";

	private static final Logger log = Logger.getLogger(CaseMaintenanceInsertEventDaoImpl.class);

	/**
	 * Method Name: Method Description:Update event detail DAM: ccmn46d
	 * 
	 * @param updateEventInDto
	 * @param updateEventOutDto
	 * @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void updateEvent(UpdateEventInDto updateEventInDto, UpdateEventOutDto updateEventOutDto) {
		log.debug("Entering method updateEvent in CaseMaintenanceInsertEventDaoImpl");
		Date dtCurrentDate = new Date();
		switch (updateEventInDto.getReqFuncCd()) {
		case ServiceConstants.REQ_FUNC_CD_ADD:
		case ServiceConstants.REQ_FUNC_CD_ADD_KIN:
			SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(strQuery1)
					.addScalar("idEvent", StandardBasicTypes.LONG)
					.setResultTransformer(Transformers.aliasToBean(UpdateEventOutDto.class)));
			List<UpdateEventOutDto> updateEventOutDtos = new ArrayList<>();
			updateEventOutDtos = (List<UpdateEventOutDto>) sQLQuery1.list();

			SQLQuery sQLQuery2 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(strQuery2)
					.setParameter("hI_tsDtEventModified", dtCurrentDate)
					.setParameter("hI_ulIdStage", updateEventInDto.getIdStage())
					.setParameter("hO_ulIdEvent", updateEventOutDtos.get(0).getIdEvent())
					.setParameter("hI_szTxtEventDescr", updateEventInDto.getTxtEventDescr())
					.setParameter("hI_szCdTask", updateEventInDto.getCdTask())
					.setParameter("hI_szCdEventStatus", updateEventInDto.getCdEventStatus())
					.setParameter("hI_ulIdPerson", updateEventInDto.getIdPerson())
					.setParameter("hI_szCdEventType", updateEventInDto.getCdEventType())
					.setParameter("hI_dtDtEventOccurred", updateEventInDto.getDtEventOccurred() != null
							? updateEventInDto.getDtEventOccurred() : dtCurrentDate));
			sQLQuery2.executeUpdate();

			break;
		case ServiceConstants.REQ_FUNC_CD_UPDATE:
			if ((PLA_EVENT.equalsIgnoreCase(updateEventInDto.getCdEventType()))
					|| (REF_EVENT.equalsIgnoreCase(updateEventInDto.getCdEventType()))
					|| (LES_EVENT.equalsIgnoreCase(updateEventInDto.getCdEventType()))
					|| (REMOVAL_EVENT.equalsIgnoreCase(updateEventInDto.getCdEventType()))
					|| (LOC_EVENT.equalsIgnoreCase(updateEventInDto.getCdEventType()))) {
				SQLQuery sQLQuery3 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(strQuery3)
						.setParameter("hI_tsDtEventModified", dtCurrentDate)
						.setParameter("hI_ulIdStage", updateEventInDto.getIdStage())
						.setParameter("hI_szTxtEventDescr", updateEventInDto.getTxtEventDescr())
						.setParameter("hI_szCdTask", updateEventInDto.getCdTask())
						.setParameter("hI_szCdEventStatus", updateEventInDto.getCdEventStatus())
						.setParameter("hI_ulIdPerson", updateEventInDto.getIdPerson())
						.setParameter("hI_tsLastUpdate", updateEventInDto.getTsLastUpdate())
						.setParameter("hI_szCdEventType", updateEventInDto.getCdEventType())
						.setParameter("hI_ulIdEvent", updateEventInDto.getIdEvent()));
				sQLQuery3.executeUpdate();
				break;
			} else {
				SQLQuery sQLQuery4 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(strQuery4)
						.setParameter("hI_tsDtEventModified", dtCurrentDate)
						.setParameter("hI_ulIdStage", updateEventInDto.getIdStage())
						.setParameter("hI_szTxtEventDescr", updateEventInDto.getTxtEventDescr())
						.setParameter("hI_szCdTask", updateEventInDto.getCdTask())
						.setParameter("hI_szCdEventStatus", updateEventInDto.getCdEventStatus())
						.setParameter("hI_ulIdPerson", updateEventInDto.getIdPerson())
						.setParameter("hI_tsLastUpdate", updateEventInDto.getTsLastUpdate())
						.setParameter("hI_szCdEventType", updateEventInDto.getCdEventType())
						.setParameter("hI_ulIdEvent", updateEventInDto.getIdEvent())
						.setParameter("hI_dtDtEventOccurred", updateEventInDto.getDtEventOccurred() != null
								? updateEventInDto.getDtEventOccurred() : dtCurrentDate));
				sQLQuery4.executeUpdate();

				break;
			}

		case ServiceConstants.REQ_FUNC_CD_DELETE:
			SQLQuery sQLQuery5 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(strQuery5)
					.setParameter("hI_tsLastUpdate", updateEventInDto.getTsLastUpdate())
					.setParameter("hI_ulIdEvent", updateEventInDto.getIdEvent()));
			sQLQuery5.executeUpdate();

			break;
		}

		log.debug("Exiting method updateEvent in CaseMaintenanceInsertEventDaoImpl");
	}

}
