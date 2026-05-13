package game.gui.views;

import game.engine.Battle;
import game.engine.BattlePhase;
import game.engine.lanes.Lane;
import game.engine.weapons.WeaponRegistry;
import game.gui.GameSettings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

import java.util.*;

public class BattleView extends StackPane {

    // ── Inner game layout ────────────────────────────────────────────
    private final BorderPane gameLayout = new BorderPane();

    // ── HUD ─────────────────────────────────────────────────────────
    private Label  scoreLabel;
    private Label  turnLabel;
    private Label  phaseLabel;
    private Label  resourcesLabel;
    private Button aiToggleButton;
    private Button settingsButton;
    private boolean aiMode = false;

    // ── Weapon shop ──────────────────────────────────────────────────
    private VBox weaponShopContent;
    private final Map<Integer, Button> weaponButtons = new HashMap<>();
    private int selectedWeaponCode = -1;

    // ── Lanes ────────────────────────────────────────────────────────
    private HBox lanesContainer;
    private final List<LaneView> laneViews = new ArrayList<>();
    private LaneView selectedLaneView = null;

    // ── Action bar ───────────────────────────────────────────────────
    private Label  selectedLaneLabel;
    private Label  selectedWeaponLabel;
    private Button passTurnButton;
    private Button buyWeaponButton;

    // ── In-game settings overlay ─────────────────────────────────────
    private StackPane overlay;
    private Button resumeButton;
    private Button resetButton;
    private Button quitButton;
    // Speed buttons (overlay)
    private Button oSlowBtn;
    private Button oNormalBtn;
    private Button oFastBtn;
    // Display toggles (overlay)
    private Button oTitanHPBtn;
    private Button oDangerBtn;

    public BattleView() {
        gameLayout.setStyle("-fx-background-color:#0d0b08;");
        buildHUD();
        buildWeaponShop();
        buildLanesArea();
        buildActionBar();
        buildOverlay();

        getChildren().addAll(gameLayout, overlay);
    }

    // ── Initialisation ───────────────────────────────────────────────

    public void initializeLanes(Battle battle) {
        lanesContainer.getChildren().clear();
        laneViews.clear();
        for (int i = 0; i < battle.getOriginalLanes().size(); i++) {
            LaneView lv = new LaneView(battle.getOriginalLanes().get(i), i + 1);
            laneViews.add(lv);
            lanesContainer.getChildren().add(lv);
        }
    }

    public void initializeWeaponShop(Battle battle) {
        weaponShopContent.getChildren().clear();
        weaponButtons.clear();
        Map<Integer, WeaponRegistry> shop = battle.getWeaponFactory().getWeaponShop();
        List<Integer> codes = new ArrayList<>(shop.keySet());
        Collections.sort(codes);
        for (int code : codes) weaponShopContent.getChildren().add(buildWeaponCard(code, shop.get(code)));
    }

    // ── Refresh ──────────────────────────────────────────────────────

    public void refreshHUD(Battle battle) {
        scoreLabel.setText("SCORE  " + battle.getScore());
        turnLabel.setText("TURN  " + battle.getNumberOfTurns());
        resourcesLabel.setText("RESOURCES  " + battle.getResourcesGathered());
        updatePhaseLabel(battle.getBattlePhase());
    }

    public void refreshLanes() {
        for (LaneView lv : laneViews) lv.refresh();
    }

    // ── Selection ────────────────────────────────────────────────────

    public void selectLane(LaneView lv) {
        if (selectedLaneView != null) selectedLaneView.setSelected(false);
        selectedLaneView = lv;
        if (lv != null) {
            lv.setSelected(true);
            selectedLaneLabel.setText("Lane " + lv.getLaneNumber() + " selected");
        } else {
            selectedLaneLabel.setText("No lane selected");
        }
        updateBuyButton();
    }

