package com.github.flycatzly.sqlparsing.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.java.tuple.Tuple2;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

@Slf4j
public class GitUtils {

    public static boolean isEmpty(@Nullable Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    public static String getCurrentDateTimeStr() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss"));
    }

    /**
     * 列出所有文件及文件夹
     * @param directory
     * @return
     */
    public static List<File> listFilesAndDirectories(File directory) {
        List<File> fileList = new ArrayList<>();
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                fileList.add(file);
                if (file.isDirectory()) {
                    // 递归列出子文件夹中的文件和文件夹
                    fileList.addAll(listFilesAndDirectories(file));
                }
            }
        }
        return fileList;
    }

    private static void deleteDirectory(File directory) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file);
                } else {
                    file.delete();
                }
            }
        }
        directory.delete();
    }

    /**
     * 从第一个任意字符【斜杠】前面或后面截取所有字符串
     * @param input
     * @param way true 前  false 后
     * @param separator 字符 [, /]
     * @return
     */
    public static String substringStr(String input,boolean way,String separator) {
        String resultStr ="";
        int index = input.indexOf(separator);
        if (index != -1) {
            if(way){
                resultStr = input.substring(0, index);
            }else{
                resultStr = input.substring(index + 1);
            }
        }
        if (resultStr.startsWith(separator)) {
            resultStr = resultStr.substring(1);
        }
        log.info("原字符串[{}],截取的结果为[{}]",input,resultStr);
        return resultStr;
    }

    /**
     * 首次需要 克隆完整 .git
     * 创建本地远程仓库
     * @param repoPath
     * @return
     * @throws IOException
     */
    public static Git getRepo(String repoPath, String remoteRepoURI, String branchName,
                              CredentialsProvider provider) {
        Git git =null;
        try {
            File repoFile = new File(repoPath+"\\.git");
            if (repoFile.exists()) {
                // Open an existing repository
                Repository repo = new FileRepositoryBuilder()
                        .setGitDir(repoFile)
                        .findGitDir()
                        .build();
                git =new Git(repo);
                log.info("Open an existing repository[{}]",repo.getDirectory());
            } else {
                // Create a new repository
                //Repository repo = FileRepositoryBuilder.create(repoFile);
                //repo.create(true);

                git = Git.cloneRepository()
                        .setURI(remoteRepoURI)   //设置git远端URL
                        .setDirectory(repoFile.getParentFile())  //设置本地仓库位置
                        .setCredentialsProvider(provider)   //设置身份验证
                        .setCloneSubmodules(true)    //设置是否克隆子仓库
                        .setBranch(branchName)  //设置克隆分支
                        .call();   //启动命令
                log.info("Create a new repository[{}]",git.getRepository().getDirectory());
            }
            StoredConfig config = git.getRepository().getConfig();
            config.setBoolean("http", null, "sslVerify", false);
            config.save();
        }catch (Exception e){
            log.error("创建本地仓库失败{}", e);
        }
        return git;
    }

    /**
     * 克隆并下载代码
     * @param branchName
     * @param proPath
     * @param remoteRepoURI
     * @param personalAccessToken
     */
    public static void gitClone(String branchName,String proPath,String remoteRepoURI,String username,String personalAccessToken) {
        Git git = null;
        try {
            log.info("start to clone code,pull:[{}] >>>To [{}]",remoteRepoURI,proPath);
            CredentialsProvider provider = new UsernamePasswordCredentialsProvider(username, personalAccessToken);
            // 使用个人访问令牌进行身份验证
            //UsernamePasswordCredentialsProvider provider = new UsernamePasswordCredentialsProvider(username, personalAccessToken);

            File localPath = new File(proPath +"\\.git");
            if (localPath.exists()) {
                Repository rep = new FileRepositoryBuilder()
                        .setGitDir(localPath)
                        //.readEnvironment()
                        .findGitDir()
                        .build();
                git = new Git(rep);
                git.pull().setCredentialsProvider(provider)
                        .setRemote("origin")
                        .setRemoteBranchName(branchName)
                        .call();
                log.info("拉取最新代码成功,{}",localPath);
            } else {
                // 克隆远程仓库
                git = Git.cloneRepository()
                        .setURI(remoteRepoURI)   //设置git远端URL
                        .setDirectory(localPath.getParentFile())  //设置本地仓库位置
                        .setCredentialsProvider(provider)   //设置身份验证
                        .setCloneSubmodules(true)    //设置是否克隆子仓库
                        .setBranch(branchName)  //设置克隆分支
                        .call();   //启动命令
                log.info("创建仓库并拉取最新代码成功,{}",localPath);
            }
            StoredConfig config = git.getRepository().getConfig();
            config.setBoolean("http", null, "sslVerify", false);
            config.save();
        } catch (Exception e) {
            log.error("创建本地仓库失败{}", e);
            throw new RuntimeException("创建本地仓库失败"+e);
        }finally {
            if(git!=null){
                if(git.getRepository()!=null){
                    git.getRepository().close();
                }
                git.close();
            }
        }
    }

    /**
     * 提交发布文件
     * 可以提交目录或文件，不支持提交空文件夹
     * 在使用 JGit 来提交空文件夹时，可能会遇到一些限制，因为 Git 不会直接跟踪和存储空文件夹
     * @param branchName
     * @param proPath
     * @param remoteRepoURI
     * @param file 目录 -目录下所有文件夹及文件递归 文件-单个
     * @param message
     * @param personalAccessToken
     */
    public static void commitPushCode(String branchName, String proPath, String remoteRepoURI, File file, String message,
                                      String username,String personalAccessToken) {
        Git git =null;
        if (!file.exists()){
            String err =String.format("param file[%s] does not exist",file.getPath());
            log.error(err);
            throw new RuntimeException(err);
        }
        try {
            CredentialsProvider provider = new UsernamePasswordCredentialsProvider(username, personalAccessToken);
            //获取仓库对象
            git = getRepo(proPath, remoteRepoURI, branchName,provider);
            String pagePath = file.getName();
            File targetFile = new File(proPath);
            if(!targetFile.exists()){targetFile.mkdirs();}
            File targetSourceFile = new File(proPath+File.separator+file.getName());
            if (targetSourceFile.exists()){
                deleteDirectory(targetSourceFile);
            }
            if(file.isDirectory()){
                // 移动目录到目标目录，并覆盖目标地址已存在的目录
                FileUtils.moveDirectoryToDirectory(file, targetFile, true);
            }else{
                // 移动文件到目标目录，并覆盖目标地址已存在的文件
                FileUtils.moveFileToDirectory(file, targetFile, true);
            }
            //添加文件
            git.add().addFilepattern(pagePath).call();
            log.info("add file success, pagePath is [{}]",pagePath);
            if(StringUtils.isBlank(message)){
                message = String.format("commit [%s] %s",file.getName(),getCurrentDateTimeStr());
            }
            git.commit().setMessage(message).call();
            log.info("commit file success,[{}]",file.getPath());
            git.push().setRemote(remoteRepoURI)    //设置推送的URL名称
                    .setRefSpecs(new RefSpec(branchName))   //设置需要推送的分支,如果远端没有则创建
                    .setCredentialsProvider(provider)       //身份验证
                    // .setForce(true) //强提交
                    .call();
            log.info("succeed add,commit,push files. to repository at {}",git.getRepository().getDirectory());

        } catch (Exception e) {
            log.error("git提交失败{}",e);
            throw new RuntimeException("git提交失败"+e);
        } finally {
            if(git!=null){
                git.close();
            }
        }
    }

    /**
     * 批量提交发布
     * 可以提交目录或文件，不支持提交空文件夹
     * 在使用 JGit 来提交空文件夹时，可能会遇到一些限制，因为 Git 不会直接跟踪和存储空文件夹
     * @param branchName
     * @param proPath
     * @param remoteRepoURI
     * @param fileList  文件必须在proPath里
     * @param message
     * @param personalAccessToken
     */
    public static void commitPushCodeBatch(String branchName, String proPath, String remoteRepoURI, List<File> fileList,
                                           String message,String username,String personalAccessToken) {
        Git git = null;
        StringJoiner stringJoiner = new StringJoiner(",");
        List<Tuple2<String, Boolean>> resultList = new ArrayList<>();
        try {
            if(isEmpty(fileList)){
                String msg = String.format("该文件地址[%s]不存在！",fileList);
                log.error(msg);
                throw new RuntimeException(msg);
            }
            CredentialsProvider provider = new UsernamePasswordCredentialsProvider(username, personalAccessToken);
            //获取仓库对象
            git = getRepo(proPath, remoteRepoURI, branchName,provider);
            String workTreePath = git.getRepository().getWorkTree().getCanonicalPath();
            for (File file : fileList) {
                String pagePath="";
                if(file.exists()){
                    pagePath = file.getCanonicalPath()
                            .substring(workTreePath.length())
                            .replace(File.separatorChar, '/');
                    pagePath = substringStr(pagePath,false,"/");

/*                    File targetSourceFile = new File(proPath+File.separator+file.getName());
                    if (targetSourceFile.exists()){
                        deleteDirectory(targetSourceFile);
                    }*/
                    File targetFile = new File(proPath);
                    if(file.isDirectory()){
                        // 移动目录到目标目录，并覆盖目标地址已存在的目录
                        FileUtils.moveDirectoryToDirectory(file, targetFile, true);
                        resultList.add(new Tuple2<>(pagePath, true));
                    }else{
                        // 移动文件到目标目录，并覆盖目标地址已存在的文件
                        FileUtils.moveFileToDirectory(file, targetFile, true);
                        resultList.add(new Tuple2<>(pagePath, false));
                    }
                }
            }

            List<String> handleStrList = resultList.stream()
                    .map(item->{
                        if(item.f1){
                            return substringStr(item.f0,true,"/");
                        }
                        if (item.f0.startsWith("/")) {
                            return item.f0.substring(1);
                        }
                        return item.f0;
                    })
                    .distinct()
                    .collect(Collectors.toList());
            for (String str : handleStrList) {
                stringJoiner.add(str);
                git.add().addFilepattern(str).call();
            }
            //添加文件
            log.info("add file success, file is [{}],sourceFile[{}]>>>To workTreePath[{}]",stringJoiner,resultList,workTreePath);
            if(StringUtils.isBlank(message)){
                message = String.format("commit [%s] %s",stringJoiner,getCurrentDateTimeStr());
            }
            git.commit().setMessage(message).call();
            log.info("commit file success,[{}]",stringJoiner);
            git.push().setRemote(remoteRepoURI)    //设置推送的URL名称
                    .setRefSpecs(new RefSpec(branchName))   //设置需要推送的分支,如果远端没有则创建
                    .setCredentialsProvider(provider)       //身份验证
                    .call();
            log.info("succeed add,commit,push files. to repository at {}",git.getRepository().getDirectory());

        } catch (Exception e) {
            log.error("git提交失败{}",e);
            throw new RuntimeException("git提交失败"+e);
        } finally {
            if(git.getRepository()!=null){
                git.getRepository().close();
            }
            git.close();
        }
    }



    public static void main(String[] args) throws IOException, GitAPIException {

        String remoteRepoURI = "https://your-git-server.com/your-repo.git";
        String localRepoPath = "D:/Git/repo";
        String branchName = "dev_alvin_20240221"; // 要拉取的分支名称
        //String personalAccessToken = "your_token";
        //String username ="your_username";
        String username ="your_username";
        String personalAccessToken = "your_personal_access_token";
        //gitClone(branchName,localRepoPath,remoteRepoURI,username,personalAccessToken);

        //File file = new File("D:\\Git\\repo\\test2");
        File file = new File("D:\\Git\\bak\\test2");
        commitPushCode(branchName,localRepoPath,remoteRepoURI, file,"",username,personalAccessToken);

        List<File> fileList =new ArrayList<>();
        fileList.add(new File("D:\\Git\\bak\\test1\\doc1\\222"));
        fileList.add(new File("D:\\Git\\bak\\test1\\doc1\\vvv"));
        fileList.add(new File("D:\\Git\\bak\\test1\\1.txt"));
        //commitPushCodeBatch(branchName,localRepoPath,remoteRepoURI, fileList,"",personalAccessToken);

//        List<File> list = listFilesAndDirectories(new File("D:\\Git\\bak"));
//        for (File file1 : list) {
//            System.out.println(file1.getPath());
//        }
//        commitPushCodeBatch(branchName,localRepoPath,remoteRepoURI, "D:\\Git\\bak","",personalAccessToken);

//        String remoteRepoURI = "https://your-git-server.com/your-repo.git";
//        String localRepoPath = "D:/Git/repo";
//        String personalAccessToken = "your_token";
//
//        // 设置本地仓库路径
//        File localPath = new File(localRepoPath);
//
//        // 初始化本地仓库
//        try (Repository repository = FileRepositoryBuilder.create(new File(localPath, ".git"))) {
//            repository.create();
//        }
//
//        // 克隆远程仓库到本地
//        Git.cloneRepository()
//                .setURI(remoteRepoURI)
//                .setDirectory(localPath)
//                .setCredentialsProvider(new UsernamePasswordCredentialsProvider("token", personalAccessToken))
//                .call();

//        // 在本地仓库中创建新的分支
//        Git git = Git.open(localPath);
//        git.checkout()
//                .setName("new_branch")
//                .setCreateBranch(true)
//                .call();
//
//        // 添加文件到暂存区
//        git.add()
//                .addFilepattern(".")
//                .call();
//
//        // 提交代码到本地仓库
//        git.commit()
//                .setMessage("commit message")
//                .call();
//
//        // 推送代码到远程仓库
//        git.push()
//                .call();
//
//        // 删除本地仓库
//        git.close();
//        localPath.delete();




//        // 初始化 Git 仓库
//        Repository repository = FileRepositoryBuilder.create(new File("/path/to/your/git/repository"));
//        Git git = new Git(repository);
//
//        // 添加单个文件
//        git.add().addFilepattern("file1.txt").call();
//
//        // 添加多个文件
//        git.add().addFilepattern("file2.txt").addFilepattern("file3.txt").call();
//
//        // 添加整个目录
//        git.add().addFilepattern("src").call();
//
//        // 提交改动
//        git.commit().setMessage("Added file1.txt, file2.txt, file3.txt and src directory").call();
//
//        // 关闭 Git 对象
//        git.close();

        // 设置本地 Git 仓库的路径
        /*File localPath = new File("/path/to/your/local/repo");

        // 初始化 Git 仓库
        try (Git git = Git.init().setDirectory(localPath).call()) {
            // 创建空文件夹
            File emptyFolder = new File(localPath, "emptyFolder");
            emptyFolder.mkdirs();

            // 在空文件夹中创建一个 .gitkeep 文件
            File gitKeepFile = new File(emptyFolder, ".gitkeep");
            gitKeepFile.createNewFile();

            // 将空文件夹和 .gitkeep 文件添加到暂存区
            git.add().addFilepattern("emptyFolder").call();

            // 提交变更
            git.commit().setMessage("Added empty folder").call();
        }*/
    }
}

