package com.sportradar.service;

import com.sportradar.domain.Game;
import com.sportradar.exception.GameAlreadyExistsException;
import com.sportradar.exception.GameNotFoundException;
import com.sportradar.repository.GameRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GameService Tests")
class GameServiceTest {
    private static final String HOME_TEAM = "Poland";
    private static final String AWAY_TEAM = "Argentina";
    @Mock
    private GameRepository gameRepository;
    @InjectMocks
    private GameServiceImpl gameService;

    @Test
    @DisplayName("Should start Game and return the instance when teams are not present in repository")
    void shouldStartGameWhenTeamsNotInRepository() {
        // Given
        final Game savedGame = Game.createNew(HOME_TEAM, AWAY_TEAM);

        when(gameRepository.findByTeams(HOME_TEAM, AWAY_TEAM)).thenReturn(Optional.empty());

        when(gameRepository.save(any(Game.class))).thenReturn(savedGame);

        // When
        final Game actualGame = gameService.startGame(HOME_TEAM, AWAY_TEAM);

        // Then
        assertThat(actualGame).isSameAs(savedGame);
        verify(gameRepository, times(1)).findByTeams(HOME_TEAM, AWAY_TEAM);
        verify(gameRepository, times(1)).save(any(Game.class));
        verifyNoMoreInteractions(gameRepository);
    }

    @Test
    @DisplayName("Should throw GameAlreadyExistsException when game is already in repository")
    void shouldThrowWhenGameStarAndGameAlreadyExists() {
        // Given
        final Game existingGame = Game.createNew(HOME_TEAM, AWAY_TEAM);
        when(gameRepository.findByTeams(HOME_TEAM, AWAY_TEAM)).thenReturn(Optional.of(existingGame));

        // When & Then
        assertThatThrownBy(() -> gameService.startGame(HOME_TEAM, AWAY_TEAM))
                .isInstanceOf(GameAlreadyExistsException.class)
                .hasMessageContaining("already in progress");

        verify(gameRepository, times(1)).findByTeams(HOME_TEAM, AWAY_TEAM);
        verify(gameRepository, never()).save(any(Game.class));
        verifyNoMoreInteractions(gameRepository);
    }

    @Test
    @DisplayName("Should delete game successfully when found")
    void shouldSucceedFinishGameWhenGameFound() {
        // Given
        final Game existingGame = Game.createNew(HOME_TEAM, AWAY_TEAM);
        when(gameRepository.findByTeams(HOME_TEAM, AWAY_TEAM)).thenReturn(Optional.of(existingGame));

        // When
        gameService.finishGame(HOME_TEAM, AWAY_TEAM);

        // Then
        verify(gameRepository, times(1)).findByTeams(HOME_TEAM, AWAY_TEAM);
        verify(gameRepository, times(1)).delete(existingGame);
        verifyNoMoreInteractions(gameRepository);
    }

    @Test
    @DisplayName("Should throw GameNotFoundException when trying to finish a game that doesn't exist")
    void shouldThrowWhenFinishGameAndGameNotFound() {
        // Given
        when(gameRepository.findByTeams(HOME_TEAM, AWAY_TEAM)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> gameService.finishGame(HOME_TEAM, AWAY_TEAM))
                .isInstanceOf(GameNotFoundException.class)
                .hasMessageContaining("not found");

        verify(gameRepository, times(1)).findByTeams(HOME_TEAM, AWAY_TEAM);
        verify(gameRepository, never()).delete(any());
        verifyNoMoreInteractions(gameRepository);
    }

    @Test
    @DisplayName("Should update score by calling updateScores on the Game record and persisting the new instance")
    void shouldUpdateGameWhenGameFound() {
        // Given
        final int oldHomeScore = 0;
        final int oldAwayScore = 1;
        final int newHomeScore = 2;
        final int newAwayScore = 3;

        final Game oldGameRecord = Game.createWithScores(HOME_TEAM, AWAY_TEAM, oldHomeScore, oldAwayScore);
        final Game updatedGameRecord = Game.createWithScores(HOME_TEAM, AWAY_TEAM, newHomeScore, newAwayScore);

        when(gameRepository.findByTeams(HOME_TEAM, AWAY_TEAM)).thenReturn(Optional.of(oldGameRecord));
        when(gameRepository.save(updatedGameRecord)).thenReturn(updatedGameRecord);

        // When
        final Game actualGame = gameService.updateScore(HOME_TEAM, AWAY_TEAM, newHomeScore, newAwayScore);

        // Then
        assertThat(actualGame).isSameAs(updatedGameRecord);
        verify(gameRepository, times(1)).findByTeams(HOME_TEAM, AWAY_TEAM);
        verify(gameRepository, times(1)).delete(oldGameRecord);
        verify(gameRepository, times(1)).save(updatedGameRecord);
        verifyNoMoreInteractions(gameRepository);
    }