    public void selectWeapon(int code) {
        if (selectedWeaponCode != -1 && weaponButtons.containsKey(selectedWeaponCode))
            weaponButtons.get(selectedWeaponCode).setStyle(weaponBtnStyle(false));
        selectedWeaponCode = code;
        if (code != -1 && weaponButtons.containsKey(code)) {
            weaponButtons.get(code).setStyle(weaponBtnStyle(true));
            selectedWeaponLabel.setText("Weapon [" + code + "] selected");
        } else {
            selectedWeaponLabel.setText("No weapon selected");
        }
        updateBuyButton();
    }

    // ── AI toggle ────────────────────────────────────────────────────

    public void setAIMode(boolean on) {
        aiMode = on;
        if (on) {
            aiToggleButton.setText("AI: ON");
            aiToggleButton.setStyle(
                "-fx-background-color:#3a5228;-fx-text-fill:#a8d878;" +
                "-fx-font-size:11px;-fx-font-weight:bold;" +
                "-fx-padding:5 14;-fx-background-radius:3;-fx-cursor:hand;" +
                "-fx-border-color:#4a6a35;-fx-border-width:1;-fx-border-radius:3;");
        } else {
            aiToggleButton.setText("AI: OFF");
            aiToggleButton.setStyle(
                "-fx-background-color:#1a1208;-fx-text-fill:#5c4a2a;" +
                "-fx-font-size:11px;-fx-font-weight:bold;" +
                "-fx-padding:5 14;-fx-background-radius:3;-fx-cursor:hand;" +
                "-fx-border-color:#3d2e18;-fx-border-width:1;-fx-border-radius:3;");
        }
    }

    public boolean isAIMode() { return aiMode; }

    // ── Overlay show / hide ──────────────────────────────────────────

    public void showSettingsOverlay() {
        syncOverlayToSettings();
        overlay.setVisible(true);
    }

    public void hideSettingsOverlay() {
        overlay.setVisible(false);
    }

    public boolean isOverlayVisible() { return overlay.isVisible(); }

    // ── Getters ──────────────────────────────────────────────────────

    public Button         getPassTurnButton()      { return passTurnButton; }
    public Button         getBuyWeaponButton()     { return buyWeaponButton; }
    public Button         getAIToggleButton()      { return aiToggleButton; }
    public Button         getSettingsButton()      { return settingsButton; }
    public Button         getResumeButton()        { return resumeButton; }
    public Button         getResetButton()         { return resetButton; }
    public Button         getQuitButton()          { return quitButton; }
    public Button         getOSlowBtn()            { return oSlowBtn; }
    public Button         getONormalBtn()          { return oNormalBtn; }
    public Button         getOFastBtn()            { return oFastBtn; }
    public Button         getOTitanHPBtn()         { return oTitanHPBtn; }
    public Button         getODangerBtn()          { return oDangerBtn; }
    public List<LaneView> getLaneViews()           { return laneViews; }
    public Map<Integer, Button> getWeaponButtons() { return weaponButtons; }
    public LaneView       getSelectedLaneView()    { return selectedLaneView; }
    public int            getSelectedWeaponCode()  { return selectedWeaponCode; }

    // ── Private layout builders ──────────────────────────────────────

