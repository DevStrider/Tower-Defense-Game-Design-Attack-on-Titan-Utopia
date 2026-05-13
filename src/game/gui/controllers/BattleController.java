package game.gui.controllers;

import game.engine.Battle;
import game.engine.lanes.Lane;
import game.engine.weapons.WeaponRegistry;
import game.engine.exceptions.InsufficientResourcesException;
import game.engine.exceptions.InvalidLaneException;
import game.gui.GameApp;
import game.gui.GameSettings;
import game.gui.views.BattleView;
import game.gui.views.LaneView;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.util.Duration;

import java.util.Map;

public class BattleController {

    private final Battle     battle;
    private final BattleView view;
    private       Timeline   aiTimeline;

    public BattleController(Battle battle, BattleView view) {
        this.battle = battle;
        this.view   = view;

        view.initializeLanes(battle);
        view.initializeWeaponShop(battle);
        view.refreshHUD(battle);

        wireEvents();
    }

    // ── Event wiring ─────────────────────────────────────────────────

    private void wireEvents() {
        // Lane click
        for (LaneView lv : view.getLaneViews()) {
            lv.setOnMouseClicked(e -> {
                if (!lv.getLane().isLaneLost())
                    view.selectLane(lv);
            });
        }

        // Weapon SELECT buttons
        for (Map.Entry<Integer, javafx.scene.control.Button> entry : view.getWeaponButtons().entrySet()) {
            int code = entry.getKey();
            entry.getValue().setOnAction(e -> view.selectWeapon(code));
        }

        // Pass Turn
        view.getPassTurnButton().setOnAction(e -> {
            battle.passTurn();
            refresh();
        });

        // Buy / Deploy Weapon
        view.getBuyWeaponButton().setOnAction(e -> handleBuy());

        // AI toggle
        view.getAIToggleButton().setOnAction(e -> toggleAI());

        // In-game settings button
        view.getSettingsButton().setOnAction(e -> openOverlay());

        // ── Overlay buttons ───────────────────────────────────────
        view.getResumeButton().setOnAction(e -> closeOverlay());

        view.getResetButton().setOnAction(e -> {
            stopAI();
            GameApp.showStartScreen();
        });

        view.getQuitButton().setOnAction(e -> Platform.exit());

        // AI speed inside overlay
        view.getOSlowBtn().setOnAction(e -> {
            GameSettings.get().setAiSpeed(2.0);
            view.syncSpeedButtons();
            restartAIIfRunning();
        });
        view.getONormalBtn().setOnAction(e -> {
            GameSettings.get().setAiSpeed(1.2);
            view.syncSpeedButtons();
            restartAIIfRunning();
        });
        view.getOFastBtn().setOnAction(e -> {
            GameSettings.get().setAiSpeed(0.4);
            view.syncSpeedButtons();
            restartAIIfRunning();
        });

        // Display toggles inside overlay
        view.getOTitanHPBtn().setOnAction(e -> {
            boolean v = !GameSettings.get().isShowTitanHP();
            GameSettings.get().setShowTitanHP(v);
            view.refreshOverlayToggle(view.getOTitanHPBtn(), "TITAN HP", v);
            view.refreshLanes();
        });
        view.getODangerBtn().setOnAction(e -> {
            boolean v = !GameSettings.get().isShowDangerLevel();
            GameSettings.get().setShowDangerLevel(v);
            view.refreshOverlayToggle(view.getODangerBtn(), "DANGER LVL", v);
            view.refreshLanes();
        });
    }

    // ── Overlay open / close ─────────────────────────────────────────

    private void openOverlay() {
        // Pause AI while settings is open
        if (aiTimeline != null) aiTimeline.pause();
        view.showSettingsOverlay();
    }

    private void closeOverlay() {
        view.hideSettingsOverlay();
        // Resume AI if it was running
        if (view.isAIMode() && aiTimeline != null) aiTimeline.play();
    }

    // ── Buy logic ────────────────────────────────────────────────────

