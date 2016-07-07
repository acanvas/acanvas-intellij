package com.sounddesignz.rockdot.action.settings;

import com.intellij.ide.IdeView;
import com.intellij.lang.properties.PropertiesBundle;
import com.intellij.lang.properties.ResourceBundle;
import com.intellij.lang.properties.editor.ResourceBundleAsVirtualFile;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.DumbModePermission;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.sounddesignz.rockdot.action.CreateHandler;
import com.sounddesignz.rockdot.common.RockdotBundle;
import icons.DartIcons;


import java.io.*;
import java.util.Properties;
import java.util.PropertyResourceBundle;

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