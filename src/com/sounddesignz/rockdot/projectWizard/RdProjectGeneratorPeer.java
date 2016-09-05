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
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.util.ui.AsyncProcessIcon;
import com.intellij.xml.util.XmlStringUtil;
import com.jetbrains.lang.dart.ide.runner.client.DartiumUtil;
import com.jetbrains.lang.dart.sdk.DartSdkUtil;
import com.sounddesignz.rockdot.common.RdSdk;
import com.sounddesignz.rockdot.common.RockdotBundle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.text.JTextComponent;
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

  private JPanel optionsTable;
  private JPanel loadingPanel;
  private JPanel sxlOptionsPanel;
  private JBCheckBox sxlBitmapFont;

  private JBLabel myErrorLabel; // shown in IntelliJ IDEA only
  private JRadioButton sxlRadioButton;
  private JRadioButton rdRadioButton;
  private JBCheckBox sxlBitmapFontExamples;
  private JBCheckBox sxlDragonBonesExamples;
  private JBCheckBox sxlDragonBones;
  private JBCheckBox sxlFlump;
  private JBCheckBox sxlFlumpExamples;
  private JBCheckBox sxlGAFExamples;
  private JBCheckBox sxlGAF;
  private JBCheckBox sxlSpine;
  private JBCheckBox sxlSpineExamples;
  private JBCheckBox rdMaterial;
  private JBCheckBox rdMaterialExamples;
  private JBCheckBox rdGoogle;
  private JBCheckBox rdGoogleExamples;
  private JBCheckBox rdFacebook;
  private JBCheckBox rdFacebookExamples;
  private JBCheckBox rdPhysics;
  private JBCheckBox rdPhysicsExamples;
  private JBCheckBox rdUGC;
  private JBCheckBox rdUGCExamples;
  private JBCheckBox rdBabylon;
  private JBCheckBox rdBabylonExamples;
  private JBCheckBox sxlExamplesForRd;
  private JPanel frameworkTable;
  private JPanel rdOptionsPanel;
  private JBCheckBox star;

  private ChromeSettings myDartiumSettingsCurrent;

  private boolean myIntellijLiveValidationEnabled = false;
  private RdProjectTemplate _selectedTemplate;

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


    myErrorLabel.setIcon(AllIcons.Actions.Lightning);
    myErrorLabel.setVisible(false);

    final String sdkPath = mySdkPathComboWithBrowse.getComboBox().getEditor().getItem().toString().trim();
    final String message = DartSdkUtil.getErrorMessageIfWrongSdkRootPath(sdkPath);
    if (message == null) {
      startLoadingTemplates();
    }
    else {
      loadingPanel.setVisible(false);

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
    loadingPanel.setVisible(true);

    frameworkTable.setEnabled(false);
    optionsTable.setEnabled(false);

    final AsyncProcessIcon asyncProcessIcon = new AsyncProcessIcon("Preparing. Be patient.");
    loadingPanel.add(asyncProcessIcon, new GridConstraints());  // defaults are ok: row = 0, column = 0
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

    if(templates.size() == 0){
      //error
      return;
    }

    _selectedTemplate = (RdProjectTemplate) templates.get(0);

    loadingPanel.setVisible(false);

    frameworkTable.setEnabled(true);
    optionsTable.setEnabled(true);

    rdRadioButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(final ActionEvent e) {
        if(rdRadioButton.isSelected()){

          rdOptionsPanel.setEnabled( true );
          _sxlExamplesSetEnabled(true);
        }
        //enable full options table
      }
    });

    sxlRadioButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(final ActionEvent e) {
        if(sxlRadioButton.isSelected()){

          rdOptionsPanel.setEnabled( false );
          _sxlExamplesSetEnabled(false);
        }
      }
    });


    rdRadioButton.setSelected(true);


    // manage selection of examples
    ActionListener al = new ActionListener() {
      @Override
      public void actionPerformed(final ActionEvent e) {

        AbstractButton ab = (AbstractButton) e.getSource();

        if(!ab.isSelected()){

          switch(ab.getName()){
            case "sxlFlump":
              sxlFlumpExamples.setSelected(false);
            break;
            case "sxlBitmapFont":
              sxlBitmapFontExamples.setSelected(false);
              break;
            case "sxlGAF":
              sxlGAFExamples.setSelected(false);
              break;
            case "sxlDragonBones":
              sxlDragonBonesExamples.setSelected(false);
              break;
            case "sxlSpine":
              sxlSpineExamples.setSelected(false);
              break;
            case "rdFacebook":
              rdFacebookExamples.setSelected(false);
              break;
            case "rdGoogle":
              rdGoogleExamples.setSelected(false);
              break;
            case "rdMaterial":
              rdMaterialExamples.setSelected(false);
              break;
            case "rdPhysics":
              rdPhysicsExamples.setSelected(false);
              break;
            case "rdUGC":
              rdUGCExamples.setSelected(false);
              break;
            case "rdBabylon":
              rdBabylonExamples.setSelected(false);
              break;
          }

        }

        if(rdRadioButton.isSelected()){

          rdOptionsPanel.setEnabled( true );
          _sxlExamplesSetEnabled(true);
        }
        //enable full options table
      }
    };

    sxlSpine.addActionListener(al);
    sxlFlump.addActionListener(al);
    sxlGAF.addActionListener(al);
    sxlBitmapFont.addActionListener(al);
    sxlDragonBones.addActionListener(al);

    rdUGC.addActionListener(al);
    rdPhysics.addActionListener(al);
    rdFacebook.addActionListener(al);
    rdGoogle.addActionListener(al);
    rdMaterial.addActionListener(al);
    rdBabylon.addActionListener(al);


  }

  private void _sxlExamplesSetEnabled(boolean b) {
    sxlBitmapFontExamples.setEnabled( b );
    sxlDragonBonesExamples.setEnabled( b );
    sxlFlumpExamples.setEnabled( b );
    sxlGAFExamples.setEnabled( b );
    sxlSpineExamples.setEnabled( b );
    sxlExamplesForRd.setEnabled( b );
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
    settingsStep.addSettingsComponent(optionsTable);
  }

  @NotNull
  @Override
  public RdProjectWizardData getSettings() {
    RdProjectWizardData pd = new RdProjectWizardData();

    pd.dartSdkPath = FileUtil.toSystemIndependentName(mySdkPathComboWithBrowse.getComboBox().getEditor().getItem().toString().trim());
    pd.dartiumPath =
      FileUtil.toSystemIndependentName(myDartiumPathComboWithBrowse.getComboBox().getEditor().getItem().toString().trim());

    pd.template = _selectedTemplate;

    PropertiesComponent.getInstance().setValue(ROCKDOT_PROJECT_TEMPLATE, _selectedTemplate.getName());

    pd.stagexl = sxlRadioButton.isSelected();
    boolean rd = rdRadioButton.isSelected();


    //StageXL Options
    pd.bitmapFont = sxlBitmapFont.isSelected();
    pd.dragonBones = sxlDragonBones.isSelected();
    pd.flump = sxlFlump.isSelected();
    pd.gaf = sxlGAF.isSelected();
    pd.spine = sxlSpine.isSelected();


    if(rd){

      //StageXL Examples for Rockdot
      pd.stagexlExamples = sxlExamplesForRd.isSelected();

      //Rockdot Options
      pd.material = rdMaterial.isSelected();
      pd.materialExamples = pd.material && rdMaterialExamples.isSelected();
      pd.google = rdGoogle.isSelected();
      pd.googleExamples = pd.google && rdGoogleExamples.isSelected();
      pd.facebook = rdFacebook.isSelected();
      pd.facebookExamples = pd.facebook && rdFacebookExamples.isSelected();
      pd.physics = rdPhysics.isSelected();
      pd.physicsExamples = pd.physics && rdPhysicsExamples.isSelected();
      pd.ugc = rdUGC.isSelected();
      pd.ugcExamples = pd.ugc && rdUGCExamples.isSelected();
      pd.babylon = rdBabylon.isSelected();
      pd.babylonExamples = pd.babylon && rdBabylonExamples.isSelected();

      pd.bitmapFontExamples = pd.bitmapFont && sxlBitmapFontExamples.isSelected();
      pd.dragonBonesExamples = pd.dragonBones && sxlDragonBonesExamples.isSelected();
      pd.flumpExamples = pd.flump && sxlFlumpExamples.isSelected();
      pd.gafExamples = pd.gaf && sxlGAFExamples.isSelected();
      pd.spineExamples = pd.spine && sxlSpineExamples.isSelected();

    }

    return pd;
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

    if (_selectedTemplate == null) {
        return new ValidationInfo(RockdotBundle.message("project.template.not.selected"), sxlBitmapFont);
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

  }

  private void createUIComponents() {
    mySdkPathComboWithBrowse = new ComboboxWithBrowseButton(new ComboBox());
    myDartiumPathComboWithBrowse = new ComboboxWithBrowseButton(new ComboBox());
  }
}
