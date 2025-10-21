package com.sportradar.domain;

public record Game(
        String homeTeam,
        String awayTeam,
        int homeScore,
        int awayScore
) {
    public static Game createNew(String homeTeam, String awayTeam) {
        return new Game(homeTeam, awayTeam, 0, 0);
    }

    public static Game createWithScores(String homeTeam, String awayTeam, int homeScore, int awayScore) {
        return new Game(homeTeam, awayTeam, homeScore, awayScore);
    }

    public Game updateScores(int newHomeScore, int newAwayScore) {
        return new Game(
                this.homeTeam,
                this.awayTeam,
                newHomeScore,
                newAwayScore
        );
    }

    public int getTotalScore() {
        return homeScore + awayScore;
    }

}