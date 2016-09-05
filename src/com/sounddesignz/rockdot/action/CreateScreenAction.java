package com.sounddesignz.rockdot.action;

import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

public class CreateScreenAction extends AbstractCreateAction {
    public CreateScreenAction() {
        super("screen");
    }

    @Override
    protected CreateHandler getHandler(Project project) {
        return new CreateScreenHandler(project, (Component) null);
    }
}

class CreateScreenHandler extends CreateHandler {

    public CreateScreenHandler(@Nullable Project project, @Nullable Component dialogParent) {
        super(project, dialogParent);
        dartCommand = "add_screen";
    }
}