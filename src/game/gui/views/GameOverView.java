package game.gui.views;

import game.gui.GameApp;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

public class GameOverView extends StackPane {

    public GameOverView(int score) {
        // ── Background ───────────────────────────────────────────
        Canvas bg = new Canvas(1200, 750);
        drawBackground(bg.getGraphicsContext2D());

        // ── Content ──────────────────────────────────────────────
        VBox content = new VBox(20);
        content.setAlignment(Pos.CENTER);
        content.setMaxWidth(560);
        content.setPadding(new Insets(50, 60, 50, 60));
        content.setStyle(
            "-fx-background-color:#0d0b08cc;" +
            "-fx-border-color:#3d2e18;" +
            "-fx-border-width:1;" +
            "-fx-border-radius:4;" +
            "-fx-background-radius:4;");

        // Top band
        VBox topBand = new VBox(8);
        topBand.setAlignment(Pos.CENTER);
        topBand.setPadding(new Insets(0, 0, 12, 0));

        Label gameOverLabel = new Label("THE WALLS HAVE FALLEN");
        gameOverLabel.setStyle(
            "-fx-font-size:26px;-fx-font-weight:bold;-fx-text-fill:#8b1a1a;" +
            "-fx-letter-spacing:3;");
        gameOverLabel.setWrapText(true);
        gameOverLabel.setAlignment(Pos.CENTER);

        Label flavourLabel = new Label("Humanity retreats once more into darkness...");
        flavourLabel.setStyle("-fx-font-size:14px;-fx-text-fill:#5c4a2a;-fx-font-style:italic;");

        Line divTop = hRule(430);
        topBand.getChildren().addAll(gameOverLabel, flavourLabel, divTop);

        // Score
        Label scoreTitleLabel = new Label("FINAL SCORE");
        scoreTitleLabel.setStyle(
            "-fx-font-size:11px;-fx-text-fill:#5c4a2a;-fx-letter-spacing:4;-fx-font-weight:bold;");

        Label scoreLabel = new Label(String.valueOf(score));
        scoreLabel.setStyle(
            "-fx-font-size:72px;-fx-font-weight:bold;-fx-text-fill:#d4b896;");

        Line divBot = hRule(430);

        // Buttons
        Button retryBtn = new Button("TRY AGAIN");
        retryBtn.setStyle(
            "-fx-background-color:#8b1a1a;-fx-text-fill:#e8d5b0;" +
            "-fx-font-size:15px;-fx-font-weight:bold;" +
            "-fx-padding:12 42;-fx-background-radius:3;-fx-cursor:hand;" +
            "-fx-border-color:#c0392b;-fx-border-width:0 0 2 0;-fx-border-radius:3;");
        retryBtn.setOnAction(e -> GameApp.showStartScreen());

        Button menuBtn = new Button("MAIN MENU");
        menuBtn.setStyle(
            "-fx-background-color:transparent;-fx-text-fill:#5c4a2a;" +
            "-fx-font-size:12px;-fx-font-weight:bold;" +
            "-fx-padding:10 30;-fx-cursor:hand;" +
            "-fx-border-color:#3d2e18;-fx-border-width:1;-fx-border-radius:3;");
        menuBtn.setOnAction(e -> GameApp.showStartScreen());

        HBox btnRow = new HBox(14, retryBtn, menuBtn);
        btnRow.setAlignment(Pos.CENTER);

        content.getChildren().addAll(topBand, scoreTitleLabel, scoreLabel, divBot, btnRow);

        getChildren().addAll(bg, content);
        StackPane.setAlignment(content, Pos.CENTER);
    }

    private Line hRule(double w) {
        Line l = new Line(0, 0, w, 0);
        l.setStroke(Color.web("#3d2e18"));
        l.setStrokeWidth(1);
        return l;
    }

    private void drawBackground(GraphicsContext gc) {
        int W = 1200, H = 750;

        // Near-black background
        gc.setFill(Color.web("#04030a"));
        gc.fillRect(0, 0, W, H);

        // Red ambient from below (wall is burning)
        for (int y = H; y > H - 200; y--) {
            double t = (H - y) / 200.0;
            gc.setFill(Color.web("#4a0808", t * 0.6));
            gc.fillRect(0, y, W, 1);
        }

        // Dark wall silhouette (broken/crumbled)
        gc.setFill(Color.web("#0a0806"));
        // Left section (intact)
        for (int x = 0; x < 250; x += 100) {
            gc.fillRect(x, H - 220, 80, 220);
        }
        // Gap in middle (broken wall)
        gc.setFill(Color.web("#0a0806"));
        // Right section (crumbled)
        for (int x = 600; x < W; x += 100) {
            gc.fillRect(x, H - 180, 80, 180);
        }
        // Rubble/debris at bottom
        gc.setFill(Color.web("#0d0b08"));
        gc.fillRect(0, H - 80, W, 80);
        // Crumbled stone pieces
        gc.setFill(Color.web("#1a1208"));
        int[] rubbleX = {200, 280, 350, 430, 510, 580};
        int[] rubbleH = {40, 25, 50, 30, 20, 45};
        for (int i = 0; i < rubbleX.length; i++) {
            gc.fillRect(rubbleX[i], H - 80 - rubbleH[i], 60, rubbleH[i]);
        }

        // Glowing ember particles
        gc.setFill(Color.web("#cc4400", 0.6));
        int[] ex = {320, 480, 560, 380, 260, 440, 520};
        int[] ey = {H-100, H-120, H-90, H-140, H-110, H-130, H-105};
        for (int i = 0; i < ex.length; i++) {
            gc.fillOval(ex[i], ey[i], 3, 3);
        }

        // Vignette
        for (int i = 0; i < 50; i++) {
            gc.setFill(Color.web("#000000", 0.015 * (50 - i) / 12.5));
            gc.fillRect(i, i, W - i*2, H - i*2);
        }
    }
}
