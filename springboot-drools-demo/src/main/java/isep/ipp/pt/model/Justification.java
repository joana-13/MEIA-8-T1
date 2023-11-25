/* (C)2023 */
package isep.ipp.pt.model;

import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Builder
@Data
@Getter
public class Justification {
    private String rule;
    private List<Fact> lhs;
    private Fact conclusion;
}
