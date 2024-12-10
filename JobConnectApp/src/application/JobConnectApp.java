package application;

import application.user.LoginPage;
import Backend.*;
import Backend.controllers.*;
import javafx.application.Application;
import javafx.stage.Stage;

public class JobConnectApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Initialize all the controllers
    	
    	
        UserController userController = new UserController();
        JobController jobController = new JobController();
        ApplicationController applicationController = new ApplicationController();
        NotificationController notificationController = new NotificationController();
        SupportQueryController supportQueryController = new SupportQueryController();

        // Pass controllers to the JobConnect singleton instance
        JobConnect jobConnect = JobConnect.getInstance(userController, jobController, applicationController, notificationController, supportQueryController);
        
        // Now that JobConnect is initialized with all the controllers, 
        // we can proceed with the scene management as before.
        SceneManager sceneManager = new SceneManager(primaryStage);
        
        // Add Login Page
        LoginPage loginPage = new LoginPage(sceneManager);
        sceneManager.addScene("LoginPage", loginPage.getScene());

        primaryStage.setTitle("Job Connect Application");
        sceneManager.switchTo("LoginPage");
        primaryStage.setWidth(900);
        primaryStage.setHeight(600);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
