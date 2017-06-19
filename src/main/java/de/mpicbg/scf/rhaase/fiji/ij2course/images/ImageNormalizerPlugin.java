package de.mpicbg.scf.rhaase.fiji.ij2course.images;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.NewImage;
import ij.measure.Measurements;
import ij.plugin.HyperStackConverter;
import ij.plugin.filter.PlugInFilter;
import ij.process.ImageProcessor;
import ij.process.ImageStatistics;

/**
 * Author: Robert Haase, Scientific Computing Facility, MPI-CBG Dresden,
 * rhaase@mpi-cbg.de
 * Date: May 2017
 * <p>
 * Copyright 2017 Max Planck Institute of Molecular Cell Biology and Genetics,
 * Dresden, Germany
 * <p>
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holder nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

public class ImageNormalizerPlugin implements PlugInFilter {


    @Override
    public int setup(String s, ImagePlus imagePlus) {
        return DOES_8G + DOES_16 + DOES_32;
    }

    @Override
    public void run(ImageProcessor imageProcessor) {
        ImagePlus input = IJ.getImage();

        ImagePlus output = normalize(input);

        output.show();
    }


    static ImagePlus normalize(ImagePlus input) {

        // determine min/max
        float maxPixelValue = Float.MIN_VALUE;
        float minPixelValue = Float.MAX_VALUE;
        for (int z = 1; z <= input.getNSlices(); z++) {
            input.setZ(z);
            ImageStatistics stats = input.getStatistics(Measurements.MIN_MAX);
            if (minPixelValue > (float) stats.min) {
                minPixelValue = (float) stats.min;
            }
            if (maxPixelValue < (float) stats.max) {
                maxPixelValue = (float) stats.max;
            }
        }

        // get memory for output image
        ImagePlus output = NewImage.createFloatImage("Normalized " + input.getTitle(), input.getWidth(), input.getHeight(), input.getNChannels() * input.getNSlices() * input.getNFrames(), NewImage.FILL_BLACK);
        if (output.getStackSize() > 1) {
            output = HyperStackConverter.toHyperStack(output, input.getNChannels(), input.getNSlices(), input.getNFrames());
        }

        // normalize all pixels
        for (int t = 1; t <= output.getNFrames(); t++) {
            input.setT(t);
            output.setT(t);
            for (int c = 1; c <= output.getNChannels(); c++) {
                input.setC(c);
                output.setC(c);
                for (int z = 1; z <= output.getNSlices(); z++) {
                    input.setZ(z);
                    output.setZ(z);
                    ImageProcessor inputProcessor = input.getProcessor();
                    ImageProcessor outputProcessor = output.getProcessor();
                    for (int x = 0; x < output.getWidth(); x++) {
                        for (int y = 0; y < output.getWidth(); y++) {
                            float value = inputProcessor.getf(x, y);
                            float normalisedValue = (value - minPixelValue) / (maxPixelValue - minPixelValue);

                            outputProcessor.setf(x, y, normalisedValue);
                        }
                    }
                }
            }
        }

        return output;
    }
}
