package com.sportradar.utils;

import com.sportradar.exception.ErrorCode;
import com.sportradar.exception.ValidationException;

public final class GameValidator {
    private GameValidator() {
        throw new AssertionError("Utility class cannot be instantiated");
    }

    public static void validateTeamNames(String homeTeam, String awayTeam) {
        validateTeamName(homeTeam, "homeTeam", ErrorCode.INVALID_HOME_TEAM);
        validateTeamName(awayTeam, "awayTeam", ErrorCode.INVALID_AWAY_TEAM);

        if (homeTeam.equalsIgnoreCase(awayTeam)) {
            throw new ValidationException(
                    "Home and away teams must be different",
                    ErrorCode.DUPLICATE_TEAM_NAMES,
                    "teams"
            );
        }
    }

    private static void validateTeamName(String teamName, String fieldName, ErrorCode errorCode) {
        if (teamName == null || teamName.isBlank()) {
            throw new ValidationException(
                    "Team name cannot be null or blank",
                    errorCode,
                    fieldName
            );
        }
    }

    public static void validateScores(int homeScore, int awayScore) {
        if (homeScore < 0) {
            throw new ValidationException(
                    "Home score cannot be negative: %d".formatted(homeScore),
                    ErrorCode.INVALID_HOME_SCORE,
                    "homeScore"
            );
        }
        if (awayScore < 0) {
            throw new ValidationException(
                    "Away score cannot be negative: %d".formatted(awayScore),
                    ErrorCode.INVALID_AWAY_SCORE,
                    "awayScore"
            );
        }
    }
}