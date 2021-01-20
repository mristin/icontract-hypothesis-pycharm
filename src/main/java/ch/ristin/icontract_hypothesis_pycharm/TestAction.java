// Copyright (c) 2020 Marko Ristin <marko@ristin.ch>

package ch.ristin.icontract_hypothesis_pycharm;

import com.intellij.execution.RunManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Test with icontract-hypothesis.
 */
public class TestAction extends AnAction {
    /**
     * Specify and run the "icontract-hypothesis test" command as a run configuration.
     *
     * @param event Event received when the associated menu item is chosen.
     */
    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        final Project project = event.getProject();
        assert project != null;

        if (!Checking.icontractHypothesis(project)) {
            return;
        }

        Context context = Contextualizing.infer(event);

        assert context != null;

        final String path = Contextualizing.path(context);

        String target = path;
        if (context.pyFunction != null) {
            target += " " + context.pyFunction.getName();
        }
        String runName = String.format("icontract-hypothesis test %s", target);

        var scriptParameters = new ArrayList<String>();
        scriptParameters.add("test");
        scriptParameters.add("--path");
        scriptParameters.add(path);
        if (context.pyFunction != null) {
            scriptParameters.add("--include");
            scriptParameters.add(context.pyFunction.getName());
        }

        var runManager = RunManager.getInstance(project);
        var runner = Running.FindOrCreateRunner(
                runManager, runName, scriptParameters,
                Pathing.systemDependentPath(context.project.getBasePath()));

        Running.execute(runManager, runner);
    }

    /**
     * Change the visibility/enabled as well as the title of the action depending on the context.
     *
     * @param event Event received when the associated menu item is chosen.
     */
    @Override
    public void update(@NotNull AnActionEvent event) {
        Context context = Contextualizing.infer(event);

        final Presentation presentation = event.getPresentation();

        if (context == null) {
            presentation.setEnabledAndVisible(false);
            return;
        }

        // Change the name depending whether it is a module or a global function
        String target;
        if (context.pyFunction != null) {
            target = context.pyFunction.getName();
        } else {
            target = Contextualizing.path(context);
        }

        presentation.setText(String.format("test %s", target), false);
        presentation.setDescription(
                String.format("Infer the Hypothesis strategy for %s and execute it", target));

        presentation.setEnabledAndVisible(true);
    }
}
