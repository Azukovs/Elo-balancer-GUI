package elo.elo_gui.calculations.dtos;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PlayerConnection {
    PlayerInputData p1;
    PlayerInputData p2;

    public PlayerInputData teamMateExists(PlayerInputData player) {
        if (p1.equals(player)) {
            return p2;
        }
        if (p2.equals(player)) {
            return p1;
        }
        return null;
    }
}
