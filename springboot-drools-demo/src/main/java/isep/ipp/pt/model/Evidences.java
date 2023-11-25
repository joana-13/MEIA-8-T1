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
public class Evidences extends Fact {
    private Float fuzzyMembershipGear1;
    private Float fuzzyMembershipGear2;
    private Float fuzzyMembershipGear3;
    private Float fuzzyMembershipGear4;
    private Float fuzzyMembershipGear5;
    private Float rpm;
    private Float speed;
    private Float pitch;
    private Float totalAcceleration;

    @Override
    public String toString() {
        return " speed was " + speed +
                " km/h with rpm around " + rpm +
                ", pitch " + pitch +
                " and total acceleration set " + totalAcceleration +
                ". " +
                "Based on Fuzzy logic speed " + speed + "km/h has membership value of " + fuzzyMembershipGear1 + " for Gear1 \n " +
                "\t and has membership value of " + fuzzyMembershipGear2 + " for Gear 2. \n" +
                "\t and has membership value of " + fuzzyMembershipGear3 + " for Gear 3. \n" +
                "\t and has membership value of " + fuzzyMembershipGear4 + " for Gear 4. \n" +
                "\t and has membership value of " + fuzzyMembershipGear5 + " for Gear 5. \n" + + '}';
    }
}
