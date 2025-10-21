package com.sportradar.utils;

import com.sportradar.exception.GameValidationException;

public final class GameValidator {
    private GameValidator() {
        throw new AssertionError("Utility class cannot be instantiated");
    }

    public static void validateTeamNames(String homeTeam, String awayTeam) {
        validateTeamName(homeTeam, "homeTeam");
        validateTeamName(awayTeam, "awayTeam");

        if (homeTeam.equalsIgnoreCase(awayTeam)) {
            throw new GameValidationException(
                    "Home and away teams must be different",
                    "teams"
            );
        }
    }

    private static void validateTeamName(String teamName, String fieldName) {
        if (teamName == null || teamName.isBlank()) {
            throw new GameValidationException(
                    "Team name cannot be null or blank",
                    fieldName
            );
        }
    }

    public static void validateScores(int homeScore, int awayScore) {
        if (homeScore < 0) {
            throw new GameValidationException(
                    "Home score cannot be negative: %d".formatted(homeScore),
                    "homeScore"
            );
        }
        if (awayScore < 0) {
            throw new GameValidationException(
                    "Away score cannot be negative: %d".formatted(awayScore),
                    "awayScore"
            );
        }
    }
}