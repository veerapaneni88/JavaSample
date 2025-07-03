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

import us.tx.state.dfps.service.admin.dao.TempStagePersLinkDao;
import us.tx.state.dfps.service.admin.dto.TempStagePersLinkInDto;
import us.tx.state.dfps.service.admin.dto.TempStagePersLinkOutDto;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description: Fetch the
 * stage person link Aug 11, 2017- 5:36:22 PM © 2017 Texas Department of Family
 * and Protective Services
 */
@Repository
public class TempStagePersLinkDaoImpl implements TempStagePersLinkDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${TempStagePersLinkDaoImpl.getTempStagePersonLink}")
	private String getTempStagePersonLink;

	private static final Logger log = Logger.getLogger(TempStagePersLinkDaoImpl.class);

	public TempStagePersLinkDaoImpl() {
		super();
	}

	/**
	 * 
	 * Method Name: getTempStagePersonLink Method Description: Get temporary
	 * stage person record for given stage id. Cses92d
	 * 
	 * @param tempStagePersLinkInDto
	 * @return List<TempStagePersLinkOutDto>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<TempStagePersLinkOutDto> getTempStagePersonLink(TempStagePersLinkInDto tempStagePersLinkInDto) {
		log.debug("Entering method getTempStagePersonLink in TempStagePersLinkDaoImpl");
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getTempStagePersonLink)
				.addScalar("idTempStagePerson", StandardBasicTypes.LONG)
				.addScalar("idTempStagePersLink", StandardBasicTypes.LONG)
				.addScalar("idTempStage", StandardBasicTypes.LONG)
				.addScalar("tsLastUpdate", StandardBasicTypes.TIMESTAMP).addScalar("cdTempStage")
				.addScalar("cdTempStagePersRole").addScalar("cdTempStagePersType")
				.addScalar("dtTempStagePersLink", StandardBasicTypes.TIMESTAMP)
				.setParameter("hI_ulIdStage", tempStagePersLinkInDto.getIdStage())
				.setParameter("hI_szCdStagePersRole", tempStagePersLinkInDto.getCdStagePersRole())
				.setResultTransformer(Transformers.aliasToBean(TempStagePersLinkOutDto.class)));
		List<TempStagePersLinkOutDto> tempStagePersLinkOutDtos = (List<TempStagePersLinkOutDto>) sQLQuery1.list();
		if (TypeConvUtil.isNullOrEmpty(tempStagePersLinkOutDtos)) {
			throw new DataNotFoundException(
					messageSource.getMessage("Cses92dDaoImpl.No.Temp.Stage.Person.Link.Found", null, Locale.US));
		}
		log.debug("Exiting method getTempStagePersonLink in TempStagePersLinkDaoImpl");
		return tempStagePersLinkOutDtos;
	}
}
