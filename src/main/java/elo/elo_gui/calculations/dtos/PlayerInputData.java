package elo.elo_gui.calculations.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PlayerInputData {
    private String discordName;
    private String currentFaceit;

    @Override
    public String toString() {
        return discordName + "(" + currentFaceit + ") ";
    }
}
