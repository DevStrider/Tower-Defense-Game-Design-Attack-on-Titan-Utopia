package game.gui.controllers;

import game.gui.GameApp;
import game.gui.views.SettingsView;

public class SettingsController {

    public SettingsController(SettingsView view) {
        view.getBackButton().setOnAction(e -> GameApp.showStartScreen());
    }
}
