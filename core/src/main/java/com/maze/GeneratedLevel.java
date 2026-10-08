package com.maze;

public record GeneratedLevel(
        String[] layout,
        Cell player,
        Cell enemy,
        Cell exit,
        Cell fruit
) {
}