    private void handleBuy() {
        LaneView lv = view.getSelectedLaneView();
        int code    = view.getSelectedWeaponCode();
        if (lv == null || code == -1) return;

        try {
            battle.purchaseWeapon(code, lv.getLane());
            refresh();
        } catch (InsufficientResourcesException ex) {
            showAlert("Insufficient Resources",
                    "Not enough resources!\nYou have " + battle.getResourcesGathered() + " resources.");
        } catch (InvalidLaneException ex) {
            showAlert("Invalid Lane", "Cannot deploy a weapon to a lost lane!");
        }
    }

    // ── AI ───────────────────────────────────────────────────────────

    private void toggleAI() {
        boolean turnOn = !view.isAIMode();
        view.setAIMode(turnOn);

        if (turnOn) {
            view.getPassTurnButton().setDisable(true);
            view.getBuyWeaponButton().setDisable(true);

            aiTimeline = new Timeline(new KeyFrame(
                Duration.seconds(GameSettings.get().getAiSpeed()), ev -> {
                    if (battle.isGameOver()) {
                        stopAI();
                        Platform.runLater(() -> GameApp.showGameOverScreen(battle.getScore()));
                        return;
                    }
                    performAIAction();
                    Platform.runLater(this::refresh);
                }));
            aiTimeline.setCycleCount(Timeline.INDEFINITE);
            aiTimeline.play();
        } else {
            stopAI();
        }
    }

    private void stopAI() {
        if (aiTimeline != null) {
            aiTimeline.stop();
            aiTimeline = null;
        }
        view.setAIMode(false);
        view.getPassTurnButton().setDisable(false);
    }

    private void restartAIIfRunning() {
        if (!view.isAIMode()) return;
        if (aiTimeline != null) aiTimeline.stop();
        aiTimeline = new Timeline(new KeyFrame(
            Duration.seconds(GameSettings.get().getAiSpeed()), ev -> {
                if (battle.isGameOver()) {
                    stopAI();
                    Platform.runLater(() -> GameApp.showGameOverScreen(battle.getScore()));
                    return;
                }
                performAIAction();
                Platform.runLater(this::refresh);
            }));
        aiTimeline.setCycleCount(Timeline.INDEFINITE);
        // Only play if overlay is closed
        if (!view.isOverlayVisible()) aiTimeline.play();
    }

    private void performAIAction() {
        Lane targetLane = null;
        int maxDanger   = -1;
        int minWallHP   = Integer.MAX_VALUE;

        for (Lane lane : battle.getOriginalLanes()) {
            if (lane.isLaneLost()) continue;
            int danger = lane.getDangerLevel();
            int wallHP = lane.getLaneWall().getCurrentHealth();
            if (danger > maxDanger || (danger == maxDanger && wallHP < minWallHP)) {
                maxDanger  = danger;
                minWallHP  = wallHP;
                targetLane = lane;
            }
        }

        if (targetLane == null) { battle.passTurn(); return; }

        Map<Integer, WeaponRegistry> shop = battle.getWeaponFactory().getWeaponShop();
        int    bestCode  = -1;
        double bestRatio = -1.0;

        for (Map.Entry<Integer, WeaponRegistry> entry : shop.entrySet()) {
            WeaponRegistry reg = entry.getValue();
            if (reg.getPrice() <= battle.getResourcesGathered()) {
                double ratio = (double) reg.getDamage() / reg.getPrice();
                if (ratio > bestRatio) { bestRatio = ratio; bestCode = entry.getKey(); }
            }
        }

        if (bestCode == -1) { battle.passTurn(); return; }

        try {
            battle.purchaseWeapon(bestCode, targetLane);
        } catch (Exception ex) {
            battle.passTurn();
        }
    }

    // ── Refresh ──────────────────────────────────────────────────────

    private void refresh() {
        view.refreshHUD(battle);
        view.refreshLanes();

        LaneView sel = view.getSelectedLaneView();
        if (sel != null && sel.getLane().isLaneLost())
            view.selectLane(null);

        if (battle.isGameOver()) {
            if (view.isAIMode()) stopAI();
            GameApp.showGameOverScreen(battle.getScore());
        }
    }

    // ── Helpers ──────────────────────────────────────────────────────

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
