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

import com.github.ethendev.gradle.GradleUtils;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.wm.ToolWindow;
import org.gradle.internal.impldep.com.google.common.collect.Maps;
import org.gradle.tooling.model.GradleModuleVersion;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.util.List;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * @author: Ethan
 * @Date: 2018/11/18
 */
public class CustomToolWindow extends SimpleToolWindowPanel {

    private final Project project;
    private final ToolWindow toolWindow;
    private static Map<String, List<GradleModuleVersion>> map = Maps.newHashMap();

    public CustomToolWindow(Project project, ToolWindow toolWindow) {
        super(true, true);
        this.project = project;
        this.toolWindow = toolWindow;
    }

    public JPanel getContent() {
        JPanel panel = new JPanel(new BorderLayout());
        JToolBar toolBar = new JToolBar();

        JButton refreshBtn = new JButton(new ImageIcon(getClass().getResource("/actions/refresh.png")));
        refreshBtn.setBorderPainted(false);
        refreshBtn.setContentAreaFilled(false);
        refreshBtn.setToolTipText("Refresh");
        refreshBtn.setMaximumSize(new Dimension(30,30));

        JLabel label = new JLabel(" Model: ");
        JComboBox moldelList = new JComboBox();
        moldelList.setMaximumSize(moldelList.getPreferredSize());

        toolBar.add(refreshBtn);
        toolBar.add(label);
        toolBar.add(moldelList);
        toolBar.setFloatable(false);

        JScrollPane jsp = new JScrollPane();
        JList<String> jlist = new JList<>();
        setData(jlist, moldelList);
        jsp.setViewportView(jlist);
        jsp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        jsp.setBorder(null);

        refreshBtn.addActionListener(e -> setData(jlist, moldelList));

        moldelList.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                jlist.setListData(convertToArray(map.get(e.getItem().toString())));
                jsp.setViewportView(jlist);
            }
        });

        panel.add(jsp, BorderLayout.CENTER);
        panel.add(toolBar, BorderLayout.PAGE_START);
        return panel;
    }

    private void setData(JList<String> jlist, JComboBox moldelList) {
        CompletableFuture.supplyAsync(() -> {
            map = GradleUtils.analysisDependencies(project);
            jlist.setListData(convertToArray(map.values()));

            DefaultComboBoxModel dcbm = new DefaultComboBoxModel();
            Optional.ofNullable(map.keySet()).ifPresent(e -> e.forEach(x -> {
                dcbm.addElement(x);
            }));
            moldelList.setModel(dcbm);
            return map;
        });
    }

    private String[] convertToArray(Collection<List<GradleModuleVersion>> collection) {
        Set<String> set = new HashSet<>();
        Optional.ofNullable(collection).ifPresent(e ->
                e.parallelStream().forEach(x -> x.forEach(y -> {
                    set.add(y.getGroup() + ":" + y.getName() + ":" + y.getVersion());
                })));
        return set.stream().sorted().collect(Collectors.toList()).toArray(new String[0]);
    }

    private String[] convertToArray(List<GradleModuleVersion> list) {
        Set<String> set = new HashSet<>();
        Optional.ofNullable(list).ifPresent(x ->
                x.parallelStream().forEach(y ->
                        set.add(y.getGroup() + ":" + y.getName() + ":" + y.getVersion())));
        return set.stream().sorted().collect(Collectors.toList()).toArray(new String[0]);
    }

}
