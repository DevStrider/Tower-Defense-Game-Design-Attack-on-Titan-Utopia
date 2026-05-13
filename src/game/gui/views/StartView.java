package game.gui.views;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

public class StartView extends StackPane {

    private final RadioButton easyButton;
    private final RadioButton hardButton;
    private final Button      startButton;
    private final Button      instructionsButton;
    private final Button      settingsButton;

    public StartView() {
        // ── Atmospheric background ────────────────────────────────
        Canvas bg = new Canvas(1200, 750);
        drawBackground(bg.getGraphicsContext2D());

        // ── Central card ─────────────────────────────────────────
        VBox card = new VBox(0);
        card.setAlignment(Pos.TOP_CENTER);
        card.setMaxWidth(520);
        card.setStyle(
            "-fx-background-color:#0d0b08cc;" +
            "-fx-border-color:#5c4a2a;" +
            "-fx-border-width:1;" +
            "-fx-border-radius:3;" +
            "-fx-background-radius:3;");

        // ── Header band ───────────────────────────────────────────
        VBox header = new VBox(4);
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(32, 40, 20, 40));
        header.setStyle("-fx-background-color:#1a1208;");

        Label titleTop = new Label("ATTACK ON TITAN");
        titleTop.setStyle(
            "-fx-font-size:38px;-fx-font-weight:bold;-fx-text-fill:#8b1a1a;" +
            "-fx-letter-spacing:4;");

        Label titleSub = new Label("U  T  O  P  I  A");
        titleSub.setStyle(
            "-fx-font-size:16px;-fx-font-weight:bold;-fx-text-fill:#d4b896;" +
            "-fx-letter-spacing:8;");

        // Wing-of-freedom style decorative line
        HBox deco = new HBox(8);
        deco.setAlignment(Pos.CENTER);
        deco.setPadding(new Insets(8, 0, 0, 0));
        Line decoL = hLine(160); Line decoR = hLine(160);
        Label decoMid = new Label("✦");
        decoMid.setStyle("-fx-text-fill:#5c4a2a;-fx-font-size:12px;");
        deco.getChildren().addAll(decoL, decoMid, decoR);

        header.getChildren().addAll(titleTop, titleSub, deco);

        // ── Body (difficulty + buttons) ───────────────────────────
        VBox body = new VBox(22);
        body.setAlignment(Pos.CENTER);
        body.setPadding(new Insets(28, 48, 32, 48));

        // Difficulty
        Label modeLabel = new Label("SELECT DIFFICULTY");
        modeLabel.setStyle(
            "-fx-font-size:11px;-fx-font-weight:bold;-fx-text-fill:#8a7356;-fx-letter-spacing:3;");

        ToggleGroup group = new ToggleGroup();

        easyButton = styledRadio(
            "EASY  —  3 Lanes  ·  750 Resources",
            "#4a7c59", group, true);
        hardButton = styledRadio(
            "HARD  —  5 Lanes  ·  625 Resources",
            "#8b1a1a", group, false);

        VBox diffBox = new VBox(10, easyButton, hardButton);
        diffBox.setPadding(new Insets(10, 16, 10, 16));
        diffBox.setStyle(
            "-fx-background-color:#0d0b08;" +
            "-fx-border-color:#3d2e18;-fx-border-width:1;-fx-border-radius:2;-fx-background-radius:2;");

        // Buttons
        startButton = new Button("START GAME");
        startButton.setMaxWidth(Double.MAX_VALUE);
        startButton.setStyle(
            "-fx-background-color:#8b1a1a;-fx-text-fill:#e8d5b0;" +
            "-fx-font-size:17px;-fx-font-weight:bold;" +
            "-fx-padding:14 0;-fx-background-radius:2;-fx-cursor:hand;" +
            "-fx-border-color:#c0392b;-fx-border-width:0 0 2 0;-fx-border-radius:2;");

        instructionsButton = new Button("HOW TO PLAY");
        instructionsButton.setMaxWidth(Double.MAX_VALUE);
        instructionsButton.setStyle(
            "-fx-background-color:transparent;-fx-text-fill:#8a7356;" +
            "-fx-font-size:13px;-fx-font-weight:bold;" +
            "-fx-padding:10 0;-fx-cursor:hand;" +
            "-fx-border-color:#3d2e18;-fx-border-width:1;-fx-border-radius:2;");

