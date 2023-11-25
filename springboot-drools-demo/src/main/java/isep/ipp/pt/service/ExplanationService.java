/* (C)2023 */
package isep.ipp.pt.service;

import isep.ipp.pt.model.Evidence;
import isep.ipp.pt.model.Evidences;
import isep.ipp.pt.model.Fact;
import isep.ipp.pt.model.How;
import isep.ipp.pt.model.Justification;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.event.DefaultAgendaEventListener;
import org.drools.core.rule.Pattern;
import org.kie.api.definition.rule.Rule;
import org.kie.api.event.rule.AfterMatchFiredEvent;

/**
 * Service to trace all the fired rules to get one conclusion.
 */
public class ExplanationService extends DefaultAgendaEventListener {
    protected List<Fact> lhs = new ArrayList<>();
    public static List<Fact> rhs = new ArrayList<>();
    private Map<Integer, Justification> justifications = new HashMap<>();

    public ArrayList<String> explanations = new ArrayList<>();
    public ArrayList<How> listJustifications = new ArrayList<>();


    public ArrayList<String> getExplanations() {
        return (ArrayList<String>) explanations.clone();
    }

    public String getExplanation(Integer factNumber, Fact fact) {
        return (generateExplanation(factNumber, 0,fact));
    }

    private String generateExplanation(Integer factNumber, int level,Fact fact) {
        StringBuilder sb = new StringBuilder();
        Justification j = justifications.get(factNumber);
        if (j != null) {
            if("unknown".equals(j.getRule())){

                listJustifications.add(How.builder()
                        .lhs(fact.toString())
                        .rule(j.getRule())
                        .conclusion(j.getConclusion()).build());
                sb.append("The data provided could not generate a valid gear. -> " + fact);
            }else{
                // justification for Fact factNumber was found
                listJustifications.add(How.builder()
                        .lhs(j.getLhs().get(0).toString())
                        .rule(j.getRule())
                        .conclusion(j.getConclusion())
                        .build());
                sb.append(formatExplanation(level));
                sb.append(j.getConclusion() + " was obtained by rule " + j.getRule() + " because ");
                sb.append('\n');
                int l = level + 1;
                for (Fact f : j.getLhs()) {
                    sb.append(formatExplanation(l));
                    sb.append(f);
//                sb.append('\n');
//                if (f instanceof Evidences) {
//                    String s = generateExplanation(f.getId(), l + 1);
//                    sb.append(s);
//                }
                }
            }
        }

        return sb.toString();
    }

    @Override
    public void afterMatchFired(AfterMatchFiredEvent event) {
        Rule rule = event.getMatch().getRule();
        String ruleName = rule.getName();

        List<Object> list = event.getMatch().getObjects();
        for (Object e : list) {
            if (e instanceof Evidences) {
                lhs.add((Evidences) e);
            }
            if (e instanceof Evidence) {
                lhs.add((Evidence) e);
            }
        }

        String ruleConstraints = "";
        try {
            ruleConstraints =
                    ((Pattern) ((ArrayList) ((RuleImpl) rule).getLhs().getChildren()).get(0))
                            .getConstraints()
                            .toString();
        } catch (Exception ex) {
            ruleConstraints = ruleName;
        }

        for (Fact fact : rhs) {
            Justification justification =
                    Justification.builder().rule(ruleConstraints).lhs(lhs).conclusion(fact).build();
            justifications.put(fact.getId(), justification);
        }

        // resetLhs();
        // resetRhs();
    }

    private String formatExplanation(int level) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < level; i++) {
            sb.append('\t');
        }
        return sb.toString();
    }

    private void resetLhs() {
        lhs.clear();
    }

    private void resetRhs() {
        rhs.clear();
    }

    public void resetExplanationModule() {
        resetLhs();
        justifications.clear();
        explanations.clear();
        listJustifications.clear();
    }

    public ArrayList<How> getHow() {
        return listJustifications;
    }
}
