package us.tx.state.dfps.service.fahomestudy.service;

import us.tx.state.dfps.service.common.request.FAHomeStudyReq;
import us.tx.state.dfps.service.forms.dto.PreFillDataServiceDto;

public interface FAHomeStudyService {

    PreFillDataServiceDto getHomeStudyDetail(FAHomeStudyReq faHomeStudyReq);
}
