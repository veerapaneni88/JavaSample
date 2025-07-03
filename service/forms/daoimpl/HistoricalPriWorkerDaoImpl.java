package us.tx.state.dfps.service.forms.daoimpl;

import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.service.forms.dao.HistoricalPriWorkerDao;
import us.tx.state.dfps.service.forms.dto.HistoricalPriWorkerDto;
import us.tx.state.dfps.service.forms.dto.HistoricalPriWorkerInDto;

@Repository
public class HistoricalPriWorkerDaoImpl implements HistoricalPriWorkerDao {
	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${historicalPriWorkerDaoImpl.historyPriWorkerSql}")
	private String historyPriWorkerSql;

	private static final Logger log = Logger.getLogger(HistoricalPriWorkerDaoImpl.class);

	/**
	 * Description : The method is to retrieve the PRIMARY ( PR ) or HISTORICAL
	 * PRIMARY ( HP ) worker and NM_STAGE of the ID_STAGE which is passed into
	 * the dam .Converted ccmn19d Dam in this.
	 **
	 * @param pInputDataRec
	 * @return List<HistoricalPriWorkerDto> @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<HistoricalPriWorkerDto> getHistoricalPriWorker(HistoricalPriWorkerInDto pInputDataRec) {
		log.debug("Entering method ccmn19dQUERYdam in HistoricalPriWorkerDaoImpl");

		List<HistoricalPriWorkerDto> historicalPriWorkerDtoList = (sessionFactory.getCurrentSession()
				.createSQLQuery(historyPriWorkerSql).addScalar("nmStage", StandardBasicTypes.STRING)
				.addScalar("idTodoPersWorker", StandardBasicTypes.LONG)
				.addScalar("nmPersonFull", StandardBasicTypes.STRING)
				.setParameter("idStage", pInputDataRec.getIdStage())
				.setString("cdStagePersRole", pInputDataRec.getCdStagePersRole())
				.setResultTransformer(Transformers.aliasToBean(HistoricalPriWorkerDto.class)).list());
		log.debug("Exiting method ccmn19dQUERYdam in HistoricalPriWorkerDaoImpl");
		return historicalPriWorkerDtoList;
	}

}
