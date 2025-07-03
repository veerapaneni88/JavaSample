package us.tx.state.dfps.service.conservatorship.daoimpl;

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

import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.conservatorship.dao.StageStartCloseDao;
import us.tx.state.dfps.service.cvs.dto.StageStartCloseInDto;
import us.tx.state.dfps.service.cvs.dto.StageStartCloseOutDto;
import us.tx.state.dfps.service.exception.DataNotFoundException;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class Description:This class
 * retrieves ID STAGE, CD STAGE TYPE, and DT STAGE CLOSE for all stages linked
 * to an ID CASE on the STAGE table Aug 11, 2017- 5:14:01 PM © 2017 Texas
 * Department of Family and Protective Services
 */
@Repository
public class StageStartCloseDaoImpl implements StageStartCloseDao {

	@Autowired
	MessageSource messageSource;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${StageStartCloseDaoImpl.getStageDtls}")
	private transient String getStageDtls;

	private static final Logger log = Logger.getLogger(StageStartCloseDaoImpl.class);

	/**
	 * Method name: getstagedtls Method desc:This class retrieves ID STAGE, CD
	 * STAGE TYPE, and DT STAGE CLOSE for all stages linked to an ID CASE on the
	 * STAGE table Legacy name:ccmne1dQUERYdam
	 * 
	 * @param stageStartCloseInDto
	 * @param pOutputDataRec
	 * @return List<Ccmne1doDto> @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<StageStartCloseOutDto> getStageDtls(StageStartCloseInDto stageStartCloseInDto) {
		log.debug("Entering method getStageDtls in StageStartCloseDaoImpl");
		{
			SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(getStageDtls)
					.setResultTransformer(Transformers.aliasToBean(StageStartCloseOutDto.class)));
			sQLQuery1.addScalar("idStage", StandardBasicTypes.LONG);
			sQLQuery1.addScalar("cdStage", StandardBasicTypes.STRING);
			sQLQuery1.addScalar("dtStageClose", StandardBasicTypes.DATE);
			sQLQuery1.addScalar("dtStageStart", StandardBasicTypes.DATE);
			sQLQuery1.setParameter("idCase", stageStartCloseInDto.getIdCase());
			List<StageStartCloseOutDto> liCcmne1doDto = (List<StageStartCloseOutDto>) sQLQuery1.list();
			if (TypeConvUtil.isNullOrEmpty(liCcmne1doDto) || liCcmne1doDto.size() == 0) {
				throw new DataNotFoundException(
						messageSource.getMessage("getstagedtls.not.found.ulIdCase", null, Locale.US));
			}
			log.debug("Exiting method getStageDtls in StageStartCloseDaoImpl");
			return liCcmne1doDto;
		}
	}
}
