/*
 * Copyright 2018-2019 Ethan Chan.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
        CustomToolWindow dependencyViewer = new CustomToolWindow(project, toolWindow);
        toolWindow.getContentManager().addContent(
                ContentFactory.SERVICE.getInstance().createContent(dependencyViewer.getContent(), "", false));
    }
}