    private void buildHUD() {
        HBox hud = new HBox(0);
        hud.setPrefHeight(46);
        hud.setAlignment(Pos.CENTER_LEFT);
        hud.setStyle(
            "-fx-background-color:#0d0b08;" +
            "-fx-border-color:#3d2e18;-fx-border-width:0 0 2 0;");

        scoreLabel     = hudChip("SCORE  0",      "#d4b896", "#1a1208");
        turnLabel      = hudChip("TURN  1",       "#8a7356", "#0d0b08");
        phaseLabel     = hudChip("EARLY",         "#4a7c59", "#0d1a10");
        resourcesLabel = hudChip("RESOURCES  0",  "#b8860b", "#1a1400");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        aiToggleButton = new Button("AI: OFF");
        aiToggleButton.setStyle(
            "-fx-background-color:#1a1208;-fx-text-fill:#5c4a2a;" +
            "-fx-font-size:11px;-fx-font-weight:bold;" +
            "-fx-padding:5 14;-fx-background-radius:3;-fx-cursor:hand;" +
            "-fx-border-color:#3d2e18;-fx-border-width:1;-fx-border-radius:3;");

        settingsButton = new Button("⚙  SETTINGS");
        settingsButton.setStyle(
            "-fx-background-color:#1a1208;-fx-text-fill:#5c4a2a;" +
            "-fx-font-size:11px;-fx-font-weight:bold;" +
            "-fx-padding:5 14;-fx-background-radius:3;-fx-cursor:hand;" +
            "-fx-border-color:#3d2e18;-fx-border-width:1;-fx-border-radius:3;");

        HBox rightBtns = new HBox(8, aiToggleButton, settingsButton);
        rightBtns.setPadding(new Insets(0, 14, 0, 0));
        rightBtns.setAlignment(Pos.CENTER);

        hud.getChildren().addAll(
            scoreLabel, vSep(), turnLabel, vSep(), phaseLabel,
            vSep(), resourcesLabel, spacer, rightBtns);
        gameLayout.setTop(hud);
    }

    private void buildWeaponShop() {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(14, 10, 14, 10));
        panel.setStyle(
            "-fx-background-color:#0d0b08;" +
            "-fx-border-color:#3d2e18;-fx-border-width:0 2 0 0;");
        panel.setPrefWidth(215);

        Label title = new Label("ARMORY");
        title.setStyle(
            "-fx-font-size:13px;-fx-font-weight:bold;-fx-text-fill:#8a7356;" +
            "-fx-letter-spacing:3;-fx-padding:0 0 4 0;");

        Separator sep = new Separator();
        sep.setStyle("-fx-background-color:#3d2e18;");

        weaponShopContent = new VBox(8);
        ScrollPane scroll = new ScrollPane(weaponShopContent);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background:#0d0b08;-fx-background-color:#0d0b08;-fx-border-color:transparent;");
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        VBox.setVgrow(scroll, Priority.ALWAYS);

