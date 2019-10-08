package me.shifei.go.plugin.cross.build;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Helper
 *
 * @author wangshifei
 * @date 2019-09-30 14:03
 **/
public class Helper {

    private static Logger logger = Logger.getInstance(Helper.class);
    private static Map<String,String> sysEnvMap  = System.getenv();
    private static String cmd = sysEnvMap.get("GOROOT") + File.separator + "bin"+File.separator+"go";
    private Helper(){}

    public static Project getProject(AnActionEvent e){
        //获取当前在操作的工程上下文
        return e.getData(PlatformDataKeys.PROJECT);
    }

    public static String getClassPath(AnActionEvent e){
        //获取当前操作的类文件
        PsiFile psiFile = e.getData(CommonDataKeys.PSI_FILE);
        //获取当前类文件的路径
        String classPath = psiFile.getVirtualFile().getPath();
        return classPath;
    }

    public static Map<String,String> getEnv(String os){
        Map<String,String> envMap = new HashMap<>();

        for(Iterator<String> iter = System.getenv().keySet().iterator(); iter.hasNext();){
            String key = iter.next();
            if("GOOS".equals(key) || "GOARCH".equals(key)){
                continue;
            }
            envMap.put(key,sysEnvMap.get(key));
        }
        envMap.put("CGO_ENABLED","0");
        envMap.put("GOOS",os);
        envMap.put("GOARCH","amd64");
        return envMap;
    }

    public static void deleteBin(Project project) {
        boolean exefile = FileUtil.exists(project.getBasePath()+ File.separator+"main");
        if(exefile){
            FileUtil.delete(new File(project.getBasePath()+File.separator+"main"));
        }
    }

    public static void build(AnActionEvent e, String os, String title){
        String classPath = getClassPath(e);
        Project project = getProject(e);

        Helper.checkMainGo(classPath,project);
        Helper.deleteBin(project);

        GeneralCommandLine generalCommandLine = new GeneralCommandLine(cmd);
        generalCommandLine.withEnvironment(getEnv(os));
        generalCommandLine.withParameters("build",classPath);
        generalCommandLine.setCharset(Charset.forName("UTF-8"));
        generalCommandLine.setWorkDirectory(project.getBasePath());

        ProgressManager.getInstance().run(new Task.Backgroundable(project,title){
            @Override
            public void run(@NotNull ProgressIndicator progressIndicator) {
                ProcessHandler processHandler = null;
                try {
                    progressIndicator.setIndeterminate(false);
                    progressIndicator.setFraction(0.1);
                    progressIndicator.setText("building......");
                    processHandler = new OSProcessHandler(generalCommandLine);
                    processHandler.startNotify();
                    progressIndicator.setFraction(0.5);
                    while (!processHandler.isProcessTerminated()){
                        Thread.sleep(1000);
                    }
                    progressIndicator.setFraction(1.0);
                    progressIndicator.setText("build success");
                } catch (ExecutionException | InterruptedException ex) {
                    progressIndicator.setText("error:"+ex.getMessage());
                    ex.printStackTrace();
                    logger.error(ex);
                }
            }
        });
    }

    public static void checkMainGo(String classPath,Project project) {
        if (!classPath.endsWith("main.go")){
            Messages.showMessageDialog(project, classPath, "Please run with main.go", Messages.getInformationIcon());
            return;
        }
    }
}
