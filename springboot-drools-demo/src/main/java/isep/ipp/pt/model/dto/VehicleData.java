/* (C)2023 */
package isep.ipp.pt.model.dto;

import isep.ipp.pt.model.Fact;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * Class to store all the variables that knowledge base contains.
 */
@Data
@AllArgsConstructor
@Builder
public class VehicleData extends Fact {
    private Float speed;
    private Float rpm;
    private Float pitch;
    private Float totalAcceleration;
}
