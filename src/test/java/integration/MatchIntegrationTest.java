package integration;

import app.foot.FootApi;
import app.foot.controller.rest.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import java.io.UnsupportedEncodingException;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static utils.TestUtils.*;

@SpringBootTest(classes = FootApi.class)
@AutoConfigureMockMvc
@Slf4j
class MatchIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper()
            .findAndRegisterModules();  //Allow 'java.time.Instant' mapping

    @Test
    void read_match_by_id_ok() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(get("/matches/2"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();
        Match actual = objectMapper.readValue(
                response.getContentAsString(), Match.class);

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(expectedMatch2(), actual);
    }

    @Test
    void read_matches_ok() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(get("/matches"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();
        List<Match> actual = convertFromHttpResponse(response);

        assertEquals(3, actual.size());
        assertTrue(actual.contains(expectedMatch2()));
        //TODO: add these checks and its values
        //assertTrue(actual.contains(expectedMatch1()));
        //assertTrue(actual.contains(expectedMatch3()));
    }
    @Test
    void read_matches_with_tree_expected_ok() throws Exception {
        MockHttpServletResponse response = mockMvc
                .perform(get("/matches"))
                .andReturn()
                .getResponse();
        List<Match> actual = convertFromHttpResponse(response);

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(3, actual.size());
        assertTrue(actual.containsAll(List.of(
                expectedMatch1(),
                expectedMatch2(),
                expectedMatch3())));
    }
    void read_match_by_id_doesnt_exist_ko()throws Exception{
        String id = "20";

        assertThrowsApiException("404 NOT_FOUND : Match#"+id+" not found.",
                mockMvc.perform(get("/matches/"+id))
                        .andExpect(status().isNotFound())
                        .andReturn()
                        .getResponse());
    }
    @Test
    void add_goals_ok() throws Exception {
        String matchId = "3";

        MockHttpServletResponse response = mockMvc
                .perform(post("/matches/"+matchId+"/goals")
                        .content(objectMapper.writeValueAsString(List.of(scorer6() )))
                        .contentType("application/json")
                        .accept("application/json"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        Match actual = objectMapper.readValue(
                response.getContentAsString(), Match.class);

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(expectedMatch3(), actual);

    }

    @Test
    void add_goals_of_goalkeeper_ko() throws Exception {
        String matchId = "3";
        String errorMessage = "400 BAD_REQUEST : Player#"+player6().getId()+" is a guardian so they cannot score.";
        assertThrowsApiException(errorMessage,
                mockMvc.perform(post("/matches/"+matchId+"/goals")
                                .content(objectMapper.writeValueAsString(List.of(
                                        scorer1().toBuilder()
                                                .player(player6().toBuilder()
                                                        .isGuardian(true)
                                                        .build())
                                                .build() )))
                                .contentType("application/json")).andExpect(status().isBadRequest())
                        .andReturn()
                        .getResponse());

    }

    private static Match expectedMatch3(){
        return Match.builder()
                .id(3)
                .teamA(teamMatchC())
                .teamB(teamMatchB())
                .stadium("S3")
                .datetime(Instant.parse("2023-01-03T16:00:00Z"))
                .build();
    }

    private static Match expectedMatch2() {
        return Match.builder()
                .id(2)
                .teamA(teamMatchA())
                .teamB(teamMatchB())
                .stadium("S2")
                .datetime(Instant.parse("2023-01-01T14:00:00Z"))
                .build();
    }
    private static Match expectedMatch1() {
        return Match.builder()
                .id(2)
                .teamA(teamMatchA())
                .teamB(teamMatchC())
                .stadium("S1")
                .datetime(Instant.parse("2023-01-02T16:00:00Z"))
                .build();
    }

    private static TeamMatch teamMatchB() {
        return TeamMatch.builder()
                .team(team3())
                .score(0)
                .scorers(List.of())
                .build();
    }
    private static TeamMatch teamMatchC() {
        return TeamMatch.builder()
                .team(team1())
                .score(0)
                .scorers(List.of())
                .build();
    }

    private static TeamMatch teamMatchA() {
        return TeamMatch.builder()
                .team(team2())
                .score(2)
                .scorers(List.of(PlayerScorer.builder()
                                .player(player3())
                                .scoreTime(70)
                                .isOG(false)
                                .build(),
                        PlayerScorer.builder()
                                .player(player6())
                                .scoreTime(80)
                                .isOG(true)
                                .build()))
                .build();
    }

    private static Team team3() {
        return Team.builder()
                .id(3)
                .name("E3")
                .build();
    }
    private static  Team team1(){
        return Team.builder()
                .id(1)
                .name("E1")
                .build();
    }

    private static Player player6() {
        return Player.builder()
                .id(6)
                .name("J6")
                .teamName("E3")
                .isGuardian(false)
                .build();
    }

    private static Player player3() {
        return Player.builder()
                .id(3)
                .name("J3")
                .teamName("E2")
                .isGuardian(false)
                .build();
    }
    private static Player player1(){
        return Player.builder()
                .id(1)
                .name("J1")
                .teamName("E1")
                .isGuardian(false)
                .build();
    }

    private static Player player2(){
        return Player.builder()
                .id(2)
                .name("J2")
                .teamName("E2")
                .isGuardian(false)
                .build();
    }

    private static Team team2() {
        return Team.builder()
                .id(2)
                .name("E2")
                .build();
    }

    private List<Match> convertFromHttpResponse(MockHttpServletResponse response)
            throws JsonProcessingException, UnsupportedEncodingException {
        CollectionType playerListType = objectMapper.getTypeFactory()
                .constructCollectionType(List.class, Match.class);
        return objectMapper.readValue(
                response.getContentAsString(),
                playerListType);
    }
}
