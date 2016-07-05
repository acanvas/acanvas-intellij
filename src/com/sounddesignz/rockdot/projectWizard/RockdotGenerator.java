package com.sounddesignz.rockdot.projectWizard;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.CapturingProcessHandler;
import com.intellij.execution.process.ProcessOutput;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class RockdotGenerator {

  public static class RockdotGeneratorDescriptor {
    public final String myId;
    public final String myLabel;
    public final String myDescription;
    public final String myEntrypoint;

    public RockdotGeneratorDescriptor(String id, String label, String description, String entrypoint) {
      myId = id;
      myLabel = label;
      myDescription = description;
      myEntrypoint = entrypoint;
    }

    @Override
    public String toString() {
      return StringUtil.join("[", myId, ",", myLabel, ",", myDescription, ",", myEntrypoint, "]");
    }
  }

  public static class RockdotGeneratorException extends Exception {
    public RockdotGeneratorException(String message) {
      super(message);
    }

    public RockdotGeneratorException(Throwable t) {
      super(t);
    }
  }

  private static final Logger LOG = Logger.getInstance("#com.sounddesignz.rockdot.projectWizard.RockdotGenerator");
  private static final List<RockdotGeneratorDescriptor> EMPTY = new ArrayList<RockdotGeneratorDescriptor>();

  private static final class PubRunner {

    private final String myWorkingDirectory;

    PubRunner() {
      myWorkingDirectory = null;
    }

    PubRunner(final VirtualFile workingDirectory) {
      myWorkingDirectory = workingDirectory.getCanonicalPath();
    }

    ProcessOutput runSync(final @NotNull String sdkRoot, int timeoutInSeconds, List<String> pubParameters) throws RockdotGeneratorException {
      final GeneralCommandLine command = new GeneralCommandLine().withWorkDirectory(myWorkingDirectory);

      final File pubFile = new File(_getPubPath(sdkRoot));
      command.setExePath(pubFile.getPath());
      command.addParameters(pubParameters);

      try {
        return new CapturingProcessHandler(command).runProcess(timeoutInSeconds * 1000, false);
      }
      catch (ExecutionException e) {
        throw new RockdotGeneratorException(e);
      }
    }
  }

  public void generateInto(@NotNull final RdProjectWizardData wizardData,
                           @NotNull final VirtualFile projectDirectory,
                           @NotNull final String templateId) throws RockdotGeneratorException {

    List<String> list = new ArrayList<String>();

    String params = "basic"; //templateId
    list.add("global");
    list.add("run");
    list.add("rockdot_generator");
    list.add("basic");

    if(wizardData.installSamples){
      params += " --install-demos";
      list.add("--install-demos");
    }
    if(wizardData.installMaterial){
      params += " --material";
      list.add("--material");
    }
    if(wizardData.installGoogle){
      params += " --google";
      list.add("--google");
    }
    if(wizardData.installFacebook){
      params += " --facebook";
      list.add("--facebook");
    }
    if(wizardData.installPhysics){
      params += " --physics";
      list.add("--physics");
    }
    if(wizardData.installUGC){
      params += " --ugc";
      list.add("--ugc");
    }

    //final ProcessOutput output = new PubRunner(projectDirectory).runSync(wizardData.dartSdkPath, 30, "global", "run", "rockdot_generator", params);
    final ProcessOutput output = new PubRunner(projectDirectory).runSync(wizardData.dartSdkPath, 30, list);
    if (output.getExitCode() != 0) {
      throw new RockdotGeneratorException(output.getStderr());
    }
  }

  public List<RockdotGeneratorDescriptor> getAvailableTemplates(@NotNull final String sdkRoot) {
    try {
      List<String> list = new ArrayList<String>();
      list.add("global");
      list.add("run");
      list.add("rockdot_generator");
      list.add("--machine");
      final ProcessOutput output = new PubRunner().runSync(sdkRoot, 10, list);
      int exitCode = output.getExitCode();

      if (exitCode != 0) {
        return EMPTY;
      }

      // [{"name":"consoleapp", "label":"Console App", "description":"A minimal command-line application."}, {"name": ..., }]
      JSONArray arr = new JSONArray(output.getStdout());
      List<RockdotGeneratorDescriptor> result = new ArrayList<RockdotGeneratorDescriptor>();

      for (int i = 0; i < arr.length(); i++) {
        JSONObject obj = arr.getJSONObject(i);

        result.add(new RockdotGeneratorDescriptor(
          obj.getString("name"),
          obj.optString("label"),
          obj.getString("description"),
          obj.optString("entrypoint")));
      }

      return result;
    }
    catch (RockdotGeneratorException e) {
      LOG.info(e);
    }
    catch (JSONException e) {
      LOG.info(e);
    }

    return EMPTY;
  }

  public void install(@NotNull final String sdkRoot) {
    try {
        List<String> list = new ArrayList<String>();
        list.add("global");
        list.add("activate");
        list.add("--source git https://github.com/blockforest/rockdot-generator");
      new PubRunner().runSync(sdkRoot, 60, list);
    }
    catch (RockdotGeneratorException e) {
      LOG.info(e);
    }
  }

  private static String _getPubPath(@NotNull String sdkRoot) {
    return sdkRoot + (SystemInfo.isWindows?"/bin/pub.bat":"/bin/pub");
  }
}
