package com.sounddesignz.rockdot.action.settings;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;

import javax.swing.*;

/**
 * User: Alisa.Afonina
 * Date: 9/29/11
 * Time: 11:54 AM
 */
public class RdSettingsFormWrapper extends DialogWrapper{
    private RdSettingsForm rdSettingsForm;
    private RdProperties rdProperties;

    public RdSettingsFormWrapper(Project project) {
        super(project);
        rdProperties = new RdProperties("public.properties", project);
        rdSettingsForm = new RdSettingsForm(project, rdProperties);
        setTitle("Rockdot Settings");
        init();
    }

    @Override
    protected String getDimensionServiceKey() {
        return getClass().getName();
    }

    @Override
    public JComponent getPreferredFocusedComponent() {
        return super.getPreferredFocusedComponent();
    }

    @Override
    protected JComponent createCenterPanel() {
        return rdSettingsForm.getContents();
    }

    @Override
    protected void doOKAction() {
        if(isOKActionEnabled()) {
            super.doOKAction();
        }
    }

    public void save() {
        rdSettingsForm.save();

    }
}