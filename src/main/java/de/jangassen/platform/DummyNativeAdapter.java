package de.jangassen.platform;

import de.jangassen.model.AppearanceMode;
import javafx.application.Platform;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.Window;

public class DummyNativeAdapter implements NativeAdapter {

  @Override
  public void setApplicationMenu(Menu menu) {
    // Only supported on macOS
  }

  @Override
  public void hide() {
    Window.getWindows().stream()
            .filter(Window::isFocused)
            .findFirst()
            .ifPresent(Window::hide);
  }

  @Override
  public void hideOtherApplications() {
    // Only supported on macOS
  }

  @Override
  public void showAllWindows() {
    Window.getWindows().stream()
            .filter(Stage.class::isInstance)
            .map(Stage.class::cast)
            .forEach(Stage::show);
  }

  @Override
  public void setDocIconMenu(Menu menu) {
    // Only supported on macOS
  }

  @Override
  public void quit() {
    Platform.exit();
  }

  @Override
  public void setForceQuitOnCmdQ(boolean forceQuit) {
    // Only supported on macOS
  }

  @Override
  public void setTrayMenu(Menu menu) {
    // Only supported on macOS
  }

  @Override
  public boolean systemUsesDarkMode() {
    return false;
  }

  @Override
  public void setAppearanceMode(AppearanceMode mode) {
    // Only supported on macOS
  }

  public void showContextMenu(Menu menu, MouseEvent event) {
    Window.getWindows().stream().filter(Window::isFocused).findFirst().ifPresent(window -> {
      ContextMenu contextMenu = new ContextMenu();
      contextMenu.getItems().addAll(menu.getItems());
      contextMenu.show(window, event.getScreenX(), event.getScreenY());
    });
  }
}
