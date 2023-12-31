/* (C)2023 */
package isep.ipp.pt.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import isep.ipp.pt.service.ExplanationService;
import lombok.Data;

/**
 * Class to store conclusions generated by inference engine.
 */
@Data
public class Conclusions extends Fact {
    public static String GEAR_1 = "GEAR_1";
    public static String GEAR_2 = "GEAR_2";
    public static String GEAR_3 = "GEAR_3";
    public static String GEAR_4 = "GEAR_4";
    public static String GEAR_5 = "GEAR_5";
    public static String UNKNOWN = "UNKNOWN";

    public final String description;

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    public float probability;

    public Conclusions(String description) {
        this.description = description;
        ExplanationService.rhs.add(this);
    }

    @Override
    public String toString() {
        return "Prediction is " + description;
    }
}
