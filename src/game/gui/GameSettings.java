package game.gui;

public class GameSettings {

    private static final GameSettings INSTANCE = new GameSettings();

    private double  aiSpeedSeconds  = 1.2;
    private boolean showTitanHP     = true;
    private boolean showDangerLevel = true;

    private GameSettings() {}

    public static GameSettings get() { return INSTANCE; }

    public double  getAiSpeed()           { return aiSpeedSeconds; }
    public void    setAiSpeed(double s)   { aiSpeedSeconds = s; }

    public boolean isShowTitanHP()            { return showTitanHP; }
    public void    setShowTitanHP(boolean v)  { showTitanHP = v; }

    public boolean isShowDangerLevel()             { return showDangerLevel; }
    public void    setShowDangerLevel(boolean v)   { showDangerLevel = v; }
}
