package com.sportradar.repository;

import com.sportradar.domain.Game;

import java.util.List;
import java.util.Optional;

public interface GameRepository {
    Game save(Game game);
    Optional<Game> findByTeams(String homeTeam, String awayTeam);
    void delete(Game game);
    List<Game> findAll();
}
