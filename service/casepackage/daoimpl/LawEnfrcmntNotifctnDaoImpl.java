package us.tx.state.dfps.service.casepackage.daoimpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import us.tx.state.dfps.common.domain.LawEnforcementNotifctn;
import us.tx.state.dfps.service.casepackage.dao.LawEnfrcmntNotifctnDao;
import us.tx.state.dfps.service.casepackage.dto.LawEnfrcmntNotifctnDto;
import us.tx.state.dfps.service.common.ServiceConstants;
import us.tx.state.dfps.service.common.request.LawEnfrcmntNotifctnReq;
import us.tx.state.dfps.service.common.response.LawEnfrcmntNotifctnRes;
import us.tx.state.dfps.service.common.service.CommonService;

@Repository
public class LawEnfrcmntNotifctnDaoImpl implements LawEnfrcmntNotifctnDao {

	@Value("${LawEnfrcmntNotifctnDaoImp.getLawEnforcementNotifList}")
	private String getLawEnforcementNotifList;

	@Autowired
	CommonService commonService;

	@Autowired
	public SessionFactory sessionFactory;

	public LawEnfrcmntNotifctnDaoImpl() {

	}

	@Override
	public LawEnfrcmntNotifctnRes saveLawEnforcementNotifcn(LawEnfrcmntNotifctnReq lawEnfrcmntNotifctnReq) {
		LawEnfrcmntNotifctnRes response = new LawEnfrcmntNotifctnRes();
		LawEnfrcmntNotifctnDto LawEnfrcmntNotifctnDto = lawEnfrcmntNotifctnReq.getLawEnfrcmntNotifctnDto();
		if (null != lawEnfrcmntNotifctnReq) {
			LawEnforcementNotifctn lawEnforcementNotifctn = new LawEnforcementNotifctn();
			lawEnforcementNotifctn.setCdType(LawEnfrcmntNotifctnDto.getCdLENType());
			lawEnforcementNotifctn.setIdResource(LawEnfrcmntNotifctnDto.getIdResource());
			lawEnforcementNotifctn.setIdStage(LawEnfrcmntNotifctnDto.getIdStage());
			lawEnforcementNotifctn.setCdStat(ServiceConstants.LEN_STATUS_10);
			lawEnforcementNotifctn.setIdCreatedPerson(LawEnfrcmntNotifctnDto.getIdCreatedPerson());
			lawEnforcementNotifctn.setIdLastUpdatePerson(LawEnfrcmntNotifctnDto.getIdLastUpdatePerson());
			lawEnforcementNotifctn.setDtCreated(new Date());
			lawEnforcementNotifctn.setDtLastUpdate(new Date());
			sessionFactory.getCurrentSession().saveOrUpdate(lawEnforcementNotifctn);
			response.setMessage(ServiceConstants.FORM_SUCCESS);
		}
		return response;
	}

	@SuppressWarnings("unchecked")
	@Override
	public LawEnfrcmntNotifctnRes getLawEnforcementNotifList(LawEnfrcmntNotifctnReq lawEnfrcmntNotifctnReq) {
		List<LawEnfrcmntNotifctnDto> lawEnfrcmntNotifctnDtoList = new ArrayList<LawEnfrcmntNotifctnDto>();
		LawEnfrcmntNotifctnRes response = new LawEnfrcmntNotifctnRes();
		lawEnfrcmntNotifctnDtoList = (List<LawEnfrcmntNotifctnDto>) sessionFactory.getCurrentSession()
				.createSQLQuery(getLawEnforcementNotifList).addScalar("idLawEnfrcmntNotifctn", StandardBasicTypes.LONG)
				.addScalar("idResource", StandardBasicTypes.LONG).addScalar("cdLENType", StandardBasicTypes.STRING)
				.addScalar("cdStat", StandardBasicTypes.STRING).addScalar("idStage", StandardBasicTypes.LONG)
				.addScalar("dtCreated", StandardBasicTypes.TIMESTAMP).addScalar("tmCreated", StandardBasicTypes.STRING)
				.addScalar("idCreatedPerson", StandardBasicTypes.LONG)
				.addScalar("dtLastUpdate", StandardBasicTypes.TIMESTAMP)
				.addScalar("idLastUpdatePerson", StandardBasicTypes.LONG)
				.addScalar("nmCreatedPerson", StandardBasicTypes.STRING)
				.addScalar("nmResource", StandardBasicTypes.STRING)
				.setParameter("id_Stage", lawEnfrcmntNotifctnReq.getIdStage())
				.setResultTransformer(Transformers.aliasToBean(LawEnfrcmntNotifctnDto.class)).list();
		response.setLawEnfrcmntNotifctnDto(lawEnfrcmntNotifctnDtoList);
		return response;
	}
}
