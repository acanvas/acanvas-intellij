package com.sounddesignz.rockdot.action;

import com.intellij.ide.IdeBundle;
import com.intellij.ide.IdeView;
import com.intellij.ide.actions.*;
import com.intellij.ide.util.DirectoryChooserUtil;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.WebModuleTypeBase;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.DumbModePermission;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.file.PsiDirectoryFactory;
import com.intellij.psi.search.FileTypeIndex;
import com.jetbrains.lang.dart.DartFileType;
import com.jetbrains.lang.dart.sdk.DartSdk;
import com.sounddesignz.rockdot.common.RockdotBundle;
import icons.DartIcons;
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