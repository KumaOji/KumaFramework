package com.kuma.cloud.idea.plugin.toolkit.listener.action;

import com.intellij.ui.EditorTextField;
import com.kuma.cloud.idea.plugin.toolkit.notification.ToolkitNotifier;
//import com.kuma.cloud.idea.plugin.toolkit.utils.JsonUtils;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.apache.commons.lang3.StringUtils;

/**
 * FormatJsonAction
 */
public class FormatJsonActionListener implements ActionListener {
    private EditorTextField editorTextField;

    public FormatJsonActionListener(EditorTextField editorTextField) {
        this.editorTextField = editorTextField;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String text = editorTextField.getText();
        if (StringUtils.isBlank(text)) {
            return;
        }
        try {
//            String formattedJson = JsonUtils.formatJson(text);
            editorTextField.setText(text);
        } catch (Exception ex) {
            ToolkitNotifier.error("Json format fail, please check the data.");
        }
    }
}
