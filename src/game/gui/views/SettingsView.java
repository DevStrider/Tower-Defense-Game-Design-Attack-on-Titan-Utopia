package game.gui.views;

import game.gui.GameSettings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

public class SettingsView extends StackPane {

    // ── AI Speed ──────────────────────────────────────────────────────
    private final Button slowBtn   = speedBtn("SLOW",   2.0);
    private final Button normalBtn = speedBtn("NORMAL", 1.2);
    private final Button fastBtn   = speedBtn("FAST",   0.4);

    // ── Toggles ───────────────────────────────────────────────────────
    private final Button titanHPBtn   = toggleBtn("TITAN HP",    GameSettings.get().isShowTitanHP());
    private final Button dangerLvlBtn = toggleBtn("DANGER LVL",  GameSettings.get().isShowDangerLevel());

    // ── Navigation ────────────────────────────────────────────────────
    private final Button backBtn = new Button("← BACK TO MENU");

    public SettingsView() {
        // ── Background ───────────────────────────────────────────
        Canvas bgCanvas = new Canvas(1200, 750);
        drawBackground(bgCanvas.getGraphicsContext2D());

        // ── Content card ─────────────────────────────────────────
        VBox card = new VBox(28);
        card.setAlignment(Pos.TOP_CENTER);
        card.setPadding(new Insets(40, 52, 40, 52));
        card.setMaxWidth(540);
        card.setStyle(
            "-fx-background-color:#1a1410;" +
            "-fx-border-color:#5c4a2a;" +
            "-fx-border-width:2;" +
            "-fx-border-radius:4;" +
            "-fx-background-radius:4;");

        // Title
        Label title = new Label("SETTINGS");
        title.setStyle("-fx-font-size:34px;-fx-font-weight:bold;-fx-text-fill:#d4b896;" +
                       "-fx-letter-spacing:6;");

        Line divider1 = hRule();

        // ── AI Speed section ─────────────────────────────────────
        Label speedTitle = sectionLabel("AI SPEED");
        HBox speedRow = new HBox(12, slowBtn, normalBtn, fastBtn);
        speedRow.setAlignment(Pos.CENTER);
        highlightSpeedBtn(GameSettings.get().getAiSpeed());

        Line divider2 = hRule();

        // ── Display section ──────────────────────────────────────
        Label dispTitle = sectionLabel("DISPLAY");
        HBox dispRow = new HBox(14, titanHPBtn, dangerLvlBtn);
        dispRow.setAlignment(Pos.CENTER);
        refreshToggle(titanHPBtn,   GameSettings.get().isShowTitanHP());
        refreshToggle(dangerLvlBtn, GameSettings.get().isShowDangerLevel());

        Line divider3 = hRule();

        // ── Back ─────────────────────────────────────────────────
        backBtn.setStyle(
            "-fx-background-color:transparent;-fx-text-fill:#8a7356;" +
            "-fx-font-size:13px;-fx-cursor:hand;-fx-underline:true;");

        card.getChildren().addAll(
            title, divider1,
            speedTitle, speedRow, divider2,
            dispTitle, dispRow, divider3,
            backBtn);

        getChildren().addAll(bgCanvas, card);
        StackPane.setAlignment(card, Pos.CENTER);

        wireEvents();
    }

    // ── Wiring ───────────────────────────────────────────────────────

    private void wireEvents() {
        slowBtn.setOnAction(e -> {
            GameSettings.get().setAiSpeed(2.0);
            highlightSpeedBtn(2.0);
        });
        normalBtn.setOnAction(e -> {
            GameSettings.get().setAiSpeed(1.2);
            highlightSpeedBtn(1.2);
        });
        fastBtn.setOnAction(e -> {
            GameSettings.get().setAiSpeed(0.4);
            highlightSpeedBtn(0.4);
        });

        titanHPBtn.setOnAction(e -> {
            boolean v = !GameSettings.get().isShowTitanHP();
            GameSettings.get().setShowTitanHP(v);
            refreshToggle(titanHPBtn, v);
        });
        dangerLvlBtn.setOnAction(e -> {
            boolean v = !GameSettings.get().isShowDangerLevel();
            GameSettings.get().setShowDangerLevel(v);
            refreshToggle(dangerLvlBtn, v);
        });
    }

