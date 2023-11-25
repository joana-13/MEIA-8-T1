/* (C)2023 */
package isep.ipp.pt.controller;

import isep.ipp.pt.model.Conclusions;
import isep.ipp.pt.model.Evidence;
import isep.ipp.pt.model.How;
import isep.ipp.pt.model.Justification;
import isep.ipp.pt.model.dto.VehicleData;
import isep.ipp.pt.service.ApiService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller to interact with drools inference engine.
 */
@RestController
@CrossOrigin
@RequiredArgsConstructor
@RequestMapping("/api/drools")
public class DroolsController {

    private final ApiService apiService;

    /**
     * Endpoint to interact with engine inference.
     * @param vehicleData
     * @return Conclusion
     */
    @PostMapping
    public ResponseEntity<List<Conclusions>> getPrediction(@RequestBody Evidence vehicleData) {
        List<Conclusions> generatedFacts = apiService.getPrediction(vehicleData);
        return new ResponseEntity<>(generatedFacts, HttpStatus.OK);
    }

    /**
     * Endpoint to interact with engine inference.
     * @param vehicleData
     * @return Conclusion
     */
    @PostMapping("/fuzzy")
    public ResponseEntity<List<Conclusions>> getFuzzyPrediction(@RequestBody VehicleData vehicleData) {
        List<Conclusions> generatedFacts = apiService.getFuzzyPrediction(vehicleData);
        return new ResponseEntity<>(generatedFacts, HttpStatus.OK);
    }

    /**
     * Get current explanation.
     * @return Explanation.
     */
    @GetMapping("/explanations")
    public ResponseEntity<List<String>> getExplanation() {
        List<String> explanationList = apiService.getExplanation();
        return new ResponseEntity<>(explanationList, HttpStatus.OK);
    }

    /**
     * Get how reached a prediction.
     * @return Explanation.
     */
    @GetMapping("/how")
    public ResponseEntity<List<How>> getHow() {
        List<How> explanationList = apiService.getHow();
        return new ResponseEntity<>(explanationList, HttpStatus.OK);
    }
}
