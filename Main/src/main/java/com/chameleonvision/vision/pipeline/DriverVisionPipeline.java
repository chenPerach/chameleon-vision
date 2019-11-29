package com.chameleonvision.vision.pipeline;

import com.chameleonvision.vision.pipeline.pipes.Draw2dContoursPipe;
import org.apache.commons.lang3.tuple.Pair;
import org.opencv.core.Mat;
import org.opencv.core.RotatedRect;

import java.util.List;

import static com.chameleonvision.vision.pipeline.DriverVisionPipeline.DriverPipelineResult;

public class DriverVisionPipeline extends CVPipeline<DriverPipelineResult, CVPipelineSettings> {

    private Draw2dContoursPipe draw2dContoursPipe = null;
    private Draw2dContoursPipe.Draw2dContoursSettings draw2dContoursSettings = new Draw2dContoursPipe.Draw2dContoursSettings();
    private final List<RotatedRect> blankList = List.of();

    public DriverVisionPipeline(CVPipelineSettings settings) {
        super(settings);
        draw2dContoursSettings.showCrosshair = true;
    }

    @Override
    public DriverPipelineResult runPipeline(Mat inputMat) {

        inputMat.copyTo(outputMat);

        var camProps = cameraCapture.getProperties().getStaticProperties();
        if(draw2dContoursPipe == null) {
            draw2dContoursPipe = new Draw2dContoursPipe(draw2dContoursSettings, camProps);
        } else {
            draw2dContoursPipe.setConfig(false,camProps);
        }

        draw2dContoursPipe.run(Pair.of(outputMat, blankList)).getLeft().copyTo(outputMat);

        return new DriverPipelineResult(null, outputMat, 0);
    }

    public static class DriverPipelineResult extends CVPipelineResult<Void> {
        public DriverPipelineResult(List<Void> targets, Mat outputMat, long processTime) {
            super(targets, outputMat, processTime);
        }
    }
}