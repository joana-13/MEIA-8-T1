/* (C)2023 */
package isep.ipp.pt.model;

import lombok.Getter;

public class Fact {
    private static int lastId = 0;
    @Getter private int id;

    public Fact() {
        Fact.lastId++;
        this.id = lastId;
    }
}
