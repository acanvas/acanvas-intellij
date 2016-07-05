package com.sounddesignz.rockdot.projectWizard;

import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.ProjectBuilder;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.options.ConfigurationException;

import javax.swing.*;

public class RdModuleWizardStep extends ModuleWizardStep implements Disposable {
  private final WizardContext myContext;
  private final RdProjectGeneratorPeer myPeer;

  public RdModuleWizardStep(final WizardContext context) {
    myContext = context;
    myPeer = new RdProjectGeneratorPeer();
  }

  @Override
  public JComponent getComponent() {
    return myPeer.getComponent();
  }

  @Override
  public void updateDataModel() {
    final ProjectBuilder projectBuilder = myContext.getProjectBuilder();
    if (projectBuilder instanceof RdModuleBuilder) {
      ((RdModuleBuilder)projectBuilder).setWizardData(myPeer.getSettings());
    }
  }

  @Override
  public boolean validate() throws ConfigurationException {
    return myPeer.validateInIntelliJ();
  }

  @Override
  public void dispose() {

  }
}
