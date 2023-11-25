/* (C)2023 */
package isep.ipp.pt.configuration;

import lombok.extern.slf4j.Slf4j;
import net.sourceforge.jFuzzyLogic.FIS;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieModule;
import org.kie.api.runtime.KieContainer;
import org.kie.internal.io.ResourceFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class InferenceEngineConfig {

    @Value("${isep.ipp.pt.kie-server.fuzzy-rules-files:rules/rules2.drl}")
    String rulesWithMembershipFile;

    @Value("${isep.ipp.pt.kie-server.original-rules-files:rules/rules.drl}")
    String originalRulesFile;

    @Value("${isep.ipp.pt.fuzzy.rules-files:src/main/resources/rules/fuzzy_rules.fcl}")
    String fuzzyRulesFile;

    @Bean
    public KieContainer normalContainer() {
        return buildKieContainer(originalRulesFile);
    }

    @Bean
    public KieContainer fuzzyContainer() {
        return buildKieContainer(rulesWithMembershipFile);
    }

    private KieContainer buildKieContainer(String rulesPathFile){
        final KieServices kieServices = KieServices.Factory.get();
        KieFileSystem kieFileSystem = kieServices.newKieFileSystem();
        kieFileSystem.write(ResourceFactory.newClassPathResource(rulesPathFile));
        KieBuilder kb = kieServices.newKieBuilder(kieFileSystem);
        kb.buildAll();
        KieModule kieModule = kb.getKieModule();
        return kieServices.newKieContainer(kieModule.getReleaseId());
    }

    @Bean
    public FIS FuzzyInferenceEngine() {
        // Load from 'FCL' file
        FIS fis = FIS.load(fuzzyRulesFile, true);
        if (fis == null) {
            throw new IllegalStateException("Can't load file: '" + fuzzyRulesFile + "'");
        }
        return fis;
    }
}
