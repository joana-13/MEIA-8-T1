/* (C)2023 */
package isep.ipp.pt.service;

import java.util.HashMap;
import java.util.List;

import isep.ipp.pt.model.dto.VehicleData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.plot.JFuzzyChart;
import net.sourceforge.jFuzzyLogic.rule.LinguisticTerm;
import net.sourceforge.jFuzzyLogic.rule.Rule;
import net.sourceforge.jFuzzyLogic.rule.Variable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class FuzzyLogicService {
    private final FIS fuzzyInference;

    public List<Rule> startFuzzyEngine(VehicleData evidences) {
        return startFuzzyEngine(
                "gear_selector",
                Math.round(evidences.getSpeed()),
                Math.round(evidences.getRpm()),
                "gear");
    }

    /**
     * Calculate level of membership of each gear based on input speed.
     * @param functionBlockName Function block name that must be defined on fuzzy_rules file(i.e: gearCalculator).
     * @param outputVariableName Output variable name (i.e: gear).
     * @param speedValue Input speed value to be evaluated.
     * @param rpmValue Input rpm value to be evaluated.
     */
    public List<Rule> startFuzzyEngine(
            String functionBlockName,
            Integer speedValue,
            Integer rpmValue,
            String outputVariableName
    ) {
        // Show
        JFuzzyChart.get().chart(fuzzyInference);

        // Set inputs and start fuzzy inference
        fuzzyInference.setVariable("speed", speedValue);
        fuzzyInference.setVariable("rpm", rpmValue);
        fuzzyInference.evaluate();

        // Show output variable's chart
        Variable gear = fuzzyInference.getVariable(outputVariableName);
        JFuzzyChart.get().chart(gear, gear.getDefuzzifier(), true);

        // Show output variable value
        log.info("Output value: " + gear.defuzzify());
        log.debug(fuzzyInference.toString());

        // Show membership values for input variables
        printFuzzification(functionBlockName, "speed");
        printFuzzification(functionBlockName, "rpm");

        // Show each rule (and degree of support)
        List<Rule> rulesList =
                fuzzyInference
                        .getFunctionBlock(functionBlockName)
                        .getFuzzyRuleBlock("GearSelection")
                        .getRules();
        printResults(rulesList);
        return rulesList;
    }

    private void printFuzzification(String functionBlock, String variable) {
        log.info("Fuzzyfication:");
        Variable v = fuzzyInference.getFunctionBlock(functionBlock).getVariable(variable);
        log.info(v.getName());
        HashMap<String, LinguisticTerm> linguisticTerms = v.getLinguisticTerms();
        for (String linguisticTerm : linguisticTerms.keySet()) {
            log.info("\t {} : {}", linguisticTerm, v.getMembership(linguisticTerm));
        }
    }

    private void printResults(List<Rule> rulesList) {
        log.info("Level of membership for each gear based on input speed:");
        for (Rule r : rulesList) {
            log.info("{}", r);
        }
    }
}
