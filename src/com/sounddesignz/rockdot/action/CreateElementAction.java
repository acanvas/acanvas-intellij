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