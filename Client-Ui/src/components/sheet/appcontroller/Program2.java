    package components.sheet.appcontroller;

    import components.login.LoginController;
    import components.sheet.gridcontroller.GridController;
    import components.sheet.headerconroller.HeaderController;
    import components.sheet.leftsidecontroller.LeftSideController;
    import javafx.application.Application;
    import javafx.fxml.FXMLLoader;
    import javafx.scene.Parent;
    import javafx.scene.Scene;
    import javafx.scene.control.Label;
    import javafx.scene.control.ScrollPane;
    import javafx.scene.layout.BorderPane;
    import javafx.scene.layout.StackPane;
    import javafx.stage.Stage;

    import java.net.URL;

    public class Program2 extends Application {
        public static void main(String[] args) {
            launch(args);  // This launches the JavaFX application
        }
//        @Override
//        public void start(Stage primaryStage) throws Exception {
//            primaryStage.setTitle("Sheet-Cell");
//
//            // Load the main layout (StackPane with ScrollPane) FXML file and get its controller
//            FXMLLoader mainLoader = new FXMLLoader();
//            URL mainUrl = getClass().getResource("/components/sheet/appcontroller/borderPaneBase.fxml");
//            mainLoader.setLocation(mainUrl);
//            assert mainUrl != null;
//            StackPane root = mainLoader.load();  // Load and cast to StackPane
//            components.sheet.appcontroller.AppController appController = mainLoader.getController();  // Get the AppController
//
//            // Retrieve the ScrollPane and the BorderPane from the FXML
//            ScrollPane scrollPane = (ScrollPane) root.getChildren().get(0);
//            BorderPane borderPane = (BorderPane) scrollPane.getContent();
//
//            // Load header FXML and get its controller
//            FXMLLoader headerLoader = new FXMLLoader();
//            URL headerUrl = getClass().getResource("/components/sheet/headerconroller/header.fxml");
//            headerLoader.setLocation(headerUrl);
//            Parent headerRoot = headerLoader.load();  // Load the header FXML
//            HeaderController headerController = headerLoader.getController();  // Get the HeaderController
//
//            // Load grid FXML and get its controller
//            FXMLLoader gridLoader = new FXMLLoader();
//            URL gridUrl = getClass().getResource("/components/sheet/gridcontroller/grid.fxml");
//            gridLoader.setLocation(gridUrl);
//            Parent gridRoot = gridLoader.load();  // Load the grid FXML
//            GridController gridController = gridLoader.getController();  // Get the GridController
//
//            // Load left-side FXML and get its controller
//            FXMLLoader leftSideLoader = new FXMLLoader();
//            URL leftSideUrl = getClass().getResource("/components/sheet/leftsidecontroller/leftside.fxml");
//            leftSideLoader.setLocation(leftSideUrl);
//            Parent leftSideRoot = leftSideLoader.load();  // Load the left-side FXML
//            LeftSideController leftSideController = leftSideLoader.getController();  // Get the LeftSideController
//
//            // Set the loaded components to the BorderPane
//            borderPane.setTop(headerRoot);
//            borderPane.setCenter(gridRoot);
//            borderPane.setLeft(leftSideRoot);
//
//            // Create a label to display in the middle of the StackPane before the sheet is loaded
//            Label initialLabel = new Label("Welcome to Sheet-Cell! Please load your XML file.");
//            initialLabel.setStyle("-fx-font-size: 24px; -fx-text-fill: INDIANRED; -fx-padding: 0 0 0 200;"); // Added padding to the left
//            root.getChildren().add(initialLabel);  // Add the label to the StackPane
//
//            // Pass the controllers to the AppController
//            appController.setGridController(gridController);
//            appController.setHeaderController(headerController);
//            appController.setLeftSideController(leftSideController);
//            appController.setInitialLabel(initialLabel);  // Pass the label to the AppController
//
//            // Initialize the AppController
//            appController.initialize();
//
//            // Set up the scene with the StackPane (which contains the ScrollPane) as the root
//            Scene scene = new Scene(root, 950, 700); // Set the initial size to 900x650
//            primaryStage.setScene(scene);
//            appController.setPrimaryStage(primaryStage);
//            primaryStage.show();
//        }
@Override
public void start(Stage primaryStage) throws Exception {
    primaryStage.setTitle("Login");

    // Load login FXML
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/components/login/login.fxml"));
    Parent root = loader.load();

    // Get controller and pass primaryStage
    LoginController loginController = loader.getController();
    loginController.setPrimaryStage(primaryStage);

    // Show login scene
    Scene scene = new Scene(root, 400, 300);
    primaryStage.setScene(scene);
    primaryStage.show();
}

    }
