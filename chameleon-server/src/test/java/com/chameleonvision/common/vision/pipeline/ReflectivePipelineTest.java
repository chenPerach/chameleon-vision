package com.chameleonvision.common.vision.pipeline;

import com.chameleonvision.common.logging.LogGroup;
import com.chameleonvision.common.logging.Logger;
import com.chameleonvision.common.util.TestUtils;
import com.chameleonvision.common.vision.frame.Frame;
import com.chameleonvision.common.vision.frame.provider.FileFrameProvider;
import com.chameleonvision.common.vision.opencv.CVMat;
import com.chameleonvision.common.vision.opencv.ContourGroupingMode;
import com.chameleonvision.common.vision.opencv.ContourIntersectionDirection;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ReflectivePipelineTest {

    @Test
    public void testDebug() {
        Logger logger = new Logger(ReflectivePipelineTest.class, LogGroup.General);
        logger.warn("yo");
        logger.info("hi");
        logger.debug("debug");
    }

    @Test
    public void test2019() {
        TestUtils.loadLibraries();
        var pipeline = new ReflectivePipeline();
        pipeline.getSettings().hsvHue = List.of(60, 100);
        pipeline.getSettings().hsvSaturation = List.of(100, 255);
        pipeline.getSettings().hsvValue = List.of(190, 255);
        pipeline.getSettings().outputShowThresholded = true;
        pipeline.getSettings().outputShowMultipleTargets = true;
        pipeline.getSettings().contourGroupingMode = ContourGroupingMode.Dual;
        pipeline.getSettings().contourIntersection = ContourIntersectionDirection.Up;

        var frameProvider =
                new FileFrameProvider(
                        TestUtils.getWPIImagePath(TestUtils.WPI2019Image.kCargoStraightDark72in_HighRes),
                        TestUtils.WPI2019Image.FOV);

        TestUtils.showImage(frameProvider.get().image.getMat(), "Pipeline input", 1);

        CVPipelineResult pipelineResult;

        pipelineResult = pipeline.run(frameProvider.get());
        printTestResults(pipelineResult);

        Assertions.assertTrue(pipelineResult.hasTargets());
        Assertions.assertEquals(2, pipelineResult.targets.size(), "Target count wrong!");

        TestUtils.showImage(pipelineResult.outputFrame.image.getMat(), "Pipeline output");
    }

    @Test
    public void test2020() {
        TestUtils.loadLibraries();
        var pipeline = new ReflectivePipeline();

        pipeline.getSettings().hsvHue = List.of(60, 100);
        pipeline.getSettings().hsvSaturation = List.of(200, 255);
        pipeline.getSettings().hsvValue = List.of(200, 255);
        pipeline.getSettings().outputShowThresholded = true;

        var frameProvider =
                new FileFrameProvider(
                        TestUtils.getWPIImagePath(TestUtils.WPI2020Image.kBlueGoal_108in_Center),
                        TestUtils.WPI2020Image.FOV);

        CVPipelineResult pipelineResult = pipeline.run(frameProvider.get());
        printTestResults(pipelineResult);

        TestUtils.showImage(pipelineResult.outputFrame.image.getMat(), "Pipeline output");
    }

    private static void continuouslyRunPipeline(Frame frame, ReflectivePipelineSettings settings) {
        var pipeline = new ReflectivePipeline();

        while (true) {
            CVPipelineResult pipelineResult = pipeline.run(frame);
            printTestResults(pipelineResult);
            int preRelease = CVMat.getMatCount();
            pipelineResult.release();
            int postRelease = CVMat.getMatCount();

            System.out.printf("Pre: %d, Post: %d\n", preRelease, postRelease);
        }
    }

    // used to run VisualVM for profiling. It won't run on unit tests.
    public static void main(String[] args) {
        TestUtils.loadLibraries();
        var frameProvider =
                new FileFrameProvider(
                        TestUtils.getWPIImagePath(TestUtils.WPI2019Image.kCargoStraightDark72in_HighRes),
                        TestUtils.WPI2019Image.FOV);

        var settings = new ReflectivePipelineSettings();
        settings.hsvHue.set(60, 100);
        settings.hsvSaturation.set(100, 255);
        settings.hsvValue.set(190, 255);
        settings.outputShowThresholded = true;
        settings.outputShowMultipleTargets = true;
        settings.contourGroupingMode = ContourGroupingMode.Dual;
        settings.contourIntersection = ContourIntersectionDirection.Up;

        continuouslyRunPipeline(frameProvider.get(), settings);
    }

    private static void printTestResults(CVPipelineResult pipelineResult) {
        double fps = 1000 / pipelineResult.getLatencyMillis();
        System.out.print(
                "Pipeline ran in " + pipelineResult.getLatencyMillis() + "ms (" + fps + " fps), ");
        System.out.println("Found " + pipelineResult.targets.size() + " valid targets");
    }
}