    // ── Public ───────────────────────────────────────────────────────

    public Button getBackButton() { return backBtn; }

    // ── Private helpers ──────────────────────────────────────────────

    private void highlightSpeedBtn(double speed) {
        slowBtn  .setStyle(speedStyle(speed == 2.0));
        normalBtn.setStyle(speedStyle(speed == 1.2));
        fastBtn  .setStyle(speedStyle(speed == 0.4));
    }

    private void refreshToggle(Button btn, boolean on) {
        String label = btn.getText().replaceAll(": .*", "");
        btn.setText(label + ": " + (on ? "ON" : "OFF"));
        btn.setStyle(on ? toggleActiveStyle() : toggleInactiveStyle());
    }

    private String speedStyle(boolean selected) {
        if (selected)
            return "-fx-background-color:#5c4a2a;-fx-text-fill:#d4b896;" +
                   "-fx-font-size:13px;-fx-font-weight:bold;" +
                   "-fx-padding:9 26;-fx-background-radius:3;-fx-cursor:hand;" +
                   "-fx-border-color:#8a7356;-fx-border-width:1;-fx-border-radius:3;";
        return "-fx-background-color:#0d0b08;-fx-text-fill:#5c4a2a;" +
               "-fx-font-size:13px;-fx-font-weight:bold;" +
               "-fx-padding:9 26;-fx-background-radius:3;-fx-cursor:hand;" +
               "-fx-border-color:#3d2e18;-fx-border-width:1;-fx-border-radius:3;";
    }

    private String toggleActiveStyle() {
        return "-fx-background-color:#3a5228;-fx-text-fill:#a8d878;" +
               "-fx-font-size:12px;-fx-font-weight:bold;" +
               "-fx-padding:9 22;-fx-background-radius:3;-fx-cursor:hand;" +
               "-fx-border-color:#4a6a35;-fx-border-width:1;-fx-border-radius:3;";
    }

    private String toggleInactiveStyle() {
        return "-fx-background-color:#0d0b08;-fx-text-fill:#5c4a2a;" +
               "-fx-font-size:12px;-fx-font-weight:bold;" +
               "-fx-padding:9 22;-fx-background-radius:3;-fx-cursor:hand;" +
               "-fx-border-color:#3d2e18;-fx-border-width:1;-fx-border-radius:3;";
    }

    private static Button speedBtn(String label, double speed) {
        Button b = new Button(label);
        return b;
    }

    private static Button toggleBtn(String label, boolean on) {
        Button b = new Button(label + ": " + (on ? "ON" : "OFF"));
        return b;
    }

    private Label sectionLabel(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-font-size:13px;-fx-font-weight:bold;-fx-text-fill:#8a7356;-fx-letter-spacing:3;");
        return l;
    }

    private Line hRule() {
        Line l = new Line(0, 0, 440, 0);
        l.setStroke(Color.web("#3d2e18"));
        l.setStrokeWidth(1);
        return l;
    }

    private void drawBackground(GraphicsContext gc) {
        int W = 1200, H = 750;
        // Dark sky
        gc.setFill(Color.web("#06080d"));
        gc.fillRect(0, 0, W, H);
        // Subtle wall silhouette at bottom
        gc.setFill(Color.web("#0d0b08"));
        gc.fillRect(0, H - 120, W, 120);
        gc.setFill(Color.web("#1a1208", 0.5));
        for (int x = 0; x < W; x += 80) {
            double battlementH = 30 + (x % 3) * 10;
            gc.fillRect(x, H - 120 - battlementH, 60, battlementH);
        }
        // Vignette
        for (int i = 0; i < 80; i++) {
            gc.setFill(Color.web("#000000", 0.02 * (80 - i) / 80.0 * 4));
            gc.fillRect(i, i, W - i*2, H - i*2);
        }
    }
}
