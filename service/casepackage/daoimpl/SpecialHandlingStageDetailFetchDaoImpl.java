package us.tx.state.dfps.service.casepackage.daoimpl;

import java.util.ArrayList;
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

import us.tx.state.dfps.service.casepackage.dao.SpecialHandlingStageDetailFetchDao;
import us.tx.state.dfps.service.casepackage.dto.SpecialHandlingStageDetailInDto;
import us.tx.state.dfps.service.casepackage.dto.SpecialHandlingStageDetailOutDto;
import us.tx.state.dfps.service.casepackage.dto.SpecialHandlingStageRowOutDto;

@Repository
public class SpecialHandlingStageDetailFetchDaoImpl implements SpecialHandlingStageDetailFetchDao {
	@Autowired
	MessageSource messageSource;

	@Value("${SpecialHandlingStageDetailFetchDaoImpl.specialHandlingStageDetailFetch}")
	private String specialHandlingStageDetailFetch;

	@Autowired
	private SessionFactory sessionFactory;

	private static final Logger log = Logger.getLogger("ServiceBusiness-EmployeeDaoLog");

	/**
	 * Method Name: specialHandlingStageDetailFetch Method Description: fetch
	 * stage details for specialHandling, DAM ccmne1d
	 * 
	 * @param specialHandlingStageDetailInDto
	 * @param specialHandlingStageDetailOutDto
	 * @return @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void specialHandlingStageDetailFetch(SpecialHandlingStageDetailInDto specialHandlingStageDetailInDto,
			SpecialHandlingStageDetailOutDto specialHandlingStageDetailOutDto) {
		log.debug("Entering method specialHandlingStageDetailFetch in SpecialHandlingStageDetailFetchDaoImpl");

		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession()
				.createSQLQuery(specialHandlingStageDetailFetch).addScalar("idStage", StandardBasicTypes.LONG)
				.addScalar("cdStage", StandardBasicTypes.STRING).addScalar("dtStageClose", StandardBasicTypes.DATE)
				.addScalar("dtStageStart", StandardBasicTypes.DATE)
				.setParameter("idCase", specialHandlingStageDetailInDto.getIdCase())
				.setResultTransformer(Transformers.aliasToBean(SpecialHandlingStageRowOutDto.class)));

		List<SpecialHandlingStageRowOutDto> specialHandlingStageRowOutDtos = new ArrayList<>();
		specialHandlingStageRowOutDtos = (List<SpecialHandlingStageRowOutDto>) sQLQuery1.list();

		specialHandlingStageDetailOutDto.setSpecialHandlingStageRowOutDtos(specialHandlingStageRowOutDtos);

		log.debug("Exiting method specialHandlingStageDetailFetch in SpecialHandlingStageDetailFetchDaoImpl");
	}

}
