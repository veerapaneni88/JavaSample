package us.tx.state.dfps.service.SDM.serviceimpl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import us.tx.state.dfps.service.SDM.SDMHouseHoldDto;
import us.tx.state.dfps.service.SDM.dao.SDMDao;
import us.tx.state.dfps.service.SDM.service.SDMService;
import us.tx.state.dfps.service.common.dao.EventPersonLinkDao;
import us.tx.state.dfps.service.common.dao.StageDao;
import us.tx.state.dfps.service.common.request.CommonCaseIdReq;
import us.tx.state.dfps.service.common.response.SDMHoseHoldRes;
import us.tx.state.dfps.service.workload.dto.StageDto;

@Service
public class SDMServiceImpl implements SDMService {

	@Autowired
	SDMDao sDMDao;

	@Autowired
	EventPersonLinkDao eventPersonLinkDao;
    @Autowired
    StageDao stageDao;

	private static final Logger log = Logger.getLogger(SDMServiceImpl.class);

	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
			Exception.class })
	public SDMHoseHoldRes getSDMComplHouseHold(CommonCaseIdReq caseIdReq) {
		log.info("TransactionId :" + caseIdReq.getTransactionId());
        StageDto stage=stageDao.getStageById(caseIdReq.getStageId());
        List<SDMHouseHoldDto> houseHoldList = new ArrayList<>();
       // [artf171225] Added closed stage check to display all households list for open stages
        if(stage.getDtStageClose()==null){
            List<SDMHouseHoldDto> allHouseHoldList = sDMDao.getSDMComplHouseHold(caseIdReq,false);
            // collect unique list idPersons
            List<Long> idPersonList = allHouseHoldList.stream().map(o -> o.getIdPerson()).collect(Collectors.toSet())
                    .stream().collect(Collectors.toList());

            // for each person in unique list get latest assessment
            for (Long idPerson : idPersonList) {
                // Defect#5417 : Changed the criteria for getting latest sdm for
                // household on the basis of Dt_Assessed. Modified the query to
                // fetch the sdm in sorted order.
                // SDMHouseHoldDto s1 = allHouseHoldList.stream().filter(o ->
                // idPerson.equals(o.getIdPerson())).max(Comparator.comparingDouble(SDMHouseHoldDto::getIdAsmntCpsSa)).orElse(null);
                SDMHouseHoldDto s1 = allHouseHoldList.stream().filter(o -> idPerson.equals(o.getIdPerson())).findFirst()
                        .orElse(null);
                s1.setCareGiverList(eventPersonLinkDao.getPrsnListByEventId(s1.getIdEvent()));
                houseHoldList.add(s1);
            }
            //[artf171225] changed the criteria from collecting unique person list to unique stages list Of the stage is closed
        }else{
            List<SDMHouseHoldDto> allHouseHoldList = sDMDao.getSDMComplHouseHold(caseIdReq,true);
            Map<Long, List<SDMHouseHoldDto>> stagesList = allHouseHoldList.stream().collect(
                    Collectors.groupingBy(SDMHouseHoldDto::getIdStage));

            for(Map.Entry<Long,List<SDMHouseHoldDto>> entry : stagesList.entrySet()){
                SDMHouseHoldDto s1 = entry.getValue().get(0);
                s1.setCareGiverList(eventPersonLinkDao.getPrsnListByEventId(s1.getIdEvent()));
                houseHoldList.add(s1);
            }
        }


        Iterator<SDMHouseHoldDto> i = houseHoldList.iterator();
        while (i.hasNext()) {
            SDMHouseHoldDto e = (SDMHouseHoldDto) i.next();
            //ALM ID : 16716 : Risk Level should be from Risk Assessments corresponding to safety assessment stage
            String risk = eventPersonLinkDao.getFinalRisk(e.getIdPerson(), e.getIdStage());
            Long idAssmntHsld = eventPersonLinkDao.getidAssmntHousehold(e.getIdAsmntCpsSa());
            if (null != risk) {
                e.setFinalRiskLevel(risk);
            }
            if (null != idAssmntHsld) {
                e.setIdHousehold(idAssmntHsld);
            }
        }
        SDMHoseHoldRes sDMHoseHoldRes = new SDMHoseHoldRes();
        sDMHoseHoldRes.setHouseHoldList(houseHoldList);
        return sDMHoseHoldRes;
    }

	@Override
	@Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED, rollbackFor = {
			Exception.class })
	public SDMHoseHoldRes getSDMHouseHold(CommonCaseIdReq commonCaseIdReq) {
		List<SDMHouseHoldDto> allHouseHoldList = sDMDao.getSDMHouseHold(commonCaseIdReq);
		for (SDMHouseHoldDto s : allHouseHoldList)
			s.setCareGiverList(eventPersonLinkDao.getPrsnListByEventId(s.getIdEvent()));
		SDMHoseHoldRes sDMHoseHoldRes = new SDMHoseHoldRes();
		sDMHoseHoldRes.setHouseHoldList(allHouseHoldList);
		return sDMHoseHoldRes;
	}
}
