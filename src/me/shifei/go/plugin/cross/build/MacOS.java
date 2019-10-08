package me.shifei.go.plugin.cross.build;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;

/**
 * @author wangshifei
 */
public class MacOS extends AnAction {

    private static Logger logger = Logger.getInstance(MacOS.class);

    @Override
    public void actionPerformed(AnActionEvent e) {
        logger.info("Go build MacOS");
        Helper.build(e,"darwin","Go build macOS");
    }
}
