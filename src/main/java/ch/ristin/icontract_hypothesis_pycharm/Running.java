// Copyright (c) 2020 Marko Ristin <marko@ristin.ch>

package ch.ristin.icontract_hypothesis_pycharm;

import com.intellij.execution.RunManager;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.execution.runners.ExecutionUtil;
import com.jetbrains.python.run.PythonConfigurationType;
import com.jetbrains.python.run.PythonRunConfiguration;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class Running {
    static private final PythonConfigurationType pythonConfigurationType = PythonConfigurationType.getInstance();

    static public RunnerAndConfigurationSettings FindOrCreateRunner(
            RunManager runManager,
            String runName,
            List<String> scriptParameters,
            @Nullable String workingDirectory) {
        RunnerAndConfigurationSettings runner = null;

        for (var runnerAndConfigurationSettings : runManager.getConfigurationSettingsList(pythonConfigurationType)) {
            if (runnerAndConfigurationSettings.getName().equals(runName)) {
                runner = runnerAndConfigurationSettings;
            }
        }

        if (runner != null) {
            return runner;
        }

        runner = runManager.createConfiguration(runName, pythonConfigurationType.getClass());

        var configuration = (PythonRunConfiguration) runner.getConfiguration();

        configuration.setModuleMode(true);
        configuration.setScriptName("icontract_hypothesis");

        // See https://www.jetbrains.com/help/pycharm/run-debug-configuration-python.html#1
        var parameters = new ArrayList<>(scriptParameters);
        for (int i = 0; i < parameters.size(); i++) {
            String parameter = parameters.get(i);

            if (parameter.contains(" ") || parameter.contains("\"")) {
                parameters.set(i, "\"" + parameter.replace("\"", "\"\"") + "\"");

            }
        }

        configuration.setScriptParameters(String.join(" ", parameters));

        if (workingDirectory != null) {
            configuration.setWorkingDirectory(workingDirectory);
        }

        return runner;
    }

    static public void execute(RunManager runManager, RunnerAndConfigurationSettings runner) {
        runManager.setTemporaryConfiguration(runner);
        runManager.setSelectedConfiguration(runner);

        ExecutionUtil.runConfiguration(runner, DefaultRunExecutor.getRunExecutorInstance());
    }
}