    @Test
    @DisplayName("Should throw GameNotFoundException when trying to update score for a game that doesn't exist")
    void shouldThrowWhenUpdateAndGameNotFound() {
        // Given
        when(gameRepository.findByTeams(HOME_TEAM, AWAY_TEAM)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> gameService.updateScore(HOME_TEAM, AWAY_TEAM, 1, 1))
                .isInstanceOf(GameNotFoundException.class)
                .hasMessageContaining("not found");

        verify(gameRepository, times(1)).findByTeams(HOME_TEAM, AWAY_TEAM);
        verify(gameRepository, never()).delete(any());
        verify(gameRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should return games sorted by total score descending and then by most recently added")
    void shouldReturnGamesSortedByTotalScoreAndReverseOrder() {
        // Given
        final Game mexicoCanada = Game.createWithScores("Mexico", "Canada", 0, 5);
        final Game spainBrazil = Game.createWithScores("Spain", "Brazil", 10, 2);
        final Game germanyFrance = Game.createWithScores("Germany", "France", 2, 2);
        final Game uruguayItaly = Game.createWithScores("Uruguay", "Italy", 6, 6);
        final Game argentinaAustralia = Game.createWithScores("Argentina", "Australia", 3, 1);

        final List<Game> gamesInInsertionOrder = List.of(
                mexicoCanada,
                spainBrazil,
                germanyFrance,
                uruguayItaly,
                argentinaAustralia
        );

        when(gameRepository.findAll()).thenReturn(new ArrayList<>(gamesInInsertionOrder));

        // When
        final List<Game> summary = gameService.getSummaryByTotalScoreAndReverseOrder();

        // Then
        assertThat(summary).isNotNull();
        assertThat(summary).isEqualTo(5);

        assertThat(summary.get(0).homeTeam()).isEqualTo("Uruguay");
        assertThat(summary.get(0).awayTeam()).isEqualTo("Italy");
        assertThat(summary.get(0).getTotalScore()).isEqualTo(12);

        assertThat(summary.get(1).homeTeam()).isEqualTo("Spain");
        assertThat(summary.get(1).awayTeam()).isEqualTo("Brazil");
        assertThat(summary.get(1).getTotalScore()).isEqualTo(12);

        assertThat(summary.get(2).homeTeam()).isEqualTo("Mexico");
        assertThat(summary.get(2).awayTeam()).isEqualTo("Canada");
        assertThat(summary.get(2).getTotalScore()).isEqualTo(5);

        assertThat(summary.get(3).homeTeam()).isEqualTo("Argentina");
        assertThat(summary.get(3).awayTeam()).isEqualTo("Australia");
        assertThat(summary.get(3).getTotalScore()).isEqualTo(4);

        assertThat(summary.get(4).homeTeam()).isEqualTo("Germany");
        assertThat(summary.get(4).awayTeam()).isEqualTo("France");
        assertThat(summary.get(4).getTotalScore()).isEqualTo(4);

        verify(gameRepository, times(1)).findAll();
        verifyNoMoreInteractions(gameRepository);
    }

    @Test
    @DisplayName("Should return single game when only one game exists")
    void shouldReturnSingleGameWhenOnlyOneExists() {
        // Given
        final Game singleGame = Game.createWithScores("Mexico", "Canada", 0, 5);
        when(gameRepository.findAll()).thenReturn(new ArrayList<>(List.of(singleGame)));

        // When
        final List<Game> summary = gameService.getSummaryByTotalScoreAndReverseOrder();

        // Then
        assertThat(summary).isNotNull();
        assertThat(summary.size()).isEqualTo(1);
        assertThat(summary.get(0).homeTeam()).isEqualTo("Mexico");
        assertThat(summary.get(0).awayTeam()).isEqualTo("Canada");
        verify(gameRepository, times(1)).findAll();
        verifyNoMoreInteractions(gameRepository);
    }

    @Test
    @DisplayName("Should return empty list when no games exists")
    void shouldReturnEmptyListWhenNoGamesExists() {
        // Given
        when(gameRepository.findAll()).thenReturn(Collections.emptyList());

        // When
        final List<Game> summary = gameService.getSummaryByTotalScoreAndReverseOrder();

        // Then
        assertThat(summary).isNotNull();
        assertThat(summary.size()).isEqualTo(0);
        verify(gameRepository, times(1)).findAll();
        verifyNoMoreInteractions(gameRepository);
    }
}
