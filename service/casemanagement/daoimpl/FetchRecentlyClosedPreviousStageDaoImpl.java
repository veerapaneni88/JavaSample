package us.tx.state.dfps.service.casemanagement.daoimpl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import us.tx.state.dfps.service.casemanagement.dao.FetchRecentlyClosedPreviousStageDao;
import us.tx.state.dfps.service.casepackage.dto.PreviousStageInputDto;
import us.tx.state.dfps.service.casepackage.dto.PreviousStageOutputDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:<enter the
 * description of class> Jan 22, 2018- 10:38:09 PM © 2017 Texas Department of
 * Family and Protective Services
 */

// Ccmnb5dDaoImpl
@Repository
public class FetchRecentlyClosedPreviousStageDaoImpl implements FetchRecentlyClosedPreviousStageDao {

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${FetchRecentlyClosedPreviousStageDaoImpl.strCCMNB5DCURSORQuery}")
	private String strCCMNB5DCURSORQuery;

	private static final Logger log = Logger.getLogger(FetchRecentlyClosedPreviousStageDaoImpl.class);

	/**
	 * Method Name: fetchRecentlyClosedPreviousStage Method Description:This
	 * Method is used to fetch the information of Recently Closed Previous Stage
	 * 
	 * @param previousStageInputDto
	 * @param previousStageOutputDto
	 * @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void fetchRecentlyClosedPreviousStage(PreviousStageInputDto previousStageInputDto,
			PreviousStageOutputDto previousStageOutputDto) {
		log.debug("Entering method fetchRecentlyClosedPreviousStage in FetchRecentlyClosedPreviousStageDaoImpl");
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(strCCMNB5DCURSORQuery)
				.addScalar("idPriorStage", StandardBasicTypes.LONG)
				.setParameter("idStage", previousStageInputDto.getIdStage())
				.setResultTransformer(Transformers.aliasToBean(PreviousStageOutputDto.class)));

		List<PreviousStageOutputDto> previousStageOutputDtos = new ArrayList<>();
		previousStageOutputDtos = (List<PreviousStageOutputDto>) sQLQuery1.list();
		if (!CollectionUtils.isEmpty(previousStageOutputDtos)) {
			previousStageOutputDto.setIdPriorStage(previousStageOutputDtos.get(0).getIdPriorStage());
		}

		log.debug("Exiting method fetchRecentlyClosedPreviousStage in FetchRecentlyClosedPreviousStageDaoImpl");
	}

}
