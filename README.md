# Tracker backend

This is the backend for a crypto tracker app to help track crypto coin values inside a wallet.
Note: Wallet in this context is not a crypto wallet, but a created portfolio inside this app.

## Running Tests

To run tests, run the following command

```bash
  mvn clean verify
```


## Run Locally
Clone the project

```bash
  git clone https://github.com/nlaert/crypto_tracker.git
```

Go to the project directory

```bash
  cd crypto_tracker
```

Start the server

```bash
  docker compose up -d
```

## Usage/Examples
Check postman collection which contains the basic calls needed in [file](Tracker.postman_collection.json) 