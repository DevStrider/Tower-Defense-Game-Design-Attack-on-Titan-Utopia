package game.gui.views;

import game.engine.titans.*;
import game.gui.GameSettings;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Rectangle;

public class TitanView extends VBox {

    public static final int TITAN_WIDTH = 46;
    private static final int HP_BAR_H   = 5;
    private static final int LABEL_H    = 11;
    private static final int GAP        = 2;

    private final int bodyHeight;

    public TitanView(Titan titan) {
        super(GAP);
        this.bodyHeight = resolveBodyHeight(titan);
        setAlignment(Pos.BOTTOM_CENTER);
        setMaxSize(TITAN_WIDTH, getTotalHeight());
        setPrefSize(TITAN_WIDTH, getTotalHeight());

        // ── Type label ───────────────────────────────────────────
        Label typeLabel = new Label(resolveTypeName(titan));
        typeLabel.setStyle(
            "-fx-font-size:7px;-fx-font-weight:bold;" +
            "-fx-text-fill:#d4b896;-fx-padding:0 2;");
        typeLabel.setMaxWidth(TITAN_WIDTH);
        typeLabel.setAlignment(Pos.CENTER);

        // ── Canvas body ──────────────────────────────────────────
        Canvas canvas = new Canvas(TITAN_WIDTH, bodyHeight);
        drawTitan(canvas.getGraphicsContext2D(), titan);

        StackPane bodyStack = new StackPane(canvas);
        bodyStack.setMaxSize(TITAN_WIDTH, bodyHeight);
        bodyStack.setPrefSize(TITAN_WIDTH, bodyHeight);

        // HP label (only if setting enabled)
        if (GameSettings.get().isShowTitanHP()) {
            Label hpText = new Label(titan.getCurrentHealth() + " HP");
            hpText.setStyle(
                "-fx-font-size:7px;-fx-text-fill:white;-fx-font-weight:bold;" +
                "-fx-background-color:rgba(0,0,0,0.5);-fx-padding:1 3;-fx-background-radius:2;");
            bodyStack.getChildren().add(hpText);
            StackPane.setAlignment(hpText, Pos.BOTTOM_CENTER);
        }

        // ── HP bar ───────────────────────────────────────────────
        double ratio = Math.max(0, Math.min(1,
                (double) titan.getCurrentHealth() / titan.getBaseHealth()));
        Rectangle hpBg = new Rectangle(TITAN_WIDTH, HP_BAR_H, Color.web("#1a1208"));
        Rectangle hpFg = new Rectangle(TITAN_WIDTH * ratio, HP_BAR_H, hpColor(ratio));
        hpFg.setArcWidth(3);
        hpFg.setArcHeight(3);
        StackPane hpBar = new StackPane(hpBg, hpFg);
        hpBar.setMaxSize(TITAN_WIDTH, HP_BAR_H);
        hpBar.setPrefSize(TITAN_WIDTH, HP_BAR_H);
        StackPane.setAlignment(hpFg, Pos.CENTER_LEFT);

        getChildren().addAll(typeLabel, bodyStack, hpBar);
    }

    // ── Dispatch ─────────────────────────────────────────────────────

    private void drawTitan(GraphicsContext gc, Titan titan) {
        if      (titan instanceof ColossalTitan)  drawColossal(gc, bodyHeight);
        else if (titan instanceof ArmoredTitan)   drawArmored(gc, bodyHeight);
        else if (titan instanceof AbnormalTitan)  drawAbnormal(gc, bodyHeight);
        else                                       drawPure(gc, bodyHeight);
    }

