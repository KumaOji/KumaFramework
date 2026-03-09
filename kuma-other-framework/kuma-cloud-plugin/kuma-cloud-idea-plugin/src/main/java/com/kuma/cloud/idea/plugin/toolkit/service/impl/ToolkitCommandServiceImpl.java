package com.kuma.cloud.idea.plugin.toolkit.service.impl;

import com.intellij.openapi.actionSystem.DataContext;
import com.kuma.cloud.idea.plugin.toolkit.domain.ToolkitCommand;
import com.kuma.cloud.idea.plugin.toolkit.domain.executor.ToolkitCommandExecutor;
import com.kuma.cloud.idea.plugin.toolkit.domain.executor.ToolkitCommandExecutorComposite;
import com.kuma.cloud.idea.plugin.toolkit.service.ToolkitCommandService;

/**
 * ToolkitCommandServiceImpl
 *
 * @author kuma
 * @version 2026.02
 * @since 2025-12-19 09:30:45
 */
public class ToolkitCommandServiceImpl implements ToolkitCommandService {

    private ToolkitCommandExecutor toolkitCommandExecutor = new ToolkitCommandExecutorComposite();

    @Override
    public void execute( ToolkitCommand command, DataContext dataContext ) {
        toolkitCommandExecutor.execute(command, dataContext);
    }
}
