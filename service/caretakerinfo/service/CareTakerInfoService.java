package us.tx.state.dfps.service.caretakerinfo.service;

import org.springframework.stereotype.Repository;

import us.tx.state.dfps.service.common.request.CaretakerInformationReq;
import us.tx.state.dfps.service.common.response.CaretakerInformationRes;

@Repository
public interface CareTakerInfoService {

	public CaretakerInformationRes getCaretakerInfo(CaretakerInformationReq careTakerInfoReq);

	public CaretakerInformationRes saveCareTakerInfo(CaretakerInformationReq careTakerInfoReq);

	public CaretakerInformationRes deleteCareTaker(CaretakerInformationReq careTakerInfoReq);

}
