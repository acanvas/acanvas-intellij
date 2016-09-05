package com.sounddesignz.rockdot.action;

import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

public class CreateElementAction extends AbstractCreateAction {
    public CreateElementAction() {
        super("element");
    }

    @Override
    protected CreateHandler getHandler(Project project) {
        return new CreateElementHandler(project, (Component) null);
    }
}

class CreateElementHandler extends CreateHandler {

    public CreateElementHandler(@Nullable Project project, @Nullable Component dialogParent) {
        super(project, dialogParent);
        dartCommand = "add_element";
    }
}