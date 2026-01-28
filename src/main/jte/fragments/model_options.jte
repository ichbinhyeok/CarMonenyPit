@import com.carmoneypit.engine.service.CarDataService.CarModel
@import java.util.List

@param List<CarModel> models

<option value="">Select specific model (Recommended)</option>
@for(var model : models)
    <option value="${model.model()}">${model.model()} (${model.startYear()} - ${model.endYear()})</option>
@endfor
<option value="other">Other / Not Listed</option>
