package com.sounddesignz.rockdot.projectWizard;

import com.intellij.ide.browsers.chrome.ChromeSettings;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class RdProjectWizardData {

  public String dartSdkPath;
  public String dartiumPath;
  public ChromeSettings dartiumSettings;
  public RdProjectTemplate template;

  public boolean stagexl;
  public boolean stagexlExamples;

  public boolean material;
  public boolean materialExamples;
  public boolean google;
  public boolean googleExamples;
  public boolean facebook;
  public boolean facebookExamples;
  public boolean physics;
  public boolean physicsExamples;
  public boolean ugc;
  public boolean ugcExamples;

  //StageXL Options
  public boolean bitmapFont;
  public boolean bitmapFontExamples;
  public boolean dragonBones;
  public boolean dragonBonesExamples;
  public boolean flump;
  public boolean flumpExamples;
  public boolean gaf;
  public boolean gafExamples;
  public boolean spine;
  public boolean spineExamples;
  public boolean babylon;
  public boolean babylonExamples;

  public RdProjectWizardData() {
   
  }

  public List<String> toList() {
    List<String> list = new ArrayList<String>();

    Field[] fields = this.getClass().getFields();
    for (Field field : fields) {
      if(field.getType().isAssignableFrom(boolean.class)){
        field.setAccessible(true);
        try {
          if((boolean) field.get(this)) {
            list.add("--" + field.getName());
          }
        } catch (IllegalAccessException e) {
          e.printStackTrace();
        }
      }
    }

    return list;
  }
}
