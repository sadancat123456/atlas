package com.taobao.android.builder;


import com.android.build.api.transform.JarInput;
import com.android.build.api.transform.QualifiedContent;
import com.google.common.collect.Multimap;
import com.taobao.android.builder.dependency.model.AwbBundle;
import com.taobao.android.builder.tasks.app.BuildAtlasEnvTask;
import com.taobao.android.builder.tools.MD5Util;
import org.gradle.internal.impldep.com.amazonaws.util.Md5Utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

/**
 * @author lilong
 * @create 2017-12-27 下午2:30
 */

public class AtlasMainDexHelper {

    private LinkedHashSet<BuildAtlasEnvTask.FileIdentity> mainDexJar = new LinkedHashSet<>();


    public List<File> getInputDirs() {
        return inputDirs;
    }

    public void setInputDirs(List<File> inputLibraries) {
        this.inputDirs = inputDirs;
    }

    private List<File> inputDirs = new ArrayList<>();

    private Map<String, Boolean> mainManifestMap = new HashMap<>();

    private Map<String, Boolean> mainNativeSoMap = new LinkedHashMap<>();

    private Map<String, Boolean> mainRes = new LinkedHashMap<>();

    public File getMainJavaRes() {
        return mainJavaRes;
    }


    private File mainJavaRes;

    private Map<QualifiedContent, List<File>> mainDexAchives = new HashMap<>();

    public void addMainDexJars(Set<BuildAtlasEnvTask.FileIdentity> maindexs) {
        mainDexJar.clear();
        mainDexJar.addAll(maindexs);
    }

    public void addAllMainDexJars(Collection<File> maindexs) {
        mainDexJar.clear();
        for (File file : maindexs) {
            mainDexJar.add(new BuildAtlasEnvTask.FileIdentity(file.getName(), file, false, false));

        }
    }


    public void addMainDexManifests(Map mainDexMap) {
        mainManifestMap.putAll(mainDexMap);
    }

    public void addMainDexNativeSos(Map mainNativeSo) {
        mainNativeSoMap.putAll(mainNativeSo);
    }

    public void addMainRes(Map mainResMap) {
        mainRes.putAll(mainResMap);
    }


    public Map<String, Boolean> getMainManifestFiles() {
        return mainManifestMap;
    }

    public LinkedHashSet<BuildAtlasEnvTask.FileIdentity> getMainDexFiles() {
        return mainDexJar;
    }

    public Map<String, Boolean> getMainSoFiles() {
        return mainNativeSoMap;
    }


    public void updateMainDexFile(File oldFile, File newFile) {
        for (BuildAtlasEnvTask.FileIdentity id : mainDexJar) {
            try {
                if (Files.isSameFile(oldFile.toPath(), id.file.toPath()) || MD5Util.getFileMD5(oldFile).equals(MD5Util.getFileMD5(id.file))) {
                    id.file = newFile;
                    break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void updateMainDexFile(JarInput oldFile, File newFile) {
        for (BuildAtlasEnvTask.FileIdentity id : mainDexJar) {
            try {
                if (Files.isSameFile(oldFile.getFile().toPath(), id.file.toPath()) || MD5Util.getFileMD5(oldFile.getFile()).equals(MD5Util.getFileMD5(id.file))) {
                    id.file = newFile;
                    break;
                } else if (oldFile.getName().startsWith("android.local.jars")) {
                    if (oldFile.getName().split(":")[1].equals(id.file.getName())) {
                        id.file = newFile;
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public void updateMainDexFiles(Map<JarInput, File> fileFileMap) {
        for (Map.Entry entry : fileFileMap.entrySet()) {
            updateMainDexFile((JarInput) entry.getKey(), (File) entry.getValue());
        }
    }


    public boolean inMainDex(JarInput jarInput) {
        File file = jarInput.getFile();
        boolean flag =  inMainDex(file);
        if (!flag) {
            if (jarInput.getName().startsWith("android.local.jars")) {
                return inMainDex(jarInput.getName().split(":")[1], jarInput);
            }
        }
        return flag;
    }

    private boolean inMainDex(String name, JarInput jarInput) {
        for (BuildAtlasEnvTask.FileIdentity fileIdentity : mainDexJar) {
            if (fileIdentity.file.getName().equals(name)) {
                fileIdentity.file = jarInput.getFile();
                return true;
            }
        }
        return false;
    }

    public boolean inMainDex(File jarFile)  {
        for (BuildAtlasEnvTask.FileIdentity fileIdentity : mainDexJar) {
            try {
                if (Files.isSameFile(fileIdentity.file.toPath(), jarFile.toPath()) || MD5Util.getFileMD5(fileIdentity.file).equals(MD5Util.getFileMD5(jarFile))) {
                    return true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }


    public BuildAtlasEnvTask.FileIdentity get(JarInput jarInput) {
        File file = jarInput.getFile();
        return get(file);
    }

    public BuildAtlasEnvTask.FileIdentity get(File jarFile) {
        for (BuildAtlasEnvTask.FileIdentity fileIdentity : mainDexJar) {
            try {
                if (Files.isSameFile(fileIdentity.file.toPath(), jarFile.toPath()) || MD5Util.getFileMD5(jarFile).equals(MD5Util.getFileMD5(fileIdentity.file))) {
                    return fileIdentity;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    public void addMainDex(BuildAtlasEnvTask.FileIdentity fileIdentity) {
        mainDexJar.add(fileIdentity);
    }


    public Collection<File> getAllMainDexJars() {
        Set<File> allMainDexJars = new HashSet<>();
        for (BuildAtlasEnvTask.FileIdentity fileIdentity : mainDexJar) {
            if (fileIdentity.file.exists()) {
                allMainDexJars.add(fileIdentity.file);
            }
        }
        return allMainDexJars;
    }

    public Map<String, Boolean> getMainResFiles() {
        return mainRes;
    }

    public void addMainDexAchives(Map<QualifiedContent, List<File>> cacheableItems) {
        this.mainDexAchives.putAll(cacheableItems);
    }

    public Map<QualifiedContent, List<File>> getMainDexAchives() {
        return mainDexAchives;
    }

    public void addMainJavaRes(File file) {
        this.mainJavaRes = file;
    }

    public void  release(){
        mainDexJar.clear();
        inputDirs.clear();
        mainRes.clear();
        mainNativeSoMap.clear();
        mainManifestMap.clear();
        mainDexAchives.clear();
        mainJavaRes = null;

    }
}