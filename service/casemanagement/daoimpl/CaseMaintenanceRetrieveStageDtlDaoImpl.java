package us.tx.state.dfps.service.casemanagement.daoimpl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import us.tx.state.dfps.service.admin.dto.StageLinkStageOutDto;
import us.tx.state.dfps.service.casemanagement.dao.CaseMaintenanceRetrieveStageDtlDao;
import us.tx.state.dfps.service.casepackage.dto.RetrieveStageInDto;
import us.tx.state.dfps.service.casepackage.dto.RetrieveStageOutDto;
import us.tx.state.dfps.service.casepackage.dto.RowStageOutDto;

/**
 * 
 * service-business- IMPACT PHASE 2 MODERNIZATION Class
 * Description:CaseMaintenanceRetrieveStageDtlDaoImpl Feb 7, 2018- 5:51:12 PM ©
 * 2017 Texas Department of Family and Protective Services
 */
@Repository
public class CaseMaintenanceRetrieveStageDtlDaoImpl implements CaseMaintenanceRetrieveStageDtlDao {
	@Autowired
	private SessionFactory sessionFactory;

	@Value("${CaseMaintenanceRetrieveStageDtlDaoImpl.strCCMNE1D_CURSORQuery}")
	private transient String strCCMNE1D_CURSORQuery;

	@Value("${CaseMaintenanceRetrieveStageDtlDaoImpl.strCCMNE1D_CURSORQuery_fetchUniqueStageId}")
	private transient String strCCMNE1D_CURSORQuery_fetchUniqueStageId;

	private static final Logger log = Logger.getLogger(CaseMaintenanceRetrieveStageDtlDaoImpl.class);

	/**
	 * Method Name: Method Description:This Method is used to fetch the stage
	 * Dtl DAM: ccmne1d
	 * 
	 * @param retrieveStageInDto
	 * @param retrieveStageOutDto
	 * @
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void fetchStageDtl(RetrieveStageInDto retrieveStageInDto, RetrieveStageOutDto retrieveStageOutDto) {
		log.debug("Entering method fetchStageDtl in CaseMaintenanceRetrieveStageDtlDaoImpl");
		List<Long> uniqueStageId = fetchUniqueStageId(retrieveStageInDto);
		List<RowStageOutDto> rowStageOutDtoList = new ArrayList<>();
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(strCCMNE1D_CURSORQuery)
				.addScalar("idStage", StandardBasicTypes.LONG)
				.addScalar("cdStage", StandardBasicTypes.STRING)
				.addScalar("dtStageClose", StandardBasicTypes.TIMESTAMP)
				.addScalar("dtStageStart", StandardBasicTypes.TIMESTAMP)
				.setParameter("idCase", retrieveStageInDto.getUlIdCase())
				.setResultTransformer(Transformers.aliasToBean(RowStageOutDto.class)));
		if(!CollectionUtils.isEmpty(uniqueStageId)) {
			for(Long idStage : uniqueStageId){
				sQLQuery1.setParameter("idStage", idStage);
				rowStageOutDtoList.addAll(sQLQuery1.list());
			}
		}else {
			log.debug("CaseMaintenanceRetrieveStageDtlDaoImpl fetchStageDtl: Getting only INT open stage");
			sQLQuery1.setParameter("idStage", retrieveStageInDto.getSelectedStageId());
			rowStageOutDtoList.addAll(sQLQuery1.list());
		}

		if(!CollectionUtils.isEmpty(rowStageOutDtoList)){
			retrieveStageOutDto.setROWCCMNE1DO(rowStageOutDtoList);
		}
		log.debug("Exiting method fetchStageDtl in CaseMaintenanceRetrieveStageDtlDaoImpl");
	}

	/**
	 * [artf284553]: This method is going to fetch all the unique stage_id for linked stages.
	 * @param retrieveStageInDto
	 * @return List
	 */
	public List <Long> fetchUniqueStageId(RetrieveStageInDto retrieveStageInDto) {
		log.debug("Entering method fetchUniqueStageId in CaseMaintenanceRetrieveStageDtlDaoImpl");
		SQLQuery sQLQuery1 = ((SQLQuery) sessionFactory.getCurrentSession().createSQLQuery(strCCMNE1D_CURSORQuery_fetchUniqueStageId)
				.addScalar("idStage", StandardBasicTypes.LONG)
				.addScalar("idPriorStage", StandardBasicTypes.LONG)
				.setParameter("idStage", retrieveStageInDto.getSelectedStageId())
				.setParameter("idCase", retrieveStageInDto.getUlIdCase())
				.setResultTransformer(Transformers.aliasToBean(StageLinkStageOutDto.class)));

		List<StageLinkStageOutDto> stageLinkStageOutDtoList =  sQLQuery1.list();
		Set<Long> uniqueStageId = new HashSet<>();
		if(!CollectionUtils.isEmpty(stageLinkStageOutDtoList)){
			for(StageLinkStageOutDto dto : stageLinkStageOutDtoList ){
				if(dto.getIdStage() != null ){
					uniqueStageId.add(dto.getIdStage());
				}
				if(dto.getIdPriorStage() != null ){
					uniqueStageId.add(dto.getIdPriorStage());
				}
			}
		}
		ArrayList<Long> uniqueStageIdList  = new ArrayList<>(uniqueStageId);
		log.debug("Exiting method fetchUniqueStageId in CaseMaintenanceRetrieveStageDtlDaoImpl");
		return uniqueStageIdList;

	}

}
