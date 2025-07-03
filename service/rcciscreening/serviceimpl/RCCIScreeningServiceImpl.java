package us.tx.state.dfps.service.rcciscreening.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;
import us.tx.state.dfps.service.forms.util.RCCIScreeningPrefillData;
import us.tx.state.dfps.service.rcciscreening.dao.RCCIScreeningDao;
import us.tx.state.dfps.service.rcciscreening.service.RCCIScreeningService;

@Service
@Transactional
public class RCCIScreeningServiceImpl implements RCCIScreeningService {

    @Autowired
    RCCIScreeningDao rcciScreeningDao;

    @Autowired
    private RCCIScreeningPrefillData prefillData;

    /**
     * Method Name: getIntakeData Method Description:This method is used to
     * retrieve the intake data.
     *
     * @param idCase
     *
     * @return RCCIScreeningDto
     */
    @Override
    public PreFillDataServiceDto getIntakeData(long idCase) {
        return prefillData.returnPrefillData(rcciScreeningDao.getIntakeData(idCase));
    }
}
