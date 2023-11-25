/* (C)2023 */
package isep.ipp.pt.service;

import isep.ipp.pt.model.Conclusions;
import isep.ipp.pt.model.Evidence;
import isep.ipp.pt.model.Evidences;
import isep.ipp.pt.model.How;
import isep.ipp.pt.model.Justification;
import isep.ipp.pt.model.dto.VehicleData;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.jFuzzyLogic.rule.Rule;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApiService {
    private final InferenceEngine inferenceEngine;
    private final FuzzyLogicService fuzzyLogicEngine;

    /**
     * Call inference engine to get the conclusions. It includes fuzzy logic.
     *
     * @param vehicleData Evidences to get the conclusions.
     * @return The generated rules.
     */
    public List<Conclusions> getFuzzyPrediction(VehicleData vehicleData) {
        // First apply fuzzy logic for speed.
        List<Rule> rulesWithMembershipLevel = fuzzyLogicEngine.startFuzzyEngine(vehicleData);

        Evidences evidence =
                Evidences.builder()
                        .rpm(vehicleData.getRpm())
                        .pitch(vehicleData.getPitch())
                        .totalAcceleration(vehicleData.getTotalAcceleration())
                        .speed(vehicleData.getSpeed())
                        .build();

        for (Rule rule : rulesWithMembershipLevel) {
            String prediction = rule.getConsequents().getLast().toString().split(" ")[2];
            float membershipValue = (float) rule.getDegreeOfSupport();

            switch (prediction) {
                case "gear1":
                    evidence.setFuzzyMembershipGear1(
                            getMaxValue(evidence.getFuzzyMembershipGear1(), membershipValue));
                    break;
                case "gear2":
                    evidence.setFuzzyMembershipGear2(
                            getMaxValue(evidence.getFuzzyMembershipGear2(), membershipValue));
                    break;
                case "gear3":
                    evidence.setFuzzyMembershipGear3(
                            getMaxValue(evidence.getFuzzyMembershipGear3(), membershipValue));
                    break;
                case "gear4":
                    evidence.setFuzzyMembershipGear4(
                            getMaxValue(evidence.getFuzzyMembershipGear4(), membershipValue));
                    break;
                case "gear5":
                    evidence.setFuzzyMembershipGear5(
                            getMaxValue(evidence.getFuzzyMembershipGear5(), membershipValue));
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + prediction);
            }
        }

        // Once we have membership level for each gear, we proceed with normal logic.
        return inferenceEngine.startEngine(evidence);
    }

    /**
     * Call inference engine to get the conclusions.
     *
     * @param evidence Evidences to get the conclusions.
     * @return The generated rules.
     */
    public List<Conclusions> getPrediction(Evidence evidence) {
        return inferenceEngine.startEngine(evidence);
    }

    private float getMaxValue(Float actualMembership, float membershipValue) {
        return (actualMembership != null)
                ? Math.max(actualMembership, membershipValue)
                : membershipValue;
    }

    /**
     * Get explanation for the conclusions.
     * @return Explanation.
     */
    public List<String> getExplanation() {
        return inferenceEngine.getExplanations();
    }

    /**
     * Get explanation for the conclusions.
     * @return Explanation.
     */
    public List<How> getHow() {
        return inferenceEngine.getHow();
    }
}
