package application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.HashMap;

public class SceneManager {
    private final Stage stage; // The primary stage for the application
    private final HashMap<String, Scene> scenes; // Stores scenes with their names
    private final HashMap<String, Object> sharedData; // Stores shared data for the app (e.g., user info)

    public SceneManager(Stage stage) {
        this.stage = stage;
        this.scenes = new HashMap<>();
        this.sharedData = new HashMap<>();
    }

    /**
     * Adds a new scene to the SceneManager.
     *
     * @param name  The name of the scene (unique identifier).
     * @param scene The scene to be added.
     */
    public void addScene(String name, Scene scene) {
        scenes.put(name, scene);
    }

    /**
     * Switches to a specified scene by its name.
     *
     * @param name The name of the scene to switch to.
     */
    public void switchTo(String name) {
        Scene scene = scenes.get(name);
        if (scene != null) {
            stage.setScene(scene);
        } else {
            System.out.println("Scene " + name + " not found!");
        }
    }

    /**
     * Adds or updates shared data.
     *
     * @param key   The key for the data.
     * @param value The value to store.
     */
    public void setSharedData(String key, Object value) {
        sharedData.put(key, value);
    }

    /**
     * Retrieves shared data by its key.
     *
     * @param key The key of the data.
     * @return The value associated with the key, or null if not found.
     */
    public Object getSharedData(String key) {
        return sharedData.get(key);
    }

    /**
     * Removes a scene from the SceneManager.
     *
     * @param name The name of the scene to remove.
     */
    public void removeScene(String name) {
        scenes.remove(name);
    }

    /**
     * Retrieves the primary stage.
     *
     * @return The primary stage.
     */
    public Stage getStage() {
        return stage;
    }

    /**
     * Checks if a scene exists.
     *
     * @param name The name of the scene to check.
     * @return True if the scene exists, false otherwise.
     */
    public boolean hasScene(String name) {
        return scenes.containsKey(name);
    }
}