        panel.getChildren().addAll(title, sep, scroll);
        gameLayout.setLeft(panel);
    }

    private void buildLanesArea() {
        lanesContainer = new HBox(12);
        lanesContainer.setPadding(new Insets(12));
        lanesContainer.setAlignment(Pos.TOP_CENTER);
        lanesContainer.setStyle("-fx-background-color:#0d0b08;");

        ScrollPane scroll = new ScrollPane(lanesContainer);
        scroll.setFitToHeight(true);
        scroll.setStyle("-fx-background:#0d0b08;-fx-background-color:#0d0b08;");
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        gameLayout.setCenter(scroll);
    }

    private void buildActionBar() {
        HBox bar = new HBox(12);
        bar.setPadding(new Insets(10, 18, 10, 18));
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.setStyle(
            "-fx-background-color:#0d0b08;" +
            "-fx-border-color:#3d2e18;-fx-border-width:2 0 0 0;");

        selectedLaneLabel   = infoLabel("No lane selected");
        selectedWeaponLabel = infoLabel("No weapon selected");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        passTurnButton = new Button("PASS TURN");
        passTurnButton.setStyle(
            "-fx-background-color:#1a1208;-fx-text-fill:#8a7356;" +
            "-fx-font-size:13px;-fx-font-weight:bold;" +
            "-fx-padding:9 22;-fx-border-color:#3d2e18;" +
            "-fx-border-radius:3;-fx-background-radius:3;-fx-cursor:hand;");

        buyWeaponButton = new Button("DEPLOY WEAPON");
        buyWeaponButton.setStyle(
            "-fx-background-color:#8b1a1a;-fx-text-fill:#e8d5b0;" +
            "-fx-font-size:13px;-fx-font-weight:bold;" +
            "-fx-padding:9 22;-fx-background-radius:3;-fx-cursor:hand;" +
            "-fx-border-color:#c0392b;-fx-border-width:0 0 2 0;-fx-border-radius:3;");
        buyWeaponButton.setDisable(true);

        bar.getChildren().addAll(selectedLaneLabel, selectedWeaponLabel,
                spacer, passTurnButton, buyWeaponButton);
        gameLayout.setBottom(bar);
    }

    private void buildOverlay() {
        // Semi-transparent dark backdrop
        Rectangle backdrop = new Rectangle(1200, 750, Color.web("#000000", 0.72));

        // ── Card ─────────────────────────────────────────────────
        VBox card = new VBox(22);
        card.setAlignment(Pos.TOP_CENTER);
        card.setPadding(new Insets(36, 52, 36, 52));
        card.setMaxWidth(460);
        card.setStyle(
            "-fx-background-color:#0d0b08;" +
            "-fx-border-color:#5c4a2a;-fx-border-width:1;" +
            "-fx-border-radius:4;-fx-background-radius:4;");

        // Title
        Label title = new Label("PAUSED");
        title.setStyle(
            "-fx-font-size:28px;-fx-font-weight:bold;-fx-text-fill:#d4b896;-fx-letter-spacing:6;");

        // ── AI Speed ─────────────────────────────────────────────
        Label speedTitle = overlaySection("AI SPEED");

        oSlowBtn   = new Button("SLOW");
        oNormalBtn = new Button("NORMAL");
        oFastBtn   = new Button("FAST");
        HBox speedRow = new HBox(10, oSlowBtn, oNormalBtn, oFastBtn);
        speedRow.setAlignment(Pos.CENTER);

        // ── Display toggles ───────────────────────────────────────
        Label dispTitle = overlaySection("DISPLAY");
        oTitanHPBtn = new Button("TITAN HP: ON");
        oDangerBtn  = new Button("DANGER LVL: ON");
        HBox dispRow = new HBox(10, oTitanHPBtn, oDangerBtn);
        dispRow.setAlignment(Pos.CENTER);

        Line divider = hRule();

        // ── Action buttons ────────────────────────────────────────
        resumeButton = new Button("RESUME");
        resumeButton.setMaxWidth(Double.MAX_VALUE);
        resumeButton.setStyle(
            "-fx-background-color:#3a5228;-fx-text-fill:#a8d878;" +
            "-fx-font-size:14px;-fx-font-weight:bold;" +
            "-fx-padding:11 0;-fx-background-radius:3;-fx-cursor:hand;" +
            "-fx-border-color:#4a6a35;-fx-border-width:0 0 2 0;-fx-border-radius:3;");

        resetButton = new Button("RESET GAME");
        resetButton.setMaxWidth(Double.MAX_VALUE);
        resetButton.setStyle(
            "-fx-background-color:#2a1a08;-fx-text-fill:#b8860b;" +
            "-fx-font-size:13px;-fx-font-weight:bold;" +
            "-fx-padding:10 0;-fx-background-radius:3;-fx-cursor:hand;" +
            "-fx-border-color:#5c3d10;-fx-border-width:1;-fx-border-radius:3;");

        quitButton = new Button("QUIT GAME");
        quitButton.setMaxWidth(Double.MAX_VALUE);
        quitButton.setStyle(
            "-fx-background-color:#1a0808;-fx-text-fill:#8b1a1a;" +
            "-fx-font-size:13px;-fx-font-weight:bold;" +
            "-fx-padding:10 0;-fx-background-radius:3;-fx-cursor:hand;" +
            "-fx-border-color:#3d1010;-fx-border-width:1;-fx-border-radius:3;");

        card.getChildren().addAll(
            title,
            speedTitle, speedRow,
            dispTitle,  dispRow,
            divider,
            resumeButton, resetButton, quitButton);

        overlay = new StackPane(backdrop, card);
        overlay.setAlignment(Pos.CENTER);
        overlay.setVisible(false);
    }

    // ── Overlay sync ─────────────────────────────────────────────────

    private void syncOverlayToSettings() {
        double speed = GameSettings.get().getAiSpeed();
        oSlowBtn  .setStyle(overlaySpeedStyle(speed == 2.0));
        oNormalBtn.setStyle(overlaySpeedStyle(speed == 1.2));
        oFastBtn  .setStyle(overlaySpeedStyle(speed == 0.4));

        refreshOverlayToggle(oTitanHPBtn, "TITAN HP",    GameSettings.get().isShowTitanHP());
        refreshOverlayToggle(oDangerBtn,  "DANGER LVL",  GameSettings.get().isShowDangerLevel());
    }

    public void syncSpeedButtons() {
        double speed = GameSettings.get().getAiSpeed();
        oSlowBtn  .setStyle(overlaySpeedStyle(speed == 2.0));
        oNormalBtn.setStyle(overlaySpeedStyle(speed == 1.2));
        oFastBtn  .setStyle(overlaySpeedStyle(speed == 0.4));
    }

    public void refreshOverlayToggle(Button btn, String label, boolean on) {
        btn.setText(label + ": " + (on ? "ON" : "OFF"));
        btn.setStyle(on ? overlayToggleActiveStyle() : overlayToggleInactiveStyle());
    }

    // ── Weapon card builder ───────────────────────────────────────────

    private VBox buildWeaponCard(int code, WeaponRegistry reg) {
        String accent = weaponAccentColor(code);
        VBox card = new VBox(5);
        card.setPadding(new Insets(9, 10, 9, 12));
        card.setStyle(
            "-fx-background-color:#0d0b08;-fx-background-radius:4;" +
            "-fx-border-color:#3d2e18 #3d2e18 #3d2e18 " + accent + ";" +
            "-fx-border-radius:4;-fx-border-width:1 1 1 3;");

        Label name = new Label(reg.getName());
        name.setStyle("-fx-font-size:11px;-fx-font-weight:bold;-fx-text-fill:#d4b896;-fx-wrap-text:true;");
        name.setMaxWidth(190);

        Label type = new Label(resolveTypeName(code));
        type.setStyle("-fx-font-size:9px;-fx-text-fill:" + accent + ";");

        HBox stats = new HBox(10);
        Label price = new Label("$" + reg.getPrice());
        price.setStyle("-fx-font-size:10px;-fx-text-fill:#b8860b;-fx-font-weight:bold;");
        Label dmg = new Label("DMG " + reg.getDamage());
        dmg.setStyle("-fx-font-size:10px;-fx-text-fill:#8b1a1a;");
        stats.getChildren().addAll(price, dmg);

        Button btn = new Button("SELECT");
        btn.setStyle(weaponBtnStyle(false));
        btn.setMaxWidth(Double.MAX_VALUE);
        weaponButtons.put(code, btn);

        card.getChildren().addAll(name, type, stats, btn);
        return card;
    }

    // ── Helpers ──────────────────────────────────────────────────────

    private void updatePhaseLabel(BattlePhase phase) {
        switch (phase) {
            case EARLY:
                phaseLabel.setText("EARLY");
                phaseLabel.setStyle(hudChipStyle("#4a7c59", "#0d1a10")); break;
            case INTENSE:
                phaseLabel.setText("INTENSE");
                phaseLabel.setStyle(hudChipStyle("#b8860b", "#1a1200")); break;
            case GRUMBLING:
                phaseLabel.setText("GRUMBLING");
                phaseLabel.setStyle(hudChipStyle("#8b1a1a", "#1a0808")); break;
        }
    }

    private void updateBuyButton() {
        buyWeaponButton.setDisable(selectedLaneView == null || selectedWeaponCode == -1);
    }

    private Label hudChip(String text, String fg, String bg) {
        Label l = new Label(text);
        l.setStyle(hudChipStyle(fg, bg));
        return l;
    }

    private String hudChipStyle(String fg, String bg) {
        return "-fx-font-size:12px;-fx-font-weight:bold;-fx-text-fill:" + fg
             + ";-fx-background-color:" + bg + ";-fx-padding:12 16;-fx-letter-spacing:1;";
    }

    private Label infoLabel(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-font-size:12px;-fx-text-fill:#3d2e18;");
        return l;
    }

    private Label vSep() {
        Label s = new Label("|");
        s.setStyle("-fx-text-fill:#2a1e10;-fx-padding:0 2;-fx-font-size:22px;");
        return s;
    }

    private Label overlaySection(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-font-size:11px;-fx-font-weight:bold;-fx-text-fill:#5c4a2a;-fx-letter-spacing:3;");
        return l;
    }

    private Line hRule() {
        Line l = new Line(0, 0, 356, 0);
        l.setStroke(Color.web("#2a1e10"));
        l.setStrokeWidth(1);
        return l;
    }

    private String overlaySpeedStyle(boolean active) {
        if (active)
            return "-fx-background-color:#3d2e18;-fx-text-fill:#d4b896;" +
                   "-fx-font-size:12px;-fx-font-weight:bold;" +
                   "-fx-padding:8 22;-fx-background-radius:3;-fx-cursor:hand;" +
                   "-fx-border-color:#5c4a2a;-fx-border-width:1;-fx-border-radius:3;";
        return "-fx-background-color:#0d0b08;-fx-text-fill:#3d2e18;" +
               "-fx-font-size:12px;-fx-font-weight:bold;" +
               "-fx-padding:8 22;-fx-background-radius:3;-fx-cursor:hand;" +
               "-fx-border-color:#1a1208;-fx-border-width:1;-fx-border-radius:3;";
    }

    private String overlayToggleActiveStyle() {
        return "-fx-background-color:#1a2a10;-fx-text-fill:#4a7c59;" +
               "-fx-font-size:12px;-fx-font-weight:bold;" +
               "-fx-padding:8 18;-fx-background-radius:3;-fx-cursor:hand;" +
               "-fx-border-color:#2a4a20;-fx-border-width:1;-fx-border-radius:3;";
    }

    private String overlayToggleInactiveStyle() {
        return "-fx-background-color:#0d0b08;-fx-text-fill:#3d2e18;" +
               "-fx-font-size:12px;-fx-font-weight:bold;" +
               "-fx-padding:8 18;-fx-background-radius:3;-fx-cursor:hand;" +
               "-fx-border-color:#1a1208;-fx-border-width:1;-fx-border-radius:3;";
    }

    private String weaponBtnStyle(boolean selected) {
        if (selected)
            return "-fx-background-color:#3d2e18;-fx-text-fill:#d4b896;" +
                   "-fx-font-size:10px;-fx-font-weight:bold;" +
                   "-fx-padding:3 10;-fx-background-radius:3;-fx-cursor:hand;" +
                   "-fx-border-color:#5c4a2a;-fx-border-width:1;-fx-border-radius:3;";
        return "-fx-background-color:#0d0b08;-fx-text-fill:#5c4a2a;" +
               "-fx-font-size:10px;-fx-font-weight:bold;" +
               "-fx-padding:3 10;-fx-background-radius:3;-fx-cursor:hand;" +
               "-fx-border-color:#2a1e10;-fx-border-width:1;-fx-border-radius:3;";
    }

    private String weaponAccentColor(int code) {
        switch (code) {
            case 1: return "#8b1a1a";
            case 2: return "#3a5228";
            case 3: return "#7a6040";
            case 4: return "#2e4820";
            default: return "#5c4a2a";
        }
    }

    private String resolveTypeName(int code) {
        switch (code) {
            case 1: return "Piercing Cannon  —  hits up to 5 titans";
            case 2: return "Sniper Cannon  —  targets closest titan";
            case 3: return "Volley Spread  —  range-based 20-50";
            case 4: return "Wall Trap  —  activates at the wall";
            default: return "Unknown";
        }
    }
}
