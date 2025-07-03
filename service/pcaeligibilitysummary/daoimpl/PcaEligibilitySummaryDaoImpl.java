package us.tx.state.dfps.service.pcaeligibilitysummary.daoimpl;

import java.util.List;

import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.service.pca.dto.PcaEligSummaryValueBeanDto;
import us.tx.state.dfps.service.pcaeligibilitysummary.dao.PcaEligibilitySummaryDao;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: DAO
 * Implement Class for PcaEligibilitySummary May 31, 2018- 11:06:49 AM © 2017
 * Texas Department of Family and Protective Services
 */
@Repository
public class PcaEligibilitySummaryDaoImpl implements PcaEligibilitySummaryDao {

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${PcaEligibilitySummaryDaoImpl.fetchActivePcaForPerson}")
	private String fetchActivePcaForPerson;

	@Autowired
	MessageSource messageSource;

	/**
	 * Method Name: fetchActivePcaForPerson Method Description:This method
	 * returns Active PCA's for Person.
	 * 
	 * @param idPerson
	 * @return List<PcaEligSummaryValueBeanDto>
	 */
	@Override
	public List<PcaEligSummaryValueBeanDto> fetchActivePcaForPerson(Long idPerson) {
		SQLQuery query = (SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(fetchActivePcaForPerson)
				.addScalar("idPcaSubsidy", StandardBasicTypes.LONG).addScalar("dtLastUpdate", StandardBasicTypes.DATE)
				.addScalar("idPerson", StandardBasicTypes.LONG).addScalar("idPayeeResource", StandardBasicTypes.LONG)
				.addScalar("idPcaEligApplication", StandardBasicTypes.LONG)
				.addScalar("idPcaEligRecert", StandardBasicTypes.LONG).addScalar("idEvent", StandardBasicTypes.LONG)
				.addScalar("idPlcmtEvent", StandardBasicTypes.LONG).addScalar("cdDeterm", StandardBasicTypes.STRING)
				.addScalar("dtApprvd", StandardBasicTypes.DATE).addScalar("amtSubsidy", StandardBasicTypes.DOUBLE)
				.addScalar("dtStart", StandardBasicTypes.DATE).addScalar("dtEnd", StandardBasicTypes.DATE)
				.addScalar("dtRecert", StandardBasicTypes.DATE).addScalar("cdCloseRsn", StandardBasicTypes.STRING)
				.addScalar("indThirdPartyInsu", StandardBasicTypes.STRING)
				.addScalar("indEligOverride", StandardBasicTypes.STRING)
				.addScalar("txtComments", StandardBasicTypes.STRING).addScalar("dtLastInvoice", StandardBasicTypes.DATE)
				.addScalar("cdWithdrawRsn", StandardBasicTypes.STRING)
				.addScalar("txtWithdrawRsn", StandardBasicTypes.STRING)
				.addScalar("idCreatedPerson", StandardBasicTypes.LONG).addScalar("dtCreated", StandardBasicTypes.DATE)
				.addScalar("idLastUpdatePerson", StandardBasicTypes.LONG)
				.setResultTransformer(Transformers.aliasToBean(PcaEligSummaryValueBeanDto.class));
		query.setParameter("idPerson", idPerson);
		return query.list();
	}
}
