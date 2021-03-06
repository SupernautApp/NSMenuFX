package de.jangassen.platform.mac.convert;

import de.jangassen.jfa.FoundationCallback;
import de.jangassen.jfa.FoundationCallbackRegistry;
import de.jangassen.jfa.ObjcToJava;
import de.jangassen.jfa.appkit.NSMenuItem;
import de.jangassen.jfa.cleanup.NSCleaner;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.KeyCombination;

import java.util.Optional;

public class MenuItemConverter {

  private static final String SEPARATOR_ITEM = "separatorItem";

  private MenuItemConverter() {
  }

  public static final FoundationCallback VOID_CALLBACK = new FoundationCallback(null, null);

  public static NSMenuItem convert(MenuItem menuItem) {
    if (menuItem == null) {
      return null;
    }

    if (menuItem instanceof SeparatorMenuItem) {
      return ObjcToJava.invokeStatic(NSMenuItem.class, SEPARATOR_ITEM);
    } else {
      return convertMenuItem(menuItem);
    }
  }

  private static NSMenuItem convertMenuItem(MenuItem menuItem) {
    FoundationCallback foundationCallback = getFoundationCallback(menuItem.getOnAction());

    NSMenuItem nsMenuItem = createNsMenuItem(menuItem, foundationCallback);
    menuItem.textProperty().addListener((observable, oldValue, newValue) ->
            nsMenuItem.setTitle(newValue)
    );

    menuItem.onActionProperty().addListener((observable, oldValue, newValue) ->
            updateAction(menuItem, nsMenuItem, newValue)
    );

    menuItem.acceleratorProperty().addListener((observable, oldValue, newValue) ->
            nsMenuItem.setKeyEquivalent(toKeyEquivalentString(newValue))
    );

    ImageConverter.convert(menuItem.getGraphic()).ifPresent(nsMenuItem::setImage);
    menuItem.graphicProperty().addListener((observable, oldValue, newValue) ->
            ImageConverter.convert(newValue).ifPresent(nsMenuItem::setImage)
    );

    NSCleaner.register(menuItem, nsMenuItem);
    registerCallbackForCleanup(menuItem, foundationCallback);
    return nsMenuItem;
  }

  private static void updateAction(MenuItem menuItem, NSMenuItem nsMenuItem, EventHandler<ActionEvent> eventHandler) {
    FoundationCallback foundationCallback = getFoundationCallback(eventHandler);
    nsMenuItem.setTarget(foundationCallback.getTarget());
    nsMenuItem.setAction(foundationCallback.getSelector());
    registerCallbackForCleanup(menuItem, foundationCallback);
  }

  private static void registerCallbackForCleanup(MenuItem menuItem, FoundationCallback foundationCallback) {
    if (foundationCallback != VOID_CALLBACK) {
      NSCleaner.register(menuItem, foundationCallback);
    }
  }

  private static NSMenuItem createNsMenuItem(MenuItem menuItem, FoundationCallback foundationCallback) {
    String text = Optional.ofNullable(menuItem.getText()).orElse("");
    NSMenuItem nsMenuItem = NSMenuItem.alloc()
            .initWithTitle(text, foundationCallback.getSelector(), toKeyEquivalentString(menuItem.getAccelerator()));
    nsMenuItem.setTarget(foundationCallback.getTarget());
    return nsMenuItem;
  }

  private static FoundationCallback getFoundationCallback(EventHandler<ActionEvent> action) {
    if (action == null) {
      return VOID_CALLBACK;
    }
    return FoundationCallbackRegistry.registerCallback(id -> action.handle(new ActionEvent()));
  }

  private static String toKeyEquivalentString(KeyCombination accelerator) {
    if (accelerator == null) {
      return "";
    }

    return keyEquivalent(accelerator);
  }

  private static String keyEquivalent(KeyCombination accelerator) {
    String keyEquivalentString = accelerator.getName().replace("Meta", "");
    if (accelerator.getShift() != KeyCombination.ModifierValue.DOWN) {
      keyEquivalentString = keyEquivalentString.toLowerCase();
    }
    if (keyEquivalentString.startsWith("+")) {
      keyEquivalentString = keyEquivalentString.substring(1);
    }
    return keyEquivalentString;
  }
}
