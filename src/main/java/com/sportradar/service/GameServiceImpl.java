package com.sportradar.service;

import com.sportradar.domain.Game;
import com.sportradar.exception.GameAlreadyExistsException;
import com.sportradar.exception.GameNotFoundException;
import com.sportradar.repository.GameRepository;
import com.sportradar.utils.GameValidator;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@RequiredArgsConstructor
public class GameServiceImpl implements GameService{

    private final GameRepository gameRepository;

    @Override
    public Game startGame(String homeTeam, String awayTeam) {
        GameValidator.validateTeamNames(homeTeam, awayTeam);

        gameRepository.findByTeams(homeTeam, awayTeam).ifPresent(game -> {
            throw new GameAlreadyExistsException("Game between " + homeTeam + " and " + awayTeam + " is already in progress.");
        });

        return gameRepository.save(Game.createNew(homeTeam, awayTeam));
    }

    @Override
    public void finishGame(String homeTeam, String awayTeam) {
        GameValidator.validateTeamNames(homeTeam, awayTeam);

        final Game game = findGame(homeTeam, awayTeam);

        gameRepository.delete(game);
    }

    @Override
    public Game updateScore(String homeTeam, String awayTeam, int homeTeamScore, int awayTeamScore) {
        GameValidator.validateTeamNames(homeTeam, awayTeam);
        GameValidator.validateScores(homeTeamScore, awayTeamScore);

        final Game oldGame = findGame(homeTeam, awayTeam);

        return gameRepository.update(oldGame.updateScores(homeTeamScore, awayTeamScore));
    }

    @Override
    public List<Game> getSummaryByTotalScoreAndReverseOrder() {
        final List<Game> games = gameRepository.findAll();
        Collections.reverse(games);
        games.sort(Comparator.comparingInt(Game::getTotalScore).reversed());
        return games;
    }

    private Game findGame(String homeTeam, String awayTeam) {
        return gameRepository.findByTeams(homeTeam, awayTeam)
                .orElseThrow(() -> new GameNotFoundException("Game between " + homeTeam + " and " + awayTeam + " not found."));
    }
}
