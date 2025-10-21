package com.sportradar.domain;

public record Game(
        String homeTeam,
        String awayTeam,
        int homeScore,
        int awayScore
) {

    private static final String TEAM_NAME_PATTERN = "^[A-Za-z ]+$";

    public Game {
        if (homeTeam == null || homeTeam.isBlank()) {
            throw new IllegalArgumentException("Home team cannot be null or blank");
        }
        if (awayTeam == null || awayTeam.isBlank()) {
            throw new IllegalArgumentException("Away team cannot be null or blank");
        }

        if (homeTeam.equalsIgnoreCase(awayTeam)) {
            throw new IllegalArgumentException("Home and away teams must be different");
        }

        if (!homeTeam.matches(TEAM_NAME_PATTERN)) {
            throw new IllegalArgumentException("Invalid home team name: " + homeTeam);
        }
        if (!awayTeam.matches(TEAM_NAME_PATTERN)) {
            throw new IllegalArgumentException("Invalid away team name: " + awayTeam);
        }

        if (homeScore < 0 || awayScore < 0) {
            throw new IllegalArgumentException("Scores cannot be negative");
        }
    }

    public static Game createNew(String homeTeam, String awayTeam) {
        return new Game(homeTeam, awayTeam, 0, 0);
    }

    public static void createWithScores(String homeTeam, String awayTeam, int homeScore, int awayScore) {
        new Game(homeTeam, awayTeam, homeScore, awayScore);
    }

    public int getTotalScore() {
        return homeScore + awayScore;
    }
}