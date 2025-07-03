package us.tx.state.dfps.service.dangerindicators.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import us.tx.state.dfps.service.dangerindicators.dao.DangerIndicatorsDao;
import us.tx.state.dfps.service.dangerindicators.dto.DangerIndicatorsDto;
import us.tx.state.dfps.service.dangerindicators.service.DangerIndicatorsService;

@Service
@Transactional
public class DangerIndicatorsServiceImpl implements DangerIndicatorsService {

    @Autowired
    private DangerIndicatorsDao dangerIndicatorsDao;
    
    @Override
    public DangerIndicatorsDto getDangerIndicator(Long stageId) {
        return dangerIndicatorsDao.getDangerIndicator(stageId);
    }
}
