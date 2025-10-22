package com.sportradar.service;

import com.sportradar.domain.Game;

import java.util.List;

public interface GameService {
    Game startGame(String homeTeam, String awayTeam);

    void finishGame(String homeTeam, String awayTeam);

    Game updateScore(String homeTeam, String awayTeam, int homeTeamScore, int awayTeamScore);

    List<Game> getSummaryByTotalScoreAndReverseOrder();
}
