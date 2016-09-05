package com.sounddesignz.rockdot.action;

import com.intellij.ide.IdeView;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.DumbModePermission;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.sounddesignz.rockdot.common.RockdotBundle;
import icons.DartIcons;

/**
 * Created by ndoehring on 05.07.16.
 */
public abstract class AbstractCreateAction extends AnAction implements DumbAware {
    private final String creationKey;

    public AbstractCreateAction(String key) {
        super(RockdotBundle.message("file." + key), RockdotBundle.message("file." + key), DartIcons.Dart_file);
        creationKey = key;
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        IdeView view = e.getData(LangDataKeys.IDE_VIEW);
        final Project project = e.getData(CommonDataKeys.PROJECT);

        if (view == null || project == null) {
            return;
        }

        final CreateHandler validator = getHandler(project);

        DumbService.allowStartingDumbModeInside(DumbModePermission.MAY_START_BACKGROUND, () -> Messages.showInputDialog(project, RockdotBundle.message("enter." + creationKey),
                RockdotBundle.message("enter." + creationKey),
                Messages.getQuestionIcon(), "", validator));


    }

    protected CreateHandler getHandler(Project project) {
        return null;
    }


}