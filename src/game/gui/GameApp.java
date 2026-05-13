package game.gui;

import game.engine.Battle;
import game.gui.controllers.BattleController;
import game.gui.controllers.SettingsController;
import game.gui.controllers.StartController;
import game.gui.views.BattleView;
import game.gui.views.GameOverView;
import game.gui.views.SettingsView;
import game.gui.views.StartView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class GameApp extends Application {

    public static Stage primaryStage;

    private static final int WIDTH  = 1200;
    private static final int HEIGHT = 750;

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        stage.setTitle("Attack on Titan: Utopia");
        stage.setResizable(false);
        showStartScreen();
        stage.show();
    }

    public static void showStartScreen() {
        StartView view = new StartView();
        new StartController(view);
        primaryStage.setScene(new Scene(view, WIDTH, HEIGHT));
    }

    public static void showBattleScreen(Battle battle) {
        BattleView view = new BattleView();
        new BattleController(battle, view);
        primaryStage.setScene(new Scene(view, WIDTH, HEIGHT));
    }

    public static void showSettingsScreen() {
        SettingsView view = new SettingsView();
        new SettingsController(view);
        primaryStage.setScene(new Scene(view, WIDTH, HEIGHT));
    }

    public static void showGameOverScreen(int score) {
        GameOverView view = new GameOverView(score);
        primaryStage.setScene(new Scene(view, WIDTH, HEIGHT));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
