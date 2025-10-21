package com.sportradar.repository;

import com.sportradar.domain.Game;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("InMemoryGameRepository Tests")
class InMemoryGameRepositoryTest {

    private GameRepository repository;
    private List<Game> gameStorage;

    private static final String HOME_TEAM = "Poland";
    private static final String AWAY_TEAM = "Argentina";

    @BeforeEach
    void setUp() {
        gameStorage = new ArrayList<>();
        repository = new InMemoryGameRepository(gameStorage);
    }

    @Test
    @DisplayName("Should save game successfully")
    void shouldSaveGame() {
        // Given
        final Game game = Game.createNew(HOME_TEAM, AWAY_TEAM);

        // When
        repository.save(game);

        // Then
        assertThat(gameStorage).hasSize(1);
        assertThat(gameStorage.get(0)).isSameAs(game);
    }

    @Test
    @DisplayName("Should save multiple games successfully")
    void shouldSaveMultipleGames() {
        // Given
        final Game game1 = Game.createNew("Mexico", "Canada");
        final Game game2 = Game.createNew("Spain", "Brazil");
        final Game game3 = Game.createNew("Germany", "France");

        // When
        repository.save(game1);
        repository.save(game2);
        repository.save(game3);

        // Then
        assertThat(gameStorage).containsExactly(game1, game2, game3);
    }

    @Test
    @DisplayName("Should find game by teams when game exists")
    void shouldFindGameByTeamsWhenExists() {
        // Given
        final Game game = Game.createNew(HOME_TEAM, AWAY_TEAM);
        repository.save(game);

        // When
        final Optional<Game> foundGame = repository.findByTeams(HOME_TEAM, AWAY_TEAM);

        // Then
        assertThat(foundGame).containsSame(game);
        assertThat(foundGame.get().homeTeam()).isEqualTo(HOME_TEAM);
        assertThat(foundGame.get().awayTeam()).isEqualTo(AWAY_TEAM);
    }

    @Test
    @DisplayName("Should return empty Optional when game not found by teams")
    void shouldReturnEmptyWhenGameNotFound() {
        // Given
        final Game game = Game.createNew("Mexico", "Canada");
        repository.save(game);

        // When
        final Optional<Game> foundGame = repository.findByTeams("Spain", "Brazil");

        // Then
        assertThat(foundGame).isEmpty();
    }

    @Test
    @DisplayName("Should find correct game when multiple games exist")
    void shouldFindCorrectGameAmongMultiple() {
        // Given
        final Game game1 = Game.createNew("Mexico", "Canada");
        final Game game2 = Game.createNew(HOME_TEAM, AWAY_TEAM);
        final Game game3 = Game.createNew("Spain", "Brazil");

        repository.save(game1);
        repository.save(game2);
        repository.save(game3);

        // When
        final Optional<Game> foundGame = repository.findByTeams(HOME_TEAM, AWAY_TEAM);

        // Then
        assertThat(foundGame).containsSame(game2);
    }

    @Test
    @DisplayName("Should return empty Optional when repository is empty")
    void shouldReturnEmptyWhenRepositoryEmpty() {
        // When
        final Optional<Game> foundGame = repository.findByTeams(HOME_TEAM, AWAY_TEAM);

        // Then
        assertThat(foundGame).isEmpty();
        assertThat(gameStorage).isEmpty();
    }

    @Test
    @DisplayName("Should delete game successfully when game exists")
    void shouldDeleteGameWhenExists() {
        // Given
        final Game game = Game.createNew(HOME_TEAM, AWAY_TEAM);
        repository.save(game);
        assertThat(gameStorage).hasSize(1);

        // When
        repository.delete(game);

        // Then
        assertThat(gameStorage).isEmpty();
    }

    @Test
    @DisplayName("Should delete correct game when multiple games exist")
    void shouldDeleteCorrectGameAmongMultiple() {
        // Given
        final Game game1 = Game.createNew("Mexico", "Canada");
        final Game game2 = Game.createNew(HOME_TEAM, AWAY_TEAM);
        final Game game3 = Game.createNew("Spain", "Brazil");

        repository.save(game1);
        repository.save(game2);
        repository.save(game3);
        assertThat(gameStorage).hasSize(3);

        // When
        repository.delete(game2);

        // Then
        assertThat(gameStorage).containsExactly(game1, game3);
    }

    @Test
    @DisplayName("Should do nothing when deleting non-existent game")
    void shouldDoNothingWhenDeletingNonExistentGame() {
        // Given
        final Game game1 = Game.createNew("Mexico", "Canada");
        final Game game2 = Game.createNew(HOME_TEAM, AWAY_TEAM);
        repository.save(game1);

        // When
        repository.delete(game2);

        // Then
        assertThat(gameStorage).containsExactly(game1);
    }

    @Test
    @DisplayName("Should return all games in insertion order")
    void shouldReturnAllGamesInInsertionOrder() {
        // Given
        final Game game1 = Game.createNew("Mexico", "Canada");
        final Game game2 = Game.createNew("Spain", "Brazil");
        final Game game3 = Game.createNew("Germany", "France");

        repository.save(game1);
        repository.save(game2);
        repository.save(game3);

        // When
        final List<Game> allGames = repository.findAll();

        // Then
        assertThat(allGames).containsExactly(game1, game2, game3);
    }

    @Test
    @DisplayName("Should return empty list when no games exist")
    void shouldReturnEmptyListWhenNoGames() {
        // When
        final List<Game> allGames = repository.findAll();

        // Then
        assertThat(allGames).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("Should return list containing single game")
    void shouldReturnSingleGame() {
        // Given
        final Game game = Game.createNew(HOME_TEAM, AWAY_TEAM);
        repository.save(game);

        // When
        final List<Game> allGames = repository.findAll();

        // Then
        assertThat(allGames).hasSize(1);
        assertThat(allGames.get(0)).isSameAs(game);
    }

    @Test
    @DisplayName("Should maintain insertion order after multiple operations")
    void shouldMaintainInsertionOrderAfterOperations() {
        // Given
        final Game game1 = Game.createNew("Mexico", "Canada");
        final Game game2 = Game.createNew(HOME_TEAM, AWAY_TEAM);
        final Game game3 = Game.createNew("Spain", "Brazil");
        final Game game4 = Game.createNew("Germany", "France");

        repository.save(game1);
        repository.save(game2);
        repository.save(game3);
        repository.delete(game2);
        repository.save(game4);

        // When
        final List<Game> allGames = repository.findAll();

        // Then
        assertThat(allGames).containsExactly(game1, game3, game4);
    }

    @Test
    @DisplayName("Should allow duplicate teams to be saved")
    void shouldAllowDuplicateTeamsToSave() {
        // Given
        final Game game1 = Game.createNew(HOME_TEAM, AWAY_TEAM);
        final Game game2 = Game.createNew(HOME_TEAM, AWAY_TEAM);

        // When
        repository.save(game1);
        repository.save(game2);

        // Then
        assertThat(gameStorage).containsExactly(game1, game2);
    }

    @Test
    @DisplayName("Should find first game when duplicates exist")
    void shouldFindFirstGameWhenDuplicatesExist() {
        // Given
        final Game game1 = Game.createNew(HOME_TEAM, AWAY_TEAM);
        final Game game2 = Game.createNew(HOME_TEAM, AWAY_TEAM);

        repository.save(game1);
        repository.save(game2);

        // When
        final Optional<Game> foundGame = repository.findByTeams(HOME_TEAM, AWAY_TEAM);

        // Then
        assertThat(foundGame).containsSame(game1);
    }
}