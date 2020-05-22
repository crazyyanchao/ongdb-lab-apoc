/**
 *
 */
package data.lab.wltea.analyzer.cfg;

import data.lab.wltea.analyzer.dic.Dictionary;

import java.nio.file.Path;

public class Configuration {

    //是否启用智能分词
    private boolean useSmart;

    //是否启用远程词典加载
    private boolean enableRemoteDict = false;

    //是否启用小写处理
    private boolean enableLowercase = true;


//	@Inject
//	public Configuration(Environment env,Settings settings) {
//		this.environment = env;
//		this.settings=settings;
//
//		this.useSmart = settings.get("use_smart", "false").equals("true");
//		this.enableLowercase = settings.get("enable_lowercase", "true").equals("true");
//		this.enableRemoteDict = settings.get("enable_remote_dict", "true").equals("true");
//
//		Dictionary.initial(this);
//
//	}

    public Configuration(boolean useSmart, boolean enableRemoteDict, boolean enableLowercase) {

        this.useSmart = useSmart;
        this.enableLowercase = enableRemoteDict;
        this.enableRemoteDict = enableLowercase;

        Dictionary.initial(this);
    }

    public Configuration(boolean useSmart) {

        this.useSmart = useSmart;
        Dictionary.initial(this);

    }

    //	public Path getConfigInPluginDir() {
//		return PathUtils
//				.get(new File(AnalysisIkPlugin.class.getProtectionDomain().getCodeSource().getLocation().getPath())
//						.getParent(), "config")
//				.toAbsolutePath();
//	}
    public Path getConfigInPluginDir() {
        return null;
    }

    public boolean isUseSmart() {
        return useSmart;
    }

    public Configuration setUseSmart(boolean useSmart) {
        this.useSmart = useSmart;
        return this;
    }

//	public Environment getEnvironment() {
//		return environment;
//	}
//
//	public Settings getSettings() {
//		return settings;
//	}

    public boolean isEnableRemoteDict() {
        return enableRemoteDict;
    }

    public boolean isEnableLowercase() {
        return enableLowercase;
    }
}

