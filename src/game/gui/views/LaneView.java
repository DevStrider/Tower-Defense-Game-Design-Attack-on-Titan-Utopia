package game.gui.views;

import game.engine.base.Wall;
import game.engine.lanes.Lane;
import game.engine.titans.Titan;
import game.engine.weapons.*;
import game.gui.GameConstants;
import game.gui.GameSettings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class LaneView extends VBox {

    private static final String STYLE_NORMAL =
        "-fx-background-color:#1a1208;" +
        "-fx-background-radius:6;-fx-border-color:#3d2e18;" +
        "-fx-border-radius:6;-fx-border-width:2;";
    private static final String STYLE_SELECTED =
        "-fx-background-color:#1a1208;" +
        "-fx-background-radius:6;-fx-border-color:#8b1a1a;" +
        "-fx-border-radius:6;-fx-border-width:3;";
    private static final String STYLE_LOST =
        "-fx-background-color:#0d0806;" +
        "-fx-background-radius:6;-fx-border-color:#3d1010;" +
        "-fx-border-radius:6;-fx-border-width:2;-fx-opacity:0.45;";

    private final Lane lane;
    private final int  laneNumber;

    private final Label     wallHPLabel;
    private final Rectangle wallHPBar;
    private final Label     dangerBadge;
    private final Pane      lanePane;
    private final FlowPane  weaponsBar;

    public LaneView(Lane lane, int laneNumber) {
        super(6);
        this.lane       = lane;
        this.laneNumber = laneNumber;
        setPadding(new Insets(9));
        setAlignment(Pos.TOP_CENTER);
        setMinWidth(GameConstants.LANE_PANE_WIDTH + 22);
        setStyle(STYLE_NORMAL);

        // ── Header ───────────────────────────────────────────────
        Label laneLabel = new Label("LANE " + laneNumber);
        laneLabel.setStyle(
            "-fx-font-size:12px;-fx-font-weight:bold;-fx-text-fill:#d4b896;-fx-letter-spacing:2;");

        dangerBadge = new Label("DANGER 0");
        dangerBadge.setStyle(dangerStyle(0));

        HBox header = new HBox(10, laneLabel, dangerBadge);
        header.setAlignment(Pos.CENTER_LEFT);

        // ── Wall HP bar ──────────────────────────────────────────
        Label wallTitle = new Label("WALL");
        wallTitle.setStyle(
            "-fx-font-size:10px;-fx-text-fill:#8a7356;-fx-font-weight:bold;");

        wallHPLabel = new Label();
        wallHPLabel.setStyle("-fx-font-size:10px;-fx-text-fill:#5c4a2a;");

        Rectangle wallHPBg = new Rectangle(176, 7, Color.web("#1a1208"));
        wallHPBg.setArcWidth(3);
        wallHPBg.setArcHeight(3);

        wallHPBar = new Rectangle(176, 7, Color.web("#3a5228"));
        wallHPBar.setArcWidth(3);
        wallHPBar.setArcHeight(3);

        StackPane hpBarPane = new StackPane(wallHPBg, wallHPBar);
        hpBarPane.setMaxWidth(176);
        hpBarPane.setPrefWidth(176);
        StackPane.setAlignment(wallHPBar, Pos.CENTER_LEFT);

        HBox wallRow = new HBox(8, wallTitle, hpBarPane, wallHPLabel);
        wallRow.setAlignment(Pos.CENTER_LEFT);

        // ── Battle pane ──────────────────────────────────────────
        lanePane = buildBattlePane();

        // ── Weapons row ──────────────────────────────────────────
        Label weaponTitle = new Label("WEAPONS:");
        weaponTitle.setStyle("-fx-font-size:9px;-fx-text-fill:#5c4a2a;-fx-letter-spacing:1;");

        weaponsBar = new FlowPane(5, 3);
        weaponsBar.setAlignment(Pos.CENTER_LEFT);

        HBox weaponRow = new HBox(6, weaponTitle, weaponsBar);
        weaponRow.setAlignment(Pos.CENTER_LEFT);
        weaponRow.setMinHeight(22);

        getChildren().addAll(header, wallRow, lanePane, weaponRow);
        refresh();
    }

    // ── Public API ───────────────────────────────────────────────────

    public void refresh() {
        if (lane.isLaneLost()) {
            setStyle(STYLE_LOST);
            wallHPLabel.setText("FALLEN");
            wallHPLabel.setStyle("-fx-font-size:10px;-fx-text-fill:#8b1a1a;-fx-font-weight:bold;");
            wallHPBar.setWidth(0);
            dangerBadge.setText("LOST");
            dangerBadge.setStyle(dangerStyle(-1));
            return;
        }
        refreshWallBar();
        if (GameSettings.get().isShowDangerLevel()) {
            dangerBadge.setVisible(true);
            refreshDanger();
        } else {
            dangerBadge.setVisible(false);
        }
        refreshTitans();
        refreshWeapons();
    }

    public void setSelected(boolean selected) {
        if (!lane.isLaneLost())
            setStyle(selected ? STYLE_SELECTED : STYLE_NORMAL);
    }

    public Lane getLane()       { return lane; }
    public int  getLaneNumber() { return laneNumber; }

    // ── Battle pane builder ──────────────────────────────────────────

    private Pane buildBattlePane() {
        Pane pane = new Pane();
        pane.setPrefSize(GameConstants.LANE_PANE_WIDTH, GameConstants.LANE_PANE_HEIGHT);
        pane.setStyle("-fx-background-radius:4;");

        // Background canvas (sky + ground + stone wall) — drawn once
        Canvas bg = new Canvas(GameConstants.LANE_PANE_WIDTH, GameConstants.LANE_PANE_HEIGHT);
        bg.setUserData("background");
        drawLaneBackground(bg.getGraphicsContext2D(),
                GameConstants.LANE_PANE_WIDTH, GameConstants.LANE_PANE_HEIGHT);
        pane.getChildren().add(bg);

        return pane;
    }

    // ── Background drawing ───────────────────────────────────────────

    private void drawLaneBackground(GraphicsContext gc, int W, int H) {
        int groundY = H - 20;
        int wallW   = GameConstants.WALL_ZONE_WIDTH;
        int spawnW  = GameConstants.SPAWN_ZONE_WIDTH;
        int fieldW  = W - wallW - spawnW;

        // ── Cloudy dark sky ──────────────────────────────────────
        gc.setFill(Color.web("#0e1520"));
        gc.fillRect(wallW, 0, W - wallW, groundY);

        // Subtle cloud streaks
        gc.setFill(Color.web("#141e2c", 0.6));
        gc.fillRect(wallW, 10, fieldW * 0.6, 18);
        gc.fillRect(wallW + fieldW * 0.3, 35, fieldW * 0.5, 14);
        gc.fillRect(wallW + fieldW * 0.1, 56, fieldW * 0.4, 10);

        // Horizon fog/glow (eerie green-grey from titans)
        for (int y = groundY - 50; y < groundY; y++) {
            double t = (y - (groundY - 50)) / 50.0;
            gc.setFill(Color.web("#1a2a1a", t * 0.35));
            gc.fillRect(wallW, y, W - wallW, 1);
        }

        // ── Earthy ground ────────────────────────────────────────
        gc.setFill(Color.web("#1a1008"));
        gc.fillRect(0, groundY, W, H - groundY);
        // Ground surface
        gc.setFill(Color.web("#2e1e0a"));
        gc.fillRect(0, groundY, W, 4);
        // Ground texture (subtle horizontal stripes)
        gc.setFill(Color.web("#241608", 0.5));
        gc.fillRect(0, groundY + 6, W, 2);
        gc.fillRect(0, groundY + 10, W, 1);
        // Small rocks/pebbles hint
        gc.setFill(Color.web("#3a2810", 0.4));
        for (int x = wallW + 30; x < W - spawnW; x += 38) {
            gc.fillOval(x, groundY + 5, 10, 4);
        }

        // ── Spawn zone mist (right edge) ─────────────────────────
        for (int x = W - spawnW; x < W; x++) {
            double t = (double)(x - (W - spawnW)) / spawnW;
            gc.setFill(Color.web("#0a0e18", t * 0.8));
            gc.fillRect(x, 0, 1, groundY);
        }

        // Subtle distance markers (barely visible posts in the ground)
        gc.setFill(Color.web("#2a1e10", 0.7));
        for (int d = 10; d < GameConstants.TITAN_SPAWN_DISTANCE; d += 10) {
            double x = wallW + (double) d / GameConstants.TITAN_SPAWN_DISTANCE * fieldW;
            gc.fillRect(x - 1, groundY - 16, 2, 16);
            // Tiny crossbar (post top)
            gc.fillRect(x - 3, groundY - 16, 6, 2);
        }

        // ── Stone brick wall (left zone) ─────────────────────────
        drawStoneBricks(gc, 0, wallW, groundY);

        // Wall base/footing (slightly wider at ground)
        gc.setFill(Color.web("#1a1208"));
        gc.fillRect(0, groundY, wallW + 2, H - groundY);
        gc.setFill(Color.web("#3d2e18"));
        gc.fillRect(0, groundY, wallW + 2, 3);

        // Wall-field dividing shadow
        gc.setFill(Color.web("#000000", 0.55));
        gc.fillRect(wallW, 0, 3, groundY);
    }

    private void drawStoneBricks(GraphicsContext gc, int startX, int width, int height) {
        int brickH = 9;
        int brickW = 17;
        Color mortar = Color.web("#0e0c08");
        Color[] stones = {
            Color.web("#5c4a2a"), Color.web("#6b5842"),
            Color.web("#4f3e22"), Color.web("#7a6040"),
            Color.web("#544530"), Color.web("#6a5438")
        };

        gc.save();
        gc.beginPath();
        gc.rect(startX, 0, width, height);
        gc.clip();

        for (int row = 0; row * brickH < height + brickH; row++) {
            int offset = (row % 2) * (brickW / 2);
            for (int col = -1; col * brickW < width + brickW; col++) {
                int bx = startX + col * brickW - offset;
                int by = row * brickH;
                gc.setFill(mortar);
                gc.fillRect(bx, by, brickW, brickH);
                Color stone = stones[Math.abs(row * 7 + col * 3) % stones.length];
                gc.setFill(stone);
                gc.fillRect(bx + 1, by + 1, brickW - 2, brickH - 2);
                // Top bevel highlight
                gc.setFill(Color.web("#9a8060", 0.28));
                gc.fillRect(bx + 1, by + 1, brickW - 2, 1);
                // Bottom shadow
                gc.setFill(Color.web("#000000", 0.25));
                gc.fillRect(bx + 1, by + brickH - 2, brickW - 2, 1);
            }
        }
        gc.restore();
    }

    // ── Refresh helpers ──────────────────────────────────────────────

    private void refreshWallBar() {
        Wall wall = lane.getLaneWall();
        double ratio = Math.max(0, Math.min(1,
                (double) wall.getCurrentHealth() / wall.getBaseHealth()));
        wallHPBar.setWidth(176 * ratio);
        wallHPBar.setFill(wallHpColor(ratio));
        wallHPLabel.setText(wall.getCurrentHealth() + "/" + wall.getBaseHealth());
    }

    private void refreshDanger() {
        int d = lane.getDangerLevel();
        dangerBadge.setText("DANGER " + d);
        dangerBadge.setStyle(dangerStyle(d));
    }

    private void refreshTitans() {
        lanePane.getChildren().removeIf(n -> n instanceof TitanView);

        PriorityQueue<Titan> copy = new PriorityQueue<>(lane.getTitans());
        List<Titan> titans = new ArrayList<>();
        while (!copy.isEmpty()) titans.add(copy.poll());

        int H       = GameConstants.LANE_PANE_HEIGHT;
        int wallW   = GameConstants.WALL_ZONE_WIDTH;
        int spawnW  = GameConstants.SPAWN_ZONE_WIDTH;
        int fieldW  = GameConstants.LANE_PANE_WIDTH - wallW - spawnW;
        int groundY = H - 20;

        for (int i = 0; i < titans.size(); i++) {
            Titan t  = titans.get(i);
            TitanView tv = new TitanView(t);

            double xRatio = (double) t.getDistance() / GameConstants.TITAN_SPAWN_DISTANCE;
            double cx = wallW + xRatio * fieldW;
            double xOff = (i % 3) * 5.0;
            double x = cx - TitanView.TITAN_WIDTH / 2.0 + xOff;
            x = Math.max(wallW + 2,
                Math.min(x, GameConstants.LANE_PANE_WIDTH - spawnW - TitanView.TITAN_WIDTH - 1));

            double y = groundY - tv.getTotalHeight();
            tv.setLayoutX(x);
            tv.setLayoutY(y);
            lanePane.getChildren().add(tv);
        }
    }

    private void refreshWeapons() {
        lanePane.getChildren().removeIf(n -> "weapon-icon".equals(n.getUserData()));

        int groundY = GameConstants.LANE_PANE_HEIGHT - 20;
        int iconW   = GameConstants.WALL_ZONE_WIDTH + 14;
        int iconH   = 20;
        int slot    = 0;
        for (Weapon w : lane.getWeapons()) {
            Canvas icon = new Canvas(iconW, iconH);
            icon.setUserData("weapon-icon");
            drawWeaponIcon(icon.getGraphicsContext2D(), w, iconW, iconH);
            icon.setLayoutX(0);
            icon.setLayoutY(groundY - iconH - slot * (iconH + 3));
            lanePane.getChildren().add(icon);
            slot++;
        }

        weaponsBar.getChildren().clear();
        for (Weapon w : lane.getWeapons()) {
            Label chip = new Label(weaponName(w));
            chip.setStyle("-fx-background-color:" + weaponColor(w)
                    + ";-fx-text-fill:#e8d5b0;-fx-font-size:9px;"
                    + "-fx-padding:2 7;-fx-background-radius:3;-fx-font-weight:bold;");
            weaponsBar.getChildren().add(chip);
        }
    }

    // ── Weapon icon drawing ──────────────────────────────────────────

    private void drawWeaponIcon(GraphicsContext gc, Weapon w, int iw, int ih) {
        if      (w instanceof PiercingCannon)     drawPiercing(gc, iw, ih);
        else if (w instanceof SniperCannon)       drawSniper(gc, iw, ih);
        else if (w instanceof VolleySpreadCannon) drawVolley(gc, iw, ih);
        else if (w instanceof WallTrap)           drawWallTrap(gc, iw, ih);
    }

    private void drawPiercing(GraphicsContext gc, int iw, int ih) {
        Color col  = Color.web("#8b1a1a");
        Color dark = Color.web("#5a0e0e");
        Color hi   = Color.web("#c0392b");
        // Three stacked barrels
        gc.setFill(col);   gc.fillRect(8, ih/2.0-2,  iw-8, 4);
        gc.setFill(hi);    gc.fillRect(8, ih/2.0-2,  iw-8, 1);
        gc.setFill(col);   gc.fillRect(10, ih/2.0-7, iw-12, 3);
        gc.setFill(hi);    gc.fillRect(10, ih/2.0-7, iw-12, 1);
        gc.setFill(col);   gc.fillRect(10, ih/2.0+4, iw-12, 3);
        gc.setFill(hi);    gc.fillRect(10, ih/2.0+4, iw-12, 1);
        // Mount
        gc.setFill(dark);  gc.fillRect(1, ih/2.0-8, 9, 16);
        gc.setFill(col);   gc.fillRect(2, ih/2.0-7, 7, 14);
        // Muzzle
        gc.setFill(dark);
        gc.fillRect(iw-8, ih/2.0-2.5, 3, 5);
        gc.fillRect(iw-9, ih/2.0-8,   2, 4);
        gc.fillRect(iw-9, ih/2.0+4,   2, 4);
        gc.setStroke(dark); gc.setLineWidth(0.6);
        gc.strokeRect(8, ih/2.0-2, iw-8, 4);
    }

    private void drawSniper(GraphicsContext gc, int iw, int ih) {
        Color col  = Color.web("#3a5228");
        Color dark = Color.web("#1e2e18");
        Color hi   = Color.web("#4a7c59");
        // Long barrel
        gc.setFill(col);  gc.fillRect(6, ih/2.0-1.5, iw-6, 3);
        gc.setFill(hi);   gc.fillRect(6, ih/2.0-1.5, iw-6, 1);
        // Scope
        gc.setFill(dark); gc.fillRect(iw/2-4, ih/2.0-6, 10, 5);
        gc.setFill(col);  gc.fillRect(iw/2-3, ih/2.0-5, 8,  3);
        gc.setFill(hi);   gc.fillRect(iw/2-3, ih/2.0-5, 8,  1);
        gc.setFill(Color.web("#aaddcc")); gc.fillOval(iw/2+5, ih/2.0-5, 3, 3);
        // Mount
        gc.setFill(dark); gc.fillRect(1, ih/2.0-4, 7, 8);
        gc.setFill(col);  gc.fillRect(2, ih/2.0-3, 5, 6);
        // Muzzle
        gc.setFill(dark); gc.fillRect(iw-5, ih/2.0-3, 4, 6);
        gc.setStroke(dark); gc.setLineWidth(0.6);
        gc.strokeRect(6, ih/2.0-1.5, iw-6, 3);
    }

    private void drawVolley(GraphicsContext gc, int iw, int ih) {
        Color col  = Color.web("#5c4a2a");
        Color dark = Color.web("#2a1e0a");
        Color hi   = Color.web("#8a7040");
        // Widening barrel
        gc.setFill(col);
        gc.fillPolygon(new double[]{8, iw, iw, 8},
                       new double[]{ih/2.0-2, ih/2.0-5, ih/2.0+5, ih/2.0+2}, 4);
        gc.setFill(hi);
        gc.fillPolygon(new double[]{8, iw, iw, 8},
                       new double[]{ih/2.0-2, ih/2.0-5, ih/2.0-4, ih/2.0-1}, 4);
        // Internal lines
        gc.setStroke(dark); gc.setLineWidth(0.6);
        gc.strokeLine(iw*0.5, ih/2.0-3.5, iw, ih/2.0-5);
        gc.strokeLine(iw*0.5, ih/2.0+1.5, iw, ih/2.0+4.5);
        // Mount
        gc.setFill(dark); gc.fillRect(1, ih/2.0-5, 8, 10);
        gc.setFill(col);  gc.fillRect(2, ih/2.0-4, 6,  8);
        gc.setFill(hi);   gc.fillRect(2, ih/2.0-4, 6,  2);
        gc.setStroke(dark); gc.setLineWidth(0.7);
        gc.strokePolygon(new double[]{8, iw, iw, 8},
                         new double[]{ih/2.0-2, ih/2.0-5, ih/2.0+5, ih/2.0+2}, 4);
    }

    private void drawWallTrap(GraphicsContext gc, int iw, int ih) {
        Color col  = Color.web("#2e4820");
        Color dark = Color.web("#1a2a10");
        Color hi   = Color.web("#4a7c30");
        // Three spike blades
        double[] spikeY = {ih/2.0-7, ih/2.0, ih/2.0+7};
        for (double sy : spikeY) {
            gc.setFill(col);
            gc.fillPolygon(new double[]{5, iw-2, 5},
                           new double[]{sy-2.5, sy, sy+2.5}, 3);
            gc.setFill(hi);
            gc.fillPolygon(new double[]{5, iw-2, 5},
                           new double[]{sy-2.5, sy, sy-0.5}, 3);
            gc.setStroke(dark); gc.setLineWidth(0.6);
            gc.strokePolygon(new double[]{5, iw-2, 5},
                             new double[]{sy-2.5, sy, sy+2.5}, 3);
        }
        // Mounting bar
        gc.setFill(dark); gc.fillRect(1, ih/2.0-9, 5, 18);
        gc.setFill(col);  gc.fillRect(2, ih/2.0-8, 3, 16);
        gc.setFill(hi);   gc.fillRect(2, ih/2.0-8, 3,  4);
    }

    // ── Style helpers ────────────────────────────────────────────────

    private Color wallHpColor(double ratio) {
        if (ratio > 0.55) return Color.web("#3a5228");
        if (ratio > 0.25) return Color.web("#8b6a00");
        return Color.web("#8b1a1a");
    }

    private String dangerStyle(int danger) {
        String bg, fg;
        if      (danger < 0)   { bg = "#1a0808"; fg = "#5a1a1a"; }
        else if (danger == 0)  { bg = "#1a1208"; fg = "#5c4a2a"; }
        else if (danger < 50)  { bg = "#1a2a10"; fg = "#4a7c59"; }
        else if (danger < 150) { bg = "#2a1a00"; fg = "#b8860b"; }
        else                   { bg = "#2a0808"; fg = "#8b1a1a"; }
        return "-fx-background-color:" + bg + ";-fx-text-fill:" + fg
             + ";-fx-font-size:9px;-fx-font-weight:bold;"
             + "-fx-padding:2 6;-fx-background-radius:3;-fx-letter-spacing:1;";
    }

    private String weaponName(Weapon w) {
        if (w instanceof PiercingCannon)     return "Piercing";
        if (w instanceof SniperCannon)       return "Sniper";
        if (w instanceof VolleySpreadCannon) return "Volley";
        if (w instanceof WallTrap)           return "Wall Trap";
        return "?";
    }

    private String weaponColor(Weapon w) {
        if (w instanceof PiercingCannon)     return "#6e1a1a";
        if (w instanceof SniperCannon)       return "#2a4a20";
        if (w instanceof VolleySpreadCannon) return "#4a3a18";
        if (w instanceof WallTrap)           return "#1e3818";
        return "#3d2e18";
    }
}
