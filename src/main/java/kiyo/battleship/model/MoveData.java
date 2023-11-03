package kiyo.battleship.model;

import java.util.List;

/**
 * Stores data of the moves made by the players
 *
 * @param damageByP1 Shots that hit, done by first player
 * @param missedByP1 Shots that missed, done by first player
 * @param damageByP2 Shots that hit, done by second player
 * @param missedByP2 Shots that missed, done by second player
 */
public record MoveData(List<Coord> damageByP1, List<Coord> missedByP1, List<Coord> damageByP2,
                       List<Coord> missedByP2) {
}
