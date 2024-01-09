# InstallJar

## 1、使用场景

Jar 包尚未发布，但需要安装到本地 maven 仓库时，可使用此工具进行本地发布，一键安装到仓库中

即封装了 maven 的 `install:install-file` 命令

```shell
mvn install:install-file -Dfile=filePath -DgroupId=groupId -DartifactId=artifactId -Dversion=version -Dpackaging=jar
```

目前只在 windows 系统中进行过测试，代码仅供参考



## 2、功能简介

![image-20240109202255454](https://s2.loli.net/2024/01/09/8Gd5WcOpFA6sjZ1.png)

- **File Path**：支持手动输入 Jar 包路径，支持右侧按钮选择路径，支持拖拽文件自动填充路径
  - 需通过 `java -jar InstallJar.jar` 命令运行
  - exe 文件直接运行时不支持该功能，拖拽功能存在问题，暂未知原因，还没有有效的解决方案（有懂的大佬可以与我联系，临表涕零，不胜感激）
- **Group ID**：Jar 包生成到仓库时坐标中的 `groupId`，若 **File Path** 填写正确，则能够进行自动反填
- **Artifact ID**：Jar 包生成到仓库时坐标中的 `artifactId`，若 **File Path** 填写正确，则能够进行自动反填
- **Version**：Jar 包生成到仓库时坐标中的 `version`，若 **File Path** 填写正确，则能够进行自动反填
- **Execute Maven Command**：执行安装 Jar 包操作，生成成功后将自动打开安装到 maven 仓库中的 Jar 包所在的 windows 资源文件夹目录



## 3、功能效果

![image-20240109220636946](https://s2.loli.net/2024/01/09/4hmHPU5ZgprdasY.png)

