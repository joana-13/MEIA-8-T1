package isep.ipp.pt.model;

import lombok.Builder;
import lombok.Data;


@Builder
@Data
public class How {
    private String rule;
    private String lhs;
    private Fact conclusion;
}
