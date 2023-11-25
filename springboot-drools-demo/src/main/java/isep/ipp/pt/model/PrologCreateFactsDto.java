/* (C)2023 */
package isep.ipp.pt.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class PrologCreateFactsDto {
    private String velocity;
    private String rpm;
    private String acceleration;
    private String pitch;
    private String n;
}
