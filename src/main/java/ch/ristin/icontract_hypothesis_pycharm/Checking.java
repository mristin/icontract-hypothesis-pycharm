package ch.ristin.icontract_hypothesis_pycharm;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.ui.Messages;
import com.jetbrains.python.packaging.PyPackageManager;

/**
 * Check that the dependencies are correctly installed and inform the user via dialogs otherwise.
 */
public class Checking {
    /**
     * Check that the correct version of icontract-hypothesis is installed in the virtual environment.
     *
     * @param project related to the event
     * @return true if everything is OK
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    static public boolean icontractHypothesis(Project project) {
        final Sdk sdk = ProjectRootManager.getInstance(project).getProjectSdk();
        if (sdk == null) {
            Messages.showMessageDialog(
                    project,
                    "There is no SDK specified for the project.\n\n" +
                    "Icontract-hypothesis-pycharm needs an interpreter to run icontract-hypothesis.",
                    "No SDK Specified",
                    Messages.getErrorIcon());
            return false;
        }

        final var packages = PyPackageManager.getInstance(sdk).getPackages();
        if (packages == null) {
            Messages.showMessageDialog(
                    project,
                    "There are no packages specified for the SDK: " + sdk.getName() + ".\n" +
                            "Icontract-hypothesis-pycharm depends on the package icontract-hypothesis.",
                    "No Packages Specified for the SDK",
                    Messages.getErrorIcon());
            return false;
        }

        String versionFound = "";
        for (var pkg : packages) {
            if (pkg.getName().equals("icontract-hypothesis")) {
                versionFound = pkg.getVersion();
                break;
            }
        }

        if (versionFound.equals("")) {
            Messages.showMessageDialog(
                    project,
                    "The package icontract-hypothesis has not been installed " +
                            "in the virtual environment of your current SDK: " + sdk.getName() + ".\n\n" +
                            "Icontract-hypothesis-pycharm depends on icontract-hypothesis.\n" +
                            "Please install it in your virtual environment.",
                    "No Icontract-Hypothesis Found",
                    Messages.getErrorIcon());
            return false;
        }

        if (!versionFound.startsWith("1.")) {
            Messages.showMessageDialog(
                    project,
                    "The version of this icontract-hypothesis-pycharm expects the version 1.*.* " +
                            "of the icontract-hypothesis.\n\n" +
                            "However, icontract-hypothesis " + versionFound + " is installed " +
                            "in the virtual environment of your current SDK: " + sdk.getName() + ".\n\n" +
                            "Please install the expected version of icontract-hypothesis in your virtual environment.",
                    "Unexpected Version of Icontract-Hypothesis Found",
                    Messages.getErrorIcon());
            return false;
        }

        return true;
    }
}
