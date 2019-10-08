package me.shifei.go.plugin.cross.build;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;

public class Windows64 extends AnAction {

    private static Logger logger = Logger.getInstance(Windows64.class);

    @Override
    public void actionPerformed(AnActionEvent e) {
        Helper.build(e,"windows","Go build windows64");
    }
}
