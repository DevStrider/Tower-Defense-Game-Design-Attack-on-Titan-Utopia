package game.gui.controllers;

import game.engine.Battle;
import game.gui.GameApp;
import game.gui.GameConstants;
import game.gui.views.StartView;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;

import java.io.IOException;

public class StartController {

    public StartController(StartView view) {
        view.getStartButton().setOnAction(e -> startGame(view));
        view.getInstructionsButton().setOnAction(e -> showInstructions());
        view.getSettingsButton().setOnAction(e -> GameApp.showSettingsScreen());
    }

    private void startGame(StartView view) {
        try {
            boolean easy   = view.isEasyModeSelected();
            int numLanes   = easy ? 3 : 5;
            int resources  = easy ? 250 : 125;
            Battle battle  = new Battle(1, 0, GameConstants.TITAN_SPAWN_DISTANCE, numLanes, resources);
            GameApp.showBattleScreen(battle);
        } catch (IOException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR,
                    "Failed to load game data:\n" + ex.getMessage());
            alert.setTitle("Load Error");
            alert.showAndWait();
        }
    }

    private void showInstructions() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("How to Play");
        alert.setHeaderText("Attack on Titan: Utopia — Game Instructions");

        TextArea ta = new TextArea(
            "OBJECTIVE\n" +
            "Protect your lane walls from incoming titans by buying and deploying weapons.\n\n" +
            "HOW TO BUY A WEAPON\n" +
            "  1. Click a lane to select it (green border).\n" +
            "  2. Click SELECT on a weapon in the shop.\n" +
            "  3. Click BUY WEAPON — the weapon is added to that lane.\n\n" +
            "PASSING A TURN\n" +
            "  Click PASS TURN to skip buying and let the game advance.\n\n" +
            "TITAN TYPES\n" +
            "  Red     — Pure Titan        (standard)\n" +
            "  Orange  — Abnormal Titan    (attacks twice per turn)\n" +
            "  Steel   — Armored Titan     (takes only 1/4 damage)\n" +
            "  Purple  — Colossal Titan    (speeds up each turn; very tough)\n\n" +
            "WEAPONS\n" +
            "  ATS  Anti Titan Shell     — Hits the closest 5 titans  ($25)\n" +
            "  LRS  Long Range Spear     — Snipes the closest titan    ($25)\n" +
            "  WSC  Wall Spread Cannon   — Hits titans in range 20-50 ($100)\n" +
            "  PT   Proximity Trap       — Hits titan only at the wall ($75)\n\n" +
            "PHASES\n" +
            "  Early    (turns  1-14) — Light waves\n" +
            "  Intense  (turns 15-29) — Heavier waves\n" +
            "  Grumbling (turn 30+)   — Every 5 turns titan count doubles!\n\n" +
            "AI MODE\n" +
            "  Toggle the AI button in the top-right to let the computer play for you.\n\n" +
            "GAME OVER\n" +
            "  All lane walls are destroyed."
        );
        ta.setEditable(false);
        ta.setWrapText(true);
        ta.setPrefSize(520, 370);
        alert.getDialogPane().setContent(ta);
        alert.showAndWait();
    }
}