    // ── Pure Titan ───────────────────────────────────────────────────
    // Pale flesh-toned human giant — the classic AoT look
    private void drawPure(GraphicsContext gc, int h) {
        Color skin     = Color.web("#e8c9a0");
        Color skinDark = Color.web("#c4a07a");
        Color skinSh   = Color.web("#b8905a");
        Color muscle   = Color.web("#d4a080");

        // Legs
        gc.setFill(skin);
        gc.fillPolygon(xs(13,21,19,11), ys(h,.72,.72,1.0,1.0), 4);
        gc.fillPolygon(xs(25,33,35,27), ys(h,.72,.72,1.0,1.0), 4);
        // Leg shadow
        gc.setFill(skinDark);
        gc.fillPolygon(xs(15,19,17,13), ys(h,.72,.72,1.0,1.0), 4);
        gc.fillPolygon(xs(27,31,33,29), ys(h,.72,.72,1.0,1.0), 4);

        // Arms
        gc.setFill(skin);
        gc.fillPolygon(xs(7,1,3,9),    ys(h,.36,.40,.70,.68), 4);
        gc.fillPolygon(xs(39,45,43,37),ys(h,.36,.40,.70,.68), 4);

        // Torso
        gc.setFill(skin);
        gc.fillPolygon(xs(7,39,34,12), ys(h,.36,.36,.72,.72), 4);
        // Torso muscle shading (centre stripe darker)
        gc.setFill(skinDark);
        gc.fillPolygon(xs(19,27,25,21), ys(h,.36,.36,.72,.72), 4);
        // Rib/muscle lines
        gc.setStroke(skinSh);
        gc.setLineWidth(0.6);
        gc.strokeLine(14, h*.44, 22, h*.44);
        gc.strokeLine(24, h*.44, 32, h*.44);
        gc.strokeLine(14, h*.54, 22, h*.54);
        gc.strokeLine(24, h*.54, 32, h*.54);
        gc.strokeLine(14, h*.64, 22, h*.64);
        gc.strokeLine(24, h*.64, 32, h*.64);

        // Neck
        gc.setFill(skin);
        gc.fillRect(18, h*.28, 10, h*.09);

        // Head
        gc.setFill(skin);
        gc.fillOval(10, 1, 26, h*.29);
        // Forehead darker
        gc.setFill(skinDark);
        gc.fillArc(10, 1, 26, h*.14, 0, 180, ArcType.ROUND);
        // Cheek blush
        gc.setFill(Color.web("#e09080", 0.35));
        gc.fillOval(11, h*.15, 7, 5);
        gc.fillOval(28, h*.15, 7, 5);

        // Eyes — white sclera with dark iris (titan eyes)
        gc.setFill(Color.WHITE);
        gc.fillOval(13, h*.11, 8, 6);
        gc.fillOval(25, h*.11, 8, 6);
        gc.setFill(Color.web("#1a0800"));
        gc.fillOval(15, h*.12, 4, 4);
        gc.fillOval(27, h*.12, 4, 4);
        gc.setFill(Color.WHITE);
        gc.fillOval(15.8, h*.12, 1.4, 1.4);
        gc.fillOval(27.8, h*.12, 1.4, 1.4);

        // Gaping mouth / grin (no lips)
        gc.setFill(Color.web("#4a1a10"));
        gc.fillOval(14, h*.22, 18, h*.06);
        // Teeth — titans have exposed teeth, no lips
        gc.setFill(Color.web("#e8ddd0"));
        for (int i = 0; i < 6; i++) gc.fillRect(15 + i*2.5, h*.22, 1.8, h*.025);
        // Lower teeth
        for (int i = 0; i < 5; i++) gc.fillRect(15.7 + i*2.5, h*.245, 1.5, h*.02);
    }

