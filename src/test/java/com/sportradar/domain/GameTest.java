package com.sportradar.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Game Record Validation and Functionality")
class GameTest {

    @Test
    @DisplayName("Should successfully create a Game with valid parameters")
    void shouldCreateGameSuccessfully() {
        // Given
        final String home = "Poland";
        final String away = "Lithuania";
        final int homeScore = 2;
        final int awayScore = 0;

        // When
        final Game game = new Game(home, away, homeScore, awayScore);

        // Then
        assertThat(game.homeTeam()).isEqualTo(home);
        assertThat(game.awayTeam()).isEqualTo(away);
        assertThat(game.homeScore()).isEqualTo(homeScore);
        assertThat(game.awayScore()).isEqualTo(awayScore);
    }

    @ParameterizedTest(name = "Home: \"{0}\" throws '...null or blank'")
    @ValueSource(strings = {"", " ", "  ", " \t "})
    @DisplayName("Should throw 'Home team cannot be null or blank' for blank/empty input")
    void shouldThrowForBlankHomeTeam(String invalidName) {
        // Given & When & Then
        assertThatThrownBy(() -> new Game(invalidName, "Italy", 0, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Home team cannot be null or blank");
    }

    @Test
    @DisplayName("Should throw 'Home team cannot be null or blank' when homeTeam is null")
    void shouldThrowWhenHomeTeamIsNull() {
        // Given & When & Then
        assertThatThrownBy(() -> new Game(null, "Italy", 0, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Home team cannot be null or blank");
    }

    @ParameterizedTest(name = "Away: \"{0}\" throws '...null or blank'")
    @ValueSource(strings = {"", " ", "  ", " \t ", "  \n"})
    @DisplayName("Should throw 'Away team cannot be null or blank' for blank/empty input")
    void shouldThrowForBlankAwayTeam(String invalidName) {
        // Given & When & Then
        assertThatThrownBy(() -> new Game("Spain", invalidName, 0, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Away team cannot be null or blank");
    }

    @Test
    @DisplayName("Should throw 'Away team cannot be null or blank' when awayTeam is null")
    void shouldThrowWhenAwayTeamIsNull() {
        // Given & When & Then
        assertThatThrownBy(() -> new Game("Spain", null, 0, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Away team cannot be null or blank");
    }

    @ParameterizedTest(name = "Home: {0}, Away: {1}")
    @CsvSource({
            "Spain, Spain",
            "SPAIN, spain",
            "Spain, spain",
    })
    @DisplayName("Should throw when home and away teams are the same (case-insensitive)")
    void shouldThrowWhenTeamsAreIdentical(String home, String away) {
        // Given & When & Then
        assertThatThrownBy(() -> new Game(home, away, 0, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Home and away teams must be different");
    }

    @ParameterizedTest(name = "Home Score: {0}, Away Score: {1}")
    @CsvSource({
            "-1, 0",   // Negative home score
            "0, -100", // Negative away score
            "-5, -5"   // Both negative
    })
    @DisplayName("Should throw when either score is negative")
    void shouldThrowWhenScoreIsNegative(int homeScore, int awayScore) {
        // Given & When & Then
        assertThatThrownBy(() -> new Game("Brazil", "Argentina", homeScore, awayScore))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Scores cannot be negative");
    }

    @Test
    @DisplayName("getTotalScore should return the correct sum of scores")
    void totalScoreShouldReturnCorrectSum() {
        // Given & When
        final Game game = new Game("Brazil", "Argentina", 3, 2);

        // Then
        assertThat(game.getTotalScore()).isEqualTo(5);
    }

    @Test
    @DisplayName("createNew factory method should set initial scores to zero")
    void createNewShouldInitializeScoresToZero() {
        // Given & When
        Game game = Game.createNew("France", "Germany");

        // Then
        assertThat(game.homeScore()).isZero();
        assertThat(game.awayScore()).isZero();
        assertThat(game.getTotalScore()).isZero();
    }

    @Test
    @DisplayName("createWithScores factory method should create game without throwing")
    void createWithScoresShouldWork() {
        // Given & When & Then
        assertThatCode(() -> Game.createWithScores("Team X", "Team Y", 5, 1))
                .doesNotThrowAnyException();
    }

    @ParameterizedTest(name = "Home: \"{0}\" throws 'Invalid home team name'")
    @ValueSource(strings = {"123Test", "Team-A", "ABC!", "Te@m", "Brazil_vs_Italy"})
    @DisplayName("Should throw 'Invalid home team name' for inputs with special characters or numbers")
    void shouldThrowForInvalidHomeTeamFormat(String invalidName) {
        // Given & When & Then
        assertThatThrownBy(() -> new Game(invalidName, "Italy", 0, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid home team name:")
                .hasMessageContaining(invalidName);
    }

    @ParameterizedTest(name = "Away: \"{0}\" throws 'Invalid away team name'")
    @ValueSource(strings = {"123Test", "Team-B", "ABC!", "Te@m", "Brazil_vs_Italy"})
    @DisplayName("Should throw 'Invalid away team name' for inputs with special characters or numbers")
    void shouldThrowForInvalidAwayTeamFormat(String invalidName) {
        // Given & When & Then
        assertThatThrownBy(() -> new Game("Spain", invalidName, 0, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid away team name:")
                .hasMessageContaining(invalidName);
    }
}