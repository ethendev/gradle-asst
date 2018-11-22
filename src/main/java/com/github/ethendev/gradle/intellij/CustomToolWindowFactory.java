package com.github.ethendev.gradle.intellij;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.ContentFactory;

/**
 * @author: Ethan
 * @Date: 2018/11/17
 */
public class CustomToolWindowFactory implements ToolWindowFactory {

    public void createToolWindowContent(Project project, ToolWindow toolWindow) {
        DependencyViewer dependencyViewer = new DependencyViewer(project, toolWindow);
        toolWindow.getContentManager().addContent(
                ContentFactory.SERVICE.getInstance().createContent(dependencyViewer, "", false));
    }
}
