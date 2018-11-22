package com.github.ethendev.gradle.intellij;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.wm.ToolWindow;
import org.gradle.tooling.GradleConnector;
import org.gradle.tooling.ProjectConnection;
import org.gradle.tooling.model.idea.IdeaDependency;
import org.gradle.tooling.model.idea.IdeaModule;
import org.gradle.tooling.model.idea.IdeaProject;

import java.io.File;

/**
 * @author: Ethan
 * @Date: 2018/11/18
 */
public class DependencyViewer extends SimpleToolWindowPanel {

    private final Project project;
    private final ToolWindow toolWindow;

    public DependencyViewer(Project project, ToolWindow toolWindow) {
        super(true, true);
        this.project = project;
        this.toolWindow = toolWindow;

        ProjectConnection connection = GradleConnector.newConnector()
                .forProjectDirectory(new File(project.getBasePath())).useBuildDistribution()
                .connect();
        try {
            IdeaProject iproject = connection.getModel(IdeaProject.class);
            for(IdeaModule module : iproject.getModules()) {
                for (IdeaDependency dependency: module.getDependencies()){
                    System.out.println("      * " + dependency);
                }
            }
        } finally {
            connection.close();
        }
    }
}
