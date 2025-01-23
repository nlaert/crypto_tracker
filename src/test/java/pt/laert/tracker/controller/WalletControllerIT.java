package pt.laert.tracker.controller;

import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import pt.laert.tracker.model.WalletEntity;
import pt.laert.tracker.model.dto.Wallet;
import pt.laert.tracker.repositories.WalletRepository;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureEmbeddedDatabase(provider = AutoConfigureEmbeddedDatabase.DatabaseProvider.ZONKY)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class WalletControllerIT {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private WalletRepository walletRepository;

    @Test
    public void testCreateWalletShouldReturnCreatedWallet() {
        // Given
        String email = "test@gmail.com";
        var wallet = new Wallet();
        wallet.setEmail(email);
        // When
        var response = restTemplate.postForEntity("/wallet/create", wallet, Wallet.class);
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
        var response = restTemplate.postForEntity("/wallet/create", wallet, String.class);
        // Then
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test
    public void testCreateWalletShouldReturnBadRequestWhenEmailIsNull() {
        // Given
        var wallet = new Wallet();
        // When
        var response = restTemplate.postForEntity("/wallet/create", wallet, String.class);
        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testCreateWalletShouldReturnBadRequestWhenEmailIsEmpty() {
        // Given
        var wallet = new Wallet();
        wallet.setEmail("");
        // When
        var response = restTemplate.postForEntity("/wallet/create", wallet, String.class);
        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }


}