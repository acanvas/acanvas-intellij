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

import java.awt.*;

public class CreateCommandAction extends AnAction implements DumbAware {
  public CreateCommandAction() {
    super(RockdotBundle.message("file.command"), RockdotBundle.message("file.command"), DartIcons.Dart_file);
  }

  @Override
  public void actionPerformed(AnActionEvent e) {
    IdeView view = e.getData(LangDataKeys.IDE_VIEW);
    final Project project = e.getData(CommonDataKeys.PROJECT);

    if (view == null || project == null) {
      return;
    }

    final CreateHandler validator = new CreateHandler(project, (Component) null);

    DumbService.allowStartingDumbModeInside(DumbModePermission.MAY_START_BACKGROUND, () -> Messages.showInputDialog(project, RockdotBundle.message("enter.command"),
            RockdotBundle.message("enter.command"),
            Messages.getQuestionIcon(), "", validator));


  }


}
