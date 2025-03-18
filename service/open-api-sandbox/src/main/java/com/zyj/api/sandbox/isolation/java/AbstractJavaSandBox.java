package com.zyj.api.sandbox.isolation.java;

import cn.hutool.core.util.ArrayUtil;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.ExecCreateCmdResponse;
import com.github.dockerjava.api.command.PullImageCmd;
import com.github.dockerjava.api.command.StatsCmd;
import com.github.dockerjava.api.model.Frame;
import com.github.dockerjava.api.model.Statistics;
import com.github.dockerjava.api.model.StreamType;
import com.zyj.model.sandbox.constant.LanguageName;
import com.zyj.model.sandbox.exception.IllegalPullImageRequestException;
import com.zyj.api.sandbox.factory.DockerClientFactory;
import com.zyj.model.sandbox.lang.LanguageEnum;
import com.zyj.api.sandbox.strategy.AbstractCompiledLanguageSandBox;
import com.zyj.core.unit.MemoryUnit;
import com.zyj.core.unit.ThrowUtil;
import com.zyj.model.sandbox.OutputPairInfo;
import com.zyj.model.sandbox.SandboxExecutionOutput;
import lombok.AllArgsConstructor;
import org.springframework.util.StopWatch;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class AbstractJavaSandBox extends AbstractCompiledLanguageSandBox {

    private static final String FILE_NAME = "Main.java";

    private static final String FILE_MAIN_NAME = FILE_NAME.substring(0, FILE_NAME.indexOf('.'));

    private static final String BASIC_RUN_COMMAND = String.format("java -cp /app %s", FILE_MAIN_NAME);

    private static final String[] RUN_COMMAND = BASIC_RUN_COMMAND.split(" ");

    private static final String BASIC_COMPILE_COMMAND = String.format("javac -encoding utf-8 /app/%s", FILE_NAME);

    private static final String[] COMPILE_COMMAND = BASIC_COMPILE_COMMAND.split(" ");

    private static final String VOLUME_INNER_PATH = "/app";

    private final LanguageEnum language;

    protected AbstractJavaSandBox(LanguageEnum language) {
        this(DockerClientFactory.getDockerClient(), language);
    }

    private AbstractJavaSandBox(DockerClient dockerClient, LanguageEnum language) {
        super(dockerClient, VOLUME_INNER_PATH, FILE_NAME, new JavaCompiledStrategy(dockerClient));
        this.language = language;
    }

    @Override
    protected void pullImage() {
        ThrowUtil.throwNotIf(language.getLang()
                                     .equals(LanguageName.JAVA), new IllegalPullImageRequestException("AbstractJavaSandbox不能拉取非Java语言的镜像"));
        PullImageCmd pullImageCmd = dockerClient.pullImageCmd(language.getImageName());
        try {
            pullImageCmd.exec(pullImageResultCallback)
                        .awaitCompletion();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    @AllArgsConstructor
    private static final class JavaCompiledStrategy extends CompileTypeStrategy {
        private DockerClient dockerClient;

        @Override
        public void compile(String containerId) {
            ExecCreateCmdResponse execCreateCmdResponse =
                    dockerClient.execCreateCmd(containerId)// 创建执行命令
                                .withCmd(COMPILE_COMMAND)
                                .withAttachStderr(true)
                                .withAttachStdin(true)
                                .withAttachStdout(true)
                                .exec();
            String execId = execCreateCmdResponse.getId();
            try {
                dockerClient.execStartCmd(execId)
                            .exec(new DockerExecResultCallback())
                            .awaitCompletion();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        private static final class DockerExecResultCallback extends ResultCallback.Adapter<Frame> {
            @Override
            public void onNext(Frame frame) {
                StreamType streamType = frame.getStreamType();
                if (StreamType.STDERR.equals(streamType)) {
                    System.err.println("编译错误：" + new String(frame.getPayload()));
                } else {
                    System.out.println("编译输出：" + new String(frame.getPayload()));
                }
                super.onNext(frame);
            }
        }
    }

    @Override
    protected SandboxExecutionOutput execCommand(String containerId, List<String> inputList) {
        SandboxExecutionOutput result = new SandboxExecutionOutput();
        inputList.forEach(inputArgs -> {
            OutputPairInfo outputPairInfo = new OutputPairInfo();
            outputPairInfo.setVar1(inputArgs);
            StopWatch stopWatch = new StopWatch();
            String[] cmdArray = ArrayUtil.append(RUN_COMMAND, inputArgs.split(" "));
            ExecCreateCmdResponse execCreateCmdResponse =
                    dockerClient.execCreateCmd(containerId)// 创建执行命令
                                .withCmd(cmdArray)
                                .withAttachStderr(true)
                                .withAttachStdin(true)
                                .withAttachStdout(true)
                                .exec();
            String execId = execCreateCmdResponse.getId();// 执行完成,拿到执行命令的id,用id执行命令

            StatsCmd statsCmd = dockerClient.statsCmd(containerId);
            AtomicBoolean atomicBoolean = new AtomicBoolean(true);
            statsCmd.exec(new ResultCallback.Adapter<Statistics>() {
                @Override
                public void onNext(Statistics statistics) {
                    outputPairInfo.setActualMemoryCost(Math.max(
                            Optional.ofNullable(statistics.getMemoryStats()
                                                          .getUsage())
                                    .orElse(0L),
                            Optional.ofNullable(outputPairInfo.getActualMemoryCost())
                                    .orElse(0L)
                    ), MemoryUnit.BYTES);
                    atomicBoolean.set(false);
                    super.onNext(statistics);
                }
            });
            try {
                stopWatch.start();
                dockerClient.execStartCmd(execId)
                            .exec(new ResultCallback.Adapter<Frame>() {
                                @Override
                                public void onNext(Frame frame) {
                                    StreamType streamType = frame.getStreamType();
                                    if (StreamType.STDERR.equals(streamType)) {
                                        System.out.println("输出错误结果：" + new String(frame.getPayload()));
                                    } else {
                                        System.out.println("输出结果：" + new String(frame.getPayload()));
                                        outputPairInfo.setVar2(new String(frame.getPayload()));
                                    }
                                    super.onNext(frame);
                                }
                            })
                            .awaitCompletion();
                stopWatch.stop();
                for (; atomicBoolean.get(); ) ;
                statsCmd.close();
                outputPairInfo.setActualTimeCost(stopWatch.getLastTaskTimeMillis(), TimeUnit.MICROSECONDS);
            } catch (InterruptedException e) {
                System.out.println("程序执行异常");
                throw new RuntimeException(e);
            }
            result.getOutputPairInfoList()
                  .add(outputPairInfo);
        });
        return result;
    }
}
