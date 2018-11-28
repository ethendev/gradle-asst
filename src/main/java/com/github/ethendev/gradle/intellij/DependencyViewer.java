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
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.wm.ToolWindow;
import org.gradle.internal.impldep.com.google.common.collect.Maps;
import org.gradle.tooling.GradleConnector;
import org.gradle.tooling.ProjectConnection;
import org.gradle.tooling.model.ExternalDependency;
import org.gradle.tooling.model.GradleModuleVersion;
import org.gradle.tooling.model.idea.IdeaDependency;
import org.gradle.tooling.model.idea.IdeaModule;
import org.gradle.tooling.model.idea.IdeaProject;

import javax.swing.*;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author: Ethan
 * @Date: 2018/11/18
 */
public class DependencyViewer extends SimpleToolWindowPanel {

    private final Project project;
    private final ToolWindow toolWindow;
    private static Map<String, List<GradleModuleVersion>> map = Maps.newHashMap();

    public DependencyViewer(Project project, ToolWindow toolWindow) {
        super(true, true);
        this.project = project;
        this.toolWindow = toolWindow;

        ProjectConnection connection = GradleConnector.newConnector()
                .forProjectDirectory(new File(project.getBasePath()))
                .connect();
        try {
            IdeaProject iproject = connection.getModel(IdeaProject.class);
            for(IdeaModule module : iproject.getModules()) {
                List<GradleModuleVersion> list = new ArrayList();
                for (IdeaDependency dependency: module.getDependencies()){
                    if (dependency instanceof ExternalDependency) {
                        GradleModuleVersion gmv = ((ExternalDependency) dependency).getGradleModuleVersion();
                        Optional.ofNullable(gmv).ifPresent(e -> list.add(e));
                    }
                }
                map.put(module.getName(), list);
            }

            for (String key : map.keySet()) {
                System.out.println("Key = " + key);
                map.get(key).forEach(e -> System.out.println(e));
            }
        } finally {
            connection.close();
        }
    }

    public JScrollPane getContent() {
        JScrollPane jsp = new JScrollPane();
        Set<String> set = new HashSet<>();
        Optional.ofNullable(map.values()).ifPresent(e ->
                e.parallelStream().forEach(x -> x.forEach(y -> {
                    set.add(y.getGroup() + ":" + y.getName() + ":" + y.getVersion());
                })));
        JList<String> jlist = new JList<>();
        jlist.setListData(set.stream().sorted().collect(Collectors.toList()).toArray(new String[0]));
        jsp.setViewportView(jlist);
        jsp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        jsp.setBorder(null);
        return jsp;
    }

}
