package pt.laert.tracker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
import pt.laert.tracker.model.WalletAssetsEntity;
import pt.laert.tracker.model.WalletEntity;
import pt.laert.tracker.model.dto.Asset;
import pt.laert.tracker.model.dto.CoinData;
import pt.laert.tracker.model.dto.Data;
import pt.laert.tracker.model.dto.Wallet;
import pt.laert.tracker.model.dto.WalletAssets;
import pt.laert.tracker.repositories.WalletAssetsRepository;
import pt.laert.tracker.repositories.WalletRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureEmbeddedDatabase(provider = AutoConfigureEmbeddedDatabase.DatabaseProvider.ZONKY)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class WalletControllerIT {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private WalletAssetsRepository walletAssetsRepository;

    @Value(value = "${spring.coincap.search-url}")
    private String API_URL;

    private MockRestServiceServer mockServer;
    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    public void init() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    public void testCreateWalletShouldReturnCreatedWallet() {
        // Given
        String email = "test@gmail.com";
        var wallet = new Wallet();
        wallet.setEmail(email);
        // When
        var response = testRestTemplate.postForEntity("/wallet/create", wallet, Wallet.class);
        // Then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Wallet responseBody = response.getBody();
        assertNotNull(responseBody);
        assertNotNull(responseBody.getWalletId());
        assertEquals(email, responseBody.getEmail());
    }

    @Test
    public void testCreateWalletShouldReturnConflictIfEmailAlreadyExists() {
        // Given
        String email = "alreadyExistingEmail@gmail.com";
        var walletEntity = new WalletEntity(email);
        walletRepository.save(walletEntity);
        var wallet = new Wallet();
        wallet.setEmail(email);
        // When
        var response = testRestTemplate.postForEntity("/wallet/create", wallet, String.class);
        // Then
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test
    public void testCreateWalletShouldReturnBadRequestWhenEmailIsNull() {
        // Given
        var wallet = new Wallet();
        // When
        var response = testRestTemplate.postForEntity("/wallet/create", wallet, String.class);
        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testCreateWalletShouldReturnBadRequestWhenEmailIsEmpty() {
        // Given
        var wallet = new Wallet();
        wallet.setEmail("");
        // When
        var response = testRestTemplate.postForEntity("/wallet/create", wallet, String.class);
        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testAddAssetToWalletShouldReturnNoContentAndCheckPriceOfAssetOnSuccess() throws Exception {
        // Given
        String email = "aRealEmail@gmail.com";
        var walletEntity = new WalletEntity(email);
        walletEntity = walletRepository.save(walletEntity);
        Asset asset = new Asset(
                "DOT",
                BigDecimal.valueOf(1.0),
                2.0
        );
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        var entity = new HttpEntity<>(asset, headers);
        var coinData = new CoinData(
                "polkadot",
                "DOT",
                "Polkadot",
                BigDecimal.valueOf(6.47)
        );

        // When
        mockServer.expect(ExpectedCount.once(),
                requestTo(new URI(API_URL + asset.getSymbol())))
          .andExpect(method(HttpMethod.GET))
          .andRespond(withStatus(HttpStatus.OK)
          .contentType(MediaType.APPLICATION_JSON)
          .body(mapper.writeValueAsString(new Data(Collections.singletonList(coinData)))));

        var response = testRestTemplate.exchange(
                "/wallet/" + walletEntity.getId() + "/asset",
                HttpMethod.PUT,
                entity,
                String.class);

        // Then
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        var walletAsset = walletAssetsRepository.findAllByWalletId(walletEntity.getId());
        assertEquals(1, walletAsset.size());
        var walletAssetEntity = walletAsset.get(0);
        assertEquals(asset.getSymbol(), walletAssetEntity.getSymbol());
        assertEquals(
                asset.getQuantity().setScale(2, RoundingMode.HALF_UP),
                walletAssetEntity.getQuantity().setScale(2, RoundingMode.HALF_UP)
        );
        assertEquals(asset.getPrice(), walletAssetEntity.getOriginalValue());
    }

    @Test
    public void testGetWalletShouldReturnWalletWithAssetsAndTotalPrice() {
         // Given
        String email = "anotherEmail@gmail.com";
        var walletEntity = new WalletEntity(email);
        walletEntity = walletRepository.save(walletEntity);
        WalletAssetsEntity assetBtc = new WalletAssetsEntity(
                walletEntity.getId(),
                "BTC",
                BigDecimal.valueOf(1),
                17000.0
        );
        WalletAssetsEntity assetEth = new WalletAssetsEntity(
                walletEntity.getId(),
                "ETH",
                BigDecimal.valueOf(5),
                2000.0
        );
        walletAssetsRepository.saveAll(List.of(assetBtc, assetEth));
        var btcSavedValue = 100000.0; // Values defined in V2 flyway script
        var ethSavedValue = 3320.23;
        var walletAssets = new WalletAssets(
                walletEntity.getId(),
                btcSavedValue + ethSavedValue * 5,
                List.of(
                        new Asset(
                                assetBtc.getSymbol(),
                                assetBtc.getQuantity(),
                                assetBtc.getOriginalValue()
                        ),
                        new Asset(
                                assetEth.getSymbol(),
                                assetEth.getQuantity(),
                                assetEth.getOriginalValue()
                        )
                )
        );

        // Assets are already created in V2 flyway script

        // When
        var response = testRestTemplate.getForEntity("/wallet/" + walletEntity.getId(), WalletAssets.class);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        var responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(walletAssets.getId(), responseBody.getId());
        assertEquals(walletAssets.getTotal(), responseBody.getTotal());
        var responseAssets = responseBody.getAssets();
        assertEquals(walletAssets.getAssets().size(), responseAssets.size());
        assertEquals(btcSavedValue, responseAssets.get(0).getPrice());
        assertEquals(ethSavedValue, responseAssets.get(1).getPrice());
    }

}