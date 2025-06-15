package frontend;

import backend.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TicketSystemGUI extends Application {
    private SystemConfiguration config;
    private TicketPool ticketPool;
    private ExecutorService executorService;

    // UI Components
    private TextField maxTicketCapacityField;
    private TextField totalTicketsField;
    private TextField ticketReleaseRateField;
    private TextField customerRetrievalRateField;
    private TextArea systemLogArea;
    private Label remainingTicketsLabel;
    private Button startButton;
    private Button stopButton;
    private Label systemStatusLabel;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Real-Time Event Ticketing System");

        // Main layout
        VBox mainLayout = new VBox(10);
        mainLayout.setPadding(new Insets(15));

        // Configuration Section
        TitledPane configSection = createConfigurationSection();

        // Control Panel with Logs
        VBox controlAndLogsSection = createControlAndLogsSection();

        Label titleLabel = new Label("Real-Time Event Ticketing System");
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 18px;");

        mainLayout.getChildren().addAll(
                titleLabel,
                configSection,
                controlAndLogsSection
        );

        Scene scene = new Scene(mainLayout, 800, 750);

        // Load previous configuration or set default
        config = loadOrCreateConfiguration();
        loadConfigurationToFields();

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private SystemConfiguration loadOrCreateConfiguration() {
        // Try to load from existing config file, otherwise create default
        File configFile = new File("systemConfig.json");
        if (configFile.exists()) {
            return SystemConfiguration.loadFromFile("systemConfig.json");
        }
        // Create default configuration with reasonable values
        return new SystemConfiguration(
                100,    // maxTicketCapacity
                50,     // totalTickets
                2,      // ticketReleaseRate
                3       // customerRetrievalRate
        );
    }

    private void loadConfigurationToFields() {
        maxTicketCapacityField.setText(String.valueOf(config.getMaxTicketCapacity()));
        totalTicketsField.setText(String.valueOf(config.getTotalTickets()));
        ticketReleaseRateField.setText(String.valueOf(config.getTicketReleaseRate()));
        customerRetrievalRateField.setText(String.valueOf(config.getCustomerRetrievalRate()));
    }

    private TitledPane createConfigurationSection() {
        GridPane configGrid = new GridPane();
        configGrid.setHgap(10);
        configGrid.setVgap(10);

        // Configuration Input Fields
        maxTicketCapacityField = createLabeledTextField(configGrid, "Max Ticket Capacity:", 0);
        totalTicketsField = createLabeledTextField(configGrid, "Total Tickets:", 1);
        ticketReleaseRateField = createLabeledTextField(configGrid, "Ticket Release Rate (sec):", 2);
        customerRetrievalRateField = createLabeledTextField(configGrid, "Customer Retrieval Rate (sec):", 3);

        TitledPane configSection = new TitledPane("System Configuration", configGrid);
        configSection.setCollapsible(false);
        return configSection;
    }

    private TextField createLabeledTextField(GridPane grid, String labelText, int row) {
        Label label = new Label(labelText);
        TextField textField = new TextField();
        grid.addRow(row, label, textField);
        return textField;
    }

    private VBox createControlAndLogsSection() {
        VBox controlAndLogsSection = new VBox(10);

        // Control Panel
        HBox controlPanel = new HBox(10);
        startButton = new Button("Start System");
        startButton.setStyle("-fx-background-color: lightgreen;");

        stopButton = new Button("Stop System");
        stopButton.setStyle("-fx-background-color: lightcoral;");

        systemStatusLabel = new Label("System Idle");
        stopButton.setDisable(true);

        // systemStatus
        systemStatusLabel = new Label("System Status: Idle");
        systemStatusLabel.setStyle("-fx-padding: 5px;");

        // Event Handlers
        startButton.setOnAction(e -> startTicketSystem());
        stopButton.setOnAction(e -> stopTicketSystem());

        controlPanel.getChildren().addAll(startButton, stopButton);

        // System Log Area
        TitledPane systemLogsPane = new TitledPane();
        systemLogsPane.setText("System Logs");
        systemLogArea = new TextArea();
        systemLogArea.setEditable(false);
        systemLogsPane.setContent(systemLogArea);

        // Set preferred height to double the default
        systemLogArea.setPrefHeight(300);

        // Remaining Tickets Label
        remainingTicketsLabel = new Label("Remaining Tickets: 0");

        controlAndLogsSection.getChildren().addAll(
                controlPanel,
                systemStatusLabel,
                remainingTicketsLabel,
                systemLogsPane);
        return controlAndLogsSection;
    }

    private void startTicketSystem() {
        try {
            // Clear previous logs
            systemLogArea.clear();

            // Validate and create configuration
            config = validateAndCreateConfiguration();

            // Save configuration
            config.saveToFile("systemConfig.json");

            // Initialize ticket pool
            ticketPool = new TicketPool(config.getMaxTicketCapacity());
            ticketPool.initialize(config.getTotalTickets());

            // Setup log listeners
            setupTicketPoolListeners();

            // Setup threads
            executorService = Executors.newFixedThreadPool(7);
            startVendors();
            startCustomers();

            systemStatusLabel.setText("System Status: Running......");
            startButton.setDisable(true);
            stopButton.setDisable(false);
        } catch (Exception e) {
            showAlert("System Initialization Error", e.getMessage());
        }
    }

    private void setupTicketPoolListeners() {
        // Combined log listener for vendors and customers
        ticketPool.setVendorLogListener(status -> Platform.runLater(() -> {
            systemLogArea.appendText("[VENDOR] " + status + "\n");
            updateRemainingTickets();
        }));

        ticketPool.setCustomerLogListener(status -> Platform.runLater(() -> {
            systemLogArea.appendText("[CUSTOMER] " + status + "\n");
            updateRemainingTickets();
        }));

        // All tickets sold out listener
        ticketPool.setAllTicketsSoldOutListener(() -> Platform.runLater(() -> {
            systemStatusLabel.setText("All Total Tickets Have Been Sold Out");
            stopTicketSystem();
        }));
    }

    private void updateRemainingTickets() {
        remainingTicketsLabel.setText("Remaining Tickets: " + ticketPool.getRemainingTickets());
    }

    private SystemConfiguration validateAndCreateConfiguration() {
        try {
            int maxCapacity = Integer.parseInt(maxTicketCapacityField.getText());
            int totalTickets = Integer.parseInt(totalTicketsField.getText());
            double ticketReleaseRate = Double.parseDouble(ticketReleaseRateField.getText());
            double customerRetrievalRate = Double.parseDouble(customerRetrievalRateField.getText());

            SystemConfiguration newConfig = new SystemConfiguration();
            newConfig.setMaxTicketCapacity(maxCapacity);
            newConfig.setTotalTickets(totalTickets);
            newConfig.setTicketReleaseRate(ticketReleaseRate);
            newConfig.setCustomerRetrievalRate(customerRetrievalRate);

            return newConfig;
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid Configuration: " + e.getMessage());
        }
    }

    private void startVendors() {
        for (int i = 0; i < 3; i++) {
            Vendor vendor = new Vendor(ticketPool, (int) config.getTicketReleaseRate());
            executorService.submit(vendor);
        }
    }

    private void startCustomers() {
        int totalTickets = config.getTotalTickets();
        for (int i = 0; i < 4; i++) {
            Customer customer = new Customer(ticketPool, (int) config.getCustomerRetrievalRate(), totalTickets);
            executorService.submit(customer);
        }
    }

    private void stopTicketSystem() {
        if (executorService != null) {
            executorService.shutdownNow();
            systemStatusLabel.setText("System Status: Stopped");
            startButton.setDisable(false);
            stopButton.setDisable(true);
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}