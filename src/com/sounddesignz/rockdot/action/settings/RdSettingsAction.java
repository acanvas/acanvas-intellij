package com.sounddesignz.rockdot.action.settings;

import com.intellij.ide.IdeView;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.sounddesignz.rockdot.common.RockdotBundle;
import icons.DartIcons;

/**
 * Created by ndoehring on 05.07.16.
 */
public  class RdSettingsAction extends AnAction implements DumbAware {

    public RdSettingsAction() {
        super(RockdotBundle.message("settings.title"), RockdotBundle.message("settings.copy"), DartIcons.Dart_file);
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        IdeView view = e.getData(LangDataKeys.IDE_VIEW);
        final Project project = e.getData(CommonDataKeys.PROJECT);

        if (view == null || project == null) {
            return;
        }

        RdSettingsFormWrapper sfw = new RdSettingsFormWrapper(project);
        sfw.show();

        if(sfw.getExitCode() == DialogWrapper.OK_EXIT_CODE){

            sfw.save();

        }

    }

}