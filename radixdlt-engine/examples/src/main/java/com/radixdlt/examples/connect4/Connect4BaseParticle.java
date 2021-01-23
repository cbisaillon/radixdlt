package com.radixdlt.examples.connect4;

import com.google.common.collect.ImmutableList;
import com.radixdlt.constraintmachine.Particle;
import com.radixdlt.identifiers.RadixAddress;

import java.util.Objects;

abstract class Connect4BaseParticle extends Particle {
    enum Connect4BoardValues {YELLOW, RED, EMPTY};

    private final RadixAddress redPlayer;
    private final RadixAddress yellowPlayer;

    private final ImmutableList<Connect4BoardValues> board;

    public Connect4BaseParticle(RadixAddress redPlayer, RadixAddress yellowPlayer, ImmutableList<Connect4BoardValues> board){
        this.redPlayer = redPlayer;
        this.yellowPlayer = yellowPlayer;
        this.board = board;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Connect4BaseParticle that = (Connect4BaseParticle) o;
        return Objects.equals(yellowPlayer, that.yellowPlayer)
                && Objects.equals(redPlayer, that.redPlayer)
                && Objects.equals(board, that.board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(yellowPlayer, redPlayer, board);
    }
}
