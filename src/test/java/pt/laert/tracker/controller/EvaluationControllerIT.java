package pt.laert.tracker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import java.math.BigDecimal;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import pt.laert.tracker.model.dto.Asset;
import pt.laert.tracker.model.dto.CoinData;
import pt.laert.tracker.model.dto.Data;
import pt.laert.tracker.model.dto.EvaluationRequest;
import pt.laert.tracker.model.dto.EvaluationResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureEmbeddedDatabase(provider = AutoConfigureEmbeddedDatabase.DatabaseProvider.ZONKY)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EvaluationControllerIT {
    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private RestTemplate restTemplate;

    @Value(value = "${spring.coincap.search-url}")
    private String API_URL;

    private MockRestServiceServer mockServer;
    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    public void init() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    public void testEvaluate() throws Exception {
        // Given
        var assetBtc = new Asset();
        assetBtc.setSymbol("BTC");
        assetBtc.setQuantity(BigDecimal.valueOf(0.5));
        assetBtc.setValue(35000.0);

        var assetEth = new Asset();
        assetEth.setSymbol("ETH");
        assetEth.setQuantity(BigDecimal.valueOf(4.25));
        assetEth.setValue(15310.71);

        var assets = List.of(assetBtc, assetEth);
        var evaluationRequest = new EvaluationRequest(assets);

        double btcValue = 94745;
        var btcCoinData = new CoinData(
                "bitcoin",
                "BTC",
                "Bitcoin",
                BigDecimal.valueOf(btcValue)
        );
        int ethValue = 3700;
        var ethCoinData = new CoinData(
                "ethereum",
                "ETH",
                "Ethereum",
                BigDecimal.valueOf(ethValue)
        );

        // When
        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI(API_URL + assetBtc.getSymbol())))
          .andExpect(method(HttpMethod.GET))
          .andRespond(withStatus(HttpStatus.OK)
          .contentType(MediaType.APPLICATION_JSON)
          .body(mapper.writeValueAsString(new Data(Collections.singletonList(btcCoinData)))));

        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI(API_URL + assetEth.getSymbol())))
          .andExpect(method(HttpMethod.GET))
          .andRespond(withStatus(HttpStatus.OK)
          .contentType(MediaType.APPLICATION_JSON)
          .body(mapper.writeValueAsString(new Data(Collections.singletonList(ethCoinData)))));

        var response = testRestTemplate.postForEntity("/evaluation", evaluationRequest, EvaluationResponse.class);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        var body = response.getBody();
        assertNotNull(body);
        assertEquals((btcValue * 0.5) + (ethValue * 4.25), body.getTotal());
        assertEquals("BTC", body.getBestAsset());
        assertEquals(35.35, body.getBestPerformance());
        assertEquals("ETH", body.getWorstAsset());
        assertEquals(2.71, body.getWorstPerformance()); // 2.70575 rounded is 2.71
    }

        @Test
    public void testEvaluateWitJson() throws Exception {
        // Given
        var jsonString = """
                {
                  "assets": [
                    {
                      "symbol": "BTC",
                      "quantity": 0.5,
                      "value": 35000
                    },
                    {
                      "symbol": "ETH",
                      "quantity": 4.25,
                      "value": 15310.71
                    }
                  ]
                }""";

        double btcValue = 94745;
        var btcCoinData = new CoinData(
                "bitcoin",
                "BTC",
                "Bitcoin",
                BigDecimal.valueOf(btcValue)
        );
        int ethValue = 3700;
        var ethCoinData = new CoinData(
                "ethereum",
                "ETH",
                "Ethereum",
                BigDecimal.valueOf(ethValue)
        );

        // When
        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI(API_URL + "BTC")))
          .andExpect(method(HttpMethod.GET))
          .andRespond(withStatus(HttpStatus.OK)
          .contentType(MediaType.APPLICATION_JSON)
          .body(mapper.writeValueAsString(new Data(Collections.singletonList(btcCoinData)))));

        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI(API_URL + "ETH")))
          .andExpect(method(HttpMethod.GET))
          .andRespond(withStatus(HttpStatus.OK)
          .contentType(MediaType.APPLICATION_JSON)
          .body(mapper.writeValueAsString(new Data(Collections.singletonList(ethCoinData)))));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        var entity = new HttpEntity<>(jsonString, headers);

        var response = testRestTemplate.exchange("/evaluation", HttpMethod.POST, entity, EvaluationResponse.class);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        var body = response.getBody();
        assertNotNull(body);
        assertEquals((btcValue * 0.5) + (ethValue * 4.25), body.getTotal());
        assertEquals("BTC", body.getBestAsset());
        assertEquals(35.35, body.getBestPerformance());
        assertEquals("ETH", body.getWorstAsset());
        assertEquals(2.71, body.getWorstPerformance()); // 2.70575 rounded is 2.71
    }

        @Test
    public void testEvaluate_WithUnknownCoin() throws Exception {
        // Given
        var assetBtc = new Asset();
        assetBtc.setSymbol("BTC");
        assetBtc.setQuantity(BigDecimal.valueOf(0.5));
        assetBtc.setValue(35000.0);

        var assetEth = new Asset();
        assetEth.setSymbol("NonExistingCoin");
        assetEth.setQuantity(BigDecimal.valueOf(4.25));
        assetEth.setValue(15310.71);

        var assets = List.of(assetBtc, assetEth);
        var evaluationRequest = new EvaluationRequest(assets);

        double btcValue = 94745;
        var btcCoinData = new CoinData(
                "bitcoin",
                "BTC",
                "Bitcoin",
                BigDecimal.valueOf(btcValue)
        );

        // When
        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI(API_URL + assetBtc.getSymbol())))
          .andExpect(method(HttpMethod.GET))
          .andRespond(withStatus(HttpStatus.OK)
          .contentType(MediaType.APPLICATION_JSON)
          .body(mapper.writeValueAsString(new Data(Collections.singletonList(btcCoinData)))));

        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI(API_URL + "NonExistingCoin")))
          .andExpect(method(HttpMethod.GET))
          .andRespond(withStatus(HttpStatus.OK)
          .contentType(MediaType.APPLICATION_JSON)
          .body(mapper.writeValueAsString(new Data(List.of()))));

        var response = testRestTemplate.postForEntity("/evaluation", evaluationRequest, String.class);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        var body = response.getBody();
//        assertNotNull(body);
//        assertEquals((btcValue * 0.5) + (ethValue * 4.25), body.getTotal());
//        assertEquals("BTC", body.getBestAsset());
//        assertEquals(35.35, body.getBestPerformance());
//        assertEquals("ETH", body.getWorstAsset());
//        assertEquals(2.71, body.getWorstPerformance()); // 2.70575 rounded is 2.71
    }

}