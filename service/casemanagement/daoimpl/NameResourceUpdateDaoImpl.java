package us.tx.state.dfps.service.casemanagement.daoimpl;

import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.service.casemanagement.dao.NameResourceUpdateDao;
import us.tx.state.dfps.service.casepackage.dto.CapsCaseResourceUpdateInDto;
import us.tx.state.dfps.service.casepackage.dto.CapsCaseResourceUpdateOutDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.exception.DataLayerException;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:NameResourceUpdateDaoImpl Feb 7, 2018- 5:53:04 PM © 2017 Texas
 * Department of Family and Protective Services
 */
@Repository
public class NameResourceUpdateDaoImpl implements NameResourceUpdateDao {
	@Autowired
	private SessionFactory sessionFactory;

	@Value("${NameResourceUpdateDaoImpl.strQuery1}")
	private transient String strQuery1;

	private static final Logger log = Logger.getLogger(NameResourceUpdateDaoImpl.class);

	/**
	 * Method Name: updateNameResource Method Description:This Method is used to
	 * retireve the information of all
	 * 
	 * @param capsCaseResourceUpdateInDto
	 * @param capsCaseResourceUpdateOutDto
	 * @return @
	 */
	@Override
	public void updateNameResource(CapsCaseResourceUpdateInDto capsCaseResourceUpdateInDto,
			CapsCaseResourceUpdateOutDto capsCaseResourceUpdateOutDto) {
		log.debug("Entering method updateNameResource in NameResourceUpdateDaoImpl");
		switch (capsCaseResourceUpdateInDto.getReqFuncCd()) {
		case ServiceConstants.REQ_FUNC_CD_ADD:
			throw new DataLayerException("Invalid function code");
		case ServiceConstants.REQ_FUNC_CD_UPDATE:
			SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(strQuery1)
					.setParameter("hI_ulIdRsrcFaHomeStage", capsCaseResourceUpdateInDto.getUlIdRsrcFaHomeStage())
					.setParameter("hI_szNmResource", capsCaseResourceUpdateInDto.getSzNmResource())
					.setParameter("hI_szNmRsrcLastUpdate", capsCaseResourceUpdateInDto.getSzNmRsrcLastUpdate()));
			sQLQuery1.executeUpdate();

			break;
		case ServiceConstants.REQ_FUNC_CD_DELETE:
			throw new DataLayerException("Invalid function code");
		}

		log.debug("Exiting method updateNameResource in NameResourceUpdateDaoImpl");
	}

}
