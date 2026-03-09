package com.kuma.cloud.idea.plugin.toolkit.service;

import com.intellij.openapi.actionSystem.DataContext;
import com.kuma.cloud.idea.plugin.toolkit.domain.ToolkitCommand;

public interface ToolkitCommandService {
    void execute(ToolkitCommand command, DataContext dataContext);
}