    // ── Abnormal Titan ───────────────────────────────────────────────
    // Leaner, more feral — same flesh tone but haunted, running pose
    private void drawAbnormal(GraphicsContext gc, int h) {
        Color skin     = Color.web("#d4b890");
        Color skinDark = Color.web("#b09060");
        Color skinSh   = Color.web("#9a7848");

        // Legs (running)
        gc.setFill(skin);
        gc.fillPolygon(xs(10,19,15,7),  ys(h,.68,.68,1.0,1.0), 4);
        gc.fillPolygon(xs(27,36,38,29), ys(h,.68,.68,1.0,1.0), 4);
        // Shadow
        gc.setFill(skinDark);
        gc.fillPolygon(xs(12,17,13,9),  ys(h,.68,.68,1.0,1.0), 4);

        // Arms (reaching forward aggressively)
        gc.setFill(skin);
        gc.fillPolygon(xs(7,0,2,8),    ys(h,.32,.28,.54,.54), 4);
        gc.fillPolygon(xs(37,46,44,36),ys(h,.32,.26,.50,.52), 4);

        // Torso (leaning forward)
        gc.setFill(skin);
        gc.fillPolygon(xs(7,37,32,11), ys(h,.32,.32,.68,.68), 4);
        gc.setFill(skinDark);
        gc.fillPolygon(xs(19,26,24,20),ys(h,.32,.32,.68,.68), 4);
        // Muscle lines
        gc.setStroke(skinSh);
        gc.setLineWidth(0.6);
        gc.strokeLine(12, h*.42, 20, h*.42);
        gc.strokeLine(22, h*.42, 30, h*.42);
        gc.strokeLine(12, h*.54, 20, h*.54);
        gc.strokeLine(22, h*.54, 30, h*.54);

        // Neck
        gc.setFill(skin);
        gc.fillRect(17, h*.24, 11, h*.09);

        // Head (smaller, frantic — bounding forward)
        gc.setFill(skin);
        gc.fillOval(11, 0, 22, h*.27);
        gc.setFill(skinDark);
        gc.fillArc(11, 0, 22, h*.12, 0, 180, ArcType.ROUND);

        // Eyes — wide, bloodshot red (abnormal titan hallmark)
        gc.setFill(Color.WHITE);
        gc.fillOval(13, h*.07, 8, 7);
        gc.fillOval(23, h*.07, 8, 7);
        gc.setFill(Color.web("#cc2200"));
        gc.fillOval(15, h*.08, 4, 5);
        gc.fillOval(25, h*.08, 4, 5);
        gc.setFill(Color.web("#440000"));
        gc.fillOval(16, h*.09, 2, 3);
        gc.fillOval(26, h*.09, 2, 3);
        // Bloodshot veins (tiny red lines)
        gc.setStroke(Color.web("#ff4444", 0.5));
        gc.setLineWidth(0.5);
        gc.strokeLine(13, h*.08, 15, h*.10);
        gc.strokeLine(21, h*.09, 23, h*.11);
        // Eye glint
        gc.setFill(Color.WHITE);
        gc.fillOval(15.5, h*.08, 1.3, 1.3);
        gc.fillOval(25.5, h*.08, 1.3, 1.3);

        // Wide open mouth
        gc.setFill(Color.web("#3a0f08"));
        gc.fillOval(13, h*.19, 18, h*.08);
        gc.setFill(Color.web("#e8ddd0"));
        for (int i = 0; i < 6; i++) gc.fillRect(14.5 + i*2.5, h*.19, 1.8, h*.03);
        for (int i = 0; i < 5; i++) gc.fillRect(15.3 + i*2.5, h*.22, 1.5, h*.025);
    }

    // ── Armored Titan ────────────────────────────────────────────────
    // Crystal armour plates over skin — grey-white crystalline material
    private void drawArmored(GraphicsContext gc, int h) {
        Color skin     = Color.web("#c4a07a");
        Color armor    = Color.web("#a8bcc8");   // crystalline grey
        Color armorHi  = Color.web("#d8eaf4");   // highlight
        Color armorDk  = Color.web("#607080");   // shadow
        Color joint    = Color.web("#8a9aaa");
        Color eyeGlow  = Color.web("#00ddaa");

        // Skin underneath (visible in joints/gaps)
        gc.setFill(skin);
        gc.fillPolygon(xs(7,39,34,12), ys(h,.36,.36,.72,.72), 4);
        gc.fillRect(18, h*.28, 10, h*.09);
        gc.fillOval(10, 1, 26, h*.28);

        // Leg armor plates
        gc.setFill(armor);
        gc.fillRect(12, h*.72, 10, h*.28);
        gc.fillRect(24, h*.72, 10, h*.28);
        gc.setFill(armorHi); gc.fillRect(12, h*.72, 10, 2); gc.fillRect(24, h*.72, 10, 2);
        gc.setFill(armorDk);
        gc.strokeRect(12, h*.72, 10, h*.28); gc.strokeRect(24, h*.72, 10, h*.28);
        // Plate seam
        gc.setStroke(armorDk); gc.setLineWidth(1.0);
        gc.strokeLine(12, h*.86, 22, h*.86); gc.strokeLine(24, h*.86, 34, h*.86);

        // Arm armor
        gc.setFill(armor);
        gc.fillRect(1, h*.36, 6, h*.30);
        gc.fillRect(39, h*.36, 6, h*.30);
        gc.setFill(armorHi); gc.fillRect(1, h*.36, 6, 2); gc.fillRect(39, h*.36, 6, 2);

        // Torso armor (two chest plates + abdomen)
        gc.setFill(armor);
        // Left chest plate
        gc.fillPolygon(xs(8,23,22,9), ys(h,.36,.36,.58,.58), 4);
        // Right chest plate
        gc.fillPolygon(xs(23,38,37,22),ys(h,.36,.36,.58,.58), 4);
        // Abdomen plate
        gc.fillPolygon(xs(10,36,34,12),ys(h,.58,.58,.72,.72), 4);
        // Highlights
        gc.setFill(armorHi);
        gc.fillPolygon(xs(8,23,22,9), ys(h,.36,.36,.38,.38), 4);
        gc.fillPolygon(xs(23,38,37,22),ys(h,.36,.36,.38,.38), 4);
        // Center seam
        gc.setStroke(armorDk); gc.setLineWidth(1.2);
        gc.strokeLine(23, h*.36, 23, h*.72);
        gc.strokeLine(8, h*.58, 38, h*.58);
        // Outline torso
        gc.strokePolygon(xs(8,38,34,12), ys(h,.36,.36,.72,.72), 4);

        // Neck joint (skin visible)
        gc.setFill(joint);
        gc.fillRect(17, h*.22, 12, h*.09);

        // Head armor — rectangular helm with visor
        gc.setFill(armor);
        gc.fillRect(10, 1, 26, h*.22);
        // Helm highlight (top bevel)
        gc.setFill(armorHi); gc.fillRect(10, 1, 26, 3);
        // Left/right face plates
        gc.setFill(armorDk); gc.fillRect(10, 1, 4, h*.22);
        gc.setFill(armorDk); gc.fillRect(32, 1, 4, h*.22);
        // Center seam
        gc.setStroke(armorDk); gc.setLineWidth(1.2);
        gc.strokeRect(10, 1, 26, h*.22);
        gc.strokeLine(23, 1, 23, h*.22);
        // Visor slot
        gc.setFill(Color.web("#0a1a14"));
        gc.fillRect(13, h*.10, 20, 5);
        // Glowing eyes behind visor
        gc.setFill(eyeGlow);
        gc.fillRect(14, h*.11, 7, 3);
        gc.fillRect(25, h*.11, 7, 3);
        // Glow bloom
        gc.setFill(Color.web("#00ddaa", 0.2));
        gc.fillRect(12, h*.09, 12, 8);
        gc.fillRect(22, h*.09, 12, 8);
    }

