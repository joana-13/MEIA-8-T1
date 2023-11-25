/* (C)2023 */
package isep.ipp.pt.service;

import isep.ipp.pt.model.Conclusions;
import isep.ipp.pt.model.Evidences;
import isep.ipp.pt.model.Fact;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import isep.ipp.pt.model.How;
import isep.ipp.pt.model.Justification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.LiveQuery;
import org.kie.api.runtime.rule.Row;
import org.kie.api.runtime.rule.ViewChangedEventListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class InferenceEngine {
    private final KieContainer normalContainer;

    private final KieContainer fuzzyContainer;

    private Map<String, Conclusions> conclusions = new HashMap<>();
    public ExplanationService explanationService = new ExplanationService();

    /**
     * Apply configuration to inference engine.
     * @return KieSession.
     */
    public KieSession configureInferenceEngine(KieContainer kieContainer, Fact fact) {
        // Start new session
        KieSession kieSession = kieContainer.newKieSession();

        // Add explanation module
        kieSession.addEventListener(explanationService);
        // kieSession.setGlobal("evidences", evidences);

        // Define Query Listener to stop engine as soon as we got a conclusion
        ViewChangedEventListener listener =
                new ViewChangedEventListener() {
                    @Override
                    public void rowDeleted(Row row) {}

                    @Override
                    public void rowInserted(Row row) {
                        Conclusions conclusion = (Conclusions) row.get("$conclusion");
                        conclusions.put(conclusion.description, conclusion);
                        explanationService.explanations.add(
                                explanationService.getExplanation(conclusion.getId(), fact));
                        //                        kieSession.halt();
                    }

                    @Override
                    public void rowUpdated(Row row) {}
                };
        LiveQuery query = kieSession.openLiveQuery("Conclusions", null, listener);
        return kieSession;
    }

    public List<Conclusions> startEngine(Fact evidences) {
        explanationService.resetExplanationModule();
        KieSession kieSession =
                (evidences instanceof Evidences)
                        ? configureInferenceEngine(fuzzyContainer, evidences)
                        : configureInferenceEngine(normalContainer, evidences);
        kieSession.insert(evidences);
        kieSession.fireAllRules();
        List<Conclusions> generatedSolution = retrieveSolution();
        kieSession.dispose();
        conclusions = new HashMap<>();
        return generatedSolution;
    }

    /**
     * Return the generated facts in a specific kie session.
     *
     * @return Generated facts.
     */
    private List<Conclusions> retrieveSolution() {
        return new ArrayList<>(this.conclusions.values());
    }

    public List<String> getExplanations() {
        List<String> explanationsList = explanationService.getExplanations();
        explanationService.resetExplanationModule();
        return explanationsList;
    }

    public List<How> getHow() {
        List<How> explanationsList = (ArrayList)explanationService.getHow().clone();
        explanationService.resetExplanationModule();
        return explanationsList;
    }
}
