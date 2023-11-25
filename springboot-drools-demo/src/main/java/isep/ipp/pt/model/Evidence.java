/* (C)2023 */
package isep.ipp.pt.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * Class to store all the variables that knowledge base contains.
 */
@Data
@AllArgsConstructor
@Builder
public class Evidence extends Fact {
    private Float speed;
    private Float rpm;
    private Float pitch;
    private Float totalAcceleration;

    @Override
    public String toString() {
        return "speed was " + speed +
                " km/ h with rpm around " + rpm +
                ", pitch " + pitch +
                " and total acceleration set " + totalAcceleration +
                '}';
    }
}
