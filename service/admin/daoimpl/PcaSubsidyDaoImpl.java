package us.tx.state.dfps.service.admin.daoimpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.common.domain.PcaSubsidy;
import us.tx.state.dfps.service.admin.dao.PcaSubsidyDao;
import us.tx.state.dfps.service.admin.dto.PcaSubsidyInDto;
import us.tx.state.dfps.service.admin.dto.PcaSubsidyOutDto;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Retrieves
 * the PCA subsidy record for an person id. Aug 10, 2017- 7:48:48 PM © 2017
 * Texas Department of Family and Protective Services
 */
@Repository
public class PcaSubsidyDaoImpl implements PcaSubsidyDao {

	public PcaSubsidyDaoImpl() {
		super();
	}

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${PcaSubsidyDaoImpl.getPCARecord}")
	private String getPCARecord;

	private static final Logger log = Logger.getLogger(PcaSubsidyDaoImpl.class);

	/**
	 * 
	 * Method Name: getPCARecord Method Description: Get PCA record for given
	 * person.
	 * 
	 * @param pInputDataRec
	 * @return List<Clssc8doDto>
	 * @,DataNotFoundException
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<PcaSubsidyOutDto> getPCARecord(PcaSubsidyInDto pInputDataRec) {
		log.debug("Entering method PcaSubsidyQUERYdam in PcaSubsidyDaoImpl");
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getPCARecord)
				.addScalar("idPCASubsidy", StandardBasicTypes.LONG).addScalar("idEvent", StandardBasicTypes.LONG)
				.addScalar("tsLastUpdate", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtStart", StandardBasicTypes.TIMESTAMP).addScalar("dtEnd", StandardBasicTypes.TIMESTAMP)
				.addScalar("idPerson", StandardBasicTypes.LONG).addScalar("idPlcmtEvent", StandardBasicTypes.LONG)
				.addScalar("amtSubsidy", StandardBasicTypes.FLOAT).addScalar("cdCloseRsn", StandardBasicTypes.STRING)
				.addScalar("cdDeterm", StandardBasicTypes.STRING)
				.setParameter("hI_ulIdPerson", pInputDataRec.getIdPerson())
				.setParameter("hI_dtDtPersonDeath", pInputDataRec.getDtPersonDeath())
				.setResultTransformer(Transformers.aliasToBean(PcaSubsidyOutDto.class)));
		List<PcaSubsidyOutDto> liClssc8doDto = (List<PcaSubsidyOutDto>) sQLQuery1.list();
		if (TypeConvUtil.isNullOrEmpty(liClssc8doDto)) {
			throw new DataNotFoundException(messageSource.getMessage("Clssc8dDaoImpl.no.pca.record", null, Locale.US));
		}
		log.debug("Exiting method PcaSubsidyQUERYdam in PcaSubsidyDaoImpl");
		return liClssc8doDto;
	}

	/**
	 * 
	 * Method Name: getPCASubsidyRecord Method Description:CCMNI8D - Retrieves
	 * the PCA subsidy record for an event id.
	 * 
	 * @param idEvent
	 * @return
	 */
	public List<PcaSubsidyOutDto> getPCASubsidyRecord(Long idEvent) {

		List<PcaSubsidyOutDto> eventDtoList = new ArrayList<>();

		eventDtoList = sessionFactory.getCurrentSession().createCriteria(PcaSubsidy.class).setProjection(Projections
				.projectionList().add(Projections.property("idPcaSubsidy"), "idPCASubsidy")
				.add(Projections.property("eventByIdEvent.idEvent"), "idEvent")
				.add(Projections.property("person.idPerson"), "idPerson")
				.add(Projections.property("eventByIdPlcmtEvent.idEvent"), "idPlcmtEvent")
				.add(Projections.property("amtSubsidy"), "amtSubsidy").add(Projections.property("cdDeterm"), "cdDeterm")
				.add(Projections.property("cdCloseRsn"), "cdCloseRsn").add(Projections.property("dtStart"), "dtStart")
				.add(Projections.property("dtLastUpdate"), "tsLastUpdate").add(Projections.property("dtEnd"), "dtEnd"))
				.add(Restrictions.eq("eventByIdEvent.idEvent", idEvent))
				.setResultTransformer(Transformers.aliasToBean(PcaSubsidyOutDto.class)).list();
		return eventDtoList;
	}
}
