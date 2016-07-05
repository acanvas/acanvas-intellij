package com.sounddesignz.rockdot.projectWizard;

import com.intellij.icons.AllIcons;
import com.intellij.ide.browsers.BrowserSpecificSettings;
import com.intellij.ide.browsers.WebBrowser;
import com.intellij.ide.browsers.chrome.ChromeSettings;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.ide.util.projectWizard.SettingsStep;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.roots.ModifiableModelsProvider;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.roots.libraries.LibraryTable;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.io.FileUtilRt;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.platform.WebProjectGenerator;
import com.intellij.ui.ColorUtil;
import com.intellij.ui.ComboboxWithBrowseButton;
import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBList;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.util.ui.AsyncProcessIcon;
import com.intellij.xml.util.XmlStringUtil;
import com.jetbrains.lang.dart.ide.runner.client.DartiumUtil;
import com.jetbrains.lang.dart.sdk.DartSdk;
import com.jetbrains.lang.dart.sdk.DartSdkUtil;
import com.sounddesignz.rockdot.common.RdSdk;
import com.sounddesignz.rockdot.common.RockdotBundle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class RdProjectGeneratorPeer implements WebProjectGenerator.GeneratorPeer<RdProjectWizardData> {
  private static final String ROCKDOT_PROJECT_TEMPLATE = "ROCKDOT_PROJECT_TEMPLATE";

  private JPanel myMainPanel;
  private ComboboxWithBrowseButton mySdkPathComboWithBrowse;
  private JBLabel myVersionLabel;

  private JPanel myDartiumSettingsPanel;
  private ComboboxWithBrowseButton myDartiumPathComboWithBrowse;
  private JButton myDartiumSettingsButton;
  private JBCheckBox myCheckedModeCheckBox;

  private JPanel myTemplatesPanel;
  private JPanel myLoadingTemplatesPanel;
  private JPanel myLoadedTemplatesPanel;
  private JBCheckBox myCreateSampleProjectCheckBox;
  private JBCheckBox myInstallMaterial;
  private JBCheckBox myInstallGoogle;
  private JBCheckBox myInstallFacebook;
  private JBCheckBox myInstallPhysics;
  private JBCheckBox myInstallUGC;
  private JBList myTemplatesList;

  private JBLabel myErrorLabel; // shown in IntelliJ IDEA only
  private JBCheckBox star;

  private ChromeSettings myDartiumSettingsCurrent;

  private boolean myIntellijLiveValidationEnabled = false;

  public RdProjectGeneratorPeer() {

    // set initial values before initDartSdkAndDartiumControls() because listeners should not be triggered on initialization

    LibraryTable.ModifiableModel modifiableModel = ModifiableModelsProvider.SERVICE.getInstance().getLibraryTableModifiableModel();
    final Library[] libraries = modifiableModel.getLibraries();
    ModifiableModelsProvider.SERVICE.getInstance().disposeLibraryTableModifiableModel(modifiableModel);

    final RdSdk sdkInitial = RdSdk.findDartSdkAmongGlobalLibs(libraries);
    final String sdkPathInitial = sdkInitial == null ? "" : FileUtil.toSystemDependentName(sdkInitial.getHomePath());
    mySdkPathComboWithBrowse.getComboBox().setEditable(true);
    mySdkPathComboWithBrowse.getComboBox().getEditor().setItem(sdkPathInitial);

    final WebBrowser dartiumInitial = DartiumUtil.getDartiumBrowser();
    myDartiumSettingsCurrent = new ChromeSettings();
    if (dartiumInitial != null) {
      final BrowserSpecificSettings browserSpecificSettings = dartiumInitial.getSpecificSettings();
      if (browserSpecificSettings instanceof ChromeSettings) {
        myDartiumSettingsCurrent = (ChromeSettings)browserSpecificSettings.clone();
      }
    }

    final String dartiumPath = dartiumInitial == null ? ""
                                                      : FileUtilRt.toSystemDependentName(StringUtil.notNullize(dartiumInitial.getPath()));
    myDartiumPathComboWithBrowse.getComboBox().setEditable(true);
    myDartiumPathComboWithBrowse.getComboBox().getEditor().setItem(dartiumPath);


    // now setup controls
    DartSdkUtil.initDartSdkAndDartiumControls(null, mySdkPathComboWithBrowse, myVersionLabel, myDartiumPathComboWithBrowse,
                                              new Computable.PredefinedValueComputable<ChromeSettings>(myDartiumSettingsCurrent),
                                              myDartiumSettingsButton, myCheckedModeCheckBox,
                                              new Computable.PredefinedValueComputable<Boolean>(false));

    final boolean checkedMode = dartiumInitial == null || DartiumUtil.isCheckedMode(myDartiumSettingsCurrent.getEnvironmentVariables());
    myCheckedModeCheckBox.setSelected(checkedMode);

    myCreateSampleProjectCheckBox.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(final ActionEvent e) {
        myTemplatesList.setEnabled(myCreateSampleProjectCheckBox.isSelected());
      }
    });

    myTemplatesList.setEmptyText(RockdotBundle.message("set.sdk.to.see.sample.content.options"));

    myErrorLabel.setIcon(AllIcons.Actions.Lightning);
    myErrorLabel.setVisible(false);

    final String sdkPath = mySdkPathComboWithBrowse.getComboBox().getEditor().getItem().toString().trim();
    final String message = DartSdkUtil.getErrorMessageIfWrongSdkRootPath(sdkPath);
    if (message == null) {
      startLoadingTemplates();
    }
    else {
      myLoadingTemplatesPanel.setVisible(false);

      myCreateSampleProjectCheckBox.setEnabled(false);
      myTemplatesList.setEnabled(false);

      final JTextComponent editorComponent = (JTextComponent)mySdkPathComboWithBrowse.getComboBox().getEditor().getEditorComponent();
      editorComponent.getDocument().addDocumentListener(new DocumentAdapter() {
        @Override
        protected void textChanged(final DocumentEvent e) {
          final String sdkPath = mySdkPathComboWithBrowse.getComboBox().getEditor().getItem().toString().trim();
          final String message = DartSdkUtil.getErrorMessageIfWrongSdkRootPath(sdkPath);
          if (message == null) {
            editorComponent.getDocument().removeDocumentListener(this);
            startLoadingTemplates();
          }
        }
      });
    }
  }

  private void startLoadingTemplates() {
    myLoadingTemplatesPanel.setVisible(true);
    myLoadingTemplatesPanel.setPreferredSize(myLoadedTemplatesPanel.getPreferredSize());

    myLoadedTemplatesPanel.setVisible(false);

    myCreateSampleProjectCheckBox.setSelected(false); // until loaded

    final AsyncProcessIcon asyncProcessIcon = new AsyncProcessIcon("Dart project templates loading");
    myLoadingTemplatesPanel.add(asyncProcessIcon, new GridConstraints());  // defaults are ok: row = 0, column = 0
    asyncProcessIcon.resume();

    ApplicationManager.getApplication().executeOnPooledThread(() -> {
      final String sdkPath = mySdkPathComboWithBrowse.getComboBox().getEditor().getItem().toString().trim();
      RdProjectTemplate.loadTemplatesAsync(sdkPath, templates -> {
        asyncProcessIcon.suspend();
        Disposer.dispose(asyncProcessIcon);
        onTemplatesLoaded(templates);
      });
    });
  }

  private void onTemplatesLoaded(final List<RdProjectTemplate> templates) {
    myLoadingTemplatesPanel.setVisible(false);
    myLoadedTemplatesPanel.setVisible(true);
    myCreateSampleProjectCheckBox.setEnabled(true);
    myTemplatesList.setEnabled(true);

    final String selectedTemplateName = PropertiesComponent.getInstance().getValue(ROCKDOT_PROJECT_TEMPLATE);
    myCreateSampleProjectCheckBox.setSelected(selectedTemplateName != null);
    myTemplatesList.setEnabled(myCreateSampleProjectCheckBox.isSelected());

    RdProjectTemplate selectedTemplate = null;

    final DefaultListModel model = new DefaultListModel();
    for (RdProjectTemplate template : templates) {
      model.addElement(template);

      if (template.getName().equals(selectedTemplateName)) {
        selectedTemplate = template;
      }
    }

    myTemplatesList.setModel(model);

    if (selectedTemplate != null) {
      myTemplatesList.setSelectedValue(selectedTemplate, true);
    }
    else if (templates.size() > 0) {
      myTemplatesList.setSelectedIndex(0);
    }

    myTemplatesList.setCellRenderer(new DefaultListCellRenderer() {
      @Override
      public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        final JLabel component = (JLabel)super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        final RdProjectTemplate template = (RdProjectTemplate)value;
        final String text = template.getDescription().isEmpty()
                            ? template.getName()
                            : template.getName() + " - " + StringUtil.decapitalize(template.getDescription());
        component.setText(text);
        return component;
      }
    });
  }

  @NotNull
  @Override
  public JComponent getComponent() {
    return myMainPanel;
  }

  @Override
  public void buildUI(final @NotNull SettingsStep settingsStep) {
    settingsStep.addSettingsField(RockdotBundle.message("dart.sdk.path.label"), mySdkPathComboWithBrowse);
    settingsStep.addSettingsField(RockdotBundle.message("version.label"), myVersionLabel);
    settingsStep.addSettingsField(RockdotBundle.message("dartium.path.label"), myDartiumSettingsPanel);
    settingsStep.addSettingsField("", myCheckedModeCheckBox);
    settingsStep.addSettingsComponent(myTemplatesPanel);
  }

  @NotNull
  @Override
  public RdProjectWizardData getSettings() {
    final String sdkPath = FileUtil.toSystemIndependentName(mySdkPathComboWithBrowse.getComboBox().getEditor().getItem().toString().trim());
    final String dartiumPath =
      FileUtil.toSystemIndependentName(myDartiumPathComboWithBrowse.getComboBox().getEditor().getItem().toString().trim());

    final RdProjectTemplate template = myCreateSampleProjectCheckBox.isSelected()
                                         ? (RdProjectTemplate)myTemplatesList.getSelectedValue() : null;
    PropertiesComponent.getInstance().setValue(ROCKDOT_PROJECT_TEMPLATE, template == null ? null : template.getName());

    final boolean samples = myCreateSampleProjectCheckBox.isSelected();
    final boolean material = myInstallMaterial.isSelected();
    final boolean google = myInstallGoogle.isSelected();
    final boolean facebook = myInstallFacebook.isSelected();
    final boolean physics = myInstallPhysics.isSelected();
    final boolean ugc = myInstallUGC.isSelected();

    return new RdProjectWizardData(sdkPath, dartiumPath, myDartiumSettingsCurrent, template, samples, material, google, facebook, physics, ugc);
  }

  @Nullable
  @Override
  public ValidationInfo validate() {
    // invalid Dartium path is not a blocking error
    final String sdkPath = mySdkPathComboWithBrowse.getComboBox().getEditor().getItem().toString().trim();
    final String message = DartSdkUtil.getErrorMessageIfWrongSdkRootPath(sdkPath);
    if (message != null) {
      return new ValidationInfo(message, mySdkPathComboWithBrowse);
    }

    if (myCreateSampleProjectCheckBox.isSelected()) {
      if (myTemplatesList.getSelectedValue() == null) {
        return new ValidationInfo(RockdotBundle.message("project.template.not.selected"), myCreateSampleProjectCheckBox);
      }
    }

    return null;
  }

  public boolean validateInIntelliJ() {
    final ValidationInfo info = validate();

    if (info == null) {
      myErrorLabel.setVisible(false);
      return true;
    }
    else {
      myErrorLabel.setVisible(true);
      myErrorLabel
        .setText(XmlStringUtil.wrapInHtml("<font color='#" + ColorUtil.toHex(JBColor.RED) + "'><left>" + info.message + "</left></font>"));

      if (!myIntellijLiveValidationEnabled) {
        myIntellijLiveValidationEnabled = true;
        enableIntellijLiveValidation();
      }

      return false;
    }
  }

  private void enableIntellijLiveValidation() {
    final JTextComponent editorComponent = (JTextComponent)mySdkPathComboWithBrowse.getComboBox().getEditor().getEditorComponent();
    editorComponent.getDocument().addDocumentListener(new DocumentAdapter() {
      @Override
      protected void textChanged(final DocumentEvent e) {
        validateInIntelliJ();
      }
    });

    myCreateSampleProjectCheckBox.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(final ActionEvent e) {
        validateInIntelliJ();
      }
    });

    myTemplatesList.addListSelectionListener(new ListSelectionListener() {
      @Override
      public void valueChanged(final ListSelectionEvent e) {
        validateInIntelliJ();
      }
    });
  }

  @Override
  public boolean isBackgroundJobRunning() {
    return false;
  }

  @Override
  public void addSettingsStateListener(final @NotNull WebProjectGenerator.SettingsStateListener stateListener) {
    // invalid Dartium path is not a blocking error
    final JTextComponent editorComponent = (JTextComponent)mySdkPathComboWithBrowse.getComboBox().getEditor().getEditorComponent();
    editorComponent.getDocument().addDocumentListener(new DocumentAdapter() {
      protected void textChanged(final DocumentEvent e) {
        stateListener.stateChanged(validate() == null);
      }
    });

    myCreateSampleProjectCheckBox.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(final ActionEvent e) {
        stateListener.stateChanged(validate() == null);
      }
    });

    myTemplatesList.addListSelectionListener(new ListSelectionListener() {
      @Override
      public void valueChanged(final ListSelectionEvent e) {
        stateListener.stateChanged(validate() == null);
      }
    });
  }

  private void createUIComponents() {
    mySdkPathComboWithBrowse = new ComboboxWithBrowseButton(new ComboBox());
    myDartiumPathComboWithBrowse = new ComboboxWithBrowseButton(new ComboBox());
  }
}
