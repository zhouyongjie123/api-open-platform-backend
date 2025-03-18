package com.zyj.api.sandbox;

import cn.hutool.core.io.FileUtil;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.*;
import com.github.dockerjava.api.model.*;
import com.zyj.api.sandbox.strategy.CompilationStrategy;
import com.zyj.model.sandbox.SandboxExecutionInput;
import com.zyj.model.sandbox.SandboxExecutionOutput;
import com.zyj.model.sandbox.lang.LanguageEnum;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.*;

@AllArgsConstructor
public abstract class AbstractSandBox implements SandBox {

    protected static final String GLOBAL_CODE_PATH = "/Users/zyj/code/java/micro_module/sandbox/src/main/resources";

    public static final int CORE_POOL_SIZE = 3;

    public static final int MAXIMUM_POOL_SIZE = 5;

    public static final int KEEP_ALIVE_TIME = 1;

    public static final long CPU_COUNT = 1L;

    protected final DockerClient dockerClient;

    protected final String containerInnerVolumePath;

    protected final String fileName;

    protected final CompilationStrategy compilationStrategy;

    private final ExecutorService threadPool =
            new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS, new ArrayBlockingQueue<>(2, true), Executors.defaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy());

    protected final PullImageResultCallback pullImageResultCallback = new PullImageResultCallback() {
        @Override
        public void onNext(PullResponseItem item) {
            System.out.println("镜像拉取中" + item.getStatus());
            super.onNext(item);
        }

        @Override
        public void onComplete() {
            System.out.println("镜像拉取完成");
            super.onComplete();
        }
    };

    public SandboxExecutionOutput execute(SandboxExecutionInput input) {
        String containerId = null;
        File originalCodeFile = null;
        try {
            // 1.将用户的代码保存为文件
            originalCodeFile = saveOriginalCodeTofile(input.getOriginalCode());
            if (!isImageExist(input.getLanguage())) {
                // 2.拉取对应的镜像
                pullImage();
            }
            // 3.创建容器
            containerId = createContainer(input.getLanguage(), originalCodeFile);
            // 4.启动容器
            startContainer(containerId);

            // 5.在容器中编译代码
            compilationStrategy.compile(containerId);

            // 6.执行命令并获取结果
            return execCommand(containerId, input.getInputList());
        } finally {
            // 7.1停止容器
            // 7.2删除容器
            CompletableFuture<Void> task1 = Optional.ofNullable(containerId)
                                                    .map((id) -> CompletableFuture.runAsync(() -> stopContainer(id), threadPool)
                                                                                  .thenRunAsync(() -> removeContainer(id), threadPool))
                                                    .orElseGet(() -> CompletableFuture.runAsync(() -> {
                                                    }));
            // 7.3删除本地生成的文件
            CompletableFuture<Void> task2 = Optional.ofNullable(originalCodeFile)
                                                    .map((file -> CompletableFuture.runAsync(() -> removeLocalDirectory(file), threadPool)))
                                                    .orElseGet(() -> CompletableFuture.runAsync(() -> {
                                                    }));
            CompletableFuture
                    .allOf(task1, task2)
                    .join();
            threadPool.shutdown();
        }
    }

    private File saveOriginalCodeTofile(String originalCode) {
        String isolationPackagePath = GLOBAL_CODE_PATH + File.separator + UUID.randomUUID();
        if (!FileUtil.exist(isolationPackagePath)) {
            FileUtil.mkdir(isolationPackagePath);
        }
        String originalCodeFilePath = isolationPackagePath + File.separator + fileName;
        return FileUtil.writeString(originalCode, originalCodeFilePath, StandardCharsets.UTF_8);
    }


    protected abstract void pullImage();

    protected String createContainer(LanguageEnum lang, File originalCodeFile) {
        String imageName = lang.getImageName();
        CreateContainerCmd createContainerCmd = dockerClient.createContainerCmd(imageName);
        HostConfig hostConfig = new HostConfig();
        hostConfig.withMemory(1000 * 1024 * 1024L)
                  .withCpuCount(CPU_COUNT);
        hostConfig.setBinds(new Bind(originalCodeFile.getParent(), new Volume(containerInnerVolumePath), AccessMode.DEFAULT));
        CreateContainerResponse createContainerResponse = createContainerCmd
                .withHostConfig(hostConfig)
                .withAttachStderr(true)
                .withAttachStdin(true)
                .withAttachStdout(true)
                .withTty(true)
                .exec();
        return createContainerResponse.getId();
    }

    private void startContainer(String containerId) {
        dockerClient.startContainerCmd(containerId)
                    .exec();
    }


    protected abstract SandboxExecutionOutput execCommand(String containerId, List<String> inputList);

    private void stopContainer(String containerId) {
        StopContainerCmd stopContainerCmd = dockerClient.stopContainerCmd(containerId);
        stopContainerCmd.exec();
    }

    private void removeContainer(String containerId) {
        RemoveContainerCmd removeContainerCmd = dockerClient.removeContainerCmd(containerId);
        removeContainerCmd.exec();
    }

    private void removeLocalDirectory(File file) {
        FileUtil.del(file.getParent());
    }

    private Boolean isImageExist(LanguageEnum language) {
        InspectImageCmd inspectImageCmd = dockerClient.inspectImageCmd(language.getImageName());
        InspectImageResponse inspectImageResponse = inspectImageCmd.exec();
        if (StringUtils.isBlank(
                inspectImageResponse.getCreated())) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }
}
