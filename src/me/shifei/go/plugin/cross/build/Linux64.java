package me.shifei.go.plugin.cross.build;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;

/**
 * @author wangshifei
 */
public class Linux64 extends AnAction {

    private static Logger logger = Logger.getInstance(Linux64.class);

    @Override
    public void actionPerformed(AnActionEvent e) {
        logger.info("Go build linux64");
        Helper.build(e,"linux","Go build linux64");
    }
}
