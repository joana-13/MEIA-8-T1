/* (C)2023 */
package isep.ipp.pt.controller;

import isep.ipp.pt.model.PrologCreateFactsDto;
import isep.ipp.pt.model.PrologHowDto;
import isep.ipp.pt.model.PrologStartEngineDto;
import isep.ipp.pt.model.PrologWhyNotDto;
import isep.ipp.pt.model.PrologWhyNotResponseDto;
import isep.ipp.pt.service.PrologService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller to interact with drools inference engine.
 */
@RestController
@CrossOrigin
@RequiredArgsConstructor
@RequestMapping("/api/prolog")
public class PrologController {

    private final PrologService prologService;

    /**
     * Create facts.
     * @param prologCreateFactsDto
     * @return Conclusion
     */
    @PostMapping("/createFacts")
    public ResponseEntity<String> createFacts(
            @RequestBody PrologCreateFactsDto prologCreateFactsDto) {
        String facts = prologService.createFacts(prologCreateFactsDto).block();
        return new ResponseEntity<>(facts, HttpStatus.OK);
    }

    /**
     * Start engine.
     * @return Explanation.
     */
    @GetMapping("/startEngine")
    public ResponseEntity<PrologStartEngineDto> startEngine() {
        try{
            prologService.startEngine().block();
        }catch (Exception ex){}

        PrologStartEngineDto explanationList = prologService.startEngine().block();
        return new ResponseEntity<>(explanationList, HttpStatus.OK);
    }

    /**
     * Start engine.
     * @return Explanation.
     */
    @GetMapping("/how/{id}")
    public ResponseEntity<PrologHowDto> getHow(@PathVariable("id") int id) {
        PrologHowDto explanationList = prologService.getHow(id).block();
        return new ResponseEntity<>(explanationList, HttpStatus.OK);
    }

    /**
     * Get why not module.
     * @param prologWhyNotDto
     * @return Conclusion
     */
    @PostMapping("/whynot")
    public ResponseEntity<PrologWhyNotResponseDto> whyNot(
            @RequestBody PrologWhyNotDto prologWhyNotDto) {
        PrologWhyNotResponseDto facts = prologService.whyNot(prologWhyNotDto).block();
        return new ResponseEntity<>(facts, HttpStatus.OK);
    }

}
