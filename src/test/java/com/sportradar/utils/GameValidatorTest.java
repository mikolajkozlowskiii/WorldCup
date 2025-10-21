package com.sportradar.utils;

import com.sportradar.exception.GameValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("GameValidator Unit Tests")
class GameValidatorTest {

    @Test
    @DisplayName("Should pass when home and away teams are valid and different")
    void validateTeamNames_ShouldPassForValidDifferentTeams() {
        // Given
        final String homeTeam = "France";
        final String awayTeam = "Germany";

        // When & Then
        assertThatCode(() -> GameValidator.validateTeamNames(homeTeam, awayTeam))
                .doesNotThrowAnyException();
    }

    @ParameterizedTest(name = "Should throw for same team names: {0} vs {1}")
    @CsvSource({
            "Brazil, brazil",
            "ARGENTINA, argentina",
            "Peru, PERU",
            "Chile, Chile",
            "USA, uSa"
    })
    @DisplayName("Should throw ValidationException when team names are the same (case-insensitive) or exact match")
    void validateTeamNames_ShouldThrowWhenTeamsAreCaseInsensitiveEqual(String homeTeam, String awayTeam) {
        // Act & Assert
        assertThatThrownBy(() -> GameValidator.validateTeamNames(homeTeam, awayTeam))
                .isInstanceOf(GameValidationException.class)
                .hasMessageContaining("Home and away teams must be different")
                .hasFieldOrPropertyWithValue("fieldName", "teams");
    }

    @ParameterizedTest(name = "Should throw for invalid home team: \"{0}\"")
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t", "\n"})
    @DisplayName("Should throw ValidationException for invalid home team name")
    void validateTeamNames_ShouldThrowForInvalidHomeTeam(String invalidHomeTeam) {
        // Given & When & When
        assertThatThrownBy(() -> GameValidator.validateTeamNames(invalidHomeTeam, "ValidAway"))
                .isInstanceOf(GameValidationException.class)
                .hasMessageContaining("Team name cannot be null or blank")
                .hasFieldOrPropertyWithValue("fieldName", "homeTeam");
    }

    @ParameterizedTest(name = "Should throw for invalid away team: \"{0}\"")
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t", "\n"})
    @DisplayName("Should throw ValidationException for invalid away team name")
    void validateTeamNames_ShouldThrowForInvalidAwayTeam(String invalidAwayTeam) {
        // Given & When & When
        assertThatThrownBy(() -> GameValidator.validateTeamNames("ValidHome", invalidAwayTeam))
                .isInstanceOf(GameValidationException.class)
                .hasMessageContaining("Team name cannot be null or blank")
                .hasFieldOrPropertyWithValue("fieldName", "awayTeam");
    }

    @ParameterizedTest(name = "Should pass for score pair: {0}-{1}")
    @CsvSource({
            "0, 5",
            "10, 0",
            "3, 3",
            "0, 0",
            "100, 99"
    })
    @DisplayName("Should pass when both scores are zero or positive")
    void validateScores_ShouldPassForZeroOrPositiveScores(int homeScore, int awayScore) {
        // Act & Assert
        assertThatCode(() -> GameValidator.validateScores(homeScore, awayScore))
                .doesNotThrowAnyException();
    }

    @ParameterizedTest(name = "Should throw for negative home score: {0}")
    @ValueSource(ints = {-1, -100})
    @DisplayName("Should throw ValidationException for negative home score")
    void validateScores_ShouldThrowForNegativeHomeScore(int negativeScore) {
        // Given
        final int awayScore = 5;

        // When & Then
        assertThatThrownBy(() -> GameValidator.validateScores(negativeScore, awayScore))
                .isInstanceOf(GameValidationException.class)
                .hasMessageContaining("Home score cannot be negative")
                .hasFieldOrPropertyWithValue("fieldName", "homeScore");
    }

    @ParameterizedTest(name = "Should throw for negative away score: {0}")
    @ValueSource(ints = {-1, -100})
    @DisplayName("Should throw ValidationException for negative away score")
    void validateScores_ShouldThrowForNegativeAwayScore(int negativeScore) {
        // Given
        final int homeScore = 5;

        // When & Then
        assertThatThrownBy(() -> GameValidator.validateScores(homeScore, negativeScore))
                .isInstanceOf(GameValidationException.class)
                .hasMessageContaining("Away score cannot be negative")
                .hasFieldOrPropertyWithValue("fieldName", "awayScore");
    }

    @Test
    @DisplayName("Should throw AssertionError when attempting to instantiate via Reflection")
    void constructor_ShouldThrowAssertionError() throws NoSuchMethodException {
        // Given
        Constructor<GameValidator> constructor = GameValidator.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        // When & Then
        assertThatThrownBy(constructor::newInstance)
                .isInstanceOf(InvocationTargetException.class)
                .hasRootCauseMessage("Utility class cannot be instantiated")
                .hasCauseInstanceOf(AssertionError.class);
    }
}