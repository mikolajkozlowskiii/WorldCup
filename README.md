# Live Data Coding Exercise

## Assumptions

### Game Domain
- **Score Validation**: Games cannot have negative scores. 
- **Team Names**:
    - Any non-null, non-blank string represents is a valid team name.
    - Team names are case-insensitive.
    - Home team and away team must have different names within the same game (case-insensitive comparison).
    - Null or blank strings are not valid team names.

### Service Layer
- **Thread Safety**: The service implementation is not thread-safe, as per the requirement to provide the simplest possible solution.
- **Business Logic**:
    - Validates team names and scores using a utility class.
    - The system prevents starting a new game if both teams (home and away) are already playing together in an existing match.
    - However, the repository's `findByTeams()` method uses case-sensitive matching, which could allow inconsistent data if validation weren't invoked beforehand.
    - Due to the `findByTeams()` implementation (exact home/away pair matching), a team can technically participate in multiple games simultaneously if paired with different opponents.
    - This design decision prioritizes simplicity.

### Repository Layer
- **Data Structure**: Uses a simple `List` implementation rather than more efficient data structures to maintain simplicity as specified in the requirements.
- **Responsibility**: The repository provides basic operations with minimal query logic. Primary business rule validation is handled at the service layer.
- **Query Behavior**: The `findByTeams()` method uses case-sensitive exact matching for both home and away team names.
- **Trade-offs**: This approach sacrifices performance optimization for code simplicity.


## Code Coverage

This solution uses JaCoCo plugin.

### Coverage Requirements
- **Instruction Coverage**: 100%
- **Branch Coverage**: 100%
- **Line Coverage**: 100%

**Note**: Domain and exception classes are excluded from coverage requirements.

### Running Coverage Check

To run tests and verify code coverage from the terminal:
```bash
mvn clean verify

mvn clean test jacoco:report
```