    // ── Colossal Titan ───────────────────────────────────────────────
    // No skin — exposed muscle, steam venting, towering height
    private void drawColossal(GraphicsContext gc, int h) {
        Color muscle  = Color.web("#a93226"); // dark red exposed muscle
        Color muscleD = Color.web("#6e1f18");
        Color bone    = Color.web("#c8a07a");
        Color steam   = Color.web("#9b59b6");

        // Background steam/aura (drawn behind body)
        gc.setFill(Color.web("#4a1a50", 0.20));
        gc.fillOval(-8, h*.25, 24, 28);
        gc.fillOval(30, h*.23, 26, 30);
        gc.setFill(Color.web("#3a1040", 0.14));
        gc.fillOval(-10, h*.45, 20, 22);
        gc.fillOval(36, h*.43, 22, 24);
        gc.fillOval(-5, h*.12, 18, 20);
        gc.fillOval(33, h*.10, 18, 22);

        // Legs (exposed muscle bundles — very long)
        gc.setFill(muscle);
        gc.fillPolygon(xs(8,18,16,6),   ys(h,.64,.64,1.0,1.0), 4);
        gc.fillPolygon(xs(28,38,40,30), ys(h,.64,.64,1.0,1.0), 4);
        // Muscle bundle detail
        gc.setFill(muscleD);
        gc.fillPolygon(xs(10,14,12,8),  ys(h,.64,.64,1.0,1.0), 4);
        gc.fillPolygon(xs(30,34,32,30), ys(h,.64,.64,1.0,1.0), 4);
        gc.setStroke(muscleD); gc.setLineWidth(0.8);
        gc.strokeLine(9, h*.82, 17, h*.82);
        gc.strokeLine(29, h*.82, 37, h*.82);

        // Arms (long, exposed muscle)
        gc.setFill(muscle);
        gc.fillPolygon(xs(1,0,1,7),    ys(h,.28,.36,.77,.74), 4);
        gc.fillPolygon(xs(45,46,45,39),ys(h,.28,.36,.77,.74), 4);

        // Torso (massive, no skin — exposed muscle & bone structure)
        gc.setFill(muscle);
        gc.fillPolygon(xs(1,45,39,7), ys(h,.28,.28,.64,.64), 4);
        // Ribcage outline (bone)
        gc.setStroke(bone);
        gc.setLineWidth(1.0);
        // Left ribs
        for (int i = 0; i < 4; i++) {
            double y1 = h*(0.32 + i*0.06), y2 = h*(0.35 + i*0.06);
            gc.strokeLine(8, y1, 20, y2);
        }
        // Right ribs
        for (int i = 0; i < 4; i++) {
            double y1 = h*(0.32 + i*0.06), y2 = h*(0.35 + i*0.06);
            gc.strokeLine(38, y1, 26, y2);
        }
        // Sternum
        gc.setStroke(bone);
        gc.setLineWidth(1.2);
        gc.strokeLine(23, h*.28, 23, h*.60);
        // Torso outline
        gc.setStroke(muscleD);
        gc.setLineWidth(1.2);
        gc.strokePolygon(xs(1,45,39,7), ys(h,.28,.28,.64,.64), 4);

        // Neck (thin, tendons visible)
        gc.setFill(muscle);
        gc.fillRect(17, h*.20, 12, h*.09);
        gc.setStroke(muscleD); gc.setLineWidth(0.7);
        gc.strokeLine(20, h*.20, 20, h*.29);
        gc.strokeLine(26, h*.20, 26, h*.29);

        // Head — skull-like, no skin on parts
        gc.setFill(muscle);
        gc.fillOval(4, 0, 38, h*.22);
        // Forehead bone (lighter patch)
        gc.setFill(bone);
        gc.fillArc(8, 1, 30, h*.09, 0, 180, ArcType.ROUND);
        // Cheekbone
        gc.setFill(bone);
        gc.fillOval(5, h*.10, 10, 7);
        gc.fillOval(31, h*.10, 10, 7);

        // Deep eye sockets (very dark)
        gc.setFill(Color.web("#080010"));
        gc.fillOval(8, h*.07, 13, 11);
        gc.fillOval(25, h*.07, 13, 11);
        // Fire eyes (orange/red glow)
        gc.setFill(Color.web("#ff4400"));
        gc.fillOval(10, h*.08, 9, 8);
        gc.fillOval(27, h*.08, 9, 8);
        gc.setFill(Color.web("#ffaa00", 0.8));
        gc.fillOval(12, h*.09, 5, 5);
        gc.fillOval(29, h*.09, 5, 5);
        // Glow bloom
        gc.setFill(Color.web("#ff4400", 0.12));
        gc.fillOval(5, h*.05, 20, 16);
        gc.fillOval(21, h*.05, 20, 16);

        // Exposed teeth and gum
        gc.setFill(Color.web("#3a1008"));
        gc.fillOval(13, h*.165, 20, h*.05);
        gc.setFill(Color.web("#e8ddd0"));
        for (int i = 0; i < 7; i++) gc.fillRect(14 + i*2.8, h*.165, 2.0, h*.03);
        for (int i = 0; i < 6; i++) gc.fillRect(14.7 + i*2.8, h*.196, 1.6, h*.02);

        // Steam venting from shoulder joints and neck
        for (int ring = 0; ring < 4; ring++) {
            double alpha = 0.40 - ring * 0.09;
            gc.setFill(Color.web("#9b59b6", alpha));
            gc.fillOval(0 - ring*4,   h*.24 - ring*6, 16 + ring*6, 16 + ring*6);
            gc.fillOval(30 + ring*2,  h*.22 - ring*6, 16 + ring*6, 16 + ring*6);
        }
        // Extra wisps near neck
        gc.setFill(Color.web("#7d3c98", 0.25));
        gc.fillOval(14, h*.14, 18, 16);
    }

    // ── Helpers ──────────────────────────────────────────────────────

    private double[] xs(double... v) { return v; }

    private double[] ys(int h, double... r) {
        double[] out = new double[r.length];
        for (int i = 0; i < r.length; i++) out[i] = r[i] * h;
        return out;
    }

    private Color hpColor(double ratio) {
        if (ratio > 0.55) return Color.web("#27ae60");
        if (ratio > 0.25) return Color.web("#d4a017");
        return Color.web("#8b1a1a");
    }

    private String resolveTypeName(Titan t) {
        if (t instanceof ColossalTitan)  return "COLOSSAL";
        if (t instanceof ArmoredTitan)   return "ARMORED";
        if (t instanceof AbnormalTitan)  return "ABNORMAL";
        return "PURE";
    }

    private int resolveBodyHeight(Titan t) {
        if (t instanceof ColossalTitan)  return 160;
        if (t instanceof AbnormalTitan)  return 52;
        return 80;
    }

    public int getTitanHeight() { return bodyHeight; }
    public int getTotalHeight() { return LABEL_H + GAP + bodyHeight + GAP + HP_BAR_H; }
}