        settingsButton = new Button("SETTINGS");
        settingsButton.setMaxWidth(Double.MAX_VALUE);
        settingsButton.setStyle(
            "-fx-background-color:transparent;-fx-text-fill:#5c4a2a;" +
            "-fx-font-size:12px;" +
            "-fx-padding:8 0;-fx-cursor:hand;" +
            "-fx-border-color:#2a2018;-fx-border-width:1;-fx-border-radius:2;");

        body.getChildren().addAll(modeLabel, diffBox, startButton, instructionsButton, settingsButton);

        card.getChildren().addAll(header, body);

        // ── Version note ──────────────────────────────────────────
        Label version = new Label("Tower Defence  ·  Utopia Edition");
        version.setStyle("-fx-font-size:10px;-fx-text-fill:#3d2e18;");

        VBox outer = new VBox(14, card, version);
        outer.setAlignment(Pos.CENTER);

        getChildren().addAll(bg, outer);
        StackPane.setAlignment(outer, Pos.CENTER);
    }

    // ── Getters ──────────────────────────────────────────────────────

    public boolean isEasyModeSelected()    { return easyButton.isSelected(); }
    public Button  getStartButton()        { return startButton; }
    public Button  getInstructionsButton() { return instructionsButton; }
    public Button  getSettingsButton()     { return settingsButton; }

    // ── Private helpers ──────────────────────────────────────────────

    private RadioButton styledRadio(String text, String color,
                                    ToggleGroup group, boolean selected) {
        RadioButton rb = new RadioButton(text);
        rb.setToggleGroup(group);
        rb.setSelected(selected);
        rb.setStyle("-fx-font-size:13px;-fx-text-fill:" + color + ";-fx-cursor:hand;");
        return rb;
    }

    private Line hLine(double w) {
        Line l = new Line(0, 0, w, 0);
        l.setStroke(Color.web("#3d2e18"));
        l.setStrokeWidth(1);
        return l;
    }

    private void drawBackground(GraphicsContext gc) {
        int W = 1200, H = 750;

        // Deep black-brown sky
        gc.setFill(Color.web("#07060a"));
        gc.fillRect(0, 0, W, H);

        // Moon / pale light source (top right)
        gc.setFill(Color.web("#e8d5b0", 0.05));
        gc.fillOval(900, 30, 200, 200);
        gc.setFill(Color.web("#e8d5b0", 0.08));
        gc.fillOval(930, 60, 140, 140);

        // Distant wall silhouette ─ three layers of depth
        // Far background wall (lighter = further away)
        gc.setFill(Color.web("#141008", 0.7));
        for (int x = 0; x < W; x += 100) {
            gc.fillRect(x + 5, H - 220, 80, 220);
        }
        // Battlement teeth on far wall
        gc.setFill(Color.web("#141008", 0.7));
        for (int x = 0; x < W; x += 100) {
            gc.fillRect(x + 5, H - 260, 30, 40);
            gc.fillRect(x + 55, H - 250, 30, 30);
        }

        // Mid-ground wall (darker)
        gc.setFill(Color.web("#0d0a06"));
        for (int x = -40; x < W; x += 120) {
            gc.fillRect(x, H - 160, 95, 160);
        }
        gc.setFill(Color.web("#0d0a06"));
        for (int x = -40; x < W; x += 120) {
            gc.fillRect(x + 5, H - 200, 35, 40);
            gc.fillRect(x + 60, H - 192, 35, 32);
        }

        // Ground (dark earth)
        gc.setFill(Color.web("#080605"));
        gc.fillRect(0, H - 80, W, 80);

        // Fog at the base of the walls
        for (int y = H - 80; y < H; y++) {
            double alpha = 0.3 * (y - (H - 80)) / 80.0;
            gc.setFill(Color.web("#1a1a28", alpha));
            gc.fillRect(0, y, W, 1);
        }

        // Subtle scanline vignette
        for (int i = 0; i < 60; i++) {
            double alpha = 0.015 * (60 - i) / 15.0;
            gc.setFill(Color.web("#000000", alpha));
            gc.fillRect(i, i, W - i * 2, H - i * 2);
        }
    }
}
