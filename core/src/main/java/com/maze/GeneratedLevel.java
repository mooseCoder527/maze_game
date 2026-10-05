package com.maze;

import java.util.Objects;

public record GeneratedLevel(
        String[] layout,
        Cell player,
        Cell enemy,
        Cell exit,
        Cell fruit
) {
    public GeneratedLevel {
        layout = Objects.requireNonNull(layout).clone();
        Objects.requireNonNull(player);
        Objects.requireNonNull(enemy);
        Objects.requireNonNull(exit);
        Objects.requireNonNull(fruit);
    }

    @Override
    public String[] layout() {
        return layout.clone();
    }
}
