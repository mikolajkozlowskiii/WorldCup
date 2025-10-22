package com.sportradar.repository;

import com.sportradar.domain.Game;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
public class InMemoryGameRepository implements GameRepository {

    private final List<Game> games;

    @Override
    public Game save(Game game) {
        games.add(game);
        return game;
    }

    @Override
    public Optional<Game> findByTeams(String homeTeam, String awayTeam) {
        return games.stream()
                .filter(game -> game.homeTeam().equals(homeTeam) && game.awayTeam().equals(awayTeam))
                .findFirst();
    }

    @Override
    public void delete(Game game)  {
       games.remove(game);
    }

    @Override
    public List<Game> findAll() {
        return games;
    }
}