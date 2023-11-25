package org.engcia;

import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.plot.JFuzzyChart;
import net.sourceforge.jFuzzyLogic.rule.LinguisticTerm;
import net.sourceforge.jFuzzyLogic.rule.Variable;
import net.sourceforge.jFuzzyLogic.rule.Rule;

import java.util.HashMap;

//import net.sourceforge.jFuzzyLogic.rule.FuzzyRuleSet;

public class Main {

    public static void main(String[] args) {
        // Load from 'FCL' file
        String fileName = "src/fcl/gears.fcl";
        FIS fis = FIS.load(fileName,true);

        // Error while loading?
        if( fis == null ) {
            System.err.println("Can't load file: '" + fileName + "'");
            return;
        }

        // Show
        JFuzzyChart.get().chart(fis);

        // Set inputs
        fis.setVariable("speed", 30);
        fis.setVariable("rpm", 2500);

        // Evaluate
        fis.evaluate();

        // Show output variable's chart
        Variable gear = fis.getVariable("gear");
        JFuzzyChart.get().chart(gear, gear.getDefuzzifier(), true);

        // Show output variable value
        System.out.println("Output value: " + gear.defuzzify());

        // Print ruleSet
        //System.out.println(fis);

        // Show membership values for input variables
        System.out.println("Fuzzyfication:");
        Variable v;
        HashMap<String, LinguisticTerm> linguisticTerms;
        v = fis.getFunctionBlock("gear_selector").getVariable("speed");
        System.out.println(v.getName());
        linguisticTerms = v.getLinguisticTerms();
        for( String linguisticTerm: linguisticTerms.keySet()) {
            System.out.println("\t" + linguisticTerm + " : " + v.getMembership(linguisticTerm));
        }
        v = fis.getFunctionBlock("gear_selector").getVariable("rpm");
        System.out.println(v.getName());
        linguisticTerms = v.getLinguisticTerms();
        for( String linguisticTerm: linguisticTerms.keySet()) {
            System.out.println("\t" + linguisticTerm + " : " + v.getMembership(linguisticTerm));
        }

        // Show each rule (and degree of support)
        System.out.println("\nInference of gear:");
        for(Rule r: fis.getFunctionBlock("gear_selector").getFuzzyRuleBlock("GearSelection").getRules() ) {
            // Format the degree of support to two decimal places
            java.text.DecimalFormat df = new java.text.DecimalFormat("#.##"); // Two decimal places
            String formattedDegreeOfSupport = df.format(r.getDegreeOfSupport());
            r.getConsequents().getLast();
            if(r.getDegreeOfSupport() > 0.1){

                System.out.println(formattedDegreeOfSupport + " " + r.getConsequents().getLast().toString().split("IS")[1]);
            }
        }
    }
}
