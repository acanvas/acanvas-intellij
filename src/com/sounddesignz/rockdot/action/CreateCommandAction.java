package com.sounddesignz.rockdot.action;

import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

public class CreateCommandAction extends AbstractCreateAction {
  public CreateCommandAction() {
    super("command");
  }

  @Override
  protected CreateHandler getHandler(Project project) {
    return new CreateCommandHandler(project, (Component) null);
  }
}

class CreateCommandHandler extends CreateHandler {

  public CreateCommandHandler(@Nullable Project project, @Nullable Component dialogParent) {
    super(project, dialogParent);
    dartCommand = "add_command";
  }

}