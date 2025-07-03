package us.tx.state.dfps.service.workload.daoimpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.common.domain.WebsvcFormTrans;
import us.tx.state.dfps.service.common.util.TypeConvUtil;
import us.tx.state.dfps.service.exception.DataNotFoundException;
import us.tx.state.dfps.service.webservices.gold.dto.GoldCommunicationDto;
import us.tx.state.dfps.service.workload.dao.WebsvcFormTransDao;
import us.tx.state.dfps.service.workload.dto.WebsvcFormTransSearchDto;

/**
 * 
 * service-business - IMPACT PHASE 2 MODERNIZATION Tuxedo Service Name:CCMN34S
 * Class Description:Dao layer for DAM-CSESF6D Apr 21, 2017 - 12:44:03 PM
 */
@Repository
public class WebsvcFormTransDaoImpl implements WebsvcFormTransDao {
	@Value("${WebsvcFormTransSearchDaoImpl.getWebsvcFromTrans}")
	private String websvcSearchSql;

	@Autowired
	private SessionFactory sessionFactory;

	public WebsvcFormTransDaoImpl() {

	}

	/**
	 * 
	 * Method Description:This Method will retrieve a row from the
	 * WebsvcFormTrans expcet the clob data for the given EventId. Dam Nmae:
	 * CSESF6D
	 * 
	 * @param idEvent
	 * @return List<WebsvcFormTransSearchDto> @
	 */
	@SuppressWarnings("unchecked")
	public List<WebsvcFormTransSearchDto> websvcFormTransSearch(Long idEvent) {
		List<WebsvcFormTransSearchDto> outputs = new ArrayList<WebsvcFormTransSearchDto>();
		Query queryWebSvc = sessionFactory.getCurrentSession().createSQLQuery(websvcSearchSql)
				.addScalar("idWebsvcFormTrans", StandardBasicTypes.LONG)
				.addScalar("dtCreated", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtLastUpdate", StandardBasicTypes.TIMESTAMP).addScalar("idEvent", StandardBasicTypes.LONG)
				.addScalar("idReturn", StandardBasicTypes.LONG)
				.setResultTransformer(Transformers.aliasToBean(WebsvcFormTransSearchDto.class));
		queryWebSvc.setParameter("idEvent", idEvent);
		outputs = queryWebSvc.list();
		if (TypeConvUtil.isNullOrEmpty(outputs)) {
			throw new DataNotFoundException("websvcFormTransSearch not found");
		}

		return outputs;
	}

	@Override
	public Long insertFormSendData(Long eventId){
		WebsvcFormTrans websvcFormTrans = new WebsvcFormTrans();
		websvcFormTrans.setIdEvent(eventId);
		websvcFormTrans.setDtCreated(new Date());
		websvcFormTrans.setDtLastUpdate(new Date());
		return (Long) sessionFactory.getCurrentSession().save(websvcFormTrans);
	}

	@Override
	public boolean updateFormSendData(GoldCommunicationDto goldCommunicationDto){
		String sentXML = (null != goldCommunicationDto.getSentXml()) ? goldCommunicationDto.getSentXml() : "";
		String returnXML = (null != goldCommunicationDto.getReturnEnvelope()) ? goldCommunicationDto.getReturnEnvelope() : "";
		WebsvcFormTrans websvcFormTrans = (WebsvcFormTrans) sessionFactory.getCurrentSession()
				.load(WebsvcFormTrans.class, Long.valueOf(goldCommunicationDto.getIdComm()));
		if (null != websvcFormTrans) {
			websvcFormTrans.setNbrReturn(goldCommunicationDto.getReturnCode());
			websvcFormTrans.setTxtFormData(returnXML);
			websvcFormTrans.setTxtSentXml(sentXML);
			websvcFormTrans.setDtLastUpdate(new Date());
			sessionFactory.getCurrentSession().update(websvcFormTrans);
			return true;
		}else{
			return false;
		}

	}
}
